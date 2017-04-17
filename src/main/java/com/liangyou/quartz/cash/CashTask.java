package com.liangyou.quartz.cash;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.UserService;
/**
 * 取现、充值业务处理
 * @author zxc
 *
 */
public class CashTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(CashTask.class);

	private AccountService accountService;
	private UserService userService;

	public CashTask(AccountService accountService,UserService userService) {
		super();
		task.setName("cash.Task");
		this.accountService = accountService;
		this.userService= userService;
	}

	@Override
	public void doLoan() {
		while (CashJobQueue.CASH!=null && CashJobQueue.CASH.size() > 0) {
			CashBean cb = CashJobQueue.CASH.peek();
			if (cb != null) {
				String result = "success";
				// add by gy 2016年11月29日12:37:27
				// 增加wap端的判断
				if (cb.getBorrowParam() != null && cb.getBorrowParam().isWap()) {
					if("mmmverifyCashBack".equals(cb.getType())) {
						result = "取现成功,等待银行处理!";
					} else if ("rechargeBack".equals(cb.getType())) {
						result = "充值成功！";
					}
				}
				// end

				try {
					//处理取现的情况
					if("rechargeBack".equals(cb.getType())){
						accountService.doRechargeTask(cb.getRechargeModel(), cb.getAccountLog());
					}else if("verifyCashBack".equals(cb.getType())){
						accountService.cashCallBack(cb.getAccountCashModel(),null);
					}else if("hfverifyCashBack".equals(cb.getType())){
						accountService.cashCallBack(cb.getAccountCashModel(),cb.getBorrowParam());
					}else if("mmmverifyCashBack".equals(cb.getType())){
						accountService.cashCallBack(cb.getAccountCashModel(),cb.getBorrowParam());
					}else if("IpsCashBack".equals(cb.getType())){	//环迅提现处理
						accountService.cashCallBack(cb.getAccountCashModel(), cb.getBorrowParam());
					}else if("payDeductRecharge".equals(cb.getType())){ //易极付代付异步处理
						accountService.deductRechargeOffLine(cb.getYzzNewDeduct());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，业务处理失败";
					}
				} finally {
					CashJobQueue.CASH.remove(cb);
				}
				if(cb.getBorrowParam() != null && cb.getBorrowParam().getResultFlag() != null){
					//在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(cb.getBorrowParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return CashTask.CASH_STATUS;
	}

}
