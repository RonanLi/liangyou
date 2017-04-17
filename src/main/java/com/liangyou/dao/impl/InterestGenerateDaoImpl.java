package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.InterestGenerateDao;
import com.liangyou.domain.InterestGenerate;

/**
 * 生利宝接口 TGPROJECT-314 qj 2014-05-30 add
 *
 */
@Repository(value="interestGenerateDao")
public class InterestGenerateDaoImpl  extends ObjectDaoImpl<InterestGenerate> implements InterestGenerateDao  {
}
