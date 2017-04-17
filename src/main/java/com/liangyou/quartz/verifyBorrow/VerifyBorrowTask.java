package com.liangyou.quartz.verifyBorrow;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.BorrowService;
/**
 * 暂无处理
 * @author zxc
 *
 */
public class VerifyBorrowTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(VerifyBorrowTask.class);

	@Resource
	private BorrowService borrowService;

	public BorrowService getBorrowService() {
		return borrowService;
	}

	public void setBorrowService(BorrowService borrowService) {
		this.borrowService = borrowService;
	}

	public VerifyBorrowTask(BorrowService borrowService) {
		super();
		task.setName("verifyBorrow.Task");
		this.borrowService = borrowService;
	}

	@Override
	public void doLoan() {
		while (VerifyBorrowJobQueue.VERIFY_BORROW!=null && VerifyBorrowJobQueue.VERIFY_BORROW.size() > 0) {
			Borrow borrow = VerifyBorrowJobQueue.VERIFY_BORROW.peek();
			if (borrow != null) {
				try {
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				} finally {
					VerifyBorrowJobQueue.VERIFY_BORROW.remove(borrow);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return VerifyBorrowTask.VERIFY_BORROW_STATUS;
	}

}
