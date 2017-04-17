package com.liangyou.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.MessageDao;
import com.liangyou.domain.Message;

/**
 * 消息管理dao类
 * @author zlm
 */
@Repository
public class MessageDaoImpl  extends ObjectDaoImpl<Message> implements MessageDao {
	/**
	 * 获取已收消息列表
	 * @param page PageResult
	 *//*
	@Override
	public PageResult<Message> getReceiveMessgeList(PageResult<Message> page) {
		return null;
	}
	
	
	*//**
	 * 获取已发消息列表
	 * @param page PageResult
	 *//*
	@Override
	public PageResult<Message> getSentMessgeList(PageResult<Message> page) {
		return null;
	}
*/
	@Override
	public Message getMessageById(long id) {
		return this.find(Message.class, id);
	}

	@Override
	public Message addMessage(Message msg) {
		save(msg);
		return msg;
	}

	@Override
	public Message modifyMessage(Message msg) {
		update(msg);
		return msg;
	}

	@Override
	public void modifyBatchMessage(List<Message> list) {
		update(list);
	}

	@Override
	public List getMessageList(Integer[] ids) {
		String jpql = "from Message where id in :ids";
		Query query = em.createQuery(jpql);
		query.setParameter("ids", Arrays.asList(ids));
		return query.getResultList();
	}

	@Override
	public int getMessageCount(long user_id, int status) {
		String jpql = "select count(1) from Message where deltype=0 and receiveUser.userId = ?1 and status = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, user_id);
		query.setParameter(2, status);
		return (Integer)query.getSingleResult();
	}


	@Override
	public List getMessgeByUserid(long sent_user, long receive_user, int start, int pernum) {
		String jpql = "from Message left join fetch receiveUser left join fetch sentUser where receiveUser.userId = ?1" +
				" and sentUser = ?2 order by addtime desc";
		Query query = em.createQuery(jpql);
		query.setParameter(1, sent_user);
		query.setParameter(2, receive_user);
		query.setFirstResult(start);
		query.setMaxResults(pernum);
 
		return query.getResultList();
	}

}
