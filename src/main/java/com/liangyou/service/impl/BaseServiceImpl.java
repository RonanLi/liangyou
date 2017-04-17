package com.liangyou.service.impl;

/**
 * 基础工具类
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.api.chinapnr.ChinapnrModel;
import com.liangyou.api.ips.IpsHelper;
import com.liangyou.api.ips.Transfer;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmLoanOrderQuery;
import com.liangyou.api.moneymoremore.MmmLoanTransfer;
import com.liangyou.api.moneymoremore.MmmResultCode;
import com.liangyou.api.moneymoremore.MmmToLoanTransferAudit;
import com.liangyou.api.pay.PayModelHelper;
import com.liangyou.api.pay.RealNameCertQuery;
import com.liangyou.api.pay.SmsCaptcha;
import com.liangyou.context.Global;
import com.liangyou.context.MmmType;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.IpsPay;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.domain.SitePayLog;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.tool.javamail.Mail;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

public class BaseServiceImpl {

	private Logger logger = Logger.getLogger(BaseServiceImpl.class);

	/**
	 * 封装资金记录
	 * 
	 * @param log
	 * @param act
	 * @param operateValue
	 */
	public void fillAccountLog(AccountLog log, Account act, double operateValue) {
		fillAccountLog(log, act, operateValue, 0, "");
	}

	/**
	 * 是否开通线上环境配置。
	 * 
	 * @return
	 */
	public boolean isOnlineConfig() {
		return "1".equals(Global.getValue("config_online"));
	}

	/**
	 * 是否启用体验金功能。
	 * 
	 * @return
	 */
	public boolean isEnableExperienceMoney() {
		return "1".equals(Global.getValue("is_enable_experienceMoney"));
	}

	/**
	 * 是否开通第三方接口
	 * 
	 * @return
	 */
	public boolean isOpenApi() {
		return "1".equals(Global.getValue("open_yjf"));
	}

	/**
	 * 激活发送邮件
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void sendMail(User user) throws Exception {
		String to = user.getEmail();
		Mail m = Mail.getInstance();
		m.setTo(to);
		m.readActiveMailMsg();
		m.replace(user.getUsername(), to, "/user/active.html?id=" + m.getdecodeIdStr(user));
		logger.debug("Email_msg:" + m.getBody());
		m.sendMail();
	}

	/**
	 * 封装资金记录
	 * 
	 * @param log
	 * @param act
	 * @param operateValue
	 */
	public void fillAccountLog(AccountLog log, Account act, double operateValue, String remark) {
		fillAccountLog(log, act, operateValue, 0, remark);
	}

	public void fillAccountLog(AccountLog log, Account act, double operateValue, long borrow_id, String remark) {
		fillAccountLog(log, act, log.getUser(), log.getToUser(), operateValue, borrow_id, remark);
	}

	public void fillAccountLog(AccountLog log, Account act, User user, User toUser, double operateValue, long borrow_id, String remark) {
		fillAccountLog(log, log.getType(), act, user, toUser, operateValue, borrow_id, remark);
	}

	public void fillAccountLog(AccountLog log, String type, Account act, User user, User toUser, double operateValue, long borrow_id, String remark) {
		log.setType(type);
		log.setUser(user);
		log.setToUser(toUser);
		log.setMoney(operateValue);
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		log.setRepay(act.getRepay());
		log.setRemark(remark);
		log.setAddtime(new Date());
	}

	public void fillPriorRepayLog(PriorRepayLog log, User user, String type, double account, double money) {
		log.setUser(user);
		log.setType(type);
		log.setAccount(account);
		log.setMoney(money);
		log.setAddtime(new Date());
	}

	public void fillSitePayLog(SitePayLog SitePayLog, double accountPay, double accountTotal, Borrow borrow, BorrowTender borrowTender, double moneyPay, double moneyTotal, String type, User user) {

		SitePayLog.setAccountPay(accountPay);// 实际垫付金额
		SitePayLog.setAccountTotal(accountTotal);
		SitePayLog.setAddtime(new Date());
		SitePayLog.setBorrow(borrow);
		SitePayLog.setBorrowTender(borrowTender);
		SitePayLog.setMoneyPay(moneyPay);
		SitePayLog.setMoneyTotal(moneyTotal);
		SitePayLog.setType(type);
		SitePayLog.setUser(user);
	}

	public UserAmountLog fillUserAmountLog(UserAmount ua, String type, double operateValue, String remark, String ip) {
		UserAmountLog amountLog = new UserAmountLog();
		amountLog.setUser(ua.getUser());
		amountLog.setType(type);
		amountLog.setAccount(Math.abs(operateValue));
		amountLog.setAccountTotal(ua.getCredit());
		amountLog.setAccountUse(ua.getCreditUse() - operateValue);
		amountLog.setAccountNouse(ua.getCreditNouse() + operateValue);
		amountLog.setRemark(remark);
		amountLog.setAddtime(new Date());
		amountLog.setAddip(ip);
		return amountLog;
	}

	protected String getLogRemark(Borrow b) {
		String s = "[<a href='" + Global.getString("weburl") + "/invest/detail.html?borrowid=" + b.getId() + "' target=_blank>" + b.getName() + "</a>]";
		return s;
	}

	public String getRequestParams(HttpServletRequest request) {
		String params = "";
		try {
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String parName = (String) e.nextElement();
				String value = request.getParameter(parName);
				params += parName + "=" + value + "&";
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return params;
	}

	public void sendActiveMail(User user) {
		try {
			String to = user.getEmail();
			Mail m = Mail.getInstance();
			m.setSubject(Global.getValue("email_from_name"));
			m.setTo(to);
			m.readActiveMailMsg();
			m.replace(user.getUsername(), to, "/user/active.html?id=" + m.getdecodeIdStr(user));
			logger.debug("Email_msg:" + m.getBody());
			m.sendMail();
		} catch (Exception e) {
			logger.error(e);
			throw new BussinessException("注册发送邮件失败！");
		}
	}

	/** ---------------------------- 所有双乾接口处理方法如下 --------------------------- */

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	/**
	 * 钱多多查询余额
	 * 
	 * @param cash
	 * @param feePercent
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, String> mmmQalanceQuery(User user) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String resp = MmmHelper.balanceQuery(user);
		String[] resps = resp.split("\\|");
		map.put("apiUseMoney", resps[0]);
		map.put("apiTotal", resps[1]);
		map.put("apiNoUseMoney", resps[2]);
		return map;
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
	public synchronized List mmmQalanceQuery(List<User> userList) throws Exception {
		List<Map<Object, Object>> list = new ArrayList();
		String resp = MmmHelper.balanceQuery(userList);
		String[] respss = resp.split(",");
		Long stratTime = System.nanoTime();
		logger.info("批量查询异常余额账户开始！请求返回总数为：" + respss.length + "，需要对比的系统用户总数为：" + userList.size());
		for (String respp : respss) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			String[] resps = respp.split("\\|");
			for (User u : userList) {
				if (u.getAccount() != null && u.getApiId().equals(resps[0]) && ((u.getAccount().getUseMoney() != Double.parseDouble(resps[1]) || (u.getAccount().getNoUseMoney() != Double.parseDouble(resps[3]))))) {
					map.put("userApiId", resps[0]);
					map.put("apiUseMoney", resps[1]);
					map.put("apiTotal", resps[2]);
					map.put("apiNoUseMoney", resps[3]);
					list.add(map);
				}
			}
		}
		logger.info("批量查询异常余额账户结束！所用时间为：" + (System.nanoTime() - stratTime));
		return list;
	}

	/**
	 * 自动投标
	 * 
	 * @return
	 */
	public synchronized String mmmAutoTender() {
		return "";
	}

	/**
	 * 第三方查询是否审核投标（满标复审时）
	 * 
	 * @return
	 */
	public synchronized void mmmQueryTenderSuccess(String borrowId) {
		List<MmmLoanOrderQuery> queryList = MmmHelper.loanOrderQueryByBorrowId(borrowId, "");
		for (MmmLoanOrderQuery mmmLoanOrderQuery : queryList) {
			if (mmmLoanOrderQuery.getTransferAction().equals("1")) {// 投标进入判断
				if (!mmmLoanOrderQuery.getTransferState().equals("1") || // 该笔是否已经转帐
						!(mmmLoanOrderQuery.getActState().equals("1") || mmmLoanOrderQuery.getActState().equals("3"))// 该笔账单是否已已经通过或者自动通过
				) {
					throw new ManageBussinessException("投标记录中有未审核或者错误的投标，请您先处理", "/admin/borrow/schedule.html");
				}
			}

		}
	}

	// v1.8.0.4 TGPROJECT-34 qj 2014-04-04 start
	/**
	 * 扣费(管理费、利息管理费等给平台)
	 * 
	 * @return
	 */
	public synchronized String mmmPayUser(MmmPay mmmPay) {
		mmmPay.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());// 订单号
		logger.info("还款参数：" + JSON.toJSONString(mmmPay));
		List<MmmLoanTransfer> list = new ArrayList<MmmLoanTransfer>();
		// edit by gy 2016-10-28 18:31:26 还原了代码
		list = MmmHelper.deductLoan(mmmPay);
		// if(mmmPay.getOperateType().equals(MmmType.WEB_RECHARGE)){
		// list = MmmHelper.deductLoan(mmmPay);
		// }else if(mmmPay.getOperateType().equals(MmmType.BACK_RECHARGE)){
		// list = MmmHelper.deductLoanBack(mmmPay);
		// }
		logger.info("还款完成：" + list);
		for (MmmLoanTransfer mmmLoanTransfer : list) {
			if (!mmmLoanTransfer.getResultCode().equals("88")) {
				throw new BussinessException("扣款失败：" + MmmResultCode.getResult(mmmLoanTransfer.getResultCode(), "loan"));
			}
		}
		if (mmmPay.getOperateType().equals(MmmType.ADDTENDER)) {// 投标
			MmmLoanTransfer mmm = list.get(0);
			String loanNo = mmm.getLoanList().get(0).getLoanNo();
			return loanNo;
		} else {
			return "";
		}
	}

	// add by lijing 后台充值  批量转账
	public synchronized String mmmPayUser(MmmPay mmmPay, String rechargeType) {
		logger.info("转账参数：" + JSON.toJSONString(mmmPay));
		List<MmmLoanTransfer> list = new ArrayList<MmmLoanTransfer>();
		list = MmmHelper.deductLoan(mmmPay, rechargeType);
		logger.info("转账完成：" + list);
		for (MmmLoanTransfer mmmLoanTransfer : list) {
			if (!mmmLoanTransfer.getResultCode().equals("88")) {
				throw new BussinessException("扣款失败：" + MmmResultCode.getResult(mmmLoanTransfer.getResultCode(), "loan"));
			}
		}
		if (mmmPay.getOperateType().equals(MmmType.ADDTENDER)) {// 投标
			MmmLoanTransfer mmm = list.get(0);
			String loanNo = mmm.getLoanList().get(0).getLoanNo();
			return loanNo;
		} else {
			return "";
		}
	}

	public synchronized String mmmRepay(MmmPay mmmPay) {
		mmmPay.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		List<MmmLoanTransfer> list = MmmHelper.mmmRepayInterest(mmmPay);
		for (MmmLoanTransfer mmmLoanTransfer : list) {
			if (!mmmLoanTransfer.getResultCode().equals("88")) {
				throw new BussinessException("还款失败：" + MmmResultCode.getResult(mmmLoanTransfer.getResultCode(), "loan"));
			}
		}
		return "";
	}

	public synchronized String mmmVerifyBorrow(MmmPay mmmPay) {
		MmmToLoanTransferAudit mmm = MmmHelper.toLoanTransferAudit(mmmPay.getOrderNo(), mmmPay.getNeedAudit());
		if (!mmm.getResultCode().equals("88")) {
			throw new BussinessException("复审失败：" + MmmResultCode.getResult(mmm.getResultCode(), "verifyFullBorrow"));
		}
		return null;
	}

	// v1.8.0.4 TGPROJECT-34 qj 2014-04-04 stop
	/** ---------------------------- 双乾接口结束 --------------------------- */

	/** --------------------------------环迅接口开始--------------------------------- **/

	/**
	 * @param ipspay
	 * @return
	 */
	public synchronized String IpsVerifyBorrow(IpsPay ipspay) {

		Transfer transfer = IpsHelper.doIpsTransfer(ipspay);
		if ("MG00008F".equals(transfer.getErrCode())) { // 受理中
			ipspay.setStatus("3");
		} else if ("MG00000F".equals(transfer.getErrCode())) { // 受理成功
			ipspay.setStatus("1");
		} else {
			throw new BussinessException("处理异常" + transfer.getErrMsg() + ":" + transfer.getErrCode());
		}
		return "";

	}

	/** --------------------------------环迅接口结束--------------------------------- **/

	/**
	 * 用户提现银行卡解绑
	 * 
	 * @param usrCusId
	 * @param cardId
	 * @return
	 */
	public ChinaPnrPayModel chinaCardRemove(String usrCusId, String cardId) {
		ChinapnrModel chinam = ChinapnrHelper.chinaCardRemove(usrCusId, cardId);
		if (!chinam.getRespCode().equals("000")) {
			throw new BussinessException(chinam.getRespDesc());
		}
		return null;
	}

	/**
	 * 短信发送验证码服务
	 * 
	 * @param userId
	 * @param username
	 * @return
	 */
	public SmsCaptcha smsCaptcha(String userId, String username) {
		if (isOpenApi()) {
			SmsCaptcha sc = PayModelHelper.smsCaptcha(userId, username);
			if (StringUtils.isBlank(sc.getCheckCodeUniqueId())) {
				throw new BussinessException("获取验证码失败");
			}
			return sc;
		}
		return null;
	}

	/**
	 * 用户易极付 实名认证查询
	 * 
	 * @return
	 */
	public RealNameCertQuery realNameCertQuery(String apiId) {
		if (isOpenApi()) {
			RealNameCertQuery rnc = PayModelHelper.realNameCertQuery(apiId);
			return rnc;
		}
		return null;
	}
}
