package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AreaMmmDao;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.AreaMmm;

@Repository("areaMmmDao")
public class AreaMmmDaoImpl extends ObjectDaoImpl<AreaMmm> implements AreaMmmDao {
	@Override
	public List<AreaMmm> getListByPid(String pid) {
		String jpql = " from AreaMmm where pid = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, pid);
		List<AreaMmm> list = query.getResultList();
		return list;
	}
}
