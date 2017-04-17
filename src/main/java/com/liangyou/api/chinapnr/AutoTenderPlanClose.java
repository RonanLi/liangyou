package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

/**
 * 自动投标计划
 * 
 * @author Administrator
 * 
 */
public class AutoTenderPlanClose extends ChinapnrModel {
	private static final Logger logger = Logger
			.getLogger(AutoTenderPlanClose.class);

	public AutoTenderPlanClose() {
		super();
	}

	public AutoTenderPlanClose(String usercustId) {
		super();
		this.setCmdId("AutoTenderPlanClose");
		this.setUsrCustId(usercustId);
		this.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/autoTenderCloseReturn.html");
	}

	private String[] paramNames = new String[] { "version", "cmdId",
			"merCustId", "usrCustId", "retUtl", "merPriv", "chkValue" };

	@Override
	public StringBuffer getMerData() throws UnsupportedEncodingException {

		StringBuffer MerData = super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))
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
					.append(StringUtils.isNull(getRetUrl()))
					.append(StringUtils.isNull(getMerPriv()));
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		//logger.info("自动投标计划回调参数拼接" + merData.toString());
		return merData;
	}

	public int callback() {
		logger.info("进入用户投标回调验证");
		String merKeyFile = createPubKeyFile();
		SecureLink sl = new SecureLink();
		logger.info("pubKeyFile:" + merKeyFile);
		logger.info("CallbackMerData:" + doCallbackURLDecoder(this.getCallbackMerData()));
		logger.info("getChkValue:" + getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile, doCallbackURLDecoder(getCallbackMerData()),
				getChkValue());
		logger.info("自动投标计划ret" + ret);
		return ret;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}


}
