package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AreaBankDao;
import com.liangyou.domain.AreaBank;

@Repository("areaBankDao")
public class AreaBankDaoImpl extends ObjectDaoImpl<AreaBank> implements AreaBankDao {
	@Override
	public List<AreaBank> getListByPid(String pid) {
		String jpql = " from AreaBank where pid = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, pid);
		List<AreaBank> list = query.getResultList();
		return list;
	}
}
