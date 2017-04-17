package com.liangyou.api.pay;
/**
 * 提现申请
 * @author Administrator
 *
 */
public class ApplyWithDrawBack extends PayModel {
	
    private String  sign  ;
	private String message;
	private String  amount  ;
	
	private String  notifyTime  ;
	private String payNo;
	private String SETTLE_REASON;
	private String payTypeMessage;
	private String resultCode;
	private String  payType ; 
	private String outBizNo;
	private String resultMessage;
	private String success;
	private String  paramRequest;
	//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  start
	private String amountIn;//实际到账金额
	private String[] paramNames = new String[]{"sign","message","amount","amountIn","notifyTime",
			"payNo","SETTLE_REASON","payTypeMessage","resultCode","payType",
			"outBizNo","resultMessage","success"};
	
	public String getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(String amountIn) {
		this.amountIn = amountIn;
	}
	//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  end
	public String getSign() {
		return sign;
	}
	
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getNotifyTime() {
		return notifyTime;
	}
	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getSETTLE_REASON() {
		return SETTLE_REASON;
	}
	public void setSETTLE_REASON(String sETTLE_REASON) {
		SETTLE_REASON = sETTLE_REASON;
	}
	public String getPayTypeMessage() {
		return payTypeMessage;
	}
	public void setPayTypeMessage(String payTypeMessage) {
		this.payTypeMessage = payTypeMessage;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getOutBizNo() {
		return outBizNo;
	}
	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getParamRequest() {
		return paramRequest;
	}
	public void setParamRequest(String paramRequest) {
		this.paramRequest = paramRequest;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
}
