package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.PrizeDetailDao;
import com.liangyou.domain.PrizeDetail;

@Repository(value = "prizeDetailDao")
public class PrizeDetailDaoImpl extends ObjectDaoImpl<PrizeDetail> implements PrizeDetailDao {

	@Override
	public List<PrizeDetail> findByPrizeIdAndStatus(int prizeId, int status) {
		String jpql = "from PrizeDetail pd where pd.prize.prizeId = ?1 and status = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, prizeId);
		query.setParameter(2, status);
		return query.getResultList();
	}

}
