package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AreaDao;
import com.liangyou.domain.Area;

@Repository("areaDao")
public class AreaDaoImpl extends ObjectDaoImpl<Area> implements AreaDao {

	@Override
	public List<Area> getListByPid(int pid) {
		String jpql = " from Area where pid = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, pid);
		List<Area> list = query.getResultList();
		return list;
	}
	
}
