package com.liangyou.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowDetail;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.ExperienceMoneyService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.tool.Page;
import com.liangyou.tool.interest.EndInterestCalculator;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.tool.interest.MonthEqualCalculator;
import com.liangyou.tool.interest.MonthInterest;
import com.liangyou.tool.interest.MonthInterestCalculator;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Namespace("/invest")
@ParentPackage("p2p-default")
@Result(name = "success", type = "ftl", location = "/invest/index.html")
public class InvestAction extends BaseAction {

	private final static Logger logger = Logger.getLogger(InvestAction.class);

	@Autowired
	private BorrowService borrowService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private ExperienceMoneyService experienceMoneyService;

	/**
	 * 介绍页面
	 */
	@Action(value = "introduceInvest", results = { @Result(name = "success", type = "ftl", location = "/invest/introduceInvest.html") })
	public String introduceInvest() throws Exception {
		return "success";
	}

	@Action("index")
	public String index() throws Exception {
		Map<String, Object> newmap = new HashMap<String, Object>();
		int status = paramInt("status");
		int page = paramInt("page") == 0 ? 1 : paramInt("page");
		int pageNum = paramInt("pageNum") == 0 ? Page.ROWS : paramInt("pageNum");
		SearchParam param = null;
		param = SearchParam.getInstance().addPage(page, pageNum);
		if (status == 2) {
			message("非法访问", "/invest/index.html?status=1");
			return MSG;
		}
		if (status == 0) {
			status = 1;// 默认查询招标中
		}
		if (status == Constant.STATUS_complete) {// 成功案例状态 6，7， 8
			param.addParam("status", Operator.GTE, 6).addParam("status", Operator.LTE, 8);
		} else if (status == Constant.STATUS_REPAYING) {// 还款中 6,7
			param.addOrFilter("status", 6, 7);
		} else if (status == Constant.STATUS_ALLREPAIED) {// 还款成功
			param.addParam("status", 8);
		} else if (status == Constant.STATUS_REVIEW) {
			param.addParam("status", 3);
		} else if (status == 11) {// 查询正在招标、还款中、已还款
			param.addOrFilter("status", 1, 6, 7, 8).addOrder(OrderType.ASC, "status").addOrder(OrderType.DESC, "id");
		} else {// 招标中 status 1，且account!=accountYes
			param.addParam("account", Operator.PROPERTY_NOTEQ, "accountYes").addParam("status", status);
		}

		// 页面搜索框，过滤条件
		String title = paramString("title");// 借款标题
		String startdate = paramString("enddate1");// 筹款到期日期
		String enddate = paramString("enddate2");// 筹款到期日期

		String account = paramString("account");// 借款金额
		String rate = paramString("rate");// 年利率
		String timeLimit = paramString("timeLimit");// 借款期限
		int borrowType = paramInt("biaoType");// 标的类型
		int timeLimitDay = paramInt("timeLimitDay");// 天标期限
		String credit = paramString("credit");
		String award = paramString("award");
		// 标的类型
		if (borrowType != 0) {
			if (borrowType == 1) {// 天标
				param.addParam("isday", 1);
				// 天标期限
				if (timeLimitDay != 0) {
					param.addParam("timeLimitDay", Operator.EQ, timeLimitDay);
				}
			} else {
				param.addParam("type", borrowType);
				newmap.put("biaoType", borrowType);
			}
		}
		// 利率
		if (!rate.equals("all") && !StringUtils.isBlank(rate)) {
			String[] rateArgs = rate.split("-");
			if (rateArgs.length <= 1) {
				param.addParam("apr", Operator.EQ, NumberUtils.getInt(rateArgs[0]));
			} else if (rateArgs[1].equals(Constant.SEARCH_UP)) {
				param.addParam("apr", Operator.GTE, NumberUtils.getInt(rateArgs[0]));
			} else if (rateArgs[1].equals(Constant.SEARCH_DOWN)) {
				param.addParam("apr", Operator.LTE, NumberUtils.getInt(rateArgs[0]));
			} else {
				param.addParam("apr", Operator.GTE, NumberUtils.getInt(rateArgs[0])).addParam("apr", Operator.LTE, NumberUtils.getInt(rateArgs[1]));
			}
		}
		// 期限
		if (!timeLimit.equals("all") && !StringUtils.isBlank(timeLimit) && !timeLimit.equals("0") && borrowType != 1 && borrowType != 101) {
			String[] timeLimitArgs = timeLimit.split("-");
			if (timeLimitArgs.length <= 1) {
				param.addParam("timeLimit", Operator.EQ, NumberUtils.getInt(timeLimitArgs[0]));
			} else if (timeLimitArgs[1].equals(Constant.SEARCH_UP)) {
				param.addParam("timeLimit", Operator.GTE, NumberUtils.getInt(timeLimitArgs[0]));
			} else if (timeLimitArgs[1].equals(Constant.SEARCH_DOWN)) {
				param.addParam("timeLimit", Operator.LTE, NumberUtils.getInt(timeLimitArgs[0]));
			} else {
				param.addParam("timeLimit", Operator.GTE, NumberUtils.getInt(timeLimitArgs[0])).addParam("timeLimit", Operator.LTE, NumberUtils.getInt(timeLimitArgs[1]));
			}
			param.addParam("isday", 0);
			param.addParam("type", Operator.NOTEQ, 101);
		}
		// 信用积分
		if (!credit.equals("all") && !StringUtils.isBlank(credit)) {
			String[] creditArgs = credit.split("-");
			if (creditArgs.length <= 1) {
				param.addParam("user.credit.value", Operator.EQ, NumberUtils.getInt(creditArgs[0]));
			} else if (creditArgs[1].equals(Constant.SEARCH_UP)) {
				param.addParam("user.credit.value", Operator.GT, NumberUtils.getInt(creditArgs[0]));
			} else if (creditArgs[1].equals(Constant.SEARCH_DOWN)) {
				param.addParam("user.credit.value", Operator.LT, NumberUtils.getInt(creditArgs[0]));
			} else {
				param.addParam("user.credit.value", Operator.GT, NumberUtils.getInt(creditArgs[0])).addParam("user.credit.value", Operator.LTE, NumberUtils.getInt(creditArgs[1]));
			}
		}

		// 奖励
		if (!award.equals("all") && !StringUtils.isBlank(award)) {
			if (award.equals("yes")) {
				param.addParam("award", Operator.GT, 0);
			} else if (award.equals("no")) {
				param.addParam("award", Operator.EQ, 0);
			}
		}
		// 标题
		if (!title.equals("all") && !StringUtils.isBlank(title)) {
			param.addParam("name", Operator.LIKE, title);
		}
		// 筹款到期日期
		if (!StringUtils.isBlank(startdate)) {
			startdate += " 00:00:00";
			param.addParam("borrowProperty.endTime", Operator.GTE, DateUtils.getDate2(startdate));
		}
		if (!StringUtils.isBlank(enddate)) {
			enddate += " 23:59:59";
			param.addParam("borrowProperty.endTime", Operator.LTE, DateUtils.getDate2(enddate));
		}
		// 排序
		// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 start
		int order = paramInt("order");
		param.addOrder(order);
		// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 end

		PageDataList pl = borrowService.getList(param);
		// v1.8.0.4_u4 TGPROJECT-368 2014-07-16 qinjun start
		newmap.put("naccount", account);// 担保标查询使用
		newmap.put("status", status);
		// v1.8.0.4_u4 TGPROJECT-368 2014-07-16 qinjun end
		param.addParam("timeLimit", timeLimit);
		param.addParam("rate", rate);
		setPageAttribute(pl, param, newmap);

		// 招标右侧的栏目
		SearchParam paramr = SearchParam.getInstance();
		paramr.addOrFilter("status", 6, 7, 8);
		paramr.addOrder(OrderType.DESC, "account");
		paramr.addPage(1, 5);
		PageDataList pr = borrowService.getList(paramr);
		request.setAttribute("rList", pr.getList());
		// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 start
		request.setAttribute("orderstr", getOrderStr());
		// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 end
		List<Borrow> unpubliclist = borrowService.getBorrowByStatus(0);
		request.setAttribute("unpubliclist", unpubliclist);

		// 总投资金额
		double borrowSum = 0.0;
		borrowSum = borrowService.getBorrowSum();
		// modify by guanyang 20160902 我要出借页面 累计成交总金额
		request.setAttribute("borrowSum", borrowSum + 2350000);

		// 总投资产生利息
		double borrowSumInterest = 0.0;
		borrowSumInterest = borrowService.getBorrowSumInterest();
		// modify by guanyang 20160902 我要出借页面 为用户累计赚取
		request.setAttribute("borrowSumInterest", borrowSumInterest + 134822);

		// modify by guanyang 20160902 我要出借页面 已投标总人数
		// int sumTender = borrowTenderDao.sumTender();
		// request.setAttribute("sumTender", sumTender + 1880);

		// modify by gy 2016-9-22 前台展示投标总人数，为注册用户数
		int sumRegisteredUser = userService.sumRegisteredUser();
		request.setAttribute("sumTender", sumRegisteredUser + 1880);

		return SUCCESS;
	}

	// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 start
	public String getOrderStr() {
		StringBuffer sb = new StringBuffer("");
		sb.append(getCurOrderStr(1, "金额"));
		sb.append(getCurOrderStr(2, "利率"));
		// sb.append(getCurOrderStr(3,"进度"));
		sb.append(getCurOrderStr(4, "信用"));
		return sb.toString();
	}

	public String getCurOrderStr(int ordertype, String text) {
		String timeLimit = paramString("timeLimit");// 借款期限
		int borrowType = paramInt("biaoType");// 标的类型
		int status = paramInt("status");
		int order = paramInt("order");
		int pageNum = paramInt("pageNum") == 0 ? Page.ROWS : paramInt("pageNum");
		StringBuffer sb = new StringBuffer();
		sb.append("<span><a  class=\'searchbtn'\' href=\"?status=" + status + "&timeLimit=" + timeLimit + "&biaoType=" + borrowType + "&pageNum=" + pageNum);
		if (Math.abs(order) == ordertype) {
			if (order > 0) {
				sb.append("&order=" + (-ordertype) + "\">");
				sb.append("<font color=\"#FF0000\">" + text + "↑</font>");
			} else {
				sb.append("&order=" + (ordertype) + "\">");
				sb.append("<font color=\"#FF0000\">" + text + "↓</font>");
			}
		} else {
			sb.append("&order=" + (ordertype) + "\">");
			sb.append(text);
		}
		sb.append("</a> </span>");
		return sb.toString();
	}

