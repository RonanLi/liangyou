package com.liangyou.api.ips;

/**
 * 还款，收款人信息封装bean
 * @author wujing
 *
 */
public class RepaymentDetail {
	

	public RepaymentDetail(){
		
	}
	
	private String  creMerBillNo;  //登记债权人时提 交的订单号
	
	private String inAcctNo;  //转入方IPS 托管 账户号
	
	private String inFee;  //转入方手续费
	
	private String outInfoFee;   //  转出方手续费
	
	private String inAmt;  // 转入金额
	
	public String doRepaymentDetailXml(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<pRow>");
		buffer.append("<pCreMerBillNo>").append(this.getCreMerBillNo()).append("</pCreMerBillNo>");
		buffer.append("<pInAcctNo>").append(this.getInAcctNo()).append("</pInAcctNo>");
		buffer.append("<pInFee>").append(this.getInFee()).append("</pInFee>");
		buffer.append("<pOutInfoFee>").append(this.getOutInfoFee()).append("</pOutInfoFee>");
		buffer.append("<pInAmt>").append(this.getInAmt()).append("</pInAmt>");
		buffer.append("</pRow >");
		
		return buffer.toString();
	}

	public String getCreMerBillNo() {
		return creMerBillNo;
	}

	public void setCreMerBillNo(String creMerBillNo) {
		this.creMerBillNo = creMerBillNo;
	}

	public String getInAcctNo() {
		return inAcctNo;
	}

	public void setInAcctNo(String inAcctNo) {
		this.inAcctNo = inAcctNo;
	}

	public String getInFee() {
		return inFee;
	}

	public void setInFee(String inFee) {
		this.inFee = inFee;
	}

	public String getOutInfoFee() {
		return outInfoFee;
	}

	public void setOutInfoFee(String outInfoFee) {
		this.outInfoFee = outInfoFee;
	}

	public String getInAmt() {
		return inAmt;
	}

	public void setInAmt(String inAmt) {
		this.inAmt = inAmt;
	}

	
	
}
