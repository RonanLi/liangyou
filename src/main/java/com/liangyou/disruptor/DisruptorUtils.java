package com.liangyou.disruptor;

import java.util.List;

import com.liangyou.api.chinapnr.CardCashOut;
import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.api.moneymoremore.MmmToLoanFastPay;
import com.liangyou.api.pay.DeductSign;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.SignmanyBank;
import com.liangyou.api.pay.YzzNewDeduct;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.model.borrow.IpsRepaymentModel;
import com.liangyou.quartz.apiAccount.ApiAccountBean;
import com.liangyou.quartz.apiAccount.ApiAccountJobQueue;
import com.liangyou.quartz.batchRecharge.BatchReachargeBean;
import com.liangyou.quartz.batchRecharge.BatchReachargeJobQueue;
import com.liangyou.quartz.cash.CashBean;
import com.liangyou.quartz.cash.CashJobQueue;
import com.liangyou.quartz.flowRepay.FlowRepayJobQueue;
import com.liangyou.quartz.notice.NoticeJobQueue;
import com.liangyou.quartz.other.OtherBean;
import com.liangyou.quartz.other.OtherJobQueue;
import com.liangyou.quartz.payOut.PayOutBean;
import com.liangyou.quartz.payOut.PayOutJobQueue;
import com.liangyou.quartz.repay.RepayBean;
import com.liangyou.quartz.repay.RepayJobQueue;
import com.liangyou.quartz.tender.TenderBean;
import com.liangyou.quartz.tender.TenderJobQueue;
import com.liangyou.quartz.verifyFullBorrow.VerifyFullBorrowBean;
import com.liangyou.quartz.verifyFullBorrow.VerifyFullBorrowJobQueue;
import com.liangyou.quartz.webSiteRepay.WebSiteRepayBean;
import com.liangyou.quartz.webSiteRepay.WebSiteRepayJobQueue;

/**
 * 并发业务，处理工具类
 * 
 * @author zxc
 *
 */
public class DisruptorUtils {
	/**
	 * 投标
	 * 
	 * @param borrowParam
	 * @param user
	 * @throws Exception
	 */
	public static void tender(BorrowParam borrowParam, User user) {
		TenderBean tenderBean = new TenderBean();
		tenderBean.setUser(user);
		tenderBean.setBorrowParam(borrowParam);
		tenderBean.setType("tender");
		TenderJobQueue.TENDER.offer(tenderBean);
	}

	/**
	 * 接口：双乾投标
	 * 
	 * @param borrowParam
	 * @param user
	 * @throws Exception
	 */
	public static void mmmTender(BorrowParam borrowParam, User user) throws Exception {
		TenderBean tenderBean = new TenderBean();
		tenderBean.setUser(user);
		tenderBean.setBorrowParam(borrowParam);
		tenderBean.setType("mmmTender");
		TenderJobQueue.TENDER.offer(tenderBean);
	}

	/**
	 * 接口：双乾投标（体验标）
	 * 
	 * @param borrowParam
	 * @param user
	 * @throws Exception
	 */
	public static void experienceTender(BorrowParam borrowParam, User user) throws Exception {
		TenderBean tenderBean = new TenderBean();
		tenderBean.setUser(user);
		tenderBean.setBorrowParam(borrowParam);
		tenderBean.setType("experienceTender");
		TenderJobQueue.TENDER.offer(tenderBean);
	}

	/**
	 * 满标复审 通过
	 * 
	 * @param borrowModel
	 * @param param
	 * @throws Exception
	 */
	public static void fullSuccess(BorrowModel borrowModel, BorrowParam param) throws Exception {

		VerifyFullBorrowBean verifyFullBorrowBean = new VerifyFullBorrowBean();
		verifyFullBorrowBean.setType("fullSuccess");
		verifyFullBorrowBean.setBorrowModel(borrowModel);
		verifyFullBorrowBean.setBorrowParam(param);

		VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW.offer(verifyFullBorrowBean);

	}

	/**
	 * 满标复审 未通过
	 * 
	 * @param borrowModel
	 * @param param
	 * @throws Exception
	 */
	public static void failBorrow(BorrowModel borrowModel, BorrowParam param) throws Exception {
		VerifyFullBorrowBean verifyFullBorrowBean = new VerifyFullBorrowBean();
		verifyFullBorrowBean.setType("failBorrow");
		verifyFullBorrowBean.setBorrowModel(borrowModel);
		verifyFullBorrowBean.setBorrowParam(param);

		VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW.offer(verifyFullBorrowBean);
	}

