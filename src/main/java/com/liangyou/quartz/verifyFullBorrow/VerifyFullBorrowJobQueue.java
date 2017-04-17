package com.liangyou.quartz.verifyFullBorrow;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
/**
 * 满标复审队列
 * @author zxc
 *
 * @param <T>
 */
public class VerifyFullBorrowJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public VerifyFullBorrowJobQueue(LoanTask task) {
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

	public static VerifyFullBorrowJobQueue<VerifyFullBorrowBean> VERIFY_FULL_BORROW = null;

	public static void init(AutoService autoService) {
		VERIFY_FULL_BORROW = new VerifyFullBorrowJobQueue<VerifyFullBorrowBean>(new VerifyFullBorrowTask(autoService));
	}

	public static void stop() {
		VERIFY_FULL_BORROW.task.stop();
	}

	public void doAutoQueue() {
	}

}
