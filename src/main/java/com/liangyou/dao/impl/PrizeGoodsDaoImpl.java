package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.PrizeGoodsDao;
import com.liangyou.domain.PrizeGoods;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Repository(value="prizeGoodsDao")
public class PrizeGoodsDaoImpl extends ObjectDaoImpl<PrizeGoods> implements PrizeGoodsDao {

	@Override
	public List<PrizeGoods> getPrizeGoodsListByRuleId(long ruleId) {
		String jqpl = "from PrizeGoods where prizeRule.id =?1";
		Query query = em.createQuery(jqpl);
		query.setParameter(1, ruleId);
		List<PrizeGoods> prizeList = query.getResultList();
		return prizeList;
	}

	@Override
	public void updateBestow(long ruleId, long prizeGoodsId) {
		// update 奖品规则表 set 抽中数量 = 抽中数量 +1 where 总数量 > 抽中数量 and 奖品id=xxx
		String jpql = "update PrizeGoods set bestow=ifnull(bestow,0)+1 where total>bestow and id=?1 and rule_id=?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, prizeGoodsId);
		query.setParameter(2, ruleId);
		query.executeUpdate();
	}

	
}
