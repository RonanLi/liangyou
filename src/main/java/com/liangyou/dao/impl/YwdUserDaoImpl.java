package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.YwdUserDao;
import com.liangyou.domain.YwdUser;

@Repository(value="ywdUserDao")
public class YwdUserDaoImpl  extends ObjectDaoImpl<YwdUser> implements YwdUserDao  {
	@Override
	public YwdUser getYwdUserByUsernameAndPassword(String username,String password){
			String jpql = "from YwdUser where username = ?1 and password = ?2 and islogin=0";
			Query query = em.createQuery(jpql);
			query.setParameter(1, username);
			query.setParameter(2, password);
			List list = query.getResultList();
			if (list != null && list.size() >= 1) {
				return (YwdUser) list.get(0);
			} else {
				return null;
			}
	}
}
