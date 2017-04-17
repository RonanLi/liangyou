package com.liangyou.context;
/**
 * 汇付后台任务计划表对应的类型。
 * @author Administrator
 *
 */
public class ChinaPnrType {
    //汇付交易类型：
	public static String UsrFreezeBg = "UsrFreezeBg";  //冻结
	public static String UsrUnFreeze = "UsrUnFreeze"; // 解冻
	public static String CashOut = "CashOut"; //取现
	public static String CREDITASSIGN = "CREDITASSIGN";//债权转让接口
	public static long ADMINOUT = 1 ; //系统账户    用于系统转账。
	public static String ADMININ = "1" ; //系统账户    用于给系统转账。
	public static String TRANSFER="TRANSFER";   //从系统账号给用户划账
	//系统操作类型
	public static String ADDBORROW = "ADDBORROW"; //发标
	public static String ADDTENDER  = "ADDTENDER"; //投标
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
	public static String CREDITASSIGN_VERIFY = "CREDITASSIGN_VERIFY";//债权转让接口
	public static String REPAYMENT="REPAYMENT";     //还款接口
	public static String BORROWFREE="BORROWFREE";    //借款管理费
	public static String RISKMONEY="RISKMONEY";       //风险准备金
	public static String BORROWCHARGE ="BORROWCHARGE";    //借款手续费
	public static String REPAYBONDMONEY="REPAYBONDMONEY";    //还款保证金
	public static String  WEBSITEREPAY="WEBSITEREPAY";       //网站垫付，用户还款
	public static String  WEBSITE="WEBSITE";           //网站垫付
	public static String  WEBPAY="WEBPAY";           //网站垫付
	public static String FAILL_BORROW = "FAILL_BORROW";// 撤标
	
	public static String FLOW_REPAY = "FLOW_REPAY";// 撤标
	
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	/**
	 * 担保公司
	 */
	public static String GUARANTEE_CORP = "Y";
	/**
	 * 借款人
	 */
	public static String BORROWER_CORP = "N";// 撤标
	/**
	 * 企业开户
	 */
	public static String CORP = "CORP";
	/**
	 * 个人开户
	 */
	public static String PERSONAL = "PERSONAL";
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 end
}
