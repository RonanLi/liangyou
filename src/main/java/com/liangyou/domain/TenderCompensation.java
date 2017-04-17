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

/** 投资补偿表（心意贷）
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
@Entity
@Table(name="tender_compensation")
public class TenderCompensation implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_user_id")
	private User tenderUser;//投资用户user_id

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_user_id")
	private User borrowUser;//借款用户user_id
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id", updatable=false)
	private BorrowTender tender;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id", updatable=false)
	private Borrow borrow;

	@Column(name="tender_money")
	private double tenderMoney;//投资金额

	private double compensation;//满标前补偿金 = 天数 * 补偿率 * 投资金额

	private int status;//状态 0 满标通过-待还，1借款人-还款(正常还款、还款给网站)，2撤标-网站垫付，3逾期-网站垫付

	@Column(name="days")
	private int days;//天数 = 满标复审通过|撤标时间  — 投标成功时间；计算这两个时间差的天数，注意不满一天按一天计算。

	private double rate;//补偿率 = 年利率 / 360

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String remark;

	public TenderCompensation() {
	}

	public TenderCompensation(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getTenderUser() {
		return tenderUser;
	}

	public void setTenderUser(User tenderUser) {
		this.tenderUser = tenderUser;
	}

	public User getBorrowUser() {
		return borrowUser;
	}

	public void setBorrowUser(User borrowUser) {
		this.borrowUser = borrowUser;
	}

	public double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public double getCompensation() {
		return compensation;
	}

	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BorrowTender getTender() {
		return tender;
	}

	public void setTender(BorrowTender tender) {
		this.tender = tender;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}
	

}
