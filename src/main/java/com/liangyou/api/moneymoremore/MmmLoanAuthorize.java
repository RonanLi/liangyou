package com.liangyou.api.moneymoremore;

import com.liangyou.context.Global;

/**
 * 授权还款接口
 * @author Qinjun
 * v1.8.0.4  TGPROJECT-42   qj  2014-04-09 start
 * v1.8.0.4  TGPROJECT-42   qj  2014-04-09 stop
 */
public class MmmLoanAuthorize extends MmmModel {

	private String authorizeTypeOpen;//开启授权类型   :1.投标 2.还款 3.二次分配审核  将所有数字用英文逗号(,)连成一个字符串
	
	private String authorizeTypeClose;//关闭授权类型 (同开启)
	
	private String returnURL=Global.getString("weburl")+"/public/mmm/loanAuthorizeReturn.html";
	
	private String notifyURL=Global.getString("weburl")+"/public/mmm/loanAuthorizeNotify.html";
	
	private String authorizeType;
	
	//提交参数列表
	private String[] commitParamNames = new String[] {"MoneymoremoreId","PlatformMoneymoremore","AuthorizeTypeOpen","AuthorizeTypeClose", "Remark1", "ReturnURL","NotifyURL"};
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"MoneymoremoreId","PlatformMoneymoremore","AuthorizeTypeOpen","AuthorizeTypeClose","AuthorizeType", "Remark1", "ResultCode"};
	
	public MmmLoanAuthorize(int operation) {
		super(operation);
	}
	
	public String getAuthorizeTypeOpen() {
		return authorizeTypeOpen;
	}
	public void setAuthorizeTypeOpen(String authorizeTypeOpen) {
		this.authorizeTypeOpen = authorizeTypeOpen;
	}
	public String getAuthorizeTypeClose() {
		return authorizeTypeClose;
	}
	public void setAuthorizeTypeClose(String authorizeTypeClose) {
		this.authorizeTypeClose = authorizeTypeClose;
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
	public String getAuthorizeType() {
		return authorizeType;
	}
	public void setAuthorizeType(String authorizeType) {
		this.authorizeType = authorizeType;
	}

	
	
}
