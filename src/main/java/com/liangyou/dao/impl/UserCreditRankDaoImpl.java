package com.liangyou.dao.impl;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserCreditRankDao;
import com.liangyou.domain.CreditRank;
@Repository
public class UserCreditRankDaoImpl extends ObjectDaoImpl<CreditRank> implements UserCreditRankDao {

	private static Logger logger = Logger.getLogger(UserCreditRankDaoImpl.class);
	
	/**
	 * 根据用户的积分 查询信用等级
	 * @param jifen
	 * @return
	 */
	@Override
	public CreditRank getCreditRankByJiFen(int jifen){

		String sql = " from CreditRank where point1 <= ?1 and point2 >= ?2  ";
		Query qy = em.createQuery(sql);
		qy.setParameter(1, jifen);
		qy.setParameter(2, jifen);
	 return (CreditRank)qy.getSingleResult();
	}
	
}
