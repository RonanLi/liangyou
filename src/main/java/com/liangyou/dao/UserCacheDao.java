package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.UserCache;
import com.liangyou.model.SearchParam;

public interface UserCacheDao extends BaseDao<UserCache> {

	public UserCache getUserCacheByUserid(long userid);
	public UserCache validUserVip(long userid);
	/**
	 * 根据页数,返回条数Vip状态换回List
	 * 
	 * @param page
	 * @param Max
	 * @param status
	 * @return
	 */
	public List getUserVipinfo(long page, int Max, int status,SearchParam p);
	
	/**
	 * 根据VIp状态返回总条数
	 */
	public int getUserVipinfo(int status,SearchParam p);
	//v1.8.0.4_u1   TGPROJECT-241 	qj   2014-05-06  start
	public List<UserCache> getVipUserList();
	//v1.8.0.4_u1   TGPROJECT-241 	qj   2014-05-06  end
}
