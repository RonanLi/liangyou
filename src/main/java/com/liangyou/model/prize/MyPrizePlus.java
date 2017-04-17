package com.liangyou.model.prize;

import java.util.Date;

import com.liangyou.domain.ExperienceMoney;
//add by lxm 新版我的奖品   2016年12月19日11:41:51
public class MyPrizePlus {

	private static final long serialVersionUID = 1L;
	private String prizeName;	// 奖品名称
	private Date time;		// 奖品获取时间
	private String prizeStatus;	// 奖品状态
	private String imgUrl; // 图片地址
	private String exchangeCode;// 兑换码
	private double experienceMoney;//体验金金额
	private int emReceiveStatus;//体验金领取状态
	private double experienceInterest; // 体验金利息
	private int interestUseStatus; // 体验金利息使用状态 0 未使用 1已使用
	private int interestIncomeStatus = 0; // 体验金利息收益状态 0 未收益 1 已收益
	private int timeLimit; // 体验金期限
	
	
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public int getEmReceiveStatus() {
		return emReceiveStatus;
	}
	public void setEmReceiveStatus(int emReceiveStatus) {
		this.emReceiveStatus = emReceiveStatus;
	}
	public double getExperienceInterest() {
		return experienceInterest;
	}
	public void setExperienceInterest(double experienceInterest) {
		this.experienceInterest = experienceInterest;
	}
	public int getInterestUseStatus() {
		return interestUseStatus;
	}
	public void setInterestUseStatus(int interestUseStatus) {
		this.interestUseStatus = interestUseStatus;
	}
	public int getInterestIncomeStatus() {
		return interestIncomeStatus;
	}
	public void setInterestIncomeStatus(int interestIncomeStatus) {
		this.interestIncomeStatus = interestIncomeStatus;
	}
	public int getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getPrizeStatus() {
		return prizeStatus;
	}
	public void setPrizeStatus(String prizeStatus) {
		this.prizeStatus = prizeStatus;
	}
	public String getExchangeCode() {
		return exchangeCode;
	}
	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	
	public double getExperienceMoney() {
		return experienceMoney;
	}
	public void setExperienceMoney(double experienceMoney) {
		this.experienceMoney = experienceMoney;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	
	
}
