package com.liangyou.api.ips;

import java.io.Serializable;

/**
 * 环迅投标接口
 * 提交方式：服务器点对点提交
 * 协议类型：http/https
 * 接口类型：POST 
 * @author wujing
 *
 */
public class IpsTenderNBorrow extends IpsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1070348341641670662L;

	public IpsTenderNBorrow(){
		super();
	}
	/**
	 * 商户日期
	 */
	private String merDate;
	
	/**
	 * 标的号
	 */
	private String bidNo;
	
	/**
	 *合同号 
	 */
	private String contractNo;
	
	/**
	 * 登记方式    1：手劢投标  2：自劢投标 
	 */
	private String regType;
	
	/**
	 *授权号：字母和数字，如a~z,A~Z,0~9 登记方式为1时
	 *，为空 登记方式为2时，填写该投资人自劢投标签约时IPS
	 *向平台接口返回的“pIpsAuthNo授权号
	 */
	private String authNo;
	
	/**
	 * 债权面额:金额单位元，丌能为负，丌允许为0
	 */
	private String authAmt;
	
	/**
	 * 交易金额:金额单位元，丌能为负，丌允许为0 
	 * 债权面额等于交易金额
	 */
	private String trdAmt;
	
	/**
	 * 投资人手续费
	 */
	private String fee;
	
	/**
	 * 账户类型：0#机构（暂未开放）；1#个人
	 */
	private String acctType;
	
	/**
	 * 证件号码    真实身份证（个人）/由IPS颁发的商户号
	 */
	private String identNo;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 投资人账户
	 */
	private String account;
	
	/**
	 * 借款用途
	 */
	private String use;
	
	/**
	 * 浏览器返回地址 
	 */
	private String webUrl;
	
	/**
	 * 异步返回地址
	 */
	private String s2SUrl;
	
	/**
	 * 备注
	 */
	private String memo1;
	
	private String memo2;
	
	private String memo3;
	
	
	private String[] paramNames = new String[]{"MerBillNo","MerDate","BidNo","ContractNo",
											"RegType","AuthNo","AuthAmt","TrdAmt",
											"Fee","AcctType","IdentNo","RealName",
											"Account","Use","WebUrl","S2SUrl","Memo1","Memo2","Memo3"};
	
/*****************以下为回调使用参数********************/
	
	/**
	 * IPS返回的投资人编号
	 */
	private String accountDealNo;
	
	/**
	 * IPS返回的标的编号
	 */
	private String bidDealNo;
	
	/**
	 * 返回1，代表投标
	 */
	private String busiType;
	
	/**
	 * 实际冻结金额
	 */
	private String transferAmt;
	
	/**
	 * 0：新增 1：迚行中 10：结束
	 */
	private String status;
	
	/**
	 * 由IPS系统生成的唯一流水号
	 */
	private String p2PBillNo;
	
	/**
	 * IPS处理时间:yyyyMMddHHmmss
	 */
	private String ipsTime;
	
	public IpsTenderNBorrow doTenderXml(String returnXml,IpsTenderNBorrow ipsTender){
		XmlTool1 tool = new XmlTool1();
		tool.SetDocument(returnXml);
		ipsTender.setMerBillNo(tool.getNodeValue("pMerBillNo")); //交易流水号
		ipsTender.setContractNo(tool.getNodeValue("pContractNo"));  //合同号
		ipsTender.setTransferAmt(tool.getNodeValue("pTransferAmt"));  //实际冻结金额
		ipsTender.setMemo1(tool.getNodeValue("pMemo1")); //标id
		ipsTender.setMemo2(tool.getNodeValue("pMemo2"));  //用户id
//		ipsTender.setMemo3(tool.getNodeValue("pMemo3"));// 密码
		return ipsTender;
	}
	
	
	

	@Override
	public boolean checkRespCode(String code) {
		if (code.equals("MG00000F")) {
			return true;
		}else{
			return false;
		}
	}

	public String getMerDate() {
		return merDate;
	}

	public void setMerDate(String merDate) {
		this.merDate = merDate;
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

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}

	public String getAuthAmt() {
		return authAmt;
	}

	public void setAuthAmt(String authAmt) {
		this.authAmt = authAmt;
	}

	public String getTrdAmt() {
		return trdAmt;
	}

	public void setTrdAmt(String trdAmt) {
		this.trdAmt = trdAmt;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
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

	public String getAccountDealNo() {
		return accountDealNo;
	}

	public void setAccountDealNo(String accountDealNo) {
		this.accountDealNo = accountDealNo;
	}

	public String getBidDealNo() {
		return bidDealNo;
	}

	public void setBidDealNo(String bidDealNo) {
		this.bidDealNo = bidDealNo;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getTransferAmt() {
		return transferAmt;
	}

	public void setTransferAmt(String transferAmt) {
		this.transferAmt = transferAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getP2PBillNo() {
		return p2PBillNo;
	}

	public void setP2PBillNo(String p2pBillNo) {
		p2PBillNo = p2pBillNo;
	}

	public String getIpsTime() {
		return ipsTime;
	}

	public void setIpsTime(String ipsTime) {
		this.ipsTime = ipsTime;
	}
	
	

}
