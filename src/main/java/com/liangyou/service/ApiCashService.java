package com.liangyou.service;

import java.util.Map;

import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.APIModel.AccountCashModel;

/**提现服务
 * 
 * 第三方接口提现方法接口
 * @author hj 2014/11/1
 *
 */
public interface ApiCashService {
	/**
	 * 环迅提现处理
	 * @param accountCash
	 */
	public void doIpsCashService(AccountCashModel cashModel,AccountLog log,BorrowParam param,AccountCash cash);
	
	/**
	 * 易极付提现处理
	 */
	public void doYjfCashService(AccountCashModel cashModel,AccountCash cash,AccountLog log);
	
	/**
	 * 汇付取现
	 */
	
	public void doHfCashService(AccountCashModel cashModel,AccountCash cash,AccountLog log);
	
	/**
	 * 乾多多提现处理
	 */
	public void doMmmCashService(AccountCashModel cashModel,AccountLog log,BorrowParam param);
	
	/**
	 * 乾多多提现对账查询
	 */
	public Map<String,String> findMmmCashBalance(String orderNo,String loanNo);
	
	/**
	 * 乾多多取现处理失败，资金退回
	 */
	public boolean cashAccountBack(AccountCash accountCash,String cashStatus);
}
