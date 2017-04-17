package com.liangyou.dao;


import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.UserCreditConvert;

public interface UserCreditConvertDao extends BaseDao<UserCreditConvert> {
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	public List<UserCreditConvert> getCreditConvertListByUser(User user, String type);
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 

 	public void delUserCreditConvert(int id);
}
