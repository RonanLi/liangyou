package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserInvitateCodeDao;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;

@Repository(value = "userInvitateCodeDao")
public class UserInvitateCodeDaoImpl extends ObjectDaoImpl<UserInvitateCode>
		implements UserInvitateCodeDao {

	@Override
	public List<UserInvitateCode> getCodeListByPid(long userId) {
		String jpql = "from UserInvitateCode where parentInvitateCodeId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userId);
		
		@SuppressWarnings("unchecked")
		List<UserInvitateCode> codeList = query.getResultList();
		
		return codeList;
	}

	//add by lj 2016/11/26
	@Override
	public UserInvitateCode findByUserId(long userId) {
		
		String jpql = "from UserInvitateCode where user.userId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userId);
		List list = query.getResultList();
		UserInvitateCode userInvitateCode=null; 
		if(!list.isEmpty()){
			userInvitateCode =(UserInvitateCode) list.get(0);
		}
		return userInvitateCode;
	}

	
	}


