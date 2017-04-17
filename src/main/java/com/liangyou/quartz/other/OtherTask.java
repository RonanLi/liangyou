package com.liangyou.quartz.other;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jgroups.tests.perf.Data;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.quartz.AbstractLoanTask;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.WebPaidService;
/**
 * 计算每天逾期利息、发放奖励、老账房还款给网站业务处理
 * @author zxc
 *
 */
public class OtherTask extends AbstractLoanTask {
	private Logger logger = Logger.getLogger(OtherTask.class);

	private AutoService autoService;
    private RewardExtendService rewardExtendService;
    private WebPaidService webPaidService;

	public OtherTask(AutoService autoService,RewardExtendService rewardExtendService,WebPaidService webPaidService) {
		super();
		task.setName("Other.Task");
		this.autoService = autoService;
		this.rewardExtendService= rewardExtendService;
		this.webPaidService = webPaidService;
	}

	@Override
	public void doLoan() {
		while (OtherJobQueue.OTHER!=null && OtherJobQueue.OTHER.size() > 0) {
			OtherBean ob = OtherJobQueue.OTHER.peek();
			if (ob != null) {
				try {
					//处理还款中的情况
					if("lateDaysAndInterest".equals(ob.getType())){
						logger.info("统计逾期利息" + new  Date());
						autoService.lateDaysAndInterest();
					}else if("doRewardAsReadyMoney".equals(ob.getType())){
						rewardExtendService.doRewardAsReadyMoney();
					}else if("doRegisterAndIdentReward".equals(ob.getType())){
						rewardExtendService.doRegisterAndIdentReward();
					}else if("doAutoRepayWeb".equals(ob.getType())){
						webPaidService.doRepayWeb();
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				} finally {
					OtherJobQueue.OTHER.remove(ob);
				}
			}
		}
	}

	@Override
	public Object getLock() {
		return OtherTask.OTHER_STATUS;
	}

}
