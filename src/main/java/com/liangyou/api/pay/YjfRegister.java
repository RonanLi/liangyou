package com.liangyou.api.pay;
/**
 * 用户注册
 * @author Administrator
 */
public class YjfRegister extends PayModel {
	
	private String userName;
	private String userType;
	private String userStatus;
	private String realName;
	private String email;
	private String mobile;
	private String certNo;
	private String registerFrom;
	private String originRegisterFrom;
	//v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  start
	private String certValidTime;
    private String[] paramNames=new String[]{"service","partnerId","signType",
    		         "sign","orderNo","userName","userType","userStatus","realName",
    		         "email","mobile","certNo","registerFrom","originRegisterFrom","certValidTime"};
    public String getCertValidTime() {
		return certValidTime;
	}
	public void setCertValidTime(String certValidTime) {
		this.certValidTime = certValidTime;
	}
    //v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  end
    public YjfRegister(){
    	super();
    }
	//返回参数
	private String channelId;
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	private String resultCode;
	private String resultMessage;
	private String userId ;  //用户注册易极付  返回的 注册 id。
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRegisterFrom() {
		return registerFrom;
	}
	public void setRegisterFrom(String registerFrom) {
		this.registerFrom = registerFrom;
	}
	public String getOriginRegisterFrom() {
		return originRegisterFrom;
	}
	public void setOriginRegisterFrom(String originRegisterFrom) {
		this.originRegisterFrom = originRegisterFrom;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
}
