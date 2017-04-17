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
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name="invite_user")
public class InviteUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;  //被推荐人

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="invite_user", updatable=false)
	private User inviteUser;  //推荐人

	private int status;

	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	//被推荐人是否投过标
	@JoinColumn(name="is_tender")
	private int isTender;

	public int getIsTender() {
		return isTender;
	}
	public void setIsTender(int isTender) {
		this.isTender = isTender;
	}
	//投标总额
	@JoinColumn(name="tender_total")
	private double tenderTotal;
	
	public double getTenderTotal() {
		return tenderTotal;
	}
	public void setTenderTotal(double tenderTotal) {
		this.tenderTotal = tenderTotal;
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start


	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String addip;

	public InviteUser(){}

	public InviteUser(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getInviteUser() {
		return inviteUser;
	}

	public void setInviteUser(User inviteUser) {
		this.inviteUser = inviteUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}



}
