package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.PrizeDetail;

public interface PrizeDetailDao extends BaseDao<PrizeDetail> {

	public List<PrizeDetail> findByPrizeIdAndStatus(int prizeId, int status	);
}
