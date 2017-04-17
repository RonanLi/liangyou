package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.CreditDao;
import com.liangyou.domain.Credit;
import com.liangyou.domain.User;

@Repository(value="creditDao")
public class CreditDaoImpl extends ObjectDaoImpl<Credit> implements CreditDao {
	
	/**
	 * 获取积分对象
	 * @param user
	 * @return
	 */
	@Override
	public Credit getCreditByUser(User user){
		String sql = " from Credit  where user = ?1 ";
		Query query = em.createQuery(sql).setParameter(1, user);
		List<Credit> list=query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
