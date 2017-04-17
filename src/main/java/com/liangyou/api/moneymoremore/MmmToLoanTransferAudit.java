package com.liangyou.api.moneymoremore;

import com.liangyou.context.Global;


/**双乾审核接口
 * //v1.8.0.4  TGPROJECT-57   qj  2014-04-14 start
 * //v1.8.0.4  TGPROJECT-57   qj  2014-04-14 stop
 * @author Qinjun
 *
 */
public class MmmToLoanTransferAudit extends MmmModel {
	
	private String loanNoList;//钱多多流水号
	
	private String auditType;//审核类型 1.通过 2.退回 3.二次分配同意  4.二次分配不同意
	
	private String randomTimeStamp;//2120140320120102456  启用防抵赖时必填 格式为2位随机数加yyyyMMddHHmmssSSS格式的当前时间
	private String remark1;
	private String remark2;
	private String remark3;
	
	private String returnURL = Global.getString("weburl")+"/public/mmm/loanTransferAuditReturn.html";// 页面回调地址
	private String notifyURL = Global.getString("weburl")+"/public/mmm/notifyPrintSuccess.html";// 后台回调地址
	
	
	private String loanNoListFail;//有问题的的乾多多流水号列表
	

	//提交参数列表
	private String[] commitParamNames = new String[] {"LoanNoList","PlatformMoneymoremore","AuditType","RandomTimeStamp","Remark1","Remark2","Remark3","ReturnURL","NotifyURL"};
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"LoanNoList","LoanNoListFail","PlatformMoneymoremore","AuditType","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
	
	public MmmToLoanTransferAudit(int operation) {
		super(operation);
	}
	
	public String getLoanNoList() {
		return loanNoList;
	}
	public void setLoanNoList(String loanNoList) {
		this.loanNoList = loanNoList;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public String getRandomTimeStamp() {
		return randomTimeStamp;
	}
	public void setRandomTimeStamp(String randomTimeStamp) {
		this.randomTimeStamp = randomTimeStamp;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public String getLoanNoListFail() {
		return loanNoListFail;
	}
	public void setLoanNoListFail(String loanNoListFail) {
		this.loanNoListFail = loanNoListFail;
	}
	public String[] getCommitParamNames() {
		return commitParamNames;
	}
	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}
	public String[] getReturnParamNames() {
		return returnParamNames;
	}
	public void setReturnParamNames(String[] returnParamNames) {
		this.returnParamNames = returnParamNames;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}


}
