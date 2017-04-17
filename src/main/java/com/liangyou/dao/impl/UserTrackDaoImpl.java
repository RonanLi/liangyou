package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserTrackDao;
import com.liangyou.domain.Usertrack;

@Repository
public class UserTrackDaoImpl extends ObjectDaoImpl<Usertrack> implements UserTrackDao {

	private static Logger logger = Logger.getLogger(UserTrackDaoImpl.class);  
	
	public void addUserTrack(Usertrack t) {
		try{
			save(t);
		}catch(DataAccessException e){
			logger.error(e);
		}
	}

	public Usertrack getLastUserTrack(long userid) {
		String jpql = "from Usertrack where userId = ?1 order by loginTime desc";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userid);
		query.setMaxResults(1);
		Usertrack usertrack = null;
		List list = null;
		try{
			list  = query.getResultList();
			if(list.size()>=1){
				usertrack = (Usertrack)list.get(0);
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			return null;
		}
		return usertrack;
	}

	@Override
	public int getUserTrackCount(long userid) {
		String jpql = "from Usertrack  where userId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, userid);
		int i = 0;
		try{
			i = query.getResultList().size();
		}catch(Exception e){
			logger.error(e.getMessage());
			return 0;
		}
		return i;
	}
	
	
	
}
