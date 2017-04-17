package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.WebPaidDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.WebPaid;
/**
 * 垫付资金表
 * @author wujing
 *
 */
@Repository("webPaidDao")
public class WebPaidDaoImpl extends ObjectDaoImpl<WebPaid> implements
		WebPaidDao{

	@Override
	public List<WebPaid> getWaitWebPaidList() {
		String jpql = "from WebPaid where waitRepay >0";
		Query query = em.createQuery(jpql);
		return query.getResultList();
	}

	@Override
	public void updateWebPenal(long WebId, double penalMoney) {
		String jpql = "update WebPaid set waitReceivePenal = waitReceivePenal+?1  ,waitRepay = waitRepay +?2  where id=?3";
		Query query = em.createQuery(jpql);
		query.setParameter(1, penalMoney);
		query.setParameter(2, penalMoney);
		query.setParameter(3, WebId);
		query.executeUpdate();
	}

	@Override
	public void updateRepayWeb(long Id, double repayMoney) {
		String jpql = "update WebPaid set yesRepayAccount = yesRepayAccount+?1 ,waitRepay =waitRepay+?2 where id=?3";
		Query query = em.createQuery(jpql);
		query.setParameter(1, repayMoney);
		query.setParameter(2, -repayMoney);
		query.setParameter(3, Id);
		query.executeUpdate();
	}

	@Override
	public WebPaid getWebPaidByBorrowIdAndRepayId(long borrowId, long repaymentId) {
		String jpql = "from WebPaid where repayment.id=?1 and borrow.id=?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, repaymentId);
		query.setParameter(2, borrowId);
		List list = query.getResultList();
		if (list.size()>0) {
			return (WebPaid)list.get(0);
		}else{
			return null;
		}
	}

	
	
	

}
