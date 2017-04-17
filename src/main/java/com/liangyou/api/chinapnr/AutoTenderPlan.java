package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

/**
 * 自动投标计划
 * @author Administrator
 *
 */
public class AutoTenderPlan extends ChinapnrModel {
	private static final Logger logger=Logger.getLogger(AutoTenderPlan.class);
	
	private String tenderPlanType = "W";  //取值：P : 部分授权。W:完全授权
	
	
	public AutoTenderPlan(){
		super();
	}
	
	public AutoTenderPlan(String usercustId){
		super();
		this.setCmdId("AutoTenderPlan");
		this.setUsrCustId(usercustId);
		this.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/autoTenderPlanReturn.html");
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/autoTenderNotify.html");
	}
	private String[] paramNames=new String[]{
			"version","cmdId","merCustId","usrCustId","tenderPlanType",
			"retUtl","merPriv","chkValue"
	};
	
	@Override
	public StringBuffer getMerData() throws UnsupportedEncodingException{

		StringBuffer MerData =super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))
				.append(getTenderPlanType())
				.append(getRetUrl())
				.append(getMerPriv());
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
						 		.append(StringUtils.isNull(getTenderPlanType()))
						 		.append(StringUtils.isNull(getTransAmt()))
						 		.append(StringUtils.isNull(getRetUrl()))
						 		.append(StringUtils.isNull(getMerPriv()));
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
					 logger.info("自动投标计划回调参数拼接"+doCallbackURLDecoder(merData));
		return merData;
	}
	
	public int callback(){
		logger.info("进入用户投标回调验证");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink( ) ;
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+doCallbackURLDecoder(this.getCallbackMerData()));
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile , doCallbackURLDecoder(this.getCallbackMerData()), getChkValue());
		logger.info("自动投标计划ret"+ret);
		return ret;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getTenderPlanType() {
		return tenderPlanType;
	}

	public void setTenderPlanType(String tenderPlanType) {
		this.tenderPlanType = tenderPlanType;
	}
	

}
