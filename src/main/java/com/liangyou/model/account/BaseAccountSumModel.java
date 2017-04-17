package com.liangyou.model.account;

public class BaseAccountSumModel {
	public String userId;
	public String userName;
	public String realName;
	public String cardId; //身份证号
	public String phone;
	public int tenderingCount;//冻结中的投资次数
	public double tenderingMoney;//冻结中的投资总额
	
	public int tenderedCount;//成功投资的次数（投资金额已经被扣除）
	public double tenderedMoney;//成功投资的总额
	public double incomeCapital;//投资赎回本金总额
	public double incomeInterest;//投资赎回利息总额
	public double waitCapital; //投资待收本金总额
	public double waitInterest;//投资待收利息总额
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public int getTenderingCount() {
		return tenderingCount;
	}
	public void setTenderingCount(int tenderingCount) {
		this.tenderingCount = tenderingCount;
	}
	public double getTenderingMoney() {
		return tenderingMoney;
	}
	public void setTenderingMoney(double tenderingMoney) {
		this.tenderingMoney = tenderingMoney;
	}
	public int getTenderedCount() {
		return tenderedCount;
	}
	public void setTenderedCount(int tenderedCount) {
		this.tenderedCount = tenderedCount;
	}
	public double getTenderedMoney() {
		return tenderedMoney;
	}
	public void setTenderedMoney(double tenderedMoney) {
		this.tenderedMoney = tenderedMoney;
	}
	public double getIncomeCapital() {
		return incomeCapital;
	}
	public void setIncomeCapital(double incomeCapital) {
		this.incomeCapital = incomeCapital;
	}
	public double getIncomeInterest() {
		return incomeInterest;
	}
	public void setIncomeInterest(double incomeInterest) {
		this.incomeInterest = incomeInterest;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public double getWaitCapital() {
		return waitCapital;
	}
	public void setWaitCapital(double waitCapital) {
		this.waitCapital = waitCapital;
	}
	public double getWaitInterest() {
		return waitInterest;
	}
	public void setWaitInterest(double waitInterest) {
		this.waitInterest = waitInterest;
	}
	

}
