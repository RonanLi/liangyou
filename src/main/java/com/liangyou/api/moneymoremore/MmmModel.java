package com.liangyou.api.moneymoremore;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.liangyou.api.chinapnr.HttpClientUtils;
import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.util.ReflectUtils;
import com.liangyou.util.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class MmmModel {
	private static final Logger logger = Logger.getLogger(MmmModel.class);

	private static Signature sign;// 签名对象
	private String submitUrl;// 页面提交地址
	private String signInfo;// 签名参数
	private String[] commitParamNames = new String[] {};// 提交参数列表
	private String[] returnParamNames = new String[] {};// 返回参数列表
	private String AccountNumber;// 钱多多账号
	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start
	private String Message;// 返回信息
	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end
	private String ResultCode;// 返回码 88表示成功
	private String moneymoremoreId;// 用户钱多多标识
	private String platformMoneymoremore = isOnlineConfig() ? Global.getString("plat_form_mmm") : Global.getString("plat_form_test_mmm");// 开通乾多多帐号为平台帐号时生成，以p开头

	private String OrderNo;// 平台流水号
	private String loanNo;// 钱多多流水号

	public String privateKey;// 私钥
	public String publicKey;// 公钥

	private boolean isTender;

	private String remark1; // add by gy 2016-10-26 17:03:07 增加请求参数remark1， 用来做使用体验金利息抵扣本金的标识，该字段存储的是，当前使用体验金的id

	public MmmModel(int operation) {
		super();
		init(operation);
	}

	/**
	 * 是否开通线上环境配置。
	 * 
	 * @return
	 */
	public static boolean isOnlineConfig() {
		return "1".equals(Global.getValue("config_online"));
	}

	public void init(int operation) {
		if (isOnlineConfig()) {// 是否开通线上环境配置
			setPrivateKeyPKCS8(Global.getString("mmm_key_private"));// 钱多多测试私钥
			setPublicKey(Global.getString("mmm_key_public"));// 钱多多测试公钥
			//  转账时     提交地址为 ：         https://transfer.moneymoremore.com/
			setSubmitUrl(Global.getValue("mmm_service_https") + this.getOperation(operation) + Global.getValue("mmm_service_url")); // 正式的线上地址  （ operation=3时， this.getOperation(operation)=transfer.）
		} else {
			setPrivateKeyPKCS8(Global.getString("mmm_test_key_private"));// 钱多多测试私钥
			setPublicKey(Global.getString("mmm_test_key_public"));// 钱多多测试公钥
			//    http://test.moneymoremore.com:88/main/
			setSubmitUrl(Global.getValue("mmm_service_test_url")); // 钱多多测试地址
		}
		try {
			sign = Signature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException nsa) {
			logger.info("" + nsa.getMessage());
		}
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	public Object submit() throws Exception {
		logger.info("双乾任务处理开始..");
		sign();
		String resp = "";
		if (isOnlineConfig()) {// 线上配置
			showParams(getHttpParam());
			logger.debug("提交路径：" + getSubmitUrl());
			resp = HttpClientUtils.doHttpsPost(getSubmitUrl(), getMapParam(getHttpParam()), "UTF-8", 50);
		} else {
			MmmHttpHelper http = new MmmHttpHelper(getSubmitUrl(), getHttpParam(), "UTF-8");
			showParams(getHttpParam());
			resp = http.execute();
		}
		if (resp != null) {
			logger.info("httpClient请求返回：" + resp);
			if (getSubmitUrl().contains("loan.action")) {// 转账接口json解析
				try {
					return StringUtils.loanTenderJson(resp);
				} catch (Exception e) {
					return StringUtils.loanJson(resp);
				}
			} else if (getSubmitUrl().contains("loanorderquery.action")) {
				return resp;// StringUtils.loanQueryJson(resp);
			} else if (getSubmitUrl().contains("toloantransferaudit.action")) {
				return response(resp);
			}
		}
		return resp;
	}

	public MmmModel response(String res) throws IOException {
		try {
			JSONObject json = JSON.parseObject(res);
			logger.info("复审结果返回：" + json.toJSONString(json));
			ResultCode = json.getString("ResultCode");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop

	/**
	 * RSA 数据签名
	 * 
	 * @param toBeSigned
	 *            (待签名的原文)
	 * @param priKey
	 *            (RSA私钥）
	 * @return （返回RSA签名后的数据签名数据base64编码）
	 */
	public void sign() {
		logger.info("开始签名");
		try {
			PrivateKey privateKey = getPrivateKey(this.privateKey);
			byte[] signByte = getParams("commit").getBytes("utf-8");
			sign.initSign(privateKey);
			sign.update(signByte);
			this.setSignInfo(b64encode(sign.sign()));
		} catch (InvalidKeyException in) {
			logger.info("Invalid Key file.Please check the key file path" + in.getCause());
			logger.info("Invalid Key file.Please check the key file path" + in.getCause());
			throw new BussinessException("系统签名异常");
		} catch (Exception se) {
			logger.info(se);
			logger.info(se.getMessage());
			throw new BussinessException("系统签名异常");
		}
	}

	/**
	 * RSA 数据签名验证
	 * 
	 * @param signature
	 *            （RSA签名数据（base64编码）
	 * @param data
	 *            （待验证的数据原文）
	 * @param pubKey
	 *            （RSA公钥数据）
	 * @return 返回验证结果（TRUE:验证成功；FALSE:验证失败）
	 */
	public boolean successSign() {
		boolean flag = false;
		try {
			byte[] signByte = b64decode(this.signInfo);
			byte[] dataByte = getParams("return").getBytes("utf-8");
			PublicKey publicKey = getPublicKey(this.publicKey);
			sign.initVerify(publicKey);
			sign.update(dataByte);
			flag = sign.verify(signByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	public static Map<String, String> getMapParam(String[][] args) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			String key = args[i][0];
			String value = args[i][1];
			map.put(key, value);
		}
		return map;
	}

	// -----------------日志（签名参数）
	public void showParams(String[][] args) {
		for (int i = 0; i < args.length; i++) {
			String key = args[i][0];
			String value = args[i][1];
			logger.info(key + "=" + value);
		}
	}

	private String[][] getHttpParam() {
		String[] paramNames = getCommitParamNames();
		String[][] namePair = new String[paramNames.length + 1][2];
		for (int i = 0; i < paramNames.length; i++) {
			String name = paramNames[i];
			Object result = ReflectUtils.invokeGetMethod(getClass(), this, name);
			String value = (result == null ? "" : result.toString());
			namePair[i][0] = StringUtils.firstCharUpperCase(name);
			// namePair[i][1]=value;
			if ("LoanJsonList".equals(name)) {
				namePair[i][1] = StringUtils.UrlEncoder(value, "UTF-8");
			} else {
				namePair[i][1] = value;
			}

			if (i == paramNames.length - 1) {
				result = ReflectUtils.invokeGetMethod(getClass(), this, "SignInfo");
				value = (result == null ? "" : result.toString());
				namePair[i + 1][0] = "SignInfo";
				namePair[i + 1][1] = value;
			}
		}
		return namePair;
	}

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 stop

	private String getParams(String type) {
		String[] paramNames;
		if (type.equals("commit")) {
			paramNames = getCommitParamNames();
		} else {
			paramNames = getReturnParamNames();
		}
		// 排序
		StringBuffer sb = new StringBuffer();
		StringBuffer sbStr = new StringBuffer();

		for (int i = 0; i < paramNames.length; i++) {
			String name = paramNames[i];
			Object result = ReflectUtils.invokeGetMethod(getClass(), this, name);
			String value = (result == null ? "" : result.toString());
			sb.append(value);

			sbStr.append(name + ": " + value + ", ");
		}
		logger.info("签名参数：" + sbStr.toString());
		return sb.toString();
	}

	public static PrivateKey getPrivateKey(String privateKeyStr) {
		try {
			byte[] privateKeyBytes = b64decode(privateKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			return keyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			logger.info("Invalid Key Specs. Not valid Key files." + e.getCause());
			return null;
		} catch (NoSuchAlgorithmException e) {
			logger.info("There is no such algorithm. Please check the JDK ver." + e.getCause());
			return null;
		}
	}

	/**
	 * base64解码
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] b64decode(String data) {
		try {
			return new BASE64Decoder().decodeBuffer(data);
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * RSA数据解密
	 * 
	 * @param encryptedData
	 *            （需要解密的数据base64编码数据）
	 * @param priKey
	 *            （RSA的私钥）
	 * @return 返回解密后的原始明文
	 */
	public String decryptData(String encryptedData) {
		try {
			byte[] encryData = b64decode(encryptedData);
			PrivateKey privateKey = getPrivateKey(this.privateKey);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(encryData), "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * RSA数据加密
	 * 
	 * @param data
	 *            （需要加密的数据）
	 * @param pubKey
	 *            （RSA公钥）
	 * @return 返回加密后的密文（BASE64编码）
	 */
	public String encryptData(String data) {
		try {
			byte[] dataByte = data.getBytes("utf-8");
			PublicKey publicKey = getPublicKey(this.publicKey);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return b64encode(cipher.doFinal(dataByte));
		} catch (Exception e) {
			return null;
		}
	}

	private PublicKey getPublicKey(String publicKeyStr) {
		try {
			byte[] publicKeyBytes = b64decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
			return keyFactory.generatePublic(publicKeySpec);

		} catch (InvalidKeySpecException e) {
			logger.info("Invalid Key Specs. Not valid Key files." + e.getCause());
			return null;
		} catch (NoSuchAlgorithmException e) {
			logger.info("There is no such algorithm. Please check the JDK ver." + e.getCause());
			return null;
		}
	}

	/**
	 * 将模型进行JSON编码
	 * 
	 * @param obModel
	 * @return String
	 */
	public final static String JSONEncode(Object obModel) {
		Gson gson = new Gson();
		return gson.toJson(obModel);
	}

	/**
	 * base64编码
	 * 
	 * @param data
	 * @return
	 */
	public static String b64encode(byte[] data) {
		return new BASE64Encoder().encode(data);
	}

	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}

	public String getLoanNo() {
		return loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getMoneymoremoreId() {
		return moneymoremoreId;
	}

	public void setMoneymoremoreId(String moneymoremoreId) {
		this.moneymoremoreId = moneymoremoreId;
	}

	public String getPrivateKeyPKCS8() {
		return privateKey;
	}

	public void setPrivateKeyPKCS8(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getResultCode() {
		return ResultCode;
	}

	public void setResultCode(String resultCode) {
		ResultCode = resultCode;
	}

	public String getAccountNumber() {
		return AccountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}

	public String[] getCommitParamNames() {
		return commitParamNames;
	}

	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}

	public String[] getReturnParamNames() {
		return returnParamNames;
	}

	public void setReturnParamNames(String[] returnParamNames) {
		this.returnParamNames = returnParamNames;
	}

	public Signature getSign() {
		return sign;
	}

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}

	public void setSign(Signature sign) {
		this.sign = sign;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public boolean isTender() {
		return isTender;
	}

	public void setTender(boolean isTender) {
		this.isTender = isTender;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start
	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end

	// add by gy 2016-10-26 17:04:41 begin
	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	// add by gy 2016-10-26 17:04:53 end

	private String getOperation(int operation) {

		switch (operation) {
		case 1:
			return "register.";
		case 2:
			return "recharge.";
		case 3:
			return "transfer.";
		case 4:
			return "audit.";
		case 5:
			return "query.";
		case 6:
			return "query.";

		default:
			return "www.";
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
