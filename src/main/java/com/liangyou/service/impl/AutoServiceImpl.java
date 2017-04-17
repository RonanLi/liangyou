package com.liangyou.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liangyou.context.ApiMethodType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.RewardType;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.AccountRechargeParamDao;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.ExperienceMoneyDao;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.dao.InviteUserRebateDao;
import com.liangyou.dao.IpsRepayDetailDao;
import com.liangyou.dao.MmmPayDao;
import com.liangyou.dao.PriorRepayLogDao;
import com.liangyou.dao.RewardExtendDao;
import com.liangyou.dao.UserAmountDao;
import com.liangyou.dao.UserAmountLogDao;
import com.liangyou.dao.UserDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountRechargeParam;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowFee;
import com.liangyou.domain.BorrowRepayType;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.InviteUserRebate;
import com.liangyou.domain.IpsRepayDetail;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.domain.RewardExtend;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.ApiPayParamModel;
import com.liangyou.model.MsgReq;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.model.borrow.IpsRepaymentModel;
import com.liangyou.model.borrow.protocol.BorrowProtocol;
import com.liangyou.service.ApiService;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.LateInterestService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.service.TenderCompensationService;
import com.liangyou.service.UserCreditService;
import com.liangyou.tool.javamail.MailWithAttachment;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Service(value = "autoService")
@Transactional
public class AutoServiceImpl extends BaseServiceImpl implements AutoService {

