package com.liangyou.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.api.chinapnr.Repayment;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.service.RepaymentService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
@Service
@Transactional
public class RepaymentServiceImpl implements RepaymentService {

	Logger logger = Logger.getLogger(RepaymentServiceImpl.class);
	@Autowired
	public BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	public BorrowDao borrowDao;
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	@Autowired
	public AccountDao accountDao;
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end 
	@Override
	public BorrowRepayment getRepayment(long id) {
		return borrowRepaymentDao.find(id);
	}

	@Override
	public List<BorrowRepayment> getRepaymentListByBorrow(long id) {
		return borrowRepaymentDao.findByProperty("borrow", new Borrow(id));
	}

	@Override
	public void repay(long id,BorrowParam param) throws Exception {
		BorrowRepayment repay=borrowRepaymentDao.find(id);
		if(repay.getStatus()==1||repay.getWebstatus()==1){
			throw new BussinessException("还款正在处理中,请勿重复操作！");
		}
		//这里添加标 有效信息校验
		repay.setWebstatus(1);
		borrowRepaymentDao.update(repay);
		borrowRepaymentDao.flush();
		
		//还款的时候如果有未完成的债权转让标， 这个时候要取消掉
		Borrow model = repay.getBorrow();
	    List<BorrowTender> tenders = model.getBorrowTenders();
	    for (BorrowTender bt : tenders) {
			if(bt.getStatus()== -1){ //正在债权转让，由于已经还款，此时要取消掉
				Borrow assignMentBorrow = borrowDao.getAssignMentBorrowByTenderId(bt.getId());
				assignMentBorrow.setStatus(5);//设置取消的状态
				assignMentBorrow.setVerifyRemark("用户还款债权转让取消");
				assignMentBorrow.setVerifyTime(new Date());
				DisruptorUtils.failBorrow(BorrowHelper.getHelper(assignMentBorrow), null);
			}
		}
		
		DisruptorUtils.repay(repay, param);
	}
	
	@Override
	public void repayToWebSite(long id, BorrowParam param) {
		BorrowRepayment repay=borrowRepaymentDao.find(id);
		borrowRepaymentDao.lock(repay);
		if(repay.getStatus()==1){
			throw new BussinessException("还款已经处理,请勿重复操作！");
		}
		DisruptorUtils.repayToWebSite(repay, param);
	}
	
	@Override
	/**
	 * 查询还款计划
	 * @param borrowId
	 * @return
	 */
	public List<BorrowRepayment> getRepayMents(Long borrowId){
		SearchParam param = SearchParam.getInstance().addParam("borrow", new Borrow(borrowId));
		return borrowRepaymentDao.findByCriteria(param);
	}

	@Override
	public PageDataList<BorrowRepayment> getRepayMentsBySearchParam(SearchParam param) {
		return borrowRepaymentDao.findPageList(param);
	}

	@Override
	public boolean checkNoPayedRepayment(int period, Long borrowId ){
		int a = borrowRepaymentDao.getNoPayedRepayments(period, borrowId);
		return a==0;
	}

	/**
	 * 提前还款
	 */
	@Override
	public void priorRepay(long id, BorrowParam param,PriorRepayLog ppLog) throws Exception {
		BorrowRepayment repay=borrowRepaymentDao.find(id);
		if(repay.getStatus()==1||repay.getWebstatus()==1){
			throw new BussinessException("还款正在处理中,请勿重复操作！");
		}
		//这里添加标 有效信息校验
		repay.setWebstatus(1);
		borrowRepaymentDao.update(repay);
		borrowRepaymentDao.flush();
		
		//还款的时候如果有未完成的债权转让标， 这个时候要取消掉
		Borrow model = repay.getBorrow();
	    List<BorrowTender> tenders = model.getBorrowTenders();
	    for (BorrowTender bt : tenders) {
			if(bt.getStatus()== -1){ //正在债权转让，由于已经还款，此时要取消掉
				Borrow assignMentBorrow = borrowDao.getAssignMentBorrowByTenderId(bt.getId());
				assignMentBorrow.setStatus(5);//设置取消的状态
				assignMentBorrow.setVerifyRemark("用户还款债权转让取消");
				assignMentBorrow.setVerifyTime(new Date());
				DisruptorUtils.failBorrow(BorrowHelper.getHelper(assignMentBorrow), null);
			}
		}
		
		DisruptorUtils.priorRepay(repay, param,ppLog);
	}

	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	/**
	 * 提前还款
	 */
	@Override
	public void hfPriorRepay(BorrowRepayment repay,BorrowParam param,PriorRepayLog ppLog) throws Exception {
		if(repay.getStatus()==1||repay.getWebstatus()==1){
			throw new BussinessException("还款正在处理中,请勿重复操作！");
		}
		//这里添加标 有效信息校验
		repay.setWebstatus(1);
		borrowRepaymentDao.update(repay);
		borrowRepaymentDao.flush();
		
		//还款的时候如果有未完成的债权转让标， 这个时候要取消掉
		Borrow model = repay.getBorrow();
	    List<BorrowTender> tenders = model.getBorrowTenders();
	    for (BorrowTender bt : tenders) {
			if(bt.getStatus()== -1){ //正在债权转让，由于已经还款，此时要取消掉
				Borrow assignMentBorrow = borrowDao.getAssignMentBorrowByTenderId(bt.getId());
				assignMentBorrow.setStatus(5);//设置取消的状态
				assignMentBorrow.setVerifyRemark("用户还款债权转让取消");
				assignMentBorrow.setVerifyTime(new Date());
				DisruptorUtils.failBorrow(BorrowHelper.getHelper(assignMentBorrow), null);
			}
		}
		
		DisruptorUtils.hfPriorRepay(repay, param,ppLog);
	}
	
