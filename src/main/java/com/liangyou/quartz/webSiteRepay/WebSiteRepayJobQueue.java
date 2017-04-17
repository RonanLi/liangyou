package com.liangyou.quartz.webSiteRepay;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.RewardExtendService;
/**s
 * 还款给网站业务处理类
 * @author zxc
 *2、充值有红包奖励，也放到此处处理【垫付业务不是很多】
 */
public class WebSiteRepayJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public WebSiteRepayJobQueue(LoanTask task) {
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

	public static WebSiteRepayJobQueue<WebSiteRepayBean> WEBSITE_REPAY = null;

	public static void init(BorrowService borrowService,RewardExtendService rewardExtendService) {
		WEBSITE_REPAY = new WebSiteRepayJobQueue<WebSiteRepayBean>(new WebSiteRepayTask(borrowService,rewardExtendService));
	}

	public static void stop() {
		WEBSITE_REPAY.task.stop();
	}

	public void doAutoQueue() {
	}

}
