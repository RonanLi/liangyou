package com.liangyou.api.pay;
/**
 * 实名状态查询
 * @author Administrator
 *
 */
public class RealNameCertQuery extends PayModel {
	private String userId ;  // 用户在易极付  id
	//封装返回的 用户信息
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId"};
	
	
	//封装查询信息 
	private String coreCusUserId;
	private String message;
	private String status;
	private String success;
	private String remark;//状态备注
	private String msg;
	
	
	public String getCoreCusUserId() {
		return coreCusUserId;
	}

	public void setCoreCusUserId(String coreCusUserId) {
		this.coreCusUserId = coreCusUserId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		setRemark();
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getUserId() {
		return userId;
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
	
	/*YJF实名认证查询返回：
	 *UNAUTHERIZED(未认证),
	 *NEW_APP(待认证),
	 *AUDIT_DENY(公安部驳回),
	 *AUDIT_PASSED(公安部通过),
	 *CHECK_DENY(审核驳回),
	 *CHECK_PASSED(审核通过),
	 *FORCE_DENY(强制驳回),
	 *NEW_APP_DENY(待认证驳回);
	 **/
	public void  setRemark(){
		if("UNAUTHERIZED".equals(this.status)) {
			this.remark = "未认证";
		}else if("NEW_APP".equals(this.status)) {
			this.remark = "待认证，审核进行中（1-2个工作日）";
		}else if("AUDIT_DENY".equals(this.status)) {
			this.remark = "公安部驳回，被拒绝";
		}else if("AUDIT_PASSED".equals(this.status)) {
			this.remark = "公安部通过,审核通过";
		}else if("CHECK_DENY".equals(this.status)) {
			this.remark = "审核驳回，被拒绝";
		}else if("CHECK_PASSED".equals(this.status)) {
			this.remark = "审核通过";
		}else if("FORCE_DENY".equals(this.status)) {
			this.remark = "未认证，被拒绝";
		}else if("NEW_APP_DENY".equals(this.status)) {
			this.remark = "待认证驳回，被拒绝";
		}else{
			this.remark = "未知状态";
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
