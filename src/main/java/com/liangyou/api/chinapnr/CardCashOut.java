package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class CardCashOut extends ChinapnrModel{
	private static final Logger logger=Logger.getLogger(CardCashOut.class);
	/**
	 * 
	 * 后台绑卡设置在2.0中取消，只能用页面方式绑卡
	 * */
	
	private String openAcctId;         //开户银行账户号
	private String openAcctName;         //  无此参数
	private String openBankId;        //开户银行代号
	private String openProvId;        //开户银行省份
	private String openAreaId;        //开户银行地区
	private String openBranchName;    //开户支行名称
	private String isDefault;         //是否默认
	private String charSet;
	
	private String address="/muser/publicRequests";
	private String testAddress="/muser/publicRequests";
	
	public CardCashOut() {
		
	}

	public CardCashOut(String usrCusId) {
		super();
		this.setUsrCustId(usrCusId);
		this.setCmdId("UserBindCard");
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/cardCashOutNotify.html");
		isDefault="Y";
	}

	
	//页面绑定银行卡方式
	private String[] paramNames=new String[] {
			"version","cmdId","merCustId","usrCustId","bgRetUrl","merPriv"
			};
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{
		StringBuffer MerData = super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))
				.append(getBgRetUrl())
				.append(getMerPriv());
		return MerData;
	}

	//用户绑卡参数回调处理
	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer merData = new StringBuffer();
					 try {
						merData.append(StringUtils.isNull(getCmdId()))
						 		.append(StringUtils.isNull(getRespCode()))
						 		.append(StringUtils.isNull(getMerCustId()))
						 		.append(StringUtils.isNull(getOpenAcctId()))
						 		.append(StringUtils.isNull(getOpenBankId()))
						 		.append(StringUtils.isNull(getUsrCustId()))
						 		.append(StringUtils.isNull(getTrxId()))
						 		.append(StringUtils.isNull(getBgRetUrl()))
						 		.append(StringUtils.isNull(getMerPriv()));
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
					// logger.info("用户绑卡回调参数拼接"+merData.toString());
		return merData;
	}


	
	public int callback(){
		logger.info("进入用户绑卡回调验证");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink() ;
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+doCallbackURLDecoder(this.getCallbackMerData()));
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile , doCallbackURLDecoder(getCallbackMerData()), getChkValue());
		return ret;
	}

	public String getOpenAcctId() {
		return openAcctId;
	}

	public void setOpenAcctId(String openAcctId) {
		this.openAcctId = openAcctId;
	}

	public String getOpenAcctName() {
		return openAcctName;
	}

	public void setOpenAcctName(String openAcctName) {
		this.openAcctName = openAcctName;
	}

	public String getOpenBankId() {
		return openBankId;
	}

	public void setOpenBankId(String openBankId) {
		this.openBankId = openBankId;
	}

	public String getOpenProvId() {
		return openProvId;
	}

	public void setOpenProvId(String openProvId) {
		this.openProvId = openProvId;
	}

	public String getOpenAreaId() {
		return openAreaId;
	}

	public void setOpenAreaId(String openAreaId) {
		this.openAreaId = openAreaId;
	}

	public String getOpenBranchName() {
		return openBranchName;
	}

	public void setOpenBranchName(String openBranchName) {
		this.openBranchName = openBranchName;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTestAddress() {
		return testAddress;
	}

	public void setTestAddress(String testAddress) {
		this.testAddress = testAddress;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
}
