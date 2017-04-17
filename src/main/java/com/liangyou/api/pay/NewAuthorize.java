package com.liangyou.api.pay;


/**
 * 易极付实名认证关联
 * @author qinjun
 *v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
 *v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end
 */
public class NewAuthorize extends PayModel {
	
	private String userId;
	private String returnUrl;
	private String notifyUrl;
	
	private String[] paramNames = new String[]{"service","partnerId","orderNo"
			,"sign", "signType","returnUrl","notifyUrl","userId"};
	
	private String status;	//认证状态	success:认证通过、fail:认证失败、precess:认证中
	private String realName	;//真实姓名	
	private String certNo	;//身份证号	
	private String validityTime	;//认证有效期	
	private String certifyPassDate;	//通过认证的时间	
	private String certFrontPath;	//身份证正面图片	
	private String certBackPath	;//身份证背面图片	
	private String message	;//提示信息	
	private String attribution	;//归属地	
	private String comName	;//企业名称	
	private String licenceNo	;//营业执照号	
	private String licence;	//营业执照图片	
	private String legalPersonName	;//法人姓名	
	private String legalPersonCertNo;	//法人身份证号	
	private String legalPersonCertFrontPath;	//法人身份证正面图片	
	private String legalPersonCertBackPath;	//法人身份证背面图片	
	private String taxAuthorityNo	;//税务登记号	
	private String organizationCode	;//组织机构代码	
	private String comCycle;	//营业年限	
	private String address;	//常用地址	
	private String phone;	//联系电话	
	private String provName;	//营业执照所在地省	
	private String cityName;//	营业执照所在地市	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getValidityTime() {
		return validityTime;
	}
	public void setValidityTime(String validityTime) {
		this.validityTime = validityTime;
	}
	public String getCertifyPassDate() {
		return certifyPassDate;
	}
	public void setCertifyPassDate(String certifyPassDate) {
		this.certifyPassDate = certifyPassDate;
	}
	public String getCertFrontPath() {
		return certFrontPath;
	}
	public void setCertFrontPath(String certFrontPath) {
		this.certFrontPath = certFrontPath;
	}
	public String getCertBackPath() {
		return certBackPath;
	}
	public void setCertBackPath(String certBackPath) {
		this.certBackPath = certBackPath;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getLicenceNo() {
		return licenceNo;
	}
	public void setLicenceNo(String licenceNo) {
		this.licenceNo = licenceNo;
	}
	public String getLicence() {
		return licence;
	}
	public void setLicence(String licence) {
		this.licence = licence;
	}
	public String getLegalPersonName() {
		return legalPersonName;
	}
	public void setLegalPersonName(String legalPersonName) {
		this.legalPersonName = legalPersonName;
	}
	public String getLegalPersonCertNo() {
		return legalPersonCertNo;
	}
	public void setLegalPersonCertNo(String legalPersonCertNo) {
		this.legalPersonCertNo = legalPersonCertNo;
	}
	public String getLegalPersonCertFrontPath() {
		return legalPersonCertFrontPath;
	}
	public void setLegalPersonCertFrontPath(String legalPersonCertFrontPath) {
		this.legalPersonCertFrontPath = legalPersonCertFrontPath;
	}
	public String getLegalPersonCertBackPath() {
		return legalPersonCertBackPath;
	}
	public void setLegalPersonCertBackPath(String legalPersonCertBackPath) {
		this.legalPersonCertBackPath = legalPersonCertBackPath;
	}
	public String getTaxAuthorityNo() {
		return taxAuthorityNo;
	}
	public void setTaxAuthorityNo(String taxAuthorityNo) {
		this.taxAuthorityNo = taxAuthorityNo;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getComCycle() {
		return comCycle;
	}
	public void setComCycle(String comCycle) {
		this.comCycle = comCycle;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProvName() {
		return provName;
	}
	public void setProvName(String provName) {
		this.provName = provName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	
	
}
