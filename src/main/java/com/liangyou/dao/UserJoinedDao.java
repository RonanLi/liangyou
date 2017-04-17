package com.liangyou.dao;

import com.liangyou.domain.UserJoined;

/**
 * 启道资本用户加盟
 * @author 武警
 *TGPROJECT-359
 */
public interface UserJoinedDao extends BaseDao<UserJoined> {
	
	/**
	 * 根据用户id查询申请记录
	 * @param userId
	 * @return
	 */
	public UserJoined getUserJoinedByUser(long userId);

}
