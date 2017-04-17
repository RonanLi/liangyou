package com.liangyou.dao;

import com.liangyou.domain.UserCreditType;

public interface UserCreditTypeDao extends BaseDao<UserCreditType> {

	/**
	 * 根据用户的类型查询积分类型
	 * @param type
	 * @return
	 */
	public UserCreditType getUserCreditType(String type);

	
}
