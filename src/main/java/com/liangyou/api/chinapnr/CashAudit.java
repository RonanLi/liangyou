package com.liangyou.api.chinapnr;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;

public class CashAudit extends ChinapnrModel {
		/**
		 * 商户审核用户取现复核接口
		 * 
		 */
		private String auditFlag;  //取现复核标识：R：拒绝  ；S：复核通过 
		private String address = "/muser/publicRequests";
		private String testAddress = "/muser/publicRequests";	
		public CashAudit(String ordId,String usrCustId,String transAmt){
			super();
			this.setCmdId("CashAudit");
			this.setOrdId(ordId);
			this.setUsrCustId(usrCustId);
			this.setTransAmt(transAmt);
			this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/chinapnrBgRet.html");
		}
		
		private String[] paramNames=new String[] {
				"version","cmdId","merCustId","ordId","usrCustId",
				"transAmt","auditFlag","bgRetUrl","merPriv","chkValue"
		};
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData = super.getMerData();
						 MerData.append(getOrdId())
						 .append(getUsrCustId())
						 .append(getTransAmt())
						 .append(getAuditFlag())
						 .append(getBgRetUrl())
						 .append(getMerPriv());
			return MerData;
		}
		@Override
		public ChinapnrModel response(String res) throws IOException {
			super.response(res);
			 try {
					JSONObject json= JSON.parseObject(res);
					this.setOrdId(json.getString("OrdId"));
					this.setTransAmt(json.getString("TransAmt"));
					this.setOpenAcctId(json.getString("OpenAcctId"));
					this.setOpenBankId(json.getString("OpenBankId"));
					this.setUsrCustId(json.getString("UsrCustId"));
					this.setMerPriv(json.getString("MerPriv"));
			 }catch  (Exception e){
				 e.printStackTrace();
			 }
			 return null;
		}
		public String getAuditFlag() {
			return auditFlag;
		}
		public void setAuditFlag(String auditFlag) {
			this.auditFlag = auditFlag;
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
