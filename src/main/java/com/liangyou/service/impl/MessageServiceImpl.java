package com.liangyou.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.MessageDao;
import com.liangyou.domain.Message;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.MessageService;

@Service
@Transactional
public class MessageServiceImpl extends BaseServiceImpl implements MessageService {
	
	private static Logger logger = Logger.getLogger(MessageServiceImpl.class);

	@Autowired
	private MessageDao messageDao;
	@Override
	public Message addMessage(Message msg) {
		messageDao.save(msg);
		return null;
	}
	
	@Override
	public PageDataList getMessageBySearchParam(SearchParam  param) {
		return messageDao.findPageList(param);
	}
	@Override
	public Message getMessageById(int id) {
		return messageDao.find(id);
	}
	
	@Override
	public void modifyMessge(Message message) {
		messageDao.update(message);
	}

//	@Override
//	public void deleteReceiveMessage(int[] ids) {		
//		List<Message> list=messageDao.getMessageList(ids);
//		for(Message msg:list){
//			msg.setDeltype(1);
//		}
//		messageDao.modifyBatchMessage(list);
//	}

	@Override
	public void deleteMessage(Integer[] ids) {
		List<Message> list=messageDao.getMessageList(ids);
		for(Message msg:list){
			msg.setSented(0);
		}
		messageDao.modifyBatchMessage(list);
	}

	@Override
	public void setReadMessage(Integer[] ids) {
		List<Message> list=messageDao.getMessageList(ids);
		for(Message msg:list){
			msg.setStatus(1);
		}
		messageDao.modifyBatchMessage(list);		
	}

	@Override
	public void setUnreadMessage(Integer[] ids) {
		List<Message> list=messageDao.getMessageList(ids);
		for(Message msg:list){
			msg.setStatus(0);
		}
		messageDao.modifyBatchMessage(list);
	}

	
	/**
	 * 未读站内信条数
	 */
	@Override
	public int getUnreadMessageCount(User user) {
		SearchParam param = SearchParam.getInstance();
		param.addParam("receiveUser", user);
		param.addParam("status", 0);
		param.addParam("sented", Operator.NOTEQ,0);
		return messageDao.countByCriteria(param);
	}
}
