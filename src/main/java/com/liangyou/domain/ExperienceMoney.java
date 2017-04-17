package com.liangyou.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * add by gy 2016-10-14 18:08:05 体验金
 */
@Entity
@Table(name = "experience_money")
public class ExperienceMoney implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "experience_money")
	private double experienceMoney; // 体验金金额

	@Column(name = "receive_status")
	private int receiveStatus; // 领取状态 0 未领取 1 已领取

	@Column(name = "time_limit")
	private int timeLimit; // 体验金期限

	@Column(name = "use_status")
	private int useStatus; // 使用状态 0 未使用 1 已使用

	@Column(name = "experience_interest")
	private double experienceInterest; // 体验金利息

	@Column(name = "interest_use_status")
	private int interestUseStatus; // 体验金利息使用状态 0 未使用 1已使用

	@Column(name = "interest_income_status")
	private int interestIncomeStatus = 0; // 体验金利息收益状态 0 未收益 1 已收益

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "add_time")
	private Date addTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "use_time")
	private Date useTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "interest_use_time")
	private Date interestUseTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "interest_income_time")
	private Date interestIncomeTime; // 体验金收益时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getExperienceMoney() {
		return experienceMoney;
	}

	public void setExperienceMoney(double experienceMoney) {
		this.experienceMoney = experienceMoney;
	}

	public int getReceiveStatus() {
		return receiveStatus;
	}

	public void setReceiveStatus(int receiveStatus) {
		this.receiveStatus = receiveStatus;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}

	public double getExperienceInterest() {
		return experienceInterest;
	}

	public void setExperienceInterest(double experienceInterest) {
		this.experienceInterest = experienceInterest;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public int getInterestUseStatus() {
		return interestUseStatus;
	}

	public void setInterestUseStatus(int interestUseStatus) {
		this.interestUseStatus = interestUseStatus;
	}

	public Date getInterestUseTime() {
		return interestUseTime;
	}

	public void setInterestUseTime(Date interestUseTime) {
		this.interestUseTime = interestUseTime;
	}

	public int getInterestIncomeStatus() {
		return interestIncomeStatus;
	}

	public void setInterestIncomeStatus(int interestIncomeStatus) {
		this.interestIncomeStatus = interestIncomeStatus;
	}

	public Date getInterestIncomeTime() {
		return interestIncomeTime;
	}

	public void setInterestIncomeTime(Date interestIncomeTime) {
		this.interestIncomeTime = interestIncomeTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
