package com.liangyou.quartz.notice;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.model.MsgReq;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.MsgRecordService;
/**
 * 站内信、短信、邮件通知业务处理
 * @author zxc
 *
 */
public class NoticeJobQueue<T> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public NoticeJobQueue(LoanTask task) {
		super();
		this.task = task;
	}

	public synchronized void offer(T model) {
		if (!queue.contains(model)) {
			queue.offer(model);
			synchronized (task.getLock()) {
				task.execute();
			}
		}
	}

	public synchronized void offer(List<T> ts) {
		for (int i = 0; i < ts.size(); i++) {
			T t = ts.get(i);
			if (!queue.contains(t)) {
				queue.offer(t);
			}
		}
		synchronized (task.getLock()) {
			task.execute();
		}
	}

	public synchronized T poll() {
		return queue.poll();
	}

	public synchronized T peek() {
		return queue.peek();
	}

	public synchronized void remove(T model) {
		queue.remove(model);
	}

	public int size() {
		return queue.size();
	}

	public static NoticeJobQueue<MsgReq> MSGREQ = null;

	public static void init(MsgRecordService msgRecordService) {
		MSGREQ = new NoticeJobQueue<MsgReq>(new NoticeTask(msgRecordService));
	}

	public static void stop() {
		MSGREQ.task.stop();
	}

	public void doAutoQueue() {
	}

}
