package com.liangyou.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.PrizeRuleDao;
import com.liangyou.domain.PrizeRule;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Repository(value="prizeRuleDao")
public class PrizeRuleDaoImpl extends ObjectDaoImpl<PrizeRule> implements PrizeRuleDao {

	@Override
	public void updateBestowMoney(long ruleId, double awardmoney) {
		// update 抽奖规则表 set 领用金额 = 领用金额 +? where 总金额 > 领用金额 and id=xxx
		String jpql = "update PrizeRule set bestowMoney=ifnull(bestowMoney,0)+?1 where totalMoney>bestowMoney and id=?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, awardmoney);
		query.setParameter(2, ruleId);
		query.executeUpdate();
	}

}
