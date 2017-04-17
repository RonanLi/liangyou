package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AttestationDao;
import com.liangyou.domain.Attestation;
import com.liangyou.domain.AttestationType;
import com.liangyou.domain.User;

@Repository(value="attestationDao")
public class AttestationDaoImpl extends ObjectDaoImpl<Attestation> implements AttestationDao {

	public List<Attestation> getAttestations(long userid){
		
		String jpql = " from Attestation where user = ?1 and status = 1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(userid));
		List list = query.getResultList();
		return list;
		
		
	}

	@Override
	public int getAttestations(long userid, int typeid) {
		
		String jpql = "from Attestation where user = ?1 and attestationType =?2 and status = 1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(userid));
		query.setParameter(2, new AttestationType(typeid));
		List list = query.getResultList();
		return list.size();
	}
	
	// v1.0.8.4 TGPROJECT-58 lx 2014-04-14 start
	public Attestation getAttestationByUserIdAndId(long userid,int attestationId){
		String jpql = "from Attestation where user = ?1 and id =?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(userid));
		query.setParameter(2, attestationId);
		Attestation attestation = (Attestation) query.getSingleResult();
		return attestation;
	}
	// v1.0.8.4 TGPROJECT-58 lx 2014-04-14 end
}
