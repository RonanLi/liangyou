package com.liangyou.service.impl;

/**
 * 此service   没有事物 只是api处理工具类
 * @author zxc
 *
 */
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liangyou.api.chinapnr.CardCashOut;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmRegister;
import com.liangyou.api.moneymoremore.MmmToLoanFastPay;
import com.liangyou.api.moneymoremore.MmmToLoanTransferAudit;
import com.liangyou.context.ChinaPnrType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.MmmType;
import com.liangyou.context.YjfType;
import com.liangyou.dao.AccountBankDao;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.DrawBankDao;
import com.liangyou.dao.DrawBankMmmDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.YjfDao;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountWebDeduct;
import com.liangyou.domain.AreaMmm;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.DrawBankMmm;
import com.liangyou.domain.GlodTransfer;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.ApiPayParamModel;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.WebPayModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

@Service(value = "apiService")
@Transactional
public class ApiServiceImpl extends BaseServiceImpl implements ApiService {

	@Autowired
	private YjfDao yjfDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AccountBankDao accountBankDao;
	@Autowired
	private DrawBankDao drawBankDao;
	@Autowired
	private DrawBankMmmDao drawBankMmmDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private AccountRechargeDao accountRechargeDao;
	Logger logger = Logger.getLogger(ApiServiceImpl.class);

	// 获取第三方接口的类型
	private int getApiType() {
		return Global.getInt("api_code");// 接口类型 1：汇付，2：易极付，3:双乾...
	}

	@Override
	public SearchParam getdrawBankParam() {
		SearchParam drawBankParam = new SearchParam();
		switch (getApiType()) {
		case 3:// 双乾支付接口
				// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start
				// drawBankParam.addParam("state", "1");
				// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end
		default:
			break;
		}
		return drawBankParam;
	}

