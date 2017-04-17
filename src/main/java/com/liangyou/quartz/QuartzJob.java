package com.liangyou.quartz;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountCashDao;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.UserCacheDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.UserCache;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.InvestSummary;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.ExperienceMoneyService;
import com.liangyou.service.LoanStatisticsService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.util.DateUtils;

@Component
public class QuartzJob {

	private Logger logger = Logger.getLogger(QuartzJob.class);

	@Autowired
	private BorrowCollectionDao borrowCollectionDao;
	@Autowired
	private BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountCashDao accountCashDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserCreditService userCreditService;
	// v1.8.0.4_u1 TGPROJECT-241 qj 2014-05-06 start
	@Autowired
	private UserCacheDao userCacheDao;
	@Autowired
	private UserService userService;
	// v1.8.0.4_u1 TGPROJECT-241 qj 2014-05-06 start
	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start
	@Autowired
	private BorrowService borrowService;
	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start

	// 1.8.0.4_u3 TGPROJECT wujing dytz start
	@Autowired
	private RewardExtendService rewardExtendService;
	// 1.8.0.4_u3 TGPROJECT wujing dytz end
	// v1.8.0.4_u4 TGPROJECT-371 qinjun 2014-07-22 start
	@Autowired
	private LoanStatisticsService loanStatisticsService;
	// v1.8.0.4_u4 TGPROJECT-371 qinjun 2014-07-22 end

	@Autowired
	private ExperienceMoneyService experienceMoneyService;

	@Scheduled(cron = "0 */5 * * * ?") // 每5分钟
	public void doAutoQueue() {// 流转标还款
		SearchParam param = SearchParam.getInstance().addParam("borrow.type", Constant.TYPE_FLOW).addParam("repaymentTime", Operator.LTE, new Date()).addParam("status", 0).addParam("webstatus", 0);
		List<BorrowRepayment> rlist = borrowRepaymentDao.findByCriteria(param);
		for (BorrowRepayment borrowRepayment : rlist) {
			DisruptorUtils.flowRepay(borrowRepayment);
		}
	}

	/* edit by gy 2017-02-10 10:12:24 关闭秒标自动还款定时任务 */
	//	@Scheduled(cron = "0 */10 * * * ?") // 秒满标，自动还款,改成每一分钟处理一次
	/* -- end  */
	public void doAutoSencondRepay() {
		logger.info("秒标自动还款进来");
		SearchParam param = SearchParam.getInstance().addParam("borrow.type", Constant.TYPE_SECOND).addParam("borrow.status", 6).addParam("status", 0).addParam("webstatus", 0);
		List<BorrowRepayment> rlist = borrowRepaymentDao.findByCriteria(param);
		for (BorrowRepayment borrowRepayment : rlist) {
			DisruptorUtils.repay(borrowRepayment, null);
		}
	}

	/**
	 * add by gy 2016年10月20日16:22:393 体验金定时任务
	 */
	// @Scheduled(cron = "0 */10 * * * ?")//每10分钟处理一次
	// @Scheduled(cron="0/30 * * * * ? ") //每30秒执行一次
	@Scheduled(cron = "0 0 0 * * ?") // 每零点执行一次
	public void doAutoExperienceMoneyRepay() {
		if ("1".equals(Global.getValue("is_enable_experienceMoney"))) {
			logger.info("体验金标自动还款进来");
			DisruptorUtils.experienceMoneyRepay(null, null);
		}
	}

	/**
	 * 修改体验金失效状态
	 */
	// @Scheduled(cron = "0 */10 * * * ?")//每10分钟处理一次
	// @Scheduled(cron="0/30 * * * * ? ") //每30秒执行一次
	@Scheduled(cron = "0 0 0 * * ?") // 每零点执行一次
	public void editExperienceMoneyStatus() {
		if ("1".equals(Global.getValue("is_enable_experienceMoney"))) {
			logger.info("体验金到期时间查询开始");

			// 如果体验金到期，则失效
			List<ExperienceMoney> emList = experienceMoneyService.findExperienceMoney(SearchParam.getInstance().addParam("useStatus", 0));
			for (ExperienceMoney em : emList) {
				if (new Date().after(DateUtils.rollDay(em.getAddTime(), em.getTimeLimit()))) {
					logger.info("修改的体验金未使用失效：" + em.getId());
					em.setUseStatus(2);
				}
			}
			experienceMoneyService.update(emList);

			// 如果利息到期，则失效
			List<ExperienceMoney> emList2 = experienceMoneyService.findExperienceMoney(SearchParam.getInstance().addParam("interestIncomeStatus", 1).addParam("interestUseStatus", 0));
			for (ExperienceMoney em : emList2) {
				if (new Date().after(DateUtils.rollDay(em.getInterestIncomeTime(), 15))) {
					logger.info("修改的体验金利息未使用失效：" + em.getId());
					em.setInterestUseStatus(2);
				}
			}
			experienceMoneyService.update(emList2);
		}
	}

	@Scheduled(cron = "0 3 * * * ?") // 每3小时零1分 ---查看global resultMap 内容是过大
	public void clearGlobalResultMap() {
		if (Global.RESULT_MAP.size() > 500) { // 当 Global.RESULT_MAP 大于 500 时候 全部清空
			Global.RESULT_MAP.clear();
		}
	}

