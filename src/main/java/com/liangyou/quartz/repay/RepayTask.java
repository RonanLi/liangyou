package com.liangyou.quartz.repay;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.WebPaidService;

/**
 * 划款、划款给网站、提前还款业务处理
 * 
 * @author zxc
 *
 */
public class RepayTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(RepayTask.class);

	private AutoService autoService;
	private BorrowService borrowService;
	private WebPaidService webPaidService;

	public RepayTask(AutoService autoService, BorrowService borrowService, WebPaidService webPaidService) {
		super();
		task.setName("Repay.Task");
		this.autoService = autoService;
		this.borrowService = borrowService;
		this.webPaidService = webPaidService;
	}

	@Override
	public void doLoan() {
		while (RepayJobQueue.REPAY != null && RepayJobQueue.REPAY.size() > 0) {

			RepayBean rb = RepayJobQueue.REPAY.peek();
			if (rb != null) {
				String result = "success";
				try {
					if ("repay".equals(rb.getType())) {// 正常还款的情况
						autoService.repay(rb.getBorrowRepayment());
					} else if ("priorRepay".equals(rb.getType())) {// 提前还款情况
						autoService.priorRepay(rb.getBorrowRepayment(), rb.getPpLog());
					} else if ("hfPriorRepay".equals(rb.getType())) {// 汇丰提前还款
						autoService.hfPriorRepay(rb.getBorrowRepayment(), rb.getPpLog());
					} else if ("repayToWebSite".equals(rb.getType())) {
						autoService.repayToWebSite(rb.getBorrowRepayment());
					} else if ("addAssignmentBorrow".equals(rb.getType())) {
						borrowService.addAssignmentBorrow(rb.getBorrowModel(), rb.getAccountLog(), rb.getBorrowTender());
					} else if ("doUserManualPayWeb".equals(rb.getType())) {
						webPaidService.doUserPayWeb(rb.getBorrowRepayment());
					} else if ("lzfPriorRepay".equals(rb.getType())) {
						autoService.LZFPriorRepay(rb.getBorrowRepayment(), rb.getPpLog());
					} else if ("ipsRepay".equals(rb.getType())) { // 环迅还款方法
						autoService.ipsRepay(rb.getIpsRepaymentModel());
					} else if ("experienceMoneyRepay".equals(rb.getType())) { // 体验金还款 add by gyu2016-10-21 12:18:42
						autoService.experienceMoneyRepay(rb.getBorrowRepayment());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					if (e instanceof BussinessException || e instanceof ManageBussinessException) {// 业务异常，保存业务处理信息
						result = e.getMessage();
					} else {
						result = "系统异常，业务处理失败";
					}
				} finally {
					RepayJobQueue.REPAY.remove(rb);
				}
				if (rb.getBorrowParam() != null && rb.getBorrowParam().getResultFlag() != null) {
					// 在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(rb.getBorrowParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return RepayTask.REPAY_STATUS;
	}

}
