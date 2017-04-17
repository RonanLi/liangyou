package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.UserCreditTypeDao;
import com.liangyou.domain.UserCreditType;
import com.liangyou.service.UserCreditTypeService;

@Service(value="userCreditTypeService")
@Transactional
public class UserCreditTypeServiceImpl implements UserCreditTypeService {
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 start
	@Autowired
	private UserCreditTypeDao dao;
	
	public List<UserCreditType> findAll(){
		return (List<UserCreditType>) dao.findAll();
	}
	public UserCreditType find(int id){
		return dao.find(id);
	}
	public void update(UserCreditType uct){
		dao.merge(uct);
	}
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end
}
