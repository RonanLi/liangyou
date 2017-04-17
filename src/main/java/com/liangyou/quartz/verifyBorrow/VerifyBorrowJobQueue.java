package com.liangyou.quartz.verifyBorrow;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
/**
 * 暂无处理
 * @author zxc
 *
 */
public class VerifyBorrowJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public VerifyBorrowJobQueue(LoanTask task) {
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

	public static VerifyBorrowJobQueue<Borrow> VERIFY_BORROW = null;

	public static void init(BorrowService borrowService) {
		VERIFY_BORROW = new VerifyBorrowJobQueue<Borrow>(new VerifyBorrowTask(borrowService));
	}

	public static void stop() {
		VERIFY_BORROW.task.stop();
	}

	public void doAutoQueue() {
	}

}
