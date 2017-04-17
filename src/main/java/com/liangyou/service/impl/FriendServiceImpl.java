package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.FriendDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Friend;
import com.liangyou.service.FriendService;

/**
 * 
 * @author fuxingxing
 * @date 2012-7-5-下午1:59:06
 * @version
 * 
 * <b>Copyright (c)</b> 2012-51融都-版权所有<br/>
 * 
 */
@Service(value="friendService")
@Transactional
public class FriendServiceImpl extends BaseServiceImpl implements FriendService {
	@Autowired
	private FriendDao friendDao;
	@Autowired
	private UserDao userDao;
	@Override
	public List<Friend> getFriendsRequest(long user_id) {//得到好友列表（请求）
		List<Friend> list=new ArrayList();
		System.out.println("ssssssssssssssssssssOJOK"+user_id);
	    list=friendDao.getFriendList(user_id);
	   // friendDao.findAll();
	  
		return list;
	}
	@Override
	public int countFriendsRequest(long user_id) {//好友数量（请求）
		int number=friendDao.getFriendNumber(user_id);
		return number;
	}
	@Override
	public List<Friend> getFriendsByUserId(long user_id) {//得到好友ID列表
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int countFriendsByUserId(long user_id) {//好友ID数量
		// TODO Auto-generated method stub
		return 0;
	}
	
	
		
	@Override
	public List<Friend> selFriendRequest(long user_id,String username) {
		List<Friend> list=friendDao.selFriendRequest(user_id,username);
		return list;
		
	}
	@Override
	public void addBlackFriend(long user_id, String username, String ip) {//黑名单
		friendDao.upBlackFriend(user_id,username,ip);
		
	}
	@Override
	public void delFriend(long user_id, String username) {//删除好友
		friendDao.deleteFriend(user_id,username);
		
	}
	@Override
	public List<Friend> readdFriend(long user_id) {//读取好友
		List<Friend> list=friendDao.readFriend(user_id);
		return list;
	}
	@Override
	public List<Friend> getBlackList(long user_id) {//黑名单列表
		List<Friend> list=friendDao.getBlackFriendList(user_id);
		return list;
	}
	@Override
	public List<Map<String, Object>> getMyFriends(String userid) {//
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void readdFriend(long userId, String username) {//重新加为好友
		friendDao.readdFriend(userId,username);
		
	}
	@Override
	public void addfriend(String id, String friend, String content) {//添加好友（请求）
		friendDao.addfriend(id, friend,content);
		
	}
	@Override
	public void addFriendRequest(Friend friend) {
		
		
	}
	@Override
	public List<Friend> selFriend(long user_id, String friendname) {
		List<Friend> list=friendDao.selFriend(user_id,friendname);
		return list;
	}
	@Override
	public List seltFriend(String uid) {
		List list =friendDao.selFriend(uid);
		return list;
	}
	
	

}