	// v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 end

	@Action(value = "detail", results = { @Result(name = "success", type = "ftl", location = "/invest/detail.html"),
			// add by gy 2016-10-14 18:07:27 增加体验金详情页面返回
			@Result(name = "detailExperienceBorrow", type = "ftl", location = "/experienceMoney/experienceMoneyDetail.html") }
	// edit by gy 2016-8-31 16:44:57 修改标的详情，不登录客户也可以查看
	// interceptorRefs={@InterceptorRef(value="mydefault")}
	)
	public String detail() throws Exception {
		long id = paramLong("borrowid");// 标的ID
		Borrow b = borrowService.getBorrow(id);// 根据ID查到此标
		if (b == null) {
			throw new BussinessException("您查的信息不存在。");
		}

		User user1 = getSessionUser();// 当前登录用户
		if (user1 == null || (b.getUser().getUserId() != user1.getUserId())) {// 未登录或该用户不是此标的发标人
			String[] notView = Global.getValue("not_view_borrow_status").split(",");// 查看标的详情，过滤不可见标状态 0,2,4,5,49,59,-1
			for (String string : notView) {
				if (string.equals(b.getStatus() + "")) {
					throw new BussinessException("尚未初审通过的标只有此标发标人才能查看");
				}
			}
		}

		if (b.getIsAssignment() == 1) { // 债权转让标志
			BorrowTender tender = borrowService.getAssignMentTenderByBorrowId(id);
			request.setAttribute("oldBorrowId", tender.getBorrow().getId());
		}
		request.setAttribute("borrow", b);

		User user = userService.getUserById(b.getUser().getUserId());

		user.setCredit(user.getCredit());// 设置用户的总信用额度
		user.getUserinfo().getCity();// 发标人用户城市

		request.setAttribute("user", user);
		CreditRank creditRank = userService.getUserCreditRankByJiFen(user.getCredit().getValue());

		// 账户详情/ 借款详情 投资详情
		UserAccountSummary summary = accountService.getUserAccountSummary(user.getUserId());

		request.setAttribute("creditRank", creditRank);
		request.setAttribute("summary", summary);
		if (b.getIsAssignment() == 1) { // 债权转让标原tender
			BorrowTender assignMentTender = borrowService.getAssignMentTenderByBorrowId(b.getId());
			request.setAttribute("assignMentTender", assignMentTender);
		}

		// 计算用户剩余投资金额
		if (getSessionUser() != null) {// 用户需要登陆才能查看。
			User sessionUser = userService.getUserById(getSessionUser().getUserId());
			session.put(Constant.SESSION_USER, sessionUser);
			double accountTender = Global.getDouble("tender_account");// 投标总额
			if (accountTender > 0) {
				double sumTenderAccount = borrowService.sumTenderWaitAccount(sessionUser.getUserId());// 用户已投总额
				request.setAttribute("waitTenderAccount", NumberUtils.format2Str(accountTender - sumTenderAccount));//用户剩余投资金额
			}
		}
		madeLeaseTender(b);

		// v1.8.0.3_u3 TGPROJECT-335 wuing 2014-06-17 start
		List<BorrowDetail> borrowDetailList = borrowService.getBorrowDetailListByBorrowId(b.getId()); // 获取借款信息的borrowDetail信息
		request.setAttribute("borrowDetailList", borrowDetailList);
		// v1.8.0.3_u3 TGPROJECT-335 wujing 2014-06-17 end
		// v1.8.0.3_u3 TGPROJECT-358 qinjun 2014-07-07 start
		repaymentDetail(b);
		// v1.8.0.3_u3 TGPROJECT-358 qinjun 2014-07-07 end

		// add by gy 2016-10-14 18:06:21 begin
		SearchParam param = new SearchParam();
		ExperienceMoney em = new ExperienceMoney();
		if (this.getSessionUser() != null)
			param.addParam("user", this.getSessionUser());
		if (isEnableExperienceMoney()) {
			if (b.getType() == Constant.TYPE_EXPERIENCE) { // 如果是体验金类型标的， 则打开体验金标的详情页
				param.addParam("useStatus", 0);
				em = this.experienceMoneyService.getExperenceMoney(param);
				request.setAttribute("em", em);
				return "detailExperienceBorrow";
			} else { // 如果不是体验金标的，则打开正常标的详情页，并添加体验金信息
				if (this.getSessionUser() != null)
					em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("interestIncomeStatus", 1).addParam("user", this.getSessionUser()));
				request.setAttribute("em", em);
			}
		}
		// add by gy 2016-10-14 18:06:21 end

