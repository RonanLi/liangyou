package com.liangyou.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.VoteAnswerDao;
import com.liangyou.domain.User;
import com.liangyou.domain.VoteAnswer;
import com.liangyou.exception.BussinessException;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Repository
public class VoteAnswerDaoImpl extends ObjectDaoImpl<VoteAnswer> implements VoteAnswerDao {

	@Override
	public void clearByUserId(User sessionUser,long id) {
		try{
			em.createNativeQuery("delete from vote_answer where user_id=?1 and vote_id=?2")
				.setParameter(1, sessionUser.getUserId())
				.setParameter(2, id)
				.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			String message = "数据库删除数据出错！";
			logger.error(message);
			throw new BussinessException(message);
		}
	}

	@Override
	public boolean findAnswer(User user, String id) {
		@SuppressWarnings("unchecked")
		List<VoteAnswer> list = em.createNativeQuery("select * from vote_answer where user_id=?1 and vote_id=?2")
									.setParameter(1, user.getUserId())
									.setParameter(2, id)
									.getResultList();
		if(list.size()==0){
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoteAnswer> findVoteAnswerListByUserAndVote(long userId, long id) {
		
		return em.createNativeQuery("select * from vote_answer where user_id=?1 and vote_id=?2 group by title_id",VoteAnswer.class)
									.setParameter(1, userId)
									.setParameter(2, id)
									.getResultList();
	}
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end