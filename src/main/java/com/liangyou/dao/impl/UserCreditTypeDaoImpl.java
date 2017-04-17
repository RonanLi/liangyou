package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserCreditTypeDao;
import com.liangyou.domain.UserCreditType;

@Repository("userCreditTypeDao")
public class UserCreditTypeDaoImpl extends ObjectDaoImpl<UserCreditType> implements UserCreditTypeDao {

	private static Logger logger = Logger.getLogger(UserCreditTypeDaoImpl.class);
	
	@Override
	public UserCreditType getUserCreditType(String type){
		String sql = " from UserCreditType where nid = ?1 ";
	    Query query = em.createQuery(sql)
	    		.setParameter(1, type);
		List<UserCreditType> list = query.getResultList();
		if( list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	
}
