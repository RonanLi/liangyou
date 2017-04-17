package com.liangyou.dao;

import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;

public interface UserAmountDao extends BaseDao<UserAmount> {

	/**
	 * 修改信用额度
	 * @param totalVar
	 * @param useVar
	 * @param nouseVar
	 * @param amount
	 */
	public void updateCreditAmount(double totalVar, double useVar,double nouseVar,long user_id);

	/**
	 * 更新用户信用额度
	 * @param totalVar
	 * @param useVar
	 * @param nouseVar
	 * @param userId
	 */
	public  boolean updateCreditAmount(double totalVar, double useVar, double nouseVar,
			String userId);
	
	public UserAmount getUserAmountByUser(User user);
}
