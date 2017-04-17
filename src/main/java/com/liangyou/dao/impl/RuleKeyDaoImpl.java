package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.RuleKeyDao;
import com.liangyou.domain.RuleKey;
@Repository(value="ruleKeyDao")
public class RuleKeyDaoImpl extends ObjectDaoImpl<RuleKey> implements
		RuleKeyDao {

}
