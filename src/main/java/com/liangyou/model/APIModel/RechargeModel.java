package com.liangyou.model.APIModel;

/**
 * 第三方托管充值后，本地处理业务逻辑所需要使用到的参数封装bean
 * @author Administrator
 *
 */
public class RechargeModel {
	private String orderId;   //充值订单号
	private String orderAmount;    //充值金额
	private String gateBusiId; //充值方式，用于区别是否是B2B还是B2C
	private String gateBankId; //充值选择的银行
	private long userId;      //充值用户id
	private String result;    //第三方处理结果
	private double feeAmt;       // 充值手续费
	private String resultMsg;   //充值失败的时候返回信息
	private String serialNo;   //充值流水号
	
	private String returnParam;  //回调参数封装
	
	private int rechargeType;   //充值方式 ：3：无卡代充值
	
	private String code; //第三方返回码
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
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
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public double getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(double feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public int getRechargeType() {
		return rechargeType;
	}
	public void setRechargeType(int rechargeType) {
		this.rechargeType = rechargeType;
	}
	public String getReturnParam() {
		return returnParam;
	}
	public void setReturnParam(String returnParam) {
		this.returnParam = returnParam;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	

}
