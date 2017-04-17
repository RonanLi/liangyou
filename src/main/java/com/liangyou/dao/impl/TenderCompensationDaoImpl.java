package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.TenderCompensationDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.CompensationAccount;
import com.liangyou.domain.TenderCompensation;
import com.liangyou.domain.User;

/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
@Repository(value="tenderCompensationDao")
@Transactional
public class TenderCompensationDaoImpl extends ObjectDaoImpl<TenderCompensation> implements TenderCompensationDao {
	Logger logger = Logger.getLogger(TenderCompensationDaoImpl.class);

	@Override
	public void updateTenderCompensation(double tenderMoney,
			double compensation, int days, double rate, int status, long user_id) {
		String jpql="update TenderCompensation set tenderMoney=tenderMoney+?1,compensation=compensation+?2,days=days+?3," +
				"rate=rate+?4,status=?5 where user=?6";
		Query q = em.createQuery(jpql).setParameter(1, tenderMoney).setParameter(2, compensation)
				.setParameter(3, days).setParameter(4, rate).setParameter(5, status)
				.setParameter(6, new User(user_id));
		q.executeUpdate();
	}

	@Override
	public double getSumBorrowCompensation(User user,Borrow borrow,int status) {
		String jpql = "select sum(tc.compensation) from TenderCompensation tc where borrowUser = ?1 and borrow = ?2 and status = ?3 ";
		Query query = em.createQuery(jpql)
				.setParameter(1, user)
				.setParameter(2, borrow)
				.setParameter(3, status);
		List list = query.getResultList();
		if (list.size()> 0 ) {
			return Double.parseDouble(list.get(0).toString());
		}else{
			return 0;
		}
	}

	@Override
	public List<TenderCompensation>  getTenderCompensationsByBorrow(User borrowUser,Borrow borrow,int status){
		String jpql = "from TenderCompensation tc where borrowUser = ?1 and borrow = ?2 and status = ?3";
		Query query = em.createQuery(jpql).setParameter(1, borrowUser).setParameter(2, borrow).setParameter(3, status);
		
		return query.getResultList();
	}

	@Override
	public TenderCompensation getTenderCompensationByUser(User tenderUser,Borrow borrow,int status,long tender_id) {
		String jpql = "from TenderCompensation tc where tenderUser = ?1 and borrow = ?2 and status = ?3 and tender = ?4";
		Query query = em.createQuery(jpql).setParameter(1, tenderUser).setParameter(2, borrow).
				setParameter(3, status).setParameter(4, new BorrowTender(tender_id));
		List<TenderCompensation> list = query.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}else{
			return null;
		}
	}
}
