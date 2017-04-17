package com.liangyou.context;
/**
 * 易极付后台任务计划表对应的类型。
 * @author Administrator
 */
public class YjfType {
	
    //交易类型：
	public static String TRADEPAYERQUITPOOLTOGETHER="tradePayerQuitPoolTogether"; // 退出投标
	public static String TRADEPAYERAPPLYPOOLTOGETHER="tradePayerApplyPoolTogether";// 投标
	public static String TRADEPAYPOOLTOGETHER="tradePayPoolTogether"; // 放款
	public static String TRADEPOOLRECEIVEBORROW="tradePoolReceiveBorrow"; //
	public static String TRADEPAYPOOLREVERSE="tradePayPoolReverse"; //还款
	public static String TRADEFINISHPOOLREVERSE ="tradeFinishPoolReverse"; // 还款接口完成
	public static String TRADEFINISHPOOL="tradeFinishPool"; // 借款交易接口
	public static String TRADECLOSEPOOLREVERSE="tradeClosePoolReverse";//还款交易关闭
	public static String TRADECLOSEPOOLTOGETHER="tradeClosePoolTogether";// 接口交易关闭
	public static String TRADECLOSEPOOL="tradeClosePool";// 集资关闭接口
	public static String TRADECREATEPOOLREVERSE="tradeCreatePoolReverse"; // 还款创建交易
	/**
	 *借款创建交易号，每一个交易必须有一个交易号
	 */
	public static String TRADECREATEPOOLTOGETHER="tradeCreatePoolTogether"; //普通表创建交易号 
	
	public static String TRADECREATEPOOL="tradeCreatePool"; //  流转标创建交易号
	public static String TRADETRANSFER="tradeTransfer";  //转账
	public static String APPLYWITHDRAW="applyWithdraw";//提现
	
	//系统操作类型
	public static String ADDBORROW = "ADDBORROW"; //发标
	public static String ADDTENDER  = "ADDTENDER"; //投标
	public static String APPLYVIP = "APPLYVIP"; //申请vip
	public static String DOREPAY  = "DOREPAY "; // 还款
	public static String PREREPAY  = "PREREPAY "; // 提前还款
	public static String AUTOREPAY = "AUTOREPAY"; //自动还款
	public static String AUTOVERIFYFULLSUCCESS  = "AUTOVERIFYFULLSUCCESS "; //后台满标复审通过
	public static String VERIFYBORROW = "VERIFYBORROW";//满标初审
	public static String FAIL  = "FAIL"; //后台满标复审不通过
	public static String AUTOCANCEL= "AUTOCANCEL";//取消的标
	public static String AUTODOREPAYFORSECOND = "AUTODOREPAYFORSECOND";//秒标冻结
	public static String VERIFYFULLBORROW = "VERIFYFULLBORROW"; //前台满标处理
	public static String VERIFYRECHARGE = "VERIFYRECHARGE"; //充值审核
	public static String VERIFYVIPSUCCESS = "VERIFYVIPSUCCESS"; //vip 审核通过
	public static String VERIFYVIPFAIL = "VERIFYVIPFAIL";//vip审核不通过
	public static String WEBSITEPAYFORLATEBORROW  = "WEBSITEPAYFORLATEBORROW "; //垫付
	public static String NEWCASH  = "NEWCASH"; //提现申请
	public static String VERIFYCASH = "VERIFYCASH"; //提现审核
	public static String CASHBACK = "CASHBACK";//扣款
	public static String CANCELCASH = "CANCELCASH";//取消提现
	public static String FLOWREPAY = "FLOWREPAY";//流标还款
	public static String CANCELBORROW = "CANCELBORROW";//标初审，取消
	//v1.8.0.4_u4  TGPROJECT-365  qinjun 2014-07-14 start
	public static String WEB_TRANSFER = "web_transfer";//网站划款
	//v1.8.0.4_u4  TGPROJECT-365  qinjun 2014-07-14 end
}
