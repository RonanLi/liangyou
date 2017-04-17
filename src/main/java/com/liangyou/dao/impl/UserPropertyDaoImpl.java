package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserPropertyDao;
import com.liangyou.domain.UserProperty;

/**
 * v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06 
 *
 */
@Repository(value = "userPropertyDao")
public class UserPropertyDaoImpl extends ObjectDaoImpl<UserProperty> implements UserPropertyDao {
	
}
