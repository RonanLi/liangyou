package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Message;

public interface MessageDao extends BaseDao<Message> {

	//public PageResult<Message> getReceiveMessgeList(PageResult<Message> page) ;
	
	//public PageResult<Message> getSentMessgeList(PageResult<Message> page) ;
	
	public List getMessgeByUserid(long sent_user,long receive_user,int start,int pernum) ;
	
	public Message getMessageById(long id);
	
	public Message addMessage(Message msg);
	
	public Message modifyMessage(Message msg);
	
	public void modifyBatchMessage(List<Message> list);
	
	public List getMessageList(Integer[] ids);
	
	public int getMessageCount(long user_id,int status);
	
}
