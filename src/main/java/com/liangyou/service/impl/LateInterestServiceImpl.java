package com.liangyou.service.impl;

/**
 * 计算逾期罚息，专用serviceImpl
 */
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.WebPaidDao;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.Rule;
import com.liangyou.domain.WebPaid;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.LateInterestService;
import com.liangyou.service.RuleService;
import com.liangyou.util.NumberUtils;

@Service(value="lateInterestService")
@Transactional
public class LateInterestServiceImpl extends BaseServiceImpl implements LateInterestService {
    Logger logger = Logger.getLogger(LateInterestServiceImpl.class);
    @Autowired
    BorrowRepaymentDao borrowRepaymentDao;
    
    //dytz 自动扣款功能 TGPROJECT-351 start
    @Autowired
    private WebPaidDao webPaidDao;
    @Autowired
    private RuleService ruleService;
    
    //dytz 自动扣款功能 TGPROJECT-351 end
    /**
     * 融都计算，公共逾期罚息计算器。
     */
    @Override
	public void calculateCommonLateInterest(){
		Date now = new Date();
		logger.info("逾期计算date:" + now.toString());
		SearchParam param = new SearchParam()
			.addParam("status", Operator.NOTEQ, 1)
			.addParam("webstatus", Operator.NOTEQ, 1)
			.addOrder(OrderType.ASC, "id")
			.addParam("borrow.type",Operator.NOTEQ, Constant.TYPE_FLOW)
			.addParam("borrow.type", Operator.NOTEQ, Constant.TYPE_CHARITY)
			.addParam("repaymentTime", Operator.LTE, now);
		List<BorrowRepayment> repaymentList = this.borrowRepaymentDao.findByCriteria(param);
		if(repaymentList!=null && repaymentList.size()>0){
			for (BorrowRepayment repayment : repaymentList) {
				logger.info("逾期计算...."+ repayment.getId());
				//逾期天数
				long lateDays = (now.getTime()-repayment.getRepaymentTime().getTime())/(24*60*60*1000);
				if((now.getTime()-repayment.getRepaymentTime().getTime())%(24*60*60*1000)>0){//不足一天按一天算
					lateDays++;
				}
				//逾期利率
				Double late_rate = Double.parseDouble(Global.getValue("late_rate"));
				//逾期剩余本金 = 所有剩余的本金的和
				Double capital = this.borrowRepaymentDao.getCapital(repayment.getBorrow().getId());
				repayment.setLateDays(Integer.parseInt(lateDays+""));
				double lateMoney = NumberUtils.format2(capital*late_rate*lateDays);
				repayment.setLateInterest(lateMoney);//罚息=剩余本金*0.04%*逾期天数
				this.borrowRepaymentDao.save(repayment);
				
			}
		}
	}
    
