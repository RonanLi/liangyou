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


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = "web_glod_log")
public class WebGlodLog implements Serializable {
	private static final long serialVersionUID = 1L;

	public WebGlodLog() {
	}
	
	public WebGlodLog(User user,User toUser,String type,double money,String addip,String ordId) {
		this.user = user;
		this.toUser = toUser;
		this.type = type;
		this.money = money;
		this.addip = addip;
		this.ordId = ordId;
		this.addtime = new Date();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="to_user_id")
	private User toUser;
	
	private String type;
	
	private Date addtime;
	
	private String addip;
	
	private String memo;
	
	@Column(name="ord_id")
	private String ordId;
	
	private double money;
	
	private double fee;

	private int status;
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public String getType() {
		return type;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public String getTypeStr(){
		if(this.type.equals("recharge")){
			return "充值";
		}else if(this.type.equals("cash")){
			return "提现";
		}else{
			return "-";
		}
	}
	
}
