package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserTypeDao;
import com.liangyou.domain.UserType;

@Repository
public class UserTypeDaoImpl  extends ObjectDaoImpl<UserType> implements UserTypeDao  {
    Logger logger = Logger.getLogger(UserTypeDaoImpl.class);

    @Override
	public List getAllUserType() {
    	String sql ="from UserType order by sort asc";
    	Query query = em.createQuery(sql);
    	List list = query.getResultList();
    	return list;
	}
    
    public List getUserTypepurviewsByUserTypeId(long user_type_id) {
    	String sql ="from UserTypepurview where user_type_id=?1";
    	Query query = em.createQuery(sql);
    	query.setParameter(1, user_type_id);
    	List list = query.getResultList();
    	return list;
    }
}
