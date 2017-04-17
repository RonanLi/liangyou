package com.liangyou.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserAmountDao;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;

@Repository
public class UserAmountDaoImpl extends ObjectDaoImpl<UserAmount> implements UserAmountDao  {

	@Override
	public void updateCreditAmount(double credit, double creditUse,double creditNouse, long user_id) {
		String jpql="update UserAmount set credit=credit+?1,creditUse=creditUse+?2,creditNouse=creditNouse+?3 where user = ?4";
		Query q = em.createQuery(jpql).setParameter(1, credit).setParameter(2, creditUse)
				.setParameter(3, creditNouse).setParameter(4, new User(user_id));
		q.executeUpdate();
	}

	
	/**
	 * 更新用户信用额度
	 * @param totalVar
	 * @param useVar
	 * @param nouseVar
	 * @param userId
	 */
	@Override
	public  boolean updateCreditAmount(double totalVar, double useVar,double nouseVar,String userId) {
		
		String jpql=" update UserAmount set credit=credit+?1,creditUse=creditUse+?2,creditNouse=creditNouse+?3 where userId=?4 ";
		Query query =  em.createQuery(jpql);
		query.setParameter(1, totalVar);
		query.setParameter(2, useVar);
		query.setParameter(3, nouseVar);
		query.setParameter(4, userId);
		return query.executeUpdate()!=0;
	}
	
	@Override
	/**
	 *  by  user  find   userAmount
	 */
	public UserAmount getUserAmountByUser(User user) {
		String jpql = "from UserAmount where user = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, user);
		
		return (UserAmount)query.getSingleResult();
	}
}
