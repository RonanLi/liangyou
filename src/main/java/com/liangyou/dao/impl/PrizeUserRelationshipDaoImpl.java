package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.PrizeUserRelationshipDao;
import com.liangyou.domain.PrizeUserRelationship;

@Repository(value = "prizeUserRelationshipDao")
public class PrizeUserRelationshipDaoImpl extends ObjectDaoImpl<PrizeUserRelationship> implements PrizeUserRelationshipDao {

//	@Override
//	public List<PrizeUserRelationship> findByPhoneAndReceiveState(String phone, int receiveState) {
//		System.out.println("phone：" + phone + ", receiveState：" + receiveState);
//		String jpql = "from PrizeUserRelationship where phone = ?1 and receiveState = ?2";
//		Query query = em.createQuery(jpql);
//		query.setParameter(1, phone);
//		query.setParameter(2, receiveState);
//		return (List<PrizeUserRelationship>) query.getResultList();
//	}
	
	@Override
	public PrizeUserRelationship findById(Long purId) {
		String jpql = "from PrizeUserRelationship where id = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, purId);
		
		List<PrizeUserRelationship> purList = query.getResultList();
		if (!purList.isEmpty()) {
			return purList.get(0);
		}
		return null;
	}

}
