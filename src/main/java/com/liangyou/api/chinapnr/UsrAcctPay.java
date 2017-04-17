package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class UsrAcctPay extends ChinapnrModel {
		private static final Logger logger=Logger.getLogger(UsrAcctPay.class);
		private String inAcctType;    //账户类型：BASEDT：基本借记账户，DEP：保证金账户
		
		
		public UsrAcctPay(String ordId,String usrCustId,String transAmt){
			super();
			this.setCmdId("UsrAcctPay");
			this.setUsrCustId(usrCustId);
			this.setTransAmt(transAmt);
			this.setOrdId(ordId);
			this.setInAcctType("MERDT");
			this.setInAcctId(Global.getValue("merAcctId"));
			this.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/payCall.html");
			this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/usrAcctPayNotify.html");
		}
		public UsrAcctPay(){
			
		}
		private String[] paramNames=new String[]{
				 "version", "cmdId","ordId","usrCustId","merCustId","transAmt",
				 "inAcctId","inAcctType","retUrl","bgRetUrl","merPriv","chkValue"
		};

		@Override
		public StringBuffer getMerData() throws UnsupportedEncodingException {
			StringBuffer merData=new StringBuffer();
			             merData.append(getVersion())
			            .append(getCmdId())
			            .append(getOrdId())
			            .append(getUsrCustId())
			            .append(getMerCustId())
			            .append(getTransAmt())
			            .append(getInAcctId())
			            .append(getInAcctType())
			            .append(getRetUrl())
			            .append(getBgRetUrl())
			            .append(getMerPriv());
			return merData;
		}
		
		
		
		@Override
		public StringBuffer getCallbackMerData() {
			StringBuffer merData = new StringBuffer();
						 try {
							merData.append(StringUtils.isNull(getCmdId()))
							 		.append(StringUtils.isNull(getRespCode()))
							 		.append(StringUtils.isNull(getOrdId()))
							 		.append(StringUtils.isNull(getUsrCustId()))
							 		.append(StringUtils.isNull(getMerCustId()))
							 		.append(StringUtils.isNull(getTransAmt()))
							 		.append(StringUtils.isNull(getInAcctId()))
							 		.append(StringUtils.isNull(getInAcctType()))
							 		.append(StringUtils.isNull(getRetUrl()))
							 		.append(StringUtils.isNull(getBgRetUrl()))
							 		.append(StringUtils.isNull(getMerPriv()));
						} catch (Exception e) {
							logger.error(e);
							e.printStackTrace();
						}
			return merData;
		}


		
		public int callback(){
			logger.info("进入用户支付回调验证");
			String merKeyFile=createPubKeyFile();
			SecureLink sl = new SecureLink( ) ;
			String retData = doCallbackURLDecoder(this.getCallbackMerData());
			logger.info("pubKeyFile:"+merKeyFile);
			logger.info("CallbackMerData:"+retData);
			logger.info("getChkValue:"+getChkValue());
			int ret = sl.VeriSignMsg(merKeyFile , retData, getChkValue());
			return ret;
		}

		public String getInAcctType() {
			return inAcctType;
		}

		public void setInAcctType(String inAcctType) {
			this.inAcctType = inAcctType;
		}
		
		
}
