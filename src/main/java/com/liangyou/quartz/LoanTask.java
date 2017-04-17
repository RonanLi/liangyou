package com.liangyou.quartz;
/**
 * 任务类接口
 * @author Administrator
 *
 */
public interface LoanTask {

	/**
	 * 父类使用
	 */
	public static String LOAN_STATUS = "loan_status";
	/**
	 * 发送消息使用
	 */
	public static String MESSAGE_STATUS = "message_status";
	/**
	 * 投资使用
	 */
	public static String TENDER_STATUS = "tender_status";
	/**
	 * 还款使用
	 */
	public static String REPAY_STATUS = "repay_status";
	/**
	 * 流转标还款使用
	 */
	public static String FLOW_REPAY_STATUS = "flow_repay_status";
	/**
	 * 第三方账户使用
	 */
	public static String API_ACCOUNT_STATUS = "api_account_status";
	/**
	 * 充值、取现使用
	 */
	public static String CASH_STATUS = "cash_status";
	/**
	 *其他业务使用
	 */
	public static String OTHER_STATUS = "other_status";
	/**
	 * 审核标的业务使用[初审、满标复审]
	 */
	public static String VERIFY_BORROW_STATUS = "verify_borrow_status";
	/**
	 * 资金支出服务
	 */
	public static String PAYOUT_STATUS = "payout_status";
	/**
	 * 网站垫付
	 */
	public static String WEBSITE_REPAY_STATUS = "website_repay_status";
	/**
	 * 父类使用
	 */
	public void execute();
	/**
	 * 父类使用
	 */
	public void doLoan();
	/**
	 * 父类使用
	 */
	public void stop();
	/**
	 * 父类使用
	 */
	public Object getLock();

}
