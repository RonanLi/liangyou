package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Account;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.model.account.BorrowSummary;
import com.liangyou.model.account.CollectSummary;
import com.liangyou.model.account.InvestSummary;
import com.liangyou.model.account.RepaySummary;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.account.WebAccountSumModel;


public interface AccountDao extends BaseDao<Account> {
	//v1.8.0.4_u1 TGPROJECT-289 lx start
	/**
	 * 获取网站所有用户资金的统计
	 * @return
	 */
	public WebAccountSumModel getWebAccountSumModel();
		//v1.8.0.4_u1 TGPROJECT-289 lx end
	/**
	 * 个人账户统计
	 * @param user_id
	 * @return
	 */
	public UserAccountSummary getUserAccountSummary(long user_id);
	
	/**
	 * 更新账户资金表
	 * @param total 账户总额
	 * @param use 账户可用余额
	 * @param nouse 账户冻结余额
	 * @param user_id 用户ID
	 */
	public void updateAccount(double total,double use,double nouse,long user_id);
	/**
	 * 更新账户资金表，保证账户更新后的账户总额大于0
	 * @param total 账户总额
	 * @param use 账户可用余额
	 * @param nouse 账户冻结余额
	 * @param user_id 用户ID
	 */
	public int updateAccountNotZero(double total,double use,double nouse,long user_id);
	/**
	 * 更新账户资金表
	 * @param total 账户总额
	 * @param use 账户可用余额
	 * @param nouse 账户冻结余额
	 * @param collect 账户待收金额
	 * @param repay 账户待还金额
	 * @param user_id 用户ID
	 */
	public void updateAccount(double total,double use,double nouse,double collect,double repay,long user_id);
	/**
	 * 管理后台中获取所有dw_account_tj 表中用户的账户信息
	 * @param start
	 * @param end
	 * @param param
	 * @return
	 */
	public Account getAcountByUser(User user);
	public double[] getUserIncoming(long user_id);
	
	public RepaySummary getRepaySummary(long user_id) ;
	/**
	 * 借款信息汇总
	 * @param user_id
	 * @return
	 */
	public BorrowSummary getBorrowSummary(long user_id);
	
	public InvestSummary getInvestSummary(long user_id);
	public List<InvestSummary>  getInvestSummaryList(int type);
	
	public CollectSummary getCollectSummary(long user_id);
	
	public double getChargeTotal(long user_id);
	
	public double getCacheTotal(long user_id);
	
	public int[] getCountRusultBySql(long userId);
	
	public double getAllPropertyBorrowRepayAccount(long user_id);
	
	public double getSumAwardDeductByUser(long user_id);
	
	public double[] getWaitFullSummary(long user_id);
	
	public void testBuilder();
	
	public int getSuccessAccountCash(long userId);
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 start
	/**
	 * 查询用户的投资详情
	 * @param tenderingParam
	 * @param tenderedParam
	 * @return
	 */
	public List<BaseAccountSumModel> getBaseAccountSum(SearchParam tenderParam, SearchParam userParam);
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 end
	/**
	 * 更新账户资金表,用于收回转账
	 * @param total 账户总额
	 * @param use 账户可用余额
	 * @param nouse 账户冻结余额
	 * @param user_id 用户ID
	 *//*
	public void updateAccountBack(double total,double use,double nouse,long user_id);*/
	
	
}
