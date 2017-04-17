package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserJoinedDao;
import com.liangyou.dao.impl.ObjectDaoImpl;
import com.liangyou.domain.UserJoined;

/**
 * 启道资本
 * @author wujing
 *TGPROJECT-359
 */
@Repository(value="userJoinedDao")
public class UserJoinedDaoImpl extends ObjectDaoImpl<UserJoined> implements
		UserJoinedDao {

	@Override
	public UserJoined getUserJoinedByUser(long userId) {
		String jqpl = "from UserJoined where joinUser.userId = ?1";
		Query query = em.createQuery(jqpl);
		query.setParameter(1, userId);
		List list = query.getResultList();
		if (list.size() >0) {
			return  (UserJoined)list.get(0);
		}else{
			return null;
		}
	
	}
}
