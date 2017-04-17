package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;


/**
 *  操作生利宝记录 TGPROJECT-314 qj 2014-05-30 add
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "interest_generate")
public class InterestGenerate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	private double amount;
	
	private int status;
	
	private String type;//in：转入 out：转出

	@Column(name="ord_id")
	private String ordId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date calltime;

	public InterestGenerate(){
		
	}
	
	public InterestGenerate(User user, String ordId) {
		super();
		this.user = user;
		this.ordId = ordId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public String getType() {
		return type;
	}

	public Date getCalltime() {
		return calltime;
	}

	public void setCalltime(Date calltime) {
		this.calltime = calltime;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
