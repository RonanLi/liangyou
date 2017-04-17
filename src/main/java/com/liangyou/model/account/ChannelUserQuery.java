package com.liangyou.model.account;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.liangyou.domain.Borrow;

public class ChannelUserQuery extends Borrow {

	private static final long serialVersionUID = 1L;

	private long userId;
	private String userName; // 用户名
	private String borrowName; // 标的名称
	private double borrowApr; // 预期年化收益率(%)
	private double tenderMoney; // 投资金额
	private double tenderYescapital; // 投资本金
	private double redPakcageMoney; // 红包使用金额
	private Date tenderAddtime; // 投标时间
	private Date borrowFulltime; // 满标时间
	private double addInterest; // 加息百分比(%)
	private double addAccount; // 加息收益
	private String fromBe; // 渠道来源
	private String assignmenState; // 转让状态

	private String timeLimitStr; // 标的期限显示

	private String channel; // 渠道码
	private String channelCode; // 渠道所属Code
	private String channelName; // 渠道名称

	private long peopleNum; // 投资人数
	private double investTotal; // 投标总额

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBorrowName() {
		return borrowName;
	}

	public void setBorrowName(String borrowName) {
		this.borrowName = borrowName;
	}

	public double getBorrowApr() {
		return borrowApr;
	}

	public void setBorrowApr(double borrowApr) {
		this.borrowApr = borrowApr;
	}

	public double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public double getTenderYescapital() {
		return tenderYescapital;
	}

	public void setTenderYescapital(double tenderYescapital) {
		this.tenderYescapital = tenderYescapital;
	}

	public double getRedPakcageMoney() {
		return redPakcageMoney;
	}

	public void setRedPakcageMoney(double redPakcageMoney) {
		this.redPakcageMoney = redPakcageMoney;
	}

	public Date getTenderAddtime() {
		return tenderAddtime;
	}

	public void setTenderAddtime(Date tenderAddtime) {
		this.tenderAddtime = tenderAddtime;
	}

	public Date getBorrowFulltime() {
		return borrowFulltime;
	}

	public void setBorrowFulltime(Date borrowFulltime) {
		this.borrowFulltime = borrowFulltime;
	}

	public double getAddInterest() {
		return addInterest;
	}

	public void setAddInterest(double addInterest) {
		this.addInterest = addInterest;
	}

	public double getAddAccount() {
		return addAccount;
	}

	public void setAddAccount(double addAccount) {
		this.addAccount = addAccount;
	}

	public String getFromBe() {
		return fromBe;
	}

	public void setFromBe(String fromBe) {
		this.fromBe = fromBe;
	}

	public String getAssignmenState() {
		return assignmenState;
	}

	public void setAssignmenState(String assignmenState) {
		this.assignmenState = assignmenState;
	}

	public String getTimeLimitStr() {
		return timeLimitStr;
	}

	public void setTimeLimitStr(String timeLimitStr) {
		this.timeLimitStr = timeLimitStr;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public long getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(long peopleNum) {
		this.peopleNum = peopleNum;
	}

	public double getInvestTotal() {
		return investTotal;
	}

	public void setInvestTotal(double investTotal) {
		this.investTotal = investTotal;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
