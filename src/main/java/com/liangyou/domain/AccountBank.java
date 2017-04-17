package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

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
 * The persistent class for the account_bank database table.
 * 
 */
@Entity
@Table(name="account_bank")
public class AccountBank implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String account;

	private String addip;
	
	private String bankName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bank")
	private DrawBank  bank;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="province")
	private AreaBank  province;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="city")
	private AreaBank  city;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="area")
	private AreaBank  area;
	

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mmmprovince")
	private AreaMmm  mmmprovince;
	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mmmcity")
	private AreaMmm  mmmcity;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mmmarea")
	private AreaMmm  mmmarea;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bank_mmm")
	private DrawBankMmm  bankMmm;
	
	public AreaMmm getMmmprovince() {
		return mmmprovince;
	}
	
	public void setMmmprovince(AreaMmm mmmprovince) {
		this.mmmprovince = mmmprovince;
	}
	
	public AreaMmm getMmmcity() {
		return mmmcity;
	}
	
	public void setMmmcity(AreaMmm mmmcity) {
		this.mmmcity = mmmcity;
	}
	
	public AreaMmm getMmmarea() {
		return mmmarea;
	}
	
	public void setMmmarea(AreaMmm mmmarea) {
		this.mmmarea = mmmarea;
	}
	private String branch;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	private int status;
	
	private int isbind;  //是否签约，1：已经签约，0：未签约

	
	public int getIsbind() {
		return isbind;
	}

	public void setIsbind(int isbind) {
		this.isbind = isbind;
	}

	public AccountBank() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public DrawBank getBank() {
		return bank;
	}

	public void setBank(DrawBank bank) {
		this.bank = bank;
	}

	public String getBranch() {
		return this.branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AreaBank getProvince() {
		return province;
	}

	public void setProvince(AreaBank province) {
		this.province = province;
	}

	public AreaBank getCity() {
		return city;
	}

	public void setCity(AreaBank city) {
		this.city = city;
	}

	public AreaBank getArea() {
		return area;
	}

	public void setArea(AreaBank area) {
		this.area = area;
	}

	public int getStatus() {
		return status;
	}

	public DrawBankMmm getBankMmm() {
		return bankMmm;
	}

	public void setBankMmm(DrawBankMmm bankMmm) {
		this.bankMmm = bankMmm;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
    
	
}