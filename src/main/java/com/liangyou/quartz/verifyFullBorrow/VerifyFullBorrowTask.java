package com.liangyou.quartz.verifyFullBorrow;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
/**
 * 满标复审业务处理类
 * @author zxc
 *
 */
public class VerifyFullBorrowTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(VerifyFullBorrowTask.class);

	@Resource
	private AutoService autoService;
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public AutoService getAutoService() {
		return autoService;
	}

	public void setAutoService(AutoService autoService) {
		this.autoService = autoService;
	}

	public VerifyFullBorrowTask(AutoService autoService) {
		super();
		this.autoService = autoService;
	}

	@Override
	public void doLoan() {
		while (VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW!=null && VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW.size() > 0) {
			VerifyFullBorrowBean vfb = VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW.peek();
			if (vfb != null) {
				String result = "success";
				try {
					if("fullSuccess".equals(vfb.getType())){
					/*	//这里考虑到债权转让的情况
						if(vfb.getBorrowModel().getModel().getIsAssignment() == 1){
							autoService.fullSuccessCreditAssign(vfb.getBorrowModel());
						}else{*/
							autoService.fullSuccess(vfb.getBorrowModel());//满标复审，通过
						//}
						
					}else if ("failBorrow".equals(vfb.getType())){
						autoService.fail(vfb.getBorrowModel());//满标复审，不通过
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，业务处理失败";
					}
				} finally {
					VerifyFullBorrowJobQueue.VERIFY_FULL_BORROW.remove(vfb);
					if(vfb.getBorrowParam()!=null&&vfb.getBorrowParam().getResultFlag()!=null){
						//在需要保存系统处理信息的地方直接保存进来
						Global.RESULT_MAP.put(vfb.getBorrowParam().getResultFlag(), result);
					}
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return VerifyFullBorrowTask.VERIFY_BORROW_STATUS;
	}

}
