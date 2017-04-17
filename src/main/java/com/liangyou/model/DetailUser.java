package com.liangyou.model;

import java.io.Serializable;

import com.liangyou.domain.User;


public class DetailUser extends User implements Serializable  {
	
	private static final long serialVersionUID = 5543205481644066198L;
	
	private int credit_jifen;
	private double use_money;
	private String  credit_pic;
	private int vip_status;
	private long  vip_verify_time;
	private long kefu_addtime;
	
	private String provincetext;
	private String citytext;
	private String areatext;
	private String typename;
	private User user;
	
	public double getUse_money() {
		return use_money;
	}
	public void setUse_money(double use_money) {
		this.use_money = use_money;
	}
	public int getCredit_jifen() {
		return credit_jifen;
	}
	public void setCredit_jifen(int credit_jifen) {
		this.credit_jifen = credit_jifen;
	}
	public String getCredit_pic() {
		return credit_pic;
	}
	public void setCredit_pic(String credit_pic) {
		this.credit_pic = credit_pic;
	}
	public int getVip_status() {
		return vip_status;
	}
	public void setVip_status(int vip_status) {
		this.vip_status = vip_status;
	}
	public String getProvincetext() {
		return provincetext;
	}
	public void setProvincetext(String provincetext) {
		this.provincetext = provincetext;
	}
	public String getCitytext() {
		return citytext;
	}
	public void setCitytext(String citytext) {
		this.citytext = citytext;
	}
	public String getAreatext() {
		return areatext;
	}
	public void setAreatext(String areatext) {
		this.areatext = areatext;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public long getVip_verify_time() {
		return vip_verify_time;
	}
	public void setVip_verify_time(long vip_verify_time) {
		this.vip_verify_time = vip_verify_time;
	}
	public long getKefu_addtime() {
		return kefu_addtime;
	}
	public void setKefu_addtime(long kefu_addtime) {
		this.kefu_addtime = kefu_addtime;
	}
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
//	@Override
//	public String toString() {
//		return "DetailUser [credit_jifen=" + credit_jifen + ", credit_pic="
//				+ credit_pic + ", vip_status=" + vip_status
//				+ ", vip_verify_time=" + vip_verify_time + ", kefu_addtime="
//				+ kefu_addtime + ", provincetext=" + provincetext
//				+ ", citytext=" + citytext + ", areatext=" + areatext
//				+ ", typename=" + typename + ", getUserId()=" + getUserId()
////				+ ", getType_id()=" + getType_id() + ", getOrder()="
////				+ getOrder() + ", getPurview()=" + getPurview()
//				+ ", getUsername()=" + getUsername() + ", getPassword()="
//				+ getPassword() + ", getPaypassword()=" + getPaypassword()
//				+ ", getIslock()=" + getIslock() + ", getInviteUserid()="
//				+ getInviteId().getInviteUser().getUserId() + ", getInviteMoney()="
//				+ getInviteMoney() + ", getRealStatus()=" + getRealStatus()
//				+ ", getCard_type()=" + getCardType() + ", getCardId()="
//				+ getCardId()+ ", getCardPic1()=" + getCardPic1()
//				+ ", getCardPic2()=" + getCardPic2() + ", getNation()="
//				+ getNation() + ", getRealname()=" + getRealname()
////				+ ", getIntegral()=" + getIntegral() 
//				+ ", getStatus()="
//				+ getStatus() + ", getAvatarStatus()=" + getAvatarStatus()
//				+ ", getEmailStatus()=" + getEmailStatus()
//				+ ", getPhoneStatus()=" + getPhoneStatus()
//				+ ", getVideoStatus()=" + getVideoStatus()
//				+ ", getSceneStatus()=" + getSceneStatus() + ", getEmail()="
//				+ getEmail() + ", getSex()=" + getSex()
////				+ ", getLitpic()="
////				+ getLitpic() + ", getTel()=" + getTel() 
//				+ ", getPhone()="
//				+ getPhone()
////				+ ", getQq()=" + getQq() + ", getWangwang()="
////				+ getWangwang() 
//				+ ", getQuestion()=" + getQuestion()
//				+ ", getAnswer()=" + getAnswer() + ", getBirthday()="
//				+ getBirthday() + ", getProvince()=" + getProvince()
//				+ ", getCity()=" + getCity() + ", getArea()=" + getArea()
//				+ ", getAddress()=" + getAddress() + ", getRemind()="
//				+ getRemind() + ", getPrivacy()=" + getPrivacy()
//				+ ", getLogintime()=" + getLogintime() + ", getAddtime()="
//				+ getAddtime() + ", getAddip()=" + getAddip()
//				+ ", getUptime()=" + getUptime() + ", getUpip()=" + getUpip()
//				+ ", getLasttime()=" + getLasttime() + ", getLastip()="
//				+ getLastip() 
////				+ ", getIs_phone()=" + getIs_phone()
////				+ ", getMemberLevel()=" + getMemberLevel()
//				+ ", getSerialId()=" + getSerialId()
//				+ ", getSceneStatus()=" + getSceneStatus()
//				+ ", getInvite_username()=" + this.getInviteUser().getUsername()
//				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
//				+ ", toString()=" + super.toString() + "]";
//	}
	
	
	
	
	
}
