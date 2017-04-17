package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.OperateLogDao;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.AutoService;
import com.liangyou.service.RuleService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

/**
 * The persistent class for the borrow_repayment database table.
 * 
 */
@Entity
@Table(name="borrow_repayment")
public class BorrowRepayment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double capital;

	private double interest;

	@Column(name="late_days")
	private int lateDays;

	@Column(name="late_interest")
	private double lateInterest;

	private int period;

	@Column(name="repayment_account")
	private double repaymentAccount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="repayment_time")
	private Date repaymentTime;

	@Column(name="repayment_yescapital")
	private double repaymentYescapital;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="repayment_yestime")
	private Date repaymentYestime;

	@Column(name="site_id")
	private int siteId;

	private int status;

	private int webstatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id")
	private BorrowTender borrowTender; //流转标专用

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repay_type_id")
	private BorrowRepayType borrowRepayType;
	
	@Column(name="repayment_yesinterest")
	private double repaymentYesinterest;
	
	private double compensation;//补偿金 ，当期还款
	
	@Column(name="credit_status")
	private int creditStatus;
	
	//TGPROJECT-351   用户已还罚息  start
	private double repayYesLateInterest;   //用户已还罚息
	
	public double getRepayYesLateInterest() {
		return repayYesLateInterest;
	}

	public void setRepayYesLateInterest(double repayYesLateInterest) {
		this.repayYesLateInterest = repayYesLateInterest;
	}
	//TGPROJECT-351   用户已还罚息   end
	
	@Column(name="reward_status")
	private int rewardStatus;

	public int getRewardStatus() {
		return rewardStatus;
	}

	public void setRewardStatus(int rewardStatus) {
		this.rewardStatus = rewardStatus;
	}

	public BorrowRepayment() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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

	public int getLateDays() {
		return this.lateDays;
	}

	public void setLateDays(int lateDays) {
		this.lateDays = lateDays;
	}

	public double getLateInterest() {
		return lateInterest;
	}

	public void setLateInterest(double lateInterest) {
		this.lateInterest = lateInterest;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getRepaymentAccount() {
		return this.repaymentAccount;
	}

	public void setRepaymentAccount(double repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	public Date getRepaymentTime() {
		return this.repaymentTime;
	}

	public void setRepaymentTime(Date repaymentTime) {
		this.repaymentTime = repaymentTime;
	}

	public double getRepaymentYescapital() {
		return repaymentYescapital;
	}

	public void setRepaymentYescapital(double repaymentYescapital) {
		this.repaymentYescapital = repaymentYescapital;
	}

	public Date getRepaymentYestime() {
		return this.repaymentYestime;
	}

	public void setRepaymentYestime(Date repaymentYestime) {
		this.repaymentYestime = repaymentYestime;
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

	public int getWebstatus() {
		return this.webstatus;
	}

	public void setWebstatus(int webstatus) {
		this.webstatus = webstatus;
	}

	public Borrow getBorrow() {
		return this.borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}
	public BorrowTender getBorrowTender() {
		return borrowTender;
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

	public double getRepaymentYesinterest() {
		return repaymentYesinterest;
	}

	public void setRepaymentYesinterest(double repaymentYesinterest) {
		this.repaymentYesinterest = repaymentYesinterest;
	}

	public double getCompensation() {
		return compensation;
	}

	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj!=null)&&(obj instanceof BorrowRepayment) && (getId()==((BorrowRepayment)obj).getId());  
	}
	@Override
	public int hashCode() {
		 int result = 17;  
		 result = 31*result +(int)getId(); 
		 return result;
	};
	
	public String getStatusStr(){
		if(this.status == 1){
			return borrowRepayType.getName();
		}else{
			return "未还款";
		}
	}
	
	public double getMakeBorrowFee(){
		return borrow.getAccount()*2;
	}
	
	/**
	 * 获取已经还款总金额
	 * @return
	 */
	public double getAllMoney(){
		return this.repaymentYescapital + this.repaymentYesinterest+this.lateInterest+this.compensation;
	}
	
	/**
	 * 获取提前还款的应还的利息
	 * @return
	 */
	public double getPrioInterest(){
		Date currTime = new Date();
		Date verify_time =  this.borrow.getVerifyTime();
		Date startDate = DateUtils.getNextDayYYYYMMdd(verify_time);
		long daySeconds = 24*60*60*1000;
		long diffTime = currTime.getTime() - startDate.getTime();
		long days = diffTime%daySeconds > 0? (diffTime/daySeconds +1 ):(diffTime/daySeconds) ;
		return NumberUtils.format2(this.capital*this.borrow.getApr()/360/100*days);
	}
	
	/**
	 * 提前还款，获取补偿金
	 * @return
	 */
	public double getExtraPay(){
		return NumberUtils.format2(this.capital*this.borrow.getApr()*30/360/100);
	}
	
	/**
	 * 提前还款的天数
	 * @return
	 */
	public long getPrioDay(){
		Date yesTime ;
		if(this.repaymentYestime == null){
			yesTime = new Date();
		}else{
			yesTime= this.repaymentYestime;
		}
		Date repayTime = this.repaymentTime;
		long daySeconds = 24*60*60*1000;
		long diffTime = repayTime.getTime() - yesTime.getTime();
		long days = diffTime/daySeconds;
		return days>0?days:0;
	}
	
	/**
	 * 查看还款计划的审核进度
	 * @return
	 */
	public String getOperateDetail(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		OperateLogDao operateLogDao = (OperateLogDao) wac.getBean("operateLogDao");  
		OperateLog operLog = operateLogDao.getRepayLastOperateLogByOrdNo(this.id);
		if(operLog == null){
			return "-";
		}
		return operLog.getOperateDetail();
	}
	
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 start
	public double getRepayManageFee(){//还款的时候有管理费，每次只是还一期的费用
		double fee = 0;
		fee = BorrowHelper.getHelper(borrow).getManageFeeWithRule(1);
		return fee;
	}
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 stop

	public int getCreditStatus() {
		return creditStatus;
	}

	public void setCreditStatus(int creditStatus) {
		this.creditStatus = creditStatus;
	}
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	public Map<String,Double> hfPriorRepayDetail(){
		Map<String,Double> map = new HashMap<String, Double>();
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowRepaymentDao borrowRepaymentDao = (BorrowRepaymentDao) wac.getBean("borrowRepaymentDao");  
		Date startDate = new Date();
		if(borrow.getStatus() == 6){
			startDate = DateUtils.getNextDayYYYYMMdd(borrow.getVerifyTime());
		}else if(borrow.getStatus() == 7){
			startDate = borrowRepaymentDao.getRecentlyRepayment(borrow.getId()).getRepaymentTime();
		}else{
			throw new BussinessException("您的借款标状态异常！");
		}
		long startTime =  DateUtils.getTime(startDate); //前一个还款时间或满标复审时间
		long nowTime = NumberUtils.getLong(DateUtils.getNowTimeStr()); //当前还款的
		//计算提前还款日中间的时间间隔
		long day = (nowTime-startTime)%(60*60*24)>0?(nowTime-startTime)/(60*60*24)+1:(nowTime-startTime)/(60*60*24);  
		day = day<0?0:day;
		double remainderMoney = borrowRepaymentDao.getRemainderCapital(borrow.getId());
		if(remainderMoney == 0){
			throw new BussinessException("您的借款标还款异常！");
		}	
		//还款利息
		double repayInterest = remainderMoney*day*borrow.getApr()/36500;
		//还款补偿金
		double compensation = NumberUtils.getDouble2(Global.getString("prior_repay_compen_ratio"))*remainderMoney*(borrow.getApr()/36500);
		//还款总金额
		double repayAccount = remainderMoney+repayInterest+compensation;
		map.put("repayCapital", remainderMoney);
		map.put("repayInterest", repayInterest);
		map.put("compensation", compensation);
		map.put("repayAccount", repayAccount);
		return map;
	}
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end 
	
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	/**
	 * 计算用户提前还款时，所需还款明细统计
	 * @return
	 */
	public Map<String, Double> lzfPriorRepayDetail(){
		Map<String, Double> map = new HashMap<String, Double>();
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowRepaymentDao borrowRepaymentDao = (BorrowRepaymentDao) wac.getBean("borrowRepaymentDao"); 
		BorrowCollectionDao borrowCollectionDao =(BorrowCollectionDao) wac.getBean("borrowCollectionDao");
		double waitOldRpayCapital = borrowRepaymentDao.getRemainderCapital(borrow.getId());  //计算剩余待还本金
		double waitRepayInterest = borrowRepaymentDao.getwaitRpayInterest(borrow.getId(), period); //本次提前还款待还利息总和
		double waitNowInterest = interest;  //当期应还利息
		double sumAccount = waitOldRpayCapital +waitRepayInterest;  //计算提前还款总额
		double compensation = waitRepayInterest - waitNowInterest;  //计算罚息：相当于下一个月的待还利息
		map.put("sumAccount", sumAccount);
		map.put("waitNowInterest", waitNowInterest);
		map.put("compensation", compensation);
		map.put("waitOldRpayCapital", waitOldRpayCapital);
		return map;
		
	}
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
	/**
	 * 期数
	 * 导出报表显示用
	 * @return
	 */
	public int getRetPeriod(){
		return this.period+1;
	}
	
	/**
	 * 计算剩余待还本金，导出报表使用
	 * @return
	 */
	public double getSurplusWaitRepayAccount(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowRepaymentDao borrowRepaymentDao = (BorrowRepaymentDao) wac.getBean("borrowRepaymentDao"); 
		BorrowCollectionDao borrowCollectionDao =(BorrowCollectionDao) wac.getBean("borrowCollectionDao");
		double waitOldRpayCapital = borrowRepaymentDao.getRemainderCapital(borrow.getId());  //计算剩余待还本金
		return waitOldRpayCapital;
	}
}
