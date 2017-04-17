package com.liangyou.quartz.batchRecharge;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.AccountRecharge;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.AutoService;
import com.liangyou.service.UserService;

public class BatchReachargeTask extends AbstractLoanTask{
	@Resource
	private AutoService autoService;
	private Logger logger = Logger.getLogger(BatchReachargeTask.class);
	public BatchReachargeTask(AutoService autoService) {
		super();
		task.setName("batchReacharge.Task");
		this.autoService = autoService;
	}

	@SuppressWarnings("unused")
	@Override
	public void doLoan() {
		while (BatchReachargeJobQueue.batchReacharge!=null && BatchReachargeJobQueue.batchReacharge.size() > 0) {
			 BatchReachargeBean reachargeBean = BatchReachargeJobQueue.batchReacharge.peek();
			if (reachargeBean != null) {
				String result = "success";
				try {
					if("verifyBatchRecharge".equals(reachargeBean.getType())){
						autoService.batchRechargeSuccess(reachargeBean.getAccountRecharge());//后台批量充值，通过
					}else if ("failVerifyBatchRecharge".equals(reachargeBean.getType())){
						autoService.failBatchRecharge(reachargeBean.getAccountRecharge());//后台批量充值，不通过
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，审核批量充值处理失败";
					}
				} finally {
					BatchReachargeJobQueue.batchReacharge.remove(reachargeBean);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return BatchReachargeTask.CASH_STATUS;
	}



}