	/**
	 * 还款
	 * 
	 * @param borrowRepayment
	 * @param param
	 */
	public static void repay(BorrowRepayment borrowRepayment, BorrowParam param) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("repay");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowRepayment(borrowRepayment);

		RepayJobQueue.REPAY.offer(repayBean);
	}

	/**
	 * add by gy 2016-10-21 12:17:27 体验金还款
	 * 
	 * @param borrowRepayment
	 * @param param
	 */
	public static void experienceMoneyRepay(BorrowRepayment borrowRepayment, BorrowParam param) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("experienceMoneyRepay");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowRepayment(borrowRepayment);

		RepayJobQueue.REPAY.offer(repayBean);
	}

	/**
	 * 环迅还款方法
	 * 
	 * @param repaymentModel
	 * @param param
	 */
	public static void ipsRepay(IpsRepaymentModel repaymentModel, BorrowParam param) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("ipsRepay");
		repayBean.setBorrowParam(param);
		repayBean.setIpsRepaymentModel(repaymentModel);
		RepayJobQueue.REPAY.offer(repayBean);

	}

	/**
	 * 国控小薇提前还款
	 * 
	 * @param borrowRepayment
	 * @param param
	 * @param ppLog
	 */
	public static void priorRepay(BorrowRepayment borrowRepayment, BorrowParam param, PriorRepayLog ppLog) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("priorRepay");
		repayBean.setBorrowParam(param);
		repayBean.setPpLog(ppLog);
		repayBean.setBorrowRepayment(borrowRepayment);

		RepayJobQueue.REPAY.offer(repayBean);
	}

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun start
	/**
	 * 汇丰贷 提前还款
	 * 
	 * @param borrowRepayment
	 * @param param
	 * @param ppLog
	 */
	public static void hfPriorRepay(BorrowRepayment borrowRepayment, BorrowParam param, PriorRepayLog ppLog) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("hfPriorRepay");
		repayBean.setBorrowParam(param);
		repayBean.setPpLog(ppLog);
		repayBean.setBorrowRepayment(borrowRepayment);

		RepayJobQueue.REPAY.offer(repayBean);
	}
	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun end

	/**
	 * 还款给网站垫付
	 * 
	 * @param borrowRepayment
	 * @param param
	 */
	public static void repayToWebSite(BorrowRepayment borrowRepayment, BorrowParam param) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("repayToWebSite");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowRepayment(borrowRepayment);

		RepayJobQueue.REPAY.offer(repayBean);

	}

	/**
	 * 流转标还款
	 * 
	 * @param borrowRepayment
	 */
	public static void flowRepay(BorrowRepayment borrowRepayment) {
		FlowRepayJobQueue.FLOW_REPAY.offer(borrowRepayment);
	}

	/**
	 * 自动投标
	 * 
	 * @param borrowModel
	 * @throws Exception
	 */
	public static void autoTender(BorrowModel borrowModel) throws Exception {
		TenderBean tenderBean = new TenderBean();
		tenderBean.setType("autoTender");
		tenderBean.setBorrowModel(borrowModel);

		TenderJobQueue.TENDER.offer(tenderBean);
	}

	/**
	 * 易极付充值回调任务处理
	 * 
	 * @param rechargeModel
	 * @param log
	 * @param param
	 * @throws Exception
	 */
	public static void doRechargeBackTask(RechargeModel rechargeModel, AccountLog log, BorrowParam param) throws Exception {
		CashBean cb = new CashBean();
		cb.setType("rechargeBack");
		cb.setRechargeModel(rechargeModel);
		cb.setAccountLog(log);
		cb.setBorrowParam(param);

		CashJobQueue.CASH.offer(cb);
	}

	/**
	 * 易极付代扣充值处理
	 * 
	 * @param rechargeModel
	 * @param log
	 * @param param
	 * @throws Exception
	 */
	public static void doRechargeBackTask000(RechargeModel rechargeModel, AccountLog log, BorrowParam param) throws Exception {
		CashBean cb = new CashBean();
		cb.setType("rechargeBack");
		cb.setRechargeModel(rechargeModel);
		cb.setAccountLog(log);
		cb.setBorrowParam(param);

		CashJobQueue.CASH.offer(cb);
	}

	/**
	 * 易极付充值回调任务处理
	 * 
	 * @param cashModel
	 * @throws Exception
	 */
	public static void doVerifyCashBackTask(AccountCashModel cashModel) throws Exception {

		CashBean cb = new CashBean();
		cb.setType("verifyCashBack");
		cb.setAccountCashModel(cashModel);

		CashJobQueue.CASH.offer(cb);
	}

	/**
	 * 发送 站内信、短信、邮件
	 * 
	 * @param msgReq
	 * @throws Exception
	 */
	public static void sendMsg(MsgReq msgReq) throws Exception {
		NoticeJobQueue.MSGREQ.offer(msgReq);// 采用队列的形式处理
	}

	/**
	 * 计算逾期天数及利息
	 * 
	 * @throws Exception
	 */
	public static void lateDaysAndInterest() throws Exception {
		OtherBean otherBean = new OtherBean();
		otherBean.setType("lateDaysAndInterest");

		OtherJobQueue.OTHER.offer(otherBean);
	}

	/**
	 * 网站垫付
	 * 
	 * @param repaymentId
	 * @param param
	 */
	public static void webSitePayForLateBorrow(long repaymentId, BorrowParam param) {
		WebSiteRepayBean wsrb = new WebSiteRepayBean();
		wsrb.setParam(param);
		wsrb.setRepaymentId(repaymentId);
		wsrb.setType("webSitePayForLateBorrow");

		WebSiteRepayJobQueue.WEBSITE_REPAY.offer(wsrb);
	}

	/**
	 * 发债权转让标
	 * 
	 * @param bm
	 * @param lg
	 * @param bt
	 * @param param
	 */
	public static void addAssignmentBorrow(BorrowModel bm, AccountLog lg, BorrowTender bt, BorrowParam param) {

		RepayBean repayBean = new RepayBean();
		repayBean.setType("addAssignmentBorrow");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowModel(bm);
		repayBean.setAccountLog(lg);
		repayBean.setBorrowTender(bt);

		RepayJobQueue.REPAY.offer(repayBean);
	}

	/**
	 * 第三方开户接口
	 */
	public static void apiUserRegister(User user, BorrowParam param) {
		ApiAccountBean aab = new ApiAccountBean();
		aab.setBorrowParam(param);
		aab.setUser(user);
		aab.setType("apiUserRegister");

		ApiAccountJobQueue.API_ACCOUNT.offer(aab);
	}

	/**
	 * 双乾激活接口
	 */
	public static void apiUserActivate(User user, BorrowParam param) {

		ApiAccountBean aab = new ApiAccountBean();
		aab.setBorrowParam(param);
		aab.setUser(user);
		aab.setType("apiUserActivate");

		ApiAccountJobQueue.API_ACCOUNT.offer(aab);

	}

	/**
	 * 绑定银行卡异步回调，没有同步回调
	 */
	public static void cardCashOutNotify(CardCashOut cardCashOut) {

		ApiAccountBean aab = new ApiAccountBean();
		aab.setType("cardCashOutNotify");
		aab.setCardCashOut(cardCashOut);

		ApiAccountJobQueue.API_ACCOUNT.offer(aab);

	}
	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 start

	/**
	 * apiLoanFastPay ---- 调用异步处理实名认证 mmmBindingCard ---- 调用异步处理绑卡 mmmwWthhold------调用异步处理代扣
	 * 
	 * @param user
	 * @param param
	 * @param loanFastPay
	 * @param operate
	 */
	public static void apiLoanFastPay(User user, BorrowParam param, MmmToLoanFastPay loanFastPay, String operate) {

		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setType(operate);
		apiAccountBean.setUser(user);
		apiAccountBean.setBorrowParam(param);
		apiAccountBean.setMmmToLoanFastPay(loanFastPay);

		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);

	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 end
	/**
	 * 汇付取现回调
	 * 
	 * @param param
	 * @param cashModel
	 * @throws Exception
	 */
	public static void doVerifyCashBackTask(BorrowParam param, AccountCashModel cashModel) throws Exception {

		CashBean cb = new CashBean();
		cb.setType("hfverifyCashBack");
		cb.setAccountCashModel(cashModel);
		cb.setBorrowParam(param);

		CashJobQueue.CASH.offer(cb);

	}

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-10 start
	/**
	 * 双乾取现回调
	 * 
	 * @param param
	 * @param cashModel
	 * @throws Exception
	 */
	public static void doMmmVerifyCashBackTask(BorrowParam param, AccountCashModel cashModel) throws Exception {

		CashBean cb = new CashBean();
		cb.setType("mmmverifyCashBack");
		cb.setAccountCashModel(cashModel);
		cb.setBorrowParam(param);

		CashJobQueue.CASH.offer(cb);

	}

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-10 end
	/**
	 * 汇付vip支付
	 * 
	 * @param cache
	 * @param log
	 * @param param
	 */
	public static void payVipCallBack(UserCache cache, AccountLog log, BorrowParam param) {

		PayOutBean payOutBean = new PayOutBean();
		payOutBean.setType("payVipCallBack");
		payOutBean.setAccountLog(log);
		payOutBean.setUserCache(cache);
		payOutBean.setBorrowParam(param);

		PayOutJobQueue.PAYOUT.offer(payOutBean);

	}

	/**
	 * 金账户充值
	 * 
	 * @param cash
	 * @param log
	 * @param param
	 */
	public static void glodCashSuccess(CashOut cash, WebGlodLog log, BorrowParam param) {

		PayOutBean payOutBean = new PayOutBean();
		payOutBean.setType("glodCashSuccess");
		payOutBean.setCashOut(cash);
		payOutBean.setWebGlodLog(log);
		payOutBean.setBorrowParam(param);

		PayOutJobQueue.PAYOUT.offer(payOutBean);
	}

	// v1.8.0.3 TGPROJECT-64 qj 2014-04-16 start
	/**
	 * 金账户充值，添加记录
	 * 
	 * @param log
	 */
	public static void webGlodLogBack(WebGlodLog log) {
		PayOutBean payOutBean = new PayOutBean();
		payOutBean.setType("webGlodLogBack");
		payOutBean.setWebGlodLog(log);

		PayOutJobQueue.PAYOUT.offer(payOutBean);
	}

	// v1.8.0.3 TGPROJECT-64 qj 2014-04-16 stop
	/**
	 * 汇付签名
	 * 
	 * @param dedu
	 */
	public static void madeDeductSign(DeductSign dedu) {
		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setType("madeDeductSign");
		apiAccountBean.setDeductSign(dedu);

		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);
	}

	// v1.8.0.3_u2 TGPROJECT-293 2014-05-29 qinjun start
	// 易极付实名关联回调处理
	/**
	 * 易极付实名回调
	 * 
	 * @param na
	 */
	public static void yjfRealNameCall(NewAuthorize na) {

		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setType("yjfRealNameCall");
		apiAccountBean.setNewAuthorize(na);

		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);
	}
	// v1.8.0.3_u2 TGPROJECT-293 2014-05-29 qinjun start

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	/**
	 * 汇付生利宝
	 * 
	 * @param fssTrans
	 * @param param
	 */
	public static void interestGenerateCall(FssTrans fssTrans, BorrowParam param) {

		PayOutBean payOutBean = new PayOutBean();
		payOutBean.setType("interestGenerateCall");
		payOutBean.setFssTrans(fssTrans);
		payOutBean.setBorrowParam(param);

		PayOutJobQueue.PAYOUT.offer(payOutBean);
	}
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end

	// v1.8.0.4_u3 TGPROJECT-340 qinjun 2014-06-23 start
	/**
	 * 汇付企业注册
	 * 
	 * @param user
	 * @param reg
	 */
	public static void huifuUserCorpRegister(User user, CorpRegister reg) {

		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setType("huifuUserCorpRegister");
		apiAccountBean.setUser(user);
		apiAccountBean.setCorpRegister(reg);

		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);
	}
	// v1.8.0.4_u3 TGPROJECT-340 qinjun 2014-06-23 start

	// 1.8.0.4_u4 TGPROJECT-345 wujing dytz start
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
	/**
	 * 每隔30分钟调度查询一次现金奖励发放，我要帮+帝业投资 网站使用
	 */
	public static void doRewardAsReadyMoney() {

		OtherBean otherBean = new OtherBean();
		otherBean.setType("doRewardAsReadyMoney");

		OtherJobQueue.OTHER.offer(otherBean);
	}

	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end
	/**
	 * 注册实名后，投资达到1000元，发放50奖励(投资人和推荐人都有)，帝业投资网站
	 */
	public static void doRegisterAndIdentReward() {

		OtherBean otherBean = new OtherBean();
		otherBean.setType("doRegisterAndIdentReward");

		OtherJobQueue.OTHER.offer(otherBean);

	}
	// 1.8.0.4_u4 TGPROJECT-345 wujing dytz start

	// TGPROJECT-351 自动扣款调度任务 start
	/**
	 * 自动扣款业务，帝业投资
	 */
	public static void doAutoRepayWeb() {
		OtherBean otherBean = new OtherBean();
		otherBean.setType("doAutoRepayWeb");
		OtherJobQueue.OTHER.offer(otherBean);
	}

	// 手动还款
	/**
	 * 老账房使用，还款给网站
	 * 
	 * @param repeyment
	 * @param param
	 */
	public static void doUserManualPayWeb(BorrowRepayment repeyment, BorrowParam param) {
		RepayBean repayBean = new RepayBean();
		repayBean.setType("doUserManualPayWeb");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowRepayment(repeyment);

		RepayJobQueue.REPAY.offer(repayBean);
	}
	// TGPROJECT-351 自动扣款调度任务 end

	// 无卡签约 TGPROJECT-362 start

	/**
	 * 无卡代扣签约
	 * 
	 * @param signBank
	 * @param param
	 */
	public static void doSignmanysign(SignmanyBank signBank, BorrowParam param) {

		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setType("doSignmanysign");
		apiAccountBean.setSinSignmanyBank(signBank);
		apiAccountBean.setBorrowParam(param);

		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);

	}

	// 无卡签约 TGPROJECT-362 end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start
	/**
	 * 老账房提前还款
	 * 
	 * @param borrowRepayment
	 * @param param
	 * @param ppLog
	 */
	public static void lzfPriorRepay(BorrowRepayment borrowRepayment, BorrowParam param, PriorRepayLog ppLog) {

		RepayBean repayBean = new RepayBean();
		repayBean.setType("lzfPriorRepay");
		repayBean.setBorrowParam(param);
		repayBean.setBorrowRepayment(borrowRepayment);
		repayBean.setPpLog(ppLog);

		RepayJobQueue.REPAY.offer(repayBean);
	}
	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

	public static void doRedExtend(String[] ids, BorrowParam param) {
		WebSiteRepayBean wsrb = new WebSiteRepayBean();
		wsrb.setType("doRedExtend");
		wsrb.setAccountRechargeIds(ids);
		wsrb.setParam(param);

		// 网站垫付任务比较少，奖励发放和还款给网站放到一块
		WebSiteRepayJobQueue.WEBSITE_REPAY.offer(wsrb);
	}

	/**
	 * 环迅开户有，自动绑定银行卡
	 * 
	 * @param cardhOut
	 */
	public static void doIpsAddAccountBank(User user, AccountBank bank) {
		ApiAccountBean apiAccountBean = new ApiAccountBean();
		apiAccountBean.setAccountBank(bank);
		apiAccountBean.setUser(user);
		apiAccountBean.setType("ipsAddBank");
		ApiAccountJobQueue.API_ACCOUNT.offer(apiAccountBean);

	}

	public static void doIpsAddBorrow(BorrowParam param) {
		TenderBean tbean = new TenderBean();
		tbean.setBorrowParam(param);
		tbean.setType("ipsAddborrow");
		TenderJobQueue.TENDER.offer(tbean);
	}

	public static void doIpsCashBackTask(AccountCashModel accountCash, BorrowParam borrowParam) {
		CashBean cb = new CashBean();
		cb.setType("IpsCashBack");
		cb.setAccountCashModel(accountCash);
		cb.setBorrowParam(borrowParam);
		CashJobQueue.CASH.offer(cb);
	}

	/**
	 * 易极付代扣充值异步回调处理
	 * 
	 * @param yzznewdeduct
	 */
	public static void deductRechargeOffLine(YzzNewDeduct yzznewdeduct) {

		CashBean cb = new CashBean();
		cb.setType("payDeductRecharge");
		cb.setYzzNewDeduct(yzznewdeduct);

		CashJobQueue.CASH.offer(cb);

	}

	/**
	 * add by lijing 后台批量充值审核
	 * 
	 * @param list
	 * @param checkString
	 *            审核状态
	 */
	public static void verifyBatchRecharge(List<AccountRecharge> list, String checkString) {
		if ("1".equals(checkString)) {
			for (AccountRecharge accountRecharge : list) {
				BatchReachargeBean brb = new BatchReachargeBean();
				brb.setType("verifyBatchRecharge");
				brb.setAccountRecharge(accountRecharge);
				BatchReachargeJobQueue.batchReacharge.offer(brb);
			}
		} else {
			for (AccountRecharge accountRecharge : list) {
				BatchReachargeBean brb = new BatchReachargeBean();
				brb.setType("failVerifyBatchRecharge");
				brb.setAccountRecharge(accountRecharge);
				BatchReachargeJobQueue.batchReacharge.offer(brb);
			}
		}

	}

}
