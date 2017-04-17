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
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.api.chinapnr.CardCashOut;
import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AccountService;
import com.liangyou.service.UserService;
/**
 * 第三方账户，账户业务处理
 * @author zxc
 *
 */
public class ApiAccountTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(ApiAccountTask.class);

	@Resource
	private UserService userService;
	
	private AccountService accountService;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public ApiAccountTask(UserService userService,AccountService accountService) {
		super();
		task.setName("apiAccount.Task");
		this.userService = userService;
		this.accountService = accountService;
	}

	@Override
	public void doLoan() {
		while (ApiAccountJobQueue.API_ACCOUNT!=null && ApiAccountJobQueue.API_ACCOUNT.size() > 0) {
			ApiAccountBean aab = ApiAccountJobQueue.API_ACCOUNT.peek();
			if (aab != null) {
				String result = "success";
				// add by gy 2016年11月29日12:37:27
				// 增加wap端的判断
				if (aab.getBorrowParam() != null && aab.getBorrowParam().isWap()) {
					if ("apiUserActivate".equals(aab.getType())) {
						result = "授权成功!";
					} else if ("apiUserRegister".equals(aab.getType())) {
						result = "开户成功!";
					}
				}
				// end
				try {
					if("apiUserRegister".equals(aab.getType())){
						userService.apiUserRegister(aab.getUser());
					}else if("apiUserActivate".equals(aab.getType())){
						//双乾支付，授权
						userService.apiUserActivate(aab.getUser());
					}else if("cardCashOutNotify".equals(aab.getType())){
						CardCashOut ca =aab.getCardCashOut();
						logger.info("绑卡回调处理业务开始，从disruptor中获取绑卡对象"+ca.getOpenAcctId()+":"+ca.getRespDesc());
						accountService.addAccountBank(aab.getCardCashOut());
					}else if("apiLoanFastPay".equals(aab.getType())){
						userService.apiUserRegister(aab.getUser());
					}else if("mmmBindingCard".equals(aab.getType())){
						accountService.addAccountBank(aab.getMmmToLoanFastPay());
					}else if("mmmwWthhold".equals(aab.getType())){
						userService.apiUserActivate(aab.getUser());
					}else if("madeDeductSign".equals(aab.getType())){
						accountService.madeDeductSign(aab.getDeductSign());
					}else if("yjfRealNameCall".equals(aab.getType())){
						userService.yjfRealNameCall(aab.getNewAuthorize());
					}else if("huifuUserCorpRegister".equals(aab.getType())){
						userService.huifuUserCorpRegister(aab.getUser(), aab.getCorpRegister());
					}else if("doSignmanysign".equals(aab.getType())){
						accountService.doSignmanyBank(aab.getSinSignmanyBank());
					}else if("ipsAddBank".equals(aab.getType())){  //环迅
						accountService.doIpsAddBank(aab.getUser(), aab.getAccountBank());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，业务处理失败";
					}
				} finally {
					ApiAccountJobQueue.API_ACCOUNT.remove(aab);
				}
				if(aab.getBorrowParam()!=null&&aab.getBorrowParam().getResultFlag()!=null){
					//在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(aab.getBorrowParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return ApiAccountTask.API_ACCOUNT_STATUS;
	}

}
