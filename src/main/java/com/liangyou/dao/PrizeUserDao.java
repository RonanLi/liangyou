package com.liangyou.dao;


import com.liangyou.domain.PrizeUser;

/**
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
public interface PrizeUserDao extends BaseDao<PrizeUser> {
	
	/**
	 * 获取用户才一个规则中的抽奖次数
	 * @param ruleId
	 * @param userId
	 * @return
	 */
	public int getUserAwardTotalCnt(long ruleId, long userId);
	
	
	/**
	 * 获取用户当天的抽奖次数
	 * @param ruleId
	 * @param userId
	 * @return
	 */
	public int getUserAwardDayCnt(long ruleId, long userId);
	
}
