package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.net.URLDecoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.util.StringUtils;

/**
 * 企业开户状态查询
 * v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-24
 */
public class CorpRegisterQuery extends ChinapnrModel {

	private String busiCode;
	
	private String[] paramNames = new String[]{"version","cmdId",
	         "merCustId","busiCode","chkValue"};
	
	//返回参数
	private String auditStat;
	private String usrId;
	
	public CorpRegisterQuery(){
		super();
		this.setCmdId("CorpRegisterQuery");
	}
	
	public StringBuffer getMerData() {
		StringBuffer MerData = new StringBuffer();
		MerData.append(StringUtils.isNull(getVersion()))
				.append(StringUtils.isNull(getCmdId()))
				.append(StringUtils.isNull(getMerCustId()))
				.append(StringUtils.isNull(getBusiCode()));
		return MerData;
	}
	 @Override
	 public ChinapnrModel response(String res) throws IOException{
		 try {
			JSONObject json= JSON.parseObject(res);
			this.setRespCode(json.getString("CmdId"));
			this.setRespDesc(StringUtils.isNull(URLDecoder.decode(StringUtils.isNull(json.getString("RespDesc")), "utf-8")));
			this.setAvlBal(json.getString("MerCustId"));
			this.setFrzBal(json.getString("UsrCustId"));
			this.setUsrId(json.getString("UsrId"));
			this.setAuditStat(json.getString("AuditStat"));
			this.setBusiCode(json.getString("BusiCode"));
		 }catch  (Exception e){
			 
		 }
		 return null;
	 }
	
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getBusiCode() {
		return busiCode;
	}
	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}
	public String getAuditStat() {
		return auditStat;
	}
	public void setAuditStat(String auditStat) {
		this.auditStat = auditStat;
	}
	public String getUsrId() {
		return usrId;
	}
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
}
