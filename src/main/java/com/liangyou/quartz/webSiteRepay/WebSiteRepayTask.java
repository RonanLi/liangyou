package com.liangyou.quartz.webSiteRepay;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.BorrowService;
import com.liangyou.service.RewardExtendService;
/**
 * 还款给网站业务处理类
 * @author zxc
 *2、充值有红包奖励，也放到此处处理【垫付业务不是很多】
 */
public class WebSiteRepayTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(WebSiteRepayTask.class);

	private BorrowService borrowService;
	private RewardExtendService rewardExtendService;

	public BorrowService getBorrowService() {
		return borrowService;
	}
	public void setBorrowService(BorrowService borrowService) {
		this.borrowService = borrowService;
	}

	public WebSiteRepayTask(BorrowService borrowService,RewardExtendService rewardExtendService) {
		super();
		task.setName("webSite_REPAY.Task");
		this.borrowService = borrowService;
		this.rewardExtendService = rewardExtendService;
	}

	public RewardExtendService getRewardExtendService() {
		return rewardExtendService;
	}

	public void setRewardExtendService(RewardExtendService rewardExtendService) {
		this.rewardExtendService = rewardExtendService;
	}

	@Override
	public void doLoan() {
		while (WebSiteRepayJobQueue.WEBSITE_REPAY!=null && WebSiteRepayJobQueue.WEBSITE_REPAY.size() > 0) {
			WebSiteRepayBean wsbb = WebSiteRepayJobQueue.WEBSITE_REPAY.peek();
			if (wsbb != null) {
				String result = "success";
				try {
					if("webSitePayForLateBorrow".equals(wsbb.getType())){
						borrowService.webSitePayForLateBorrow(wsbb.getRepaymentId(), wsbb.getParam());
					}else if("doRedExtend".equals(wsbb.getType())){
						//发放红包
						rewardExtendService.doRedExtend(wsbb.getAccountRechargeIds());
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					if(e instanceof BussinessException || e instanceof ManageBussinessException){//业务异常，保存业务处理信息
						result = e.getMessage();
					}else{
						result = "系统异常，业务处理失败";
					}
				} finally {
					WebSiteRepayJobQueue.WEBSITE_REPAY.remove(wsbb);
				}
				if(wsbb.getParam()!=null&&wsbb.getParam().getResultFlag()!=null){
					//在需要保存系统处理信息的地方直接保存进来
					Global.RESULT_MAP.put(wsbb.getParam().getResultFlag(), result);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return WebSiteRepayTask.WEBSITE_REPAY_STATUS;
	}

}