	@Override
	public Map<String,Double> getHfPriorRepayMoney(Borrow borrow) {
		Map<String,Double> map = new HashMap<String, Double>();
		Account act = accountDao.getAcountByUser(borrow.getUser());
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
		double repayInterest = remainderMoney*day*(borrow.getApr()/365000);
		//还款补偿金
		double compensation = NumberUtils.getDouble2(Global.getString("prior_repay_compen_ratio"))*remainderMoney*(borrow.getApr()/365000);
		//还款总金额
		double repayAccount = remainderMoney+repayInterest+compensation;
		logger.info("用户提前还款进入方法计算:还款本金"+remainderMoney+" 还款利息："+repayInterest + " 提前还款天数："+day +"  还款补偿金:"+compensation + "   还款总金额："+repayAccount);
		if(act.getUseMoney() < repayAccount){
			throw new BussinessException("您的可用余额不够，请充值！（还款总金额："+repayAccount+"，您的可用余额："+act.getUseMoney() +"）");
		}
		map.put("remainderMoney", remainderMoney);
		map.put("repayInterest", repayInterest);
		map.put("compensation", compensation);
		map.put("repayAccount", repayAccount);
		return map;
	}
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end 

	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	

	@Override
	public double doCheckLZFPriorrepay(BorrowRepayment rep) {
		//校验标的状态
		Borrow borrow = rep.getBorrow();
		if (borrow.getStatus() !=6 && borrow.getStatus() !=7) {
			throw new BussinessException("您的借款标状态异常！");
		}
		User user = rep.getBorrow().getUser();
		//校验账户余额是否够待还金额
		Account account  = accountDao.getAcountByUser(user);
		double waitOldRpayCapital = borrowRepaymentDao.getRemainderCapital(borrow.getId());  //计算剩余待还本金
		double waitRepayInterest = borrowRepaymentDao.getwaitRpayInterest(borrow.getId(), rep.getPeriod()); //本次提前还款待还利息总和
		double waitRepayAccount = waitOldRpayCapital +waitRepayInterest;
		if (account.getUseMoney()< waitRepayAccount) {
			throw new BussinessException("您账户的可用余额为："+account.getUseMoney()+",小于提前还款总额："+waitRepayAccount+"，请充值再进行提前还款操作");
		}
		return waitRepayAccount;
	}

	@Override
	public void doLZFPriorRepay(BorrowRepayment repay, BorrowParam param,
			PriorRepayLog ppLog) throws Exception {
		if(repay.getStatus()==1||repay.getWebstatus()==1){
			throw new BussinessException("还款正在处理中,请勿重复操作！");
		}
		//这里添加标 有效信息校验
		repay.setWebstatus(1);
		borrowRepaymentDao.update(repay);
		borrowRepaymentDao.flush();
		//还款的时候如果有未完成的债权转让标， 这个时候要取消掉
		Borrow model = repay.getBorrow();
	    List<BorrowTender> tenders = model.getBorrowTenders();
	    for (BorrowTender bt : tenders) {
			if(bt.getStatus()== -1){ //正在债权转让，由于已经还款，此时要取消掉
				Borrow assignMentBorrow = borrowDao.getAssignMentBorrowByTenderId(bt.getId());
				assignMentBorrow.setStatus(5);//设置取消的状态
				assignMentBorrow.setVerifyRemark("用户还款债权转让取消");
				assignMentBorrow.setVerifyTime(new Date());
				DisruptorUtils.failBorrow(BorrowHelper.getHelper(assignMentBorrow), null);
			}
		}
		
		DisruptorUtils.lzfPriorRepay(repay, param,ppLog);
		
	}

	@Override
	public Map<String, Double> getLZFPriorMoney(Borrow borrow) {
		double waitOldRpayCapital = borrowRepaymentDao.getRemainderCapital(borrow.getId());  //计算剩余待还本金
		SearchParam param = new SearchParam();
		param.addParam("status", 0);
		param.addParam("webstatus", 0);
		param.addParam("borrow.id", borrow.getId());
		param.addOrder(OrderType.ASC, "period");
		List<BorrowRepayment> repayList =borrowRepaymentDao.findByCriteria(param);
		//获取第一条未还款的还款计划
		BorrowRepayment repayMent = repayList.get(0);
		double waitRepayInterest = borrowRepaymentDao.getwaitRpayInterest(borrow.getId(), repayMent.getPeriod()); //本次提前还款待还利息总和
		double waitRepayAccount = NumberUtils.format2(waitOldRpayCapital +waitRepayInterest);   //待还总额
		double nowInterest = repayMent.getInterest();   //当期利息
		double CompenMoney = NumberUtils.format2(waitRepayInterest - nowInterest);  //计算补偿金
		Map<String, Double> dataResult = new HashMap<String, Double>();
		dataResult.put("waitRepayAccount", waitRepayAccount);
		dataResult.put("waitOldRpayCapital", waitOldRpayCapital);
		dataResult.put("nowInterest", nowInterest);
		dataResult.put("CompenMoney", CompenMoney);
		return dataResult;
	}
	
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
}
