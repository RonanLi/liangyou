package com.liangyou.dao;

import com.liangyou.domain.YwdUser;


public interface YwdUserDao extends BaseDao<YwdUser> {
	/**
	 * 根据 用户名，password 查询用户
	 * @param username
	 * @param password
	 * @return ywdUser
	 */
	public YwdUser getYwdUserByUsernameAndPassword(String username,String password);
}
