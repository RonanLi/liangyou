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
 * v1.8.0.4_u4 TGPROJECT-371 	qinjun 2014-07-22
 */
@Entity
@Table(name="loan_statistics")
public class LoanStatistics implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	/**
	 * 用户类型，1：借款人，2：投资人，3：两者都是
	 */
	@Column(name="user_type")
	private int userType;
	
	/**
	 * 总投标笔数
	 */
	@Column(name="tender_count")
	private int tenderCount;
	
	/**
	 * 未收款完成的投标笔数
	 */
	@Column(name="wait_tender_count")
	private int waitTenderCount;
	
	/**
	 * 投标总额
	 */
	@Column(name="tender_total")
	private double tenderTotal;
	
	/**
	 * 未收款总额
	 */
	@Column(name="wait_collection_capital")
	private double waitCollectionCapital;
	
	/**
	 * 已收利息
	 */
	@Column(name="earn_interest")
	private double earnInterest;
	
	/**
	 * 亏损总额
	 */
	@Column(name="profit_and_loss_total")
	private double profitAndLossTotal;
	
	/**
	 * 总贷款笔数
	 */
	@Column(name="loan_count")
	private double loanCount;
	
	/**
	 * 总贷款金额
	 */
	@Column(name="loan_total")
	private double loanTotal;
	
	/**
	 * 未还款总额
	 */
	@Column(name="wait_repay_capital")
	private double waitRepayCapital;
	
	/**
	 * 自定义统计利率
	 */
	@Column(name="diy_apr")
	private double diyApr;
	
	/**
	 * 自定义统计利率
	 */
	private Date updatetime;

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
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

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getTenderCount() {
		return tenderCount;
	}

	public void setTenderCount(int tenderCount) {
		this.tenderCount = tenderCount;
	}

	public int getWaitTenderCount() {
		return waitTenderCount;
	}

	public void setWaitTenderCount(int waitTenderCount) {
		this.waitTenderCount = waitTenderCount;
	}

	public double getTenderTotal() {
		return tenderTotal;
	}

	public void setTenderTotal(double tenderTotal) {
		this.tenderTotal = tenderTotal;
	}

	public double getProfitAndLossTotal() {
		return profitAndLossTotal;
	}

	public void setProfitAndLossTotal(double profitAndLossTotal) {
		this.profitAndLossTotal = profitAndLossTotal;
	}

	public double getLoanCount() {
		return loanCount;
	}

	public double getWaitCollectionCapital() {
		return waitCollectionCapital;
	}

	public void setWaitCollectionCapital(double waitCollectionCapital) {
		this.waitCollectionCapital = waitCollectionCapital;
	}

	public double getEarnInterest() {
		return earnInterest;
	}

	public void setEarnInterest(double earnInterest) {
		this.earnInterest = earnInterest;
	}

	public void setLoanCount(double loanCount) {
		this.loanCount = loanCount;
	}

	public double getLoanTotal() {
		return loanTotal;
	}

	public void setLoanTotal(double loanTotal) {
		this.loanTotal = loanTotal;
	}

	public double getWaitRepayCapital() {
		return waitRepayCapital;
	}

	public void setWaitRepayCapital(double waitRepayCapital) {
		this.waitRepayCapital = waitRepayCapital;
	}

	public double getDiyApr() {
		return diyApr;
	}

	public void setDiyApr(double diyApr) {
		this.diyApr = diyApr;
	}

}
