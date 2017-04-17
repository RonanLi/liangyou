package com.liangyou.dao;

import com.liangyou.domain.StarRank;

public interface StarRankDao extends BaseDao<StarRank> {
	StarRank getStartRankByRank(int rank);

}
