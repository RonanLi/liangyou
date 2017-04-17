package com.liangyou.api.moneymoremore;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 转账列表 //v1.8.0.4 TGPROJECT-27 qj 2014-04-03 start //v1.8.0.4 TGPROJECT-27 qj 2014-04-03 stop
 * 
 * @author Qinjun
 */
public class LoanJson {
	private String LoanOutMoneymoremore;// 付款人乾多多标识
	private String LoanInMoneymoremore;// 收款人乾多多标识
	private String OrderNo;// 网贷平台订单号
	private String BatchNo;// 网贷平台标号
	private String Amount;// 金额
	private String FullAmount;// 满标标额
	private String TransferName;// 用途
	private String Remark;// 备注
	private String SecondaryJsonList;// 二次分配列表

	private String LoanNo;// 乾多多流水号

	private List<SecondaryJson> secondaryList;// 二次分配列表

	public String getLoanOutMoneymoremore() {
		return LoanOutMoneymoremore;
	}

	public void setLoanOutMoneymoremore(String loanOutMoneymoremore) {
		LoanOutMoneymoremore = loanOutMoneymoremore;
	}

	public String getLoanInMoneymoremore() {
		return LoanInMoneymoremore;
	}

	public void setLoanInMoneymoremore(String loanInMoneymoremore) {
		LoanInMoneymoremore = loanInMoneymoremore;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getBatchNo() {
		return BatchNo;
	}

	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getFullAmount() {
		return FullAmount;
	}

	public void setFullAmount(String fullAmount) {
		FullAmount = fullAmount;
	}

	public String getTransferName() {
		return TransferName;
	}

	public void setTransferName(String transferName) {
		TransferName = transferName;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getSecondaryJsonList() {
		return SecondaryJsonList;
	}

	public String getLoanNo() {
		return LoanNo;
	}

	public void setLoanNo(String loanNo) {
		LoanNo = loanNo;
	}

	public void setSecondaryJsonList(List<SecondaryJson> list) {
		this.SecondaryJsonList = list != null ? MmmModel.JSONEncode(list) : "";
	}

	public List<SecondaryJson> getSecondaryList() {
		return secondaryList;
	}

	public void setSecondaryList(List<SecondaryJson> secondaryList) {
		this.secondaryList = secondaryList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
