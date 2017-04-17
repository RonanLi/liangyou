package com.liangyou.quartz.tender;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.BorrowService;
/**
 * 投标、自动投标业务处理
 * @author Administrator
 */
public class TenderJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public TenderJobQueue(LoanTask task) {
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

	public static TenderJobQueue<TenderBean> TENDER = null;

	public static void init(BorrowService borrowService) {
		TENDER = new TenderJobQueue<TenderBean>(new TenderTask(borrowService));
	}

	public static void stop() {
		TENDER.task.stop();
	}

	public void doAutoQueue() {
	}

}
