package com.liangyou.service;

import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.model.MsgReq;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.model.borrow.IpsRepaymentModel;

/**
 * AutoService
 *
 * @author fuxingxing
 */
public interface AutoService {
	public void fullSuccess(BorrowModel wrapModel);

	public void fail(BorrowModel wrapModel);

	public void repay(BorrowRepayment repay);

	public void priorRepay(BorrowRepayment repay, PriorRepayLog ppLog);

	public void repayToWebSite(BorrowRepayment repay);

	public void flowRepay(BorrowRepayment repay);

	public void msgReq(MsgReq req);

	/**
	 * add by gy 2016-10-21 12:20:47 体验金还款
	 */
	public void experienceMoneyRepay(BorrowRepayment repay);

	/**
	 * 计算逾期天数及逾期罚息
	 */
	public void lateDaysAndInterest();

	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun start

	/**
	 * 汇丰贷提前还款
	 *
	 * @param repay
	 * @param ppLog
	 * @throws Exception
	 */
	public void hfPriorRepay(BorrowRepayment repay, PriorRepayLog ppLog) throws Exception;
	// v1.8.0.3_u3 TGPROJECT-334 2014-06-11 qinjun end

	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing start

	/**
	 * 老账房项目提前还款方法： 需求说明：提前还款需多支付一个月利息（即：提前还款当前的下一期的利息）
	 *
	 * @param repay
	 * @param ppLog
	 */
	public void LZFPriorRepay(BorrowRepayment repay, PriorRepayLog ppLog) throws Exception;
	// TGPROJECT-372 老账房项目提前还款 2014-07-21 wujing end

	/**
	 * 环迅还款方法 环迅存在一个条还款计划，需要分多次进行还款 所以单独处理
	 *
	 * @param ipsModel
	 */
	public void ipsRepay(IpsRepaymentModel ipsModel);

	/**
	 * 汇付，债权转让 调用新的债权转让接口重新处理
	 */
	public void fullSuccessCreditAssign(BorrowModel borrow);

	/**
	 * add by lijing 后台批量充值审核通过
	 * 
	 * @param accountRecharge
	 */
	public void batchRechargeSuccess(AccountRecharge accountRecharge);

	/**
	 * add by lijing 后台批量充值审核不通过
	 * 
	 * @param accountRecharge
	 */
	public void failBatchRecharge(AccountRecharge accountRecharge);

}
