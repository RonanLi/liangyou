package com.liangyou.quartz.cash;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.AccountCash;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.UserService;
/**
 * 取现、充值业务处理
 * @author zxc
 *
 */
public class CashJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public CashJobQueue(LoanTask task) {
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

	public static CashJobQueue<CashBean> CASH = null;

	public static void init(AccountService accountService,UserService userService) {
		CASH = new CashJobQueue<CashBean>(new CashTask(accountService,userService));
	}

	public static void stop() {
		CASH.task.stop();
	}

	public void doAutoQueue() {
	}

}
