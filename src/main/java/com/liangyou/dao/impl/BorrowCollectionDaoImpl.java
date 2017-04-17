package com.liangyou.dao.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowTender;
import com.liangyou.util.DateUtils;

@Repository("borrowCollectionDao")
public class BorrowCollectionDaoImpl extends ObjectDaoImpl<BorrowCollection> implements BorrowCollectionDao {

	@Override
	public List<BorrowCollection> getCollectionByBorrow(long borrow_id) {
		String sql = "select bc from BorrowCollection bc join bc.borrowTender bt where bt.borrow = ?1 and  bc.status = 0 and bt.status= 0   ";
		Query query = em.createQuery(sql);
		query.setParameter(1, new Borrow(borrow_id));
		return query.getResultList();
	}

	/**
	 * 查询此标当期的所有投资人
	 * 
	 * @param borrowId
	 * @param period
	 * @return
	 */
	@Override
	public List<BorrowCollection> getCollectionByBorrowAndPeriod(long borrowId, int period) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where a.period = ?1 AND b.borrow_id = ?2 and a.status in(0,3) and b.status=0 ";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, period).setParameter(2, borrowId);
		return query.getResultList();
	}

	// v1.8.0.4 TGPROJECT-30 lx 2014-04-14 start
	@Override
	public List<BorrowCollection> getCollectionByBorrowIdAndPeriod(long borrowId, int period) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where a.period = ?1 AND b.borrow_id = ?2  ";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, period).setParameter(2, borrowId);
		return query.getResultList();
	}

	// v1.8.0.4 TGPROJECT-30 lx 2014-04-14 end
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getUserLateMoney(long tenderId) throws Exception {
		// 网站会垫付你从逾期那一期以后所有未还的借款，包含后面未逾期的，本金+利息
		String sql = " SELECT MIN(repay_time) repay_time , SUM(capital) capital, SUM(interest) interest FROM borrow_collection " + " WHERE tender_id = ?1 AND status = 0 ";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, tenderId);
		List<Object> list = query.getResultList();
		Object[] result = null;
		if (list != null && list.size() > 0) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				result = (Object[]) iterator.next();
			}
		}
		return result;

	}

	/**
	 * 更新网站垫款的收款纪录
	 * 
	 * @param borrowid
	 */
	@Override
	public void updateCollectionsStatus(long borrowId) throws Exception {
		String sql = " UPDATE borrow_collection c SET STATUS = 1,repay_yesaccount = repay_account , repay_yestime='" + DateUtils.dateStr4(new Date()) + "' WHERE EXISTS (SELECT t.id FROM borrow_tender t where t.borrow_id= ?1 AND t.id = c.tender_id) AND STATUS = 0 ";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, borrowId);
		query.executeUpdate();
	}

	/**
	 * 查询 转让标的 collection
	 * 
	 * @param bt
	 * @return
	 */
	@Override
	public List<BorrowCollection> getAssignMentCollectionByTenderId(BorrowTender bt) {
		String sql = " from BorrowCollection where borrowTender= ?1 and status = 0 order by id ";
		Query query = em.createQuery(sql);
		query.setParameter(1, bt);
		return query.getResultList();
	}

	/**
	 * 获取体验金当前用户体验金投标collection记录
	 *
	 * @param userId
	 * @param borrowId
	 * @return
	 */
	@Override
	public List<BorrowCollection> getExperienceCollection(Long userId, Long borrowId) {
		String sql = "select * from `borrow_collection` bc, `borrow_tender` bt where bc.`tender_id` = bt.`id` and bt.`user_id` = ?1 and bt.`borrow_id` = ?2";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, userId);
		query.setParameter(2, borrowId);
		return query.getResultList();
	}

	// v1.8.0.4_u2 TGPROJECT-299 lx start
	@Override
	public List<BorrowCollection> getCollectionList(long borrowId) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where b.borrow_id = ?1  ";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, borrowId);
		return query.getResultList();
	}

	@Override
	public List<BorrowCollection> getRepaidCollectionList(long borrowId) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where b.borrow_id = ?1 and a.status = 1 ";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, borrowId);
		return query.getResultList();
	}

	// v1.8.0.4_u2 TGPROJECT-299 lx end

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun start
	@Override
	public double getRemainderCapital(long tenderId) {
		String jpql = "select sum(capital) from BorrowCollection bc where bc.status=0  and bc.borrowTender.id= ?1 ";
		Query q = em.createQuery(jpql).setParameter(1, tenderId);
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	@Override
	public double getRemainderInterest(long tenderId) {
		String jpql = "select sum(interest) from BorrowCollection bc where bc.status=0  and bc.borrowTender.id= ?1 ";
		Query q = em.createQuery(jpql).setParameter(1, tenderId);
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	@Override
	public void updateCollectionsPriorRepay(long tenderId) throws Exception {
		String sql = " UPDATE borrow_collection set status = 1 where tender_id = ?1 and status = 0 ";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, tenderId);
		query.executeUpdate();
	}
	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start
	@Override
	public BorrowCollection getCollectionByTenderAndPeriod(long tenderId, int period) {
		String jpql = "from BorrowCollection bc where bc.status=0  and bc.borrowTender.id= ?1 and bc.period = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, tenderId);
		query.setParameter(2, period);
		List list = query.getResultList();

		return (BorrowCollection) list.get(0);
	}

	@Override
	public List<BorrowCollection> getRepayCollection(long borrowId, int period) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where a.period = ?1 AND a.status IN (0,2) AND b.borrow_id = ?2  limit 0,200";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, period).setParameter(2, borrowId);
		return query.getResultList();
	}

	@Override
	public List<BorrowCollection> getNoRepayCollection(long borrowId, int period) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where a.period = ?1 AND b.borrow_id = ?2  AND a.status = 0";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, period).setParameter(2, borrowId);
		return query.getResultList();
	}

	@Override
	public List<BorrowCollection> getRepayCollectionByPeriod(long borrowId, int period) {
		String sql = " SELECT DISTINCT a.* FROM borrow_collection a " + " inner JOIN borrow_tender b " + " ON a.tender_id = b.id " + "  where a.period = ?1 AND b.borrow_id = ?2  AND a.status = 1";
		Query query = em.createNativeQuery(sql, BorrowCollection.class);
		query.setParameter(1, period).setParameter(2, borrowId);
		return query.getResultList();
	}

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

}
