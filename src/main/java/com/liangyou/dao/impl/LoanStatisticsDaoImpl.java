package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.LoanStatisticsDao;
import com.liangyou.domain.LoanStatistics;
import com.liangyou.domain.User;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

/**
 * v1.8.0.4_u4 TGPROJECT-371 	qinjun 2014-07-22
 */
@Repository(value="loanStatisticsDao")
public class LoanStatisticsDaoImpl extends ObjectDaoImpl<LoanStatistics> implements LoanStatisticsDao {
	private static Logger logger = Logger.getLogger(LoanStatisticsDaoImpl.class);

	@Override
	public LoanStatistics getLoanStatisticsByUser(User user){
		String sql = " select l from LoanStatistics l where l.user.userId = ?1 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, user.getUserId());
		List<LoanStatistics> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<LoanStatistics> getTenderStatisticsList() {
		String sql="select ifnull(sum(t.account),0) as tenderTotal, " +
				" ifnull(count(1),0) as tenderCount, " +
				" ifnull(sum(t.wait_account),0) as waitCollectionCapital, " +
				" ifnull(sum(t.repayment_yesinterest),0) as earnInterest, " +
				" t.user_id as userId" +
				" from borrow_tender t " +
				" left join borrow b on b.id=t.borrow_id " +
				" where b.status in(6,7,8)  group by t.user_id " ;
		List<LoanStatistics> loanList = new ArrayList<LoanStatistics>();
		try{
			Query query = em.createNativeQuery(sql);
			List<Object> result = query.getResultList();
			if (result.size()>0){ 
				Iterator<Object> iterator = result.iterator(); 
				while(iterator.hasNext()){
					LoanStatistics loan = new LoanStatistics();
					Object[] row = ( Object[]) iterator.next();					
					double tenderTotal = NumberUtils.getDouble(row[0].toString());
					int tenderCount = NumberUtils.getInt(row[1].toString());
					double waitCollectionCapital = NumberUtils.getDouble(row[2].toString());
					double earnInterest = NumberUtils.getDouble(row[3].toString());
					long userId = NumberUtils.getLong(row[4].toString());
					loan.setTenderTotal(tenderTotal);
					loan.setTenderCount(tenderCount);
					loan.setWaitCollectionCapital(waitCollectionCapital);
					loan.setEarnInterest(earnInterest);
					loan.setUser(new User(userId));
					loanList.add(loan);
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}		
		return loanList;		
	}

	@Override
	public int getWaitTenderCountByUser(long userId) {
		String sql="select ifnull(count(1),0) as num from borrow_tender where wait_account > 0 and user_id=?1";
		List list = null;
		int num = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, userId);
			list  = query.getResultList();
			if(list!=null&&list.size()>0){
				Object ob = list.get(0);
				num = NumberUtils.getInt(ob.toString());
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return num;
	}
	@Override
	public double getLateCapitalByUser(long userId) {
		String sql="select ifnull(sum(c.repay_yescapital-capital),0) as num from borrow_collection c "
				+ " left join borrow_tender t on t.id = c.tender_id "
				+ " where repay_time < now() and t.user_id=?1 ";
		double total = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, userId);
			List list  = query.getResultList();
			if(list!=null&&list.size()>0){
				Object ob = list.get(0);
				total = NumberUtils.getDouble(ob.toString());
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}
		return total;
	}
	
	@Override
	public List<LoanStatistics> getLoanStatisticsList() {
		String sql="select ifnull(sum(b.account),0) as loanTotal," +
				" ifnull(count(1),0)as loanCount, " +
				" user_id as userId" +
				" from borrow b " +
				"where b.status in (6,7,8)   group by b.user_id ";
		List<LoanStatistics> loanList = new ArrayList<LoanStatistics>();
		try{
			Query query = em.createNativeQuery(sql);
			List<Object> result = query.getResultList();
			if (result.size()>0){ 
				Iterator<Object> iterator = result.iterator(); 
				while(iterator.hasNext()){
					LoanStatistics loan = new LoanStatistics();
					Object[] row = ( Object[]) iterator.next();					
					double loanTotal = NumberUtils.getDouble(row[0].toString());
					int loanCount = NumberUtils.getInt(row[1].toString());
					long userId = NumberUtils.getLong(row[2].toString());
					loan.setLoanTotal(loanTotal);
					loan.setLoanCount(loanCount);
					loan.setUser(new User(userId));
					loanList.add(loan);
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}		
		return loanList;
	}
	
	@Override
	public double getWaitRepayCapitalByUser(long userId) {
		String sql="select ifnull(sum(r.capital-r.repayment_yescapital),0) as num from borrow_repayment r "
				+ " left join borrow b on b.id = r.borrow_id "
				+ " where b.user_id=?1 ";
		double total = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, userId);
			List list  = query.getResultList();
			if(list!=null&&list.size()>0){
				Object ob = list.get(0);
				total = NumberUtils.getDouble(ob.toString());
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}
		return total;
	}
	
	@Override
	public double getDiyBorrowAcountByUser(long userId) {
		String sql="select ifnull(sum(account*apr/100),0) as num from borrow where user_id=?1 ";
		double total = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, userId);
			List list  = query.getResultList();
			if(list!=null&&list.size()>0){
				Object ob = list.get(0);
				total = NumberUtils.getDouble(ob.toString());
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}
		return total;
	}
}
