package com.liangyou.api.chinapnr;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;
  
public class FssRedeemReconciliation extends ChinapnrModel {
	private static final Logger logger=Logger.getLogger(FssRedeemReconciliation.class);
		/**
		 * 生利宝接口  TGPROJECT-314 lx 2014-05-30 add
		 */
		private String transType;  
		private String transAmt ;
		private String respExt;
		
		public FssRedeemReconciliation(String transType, String transAmt,
				String[] paramNames) {
			super();
			this.transType = transType;
			this.transAmt = transAmt;
			this.paramNames = paramNames;
		}
		
		
		
		private String[] paramNames=new String[] {
				"version","cmdId","merCustId","usrCustId","ordId","ordDate",
				"retUrl","bgRetUrl","merPriv","reqExt","chkValue"
		};
		
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData = super.getMerData();
						 MerData
						 .append(getUsrCustId())
						 .append(getOrdId())
						 .append(getOrdDate())
						 .append(getRetUrl())
						 .append(getBgRetUrl())
						 .append(getMerPriv())
						 .append(getReqExt());
			return MerData;
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
							 		.append(URLDecoder.decode(StringUtils.isNull(getRetUrl()),"utf-8"))
							 		.append(URLDecoder.decode(StringUtils.isNull(getBgRetUrl()),"utf-8"))
							 		.append(URLDecoder.decode(StringUtils.isNull(getMerPriv()),"utf-8"))
									.append(StringUtils.isNull(getRespExt()));
						} catch (UnsupportedEncodingException e) {
							logger.error(e);
							e.printStackTrace();
						}
					
			return merData;
		}
		
		
		
		@Override
		public ChinapnrModel response(String res) throws IOException {
			super.response(res);
			 try {
					JSONObject json= JSON.parseObject(res);
					this.setOrdId(json.getString("OrdId"));
					this.setTransAmt(json.getString("TransAmt"));
					this.setOpenBankId(json.getString("TransType"));
					this.setUsrCustId(json.getString("UsrCustId"));
					this.setMerPriv(json.getString("MerPriv"));
			 }catch  (Exception e){
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
