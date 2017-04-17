package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * The persistent class for the account database table.
 */
@Entity
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private double collection;

	@Column(name = "no_use_money")
	private double noUseMoney;

	@Column(name = "total")
	private double total;

	@Column(name = "use_money")
	private double useMoney;

	@Column(name = "repay")
	private double repay;

	@Column(name = "free_cash_money")
	private double freeCashMoney;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = false)
	private User user;

	public Account() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getCollection() {
		return this.collection;
	}

	public void setCollection(double collection) {
		this.collection = collection;
	}

	public double getNoUseMoney() {
		return this.noUseMoney;
	}

	public void setNoUseMoney(double noUseMoney) {
		this.noUseMoney = noUseMoney;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getUseMoney() {
		return this.useMoney;
	}

	public void setUseMoney(double useMoney) {
		this.useMoney = useMoney;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getRepay() {
		return repay;
	}

	public void setRepay(double repay) {
		this.repay = repay;
	}

	public double getOwnMoney() {
		return this.total - this.repay;
	}

	public double getRemainderMoney() {
		return this.useMoney + this.noUseMoney;
	}

	public double getFreeCashMoney() {
		return freeCashMoney;
	}

	public void setFreeCashMoney(double freeCashMoney) {
		this.freeCashMoney = freeCashMoney;
	}

}