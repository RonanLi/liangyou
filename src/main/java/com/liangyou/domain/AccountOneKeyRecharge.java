package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="account_onekey_recharge")
public class AccountOneKeyRecharge implements Serializable{

	/**
	 * add by lijing 一键充值
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String phone;
	private double money;
	public AccountOneKeyRecharge() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AccountOneKeyRecharge(long id, String phone, double money) {
		super();
		this.id = id;
		this.phone = phone;
		this.money = money;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	
}
