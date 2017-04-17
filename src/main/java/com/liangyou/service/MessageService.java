package com.liangyou.service;

import com.liangyou.domain.Message;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 消息服务
 * 
 * @author 1432
 *
 */
public interface MessageService {

	public Message addMessage(Message msg);

	public PageDataList getMessageBySearchParam(SearchParam param);

	/**
	 * 未读站内信条数
	 */
	public int getUnreadMessageCount(User user);

	public Message getMessageById(int id);

	public void modifyMessge(Message message);

	// public void deleteReceiveMessage(int[] ids);
	public void deleteMessage(Integer[] ids);

	public void setReadMessage(Integer[] ids);

	public void setUnreadMessage(Integer[] ids);
}