	@Override
	public void verifyVipSuccess(UserCache cache, double vipFee, List<Object> taskList) {
		switch (getApiType()) {
		case 3:// 双乾接口
			MmmPay mmmPay = new MmmPay("2", "1", "1", cache.getUser().getUserId(), cache.getUser().getApiId(), 1, Global.getValue("plat_form_mmm"), vipFee, MmmType.MMM_PAY_USER, "vip_fee", MmmType.VERIFYVIPSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	@Override
	public AccountBank addAccountBank(Object object) {
		AccountBank ab = null;
		switch (getApiType()) {
		case 3:// 双乾接口
			ab = (AccountBank) object;
			ab.setStatus(1);
			break;
		default:
			break;
		}
		logger.info("绑卡addAccountBank" + "方法执行结束" + ab.getStatus());
		return ab;
	}

	/**
	 * 投标,冻结资金接口
	 */
	@Override
	public void addTenderFreezeMoney(BorrowTender borrowTender, BorrowParam param, String apiMethodType, List<Object> taskList) {
		boolean isDo = apiMethodType.contains("" + getApiType());// 调用是否执行。
		switch (getApiType()) {
		case 3:
			if (!isDo)
				return;
			MmmPay mmmPay = new MmmPay("2", "1", "", borrowTender.getUser().getUserId(), borrowTender.getUser().getApiId(), borrowTender.getBorrow().getUser().getUserId(), borrowTender.getBorrow().getUser().getApiId(), borrowTender.getAccount(), MmmType.MMM_PAY_USER, borrowTender.getBorrow().getId() + "", MmmType.ADDTENDER, new Date());
			mmmPay.setTenderId(borrowTender.getId() + "");
			mmmPay.setFullAmount(borrowTender.getBorrow().getAccount());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}

	}

	/**
	 * @param fee,
	 *            转让费用
	 * @param model，债权转让标
	 * @param tender,当前投资
	 * @param oldTender,原投资
	 * @param taskList,任务list
	 *            汇付债权转让，接口 * 这里只有汇付的接口，其他的接口暂时不使用,对应字段说明：ChinaPnrPayModel, ordamt，承接金额 ChinaPnrPayModel中fee里边封装(CreditDealAmt实际转让金额\Fee转让手续费\PrinAmt已还本金\),以逗号隔开
	 */
	@Override
	public void fullSuccessCreditAssign(double fee, Borrow model, BorrowTender tender, BorrowTender oldTender, Borrow oldBorrow, List<Object> taskList) {
		// 封装请求参数
		User tenderUser = tender.getUser();// 当前投资人
		User borrowUser = model.getUser();// 当前转让人

		double ordamt = NumberUtils.format2(tender.getWaitAccount());// 要转让的待收本金
		long user_id = tenderUser.getUserId();// 投资人userId
		String payuser_id = borrowUser.getUserId() + "";// 发布债权转让人的userId.
		String operateTime = DateUtils.dateStr4(new Date());
		String addTime = DateUtils.dateStr4(new Date());
		ChinaPnrPayModel assignModel = new ChinaPnrPayModel(ChinaPnrType.CREDITASSIGN, ordamt, user_id, payuser_id, "0", operateTime, addTime, ChinaPnrType.CREDITASSIGN_VERIFY);

		String ordId = OrderNoUtils.getInstance().getSerialNumber();
		String ordDate = DateUtils.newdateStr2(new Date());
		assignModel.setOrdId(ordId);
		assignModel.setOrddate(ordDate);
		assignModel.setBorrowId(model.getId() + "");
		assignModel.setTenderId(tender.getId() + "");
		assignModel.setPayuserId(borrowUser.getApiUsercustId());// 收款人
		assignModel.setUsrCustId(tenderUser.getApiUsercustId());// 付款人
		assignModel.setSubordId(oldTender.getSubOrdId());// 原投资订单号
		assignModel.setSuborddate(oldTender.getSubOrdDate());// 原投资订单日期

		assignModel.setFee(model.getAccount() + "," + NumberUtils.format2(fee) + "," + oldTender.getRepaymentYescapital() + "," + oldBorrow.getUser().getApiUsercustId());

		// 添加任务
		taskList.add(assignModel);

	}

	/**
	 * 放款，放款接口
	 */
	@Override
	public void FullSuccessLoanMoney(String apiNo, Borrow model, BorrowTender tender, User borrowUser, User tenderUser, List<Object> taskList, String apiMethodType) {
		boolean isDo = apiMethodType.contains("" + getApiType());// 调用是否执行。
		model = borrowDao.find(model.getId());
		switch (getApiType()) {
		case 3:// 处理双乾接口
			if (!isDo) {
				return;
			}
			List<BorrowTender> list = model.getBorrowTenders();
			for (BorrowTender t : list) {
				User t_user = t.getUser();
				double account = t.getAccount();
				if (t.getExperienceMoney() != null && t.getExperienceMoney().getInterestUseStatus() == 1 && !StringUtils.isBlank(t.getEmSubOrdId()) && !StringUtils.isBlank(t.getEmTrxId())) {
					account = t.getAccount() - t.getExperienceMoney().getExperienceInterest();
					MmmPay mmmPay2 = new MmmPay("", "", "1", 1, Global.getValue("plat_form_mmm"), borrowUser.getUserId(), borrowUser.getApiId(), t.getExperienceMoney().getExperienceInterest(), MmmType.MMM_VERIFY_BORROW, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
					mmmPay2.setOrderNo(t.getEmTrxId());
					mmmPay2.setFullAmount(model.getAccount());
					taskList.add(mmmPay2);
				}
				MmmPay mmmPay = new MmmPay("", "", "1", t_user.getUserId(), t_user.getApiId(), borrowUser.getUserId(), borrowUser.getApiId(), account, MmmType.MMM_VERIFY_BORROW, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
				mmmPay.setOrderNo(t.getTrxId());
				mmmPay.setFullAmount(model.getAccount());
				taskList.add(mmmPay);
			}
			break;
		default:
			break;
		}
	}

	private String getApiTenderNoStr(Borrow model) {
		StringBuffer sb = new StringBuffer();
		List<BorrowTender> list = model.getBorrowTenders();
		for (int i = 0; i < list.size(); i++) {
			BorrowTender borrowTender = list.get(i);
			sb.append(borrowTender.getTrxId());
			if (i < list.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * 放款，分发奖励 User borrowUser:借款用户 User tenderUser：投标用户
	 */
	@Override
	public void FullSuccessAward(Borrow model, BorrowTender tender, User borrowUser, User tenderUser, List<Object> taskList, double awardValue) {
		switch (getApiType()) {
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrowUser.getUserId(), borrowUser.getApiId(), tenderUser.getUserId(), tenderUser.getApiId(), awardValue, MmmType.MMM_PAY_USER, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	// TGPROJECT-411 wsl 2014-09-04 start
	@Override
	public void FullSuccessAdminAward(Borrow model, BorrowTender tender, User borrowUser, User tenderUser, List<Object> taskList, double awardValue) {
		switch (getApiType()) {
		case 1:// 汇付
				// 奖励汇付才处理
			logger.info("用户投标" + model.getName() + "奖励" + awardValue + ",用户");
			ChinaPnrPayModel cppModel = new ChinaPnrPayModel(ChinaPnrType.TRANSFER, awardValue, 1, String.valueOf(tenderUser.getUserId()), "0", null, DateUtils.getNowTimeStr(), ChinaPnrType.AUTOREWARD);
			cppModel.setPayusrCustId(tenderUser.getApiUsercustId());
			cppModel.setUsrCustId("");
			taskList.add(cppModel);// 从主账号
			break;

		case 2:// 易极付
			YjfPay yjfPayawArdValue = new YjfPay(model.getId() + "", Global.getValue("trade_name_award"), NumberUtils.format2(awardValue) + "", YjfType.AUTOVERIFYFULLSUCCESS, null, null, YjfType.TRADETRANSFER, null, tenderUser.getApiId(), null, Global.getValue("guarantee_money_accoount"));// 备用金账户转账
			taskList.add(yjfPayawArdValue);
			break;
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", Constant.ADMIN_ID, Global.getValue("plat_form_mmm"), tenderUser.getUserId(), tenderUser.getApiId(), awardValue, MmmType.MMM_PAY_USER, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	// TGPROJECT-411 wsl 2014-09-04 end
	/**
	 * 放款，收取手续费
	 */
	@Override
	public void FullSuccessDeductFee(List<Object> taskList, List<BorrowTender> tenderList, double borrowfee, Borrow borrow, String menageFee) {
		borrow = borrowDao.find(borrow.getId());
		switch (getApiType()) {
		case 1:// 汇付
			doBorrowFee(taskList, tenderList, borrowfee, borrow, menageFee);
			break;
		case 2:// 易极付
				// 转账给 参与账户 当做风险基金池
			YjfPay feepayChargeFee = new YjfPay(borrow.getId() + "", Global.getValue("trade_name_manage_fee"), NumberUtils.format2(borrowfee) + "", YjfType.AUTOVERIFYFULLSUCCESS, null, null, YjfType.TRADETRANSFER, null, Global.getValue("yjf_partnerId"), null, borrow.getUser().getApiId());
			taskList.add(feepayChargeFee);
			break;
		// v1.8.0.4 TGPROJECT-34 qj 2014-04-10 start
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), 1, Global.getValue("plat_form_mmm"), borrowfee, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		// v1.8.0.4 TGPROJECT-34 qj 2014-04-10 start
		default:
			break;
		}
	}

	/**
	 * 放款，风险备用金
	 */
	@Override
	public void FullSuccessRiskFee(List<Object> taskList, List<BorrowTender> tenderList, double riskFee, Borrow borrow, String menageFee) {
		switch (getApiType()) {
		case 1:// 汇付
			doBorrowFee(taskList, tenderList, riskFee, borrow, menageFee);
			break;
		case 2:// 易极付
				// 转账给 参与账户 当做风险基金池
			YjfPay feepayChargeFee = new YjfPay(borrow.getId() + "", Global.getValue("trade_name_guarantee"), NumberUtils.format2(riskFee) + "", YjfType.AUTOVERIFYFULLSUCCESS, null, null, YjfType.TRADETRANSFER, null, Global.getValue("guarantee_money_accoount"), null, borrow.getUser().getApiId());
			taskList.add(feepayChargeFee);
			break;
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), 1, Global.getValue("plat_form_mmm"), riskFee, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 还款，还<本金等>接口
	 */
	@Override
	public void repayLoanMoney(String apiId, double money, List<Object> taskList, Borrow borrow, BorrowCollection c, String apiMethodType) {
		boolean isDo = apiMethodType.contains(getApiType() + "");
		switch (getApiType()) {
		case 3:// 双乾
			if (!isDo)
				return;
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), c.getBorrowTender().getUser().getUserId(), c.getBorrowTender().getUser().getApiId(), money, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 还款，逾期罚息接口
	 */
	@Override
	public void payManageFee(String apiId, double money, List<Object> taskList, Borrow borrow) {
		switch (getApiType()) {
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), 1, Global.getValue("plat_form_mmm"), money, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.AUTOREPAY, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 还款，易极付利息管理费，汇付，还利息与扣除利息管理费。
	 */
	@Override
	public void repayBorrowFee(Borrow model, BorrowRepayment repay, User tenderUser, double borrowFee, double interest, List<Object> taskList, BorrowCollection c, String apiMethodType) {
		boolean isDo = apiMethodType.contains(getApiType() + "");
		User borrowUser = model.getUser();
		switch (getApiType()) {
		case 3:// 双乾
			if (!isDo)
				return;
			MmmPay mmmPay = null;
			if (borrowFee > 0) {
				mmmPay = new MmmPay("2", "2", "1", borrowUser.getUserId(), borrowUser.getApiId(), c.getBorrowTender().getUser().getUserId(), c.getBorrowTender().getUser().getApiId(), interest, MmmType.MMM_REPAY_INTEREST, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
				mmmPay.setSecondaryAmount(borrowFee);
				mmmPay.setSecondaryMmmId(Global.getValue("plat_form_mmm"));
			} else {
				mmmPay = new MmmPay("2", "2", "1", borrowUser.getUserId(), borrowUser.getApiId(), c.getBorrowTender().getUser().getUserId(), c.getBorrowTender().getUser().getApiId(), interest, MmmType.MMM_PAY_USER, model.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			}
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/***
	 * 流转标还款,利息管理费
	 */
	@Override
	public void flowRepayBorrowFee(Borrow model, BorrowRepayment repayment, List<Object> interestFeeList, User tenderUser, double borrow_fee, double repayments, BorrowTender tender, List<Object> taskList) {
		switch (getApiType()) {
		case 3:// 双乾
			if (borrow_fee > 0) {
				MmmPay mmmPay = new MmmPay("2", "2", "1", model.getUser().getUserId(), model.getUser().getApiId(), tender.getUser().getUserId(), tender.getUser().getApiId(), repayments, MmmType.MMM_REPAY_INTEREST, model.getId() + "", MmmType.FLOW_REPAY, new Date());
				mmmPay.setSecondaryAmount(borrow_fee);
				mmmPay.setSecondaryMmmId(Global.getValue("plat_form_mmm"));
				taskList.add(mmmPay);
			} else {
				MmmPay mmmPay = new MmmPay("2", "2", "1", model.getUser().getUserId(), model.getUser().getApiId(), tender.getUser().getUserId(), tender.getUser().getApiId(), repayments, MmmType.MMM_PAY_USER, model.getId() + "", MmmType.FLOW_REPAY, new Date());
				taskList.add(mmmPay);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 还款给网站，还款给网站接口
	 */
	@Override
	public void repayToWebSiteLoanMoney(BorrowRepayment repay, double money, Borrow borrow, List<Object> taskList) {
		borrow = borrowDao.find(borrow.getId());
		switch (getApiType()) {
		case 3:
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), 1, Global.getValue("plat_form_mmm"), money, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.WEBSITEREPAY, new Date());
			taskList.add(mmmPay);
		default:
			break;
		}
	}

	/**
	 * 汇付绑卡操作
	 * 
	 * @param cardCashOut
	 * @param ab
	 */
	private AccountBank ckeckBankHf(CardCashOut cardCashOut, AccountBank ab) {
		logger.info("进入绑卡业务处理方法");
		long userId = NumberUtils.getLong(cardCashOut.getMerPriv());
		User user = userDao.find(userId);
		if (user == null) {
			throw new BussinessException("用户绑定银行卡失败 ，user :" + user + " userId:" + userId);
		}
		String accountId = cardCashOut.getOpenAcctId();
		String bankId = cardCashOut.getOpenBankId();
		// 同一个账号不能绑定两次
		ab = accountBankDao.getAccountBankByCardNo(accountId, user);
		DrawBank db = drawBankDao.getDrawBankByBankCode(bankId);
		if (db == null) {
			throw new BussinessException("bankId:" + bankId + " accountId" + accountId + "  系统找不到指定的参数， userId:" + userId);
		}
		if (ab == null) {// 添加新的银行卡
			ab = new AccountBank();
			ab.setUser(user);
			ab.setAccount(accountId);
			ab.setBank(db);
			ab.setAddip("");
			ab.setAddtime(new Date());
			ab.setStatus(1);
		} else {
			throw new BussinessException("no:" + accountId + " 已经绑定过，此次绑定失败 ");
		}
		return ab;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start
	/**
	 * 双乾绑卡操作
	 * 
	 * @param mmm
	 * @param ab
	 */
	private AccountBank checkBankMmm(MmmToLoanFastPay mmm, AccountBank ab) {
		logger.info("进入三合一绑卡业务处理方法");
		String apiId = StringUtils.isNull(mmm.getMoneymoremoreId());
		User user = userDao.getUserByApiId(apiId);
		if (user == null) {
			throw new BussinessException("用户绑定银行卡失败 ，user :" + user + " userId:" + user.getUserId());
		}
		String accountId = mmm.getCardNo();
		String bankId = mmm.getBankCode();
		String branchBankName = mmm.getBranchBankName();
		long province = NumberUtils.getLong(mmm.getProvince());
		long city = NumberUtils.getLong(mmm.getCity());
		// 同个银行卡多次绑定的校验
		ab = accountBankDao.getAccountBankByCardNo(accountId, user);
		// 同一个账号不能绑定两次
		if (ab != null) {
			throw new BussinessException("该卡号银行卡已绑定，请勿重复绑定！", "/member/account/bank.html");
		}
		DrawBankMmm db = drawBankMmmDao.getDrawBankMmmByBankCode(bankId);
		if (db == null) {
			throw new BussinessException("bankId:" + bankId + " accountId" + accountId + "  系统找不到指定的参数， userId:" + user.getUserId());
		}
		if (ab == null) {// 添加新的银行卡
			ab = new AccountBank();
			ab.setUser(user);
			ab.setAccount(accountId);
			ab.setBankMmm(db);
			ab.setBranch(branchBankName);
			ab.setMmmprovince(new AreaMmm(province));
			ab.setMmmcity(new AreaMmm(city));
			ab.setStatus(1);
			ab.setAddtime(new Date());
			ab.setAddip("");
		} else {
			throw new BussinessException("no:" + accountId + " 已经绑定过，此次绑定失败 ");
		}
		return ab;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end
	/**
	 * 网站垫付，还款<本金、利息、逾期罚息>
	 */
	@Override
	public void webSitePayLoanMoney(Borrow borrow, List<Object> taskList, User tenderUser, double money) {
		switch (getApiType()) {
		case 1:// 汇付
			ChinaPnrPayModel cppModel = new ChinaPnrPayModel(ChinaPnrType.TRANSFER, money, 1, String.valueOf(tenderUser.getUserId()), "0", null, DateUtils.getNowTimeStr(), ChinaPnrType.WEBPAY);
			cppModel.setPayusrCustId(tenderUser.getApiUsercustId());
			cppModel.setUsrCustId("");
			taskList.add(cppModel);// 从主账号
			break;
		case 2:// 易极付
			YjfPay yjfPay = new YjfPay(borrow.getId() + "", null, NumberUtils.format2Str(money), YjfType.WEBSITEPAYFORLATEBORROW, null, null, YjfType.TRADEPAYPOOLREVERSE, null, tenderUser.getApiId(), null, Global.getValue("guarantee_money_accoount")); // 从备用金转账给投资人
			taskList.add(yjfPay);
			break;
		// v1.8.0.4_u1 TGPROJECT-228 qj 2014-05-05 start
		case 3:
			// v1.8.0.4_u4 TGPROJECT-364 qinjun 2014-07-01 start
			MmmPay mmmPay = new MmmPay("2", "2", "1", 1, Global.getValue("web_pay_account"), tenderUser.getUserId(), tenderUser.getApiId(), money, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.WEBSITE, new Date());
			// v1.8.0.4_u4 TGPROJECT-364 qinjun 2014-07-01 start
			taskList.add(mmmPay);
			break;
		// v1.8.0.4_u1 TGPROJECT-228 qj 2014-05-05 end
		default:
			break;
		}
	}

	// 帝业投资自动扣款功能 start
	@Override
	public void doMmmUserPayWeb(User user, double money, List<Object> taskList) {
		MmmPay mmmPay = new MmmPay("2", "2", "1", user.getUserId(), user.getApiId(), 1, Global.getValue("plat_form_mmm"), money, MmmType.MMM_PAY_USER, "", MmmType.WEBSITEREPAY, new Date());
		taskList.add(mmmPay);
	}

	// 帝业投资自动扣款功能 start
	/**
	 * 网站垫付，还款<本金、利息、逾期罚息>最后一步，易极付等接口划款过程。
	 */
	@Override
	public void webSitePayLoanMoneyEnd(Borrow borrow, ApiPayParamModel apm, BorrowRepayment br, List<Object> taskList, String apiMethodType) {
		boolean isDo = apiMethodType.contains(getApiType() + "");
		switch (getApiType()) {
		case 1:// 汇付
			break;
		case 2:// 易极付
			if (!isDo)
				return;
			YjfPay yjfPay = new YjfPay(borrow.getId() + "", null, NumberUtils.format2(apm.getTotalMoney()) + "", YjfType.WEBSITEPAYFORLATEBORROW, null, br.getPeriod() + "", YjfType.TRADEPAYPOOLREVERSE, null, apm.getTenderStr().substring(0, apm.getTenderStr().length() - 1), null, Global.getValue("guarantee_money_accoount")); // 从备用金转账给投资人
			taskList.add(yjfPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 管理费处理方法： 由于汇付没有直接从用户划款值商户的接口，所以在处理管理费时模拟一笔 还款操作，付款人为借款者，收款方为平台.扣费的方式为评分到每一个投资者的 投标订单里面去扣除 确保这笔资金是从借款人账户里面扣除的
	 */
	@Override
	public void doBorrowFee(List<Object> taskList, List<BorrowTender> tenderList, double borrowfee, Borrow borrow, String menageFee) {
		double total = 0;
		for (int i = 0; i < tenderList.size(); i++) {
			BorrowTender borrowTender = tenderList.get(i);
			double borrowAccount = borrow.getAccountYes(); // 获取标的总额
			double feeMoney = 0;
			if (i == tenderList.size() - 1) {
				feeMoney = NumberUtils.format2(NumberUtils.format2(borrowfee) - NumberUtils.format2(total)); // 获取最后一个人
			} else {
				feeMoney = NumberUtils.format2(borrowTender.getAccount() / borrowAccount * borrowfee); // 获取每个人
				// 判断当前用户的扣除金额加上累计扣除的金额是否超出所需要扣的管理费，如果超出，则只从当前用户身上扣除总管理费和累计扣除的差额费用
				if (NumberUtils.format2(total + feeMoney) > borrowfee) {
					feeMoney = NumberUtils.format2(NumberUtils.format2(borrowfee) - NumberUtils.format2(total));
				}
				total += feeMoney;
				total = NumberUtils.format2(total);
			}
			if (feeMoney > 0) {
				ChinaPnrPayModel cppFee = new ChinaPnrPayModel(ChinaPnrType.REPAYMENT, feeMoney, borrow.getUser().getUserId(), ChinaPnrType.ADMININ, "0", null, DateUtils.dateStr4(new Date()), ChinaPnrType.AUTOVERIFYFULLSUCCESS);
				cppFee.setOrdId(OrderNoUtils.getInstance().getSerialNumber());
				cppFee.setOrddate(DateUtils.newdateStr2(new Date()));
				cppFee.setBorrowId(borrow.getId() + "");
				cppFee.setUsrCustId(borrow.getUser().getApiUsercustId());
				cppFee.setSuborddate(borrowTender.getSubOrdDate());
				cppFee.setSubordId(borrowTender.getSubOrdId());
				cppFee.setFee("0.00");
				cppFee.setManageFee(menageFee);
				cppFee.setPayusrCustId("1");
				taskList.add(cppFee);
			}
		}

	}

	/**
	 * 流转标，投标放款。
	 */
	@Override
	public void flowBorrowLoan(BorrowTender borrowTender, BorrowParam param, User tenderUser, Borrow borrow, User borrowUser, List<Object> taskList) {
		switch (getApiType()) {
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "1", "1", tenderUser.getUserId(), tenderUser.getApiId(), borrowUser.getUserId(), borrowUser.getApiId(), borrowTender.getAccount(), MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.ADDFLOWTENDER, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 网站后台充值
	 */
	@Override
	public void webRecharge(AccountRecharge ar, List<Object> taskList) {
		switch (getApiType()) {
		case 1:
			ChinaPnrPayModel cppModel = new ChinaPnrPayModel(ChinaPnrType.TRANSFER, ar.getMoney(), 1, String.valueOf(ar.getUser().getUserId()), "0", null, DateUtils.getNowTimeStr(), ChinaPnrType.WEBPAY);
			cppModel.setPayusrCustId(ar.getUser().getApiUsercustId());
			cppModel.setUsrCustId("");
			taskList.add(cppModel);
			break;
		case 2:
			YjfPay yjfPay = new YjfPay(null, Global.getValue("trade_offline_recharge"), ar.getMoney() + "", YjfType.VERIFYRECHARGE, null, null, YjfType.TRADETRANSFER, null, ar.getUser().getApiId(), null, Global.getValue("yjf_partnerId"));
			taskList.add(yjfPay);
			break;
		case 3:// 2.直连（冻结资金）~1.投标~1.自动通过~付款方ID~付款方双乾账户~收款方ID~收款方双乾账户~发生的金额~接口操作类型（付款给平台）~标ID~操作类型~时间
			MmmPay mmmPay = new MmmPay("2", "1", "1", 1, Global.getValue("plat_form_mmm"), ar.getUser().getUserId(), ar.getUser().getApiId(), ar.getMoney(), MmmType.MMM_PAY_USER, "后台充值", MmmType.WEB_RECHARGE, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
	/**
	 * 网站划款
	 */
	@Override
	public void webTransfer(List<Object> taskList, User toUser, double money) {
		switch (getApiType()) {
		case 1:
			ChinaPnrPayModel cppModel = new ChinaPnrPayModel(ChinaPnrType.TRANSFER, money, 1, String.valueOf(toUser.getUserId() + ""), "0", null, DateUtils.getNowTimeStr(), ChinaPnrType.WEBPAY);
			cppModel.setPayusrCustId(toUser.getApiUsercustId());
			cppModel.setUsrCustId("");
			taskList.add(cppModel);
			break;
		case 2:
			YjfPay yjfPay = new YjfPay(null, Global.getValue("trade_offline_recharge"), money + "", YjfType.WEB_TRANSFER, null, null, YjfType.TRADETRANSFER, null, toUser.getApiId(), null, Global.getValue("yjf_partnerId"));
			taskList.add(yjfPay);
			break;
		case 3:
			MmmPay mmmPay = new MmmPay("2", "1", "1", 1, Global.getValue("plat_form_mmm"), toUser.getUserId(), toUser.getApiId(), money, MmmType.MMM_PAY_USER, "发放奖励", MmmType.REWARD_PAY, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}
	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 end

	@Override
	public void glodTransfer(GlodTransfer gt, List<Object> taskList) {
		switch (getApiType()) {
		case 1:
			ChinaPnrPayModel cppModel = new ChinaPnrPayModel(ChinaPnrType.TRANSFER, gt.getMoney(), 1, gt.getAccount(), "0", null, DateUtils.getNowTimeStr(), ChinaPnrType.WEBPAY);
			cppModel.setPayuserId(gt.getPayAccount());
			cppModel.setUsrCustId(gt.getAccount());
			cppModel.setPayusrCustId(Global.getValue("chinapnr_merid"));
			taskList.add(cppModel);
			break;
		case 2:
			break;
		default:
			break;
		}

	}

	@Override
	public void webDeduct(AccountWebDeduct awd, List<Object> taskList) {
		switch (getApiType()) {
		case 1:
			break;
		case 2:
			YjfPay yjfPay = new YjfPay(null, Global.getValue("trade_name_web_deduct"), awd.getMoney() + "", YjfType.CASHBACK, null, null, YjfType.TRADETRANSFER, null, Global.getValue(awd.getDedcutAccount()), null, awd.getUser().getApiId());
			taskList.add(yjfPay);
			break;
		default:
			break;
		}

	}

	/**
	 * 撤标过程
	 */
	@Override
	public void faillBorrow(List<Object> taskList, Borrow model, String apiMethodType) {
		boolean isDo = apiMethodType.contains(getApiType() + "");
		switch (getApiType()) {
		case 3:// 双乾接口
			if (!isDo)
				return;
			MmmPay mmmPay = new MmmPay();
			mmmPay.setOrderNo(getApiTenderNoStr(model));
			mmmPay.setNeedAudit("2");
			mmmPay.setAddtime(new Date());
			mmmPay.setOperateType(MmmType.AUTOCANCEL);
			mmmPay.setType(MmmType.MMM_VERIFY_BORROW);
			mmmPay.setBorrowId(model.getId() + "");
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	// 此处只处理第三方业务，对于本地
	@Override
	public double verifyCash(AccountCash cash, List<Object> taskList) {
		String yjfReflag = "";
		boolean reflag = false;
		double account = 0;
		switch (getApiType()) {
		case 3: // 双钱取现审核
			String auditType = "";
			if (cash.getStatus() == 1) {
				auditType = "5";
			} else if (cash.getStatus() == 3) { // 审核拒绝
				auditType = "6";
			}
			reflag = mmmCashVerify(cash.getLoanNo(), auditType);
			account = cash.getTotal();
			if (!reflag) {
				cash.setStatus(2);
			}
			break;
		default:
			break;
		}
		if (reflag) {
			cash.setStatus(1);
		} else {
			cash.setStatus(2);
		}
		return account;
	}

	/**
	 * 双钱取现审核
	 * 
	 * @param loanNoList
	 * @param auditType
	 * @return
	 */
	public synchronized boolean mmmCashVerify(String loanNoList, String auditType) {
		boolean flage = false;
		MmmToLoanTransferAudit mmt = MmmHelper.toLoanTransferAudit(loanNoList, auditType);
		if (mmt.getResultCode().equals("88")) {
			flage = true;
		}
		return flage;

	}

	/**
	 * 是否需要注册第三方
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public Object doRegisterApi(User user, boolean isWap) {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾接口
			MmmRegister mmmReg = MmmHelper.mmmRegister(user, isWap);
			return mmmReg;
		default:
			break;
		}
		return null;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 start
	/**
	 * 钱多多三合一接口实名认证
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public Object doLoanFastPayApi(User user) {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾接口
			MmmToLoanFastPay mmmReg = MmmHelper.mmmToLoanFastPay(user, "1");
			return mmmReg;
		default:
			break;
		}
		return null;
	}

	@Override
	public Map<String, String> findApiAccount(User user) throws Exception {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		Map<String, String> map = new HashMap<String, String>();
		switch (apiType) {
		case 3:// 双乾接口
			map = mmmQalanceQuery(user);
			break;
		default:
			break;
		}
		return map;
	}

	@Override
	public List findBatchApiAccount(List<User> userList) throws Exception {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		List<Map<Object, Object>> list = new ArrayList();
		switch (apiType) {
		case 3:// 双乾接口
			list = mmmQalanceQuery(userList);
			break;
		default:
			break;
		}
		return list;
	}

	@Override
	public void checkApiLoan(User user) {
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾接口
			if (user.getApiLoanAuthorize() == 0) {
				throw new BussinessException("请先授权再进行该操作！", "/member/loanAuthorize.html", "点击进行授权");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void creditForMoney(User user, double money, List<Object> taskList) {
		switch (getApiType()) {
		case 3:
			MmmPay mmmPay = new MmmPay("2", "1", "1", 1, Global.getValue("plat_form_mmm"), user.getUserId(), user.getApiId(), money, MmmType.MMM_PAY_USER, "积分兑换现金", MmmType.GOODS_EXPENSE_MONEY, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	@Override
	public void doWebPayMoney(WebPayModel payModel, List<Object> taskList) {
		switch (getApiType()) {
		case 3:
			MmmPay mmmPay = new MmmPay("2", "1", "1", 1, Global.getValue("plat_form_mmm"), payModel.getPayUser().getUserId(), payModel.getPayUser().getApiId(), payModel.getMoney(), MmmType.MMM_PAY_USER, "发放奖励", MmmType.REWARD_PAY, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 放款，收取手续费
	 */
	@Override
	public void borrowertTransferToUser(List<Object> taskList, double borrowfee, Borrow borrow, User toUser) {
		borrow = borrowDao.find(borrow.getId());
		switch (getApiType()) {
		case 3:// 双乾
			MmmPay mmmPay = new MmmPay("2", "2", "1", borrow.getUser().getUserId(), borrow.getUser().getApiId(), toUser.getUserId(), toUser.getApiId(), borrowfee, MmmType.MMM_PAY_USER, borrow.getId() + "", MmmType.AUTOVERIFYFULLSUCCESS, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/*
	 * add by lijing 2016-11-28 网站后台收回转账
	 */
	@Override
	public void webBackRecharge(AccountRecharge ar, List<Object> taskList, User rechargeUser) {
		switch (getApiType()) {
		case 3:// 2.直连（冻结资金）~1.投标~1.自动通过~付款方ID~付款方双乾账户~收款方ID~收款方双乾账户~发生的金额~接口操作类型（付款给平台）~标ID~操作类型~时间
			MmmPay mmmPay = new MmmPay("2", "1", "1", rechargeUser.getUserId(), rechargeUser.getApiId(), ar.getUser().getUserId(), Global.getValue("plat_form_mmm"), ar.getMoney(), MmmType.MMM_PAY_USER, "后台收回转账", MmmType.WEB_BACK_RECHARGE, new Date());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}

	}

	// add by lijing 后台批量充值 加入审核
	@Override
	public void webBatchRecharge(AccountRecharge ar, List<Object> taskList) {
		logger.info("初始化转账信息mmmpay:");
		switch (getApiType()) {
		case 3:// 2.直连（冻结资金）~1.投标(2还款)~1.自动通过(""需要审核)~付款方ID~付款方双乾账户~收款方ID~收款方双乾账户~发生的金额~接口操作类型（付款给平台）~标ID(充值id)~操作类型~时间
			MmmPay mmmPay = new MmmPay("2", "2", "", Constant.ADMIN_ID, Global.getValue("plat_transfer_account"), ar.getUser().getUserId(), ar.getUser().getApiId(), ar.getMoney(), MmmType.MMM_PAY_USER, ar.getId() + "", MmmType.WEB_RECHARGE, new Date());
			mmmPay.setOrderNo(ar.getTradeNo());
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	/*
	 * add by lijing 后台批量充值 处理
	 */
	@Override
	public void batchRechargeLoanMoney(String apiNo, AccountRecharge accountRecharge, User payer, User payee, List<Object> taskList, String apiMethodType) {
		boolean isDo = apiMethodType.contains("" + getApiType());// 调用是否执行。
		accountRecharge = accountRechargeDao.find(accountRecharge.getId());
		switch (getApiType()) {
		case 3:// 处理双乾接口
			if (!isDo) {
				return;
			}
			MmmPay mmmPay = new MmmPay("", "", "1", 1, Global.getValue("plat_form_mmm"), accountRecharge.getUser().getUserId(), accountRecharge.getUser().getApiId(), accountRecharge.getMoney(), MmmType.MMM_VERIFY_BATCHREACHARGE, "后台批量充值审核", MmmType.MMM_VERIFY_BATCHREACHARGE, new Date());
			mmmPay.setOrderNo(apiNo);// 双乾流水号
			logger.info("复审的流水号:" + apiNo);
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}

	// add by lijing 后台批量审核不通过
	@Override
	public void failBatchRecharge(List<Object> taskList, AccountRecharge ar, String apiMethodType) {
		boolean isDo = apiMethodType.contains(getApiType() + "");
		switch (getApiType()) {
		case 3:// 双乾接口
			if (!isDo)
				return;
			MmmPay mmmPay = new MmmPay();
			mmmPay.setOrderNo(ar.getSerialNo());
			mmmPay.setNeedAudit("2");
			mmmPay.setAddtime(new Date());
			mmmPay.setOperateType(MmmType.AUTOCANCEL);
			mmmPay.setType(MmmType.MMM_VERIFY_BATCHREACHARGE);
			taskList.add(mmmPay);
			break;
		default:
			break;
		}
	}
}
