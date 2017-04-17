package com.liangyou.dao;

import com.liangyou.domain.CreditRank;

public interface UserCreditRankDao extends BaseDao<CreditRank> {

	/**
	 * 根据用户的积分 查询信用等级
	 * @param jifen
	 * @return
	 */
	public CreditRank getCreditRankByJiFen(int jifen);
	
}
