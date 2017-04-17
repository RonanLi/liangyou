package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserCreditDao;
import com.liangyou.domain.Credit;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;

@Repository
public class UserCreditDaoImpl extends ObjectDaoImpl<UserCredit> implements UserCreditDao {

	private static Logger logger = Logger.getLogger(UserCreditDaoImpl.class);
	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start
	@Override
	public UserCredit getUserCreditByUserId(User user){
		String sql = " from UserCredit  where user = ?1 ";
		Query query = em.createQuery(sql).setParameter(1, user);
		List<UserCredit> list=query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 end
}
