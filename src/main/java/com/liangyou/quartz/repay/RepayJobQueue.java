package com.liangyou.quartz.repay;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.TenderCompensationService;
import com.liangyou.service.WebPaidService;
/**
 * 划款、划款给网站、提前还款业务处理
 * @author zxc
 *
 */
public class RepayJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public RepayJobQueue(LoanTask task) {
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

	public static RepayJobQueue<RepayBean> REPAY = null;

	public static void init(AutoService autoService,BorrowService borrowService,WebPaidService webPaidService) {
		REPAY = new RepayJobQueue<RepayBean>(new RepayTask(autoService, borrowService, webPaidService));
	}
	
	public static void stop() {
		REPAY.task.stop();
	}

	public void doAutoQueue() {
	}

}
