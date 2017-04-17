package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the usertrack database table.
 * 
 */
@Entity
public class Usertrack implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;


	@Column(name="user_id")
	private Long userId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="login_time")
	private Date loginTime;

	@Column(name="long_ip")
	private String longIp;

	
	public Usertrack() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getLongIp() {
		return this.longIp;
	}

	public void setLongIp(String longIp) {
		this.longIp = longIp;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}