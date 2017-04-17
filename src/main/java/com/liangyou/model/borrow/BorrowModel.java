package com.liangyou.model.borrow;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.util.DateUtils;


public class BorrowModel extends Borrow {
	
	private static final long serialVersionUID = 6227166783859660460L;
	
	private BorrowConfig config;
	private Borrow model;
	
	public BorrowModel() {
		super();
	}
	public void setModel(Borrow model) {
		this.model = model;
	}
	public BorrowConfig getConfig() {
		return config;
	}
	public void setConfig(BorrowConfig config) {
		this.config = config;
	}
	
	public String getTypeSql() {
		String typeSql = "";
		switch (getModel().getType()) {
		case 100:
			typeSql = "";
			//typeSql = "";
			break;
		case 101:// 秒标 TYPE_MB
			typeSql = " and p1.is_mb =1 ";
			break;
		case 102:// 信用标 TYPE_XIN
			typeSql = " and p1.is_xin=1 and p1.is_student<>1 ";
			break;
		case 103:
			typeSql = " and p1.is_fast=1 ";
			break;
		case 104:// 净标 TYPE_JIN
			typeSql = " and p1.is_jin=1  ";
			break;
		case 105://  保标 TYPE_VOUCH
			typeSql = " and p1.is_vouch=1 ";
			break;
		case 106:// 艺术品区
			typeSql = " and p1.is_art=1 ";
			break;
		case 107:// 慈善专区
			typeSql = " and p1.is_charity=1 ";
			break;
		case 108:// 
			typeSql = " ";
			break;
		case 109:// 
			typeSql = " and p1.is_project=1 ";
			break;
		case 110:// 
			typeSql = " and p1.is_flow=1 ";
			break;
		case 111:// 
			typeSql = " and p1.is_student=1 ";
			break;
		case 112:// 
			typeSql = " and p1.is_offvouch=1 ";
			break;
		case 113:// 
			typeSql = " and p1.is_pledge=1 ";
			break;
		default:// 未发布
			throw new BussinessException("不正确的Borrow类型:"+getModel().getType());
		}
		return typeSql;
	}
	
