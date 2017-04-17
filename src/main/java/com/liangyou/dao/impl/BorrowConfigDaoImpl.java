package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowConfigDao;
import com.liangyou.domain.BorrowConfig;

@Repository("borrowConfigDao")
public class BorrowConfigDaoImpl extends ObjectDaoImpl<BorrowConfig> implements BorrowConfigDao {
	
}