	// @Scheduled(cron = "0 */2 * * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void lateDaysAndInterest() {// 计算逾期天数及利息
		try {
			DisruptorUtils.lateDaysAndInterest();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	// @Scheduled(cron = "0 0 1 * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void setWebSummary() {// 更新缓存，就算统计金额，聚人贷在用。
		try {
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			// 网站交易总额
			double successSumAccount = borrowDao.getSuccessBorrowSumAccount();
			int sumTender = borrowTenderDao.sumTender();
			double riskreserve_fee = accountLogDao.getSumRiskreserveFee();
			wac.getServletContext().setAttribute("sumBorrowAccount", successSumAccount);
			wac.getServletContext().setAttribute("sumTender", sumTender);
			wac.getServletContext().setAttribute("riskreserve_fee", riskreserve_fee);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-10 start
	// @Scheduled(cron = "0 0 1 * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void setAccountCash() {// 双乾取现 更新accoutcash表，当提现时间超过一天 且状态为1的就改为5
		try {
			Date d = new Date();
			long now = d.getTime();
			int dayTime = 24 * 3600 * 1000;
			List<AccountCash> list = accountCashDao.findAll();
			for (AccountCash ac : list) {
				if ((now - ac.getAddtime().getTime()) >= dayTime && ac.getStatus() == 1) {
					ac.setStatus(5);
					accountCashDao.update(ac);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-10 end
	// @Scheduled(cron = "0 0 23 * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void setWebInvestmentSum() {// 更新缓存，统计页面出借排行版，沪联贷在用。
		try {
			// v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 start
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			// 年、季度、月出借排行
			List<InvestSummary> listyear = accountDao.getInvestSummaryList(1);
			List<InvestSummary> listseason = accountDao.getInvestSummaryList(2);
			List<InvestSummary> listmonth = accountDao.getInvestSummaryList(3);
			List<InvestSummary> listall = accountDao.getInvestSummaryList(4);
			wac.getServletContext().setAttribute("listyear", listyear);
			wac.getServletContext().setAttribute("listseason", listseason);
			wac.getServletContext().setAttribute("listmonth", listmonth);
			wac.getServletContext().setAttribute("listall", listall);
			// v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 end
		} catch (Exception e) {
			logger.error(e);
		}
	}

	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
	/// @Scheduled(cron = "0 0 1 * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void creditForBorrow() {
		int quartz_status = Global.getInt("quartz_credit_borrow");
		if (quartz_status == 1) {
			userCreditService.madeRepay();
		}
	}
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end

	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start
	// @Scheduled(cron = "0 */5 * * * ?")// 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点0 */1 * * * ?
	public void awardForFriendBorrow() {
		int quartz_status = Global.getInt("quartz_award_borrow");
		if (quartz_status == 1) {
			borrowService.quzrtzAwardForFridenBorrow();
		}
	}
	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 end

	// v1.8.0.4_u1 TGPROJECT-241 qj 2014-05-06 start
	@Scheduled(cron = "0 0 1 * * ?") // 0 */5 * * * ? 5分钟 ，0 0 1 * * ? 凌晨一点
	public void setVipStatus() {
		List<UserCache> userlist = userCacheDao.getVipUserList();
		for (UserCache userCache : userlist) {
			Date vipTime = userCache.getVipEndTime();
			long time = vipTime.getTime() - System.currentTimeMillis();
			if (time < 24 * 3600 * 1000) {
				userCache.setVipStatus(0);
				userService.updateUserCache(userCache);
			}
		}
	}
	// v1.8.0.4_u1 TGPROJECT-241 qj 2014-05-06 end

	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-06 start
	// @Scheduled(cron = "0 */5 * * * ?")//5分钟调度
	public void sendMsgToContractUser() {
		userService.sendMsgToContractUser();
	}
	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-06 end

	// 1.8.0.4_u4 TGPROJECT-345 wujing dytz start

	/**
	 * 每隔30分钟调度查询一次现金奖励发放
	 */
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
	// @Scheduled(cron = "0 */10 * * * ?")//10分钟钟调度
	public void doRegisterReward() {
		DisruptorUtils.doRewardAsReadyMoney();
	}
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end

	/**
	 * 每50分钟(注册实名后，出借达到1000元，发放50奖励(出借人和推荐人都有))
	 */
	// @Scheduled(cron="0 */10 * * * ?") //每10分钟发放一次
	public void doRegisterIdentReward() {
		logger.debug("注册实名后，出借达到1000元，发放50奖励(出借人和推荐人都有)");
		DisruptorUtils.doRegisterAndIdentReward();
	}
	// 1.8.0.4_u4 TGPROJECT-345 wujing dytz end

	// TGPROJECT-351 自动扣款调度任务
	// @Scheduled(cron = "0 */5 * * * ?")//5分钟调度
	public void doAutoSequestrate() {
		logger.debug("进入系统自动扣款业务");
		DisruptorUtils.doAutoRepayWeb();
	}

	// v1.8.0.4_u4 TGPROJECT-371 qinjun 2014-07-22 start
	// 统计出借人信息
	// @Scheduled(cron = "0 0 2 * * ?")//凌晨2点调度
	public void quartzTenderStaticstics() {
		logger.debug("统计出借人信息");
		loanStatisticsService.quartzTenderStaticstics();
	}

	// 统计借款人信息
	// @Scheduled(cron = "0 0 3 * * ?")//凌晨3点调度
	public void quartzBorrowerStaticstics() {
		logger.debug("统计借款人信息");
		loanStatisticsService.quartzBorrowerStaticstics();
	}
	// v1.8.0.4_u4 TGPROJECT-371 qinjun 2014-07-22 end

	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 start
	// @Scheduled(cron = "0 */5 * * * ?")//5分钟调度
	public void noticeBorrowerRepay() {
		logger.debug("借款人还款提醒");
		borrowService.noticeBorrowerRepay();
	}
	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 end

}
