package com.liangyou.api.ips;

import java.util.ArrayList;
import java.util.List;

/**
 * 转账的所有业务类型（投资、代偿、代偿还款、债权转让、担保收益）
 * 均调用本接口迚行转账。
 * 提交方式：服务器点对点提交 
 * 协议类型：http/https
 * 接口类型：webservice 
 * @author wujing
 *
 */
public class Transfer extends IpsModel {
	
	public Transfer(){
		super();
		this.setUrl(this.getSubmitUrl()+"/CreditWS/Service.asmx");
	}
	
	private String merBillNo; //商户订单号
	
	private String bidNo;    //标的号 
	
	private String date;    //商户日期 格式：YYYYMMDD
	
	/**
	 * 转账类型    1：投资（报文提交关系，转出方：转入方=N：1），
	 *   2：代偿（报文提交关系，转出方：转入方=1：N），
	 *     3：代偿还款（报文提交关系，转出方：转入方=1：1），  
	 *     4：债权转让（报文提交关系，转出方：转入方=1：1）， 
	 *     5：结算担保收益（报文提交关系，转出方：转入方=1：1）
	 */
	private String transferType;  
	
	/**
	 * 转账方式，1：逐笔入账；
	 * 2：批量入账 逐笔入账：丌将转账款项汇总，而是按明细
	 * 交易一笔一笔计入账户 批量入帐：针对投资，
	 * 将明细交易按1笔汇总本金和1笔汇总手续费记入借款人
	 * 帐户 当转账类型为“1：投资”时，可选择1戒2。其余交易只能选1
	 */
	private String transferMode;  
	
	
	private String memo1;
	
	private String memo2;
	
	private String memo3;
	
	private String details;   //转账明细
	
	private List<TransferProw> pageList = new ArrayList<TransferProw>();
	
	private String[] paramNames = new String[]{"MerBillNo","BidNo","Date","TransferType","TransferMode","S2SUrl","Details",
			"Memo1","Memo2","Memo3"};
	
	

	private String[][] commitParams = new String[][]{{"pMerCode","p3DesXmlPara","pSign"},{"","",""}};  //webservice提交的参数名
	
	
	
	public Transfer response(String str,Transfer transfer) {
		
		
		XmlTool1 tool = super.response(str);
		transfer.setMerBillNo(tool.getNodeValue("pMerBillNo"));
		return transfer;
	}
	





	//创建转账detail的xml
	
	public void doDetailXml(){
		StringBuffer bu = new StringBuffer();
		for (TransferProw pr : this.pageList) {
			bu.append(pr.prowXml());
		}
		this.setDetails(bu.toString());
		
	}






	public String getMerBillNo() {
		return merBillNo;
	}






	public void setMerBillNo(String merBillNo) {
		this.merBillNo = merBillNo;
	}






	public String getBidNo() {
		return bidNo;
	}






	public void setBidNo(String bidNo) {
		this.bidNo = bidNo;
	}






	public String getDate() {
		return date;
	}






	public void setDate(String date) {
		this.date = date;
	}






	public String getTransferType() {
		return transferType;
	}






	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}






	public String getTransferMode() {
		return transferMode;
	}






	public void setTransferMode(String transferMode) {
		this.transferMode = transferMode;
	}






	public String getMemo1() {
		return memo1;
	}






	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}






	public String getMemo2() {
		return memo2;
	}






	public void setMemo2(String memo2) {
		this.memo2 = memo2;
	}






	public String getMemo3() {
		return memo3;
	}






	public void setMemo3(String memo3) {
		this.memo3 = memo3;
	}






	public String getDetails() {
		return details;
	}






	public void setDetails(String details) {
		this.details = details;
	}






	public List<TransferProw> getPageList() {
		return pageList;
	}






	public void setPageList(List<TransferProw> pageList) {
		this.pageList = pageList;
	}






	public String[] getParamNames() {
		return paramNames;
	}






	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}






	public String[][] getCommitParams() {
		return commitParams;
	}






	public void setCommitParams(String[][] commitParams) {
		this.commitParams = commitParams;
	}


	

}
