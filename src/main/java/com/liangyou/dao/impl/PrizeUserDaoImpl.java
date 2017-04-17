package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.PrizeUserDao;
import com.liangyou.domain.PrizeUser;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Repository(value="prizeUserDao")
public class PrizeUserDaoImpl extends ObjectDaoImpl<PrizeUser> implements PrizeUserDao {

	@Override
	public int getUserAwardTotalCnt(long ruleId, long userId) {
		String jpql = "select count(*) from PrizeUser where prizeRule.id=?1 and user.userId=?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ruleId);
		query.setParameter(2, userId);
		List list = query.getResultList();
		Object obj = list.get(0);
		return NumberUtils.getInt(obj.toString());
	}

	@Override
	public int getUserAwardDayCnt(long ruleId, long userId) {
		String jpql = "select count(*) from PrizeUser where prizeRule.id=?1 and user.userId=?2 and addtime>"
				+ DateUtils.getIntegralTime().getTime() / 1000 + " and addtime<"
				+ DateUtils.getLastIntegralTime().getTime() / 1000;
		Query query = em.createQuery(jpql);
		query.setParameter(1, ruleId);
		query.setParameter(2, userId);
		List list = query.getResultList();
		Object obj = list.get(0);
		return NumberUtils.getInt(obj.toString());
	}

}