    /**
     * 国控小微逾期罚息计算方法，一次还款。可以直接从repayment中获取未还总额+利息
     */
    @Override
    public void calculateGkxw(){
    	Date now = new Date();
		logger.info("逾期计算date:" + now.toString());
		SearchParam param = new SearchParam()
			.addParam("status", Operator.NOTEQ, 1)
			.addParam("webstatus", Operator.NOTEQ, 1)
			.addOrder(OrderType.ASC, "id")
			.addParam("borrow.type",Operator.NOTEQ, Constant.TYPE_FLOW)
			.addParam("borrow.type", Operator.NOTEQ, Constant.TYPE_CHARITY)
			.addParam("repaymentTime", Operator.LTE, now);
		List<BorrowRepayment> repaymentList = this.borrowRepaymentDao.findByCriteria(param);
		if(repaymentList!=null && repaymentList.size()>0){
			for (BorrowRepayment repayment : repaymentList) {
				logger.info("逾期计算...."+ repayment.getId());
				//逾期天数
				long lateDays = (now.getTime()-repayment.getRepaymentTime().getTime())/(24*60*60*1000);
				if((now.getTime()-repayment.getRepaymentTime().getTime())%(24*60*60*1000)>0){//不足一天按一天算
					lateDays++;
				}
				//计算未还的本金+利息
				double waitAccount = repayment.getRepaymentAccount(); //由于国控小微是一次还款，所以可以直接获取还款总额即为未还总额
				//逾期罚息:（借款本金+利息）* [年利率 * 150% * 逾期天数 / 360]
				double lateInterest = waitAccount*(repayment.getBorrow().getApr()/100*1.5*lateDays/360);
				
				repayment.setLateDays(Integer.parseInt(lateDays+""));
				repayment.setLateInterest(NumberUtils.format2(lateInterest));//罚息=剩余本金*0.04%*逾期天数
				this.borrowRepaymentDao.save(repayment);
			}
		}
    }
  //TGPROJECT-351 wujing  跟新垫付表中罚息 start
	@Override
	public void calculatePartRepayment() {
		logger.info("进入部分还款的逾期罚息计算方法………………");
    	Date now = new Date();
		logger.info("逾期计算date:" + now.toString());
		SearchParam param = new SearchParam()
			.addParam("status", Operator.NOTEQ, 1)
			.addParam("webstatus", Operator.NOTEQ, 1)
			.addOrder(OrderType.ASC, "id")
			.addParam("borrow.type",Operator.NOTEQ, Constant.TYPE_FLOW)
			.addParam("borrow.type", Operator.NOTEQ, Constant.TYPE_CHARITY)
			.addParam("repaymentTime", Operator.LTE, now);
		List<BorrowRepayment> repaymentList = this.borrowRepaymentDao.findByCriteria(param);
 		if (null!=repaymentList && repaymentList.size()>0) {
			for (BorrowRepayment repayment : repaymentList) {
				Double late_rate = Double.parseDouble(Global.getValue("late_rate"));
				double lateMoney = repayment.getLateInterest();   //记录原始罚息
				double surplusAccount = repayment.getCapital() - repayment.getRepaymentYescapital();  //当期未还本金之和
				double dayLateMoney = surplusAccount * late_rate;   //计算当天的罚息金额
				double sumLateMoney = NumberUtils.format2(dayLateMoney + lateMoney);
				int lateDay = repayment.getLateDays()+1;//计算逾期天数
				repayment.setLateDays(lateDay);
				repayment.setLateInterest(sumLateMoney);
				//是否有自动扣款，若有，更新自动扣款中罚息
				Rule rule = ruleService.getRuleByNid("auto_repay_web_paid");  //判断自动扣款功能是否开启
				if (null !=rule && rule.getStatus() ==1) {//开启自动扣款
					int autoRepay = rule.getValueIntByKey("auto_repay");
					if (autoRepay ==1) {
						WebPaid webPaid = webPaidDao.getWebPaidByBorrowIdAndRepayId(repayment.getBorrow().getId(), repayment.getId());
						if (null != webPaid) {  //存在平台垫付记录
							webPaidDao.updateWebPenal(webPaid.getId(), dayLateMoney);
						}
					}
				}
				borrowRepaymentDao.save(repayment);
			}
		}
	}
	//TGPROJECT-351 wujing  跟新垫付表中罚息 end

	@Override
	public void calculateYDD() {
		
		Date now = new Date();
		logger.info("逾期计算date:" + now.toString());
		SearchParam param = new SearchParam()
			.addParam("status", Operator.NOTEQ, 1)
			.addParam("webstatus", Operator.NOTEQ, 1)
			.addOrder(OrderType.ASC, "id")
			.addParam("borrow.type",Operator.NOTEQ, Constant.TYPE_FLOW)
			.addParam("borrow.type", Operator.NOTEQ, Constant.TYPE_CHARITY)
			.addParam("repaymentTime", Operator.LTE, now);
		List<BorrowRepayment> repaymentList = this.borrowRepaymentDao.findByCriteria(param);
		Double late_rate = Double.parseDouble(Global.getValue("late_rate"));  //逾期罚息
		if (null != repaymentList && repaymentList.size()>0) {
			for (BorrowRepayment repayment : repaymentList) {
				int repayStyle = NumberUtils.getInt(repayment.getBorrow().getStyle()); //获取还款方式
				long lateDays = (now.getTime()-repayment.getRepaymentTime().getTime())/(24*60*60*1000);
				if((now.getTime()-repayment.getRepaymentTime().getTime())%(24*60*60*1000)>0){//不足一天按一天算
					lateDays++;
				}
				double lateInterest = 0;  //初始化罚息金额
				lateInterest = (repayment.getCapital()+repayment.getInterest())*late_rate*lateDays;
				repayment.setLateDays(Integer.parseInt(lateDays+""));
				repayment.setLateInterest(lateInterest);
				borrowRepaymentDao.save(repayment);
				
			}
			
		}
	}
	
}
