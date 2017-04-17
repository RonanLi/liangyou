package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserJoinedDao;
import com.liangyou.domain.User;
import com.liangyou.domain.UserJoined;
import com.liangyou.domain.UserType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserJoinedService;

/**
 * 启道资本
 * @author wujing
 *TGPROJECT-359
 */
@Service
@Transactional
public class UserJoinedServiceImpl implements UserJoinedService {
	
	@Autowired
	private UserJoinedDao userJoinedDao;
	@Autowired
	private UserDao userDao;
	

	@Override
	public List<UserJoined> getUserJoinedList(SearchParam param) {
		
		return userJoinedDao.findByCriteria(param);
	}

	@Override
	public void addUserJoined(UserJoined userJoined) {
		userJoinedDao.save(userJoined);
	}

	@Override
	public UserJoined getUserJoinedByUser(long userId) {
	
		return userJoinedDao.getUserJoinedByUser(userId);
	}

	@Override
	public PageDataList<UserJoined> getPageJoinList(SearchParam param) {
		return userJoinedDao.findPageList(param);
		
	}

	@Override
	public UserJoined getUserJoinedById(long id) {
		return userJoinedDao.find(id);
	}

	@Override
	public void doVerifyJoined(UserJoined joined) {
		if (joined.getStatus() ==1) {
			joined.getJoinUser().setUserType(new UserType(28));
		}
		joined.setVerifyTime(new Date());
		userJoinedDao.update(joined);
		
	}
	
	

}
