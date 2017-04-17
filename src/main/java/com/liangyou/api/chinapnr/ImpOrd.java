package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;

/**
 * 用于汇付1.0升级2.0时，补录之前用户之间的订单号
 * @author wujing
 *
 */
public class ImpOrd extends InitiativeTender {
	
		public ImpOrd(String ordId,String ordDate,String usrCustid,String transAmt){
			super();
			this.setCmdId("ImpOrd");
			this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnrBgRet.html");
			this.setUsrCustId(usrCustid);
			this.setTransAmt(transAmt);
			this.setOrdId(ordId);
			this.setOrdDate(ordDate);
		}
		private String[] paramNames=new String[] {
				"version", "cmdId", "merCustId",
				"ordId","ordDate","transAmt","usrCustId", 
				"maxTenderRate","borrowerDetails","bgRetUrl","chkValue"
				};
		
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData =new StringBuffer();
			            MerData.append(getVersion())
			            .append(getCmdId())
			            .append(getMerCustId())
			            .append(getOrdId())
			            .append(getOrdDate())
			            .append(getTransAmt())
			            .append(getUsrCustId())
			            .append(getMaxTenderRate())
			            .append(getBorrowerDetails())
			            .append(getBgRetUrl());
			      
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
			return null;
		}
		public String[] getParamNames() {
			return paramNames;
		}
		public void setParamNames(String[] paramNames) {
			this.paramNames = paramNames;
		}
		
	}

