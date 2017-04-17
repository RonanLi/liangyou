package com.liangyou.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Repository(value = "borrowTenderDao")
public class BorrowTenderDaoImpl extends ObjectDaoImpl<BorrowTender> implements BorrowTenderDao {

	private static Logger logger = Logger.getLogger(BorrowTenderDaoImpl.class);

	@Override
	public List<BorrowTender> getBorrowTenderList(long borrowid) {
		String jpql = " from BorrowTender where borrow = ?1 and status = 0 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new Borrow(borrowid));
		return query.getResultList();
	}
	
	@Override
	public List<BorrowTender> getBorrowTenderList(String cid, Date startData, Date endDate) {
		String jpql = "from BorrowTender bt where bt.yqfCpsCampaignId = ?1 and bt.addtime between ?2 and ?3";
		Query query = em.createQuery(jpql);
		query.setParameter(1, cid);
		query.setParameter(2, startData, TemporalType.TIMESTAMP);
		query.setParameter(3, endDate, TemporalType.TIMESTAMP);
		
		return query.getResultList();
	}
	
	@Override
	@Transactional
	public Integer updateBT(long btId, String aid, String cid, String feedback, String target, String channel) {
		logger.info("执行更新：btid：" + btId + ", aid：" + aid + ", cid：" + cid + ", feedback：" + feedback + ", target：" + target + ", channel" + channel);

		String jpql = "UPDATE BorrowTender SET yqfCpsCampaignId = ?1, yqfCpsAid = ?2, yqfCpsFeedback = ?3, yqfCpsTarget = ?4, yqfCpsChannel = ?5 where id = ?6";
		Query query = em.createQuery(jpql);
		query.setParameter(1, cid);
		query.setParameter(2, aid);
		query.setParameter(3, feedback);
		query.setParameter(4, target);
		query.setParameter(5, channel);
		query.setParameter(6, btId);
		int numb = query.executeUpdate();
		
		logger.info("更新bt中的cid结果：" + numb);
		return numb;
	}
	
	@Override
	public List getBorrowTenderListByborrowId(long id, int status) {
		return null;
	}

	@Override
	public List<BorrowTender> getBorrowTenderListByborrowId(long id) {
		String jpql = "select bt from BorrowTender bt join bt.borrow br where br.id = ?1 and bt.status = 0 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, id);
		return query.getResultList();
	}

	@Override	
	public List<BorrowTender> getBorrowTenderListByborrow(long id) {
		String jpql = "select bt from BorrowTender bt join bt.borrow br where br.id = ?1 and bt.status in (0,1) ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, id);
		return query.getResultList();
	}

	@Override
	public List getBorrowTenderListByborrowId(long id, long user_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getBorrowTenderListByborrowId(long id, int start, int pernum, SearchParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getTenderListByUserId(long user_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getInvestBorrowTenderListByUserid(long user_id, int start, int end, SearchParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInvestBorrowTenderCountByUserid(long user_id, SearchParam param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getSuccessBorrowTenderList(long user_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getSuccessBorrowTenderList(long user_id, int start, int end, SearchParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double hasTenderTotalPerBorrowByUserid(long bid, long userId) {
		String jpql = "select sum(account) from BorrowTender where borrow=?1 and user=?2";
		Query q = em.createQuery(jpql).setParameter(1, new Borrow(bid)).setParameter(2, new User(userId));
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	@Override
	public BorrowTender getAssignMentTender(long borrowId) {
		String jpql = " from BorrowTender where assignmentId = ?1";
		Query query = em.createQuery(jpql).setParameter(1, borrowId);
		List<BorrowTender> list = query.getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public double sumCollectionMoney(long userId) {
		String jpql = "select sum(waitAccount) from BorrowTender where user=?1";
		Query q = em.createQuery(jpql).setParameter(1, new User(userId));
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	@Override
	public int sumTenderByDay() {
		String jpql = " SELECT COUNT(b) FROM BorrowTender b WHERE date(addtime)=curdate() ";
		Query query = em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}

	@Override
	public int sumTenderByTenderNo(String tenderNo) {
		String jpql = "SELECT COUNT(b) FROM BorrowTender b WHERE  subOrdId=?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, tenderNo);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}

	@Override
	public int sumTender() {
		String jpql = "SELECT count( distinct b.user.id ) FROM BorrowTender b where status in (0,1,3) ";// 3为债权转让
		Query query = em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}

	/**
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	@Override
	public double getTenderMoneyByTime(String time1, String time2, long userId) {
		String sql = "select sum(b.account) from BorrowTender b where user =?1 and status in(0,1,3)";
		if (!StringUtils.isNull(time1).equals("") && !StringUtils.isNull(time2).equals("")) {
			sql = sql + " and addtime>='" + time1 + "' and addtime<'" + time2 + "'";
		}
		Query query = em.createQuery(sql);
		query.setParameter(1, new User(userId));
		Object ob = query.getSingleResult();
		if (ob == null)
			return 0;
		return (Double) ob;

	}

	@Override
	public double countUserCollectionMoney(String time1, String time2, long userId) {
		String sql = "select sum(waitAccount) from BorrowTender where user=?1 ";
		if (!StringUtils.isNull(time1).equals("") && !StringUtils.isNull(time2).equals("")) {
			sql = sql + " and addtime>='" + time1 + "' and addtime<'" + time2 + "'";
		}
		Query q = em.createQuery(sql).setParameter(1, new User(userId));
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	@Override
	public int countUserTender(String time1, String time2, long userId) {
		String sql = "SELECT COUNT(b) FROM BorrowTender b where user =?1 and status in (0,1,3) ";// 3为债权转让
		if (!StringUtils.isNull(time1).equals("") && !StringUtils.isNull(time2).equals("")) {
			sql = sql + "and addtime>='" + time1 + "' and addtime<'" + time2 + "'";
		}
		Query query = em.createQuery(sql).setParameter(1, new User(userId));
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}

	// v1.8.0.4 TGPROJECT-63 lx 2014-04-16 start
	@Override
	public double sumInterest() {
		String sql = "select sum(repaymentYesinterest) from BorrowTender  ";
		Query q = em.createQuery(sql);
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	// v1.8.0.4 TGPROJECT-63 lx 2014-04-16 end

	@Override
	public double countTenderRepayment(long borrow_id) {
		String sql = "SELECT sum(repaymentAccount) FROM BorrowTender b where borrow =?1";
		Query q = em.createQuery(sql).setParameter(1, new Borrow(borrow_id));
		Object ret = q.getSingleResult();
		if (ret == null)
			return 0;
		return (Double) ret;
	}

	public List<BorrowTender> getBorrowTenderByOrderNo(String subOrdId) {
		String sql = " FROM BorrowTender b where subOrdId =?1";
		Query q = em.createQuery(sql).setParameter(1, subOrdId);
		return q.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public BorrowTender getListByUserId(long userId) {
		String jpql = "select bt from BorrowTender bt,User u,Borrow b where bt.user.userId=u.userId and bt.borrow.id = b.id and u.userId = ?1 and bt.borrow.type != 115 and u.userType.typeId=2";
		Query query = em.createQuery(jpql).setParameter(1, userId);
		List<BorrowTender> list = query.getResultList();
		if (null==list||list.size() == 0) {
			return null;
		} 
		return (BorrowTender)list.get(0);
	}
	
	}

