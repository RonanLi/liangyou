package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class UsrFreezeBg extends ChinapnrModel {
	/**
	 * 后台冻结资金接口
	 * */
	private String usrId;     
	
	private String address = "/muser/publicRequests";
	private String testAddress = "/muser/publicRequests";

	private String[] paramNames = new String[] { 
			"version", "cmdId", "merCustId",
			"usrCustId", "ordId","ordDate",
			"transAmt","bgRetUrl","chkValue"};
	public UsrFreezeBg() {
		this("","");
	}
	
	public UsrFreezeBg(String usrId,String transAmt) {
		this(usrId,transAmt,"","");
	}
	
	public UsrFreezeBg(String usrId,String transAmt,String ordId,String date) {
		super();
		this.setUsrCustId(usrId);
		this.setTransAmt(transAmt);
		this.setOrdId(ordId);
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/chinapnrBgRet.html");
		this.setCmdId("UsrFreezeBg");
		this.setOrdDate(date);
		
	}
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{

		StringBuffer MerData =super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))	
				.append(StringUtils.isNull(getOrdId()))
				.append(StringUtils.isNull(getOrdDate()))
				.append(StringUtils.isNull(getTransAmt()))
				.append(StringUtils.isNull(getBgRetUrl()));
		return MerData;
	}



	@Override
	public ChinapnrModel response(String res) throws IOException {
		super.response(res);
		try {
			JSONObject json= JSON.parseObject(res);
			this.setTransAmt(json.getString("TransAmt"));
			this.setTrxId(json.getString("TrxId"));	 //此参数必须要，在解冻时根据trxId来解冻
			this.setOrdId(json.getString("OrdId"));
			this.setUsrCustId(json.getString("UsrCustId"));
			this.setOrdDate(json.getString("OrdDate"));
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

}
