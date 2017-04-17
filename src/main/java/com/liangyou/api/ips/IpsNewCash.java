package com.liangyou.api.ips;

import java.io.Serializable;

/**
 * 取现
 * 提交方式：浏览器跳转POST
 * 协议类型：http/https 
 * 接口类型：POST 
 * @author huangjun
 *
 */
public class IpsNewCash extends IpsModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 0#机构（暂未开放） ；1#个人 
	 * 默认 1
	 */
	private String acctType = "1";
	
	/**
	 * 1#普通提现；2#定向提现<暂未开放> 
	 */
	private String outType = "1";
	
	/**
	 * 提现模式为 2 时，此字段生效,内容是投标时的标号
	 */
	private String bidNo;
	
	/**
	 * 提现模式为 2 时，此字段生效,内容是投标时的合同号
	 */
	private String contractNo;
	
	/**
	 * 提现模式为 2 时，此字段生效,上送 IPS 托管账户号（个人/商户号）
	 */
	private String dwTo;
	
	/**
	 * 真实身份证（个人）/由 IPS 颁发的商户号（商户）
	 */
	private String identNo;
	
	/**
	 * 真实姓名（中文） 
	 */
	private String realName;
	
	/**
	 * 账户类型为 1 时，IPS 个人托管账户号 
	 * 账户类型为 0 时，由 IPS 颁发的商户号
	 */
	private String ipsAcctNo;
	
	/**
	 * 格式：YYYYMMDD 
	 */
	private String dwDate;
	
	/**
	 * 金额单位，不能为负，不允许为 0 
	 */
	private String trdAmt;
	
	/**
	 * 金额单位，不能为负，允许为 0,这里是平台向用户收取的费用 
	 */
	private String merFee;
	
	/**
	 * 由 IPS 系统生成的唯一流水号 
	 */
	private String ipsBillNo;

	public String getIpsBillNo() {
		return ipsBillNo;
	}

	public void setIpsBillNo(String ipsBillNo) {
		this.ipsBillNo = ipsBillNo;
	}

	/**
	 * 这里是 IPS 收取的费用 
	 * 1：平台支付 
	 * 2：提现方支付
	 */
	private String ipsFeeType;
	
	public IpsNewCash(){
		super();
	}

	/**
     * 请求参数列表
     */
    private String[] paramNames=new String[]{"MerBillNo","AcctType","OutType","BidNo","ContractNo","DwTo",
    		"IdentNo","RealName","IpsAcctNo","DwDate","TrdAmt","MerFee","IpsFeeType","WebUrl","S2SUrl","Memo1","Memo2","Memo3"};
    
    public IpsNewCash doCashCreate(String str){
    	IpsNewCash ipsCash = new IpsNewCash();
		XmlTool1 tool = new XmlTool1();
		tool.SetDocument(str);
		ipsCash.setMerBillNo(tool.getNodeValue("pMerBillNo"));
		ipsCash.setAcctType(tool.getNodeValue("pAcctType"));
		ipsCash.setIdentNo(tool.getNodeValue("pIdentNo"));
		ipsCash.setRealName(tool.getNodeValue("pRealName"));
		ipsCash.setIpsAcctNo(tool.getNodeValue("pIpsAcctNo"));
		ipsCash.setDwDate(tool.getNodeValue("pDwDate"));
		ipsCash.setTrdAmt(tool.getNodeValue("pTrdAmt"));
		ipsCash.setIpsBillNo(tool.getNodeValue("pIpsBillNo"));
		ipsCash.setMemo1(tool.getNodeValue("pMemo1"));
		ipsCash.setMemo2(tool.getNodeValue("pMemo2"));
		ipsCash.setMemo3(tool.getNodeValue("pMemo3"));
		return ipsCash;
	}
    
	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getDwTo() {
		return dwTo;
	}

	public void setDwTo(String dwTo) {
		this.dwTo = dwTo;
	}

	public String getDwDate() {
		return dwDate;
	}

	public void setDwDate(String dwDate) {
		this.dwDate = dwDate;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getOutType() {
		return outType;
	}

	public void setOutType(String outType) {
		this.outType = outType;
	}

	public String getBidNo() {
		return bidNo;
	}

	public void setBidNo(String bidNo) {
		this.bidNo = bidNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getIdentNo() {
		return identNo;
	}

	public void setIdentNo(String identNo) {
		this.identNo = identNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIpsAcctNo() {
		return ipsAcctNo;
	}

	public void setIpsAcctNo(String ipsAcctNo) {
		this.ipsAcctNo = ipsAcctNo;
	}

	public String getTrdAmt() {
		return trdAmt;
	}

	public void setTrdAmt(String trdAmt) {
		this.trdAmt = trdAmt;
	}

	public String getMerFee() {
		return merFee;
	}

	public void setMerFee(String merFee) {
		this.merFee = merFee;
	}

	public String getIpsFeeType() {
		return ipsFeeType;
	}

	public void setIpsFeeType(String ipsFeeType) {
		this.ipsFeeType = ipsFeeType;
	}
	
	
}
