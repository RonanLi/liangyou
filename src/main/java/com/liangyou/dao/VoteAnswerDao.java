package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.VoteAnswer;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
public interface VoteAnswerDao extends BaseDao<VoteAnswer> {

	void clearByUserId(User sessionUser,long id);

	boolean findAnswer(User user, String id);

	List<VoteAnswer> findVoteAnswerListByUserAndVote(long userId, long id);
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end