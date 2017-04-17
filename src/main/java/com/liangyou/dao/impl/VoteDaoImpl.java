package com.liangyou.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.VoteDao;
import com.liangyou.domain.Vote;
//v1.8.0.4_u1 TGPROJECT-270 zf 2014-5-8 start
@Repository
public class VoteDaoImpl extends ObjectDaoImpl<Vote> implements VoteDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Vote> findVoteByUserId(long id) {
		return em.createNativeQuery("select v.* from user u,vote_answer a,vote v where u.user_id=a.user_id and a.user_id=? and a.vote_id=v.id group by v.id;", Vote.class)
			.setParameter(1, id)
			.getResultList();
	}
}
//v1.8.0.4_u1 TGPROJECT-270 zf 2014-5-8 end