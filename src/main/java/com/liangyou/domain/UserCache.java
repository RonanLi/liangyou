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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the user_cache database table.
 * 
 */
@Entity
@Table(name="user_cache")
public class UserCache implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int credit;

	@Column(name="friends_apply")
	private int friendsApply;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="kefu_addtime")
	private Date kefuAddtime;

	@Column(name="kefu_userid")
	private int kefuUserid;

	@Column(name="kefu_username")
	private String kefuUsername;

	@Column(name="vip_money")
	private String vipMoney;

	@Column(name="vip_remark")
	private String vipRemark;

	@Column(name="vip_status")
	private int vipStatus;

	@Column(name="vip_verify_remark")
	private String vipVerifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="vip_verify_time")
	private Date vipVerifyTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="vip_end_time")
	private Date vipEndTime;

	@OneToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="user_id", updatable=false)  
	private User user;

	//v1.8.0.4 TGPROJECT-46 
	@Column(name="cash_forbid")
	private int cashForbid;
	
	public int getCashForbid() {
		return cashForbid;
	}

	public void setCashForbid(int cashForbid) {
		this.cashForbid = cashForbid;
	}
	//v1.8.0.4 TGPROJECT-46 
	public UserCache() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCredit() {
		return this.credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getFriendsApply() {
		return this.friendsApply;
	}

	public void setFriendsApply(int friendsApply) {
		this.friendsApply = friendsApply;
	}

	public Date getKefuAddtime() {
		return this.kefuAddtime;
	}

	public void setKefuAddtime(Date kefuAddtime) {
		this.kefuAddtime = kefuAddtime;
	}

	public int getKefuUserid() {
		return this.kefuUserid;
	}

	public void setKefuUserid(int kefuUserid) {
		this.kefuUserid = kefuUserid;
	}

	public String getKefuUsername() {
		return this.kefuUsername;
	}

	public void setKefuUsername(String kefuUsername) {
		this.kefuUsername = kefuUsername;
	}

	public String getVipMoney() {
		return this.vipMoney;
	}

	public void setVipMoney(String vipMoney) {
		this.vipMoney = vipMoney;
	}

	public String getVipRemark() {
		return this.vipRemark;
	}

	public void setVipRemark(String vipRemark) {
		this.vipRemark = vipRemark;
	}

	public int getVipStatus() {
		return this.vipStatus;
	}

	public void setVipStatus(int vipStatus) {
		this.vipStatus = vipStatus;
	}

	public String getVipVerifyRemark() {
		return this.vipVerifyRemark;
	}

	public Date getVipEndTime() {
		return vipEndTime;
	}

	public void setVipEndTime(Date vipEndTime) {
		this.vipEndTime = vipEndTime;
	}

	public void setVipVerifyRemark(String vipVerifyRemark) {
		this.vipVerifyRemark = vipVerifyRemark;
	}

	public Date getVipVerifyTime() {
		return this.vipVerifyTime;
	}

	public void setVipVerifyTime(Date vipVerifyTime) {
		this.vipVerifyTime = vipVerifyTime;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}