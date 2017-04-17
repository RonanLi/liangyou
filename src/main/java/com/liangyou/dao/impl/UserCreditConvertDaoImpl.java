package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserCreditConvertDao;
import com.liangyou.domain.GoodsCategory;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCreditConvert;

@Repository("userCreditConvertDao")
public class UserCreditConvertDaoImpl extends ObjectDaoImpl<UserCreditConvert> implements UserCreditConvertDao {
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	@Override
	public List<UserCreditConvert> getCreditConvertListByUser(User user,
			String type) {
		String sql = " from UserCreditConvert where user= ?1 and type = ?2 ";
		Query query = em.createQuery(sql).setParameter(1, user).setParameter(2, type);
		return (List<UserCreditConvert>)query.getResultList();
	}
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 

	@Override
	public void delUserCreditConvert(int id) {
		delete(id);
	}
}
