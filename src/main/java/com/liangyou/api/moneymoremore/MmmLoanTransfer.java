package com.liangyou.api.moneymoremore;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.liangyou.context.Global;

/**
 * 转账接口
 * 
 * //v1.8.0.4 TGPROJECT-27 qj 2014-04-03 start //v1.8.0.4 TGPROJECT-27 qj 2014-04-03 stop
 * 
 * @author Qinjun
 */
public class MmmLoanTransfer extends MmmModel {
	private static Logger logger = Logger.getLogger(MmmLoanTransfer.class);

	private String loanJsonList;// 转账列表
	private String transferAction;// 1.投标 2.还款
	private String action;// 1.手动转账 2.自动转账
	private String transferType;// 1.桥连(资金到平台托管账户) 2.直连（资金为冻结状态）
	private String needAudit;// 空.需要审核(冻结状态) 1.自动通过
	private String returnURL = Global.getString("weburl") + "/public/mmm/loanTransferAuditReturn.html";// 页面回调地址
	private String notifyURL = Global.getString("weburl") + "/public/mmm/notifyPrintSuccess.html";// 后台回调地址

	private List<LoanJson> loanList;
	// 提交参数列表
	private String[] commitParamNames = new String[] { "LoanJsonList", "PlatformMoneymoremore", "TransferAction", "Action", "TransferType", "NeedAudit", "Remark1", "ReturnURL", "NotifyURL" };
	private String[] returnParamNames = new String[] { "LoanJsonList", "PlatformMoneymoremore", "Action", "Remark1", "ResultCode" };

	public MmmLoanTransfer(int operation) {
		super(operation);
	}

	public String getTransferAction() {
		return transferAction;
	}

	public void setTransferAction(String transferAction) {
		this.transferAction = transferAction;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
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

	public String getLoanJsonList() {
		return this.loanJsonList;
	}

	public void setLoanJsonList(List<LoanJson> list) {
		this.loanJsonList = list != null ? JSONEncode(list) : "";
	}

	public void setLoanJsonList(String jsonStr) {
		this.loanJsonList = jsonStr;
	}

	public List<LoanJson> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<LoanJson> loanList) {
		this.loanList = loanList;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
