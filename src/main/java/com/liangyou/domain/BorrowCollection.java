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

import com.liangyou.util.DateUtils;

@Entity
@Table(name="borrow_collection")
public class BorrowCollection implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double capital;

	private double interest;

	@Column(name="late_interest")
	private double lateInterest;
	
	@Column(name="late_days")
	private int lateDays;

	private int period;

	@Column(name="repay_account")
	private double repayAccount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="repay_time")
	private Date repayTime;

	@Column(name="repay_yescapital")
	private double repayYescapital;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="repay_yestime")
	private Date repayYestime;

	@Column(name="site_id")
	private int siteId;

	private int status;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id")
	private BorrowTender borrowTender;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repay_type_id")
	private BorrowRepayType borrowRepayType;
	
	@Column(name="capital_site_pay")
	private double capitalSitePay;
	
	@Column(name="interest_site_pay")
	private double  interestSitePay;
	
	@Column(name="repay_yesinterest")
	private double repayYesinterest;
	
	private double compensation;//补偿金 ，当期
	
	@Column(name="interest_fee")
	private double interestFee; //利息管理费，当期

	public BorrowCollection() {
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

	public double getCapital() {
		return this.capital;
	}

	public void setCapital(double capital) {
		this.capital = capital;
	}

	public double getInterest() {
		return this.interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getLateInterest() {
		return this.lateInterest;
	}

	public void setLateInterest(double lateInterest) {
		this.lateInterest = lateInterest;
	}
	public int getLateDays() {
		return lateDays;
	}

	public void setLateDays(int lateDays) {
		this.lateDays = lateDays;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getRepayAccount() {
		return this.repayAccount;
	}

	public void setRepayAccount(double repayAccount) {
		this.repayAccount = repayAccount;
	}

	public Date getRepayTime() {
		return this.repayTime;
	}

	public void setRepayTime(Date repayTime) {
		this.repayTime = repayTime;
	}

	public double getRepayYescapital() {
		return repayYescapital;
	}

	public void setRepayYescapital(double repayYescapital) {
		this.repayYescapital = repayYescapital;
	}

	public Date getRepayYestime() {
		return this.repayYestime;
	}

	public void setRepayYestime(Date repayYestime) {
		this.repayYestime = repayYestime;
	}

	public int getSiteId() {
		return this.siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BorrowTender getBorrowTender() {
		return this.borrowTender;
	}

	public void setBorrowTender(BorrowTender borrowTender) {
		this.borrowTender = borrowTender;
	}
	
	
	public BorrowRepayType getBorrowRepayType() {
		return borrowRepayType;
	}

	public void setBorrowRepayType(BorrowRepayType borrowRepayType) {
		this.borrowRepayType = borrowRepayType;
	}

	public double getCapitalSitePay() {
		return capitalSitePay;
	}

	public void setCapitalSitePay(double capitalSitePay) {
		this.capitalSitePay = capitalSitePay;
	}

	public double getInterestSitePay() {
		return interestSitePay;
	}

	public void setInterestSitePay(double interestSitePay) {
		this.interestSitePay = interestSitePay;
	}

	public double getRepayYesinterest() {
		return repayYesinterest;
	}

	public void setRepayYesinterest(double repayYesinterest) {
		this.repayYesinterest = repayYesinterest;
	}
	
	public double getCompensation() {
		return compensation;
	}

	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}

	public double getInterestFee() {
		return interestFee;
	}

	public void setInterestFee(double interestFee) {
		this.interestFee = interestFee;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj!=null)&&(obj instanceof BorrowCollection) && (getId()==((BorrowCollection)obj).getId());  
	}
	@Override
	public int hashCode() {
		 int result = 17;  
		 result = 31*result +(int)getId(); 
		 return result;
	};
	
	public double getMyTotal(){
		//区分提前还款，正常还款，逾期还款
		return this.capital + getMyInterest() + getExtraMoney()+this.lateInterest;
	}
	public double getMyInterest(){
		//区分提前还款，正常还款，逾期还款
		if(this.status ==1){//已经还款的
			long reTime =this.repayTime.getTime();
			long yesTime = this.repayYestime.getTime();
			long diff = reTime - yesTime;
			long days = diff%(24*3600*1000)>0?(diff/(24*3600*1000)+1):(diff/(24*3600*1000));
			
			long startTime = DateUtils.getNextDayYYYYMMdd(this.getBorrowTender().getBorrow().getVerifyTime()).getTime();
			if(this.lateDays>0&&this.lateInterest>0){
				return this.interest;
			}else if(days>3){//提前还款
				//提前还款日-借款起始日
				long allDays = (yesTime - startTime)%(24*3600*1000)>0? ((yesTime - startTime)/(24*3600*1000)+1):((yesTime - startTime)/(24*3600*1000));
				return this.capital*(this.borrowTender.getBorrow().getApr()/100)/360*(allDays);
			}else{//正常还款
				return this.interest;
			}
		}else{//处理未还款的
			return this.interest;
		}
	}
	
	/**
	 * 计算补偿金，只有还款完成才有这个情况
	 * @return
	 */
	public double getExtraMoney(){
		if(this.status !=1){
			return 0;
		}
		long reTime =this.repayTime.getTime();
		long yesTime = this.repayYestime.getTime();
		long diff = reTime - yesTime;
		long days = diff%(24*3600*1000)>0 ? (diff/(24*3600*1000) + 1):(diff/(24*3600*1000));
		if(days>3){
			return this.capital*(this.borrowTender.getBorrow().getApr()/100)/360*30;
		}else{
			return 0;
		}
	}
}