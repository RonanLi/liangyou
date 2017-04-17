package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the borrow_tender database table.
 * 
 */
@Entity
@Table(name = "borrow_tender")
public class BorrowTender implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private double account;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double interest;

	private double money;

	@Column(name = "repayment_account")
	private double repaymentAccount;

	@Column(name = "repayment_yescapital")
	private double repaymentYescapital;

	@Column(name = "repayment_yesinterest")
	private double repaymentYesinterest;

	private int status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "wait_account")
	private double waitAccount;

	@Column(name = "assignment_borrow_id")
	private long assignment_borrow_id; // 投债权转让标 关联当前标的id，因为后边债权复审的时候 borrow_id 会修改。

	@Column(name = "wait_interest")
	private double waitInterest;

	@OneToMany(mappedBy = "borrowTender", cascade = CascadeType.PERSIST)
	private List<BorrowCollection> borrowCollections;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "borrow_id")
	private Borrow borrow;

	@Column(name = "assignment_id")
	private long assignmentId;

	@Column(name = "sub_ord_id")
	private String subOrdId;// 交易号

	@Column(name = "sub_ord_date")
	private String subOrdDate;// 交易日期

	@Column(name = "trx_id")
	private String trxId; // 投标冻结流水号

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "borrowTender")
	private TenderProperty tenderProperty;

	@Column(name = "late_account")
	private double lateAccount;

	@Column(name = "capital_web_site_pay")
	private double capitalWebSitePay;

	@Column(name = "interest_web_site_pay")
	private double interestWebSitePay;

	private double compensation;// 补偿金 ，累计

	@Column(name = "interest_fee")
	private double interestFee; // 利息管理费，累计

	private double apr;// 年利率

	private String ipsTenderNo;// 环迅投标订单号

	private String ipsTenderContract; // 环迅投标合同号：备用字段

	@Column(name = "yqf_cps_campaign_id")
	private String yqfCpsCampaignId; // 亿起发查询字段

	@Column(name = "yqf_cps_aid")
	private String yqfCpsAid; // 亿起发查询字段

	@Column(name = "yqf_cps_feedback")
	private String yqfCpsFeedback; // 亿起发查询字段

	@Column(name = "yqf_cps_target")
	private String yqfCpsTarget; // 亿起发查询字段

	@Column(name = "yqf_cps_channel")
	private String yqfCpsChannel; // 亿起发查询字段

	// add by gy 2016-10-26 17:17:41 begin
	// 增加投标记录跟体验金的关联
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "experience_interest_id")
	private ExperienceMoney experienceMoney; // 体验金
	// add by gy 2016-10-26 17:18:10 end

	@Column(name = "em_sub_ord_id")
	private String emSubOrdId;// 体验金双乾交易号

	@Column(name = "em_trx_id")
	private String emTrxId; // 体验金投标冻结流水号

    // 保全ID
    @Column(name = "preservation_id")
    private Long preservationId;

	public BorrowTender() {
	}

	public BorrowTender(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getAccount() {
		return this.account;
	}

	public void setAccount(double account) {
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

	public double getInterest() {
		return this.interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getRepaymentAccount() {
		return this.repaymentAccount;
	}

	public void setRepaymentAccount(double repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	public double getRepaymentYescapital() {
		return repaymentYescapital;
	}

	public void setRepaymentYescapital(double repaymentYescapital) {
		this.repaymentYescapital = repaymentYescapital;
	}

	public double getRepaymentYesinterest() {
		return this.repaymentYesinterest;
	}

	public void setRepaymentYesinterest(double repaymentYesinterest) {
		this.repaymentYesinterest = repaymentYesinterest;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getWaitAccount() {
		return this.waitAccount;
	}

	public void setWaitAccount(double waitAccount) {
		this.waitAccount = waitAccount;
	}

	public double getWaitInterest() {
		return this.waitInterest;
	}

	public void setWaitInterest(double waitInterest) {
		this.waitInterest = waitInterest;
	}

	public List<BorrowCollection> getBorrowCollections() {
		return this.borrowCollections;
	}

	public void setBorrowCollections(List<BorrowCollection> borrowCollections) {
		this.borrowCollections = borrowCollections;
	}

	public BorrowCollection addBorrowCollection(BorrowCollection borrowCollection) {
		getBorrowCollections().add(borrowCollection);
		borrowCollection.setBorrowTender(this);

		return borrowCollection;
	}

	public BorrowCollection removeBorrowCollection(BorrowCollection borrowCollection) {
		getBorrowCollections().remove(borrowCollection);
		borrowCollection.setBorrowTender(null);

		return borrowCollection;
	}

	public Borrow getBorrow() {
		return this.borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public long getAssignmentBorrow() {
		return assignmentId;
	}

	public long getAssignment_borrow_id() {
		return assignment_borrow_id;
	}

	public void setAssignment_borrow_id(long assignment_borrow_id) {
		this.assignment_borrow_id = assignment_borrow_id;
	}

	public long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public String getSubOrdId() {
		return subOrdId;
	}

	public void setSubOrdId(String subOrdId) {
		this.subOrdId = subOrdId;
	}

	public String getSubOrdDate() {
		return subOrdDate;
	}

	public void setSubOrdDate(String subOrdDate) {
		this.subOrdDate = subOrdDate;
	}

	public TenderProperty getTenderProperty() {
		return tenderProperty;
	}

	public void setTenderProperty(TenderProperty tenderProperty) {
		this.tenderProperty = tenderProperty;
	}

	public double getLateAccount() {
		return lateAccount;
	}

	public void setLateAccount(double lateAccount) {
		this.lateAccount = lateAccount;
	}

	public double getCapitalWebSitePay() {
		return capitalWebSitePay;
	}

	public void setCapitalWebSitePay(double capitalWebSitePay) {
		this.capitalWebSitePay = capitalWebSitePay;
	}

	public double getInterestWebSitePay() {
		return interestWebSitePay;
	}

	public void setInterestWebSitePay(double interestWebSitePay) {
		this.interestWebSitePay = interestWebSitePay;
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

	public double getApr() {
		return apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public String getIpsTenderNo() {
		return ipsTenderNo;
	}

	public void setIpsTenderNo(String ipsTenderNo) {
		this.ipsTenderNo = ipsTenderNo;
	}

	public String getIpsTenderContract() {
		return ipsTenderContract;
	}

	public void setIpsTenderContract(String ipsTenderContract) {
		this.ipsTenderContract = ipsTenderContract;
	}

	public String getYqfCpsCampaignId() {
		return yqfCpsCampaignId;
	}

	public void setYqfCpsCampaignId(String yqfCpsCampaignId) {
		this.yqfCpsCampaignId = yqfCpsCampaignId;
	}

	public String getYqfCpsAid() {
		return yqfCpsAid;
	}

	public void setYqfCpsAid(String yqfCpsAid) {
		this.yqfCpsAid = yqfCpsAid;
	}

	public String getYqfCpsFeedback() {
		return yqfCpsFeedback;
	}

	public void setYqfCpsFeedback(String yqfCpsFeedback) {
		this.yqfCpsFeedback = yqfCpsFeedback;
	}

	public String getYqfCpsTarget() {
		return yqfCpsTarget;
	}

	public void setYqfCpsTarget(String yqfCpsTarget) {
		this.yqfCpsTarget = yqfCpsTarget;
	}

	public String getYqfCpsChannel() {
		return yqfCpsChannel;
	}

	public void setYqfCpsChannel(String yqfCpsChannel) {
		this.yqfCpsChannel = yqfCpsChannel;
	}

	// add by gy 2016-10-26 17:18:30 begin
	public ExperienceMoney getExperienceMoney() {
		return experienceMoney;
	}

	public void setExperienceMoney(ExperienceMoney experienceMoney) {
		this.experienceMoney = experienceMoney;
	}
	// add by gy 2016-10-26 17:18:30 end

	public String getEmSubOrdId() {
		return emSubOrdId;
	}

	public void setEmSubOrdId(String emSubOrdId) {
		this.emSubOrdId = emSubOrdId;
	}

	public String getEmTrxId() {
		return emTrxId;
	}

	public void setEmTrxId(String emTrxId) {
		this.emTrxId = emTrxId;
	}

    public Long getPreservationId() {
        return preservationId;
    }

    public void setPreservationId(Long preservationId) {
        this.preservationId = preservationId;
    }

}
