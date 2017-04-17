package com.liangyou.api.moneymoremore;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.liangyou.context.Global;

/**
 * 用户注册
 * 
 * @author Qinjun
 */
public class MmmRegister extends MmmModel {

	private String registerType = "2";// 1:全自动，2：半自动
										// 全自动注册会生成随机的登录密码和支付密码发送到用户的手机，安保问题需要登录乾多多后台设置

	private String accountType = ""; // 账户类型

	private String mobile;// 手机
	private String email;// 邮箱
	private String realName;// 真实姓名 / 企业名
	private String identificationNo;// 身份证号码 / 营业执照号
	private String image1;// 身份证正面（可无）
	private String image2;// 身份证反面（可无）
	private String loanPlatformAccount;//用户名
	private String authFee;  //姓名匹配手续费
	private String authState;  //实名认证状态
	
	private String returnURL = Global.getString("weburl")+"/public/mmm/registerReturn.html";// 页面回调地址
//	private String returnURL = Global.getString("weburl") + "/member/apiRealname.html?authcallback"; // 页面回调地址
	private String notifyURL = Global.getString("weburl") + "/public/mmm/registerNotify.html";// 后台回调地址
	//提交参数列表
	private String[] commitParamNames = new String[] { "registerType", "accountType", "mobile",
			"email", "realName", "identificationNo", "image1", "image2","loanPlatformAccount",
			"platformMoneymoremore", "returnURL", "notifyURL" };
	//验证签名返回参数列表
	private String[] returnParamNames = new String[] {"accountType", "accountNumber", "mobile", "email", "realName"  ,
			"identificationNo","loanPlatformAccount" , "moneymoremoreId", "platformMoneymoremore" ,"authFee","authState" , "resultCode"};
	
	public MmmRegister(int operation) {
		super(operation);
	}

	public String getLoanPlatformAccount() {
		return loanPlatformAccount;
	}

	public void setLoanPlatformAccount(String loanPlatformAccount) {
		this.loanPlatformAccount = loanPlatformAccount;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getNotifyURL() {
		return notifyURL;
	}

	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}

	public String[] getCommitParamNames() {
		return commitParamNames;
	}

	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}

	public String[] getReturnParamNames() {
		return returnParamNames;
	}

	public void setReturnParamNames(String[] returnParamNames) {
		this.returnParamNames = returnParamNames;
	}

	public String getAuthFee() {
		return authFee;
	}

	public void setAuthFee(String authFee) {
		this.authFee = authFee;
	}

	public String getAuthState() {
		return authState;
	}

	public void setAuthState(String authState) {
		this.authState = authState;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}


}
