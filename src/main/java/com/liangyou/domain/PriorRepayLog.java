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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the prior_repay_log database table.
 * 
 */
@Entity
@Table(name="prior_repay_log")
public class PriorRepayLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repaymnet_id")
	private BorrowRepayment borrowRepayment;
	
	private String type;

	private double account;
	
	private double money;

	private String addip;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public PriorRepayLog(){
	}
	
	public PriorRepayLog(Borrow borrow,
			BorrowRepayment borrowRepayment, String addip) {
		super();
		this.borrow = borrow;
		this.borrowRepayment = borrowRepayment;
		this.addip = addip;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public BorrowRepayment getBorrowRepayment() {
		return borrowRepayment;
	}

	public void setBorrowRepayment(BorrowRepayment borrowRepayment) {
		this.borrowRepayment = borrowRepayment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAccount() {
		return account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	
	
}
