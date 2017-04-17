package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;

public class Loans extends ChinapnrModel {
	/**
	 * 自动扣款，放款接口
	 * */
		private String outCustId;        //出账客户号
		private String fee;           //  
		private String subOrdId;       //   投标订单流水
		private String subOrdDate;       // 投标订单日期
		private String inCustId;        //    入账客户号
		private String isDefault;      //      是否默认
		private String address = "/muser/publicRequests";
		private String testAddress = "/muser/publicRequests";
		public Loans(String outCustId,String inCustId,String transAmt,String ordId,String ordDate,String subOrdId,String subOrdDate,String fee){
			super();
			this.setCmdId("Loans");
			setOutCustId(outCustId);
			setInCustId(inCustId);
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
				"ordDate","outCustId","transAmt","fee",
				"subOrdId","subOrdDate","inCustId","divDetails","isDefault","bgRetUrl","chkValue"
				};
		
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData = super.getMerData();
			MerData.append(getOrdId())
			.append(getOrdDate())
			.append(getOutCustId())
			.append(getTransAmt())
			.append(getFee())
			.append(getSubOrdId())
			.append(getSubOrdDate())
			.append(getInCustId())
			.append(getDivDetails())
			.append(getIsDefault())
			.append(getBgRetUrl());
			return MerData;
		}
		
		@Override
		public ChinapnrModel response(String res) throws IOException {
			super.response(res);
			try {
				JSONObject json= JSON.parseObject(res);
				this.setTransAmt(json.getString("TransAmt"));
				this.setOutCustId(json.getString("OutCustId"));
				this.setInCustId(json.getString("InCustId"));
				this.setOrdId(json.getString("OrdId"));
				this.setOrdDate(json.getString("OrdDate"));
				this.setSubOrdId(json.getString("SubOrdId"));
				this.setSubOrdDate(json.getString("SubOrdDate"));
				this.setFee(json.getString("Fee"));
			} catch (Exception e) {
				// TODO: handle exception
			}
			return super.response(res);
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
		public String getFee() {
			return fee;
		}
		public String getOutCustId() {
			return outCustId;
		}
		public void setOutCustId(String outCustId) {
			this.outCustId = outCustId;
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
}
