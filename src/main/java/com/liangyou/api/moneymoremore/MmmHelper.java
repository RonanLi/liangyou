package com.liangyou.api.moneymoremore;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.MmmType;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.User;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

/**
 * RSA 签名、验证、加密、解密帮助类
 * 
 * @author Qinjun
 * 
 */
public class MmmHelper {

	private static final Logger logger = Logger.getLogger(MmmHelper.class);

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	private static Object doSubmit(MmmModel mod) {
		Object object = null;
		try {
			object = mod.submit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
		return object;
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop
	/**
	 * 钱多多开户
	 * 
	 * @param user
	 * @return
	 */
	public static MmmRegister mmmRegister(User user, boolean isWap) {
		MmmRegister mmm = new MmmRegister(1);
		if (user.getUserType() != null && user.getUserType().getTypeId() == 28) {
			mmm.setAccountType("1");
		}
		// wap端用户，页面回掉地址需更改
		if (isWap) {
			mmm.setReturnURL(Global.getString("weburl") + "/public/mmm/registerReturn.html");
		}
		mmm.setEmail(user.getEmail());
		mmm.setIdentificationNo(user.getCardId());
		mmm.setMobile(user.getPhone());
		mmm.setRealName(user.getRealname());
		mmm.setLoanPlatformAccount(user.getUsername());
		mmm.setSubmitUrl(mmm.getSubmitUrl() + "loan/toloanregisterbind.action");
		mmm.sign();
		return mmm;
	}

	/**
	 * 钱多多授权还款
	 *
	 *
	 **/
	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 start
	public static MmmLoanAuthorize mmmLoanAuthorize(User user, String borrowId) {
		// MmmLoanAuthorize mmm=new MmmLoanAuthorize(0);
		MmmLoanAuthorize mmm = new MmmLoanAuthorize(7);// 授权还款接口
		mmm.setSubmitUrl(mmm.getSubmitUrl() + "loan/toloanauthorize.action");
		mmm.setMoneymoremoreId(user.getApiId());
		mmm.setAuthorizeTypeOpen("1,2,3");// 开启授权类型 :1.投标 2.还款 3.二次分配审核 将所有数字用英文逗号(,)连成一个字符串
		mmm.setAuthorizeTypeClose("");// 关闭授权类型 (同开启)
		// add by gy 2016年11月29日17:24:56
		// 增加跳转授权页面的标的ID
		if (!StringUtils.isBlank(borrowId)) {
			mmm.setRemark1(borrowId);
		}
		// end
		mmm.sign();
		return mmm;
	}

	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 stop
	/**
	 * 钱多多充值
	 * 
	 * @param user
	 * @param money
	 * @return
	 */
	public static MmmRecharge mmmRecharge(User user, String money, String type) {
		MmmRecharge mmmRecharge = new MmmRecharge(2);
		mmmRecharge.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		mmmRecharge.setAmount(money);
		mmmRecharge.setRechargeMoneymoremore(user.getApiId());// 要充值的账号的钱多多标识
		mmmRecharge.setSubmitUrl(mmmRecharge.getSubmitUrl() + "loan/toloanrecharge.action");// 页面提交地址

		if (type.equals("1") && user.getUserType().getTypeId() == 28) {
			mmmRecharge.setRechargeType("4");
			mmmRecharge.setFeeType("2");
		}
		if (type.equals("8")) {
			mmmRecharge.setRechargeType("3");
			mmmRecharge.setFeeType("2");
		}
		if (type.equals("9")) {
			mmmRecharge.setRechargeType("2");
			mmmRecharge.setFeeType("2");
		}
		mmmRecharge.sign();
		logger.info("充值参数================" + mmmRecharge.toString());
		return mmmRecharge;
	}

	/**
	 * 钱多多提现
	 * 
	 * @param cash
	 * @param feePercent
	 * @return
	 */
	public static MmmCash mmmCash(AccountCash cash, String feePercent) {
		// MmmCash mmmCash = new MmmCash(0);
		MmmCash mmmCash = new MmmCash(8);
		mmmCash.setWithdrawMoneymoremore(cash.getUser().getApiId());// 提现人乾多多标识
		mmmCash.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());// 平台的提现订单号
		mmmCash.setAmount(cash.getTotal() + "");// 金额
		mmmCash.setSubmitUrl(mmmCash.getSubmitUrl() + "loan/toloanwithdraws.action");// 接口地址
		mmmCash.setProvince(cash.getAccountBank().getMmmprovince().getNid() + "");// 开户行省份
		mmmCash.setCity(cash.getAccountBank().getMmmcity().getNid() + "");// 开户行城市
		mmmCash.setCardNo(cash.getAccountBank().getAccount()); // 银行卡号
		mmmCash.setBankCode(cash.getAccountBank().getBankMmm().getBankCode());// 银行代码
		mmmCash.setFeePercent(feePercent);// 平台承担的手续费比例
		mmmCash.sign();
		mmmCash.setCardNo(mmmCash.encryptData(cash.getAccountBank().getAccount()));// 银行卡号加密使用
		logger.info("提现参数：" + mmmCash.toString());
		return mmmCash;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 start
	/**
	 * 钱多多认证、提现银行卡绑定、代扣授权三合一接口
	 * 
	 * @param user
	 * @param cash
	 * @return
	 */
	public static MmmToLoanFastPay mmmToLoanFastPay(User user, String action) {
		// MmmToLoanFastPay mmmFastpay = new MmmToLoanFastPay(0);
		MmmToLoanFastPay mmmFastpay = new MmmToLoanFastPay(9);
		mmmFastpay.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		mmmFastpay.setMoneymoremoreId(user.getApiId());
		mmmFastpay.setAction(action);
		mmmFastpay.setSubmitUrl(mmmFastpay.getSubmitUrl() + "loan/toloanfastpay.action");
		mmmFastpay.sign();
		logger.info("三合一接口参数：" + mmmFastpay.toString());
		return mmmFastpay;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 end
	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	/**
	 * 钱多多查询余额
	 * 
	 * @param cash
	 * @param feePercent
	 * @return
	 * @throws Exception
	 */
	public static String balanceQuery(User user) throws Exception {
		MmmBalanceQuery mmmBalance = new MmmBalanceQuery(5);
		mmmBalance.setPlatformId(user.getApiId());
		mmmBalance.setSubmitUrl(mmmBalance.getSubmitUrl() + "loan/balancequery.action");
		logger.info("钱多多查询余额参数：" + mmmBalance.toString());
		return (String) doSubmit(mmmBalance);
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop

    /**
     * 批量钱多多查询余额
     *
     * @param cash
     * @param feePercent
     * @return
     * @throws Exception
     */
    public static String balanceQuery(List<User> userList) throws Exception {
        MmmBalanceQuery mmmBalance = new MmmBalanceQuery(5);
        String platformId = "";
        for (User user : userList) {
            platformId += user.getApiId() + ",";
        }
        mmmBalance.setPlatformId(platformId.substring(0, platformId.length() - 1));
        mmmBalance.setSubmitUrl(mmmBalance.getSubmitUrl() + "loan/balancequery.action");
        logger.info("批量钱多多查询余额参数：" + mmmBalance.toString());
        return (String) doSubmit(mmmBalance);
    }

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	/**
	 * 钱多多对账
	 * 
	 * @param cash
	 * @param feePercent
	 * @return
	 * @throws Exception
	 */
	public static List<MmmLoanOrderQuery> loanOrderQueryByBorrowId(String borrowId, String action) {
		MmmLoanOrderQuery mmmQuery = new MmmLoanOrderQuery(6);
		mmmQuery.setBatchNo(borrowId);
		mmmQuery.setSubmitUrl(mmmQuery.getSubmitUrl() + "loan/loanorderquery.action");
		Object resp = doSubmit(mmmQuery);
		List<MmmLoanOrderQuery> list = StringUtils.loanQueryJson(resp.toString());
		return list;
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop

	// v1.8.0.4 TGPROJECT-57 qj 2014-04-14 start
	/**
	 * 钱多多审核转账
	 * 
	 * @param cash
	 * @param feePercent
	 * @return
	 * @throws Exception
	 */
	public static MmmToLoanTransferAudit toLoanTransferAudit(String loanNoList, String auditType) {
		MmmToLoanTransferAudit mmm = new MmmToLoanTransferAudit(4);
		mmm.setLoanNoList(loanNoList);
		mmm.setAuditType(auditType);
		mmm.setSubmitUrl(mmm.getSubmitUrl() + "loan/toloantransferaudit.action");
		logger.info("钱多多审核转账参数：" + mmm.toString());
		doSubmit(mmm);
		return mmm;
	}

	// v1.8.0.4 TGPROJECT-57 qj 2014-04-14 stop

	// v1.8.0.4 TGPROJECT-27 qj 2014-04-04 start
	/**
	 * 双乾接口填充手动投标
	 * 
	 * @param tender
	 * @return
	 */
	public static MmmLoanTransfer addTender(BorrowTender tender) {
		MmmLoanTransfer loan = new MmmLoanTransfer(3);//3是转账
		if (tender.getBorrow().getType() == Constant.TYPE_FLOW) {
			loan.setNeedAudit("1");// 不需要审核直接划款
		}
		loan.setSubmitUrl(loan.getSubmitUrl() + "/loan/loan.action");// 页面提交地址  loan在上面一开始初始化时就有了SubmitUrl，所以loan.getSubmitUrl()有值
		loan.setTransferAction("1");// 1.投标 2.还款
		loan.setAction("1");// 1.手动转账 2.自动转账
		loan.setTransferType("2");// 1.桥连(资金到平台托管账户) 2.直连（资金为冻结状态）
		loan.setReturnURL(Global.getString("weburl") + "/public/mmm/tenderReturn.html");// 页面回调地址
		loan.setNotifyURL(Global.getString("weburl") + "/public/mmm/tenderNotify.html");// 后台回调地址
		List<LoanJson> list = new ArrayList<LoanJson>();
		LoanJson json = new LoanJson();// 转账列表类
		json.setLoanOutMoneymoremore(tender.getUser().getApiId());// 付款人乾多多标识 
		json.setLoanInMoneymoremore(tender.getBorrow().getUser().getApiId());// 收款人乾多多标识
		json.setBatchNo(tender.getBorrow().getId() + "");// 网贷平台标号
		json.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());// 网贷平台订单号??
		json.setAmount(tender.getAccount() + "");// 金额
		json.setFullAmount(tender.getBorrow().getAccount() + "");// 满标标额
		json.setRemark("投标");// 备注
		json.setSecondaryJsonList(null);// 二次分配列表

		// add by gy 2016-10-26 16:53:28 begin
		logger.info("是否需要将体验金利息封装到转账list：" + tender.getExperienceMoney());
		if (tender.getExperienceMoney() != null) { // 如果存在体验金，则需要从自由账户转钱给借款人
			// 如果使用体验金抵扣本金，则需要将接口请求参数remark1填上体验金的id，并且投资总额扣除体验金利息
			loan.setRemark1(String.valueOf(tender.getExperienceMoney().getId()));
			json.setAmount((tender.getAccount() - tender.getExperienceMoney().getExperienceInterest()) + "");// 金额

			LoanJson json2 = new LoanJson();// 转账列表类
			json2.setLoanOutMoneymoremore(Integer.parseInt(Global.getValue("config_online")) == 1 ? Global.getValue("plat_form_mmm") : Global.getValue("plat_form_test_mmm"));// 付款人乾多多标识 -- 这里是自有账户的乾多多标识
			json2.setLoanInMoneymoremore(tender.getBorrow().getUser().getApiId());// 收款人乾多多标识
			json2.setBatchNo(tender.getBorrow().getId() + "");// 网贷平台标号
			json2.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());// 网贷平台订单号
			json2.setAmount(tender.getExperienceMoney().getExperienceInterest() + "");// 金额
			json2.setFullAmount(tender.getBorrow().getAccount() + "");// 满标标额
			json2.setRemark("体验金利息抵扣投标本金");// 备注
			json2.setSecondaryJsonList(null);// 二次分配列表
			list.add(json2);
		}
		list.add(json);
		// add by gy 2016-10-26 17:01:08 end

		loan.setLoanJsonList(list);// 转账列表
		loan.sign();
		loan.setLoanJsonList(StringUtils.UrlEncoder(loan.getLoanJsonList(), "UTF-8"));//??
		logger.info("手动投标参数封装：" + loan.toString());
		return loan;
	}

	/**
	 * 扣款
	 * 
	 * @param mmmPay
	 * @return
	 */
	public static List<MmmLoanTransfer> deductLoan(MmmPay mmmPay) {
		MmmLoanTransfer loan = new MmmLoanTransfer(3);
		logger.info("转账签名参数:" + loan.getSignInfo());
		loan.setSubmitUrl(loan.getSubmitUrl() + "/loan/loan.action");// 页面提交地址
		loan.setNeedAudit(mmmPay.getNeedAudit());// 空.需要审核(冻结状态) 1.自动通过
		loan.setTransferAction(mmmPay.getTransferAction());// 1.投标 2.还款3，其他
		loan.setAction("2");// 1.手动转账 2.自动转账
		loan.setTransferType(mmmPay.getTransferType());// 1.桥连(资金到平台托管账户) 2.直连（资金为冻结状态）
		List<LoanJson> list = new ArrayList<LoanJson>();
		LoanJson json = new LoanJson();// 转账列表
		json.setLoanOutMoneymoremore(mmmPay.getMmmId());// 付款人乾多多标识
		json.setLoanInMoneymoremore(mmmPay.getToMmmId());// 收款人乾多多标识
		json.setBatchNo(mmmPay.getBorrowId());// 网贷平台标号
		json.setOrderNo(mmmPay.getOrderNo());// 网贷平台订单号
		json.setAmount(mmmPay.getAmount() + "");// 金额
		// 1.8.0.4_u2 TGPROJECT-304 lx start
		if (mmmPay.getFullAmount() != 0) {// 满标标额
			json.setFullAmount(mmmPay.getFullAmount() + "");// 满标标额
		}
		// 1.8.0.4_u2 TGPROJECT-304 lx end
		json.setSecondaryJsonList(null);
		list.add(json);
		loan.setLoanJsonList(list);
		if (mmmPay.getOperateType().equals(MmmType.ADDTENDER)) {
			loan.setTender(true);
		}
		logger.info("还款自动扣款接口调用开始： 参数：" + loan.toString());
		return (List<MmmLoanTransfer>) doSubmit(loan);
	}

	/**
	 * add by lijing 后台充值专用
	 * 
	 * @param mmmPay
	 * @param rechargeType
	 * @return
	 */
	public static List<MmmLoanTransfer> deductLoan(MmmPay mmmPay, String rechargeType) {
		logger.info("当前转账状态为:"+mmmPay.getNeedAudit()+"(1为自动通过,\"\"为需要审核");
		MmmLoanTransfer loan = new MmmLoanTransfer(3);
		loan.setSubmitUrl(loan.getSubmitUrl() + "/loan/loan.action");// 页面提交地址
		loan.setNeedAudit(mmmPay.getNeedAudit());// 空.需要审核(冻结状态) 1.自动通过
		loan.setTransferAction(mmmPay.getTransferAction());// 1.投标 2.还款3，其他
		loan.setAction("2");// 1.手动转账 2.自动转账 设置为手动会跳到手动输入密码页面
		loan.setTransferType(mmmPay.getTransferType());// 1.桥连(资金到平台托管账户) 2.直连（资金为冻结状态）
		loan.setRemark1(rechargeType);// 1.后台奖励充值 2.后台充值 3.其他4.活动折现充值(批量充值)
		List<LoanJson> list = new ArrayList<LoanJson>();
		LoanJson json = new LoanJson();// 转账列表
		json.setLoanOutMoneymoremore(mmmPay.getMmmId());// 付款人乾多多标识
		json.setLoanInMoneymoremore(mmmPay.getToMmmId());// 收款人乾多多标识
		json.setBatchNo(mmmPay.getBorrowId());// 当前充值记录id
		json.setOrderNo(mmmPay.getOrderNo());// 网贷平台订单号(工具类生成)
		json.setAmount(mmmPay.getAmount() + "");// 金额
		int key = NumberUtils.getInt(rechargeType);
		switch (key) {
		case 1:
			json.setRemark("邀请奖励充值");
			break;
		case 2:
			json.setRemark("后台充值");
			break;
		case 3:
			json.setRemark("其他");
			break;
		case 4:
			json.setRemark("后台批量充值");
			break;
		default:
			json.setRemark("网站后台充值");
			break;
		}
		if("4".equals(rechargeType)){
			loan.setNotifyURL(Global.getString("weburl") + "/public/mmm/batchReachargeNotify.html");
		}
		// 1.8.0.4_u2 TGPROJECT-304 lx start
		if (mmmPay.getFullAmount() != 0) {// 满标标额
			json.setFullAmount(mmmPay.getFullAmount() + "");// 满标标额
		}
		// 1.8.0.4_u2 TGPROJECT-304 lx end
		json.setSecondaryJsonList(null);
		list.add(json);
		loan.setLoanJsonList(list);
		if (mmmPay.getOperateType().equals(MmmType.ADDTENDER)) {
			loan.setTender(true);
		}
		logger.info("双乾转账接口调用开始： 参数：" + loan.toString());
		return (List<MmmLoanTransfer>) doSubmit(loan);
	}

	/**
	 * 划款及二次分配（还款还利息及扣除利息管理费）
	 * 
	 * @param mmmPay
	 * @return
	 */
	public static List<MmmLoanTransfer> mmmRepayInterest(MmmPay mmmPay) {
		MmmLoanTransfer loan = new MmmLoanTransfer(3);
		loan.setSubmitUrl(loan.getSubmitUrl() + "/loan/loan.action");
		loan.setNeedAudit(mmmPay.getNeedAudit());
		loan.setTransferAction(mmmPay.getTransferAction());
		loan.setAction("2");
		loan.setTransferType(mmmPay.getTransferType());
		List<LoanJson> loanList = new ArrayList<LoanJson>();
		LoanJson loanJson = new LoanJson();
		loanJson.setLoanOutMoneymoremore(mmmPay.getMmmId());
		loanJson.setLoanInMoneymoremore(mmmPay.getToMmmId());
		loanJson.setBatchNo(mmmPay.getBorrowId());
		loanJson.setOrderNo(mmmPay.getOrderNo());
		loanJson.setAmount(mmmPay.getAmount() + "");
		// 二次分配利息管理费
		if (mmmPay.getSecondaryAmount() != 0) {
			List<SecondaryJson> secondlist = new ArrayList<SecondaryJson>();
			SecondaryJson secondJson = new SecondaryJson();
			secondJson.setLoanInMoneymoremore(mmmPay.getSecondaryMmmId());
			secondJson.setAmount(mmmPay.getSecondaryAmount() + "");
			secondJson.setTransferName("利息管理费");
			secondlist.add(secondJson);
			loanJson.setSecondaryJsonList(secondlist);
		}
		loanList.add(loanJson);
		loan.setLoanJsonList(loanList);
		return (List<MmmLoanTransfer>) doSubmit(loan);
	}

	public static Object findMmmCashBalance(String orderNo, String loanNo) {
		MmmQueryCashBalance mqcb = new MmmQueryCashBalance(6);
		mqcb.setOrderNo(orderNo);
		mqcb.setLoanNo(loanNo);
		// mqcb.sign();
		mqcb.setSubmitUrl(mqcb.getSubmitUrl() + "loan/loanorderquery.action");
		return doSubmit(mqcb);
	}
}
