package com.liangyou.quartz.tender;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.BorrowService;

/**
 * 投标、自动投标业务处理
 * 
 * @author Administrator
 */
public class TenderTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(TenderTask.class);

	@Resource
	private BorrowService borrowService;

	public BorrowService getBorrowService() {
		return borrowService;
	}

	public void setBorrowService(BorrowService borrowService) {
		this.borrowService = borrowService;
	}

	public TenderTask(BorrowService borrowService) {
		super();
		task.setName("Tender.Task");
		this.borrowService = borrowService;
	}

	@Override
	public void doLoan() {
		while (TenderJobQueue.TENDER != null && TenderJobQueue.TENDER.size() > 0) {
			TenderBean tb = TenderJobQueue.TENDER.peek();
			if (tb != null) {
				String result = "success";
				// add by gy 2016年11月29日12:37:27
				// 增加wap端的判断
				if (tb.getBorrowParam() != null && tb.getBorrowParam().isWap()) {
					result = "投标成功!";
				}
				// end
				try {
					if ("tender".equals(tb.getType())) {// 正常投标
						borrowService.addTender(tb.getBorrowParam(), tb.getUser());
					} else if ("mmmTender".equals(tb.getType())) {
						borrowService.addMmmTender(tb.getBorrowParam(), tb.getUser());
					} else if ("autoTender".equals(tb.getType())) {
						borrowService.doAutoTender(tb.getBorrowModel());
					} else if ("ipsAddborrow".equals(tb.getType())) {
						borrowService.doIpsAddBorrow(tb.getBorrowParam());
					} else if ("experienceTender".equals(tb.getType())) {// add by gy 2016-10-18 11:24:27 增加体验金的线程调用
						borrowService.doTenderExperienceBorrow(tb.getBorrowParam(), tb.getUser());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					if (e instanceof BussinessException || e instanceof ManageBussinessException) {// 业务异常，保存业务处理信息
						result = e.getMessage();
					} else {
						result = "系统异常，业务处理失败";
					}
				} finally {
					TenderJobQueue.TENDER.remove(tb);
				}
				if (tb.getBorrowParam() != null && tb.getBorrowParam().getResultFlag() != null) {
					// 在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(tb.getBorrowParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return TenderTask.TENDER_STATUS;
	}

}
