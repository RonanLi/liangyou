package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.StarRankDao;
import com.liangyou.domain.StarRank;

@Repository(value="starRankDao")
public class StarRankDaoImpl extends ObjectDaoImpl<StarRank> implements StarRankDao {

	@Override
	public StarRank getStartRankByRank(int rank) {
		String jpql = "from StarRank where rank =?1";
		Query query = em.createQuery(jpql).setParameter(1, rank);
		List<StarRank> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			return null;
		}
	}

}
