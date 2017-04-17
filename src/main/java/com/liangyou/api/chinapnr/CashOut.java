package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.util.StringUtils;

public class CashOut extends ChinapnrModel {
	/**
	 * 用户取现接口,通过页面形式
	 * 
	 * */
	private static final Logger logger=Logger.getLogger(CashOut.class);
	private String usrId;
	private String transAmt;
	private String openAcctId;
	private String address = "/muser/publicRequests";
	private String testAddress = "/muser/publicRequests";	
	private String feeAmt;    //汇付取现手续费 
	private String feeCustId;
	private String feeAcctId;
	private String servFee;  //商户收取服务费金额
	private String ServFeeAcctId; //商户子账户号
	
	private String[] paramNames=new String[]{
			"version","cmdId","merCustId","ordId","usrCustId",
			"transAmt","servFee","ServFeeAcctId","openAcctId","retUtl","bgRetUrl","remark","merPriv","reqExt","chkValue"
	};

	public CashOut() {
		super();
		
	}
	public CashOut(String usrCustId) {
		super();
		this.setUsrCustId(usrCustId);
		this.setCmdId("Cash");
	}
	
	public CashOut(long usrId) {
	
		this.usrId=getMerId()+usrId;
		
	}
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{
		
		StringBuffer MerData =super.getMerData();
		MerData.append(StringUtils.isNull(getOrdId()))
				.append(StringUtils.isNull(getUsrCustId()))
				.append(StringUtils.isNull(getTransAmt()))
				.append(StringUtils.isNull(getServFee()))
				.append(StringUtils.isNull(getServFeeAcctId()))
				.append(getRetUrl())
				.append(StringUtils.isNull(getBgRetUrl()))
				.append(getMerPriv());
		return MerData;
	}

	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer merData = new StringBuffer();
					 try {
						merData.append(StringUtils.isNull(getCmdId()))
						 		.append(StringUtils.isNull(getRespCode()))
						 		.append(StringUtils.isNull(getMerCustId()))
						 		.append(StringUtils.isNull(getOrdId()))
						 		.append(StringUtils.isNull(getUsrCustId()))
						 		.append(StringUtils.isNull(getTransAmt()))
						 		.append(StringUtils.isNull(getOpenAcctId()))
						 		.append(StringUtils.isNull(getOpenBankId()))
						 		.append(StringUtils.isNull(getFeeAmt()))
						 		.append(StringUtils.isNull(getFeeCustId()))
						 		.append(StringUtils.isNull(getFeeAcctId()))
						 		.append(StringUtils.isNull(getServFee()))
						 		.append(StringUtils.isNull(getServFeeAcctId()))
						 		.append(StringUtils.isNull(getRetUrl()))
						 		.append(StringUtils.isNull(getBgRetUrl()))
						 		.append(StringUtils.isNull(getMerPriv()))
						        .append(StringUtils.isNull(getReqExt()));
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
					// logger.info("用户取现回调参数拼接"+merData.toString());
		return merData;
	}
	
	//用户取现验签操作
	public int callback(){
		logger.info("进入用户取现验签回调验证………………");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink( ) ;
		String retData = doCallbackURLDecoder(this.getCallbackMerData());
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+retData);
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile , retData, getChkValue());
		return ret;
	}
	public String getUsrId() {
		return usrId;
	}

	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public String getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
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

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getOpenAcctId() {
		return openAcctId;
	}

	public void setOpenAcctId(String openAcctId) {
		this.openAcctId = openAcctId;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getServFee() {
		return servFee;
	}
	public void setServFee(String servFee) {
		this.servFee = servFee;
	}
	public String getServFeeAcctId() {
		return ServFeeAcctId;
	}
	public void setServFeeAcctId(String servFeeAcctId) {
		ServFeeAcctId = servFeeAcctId;
	}
	public String getFeeCustId() {
		return feeCustId;
	}
	public void setFeeCustId(String feeCustId) {
		this.feeCustId = feeCustId;
	}
	public String getFeeAcctId() {
		return feeAcctId;
	}
	public void setFeeAcctId(String feeAcctId) {
		this.feeAcctId = feeAcctId;
	}
	
}
