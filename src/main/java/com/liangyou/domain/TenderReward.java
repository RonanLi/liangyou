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
 * //1.8.0.4_u3   TGPROJECT-337  qinjun 2014-06-23  start
 * @author wujing
 *投标奖励记录
 */
@Entity
@Table(name="tender_reward")
public class TenderReward implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5518912281155376594L;

	public TenderReward(){
		
	}
	
	public TenderReward(long id){
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_user_id")
	private User tenderUser;    //投资人
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User  rewardUser;   //受奖励人
	
	private int status;
	
	private double tenderMoney;
	
	private double rewardMoney;  //理论奖励金额
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date verifyTime;
	
	private double extendMoney;   //发放金额
	
	private String verifyContent;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user")
	private User verifyUser;
	
	private String verifyIp;  //审核ip

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public User getTenderUser() {
		return tenderUser;
	}

	public void setTenderUser(User tenderUser) {
		this.tenderUser = tenderUser;
	}

	public User getRewardUser() {
		return rewardUser;
	}

	public void setRewardUser(User rewardUser) {
		this.rewardUser = rewardUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	public double getExtendMoney() {
		return extendMoney;
	}

	public void setExtendMoney(double extendMoney) {
		this.extendMoney = extendMoney;
	}

	public String getVerifyContent() {
		return verifyContent;
	}

	public void setVerifyContent(String verifyContent) {
		this.verifyContent = verifyContent;
	}

	public User getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}

	public String getVerifyIp() {
		return verifyIp;
	}

	public void setVerifyIp(String verifyIp) {
		this.verifyIp = verifyIp;
	}

	
	
}
