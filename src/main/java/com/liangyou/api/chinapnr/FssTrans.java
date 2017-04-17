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

/**
 * 
 * 生利宝接口 TGPROJECT-314 lx 2014-05-30 add
 * 
 */
public class FssTrans extends ChinapnrModel {
	private static final Logger logger = Logger.getLogger(FssTrans.class);

	private String transType;//I：转入生利宝 O：转出生利宝
	private String transAmt;//转入金额
	private String respExt;
	
	public FssTrans(){
	}
	
	public FssTrans(String usrCustId){
		super();
		this.setCmdId("FssTrans");
		this.setUsrCustId(usrCustId);
		this.setRetUrl(Global.getString("weburl")+"/public/chinapnr/interestGenerateReturn.html");
		this.setBgRetUrl(Global.getString("weburl")+"/public/chinapnr/interestGenerateNotify.html");
	}

	private String[] paramNames = new String[] { "version", "cmdId",
			"merCustId", "usrCustId", "ordId", "ordDate", "retUrl", "bgRetUrl",
			"merPriv", "reqExt", "chkValue" };

	public StringBuffer getMerData() throws UnsupportedEncodingException {
		StringBuffer MerData = super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))
				.append(StringUtils.isNull(getOrdId()))
				.append(StringUtils.isNull(getOrdDate()))
				.append(StringUtils.isNull(getRetUrl()))
				.append(StringUtils.isNull(getBgRetUrl()))
				.append(StringUtils.isNull(getMerPriv()))
				.append(StringUtils.isNull(getReqExt()));
		return MerData;
	}
	
	@Override
	public int callback(){
		logger.info("进入用户投标回调验证");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink( ) ;
		String retData = doCallbackURLDecoder(this.getCallbackMerData());
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+retData);
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile ,retData, getChkValue());
		logger.info("操作生利宝ret"+ret);
		return ret;
	}

	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer merData = new StringBuffer();
		try {
			merData.append(StringUtils.isNull(getCmdId()))
					.append(StringUtils.isNull(getRespCode()))
					.append(StringUtils.isNull(getMerCustId()))
					.append(StringUtils.isNull(getUsrCustId()))
					.append(StringUtils.isNull(getOrdId()))
					.append(StringUtils.isNull(getOrdDate()))
					.append(StringUtils.isNull(getTransType()))
					.append(StringUtils.isNull(getTransAmt()))
					.append(StringUtils.isNull(getRetUrl()))
					.append(StringUtils.isNull(getBgRetUrl()))
					.append(StringUtils.isNull(getMerPriv()))
					.append(StringUtils.isNull(getRespExt()));
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return merData;
	}

	@Override
	public ChinapnrModel response(String res) throws IOException {
		super.response(res);
		try {
			JSONObject json = JSON.parseObject(res);
			this.setOrdId(json.getString("OrdId"));
			this.setTransAmt(json.getString("TransAmt"));
			this.setOpenBankId(json.getString("TransType"));
			this.setUsrCustId(json.getString("UsrCustId"));
			this.setMerPriv(json.getString("MerPriv"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getRespExt() {
		return respExt;
	}

	public void setRespExt(String respExt) {
		this.respExt = respExt;
	}

}
