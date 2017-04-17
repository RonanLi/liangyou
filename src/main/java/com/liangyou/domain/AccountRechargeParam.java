package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account_recharge_param")
public class AccountRechargeParam {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String orderno;
	private String serialno;
	private Date addtime;
	@Column(name="recharge_id")
	private int rechargeId;
	public AccountRechargeParam() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(int rechargeId) {
		this.rechargeId = rechargeId;
	}

}
