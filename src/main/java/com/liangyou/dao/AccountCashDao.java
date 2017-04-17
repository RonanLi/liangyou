package com.liangyou.dao;

import com.liangyou.domain.AccountCash;

public interface AccountCashDao extends BaseDao<AccountCash>{

	public AccountCash getAccountCashByOrderNo(String orderNo);

	//TGPROJECT-410 wsl 2014-09-03 start
	/**
	 * 查询当日提现金额（包括提现成功的和正在申请的）
	 * @param userId
	 * @return
	 */
	public double getAccountCashDailySum(long userId);
	//TGPROJECT-410 wsl 2014-09-03 end
}
