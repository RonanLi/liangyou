package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.CreditRankDao;
import com.liangyou.domain.CreditRank;

@Repository(value="creditRankDao")
public class CreditRankDaoImpl extends ObjectDaoImpl<CreditRank> implements CreditRankDao {

	@Override
	public CreditRank getCreditRankByName(String name) {
		String jpql = "from CreditRank where name =?1";
		Query query = em.createQuery(jpql).setParameter(1, name);
		List<CreditRank> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			return null;
		}
	}
	

	
}
