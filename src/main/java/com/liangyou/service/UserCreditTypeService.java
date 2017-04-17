package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.UserCreditType;

/**
 * 用户积分类型服务
 * 
 * @author 1432
 *
 */
public interface UserCreditTypeService {

	public List<UserCreditType> findAll();

	public UserCreditType find(int id);

	public void update(UserCreditType uct);
}
