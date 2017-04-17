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

import com.liangyou.context.AccountLogTypeName;

/**
 * 网站垫付日志表  周学成
 * 
 */
@Entity
@Table(name="site_pay_log")
public class SitePayLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id", updatable=false)
	private BorrowTender  borrowTender;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id", updatable=false)
	private Borrow  borrow;
	
	private String type;

	@Column(name="money_pay")
	private double moneyPay;
	
	@Column(name="account_pay")
	private double accountPay;
	
	@Column(name="money_total")
	private double moneyTotal;
	
	@Column(name="account_total")
	private double accountTotal;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	public SitePayLog() {
		super();
	}

	public SitePayLog(long id) {
		super();
		this.id = id;
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

	public BorrowTender getBorrowTender() {
		return borrowTender;
	}

	public void setBorrowTender(BorrowTender borrowTender) {
		this.borrowTender = borrowTender;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getMoneyPay() {
		return moneyPay;
	}

	public void setMoneyPay(double moneyPay) {
		this.moneyPay = moneyPay;
	}

	public double getAccountPay() {
		return accountPay;
	}

	public void setAccountPay(double accountPay) {
		this.accountPay = accountPay;
	}

	public double getMoneyTotal() {
		return moneyTotal;
	}

	public void setMoneyTotal(double moneyTotal) {
		this.moneyTotal = moneyTotal;
	}

	public double getAccountTotal() {
		return accountTotal;
	}

	public void setAccountTotal(double accountTotal) {
		this.accountTotal = accountTotal;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
	/**
	 * 获取type对应 name(用于导出excel)
	 * @return
	 */
	public String getTypeName(){
		return  AccountLogTypeName.getInstance().typeNameMap.get(type);
	}
	
}