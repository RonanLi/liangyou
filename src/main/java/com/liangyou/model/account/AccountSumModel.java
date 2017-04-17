package com.liangyou.model.account;

import com.liangyou.domain.Account;

public class AccountSumModel extends Account {
	
	private static final long serialVersionUID = 4038060732774453500L;
	
	private double wait_collect;
	
	private double wait_repay;
	
	private String username;
	
	private String realname;
	
	private String userName;
	private String userId;
	private String freezeAccount;
	private String freezeCount;
	private String yesCapital;
	private String yesInterest;
	private String yesTenderTotal;
	private String yesTenderTimes;
	
	

	public double getWait_collect() {
		return wait_collect;
	}
	public void setWait_collect(double wait_collect) {
		this.wait_collect = wait_collect;
	}
	public double getWait_repay() {
		return wait_repay;
	}
	public void setWait_repay(double wait_repay) {
		this.wait_repay = wait_repay;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFreezeAccount() {
		return freezeAccount;
	}
	public void setFreezeAccount(String freezeAccount) {
		this.freezeAccount = freezeAccount;
	}
	public String getFreezeCount() {
		return freezeCount;
	}
	public void setFreezeCount(String freezeCount) {
		this.freezeCount = freezeCount;
	}
	public String getYesCapital() {
		return yesCapital;
	}
	public void setYesCapital(String yesCapital) {
		this.yesCapital = yesCapital;
	}
	public String getYesInterest() {
		return yesInterest;
	}
	public void setYesInterest(String yesInterest) {
		this.yesInterest = yesInterest;
	}
	public String getYesTenderTotal() {
		return yesTenderTotal;
	}
	public void setYesTenderTotal(String yesTenderTotal) {
		this.yesTenderTotal = yesTenderTotal;
	}
	public String getYesTenderTimes() {
		return yesTenderTimes;
	}
	public void setYesTenderTimes(String yesTenderTimes) {
		this.yesTenderTimes = yesTenderTimes;
	}
	
	
}
