package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.CompensationAccountDao;
import com.liangyou.domain.CompensationAccount;
import com.liangyou.domain.User;

/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
@Repository(value="compensationAccountDao")
public class CompensationAccountDaoImpl extends ObjectDaoImpl<CompensationAccount> implements CompensationAccountDao {
	Logger logger = Logger.getLogger(CompensationAccountDaoImpl.class);

	@Override
	public void updateCompensationAccount(double collectionCompensation, double repayCompensation, 
										  double collectionYesCompensation, double repayYesCompensation, 
										  long user_id) {
		String jpql="update CompensationAccount set collectionCompensation=collectionCompensation+?1,repayCompensation=repayCompensation+?2,collectionYesCompensation=collectionYesCompensation+?3," +
				"repayYesCompensation=repayYesCompensation+?4 where user=?5";
		Query q = em.createQuery(jpql).setParameter(1, collectionCompensation).setParameter(2, repayCompensation)
				.setParameter(3, collectionYesCompensation).setParameter(4, repayYesCompensation)
				.setParameter(5, new User(user_id));
		q.executeUpdate();
	}

	@Override
	public CompensationAccount getCompensationAccountByUser(User user) {
		String jpql = "from CompensationAccount where user = ?1";
		Query query = em.createQuery(jpql).setParameter(1, user);
		List<CompensationAccount> list = query.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}else{
			return null;
		}
	}
}
