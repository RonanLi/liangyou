package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.StarLogDao;
import com.liangyou.domain.StarLog;

@Repository(value="starLogDao")
public class StarLogDaoImpl extends ObjectDaoImpl<StarLog> implements StarLogDao {

}
