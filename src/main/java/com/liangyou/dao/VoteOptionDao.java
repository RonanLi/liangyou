package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.VoteOption;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
public interface VoteOptionDao extends BaseDao<VoteOption> {

	List<VoteOption> findVoteOptionByUserId(long userId,long tid);
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end