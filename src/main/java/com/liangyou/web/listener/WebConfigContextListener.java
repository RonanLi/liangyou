package com.liangyou.web.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowConfigDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.CreditRankDao;
import com.liangyou.dao.StarRankDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.StarRank;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SystemInfo;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.account.InvestSummary;
import com.liangyou.quartz.apiAccount.ApiAccountJobQueue;
import com.liangyou.quartz.batchRecharge.BatchReachargeJobQueue;
import com.liangyou.quartz.cash.CashJobQueue;
import com.liangyou.quartz.flowRepay.FlowRepayJobQueue;
import com.liangyou.quartz.notice.NoticeJobQueue;
import com.liangyou.quartz.other.OtherJobQueue;
import com.liangyou.quartz.payOut.PayOutJobQueue;
import com.liangyou.quartz.repay.RepayJobQueue;
import com.liangyou.quartz.tender.TenderJobQueue;
import com.liangyou.quartz.verifyBorrow.VerifyBorrowJobQueue;
import com.liangyou.quartz.verifyFullBorrow.VerifyFullBorrowJobQueue;
import com.liangyou.quartz.webSiteRepay.WebSiteRepayJobQueue;
import com.liangyou.service.AccountService;
import com.liangyou.service.ArticleService;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.SystemService;
import com.liangyou.service.UserService;
import com.liangyou.service.WebPaidService;
import com.liangyou.util.NumberUtils;

