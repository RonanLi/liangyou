package com.liangyou.api.ips;

import com.liangyou.util.StringUtils;

/**
 * 转账详情列表封装类
 * @author wujing
 *
 */
public class TransferProw {

	private String oriMerBillNo;  //原商户订单号
	private String trdAmt;  //转账金额  :0.00
	
	private String facctType;  //转出方账户类型  :  0#机构；1#个人 
	
	private String fipsAcctNo;  //转出方IPS 托管 账户号
	
	private String ftrdFee;  //转出方明细手续 费
	
	private String  tacctType;  //转入方账户类型
	
	private String tipsAcctNo; //转入方IPS 托管 账户号 
	
	private String ttrdFee;  //转入方明细手续 费
	
	/**
	 * 创建xml文件信息
	 * @return
	 */
	public String prowXml(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<pRow>");
		buffer.append("<pOriMerBillNo>").append(StringUtils.isNull(this.getOriMerBillNo())).append("</pOriMerBillNo>");
		buffer.append("<pTrdAmt>").append(this.getTrdAmt()).append("</pTrdAmt>");
		buffer.append("<pFAcctType>").append(this.getFacctType()).append("</pFAcctType>");
		buffer.append("<pFIpsAcctNo>").append(this.getFipsAcctNo()).append("</pFIpsAcctNo>");
		buffer.append("<pFTrdFee>").append(this.getFtrdFee()).append("</pFTrdFee>");
		buffer.append("<pTAcctType>").append(this.getTacctType()).append("</pTAcctType>");
		buffer.append("<pTIpsAcctNo>").append(this.getTipsAcctNo()).append("</pTIpsAcctNo>");
		buffer.append("<pTTrdFee>").append(this.getTtrdFee()).append("</pTTrdFee>");
		buffer.append("</pRow>");
		return buffer.toString();
	}

	public String getOriMerBillNo() {
		return oriMerBillNo;
	}

	public void setOriMerBillNo(String oriMerBillNo) {
		this.oriMerBillNo = oriMerBillNo;
	}

	public String getTrdAmt() {
		return trdAmt;
	}

	public void setTrdAmt(String trdAmt) {
		this.trdAmt = trdAmt;
	}

	public String getFacctType() {
		return facctType;
	}

	public void setFacctType(String facctType) {
		this.facctType = facctType;
	}

	public String getFipsAcctNo() {
		return fipsAcctNo;
	}

	public void setFipsAcctNo(String fipsAcctNo) {
		this.fipsAcctNo = fipsAcctNo;
	}

	public String getFtrdFee() {
		return ftrdFee;
	}

	public void setFtrdFee(String ftrdFee) {
		this.ftrdFee = ftrdFee;
	}

	public String getTacctType() {
		return tacctType;
	}

	public void setTacctType(String tacctType) {
		this.tacctType = tacctType;
	}

	public String getTipsAcctNo() {
		return tipsAcctNo;
	}

	public void setTipsAcctNo(String tipsAcctNo) {
		this.tipsAcctNo = tipsAcctNo;
	}

	public String getTtrdFee() {
		return ttrdFee;
	}

	public void setTtrdFee(String ttrdFee) {
		this.ttrdFee = ttrdFee;
	}
	
	
}
