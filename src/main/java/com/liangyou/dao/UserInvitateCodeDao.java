package com.liangyou.dao;


import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;


public interface UserInvitateCodeDao extends BaseDao<UserInvitateCode> {

	List<UserInvitateCode> getCodeListByPid(long userId);

	UserInvitateCode findByUserId(long userId);

	
	

}
