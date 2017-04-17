package com.liangyou.quartz.payOut;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.UserService;
/**
 * vip付款、汇付金账户操作业务处理
 * @author zxc
 *
 */
public class PayOutTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(PayOutTask.class);

	private AccountService accountService;
	private UserService userService;

	public PayOutTask(AccountService accountService,UserService userService) {
		super();
		task.setName("Repay.Task");
		this.accountService = accountService;
		this.userService = userService;
	}

	@Override
	public void doLoan() {
		while (PayOutJobQueue.PAYOUT!=null && PayOutJobQueue.PAYOUT.size() > 0) {
			PayOutBean pt = PayOutJobQueue.PAYOUT.peek();
			if (pt != null) {
				String result = "success";
				try {
					if("glodCashSuccess".equals(pt.getType())){
						accountService.glodCashSuccess(pt.getCashOut(), pt.getWebGlodLog());
					}else if("payVipCallBack".equals(pt.getType())){
						userService.VerifyVipSuccess(pt.getUserCache(), pt.getAccountLog());
					}else if("webGlodLogBack".equals(pt.getType())){
						accountService.webGlodLogBack(pt.getWebGlodLog());
					}else if("interestGenerateCall".equals(pt.getType())){
						accountService.madeInterestGenerateCall(pt.getFssTrans());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，业务处理失败";
					}
				} finally {
					PayOutJobQueue.PAYOUT.remove(pt);
				}
				if(pt.getBorrowParam()!=null&&pt.getBorrowParam().getResultFlag()!=null){
					//在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(pt.getBorrowParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return PayOutTask.PAYOUT_STATUS;
	}

}
