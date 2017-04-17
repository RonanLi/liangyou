package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowTender;

public interface BorrowCollectionDao extends BaseDao<BorrowCollection> {
	public List<BorrowCollection> getCollectionByBorrow(long borrow_id);

	/**
	 * 查询此标当期的所有投资人
	 * 
	 * @param borrowId
	 * @param period
	 * @return
	 */
	public List<BorrowCollection> getCollectionByBorrowAndPeriod(long borrowId, int period);

	// v1.8.0.4 TGPROJECT-30 lx 2014-04-14 start
	/**
	 * 查询此标当期的所有投资人,不管是否已经还款
	 * 
	 * @param borrowId
	 * @param period
	 * @return
	 */
	public List<BorrowCollection> getCollectionByBorrowIdAndPeriod(long borrowId, int period);

	// v1.8.0.4 TGPROJECT-30 lx 2014-04-14 end
	/**
	 * 网站垫付，查询本金（从逾期那一期以后所有未还的借款，包含后面未逾期的）， 和当前时间，垫付日之前的为还利息
	 * 
	 * @param tenderid
	 * @return
	 * @throws Exception
	 */
	public Object[] getUserLateMoney(long tenderId) throws Exception;

	/**
	 * 更新网站垫款的收款纪录
	 * 
	 * @param borrowId
	 * @throws Exception
	 */
	public void updateCollectionsStatus(long borrowId) throws Exception;

	/**
	 * 查询 转让标的 collection
	 * 
	 * @param bt
	 * @return
	 */
	public List<BorrowCollection> getAssignMentCollectionByTenderId(BorrowTender bt);

	/**
	 * add by gy 2016-11-4 10:05:25 查询体验标的投标collection
	 * 
	 * @param userId
	 * @param borrowId
	 * @return
	 */
	public List<BorrowCollection> getExperienceCollection(Long userId, Long borrowId);

	// v1.8.0.4_u2 TGPROJECT-299 lx start
	public List<BorrowCollection> getCollectionList(long borrowId);
	// v1.8.0.4_u2 TGPROJECT-299 lx end

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun start
	/**
	 * 剩余未还款的本金
	 * 
	 * @param borrowId
	 * @return
	 */
	public double getRemainderCapital(long tenderId);

	/**
	 * 剩余未还款的利息
	 * 
	 * @param borrowId
	 * @return
	 */
	public double getRemainderInterest(long tenderId);

	/**
	 * 更新提前还款剩余待还状态
	 * 
	 * @param tenderId
	 * @throws Exception
	 */
	public void updateCollectionsPriorRepay(long tenderId) throws Exception;
	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start
	/**
	 * 根据tenderId和期数，查询待收对象
	 * 
	 * @param tenderId
	 * @param period
	 * @return
	 */
	public BorrowCollection getCollectionByTenderAndPeriod(long tenderId, int period);
	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

	/**
	 * 环迅还款使用，一次去200条
	 * 
	 * @param borrowId
	 * @param period
	 * @return
	 */
	public List<BorrowCollection> getRepayCollection(long borrowId, int period);

	public List<BorrowCollection> getRepaidCollectionList(long borrowId);

	public List<BorrowCollection> getNoRepayCollection(long borrowId, int period);

	public List<BorrowCollection> getRepayCollectionByPeriod(long borrowId, int period);
}