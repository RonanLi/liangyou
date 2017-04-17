package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 


/**
 * The persistent class for the borrow_config database table.
 * 
 */
@Entity
@Table(name="borrow_config")
public class BorrowConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String identify;

	@Column(name="is_review")
	private int isReview=0;

	@Column(name="is_trail")
	private int isTrail=0;

	@Column(name="lowest_account")
	private double lowestAccount;

	@Column(name="lowest_apr")
	private double lowestApr;

	@Column(name="lowest_award_apr")
	private double lowestAwardApr;

	@Column(name="lowest_award_funds")
	private double lowestAwardFunds;

	@Column(name="most_account")
	private double mostAccount;

	@Column(name="most_apr")
	private double mostApr;

	@Column(name="most_award_apr")
	private double mostAwardApr;

	@Column(name="most_award_funds")
	private double mostAwardFunds;

	private String name;

	private String remark;
	
	private double daymanagefee;
	
	private double managefee;
	
    private String attestation;//发标，校验用户，上传资料情况.

	public BorrowConfig() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdentify() {
		return this.identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public int getIsReview() {
		return this.isReview;
	}

	public void setIsReview(int isReview) {
		this.isReview = isReview;
	}

	public int getIsTrail() {
		return this.isTrail;
	}

	public void setIsTrail(int isTrail) {
		this.isTrail = isTrail;
	}

	public double getLowestAccount() {
		return this.lowestAccount;
	}

	public void setLowestAccount(double lowestAccount) {
		this.lowestAccount = lowestAccount;
	}

	public double getLowestApr() {
		return this.lowestApr;
	}

	public void setLowestApr(double lowestApr) {
		this.lowestApr = lowestApr;
	}

	public double getLowestAwardApr() {
		return this.lowestAwardApr;
	}

	public void setLowestAwardApr(double lowestAwardApr) {
		this.lowestAwardApr = lowestAwardApr;
	}

	public double getLowestAwardFunds() {
		return this.lowestAwardFunds;
	}

	public void setLowestAwardFunds(double lowestAwardFunds) {
		this.lowestAwardFunds = lowestAwardFunds;
	}

	public double getMostAccount() {
		return this.mostAccount;
	}

	public void setMostAccount(double mostAccount) {
		this.mostAccount = mostAccount;
	}

	public double getMostApr() {
		return this.mostApr;
	}

	public void setMostApr(double mostApr) {
		this.mostApr = mostApr;
	}

	public double getMostAwardApr() {
		return this.mostAwardApr;
	}

	public void setMostAwardApr(double mostAwardApr) {
		this.mostAwardApr = mostAwardApr;
	}

	public double getMostAwardFunds() {
		return this.mostAwardFunds;
	}

	public void setMostAwardFunds(double mostAwardFunds) {
		this.mostAwardFunds = mostAwardFunds;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getDaymanagefee() {
		return daymanagefee;
	}

	public void setDaymanagefee(double daymanagefee) {
		this.daymanagefee = daymanagefee;
	}

	public double getManagefee() {
		return managefee;
	}

	public void setManagefee(double managefee) {
		this.managefee = managefee;
	}

	public String getAttestation() {
		return attestation;
	}

	public void setAttestation(String attestation) {
		this.attestation = attestation;
	}
	
}