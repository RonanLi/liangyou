package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 用于申请加盟  ：启道资本
 * @author wujing
 *TGPROJECT-359
 */
@Entity
@Table(name = "user_joined")
public class UserJoined implements Serializable {

	private static final long serialVersionUID = 6845218578476172426L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id",updatable=false)
	private User joinUser;  //申请用户
	
	private String userName;  //用户名，便于json输出时取值
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="province_id")
	private Area province;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="city_id")
	private Area city;
	
	private String remark;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date verifyTime;
	
	private String verifyRemark;
	
	private int status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user")
	private User verifyUser;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getJoinUser() {
		return joinUser;
	}

	public void setJoinUser(User joinUser) {
		this.joinUser = joinUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Area getProvince() {
		return province;
	}

	public void setProvince(Area province) {
		this.province = province;
	}

	public Area getCity() {
		return city;
	}

	public void setCity(Area city) {
		this.city = city;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	public String getVerifyRemark() {
		return verifyRemark;
	}

	public void setVerifyRemark(String verifyRemark) {
		this.verifyRemark = verifyRemark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}
	
	
}
