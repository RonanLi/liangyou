package com.liangyou.quartz.payOut;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.UserService;
/**
 * vip付款、汇付金账户操作业务处理
 * @author zxc
 *
 */
public class PayOutJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public PayOutJobQueue(LoanTask task) {
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

	public static PayOutJobQueue<PayOutBean> PAYOUT = null;

	public static void init(AccountService accountService,UserService userService) {
		PAYOUT = new PayOutJobQueue<PayOutBean>(new PayOutTask(accountService,userService));
	}

	public static void stop() {
		PAYOUT.task.stop();
	}

	public void doAutoQueue() {
	}

}
