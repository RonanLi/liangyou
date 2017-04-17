package com.liangyou.quartz.notice;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.model.MsgReq;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.MsgRecordService;
/**
 * 站内信、短信、邮件通知业务处理
 * @author zxc
 *
 */
public class NoticeTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(NoticeTask.class);

	@Resource
	private MsgRecordService msgRecordService;

	public MsgRecordService getMsgRecordService() {
		return msgRecordService;
	}

	public void setMsgRecordService(MsgRecordService msgRecordService) {
		this.msgRecordService = msgRecordService;
	}

	public NoticeTask(MsgRecordService msgRecordService) {
		super();
		task.setName("Notice.Task");
		this.msgRecordService = msgRecordService;
	}

	@Override
	public void doLoan() {
		while (NoticeJobQueue.MSGREQ!=null && NoticeJobQueue.MSGREQ.size() > 0) {
			MsgReq msgReq = NoticeJobQueue.MSGREQ.peek();//从队列中取出信息，peek()返回队列头部的元素，如果队列为空，则返回null
			logger.info("--------------------msgReq"+msgReq);
			if (msgReq != null) {
				try {
					msgRecordService.send(msgReq);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				} finally {
					NoticeJobQueue.MSGREQ.remove(msgReq);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return NoticeTask.MESSAGE_STATUS;
	}

}
