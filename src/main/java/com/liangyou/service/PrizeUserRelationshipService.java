package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;

/**
 * 获奖用户管理
 * 
 * @author 1432
 *
 */
public interface PrizeUserRelationshipService {

	public void save(PrizeUserRelationship prizeUserRelationship);

	public void update(PrizeUserRelationship prizeUserRelationship);

	public List<PrizeUserRelationship> findByParam(SearchParam param);

	public PrizeUserRelationship findById(Long purId);

	public List<PrizeUserRelationship> findByUser(SearchParam param);

}
