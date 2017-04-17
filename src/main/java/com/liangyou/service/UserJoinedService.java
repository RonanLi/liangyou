package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.UserJoined;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 启道资本
 * @author 武警
 *TGPROJECT-359
 */
public interface UserJoinedService {
	
	/**
	 * 获取全部加盟用户，不带分页
	 * @param param
	 * @return
	 */
	public List<UserJoined>  getUserJoinedList(SearchParam param);
	
	/**
	 * 保存用户加盟
	 * @param userJoined
	 */
	public void addUserJoined(UserJoined userJoined);
	
	/**
	 * 根据用户id查询申请记录
	 * @param userId
	 * @return
	 */
	public UserJoined getUserJoinedByUser(long userId);
	
	public PageDataList<UserJoined> getPageJoinList(SearchParam param);
	
	public UserJoined getUserJoinedById(long id);
	
	public void doVerifyJoined(UserJoined joined);

}
