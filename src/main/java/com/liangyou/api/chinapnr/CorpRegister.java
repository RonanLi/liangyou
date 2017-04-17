package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;

/**
 *v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 
 */
public class CorpRegister extends ChinapnrModel{
	/**
	 * 用户开户，现在统一使用页面开户模式
	 * */
	private static final Logger logger=Logger.getLogger(CorpRegister.class);
	/**
	 * 用户名
	 */
	private String usrId;
	/**
	 * 1. 填写企业用户的全称
	 * 2. 可在请求参数中传入，也可在汇付页面填写
	 */
	private String usrName;
	/**
	 * 组织机构代码
	 */
	private String instuCode;         
	/**
	 * 营业执照编号
	 * 1. 必填，作为商户下企业用户的唯一标识
	 * 2. 可使用该字段作为查询企业用户开户状态
     * 3. 如果审核未通过或需要分多次提交图片等，可再次传入该字段，页面会显示审核拒绝的原因，并回显已经填写过的信息。
	 */
	private String busiCode;         //用户支付密码	
	/**
	 * 税务登记号(可在请求参数中传入，也可在汇付页面填写)
	 */
	private String taxCode;                  //用户手机号
	/**
	 * 1. 此参数用户限定该企业用户是否为担保用户的角色
	 * 2. 如不传，则默认“否”
 	 * 3. 格式为：Y - 是；N - 否
	 */
	private String guarType;

	//返回参数
	/**
	 * 审核过程中的状态：
	 * I：  初始 T：提交 P：审核中 R：审核拒绝 F：开户失败 K：  开户中 Y：开户成功
	 */
	private String auditStat; 
	
	private String auditDesc; 
	
	/**
	 * 开户银行代号
	 */
	private String openBankId;
	
	/**
	 * 开户银行账号
	 */
	private String cardId;
	
	public CorpRegister() {
		super();
		this.setCmdId("CorpRegister");
		this.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/corpRegisterNotify.html");
	
	}

	private String[] paramNames = new String[] { "version", "cmdId",
			"merCustId", "usrId", "usrName", "instuCode", "busiCode",
			"taxCode", "merPriv", "guarType", "bgRetUrl","chkValue" };
	
	public StringBuffer getMerData() throws UnsupportedEncodingException {
		StringBuffer MerData = super.getMerData();
		MerData.append(StringUtils.isNull(this.getUsrId()))
		.append(StringUtils.isNull(this.getUsrName()))
		.append(StringUtils.isNull(this.getInstuCode()))
		.append(StringUtils.isNull(this.getBusiCode()))
		.append(StringUtils.isNull(this.getTaxCode()))
		.append(StringUtils.isNull(this.getMerPriv()))
		.append(StringUtils.isNull(this.getGuarType()))
		.append(StringUtils.isNull(this.getBgRetUrl()));
		return MerData;
	}
	
	@Override
	public ChinapnrRegister response(String res) throws IOException {
	logger.info(res);
	    try {
	    	JSONObject json= JSON.parseObject(res);
	    	setCmdId(json.get("CmdId").toString());
	    	setRespCode(json.get("RespCode").toString());
	    	setRespDesc(json.getString("RespDesc"));
	    	setMerCustId(json.get("MerCustId").toString());
	    	setUsrCustId(json.get("UsrCustId").toString());
	    	setUsrId(json.getString("UsrId"));
	    	setMerPriv(json.getString("MerPrk"));
		} catch (Exception e) {
			logger.info("企业开户汇付账号获取回调参数json转换出错"+e.getMessage());
		}
		return null;
	}
	
	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer merData = new StringBuffer();
		try {
			merData.append(StringUtils.isNull(getCmdId()))
					.append(StringUtils.isNull(getRespCode()))
					.append(StringUtils.isNull(getMerCustId()))
					.append(StringUtils.isNull(getUsrId()))
					.append(StringUtils.isNull(getUsrName()))
					.append(StringUtils.isNull(getUsrCustId()))
					.append(StringUtils.isNull(getAuditStat()))
					.append(StringUtils.isNull(getTrxId()))
					.append(StringUtils.isNull(getOpenBankId()))
					.append(StringUtils.isNull(getCardId()))
					.append(StringUtils.isNull(getBgRetUrl()));
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return merData;
	}
	
	
	//用户开户验签操作
	public int callback(){
		logger.info("进入用户开户验签回调验证………………");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink() ;
		String retData = doCallbackURLDecoder(this.getCallbackMerData());
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+retData);
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile , retData, getChkValue());
		return ret;
	}
	
	
	public String getUsrId() {
		return usrId;
	}
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	public String getUsrName() {
		return usrName;
	}
	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}
	public String getInstuCode() {
		return instuCode;
	}
	public void setInstuCode(String instuCode) {
		this.instuCode = instuCode;
	}
	public String getBusiCode() {
		return busiCode;
	}
	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getGuarType() {
		return guarType;
	}
	public void setGuarType(String guarType) {
		this.guarType = guarType;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getAuditStat() {
		return auditStat;
	}
	public void setAuditStat(String auditStat) {
		this.auditStat = auditStat;
	}
	public String getOpenBankId() {
		return openBankId;
	}
	public void setOpenBankId(String openBankId) {
		this.openBankId = openBankId;
	}
	public String getAuditDesc() {
		return auditDesc;
	}
	public void setAuditDesc(String auditDesc) {
		this.auditDesc = auditDesc;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	
}
