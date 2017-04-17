package com.liangyou.model.account;

import com.liangyou.domain.Account;

public class AccountModel extends Account {

	private static final long serialVersionUID = 3261895775405456319L;
	
	private String bank;
	private String bankaccount;
	private String bankname;
	private String branch;
	private String province;
	private String city;
	private String area;
	private String addtime;
	private String addip;
	private String username;
	private String realname;
	
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getAddip() {
		return addip;
	}
	public void setAddip(String addip) {
		this.addip = addip;
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
	//业务方法
	/**
	 * 冻结资金
	 * @param money
	 */
	public synchronized void freeze(double money){
		this.setNoUseMoney(this.getNoUseMoney()+money);
		this.setUseMoney(this.getUseMoney()-money);
	}
	

}
