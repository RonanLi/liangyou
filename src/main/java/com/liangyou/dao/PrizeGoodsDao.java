package com.liangyou.dao;


import java.util.List;

import com.liangyou.domain.PrizeGoods;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
public interface PrizeGoodsDao extends BaseDao<PrizeGoods> {
	
	/**
	 * 根据抽奖规则id获取该规则奖品集合
	 * @param ruleId
	 * @return
	 */
	public List<PrizeGoods> getPrizeGoodsListByRuleId(long ruleId);
	
	
	/**
	 *  奖品规则表 set 抽中数量 = 抽中数量 +1 where 总数量 > 抽中数量 and 奖品id=xxx
	 * @param ruleId
	 * @param prizeGoodsId
	 */
	public void updateBestow(long ruleId,long prizeGoodsId);
	
	
	
}
