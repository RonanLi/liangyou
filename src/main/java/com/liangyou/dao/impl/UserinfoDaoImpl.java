package com.liangyou.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserinfoDao;
import com.liangyou.domain.Userinfo;

@Repository
public class UserinfoDaoImpl  extends ObjectDaoImpl<Userinfo> implements UserinfoDao  {
    Logger logger = Logger.getLogger(UserinfoDaoImpl.class);

    
}
