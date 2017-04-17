package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.PrizeUserRelationshipDao;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.service.PrizeUserRelationshipService;

@Service(value = "prizeUserRelationshipService")
@Transactional
public class PrizeUserRelationshipServiceImpl extends BaseServiceImpl implements PrizeUserRelationshipService {
	@Autowired
	private PrizeUserRelationshipDao prizeUserRelationshipDao;
	
	public void save(PrizeUserRelationship prizeUserRelationship) {
		prizeUserRelationshipDao.save(prizeUserRelationship);
	}

	@Override
	public void update(PrizeUserRelationship prizeUserRelationship) {
		prizeUserRelationshipDao.update(prizeUserRelationship);
	}
	
	@Override
	public List<PrizeUserRelationship> findByParam(SearchParam param) {
		return (List<PrizeUserRelationship>) prizeUserRelationshipDao.findByCriteria(param);
	}
	
	@Override
	public PrizeUserRelationship findById(Long purId) {
		return prizeUserRelationshipDao.findById(purId);
	}

	@Override
	public List<PrizeUserRelationship> findByUser(SearchParam param) {
		return (List<PrizeUserRelationship>) prizeUserRelationshipDao.findByCriteriaForUnique(param);
	}
}
