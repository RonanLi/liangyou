package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.LoanStatistics;
import com.liangyou.domain.User;

/**
 * v1.8.0.4_u4 TGPROJECT-371 	qinjun 2014-07-22
 */
public interface LoanStatisticsDao extends BaseDao<LoanStatistics>{
	/**
	 * 根据用户查询记录
	 * @param user
	 * @return
	 */
	public LoanStatistics getLoanStatisticsByUser(User user);
	/**
	 * 获取网站投资人投资情况
	 * @return
	 */
	public List<LoanStatistics> getTenderStatisticsList();
	/**
	 * 获取未完成还款的投资笔数
	 * @param userId
	 * @return
	 */
	public int getWaitTenderCountByUser(long userId);
	/**
	 * 获取逾期的待收本金
	 * @param userId
	 * @return
	 */
	public double getLateCapitalByUser(long userId);
	/**
	 * 获取借款人借款情况
	 * @return
	 */
	public List<LoanStatistics> getLoanStatisticsList() ;
	/**
	 * 获取用户未还款本金
	 * @param userId
	 * @return
	 */
	public double getWaitRepayCapitalByUser(long userId);
	/**
	 * 获取网站自定义计算标应还金额
	 * @param userId
	 * @return
	 */
	public double getDiyBorrowAcountByUser(long userId) ;
}
