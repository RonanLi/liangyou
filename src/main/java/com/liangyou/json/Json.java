package com.liangyou.json;


/**
 * JSON模型
 * 
 * @author GY
 * 
 */
public class Json implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Integer CODE_OK = 1; // 成功
	public static final Integer CODE_FAILURE = 0; // 失败
	public static final Integer CODE_OUTLOGIN = -1; // 登陆失效
	public static final Integer CODE_GETPRICEERROR = -101; // 获取报价失败
	public static final Integer CODE_GETIBAOYANGPRICEERROR = -102; // 获取i保养报价失败
	public static final Integer CODE_POSTPRICEERROR = -103; // 请求报价失败
	public static final Integer CODE_GETREINFO = -104; // 获取续保信息失败
	public static final Integer CODE_WANSHANXINGSHIBEN = -105;// 需要完善行驶本信息
	public static final Integer CODE_WEIDAOQI = 0; // 交强险未到期,不能提交订单
	public static final Integer CODE_YIDAOQI = 1; // 交强险已到期,可提交订单

	private Integer returnCode = CODE_OK;
	private boolean success = true;// 是否成功
	private String msg = "";// 提示信息
	private Object obj = null;// 其他信息
	private String remark = "";

	public Json() {
		super();
	}
	
	public Json(String msg, Object obj, String remark) {
		super();
		this.msg = msg;
		this.obj = obj;
		this.remark = remark;
	}

	public Json(Integer returnCode, boolean success, String msg, Object obj, String remark) {
		super();
		this.returnCode = returnCode;
		this.success = success;
		this.msg = msg;
		this.obj = obj;
		this.remark = remark;
	}
	public Json( Object obj, String remark) {
		this.obj = obj;
		this.remark = remark;
	}
	
	

	public Json(Integer returnCode, boolean success, Object obj) {
		super();
		this.returnCode = returnCode;
		this.success = success;
		this.obj = obj;
	}

	public Integer getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}

	public Json(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