	public String getStatusSql() {
		String statusSql = " ";
		switch (getStatus()) {
		case 0:
			statusSql=" ";
			break;
		case 1:
			statusSql=" and  (p1.status=1 and ((p1.account>p1.account_yes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+") or (p1.is_flow=1 and p1.account>p1.account_yes+0) ))";
			break;
		case 2:
			statusSql=" and (p1.status=1 and (p1.account=p1.account_yes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+"))";
			break;
		case 3:
			statusSql=" and p1.status=3";
			break;
		case 4:
			statusSql=" and (p1.status=4 or p1.status=49)";
			break;
		case 5:
			statusSql=" and (p1.status=5 or p1.status=59)";
			break;
		case 6:
			statusSql=" and p1.status=6";
			break;
		case 7:
			statusSql=" and p1.status=7";
			break;
		case 8:
			statusSql=" and p1.status=8";
			break;
		case 9://流标状态
			statusSql=" and p1.verify_time+p1.valid_time*24*60*60 <"+ DateUtils.getTime(new Date());
			break;
		case 10: //还款中..
			statusSql=" and ((p1.status in (3,6,7) and p1.is_flow!=1) or (p1.status in(1,3,6,7) and p1.is_flow=1)) ";
			break;
		case 11: //首页显示状态
			statusSql=" and ((((p1.status=1 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+") " +
					" or (p1.status in (3,6,7,8) and p1.is_mb!=1)) " +
					"and p1.is_flow!=1 ) or (p1.is_flow=1 and p1.status in (1,3,6,7))) ";
			break;
		case 14://美贷首页显示状态
			/*statusSql=" and ((((p1.status=1 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+") " +
					") " +
					"and p1.is_flow!=1 ) or (p1.is_flow=1 and p1.account_yes<p1.account+0 and p1.status in (1,3,6,7))) ";*/
			statusSql=" and  (p1.status in(1,3,6,7,8) and ((1=1) " +
					"or (p1.is_flow=1)))";
			break;
		case 12://成功状态
		      statusSql = " and ((p1.status in (3,6,7,8) and p1.is_flow!=1) or (p1.status in(1,3,6,7) and p1.is_flow=1))";
		      break;
		case 13://中融资本首页显示
			statusSql=" and  (p1.status=1 and ((p1.account>p1.account_yes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+") " +
					"or (p1.is_flow=1 and p1.account>p1.account_yes+0)))";
		      break;  
		case 15://信达财富首页显示
			statusSql=" and  (p1.status=1 and ((p1.account>p1.account_yes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+") " +
					"or (p1.is_flow=1 and p1.account>p1.account_yes+0)))";
		      break;  
		case 19: //流标状态
			statusSql=" and (p1.status=1 and (p1.account=p1.account_yes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+"))";
			break;
		case 20: //等待复审状态
			statusSql=" and p1.status=1 and (p1.account_yes+0)=(p1.account+0) and p1.is_flow<>1";
			break;
		case 21:
			statusSql=" and p1.repayment_time";
		default:
			throw new RuntimeException("不正确的状态:"+getStatus());
		}
		return statusSql;
	}
	
	/**
	 * 是否跳过初审
	 */
	public void skipTrial(){
		
	}
	/**
	 * 是否跳过满表复审
	 */
	public void skipReview(){
		
	}
	public void skipStatus(){
		
	}
	public void verify(int status,int verifyStatus){
		
	}
	public Borrow getModel(){
		return this.model;
	}
	
	/**
	 * 计算借款标的利息
	 * @return
	 */
	public double calculateInterest(){
		return 0.0;
	}
	/**
	 * 计算借款标的利息
	 * @return
	 */
	public double calculateInterest(double validAccount){return 0.0;}
	
	public double calculateInterest(double validAccount, BorrowTender tender){return 0.0;}
	
	public InterestCalculator interestCalculator(){
		return null;
	}
	
	public InterestCalculator interestCalculator(double validAccount){
		return null;
	}
	
	public InterestCalculator interestCalculator(double validAccount, BorrowTender tender){
		return null;
	}
	
	public InterestCalculator interestCalculator(double validAccount, double remainRepayment){
		return null;
	}
	
	public InterestCalculator interestCalculator(double validAccount, double remainRepayment, BorrowTender t){
		return null;
	}
	
	/**
	 * 计算借款标的手续费
	 * @return
	 */
	public double calculateBorrowFee(){
		return 0.0;
	}
	
	public double calculateBorrowAward(BorrowTender bt){
		return 0.0;
	}
	
	public double calculateBorrowAward(){
		return 0.0;
	}
	
	public Date getRepayTime(int period){
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal.getTime();
	}
	
	public boolean isNeedRealName(){return true;}
	public boolean isNeedVIP() {return true;}
	public boolean isNeedEmail() {return true;}
	public boolean isNeedPhone() {return true;}
	public boolean isNeedVideo() {return true;}
	public boolean isNeedScene() {return true;}
	
	public boolean checkIdentify(User u) {throw new BussinessException("借款标的配置出错！");}
	
	public boolean checkModelData() {throw new BussinessException("借款标的配置出错！");}
	public boolean isLastPeriod(int order){return false;}
	
	public List<BorrowRepayment> getRepayment() {return null;}
	public List<BorrowRepayment> getRepayment(List<BorrowCollection> list) {return null;}
	public List<BorrowCollection> getCollectionList(BorrowTender t) {return null;}
	public List<BorrowCollection> getCollectionList(BorrowTender t,List<BorrowTender> list) {return null;}
	
	public double getAwardValue(double account){return 0;}
	public double getAwardValue(){return 0;}
	
	public double getManageFee(){return 0;};
	public double getManageFee(double account){return 0;}
	
	public double getOneTimeLimitFee(double account){ return 0; }//获取每期的管理费
	public double getManageFeeWithRule( int timeLimit ){ return 0; }
	
	public int getCreditValue(double account){return 0;}
	public List<BorrowRepayment> getFlowRepayment(BorrowTender bt, Borrow borrow) { //生成流标还款计划
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj!=null)&&(obj instanceof BorrowModel) && (getModel().getId()==((BorrowModel)obj).getModel().getId());  
	}
	@Override
	public int hashCode() {
		 int result = 145;  
		 result = 47*result +(int)getModel().getId(); 
		 return result;
	}
	/**
	 * 获取债权转让 的tender  和  collection 
	 * @param t 当前投标
	 * @param tenderList 当前投标的所有记录
	 * @param assignMentTender 转让的投标
	 * @param assignCollection 转让的投标对应的待收
	 * @param hasCapital  
	 * @param hasInterest
	 * @return
	 */
	public List<BorrowCollection> getAssignMentCollectionList(BorrowTender t,
			List<BorrowTender> tenderList, BorrowTender assignMentTender,
			List<BorrowCollection> assignCollection) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double calculateBorrowSecondFreeze() {
		return 0;
	};

}
