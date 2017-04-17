package com.liangyou.service;

import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.springframework.stereotype.Service;

import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountWebDeduct;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.GlodTransfer;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.model.ApiPayParamModel;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.WebPayModel;

/**
 * 费用处理服务
 */
@Service
public interface ApiService {
	/**
	 * 根据第三方获取查询param
	 */
	public SearchParam getdrawBankParam();

	/**
	 * vip处理
	 */
	public void verifyVipSuccess(UserCache cache, double vipFee,
			List<Object> taskList);

	/**
	 * 绑卡回调处理
	 */
	public AccountBank addAccountBank(Object object);

	/**
	 * 放款，放款接口
	 */
	public void FullSuccessLoanMoney(String apiNo, Borrow model,
			BorrowTender tender, User borrowUser, User tenderUser,
			List<Object> taskList, String apiMethodType);

	/**
	 * 放款，分发奖励
	 */
	public void FullSuccessAward(Borrow model, BorrowTender tender,
			User borrowUser, User tenderUser, List<Object> taskList,
			double awardValue);

	// TGPROJECT-411 wsl 2014-09-04 start
	/**
	 * 放款，网站垫付奖励
	 */
	public void FullSuccessAdminAward(Borrow model, BorrowTender tender,
			User borrowUser, User tenderUser, List<Object> taskList,
			double awardValue);

	// TGPROJECT-411 wsl 2014-09-04 end
	/**
	 * 管理费处理方法： 由于汇付没有直接从用户划款值商户的接口，所以在处理管理费时模拟一笔
	 * 还款操作，付款人为借款者，收款方为平台.扣费的方式为评分到每一个投资者的 投标订单里面去扣除 确保这笔资金是从借款人账户里面扣除的
	 */
	public void doBorrowFee(List<Object> taskList,
			List<BorrowTender> tenderList, double borrowfee, Borrow borrow,
			String menageFee);

	/**
	 * 放款，收取手续费
	 */
	public void FullSuccessDeductFee(List<Object> taskList,
			List<BorrowTender> tenderList, double borrowfee, Borrow borrow,
			String menageFee);

	/**
	 * 放款，风险备用金
	 */
	public void FullSuccessRiskFee(List<Object> taskList,
			List<BorrowTender> tenderList, double riskFee, Borrow borrow,
			String menageFee);

	/**
	 * 还款给网站，还款给网站接口
	 */
	public void repayToWebSiteLoanMoney(BorrowRepayment repay, double money,
			Borrow borrow, List<Object> taskList);

	/**
	 * 网站垫付，还款给网站接口
	 */
	public void webSitePayLoanMoney(Borrow borrow, List<Object> taskList,
			User tenderUser, double money);

	/**
	 * 网站垫付，还款<本金、利息、逾期罚息>最后一步，划款过程。
	 */
	public void webSitePayLoanMoneyEnd(Borrow borrow, ApiPayParamModel apm,
			BorrowRepayment br, List<Object> taskList, String apiMethodType);

	/**
	 * 投标冻结接口
	 * 
	 * @param borrowTender
	 */
	public void addTenderFreezeMoney(BorrowTender borrowTender,
			BorrowParam param, String apiMethodType, List<Object> taskList);

	/**
	 * 投流转标，直接放款方法
	 */
	public void flowBorrowLoan(BorrowTender borrowTender, BorrowParam param,
			User tenderUser, Borrow borrow, User borrowUser,
			List<Object> taskList);

	/**
	 * 网站后台充值
	 */
	public void webRecharge(AccountRecharge ar, List<Object> taskList);

	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
	/**
	 * 网站划款
	 */
	public void webTransfer(List<Object> taskList, User toUser, double money);

	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 end
	/**
	 * 网站后台账户间转账
	 */
	public void glodTransfer(GlodTransfer gt, List<Object> taskList);

	/**
	 * 网站后台扣款
	 */
	public void webDeduct(AccountWebDeduct awd, List<Object> taskList);

	/**
	 * 撤标过程
	 */
	public void faillBorrow(List<Object> taskList, Borrow model,
			String apiMethodType);

	/**
	 * 还款，放款接口
	 */
	public void repayLoanMoney(String apiId, double money,
			List<Object> taskList, Borrow borrow, BorrowCollection c,
			String apiMethodType);

	/**
	 * 还款，放款接口;
	 */
	public void payManageFee(String apiId, double money, List<Object> taskList,
			Borrow borrow);

