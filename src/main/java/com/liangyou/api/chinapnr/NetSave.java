package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

public class NetSave extends ChinapnrModel {
	private static final Logger logger=Logger.getLogger(NetSave.class);

	/*
	 * 充值页面跳转模式
	 * 
	 * */
	private String address="/muser/publicRequests";
	private String testAddress="/muser/publicRequests";
	private String dcFlag;        //借贷标记：D：借记,;C：贷记
	private String gateBusiId;         //充值方式
	private String gateBankId;          //充值选择的银行
	private String feeAmt;    //充值手续费，新增
	private String feeCustId;  //充值扣费客户号
	private String feeAcctId;   //充值扣费子账户
		public NetSave(){
			
		}
		public NetSave(String ordid,String date,String dcFlag,String transAmt,String usrCusid){
			super();
			this.setUsrCustId(usrCusid);
			this.setCmdId("NetSave");
			this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/netSaveNotify.html");
			setDcFlag(dcFlag);
			setOrdId(ordid);
			setOrdDate(date);
			setTransAmt(transAmt);
		}
		private String[] paramNames=new String[]{
				 "version", "cmdId", "merCustId","usrCustId","ordId", "ordDate",
				 "dcFlag","transAmt","retUrl","bgRetUrl","merPriv","chkValue"
		};
		

		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData = super.getMerData();
			MerData.append(getUsrCustId())
			.append(getOrdId())
			.append(getOrdDate())
			.append(getDcFlag())
			.append(getTransAmt())
			.append(getRetUrl())
			.append(getBgRetUrl())
			.append(getMerPriv());
			return MerData;
			
		}
		public String getDcFlag() {
		return dcFlag;
		}
		public void setDcFlag(String dcFlag) {
		this.dcFlag = dcFlag;
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
		
		public String getGateBusiId() {
			return gateBusiId;
		}
		public void setGateBusiId(String gateBusiId) {
			this.gateBusiId = gateBusiId;
		}
		public String getGateBankId() {
			return gateBankId;
		}
		public void setGateBankId(String gateBankId) {
			this.gateBankId = gateBankId;
		}
		
		//获取返回参数列表
		public String getReturnString() {
			StringBuffer merData=new StringBuffer();
			merData.append("CmdId=" + StringUtils.isNull(getCmdId()))
			        .append("&")
				   .append("RespCode="+StringUtils.isNull(getRespCode()))
				   .append("&")
				   .append("MerCustId="+StringUtils.isNull(getMerCustId()))
				   .append("&")
				   .append("UsrCustId="+StringUtils.isNull(getUsrCustId()))
				   .append("&")
				   .append("OrdId=" + StringUtils.isNull(getOrdId()))
				   .append("&")
				   .append("OrdDate="+StringUtils.isNull(getOrdDate()))
				   .append("&")
				   .append("TransAmt="+StringUtils.isNull(getTransAmt()))
				   .append("&")
				   .append("TrxId="+StringUtils.isNull(getTrxId()))
				   .append("&")
				   .append("RetUrl="+StringUtils.isNull(getRetUrl()))
				   .append("&")
				   .append("BgRetUrl="+StringUtils.isNull(getBgRetUrl()))
				   .append("&")
				   .append("MerPriv="+StringUtils.isNull(getMerPriv()));
			logger.info(merData.toString());
			return merData.toString();
		}
		
		@Override
		public StringBuffer getCallbackMerData() {
			logger.info("进入充值回调参数拼接………………");
			StringBuffer merData=new StringBuffer();
			try {
				merData.append(StringUtils.isNull(getCmdId()))
					   .append(StringUtils.isNull(getRespCode()))
					   .append(StringUtils.isNull(getMerCustId()))
					   .append(StringUtils.isNull(getUsrCustId()))
					   .append(StringUtils.isNull(getOrdId()))
					   .append(StringUtils.isNull(getOrdDate()))
					   .append(StringUtils.isNull(getTransAmt()))
					   .append(StringUtils.isNull(getTrxId()))
					   .append(StringUtils.isNull(getRetUrl()))
					   .append(StringUtils.isNull(getBgRetUrl()))
					   .append(StringUtils.isNull(getMerPriv()));
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
			logger.info(merData.toString());
			return merData;
		}

		public int callback(){
			logger.info("汇付2.0进入充值");
			String merKeyFile=createPubKeyFile();
			SecureLink sl = new SecureLink( ) ;
			String retData = doCallbackURLDecoder(this.getCallbackMerData());
			logger.info("pubKeyFile:"+merKeyFile);
			logger.info("CallbackMerData:"+retData);
			logger.info("getChkValue:"+getChkValue());
			int ret = sl.VeriSignMsg(merKeyFile ,retData, getChkValue());
			return ret;
		}
		public String getFeeAmt() {
			return feeAmt;
		}
		public void setFeeAmt(String feeAmt) {
			this.feeAmt = feeAmt;
		}
		public String getFeeCustId() {
			return feeCustId;
		}
		public void setFeeCustId(String feeCustId) {
			this.feeCustId = feeCustId;
		}
		public String getFeeAcctId() {
			return feeAcctId;
		}
		public void setFeeAcctId(String feeAcctId) {
			this.feeAcctId = feeAcctId;
		}
		
}
