package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.LoanStatistics;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 贷款统计服务
 *  v1.8.0.4_u4 TGPROJECT-371 qinjun 2014-07-22
 */
public interface LoanStatisticsService {
	/**
	 * 调度统计投资人信息
	 */
	public void quartzTenderStaticstics();

	/**
	 * 调度统计借款人信息
	 */
	public void quartzBorrowerStaticstics();

	public PageDataList<LoanStatistics> getLoanStatisticsList(SearchParam param);

	public PageDataList<LoanStatistics> getAllLoanStatisticsList(
			SearchParam param);

	/**
	 * 户账户资金情况统计表（用户名、客户姓名、经办客户经理、 还款日期、应还本金、应还利息、实还本金、实还利息、余额、可用余额） 【带分页的方法】
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<BorrowRepayment> getPageCustomAccountCount(
			SearchParam param);

	/**
	 * 户账户资金情况统计表（用户名、客户姓名、经办客户经理、 还款日期、应还本金、应还利息、实还本金、实还利息、余额、可用余额）
	 * 【无分页的方法】一般用作导出
	 * 
	 * @param param
	 * @return
	 */
	public List<BorrowRepayment> getListCustomAccountCount(SearchParam param);
}
