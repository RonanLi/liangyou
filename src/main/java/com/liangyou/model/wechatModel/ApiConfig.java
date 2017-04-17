package com.liangyou.model.wechatModel;

/**
 * @author lijing
 *
 *微信 api 封装
 */
public class ApiConfig {
	private String appId;
	private String appSecret;
	private String Token;
	private String encodingAesKey;
	private boolean encryptMessage;
	public String getEncodingAesKey() {
		return encodingAesKey;
	}
	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}
	
	public boolean isEncryptMessage() {
		return encryptMessage;
	}
	public void setEncryptMessage(boolean encryptMessage) {
		this.encryptMessage = encryptMessage;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	@Override
	public String toString() {
		return "ApiConfig [appId=" + appId + ", appSecret=" + appSecret
				+ ", Token=" + Token + ", encodingAesKey=" + encodingAesKey
				+ ", encryptMessage=" + encryptMessage + "]";
	}
	
	
}
