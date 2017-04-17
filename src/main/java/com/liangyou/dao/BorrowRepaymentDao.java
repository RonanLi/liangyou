package com.liangyou.dao;

import java.util.Date;
import java.util.List;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.User;

public interface BorrowRepaymentDao extends BaseDao<BorrowRepayment>{
	/**
	 * 获取当前期数前的
	 * @param period
	 * @param borrowId
	 * @return
	 */
	public int getNoPayedRepayments(int period, Long borrowId);
	
	/**
	 * 获取所有未还款的repayment
	 * @param borrowId
	 * @return
	 */
	public List<BorrowRepayment> getNotRepayByBorrow(Long borrowId);

	
	/**
	 * 获取剩余本金，用于计算逾期罚息
	 * @param borrowId
	 * @return
	 */
	public double getCapital(long borrowId);
	
	/**
	 * 网站垫付后更改repayment的状态
	 * @param borrowId
	 */
	public void updateBorrowWebstatus(long borrowId);


	public BorrowRepayment getBorrowRepaymentByBorrowIdAndPeriod(long borrowId,
			int repaymentId);
	/**
	 * 查询指定用户逾期还款的个数
	 * @param user
	 * @return
	 */
	public int getLateRepaymentByUser(User user);
	
	/**
	 * 计算用户所有的待还本金
	 * @param userId
	 * @return
	 */
	public double sumNotRepaymentAccountByUser(long userId);
	
	/**
	 * 获取没有发放积分的repayment
	 * @return
	 */
	public List<BorrowRepayment> getNotRepayForCreditStatus();
	
	/**
	 * 获取没有发放奖励的repayment
	 * @return
	 */
	public List<BorrowRepayment> getRepayListForRewardStatus();
	
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	/**
	 * 最近的一笔还款
	 * @param borrowId
	 * @return
	 */
	public BorrowRepayment getRecentlyRepayment(long borrowId);
	/**
	 * 剩余未还款的本金
	 * @param borrowId
	 * @return
	 */
	public double getRemainderCapital(long borrowId);
	/**
	 * 剩余未还款的利息
	 * @param borrowId
	 * @return
	 */
	public double getRemainderInterest(long borrowId);
	/**
	 * 更新提前还款的repayment状态
	 * @param borrowId
	 */
	public void updatePriorRepay(long borrowId);
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	/**
	 * 计算当前还款利息和下期利息总和
	 * @param borrowId
	 * @param period
	 * @return
	 */
	public double getwaitRpayInterest(long borrowId,int period);
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
	//v1.8.0.5_u4 TGPROJECT-386   qinjun  2014-08-11 start 
	/**
	 * 获取没有还款的Repayment
	 * @return
	 */
	public List<BorrowRepayment> notRepayRepaymentList();
	//v1.8.0.5_u4 TGPROJECT-386   qinjun  2014-08-11 end
	
	public void updateRepayment(double account ,double interest,double lateFee,double comMoney,long id);
	
	public BorrowRepayment getRepaymentId(long id);
	
}
