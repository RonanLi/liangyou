package com.liangyou.api.pay;
/**
 * 充值记录查询
 * @author Administrator
 *
 */
public class WithdrawQquery extends PayModel {
	
	private String userId;
	private String currPage;
	private String pageSize = "10";
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","currPage","pageSize","userId"};
	
	//封装返回信息
	private String amounts;
	private String amountsIn;
	private String charges;
	private String count;
	private String success;
	
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getAmounts() {
		return amounts;
	}
	public void setAmounts(String amounts) {
		this.amounts = amounts;
	}
	public String getAmountsIn() {
		return amountsIn;
	}
	public void setAmountsIn(String amountsIn) {
		this.amountsIn = amountsIn;
	}
	public String getCharges() {
		return charges;
	}
	public void setCharges(String charges) {
		this.charges = charges;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getWithdrawInfos() {
		return withdrawInfos;
	}
	public void setWithdrawInfos(String withdrawInfos) {
		this.withdrawInfos = withdrawInfos;
	}
	public String getErrCodeCtx() {
		return errCodeCtx;
	}
	public void setErrCodeCtx(String errCodeCtx) {
		this.errCodeCtx = errCodeCtx;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	private String withdrawInfos;
	private String errCodeCtx;
	private String message;
	private String resultCode;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCurrPage() {
		return currPage;
	}
	public void setCurrPage(String currPage) {
		this.currPage = currPage;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
	
}
