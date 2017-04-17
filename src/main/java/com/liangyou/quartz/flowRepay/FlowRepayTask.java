package com.liangyou.quartz.flowRepay;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AutoService;
/**
 *流转标还款业务处理
 * @author zxc
 */
public class FlowRepayTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(FlowRepayTask.class);

	@Resource
	private AutoService autoService;

	public AutoService getAutoService() {
		return autoService;
	}

	public void setAutoService(AutoService autoService) {
		this.autoService = autoService;
	}

	public FlowRepayTask(AutoService autoService) {
		super();
		task.setName("FLOW_REPAY.Task");
		this.autoService = autoService;
	}

	@Override
	public void doLoan() {
		while (FlowRepayJobQueue.FLOW_REPAY!=null && FlowRepayJobQueue.FLOW_REPAY.size() > 0) {
			BorrowRepayment br = FlowRepayJobQueue.FLOW_REPAY.peek();
			if (br != null) {
				try {
					autoService.flowRepay(br);//流转标，自动还款
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				} finally {
					FlowRepayJobQueue.FLOW_REPAY.remove(br);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return FlowRepayTask.FLOW_REPAY_STATUS;
	}

}
