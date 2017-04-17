package com.liangyou.dao;

import com.liangyou.domain.AccountLog;


public interface AccountLogDao extends BaseDao<AccountLog> {

	/**
	 * 计算风险准备金总和
	 * @return
	 */
	public double getSumRiskreserveFee();
}
