package com.liangyou.context;
/**
 * 汇付后台任务计划表对应的类型。
 * @author Administrator
 * //v1.8.0.4  TGPROJECT-34   qj  2014-04-10 start
 * //v1.8.0.4  TGPROJECT-34   qj  2014-04-10 start
 *
 */
public class MmmType {
    //双乾交易类型：
	public static String MMM_VERIFY_BORROW = "mmm_verify_borrow"; //满标复审审核投标
	public static String MMM_PAY_USER = "mmm_pay_user"; //付款给平台
	public static String MMM_REPAY_INTEREST = "mmm_repay_interest"; //还款管理费及分账给平台
	public static String MMM_VERIFY_BATCHREACHARGE = "mmm_verify_batch";//后台批量充值审核
	/**
	 * 平台垫付，满标前补偿金
	 */
	public static String SITE_PAY_COMPENSATION = "site_pay_compensation"; 
	/**
	 * 借款人还款，满标前补偿金
	 */
	public static String REPAY_COMPENSATION = "repay_compensation"; 
	
	//系统操作类型
	public static String ADDBORROW = "ADDBORROW"; //发标
	public static String ADDTENDER  = "ADDTENDER"; //投标
	//v1.8.0.4_u5  TGPROJECT-399   qj  2014-08-14 start
	public static String ADDFLOWTENDER  = "ADDFLOWTENDER"; //投标
	//v1.8.0.4_u5  TGPROJECT-399   qj  2014-08-14 end
	public static String APPLYVIP = "APPLYVIP"; //申请vip
	public static String DOREPAY  = "DOREPAY "; // 还款
	public static String PREREPAY  = "PREREPAY "; // 提前还款
	public static String AUTOREPAY = "AUTOREPAY"; //自动还款
	public static String AUTOVERIFYFULLSUCCESS  = "AUTOVERIFYFULLSUCCESS "; //后台满标复审通过
	public static String VERIFYLOANS  = "VERIFYLOANS "; //后台满标复审通过放款
	public static String AUTOREWARD="AUTOREWARD";      //投标奖励
	public static String AUTOFLOWREPAY = "AUTOFLOWREPAY";//流转标还款
	public static String AUTOFLOWLOANS = "AUTOFLOWLOANS";//流转标放款
	public static String VERIFYBORROW = "VERIFYBORROW";//满标初审
	public static String AUTOVERIFYFULLFAIL  = "AUTOVERIFYFULLFAIL "; //后台满标复审不通过
	public static String ADDVERIFAIL  = "ADDVERIFAIL"; //后台发标审不通过
	public static String AUTOCANCEL= "AUTOCANCEL";//取消的标
	public static String AUTODOREPAYFORSECOND = "AUTODOREPAYFORSECOND";//秒标冻结
	public static String SECONDUNUNFREEZE = "SECONDUNUNFREEZE";//秒标冻结
	public static String VERIFYFULLBORROW = "VERIFYFULLBORROW"; //前台满标处理
	public static String VERIFYRECHARGE = "VERIFYRECHARGE"; //扣款审核
	public static String VERIFYVIPSUCCESS = "VERIFYVIPSUCCESS"; //vip 审核通过
	public static String VERIFYVIPFAIL = "VERIFYVIPFAIL";//vip审核不通过
	public static String WEBSITEPAYFORLATEBORROW  = "WEBSITEPAYFORLATEBORROW "; //发标
	public static String NEWCASH  = "NEWCASH"; //提现申请
	public static String VERIFYCASH = "VERIFYCASH"; //提现审核
	public static String CASHBACK = "CASHBACK";//扣款
	public static String CANCELCASH = "CANCELCASH";//取消提现
	public static String LOANS="LOANS";     //放款接口
	public static String REPAYMENT="REPAYMENT";     //还款接口
	public static String BORROWFREE="BORROWFREE";    //借款管理费
	public static String RISKMONEY="RISKMONEY";       //风险准备金
	public static String BORROWCHARGE ="BORROWCHARGE";    //借款手续费
	public static String REPAYBONDMONEY="REPAYBONDMONEY";    //还款保证金
	public static String  WEBSITEREPAY="WEBSITEREPAY";       //网站垫付，用户还款
	public static String  WEBSITE="WEBSITE";           //网站垫付
	public static String  WEBPAY="WEBPAY";           //网站垫付
	public static String FAILL_BORROW = "FAILL_BORROW";// 撤标
	
	public static String FLOW_REPAY = "FLOW_REPAY";// 流转标还款
	
	public static String  GOODS_EXPENSE_MONEY="goods_expense_money";           
	public static String  REWARD_PAY="REWARD_PAY";
	public static String  WEB_RECHARGE="WEB_RECHARGE";//后台充值
	public static String  WEB_BACK_RECHARGE="WEB_BACK_RECHARGE";//收回转账
}
