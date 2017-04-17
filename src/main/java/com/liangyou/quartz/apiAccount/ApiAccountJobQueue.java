package com.liangyou.quartz.apiAccount;
/**
 * 业务处理类型：
 * 第三方账户开立
 * 用户账户激活
 * 绑定银行卡异步通知
 * 双乾三合一接口
 * 扣款签名
 * 易极付实名
 * 汇付合作账户开立
 * 【重要】：
 *  此队列只处理第三方账户相关注册信息的，和资金信息无关。
 */
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.liangyou.domain.User;
import com.liangyou.quartz.LoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.UserService;
/**
 * 第三方账户处理专用队列
 * @author zxc
 *
 * @param <T>
 */
public class ApiAccountJobQueue<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Queue<T> queue = new ConcurrentLinkedQueue();
	private LoanTask task;

	public ApiAccountJobQueue(LoanTask task) {
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

	public static ApiAccountJobQueue<ApiAccountBean> API_ACCOUNT = null;

	public static void init(UserService userService,AccountService accountService) {
		API_ACCOUNT = new ApiAccountJobQueue<ApiAccountBean>(new ApiAccountTask(userService,accountService));
	}

	public static void stop() {
		API_ACCOUNT.task.stop();
	}

	public void doAutoQueue() {
	}

}
