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
 * The persistent class for the credit_type database table.
 * 
 */
@Entity
@Table(name="credit_type")
public class CreditType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private int cycle;

	private int interval;

	private String name;

	private String nid;

	@Column(name="op_user")
	private int opUser;

	private String remark;

	private String updateip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;

	private int value;

	public CreditType() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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

	public int getCycle() {
		return this.cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public int getInterval() {
		return this.interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNid() {
		return this.nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
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

	public String getUpdateip() {
		return this.updateip;
	}

	public void setUpdateip(String updateip) {
		this.updateip = updateip;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}