package com.liangyou.api.moneymoremore;

/**
 * 用户提现对账查询
 * @author huangjun
 */
	

public class MmmQueryCashBalance extends MmmModel {
	
	private int action = 2;//空是转账 1.充值 2.提现

	private String batchNo;	//网贷平台标号
	
	private String beginTime;	//开始时间
	
	private String endTime;	//结束时间
	//提交参数列表
	private String[] commitParamNames = new String[] {"PlatformMoneymoremore","Action","LoanNo","OrderNo","BatchNo","BeginTime","EndTime"};

	public MmmQueryCashBalance(int operation) {
		super(operation);
	}


	public int getAction() {
		return action;
	}


	public void setAction(int action) {
		this.action = action;
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

	public String[] getCommitParamNames() {
		return commitParamNames;
	}

	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}

	
}
