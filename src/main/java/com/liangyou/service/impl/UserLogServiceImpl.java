package com.liangyou.service.impl;

import org.springframework.stereotype.Service;

import com.liangyou.dao.UserLogDao;
import com.liangyou.service.UserLogService;
@Service
public class UserLogServiceImpl implements UserLogService {

	private UserLogDao userLogDao;
	
	public UserLogDao getUserLogDao() {
		return userLogDao;
	}

	public void setUserLogDao(UserLogDao userLogDao) {
		this.userLogDao = userLogDao;
	}

}
