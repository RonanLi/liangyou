package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserCacheDao;
import com.liangyou.domain.UserCache;
import com.liangyou.model.SearchParam;

@Repository(value="userCacheDao")
public class UserCacheDaoImpl extends ObjectDaoImpl<UserCache> implements UserCacheDao {

	private static Logger logger = Logger.getLogger(UserCacheDaoImpl.class);

	@Override
	public List getUserVipinfo(long page, int Max, int status, SearchParam p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUserVipinfo(int status, SearchParam p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserCache getUserCacheByUserid(long userid) {
		String jpql = "from UserCache where user.userId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userid);
		UserCache userCache = null;
		try{
			userCache = (UserCache)query.getSingleResult();
		}catch(Exception e){
			logger.error(e.getMessage());
			return null;
		}
		return userCache;
	}

	@Override
	public UserCache validUserVip(long userid) {
		// TODO Auto-generated method stub
		return null;
	}
	//v1.8.0.4_u1   TGPROJECT-241 	qj   2014-05-06  start
	@Override
	public List<UserCache> getVipUserList() {
		String jpql = "from UserCache where vipStatus = 1 ";
		Query query = em.createQuery(jpql);
		return (List<UserCache>)query.getResultList();
	}
	//v1.8.0.4_u1   TGPROJECT-241 	qj   2014-05-06  end


}
