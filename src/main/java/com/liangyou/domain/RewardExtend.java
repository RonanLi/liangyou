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
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz 
 * 奖励发放实体
 * @author wujing
 *
 */
@Entity()
@Table(name = Global.TABLE_PREFIX +  "reward_extend")
public class RewardExtend implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2084043749230138845L;
	
	public RewardExtend(){
		
	}
	
	public RewardExtend(long id){
		this.id = id;
	}
	
	public RewardExtend(int rewardType, double rewardMoney, int status, User rewardUser, String content,Date addTime){
		this.rewardType=rewardType;
		this.status = status;
		this.rewardMoney = rewardMoney;
		this.rewardUser = rewardUser;
		this.addTime = addTime;
		this.content = content;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private int rewardType;
	
	private double rewardMoney;
	
	private int status;
	 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="reward_user_id")
	private User rewardUser;
	
	private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date extendTime;   //发放时间
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;
	
	private String  verifyContent;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user_id")
	private User verifyUser;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_user_id")
	private User tenderUser;
	

	private int validDay;  //有效期
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	private String orderNo;
	
	private int rewardWay;  //类型：0：直接奖励，1：红包
	
	//TGPROJECT-366  设置有效期
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;    //红包结束时间
	
	private int validType;    //有效期类型 1：按天，2：按月 ,3：长期
	//TGPROJECT-366  设置有效期
	
	

	public String getOrderNo() {
		return orderNo;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getValidType() {
		return validType;
	}

	public void setValidType(int validType) {
		this.validType = validType;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}

	public double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getRewardUser() {
		return rewardUser;
	}

	public void setRewardUser(User rewardUser) {
		this.rewardUser = rewardUser;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getExtendTime() {
		return extendTime;
	}

	public void setExtendTime(Date extendTime) {
		this.extendTime = extendTime;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
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

	public int getValidDay() {
		return validDay;
	}

	public void setValidDay(int validDay) {
		this.validDay = validDay;
	}

	public User getTenderUser() {
		return tenderUser;
	}

	public void setTenderUser(User tenderUser) {
		this.tenderUser = tenderUser;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public int getRewardWay() {
		return rewardWay;
	}

	public void setRewardWay(int rewardWay) {
		this.rewardWay = rewardWay;
	}
	
	
	
}
