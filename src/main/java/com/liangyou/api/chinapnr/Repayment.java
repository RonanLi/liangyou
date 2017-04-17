package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;

public class Repayment extends ChinapnrModel {
	/**
	 * 自动扣款(还款)
	 * */
	private String outCustId;        //出账客户号
	private String fee;           //  利率
	private String subOrdId;       //   投标订单流水
	private String subOrdDate;       // 投标订单日期
	private String inCustId;        //    入账客户号
	private String isDefault;      //      是否默认

	
	public Repayment(){
		
	}
	public Repayment(String outCustId,String inCustId,String transAmt,String ordId,String ordDate,String subOrdId,String subOrdDate,String fee){
		super();
		this.setCmdId("Repayment");
		this.setOutCustId(outCustId);
		this.setInCustId(inCustId);
		this.setTransAmt(transAmt);
		this.setOrdId(ordId);
		this.setOrdDate(ordDate);
		this.setSubOrdId(subOrdId);
		this.setSubOrdDate(subOrdDate);
		this.setFee(fee);
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/loanAndrepay.html");
	}
	
	private String[] paramNames=new String[] {
			"version","cmdId","merCustId","ordId",
			"ordDate","outCustId","subOrdId","subOrdDate",
			"transAmt","fee","inCustId","inAcctId","divDetails","bgRetUrl","chkValue"
			};
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{
		StringBuffer MerData = super.getMerData();
		MerData.append(getOrdId())
		.append(getOrdDate())
		.append(getOutCustId())
		.append(getSubOrdId())
		.append(getSubOrdDate())
		.append(getTransAmt())
		.append(getFee())
		.append(getInCustId())
		.append(getInAcctId())
		.append(getDivDetails())
		.append(getBgRetUrl());
		return MerData;
	}
	
	
	
	@Override
	public ChinapnrModel response(String res) throws IOException {

		super.response(res);
		try {
			JSONObject json= JSON.parseObject(res);
			this.setOrdId(json.getString("OrdId"));
			this.setOrdDate(json.getString("OrdDate"));
			this.setOutAcctId(json.getString("OutAcctId"));
			this.setInAcctId(json.getString("InAcctId"));
			this.setFee(json.getString("Fee"));
			this.setInCustId(json.getString("InCustId"));
			this.setOutCustId(json.getString("OutCustId"));
			this.setSubOrdId(json.getString("SubOrdId"));
			this.setSubOrdDate(json.getString("SubOrdDate"));
			this.setTransAmt(json.getString("TransAmt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.response(res);
	}

	public String getOutCustId() {
		return outCustId;
	}
	public void setOutCustId(String outCustId) {
		this.outCustId = outCustId;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getSubOrdId() {
		return subOrdId;
	}
	public void setSubOrdId(String subOrdId) {
		this.subOrdId = subOrdId;
	}
	public String getSubOrdDate() {
		return subOrdDate;
	}
	public void setSubOrdDate(String subOrdDate) {
		this.subOrdDate = subOrdDate;
	}
	public String getInCustId() {
		return inCustId;
	}
	public void setInCustId(String inCustId) {
		this.inCustId = inCustId;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
	
	
}
