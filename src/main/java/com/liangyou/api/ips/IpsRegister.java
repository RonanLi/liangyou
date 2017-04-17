package com.liangyou.api.ips;

/**
 * 注册：提交方式：服务器点对点提交 
 * 协议类型：http/https 
 * 接口类型：POST
 * @author wujing
 *
 */
public class IpsRegister extends IpsModel {
	private String  merBillNo;  //商户开户流水号
	 
	private String identType;  // 证件类型:1#身份证，默讣：1 
	
	private String identNo;  //  证件号码
	
	private String realName;  //  姓名
	
	private String mobileNo;  //  手机号
	
	private String email;  //   注册邮箱
	
	private String smDate;  //  提交日期yyyyMMdd
	
	private String webUrl;  //  状态返回地址 :同步
	
	private String s2SUrl;  //状态返回地址 ：异步
	
	private String memo1;  // 备注
	
	private String memo2;  //
	
	private String memo3;  //
	
	private String[] paramNames = new String[]{"MerBillNo","IdentType","IdentNo","RealName","MobileNo","Email","SmDate","WebUrl","S2SUrl","Memo1","Memo2","Memo3"};

	/**回调参数***/
	private String status;  //开户状态
	
	private String bankName;  //银行名称 
	
	private String bkAccName; // 户名
	
	private String bkAccNo;  //银行卡账号
	
	private String cardStatus;  //身份证状态
	
	private String phStatus; //手机状态
	
	private String ipsAcctNo;  //IPS托管平台账 户号
	
	private String ipsAcctDate;  //开户日期
	
	public IpsRegister(){
		super();
		this.setUrl(super.getSubmitUrl()+"/CreditWeb/CreateNewIpsAcct.aspx");
	}
	
	public boolean checkRegister(String code){
		if ("MG00000F".equals(code)) {
			return true;
		}else{
			return false;
		}
	}
	
	public IpsRegister doReturnCreate(String str){
		IpsRegister cre = new IpsRegister();
		XmlTool1 tool = new XmlTool1();
		tool.SetDocument(str);
		cre.setStatus(tool.getNodeValue("pStatus"));
		cre.setBankName(tool.getNodeValue("pBankName"));
		cre.setBkAccName(tool.getNodeValue("pBkAccName"));
		cre.setBkAccNo(tool.getNodeValue("pBkAccNo"));
		cre.setCardStatus(tool.getNodeValue("pCardStatus"));
		cre.setPhStatus(tool.getNodeValue("pPhStatus"));
		cre.setIpsAcctNo(tool.getNodeValue("pIpsAcctNo"));
		cre.setMemo1(tool.getNodeValue("pMemo1"));
		return cre;
	}
	
	
	public String getMerBillNo() {
		return merBillNo;
	}
	public void setMerBillNo(String merBillNo) {
		this.merBillNo = merBillNo;
	}
	public String getIdentType() {
		return identType;
	}
	public void setIdentType(String identType) {
		this.identType = identType;
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
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSmDate() {
		return smDate;
	}
	public void setSmDate(String smDate) {
		this.smDate = smDate;
	}
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public String getS2SUrl() {
		return s2SUrl;
	}
	public void setS2SUrl(String s2sUrl) {
		s2SUrl = s2sUrl;
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
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBkAccName() {
		return bkAccName;
	}
	public void setBkAccName(String bkAccName) {
		this.bkAccName = bkAccName;
	}
	public String getBkAccNo() {
		return bkAccNo;
	}
	public void setBkAccNo(String bkAccNo) {
		this.bkAccNo = bkAccNo;
	}
	public String getCardStatus() {
		return cardStatus;
	}
	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}
	public String getPhStatus() {
		return phStatus;
	}
	public void setPhStatus(String phStatus) {
		this.phStatus = phStatus;
	}
	public String getIpsAcctNo() {
		return ipsAcctNo;
	}
	public void setIpsAcctNo(String ipsAcctNo) {
		this.ipsAcctNo = ipsAcctNo;
	}
	public String getIpsAcctDate() {
		return ipsAcctDate;
	}
	public void setIpsAcctDate(String ipsAcctDate) {
		this.ipsAcctDate = ipsAcctDate;
	}
	
}