	private static Logger logger = Logger.getLogger(AutoServiceImpl.class);

	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private BorrowCollectionDao borrowCollectionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private UserAmountDao userAmountDao;
	@Autowired
	private UserAmountLogDao userAmountLogDao;
	@Autowired
	private BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private MsgRecordService msgRecordService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private PriorRepayLogDao priorRepayLogDao;
	@Autowired
	private ApiService apiService;
	@Autowired
	private LateInterestService lateInterestService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private InviteUserDao inviteUserDao;
	@Autowired
	private InviteUserRebateDao inviteUserRebateDao;
	@Autowired
	private RewardExtendDao rewardExtendDao;
	@Autowired
	private RewardExtendService rewardExtendService;
	@Autowired
	private TenderCompensationService tenderCompensationService;
	@Autowired
	private IpsRepayDetailDao ipsRepayDetailDao;
	@Autowired
	private ExperienceMoneyDao experienceMoneyDao;
	@Autowired
	private AccountRechargeDao accountRechargeDao;
	@Autowired
	private MsgService msgService;
	@Autowired
	private AccountRechargeParamDao accountRechargeParamDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MmmPayDao mmmPayDao;
	/**
	 * 满标成功，并放款
	 */
	@Override
	public void fullSuccess(BorrowModel borrow) {

		logger.info("进入放款....borrowId:" + borrow.getModel().getId());

		List<Object> taskList = new ArrayList<Object>(); // 任务列表

		Borrow model = borrow.getModel();
		Borrow dbmodel = borrowDao.find(model.getId());
		User borrowUser = dbmodel.getUser();
		if (dbmodel.getStatus() != 3) {
			// 处理过的标 绝对不能再次处理。
			logger.error("该借款标的状态已经处在放款或错误状态，放款被拒绝,status:" + model.getStatus());
			return;
		}
		if (dbmodel.getType() == Constant.TYPE_FLOW) {
			logger.error("流转标，不允许进行满标复审！");
			return;
		}
		logger.info("借款入账开始　borrowId:" + model.getId());

		// 借款入账
		double money = model.getAccount();
		BorrowTender assignMentTender = null;
		if (dbmodel.getIsAssignment() == 1) { // 债权转让入账和普通的不一样， 待收减少，可用增加
			assignMentTender = borrowTenderDao.getAssignMentTender(dbmodel.getId());
			if (assignMentTender.getStatus() != -1) {
				logger.error("该债权标的状态已经处理过，放款被拒绝,tenderId:" + assignMentTender.getId() + " status:" + assignMentTender.getStatus());
			}
			// 待收减少，本金入账，利息全部转让出去
			double outInterest = model.getRepaymentAccount() - model.getAccount();
			accountDao.updateAccount(-outInterest, money, 0, -model.getRepaymentAccount(), 0, borrowUser.getUserId()); // 待还也要添加上去。
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.BORROW_SUCCESS, act, borrowUser, new User(Constant.ADMIN_ID), money, 0, "[" + getLogRemark(model) + "]债权转让资金入账" + money);
			accountLogDao.save(log);
		} else if (dbmodel.getType() == 107 && dbmodel.getStyle().equals("4")) {// 公益标放款不还款，不能有待还
			accountDao.updateAccount(money, money, 0, 0, 0, borrowUser.getUserId()); // 借款入账生成待还啊。
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.BORROW_SUCCESS, act, borrowUser, new User(Constant.ADMIN_ID), money, 0, "[" + getLogRemark(model) + "]借款入账" + money);
			accountLogDao.save(log);
		} else {
			accountDao.updateAccount(money, money, 0, 0, model.getRepaymentAccount(), borrowUser.getUserId()); // 借款入账生成待还啊。
			Account act = accountDao.getAcountByUser(borrowUser);

			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, Constant.ADMIN_ID);

			fillAccountLog(log, Constant.BORROW_SUCCESS, act, borrowUser, new User(Constant.ADMIN_ID), money, 0, "[" + getLogRemark(model) + "]借款入账" + money);
			accountLogDao.save(log);

		}

		// 调用放款接口
		apiService.FullSuccessLoanMoney(null, dbmodel, null, borrowUser, null, taskList, ApiMethodType.FULL_SUCCESS_C);

		List<BorrowTender> tenderList = borrowTenderDao.getBorrowTenderListByborrowId(dbmodel.getId());
		for (int i = 0; i < tenderList.size(); i++) {
			BorrowTender t = tenderList.get(i);
			User tenderUser = t.getUser();
			long tenderUserId = tenderUser.getUserId();

			logger.info("投资人" + tenderUserId + ",本金扣除，生成待收本金....");
			// 账户总额 可用余额 冻结总额 代收总额 待还总额 用户
			// edit by gy 2016-10-27 14:33:14 begin
			// 审核扣除投资人本金的时候， 判断当前投资人的投资记录，是否使用过体验金抵扣本金，如果使用过体验金，则需要将体验金金额扣除，并且账户总额需要加上使用的体验金利息
			double noUseMoney = t.getAccount();
			double total = 0;
			if (t.getExperienceMoney() != null && t.getExperienceMoney().getInterestUseStatus() == 1) {
				noUseMoney = t.getAccount() - t.getExperienceMoney().getExperienceInterest();
				total = t.getExperienceMoney().getExperienceInterest();

				// 如果使用了体验金，这一步将体验金打给借款人
				// apiService.webTransfer(taskList, borrowUser, total);
				// borrowService.doApiTask(taskList);
			}
			accountDao.updateAccount(total, 0, -noUseMoney, t.getAccount(), 0, tenderUserId);
			// edit by gy 2016-10-27 14:34:10 end
			Account actInvest = accountDao.getAcountByUser(tenderUser);
			AccountLog logAct = new AccountLog(tenderUser.getUserId(), Constant.INVEST, borrowUser.getUserId(), DateUtils.getNowTimeStr(), "");
			fillAccountLog(logAct, actInvest, t.getAccount(), model.getId(), "扣除投资[" + getLogRemark(model) + "]冻结资金,同时生成待收本金!" + t.getAccount());
			accountLogDao.save(logAct);

			logger.info("投资人" + tenderUserId + ",生成待收利息....");
			double interest = t.getInterest();
			accountDao.updateAccount(interest, 0, 0, interest, 0, tenderUser.getUserId());
			Account actInterest = accountDao.getAcountByUser(tenderUser);
			AccountLog logInterest = new AccountLog(tenderUser.getUserId(), Constant.WAIT_INTEREST, borrowUser.getUserId(), DateUtils.getNowTimeStr(), "");
			fillAccountLog(logInterest, Constant.WAIT_INTEREST, actInterest, tenderUser, borrowUser, interest, 0, "生成[" + getLogRemark(model) + "]待收利息" + interest);
			accountLogDao.save(logInterest);

			// TGPROJECT-411 wsl 2014-09-04 start
			logger.info("奖励发放，开始...");
			double awardValue = borrow.getAwardValue(t.getAccount());// 触发易极付 转账功能是实现 奖励的划分
			String remark = "收到标[" + getLogRemark(model) + "]投资奖励!!";
			if (awardValue > 0) {

				logger.info("借款人" + borrowUser.getUserId() + ",扣除奖励...");
				awardValue = NumberUtils.format2(awardValue); // 强制两位
				accountDao.updateAccount(-awardValue, -awardValue, 0, 0, 0, borrowUser.getUserId());
				Account actAwareOut = accountDao.getAcountByUser(borrowUser);
				AccountLog logDeuct = new AccountLog(borrowUser.getUserId(), Constant.AWARD_DEDUCT, tenderUser.getUserId(), DateUtils.getNowTimeStr(), "");
				fillAccountLog(logDeuct, Constant.AWARD_DEDUCT, actAwareOut, borrowUser, tenderUser, awardValue, 0, "投资成功，扣除奖励!" + awardValue + "元");
				accountLogDao.save(logDeuct);

				logger.info("借款人" + tenderUser.getUserId() + ",扣除奖励...");
				accountDao.updateAccount(awardValue, awardValue, 0, 0, 0, tenderUser.getUserId());
				Account actAwareIn = accountDao.getAcountByUser(tenderUser);
				AccountLog logAward = new AccountLog(tenderUser.getUserId(), Constant.AWARD_ADD, borrowUser.getUserId(), DateUtils.getNowTimeStr(), "");
				fillAccountLog(logAward, Constant.AWARD_ADD, actAwareIn, tenderUser, borrowUser, awardValue, 0, remark + awardValue);
				accountLogDao.save(logAward);

				// 调用双乾接口，划分奖励
				apiService.FullSuccessAward(dbmodel, t, borrowUser, tenderUser, taskList, awardValue);

			}
			logger.info("奖励发放，结束...");
			// 修改Tender表中的待收利息
			logger.info("修改Tender表中的待收利息，开始...");
			t.setWaitAccount(t.getAccount());
			t.setWaitInterest(t.getInterest());
			if (model.getIsAssignment() == 1) {// 债权转让 把 tender 中的 borrow_id 改成原来的标便于还款
				BorrowTender assignTender = borrowTenderDao.getAssignMentTender(dbmodel.getId());
				t.setBorrow(assignTender.getBorrow());
			}
			borrowTenderDao.update(t);
			logger.info("修改Tender表中的待收利息，结束...");

			// 投标成功，处理客户星级积分
			logger.info("投标成功，处理客户星级积分..");
			ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "add_tender", tenderUser, t.getAccount());

			// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start
			// 处理投资积分
			logger.info("处理投资积分..");
			userCreditService.borrowTenderCredit(t);
			// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 end

			// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
			// 更新被推荐人信息
			InviteUser inviteUser = inviteUserDao.getInviter(tenderUser.getUserId());
			if (inviteUser != null) {
				inviteUser.setIsTender(1);
				inviteUser.setTenderTotal(inviteUser.getTenderTotal() + t.getAccount());
				// v1.8.0.4_u4 TGPROJECT-354 qinjun 2014-07-04 start
				rewardExtendService.tenderExtendRedPacket(t.getUser(), t.getAccount());
				// v1.8.0.4_u4 TGPROJECT-354 qinjun 2014-07-04 end
			}
			// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end

			// wsl 满标前补偿金功能【心意贷】2014-08-25 start
			tenderCompensationService.verifyFullSuccessCompensation(model, t);
			// wsl 满标前补偿金功能【心意贷】2014-08-25 end

			// wsl 首次投资奖励功能【德益网】 2014-09-02 start
			rewardExtendService.tenderFirstReward(tenderUser, t.getAccount());
			// wsl 首次投资奖励功能【德益网】 2014-09-02 end
		}

		// 借款成功，为借款人添加客户星级积分
		ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "add_borrow", borrowUser, dbmodel.getAccount());

		// 处理借款积分
		userCreditService.borrowSuccessCredit(borrow, borrowUser);

		// 扣手续费
		deductFee(borrow, taskList, borrowUser, tenderList);

		// 放款，网站扣除额外的费用
		borrowUserPayAdmin(borrow, taskList, borrowUser, tenderList, Constant.BORROW_CONTRACT);

		if (model.getIsAssignment() == 1) {// 债权转让标不需要生成还款计划，减掉债权转让人的待收、修改tender 信息，转让的只是本金
			assignMentTender.setStatus(3);
			assignMentTender.setWaitAccount(0);// 债权转让出去，清除待收
			assignMentTender.setWaitInterest(0);// 债权转让出去，清除待收
			assignMentTender.setRepaymentYescapital(assignMentTender.getRepaymentYescapital() + money);// 只收取本金。
			borrowTenderDao.update(assignMentTender);// 更新原来的投资生产的待收
			List<BorrowCollection> assignCollections = assignMentTender.getBorrowCollections();// 更新原来的待收
			int i = 0;
			for (BorrowCollection bc : assignCollections) {
				if (bc.getStatus() == 0) {
					bc.setStatus(3);
					if (i == 0) {
						bc.setRepayYescapital(bc.getRepayYescapital() + money);// 债权转让成功，收到的本金，记录到未收款的第一条里边。
					}
					bc.setRepayYestime(new Date());
					bc.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_ASSIGNMENT_PAY));
					i++;
				} // 改成已经完成
			}
			borrowCollectionDao.update(assignCollections);
			// 更新
		} else {
			// 修改borrow的状态，将还款生产计划时机调整至放款的最后一步
			List<BorrowCollection> listCollections = borrowCollectionDao.getCollectionByBorrow(model.getId());
			if (!(dbmodel.getType() == 107 && dbmodel.getStyle().equals("4"))) {// 公益标，不还款
				borrowRepaymentDao.save(borrow.getRepayment(listCollections));
				borrowCollectionDao.update(listCollections);
			}
		}

		if (!borrowService.doApiTask(taskList)) {// 触发易极付任务。 如果失败 标的的状态改成 99
			model.setStatus(99);
		} else {
			if (model.getIsAssignment() == 1) {// 如果是债权标，复审后标的状态改成8
				model.setStatus(8);
			} else {
				model.setStatus(6);
			}
		}
		borrowDao.update(model);
		logger.info("跟新标的状态 id:" + model.getId() + "   " + model.getStatus());

		// 推荐人1%的投资金的奖历(帝业投资)
		rewardExtendService.doTenderRewardForInvite(tenderList);

		// v1.8.0.4_u1 TGPROJECT-290 lx start
		for (int i = 0; i < tenderList.size(); i++) {
			BorrowTender t = tenderList.get(i);
			long borrow_id = t.getBorrow().getId();
			long tender_id = t.getId();
			User toUser = t.getUser();
			// 是否启用发送借款协议到投资人邮箱
			if (Global.getInt("protocol_toEmail_enable") == 1 && StringUtils.isNull(toUser.getEmailStatus()).equals("1")) {
				// 生成协议
				BorrowProtocol bp = BorrowProtocol.getInstance(toUser, borrow_id, tender_id, 100);
				File pdfFile = new File(bp.getInPdfName());
				if (pdfFile.exists()) {
					try {
						bp.createPdf();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				int tenderIndex = 0;
				for (int j = 0; j < tenderList.size(); j++) {
					if (tender_id == tenderList.get(j).getId()) {
						tenderIndex = j + 1;
						break;
					}
				}
				String outPdfName = "借款电子协议" + Global.getValue("pdftemplate").toUpperCase() + DateUtils.newdateStr2(new Date()) + "-" + borrow_id + "-" + tenderIndex + ".pdf";

				// 协议已附件形式发送邮件
				try {
					sendMailWithAttachment(toUser, model, t, bp.getInPdfName(), outPdfName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// v1.8.0.4_u1 TGPROJECT-290 lx end

	}

	/**
	 * 汇付，债权转让 调用新的债权转让接口重新处理
	 */
	@Override
	public void fullSuccessCreditAssign(BorrowModel borrow) {

		logger.info("进入债权转让满标复审....borrowId:" + borrow.getModel().getId());
		List<Object> taskList = new ArrayList<Object>(); // 任务列表

		Borrow model = borrow.getModel();
		Borrow dbmodel = borrowDao.find(model.getId());
		// 借款人(债权转让人)
		User borrowUser = dbmodel.getUser();

		if (dbmodel.getStatus() != 3) {
			// 处理过的标 绝对不能再次处理。
			logger.error("该借款标的状态已经处在放款或错误状态，放款被拒绝,status:" + model.getStatus());
			return;
		}
		if (dbmodel.getIsAssignment() != 1) {
			logger.error("非债权转让标，不能进入...");
			return;
		}
		logger.info("债权转让满标复审开始　borrowId:" + model.getId());
		// 债权转让入账和普通的不一样，;待收减少，可用增加
		BorrowTender assignMentTender = borrowTenderDao.getAssignMentTender(dbmodel.getId());

		if (assignMentTender.getStatus() != -1) {
			logger.error("该债权标的状态已经处理过，放款被拒绝,tenderId:" + assignMentTender.getId() + " status:" + assignMentTender.getStatus());
		}
		// 待收减少，本金入账，利息全部转让出去
		double money = model.getAccount();
		double outInterest = model.getRepaymentAccount() - model.getAccount();
		accountDao.updateAccount(-outInterest, money, 0, -model.getRepaymentAccount(), 0, borrowUser.getUserId()); // 待还也要添加上去。
		Account act = accountDao.getAcountByUser(borrowUser);
		AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, Constant.ADMIN_ID);
		fillAccountLog(log, Constant.BORROW_SUCCESS, act, borrowUser, new User(Constant.ADMIN_ID), money, 0, "[" + getLogRemark(model) + "]债权转让资金入账" + money);
		accountLogDao.save(log);

		List<BorrowTender> tenderList = borrowTenderDao.getBorrowTenderListByborrowId(dbmodel.getId());
		for (int i = 0; i < tenderList.size(); i++) {
			BorrowTender t = tenderList.get(i);
			User tenderUser = t.getUser();
			long tenderUserId = tenderUser.getUserId();

			logger.info("受让人" + tenderUserId + ",本金扣除，生成待收本金....");
			accountDao.updateAccount(0, 0, -t.getAccount(), t.getAccount(), 0, tenderUserId);
			Account actInvest = accountDao.getAcountByUser(tenderUser);
			AccountLog logAct = new AccountLog(tenderUser.getUserId(), Constant.INVEST, borrowUser.getUserId(), DateUtils.getNowTimeStr(), "");
			fillAccountLog(logAct, actInvest, t.getAccount(), model.getId(), "扣除投资[" + getLogRemark(model) + "]冻结资金,同时生成待收本金!" + t.getAccount());
			accountLogDao.save(logAct);

			// 汇付等接口有解冻资金
			// apiService.FullSuccessUnFreezeMoney(tenderUser, t.getTrxId(), model.getId(), t.getAccount(), taskList, ApiMethodType.FULL_SUCCESS_B);

			double feeRate = dbmodel.getBorrowFee() == null ? 0 : dbmodel.getBorrowFee().getManagefee();
			double fee = t.getAccount() * feeRate; // 债权转让收取的费用，按投资金额，每笔收取。
			// 处理汇付等接口放款
			apiService.fullSuccessCreditAssign(fee, dbmodel, t, assignMentTender, assignMentTender.getBorrow(), taskList);

			logger.info("受让人" + tenderUserId + ",生成待收利息....");
			double interest = t.getInterest();
			accountDao.updateAccount(interest, 0, 0, interest, 0, tenderUser.getUserId());
			Account actInterest = accountDao.getAcountByUser(tenderUser);
			AccountLog logInterest = new AccountLog(tenderUser.getUserId(), Constant.WAIT_INTEREST, borrowUser.getUserId(), DateUtils.getNowTimeStr(), "");
			fillAccountLog(logInterest, Constant.WAIT_INTEREST, actInterest, tenderUser, borrowUser, interest, 0, "生成[" + getLogRemark(model) + "]待收利息" + interest);
			accountLogDao.save(logInterest);

			// 修改Tender表中的待收利息
			t.setWaitAccount(t.getAccount());
			t.setWaitInterest(t.getInterest());
			// 债权转让，把当前受让人tender 中的 borrow_id 改成原来的标id，便于还款
			BorrowTender assignTender = borrowTenderDao.getAssignMentTender(dbmodel.getId());
			t.setBorrow(assignTender.getBorrow());

			borrowTenderDao.update(t);
		}

		// 债权转让标不需要生成还款计划，减掉债权转让人的待收、修改tender 信息，转让的只是本金
		assignMentTender.setStatus(3);
		assignMentTender.setWaitAccount(0);// 债权转让出去，清除待收
		assignMentTender.setWaitInterest(0);// 债权转让出去，清除待收
		assignMentTender.setRepaymentYescapital(assignMentTender.getRepaymentYescapital() + money);// 只收取本金。
		borrowTenderDao.update(assignMentTender);// 更新原来的投资生产的待收
		List<BorrowCollection> assignCollections = assignMentTender.getBorrowCollections();// 更新原来的待收
		int i = 0;
		for (BorrowCollection bc : assignCollections) {
			if (bc.getStatus() == 0) {
				bc.setStatus(3);
				if (i == 0) {
					bc.setRepayYescapital(bc.getRepayYescapital() + money);// 债权转让成功，收到的本金，记录到未收款的第一条里边。
				}
				bc.setRepayYestime(new Date());
				bc.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_ASSIGNMENT_PAY));
				i++;
			} // 改成已经完成
		}
		borrowCollectionDao.update(assignCollections);

		if (!borrowService.doApiTask(taskList)) {// 触发易极付任务。 如果失败 标的的状态改成 99
			model.setStatus(99);
		} else {
			model.setStatus(8);
		}
		borrowDao.update(model);
		logger.info("跟新标的状态 id:" + model.getId() + "   " + model.getStatus());

	}

	// v1.8.0.4_u1 TGPROJECT-290 lx start

	/**
	 * 获取服务器路径
	 *
	 * @return
	 */
	public String findServerPath() {
		String path = this.getClass().getResource("/").getPath();
		path = path.replaceAll("/WEB-INF/classes/", "/");
		return path;
	}

	/**
	 * 发送邮件(带附件)
	 *
	 * @param user
	 * @throws Exception
	 */
	private void sendMailWithAttachment(User user, Borrow borrow, BorrowTender bt, String pdfPath, String outPdfName) throws Exception {
		String to = user.getEmail();
		String attachfile = pdfPath;
		MailWithAttachment m = MailWithAttachment.getInstance();
		m.setOutPdfName(outPdfName);
		m.setTo(to);
		m.readProtocolMsg();
		m.replace(user.getUsername(), to, "/member/identify/active.html?id=" + m.getdecodeIdStr(user), borrow, bt);
		m.attachfile(attachfile);
		m.sendMail();
	}

	// v1.8.0.4_u1 TGPROJECT-290 lx end
	@Override
	public void fail(BorrowModel borrow) {// 满标拒绝或者撤标
		logger.info("进入满标复审不通过.....  " + borrow.getModel().getId() + " " + borrow.getModel().getStatus());
		List<Object> taskList = new ArrayList<Object>();
		Borrow model = borrow.getModel();
		Borrow dbModel = borrowDao.find(model.getId()); // 数据库 原始项目
		User borrowUser = dbModel.getUser();
		if (dbModel.getStatus() == 49 || dbModel.getStatus() == 59 || dbModel.getStatus() == 2 || dbModel.getStatus() == -1) {
			logger.error("该借款标已经处在在拒绝的状态... status:" + dbModel.getStatus());
			return;
		}
		List<BorrowTender> tenderList = borrowTenderDao.findByProperty("borrow", dbModel);
		List<BorrowCollection> listCollects = new ArrayList<BorrowCollection>();
		for (int i = 0; i < tenderList.size(); i++) {
			BorrowTender t = tenderList.get(i);
			logger.info(" t.status:" + t.getStatus());
			if (t.getStatus() == 0) { // 成功的 和 已经撤回的不能撤回
				User tenderUser = t.getUser();
				logger.info("  user:" + tenderUser.getUserId());

				// double total, double use, double nouse,double collect, double repay, long user_id
				if (dbModel.getType() == Constant.TYPE_EXPERIENCE) {
					ExperienceMoney em = experienceMoneyDao.findByCriteriaForUnique(SearchParam.getInstance().addParam("user", tenderUser).addParam("useStatus", 1));

					Account act = accountDao.getAcountByUser(tenderUser);
					act.setCollection(act.getCollection() - em.getExperienceInterest());
					this.accountDao.update(act);
					AccountLog log = new AccountLog(tenderUser.getUserId(), Constant.RETURN_EXPERIENCEMONEY, borrowUser.getUserId());
					fillAccountLog(log, act, Double.parseDouble(Global.getValue("amount_handsel_experienceMoney")));
					log.setRemark("[" + getLogRemark(model) + "]标撤回，返还体验金!" + (Global.getValue("amount_handsel_experienceMoney")));
					accountLogDao.save(log);

					em.setUseStatus(0);
					em.setExperienceInterest(0.00);
					em.setUseTime(null);
					experienceMoneyDao.update(em);

				} else {
					double tenderValue = t.getAccount();
					accountDao.updateAccount(0, tenderValue, -tenderValue, 0, 0, tenderUser.getUserId());
					Account act = accountDao.getAcountByUser(tenderUser);
					AccountLog log = new AccountLog(tenderUser.getUserId(), Constant.UNFREEZE, borrowUser.getUserId());
					fillAccountLog(log, act, tenderValue);
					if (dbModel.getStatus() == 5) {
						log.setRemark("[" + getLogRemark(model) + "]标撤回，解冻资金!" + (t.getAccount()));
					} else {
						log.setRemark("[" + getLogRemark(model) + "]审核不通过，解冻资金!" + (t.getAccount()));
					}
					accountLogDao.save(log);
				}
				t.setStatus(2);
				List<BorrowCollection> lc = t.getBorrowCollections();
				for (BorrowCollection bc : lc) {
					bc.setStatus(2);
					listCollects.add(bc);
				}

				// wsl 满标前补偿金功能【心意贷】2014-08-25 start
				tenderCompensationService.failTenderCompensation(t, model, taskList);
				// wsl 满标前补偿金功能【心意贷】2014-08-25 end
			}
		}
		if (model.getType() == Constant.TYPE_CREDIT) { // 解冻信用额度
			double amount = model.getAccount();
			UserAmount ua = userAmountDao.getUserAmountByUser(model.getUser());
			if (ua == null) {
				logger.error("用户" + model.getUser().getUserId() + "的信用账户不存在.");
				throw new BussinessException("用户" + model.getUser().getUserId() + "的信用账户不存在.");
			}
			userAmountDao.updateCreditAmount(0, amount, -amount, model.getUser().getUserId());
			UserAmountLog amountLog = new UserAmountLog();
			amountLog.setUser(new User(model.getUser().getUserId()));
			amountLog.setType("borrow_cancel");
			amountLog.setAccountType("credit");
			amountLog.setAccount(amount);
			amountLog.setAccountTotal(ua.getCredit());
			amountLog.setAccountUse(ua.getCreditUse() + amount);
			amountLog.setAccountNouse(ua.getCreditNouse() - amount);
			if (dbModel.getStatus() == 5) {
				amountLog.setRemark("管理员撤回借款标,解冻" + amount);
			} else {
				amountLog.setRemark("借款标审核不通过,解冻" + amount);
			}
			amountLog.setAddtime(new Date());
			amountLog.setAddip("");
			userAmountLogDao.save(amountLog);
		}

		if (model.getStatus() == 4)
			model.setStatus(49);
		if (model.getStatus() == 5)
			model.setStatus(59);
		if (dbModel.getIsAssignment() == 1) { // 债权转让失败,把 原来tender 信息修改过来。
			BorrowTender assignTender = borrowTenderDao.getAssignMentTender(dbModel.getId());
			assignTender.setStatus(0);
			assignTender.setAssignmentId(0);
			borrowTenderDao.update(assignTender);
		}
		borrowDao.update(model);
		if (tenderList.size() > 0) { // 如果有投资记录 触发易极付申请退资金 不允许投资人单独撤回。
			borrowTenderDao.update(tenderList);
			borrowCollectionDao.update(listCollects);
			apiService.faillBorrow(taskList, dbModel, ApiMethodType.FAILL_BORROW_D);
			borrowService.doApiTask(taskList); // 易极付集中处理
		}
	}

	/**
	 * 提前还款
	 */
	@Override
	public void priorRepay(BorrowRepayment repay, PriorRepayLog ppLog) {
		logger.info("提前还款进来： " + repay.getId());
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		BorrowRepayment repayment = borrowRepaymentDao.find(repay.getId());
		if (repayment.getStatus() != 0) {
			logger.error("已经还款/网站垫付或正在还款中，请勿重复 操作");
			return;
		}
		Borrow model = repayment.getBorrow();
		User borrowUser = model.getUser();
		Account at = accountDao.getAcountByUser(borrowUser);
		long nowTime = NumberUtils.getLong(DateUtils.getNowTimeStr()); // 当前还款的
		Date startDate = DateUtils.getNextDayYYYYMMdd(repay.getBorrow().getVerifyTime());// 起始还款日
		long verifyTime = DateUtils.getTime(startDate); // 满标复审时间
		// 计算从满标复审日到提前还款日中间的时间间隔（相当于重新计算借款时间）
		long dates = (nowTime - verifyTime) % (60 * 60 * 24) > 0 ? (nowTime - verifyTime) / (60 * 60 * 24) + 1 : (nowTime - verifyTime) / (60 * 60 * 24);
		// 还款利息
		double repayInterest_ratio = repay.getBorrow().getApr() * dates / 36000;// 国控小薇还款利息算法
		double repayInterest = repayInterest_ratio * repayment.getCapital();
		// 计算补偿金
		double compenMoney_ratio = repay.getBorrow().getApr() / 100 * 30 / 360;// 国控小薇还款补偿金算法
		double compenMoney = compenMoney_ratio * repayment.getCapital();

		double sumRepayMoney = repay.getCapital() + repayInterest + compenMoney; // 计算应还总额
		if (at.getUseMoney() < sumRepayMoney) {// 判断可用余额是否足够
			throw new BussinessException("可用余额不足还款失败。");
		}
		double capital = repay.getCapital(); // 本金
		double interest = repay.getInterest();// 利息
		BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow 计算费用方法。

		repayInterest = 0;
		compenMoney = 0;

		// 查询此标当期的所有投资人，并循环投资人还款
		List<BorrowCollection> collectList = borrowCollectionDao.getCollectionByBorrowAndPeriod(model.getId(), repay.getPeriod());

		for (BorrowCollection c : collectList) {
			double cCapital = c.getCapital();
			double cInterest = NumberUtils.formatTo2(repayInterest_ratio * c.getCapital());
			double cCompenMoney = NumberUtils.formatTo2(compenMoney_ratio * c.getCapital());
			BorrowTender t = c.getBorrowTender();
			User tenderUser = t.getUser();
			// 归还投资人本金
			if (cCapital > 0) {
				accountDao.updateAccount(0, cCapital, 0, -cCapital, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_CAPTIAL, cAct, tenderUser, borrowUser, cCapital, 0, "[" + getLogRemark(model) + "]提前还款，收到归还本金" + cCapital);
				accountLogDao.save(cLog);
			}

			// 归还投资人利息
			double borrowFee = 0;
			if (cInterest >= 0) {
				accountDao.updateAccount(cInterest - c.getInterest(), cInterest, 0, -c.getInterest(), 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_INTEREST, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_INTEREST, cAct, tenderUser, borrowUser, cInterest, 0, "[" + getLogRemark(model) + "]提前还款，收到实际归还利息" + cInterest + ",应还利息：" + c.getInterest());
				accountLogDao.save(cLog);
				// 计算实还利息
				repayInterest += cInterest;
				// 扣除投资人利息管理费
				double borrow_fee = Global.getDouble("borrow_fee");
				borrowFee = NumberUtils.format2(cInterest * borrow_fee);
				if (tenderUser.getUserCache().getVipStatus() == 1) {// vip会员
					Rule rule = ruleService.getRuleByNid("vip_rule");
					if (rule.getStatus() == 1) {// 启用vip规则
						borrowFee = NumberUtils.format2(cInterest * rule.getValueDoubleByKey("interest_manage_fee"));
					}
				}

				if (borrowFee > 0) {
					accountDao.updateAccount(-borrowFee, -borrowFee, 0, 0, 0, tenderUser.getUserId());
					cAct = accountDao.getAcountByUser(tenderUser);
					cLog = new AccountLog(tenderUser.getUserId(), Constant.MANAGE_FEE, 1);
					fillAccountLog(cLog, Constant.MANAGE_FEE, cAct, tenderUser, new User(Constant.ADMIN_ID), borrowFee, 0, getLogRemark(model) + "还款，扣除" + Global.getString("trade_name_interest_fee") + ":" + borrowFee);
					accountLogDao.save(cLog);
				}
			}
			// 还款补偿金
			if (cCompenMoney > 0) {
				accountDao.updateAccount(cCompenMoney, cCompenMoney, 0, 0, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIOR_REPAID_COMPEN, cAct, tenderUser, borrowUser, cCompenMoney, 0, "[" + getLogRemark(model) + "]提前还款，收到补偿金:" + cCompenMoney);
				accountLogDao.save(cLog);
				// 计算还款补偿金
				compenMoney += cCompenMoney;
			}
			// 还投资人 逾期的利息 按投资的比例来计算
			if (borrow.isLastPeriod(repay.getPeriod())) {
				t.setStatus(1);
			}
			t.setWaitAccount((t.getWaitAccount() - c.getCapital())); // 不能小于0
			t.setWaitInterest((t.getWaitInterest() - c.getInterest()));
			t.setRepaymentYescapital(t.getRepaymentYescapital() + cCapital);
			t.setRepaymentYesinterest(t.getRepaymentYesinterest() + cInterest);
			t.setInterestFee(t.getInterestFee() + borrowFee);
			t.setCompensation(t.getCompensation() + cCompenMoney);
			// 更新tender记录
			borrowTenderDao.update(t);
			// 更新collection记录
			c.setStatus(1);
			c.setRepayYestime(new Date());
			c.setRepayYesinterest(cInterest);
			c.setRepayYescapital(cCapital);
			c.setInterestFee(borrowFee);
			c.setCompensation(cCompenMoney);
			c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
			borrowCollectionDao.save(c);

		}

		// 从用户冻结账户中扣除还款本金
		if (capital > 0) {
			accountDao.updateAccount(-capital, -capital, 0, 0, -capital, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), capital, 0, "[" + getLogRemark(model) + "]提前还款，扣除还款本金" + capital);
			accountLogDao.save(log);
			// 添加提前还款记录
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_CAPTIAL, capital, capital);
			priorRepayLogDao.save(ppLog);
		}
		repayInterest = NumberUtils.formatTo2(repayInterest);
		// 扣除还款利息
		if (repayInterest >= 0) {
			accountDao.updateAccount(-repayInterest, -repayInterest, 0, 0, -interest, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_INTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), repayInterest, 0, "[" + getLogRemark(model) + "]提前还款，扣除利息" + interest + "实际利息：" + repayInterest);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_INTEREST, interest, repayInterest);
			priorRepayLogDao.save(ppLog);
		}
		compenMoney = NumberUtils.formatTo2(compenMoney);
		if (compenMoney > 0) {
			accountDao.updateAccount(-compenMoney, -compenMoney, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIOR_REPAID_COMPEN, act, borrowUser, new User(Constant.ADMIN_ID), compenMoney, 0, "[" + getLogRemark(model) + "]提前还款，扣除补偿金" + compenMoney);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIOR_REPAID_COMPEN, compenMoney, compenMoney);
			priorRepayLogDao.save(ppLog);
		}
		// 如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
		if (model.getType() == Constant.TYPE_CREDIT) {
			userAmountDao.updateCreditAmount(0, repay.getCapital(), -repay.getCapital(), borrowUser.getUserId());
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, Constant.REPAID, -repay.getCapital(), "还款，解冻信用额度：" + repay.getCapital(), ""));
		}

		int repayStatus = 7;
		if (borrow.isLastPeriod(repay.getPeriod())) {
			repayStatus = 8;
		}
		model.setStatus(repayStatus);
		borrowDao.save(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setWebstatus(1);
		repay.setRepaymentYescapital(capital);
		repay.setRepaymentYesinterest(repayInterest);
		repay.setCompensation(compenMoney);
		repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
		borrowRepaymentDao.update(repay);

		// 处理人任务。
		borrowService.doApiTask(taskList);

	}

	/**
	 * add by gy 2016-10-20 16:12:29 体验金还款
	 *
	 * @param repay
	 */
	@Override
	public void experienceMoneyRepay(BorrowRepayment repay1) {
		logger.info("体验金还款进来~~ ");

		// 查询体验标标的
		List<Borrow> borrowList = borrowDao.findByCriteria(SearchParam.getInstance().addParam("type", Constant.TYPE_EXPERIENCE));
		// Borrow model = borrowDao.find(Borrow.class, Long.parseLong(Global.getValue("experienceMoney_borrow_id")));
		for (Borrow model : borrowList) {
			BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow 计算费用方法。
			User borrowUser = model.getUser();
			Account at = accountDao.getAcountByUser(borrowUser);

			// 查询当前标的所有投资人
			List<BorrowCollection> collectList = borrowCollectionDao.getCollectionByBorrow(model.getId());
			for (BorrowCollection c : collectList) {
				double cCapital = c.getCapital();
				double cInterest = c.getInterest();
				BorrowTender t = c.getBorrowTender();
				User tenderUser = t.getUser();

				Date nowDate = DateUtils.getLastSecIntegralTime(new Date());
				logger.info("nowDate:  " + DateUtils.dateStr4(nowDate));
				logger.info("RepayTime:" + DateUtils.dateStr4(c.getRepayTime()));

				logger.info("体验金还款日期判断：" + nowDate.after(c.getRepayTime()));

				// 未还款 并且在还款时间在当前天内
				if (t.getStatus() == 0 && c.getStatus() == 0 && nowDate.after(c.getRepayTime())) { // DateUtils.rollDay(nowDate, 2)
					logger.info("开始还款：" + c.getId());
					// 归还投资人利息
					cInterest = NumberUtils.format2(cInterest);// 保留小数点
					if (cInterest > 0) { // total useMoney noUseMoney collection repay user
						accountDao.updateAccount(0, 0, 0, -cInterest, 0, tenderUser.getUserId());
						Account cAct = accountDao.getAcountByUser(tenderUser);
						AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.INTEREST_COLLECT, borrowUser.getUserId());
						fillAccountLog(cLog, Constant.INTEREST_COLLECT, cAct, tenderUser, borrowUser, cInterest, 0, "[" + getLogRemark(model) + "]体验金还款，归还利息" + cInterest);
						accountLogDao.save(cLog);

						// 更新tender记录
						t.setStatus(1);
						t.setWaitAccount((t.getWaitAccount() - cCapital)); // 不能小于0
						t.setWaitInterest((t.getWaitInterest() - cInterest));
						t.setRepaymentYesinterest(t.getRepaymentYesinterest() + cInterest);
						t.setRepaymentYescapital(t.getRepaymentYescapital() + cCapital);
						borrowTenderDao.update(t);
						// 更新collection记录
						c.setStatus(1);
						c.setRepayYestime(new Date());
						c.setRepayYesinterest(c.getInterest());
						c.setRepayYescapital(c.getCapital());
						c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_EXPERIENCEMONEY_PAY));
						borrowCollectionDao.save(c);

						// 更新体验金记录
						ExperienceMoney em = this.experienceMoneyDao.findByCriteriaForUnique(SearchParam.getInstance().addParam("user", t.getUser()));
						em.setInterestIncomeStatus(1);
						em.setInterestIncomeTime(new Date());
						this.experienceMoneyDao.update(em);
					}
				}
			}
		}

	}

	/**
	 * 正常还款
	 *
	 * @param repay
	 */
	@Override
	public void repay(BorrowRepayment repay) {
		logger.info("还款进来： " + repay.getId());

		List<Object> taskList = new ArrayList<Object>(); // 任务列表

		BorrowRepayment repayment = borrowRepaymentDao.find(repay.getId());
		if (repayment.getStatus() != 0) {
			logger.error("已经还款/网站垫付或正在还款中，请勿重复操作");
			return;
		}
		Borrow model = repayment.getBorrow();
		BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow 计算费用方法。
		User borrowUser = model.getUser();// 借款人
		Account at = accountDao.getAcountByUser(borrowUser);// 借款人账户
		// v1.8.0.3 TGPROJECT-15 qj 2014-04-02 start
		double fee = 0;
		Rule borrowFeeRule = ruleService.getRuleByNid("global_borrow_fee_rule");
		if (borrowFeeRule != null && borrowFeeRule.getStatus() == 1) {
			int type = borrowFeeRule.getValueIntByKey("collect_fee_type");// 收取管理费类型 0：满标复审时收取 1：还款时收取
			if (type == 1) { // 扣除借款管理费,每次还款只是扣除一期的管理费。
				fee = borrow.getManageFeeWithRule(1);
			}
		}

		// v1.8.0.3 TGPROJECT-15 qj 2014-04-02 stop
		if (at.getUseMoney() < repayment.getRepaymentAccount() + repayment.getLateInterest() + fee) {// 判断可用余额是否足够,逾期罚息也要算在内。
			throw new BussinessException("可用余额不足还款失败。");
		}

		double capital = repay.getCapital(); // 本金
		double interest = repay.getInterest();// 利息

		// 从用户冻结账户中扣除还款本金
		if (capital > 0) {
			accountDao.updateAccount(-capital, -capital, 0, 0, -capital, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAID_CAPTIAL, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAID_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), capital, 0, "[" + getLogRemark(model) + "]还款，扣除还款本金" + capital);
			accountLogDao.save(log);
		}

		// 扣除还款利息
		if (interest > 0) {
			accountDao.updateAccount(-interest, -interest, 0, 0, -interest, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAID_INTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAID_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), interest, 0, "[" + getLogRemark(model) + "]还款，扣除利息" + interest);
			accountLogDao.save(log);
		}

		double lateInterest = 0;
		lateInterest = repay.getLateInterest();
		// 扣除逾期罚息
		if (lateInterest > 0) {
			accountDao.updateAccount(-lateInterest, -lateInterest, 0, 0, 0, borrowUser.getUserId()); // 待还要 减掉
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.LATE_DEDUCT, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.LATE_DEDUCT, act, borrowUser, new User(Constant.ADMIN_ID), lateInterest, 0, "[" + getLogRemark(model) + "]还款，扣除逾期罚息" + lateInterest);
			accountLogDao.save(log);
		}

		// 如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
		if (model.getType() == Constant.TYPE_CREDIT) {// 还款解冻资金
			userAmountDao.updateCreditAmount(0, repay.getCapital(), -repay.getCapital(), borrowUser.getUserId());
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, Constant.REPAID, -repay.getCapital(), "还款，解冻信用额度：" + repay.getCapital(), ""));
		}
		// 逾期的利息特殊---平台和投资人平分
		double lateInterestPayApr = Global.getDouble("lateInterest_payAdmin");// 逾期罚息垫付给平台的百分比
		double adminLateInterest = NumberUtils.format2(lateInterest * lateInterestPayApr);// 平台应得逾期罚息
		double tendersLateInterest = lateInterest - adminLateInterest;// 所有投资人应得的利息
		int i = 0; // 记录循环次数
		double hasPayedLateInterest = 0;// 累计所有的投资人累计已经得到利息
		// 50%的逾期罚息 易极付转账给 平台账户。
		if (adminLateInterest > 0) {
			apiService.payManageFee(Global.getValue("guarantee_money_accoount"), NumberUtils.format2(adminLateInterest), taskList, borrow.getModel());
		}
		// v1.8.0.3 TGPROJECT-15 qj 2014-04-01 start
		// 管理费
		if (fee > 0) {
			accountDao.updateAccount(-fee, -fee, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(model.getUser());
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_FEE, Constant.ADMIN_ID);
			fillAccountLog(log, act, fee);
			log.setRemark("[" + getLogRemark(model) + "]还款，扣除" + Global.getString("trade_name_manage_fee") + fee);
			accountLogDao.save(log);
			// 调用第三方处理接口
			apiService.payManageFee(Global.getValue("guarantee_money_accoount"), NumberUtils.format2(fee), taskList, borrow.getModel());
		}
		// v1.8.0.3 TGPROJECT-15 qj 2014-04-01 stop

		// 查询此标当期的所有投资人，并循环投资人还款
		List<BorrowCollection> collectList = borrowCollectionDao.getCollectionByBorrowAndPeriod(model.getId(), repay.getPeriod());// 待收记录 参数标的ID和期数

		for (BorrowCollection c : collectList) {
			i++;
			double cCapital = c.getCapital();
			double cInterest = c.getInterest();
			BorrowTender t = c.getBorrowTender();
			User tenderUser = t.getUser();// 投资人
			// 归还投资人本金
			if (cCapital > 0) {
				accountDao.updateAccount(0, cCapital, 0, -cCapital, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);// 投资人账户
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.CAPITAL_COLLECT, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.CAPITAL_COLLECT, cAct, tenderUser, borrowUser, cCapital, 0, "[" + getLogRemark(model) + "]还款，归还本金" + cCapital);
				accountLogDao.save(cLog);
				// 调用第三方接口，添加还款本金任务
				apiService.repayLoanMoney(tenderUser.getApiId(), NumberUtils.format2(cCapital), taskList, borrow.getModel(), c, ApiMethodType.BORROW_REPAY_D);
			}

			// 归还投资人利息
			cInterest = NumberUtils.format2(cInterest);// 保留小数点
			if (cInterest > 0) {
				accountDao.updateAccount(0, cInterest, 0, -cInterest, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.INTEREST_COLLECT, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.INTEREST_COLLECT, cAct, tenderUser, borrowUser, cInterest, 0, "[" + getLogRemark(model) + "]还款，归还利息" + cInterest);
				accountLogDao.save(cLog);

				// 扣除投资人利息管理费
				double borrowFee = getInterestFeeWithRule(cInterest, tenderUser, model);

				if (borrowFee > 0) {
					accountDao.updateAccount(-borrowFee, -borrowFee, 0, 0, 0, tenderUser.getUserId());
					cAct = accountDao.getAcountByUser(tenderUser);
					cLog = new AccountLog(tenderUser.getUserId(), Constant.MANAGE_FEE, 1);
					fillAccountLog(cLog, Constant.MANAGE_FEE, cAct, tenderUser, new User(Constant.ADMIN_ID), borrowFee, 0, "[" + getLogRemark(model) + "]还款，扣除" + Global.getString("trade_name_interest_fee") + "！" + borrowFee);
					accountLogDao.save(cLog);
				}
				// 调用第三方接口，添加还款利息和利息管理费任务
				apiService.repayBorrowFee(model, repayment, tenderUser, borrowFee, cInterest, taskList, c, ApiMethodType.BORROW_REPAY_D);
				// 利息管理费记录在borrowCollection,borrowTender
				c.setInterestFee(borrowFee);
				t.setInterestFee(t.getInterestFee() + borrowFee);
				/**
				 * 汇丰贷 还款的时候记录推荐的推荐投资
				 */
				InviteUser it = inviteUserDao.getInviter(tenderUser.getUserId());
				if (it != null) {// 存在推荐人,就往联系人提成记录表中新增记录
					// Map<String,Double> map=inviteUserRebateService.calculateRebate(it,t);
					InviteUserRebate iur = new InviteUserRebate();
					iur.setBorrowTender(t);
					iur.setBorrowCollection(c);
					iur.setBorrowFee(borrowFee);
					iur.setInviteUser(it.getInviteUser());
					iur.setAddtime(new Date());
					iur.setAddip("");
					iur.setStatus(0);
					// double rebateAmount=borrowFee*map.get("rebateProportion");
					// iur.setRebateAmount(rebateAmount);
					// iur.setRebateProportion(map.get("rebateProportion")+"");
					inviteUserRebateDao.save(iur);

					// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
					// 网站其他关于推荐人业务处理
					webOtherInviteBusiness(it, borrowFee, t);
					// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end
				}
				// v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-22 end
			}
			// 还投资人 逾期的利息 按投资的比例来计算
			double totalRepay = model.getAccount(); // 此期借款总金额
			if (lateInterest > 0) {
				double tenderLateInterest = 0d;
				if (i - 1 == collectList.size()) {// 最后一期,用总的减去前面的和。
					tenderLateInterest = tendersLateInterest - hasPayedLateInterest;
				} else {// 其他期
					tenderLateInterest = NumberUtils.format2(tendersLateInterest * (c.getBorrowTender().getAccount() / totalRepay));
					hasPayedLateInterest += tenderLateInterest;
				}
				if (tenderLateInterest > 0) {
					accountDao.updateAccount(tenderLateInterest, tenderLateInterest, 0, 0, 0, tenderUser.getUserId());
					Account cAct = accountDao.getAcountByUser(tenderUser);
					AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.LATE_COLLECTION, borrowUser.getUserId());
					fillAccountLog(cLog, Constant.LATE_COLLECTION, cAct, tenderUser, borrowUser, tenderLateInterest, 0, "[" + getLogRemark(model) + "]还息，归还逾期罚息" + tenderLateInterest);
					cLog.setRemark("[" + getLogRemark(model) + "]还息，归还逾期罚息");
					accountLogDao.save(cLog);
					// 调用第三方接口，添加罚息任务
					apiService.repayLoanMoney(tenderUser.getApiId(), NumberUtils.format2(tenderLateInterest), taskList, borrow.getModel(), c, ApiMethodType.BORROW_REPAY_D);
				}
				c.setLateDays(repay.getLateDays());
				c.setLateInterest(tenderLateInterest);
			}

			if (borrow.isLastPeriod(repay.getPeriod())) {
				t.setStatus(1);
			}
			t.setWaitAccount((t.getWaitAccount() - cCapital)); // 不能小于0
			t.setWaitInterest((t.getWaitInterest() - cInterest));
			t.setRepaymentYesinterest(t.getRepaymentYesinterest() + cInterest);
			t.setRepaymentYescapital(t.getRepaymentYescapital() + cCapital);
			t.setLateAccount(t.getLateAccount() + lateInterest);
			// 更新tender记录
			borrowTenderDao.update(t);
			// 更新collection记录
			c.setStatus(1);
			c.setRepayYestime(new Date());
			c.setRepayYesinterest(c.getInterest());
			c.setRepayYescapital(c.getCapital());
			// 这里要区分，正常还款和逾期还款
			if (c.getRepayTime().compareTo(new Date()) > 0) {
				c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_BASE));
			} else {
				c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_LATE));
			}
			borrowCollectionDao.save(c);

		}
		int repayStatus = 7;
		if (borrow.isLastPeriod(repay.getPeriod())) {
			repayStatus = 8;
			// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
			riskFeeToBorrower(model, taskList);
			// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 end
		}
		model.setStatus(repayStatus);
		borrowDao.save(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setWebstatus(1);
		repay.setRepaymentYescapital(capital);
		repay.setRepaymentYesinterest(interest);
		if (repay.getRepaymentTime().compareTo(new Date()) > 0) {
			repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_BASE));
		} else {
			repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_LATE));
		}
		borrowRepaymentDao.update(repay);

		// wsl 满标前补偿金功能【心意贷】2014-08-29 start
		tenderCompensationService.tenderCompensationRepay(repayment, taskList);
		// wsl 满标前补偿金功能【心意贷】2014-08-29 end

		// 处理人任务。
		borrowService.doApiTask(taskList);

		// 还款，修改用户客户星级
		Date repayDate = repayment.getRepaymentTime();
		Date nowDate = new Date();
		double lateDays = (double) (nowDate.getTime() - repayDate.getTime()) / (double) (24 * 60 * 60 * 1000);
		if (lateDays > 0 && lateDays <= 3) { // 逾期三天及以下
			ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "late_less_3", borrowUser, 0);
		} else if (lateDays > 3) { // 逾期三天以上
			ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "late_than_3", borrowUser, 0);
		} else {// 正常还款
			ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "repay_ontime", borrowUser, 0);
		}
		if (lateDays > 0) {// 逾期还款,积分处理
			userCreditService.borrowRepayLateCredit(repayment, borrowUser);
		} else {// 正常还款，积分处理
			userCreditService.borrowRepayOnTimeCredit(repayment, borrowUser);
		}
	}

	// v1.8.0.4_u4 TGPROJECT-353 qinjun 2014-07-03 start
	private double getInterestFeeWithRule(double interest, User tenderUser, Borrow borrow) {
		double borrowFee = NumberUtils.format2(interest * Global.getDouble("borrow_fee"));
		Rule vipRule = ruleService.getRuleByNid("vip_rule");
		if (vipRule != null && vipRule.getStatus() == 1) {
			if (tenderUser.getUserCache().getVipStatus() == 1) {// vip会员
				borrowFee = NumberUtils.format2(interest * vipRule.getValueDoubleByKey("interest_manage_fee"));
			}
		}

		Rule addUserRule = ruleService.getRuleByNid("interest_fee_as_adduser");
		if (addUserRule != null && addUserRule.getStatus() == 1) {
			logger.info("标审核时间：" + borrow.getVerifyTime() + "   投资人注册时间：" + tenderUser.getAddtime());
			double time = (borrow.getVerifyTime().getTime() / 1000 - tenderUser.getAddtime().getTime() / 1000) / 2592000d;
			int month = NumberUtils.getIntWithDouble(time);
			borrowFee = NumberUtils.format2(interest * addUserRule.getValueByDiyCheckValue(month, Constant.INTEREST_FEE_RATE_RULE));
		}
		return borrowFee;
	}
	// v1.8.0.4_u4 TGPROJECT-353 qinjun 2014-07-03 end

	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start

	/**
	 * 其他网站推荐人业务处理
	 *
	 * @param userCache
	 * @param vipFee
	 */
	private void webOtherInviteBusiness(InviteUser itUser, double interestFee, BorrowTender tender) {
		Rule rule = ruleService.getRuleByNid("invite_award_rate");
		if (interestFee > 0 && rule != null && rule.getStatus() == 1) {
			if (itUser != null) {
				User inviteUser = itUser.getInviteUser();
				if (inviteUser != null) {
					int inviteTotal = inviteUserDao.getInviteUserTotal(inviteUser);
					double interest_rate = rule.getValueByDiyCheckValue(inviteTotal, Constant.INTEREST_INVITE_RATE_RULE);
					double fee = NumberUtils.format2(interestFee * interest_rate);
					String content = "用户" + itUser.getUser().getUsername() + "成功投资并回款，获得利息管理费提成：" + fee + "元";
					RewardExtend extend = new RewardExtend(RewardType.INTERESTINVITE, fee, 0, inviteUser, content, new Date());
					extend.setBorrow(tender.getBorrow());
					extend.setTenderUser(tender.getUser());
					rewardExtendDao.save(extend);
				}
			}
		}
	}
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end

	/**
	 * 还款给网站，资金类型，重新添加
	 */
	@Override
	public void repayToWebSite(BorrowRepayment repay) {
		logger.info("还款给网站垫付进来： " + repay.getId());
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		BorrowRepayment repayment = borrowRepaymentDao.find(repay.getId());
		if (repayment.getStatus() != 2 && repayment.getWebstatus() != 3) {
			logger.error("非还款给网站垫付状态,请勿重复操作");
			return;
		}
		Borrow model = repayment.getBorrow();
		User borrowUser = model.getUser();
		Account at = accountDao.getAcountByUser(borrowUser);
		//
		if (at.getUseMoney() < repayment.getRepaymentAccount()) {// 判断可用余额是否足够
			throw new BussinessException("可用余额不足还款给网站垫付失败。");
		}
		double capital = repay.getCapital(); // 本金
		double interest = repay.getInterest();// 利息
		BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow 计算费用方法。

		// 还本金
		if (capital > 0) {
			accountDao.updateAccount(-capital, -capital, 0, 0, -capital, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAY_WEB_CAPTIAL, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAY_WEB_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), capital, 0, "[" + getLogRemark(model) + "]还款给网站，扣除本金" + capital);
			accountLogDao.save(log);
		}
		// 还利息
		if (interest > 0) {
			accountDao.updateAccount(-interest, -interest, 0, 0, -interest, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAY_WEB_INTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAY_WEB_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), interest, 0, "[" + getLogRemark(model) + "]还款给网站，扣除利息" + interest);
			accountLogDao.save(log);

		}
		// 还逾期罚息
		double lateInterest = repay.getLateInterest();
		if (lateInterest > 0) {
			accountDao.updateAccount(-lateInterest, -lateInterest, 0, 0, 0, borrowUser.getUserId()); // 待还要 减掉
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAY_WEB_LATEINTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAY_WEB_LATEINTEREST, act, borrowUser, new User(Constant.ADMIN_ID), lateInterest, 0, "[" + getLogRemark(model) + "]还款给网站，扣除逾期罚息" + lateInterest);
			accountLogDao.save(log);
		}
		// 如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
		if (model.getType() == Constant.TYPE_CREDIT) {// 还款解冻资金
			userAmountDao.updateCreditAmount(0, repay.getCapital(), -repay.getCapital(), borrowUser.getUserId());
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, Constant.REPAID, -repay.getCapital(), "还款，解冻信用额度：" + repay.getCapital(), ""));
		}

		int repayStatus = 7;
		if (borrow.isLastPeriod(repay.getPeriod())) {
			repayStatus = 8;
		}
		model.setStatus(repayStatus);
		borrowDao.save(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setRepaymentYescapital(capital);
		repay.setRepaymentYesinterest(interest);
		repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PAY_WEB));
		borrowRepaymentDao.update(repay);
		// 添加扣款任务

		// wsl 满标前补偿金功能【心意贷】2014-08-29 start
		tenderCompensationService.borrowUserPaySiteCompensation(repay, taskList);
		// wsl 满标前补偿金功能【心意贷】2014-08-29 end

		borrowService.doApiTask(taskList); // 处理易极付还款任务。

	}

	@Override
	public void flowRepay(BorrowRepayment r) { // 流标还款,按照repayement 的顺序来还款
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		ApiPayParamModel apiModel = new ApiPayParamModel();// 易极付拼接参数所用类
		BorrowRepayment repayment = borrowRepaymentDao.find(r.getId()); // 当前还款计划
		if (repayment.getStatus() == 1) {
			logger.error("该改还款计划已经还款");
			return;
		}
		Borrow model = repayment.getBorrow();
		User borrowUser = model.getUser();
		Account actCheck = accountDao.getAcountByUser(borrowUser);
		if (actCheck.getUseMoney() < repayment.getRepaymentAccount()) {
			logger.info("账户可用余额不足，流标还款失败 borrowId: " + model.getId() + " tenderId: " + repayment.getBorrowTender().getId() + " userId: " + borrowUser.getUserId());
			return;
		}
		double reapyments = repayment.getRepaymentAccount();// 还款本息
		double interest = repayment.getInterest();// 利息
		if (reapyments > 0) { // 扣除还款的本息
			accountDao.updateAccount(-reapyments, -reapyments, 0, 0, -reapyments, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.REPAID, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.REPAID, act, borrowUser, new User(Constant.ADMIN_ID), reapyments, 0, "[" + getLogRemark(model) + "]还款，扣除还款本息" + reapyments);
			accountLogDao.save(log);
		}
		BorrowTender t = repayment.getBorrowTender();
		User tenderUser = t.getUser(); // 投资人
		// 增加投资人的可用金额
		accountDao.updateAccount(0, reapyments, 0, -reapyments, 0, tenderUser.getUserId());
		Account cAct = accountDao.getAcountByUser(tenderUser);
		AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.REPAID, borrowUser.getUserId());
		fillAccountLog(cLog, Constant.REPAID, cAct, tenderUser, borrowUser, reapyments, 0, "[" + getLogRemark(model) + "]还款，流转标归还本息" + reapyments);
		accountLogDao.save(cLog);

		// 扣除投资人的利息管理费----->易极付转账实现
		double borrow_fee_precent = Global.getDouble("borrow_fee");
		double borrow_fee = NumberUtils.format2(borrow_fee_precent * interest);
		if (tenderUser.getUserCache().getVipStatus() == 1) {// vip会员
			Rule rule = ruleService.getRuleByNid("vip_rule");
			if (rule.getStatus() == 1) {// 启用vip规则
				borrow_fee = NumberUtils.format2(interest * rule.getValueDoubleByKey("interest_manage_fee"));
			}
		}
		if (borrow_fee > 0) {
			accountDao.updateAccount(-borrow_fee, -borrow_fee, 0, 0, 0, tenderUser.getUserId());
			cAct = accountDao.getAcountByUser(tenderUser);
			AccountLog LogFee = new AccountLog(tenderUser.getUserId(), Constant.MANAGE_FEE, Constant.ADMIN_ID);
			fillAccountLog(LogFee, Constant.MANAGE_FEE, cAct, tenderUser, new User(Constant.ADMIN_ID), borrow_fee, 0, "[" + getLogRemark(model) + "]流转标还款，扣除" + Global.getString("trade_name_interest_fee") + "!" + borrow_fee);
			accountLogDao.save(LogFee);
		}
		// 更新collection记录
		BorrowCollection c = t.getBorrowCollections().get(0);
		if (c != null) {
			c.setStatus(1);
			c.setRepayYestime(new Date());
			c.setRepayYescapital(c.getCapital());
			c.setRepayYesinterest(c.getInterest());
			c.setInterestFee(borrow_fee);// add borrrow_fee
			t.setInterestFee(t.getInterestFee() + borrow_fee);
			c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_FLOW));
			borrowCollectionDao.update(c);
		} else {
			logger.warn("无待收计划更新！");
		}
		// 释放流转标已经回款的金额
		model.setAccountYes(model.getAccountYes() - repayment.getCapital());
		if (model.getAccountYes() < 0) {
			throw new BussinessException("释放流转标已经回款的金额！");
		}
		borrowDao.update(model);

		t.setWaitAccount(t.getWaitAccount() - repayment.getCapital());
		t.setWaitInterest(t.getWaitInterest() - interest);
		t.setRepaymentYescapital(repayment.getCapital());
		t.setRepaymentYesinterest(interest);
		t.setStatus(1);// 状态改成完成还款。
		borrowTenderDao.update(t);

		repayment.setRepaymentYestime(new Date());
		repayment.setStatus(1);
		repayment.setWebstatus(1);
		repayment.setRepaymentYescapital(repayment.getCapital());
		repayment.setRepaymentYesinterest(interest);
		repayment.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_FLOW));
		borrowRepaymentDao.update(repayment);
	}

	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
	private void riskFeeToBorrower(Borrow borrow, List<Object> taskList) {
		double fee = 0;
		// 风险准备金,转到特殊的保证金账号。
		Rule riseRule = ruleService.getRuleByNid("risk_reserve_rule");// 风险准备金规则规则
		if (riseRule != null && riseRule.getStatus() == 1) {
			if (riseRule.getValueIntByKey("repay_to_borrow") == 1) {// 是否归还保证金
				String repayBorrowType = riseRule.getValueStrByKey("borrow_type");
				if (!StringUtils.isNull(repayBorrowType).contains(borrow.getType() + "")) {
					logger.info("标类型:" + borrow.getType() + "标id：" + borrow.getId() + ",不归还保证金");
					return;
				}
				if (borrow.getType() == Constant.TYPE_SECOND) {
					logger.info("秒标不收备用金");
				} else {// 风险备用金，从borrow_fee 表中取。
					double borrow_fee = 0d;
					Rule riseCreditRule = ruleService.getRuleByNid("risk_credit_rule");// 风险准备金信用等级规则
					if (riseCreditRule != null && riseCreditRule.getStatus() == 1) {
						String rank = borrow.getUser().getCreditRank();
						if (!StringUtils.isBlank(rank)) {
							borrow_fee = riseCreditRule.getValueDoubleByKey(rank.toLowerCase());
						}
					} else {
						BorrowFee borrowFee = borrow.getBorrowFee();
						if (borrowFee == null) {
							logger.info("标：" + borrow.getId() + " 没有设置风险备用金");
						} else {
							borrow_fee = borrowFee.getRiskreserve();
							logger.info("标：" + borrow.getId() + " 备用金收取比例：" + borrow_fee);
						}
					}
					fee = NumberUtils.format2(borrow_fee * borrow.getAccount());
					if (fee > 0) {
						accountDao.updateAccount(fee, fee, 0, 0, 0, borrow.getUser().getUserId());
						Account act = accountDao.getAcountByUser(borrow.getUser());
						AccountLog log = new AccountLog(borrow.getUser().getUserId(), Constant.RISKRESERVE_FEE_TO_BORROWER, Constant.ADMIN_ID);
						fillAccountLog(log, act, fee);
						log.setRemark("[" + getLogRemark(borrow) + "]还款完成，归还风险备用金：" + fee);
						accountLogDao.save(log);
						apiService.webTransfer(taskList, borrow.getUser(), fee);
					}
				}
			}
		}
	}
	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 end

	private void deductFee(BorrowModel borrow, List<Object> taskList, User borrowUser, List<BorrowTender> tenderList) {
		double fee = 0;
		Borrow model = borrow.getModel();

		// 扣除交易手续费
		fee = madeBorrowFee(borrow);

		if (fee > 0) {
			accountDao.updateAccount(-fee, -fee, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(model.getUser());
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.BORROW_FEE, Constant.ADMIN_ID);
			fillAccountLog(log, act, fee);
			log.setRemark("[" + getLogRemark(model) + "]" + Global.getString("trade_name_manage_fee") + fee);
			accountLogDao.save(log);

			// 第三方接口扣费
			apiService.FullSuccessDeductFee(taskList, tenderList, fee, model, "");
		}

		// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 start
		// 处理其他自定义管理费
		deductOtherDiyFee(taskList, model);
		// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 end

		// 风险准备金,转到特殊的保证金账号。
		Rule riseRule = ruleService.getRuleByNid("risk_reserve_rule");// 风险准备金规则规则
		if (riseRule != null) {
			if (riseRule.getStatus() == 1) {
				if (model.getType() == Constant.TYPE_SECOND) {
					logger.info("秒标不收备用金");
				} else {// 风险备用金，从borrow_fee 表中取。
					double borrow_fee = 0d;
					Rule riseCreditRule = ruleService.getRuleByNid("risk_credit_rule");// 风险准备金信用等级规则
					if (riseCreditRule != null && riseCreditRule.getStatus() == 1) {
						String rank = borrowUser.getCreditRank();
						if (!StringUtils.isBlank(rank)) {
							borrow_fee = riseCreditRule.getValueDoubleByKey(rank.toLowerCase());
						}
					} else {
						BorrowFee borrowFee = borrow.getModel().getBorrowFee();
						if (borrowFee == null) {
							logger.info("标：" + model.getId() + " 没有设置风险备用金");
						} else {
							borrow_fee = borrowFee.getRiskreserve();
							logger.info("标：" + model.getId() + " 备用金收取比例：" + borrow_fee);
						}
					}
					fee = NumberUtils.format2(borrow_fee * model.getAccount());
					if (fee > 0) {
						accountDao.updateAccount(-fee, -fee, 0, 0, 0, borrowUser.getUserId());
						Account act = accountDao.getAcountByUser(model.getUser());
						AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.RISKRESERVE_FEE, Constant.ADMIN_ID);
						fillAccountLog(log, act, fee);
						log.setRemark("[" + getLogRemark(model) + "]风险备用金" + fee);
						accountLogDao.save(log);
						apiService.FullSuccessRiskFee(taskList, tenderList, fee, model, "");
					}
				}
			}
		}

	}

	// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 start

	/**
	 * 帝友扣费方法
	 */
	private void deductOtherDiyFee(List<Object> taskList, Borrow borrow) {
		borrow = borrowDao.find(borrow.getId());
		BorrowFee borrowFee = borrow.getBorrowFee();
		double fee = 0d;
		User user = null;
		// 平台管理费
		double webManageFee = borrowFee.getWebManageFee();
		user = borrowFee.getWebManageUser();
		fee = NumberUtils.format2(webManageFee * borrow.getAccount());
		publicDyDedcutFeeToUser(taskList, borrow, "满标复审收取【平台管理费】", fee, user, Constant.WEB_MANAGE_FEE);
		// 小贷审核费
		double smallLoanFee = borrowFee.getSmallLoanFee();
		user = borrowFee.getSmallLoanUser();
		fee = NumberUtils.format2(smallLoanFee * borrow.getAccount());
		publicDyDedcutFeeToUser(taskList, borrow, "满标复审收取【小贷审核费】", fee, user, Constant.SMALL_LOAN_FEE);
		// 担保公司担保费
		double warrantFee = borrowFee.getWarrantFee();
		user = borrowFee.getWarrantUser();
		fee = NumberUtils.format2(warrantFee * borrow.getAccount());
		publicDyDedcutFeeToUser(taskList, borrow, "满标复审收取【担保公司担保费】", fee, user, Constant.WARRANT_FEE);
		// 服务公司推荐费
		double introduceFee = borrowFee.getIntroduceFee();
		user = borrowFee.getIntroduceUser();
		fee = NumberUtils.format2(introduceFee * borrow.getAccount());
		publicDyDedcutFeeToUser(taskList, borrow, "满标复审收取【服务公司推荐费】", fee, user, Constant.INTRODUCE_FEE);
	}

	private void publicDyDedcutFeeToUser(List<Object> taskList, Borrow borrow, String feeRemarkType, double fee, User toUser, String feeType) {
		if (toUser != null && fee != 0) {
			// 借款人扣款
			accountDao.updateAccount(-fee, -fee, 0, 0, 0, borrow.getUser().getUserId());
			Account act = accountDao.getAcountByUser(borrow.getUser());
			AccountLog log = new AccountLog(borrow.getUser().getUserId(), feeType, toUser.getUserId());
			fillAccountLog(log, act, fee);
			log.setRemark("[" + getLogRemark(borrow) + "]" + feeRemarkType + "：" + fee);
			accountLogDao.save(log);
			// 收款人收款
			accountDao.updateAccount(fee, fee, 0, 0, 0, toUser.getUserId());
			Account toUserAct = accountDao.getAcountByUser(toUser);
			AccountLog toUserLog = new AccountLog(toUser.getUserId(), feeType, borrow.getUser().getUserId());
			fillAccountLog(toUserLog, toUserAct, fee);
			toUserLog.setRemark("[" + getLogRemark(borrow) + "]" + feeRemarkType + "：" + fee);
			accountLogDao.save(toUserLog);
			// 第三方接口扣费
			apiService.borrowertTransferToUser(taskList, fee, borrow, toUser);
		}
	}
	// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 end

	/**
	 * 满标复审收取借款管理费的方法， 备注：是否重新计算
	 *
	 * @return
	 */
	// v1.8.0.3 TGPROJECT-15 qj 2014-04-01 start
	public double madeBorrowFee(BorrowModel borrow) {
		double fee = 0;
		Borrow model = borrow.getModel();

		Rule globalBorrowFeeRule = ruleService.getRuleByNid("global_borrow_fee_rule");// 收取管理费规则，全局在用

		Rule gkxwFeeRule = ruleService.getRuleByNid("gkxw_borrow_fee_rule");// 获取国控小微收取管理费规则

		if (gkxwFeeRule != null && gkxwFeeRule.getStatus() == 1) {
			double base_fee = gkxwFeeRule.getValueDoubleByKey("base_fee"); // 初始管理费
			if (base_fee != 0) {
				fee = model.getAccount() * base_fee / 100;
			}
			if (fee == 0) {// 国控小微在用
				// 获取第一个区间值：
				int lossFeea = gkxwFeeRule.getValueIntByKey("low_fee_a");// 获取区间值一、1
				int hightFeea = gkxwFeeRule.getValueIntByKey("high_fee_a"); // 获取区间值一、2
				double feea = gkxwFeeRule.getValueDoubleByKey("fee_a"); // 第一个区间值的费率
				int borrowTime = model.getTimeLimit(); // 借款期限
				if (lossFeea <= borrowTime && borrowTime <= hightFeea) { // 第一个借款区间比较
					fee = model.getAccount() * feea / 100;
				}
				if (fee == 0) {
					// 获取第二个区间值
					int lossFeeb = gkxwFeeRule.getValueIntByKey("low_fee_b"); // 收取管理费区间值二、1
					int hightFeeb = gkxwFeeRule.getValueIntByKey("high_fee_b");// 收取管理费区间二、2
					double feeb = gkxwFeeRule.getValueDoubleByKey("fee_b"); // 第二个区间的管理费
					if (lossFeeb <= borrowTime && borrowTime <= hightFeeb) { // 第二个借款区间比较
						fee = model.getAccount() * feeb / 100;
					}
				}
			}
		} else {
			if (model.getIsAssignment() == 1) {// 债权转让标的借款管理费收取
				Rule assignment_rule = ruleService.getRuleByNid("assignment_borrow_fee_rule");// 获取收取管理费规则
				if (assignment_rule != null && assignment_rule.getStatus() == 1) {
					double feeb = assignment_rule.getValueDoubleByKey("fee"); // 债权转让管理费
					fee = model.getAccount() * feeb;
				}
			} else {// 非债权转让标的收费方式，正常标的管理费收取
				if (globalBorrowFeeRule != null && globalBorrowFeeRule.getStatus() == 1) {
					int type = globalBorrowFeeRule.getValueIntByKey("collect_fee_type");// 收取管理费类型 0：满标复审时收取所有期数 1：还款时收取每期
					if (type == 0) {// 0:满标复审，一次性扣除所有期的管理费；
						// v1.8.0.4_u4 TGPROJECT-350 2014-07-02 qinjun start
						int monthType = globalBorrowFeeRule.getValueIntByKey("month_fee_type");
						if (monthType == 1) {
							fee = borrow.getManageFeeWithRule(1);
						} else {
							fee = borrow.getManageFeeWithRule(model.getTimeLimit());
						}

						// v1.8.0.4_u4 TGPROJECT-350 2014-07-02 qinjun start
					} else { // 1：还款时收取费用，只收取一期的管理费用
						logger.info("网站配置管理费，在还款的时候收取。");
					}
				}
			}
		}
		fee = NumberUtils.format2(fee);
		return fee;
	}
	// v1.8.0.3 TGPROJECT-15 qj 2014-04-01 stop

	/**
	 * 用于处理平台在满标复审时收取额外的费用方法
	 *
	 * @param borrow
	 * @param taskList
	 * @param borrowUser
	 * @param tenderList
	 * @param type
	 */
	private void borrowUserPayAdmin(BorrowModel model, List<Object> taskList, User borrowUser, List<BorrowTender> tenderList, String type) {
		Borrow borrow = model.getModel();
		Rule rule = ruleService.getRuleByNid("collect_contract"); // 校验是否收取合同费
		if (rule != null && rule.getStatus() == 1) {
			double contractFee = rule.getValueDoubleByKey("contractFee"); // 合同费
			accountDao.updateAccount(-contractFee, -contractFee, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrow.getUser());
			AccountLog log = new AccountLog(borrowUser.getUserId(), type, Constant.ADMIN_ID);
			fillAccountLog(log, act, contractFee);
			log.setRemark("[" + borrow.getName() + "]扣除借款合同登记费" + contractFee);
			accountLogDao.save(log);
			apiService.FullSuccessDeductFee(taskList, tenderList, contractFee, borrow, "");
		}
	}

	/**
	 * 发送消息
	 *
	 * @param req
	 */
	@Override
	public void msgReq(MsgReq req) {
		this.msgRecordService.send(req);
	}

	/**
	 * 计算逾期罚息，lateInterestType : 1 代表融都标准算法，2及其他代表客户自己的算法。
	 */
	@Override
	public void lateDaysAndInterest() {// 计算逾期罚息
		int lateInterestType = Global.getInt("late_interest_type");
		switch (lateInterestType) {
		case 1:// 代表融都标准算法
			lateInterestService.calculateCommonLateInterest();
			break;
		case 2:// 国控小微计算方法
			lateInterestService.calculateGkxw();
			break;
		case 3: // 部分还款后，计算罚息
			lateInterestService.calculatePartRepayment();
		case 4:
			lateInterestService.calculateYDD();
			break;
		default:
			lateInterestService.calculateCommonLateInterest();
		}
	}

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-12 qinjun start
	@Override
	public void hfPriorRepay(BorrowRepayment repay, PriorRepayLog ppLog) throws Exception {
		logger.info("提前还款进来： " + repay.getId());
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		BorrowRepayment repayment = borrowRepaymentDao.find(repay.getId());
		if (repayment.getStatus() != 0) {
			logger.error("已经还款/网站垫付或正在还款中，请勿重复 操作");
			return;
		}
		Borrow model = repayment.getBorrow();
		User borrowUser = model.getUser();
		Account at = accountDao.getAcountByUser(borrowUser);
		double old_repayCapital = borrowRepaymentDao.getRemainderCapital(model.getId());
		double old_repayInterest = borrowRepaymentDao.getRemainderInterest(model.getId());
		Date startDate = new Date();
		if (model.getStatus() == 6) {
			startDate = DateUtils.getNextDayYYYYMMdd(model.getVerifyTime());
		} else if (model.getStatus() == 7) {
			startDate = borrowRepaymentDao.getRecentlyRepayment(model.getId()).getRepaymentTime();
		} else {
			throw new BussinessException("您的借款标状态异常！");
		}
		long startTime = DateUtils.getTime(startDate); // 前一个还款时间或满标复审时间-
		long nowTime = NumberUtils.getLong(DateUtils.getNowTimeStr()); // 当前还款的
		// 计算提前还款日中间的时间间隔
		long day = (nowTime - startTime) % (60 * 60 * 24) > 0 ? (nowTime - startTime) / (60 * 60 * 24) + 1 : (nowTime - startTime) / (60 * 60 * 24);
		day = day < 0 ? 0 : day;
		// 还款利息
		double repayInterest_ratio = repay.getBorrow().getApr() * day / 36500;
		double repayInterest = repayInterest_ratio * old_repayCapital;
		// 计算补偿金
		double compenMoney_ratio = NumberUtils.getDouble2(Global.getString("prior_repay_compen_ratio")) * repay.getBorrow().getApr() / 36500;
		double compenMoney = compenMoney_ratio * old_repayCapital;

		double sumRepayMoney = old_repayCapital + repayInterest + compenMoney; // 计算应还总额
		if (at.getUseMoney() < sumRepayMoney) {// 判断可用余额是否足够
			throw new BussinessException("可用余额不足还款失败。");
		}

		repayInterest = 0;
		compenMoney = 0;

		// 查询此标当期的所有投资人，并循环投资人还款
		List<BorrowCollection> collectList = borrowCollectionDao.getCollectionByBorrowAndPeriod(model.getId(), repay.getPeriod());

		for (BorrowCollection c : collectList) {
			double cCapital = borrowCollectionDao.getRemainderCapital(c.getBorrowTender().getId());
			double old_cInterest = borrowCollectionDao.getRemainderInterest(c.getBorrowTender().getId());
			double cInterest = cCapital * repayInterest_ratio;
			double cCompenMoney = NumberUtils.formatTo2(compenMoney_ratio * cCapital);
			BorrowTender t = c.getBorrowTender();
			User tenderUser = t.getUser();
			// 归还投资人本金
			if (cCapital > 0) {
				accountDao.updateAccount(0, cCapital, 0, -cCapital, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_CAPTIAL, cAct, tenderUser, borrowUser, cCapital, 0, getLogRemark(model) + "提前还款，收到归还本金" + cCapital);
				accountLogDao.save(cLog);

			}

			// 归还投资人利息
			double borrowFee = 0;
			if (cInterest >= 0) {
				accountDao.updateAccount(cInterest - c.getInterest(), cInterest, 0, -old_cInterest, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_INTEREST, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_INTEREST, cAct, tenderUser, borrowUser, cInterest, 0, getLogRemark(model) + "提前还款，收到实际归还利息" + cInterest + ",应还利息：" + old_cInterest);
				accountLogDao.save(cLog);
				// 计算实还利息
				repayInterest += cInterest;
				// 扣除投资人利息管理费
				double borrow_fee = Global.getDouble("borrow_fee");
				borrowFee = NumberUtils.format2(cInterest * borrow_fee);
				if (tenderUser.getUserCache().getVipStatus() == 1) {// vip会员
					Rule rule = ruleService.getRuleByNid("vip_rule");
					if (rule.getStatus() == 1) {// 启用vip规则
						borrowFee = NumberUtils.format2(cInterest * rule.getValueDoubleByKey("interest_manage_fee"));
					}
				}

				if (borrowFee > 0) {
					accountDao.updateAccount(-borrowFee, -borrowFee, 0, 0, 0, tenderUser.getUserId());
					cAct = accountDao.getAcountByUser(tenderUser);
					cLog = new AccountLog(tenderUser.getUserId(), Constant.MANAGE_FEE, 1);
					fillAccountLog(cLog, Constant.MANAGE_FEE, cAct, tenderUser, new User(Constant.ADMIN_ID), borrowFee, 0, getLogRemark(model) + "提前还款，扣除" + Global.getString("trade_name_interest_fee") + ":" + borrowFee);
					accountLogDao.save(cLog);
				}
			}
			// 还款补偿金
			if (cCompenMoney > 0) {
				accountDao.updateAccount(cCompenMoney, cCompenMoney, 0, 0, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIOR_REPAID_COMPEN, cAct, tenderUser, borrowUser, cCompenMoney, 0, getLogRemark(model) + "提前还款，收到补偿金:" + cCompenMoney);
				accountLogDao.save(cLog);
				// 计算还款补偿金
				compenMoney += cCompenMoney;
			}
			t.setWaitAccount(0); // 不能小于0
			t.setWaitInterest(0);
			t.setRepaymentYescapital(t.getRepaymentYescapital() + cCapital);
			t.setRepaymentYesinterest(t.getRepaymentYesinterest() + cInterest);
			t.setInterestFee(t.getInterestFee() + borrowFee);
			t.setCompensation(t.getCompensation() + cCompenMoney);
			// 更新tender记录
			borrowTenderDao.update(t);
			// 更新collection记录
			c.setStatus(1);
			c.setRepayYestime(new Date());
			c.setRepayYesinterest(cInterest);
			c.setRepayYescapital(cCapital);
			c.setInterestFee(borrowFee);
			c.setCompensation(cCompenMoney);
			c.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
			borrowCollectionDao.save(c);
			borrowCollectionDao.updateCollectionsPriorRepay(c.getBorrowTender().getId());
		}

		// 从用户冻结账户中扣除还款本金
		if (old_repayCapital > 0) {
			accountDao.updateAccount(-old_repayCapital, -old_repayCapital, 0, 0, -old_repayCapital, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), old_repayCapital, 0, getLogRemark(model) + "提前还款，扣除还款本金" + old_repayCapital);
			accountLogDao.save(log);
			// 添加提前还款记录
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_CAPTIAL, old_repayCapital, old_repayCapital);
			priorRepayLogDao.save(ppLog);
		}
		repayInterest = NumberUtils.formatTo2(repayInterest);
		// 扣除还款利息
		if (repayInterest >= 0) {
			accountDao.updateAccount(-repayInterest, -repayInterest, 0, 0, -old_repayInterest, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_INTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), repayInterest, 0, getLogRemark(model) + "提前还款，扣除利息" + old_repayInterest + "实际利息：" + repayInterest);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_INTEREST, old_repayInterest, repayInterest);
			priorRepayLogDao.save(ppLog);
		}
		compenMoney = NumberUtils.formatTo2(compenMoney);
		if (compenMoney > 0) {
			accountDao.updateAccount(-compenMoney, -compenMoney, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIOR_REPAID_COMPEN, act, borrowUser, new User(Constant.ADMIN_ID), compenMoney, 0, getLogRemark(model) + "提前还款，扣除补偿金" + compenMoney);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIOR_REPAID_COMPEN, compenMoney, compenMoney);
			priorRepayLogDao.save(ppLog);
		}
		// 如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
		if (model.getType() == Constant.TYPE_CREDIT) {
			userAmountDao.updateCreditAmount(0, old_repayCapital, -old_repayCapital, borrowUser.getUserId());
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, Constant.REPAID, -old_repayCapital, "还款，解冻信用额度：" + old_repayCapital, ""));
		}

		model.setStatus(8);
		borrowDao.save(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setWebstatus(1);
		repay.setRepaymentYesinterest(repayInterest);
		repay.setCompensation(compenMoney);
		repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
		borrowRepaymentDao.update(repay);
		// 更新剩余还款计划
		borrowRepaymentDao.updatePriorRepay(model.getId());
		// 处理任务。
		borrowService.doApiTask(taskList);
	}
	// v1.8.0.3_u3 TGPROJECT-334 2014-06-12 qinjun end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start
	@Override
	public void LZFPriorRepay(BorrowRepayment repay, PriorRepayLog ppLog) throws Exception {
		logger.info("提前还款进来： " + repay.getId());
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		BorrowRepayment repayment = borrowRepaymentDao.find(repay.getId());
		if (repayment.getStatus() != 0) {
			logger.error("已经还款/网站垫付或正在还款中，请勿重复 操作");
			return;
		}
		Borrow model = repayment.getBorrow();
		BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow 计算费用方法。
		if (borrow.isLastPeriod(repay.getPeriod())) { // 判断是否为最后一期，当未最后一期时，不允许提前还款
			throw new BussinessException("你正在操作的提前还款为最后一期还款，不能进行提前还款操作！");
		}
		User borrowUser = model.getUser();
		Account at = accountDao.getAcountByUser(borrowUser);
		// 计算用户可用资金是否足够提前还款金额
		double waitOldRpayCapital = borrowRepaymentDao.getRemainderCapital(model.getId()); // 计算剩余待还本金
		double old_repayInterest = borrowRepaymentDao.getRemainderInterest(model.getId()); // 原始待还利息总额
		double waitRepayInterest = borrowRepaymentDao.getwaitRpayInterest(model.getId(), repay.getPeriod()); // 本次提前还款待还利息总和
		double sumWaitAccount = waitOldRpayCapital + waitRepayInterest; // 计算待还总额
		if (at.getUseMoney() < sumWaitAccount) { // 判断用户账户可用金额
			throw new BussinessException("你的账户可用金额不足以支付提前还款总额，请充值后在进行还款！！");
		}
		waitRepayInterest = 0; // 将待还本期利息归0；
		double waitRepaycCompenMoney = 0; // 待还罚息利息
		// 查询此标当期的所有投资人，并循环投资人还款
		List<BorrowCollection> collectList = borrowCollectionDao.getCollectionByBorrowAndPeriod(model.getId(), repay.getPeriod());
		for (BorrowCollection bc : collectList) {
			double cCapital = borrowCollectionDao.getRemainderCapital(bc.getBorrowTender().getId()); // 计算投资人剩余待还本金
			double old_cInterest = borrowCollectionDao.getRemainderInterest(bc.getBorrowTender().getId()); // 所有待收利息
			double nowInterest = bc.getInterest(); // 当期应收利息
			BorrowTender t = bc.getBorrowTender();
			User tenderUser = t.getUser();
			// 归还投资人本金
			if (cCapital > 0) {
				accountDao.updateAccount(0, cCapital, 0, -cCapital, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_CAPTIAL, cAct, tenderUser, borrowUser, cCapital, 0, getLogRemark(model) + "提前还款，收到归还本金" + cCapital);
				accountLogDao.save(cLog);
			}
			// 归还投资人利息
			double borrowFee = 0;
			if (nowInterest >= 0) {
				accountDao.updateAccount(nowInterest, nowInterest, 0, -old_cInterest, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIORREPAID_INTEREST, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIORREPAID_INTEREST, cAct, tenderUser, borrowUser, nowInterest, 0, getLogRemark(model) + "提前还款，收到实际归还利息" + nowInterest + ",应还利息：" + old_cInterest);
				accountLogDao.save(cLog);
				// 计算实还利息
				waitRepayInterest += nowInterest;
				// 扣除投资人利息管理费
				double borrow_fee = Global.getDouble("borrow_fee");
				borrowFee = NumberUtils.format2(nowInterest * borrow_fee);
				if (tenderUser.getUserCache().getVipStatus() == 1) {// vip会员
					Rule rule = ruleService.getRuleByNid("vip_rule");
					if (rule.getStatus() == 1) {// 启用vip规则
						borrowFee = NumberUtils.format2(nowInterest * rule.getValueDoubleByKey("interest_manage_fee"));
					}
				}

				if (borrowFee > 0) {
					accountDao.updateAccount(-borrowFee, -borrowFee, 0, 0, 0, tenderUser.getUserId());
					cAct = accountDao.getAcountByUser(tenderUser);
					cLog = new AccountLog(tenderUser.getUserId(), Constant.MANAGE_FEE, 1);
					fillAccountLog(cLog, Constant.MANAGE_FEE, cAct, tenderUser, new User(Constant.ADMIN_ID), borrowFee, 0, getLogRemark(model) + "提前还款，扣除" + Global.getString("trade_name_interest_fee") + ":" + borrowFee);
					accountLogDao.save(cLog);
				}
			}
			// 提前还款多支付投资者一个月利息（下期的待收利息）
			BorrowCollection collection = borrowCollectionDao.getCollectionByTenderAndPeriod(t.getId(), repay.getPeriod() + 1);
			double cCompenMoney = collection.getInterest(); // 当期还款的下一期利息
			// 还款补偿金
			if (cCompenMoney > 0) {
				accountDao.updateAccount(cCompenMoney, cCompenMoney, 0, 0, 0, tenderUser.getUserId());
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog cLog = new AccountLog(tenderUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, borrowUser.getUserId());
				fillAccountLog(cLog, Constant.PRIOR_REPAID_COMPEN, cAct, tenderUser, borrowUser, cCompenMoney, 0, getLogRemark(model) + "提前还款，收到补偿金:" + cCompenMoney);
				accountLogDao.save(cLog);
				// 计算还款补偿金
				waitRepaycCompenMoney += cCompenMoney;
			}

			t.setWaitAccount(0); // 不能小于0
			t.setWaitInterest(0);
			t.setRepaymentYescapital(t.getRepaymentYescapital() + cCapital);
			t.setRepaymentYesinterest(t.getRepaymentYesinterest() + nowInterest);
			t.setInterestFee(t.getInterestFee() + borrowFee);
			t.setCompensation(t.getCompensation() + cCompenMoney);
			// 更新tender记录
			borrowTenderDao.update(t);
			// 更新collection记录
			bc.setStatus(1);
			bc.setRepayYestime(new Date());
			bc.setRepayYesinterest(nowInterest);
			bc.setRepayYescapital(cCapital);
			bc.setInterestFee(borrowFee);
			bc.setCompensation(cCompenMoney);
			bc.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
			borrowCollectionDao.save(bc);
			borrowCollectionDao.updateCollectionsPriorRepay(bc.getBorrowTender().getId());
		}

		// 操作借款人账户信息
		// 从用户冻结账户中扣除还款本金
		waitOldRpayCapital = NumberUtils.formatTo2(waitOldRpayCapital);
		if (waitOldRpayCapital > 0) {
			accountDao.updateAccount(-waitOldRpayCapital, -waitOldRpayCapital, 0, 0, -waitOldRpayCapital, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_CAPTIAL, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), waitOldRpayCapital, 0, getLogRemark(model) + "提前还款，扣除还款本金" + waitOldRpayCapital);
			accountLogDao.save(log);
			// 添加提前还款记录
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_CAPTIAL, waitOldRpayCapital, waitOldRpayCapital);
			priorRepayLogDao.save(ppLog);
		}

		// 扣除还款利息
		waitRepayInterest = NumberUtils.formatTo2(waitRepayInterest);
		if (waitRepayInterest > 0) {
			accountDao.updateAccount(-waitRepayInterest, -waitRepayInterest, 0, 0, -old_repayInterest, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIORREPAID_INTEREST, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIORREPAID_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), waitRepayInterest, 0, getLogRemark(model) + "提前还款，扣除利息" + old_repayInterest + "实际利息：" + waitRepayInterest);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIORREPAID_INTEREST, old_repayInterest, waitRepayInterest);
			priorRepayLogDao.save(ppLog);
		}

		// 扣除借款人提前还款罚息
		waitRepaycCompenMoney = NumberUtils.format2(waitRepaycCompenMoney);
		if (waitRepaycCompenMoney > 0) {
			accountDao.updateAccount(-waitRepaycCompenMoney, -waitRepaycCompenMoney, 0, 0, 0, borrowUser.getUserId());
			Account act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog(borrowUser.getUserId(), Constant.PRIOR_REPAID_COMPEN, Constant.ADMIN_ID);
			fillAccountLog(log, Constant.PRIOR_REPAID_COMPEN, act, borrowUser, new User(Constant.ADMIN_ID), waitRepaycCompenMoney, 0, getLogRemark(model) + "提前还款，扣除补偿金" + waitRepaycCompenMoney);
			accountLogDao.save(log);
			ppLog = new PriorRepayLog(model, repayment, ppLog.getAddip());
			fillPriorRepayLog(ppLog, borrowUser, Constant.PRIOR_REPAID_COMPEN, waitRepaycCompenMoney, waitRepaycCompenMoney);
			priorRepayLogDao.save(ppLog);
		}
		// 如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
		if (model.getType() == Constant.TYPE_CREDIT) {
			userAmountDao.updateCreditAmount(0, waitOldRpayCapital, -waitOldRpayCapital, borrowUser.getUserId());
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, Constant.REPAID, -waitOldRpayCapital, "还款，解冻信用额度：" + waitOldRpayCapital, ""));
		}

		model.setStatus(8);
		borrowDao.save(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setWebstatus(1);
		repay.setRepaymentYesinterest(waitRepayInterest);
		repay.setCompensation(waitRepaycCompenMoney);
		repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PRIOR));
		borrowRepaymentDao.update(repay);
		// 更新剩余还款计划
		borrowRepaymentDao.updatePriorRepay(model.getId());
		// 处理任务。
		borrowService.doApiTask(taskList);
	}
	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

	/*
	 * 返回状态为处理中时 1、还款操作表中数据更改：状态更改，返回码更改 2、将collection记录更改为处理中 返回成功时： 1、将还款操作表中数据更改为处理成功，返回码更改 2、将collection记录更改为处理成功 3、将repayment数据更改，在最后一笔还款完成后 将repayment修改为已还款
	 *
	 */
	@Override
	public void ipsRepay(IpsRepaymentModel ipsModel) {
		String ordId = ipsModel.getOrdId();
		IpsRepayDetail ipsRepayDetail = ipsRepayDetailDao.getIpsRepayByOrdId(ordId);
		if (null == ipsRepayDetail) {
			logger.info("还款回调订单号异常！！！：" + ordId);
			return;
		}

		// 处理还款业务
		// 1、根据状态，判断处理结果
		String code = ipsModel.getResultCode();
		String msg = ipsModel.getResultMsg();
		// 判断处理中状态
		// 计算本次还款总额
		double repayMoney = NumberUtils.getDouble(ipsModel.getMoney()); // ipsRepayDetail.getCapital()+ipsRepayDetail.getInterest()+ipsRepayDetail.getLateFee()+ipsRepayDetail.getCompensation();
		// 处理中，冻结用户本次还款金额
		BorrowRepayment repayment = borrowRepaymentDao.find(ipsRepayDetail.getRepayId());

		Borrow model = repayment.getBorrow();
		BorrowModel borrow = BorrowHelper.getHelper(model); // 封装borrow

		User user = repayment.getBorrow().getUser(); // 获取还款用户
		if ("MG00008F".equals(code)) {
			if (ipsRepayDetail.getStatus() != 3) { // 已经处理过处中的状态
				// 处理还款操作记录
				ipsRepayDetail.setStatus(3);
				ipsRepayDetail.setReturnCode(code);
				ipsRepayDetail.setReturnMsg(msg);
				// 更新collection状态
				List<BorrowCollection> coLists = doCollectionStatus(ipsRepayDetail.getCollectionIds());
				for (BorrowCollection con : coLists) {

					con.setStatus(4);// 设置为处理中
				}
				accountDao.updateAccount(0, -repayMoney, repayMoney, user.getUserId());
				Account bAccount = accountDao.getAcountByUser(user);
				// 插入资金记录
				AccountLog log = new AccountLog();
				fillAccountLog(log, Constant.REPAYMENT, bAccount, user, new User(Constant.ADMIN_ID), repayMoney, repayment.getBorrow().getId(), "[" + getLogRemark(repayment.getBorrow()) + "]用户还款处理中，冻结资金" + repayMoney);
				ipsRepayDetailDao.update(ipsRepayDetail);
				accountLogDao.save(log);
				borrowCollectionDao.update(coLists);
			} else {
				logger.info("订单：" + ordId + ",处理中状态已经处理！");
			}

		} else if ("MG00000F".equals(code)) { // 操作成功
			// 更新还款操作记录表
			if (ipsRepayDetail.getStatus() == 1) { // 已经处理,直接跳过，不再进行业务处理
				logger.info("还款操作记录已经处理，订单号：" + ordId);
				return;
			}
			AccountLog blog = new AccountLog();
			if (0 == ipsRepayDetail.getStatus()) { // 直接还款成功，资金直接减去，无需解冻冻结资金
				accountDao.updateAccount(0, 0, -repayMoney, 0, -repayMoney, user.getUserId());
				Account bAccount = accountDao.getAcountByUser(user);

				fillAccountLog(blog, Constant.REPAYMENT, bAccount, user, new User(Constant.ADMIN_ID), repayMoney, repayment.getBorrow().getId(), "[" + getLogRemark(repayment.getBorrow()) + "]用户还款成功，扣除可用金额：" + repayMoney);
			} else if (3 == ipsRepayDetail.getStatus()) {
				accountDao.updateAccount(0, 0, 0, -repayMoney, -repayMoney, user.getUserId());
				Account bAccount = accountDao.getAcountByUser(user);
				fillAccountLog(blog, Constant.REPAYMENT, bAccount, user, new User(Constant.ADMIN_ID), repayMoney, repayment.getBorrow().getId(), "[" + getLogRemark(repayment.getBorrow()) + "]用户还款成功，扣除冻结金额：" + repayMoney);
			}
			// 保存资金记录
			accountLogDao.save(blog);
			// 更新还款成功的collection，包括状态，还款金额，以及tender
			List<BorrowCollection> coLists = doCollectionStatus(ipsRepayDetail.getCollectionIds());
			for (BorrowCollection collection : coLists) {
				User tenderUser = collection.getBorrowTender().getUser();

				collection.setStatus(1);
				// 计算利息管理费
				double interestFee = getInterestFeeWithRule(collection.getInterest(), collection.getBorrowTender().getUser(), repayment.getBorrow());
				collection.setInterestFee(interestFee);
				collection.setRepayYesinterest(collection.getInterest());
				double late = 0;// 计算归还给用户的罚息
				collection.setLateInterest(late);
				double com = 0; // 补偿金
				collection.setCompensation(com);
				collection.setRepayYestime(new Date());
				// 更新账户信息
				double sumMoney = collection.getCapital() + collection.getInterest() + collection.getLateInterest() + collection.getCompensation() - collection.getInterestFee();
				accountDao.updateAccount(collection.getLateInterest() + collection.getCompensation() - interestFee, sumMoney, 0, tenderUser.getUserId());
				// 更新tender信息
				BorrowTender t = collection.getBorrowTender();
				t.setWaitAccount((t.getWaitAccount() - collection.getRepayYescapital())); // 不能小于0
				t.setWaitInterest((t.getWaitInterest() - collection.getRepayYesinterest()));
				t.setRepaymentYesinterest(t.getRepaymentYesinterest() + collection.getRepayYesinterest());
				t.setRepaymentYescapital(t.getRepaymentYescapital() + collection.getRepayYescapital());
				t.setLateAccount(t.getLateAccount() + late);
				borrowTenderDao.update(t);
				if (collection.getRepayTime().compareTo(new Date()) > 0) {
					collection.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_BASE));
				} else {
					collection.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_LATE));
				}
				borrowCollectionDao.save(collection);
				// 添加投资人资金记录
				AccountLog tlog = new AccountLog();
				Account tact = accountDao.getAcountByUser(tenderUser);
				fillAccountLog(tlog, Constant.REPAYMENT, tact, tenderUser, new User(Constant.ADMIN_ID), sumMoney, repayment.getBorrow().getId(), "[" + getLogRemark(repayment.getBorrow()) + "]借款人还款，收到金额：" + sumMoney + "，扣除管理费：" + interestFee);
				accountLogDao.save(tlog);
			}

			// 跟新repayment本次还款金额
			repayment.setRepaymentYestime(new Date());
			// 更新repayment状态（0：未完成；1：完成）
			int hxRepayStatus = 0;
			List<BorrowCollection> noRepayCollectionList = borrowCollectionDao.getNoRepayCollection(repayment.getBorrow().getId(), repayment.getPeriod());
			if (noRepayCollectionList.size() == 0) {
				// 当期所有人还完的场合
				hxRepayStatus = 1;
			}
			repayment.setStatus(hxRepayStatus);
			borrowRepaymentDao.updateRepayment(ipsRepayDetail.getCapital(), ipsRepayDetail.getInterest(), ipsRepayDetail.getLateFee(), ipsRepayDetail.getCompensation(), repayment.getId());

			// 处理ips还款操作记录
			ipsRepayDetail.setStatus(1);
			ipsRepayDetail.setReturnCode(code);
			ipsRepayDetail.setReturnMsg(msg);
			ipsRepayDetailDao.update(ipsRepayDetail);

			// 更新标状态（7：部分还款中；8：完成）
			int repayStatus = 7;
			if (borrow.isLastPeriod(repayment.getPeriod())) {
				if (noRepayCollectionList.size() == 0) {
					// 当期所有人还完的场合
					repayStatus = 8;
				}
			}
			model.setStatus(repayStatus);
			borrowDao.save(model);

		} else { // 其他状态为返回失败
			if (ipsRepayDetail.getStatus() == 2) {
				logger.info("还款订单：" + ordId + "，失败状态已经处理，返回码：" + code + "，返回中文说明：" + msg);
				return;
			}
			if (ipsRepayDetail.getStatus() == 3) { // 由处理中变为失败，要解冻冻结资金，将collection状态还原
				// 解冻资金
				accountDao.updateAccount(0, repayMoney, -repayMoney, user.getUserId());
				Account act = accountDao.getAcountByUser(user);
				Account tact = accountDao.getAcountByUser(user);
				AccountLog log = new AccountLog();
				fillAccountLog(log, Constant.REPAYMENT, tact, user, new User(Constant.ADMIN_ID), repayMoney, repayment.getBorrow().getId(), "[" + getLogRemark(repayment.getBorrow()) + "]还款失败，解冻金额" + repayMoney);
				accountLogDao.save(log);
				// 处理collection，将状态修改为0，
				List<BorrowCollection> coLists = doCollectionStatus(ipsRepayDetail.getCollectionIds());
				for (BorrowCollection con : coLists) {
					con.setStatus(0);
				}
				borrowCollectionDao.update(coLists);
				// 更新还款操作记录
				ipsRepayDetail.setStatus(2);
				ipsRepayDetail.setReturnCode(code);
				ipsRepayDetail.setReturnMsg(msg);
				ipsRepayDetailDao.update(ipsRepayDetail);
			}
			if (ipsRepayDetail.getStatus() == 0) { // 不用处理用户资金以及待收记录
				ipsRepayDetail.setStatus(2);
				ipsRepayDetail.setReturnCode(code);
				ipsRepayDetail.setReturnMsg(msg);
				ipsRepayDetailDao.update(ipsRepayDetail);
			}

		}
	}

	/**
	 * 根据collectionid字符串获取collection对象集合
	 *
	 * @param collectionIds
	 * @return
	 */
	private List<BorrowCollection> doCollectionStatus(String collectionIds) {
		List<BorrowCollection> lists = new ArrayList<BorrowCollection>();
		String[] ids = collectionIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			BorrowCollection collection = borrowCollectionDao.find(NumberUtils.getInt(ids[i]));
			lists.add(collection);
		}
		return lists;
	}

	@Override
	public void batchRechargeSuccess(AccountRecharge accountRecharge) {
		List<Object> taskList = new ArrayList<Object>(); // 任务列表
		if (accountRecharge.getStatus() != 0) {
			logger.error("该充值已被处理,id:" + accountRecharge.getId());
			return;
		}
		// 完整数据来自 accountrechargeparam
		AccountRechargeParam param = null;
		try {
			param = accountRechargeParamDao.findByRechargeId(accountRecharge.getId());
			if (param == null) {
				throw new ManageBussinessException("审核出错钱多多充值掉单,id:" + accountRecharge.getId());
			}
			// 调用放款接口
			accountRecharge.setSerialNo(param.getSerialno());
			apiService.batchRechargeLoanMoney(param.getSerialno(), accountRecharge, null, null, taskList, ApiMethodType.FULL_SUCCESS_C);
			// 修改账户金额
			if (borrowService.doApiTask(taskList)) {
				for (Object object : taskList) {
					// 审核成功后修改被充值账户金额
					double money = accountRecharge.getMoney();
					logger.info("修改用户账户资金:id:" + accountRecharge.getUser().getUserId() + "用户名:" + accountRecharge.getUser().getRealname());
					accountDao.updateAccount(money, money, 0, accountRecharge.getUser().getUserId());
					Account act = accountDao.getAcountByUser(accountRecharge.getUser());
					// 修改订单状态
					accountRecharge.setStatus(1);
					accountRecharge.setVerifyRemark("后台批量充值审核成功");
					accountRechargeDao.update(accountRecharge);
					// 插入资金记录表
					AccountLog log = new AccountLog();
					log.setUser(accountRecharge.getUser());
					User toUser = new User();
					log.setToUser(toUser);
					log.setType("web_recharge");
					log.setRemark(accountRecharge.getRemark());
					log.setAddtime(new Date());
					toUser.setUserId(Constant.ADMIN_ID);
					log.setMoney(accountRecharge.getMoney());
					log.setTotal(act.getTotal());
					log.setUseMoney(act.getUseMoney());
					log.setNoUseMoney(act.getNoUseMoney());
					log.setCollection(act.getCollection());
					log.setRepay(act.getRepay());
					accountLogDao.save(log);
					logger.info("批量充值审核通过,后台处理成功!");
					//审核成功，添加平台转账账户资金记录
					MmmPay mmmPay = mmmPayDao.findByPropertyForUnique("orderNo",param.getOrderno());
					User transferAccount = userDao.findByPropertyForUnique("apiId",mmmPay.getMmmId());
					AccountLog transferAccountLog = new AccountLog(transferAccount.getUserId(), "fund_transfer", transferAccount.getAccount().getTotal()-accountRecharge.getMoney(), accountRecharge.getMoney(), transferAccount.getAccount().getUseMoney(), transferAccount.getAccount().getNoUseMoney()-accountRecharge.getMoney(), transferAccount.getAccount().getCollection(),accountRecharge.getUser().getUserId(), accountRecharge.getRemark(),new Date(), "");
					accountLogDao.save(log);
					accountLogDao.save(transferAccountLog);
					//审核成功，修改平台转账账户金额总金额 可用金额 冻结金额
					accountDao.updateAccount(-money,0, -money, transferAccount.getUserId());
					// 审核成功,给用户发送消息通知
					try {
						MsgReq req1 = new MsgReq();
						req1.setReceiver(accountRecharge.getUser());
						req1.setSender(new User(Constant.ADMIN_ID));
						req1.setMsgOperate(this.msgService.getMsgOperate(31));
						req1.setTime(DateUtils.dateStr4(new Date()));
						req1.setMoney("" + accountRecharge.getMoney());
						DisruptorUtils.sendMsg(req1);
					} catch (Exception e) {
						logger.info("批量充值发送消息出错："+e.getMessage());
						e.printStackTrace();
					}
				}
			
			} else {
				logger.info("后台批量充值审核失败,充值记录id:" + accountRecharge.getId());
			}
		} catch (Exception e1) {
			logger.info("批量充值审核出错："+e1.getMessage());
			e1.printStackTrace();
		}
		
	}

	@Override
	public void failBatchRecharge(AccountRecharge accountRecharge) {
		// 批量审核不通过,解冻账户资金(批量充值冻结到托管账户)
		List<Object> taskList = new ArrayList<Object>();
		// 完整数据来自 accountrechargeparam
		AccountRechargeParam param = null;
		try {
			param = accountRechargeParamDao.findByRechargeId(accountRecharge.getId());
			if (param == null) {
				throw new ManageBussinessException("审核出错,id:" + accountRecharge.getId());
			}
			// 调用放款接口
			accountRecharge.setSerialNo(param.getSerialno());
			apiService.failBatchRecharge(taskList, accountRecharge, ApiMethodType.FAILL_BORROW_D);
			if (borrowService.doApiTask(taskList)) {
				for (Object object : taskList) {
					MmmPay mmmPay = mmmPayDao.findByPropertyForUnique("orderNo",param.getOrderno());
					User transferAccount = userDao.findByPropertyForUnique("apiId",mmmPay.getMmmId());
					AccountLog transferAccountLog = new AccountLog(transferAccount.getUserId(), Constant.UNFREEZE, transferAccount.getAccount().getTotal()+accountRecharge.getMoney(), accountRecharge.getMoney(), transferAccount.getAccount().getUseMoney()+accountRecharge.getMoney(), transferAccount.getAccount().getNoUseMoney()-accountRecharge.getMoney(), transferAccount.getAccount().getCollection(),accountRecharge.getUser().getUserId(), "审核不通过解冻双乾资金",new Date(), "");
					accountLogDao.save(transferAccountLog);
					// 审核不通过修改状态为1 已处理
					accountRecharge.setStatus(1);
					accountRecharge.setVerifyRemark("审核不通过");
					accountRechargeDao.update(accountRecharge);
					//审核不通过修改平台转账账户资金
					accountDao.updateAccount(accountRecharge.getMoney(), accountRecharge.getMoney(), -accountRecharge.getMoney(), transferAccount.getUserId());
				}
			} else {
				logger.info("审核不通过解冻资金失败!");
			}
		} catch (Exception e1) {
			logger.info("批量充值审核出错："+e1.getMessage());
			e1.printStackTrace();
		}
		
	}

}