public class WebConfigContextListener implements ServletContextListener {
	private static Logger logger=Logger.getLogger(WebConfigContextListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		//这个方法在container卸载整个web应用时调用,运行在该web应用中servlet和filter的destroy()方法之后
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//这个方法在container初始化整个web应用时调用,运行在该web应用中servlet和filter初始化之前
		ServletContext context=event.getServletContext();
		ApplicationContext ctx= WebApplicationContextUtils.getRequiredWebApplicationContext(context);

		SystemService systemService=(SystemService)ctx.getBean("systemService");
		BorrowConfigDao borrowConfigDao=(BorrowConfigDao)ctx.getBean("borrowConfigDao");
		AutoService autoService=(AutoService)ctx.getBean("autoService");
		UserDao userDao=(UserDao)ctx.getBean("userDao");
		CreditRankDao creditRankDao=(CreditRankDao)ctx.getBean("creditRankDao");
		StarRankDao   starRankDao = (StarRankDao)ctx.getBean("starRankDao");
		BorrowDao   borrowDao = (BorrowDao)ctx.getBean("borrowDao");
		BorrowTenderDao   borrowTenderDao = (BorrowTenderDao)ctx.getBean("borrowTenderDao");
		AccountLogDao   accountLogDao = (AccountLogDao)ctx.getBean("accountLogDao");
		//v1.8.0.4 TGPROJECT-144 lx 2014-04-21 start
		ArticleService articleService=(ArticleService)ctx.getBean("articleService");
		//v1.8.0.4 TGPROJECT-144 lx 2014-04-21 
		AccountDao accountDao=(AccountDao)ctx.getBean("accountDao");
		
		//disruptor 替换成队列，service 初始化
		UserService userService=(UserService)ctx.getBean("userService");
		AccountService accountService=(AccountService)ctx.getBean("accountService");
		MsgRecordService msgRecordService=(MsgRecordService)ctx.getBean("msgRecordService");
		RewardExtendService rewardExtendService=(RewardExtendService)ctx.getBean("rewardExtendService");
		WebPaidService webPaidService=(WebPaidService)ctx.getBean("webPaidService");
		BorrowService borrowService=(BorrowService)ctx.getBean("borrowService");
		
		List list=borrowConfigDao.findAll();
		Map map=new HashMap();
		for(int i=0;i<list.size();i++){
			BorrowConfig config=(BorrowConfig)list.get(i);
			map.put(config.getId(), config);
		}
		Global.BORROWCONFIG=map;
		
		SystemInfo info = systemService.getSystemInfo();
		Global.SYSTEMINFO = info;
		setWebConfig(context,info);
		//v1.8.0.4 TGPROJECT-111 lx 2014-04-21 start
		//检查系统、数据库版本是否一致！
		checkVersion();
		//v1.8.0.4 TGPROJECT-111 lx 2014-04-21 end
		//获取所有客服
		List allKefuList = userDao.getAllKefu();
		Global.ALLKEFU = allKefuList;
		context.setAttribute("allkefu",Global.ALLKEFU);
		//获取信用等级信息
		List<CreditRank> allCreditRank = creditRankDao.findAll();
		Global.ALLCREDITRANK = allCreditRank;
		List<StarRank> allStarRank = starRankDao.findAll();
		Global.ALLSTARRANK = allStarRank;
		//网站投资统计
		//网站交易总额
		double successSumAccount = borrowDao.getSuccessBorrowSumAccount();
		int sumTender = borrowTenderDao.sumTender();
		double maxApr = borrowDao.getMaxAprBorrow();
		double riskreserve_fee = accountLogDao.getSumRiskreserveFee();
		//v1.8.0.4 TGPROJECT-63 lx 2014-04-16 start
		double sumInterest=borrowTenderDao.sumInterest();
		context.setAttribute("sumInterest", sumInterest);//网站投资人的收益
		//v1.8.0.4 TGPROJECT-63 lx 2014-04-16 end
		context.setAttribute("sumBorrowAccount",NumberUtils.getLong((int)successSumAccount+""));//网站借款总额
		context.setAttribute("sumTender", sumTender);//网站投资总人数
		context.setAttribute("maxApr", maxApr);//网站最高年利率
		context.setAttribute("riskreserve_fee", riskreserve_fee);
		//v1.8.0.4 TGPROJECT-144 lx 2014-04-21 start
		// 合作伙伴
		SearchParam  picParam = SearchParam.getInstance();
		picParam.addParam("status", 1).addParam("typeId", 2);
		picParam.addOrder(OrderType.ASC, "sort");
		picParam.addPage(0, 100);
		PageDataList plist = articleService.getScrollPicList(picParam);
		List cooperativePartnerPic= plist.getList();
		context.setAttribute("cooperativePartnerPic", cooperativePartnerPic);

		// 友情链接
		SearchParam  picParam1 = SearchParam.getInstance();
		picParam1.addParam("status", 1).addParam("typeId", 3);
		picParam1.addOrder(OrderType.ASC, "sort");
		PageDataList plist1 = articleService.getScrollPicList(picParam1);
		List linksPic= plist1.getList();
		context.setAttribute("linksPic", linksPic);
		//v1.8.0.4 TGPROJECT-144 lx 2014-04-21 end
		//v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 start
		List<InvestSummary> listyear=accountDao.getInvestSummaryList(1);
		List<InvestSummary> listseason=accountDao.getInvestSummaryList(2);
		List<InvestSummary> listmonth=accountDao.getInvestSummaryList(3);
		List<InvestSummary> listall=accountDao.getInvestSummaryList(4);
		context.setAttribute("listyear", listyear);
		context.setAttribute("listseason", listseason);
		context.setAttribute("listmonth", listmonth);
		context.setAttribute("listall", listall);
		//v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 end
		
		//初始化业务处理队列
		ApiAccountJobQueue.init(userService, accountService);
		CashJobQueue.init(accountService, userService);
		FlowRepayJobQueue.init(autoService);
		NoticeJobQueue.init(msgRecordService);
		OtherJobQueue.init(autoService, rewardExtendService, webPaidService);
		PayOutJobQueue.init(accountService, userService);
		RepayJobQueue.init(autoService, borrowService, webPaidService);
		TenderJobQueue.init(borrowService);
		BatchReachargeJobQueue.init(autoService);
		//VerifyBorrowJobQueue,暂时不使用
		VerifyFullBorrowJobQueue.init(autoService);
		WebSiteRepayJobQueue.init(borrowService,rewardExtendService);
		
	}

	
	private void setWebConfig(ServletContext context, SystemInfo info){

		String[] webinfo=Global.SYSTEMNAME;
		for(String s:webinfo){
			logger.info(s+":"+info.getValue(s));
			context.setAttribute(s, info.getValue(s));
		}
		context.setAttribute("webroot", context.getContextPath());
	}
	//v1.8.0.4 TGPROJECT-111 lx 2014-04-21 start
	/**
	 * 校验系统版本
	 */
	public void checkVersion(){
		String dbVersion=Global.getVersion();
		String sysVersion=Global.VERSION;
		logger.info("数据库版本："+dbVersion);
		logger.info("系统版本:"+sysVersion);
		if(!Global.getVersion().equals(Global.VERSION)){
			throw new RuntimeException("数据库版本与系统版本不一致，请更新数据库！");
		}
	}
	//v1.8.0.4 TGPROJECT-111 lx 2014-04-21 end
}
