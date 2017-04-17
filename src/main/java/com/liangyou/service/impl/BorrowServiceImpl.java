package com.liangyou.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.liangyou.api.ips.IpsHelper;
import com.liangyou.api.ips.IpsRepaymentNewTrade;
import com.liangyou.context.ApiMethodType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.IpsPayType;
import com.liangyou.context.MmmType;
import com.liangyou.context.YjfType;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowAutoDao;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.dao.BorrowConfigDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowDetailDao;
import com.liangyou.dao.BorrowDetailTypeDao;
import com.liangyou.dao.BorrowFeeDao;
import com.liangyou.dao.BorrowIncomeLadderDao;
import com.liangyou.dao.BorrowIntentDao;
import com.liangyou.dao.BorrowPropertyDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.ChinapnrPayDao;
import com.liangyou.dao.ExperienceMoneyDao;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.dao.IpsPayDao;
import com.liangyou.dao.IpsRepayDetailDao;
import com.liangyou.dao.LinkageDao;
import com.liangyou.dao.MessageDao;
import com.liangyou.dao.MmmPayDao;
import com.liangyou.dao.SitePayLogDao;
import com.liangyou.dao.TenderPropertyDao;
import com.liangyou.dao.TenderRewardDao;
import com.liangyou.dao.UserAmountDao;
import com.liangyou.dao.UserAmountLogDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.ViewAutoInvestDao;
import com.liangyou.dao.YjfDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.BorrowDetail;
import com.liangyou.domain.BorrowDetailType;
import com.liangyou.domain.BorrowFee;
import com.liangyou.domain.BorrowIncomeLadder;
import com.liangyou.domain.BorrowIntent;
import com.liangyou.domain.BorrowProperty;
import com.liangyou.domain.BorrowRepayType;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.IpsPay;
import com.liangyou.domain.IpsRepayDetail;
import com.liangyou.domain.Linkage;
import com.liangyou.domain.Message;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.Rule;
import com.liangyou.domain.SitePayLog;
import com.liangyou.domain.TenderProperty;
import com.liangyou.domain.TenderReward;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.domain.ViewAutoInvest;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.ApiPayParamModel;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.sendPhone.PhoneUtil;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowAwardService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RepaymentService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.service.TenderCompensationService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.service.WebPaidService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Service(value = "borrowService")
@Transactional // propagation = Propagation.REQUIRED
public class BorrowServiceImpl extends BaseServiceImpl implements BorrowService {

	private static Logger logger = Logger.getLogger(BorrowServiceImpl.class);
	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private BorrowAutoDao borrowAutoDao;
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
	private MessageDao messageDao;
	@Autowired
	private YjfDao yjfDao;
	@Autowired
	BorrowConfigDao borrowConfigDao;
	@Autowired
	ViewAutoInvestDao viewAutoInvestDao;
	@Autowired
	private MsgService msgService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	OperateService operateService;
	@Autowired
	UserService userService;
	@Autowired
	private RepaymentService repaymentService;
	@Autowired
	private BorrowFeeDao borrowFeeDao;
	@Autowired
	private SitePayLogDao sitePayLogDao;
	@Autowired
	private BorrowPropertyDao borrowPropertyDao;
	@Autowired
	private ApiService apiService;
	@Autowired
	private ChinapnrPayDao chinapnrPayDao;
	@Autowired
	private LinkageDao linkageDao;
	@Autowired
	private TenderPropertyDao tenderPropertyDao;
	@Autowired
	private MmmPayDao mmmPayDao;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private BorrowIntentDao borrowIntentDao;
	@Autowired
	private BorrowDetailTypeDao borrowDetailTypeDao;
	@Autowired
	private BorrowDetailDao borrowDetailDao;
	@Autowired
	private TenderRewardDao tenderRewardDao;
	@Autowired
	private InviteUserDao inviteUserDao;
	@Autowired
	private RewardExtendService rewardExtendService;
	@Autowired
	private TenderCompensationService tenderCompensationService;
	@Autowired
	private WebPaidService webPaidService;
	@Autowired
	private BorrowAwardService borrowAwardService;
	@Autowired
	private BorrowIncomeLadderDao borrowIncomeLadderDao;
	@Autowired
	private IpsPayDao ipsPayDao;
	@Autowired
	private IpsRepayDetailDao ipsRepayDetailDao;
	@Autowired
	private ExperienceMoneyDao experienceMoneyDao;
	@Override
	public List getList() {
		SearchParam param = new SearchParam();
		param.addParam("status", 1);
		List<Borrow> indexList = borrowDao.findByCriteria(param);
		return indexList;
	}

	@Override
	public List getList(int type) {
		SearchParam param = new SearchParam();
		param.addParam("type", type);
		List<Borrow> indexList = borrowDao.findByCriteria(param);
		return indexList;
	}

	@Override
	public List getList(int type, int status) {
		SearchParam param = new SearchParam();
		param.addParam("type", type).addParam("status", 1);
		List<Borrow> indexList = borrowDao.findByCriteria(param);
		return indexList;
	}

	@Override
	public PageDataList getList(SearchParam param) {
		return borrowDao.findPageList(param);
	}

	/**
	 * add by gy 2016年12月14日15:19:35
	 * 获取wap端首页的标的列表
	 * @return
	 */
	@Override
	public List<Borrow> getWapIndexBorrowList() {
		return borrowDao.getWapIndexBorrowList();
	}

	/**
	 * add by gy 2016年12月14日15:19:35
	 * wap端获取标的列表
	 * @param param
	 * @param clazz
	 * @return
	 */
	@Override
	public PageDataList findWapBorrowPageListBySql(SearchParam param) {
		return borrowDao.findWapBorrowPageListBySql(param);
	}

	@Override
	public List getExportBorrowList(SearchParam param) {
		return borrowDao.findAllPageList(param).getList();
	}

	@Override
	public Borrow getBorrow(long id) {
		return borrowDao.find(id);
	}

	@Override
	public PageDataList getTenderList(SearchParam param) {
		return borrowTenderDao.findPageList(param);
	}

	@Override
	public BorrowTender getTenderById(long id) {
		return borrowTenderDao.find(id);
	}

	/**
	 * 根据borrowId 查询所有的 投资人。
	 *
	 * @param borrowId
	 * @return
	 */
	@Override
	public List<BorrowTender> getAllTenderByBorrowId(Long borrowId) {
		return borrowTenderDao.getBorrowTenderList(borrowId);
	}

	// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-18 start
	// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 start
	@Override
	public void addBorrow(BorrowModel borrow, AccountLog log, BorrowProperty property, List<BorrowDetail> details) {
		// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 end
		// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-18 end
		List<Object> taskList = new ArrayList<Object>();
		borrow.skipTrial();// 是否跳过初审
		Borrow model = borrow.getModel();
		User borrowUser = model.getUser();
		// 秒标需要发标时就冻结资金
		if (model.getType() == Constant.TYPE_SECOND) {
			// 计算需要冻结的资金
			double freezeVal = borrow.calculateBorrowSecondFreeze();
			// 冻结账户资金
			Account act = accountDao.getAcountByUser(borrowUser);
			if (act.getUseMoney() < freezeVal) {
				throw new BussinessException("可用余额不足，不能发秒标");
			}
			accountDao.updateAccount(0, -freezeVal, freezeVal, model.getUser().getUserId());
			// 调用冻结接口
			String remark = "发布标秒标，冻结资金" + NumberUtils.format2(freezeVal) + "元";
			log.setUser(borrowUser);
			log.setToUser(new User(Constant.ADMIN_ID));
			fillAccountLog(log, act, freezeVal, remark);
			accountLogDao.save(log);
		}
		// 信用标扣除信用或者学信标
		if (model.getType() == Constant.TYPE_CREDIT) {
			double amount = model.getAccount();
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			if (amount > ua.getCreditUse()) { // 信用额度判断，发标已超过可用信用额度，不能继续借款
				throw new BussinessException("发标已超过可用信用额度，不能继续借款");
			}
			userAmountDao.updateCreditAmount(0, -amount, amount, borrowUser.getUserId());
			ua = userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, "addborrow", amount, " 发标冻结信用额度：" + amount, ""));
		}
		// 净值标校验
		if (model.getType() == Constant.TYPE_PROPERTY) {
			Account act = accountDao.getAcountByUser(borrowUser);
			// 金额判断 ： 总额额-待还<发标金额
			if ((act.getTotal() - act.getRepay()) < model.getAccount()) {
				throw new BussinessException("您的净资产额度小于发标金额！");
			}
		}
		if (model.getType() == Constant.TYPE_FLOW) {
			int flow_money = model.getFlowMoney();
			long account = (long) model.getAccount();
			if (account <= 0) {
				throw new BussinessException("借款金额必须是大于0的整数！");
			}
			if (flow_money <= 0) {
				throw new BussinessException("每份金额必须是大于0的整数！");
			}
			if (account % flow_money != 0) {
				throw new BussinessException("借款金额必须是每份金额的整数倍！");
			}
			model.setFlowMoney(flow_money);
			model.setFlowCount((int) (account / flow_money));
		}

		borrowDao.save(borrow.getModel());
		// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-19 start
		// 添加borrowproperty保存方法
		if (property != null) {
			property.setBorrow(new Borrow(borrow.getModel().getId()));
			borrowPropertyDao.save(property); // 添加借款属性方法
		}
		// v1.8.0.3_u3 XINHEHANG-66 wujing 2014-06-19 end

		// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 start
		if (details != null && details.size() > 0) {
			borrowDetailDao.save(details);
		}
		// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 end

		if (!doApiTask(taskList)) {
			throw new BussinessException("发标创建交易失败！");
		}

	}

	/**
	 * 债权转让发标
	 */
	@Override
	public void addAssignmentBorrow(BorrowModel borrow, AccountLog log, BorrowTender bt) {
		List<Object> taskList = new ArrayList<Object>();
		borrow.skipTrial();// 是否跳过初审
		Borrow model = borrow.getModel();
		if (bt == null) {
			throw new BussinessException("该债券转让标的原投资记录查询不到，不能转让！");
		}
		if (bt.getStatus() != 0) {
			throw new BussinessException("该债券转让标的原投资已被处理，请查看投资记录与资金记录！");
		}

		model.setAssignmentTender(bt.getId());
		borrowDao.save(borrow.getModel());
		// 原债权人的投资记录添加发布的债权转让标的信息
		bt.setAssignmentId(model.getId());
		bt.setStatus(-1); // 债权款转让成功 原来的tender 修改成 -1
		borrowTenderDao.update(bt);

		doApiTask(taskList);// 触发接口。

	}

	@Override
	/**
	 * 用户撤回标
	 * 
	 * @param borrow
	 * @param log
	 */
	public void deleteBorrow(Borrow borrow, AccountLog log, BorrowParam param) {
		borrowDao.update(borrow);
		borrowDao.flush();
		String sendMsg = "";
		BorrowModel model = BorrowHelper.getHelper(borrow);
		if (borrow.getType() == Constant.TYPE_SECOND) {
			double freezeVal = model.calculateBorrowSecondFreeze();
			Account act = accountDao.getAcountByUser(model.getModel().getUser());
			accountDao.updateAccount(0, freezeVal, -freezeVal, model.getModel().getUser().getUserId());
			log.setUser(borrow.getUser());
			log.setToUser(new User(Constant.ADMIN_ID));
			log.setMoney(freezeVal);
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			sendMsg = "撤回秒标，解冻资金" + NumberUtils.format2(freezeVal) + "元";
			log.setRemark(sendMsg);
			accountLogDao.save(log);
			Message message = getSiteMessage(borrow.getUser().getUserId(), sendMsg, sendMsg, Constant.ADMIN_ID, new Message());
			message.setAddtime(new Date());
			messageDao.addMessage(message);
		}
		// 调用后台，撤销标 解冻用户的投资
		try {
			DisruptorUtils.failBorrow(model, param);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 用户取消待审核的标
	 *
	 * @param borrowModel
	 */
	@Override
	public void userCancelBorrow(BorrowModel borrowModel) {
		Borrow model = borrowModel.getModel();
		long userId = model.getUser().getUserId();

		if (model.getType() == Constant.TYPE_SECOND) {// 秒表个人取消解冻资金
			double freezeVal = borrowModel.calculateBorrowSecondFreeze();
			Account act = accountDao.getAcountByUser(new User(userId));
			accountDao.updateAccount(0, freezeVal, -freezeVal, userId);
			AccountLog log = new AccountLog();
			log.setUser(model.getUser());
			log.setToUser(new User(Constant.ADMIN_ID));
			log.setMoney(freezeVal);
			log.setType(Constant.UNFREEZE);
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			String sendMsg = "借款标取消，解冻资金" + NumberUtils.format2(freezeVal) + "元";
			log.setRemark(sendMsg);
			log.setAddtime(new Date());
			accountLogDao.save(log);
		}
		if (model.getStatus() == -1) {
			OperateProgress op = operateService.getOperateProgressByTypeAndOrderNo(Constant.VERIFY_BORROW, model.getId());
			if (op != null) {
				op.setStatus(1);// 完结初审流程中的任务。
				op.setAddtime(new Date());
			}
		}
		// 调用后台，撤销标 解冻用户的投资
		try {
			DisruptorUtils.failBorrow(borrowModel, null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void updateBorrow(Borrow borrow) {
		borrowDao.update(borrow);
	}

	@Override
	public void updateBorrowProperty(BorrowProperty borrowProperty) {
		borrowPropertyDao.update(borrowProperty);
	}

	@Override
	public List unfinshBorrowList(long userId) {
		return borrowDao.unfinshBorrow(userId);
	}

	public BorrowTender addTender(BorrowParam param, User user) throws Exception {
		// 用户投标回调，重复处理拦截：
		boolean result = checkAddTenderRepeat(param);
		if (result == false) {
			logger.info("已经处理过此订单投标业务，不再进行投标处理操作！订单编号：" + param.getTenderNo());
			return null;
		}
		// 从新查询防止缓存
		Borrow model = null;
		BorrowTender t = null;
		model = borrowDao.find(param.getId());
		List<Object> taskList = new ArrayList<Object>(); // 第三方对象 处理任务。
		checkTender(model, user, param); // 校验标
		BorrowModel borrowModel = BorrowHelper.getHelper(model); // 封装borrow
		BorrowTender tender = fillTender(borrowModel, param, user); // 封装borrowTender

		User tenderUser = tender.getUser();
		User borrowUser = model.getUser();
		tenderLog("Begin tender service!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		// 用户投标成功更新account_yes账户记录投资人信息借贷关系还款列表
		t = getValidTender(tender, borrowModel, param, taskList); // 参数完善

		double validAccount = t.getAccount();
		t.setIpsTenderNo(param.getTenderNo());
		// t.setIpsTenderContract(param.get);
		borrowTenderDao.update(t); // 更新利息 等其他信息。
		// 修改账户资金
		int row = 0;
		try {
			row = accountDao.updateAccountNotZero(0, -validAccount, validAccount, tenderUser.getUserId());
		} catch (Exception e) {
			tenderLog("freeze account fail!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
			logger.error(e.getMessage());
		} finally {
			if (row < 1)
				throw new BussinessException("投资人冻结投资款失败！请注意您的可用余额。");
		}

		tenderLog("freeze account success!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		Account act = accountDao.getAcountByUser(tenderUser);
		AccountLog log = new AccountLog(tender.getUser().getUserId(), Constant.TENDER, model.getUser().getUserId(), getLogRemark(model), param.getIp());
		this.fillAccountLog(log, act, validAccount, log.getRemark() + "投资成功，冻结投资者的投标资金" + NumberUtils.format4(validAccount));
		accountLogDao.save(log);

		// 做处理流转标
		if (model.getType() == Constant.TYPE_FLOW) {
			// 流转标立即生效，扣除投标金额
			accountDao.updateAccount(0d, 0d, -validAccount, validAccount, 0d, tenderUser.getUserId());
			act = accountDao.getAcountByUser(tenderUser);
			AccountLog logIvest = new AccountLog(tender.getUser().getUserId(), Constant.INVEST, model.getUser().getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logIvest, act, validAccount, logIvest.getRemark() + "扣除流转标投资冻结款,生成待收本金" + NumberUtils.format4(validAccount));
			accountLogDao.save(logIvest);

			// 生产待收利息

			// 阶梯收益sj
			double interest = 0;
			if (model.getIsIncomeLadder() == 1) {
				double apr = 0;
				List<BorrowIncomeLadder> list = borrowIncomeLadderDao.getList(model);
				for (BorrowIncomeLadder borrowIncomeLadder : list) {
					if (borrowIncomeLadder.getMoney() <= validAccount) {
						apr = borrowIncomeLadder.getApr();
					}
				}
				tender.setApr(model.getApr() + apr);
				interest = borrowModel.calculateInterest(validAccount, tender);
			} else {
				interest = borrowModel.calculateInterest(validAccount);
			}

			accountDao.updateAccount(interest, 0d, 0d, interest, 0d, tenderUser.getUserId());
			act = accountDao.getAcountByUser(tenderUser);
			AccountLog logInterest = new AccountLog(tender.getUser().getUserId(), Constant.WAIT_INTEREST, model.getUser().getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logInterest, act, interest, logInterest.getRemark() + "生产待收利息" + NumberUtils.format4(interest));
			accountLogDao.save(logInterest);

			// 借款人资金入账 生成待还
			accountDao.updateAccount(validAccount, validAccount, 0d, 0d, validAccount + interest, borrowUser.getUserId());
			act = accountDao.getAcountByUser(model.getUser());
			AccountLog logBorrowSuc = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, tenderUser.getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logBorrowSuc, act, validAccount, logBorrowSuc.getRemark() + "借款入账" + NumberUtils.format4(validAccount));
			accountLogDao.save(logBorrowSuc);

			// 生成还款计划
			borrowRepaymentDao.save(borrowModel.getFlowRepayment(tender, model));
			// 汇付流转标扣款修改 周学成 9-17 start
			// 流转标投标 直接划款
			apiService.flowBorrowLoan(tender, param, tenderUser, model, borrowUser, taskList);
			// 汇付流转标扣款修改 周学成 9-17 end

			// TGPROJECT-411 wsl 2014-09-04 start
			logger.info("奖励发放，开始...");
			borrowAwardService.flowBorrowGiveAward(model, tender, borrowUser, tenderUser, taskList, param);
			// TGPROJECT-411 wsl 2014-09-04 end

			// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start
			/**
			 * 处理投资积分
			 */
			userCreditService.borrowTenderCredit(tender);
			userCreditService.flowBorrowSuccessCredit(borrowUser, tender);
			// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start

			// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
			// 更新被推荐人信息
			InviteUser inviteUser = inviteUserDao.getInviter(tenderUser.getUserId());
			if (inviteUser != null) {
				inviteUser.setIsTender(1);
				inviteUser.setTenderTotal(inviteUser.getTenderTotal() + t.getAccount());
			}
			// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end

			// v1.8.0.4_u4 TGPROJECT-398 qinjun 2014-08-04 start
			rewardExtendService.tenderExtendRedPacket(tender.getUser(), tender.getAccount());
			// v1.8.0.4_u4 TGPROJECT-398 qinjun 2014-08-04 end
		} else {// 非流转标 投资冻结
			apiService.addTenderFreezeMoney(tender, param, ApiMethodType.BORROW_TENDER_C, taskList);
		}
		tenderLog("Tender service end!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());

		// 投标成功，发消息通知投资人
		MsgReq req = new MsgReq();
		req.setIp(param.getIp());
		req.setReceiver(tenderUser);
		req.setSender(new User(Constant.ADMIN_ID));
		req.setMsgOperate(this.msgService.getMsgOperate(10));
		req.setBorrowname(model.getName());
		req.setAccount("" + validAccount);
		req.setApr("" + model.getApr());// 标id
		// v1.8.0.4_u1 TGPROJECT-260 lx start
		if (model.getBorrowProperty() != null) {
			req.setEndTime("" + DateUtils.newdateStr6(model.getBorrowProperty().getEndTime()));
		}
		// v1.8.0.4_u1 TGPROJECT-260 lx end
		req.setMonthapr(NumberUtils.format2Str(model.getApr() / 12));
		DisruptorUtils.sendMsg(req);

		MsgReq req1 = new MsgReq();
		req1.setIp(param.getIp());
		req1.setReceiver(borrowUser);
		req1.setSender(new User(Constant.ADMIN_ID));
		req1.setMsgOperate(this.msgService.getMsgOperate(11));
		req1.setBorrowname(model.getName());
		req1.setMoney("" + (model.getAccountYes() + validAccount));
		req1.setAccount("" + model.getAccount());
		req1.setApr("" + model.getApr());
		// v1.8.0.4_u1 TGPROJECT-260 lx start
		if (model.getBorrowProperty() != null) {
			req.setEndTime("" + DateUtils.newdateStr6(model.getBorrowProperty().getEndTime()));
		}
		// v1.8.0.4_u1 TGPROJECT-260 lx start
		req1.setMonthapr(NumberUtils.format2Str(model.getApr() / 12));
		DisruptorUtils.sendMsg(req1);

		doApiTask(taskList); // 触发第三方任务

		// 添加投标额外信息
		addTenderProperty(tender);
		return t;

	}

	/**
	 * 初始化投标
	 *
	 * @param tender
	 * @param borrow
	 * @param log
	 * @return
	 */
	private BorrowTender getValidTender(BorrowTender tender, BorrowModel borrow, BorrowParam param, List<Object> taskList) {
		Borrow model = borrow.getModel();
		// 锁住对象，下面进行校验更新处理

		User borrowUser = model.getUser();
		User tenderUser = tender.getUser();
		double validAccount = 0.0;
		double tenderAccount = tender.getMoney();
		double account = model.getAccount();
		double accountyYes = model.getAccountYes();
		double mostAccount = model.getMostAccount(); // 最大的投标金额
		double minAccount = model.getLowestAccount(); // 最小投标金额
		double hasTender = borrowTenderDao.hasTenderTotalPerBorrowByUserid(model.getId(), tenderUser.getUserId());// 本人已投标金额

		logger.info(" 投标的资金记录-1：" + tenderAccount + "  " + accountyYes + "  " + account);
		if (tenderAccount + accountyYes >= account) {// 超过总额
			validAccount = account - accountyYes;
			borrow.skipReview();
			if (model.getStatus() == 3) {
				// 秒标满标自动还款，对冻结资金进行解冻
				if (model.getType() == Constant.TYPE_SECOND) {
					double freeze = borrow.calculateBorrowSecondFreeze();
					accountDao.updateAccount(0, freeze, -freeze, borrowUser.getUserId());

					Account borrowAct = accountDao.getAcountByUser(borrowUser);
					AccountLog logFree = new AccountLog(borrowUser.getUserId(), Constant.UNFREEZE, Constant.ADMIN_ID, getLogRemark(model), param.getIp());
					logFree.setType(Constant.UNFREEZE);
					fillAccountLog(logFree, borrowAct, freeze, logFree.getRemark() + "解冻资金" + NumberUtils.format2(freeze));
					accountLogDao.save(logFree);
				}
				model.setVerifyTime(new Date());
			}
		} else {
			validAccount = tenderAccount;
			if (validAccount < minAccount) {// 正常
				throw new BussinessException("投资金额不能小于最小限制金额额度! ");
			}
		}
		if (validAccount + hasTender > mostAccount && mostAccount > 0) {// 确保不高于个人投标限额（最大投标额）
			validAccount = mostAccount - hasTender;
		}
		if (validAccount <= 0) {
			throw new BussinessException("投标金额已达到最大投标额,无法再次投标！");
		}

		checkSystemRule(tenderAccount, hasTender, model, tender.getUser().getUserId());// 系统校验投标限制

		tenderLog("Tender service,get validAccount!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), validAccount);
		// 判断是否超出有效投标金额,满标情况
		int row = 0;
		try {
			row = borrowDao.updateBorrowAccountyes(model.getId(), validAccount, 1);
			logger.info("投标的资金记录-2" + validAccount + "  " + accountyYes + "  " + account);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			tenderLog("Borrow is full!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), validAccount);
			if (row < 1) {
				throw new BussinessException("此标已满！");
			}
		}
		logger.info(" 每次进来的   account :" + account + "   accountyes:" + accountyYes + "  validAccount:" + validAccount + " userId:" + tenderUser.getUserId());
		Account newAccount = accountDao.getAcountByUser(tender.getUser()); // 投资人账户
		tenderLog("Tender service,New account use_money!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), validAccount);
		if (validAccount > newAccount.getUseMoney()) {
			tenderLog("Tender fail,not enough money!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), validAccount);
			throw new BussinessException("投资金额大于您的可用金额，投标失败！");
		}
		// 待收金额小于5000，无法进行投秒
		double mbTenderAccount = Global.getDouble("mb_tender_account");
		if (mbTenderAccount > 0 && newAccount.getCollection() < mbTenderAccount && model.getType() == Constant.TYPE_SECOND) {
			tenderLog("Tender fail!", tenderUser.getUserId(), model.getId(), tender.getMoney(), validAccount);
			throw new BussinessException("您的待收金额小于" + mbTenderAccount + ",不能进行投秒标，投标失败！");
		}
		tenderLog("Tender success!", tenderUser.getUserId(), model.getId(), tender.getMoney(), validAccount);
		// 借贷关系记录插入
		tender.setMoney(tenderAccount);
		tender.setAccount(validAccount);

		// 阶梯收益sj
		if (model.getIsIncomeLadder() == 1) {
			double apr = 0;
			List<BorrowIncomeLadder> list = borrowIncomeLadderDao.getList(model);
			for (BorrowIncomeLadder borrowIncomeLadder : list) {
				if (borrowIncomeLadder.getMoney() <= tenderAccount) {
					apr = borrowIncomeLadder.getApr();
				}
			}
			tender.setApr(model.getApr() + apr);
		}

		List<BorrowTender> listTenders = borrowTenderDao.getBorrowTenderList(model.getId());
		List<BorrowCollection> collectList = null;
		if (model.getIsAssignment() == 1) {// 债权转让标的tender 和 collection,必须和转让人的剩余投资吻合，避免还款产生错误
			BorrowTender assignMentTender = borrowTenderDao.getAssignMentTender(model.getId());
			if (assignMentTender.getStatus() != -1) {
				throw new BussinessException("债权转让标的状态错误");
			}
			tender.setAssignment_borrow_id(model.getId());
			List<BorrowCollection> assignCollection = borrowCollectionDao.getAssignMentCollectionByTenderId(assignMentTender);
			collectList = borrow.getAssignMentCollectionList(tender, listTenders, assignMentTender, assignCollection);
		} else {// 正常标产生 collection的方法
			collectList = borrow.getCollectionList(tender, listTenders);
		}
		tender.setBorrowCollections(collectList);// 待收

		borrowTenderDao.save(tender); // 保存投资人信息
		return tender;
	}

	// v1.8.0.4 TGPROJECT-27 qj 2014-04-09 start
	public BorrowTender addMmmTender(BorrowParam param, User user) throws Exception {
		// 用户投标回调，重复处理拦截：
		boolean result = checkAddTenderRepeat(param);
		if (result == false) {
			logger.info("已经处理过此订单投标业务，不再进行投标处理操作！订单编号：" + param.getTenderNo());
			return null;
		}
		// 从新查询防止缓存
		Borrow model = null;
		BorrowTender t = null;
		model = borrowDao.find(param.getId());
		List<Object> taskList = new ArrayList<Object>(); // 第三方对象 处理任务。
		BorrowModel borrowModel = BorrowHelper.getHelper(model); // 封装borrow
		BorrowTender tender = fillTender(borrowModel, param, user); // 封装borrowTender
		tender.setMoney(param.getMoney());// 流转标需重新设置
		User tenderUser = tender.getUser();
		User borrowUser = model.getUser();
		tenderLog("Begin tender service!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		// 更新标的已投金额、投标次数
		t = getValidMmmTender(tender, borrowModel, param); // 初始化投标
		double validAccount = t.getAccount();
		borrowTenderDao.update(t); // 更新利息 等其他信息。

		/*
		 * 修改账户资金表 account
		 */
		// add by gy 2016-10-26 16:45:39 begin
		if (param.getExperienceMoney() != null) {
			// 如果使用过体验金抵扣本金， 则账户需要冻结的资金是本金-体验金利息
			validAccount = t.getAccount() - param.getExperienceMoney().getExperienceInterest();// 冻结金额
			// 修改个人账户，将体验金利息放在账户可用余额中
			// modify by lixiaomin 体验金利息不应放在可用余额中
			// accountDao.updateAccountNotZero(0, param.getExperienceMoney().getExperienceInterest(), 0, tenderUser.getUserId());
		}
		// add by gy 2016-10-26 16:45:39 end

		accountDao.updateAccountNotZero(0, -validAccount, validAccount, tenderUser.getUserId());

		tenderLog("freeze account success!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		Account act = accountDao.getAcountByUser(tenderUser);
		AccountLog log = new AccountLog(tender.getUser().getUserId(), Constant.TENDER, model.getUser().getUserId(), getLogRemark(model), param.getIp());
		this.fillAccountLog(log, act, validAccount, log.getRemark() + "投资成功，冻结投资者的投标资金" + NumberUtils.format4(validAccount));
		accountLogDao.save(log);

		// add by gy 2016-10-26 16:48:00 投标成功，需要将体验金记录修改为已经投资使用
		ExperienceMoney em = param.getExperienceMoney();
		if (em != null) {
			em.setInterestUseStatus(1);
			em.setInterestUseTime(new Date());
			experienceMoneyDao.update(em);
		}
		// add by gy 2016-10-26 17:15:45 end

		// 做处理流转标
		if (model.getType() == Constant.TYPE_FLOW) {
			// 流转标立即生效，扣除投标金额
			accountDao.updateAccount(0d, 0d, -validAccount, validAccount, 0d, tenderUser.getUserId());
			act = accountDao.getAcountByUser(tenderUser);
			AccountLog logIvest = new AccountLog(tender.getUser().getUserId(), Constant.INVEST, model.getUser().getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logIvest, act, validAccount, logIvest.getRemark() + "扣除流转标投资冻结款,生成待收本金" + NumberUtils.format4(validAccount));
			accountLogDao.save(logIvest);
			// 生产待收利息
			double interest = borrowModel.calculateInterest(validAccount);
			accountDao.updateAccount(interest, 0d, 0d, interest, 0d, tenderUser.getUserId());
			act = accountDao.getAcountByUser(tenderUser);
			AccountLog logInterest = new AccountLog(tender.getUser().getUserId(), Constant.WAIT_INTEREST, model.getUser().getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logInterest, act, interest, logInterest.getRemark() + "生产待收利息" + NumberUtils.format4(interest));
			accountLogDao.save(logInterest);

			// 借款人资金入账 生成待还
			accountDao.updateAccount(validAccount, validAccount, 0d, 0d, validAccount + interest, borrowUser.getUserId());
			act = accountDao.getAcountByUser(model.getUser());
			AccountLog logBorrowSuc = new AccountLog(borrowUser.getUserId(), Constant.BORROW_SUCCESS, tenderUser.getUserId(), getLogRemark(model), param.getIp());
			fillAccountLog(logBorrowSuc, act, validAccount, logBorrowSuc.getRemark() + "借款入账" + NumberUtils.format4(validAccount));
			accountLogDao.save(logBorrowSuc);

			// 生成还款计划
			borrowRepaymentDao.save(borrowModel.getFlowRepayment(tender, model));
			// 流转标投标 直接划款
			// 汇付流转标扣款修改 周学成 9-17 start
			apiService.flowBorrowLoan(tender, param, tenderUser, model, borrowUser, taskList);
			// 汇付流转标扣款修改 周学成 9-17 end

			// 投标奖励-------让用户先到账，然后 扣除奖励，呵呵(笑你妹)
			String remark = "收到 标[" + model.getName() + "]投资奖励!!";
			double awardValue = 0;
			awardValue = borrowModel.calculateBorrowAward(tender);
			if (awardValue > 0) {
				// 借款人扣除奖励
				accountDao.updateAccount(-awardValue, -awardValue, 0d, 0d, 0d, borrowUser.getUserId());
				act = accountDao.getAcountByUser(borrowUser);
				AccountLog logDeduct = new AccountLog(borrowUser.getUserId(), Constant.AWARD_DEDUCT, tenderUser.getUserId(), getLogRemark(model), param.getIp());
				this.fillAccountLog(logDeduct, act, awardValue, "投资成功，扣除投资奖励" + NumberUtils.format4(awardValue));
				accountLogDao.save(logDeduct);
				// 投资人 获得奖励
				accountDao.updateAccount(awardValue, awardValue, 0d, 0d, 0d, tenderUser.getUserId());
				act = accountDao.getAcountByUser(tenderUser);
				AccountLog logAward = new AccountLog(tenderUser.getUserId(), Constant.AWARD_ADD, borrowUser.getUserId(), getLogRemark(model), param.getIp());
				this.fillAccountLog(logAward, act, awardValue, remark + NumberUtils.format4(awardValue));
				accountLogDao.save(logAward);
				// 奖励,调用第三方
			}
		}
		tenderLog("Tender service end!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());

		// 投标成功，发消息通知投资人
		MsgReq req = new MsgReq();
		req.setIp(param.getIp());
		req.setReceiver(tenderUser);
		req.setSender(new User(Constant.ADMIN_ID));
		req.setMsgOperate(this.msgService.getMsgOperate(10));
		req.setBorrowname(model.getName());
		req.setAccount("" + validAccount);
		req.setApr("" + model.getApr());// 标id
		DisruptorUtils.sendMsg(req);

		MsgReq req1 = new MsgReq();
		req1.setIp(param.getIp());
		req1.setReceiver(borrowUser);
		req1.setSender(new User(Constant.ADMIN_ID));
		req1.setMsgOperate(this.msgService.getMsgOperate(11));
		req1.setBorrowname(model.getName());
		req1.setMoney("" + (model.getAccountYes() + validAccount));
		req1.setAccount("" + model.getAccount());
		req1.setApr("" + model.getApr());
		DisruptorUtils.sendMsg(req1);// 只是执行了该方法未触发第三方

		// 添加投标额外信息
		addTenderProperty(tender);
		return t;

	}

	/**
	 * add by gy 2016-10-21 12:20:16 体验标投标实现
	 */
	public BorrowTender doTenderExperienceBorrow(BorrowParam param, User user) throws Exception {

		// 从新查询防止缓存
		Borrow model = null;
		BorrowTender t = null;
		model = borrowDao.find(param.getId());
		BorrowModel borrowModel = BorrowHelper.getHelper(model); // 封装borrow
		BorrowTender tender = fillTender(borrowModel, param, user); // 封装 borrowTender
		User tenderUser = tender.getUser(); // 投资人
		User borrowUser = model.getUser(); // 借款人
		tenderLog("Begin ExperienceBorrowTender service!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		t = getValidMmmTender(tender, borrowModel, param);
		double validAccount = t.getAccount();
		borrowTenderDao.update(t); // 更新利息 等其他信息。

		SearchParam params = new SearchParam();
		params.addParam("user", tenderUser);
		ExperienceMoney em = this.experienceMoneyDao.findByCriteriaForUnique(params);
		if (em != null) {
			em.setUseStatus(1);
			em.setExperienceInterest(tender.getInterest());
			em.setUseTime(new Date());
			this.experienceMoneyDao.update(em);
		}

		// 修改账户资金
		accountDao.updateAccount(0, 0, 0, t.getInterest(), 0, tenderUser.getUserId());
		tenderLog("freeze account success!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		Account act = accountDao.getAcountByUser(tenderUser);
		AccountLog log = new AccountLog(tender.getUser().getUserId(), Constant.EXPERIENCE_TENDER, model.getUser().getUserId(), getLogRemark(model), param.getIp());
		this.fillAccountLog(log, act, validAccount, log.getRemark() + "投资成功，冻结投资者的体验金" + NumberUtils.format4(validAccount));
		accountLogDao.save(log);
		tenderLog("Tender service end!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());

		// accountDao.updateAccountNotZero(0, -validAccount, validAccount, tenderUser.getUserId());
		//
		// tenderLog("freeze account success!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());
		// Account act = accountDao.getAcountByUser(tenderUser);
		// AccountLog log = new AccountLog(tender.getUser().getUserId(), Constant.TENDER, model.getUser().getUserId(), getLogRemark(model), param.getIp());
		// this.fillAccountLog(log, act, validAccount, log.getRemark() + "投资成功，冻结投资者的投标资金" + NumberUtils.format4(validAccount));
		// accountLogDao.save(log);
		//
		// tenderLog("Tender service end!", tenderUser.getUserId(), model.getId(), tender.getMoney(), tender.getAccount());

		// 投标成功，发消息通知投资人
		MsgReq req = new MsgReq();
		req.setIp(param.getIp());
		req.setReceiver(tenderUser);
		req.setSender(new User(Constant.ADMIN_ID));
		req.setMsgOperate(this.msgService.getMsgOperate(10));
		req.setBorrowname(model.getName());
		req.setAccount("" + validAccount);
		req.setApr("" + model.getApr());// 标id
		DisruptorUtils.sendMsg(req);

		// 投标成功,通知借款人
		MsgReq req1 = new MsgReq();
		req1.setIp(param.getIp());
		req1.setReceiver(borrowUser);
		req1.setSender(new User(Constant.ADMIN_ID));
		req1.setMsgOperate(this.msgService.getMsgOperate(11));
		req1.setBorrowname(model.getName());
		req1.setMoney("" + (model.getAccountYes() + validAccount));
		req1.setAccount("" + model.getAccount());
		req1.setApr("" + model.getApr());
		DisruptorUtils.sendMsg(req1);

		// 添加投标额外信息
		addTenderProperty(tender);
		return t;

	}

	/**
	 * 初始化投标
	 *
	 * @param tender
	 * @param borrow
	 * @param log
	 * @return
	 */
	private BorrowTender getValidMmmTender(BorrowTender tender, BorrowModel borrow, BorrowParam param) {
		Borrow model = borrow.getModel();
		User tenderUser = tender.getUser();
		double tenderAccount = tender.getMoney();
		double account = model.getAccount();
		double accountyYes = model.getAccountYes();
		//add by lijing 投标回调 满标后 发短信通知 审核人
		if(tenderAccount + accountyYes == account){
			logger.info("满标通知审核人");
			try {
				logger.info("满标给审核员发送信息");
				String sendResult = PhoneUtil.sentPhone(1,Global.getValue("full_borrow_notice"), "通知:标号"+model.getId()+",名称:"+model.getName()+"的标已满标,请您及时审核!");
				if("短信发送成功".equals(sendResult)){
				logger.info("满标提醒短信消息发送成功");
				}else {
				logger.info("满标提醒短信消息发送失败");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		logger.info(" 投标的资金记录-1：" + tenderAccount + "  " + accountyYes + "  " + account);
		tenderLog("Tender service,get validAccount!", tender.getUser().getUserId(), model.getId(), tender.getMoney(), tenderAccount);
		borrowDao.updateBorrowAccountyes(model.getId(), tenderAccount, 1); // 更新标的已投金额、投标次数
		logger.info(" 每次进来的   account :" + account + "   accountyes:" + accountyYes + "  validAccount:" + tenderAccount + " userId:" + tenderUser.getUserId());
		// 借贷关系记录插入
		tender.setMoney(tenderAccount);
		tender.setAccount(tenderAccount);
		List<BorrowTender> listTenders = borrowTenderDao.getBorrowTenderList(model.getId());
		List<BorrowCollection> collectList = null;
		if (model.getIsAssignment() == 1) {// 债权转让标的tender 和 collection, 必须和转让人的剩余投资吻合，避免还款产生错误
			BorrowTender assignMentTender = borrowTenderDao.getAssignMentTender(model.getId());
			tender.setAssignment_borrow_id(model.getId());
			List<BorrowCollection> assignCollection = borrowCollectionDao.getAssignMentCollectionByTenderId(assignMentTender);
			collectList = borrow.getAssignMentCollectionList(tender, listTenders, assignMentTender, assignCollection);
		} else {// 正常标产生 collection的方法
			collectList = borrow.getCollectionList(tender, listTenders);
		}
		// 如果是体验标，并且是最后一次还款， 则直接将标的状态改为已完成。
		if (model.getType() == Constant.TYPE_EXPERIENCE && tender.getAccount() + model.getAccountYes() >= model.getAccount()) {
			// 体验金最后一次投标，需要将标的accountYes改为account的值，tenderTimes次数+1
			model.setAccountYes(tender.getAccount() + model.getAccountYes());
			model.setTenderTimes(model.getTenderTimes() + 1);
			model.setStatus(6);
			borrowDao.update(model);
		}

		// add by gy 2016-10-26 16:37:53 添加投标记录中，体验金的关联
		if (param.getExperienceMoney() != null)
			tender.setExperienceMoney(param.getExperienceMoney());
		if (!StringUtils.isBlank(param.getEmLoanNo()))
			tender.setEmTrxId(param.getEmLoanNo());
		if (!StringUtils.isBlank(param.getEmTenderNo()))
			tender.setEmSubOrdId(param.getEmTenderNo());
		tender.setBorrowCollections(collectList);// 待收
		tender.setSubOrdId(param.getTenderNo());
		tender.setTrxId(param.getLoanNo());
		// 投标冻结，调用第三方接口处理
		borrowTenderDao.save(tender); // 保存投资人信息
		return tender;
	}

	// v1.8.0.4 TGPROJECT-27 qj 2014-04-09 stop

	/**
	 * 校验投标金额
	 *
	 * @param tenderAccount
	 * @param hasTender
	 * @param model
	 */
	private void checkSystemRule(double tenderAccount, double hasTender, Borrow model, long tenderId) {
		Rule rule = ruleService.getRuleByNid("tender_account_rule");
		if (rule != null) {
			if (rule.getStatus() == 1) {
				// 校验投标金额的具体规则(是否为tender_money的整数倍)
				if (rule.getValueIntByKey("tender_money_status") == 1) {
					int account = rule.getValueIntByKey("tender_money");
					if (!(tenderAccount % account == 0)) {
						throw new BussinessException("投标金额必须为" + account + "的整数倍！");
					}
				}
				// 校验最大的投标金额限制
				if (rule.getValueIntByKey("most_status") == 1) {
					double account = 0;
					double rate = rule.getValueDoubleByKey("tender_most_rate");
					double most_rate = rate * model.getAccount();
					double most_account = rule.getValueDoubleByKey("tender_most_account");
					account = NumberUtils.format2(most_rate > most_account ? most_account : most_rate);
					if (tenderAccount + hasTender > account) {
						throw new BussinessException("您的投标总金额大于系统指定金额:" + account + "元,您还剩余" + (account - hasTender) + "元可投!");
					}
				}
				// 校验最低的投标金额
				if (rule.getValueIntByKey("lease_status") == 1) {
					double rate = rule.getValueDoubleByKey("tender_least_rate");// 最低投标比例
					double leaseMoney = rate * model.getAccount();// 最低投标金额
					double leaseTenderMoney = (model.getAccount() - model.getAccountYes() - leaseMoney) < leaseMoney ? leaseMoney + (model.getAccount() - model.getAccountYes() - leaseMoney) : leaseMoney; // 若剩余标的金额小于最低投标金额的话，最后一个人的投标金额为：最低投标金额+剩余投标金额。否则为最低投标金额
					if (tenderAccount < leaseTenderMoney) { // 校验投标金额是否满足最低投标金额限制
						throw new BussinessException("本次你的最低投资金额为:" + leaseMoney + "元，请查看后再进行投资");
					}

					double tenderAll = Global.getDouble("tender_account");
					if (tenderAll > 0) {
						double waitAccount = borrowTenderDao.sumCollectionMoney(tenderId);
						double surplusTenderAccount = tenderAll - waitAccount; // 剩余投标金额
						if (surplusTenderAccount < leaseMoney) {
							throw new BussinessException("你的投资总额已经达到平台规定的上线，不能在进行投资操作！");
						}
					}
				}

			}
		}
	}

	private BorrowTender fillTender(BorrowModel b, BorrowParam param, User user) {
		Borrow model = b.getModel();
		BorrowTender tender = new BorrowTender();
		if (model.getType() == Constant.TYPE_FLOW) {
			tender.setMoney(param.getTenderCount() * model.getFlowMoney());
		} else {
			tender.setMoney(param.getMoney());
		}
		tender.setAddtime(new Date());
		tender.setAddip(param.getIp());
		tender.setUser(user);
		tender.setBorrow(model);
		return tender;
	}

	private void checkTender(Borrow b, User u, BorrowParam param) {
		if (b.getStatus() != 1) {
			throw new BussinessException("标的状态异常");
		}
		String pwd = param.getPwd();// 定向密码
		if (!StringUtils.isBlank(b.getPwd())) {// 如果 标设置定向密码
			if (StringUtils.isNull(pwd).equals("")) {
				throw new BussinessException("定向标密码不能为空! ");
			}
			if (!b.getPwd().equals(pwd)) {// 名文保存不要加密
				throw new BussinessException("定向标密码不正确! ");
			}
		}
		if (b.getUser().getUserId() == u.getUserId()) {
			throw new BussinessException("自己不能投自己发布的标！");
		}
		if (b.getIsAssignment() == 1) { // 债权转让标 原来的借款人不能投
			BorrowTender assigMentTender = borrowTenderDao.getAssignMentTender(b.getId());
			Borrow oldBorrow = assigMentTender.getBorrow();
			if (u.getUserId() == oldBorrow.getUser().getUserId()) {
				throw new BussinessException("原借款人，不能投本人的债权转让标");
			}
		}
	}

	/**
	 * 是否可以奖励
	 *
	 * @param model
	 *            当前标种
	 * @param typeNoList
	 *            不合适奖励的标
	 * @return
	 */
	private boolean isRewardCredits(BorrowModel model, String typeStr) {
		String nowBorrowType = model.getType() + "";
		return typeStr.contains(nowBorrowType);

	}

	/**
	 * 初审借款标
	 */
	@Override
	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
	public void verifyBorrow(BorrowModel borrowModel, AccountLog log, BorrowFee borrowFee, List<BorrowDetail> details) throws Exception {
		// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
		Borrow model = borrowModel.getModel();
		Long userId = model.getUser().getUserId();

		if (model.getBorrowFee() == null) {// 如果没有，就添加
			borrowFee.setBorrow(model);
			borrowFeeDao.save(borrowFee);
			borrowFeeDao.flush();// 保存或更新费率

			model.setBorrowFee(borrowFee);
		}
		borrowDao.update(model);
		borrowDao.flush();
		String sendMsg = "";
		if (model.getStatus() != 1) {// 审核不通过----model.getStatus() == 2
			if (model.getType() == Constant.TYPE_SECOND) { // 秒标处理
				double freezeVal = borrowModel.calculateBorrowSecondFreeze();
				Account act = accountDao.find(userId);
				accountDao.updateAccount(0, freezeVal, -freezeVal, userId);
				log.setUser(model.getUser());
				log.setToUser(new User(Constant.ADMIN_ID));
				log.setMoney(freezeVal);
				log.setTotal(act.getTotal());
				log.setUseMoney(act.getUseMoney());
				log.setNoUseMoney(act.getNoUseMoney());
				log.setCollection(act.getCollection());
				sendMsg = "借款标初审不通过，解冻资金" + NumberUtils.format2(freezeVal) + "元";
				log.setRemark(sendMsg);
				accountLogDao.save(log);

			} else if (model.getType() == Constant.TYPE_CREDIT) {
				double amount = model.getAccount();
				UserAmount ua = userAmountDao.getUserAmountByUser(new User(userId));
				if (ua == null) {
					logger.error("用户" + userId + "的信用账户不存在.");
					throw new BussinessException("用户" + userId + "的信用账户不存在.");
				}
				userAmountDao.updateCreditAmount(0, amount, -amount, userId);
				UserAmountLog amountLog = new UserAmountLog();
				amountLog.setType("borrow_cancel");
				amountLog.setType("credit");
				amountLog.setAccount(amount);
				amountLog.setAccountTotal(ua.getCreditUse());
				amountLog.setAccountUse(ua.getCreditUse() + amount);
				amountLog.setAccountNouse(ua.getCreditNouse() - amount);
				amountLog.setRemark("借款标初审不通过,解冻信用：" + amount);
				amountLog.setAddtime(new Date());

				amountLog.setAddip("");
				sendMsg = "借款标初审不通过";
				userAmountLogDao.save(amountLog);
			} else {
				sendMsg = "借款标初审不通过";
			}
			if (model.getIsAssignment() == 1) {// 债权转让的标被拒绝 原来的投资信息要还原
				BorrowTender assignTender = borrowTenderDao.getAssignMentTender(model.getId());
				assignTender.setStatus(0);
				assignTender.setAssignmentId(0);
				borrowTenderDao.update(assignTender);
			}
		} else {// 审核通过，调用自动投标的用户投标
				// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
			if (details != null && details.size() > 0) {
				for (BorrowDetail borrowDetail : details) {
					borrowDetailDao.save(borrowDetail);
				}
			}
			// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 end
			DisruptorUtils.autoTender(borrowModel);
		}
		// 初审，发消息通知<短信、邮件、站内信>
		MsgReq req = new MsgReq();
		req.setReceiver(model.getUser());
		req.setSender(new User(Constant.ADMIN_ID));
		if (model.getStatus() == 1) {// 通过
			req.setMsgOperate(this.msgService.getMsgOperate(15));
		} else {// 不通过
			req.setMsgOperate(this.msgService.getMsgOperate(17));
		}
		req.setTime(DateUtils.dateStr4(new Date()));
		String borrowName = (model.getName() == null ? "" : (model.getName().length() > 20 ? (model.getName().substring(0, 19)) : (model.getName())));
		req.setBorrowname(borrowName);
		req.setApr(NumberUtils.format2Str(model.getApr()));
		req.setMonthapr(NumberUtils.format2Str(model.getApr() / 12));
		// v1.8.0.4_u1 TGPROJECT-260 lx start
		if (model.getBorrowProperty() != null) {
			req.setEndTime(DateUtils.newdateStr6(model.getBorrowProperty().getEndTime()));
		}
		// v1.8.0.4_u1 TGPROJECT-260 lx start
		DisruptorUtils.sendMsg(req);
	}

	private Message getSiteMessage(long receive_user, String title, String content, long sent_user, Message message) {
		message.setSentUser(new User(sent_user));
		message.setReceiveUser(new User(receive_user));
		message.setStatus(0);
		message.setType(Constant.SYSTEM);
		message.setName(title);
		message.setSented(1);
		message.setContent(content);
		return message;
	}

	@Override
	public PageDataList getCollectList(SearchParam param) {
		return borrowCollectionDao.findPageList(param);
	}

	@Override
	public void verifyFullBorrow(BorrowModel borrow, BorrowParam param) throws Exception {
		Borrow model = borrow.getModel();
		User borrowUser = model.getUser();

		Account act = accountDao.getAcountByUser(borrowUser);
		if (model.getType() == Constant.TYPE_SECOND) {// 秒标解冻资金
			double freeze = borrow.calculateBorrowSecondFreeze();
			accountDao.updateAccount(0, freeze, -freeze, 0, 0, borrowUser.getUserId());
			act = accountDao.getAcountByUser(borrowUser);
			AccountLog log = new AccountLog();
			this.fillAccountLog(log, Constant.UNFREEZE, act, borrowUser, new User(Constant.ADMIN_ID), freeze, model.getId(), getLogRemark(model) + "解冻资金" + freeze);
			accountLogDao.save(log);
		}
		borrowDao.update(model);
		logger.info("---borrowId:" + model.getId() + "  status:" + model.getStatus());
		if (model.getStatus() == 3) { // 满标复审通过
			// 债权转让和普通标的审核都通过这里处理，在VerifyFullBorrowTask 里区分
			DisruptorUtils.fullSuccess(borrow, param);
		} else {// 4 满标复审取消
			DisruptorUtils.failBorrow(borrow, param);
		}
	}

	/**
	 * 结标方法， 任何标
	 *
	 * @param borrowId
	 */
	@Override
	public double doBorrowFull(long borrowId) {
		Borrow borrow = getBorrow(borrowId);
		BorrowModel model = BorrowHelper.getHelper(borrow);
		// 下面处理业务。
		List<BorrowTender> tenders = borrow.getBorrowTenders();
		double repayAccount = 0; // 获取截标时应还的总额
		for (BorrowTender borrowTender : tenders) {
			repayAccount += borrowTender.getRepaymentAccount();
		}
		return repayAccount;

	}

	/**
	 * 校验投标回调，是否重复处理投标业务
	 *
	 * @param param
	 */
	private boolean checkAddTenderRepeat(BorrowParam param) {
		int apiTpye = Global.getInt("api_code");
		int result = 0;
		logger.info("投标回调校验方法：" + apiTpye);
		switch (apiTpye) {
		//cancel by lxm 2017-2-13 17:16:58
//		case 1:// 汇付处理投标业务拦截，查询tender，查看是否处理:投标订单号查询，若此投标订单号已经存在，说明已经处理
//			result = borrowTenderDao.sumTenderByTenderNo(param.getTenderNo());
//			return result <= 0;
//		case 2:
//			return true;
		case 3:
			result = borrowTenderDao.sumTenderByTenderNo(param.getTenderNo());
			if (result > 0) {
				return false;
			}
		default:
			return true;
		}

	}

	@Override
	public double sumTenderWaitAccount(long userId) {
		return borrowTenderDao.sumCollectionMoney(userId);
	}

	/**
	 * 调用统一处理。
	 */
	public YjfPay autoYjfPay(YjfPay yjfPay) { // status=1 处理完成； status=2 // 处理失败；status = 0 未处理。

		if ("1".equals(yjfPay.getStatus())) { // 过滤 已经完成的
			throw new BussinessException("已经处理完成，请不要重复处理。");
		}

		return yjfPay;
	}

	/**
	 * 双乾接口调用统一处理。
	 */
	private String autoMmmPay(MmmPay mmmPay) {
		logger.info("双乾接口调用统一处理：" + JSON.toJSONString(mmmPay));
		// 1=处理成功 2 = 处理失败。
		if (mmmPay.getType().equals(MmmType.MMM_PAY_USER)) {// 付款给平台
			logger.info("还款开始：");
			String loanNo = mmmPayUser(mmmPay);
			// v1.8.0.4_u5 TGPROJECT-399 qj 2014-08-14 start
			if (!StringUtils.isBlank(loanNo) && !mmmPay.getOperateType().equals(MmmType.ADDFLOWTENDER)) {
				// v1.8.0.4_u5 TGPROJECT-399 qj 2014-08-14 start
				BorrowTender tender = borrowTenderDao.find(NumberUtils.getLong(mmmPay.getTenderId()));
				tender.setTrxId(loanNo);// 投标冻结订单号
				tender.setSubOrdId(mmmPay.getOrderNo());// 交易日期
				borrowTenderDao.save(tender);
			}
		} else if (mmmPay.getType().equals(MmmType.MMM_REPAY_INTEREST)) {// 还款管理费及分账给平台
			mmmRepay(mmmPay);
		} else if (mmmPay.getType().equals(MmmType.MMM_VERIFY_BORROW)) {// 满标复审审核投标
			mmmVerifyBorrow(mmmPay);
		} else if(mmmPay.getType().equals(MmmType.MMM_VERIFY_BATCHREACHARGE)){//后台批量充值审核
			logger.info("进入后台批量充值审核!");
			mmmVerifyBorrow(mmmPay);//功能上是一样的
		}
		return "";
	}

	// add by lijing 后台充值
	private String autoMmmPay(MmmPay mmmPay, String rechargeType) {
		logger.info("双乾接口后台充值统一处理：" + JSON.toJSONString(mmmPay));
		// 1=处理成功 2 = 处理失败。
		if (mmmPay.getType().equals(MmmType.MMM_PAY_USER)) {// 付款给平台
			logger.info("转账开始：");
			String loanNo = mmmPayUser(mmmPay,rechargeType);
		}
		return "";
	}

	/**
	 * 双钱任务处理 mmm_pay 双乾资金记录 状态默认0：未处理或未处理成功， 处理成功是1，处理失败是2
	 *
	 */
	@Override
	public boolean doMmmTask(List<Object> taskList) {
		boolean isSuccess = true;
		logger.info("双乾任务待处理列表：" + JSON.toJSONString(taskList));
		for (Object obj : taskList) {
			MmmPay mmmPay = (MmmPay) obj;
			mmmPay.setStatus("0");
			if (isSuccess) {
				try {
					mmmPay.setStatus("1");
					autoMmmPay(mmmPay);
					mmmPay.setResultMsg("success");
					logger.info("处理成功...");
				} catch (Exception e) {
					mmmPay.setStatus("2");
					isSuccess = false; // 一旦处理失败，就不在触发;但是记录还要插入记录表
					mmmPay.setResultMsg(e.getMessage());
					logger.info("处理失败..." + e.getMessage());
					// logger.info(e.getMessage());
				}
			}
			try {
				mmmPayDao.save(mmmPay);
			} catch (Exception e) {
				logger.error(e + "保存交易信息出错！！！");
			}
		}
		logger.info("复审的结果：" + isSuccess);
		return isSuccess;
	}

	// v1.8.0.4 TGPROJECT-40 qj 2014-04-10 start

	/**
	 * 环迅接口统一处理方法
	 *
	 * @param taskList
	 * @return
	 */
	@Override
	public boolean doIpsTask(List<Object> taskList) {
		boolean isSuccess = true;
		for (Object obj : taskList) {
			IpsPay ipsPay = (IpsPay) obj;
			ipsPay.setStatus("0");
			if (isSuccess) {
				try {
					autoIpsPay(ipsPay);
					ipsPay.setStatus("1");
					ipsPay.setResultMsg("success");
				} catch (Exception e) {
					ipsPay.setStatus("2");
					isSuccess = false; // 一旦处理失败，就不在触发;但是记录还要插入记录表
					ipsPay.setResultMsg(e.getMessage());
					logger.info(e.getMessage());
				}
			}
			try {
				ipsPayDao.save(ipsPay);
			} catch (Exception e) {
				logger.error(e + "保存交易信息出错！！！");
			}
		}
		return isSuccess;
	}

	@Override
	public String autoIpsPay(IpsPay ipsPay) {
		if (IpsPayType.VERIFY_BORROW_TRANSTER.equals(ipsPay.getPayType())) { // 满标复审转账
			IpsVerifyBorrow(ipsPay);
		}
		return "";
	}

	/**
	 * 统一处理所有的接口任务
	 *
	 * @param taskList
	 * @return 没有接口直接 返回true
	 */
	@Override
	public boolean doApiTask(List<Object> taskList) {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾业务处理
			return doMmmTask(taskList);
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean doApiTask(List<Object> taskList, String rechargeType) {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾业务处理
			return doMmmTask(taskList, rechargeType);
		default:
			break;
		}
		return true;
	}

	private boolean doMmmTask(List<Object> taskList, String rechargeType) {

		boolean isSuccess = true;
		logger.info("双乾任务待处理列表：" + JSON.toJSONString(taskList));
		for (Object obj : taskList) {
			MmmPay mmmPay = (MmmPay) obj;
			mmmPay.setStatus("0");
			if (isSuccess) {
				try {
					autoMmmPay(mmmPay, rechargeType);
					mmmPay.setStatus("1");
					mmmPay.setResultMsg("success");
					logger.info("后台充值处理成功...");
				} catch (Exception e) {
					mmmPay.setStatus("2");
					isSuccess = false; // 一旦处理失败，就不在触发;但是记录还要插入记录表
					mmmPay.setResultMsg(e.getMessage());
					logger.info("后台充值处理失败..." + e.getMessage());
					// logger.info(e.getMessage());
				}
			}
			try {
				mmmPayDao.save(mmmPay);
			} catch (Exception e) {
				logger.error(e + "后台充值保存交易信息出错！！！");
			}
		}
		logger.info("复审的结果：" + isSuccess);
		return isSuccess;

	}

	/**
	 * 查询发标拦截配置
	 *
	 * @param id
	 * @return
	 */
	@Override
	public BorrowConfig getBorrowConfig(int id) {
		return borrowConfigDao.find(id);
	}

	/**
	 * yjf任务调度查询
	 *
	 * @return
	 */
	@Override
	public PageDataList<YjfPay> getYjfPayList(SearchParam sp) {
		return yjfDao.getList(sp);
	}

	@Override
	public PageDataList<MmmPay> getMmmPayList(SearchParam sp) {
		return mmmPayDao.findPageList(sp);
	}

	@Override
	public YjfPay getYjfPayById(int id) {
		return yjfDao.find(id);
	}

	@Override
	public MmmPay getMmmPayById(int id) {
		return mmmPayDao.find(id);
	}

	@Override
	public PageDataList getBorrowRepaymentList(SearchParam param) {
		return borrowRepaymentDao.findPageList(param);
	}

	@Override
	public List getBorrowRepaymentExportList(SearchParam param) {
		return borrowRepaymentDao.findAllPageList(param).getList();
	}

	@Override
	public BorrowRepayment getBorrowRepaymentById(long repayMentId) {
		return borrowRepaymentDao.find(repayMentId);
	}

	@Override
	public BorrowRepayment getBorrowRepaymentByBorrowIdAndPeriod(long borrowId, int repaymentId) {
		return borrowRepaymentDao.getBorrowRepaymentByBorrowIdAndPeriod(borrowId, repaymentId);
	}

	/**
	 * 填充borrowTenderProperty对象
	 *
	 * @param tender
	 */
	private void addTenderProperty(BorrowTender tender) {
		// 创建投标订单号：
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		Rule rule = ruleService.getRuleByNid("borrow_detail");
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 end
		if (rule != null) {
			if (rule.getStatus() == 1) {
				int result = rule.getValueIntByKey("addtender_property");
				if (result == 1) {
					int numb = borrowTenderDao.sumTenderByDay(); // 获取当天投标总比数
					int randNu = (int) (Math.random() * 90) + 10; // 创建随机数
					String tenderNo = StringUtils.madeAgreementNo("TZ", DateUtils.newdateStr3(new Date()), numb, randNu);
					TenderProperty tenderProperty = new TenderProperty();
					tenderProperty.setBorrowTender(tender);
					tenderProperty.setTenderNo(tenderNo);
					tenderPropertyDao.save(tenderProperty);
				}
			}
		}
	}

	@Override
	public double sumBorrowAccountByUserId(long userId) {
		double waitRepayAccount = borrowRepaymentDao.sumNotRepaymentAccountByUser(userId); // 从repayment中查询出用户的待还总额
		double borrowIngAccount = borrowDao.sumBorrowAccount(userId); // 从borrow表中查询出正在招标的借款总和
		return waitRepayAccount + borrowIngAccount;
	}

	/**
	 * 投标日志
	 *
	 * @param type
	 * @param user_id
	 * @param bid
	 * @param money
	 * @param account
	 */
	private void tenderLog(String type, long user_id, long bid, double money, double account) {
		logger.info(type + "[uid=" + user_id + ",bid=" + bid + ",money=" + money + ",account=" + account + "]");
	}

	@Override
	public List<Borrow> getBorrowListOrderByStatus() {
		return borrowDao.getBorrowListOrderByStatus();
	}

	/**
	 * 查询所有的用户 满足的话就投标
	 *
	 * @param model
	 */
	@Override
	public void doAutoTender(BorrowModel model) throws Exception {
		Borrow borrow = model.getModel();
		// 查询自动投标用户的列表
		List<ViewAutoInvest> allUsers = viewAutoInvestDao.findAll();
		for (ViewAutoInvest auto : allUsers) {
			logger.info("userid:" + auto.getUserId() + "  开始自动投标");
			// 校验用户的条件开始投标 ， 自动投标只能投标一次
			if (auto.getStatus() == 0) {// 没有启用
				logger.info("自动投标没启用，返回");
				continue;
			}
			if (auto.getTenderAccount() < 50) {// 每次投标金额不能小于50
				logger.info("金额小于50，返回");
				continue;
			}
			String borrowStyle = auto.getBorrowStyle();
			String nowStyle = borrow.getStyle();
			if (auto.getBorrowStyleStatus() == 1 && !borrowStyle.contains(nowStyle)) {
				logger.info("还款方式不对,borrow_id:" + borrow.getId() + " style:" + nowStyle);
				continue;
			}

			if (auto.getTimelimitStatus() == 1) {// 借款期限判断
				if (auto.getTimelimitMonthFirst() != 0 && auto.getTimelimitMonthLast() != 0) {// 月标判断
					if (!(borrow.getIsday() != 1 && borrow.getTimeLimit() >= auto.getTimelimitMonthFirst() && borrow.getTimeLimit() <= auto.getTimelimitMonthLast())) {// 不在范围内设置成false
						logger.info("借款期限方式不对，borrow_id:" + borrow.getId());
						continue;
					}
				}
				if (auto.getTimelimitDayFirst() != 0 && auto.getTimelimitDayFirst() != 0) {// 天标判断
					if (borrow.getIsday() == 1 && borrow.getTimeLimitDay() >= auto.getTimelimitDayFirst() && borrow.getTimeLimitDay() <= auto.getTimelimitDayLast()) {
					} else {// 不在范围内设置成 false;
						logger.info("借款期限方式不对，borrow_id:" + borrow.getId());
						continue;
					}
				}
			}

			if (auto.getAprStatus() == 1) {// 年利率判断
				if (borrow.getApr() >= auto.getAprFirst() && borrow.getApr() <= auto.getAprLast()) {
				} else {// 不在范围内设置成 false;
					logger.info("年利率判断方式不对，borrow_id:" + borrow.getId());
					continue;
				}
			}

			if (auto.getAwardStatus() == 1) {// 投标奖励判断
				if (borrow.getPartAccount() >= auto.getAward()) {
				} else {// 不在范围内设置成 false;
					logger.info("投标奖励方式不对，borrow_id:" + borrow.getId());
					continue;
				}

			}

			String borrowType = auto.getBorrowType(); // 可投标的type集合
			String nowType = borrow.getType() + "";
			if (!borrowType.contains(nowType)) {
				logger.info("投标类型方式不对，borrow_type:" + borrow.getType());
				continue;
			}

			// 能投标的用户进来
			long userId = auto.getUserId();
			User user = userDao.find(userId);
			if (user == null) {
				throw new ManageBussinessException("用户： userId:" + userId + "  查询不到！！！");
			}
			BorrowParam param = new BorrowParam();
			param.setAuto(true);
			param.setId(borrow.getId());
			if (borrow.getType() == Constant.TYPE_FLOW) {
				double floeMoney = borrow.getFlowMoney();
				int count = (int) Math.floor(auto.getTenderAccount() / floeMoney);
				param.setMoney(floeMoney * count);
				param.setTenderCount(count);
			} else {
				param.setMoney(auto.getTenderAccount());
			}
			param.setIp("127.0.0.1");
			DisruptorUtils.tender(param, user);
		}
	}

	@Override
	public void webSitePayForLateBorrow(long repaymentId, BorrowParam param) throws Exception {
		/*
		 * 垫付需求修改： 1：垫付当期<垫付类型： 本金、利息、逾期罚息、分vip > 2：垫付当期及剩余各期<垫付类型：本金、利息、逾期罚息、分vip>
		 */
		Rule rule = ruleService.getRuleByNid("web_site_pay");

		// now_repayment_method默认值：1，融都标准的网站垫付，其他的网站特有的垫付，以后可以配置
		int now_repayment_method = rule.getValueIntByKey("now_repayment_method");
		// all_repayment_method:默认值：1，融都标准垫付规则，2好利贷垫付规则。
		int all_repayment_method = rule.getValueIntByKey("all_repayment_method");

		List<Object> taskList = new ArrayList<Object>(); // 任务列表

		double webSitePayMoney = 0d; // 总共垫付金额
		BorrowRepayment br = borrowRepaymentDao.find(repaymentId);
		if (!repaymentService.checkNoPayedRepayment(br.getPeriod(), br.getBorrow().getId())) {
			throw new ManageBussinessException("您还有未垫付的还款，请您依次垫付，不能越级垫付。");
		}
		Borrow borrow = br.getBorrow();
		if (br.getWebstatus() != 0 && br.getStatus() != 0) {
			logger.info("网站垫付当期：repaymentId: " + br.getId() + " borrowId:" + borrow.getId() + ", status:" + br.getStatus() + " webstatus:" + br.getWebstatus() + " ， 不在垫付的状态，请详细查看");
			throw new ManageBussinessException("垫付还款状态不对，请联系技术人员！！！");
		}
		if (param.getType() == 1) {// 垫付当前期+
			switch (now_repayment_method) {
			case 1: // 网站默认值：1，标准的垫付
				dealCommonNowRepayment(br, borrow, taskList, rule);
				break;
			default:
				break;
			}
		} else if (param.getType() == 2) {// 垫付所有
			switch (all_repayment_method) {
			case 1:// 默认值：1，网站标准的垫付规则
				dealCommonAllRepayment(br, borrow, taskList, rule);
				break;
			case 2: // 值：2，好利贷垫付全部方法调用
				dealHaoLiDaiAllRepayment(br, borrow, taskList, rule);
				break;
			default:
				break;
			}
		}
		// 处理接口任务。
		doApiTask(taskList);
	}

	private void dealCommonNowRepayment(BorrowRepayment br, Borrow borrow, List<Object> taskList, Rule rule) {
		double webPaidAccount = 0; // 网站垫付总本金
		double webPaidInterest = 0; // 网站垫付总利息
		double webPaidLateInterest = 0; // 网站垫付的总罚息

		ApiPayParamModel apm = new ApiPayParamModel();// 第三方接口封装参数对象。

		boolean isCapital = "1".equals(rule.getValueStrByKey("capital"));// 是否开通垫付本金
		double capital_vip_rate = rule.getValueDoubleByKey("capital_vip_rate");// vip本金垫付比例
		double capital_not_vip_rate = rule.getValueDoubleByKey("capital_not_vip_rate");// 非vip本金垫付比例

		boolean isInterest = "1".equals(rule.getValueStrByKey("interest"));// 是否开通垫付利息
		double interest_vip_rate = rule.getValueDoubleByKey("interest_vip_rate");// vip垫付利息比例
		double interest_not_vip_rate = rule.getValueDoubleByKey("interest_not_vip_rate");// 非vip垫付利息比例

		boolean isBorrowFee = "1".equals(rule.getValueStrByKey("borrow_fee"));// 利息管理费
		double borrow_fee_vip_rate = rule.getValueDoubleByKey("borrow_fee_vip_rate");// vip利息管理费
		double borrow_fee_not_vip_rate = rule.getValueDoubleByKey("borrow_fee_not_vip_rate");// 非vip利息管理费

		boolean isLate_interest = "1".equals(rule.getValueStrByKey("late_interest"));// 是否开通逾期垫付
		double late_interest_vip_rate = rule.getValueDoubleByKey("late_interest_vip_rate");// vip逾期垫付比例
		double late_interest_not_vip_rate = rule.getValueDoubleByKey("late_interest_not_vip_rate");// 非vip逾期垫付比例

		BorrowModel borrowModel = BorrowHelper.getHelper(borrow);
		// 逾期罚息， 平台和投资人共分，按照设定的比例算要垫付的利息
		double lateInterest = br.getLateInterest();// 借款人逾期罚息总和
		// 还投资人的本金和利息和逾期罚息
		List<BorrowCollection> collectionList = borrowCollectionDao.getCollectionByBorrowAndPeriod(borrow.getId(), br.getPeriod());
		for (BorrowCollection bc : collectionList) {// 循环投资人，当期待收
			BorrowTender bt = bc.getBorrowTender();// 投资记录
			double capital = bc.getCapital(); // 应得本金
			double interest = bc.getInterest();// 应得利息
			User tenderUser = bc.getBorrowTender().getUser();
			long tenderUserId = tenderUser.getUserId();
			boolean isVip = (tenderUser.getUserCache().getVipStatus() == 1);
			// 垫付投资人本金

			if (isCapital) {// 设定是否垫付本金
				if (capital > 0) {
					double realCapital = capital;// 本金真实的垫付金额
					if (isVip) {
						realCapital = NumberUtils.format2(realCapital * capital_vip_rate);
					} else {
						realCapital = NumberUtils.format2(realCapital * capital_not_vip_rate);
					}
					realCapital = NumberUtils.format2(realCapital);
					accountDao.updateAccount(-(capital - realCapital), realCapital, 0, -capital, 0, tenderUserId);// 收入应该加上真正的垫付，待收应该减去所有原来的待收。
					Account cAct = accountDao.getAcountByUser(tenderUser);
					// 插入投资人的log
					AccountLog capitalLog = new AccountLog(tenderUser.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
					fillAccountLog(capitalLog, Constant.WEBSITEPAY, cAct, tenderUser, new User(Constant.ADMIN_ID), realCapital, 0, "[" + getLogRemark(borrow) + "]网站垫付，据网站规则实际垫付本金" + realCapital + "元,扣除待收本金" + capital + "元");
					accountLogDao.save(capitalLog);
					// 插入垫付log
					SitePayLog sitePayLogLast = sitePayLogDao.getLastSitePayLog();
					double total1 = 0d;
					double total2 = 0d;
					if (sitePayLogLast == null) {
						total1 += realCapital;
						total2 += capital;
					} else {
						total1 = sitePayLogLast.getAccountTotal() + realCapital;
						total2 += sitePayLogLast.getMoneyTotal() + capital;
					}
					SitePayLog capSitePayLog = new SitePayLog();
					fillSitePayLog(capSitePayLog, realCapital, total1, borrow, bt, capital, total2, Constant.SITE_PAY_CAPITAL, tenderUser);
					sitePayLogDao.save(capSitePayLog);// 垫付记录结束

					// 调用汇付、易极付等接口，还款本金添加任务。
					if (realCapital > 0) {
						apiService.webSitePayLoanMoney(borrow, taskList, tenderUser, realCapital);
					}
					// 更新borrowCollection
					bc.setCapitalSitePay(bc.getCapitalSitePay() + realCapital);// 实际垫付本金
					bc.setRepayYescapital(bc.getRepayYescapital() + realCapital);// 实际还款本金

					// 更新borrowTender
					bt.setRepaymentYescapital(bt.getRepaymentYescapital() + realCapital);
					bt.setCapitalWebSitePay(bt.getCapitalWebSitePay() + realCapital);
					// TGPROJECT-351 计算本金和 start
					webPaidAccount = webPaidAccount + realCapital;
					// TGPROJECT-351 计算本金和 end
				}
			} else {// 垫付的过程中不垫付本金,本金的待收和总金额要修改掉
				accountDao.updateAccount(-capital, 0, 0, -capital, 0, tenderUserId);// 收入应该加上真正的垫付，待收应该减去所有原来的待收。
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog capitalLog = new AccountLog(tenderUser.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
				fillAccountLog(capitalLog, Constant.WEBSITEPAY, cAct, tenderUser, new User(Constant.ADMIN_ID), capital, 0, "[" + getLogRemark(borrow) + "]网站垫付，据网站规则未垫付本金,扣除待收本金" + capital + "元");
				accountLogDao.save(capitalLog);
			}

			if (isInterest) {// 设定是否垫付利息
				if (interest > 0) { // 还利息
					double realInterest = interest;// 真实垫付的金额
					if (isVip) {
						realInterest = NumberUtils.format2(realInterest * interest_vip_rate);
					} else {
						realInterest = NumberUtils.format2(realInterest * interest_not_vip_rate);
					}

					// ↓↓↓↓↓↓↓↓↓↓垫付收取，管理费规则：利息-利息管理费。垫付利息的时候 总金额=利息-利息管理费
					// 扣除投资人利息管理费
					String borrowFeeMsg = "";
					if (isBorrowFee) {// 根据规则判断是否启用扣除除管理费
						double borrowFee = 0;
						if (isVip) {// vip处理
							borrowFee = NumberUtils.format2(realInterest * borrow_fee_vip_rate);// vip管理费收取比例
						} else {
							borrowFee = NumberUtils.format2(realInterest * borrow_fee_not_vip_rate);// 非vip管理费收取比例
						}
						realInterest = realInterest - borrowFee;// 扣除利息管理费。
						borrowFeeMsg = ",<注：已经扣除网站服务费：" + borrowFee + ">";
					}

					accountDao.updateAccount(-(interest - realInterest), realInterest, 0, -interest, 0, tenderUserId);// 同本金一样处理
					Account cAct = accountDao.getAcountByUser(tenderUser);
					AccountLog interestLog = new AccountLog(tenderUser.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
					fillAccountLog(interestLog, Constant.WEBSITEPAY, cAct, tenderUser, new User(Constant.ADMIN_ID), realInterest, 0, "[" + getLogRemark(borrow) + "]网站垫付，据网站规则实际垫付利息" + realInterest + "元" + borrowFeeMsg + ",扣除待收利息" + interest + "元");
					accountLogDao.save(interestLog);

					// 插入垫付log
					SitePayLog sitePayLogLast = sitePayLogDao.getLastSitePayLog();
					double total1 = 0d;
					double total2 = 0d;
					if (sitePayLogLast == null) {
						total1 += realInterest;
						total2 += interest;
					} else {
						total1 = sitePayLogLast.getAccountTotal() + realInterest;
						total2 += sitePayLogLast.getMoneyTotal() + interest;
					}
					SitePayLog capSitePayLog = new SitePayLog();
					fillSitePayLog(capSitePayLog, realInterest, total1, borrow, bt, interest, total2, Constant.SITE_PAY_INTEREST, tenderUser);
					sitePayLogDao.save(capSitePayLog);// 垫付记录结束

					// 调用易极付、汇付等接口，还款利息添加任务
					if (realInterest > 0) {
						apiService.webSitePayLoanMoney(borrow, taskList, tenderUser, realInterest);
					}
					// 更新borrowCollection
					bc.setInterestSitePay(bc.getInterestSitePay() + realInterest);// 垫付利息
					bc.setRepayYesinterest(bc.getRepayYesinterest() + realInterest);// 实际还款利息
					// 更新borrowTender
					bt.setRepaymentYesinterest(bt.getRepaymentYesinterest() + realInterest);
					bt.setInterestWebSitePay(bt.getInterestWebSitePay() + realInterest);
					// TGPROJECT-351 计算一共垫付利息 start
					webPaidInterest = webPaidInterest + realInterest;
					// TGPROJECT-351 计算一共垫付利息 end
				}
			} else {// 根据网站不垫付利息，待收和总金额必须扣除
				accountDao.updateAccount(-interest, 0, 0, -interest, 0, tenderUserId);// 同本金一样 处理
				Account cAct = accountDao.getAcountByUser(tenderUser);
				AccountLog interestLog = new AccountLog(tenderUser.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
				fillAccountLog(interestLog, Constant.WEBSITEPAY, cAct, tenderUser, new User(Constant.ADMIN_ID), interest, 0, "[" + getLogRemark(borrow) + "]网站垫付，据网站规则未垫付利息,扣除待收利息" + interest + "元");
				accountLogDao.save(interestLog);
			}

			if (isLate_interest) {// 设定是否垫付逾期罚息
				// 垫付当期，还款给当期所有投资人的逾期罚息。
				if (lateInterest > 0) {
					double tendersLateInterest = lateInterest; // 实际垫付逾期罚息总和
					// 还投资人 逾期的利息 按投资的比例来计算
					double totalRepay = borrow.getAccount(); // 此期借款总金额
					double tenderLateInterest = 0d;
					if (isVip) {// vip
						tenderLateInterest = NumberUtils.format2(tendersLateInterest * (bc.getBorrowTender().getAccount() / totalRepay) * late_interest_vip_rate);
					} else { // not vip
						tenderLateInterest = NumberUtils.format2(tendersLateInterest * (bc.getBorrowTender().getAccount() / totalRepay) * late_interest_not_vip_rate);
					}
					if (tenderLateInterest > 0) {
						tenderLateInterest = NumberUtils.format2(tenderLateInterest);
						accountDao.updateAccount(tenderLateInterest, tenderLateInterest, 0, 0, 0, tenderUser.getUserId());
						Account cAct = accountDao.getAcountByUser(tenderUser);
						AccountLog lateInterestLog = new AccountLog(tenderUser.getUserId(), Constant.LATE_COLLECTION, Constant.ADMIN_ID);
						fillAccountLog(lateInterestLog, Constant.LATE_COLLECTION, cAct, tenderUser, new User(Constant.ADMIN_ID), tenderLateInterest, 0, "[" + getLogRemark(borrow) + "]网站垫付，据规则垫付逾期罚息" + tenderLateInterest);
						lateInterestLog.setRemark("[" + getLogRemark(borrow) + "]网站垫付，归还逾期罚息" + tenderLateInterest + "元");
						accountLogDao.save(lateInterestLog);

						// 插入垫付log
						SitePayLog sitePayLogLast = sitePayLogDao.getLastSitePayLog();
						double total1 = 0d;
						double total2 = 0d;
						if (sitePayLogLast == null) {
							total1 += tenderLateInterest;
							total2 += tenderLateInterest;
						} else {
							total1 += sitePayLogLast.getAccountTotal() + tenderLateInterest;
							total2 += sitePayLogLast.getMoneyTotal() + tenderLateInterest;
						}
						SitePayLog capSitePayLog = new SitePayLog();
						fillSitePayLog(capSitePayLog, tenderLateInterest, total1, borrow, bt, tenderLateInterest, total2, Constant.SITE_PAY_LATE_INTEREST, tenderUser);
						sitePayLogDao.save(capSitePayLog);// 垫付记录结束

						// 调用易极付、汇付的接口，还逾期罚息。
						if (tenderLateInterest > 0) {
							apiService.webSitePayLoanMoney(borrow, taskList, tenderUser, tenderLateInterest);
						}
					}
					bc.setLateDays(br.getLateDays());
					bc.setLateInterest(tenderLateInterest);
					bt.setLateAccount(bt.getLateAccount() + tenderLateInterest);// 逾期利息合计
					// TGPROJECT-351 计算网站垫付的罚息总和 start
					webPaidLateInterest = webPaidLateInterest + tenderLateInterest;
					// TGPROJECT-351 计算网站垫付的罚息总和 start
				}
			}
			// 更新tender记录
			if (borrowModel.isLastPeriod(br.getPeriod())) {
				bt.setStatus(1);
			} // 最后一期全部改成完成 状态是 1
			bt.setWaitAccount((bt.getWaitAccount() - capital)); // 不能小于0
			bt.setWaitInterest(bt.getWaitInterest() - interest);
			borrowTenderDao.update(bt);
			// 更新collection记录
			bc.setStatus(1);
			bc.setRepayYestime(new Date());
			bc.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_SITE_PAY));
			borrowCollectionDao.save(bc);
			// 更新repayment status = 2 , webstatus = 3 代表是网站垫付。
			br.setStatus(2);
			br.setWebstatus(3);
			borrowRepaymentDao.update(br);

		}

		// wsl 满标前补偿金功能【心意贷】2014-08-29 start
		tenderCompensationService.sitePayLateBorrowCompensation(br, taskList);
		// wsl 满标前补偿金功能【心意贷】2014-08-29 end

		// TGPROJECT-351 插入垫付记录，进行网站自动扣款 start
		webPaidService.doWebPay(br, webPaidAccount, webPaidInterest, webPaidLateInterest, br.getCapital() + br.getInterest() + br.getLateInterest());
		// TGPROJECT-351 插入垫付记录 end
	}

	private void dealCommonAllRepayment(BorrowRepayment br, Borrow borrow, List<Object> taskList, Rule rule) {
		List<BorrowRepayment> notRepayments = borrowRepaymentDao.getNotRepayByBorrow(borrow.getId());
		for (BorrowRepayment borrowRepayment : notRepayments) {
			dealCommonNowRepayment(borrowRepayment, borrow, taskList, rule);
		}
	}

	private void dealHaoLiDaiAllRepayment(BorrowRepayment br, Borrow borrow, List<Object> taskList, Rule rule) {

		ApiPayParamModel apm = new ApiPayParamModel();// 第三方参数封装对象。

		List<BorrowTender> tenderList = borrow.getBorrowTenders();
		for (BorrowTender tender : tenderList) {
			List<BorrowCollection> collectonList = new ArrayList<BorrowCollection>();
			if (tender.getStatus() != 0) {
				throw new ManageBussinessException("垫付状态错误，请联系技术人员");
			}
			User tender_user = tender.getUser();
			boolean isVip = (tender_user.getUserCache().getVipStatus() == 1);

			double tenderAccount = tender.getAccount();// 投标金额
			double repayYesAccount = tender.getRepaymentYescapital() + tender.getRepaymentYesinterest() + tender.getLateAccount();// 投资人实际收到的所有还款
			/*
			 * 根据好利贷规则，应该垫付的资金 = 投资金额 - （实际中收到的本金和利息+罚息）的净值。
			 */
			double dealMoney = tenderAccount - repayYesAccount;// 根据好利贷规则，应该垫付的资金
			double repayMoney = tender.getWaitAccount() + tender.getWaitInterest();// 正常情况下应该还总金额

			if (dealMoney > 0) {// 如果本期投资未收回投资款，进行垫付
				if (!isVip) {// 如果不是vip则只垫付对于垫付金额的50%
					dealMoney = dealMoney / 2;
				}
				accountDao.updateAccount(-(repayMoney - dealMoney), dealMoney, 0, -repayMoney, 0, tender_user.getUserId());
				Account cAct = accountDao.getAcountByUser(tender_user);
				AccountLog capitalLog = new AccountLog(tender_user.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
				fillAccountLog(capitalLog, Constant.WEBSITEPAY, cAct, tender_user, new User(Constant.ADMIN_ID), repayMoney, 0, "[" + getLogRemark(borrow) + "]网站垫付本期及剩余全部，实际垫付" + dealMoney + "元" + ",扣除待收" + repayMoney + "元");
				accountLogDao.save(capitalLog);

				// 插入垫付log
				SitePayLog sitePayLogLast = sitePayLogDao.getLastSitePayLog();
				double total1 = 0d;
				double total2 = 0d;
				if (sitePayLogLast == null) {
					total1 += dealMoney;
					total2 += repayMoney;
				} else {
					total1 += sitePayLogLast.getAccountTotal() + dealMoney;
					total2 += sitePayLogLast.getMoneyTotal() + repayMoney;
				}
				SitePayLog capSitePayLog = new SitePayLog();
				fillSitePayLog(capSitePayLog, dealMoney, total1, borrow, tender, repayMoney, total2, Constant.SITE_PAY_LATE_INTEREST, tender_user);
				sitePayLogDao.save(capSitePayLog);// 垫付记录结束

				tender.setRepaymentYescapital(tender.getRepaymentYescapital() + dealMoney);// 好利贷全部还款，垫付实际金额值记录在本金里边
				tender.setWaitInterest(0);// 垫付全部，清除待收
				tender.setWaitAccount(0);// 垫付全部，清除待收
				tender.setCapitalWebSitePay(tender.getCapitalWebSitePay() + dealMoney);
				tender.setStatus(1);
			} else {// 根据好贷规则，网站不给予垫付；但是待收 和 总金额必须更新。
				accountDao.updateAccount(-repayMoney, 0, 0, -repayMoney, 0, tender_user.getUserId());
				Account cAct = accountDao.getAcountByUser(tender_user);
				AccountLog capitalLog = new AccountLog(tender_user.getUserId(), Constant.WEBSITEPAY, Constant.ADMIN_ID);
				fillAccountLog(capitalLog, Constant.WEBSITEPAY, cAct, tender_user, new User(Constant.ADMIN_ID), repayMoney, 0, "[" + getLogRemark(borrow) + "]网站垫付本期及剩余全部，据好利贷垫付规则，您未获取垫付，扣除待收：" + repayMoney);
				accountLogDao.save(capitalLog);
				tender.setWaitInterest(0);// 垫付全部，清除待收
				tender.setWaitAccount(0);// 垫付全部，清除待收
				tender.setStatus(1);
			}

			List<BorrowCollection> borrowCollectoins = tender.getBorrowCollections();
			int i = 0;
			for (BorrowCollection bc : borrowCollectoins) {
				if (bc.getStatus() == 0) {
					bc.setStatus(1);
					bc.setRepayYestime(new Date());
					if (i == 0) {// 好利贷全部还款，垫付实际金额值记录在未还款中第一条本金里边。
						bc.setRepayYescapital(bc.getRepayYescapital() + dealMoney);
					}
					bc.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_SITE_PAY));
					collectonList.add(bc);
					i++;
				}
			}
			borrowCollectionDao.update(collectonList);
			borrowTenderDao.update(tenderList);
		}
		borrowRepaymentDao.updateBorrowWebstatus(borrow.getId());
	}

	/**
	 * 封装债权转让，列表
	 */
	@Override
	public PageDataList<BorrowTender> getAssignmentBorrowTenders(SearchParam param) {
		PageDataList<BorrowTender> dataList = getTenderList(param);
		List<BorrowTender> tenderList = dataList.getList();
		List<BorrowTender> newTenderList = new ArrayList<BorrowTender>();
		for (BorrowTender bt : tenderList) {
			String message = checkAssignMentBorrow(bt);
			if (StringUtils.isBlank(message)) {
				newTenderList.add(bt);
			}
		}
		dataList.setList(newTenderList);
		return dataList;
	}

	/**
	 * 核对是否能发 债权转让标
	 *
	 * @return
	 */
	@Override
	public String checkAssignMentBorrow(BorrowTender bt) {
		int curr_period = 0;
		String message = null;

		Borrow b = bt.getBorrow();
		int period = b.getTimeLimit();// 期数

		if (bt.getStatus() != 0) {
			message = "标状态不正确，请您重新核实";
		}
		Borrow assignMentBorrow = borrowDao.getAssignMentBorrowByTenderId(bt.getId());
		if (assignMentBorrow != null) {
			message = "债权已经转让，请您重新核实";
		}
		List<BorrowRepayment> borrowRepayments = b.getBorrowRepayments();
		for (int i = 0; i < borrowRepayments.size(); i++) {
			curr_period++;
			BorrowRepayment br = borrowRepayments.get(i);
			int status = br.getStatus();
			int webstatus = br.getWebstatus();
			if (status == 0 && webstatus == 0) {// 未还款中 不能有逾期
				Calendar repayTime = Calendar.getInstance();
				repayTime.setTime(br.getRepaymentTime());
				Calendar now = Calendar.getInstance();
				now.setTime(new Date());
				if (now.compareTo(repayTime) > 0) {
					message = "tenderId:" + bt.getId() + " 有逾期不能债权转让";
					break;
				}
			}
			if (curr_period == period) {// 最后一期
				if (br.getStatus() == 0 && br.getWebstatus() == 0) {
				} else {
					message = "tenderId:" + bt.getId() + " 全部还款暂时不能能债权转让";
					break;
				}
			}
			if (br.getStatus() == 0 && br.getWebstatus() == 1) {
				message = "tenderId:" + bt.getId() + " 正在还款，暂时不能能债权转让";
				break;
			}
			if (bt.getWaitAccount() <= Double.parseDouble(Global.getValue("min_assignment_money"))) {
				message = "tenderId:" + bt.getId() + " 小于设置的最小转让金额：" + Global.getValue("min_assignment_money");
				break;
			}
		}
		return message;
	}

	@Override
	public Borrow getAssignMentBorrowByTenderId(long tenderId) {
		return borrowDao.getAssignMentBorrowByTenderId(tenderId);
	}

	@Override
	public BorrowTender getAssignMentTenderByBorrowId(long borrowId) {
		return borrowTenderDao.getAssignMentTender(borrowId);
	}

	@Override
	public void isCanVerifyFullSuccess(long borrowId) {
		List<YjfPay> list = yjfDao.getWrongStatusYjfPayByBorrowId(borrowId, YjfType.TRADEPAYERAPPLYPOOLTOGETHER);
		if (list.size() > 0) {
			throw new ManageBussinessException("投标记录中有调用" + Global.getValue("api_name") + "接口失败的用户，请您先处理", "/admin/borrow/schedule.html");
		}
	}

	@Override
	public int getLateRepaymentByUser(User user) {
		return borrowRepaymentDao.getLateRepaymentByUser(user);
	}

	@Override
	public double getSuccessBorrowSumAccount() {
		return borrowDao.getSuccessBorrowSumAccount();
	}

	@Override
	public void addBorrowProperty(BorrowProperty borrowProperty) {
		borrowPropertyDao.save(borrowProperty);
		logger.info("保存标的子类型信息:borrowid:" + borrowProperty.getBorrow().getId() + "标的类型" + borrowProperty.getPropertyType());

	}

	@Override
	public int countBorrowByDay() {
		return borrowDao.sumBorrowByDay();
	}

	@Override
	public int countTenderByDay() {
		return borrowTenderDao.sumTenderByDay();
	}

	@Override
	public int getlimtTime(int id) {
		Linkage linkage = linkageDao.getLinkageById(id);
		return NumberUtils.getInt(linkage.getValue());
	}
	
	//cancel by lxm 2017-2-13 17:16:11    no used
//	/**
//	 * 发标时各种接口的特殊校验
//	 */
//	private void checkBorrow(Borrow model) {
//		int apiType = Global.getInt("api_code");
//		switch (apiType) {
//		case 1:
//			break;
//		case 2: // 易极付发标复审操作校验
//			String apiId = model.getUser().getApiId();
//			if ("1".equals(Global.getValue("open_yjf"))) { // 不开通易极付 不校验易 极付
//				YjfPay yjfPay = yjfDao.getBorrowTradeNo(apiId, model.getId() + "", 1);
//				if (model.getStatus() == 1) {
//					if (yjfPay == null) {
//						throw new BussinessException("发标创建交易失败，请撤回该标！！！");
//					}
//				}
//			}
//			break;
//		default:
//			break;
//		}
//	}

	@Override
	public ChinaPnrPayModel getChinapnrPayById(int id) {
		return chinapnrPayDao.find(id);
	}

	@Override
	public PageDataList<ChinaPnrPayModel> getChinapnrList(SearchParam param) {

		return chinapnrPayDao.findPageList(param);
	}

	// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
	public List<BorrowCollection> getCollectionByBorrowIdAndPeriod(long borrowId, int period) {
		return borrowCollectionDao.getCollectionByBorrowIdAndPeriod(borrowId, period);
	}

	// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end

	// v1.8.0.4_u1 TGPROJECT-127 lx start
	@Override
	public PageDataList<Borrow> getFriendBorrowList(SearchParam param) {
		return borrowDao.findPageList(param);
	}

	// v1.8.0.4_u1 TGPROJECT-127 lx end
	// v1.8.0.4_u1 TGPROJECT-240 zf start
	@Override
	public PageDataList<BorrowAuto> findAutoTenderList(SearchParam param) {

		return borrowAutoDao.findPageList(param);
	}

	// v1.8.0.4_u1 TGPROJECT-240 zf end
	// v1.8.0.4_u2 TGPROJECT-299 lx start
	@Override
	public List<BorrowCollection> getCollectionList(long borrowId) {
		return borrowCollectionDao.getCollectionList(borrowId);
	}

	// v1.8.0.4_u2 TGPROJECT-299 lx end
	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 start
	@Override
	public void addBorrowIntent(BorrowIntent borrowIntent) {
		borrowIntentDao.save(borrowIntent);
	}

	@Override
	public PageDataList<BorrowIntent> findListBorrowIntent(SearchParam param) {
		return borrowIntentDao.findPageList(param);
	}

	@Override
	public BorrowIntent getBorrowIntent(long id) {
		return borrowIntentDao.find(id);
	}

	@Override
	public void updateBorrowIntent(BorrowIntent borrowIntent) {
		borrowIntentDao.update(borrowIntent);
	}

	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 end

	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
	@Override
	public List<BorrowDetailType> getBorrowDetailTypeListByPid(long pid) {
		BorrowDetailType ab = borrowDetailTypeDao.find(pid);
		return borrowDetailTypeDao.getListByPid(ab.getNid());
	}

	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 end

	// v1.8.0.3_u3 TGPROJECT-335 wuing 2014-06-17 start
	@Override
	public List<BorrowDetail> getBorrowDetailListByBorrowId(long borrowId) {
		SearchParam param = new SearchParam();
		param.addParam("borrow.id", borrowId);
		param.addOrder("typeName.id");
		List<BorrowDetail> borrowDetailList = borrowDetailDao.findByCriteria(param);
		return borrowDetailList;
	}

	// v1.8.0.3_u3 TGPROJECT-335 wujing 2014-06-17 end

	// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-19 start
	@Override
	public void updateBorrowAndProperty(Borrow borrow, BorrowProperty property) {
		borrowDao.update(borrow);
		borrowPropertyDao.update(property);

	}

	// v1.8.0.3_u3 XINHEHANG-66 wujing 2014-06-19 end
	// v1.8.0.4_u1 TGPROJECT-379 wsl 2014-08-01 start 信合行逾期罚息修改功能
	@Override
	public void updateBorrowRepayment(BorrowRepayment repaymnet) {
		borrowRepaymentDao.update(repaymnet);
	}

	// v1.8.0.4_u1 TGPROJECT-379 wsl 2014-08-01 end
	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start
	@Override
	public void quzrtzAwardForFridenBorrow() {
		double tenderRewardApr = Global.getDouble("tender_reward_apr"); // 投标奖励比例
		logger.info("借款还款成功后，若投资用户有推荐人，添加发放推荐人奖励记录(渝都贷),奖励比例：" + tenderRewardApr);
		if (tenderRewardApr > 0) { // 判断投标奖励比例是否大于0，在不大于0的情况下，代表没投标奖励
			List<BorrowRepayment> repayList = borrowRepaymentDao.getRepayListForRewardStatus();
			List<TenderReward> tenderRewardList = new ArrayList<TenderReward>();
			List<BorrowRepayment> giveRepayList = new ArrayList<BorrowRepayment>();
			for (BorrowRepayment br : repayList) {
				logger.info("进入奖励,repaymentid" + br.getId());
				List<BorrowTender> tenderList = borrowTenderDao.getBorrowTenderListByborrow(br.getBorrow().getId());
				for (BorrowTender borrowTender : tenderList) {
					logger.info("进入奖励,tenderId:" + borrowTender.getId());
					User tenderUser = borrowTender.getUser(); // 投资人
					if (null != borrowTender.getUser().getInviteUser()) {
						User inviteUser = borrowTender.getUser().getInviteUser().getInviteUser(); // 获取推荐人
						TenderReward reward = new TenderReward();
						reward.setTenderMoney(borrowTender.getAccount());
						reward.setBorrow(new Borrow(borrowTender.getBorrow().getId()));
						reward.setRewardUser(new User(inviteUser.getUserId()));
						reward.setTenderUser(new User(tenderUser.getUserId()));
						double rewardMoney = borrowTender.getAccount() * 30 * tenderRewardApr / 365;
						reward.setRewardMoney(rewardMoney);
						reward.setStatus(0);
						reward.setAddTime(new Date());
						tenderRewardList.add(reward);
						br.setRewardStatus(1);
						giveRepayList.add(br);
					}
				}
			}
			if (tenderRewardList.size() > 0) {
				logger.info("size" + tenderRewardList.size());
				tenderRewardDao.save(tenderRewardList);
				borrowRepaymentDao.update(giveRepayList);
			}
		}
	}

	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 end

	// 1.8.0.4_u3 TGPROJECT qinjun 2014-06-25 start
	@Override
	public void updateBorrowStartDate(long borrowId, Date startDate) {
		borrowDao.updateBorrowStartDate(borrowId, startDate);
	}

	// 1.8.0.4_u3 TGPROJECT qinjun 2014-06-25 start

	public List getBorrowByStatus(int borrowStatus) {
		return borrowDao.borrowByStatus(borrowStatus);
	}

	public double getBorrowSum() {
		return borrowDao.getBorrowSum();
	}

	public double getBorrowSumInterest() {
		return borrowDao.getBorrowSumInterest();
	}

	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 start
	@Override
	public void noticeBorrowerRepay() {
		List<BorrowRepayment> list = borrowRepaymentDao.notRepayRepaymentList();
		for (BorrowRepayment repayment : list) {
			String[] noticeDays = Global.getString("notice_repay_day").split(",");
			long repayTime = repayment.getRepaymentTime().getTime() / 86400000;
			long nowTime = System.currentTimeMillis() / 86400000;
			long repayDay = nowTime - repayTime;
			for (int i = 0; i < noticeDays.length; i++) {
				String noticeDay = noticeDays[i];
				if (noticeDay.equals(repayDay + "")) {
					MsgReq req = new MsgReq();
					req.setSender(new User(Constant.ADMIN_ID));
					req.setReceiver(repayment.getBorrow().getUser());
					req.setMsgOperate(this.msgService.getMsgOperate(27));
					req.setBorrowname(repayment.getBorrow().getName());
					req.setAccount("" + repayment.getBorrow().getAccount());
					req.setApr("" + repayment.getBorrow().getApr());
					req.setMonthapr(NumberUtils.format2Str(repayment.getBorrow().getApr() / 12));
					req.setRepaymentTime(DateUtils.dateStr2(new Date()));
					try {
						DisruptorUtils.sendMsg(req);
					} catch (Exception e) {
						e.printStackTrace();
						logger.info("发送短信失败：" + repayment.getId());
					}
				}
			}
		}
	}

	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 end

	@Override
	public void batchSave(List<BorrowIncomeLadder> list) {
		borrowIncomeLadderDao.save(list);
	}

	@Override
	public void doIpsAddBorrow(BorrowParam param) {
		long borrowId = param.getId();
		String bNo = param.getTenderNo();
		Borrow borrow = borrowDao.find(borrowId);
		if (null == borrow || StringUtils.isBlank(bNo)) {
			throw new BussinessException("发标处理异常！");
		}
		if (StringUtils.isBlank(borrow.getIpsBorrowNo()) && borrow.getStatus() == 1) {
			String recode = param.getResultCode();
			if ("MG02500F".equals(recode)) {
				borrow.setIpsBorrowNo(bNo);
				borrowDao.update(borrow);
			} else {
				logger.info("发标，环迅创建标交易失败！返回码：" + recode);
				borrow.setStatus(-1); // 环迅创建标交易异常，更改表标的状态
				borrowDao.update(borrow);
			}
		} else {
			logger.info("环迅发标处理：" + borrowId + "，标号为：" + bNo);
		}
	}

	@Override
	public void updatePayStatus(IpsPay ipspay) {
		String result = ipspay.getResultMsg();
		String ordId = ipspay.getResultMsg();
		String reMsg = ipspay.getResultMsg();
		String code = ipspay.getReturnCode();
		IpsPay ips = ipsPayDao.getIpsPayByOrdId(ordId);
		if (null == ips) {
			logger.info("订单:" + ordId + ",异常，请查找原因！！！");
			return;
		}
		if ("MG00008".equals(result)) { // 处理中
			ipsPayDao.updateIpsStatus("3", ordId, code, reMsg);
		} else if ("MG00000F".equals(result)) { // 处理成功
			ipsPayDao.updateIpsStatus("1", ordId, code, reMsg);
		} else {
			ipsPayDao.updateIpsStatus("2", ordId, code, reMsg);
		}
	}

	@Override
	public IpsRepaymentNewTrade doIpsRepayment(long repayId) {
		BorrowRepayment repayment = borrowRepaymentDao.find(repayId);
		if (repayment.getStatus() == 1 && repayment.getWebstatus() == 1) {
			logger.info("此笔借款已经全部还款！！");
			throw new BussinessException("此笔借款已经全部还款！！");
		}
		// 环迅转出方一次最大300 限制，这里一次只还200个
		List<BorrowCollection> conList = borrowCollectionDao.getRepayCollection(repayment.getBorrow().getId(), repayment.getPeriod());
		IpsRepaymentNewTrade ipsRepayment = new IpsRepaymentNewTrade();
		Map<String, Double> map = IpsHelper.ipsRepayment(repayment, conList, ipsRepayment);
		// 获取校验借款人账户信息
		User user = repayment.getBorrow().getUser();
		Account account = accountDao.getAcountByUser(user);
		if (account.getUseMoney() < NumberUtils.getDouble(ipsRepayment.getOutAmt())) {
			throw new BussinessException("你的可用金额小于本次还款应还总额！");
		}
		IpsRepayDetail ipsDetail = new IpsRepayDetail();
		ipsDetail.setCollectionIds(ipsRepayment.getMemo2());
		ipsDetail.setRepayId(repayId);
		ipsDetail.setOrdId(ipsRepayment.getMerBillNo());
		ipsDetail.setCapital(map.get("copitalSum"));
		ipsDetail.setInterest(map.get("interestSum"));
		ipsDetail.setLateFee(map.get("lateSum"));
		ipsRepayDetailDao.save(ipsDetail);
		return ipsRepayment;
	}

	@Override
	public double countTenderRepayment(long borrow_id) {
		return borrowTenderDao.countTenderRepayment(borrow_id);
	}

	@Override
	public void doEndBorrow(Borrow borrow) {

		if (borrow.getType() == Constant.TYPE_CREDIT) {
			double amount = borrow.getViewMoney() - borrow.getAccount();
			UserAmount ua = userAmountDao.findByProperty("user", new User(borrow.getUser().getUserId())).get(0);
			if (amount > ua.getCreditUse()) { // 信用额度判断，发标已超过可用信用额度，不能继续借款
				throw new BussinessException("发标已超过可用信用额度，不能继续借款");
			}
			userAmountDao.updateCreditAmount(0, amount, -amount, borrow.getUser().getUserId());
			ua = userAmountDao.findByProperty("user", new User(borrow.getUser().getUserId())).get(0);
			userAmountLogDao.save(fillUserAmountLog(ua, "endborrow", amount, " 截标，解冻截标信用额度：" + amount, ""));
		}
		borrowDao.update(borrow);

	}

	@Override
	public List<Borrow> getBorrowListByUserId(long userId, int type) {
		return borrowDao.getBorrowListByUserId(userId, type);
	}

	@Override
	public double hasTenderTotalPerBorrowByUserid(long bid, long userId) {
		return borrowTenderDao.hasTenderTotalPerBorrowByUserid(bid, userId);
	}

}
