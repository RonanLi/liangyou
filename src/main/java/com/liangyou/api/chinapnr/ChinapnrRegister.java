package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class ChinapnrRegister extends ChinapnrModel{
	/**
	 * 用户开户，现在统一使用页面开户模式
	 * */
	private static final Logger logger=Logger.getLogger(ChinapnrRegister.class);
	private String merUsrId;
	//证件号码
	private String idNo;
	private String idType;       //2.0定长 ：身份证'00'
	private String certId;
	private String certType;
	private String usrName;
	private String loginPwd;         //用户登录密码
	private String transPwd;         //用户支付密码	
	private String usrMp;                  //用户手机号
	private String usrId;
	private String usrEmail;
	private String charSet;
	private String address="/muser/publicRequests";
	private String testAddress="/muser/publicRequests";
	private String MerPrk;

	public ChinapnrRegister() {
		super();
		this.setCmdId("UserRegister");
		this.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/userRegisterReturn.html");
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/userRegisterNotify.html");
	
	}

		private String [] paramNames=new String []{
			"version","cmdId","merCustId","bgRetUrl","retUrl","usrId"
			,"usrName","idType","idNo","usrMp","usrEmail","merPriv","chkValue"
	};
	

	
	public StringBuffer getMerData() throws UnsupportedEncodingException {
		StringBuffer MerData = super.getMerData();
		//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
		MerData.append(StringUtils.isNull(this.getBgRetUrl()))
		.append(StringUtils.isNull(getRetUrl()))
		.append(StringUtils.isNull(getUsrId()))
		.append(StringUtils.isNull(getUsrName()))
		.append(StringUtils.isNull(getIdType()))
		.append(StringUtils.isNull(getIdNo()))
		.append(StringUtils.isNull(getUsrMp()))
		.append(StringUtils.isNull(getUsrEmail()))
		.append(StringUtils.isNull(getMerPriv()));
		//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 end 
		return MerData;
	}
	@Override
	public ChinapnrRegister response(String res) throws IOException {
	logger.info(res);
	    try {
	    	JSONObject json= JSON.parseObject(res);
	    	setCmdId(json.get("CmdId").toString());
	    	setRespCode(json.get("RespCode").toString());
	    	setRespDesc(json.getString("RespDesc"));
	    	setMerCustId(json.get("MerCustId").toString());
	    	setUsrCustId(json.get("UsrCustId").toString());
	    	setUsrId(json.getString("UsrId"));
	    	setMerPriv(json.getString("MerPrk"));
		} catch (Exception e) {
			logger.info("开通汇付账号获取回调参数json转换出错"+e.getMessage());
		}
		return null;
	}
	
	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer merData = new StringBuffer();
					 try {
						merData.append(StringUtils.isNull(getCmdId()))
						 		.append(StringUtils.isNull(getRespCode()))
						 		.append(StringUtils.isNull(getMerCustId()))
						 		.append(StringUtils.isNull(getUsrId()))
						 		.append(StringUtils.isNull(getUsrCustId()))
						 		.append(StringUtils.isNull(getBgRetUrl()))
						 		.append(StringUtils.isNull(getTrxId()))
						 		.append(StringUtils.isNull(getRetUrl()))
						 		.append(StringUtils.isNull(getMerPriv()));
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
					 //logger.info("用户开户回调参数拼接"+merData.toString());
		return merData;
	}
	//用户开户验签操作
	public int callback(){
		logger.info("进入用户开户验签回调验证………………");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink() ;
		String retData = doCallbackURLDecoder(this.getCallbackMerData());
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+retData);
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile , retData, getChkValue());
		return ret;
	}
	
	public String getMerUsrId() {
		return merUsrId;
	}
	public void setMerUsrId(String merUsrId) {
		this.merUsrId = merUsrId;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getUsrName() {
		return usrName;
	}
	public void setUsrName(String usrName) {
		this.usrName = usrName;
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
	public String getUsrId() {
		return usrId;
	}
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getTransPwd() {
		return transPwd;
	}

	public void setTransPwd(String transPwd) {
		this.transPwd = transPwd;
	}

	public String getUsrMp() {
		return usrMp;
	}

	public void setUsrMp(String usrMp) {
		this.usrMp = usrMp;
	}

	public String getUsrEmail() {
		return usrEmail;
	}

	public void setUsrEmail(String usrEmail) {
		this.usrEmail = usrEmail;
	}

	public String getMerPrk() {
		return MerPrk;
	}

	public void setMerPrk(String merPrk) {
		MerPrk = merPrk;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	
	
}
