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
 * The persistent class for the user_amountapply database table.
 * 
 */
@Entity
@Table(name="user_amountapply")
public class UserAmountApply implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private double account;

	@Column(name="account_new")
	private double accountNew;

	@Column(name="account_old")
	private double accountOld;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String content;

	private String remark;

	private int status;

	private String type;

	@Column(name="verify_remark")
	private String verifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verify_time")
	private Date verifyTime;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user")
	private User verifyUser;

	//bi-directional many-to-one association to User
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="pass_account")
	private double passAccount;  //当前审核操作通过的额度
	@Column(name="give_account")
	private double giveAccount;   //当前审核操作奖励的积分    

	public UserAmountApply() {
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

	public double getAccountNew() {
		return this.accountNew;
	}

	public void setAccountNew(double accountNew) {
		this.accountNew = accountNew;
	}

	public double getAccountOld() {
		return this.accountOld;
	}

	public void setAccountOld(double accountOld) {
		this.accountOld = accountOld;
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

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVerifyRemark() {
		return this.verifyRemark;
	}

	public void setVerifyRemark(String verifyRemark) {
		this.verifyRemark = verifyRemark;
	}

	public Date getVerifyTime() {
		return this.verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	

	public User getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getPassAccount() {
		return passAccount;
	}

	public void setPassAccount(double passAccount) {
		this.passAccount = passAccount;
	}

	public double getGiveAccount() {
		return giveAccount;
	}

	public void setGiveAccount(double giveAccount) {
		this.giveAccount = giveAccount;
	}

	
	

}