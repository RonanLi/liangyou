package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.PrizeUserRelationship;

public interface PrizeUserRelationshipDao extends BaseDao<PrizeUserRelationship> {

	
	public PrizeUserRelationship findById(Long purId); 
}
