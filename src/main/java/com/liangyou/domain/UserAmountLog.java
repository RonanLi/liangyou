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

/**
 * The persistent class for the user_amountlog database table.
 * 
 */
@Entity
@Table(name="user_amountlog")
public class UserAmountLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private double account;

	@Column(name="account_nouse")
	private double accountNouse;

	@Column(name="account_total")
	private double accountTotal;

	@Column(name="account_type")
	private String accountType;

	@Column(name="account_use")
	private double accountUse;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String remark;

	private String type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	public UserAmountLog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getAccount() {
		return this.account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getAccountNouse() {
		return this.accountNouse;
	}

	public void setAccountNouse(double accountNouse) {
		this.accountNouse = accountNouse;
	}

	public double getAccountTotal() {
		return this.accountTotal;
	}

	public void setAccountTotal(double accountTotal) {
		this.accountTotal = accountTotal;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getAccountUse() {
		return this.accountUse;
	}

	public void setAccountUse(double accountUse) {
		this.accountUse = accountUse;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}