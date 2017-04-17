package com.liangyou.dao;

import com.liangyou.domain.AccountRecharge;

public interface RechargeDao {

	public AccountRecharge getRechargeByTradeno(String orderNo);
	public boolean updateRecharge(int status, String returnText, String trade_no,String flowNo);
}
