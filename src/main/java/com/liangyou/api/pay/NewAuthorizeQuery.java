package com.liangyou.api.pay;


/**
 * 易极付实名认证关联查询
 * @author qinjun
 *v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
 *v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end
 */
public class NewAuthorizeQuery extends PayModel {
	private String userId;
	
	private String[] paramNames = new String[]{"service","partnerId","orderNo"
			,"sign", "signType","userId"};
	
	private String message;
	private String method;
	private String status;
	private String resultMessage;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		if(method.equals("NOC")){
			this.method = "未认证";
		}else if(method.equals("GPA")){
			this.method = "身份证快速认证";
		}else if(method.equals("GYK")){
			this.method = "银行快键认证";
		}else if(method.equals("GPT")){
			this.method = "个人普通认证";
		}else if(method.equals("GZQ")){
			this.method = "个人增强认证";
		}else if(method.equals("FDK")){
			this.method = "打款认证";
		}else if(method.equals("FDB")){
			this.method = "担保认证";
		}else if(method.equals("QPT")){
			this.method = "企业普通认证";
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
}