		return SUCCESS;
	}

	// v1.8.0.3_u3 TGPROJECT-358 qinjun 2014-07-07 start
	private void repaymentDetail(Borrow borrow) {
		double account = borrow.getAccount();// 投标金额
		double lilv = borrow.getApr();// 年利率
		int times = borrow.getTimeLimit();// 借款期限 月标
		int time_limit_day = borrow.getTimeLimitDay();// 借款期限 天标
		String style = borrow.getStyle();// 还款方式
		if (borrow.getIsday() == 1) {// 天标
			// 计算每期还款信息
			InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, time_limit_day);
			// 循环每期的利息
			ic.each();
			request.setAttribute("ic", ic);
		} else {// 月标
			if (style.equals("0")) {// 等额本金
				InterestCalculator ic = new MonthEqualCalculator(account, lilv / 100, times);
				ic.each();
				request.setAttribute("ic", ic);
			} else if (style.equals("3")) {// 按月付息到期还本
				InterestCalculator ic = new MonthInterestCalculator(account, lilv / 100, times);
				ic.each();
				request.setAttribute("ic", ic);
			} else if (style.equals("2")) {// 一次性还本付息
				InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, times, InterestCalculator.TYPE_MONTH_END);
				ic.each();
				request.setAttribute("ic", ic);
			}
		}
		request.setAttribute("account", account);
		request.setAttribute("lilv", lilv);
		request.setAttribute("times", times);
		request.setAttribute("time_limit_day", time_limit_day);
	}

	// v1.8.0.3_u3 TGPROJECT-358 qinjun 2014-07-07 end
	private double madeLeaseTender(Borrow borrow) {
		Rule rule = ruleService.getRuleByNid("tender_account_rule");// 投标限制规则
																	// 投标金额限制
		if (rule != null && rule.getStatus() == 1) {// getStatus 1开启，0停用
			int leaseRate = rule.getValueIntByKey("lease_status");
			if (leaseRate == 1) {
				double rate = rule.getValueDoubleByKey("tender_least_rate");// 最低投标比例
				double leaseMoney = rate * borrow.getAccount();// 最低投标金额
				double leaseTenderMoney = (borrow.getAccount() - borrow.getAccountYes() - leaseMoney) < leaseMoney ? (borrow.getAccount() - borrow.getAccountYes()) : leaseMoney; // 若剩余标的金额小于最低投标金额的话，最后一个人的投标金额为：最低投标金额+剩余投标金额。否则为最低投标金额
				request.setAttribute("leaseTenderMoney", leaseTenderMoney);
				// 计算成立时间
				Date date = borrow.getBorrowProperty().getEndTime();
				date = DateUtils.rollDay(date, 1);
				request.setAttribute("update", DateUtils.dateStr2(date));
			}

		}
		return 0;
	}

	// 动态利息计算
	@Action(value = "interest")
	public void interest() {
		long id = paramLong("borrowid");// 标的ID
		Borrow b = borrowService.getBorrow(id);// 根据ID查到此标
		double account = paramDouble("money");// 用户输入的投标金额
		double lilv = b.getApr();// 年利率
		int isDay = b.getIsday();// 1天标，0月标
		int time_limit_day = b.getTimeLimitDay();// 天标期限
		String TenderType = b.getStyle();// 获得利息方式
		int times = b.getTimeLimit();// 月标期限
		double interest = 0.0;// 利息
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (isDay == 1) {// 天标
			InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, time_limit_day);
			// 循环每期的利息
			List<MonthInterest> list = ic.each();
			for (MonthInterest i : list) {
				interest += i.getInterest();
			}
			double newInterest = NumberUtils.format2(interest);
			dataMap.put("interest", newInterest);
		} else {// 月标
			if (TenderType.equals("0")) {// 等额本金
				InterestCalculator ic = new MonthEqualCalculator(account, lilv / 100, times);

				List<MonthInterest> list = ic.each();
				for (MonthInterest i : list) {
					interest += i.getInterest();
				}
				double newInterest = NumberUtils.format2(interest);
				dataMap.put("interest", newInterest);
			} else if (TenderType.equals("3")) {// 按月付息到期还本
				InterestCalculator ic = new MonthInterestCalculator(account, lilv / 100, times);
				List<MonthInterest> list = ic.each();
				for (MonthInterest i : list) {
					interest += i.getInterest();
				}
				double newInterest = NumberUtils.format2(interest);
				dataMap.put("interest", newInterest);
			} else if (TenderType.equals("2")) {// 一次性还本付息
				InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, times, InterestCalculator.TYPE_MONTH_END);
				List<MonthInterest> list = ic.each();
				for (MonthInterest i : list) {
					interest += i.getInterest();
				}
				double newInterest = NumberUtils.format2(interest);
				dataMap.put("interest", newInterest);
			}
		}

		Json json = new Json(Json.CODE_OK, true, dataMap);
		super.writeJson(json);
	}

	@Action(value = "detailTenderForJson", results = { @Result(name = "success", type = "ftl", location = "/invest/detail.html") }
	// edit by gy 2016-9-1 14:30:34 投标记录，未登录用户查看权限放开
	// interceptorRefs={@InterceptorRef(value="mydefault")}
	)
	public String detailTenderForJson() throws Exception {
		int page = paramInt("page");
		long id = paramLong("borrowid");
		SearchParam param = SearchParam.getInstance().addParam("borrow", new Borrow(id)).addPage(page);
		PageDataList data = borrowService.getTenderList(param);
		List<BorrowTender> list = data.getList();
		for (BorrowTender bt : list) {
			User user = new User();
			// user.setUsername(bt.getUser().getUsername());
			user.setUsername(StringUtils.hideModdileChar(bt.getUser().getUsername(), 6, 1));
			bt.setUser(user);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (list == null || list.size() < 1) {
			map.put("msg", null);
		} else {
			map.put("msg", "success");
			map.put("data", data);
		}
		printJson(getStringOfJpaMap(map));
		return null;
	}
}
