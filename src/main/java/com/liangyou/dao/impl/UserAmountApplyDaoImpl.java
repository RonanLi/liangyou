package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserAmountApplyDao;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmountApply;

@Repository
public class UserAmountApplyDaoImpl extends ObjectDaoImpl<UserAmountApply> implements UserAmountApplyDao  {

	@Override
	/**
	 * 根据用户Id查询所有的申请集合
	 * @param userId
	 * @return
	 */
	public List<UserAmountApply> getUserAmountApplyList(Long userId){
		String sql = " from UserAmountApply where user = ?1 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, new User(userId));
		return  query.getResultList();
	}
}
