package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Friend;

public interface FriendDao extends BaseDao<Friend> {

	List getFriendList(long user_id);

	int getFriendNumber(long user_id);

	void upFriend(Friend friend);

	void upBlackFriend(long user_id, String username, String ip);

	void deleteFriend(long user_id, String username);

	List<Friend> readFriend(long user_id);

	List getBlackFriendList(long user_id);

	void readdFriend(long userId, String username);

	void addfriend(String id, String friend, String content);

	List<Friend> selFriendRequest(long user_id, String username);

	List<Friend> selFriend(long user_id, String friendname);

	List selFriend(String uid);
	
}
