package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * 标的各种费用管理表
 * 
 */
@Entity(name="borrow_fee")
public class BorrowFee implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id",updatable=false)
	private Borrow borrow;
	
	@Column(name="riskreserve") //'风险备用金'
	private double riskreserve;
	
	@Column(name="guaranteefee")//'还款保证金'
	private double guaranteefee;
	
	@Column(name="managefee")//借款管理费
	private double managefee;
	
	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 end
	/**
	 * 平台管理费
	 */
	@Column(name="web_manage_fee")//借款管理费
	private double webManageFee;
	
	/**
	 * 平台管理费到账用户
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="web_manage_user_id",updatable=false)
	private User webManageUser;
	
	/**
	 * 小贷审核费
	 */
	@Column(name="small_loan_fee")
	private double smallLoanFee;
	
	/**
	 * 小贷审核费到账用户
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="small_loan_user_id",updatable=false)
	private User smallLoanUser;
	
	/**
	 * 担保公司担保费
	 */
	@Column(name="warrant_fee")
	private double warrantFee;
	
	/**
	 * 担保公司担保费到帐用户
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="warrant_user_id",updatable=false)
	private User warrantUser;
	
	/**
	 * 服务公司推荐费
	 */
	@Column(name="introduce_fee")
	private double introduceFee;
	
	/**
	 * 服务公司推荐费到账用户
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="introduce_user_id",updatable=false)
	private User introduceUser;
	
	public double getWebManageFee() {
		return webManageFee;
	}

	public void setWebManageFee(double webManageFee) {
		this.webManageFee = webManageFee;
	}

	public User getWebManageUser() {
		return webManageUser;
	}

	public void setWebManageUser(User webManageUser) {
		this.webManageUser = webManageUser;
	}

	public double getSmallLoanFee() {
		return smallLoanFee;
	}

	public void setSmallLoanFee(double smallLoanFee) {
		this.smallLoanFee = smallLoanFee;
	}

	public User getSmallLoanUser() {
		return smallLoanUser;
	}

	public void setSmallLoanUser(User smallLoanUser) {
		this.smallLoanUser = smallLoanUser;
	}

	public double getWarrantFee() {
		return warrantFee;
	}

	public void setWarrantFee(double warrantFee) {
		this.warrantFee = warrantFee;
	}

	public User getWarrantUser() {
		return warrantUser;
	}

	public void setWarrantUser(User warrantUser) {
		this.warrantUser = warrantUser;
	}

	public double getIntroduceFee() {
		return introduceFee;
	}

	public void setIntroduceFee(double introduceFee) {
		this.introduceFee = introduceFee;
	}

	public User getIntroduceUser() {
		return introduceUser;
	}

	public void setIntroduceUser(User introduceUser) {
		this.introduceUser = introduceUser;
	}
	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 end

	public BorrowFee() {
		super();
	}

	public BorrowFee(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public double getRiskreserve() {
		return riskreserve;
	}

	public void setRiskreserve(double riskreserve) {
		this.riskreserve = riskreserve;
	}

	public double getGuaranteefee() {
		return guaranteefee;
	}

	public void setGuaranteefee(double guaranteefee) {
		this.guaranteefee = guaranteefee;
	}

	public double getManagefee() {
		return managefee;
	}

	public void setManagefee(double managefee) {
		this.managefee = managefee;
	}
	
}