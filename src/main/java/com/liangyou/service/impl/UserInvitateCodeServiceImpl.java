package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.UserInvitateCodeDao;
import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserInvitateCodeService;

/**
 * add by lixiaomin on 2016/11/14.
 */

@Service(value = "userInvitateCodeService")
@Transactional
public class UserInvitateCodeServiceImpl extends BaseServiceImpl  implements UserInvitateCodeService{

	
	@Autowired
    private UserInvitateCodeDao userInvitateCodeDao;
	
	@Override
	public UserInvitateCode getInvitateCode(SearchParam param) {
		return userInvitateCodeDao.findByCriteriaForUnique(param);
	}

	//modify by lj 20161126
	@Override
	public void save(UserInvitateCode userInvitateCode) {
		this.userInvitateCodeDao.merge(userInvitateCode);
	}

	@Override
	public List<UserInvitateCode> getCodeListByPid(long userId) {
		return userInvitateCodeDao.getCodeListByPid(userId);
	}
	@Override
	public UserInvitateCode findByUserId(long userId){
		return userInvitateCodeDao.findByUserId(userId);
	}

	//add by lj 20161126
	@Override
	public UserInvitateCode findEntityByUserId(long userId) {
		// TODO Auto-generated method stub
		return userInvitateCodeDao.findByUserId(userId);
	} 
	
}
