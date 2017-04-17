package com.liangyou.api.ips;

import java.util.List;

/**
 * 环迅还款接口
 * 提交方式：浏览器跳转POST 
 * 协议类型：http/https
 * 接口类型：POST 
 * @author wujing
 *
 */
public class IpsRepaymentNewTrade extends IpsModel {
	
	public IpsRepaymentNewTrade(){
		super();
		this.setUrl(this.getSubmitUrl()+"/CreditWeb/RepaymentNewTrade.aspx");
	}
	
	private String[] paramNames = new String[]{"BidNo","RepaymentDate","MerBillNo","RepayType","IpsAuthNo",
			"OutAcctNo","OutAmt","OutFee","WebUrl",
			"S2SUrl","Details","Memo1","Memo2","Memo3"};
	
	private String bidNo;
	
	private String repaymentDate;
	
	private String merBillNo;
	
	private String repayType;
	
	private String ipsAuthNo;
	
	private String outAcctNo;
	
	private String outAmt;
	
	private String outFee;
	
	private String details;
	
//	private List<RepaymentDetail> detailList;
	
	
	/**
	 * 将还款人详情信息转换成detailxml的格式
	 * @param detailList
	 */
	public void doDetaisXml(List<RepaymentDetail> detailList){
		StringBuffer buffer = new StringBuffer();
		for (RepaymentDetail item : detailList) {
			buffer.append(item.doRepaymentDetailXml());
		}
		this.setDetails(buffer.toString());
	}
	
	public IpsRepaymentNewTrade doReturnRepayment(IpsRepaymentNewTrade ipsrepayment ,String xml){
		XmlTool1 tool = super.response(xml);
		ipsrepayment.setBidNo(tool.getNodeValue("pBidNo"));
		ipsrepayment.setRepaymentDate(tool.getNodeValue("pRepaymentDate"));
		ipsrepayment.setMerBillNo(tool.getNodeValue("pMerBillNo"));
		ipsrepayment.setOutAcctNo(tool.getNodeValue("pOutAcctNo"));
		ipsrepayment.setOutAmt(tool.getNodeValue("pOutAmt"));
		ipsrepayment.setOutFee(tool.getNodeValue("pOutFee"));
		ipsrepayment.setMemo1(tool.getNodeValue("pMemo1"));
		return ipsrepayment;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getBidNo() {
		return bidNo;
	}

	public void setBidNo(String bidNo) {
		this.bidNo = bidNo;
	}

	public String getRepaymentDate() {
		return repaymentDate;
	}

	public void setRepaymentDate(String repaymentDate) {
		this.repaymentDate = repaymentDate;
	}

	public String getMerBillNo() {
		return merBillNo;
	}

	public void setMerBillNo(String merBillNo) {
		this.merBillNo = merBillNo;
	}

	public String getRepayType() {
		return repayType;
	}

	public void setRepayType(String repayType) {
		this.repayType = repayType;
	}

	public String getIpsAuthNo() {
		return ipsAuthNo;
	}

	public void setIpsAuthNo(String ipsAuthNo) {
		this.ipsAuthNo = ipsAuthNo;
	}

	public String getOutAcctNo() {
		return outAcctNo;
	}

	public void setOutAcctNo(String outAcctNo) {
		this.outAcctNo = outAcctNo;
	}

	public String getOutAmt() {
		return outAmt;
	}

	public void setOutAmt(String outAmt) {
		this.outAmt = outAmt;
	}

	public String getOutFee() {
		return outFee;
	}

	public void setOutFee(String outFee) {
		this.outFee = outFee;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

//	public List<RepaymentDetail> getDetailList() {
//		return detailList;
//	}
//
//	public void setDetailList(List<RepaymentDetail> detailList) {
//		this.detailList = detailList;
//	}
	
	
	
	

}
