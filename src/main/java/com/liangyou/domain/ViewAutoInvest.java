package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="view_auto_invest")
public class ViewAutoInvest implements Serializable {
	private static final long serialVersionUID = 112321313L;
	
	@Id
	private int id;
	@Column(name="user_id")  
	private long userId;
	
	private int status;
	
	@Column(name="tender_account")
	private int tenderAccount;
	
	@Column(name="borrow_style_status")
	private int borrowStyleStatus;

	@Column(name="borrow_style")
	private String borrowStyle;
	
	@Column(name="timelimit_status")
	private int timelimitStatus;

	@Column(name="timelimit_month_first")
	private int timelimitMonthFirst;

	@Column(name="timelimit_month_last")
	private int timelimitMonthLast;
	
	@Column(name="timelimit_day_first")
	private int timelimitDayFirst;
	
	@Column(name="timelimit_day_last")
	private int timelimitDayLast;
	
	@Column(name="apr_status")
	private int aprStatus;
	
	@Column(name="apr_first")
	private int aprFirst;

	@Column(name="apr_last")
	private int aprLast;
	
	@Column(name="award_status")
	private int awardStatus;

	private double award;

	@Column(name="borrow_type")
	private String borrowType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public ViewAutoInvest() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTenderAccount() {
		return tenderAccount;
	}

	public void setTenderAccount(int tenderAccount) {
		this.tenderAccount = tenderAccount;
	}

	public int getBorrowStyleStatus() {
		return borrowStyleStatus;
	}

	public void setBorrowStyleStatus(int borrowStyleStatus) {
		this.borrowStyleStatus = borrowStyleStatus;
	}

	public int getTimelimitStatus() {
		return timelimitStatus;
	}

	public void setTimelimitStatus(int timelimitStatus) {
		this.timelimitStatus = timelimitStatus;
	}

	public int getTimelimitMonthFirst() {
		return timelimitMonthFirst;
	}

	public void setTimelimitMonthFirst(int timelimitMonthFirst) {
		this.timelimitMonthFirst = timelimitMonthFirst;
	}

	public int getTimelimitMonthLast() {
		return timelimitMonthLast;
	}

	public void setTimelimitMonthLast(int timelimitMonthLast) {
		this.timelimitMonthLast = timelimitMonthLast;
	}

	public int getTimelimitDayFirst() {
		return timelimitDayFirst;
	}

	public void setTimelimitDayFirst(int timelimitDayFirst) {
		this.timelimitDayFirst = timelimitDayFirst;
	}

	public int getTimelimitDayLast() {
		return timelimitDayLast;
	}

	public void setTimelimitDayLast(int timelimitDayLast) {
		this.timelimitDayLast = timelimitDayLast;
	}

	public int getAprStatus() {
		return aprStatus;
	}

	public void setAprStatus(int aprStatus) {
		this.aprStatus = aprStatus;
	}

	public int getAprFirst() {
		return aprFirst;
	}

	public void setAprFirst(int aprFirst) {
		this.aprFirst = aprFirst;
	}

	public int getAprLast() {
		return aprLast;
	}

	public void setAprLast(int aprLast) {
		this.aprLast = aprLast;
	}

	public int getAwardStatus() {
		return awardStatus;
	}

	public void setAwardStatus(int awardStatus) {
		this.awardStatus = awardStatus;
	}

	public double getAward() {
		return award;
	}

	public void setAward(double award) {
		this.award = award;
	}
	
	public String getBorrowStyle() {
		return borrowStyle;
	}

	public void setBorrowStyle(String borrowStyle) {
		this.borrowStyle = borrowStyle;
	}

	public String getBorrowType() {
		return borrowType;
	}

	public void setBorrowType(String borrowType) {
		this.borrowType = borrowType;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	
}