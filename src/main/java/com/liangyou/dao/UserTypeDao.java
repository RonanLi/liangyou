package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.UserType;

public interface UserTypeDao extends BaseDao<UserType> {
	
	public List getAllUserType();
	public List getUserTypepurviewsByUserTypeId(long user_type_id);
}
