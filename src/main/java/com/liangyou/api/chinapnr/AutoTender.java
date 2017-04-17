package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;


public class AutoTender extends ChinapnrModel {
	private static final Logger logger=Logger.getLogger(AutoTender.class);
	   /**
	    * 用户主动投标
	    *  借款人信息使用json传输
	    * BorrowerAmt借款金额，BorrowerRate借款手续费
	    * */
		private String borrowerDetails; 
		private String maxTenderRate;   //最大投资手续费
		
		public AutoTender(){
			super();
		}
		public AutoTender(String usrCustId,String transAmt,String maxTenderRate){
			super();
			this.setCmdId("AutoTender");
			this.setUsrCustId(usrCustId);
			this.setTransAmt(transAmt);
			this.setMerCustId(super.getMerCustId());
			this.setMaxTenderRate(maxTenderRate);
			this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/autoTenderNotify.html");
		}
			
		private String[] paramNames = new String[] { 
				"version", "cmdId", "merCustId",
				"ordId","ordDate","transAmt","usrCustId", 
				"maxTenderRate","borrowerDetails","bgRetUrl","merPriv","chkValue"};
		
		
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData =super.getMerData();
			            MerData.append(getOrdId())
			            .append(getOrdDate())
			            .append(getTransAmt())
			            .append(getUsrCustId())
			            .append(getMaxTenderRate())
			            .append(getBorrowerDetails())
			            .append(getBgRetUrl())
			            .append(getMerPriv());
			return MerData;	
		}
		
		 /**
		  * 在汇付2.0版本中，转换投标接口中
		  * BorrowerDetails字段转换为json格式
		  * */
		 public void fildBorrowerDetails(String[][] args){
			 StringBuffer sb=new StringBuffer();
				
		        boolean first = true;
		        sb.append("[");
		        for (int i = 0; i < args.length; i++) {
		            String[] blogItem = args[i];
		            if (!first) {
		                sb.append(",");
		            }
		            sb.append("{");
		            sb.append("\"BorrowerCustId\":\""+blogItem[0] + "\",");
		            sb.append("\"BorrowerAmt\":\""+blogItem[1]+"\",");
		            sb.append("\"BorrowerRate\":\""+blogItem[2]+"\"");
		            sb.append("}");
		            first = false;
		        }
		        sb.append("]");
		        this.setBorrowerDetails(sb.toString());
		 }
		  
		 //用户投标回调参数拼接
		@Override
		public StringBuffer getCallbackMerData() {
			StringBuffer merData = new StringBuffer();
						 try {
							merData.append(StringUtils.isNull(getCmdId()))
							 		.append(StringUtils.isNull(getRespCode()))
							 		.append(StringUtils.isNull(getMerCustId()))
							 		.append(StringUtils.isNull(getOrdId()))
							 		.append(StringUtils.isNull(getOrdDate()))
							 		.append(StringUtils.isNull(getTransAmt()))
							 		.append(StringUtils.isNull(getUsrCustId()))
							 		.append(StringUtils.isNull(getTrxId()))
							 		.append(StringUtils.isNull(getBgRetUrl()))
							 		.append(StringUtils.isNull(getMerPriv()));
						}catch (Exception e) {
							logger.error(e);
							e.printStackTrace();
						}
						// logger.info("用户投标回调参数拼接"+doCallbackURLDecoder(merData));
			return merData;
		}


			
		public int callback(){
			logger.info("进入用户投标回调验证");
			String merKeyFile=createPubKeyFile();
			SecureLink sl = new SecureLink( ) ;
			logger.info("pubKeyFile:"+merKeyFile);
			logger.info("CallbackMerData:"+doCallbackURLDecoder(this.getCallbackMerData()));
			logger.info("getChkValue:"+getChkValue());
			int ret = sl.VeriSignMsg(merKeyFile , doCallbackURLDecoder(getCallbackMerData()), getChkValue());
			logger.info("投标ret"+ret);
			return ret;
		}


  
		 
		public String getBorrowerDetails() {
			return borrowerDetails;
		}



		public void setBorrowerDetails(String borrowerDetails) {
			this.borrowerDetails = borrowerDetails;
		}

		public String getMaxTenderRate() {
			return maxTenderRate;
		}

		public void setMaxTenderRate(String maxTenderRate) {
			this.maxTenderRate = maxTenderRate;
		}
		public String[] getParamNames() {
			return paramNames;
		}

		public void setParamNames(String[] paramNames) {
			this.paramNames = paramNames;
		}
		 
}
