package com.liangyou.dao;

import com.liangyou.domain.Credit;
import com.liangyou.domain.User;


public interface CreditDao extends BaseDao<Credit>{
	/**
	 * 获取积分对象
	 * @param user
	 * @return
	 */
	public Credit getCreditByUser(User user);
	
}
