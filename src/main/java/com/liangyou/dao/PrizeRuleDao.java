package com.liangyou.dao;


import com.liangyou.domain.PrizeRule;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
public interface PrizeRuleDao extends BaseDao<PrizeRule> {
	
	/**
	 *更新领用金额
	 * @param ruleId  规则id
	 * @param awardmoney   
	 */
	public void updateBestowMoney(long ruleId,double awardmoney);
	
}
