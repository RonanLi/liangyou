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
import javax.persistence.Table;

 


/**
 * The persistent class for the user_amount database table.
 * 
 */
@Entity
@Table(name="user_amount")
public class UserAmount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private double credit;

	@Column(name="credit_nouse")
	private double creditNouse;

	@Column(name="credit_use")
	private double creditUse;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;

	public UserAmount() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getCredit() {
		return this.credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getCreditNouse() {
		return this.creditNouse;
	}

	public void setCreditNouse(double creditNouse) {
		this.creditNouse = creditNouse;
	}

	public double getCreditUse() {
		return this.creditUse;
	}

	public void setCreditUse(double creditUse) {
		this.creditUse = creditUse;
	}


	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}