package com.liangyou.service;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.WebPaid;
import com.liangyou.domain.WebRepayLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 自动扣款业务
 * @author wujing
 *
 */

public interface WebPaidService {
	
	/**
	 * 获取分页的集合
	 * @param param
	 * @return
	 */
	public PageDataList<WebPaid> getPageWebRepayList(SearchParam param);
	
	/**
	 * 系统自动扣款业务
	 */
	public void doRepayWeb();
	
	
	
	/**
	 * 此处应该在网站垫付时添加垫付记录
	 * 平台垫付，处理添加垫付记录:垫付规则：先本金，再利息，然后罚息
	 * @param waitRepayAccount   待还垫付总额
	 * @param repayment     垫付还款计划
	 * @param webPayAccount    垫付本金
	 * @param webPayInterest   垫付利息
	 * @param webPayPenalty    垫付罚息
	 */
	public void doWebPay(BorrowRepayment  repayment, double webPayAccount, double webPayInterest, double webPayPenalty,double waitRepayAccount);
	
	/**
	 * 处理垫付后，用户手动还款给网站
	 * @param repaymentId
	 */
	public void doUserPayWeb(BorrowRepayment  repayment);
	
	/**
	 * 根据还款id和borrowid 获取垫付记录
	 * @param repayId
	 * @param borrowId
	 * @return
	 */
	public WebPaid getWebPaidByRepayIdAndBorrowId(long repayId,long borrowId);
	
	/**
	 * 获取
	 * @param param
	 * @return
	 */
	public PageDataList<WebRepayLog> getRepayWebLog(SearchParam param);

}
