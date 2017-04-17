package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Vote;
//v1.8.0.4_u1 TGPROJECT-270 zf 2014-5-8 start
public interface VoteDao extends BaseDao<Vote> {

	List<Vote> findVoteByUserId(long id);
}
//v1.8.0.4_u1 TGPROJECT-270 zf 2014-5-8 end