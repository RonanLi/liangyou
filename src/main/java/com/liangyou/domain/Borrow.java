package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.OperateLogDao;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

/**
 * The persistent class for the borrow database table.
 * 
 */
@Entity
@Table(name = Global.TABLE_PREFIX+"borrow")
public class Borrow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private double account;

	@Column(name="account_yes")
	private double accountYes;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double apr;

	private double award;

	private String content;

	@Column(name="flow_count")
	private int flowCount;

	@Column(name="flow_money")
	private int flowMoney;

	@Column(name="flow_yescount")
	private int flowYescount;

	private double funds;
	private int isday;

	@Column(name="lowest_account")
	private double lowestAccount;

	@Column(name="most_account")
	private double mostAccount;

	private String name;

	private int sort;

	@Column(name="part_account")
	private double partAccount;

	private String pwd;

	@Column(name="repayment_account")
	private double repaymentAccount;

	@Column(name="repayment_remark")
	private String repaymentRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="repayment_time")
	private Date repaymentTime;

	@Column(name="repayment_yesinterest")
	private double repaymentYesinterest;

	private int status;

	private String style;

	@Column(name="tender_times")
	private int tenderTimes;

	@Column(name="time_limit")
	private int timeLimit;

	
	@Column(name="time_limit_day")
	private int timeLimitDay;
	
	@Column(name="view_money")
	private double viewMoney;        //用于截标时保存原标的借款金额
	
	@Column(name="trx_id")
	private String trxId;        //用于截标时保存原标的借款金额
	
	public double getViewMoney() {
		return viewMoney;
	}

	public void setViewMoney(double viewMoney) {
		this.viewMoney = viewMoney;
	}

	private int type;

	//借款用途
	private String usetype;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	@Column(name="valid_time")
	private String validTime;

	@Column(name="verify_remark")
	private String verifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verify_time")
	private Date verifyTime;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user")
	private User verifyUser;

	@OneToMany(mappedBy="borrow")
	private List<BorrowRepayment> borrowRepayments;

	@OneToMany(mappedBy="borrow")
	private List<BorrowTender> borrowTenders;
	
	@Column(name="open_account")
	private int   openAccount ;        //公开我的借款资金情况
	
	@Column(name="open_borrow")        //'公开我的借款资金情况',
	private int   openBorrow ;       
	
	@Column(name="open_tender")
	private int   openTender ;         //公开我的投标资金情况
	
	@Column(name="open_credit")
	private int   openCredit ;       //公开我的信用额度情况
	
	@Column(name="recommend_status")
	private int recommendStatus;	//推荐标  [1：是； 0：否]
	
	@Column(name="is_assignment")		//债权转让标志
	private int isAssignment;

	@Column(name="assignment_tender")    //债权转让标源自哪笔投资
	private long assignmentTender;
	
	@OneToOne(fetch=FetchType.LAZY, mappedBy="borrow")  
	private BorrowFee borrowFee;
	
	@OneToOne(fetch=FetchType.LAZY, mappedBy="borrow") 
	private BorrowProperty borrowProperty;
	
	//v1.8.0.4_u3 TGPROJECT-333 qinjun 2014-06-10 start
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_tender_time")
	private Date startTenderTime;
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	private int priorStatus;   //申请提前还款：1：申请中，2：审核通过，-1:审核未通过
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
	//TGPROJECT-376  老账房项目借款合同手机验证功能
	private int isPhoneContract;  //手机验证是否同意借款合同 ，0：同意，1：已通过同意
	//TGPROJECT-376  老账房项目借款合同手机验证功能
	
	@Column(name="is_income_ladder")
	private int isIncomeLadder;
	
	private String ipsBorrowNo; //环迅发标的标号

	public Date getStartTenderTime() {
		return startTenderTime;
	}

	public void setStartTenderTime(Date startTenderTime) {
		this.startTenderTime = startTenderTime;
	}
	//v1.8.0.4_u3 TGPROJECT-333 qinjun 2014-06-10 end
	
	//v1.8.0.3_u3 TGPROJECT-375  qinjun 2014-07-22  start 
	@OneToMany(mappedBy="borrow")
	private List<BorrowDetail> borrowDetails;
	
	public List<BorrowDetail> getBorrowDetails() {
		return borrowDetails;
	}

	public void setBorrowDetails(List<BorrowDetail> borrowDetails) {
		this.borrowDetails = borrowDetails;
	}
	//v1.8.0.3_u3 TGPROJECT-375  qinjun 2014-07-22  end 
	
	//大互联  后台设置奖励  2014-09-03 zxc start
	@Column(name="admin_award")
	private double adminAward;
	
	@Column(name="admin_funds")
	private double adminFunds;
	
	public double getAdminAward() {
		return adminAward;
	}

	public void setAdminAward(double adminAward) {
		this.adminAward = adminAward;
	}

	public double getAdminFunds() {
		return adminFunds;
	}

	public void setAdminFunds(double adminFunds) {
		this.adminFunds = adminFunds;
	}
	
	//大互联  后台设置奖励  2014-09-03 zxc end
	public Borrow() {
	}

	public Borrow(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getAccount() {
		return this.account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getAccountYes() {
		return this.accountYes;
	}

	public void setAccountYes(double accountYes) {
		this.accountYes = accountYes;
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

	public double getApr() {
		return this.apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public double getAward() {
		return this.award;
	}

	public void setAward(double award) {
		this.award = award;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getFlowCount() {
		return this.flowCount;
	}

	public void setFlowCount(int flowCount) {
		this.flowCount = flowCount;
	}

	public int getFlowMoney() {
		return this.flowMoney;
	}

	public void setFlowMoney(int flowMoney) {
		this.flowMoney = flowMoney;
	}

	public int getFlowYescount() {
		return this.flowYescount;
	}

	public void setFlowYescount(int flowYescount) {
		this.flowYescount = flowYescount;
	}

	public double getFunds() {
		return this.funds;
	}

	public void setFunds(double funds) {
		this.funds = funds;
	}

	public int getIsday() {
		return this.isday;
	}

	public void setIsday(int isday) {
		this.isday = isday;
	}

	public double getLowestAccount() {
		return this.lowestAccount;
	}

	public void setLowestAccount(double lowestAccount) {
		this.lowestAccount = lowestAccount;
	}

	public double getMostAccount() {
		return this.mostAccount;
	}

	public void setMostAccount(double mostAccount) {
		this.mostAccount = mostAccount;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name.replace("#", "");
	}
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public double getPartAccount() {
		return this.partAccount;
	}

	public void setPartAccount(double partAccount) {
		this.partAccount = partAccount;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public double getRepaymentAccount() {
		return this.repaymentAccount;
	}

	public void setRepaymentAccount(double repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	public String getRepaymentRemark() {
		return this.repaymentRemark;
	}

	public void setRepaymentRemark(String repaymentRemark) {
		this.repaymentRemark = repaymentRemark;
	}

	public Date getRepaymentTime() {
		return this.repaymentTime;
	}

	public void setRepaymentTime(Date repaymentTime) {
		this.repaymentTime = repaymentTime;
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

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getTenderTimes() {
		return this.tenderTimes;
	}

	public void setTenderTimes(int tenderTimes) {
		this.tenderTimes = tenderTimes;
	}

	public int getTimeLimit() {
		return this.timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getTimeLimitDay() {
		return this.timeLimitDay;
	}

	public void setTimeLimitDay(int timeLimitDay) {
		this.timeLimitDay = timeLimitDay;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public String getUsetype() {
		return usetype;
	}

	public void setUsetype(String usetype) {
		this.usetype = usetype;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getValidTime() {
		return this.validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public String getVerifyRemark() {
		return this.verifyRemark;
	}

	public void setVerifyRemark(String verifyRemark) {
		this.verifyRemark = verifyRemark;
	}

	public Date getVerifyTime() {
		return this.verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	public User getVerifyUser() {
		return this.verifyUser;
	}

	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
	}

	public List<BorrowRepayment> getBorrowRepayments() {
		return this.borrowRepayments;
	}

	public void setBorrowRepayments(List<BorrowRepayment> borrowRepayments) {
		this.borrowRepayments = borrowRepayments;
	}

	public BorrowRepayment addBorrowRepayment(BorrowRepayment borrowRepayment) {
		getBorrowRepayments().add(borrowRepayment);
		borrowRepayment.setBorrow(this);

		return borrowRepayment;
	}

	public BorrowRepayment removeBorrowRepayment(BorrowRepayment borrowRepayment) {
		getBorrowRepayments().remove(borrowRepayment);
		borrowRepayment.setBorrow(null);

		return borrowRepayment;
	}

	public List<BorrowTender> getBorrowTenders() {
		return this.borrowTenders;
	}

	public void setBorrowTenders(List<BorrowTender> borrowTenders) {
		this.borrowTenders = borrowTenders;
	}

	public BorrowTender addBorrowTender(BorrowTender borrowTender) {
		getBorrowTenders().add(borrowTender);
		borrowTender.setBorrow(this);
		return borrowTender;
	}

	public BorrowTender removeBorrowTender(BorrowTender borrowTender) {
		getBorrowTenders().remove(borrowTender);
		borrowTender.setBorrow(null);
		return borrowTender;
	}

	public int getOpenAccount() {
		return openAccount;
	}

	public void setOpenAccount(int openAccount) {
		this.openAccount = openAccount;
	}

	public int getOpenBorrow() {
		return openBorrow;
	}

	public void setOpenBorrow(int openBorrow) {
		this.openBorrow = openBorrow;
	}

	public int getOpenTender() {
		return openTender;
	}

	public void setOpenTender(int openTender) {
		this.openTender = openTender;
	}

	public int getOpenCredit() {
		return openCredit;
	}

	public void setOpenCredit(int openCredit) {
		this.openCredit = openCredit;
	}

	public int getRecommendStatus() {
		return recommendStatus;
	}

	public void setRecommendStatus(int recommendStatus) {
		this.recommendStatus = recommendStatus;
	}

	public int getIsAssignment() {
		return isAssignment;
	}

	public void setIsAssignment(int isAssignment) {
		this.isAssignment = isAssignment;
	}

	public long getAssignmentTender() {
		return assignmentTender;
	}

	public void setAssignmentTender(long assignmentTender) {
		this.assignmentTender = assignmentTender;
	}

	public BorrowFee getBorrowFee() {
		return borrowFee;
	}

	public void setBorrowFee(BorrowFee borrowFee) {
		this.borrowFee = borrowFee;
	}

	public BorrowProperty getBorrowProperty() {
		return borrowProperty;
	}

	public void setBorrowProperty(BorrowProperty borrowProperty) {
		this.borrowProperty = borrowProperty;
	}

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}	
	
	public int getPriorStatus() {
		return priorStatus;
	}

	public void setPriorStatus(int priorStatus) {
		this.priorStatus = priorStatus;
	}
	
	

	public int getIsPhoneContract() {
		return isPhoneContract;
	}

	public void setIsPhoneContract(int isPhoneContract) {
		this.isPhoneContract = isPhoneContract;
	}
	
	public int getIsIncomeLadder() {
		return isIncomeLadder;
	}

	public void setIsIncomeLadder(int isIncomeLadder) {
		this.isIncomeLadder = isIncomeLadder;
	}
	
	

	public String getIpsBorrowNo() {
		return ipsBorrowNo;
	}

	public void setIpsBorrowNo(String ipsBorrowNo) {
		this.ipsBorrowNo = ipsBorrowNo;
	}

	//查看标的审核进度
	public String getOperateDetail(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		OperateLogDao operateLogDao = (OperateLogDao) wac.getBean("operateLogDao");  
		OperateLog operLog = operateLogDao.getLastOperateLogByOrdNo(this.id);
		if(operLog == null){
			return "-";
		}
		return operLog.getOperateDetail();
	}
	//导出excel用到的最低筹集金额
	public String getLowestMoney(){
		if(getBorrowProperty() == null){
			return "-";
		}else{
			double lowestMoney = getBorrowProperty().getLowestMoney();
			if(lowestMoney == 0){
				return "-";
			}else{
				return lowestMoney+"";
			}
		}
	}
	//导出excel用到的借款期限
	public String getTimeStr(){
		if(this.isday == 1){
			return timeLimitDay + "天";
		}else{
			return timeLimit+"月";
		}
	}
	//导出excel用到的标的状态
	public String getStatusStr(){
		switch (this.status) {
		case 0:
			return "等待初审";
		case 1:
			return "招标中";
		case 2:
			return "初审不通过";
		case 3:
			return "满标复审处理中";
		case 4:
			return "满标复审不通过";
		case 5:
			return "取消状态";
		case 6:
			return "满标复审通过";
		case 7:
			return "还款中";
		case 8:
			return "还款完成";
		case -1:
			return "用户撤回";
		case 59:
			return "管理员撤回";
			//v1.8.0.4_u1 TGPROJECT-287  lx   start
		case 49:
			return "满标复审不通过";
			//v1.8.0.4_u1 TGPROJECT-287  lx   end
		default:
			return "错误状态";
		}
	}
	/**
	 * 导出报表计算风险准备金
	 * @return
	 */
	public String getRiskreserveFeeStr(){
		return NumberUtils.format2(this.borrowFee.getRiskreserve() * this.account)+"";
	}
	/**
	 * 导出报表计算管理费
	 * @return
	 */
	public String getManageFeeStr(){
		return NumberUtils.format2(this.borrowFee.getManagefee() * this.account *this.timeLimit)+"";
	}
	
	/**
	 * 导出报表时，计算结束时间
	 * @return
	 */
	public String getEndBorrowTime(){
		if (isday==1) {
			return DateUtils.dateStr2(DateUtils.rollDay(this.verifyTime, this.timeLimitDay));
		}else{
			return DateUtils.dateStr2(DateUtils.rollMon(verifyTime, this.timeLimit));
		}
	}
	
	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}

}
