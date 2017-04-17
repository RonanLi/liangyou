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

import com.liangyou.context.Global;

/**
 * 系统垫付，自动扣款
 * @author wujing
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "web_paid")
public class WebPaid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1216934190541274975L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	public WebPaid(){
		
	}
	
	public WebPaid( double webAccount, double waitRepay ,Borrow borrow, BorrowRepayment repayment){
		this.webAccount = webAccount;
		this.waitRepay = waitRepay;
		this.borrow = borrow;
		this.repayment = repayment;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repay_id")
	private BorrowRepayment repayment;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	private double webAccount;  //网站垫付总额
	
	private double yesRepayAccount;    //用户已还垫付总额
	
	private double waitRepay;         //剩余待还垫付总额
	
	private double autoRepay;    //自动扣款总额
	
	private double manualRepay;   //手动还款总额
	
	private double webPayAccount; // 平台垫付本金
	
	private double webPayInterest;  // 平台垫付利息
	
	private double webPayPenalty;  //平台垫付罚息
	
	private double waitReceivePenal;   //平台待收罚息总额
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;   //添加时间

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public double getWebAccount() {
		return webAccount;
	}

	public void setWebAccount(double webAccount) {
		this.webAccount = webAccount;
	}

	public double getYesRepayAccount() {
		return yesRepayAccount;
	}

	public void setYesRepayAccount(double yesRepayAccount) {
		this.yesRepayAccount = yesRepayAccount;
	}

	public double getWaitRepay() {
		return waitRepay;
	}

	public void setWaitRepay(double waitRepay) {
		this.waitRepay = waitRepay;
	}

	public double getAutoRepay() {
		return autoRepay;
	}

	public void setAutoRepay(double autoRepay) {
		this.autoRepay = autoRepay;
	}

	public double getManualRepay() {
		return manualRepay;
	}

	public void setManualRepay(double manualRepay) {
		this.manualRepay = manualRepay;
	}

	public BorrowRepayment getRepayment() {
		return repayment;
	}

	public void setRepayment(BorrowRepayment repayment) {
		this.repayment = repayment;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public double getWebPayAccount() {
		return webPayAccount;
	}

	public void setWebPayAccount(double webPayAccount) {
		this.webPayAccount = webPayAccount;
	}

	public double getWebPayInterest() {
		return webPayInterest;
	}

	public void setWebPayInterest(double webPayInterest) {
		this.webPayInterest = webPayInterest;
	}

	public double getWebPayPenalty() {
		return webPayPenalty;
	}

	public void setWebPayPenalty(double webPayPenalty) {
		this.webPayPenalty = webPayPenalty;
	}

	public double getWaitReceivePenal() {
		return waitReceivePenal;
	}

	public void setWaitReceivePenal(double waitReceivePenal) {
		this.waitReceivePenal = waitReceivePenal;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	
	
}
