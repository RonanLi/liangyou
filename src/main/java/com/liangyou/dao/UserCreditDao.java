package com.liangyou.dao;

import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;

public interface UserCreditDao extends BaseDao<UserCredit> {
	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start
	/**
	 * 通过用户Id查询积分账号
	 * @param userId
	 * @return
	 */
	public UserCredit getUserCreditByUserId(User user);
	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 end
}
