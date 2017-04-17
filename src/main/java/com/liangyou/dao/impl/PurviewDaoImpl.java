package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.PurviewDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserTypepurviewDao;
import com.liangyou.domain.Purview;
import com.liangyou.domain.PurviewModel;
import com.liangyou.domain.UserTypepurview;

@Repository
public class PurviewDaoImpl extends ObjectDaoImpl<Purview> implements
		PurviewDao {
	private Logger logger = Logger.getLogger(PurviewDaoImpl.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserTypepurviewDao userTypepurviewDao;

	@Override
	public List getPurviewByPid(int pid) {
		List list = new ArrayList();
		String sql = "from Purview where pid= ?1";
		try {
			Query query = em.createQuery(sql);
			query.setParameter(1, pid);
			list = query.getResultList();
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	@Override
	public List<Purview> getPurviewByUserid(long user_id) {
		String sql = "SELECT p from Purview p, UserTypepurview up,UserType ut,User u WHERE up.purview = p.id and ut.typeId = up.userType and u.userType = ut.typeId and user_id = ?1";
		Query query = em.createQuery(sql);
		query.setParameter(1, user_id);
		List<Purview> list = query.getResultList();
		return list;
	}

	@Override
	public List getAllPurview() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getAllCheckedPurview(long user_typeid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPurview(Purview p) {
		// TODO Auto-generated method stub

	}

	@Override
	public Purview getPurview(long id) {
		String sql = "from Purview where id=?1";
		Purview p = null;
		try {
			Query query = em.createQuery(sql);
			query.setParameter(1, id);
			p = (Purview) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e);
		}
		return p;
	}

	@Override
	public void delPurview(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRoleHasPurview(long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void modifyPurview(Purview p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUserTypePurviews(List<Integer> purviewid, long user_type_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delUserTypePurviews(long user_type_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Purview> getPurviewsByLevel(int level) {
		// TODO Auto-generated method stub
		String sql = "from Purview where level=?1";
		Query query = em.createQuery(sql);
		query.setParameter(1, level);
		return query.getResultList();
	}
	@Override
	public List<Purview> getPurviewByAndLevel(int level, long pid, long userId) {
		String sql = "SELECT p from Purview p, UserTypepurview up,UserType ut,User u WHERE up.purview = p.id and ut.typeId = up.userType and u.userType = ut.typeId and p.level = ?1 and pid = ?2 and user_id = ?3";
		Query query = em.createQuery(sql);
		query.setParameter(1, level);
		query.setParameter(2, pid);
		query.setParameter(3, userId);
		List<Purview> list = query.getResultList();
		return list;
	}

}