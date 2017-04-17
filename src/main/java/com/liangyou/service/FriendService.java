package com.liangyou.service;

import java.util.List;
import java.util.Map;

import com.liangyou.domain.Friend;

/**
 * 好友服务
 * 
 * @author 1432
 *
 */
public interface FriendService {
	/**
	 * 获取好友请求的列表
	 * 
	 * @param user_id
	 * @return
	 */
	public List<Friend> getFriendsRequest(long user_id);

	/**
	 * 统计好友请求个数
	 * 
	 * @param user_id
	 * @return
	 */
	public int countFriendsRequest(long user_id);

	/**
	 * 根据user_id获取好友列表
	 * 
	 * @param user_id
	 * @return
	 */
	public List<Friend> getFriendsByUserId(long user_id);

	/**
	 * 获取用户的好友的个数
	 * 
	 * @param user_id
	 * @return
	 */
	public int countFriendsByUserId(long user_id);

	/**
	 * 新增好友
	 * 
	 * @param friend
	 */
	public List<Friend> selFriend(long user_id, String friendname);

	/**
	 * 新增用户请求
	 * 
	 * @param friend
	 */
	public void addFriendRequest(Friend friend);

	/**
	 * 新增好友黑名单
	 * 
	 * @param user_id
	 * @param username
	 */
	public void addBlackFriend(long user_id, String username, String ip);

	/**
	 * 删除好友
	 * 
	 * @param user_id
	 * @param username
	 */
	public void delFriend(long user_id, String username);

	/**
	 * 重新新增好友
	 * 
	 * @param user_id
	 * @param username
	 * @return
	 */
	public List<Friend> readdFriend(long user_id);

	public List getBlackList(long userid);

	/**
	 * 查询好友id
	 * 
	 * @param borrow
	 * @return
	 */
	public List<Map<String, Object>> getMyFriends(String userid);

	/**
	 * 重新加为好友
	 * 
	 * @param userId
	 * @param username
	 * @return
	 */
	public void readdFriend(long userId, String username);

	public void addfriend(String id, String friend, String content);

	/**
	 * 查询用户的好友
	 * 
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<Friend> selFriendRequest(long userId, String username);

	/**
	 * 根据好友id查询好友
	 * 
	 * @param uid
	 * @return
	 */
	public List seltFriend(String uid);

}
