package com.liangyou.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.User;



/**
 * 用户表新增、删除、查找Dao类
 * 
 * @author zhuliming
 * @date 2013-5-12-下午2:08:27
 * @version 1.1
 * 
 * <b>Copyright (c)</b> 2012-融都-版权所有<br/>
 * 
 */
@Repository("borrowRepaymentDao")
public class BorrowRepaymentDaoImpl extends ObjectDaoImpl<BorrowRepayment> implements BorrowRepaymentDao {
	
	private static Logger logger = Logger.getLogger(BorrowRepaymentDaoImpl.class);
	
	/**
	 * 获取当前期数前的
	 * @param period
	 * @param borrowId
	 * @return
	 */
	@Override
	public int getNoPayedRepayments( int period,Long borrowId ){
		String jpql = "  SELECT COUNT(br) FROM BorrowRepayment br WHERE borrow= ?1 AND period < ?2  and status = ?3 and webstatus= ?4 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new Borrow(borrowId))
		.setParameter(2, period).setParameter(3, 0)
		.setParameter(4, 0);
		 Object ob = query.getSingleResult();
		 return Integer.parseInt(ob.toString());
	}
	
	@Override
	public List<BorrowRepayment> getNotRepayByBorrow(Long borrowId){
		String sql = " select br from BorrowRepayment br where br.status = 0 and br.webstatus = 0 and br.borrow=?1  ";
	    Query query = em.createQuery(sql);
	    query.setParameter(1, new Borrow(borrowId) );
		return query.getResultList();
	}

	@Override
	public double getCapital(long borrowId) {
		String jpql = "SELECT SUM(capital) FROM BorrowRepayment WHERE borrow= ?1 AND status <> 1 and webstatus <> 1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new Borrow(borrowId));
		Object obj = query.getSingleResult();
		if(obj == null){
			return 0;
		}else{
			return (Double)obj;
		}
	}
	
	@Override
	public void updateBorrowWebstatus(long borrowId) { //网站垫付，修改还款状态
		String sql = " UPDATE borrow_repayment set status = 2 , webstatus = 3 WHERE borrow_id = ?1 AND status = 0 AND webstatus = 0";
		Query query = em.createNativeQuery(sql, BorrowRepayment.class);
		query.setParameter(1, borrowId);
		query.executeUpdate();
	}

	@Override
	public BorrowRepayment getBorrowRepaymentByBorrowIdAndPeriod(long borrowId, int repaymentId){
		String hql = " from BorrowRepayment where borrow.id =?1 and period =?2 ";
		Query query =  em.createQuery(hql);
		query.setParameter(1, borrowId);
		query.setParameter(2, repaymentId);
	    List<BorrowRepayment> list =  query.getResultList();
	    if(list.size() >0){
	    	return list.get(0);
	    }else{
	    	return null;
	    }
	}
	
	public int getLateRepaymentByUser(User user){
		String jpql = " SELECT COUNT(*) FROM BorrowRepayment WHERE borrow.user =?1 AND lateDays >= 1  AND status != 1 AND webstatus != 1 ";
		Query query =  em.createQuery(jpql);
		query.setParameter(1, user);
		Object ob = query.getSingleResult();
		return Integer.parseInt(ob.toString());
	}

	@Override
	public double sumNotRepaymentAccountByUser(long userId) {
		String jpql = "SELECT SUM(r.capital) FROM BorrowRepayment r WHERE  r.borrow.status in (6,7) AND r.status <> 1 AND r.webstatus <> 1  AND r.borrow.user.userId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userId);
		Object obj = query.getSingleResult();
		if(obj == null){
			return 0;
		}else{
			return (Double)obj;
		}
	}
	
	@Override
	public List<BorrowRepayment> getNotRepayForCreditStatus(){
		String sql = " from BorrowRepayment br where br.creditStatus = 0 and  br.repaymentTime < ?1 ";
	    Query query = em.createQuery(sql);
	    query.setParameter(1, new Date());
		return query.getResultList();
	}
	
	@Override
	public List<BorrowRepayment> getRepayListForRewardStatus(){
		String sql = " from BorrowRepayment br where br.rewardStatus = 0 and  br.repaymentTime < ?1 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, new Date());
		return query.getResultList();
	}

	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	@Override
	public BorrowRepayment getRecentlyRepayment(long borrowId) {
		String sql = " from BorrowRepayment br where status = 1 and br.borrow.id = ?1  order by id desc ";
	    Query query = em.createQuery(sql);
	    query.setParameter(1,borrowId);
	    List<BorrowRepayment> list = query.getResultList();
		return list.get(0);
	}
	
	@Override
	public double getRemainderCapital(long borrowId) {
		String jpql="select sum(capital) from BorrowRepayment br where br.status=0  and br.borrow.id= ?1 ";
		Query q=em.createQuery(jpql).setParameter(1, borrowId);
		Object ret=q.getSingleResult();
		if(ret==null) return 0;
		return (Double)ret;
	}
	
	@Override
	public double getRemainderInterest(long borrowId) {
		String jpql="select sum(interest) from BorrowRepayment br where br.status=0  and br.borrow.id= ?1 ";
		Query q=em.createQuery(jpql).setParameter(1, borrowId);
		Object ret=q.getSingleResult();
		if(ret==null) return 0;
		return (Double)ret;
	}
	
	@Override
	public void updatePriorRepay(long borrowId) { //网站垫付，修改还款状态
		String sql = " UPDATE borrow_repayment set status = 1 , webstatus = 1 WHERE borrow_id = ?1 AND status = 0 AND webstatus = 0";
		Query query = em.createNativeQuery(sql, BorrowRepayment.class);
		query.setParameter(1, borrowId);
		query.executeUpdate();
	}
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end

	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	@Override
	public double getwaitRpayInterest(long borrowId, int period) {
		String jpql="select sum(interest) from BorrowRepayment br where br.status=0  and br.borrow.id= ?1  and (br.period >=?2 and br.period <=?3)";
		Query q=em.createQuery(jpql).setParameter(1, borrowId);
		q.setParameter(2, period);
		q.setParameter(3, period+1);
		Object ret=q.getSingleResult();
		if(ret==null) return 0;
		return (Double)ret;
		
		
	}
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end

	//v1.8.0.5_u4 TGPROJECT-386   qinjun  2014-08-11 start 
	@Override
	public List<BorrowRepayment> notRepayRepaymentList() {
		String sql = " select br from BorrowRepayment br where br.status = 0 and br.webstatus = 0 ";
	    Query query = em.createQuery(sql);
		return query.getResultList();
	}
	//v1.8.0.5_u4 TGPROJECT-386   qinjun  2014-08-11 start 

	@Override
	public void updateRepayment(double account, double interest,
			double lateFee, double comMoney, long id) {
		String jpql = "update BorrowRepayment set repaymentYescapital =repaymentYescapital +?1 ,repaymentYesinterest = repaymentYesinterest+?2" +
					  " ,repayYesLateInterest=repayYesLateInterest+?3 ,compensation =compensation+?4  where id=?5";
		Query query = em.createQuery(jpql);
		query.setParameter(1, account);
		query.setParameter(2, interest);
		query.setParameter(3, lateFee);
		query.setParameter(4, comMoney);
		query.setParameter(5, id);
		query.executeUpdate();
		em.refresh(getRepaymentId(id));
	}

	@Override
	public BorrowRepayment getRepaymentId(long id) {
		String jpql = "from BorrowRepayment where id = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, id);
		List list = query.getResultList();
		if (list.size()>0) {
			return (BorrowRepayment)list.get(0);
		}
		
		return null;
	}
	
	
	
}