	/**
	 * 还款，扣除利息管理费(易极付)；扣除利息管理费与归还利息（汇付）
	 */
	public void repayBorrowFee(Borrow model, BorrowRepayment repay,
			User tenderUser, double borrowFee, double interest,
			List<Object> taskList, BorrowCollection c, String apiMethodType);

	/***
	 * 流转标还款,利息管理费
	 */
	public void flowRepayBorrowFee(Borrow model, BorrowRepayment repayment,
			List<Object> interestFeeList, User tenderUser, double borrow_fee,
			double repayments, BorrowTender tender, List<Object> taskList);

	/**
	 * 取现复审，第三方处理
	 * 
	 * @param cash
	 * @param taskList
	 * @return
	 */
	public double verifyCash(AccountCash cash, List<Object> taskList);

	/**
	 * 是否需要注册第三方
	 * 
	 * @param user
	 * @return
	 */
	public Object doRegisterApi(User user, boolean isWap);

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-05 start
	public Object doLoanFastPayApi(User user);

	/**
	 * 查询账户资金
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, String> findApiAccount(User user) throws Exception;

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop

    /**
     * 批量查询账户资金
     *
     * @param user
     * @return
     */
    public List findBatchApiAccount(List<User> userList) throws Exception;


    // v1.8.0.4 TGPROJECT-42 qj 2014-04-09 start
	/**
	 * 第三方发标、投标、自动投标拦截
	 */
	public void checkApiLoan(User user);

	/**
	 * 积分兑换现金触发接口
	 * 
	 * @param user
	 * @param money
	 * @param taskList
	 */
	public void creditForMoney(User user, double money, List<Object> taskList);

	// 1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start
	/**
	 * 处理关于平台划账给用户的方法
	 * 
	 * @param payModel
	 * @param money
	 * @param taskList
	 */
	public void doWebPayMoney(WebPayModel payModel, List<Object> taskList);

	// 1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 end
	// 帝业投资自动扣款功能 start

	/**
	 * 双乾用户扣款给平台
	 * 
	 * @param user
	 * @param money
	 */
	public void doMmmUserPayWeb(User user, double money, List<Object> taskList);

	// 帝业投资自动扣款功能 end

	/**
	 * 借款人划款给用户
	 * 
	 * @param taskList
	 * @param borrowfee
	 * @param borrow
	 * @param toUser
	 */
	public void borrowertTransferToUser(List<Object> taskList,
			double borrowfee, Borrow borrow, User toUser);

	/**
	 * @param fee, 转让费用
	 * @param model，债权转让标
	 * @param tender,当前投资
	 * @param oldTender,原投资
	 * @param taskList,任务list
	 * 汇付债权转让，接口
	 * 这里只有汇付的接口，其他的接口暂时不使用
	 * ChinaPnrPayModel 对应字段说明：
	 * ordamt，承接金额
	 * ChinaPnrPayModel中fee里边封装(CreditDealAmt实际转让金额\Fee转让手续费\PrinAmt已还本金\),以逗号隔开
	 */
	public void fullSuccessCreditAssign(double fee, Borrow model,
			BorrowTender tender, BorrowTender oldTender, Borrow oldBorrow,
			List<Object> taskList);

	/**add by lijing 
	 * 网站后台收回转账
	 * @param ar
	 * @param taskList
	 * @param rechargeUser
	 */
	public void webBackRecharge(AccountRecharge ar, List<Object> taskList,
			User rechargeUser);

	/**add by lijing 2016-12-7 15:52:13
	 * 后台批量充值
	 * @param r
	 * @param taskList
	 */
	public void webBatchRecharge(AccountRecharge r, List<Object> taskList);


	/**add by lijing 
	 * 后台批量充值 审核
	 * @param apiNo
	 * @param accountRecharge
	 * @param payer
	 * @param payee
	 * @param taskList
	 * @param apiMethodType
	 */
	public void batchRechargeLoanMoney(String apiNo,
			AccountRecharge accountRecharge, User payer, User payee,
			List<Object> taskList, String apiMethodType);

	/**add by lijing
	 * 后台批量充值审核不通过
	 * @param taskList
	 * @param accountRecharge
	 * @param fAILL_BORROW_D
	 */
	public void failBatchRecharge(List<Object> taskList,
			AccountRecharge accountRecharge, String fAILL_BORROW_D);

}
