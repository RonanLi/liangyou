package com.liangyou.api.pay;

public class UserInfoQuery extends PayModel {

    private String userId;
    
    //返回信息
    private String userName;
    private String email;
    private String mobileNo;
    private String realName;
    /**
     *绑定银行卡数
     */
    private String bankCardCount; 
    
    /**
     * 实名认证状态
     * 可选值
	 *UNAUTHERIZED：未认证
	 *AUTHORIZED：已认证 INAUTHORIZED：认证中
	 *REJECT：被驳回
     */
    private String certifyStatus;
	/**
	 * 实名认证等级
	 * 
	 * 个人实名认证等级：
		0代表未通过，认证中或者证件过期
		1 代表快速认证
    	3 代表普通认证
		5 代表增强认证
		
		个人非大陆等级：
		0代表未通过，认证中或者证件过期
		3 代表审核通过
		 
		大陆企业等级：
		0代表未通过，认证中或者证件过期
		4 代表审核通过
	 */
    private String certifyLevel;
    /**
     *  NORMAL：激活
		UNACTIVATED：未激活
     */
    private String userStatus;
    /**
     *  FREEZE：冻结
		UNSUBSCRIBE：注销
		NOMARL：正常
     */
    private String runtimeStatus;
    
    /**
     * 提价参数
     */
	private String[] paramNames = new String[]{"service","partnerId",
	         "signType","sign","orderNo","userId"
	         };
    
	public UserInfoQuery() {
		super();
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getBankCardCount() {
		return bankCardCount;
	}
	public void setBankCardCount(String bankCardCount) {
		this.bankCardCount = bankCardCount;
	}
	public String getCertifyStatus() {
		return certifyStatus;
	}
	public void setCertifyStatus(String certifyStatus) {
		this.certifyStatus = certifyStatus;
	}
	public String getCertifyLevel() {
		return certifyLevel;
	}
	public void setCertifyLevel(String certifyLevel) {
		this.certifyLevel = certifyLevel;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getRuntimeStatus() {
		return runtimeStatus;
	}
	public void setRuntimeStatus(String runtimeStatus) {
		this.runtimeStatus = runtimeStatus;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
	
}
