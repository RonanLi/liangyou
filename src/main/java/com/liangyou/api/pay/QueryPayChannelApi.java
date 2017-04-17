package com.liangyou.api.pay;
/**
 * 支付渠道查询
 * @author Administrator
 *
 */
public class QueryPayChannelApi extends PayModel {

	private String apiType; //渠道类型   
	private String cardType; //渠道类型   
	private String payType; //渠道类型   
	private String publicTag; //渠道类型   
	private String owner; //渠道类型   
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","apiType","cardType","payType","publicTag","owner"};

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPublicTag() {
		return publicTag;
	}

	public void setPublicTag(String publicTag) {
		this.publicTag = publicTag;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}
