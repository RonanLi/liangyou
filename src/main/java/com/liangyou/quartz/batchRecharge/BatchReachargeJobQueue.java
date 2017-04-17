package com.liangyou.quartz.batchRecharge;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.AutoService;
import com.liangyou.service.UserService;

public class BatchReachargeJobQueue<T> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public BatchReachargeJobQueue(LoanTask task) {
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

	public static BatchReachargeJobQueue<BatchReachargeBean> batchReacharge = null;

	public static void init(AutoService autoService) {
		batchReacharge = new BatchReachargeJobQueue<BatchReachargeBean>(new BatchReachargeTask(autoService));
	}

	public static void stop() {
		batchReacharge.task.stop();
	}

	public void doAutoQueue() {
	}


}
