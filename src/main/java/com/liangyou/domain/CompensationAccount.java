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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** 补偿金账户表（心意贷）
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
@Entity
@Table(name="compensation_account")
public class CompensationAccount implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;
	
	@Column(name="collection_compensation")
	private double collectionCompensation;//待收补偿金
	
	@Column(name="repay_compensation")
	private double repayCompensation;//待还补偿金
	
	@Column(name="collection_yescompensation")
	private double collectionYesCompensation;//已收补偿金
	
	@Column(name="repay_yescompensation")
	private double repayYesCompensation;//已还补偿金
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public CompensationAccount() {
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

	public void setUser(User user) {
		this.user = user;
	}

	public double getCollectionCompensation() {
		return collectionCompensation;
	}

	public void setCollectionCompensation(double collectionCompensation) {
		this.collectionCompensation = collectionCompensation;
	}

	public double getRepayCompensation() {
		return repayCompensation;
	}

	public void setRepayCompensation(double repayCompensation) {
		this.repayCompensation = repayCompensation;
	}

	public double getCollectionYesCompensation() {
		return collectionYesCompensation;
	}

	public void setCollectionYesCompensation(double collectionYesCompensation) {
		this.collectionYesCompensation = collectionYesCompensation;
	}

	public double getRepayYesCompensation() {
		return repayYesCompensation;
	}

	public void setRepayYesCompensation(double repayYesCompensation) {
		this.repayYesCompensation = repayYesCompensation;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
}
