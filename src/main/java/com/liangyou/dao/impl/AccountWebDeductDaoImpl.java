package com.liangyou.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.AccountWebDeductDao;
import com.liangyou.domain.AccountWebDeduct;
@Repository
public class AccountWebDeductDaoImpl extends ObjectDaoImpl<AccountWebDeduct> implements AccountWebDeductDao {
	private Logger logger =Logger.getLogger(AccountWebDeductDaoImpl.class);


}
