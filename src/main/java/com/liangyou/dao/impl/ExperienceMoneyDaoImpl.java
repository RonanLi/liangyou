package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.ExperienceMoneyDao;
import com.liangyou.domain.ExperienceMoney;

@Repository(value = "experienceMoneyDao")
public class ExperienceMoneyDaoImpl extends ObjectDaoImpl<ExperienceMoney> implements ExperienceMoneyDao {

}
