package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the credit_log database table.
 * 用户信用积分和客户星级积分一样
 */
@Entity
@Table(name="credit_log")
public class CreditLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private int op;

	@Column(name="op_user")
	private int opUser;

	private String remark;

	@Column(name="type_id")
	private int typeId;

	@Column(name="user_id")
	private long userId;

	private int value;
	
	public CreditLog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getOp() {
		return this.op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public int getOpUser() {
		return this.opUser;
	}

	public void setOpUser(int opUser) {
		this.opUser = opUser;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	
}