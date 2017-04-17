package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="account_web_deduct")
public class AccountWebDeduct implements Serializable {
	private static final long serialVersionUID = 123432422342L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	private double money;

	private String remark;

	private int status;

	@Column(name="trade_no")
	private String tradeNo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="add_time")
	private Date addTime;

	@Column(name="add_userid")
	private long addUserid;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="dedcut_account")
	private String dedcutAccount;

	public String getDedcutAccount() {
		return dedcutAccount;
	}

	public void setDedcutAccount(String dedcutAccount) {
		this.dedcutAccount = dedcutAccount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Date getAddTime() {
		return addTime;
	}
	
	public long getAddUserid() {
		return addUserid;
	}

	public void setAddUserid(long addUserid) {
		this.addUserid = addUserid;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}