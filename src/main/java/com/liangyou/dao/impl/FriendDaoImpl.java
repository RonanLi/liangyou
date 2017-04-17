package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.FriendDao;
import com.liangyou.domain.Friend;
import com.liangyou.domain.User;


@Repository
public class FriendDaoImpl extends ObjectDaoImpl<Friend> implements FriendDao {
	
	private static Logger logger = Logger.getLogger(FriendDaoImpl.class);

	@Override
	public List<Friend> getFriendList(long user_id) {
		String jpql = " from Friend where user = ? and status=1";
//		String sql = " select f.user,f.friendsUser,u.username,f.type,f.status,f.content,f.addtime,f.addip "+
//		               "from Friend f join User u on  u.user_id=f.friends_userid where f.user = ?";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
	    List<Friend>  list = query.getResultList();
		return list;
	}

	@Override
	public int getFriendNumber(long user_id) {
		String jpql = "from Friend  where user = ? and status = 1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
		int i = 0;
		try{
			i = query.getResultList().size();
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			return 0;
		}
		return i;
	}

	@Override
	public void upFriend(Friend friend) {
		
		
	}

	@Override
	public void upBlackFriend(long user_id, String username, String ip) {
		String jpql = "update Friend set status=4 where user = ? and friends_username='"+username+"'";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
		query.executeUpdate();
	}

	@Override
	public void deleteFriend(long user_id, String username) {
		String jpql = "delete Friend where user = ? and friends_username='"+username+"'";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
		query.executeUpdate();
		
	}

	@Override
	public List<Friend> readFriend(long user_id) {
		String jpql = " from Friend where user = ? and status=3";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
	    List<Friend>  list = query.getResultList();
		return list;
		
		
	}

	@Override
	public List<Friend> getBlackFriendList(long user_id) {
		String jpql = "from Friend  where user = ? and status = 4";
		Query query = em.createQuery(jpql);
		
		query.setParameter(1, new User(user_id));
	    List<Friend>  list = query.getResultList();
		return list;
	}

	@Override
	public void readdFriend(long userId, String username) {
		String jpql = "update Friend set status=1 where user = ? and friends_username='"+username+"'";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(userId));
		query.executeUpdate();
		
	}

	@Override
	public void addfriend(String id, String friend, String content) {//添加好友
		String jpql = "update Friend set content='"+content+"',type="+friend+",status=1 where id = "+id+"";
		Query query = em.createQuery(jpql);
//		query.setParameter(1, content);
//		query.setParameter(2,Integer.parseInt(friend));
//		query.setParameter(3, Integer.parseInt(id));
		query.executeUpdate();
		
	}

	@Override
	public List<Friend> selFriendRequest(long user_id, String username) {
		String jpql = "from Friend  where user = ? and friends_username='"+username+"'";
		Query query = em.createQuery(jpql);
		
		query.setParameter(1, new User(user_id));
	    List<Friend>  list = query.getResultList();
		return list;
		
	}

	@Override
	public List<Friend> selFriend(long user_id, String friendname) {
		String jpql = " from Friend where user = ? and status=1 and friends_username like  '%"+friendname+"%'";

		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(user_id));
	    List<Friend>  list = query.getResultList();
		return list;
		
	}

	@Override
	public List selFriend(String uid) {
		
		String jpql = "from User  where inviteUserid = ? ";
		Query query = em.createQuery(jpql);		
		query.setParameter(1, uid);
	    List  list = query.getResultList();
		return list;
		//return null;
	}
	
	
}
