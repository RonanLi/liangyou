package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.ExperienceMoney;
import com.liangyou.model.SearchParam;

/**
 * Created by Young on 2016/10/13.
 */
public interface ExperienceMoneyService {

	ExperienceMoney getExperenceMoney(SearchParam param);

	List<ExperienceMoney> findExperienceMoney(SearchParam param);

	boolean update(List<ExperienceMoney> emList);

	void save(ExperienceMoney experienceMoney);
}
