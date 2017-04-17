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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;


/**
 * The persistent class for the credit database table.
 * 
 */
@Entity
public class Credit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	@Column(name="op_user")
	private int opUser;

	@Column(name="tender_value")
	private int tenderValue;

	private String updateip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;

	private int value;
	
	@Column(name="star_score")
	private double starScore;

	@OneToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="user_id", updatable=false)  
	private User user;

	public Credit() {
	}
	public Credit(User user, int value, Date addtime, String addip) {
		super();
		this.user = user;
		this.value = value;
		this.addtime = addtime;
		this.addip = addip;
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

	public int getOpUser() {
		return this.opUser;
	}

	public void setOpUser(int opUser) {
		this.opUser = opUser;
	}

	public int getTenderValue() {
		return this.tenderValue;
	}

	public void setTenderValue(int tenderValue) {
		this.tenderValue = tenderValue;
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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public double getStarScore() {
		return starScore;
	}
	public void setStarScore(double starScore) {
		this.starScore = starScore;
	}
	public CreditRank findCreditRank(){
		CreditRank backCreaditRank = null;
		for(int i=0;i<Global.ALLCREDITRANK.size();i++){
			CreditRank cr = (CreditRank)Global.ALLCREDITRANK.get(i);
			if(this.value>=cr.getPoint1()&&this.value<=cr.getPoint2()){
				backCreaditRank = cr;
				break;
			}			
		}
		return backCreaditRank;
	}
	
	public StarRank findStarRank(){
		StarRank  returnStarRank = null;
		for(int i=0;i<Global.ALLSTARRANK.size();i++){
			StarRank cr = (StarRank)Global.ALLSTARRANK.get(i);
			if(this.starScore>=cr.getScoreStart()&&this.starScore<=cr.getScoreEnd()){
				returnStarRank = cr;
				break;
			}			
		}
		return returnStarRank;
	}
}