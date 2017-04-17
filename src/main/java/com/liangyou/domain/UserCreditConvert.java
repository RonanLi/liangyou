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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author lx 
 *
 */
@Entity
@Table(name="user_credit_convert")
public class UserCreditConvert implements Serializable {
	private static final long serialVersionUID = -7724114936302705927L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="user_id", updatable=false)  
	private User user;
	
	@Column(name="credit_value")
	private int creditValue;
	
	@Column(name="convert_money")
	private double convertMoney;
	
	private double money;
	
	private int status;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verifytime")
	private Date verifytime;
	
	@Column(name="verify_user")
	private String verifyUsername;

	@OneToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="verify_user_id", updatable=false)  
	private User verifyUser;
	
	private String remark;
	
	
	@Column(name="convert_num")
	private int convertNum;

	private String type;
	
	@ManyToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="goods_id", updatable=false)  
	private Goods goods;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getCreditValue() {
		return creditValue;
	}

	public void setCreditValue(int creditValue) {
		this.creditValue = creditValue;
	}

	public double getConvertMoney() {
		return convertMoney;
	}

	public void setConvertMoney(double convertMoney) {
		this.convertMoney = convertMoney;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Date getVerifytime() {
		return verifytime;
	}

	public void setVerifytime(Date verifytime) {
		this.verifytime = verifytime;
	}

	public String getVerifyUsername() {
		return verifyUsername;
	}

	public void setVerifyUsername(String verifyUsername) {
		this.verifyUsername = verifyUsername;
	}

	public User getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getConvertNum() {
		return convertNum;
	}

	public void setConvertNum(int convertNum) {
		this.convertNum = convertNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	
}