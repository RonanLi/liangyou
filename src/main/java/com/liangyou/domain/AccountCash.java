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
 * The persistent class for the account_cash database table.
 * 
 */
@Entity
@Table(name="account_cash")
public class AccountCash implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String addip;
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_bank_id")
	private AccountBank accountBank;
	
	private double credited;

	private double fee;

	private String honghao;

	private int status;	//1.成功，6.退回

	private double total;

	@Column(name="verify_remark")
	private String verifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verify_time")
	private Date verifyTime;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user_id")
	private User verifyUser;
	
	@Column(name="draw_type")
	private int drawType;
	
	@Column(name="order_no")
	private String orderNo;
	
	@Column(name="loan_no")
	private String loanNo;
	
	
	
	public String getLoanNo() {
		return loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	@Column(name="deal_status")
	private int dealStatus;//易及付取现处理状态，4代表申请成功，1，代表取现回调处理成功，2代表取现申请失败

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public AccountBank getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(AccountBank accountBank) {
		this.accountBank = accountBank;
	}

	public User getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}

	public AccountCash() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public double getCredited() {
		return this.credited;
	}
	public void setCredited(double credited) {
		this.credited = credited;
	}
	public double getFee() {
		return this.fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public String getHonghao() {
		return this.honghao;
	}

	public void setHonghao(String honghao) {
		this.honghao = honghao;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	public String getVerifyRemark() {
		return verifyRemark;
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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getDrawType() {
		return drawType;
	}

	public void setDrawType(int drawType) {
		this.drawType = drawType;
	}

	public String getStatusStr(){
		if(this.status == 0){
			return "审核中";
		}else if(this.status == 1){
			return "成功";
		}else if(this.status == 2){
			return "用户取消";
		}else{
			return "审核失败";
		}
	}

	public int getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(int dealStatus) {
		this.dealStatus = dealStatus;
	}
	
}