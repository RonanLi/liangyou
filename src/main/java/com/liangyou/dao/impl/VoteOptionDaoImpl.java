package com.liangyou.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.VoteOptionDao;
import com.liangyou.domain.VoteOption;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Repository
public class VoteOptionDaoImpl extends ObjectDaoImpl<VoteOption> implements VoteOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteOption> findVoteOptionByUserId(long id,long tid) {
		return em.createNativeQuery("select o.* from user u,vote_answer a,vote_option o where u.user_id=a.user_id and a.user_id=? and o.title_id=? and a.option_id=o.id group by o.id", VoteOption.class)
				.setParameter(1, id)
				.setParameter(2, tid)
				.getResultList();
	}
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end