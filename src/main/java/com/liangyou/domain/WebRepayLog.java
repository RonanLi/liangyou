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
 * 归还网站垫付扣款记录
 * @author wujing
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "web_repay_log")
public class WebRepayLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4265651905353240719L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	public WebRepayLog(){
		
	}
	
	public WebRepayLog(double money, Date addTime, String content,Borrow borrow, BorrowRepayment repayment){
		this.borrow =borrow;
		this.repayment = repayment;
		this.addTime = addTime;
		this.content = content;
		this.money = money;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repayment_id")
	private BorrowRepayment repayment;
	
	private double money;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;
	
	private String content;
	
	private String ordNo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrdNo() {
		return ordNo;
	}

	public void setOrdNo(String ordNo) {
		this.ordNo = ordNo;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public BorrowRepayment getRepayment() {
		return repayment;
	}

	public void setRepayment(BorrowRepayment repayment) {
		this.repayment = repayment;
	}

}
