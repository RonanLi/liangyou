package com.liangyou.api.moneymoremore;

import java.util.List;


/**
 * //v1.8.0.4  TGPROJECT-39   qj  2014-04-11 start
 * //v1.8.0.4  TGPROJECT-39   qj  2014-04-11 stop
 * @author Qinjun
 *
 */
public class MmmLoanOrderQuery extends MmmModel {
	
	private String batchNo;//标号
	
	private String beginTime;//开始时间
	
	private String endTime;//结束时间
	
	private String loanOutMoneymoremore;//付款人
	private String loanInMoneymoremore;//收款人
	private String amount;//金额
	private String transferAction;//转账类型：1.投标  2.还款
	private String action;//1.手动投标 2.自动投标
	private String transferType;//1.桥连2.直连
	private String transferState;//转账方式：转账状态：0.未转账1.已转账
	private String transferTime;//转账时间  格式：yyyyMMddHHmmss 20130101140528
	private String actState;//操作状态：0.未操作1.已通过2.已退回3.自动通过
	private String actTime;//操作时间
	private String actNo;//操作流水号
	private String secondaryState;//二次分配确认状态空.无二次分配0.未确认1.同意2.不同意3.系统自动同意
	private String secondaryTime;//二次分配确认时间
	private String secondaryJsonList;//二次分配列表
	private String transferName;//用途
	private String remark;//备注
	
	
	private List<SecondaryJson> secondaryList;
	

	//提交参数列表
	private String[] commitParamNames = new String[] {"PlatformMoneymoremore","LoanNo","OrderNo","BatchNo","BeginTime","EndTime"};
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"LoanOutMoneymoremore","LoanInMoneymoremore","LoanNo","OrderNo","BatchNo","Amount",
			"PlatformMoneymoremore","TransferAction","Action","TransferType","TransferState","TransferTime","ActState","ActTime","ActNo","SecondaryState"
			,"SecondaryTime","SecondaryJsonList","TransferName","Remark"};

	public MmmLoanOrderQuery(int operation) {
		super(operation);
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLoanOutMoneymoremore() {
		return loanOutMoneymoremore;
	}

	public void setLoanOutMoneymoremore(String loanOutMoneymoremore) {
		this.loanOutMoneymoremore = loanOutMoneymoremore;
	}

	public String getLoanInMoneymoremore() {
		return loanInMoneymoremore;
	}

	public void setLoanInMoneymoremore(String loanInMoneymoremore) {
		this.loanInMoneymoremore = loanInMoneymoremore;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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

	public String getTransferState() {
		return transferState;
	}

	public void setTransferState(String transferState) {
		this.transferState = transferState;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getActState() {
		return actState;
	}

	public void setActState(String actState) {
		this.actState = actState;
	}

	public String getActTime() {
		return actTime;
	}

	public void setActTime(String actTime) {
		this.actTime = actTime;
	}

	public String getActNo() {
		return actNo;
	}

	public void setActNo(String actNo) {
		this.actNo = actNo;
	}

	public String getSecondaryState() {
		return secondaryState;
	}

	public void setSecondaryState(String secondaryState) {
		this.secondaryState = secondaryState;
	}

	public String getSecondaryTime() {
		return secondaryTime;
	}

	public void setSecondaryTime(String secondaryTime) {
		this.secondaryTime = secondaryTime;
	}

	public String getSecondaryJsonList() {
		return secondaryJsonList;
	}

	public void setSecondaryJsonList(String secondaryJsonList) {
		this.secondaryJsonList = secondaryJsonList;
	}

	public String getTransferName() {
		return transferName;
	}

	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public List<SecondaryJson> getSecondaryList() {
		return secondaryList;
	}

	public void setSecondaryList(List<SecondaryJson> secondaryList) {
		this.secondaryList = secondaryList;
	}

}
