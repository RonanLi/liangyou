package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.model.SearchParam;

/**
 * add by lixiaomin on 2016/11/14.
 */
public interface UserInvitateCodeService {

	UserInvitateCode getInvitateCode(SearchParam param);

	void save(UserInvitateCode userInvitateCode);

	List<UserInvitateCode> getCodeListByPid(long userId);
	/**add by lijing 
	 * @param userId
	 * @return
	 */
	public UserInvitateCode findByUserId(long userId);

	//add by lj 2016/11/26
	UserInvitateCode findEntityByUserId(long userId);
}
