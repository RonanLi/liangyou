package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Account;
import com.liangyou.domain.WebPaid;

/**
 * 垫付系统自动扣款
 * @author wujing
 *
 */
public interface WebPaidDao extends BaseDao<WebPaid> {
	
	
	/**
	 *查询垫付待还大于0的用户
	 * @return
	 */
	public List<WebPaid> getWaitWebPaidList();
	
	/**
	 * 更新借款逾期费用：只用跟新罚息总额和待还总额
	 * @param id
	 * @param penalMoney：罚息金额
	 */
	public void updateWebPenal(long WebId,double penalMoney);
	
	/**
	 * 垫付扣款：更新已还金额和待还总额
	 * @param Id
	 * @param repayMoney：扣款金额
	 */
	public void updateRepayWeb(long Id,double repayMoney);
	
	/**
	 * 根据borrowid和repaymentid获取垫付对象
	 * @param borrowId
	 * @param repaymentId
	 */
	public WebPaid getWebPaidByBorrowIdAndRepayId(long borrowId,long repaymentId);
	
	
	
	

}
