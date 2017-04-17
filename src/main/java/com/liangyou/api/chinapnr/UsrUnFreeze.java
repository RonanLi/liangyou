package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class UsrUnFreeze extends ChinapnrModel {
	/**
	 * 后台解冻冻结资金接口
	 * */
	private String usrId;
	private String trxId;       //日期加流水账户

	private String address = "/muser/publicRequests";
	private String testAddress = "/muser/publicRequests";

	private String[] paramNames = new String[] {"version","cmdId","merCustId",
			"ordId","ordDate","trxId","bgRetUrl","chkValue"};
	
	public UsrUnFreeze() {
		this("","");
	}
	
	public UsrUnFreeze(String usrId,String transAmt) {
		this(usrId,transAmt,"","");
	}
	
	public UsrUnFreeze(String usrId,String date,String ordId,String trxId) {
		super();
		this.setOrdDate(date);
		this.setOrdId(ordId);
		this.setCmdId("UsrUnFreeze");
		this.setTrxId(trxId);
		
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/chinapnrBgRet.html");
	}

	public StringBuffer getMerData() throws UnsupportedEncodingException{

		StringBuffer MerData =super.getMerData();
		MerData.append(StringUtils.isNull(getOrdId()))
				.append(StringUtils.isNull(getOrdDate()))
				.append(StringUtils.isNull(getTrxId()))				
				.append(StringUtils.isNull(getBgRetUrl()));
		return MerData;
	}
	
	@Override
	public ChinapnrModel response(String res) throws IOException {
		super.response(res);
		try {
			JSONObject json= JSON.parseObject(res);
			this.setTrxId(json.getString("TrxId"));
			this.setOrdId(json.getString("OrdId"));
			this.setOrdDate(json.getString("OrdDate"));
			this.setRespDesc(json.getString("RespDesc"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return super.response(res);
	}

	public String getUsrId() {
		return usrId;
	}

	public void setUsrId(String usrId) {
		this.usrId = usrId;
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

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

}
