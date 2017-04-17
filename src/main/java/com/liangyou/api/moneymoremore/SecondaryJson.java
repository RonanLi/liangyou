package com.liangyou.api.moneymoremore;


/**
 * 转账二次分配列表
 * //v1.8.0.4  TGPROJECT-27   qj  2014-04-03 start
 * //v1.8.0.4  TGPROJECT-2   qj  2014-04-03 stop
 * @author Qinjun
 */
public class SecondaryJson {
	private String LoanInMoneymoremore;//二次收款人乾多多标识
	private String Amount ;// 二次分配金额
	private String TransferName;//用途
	private String Remark;//备注
	public String getLoanInMoneymoremore() {
		return LoanInMoneymoremore;
	}
	public void setLoanInMoneymoremore(String loanInMoneymoremore) {
		LoanInMoneymoremore = loanInMoneymoremore;
	}
	public String getAmount() {
		return Amount;
	}
	public void setAmount(String amount) {
		Amount = amount;
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
}
