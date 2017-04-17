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

import com.liangyou.util.DateUtils;


/**
 * The persistent class for the account_recharge database table.
 * 
 */
@Entity
@Table(name="account_recharge")
public class AccountRecharge implements Serializable {
	private static final long serialVersionUID = 123432422342L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double fee;

	private double money;

	private String payment;

	private String remark;

	@Column(name="return_txt")
	private String returnTxt;

	private int status;//1：充值成功 0：充值失败


	@Column(name="trade_no")
	private String tradeNo;
	
	@Column(name="serial_no")
	private String serialNo; //易极付处理产生的流水号

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	private String type;

	@Column(name="verify_remark")
	private String verifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verify_time")
	private Date verifyTime;

	@Column(name="verify_userid")
	private int verifyUserid;
	private String batch_no;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_bank")
	private AccountBank accountBank;
	// v1.8.0.4_u1 TGPROJECT-221 lx start
	public String getStatusName(){
			try {
				int status=this.status;
				if(status==1){
					return "充值成功";
				}
				if(status==2){
					return "充值失败";
				}
				if(status==8){
					return "第三方处理中";
				}
				if(status==0){
					if(new Date().getTime()>DateUtils.rollDay(this.getAddtime(),1).getTime()){
						return "充值失败";
					}
					return "系统处理中";
				}
			} catch (Exception e) {
				return "充值失败";
			}
			return "充值失败";
	}
	// v1.8.0.4_u1 TGPROJECT-221 lx start
	public AccountRecharge() {
	}

	public int getId() {
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

	public double getFee() {
		return this.fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getPayment() {
		return this.payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReturnTxt() {
		return this.returnTxt;
	}

	public void setReturnTxt(String returnTxt) {
		this.returnTxt = returnTxt;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public int getVerifyUserid() {
		return this.verifyUserid;
	}

	public void setVerifyUserid(int verifyUserid) {
		this.verifyUserid = verifyUserid;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	public AccountBank getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(AccountBank accountBank) {
		this.accountBank = accountBank;
	}

	public String getStatusStr(){
		if(this.status ==1){
			return "充值成功";
		}else if(this.status ==0){
			return "充值失败";
		}else{
			return "未审核";
		}
	}
	
	public String getTypeStr(){
		if(this.type.equals("1")){
			return "网上充值";
		}else if(this.type.equals("2")){
			return "线下充值";
		}else if(this.type.equals("8")){
			return "汇款充值";
		}else{
			return "-";
		}
	}

	public String getBatch_no() {
		return batch_no;
	}
	
	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}
}
