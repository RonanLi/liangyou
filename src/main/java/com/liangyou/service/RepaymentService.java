package com.liangyou.service;

import java.util.List;
import java.util.Map;

import com.liangyou.api.chinapnr.Repayment;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 还款处理服务
 * 
 * @author 1432
 *
 */
public interface RepaymentService {
	public BorrowRepayment getRepayment(long id);

	public List<BorrowRepayment> getRepaymentListByBorrow(long id);

	public void repay(long id, BorrowParam param) throws Exception;

	public void repayToWebSite(long id, BorrowParam param);

	/**
	 * 提前还款方法
	 * 
	 * @param id
	 * @param param
	 */
	public void priorRepay(long id, BorrowParam param, PriorRepayLog ppLog)
			throws Exception;

	/**
	 * 查询还款计划
	 * 
	 * @param borrowId
	 * @return
	 */
	public List<BorrowRepayment> getRepayMents(Long borrowId);

	public PageDataList getRepayMentsBySearchParam(SearchParam param);

	/**
	 * //true 没有 ， false 有
	 * 
	 * @param period
	 * @param borrowId
	 * @return
	 */
	public boolean checkNoPayedRepayment(int period, Long borrowId);

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun start
	/**
	 * 汇丰贷提前还款剩余期数
	 * 
	 * @param borrow
	 * @throws Exception
	 */
	public void hfPriorRepay(BorrowRepayment repay, BorrowParam param,
			PriorRepayLog ppLog) throws Exception;

	/**
	 * 汇丰贷计算提前还款金额
	 */
	public Map<String, Double> getHfPriorRepayMoney(Borrow borrow);

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start

	/**
	 * 提前还款校验
	 * 
	 * @param repayment
	 * @return
	 */
	public double doCheckLZFPriorrepay(BorrowRepayment repayment);

	/**
	 * 处理提前还款操作
	 * 
	 * @param repay
	 * @param param
	 * @param ppLog
	 * @throws Exception
	 */
	public void doLZFPriorRepay(BorrowRepayment repay, BorrowParam param,
			PriorRepayLog ppLog) throws Exception;;

	/**
	 * 计算老账房提前还款应还本金，当期利息，补偿金
	 * 
	 * @param borrow
	 * @return
	 */
	public Map<String, Double> getLZFPriorMoney(Borrow borrow);
	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

}