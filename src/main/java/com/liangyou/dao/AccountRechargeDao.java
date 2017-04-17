package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.AccountOneKeyRecharge;
import com.liangyou.domain.AccountRecharge;

public interface AccountRechargeDao extends BaseDao<AccountRecharge> {

	public AccountRecharge getRechargeByTradeno(String orderNo);

	/**
	 * 更新充值单据
	 * @param status
	 * @param returnText
	 * @param trade_no
	 * @return
	 */
	public  boolean updateRecharge(int status, String returnText, String trade_no,String flowNo);

	/**
	 * 线下充值，根据流水号查询
	 * @param serialNo
	 * @return
	 */
	int getCountBySerialNo(String serialNo);

	//add by lijing  2016-11-25 09:22:20 查询所有一键充值的用户
	
	public List<AccountOneKeyRecharge> findAllRechargeUser();

	//add by lijing 2016-11-25 09:22:40 删除一键充值数据库
	public void deleteAllRecharge();
	
}
