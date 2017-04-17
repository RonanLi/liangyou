package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.AccountBank;
import com.liangyou.domain.User;


public interface AccountBankDao extends BaseDao<AccountBank> {
	public AccountBank getBankByNo(String cardNo,String jyfId); //根据银行卡号进行查询
	public List<AccountBank> getBankByUserId(long userId,int isbuild);  //根据用户id和签约状态查询
	/**
	 * 获取有效的提现银行
	 * @param userId
	 * @return
	 */
	public List<AccountBank> getAllBankList(long userId);
	
	public AccountBank getAccountBankByCardNo(String cardNo, User user);
}
