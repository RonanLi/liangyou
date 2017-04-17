package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ExperienceMoneyDao;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.model.SearchParam;
import com.liangyou.service.ExperienceMoneyService;

/**
 * Created by Young on 2016/10/13.
 */

@Service(value = "experienceMoneyService")
@Transactional
public class ExperienceMoneyServiceImpl extends BaseServiceImpl implements ExperienceMoneyService {

	@Autowired
	private ExperienceMoneyDao experienceMoneyDao;

	@Override
	public ExperienceMoney getExperenceMoney(SearchParam param) {
		return experienceMoneyDao.findByCriteriaForUnique(param);
	}

	@Override
	public List<ExperienceMoney> findExperienceMoney(SearchParam param) {
		return experienceMoneyDao.findByCriteria(param);
	}

	@Override
	public boolean update(List<ExperienceMoney> emList) {
		return this.experienceMoneyDao.update(emList);
	}

	@Override
	public void save(ExperienceMoney experienceMoney) {
		this.experienceMoneyDao.save(experienceMoney);
	}
}
