package com.liangyou.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.Rule;
import com.liangyou.domain.Site;
import com.liangyou.domain.User;
import com.liangyou.domain.Warrant;
import com.liangyou.domain.YwdUser;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.service.AccountService;
import com.liangyou.service.ArticleService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.ExperienceMoneyService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.service.WarrantService;
import com.liangyou.util.CookiesUtil;
import com.liangyou.util.NumberUtils;

@Namespace("/")
@ParentPackage("p2p-default")
@Result(name = "success", type = "ftl", location = "/index.html")
public class IndexAction extends BaseAction {

	private static final long serialVersionUID = -2578417106463783748L;
	private static Logger logger = Logger.getLogger(IndexAction.class);
	@Autowired
	BorrowService borrowService;
	@Autowired
	ArticleService articleService;
	@Autowired
	RuleService ruleService;
	@Autowired
	WarrantService warrantService;
	@Autowired
	AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	BorrowTenderDao borrowTenderDao;
	@Autowired
	ExperienceMoneyService experienceMoneyService;

	@Action("index")
	public String index() throws Exception {

		// -- start add by gy 2016-9-20 09:39:13 --
		// 只有线上并且启用亿起发推广的时候，才会记录最新来源Cookie
		if (isOnlineConfig() && isEnableYQFPromotion()) {
			String aid = paramString("aid");
			String channel = paramString("channel");
			String cid = paramString("cid");
			String wi = paramString("wi");
			String target = paramString("target");
			StringBuffer cookieValue = new StringBuffer();
			if (!StringUtils.isBlank(aid) && !StringUtils.isBlank(channel) && !StringUtils.isBlank(cid) && !StringUtils.isBlank(wi) && !StringUtils.isBlank(target)) {
				cookieValue.append(aid + "_").append(channel + "_").append(cid + "_").append(wi + "_").append(target);
				logger.debug("亿起发推广的cookieValue:" + cookieValue.toString());
				CookiesUtil.addCookie(response, "union_cookie", cookieValue.toString(), 30 * 24 * 3600);

				response.sendRedirect(target);
			}
		}
		// --end--

		Rule rule = ruleService.getRuleByNid("index_borrow_sort");

		long newBorrowId = Long.parseLong(Global.getValue("new_borrow_id"));
		long hotBorrowId = Long.parseLong(Global.getValue("hot_borrow_id"));
		long indexBorrow1Id = Long.parseLong(Global.getValue("index_borrow1_id"));
		long indexBorrow2Id = Long.parseLong(Global.getValue("index_borrow2_id"));
		long indexBorrow3Id = Long.parseLong(Global.getValue("index_borrow3_id"));
		long indexBorrow4Id = Long.parseLong(Global.getValue("index_borrow4_id"));

		// 优质标
		request.setAttribute("indexHot1", this.borrowService.getBorrow(indexBorrow1Id));
		request.setAttribute("indexHot2", this.borrowService.getBorrow(indexBorrow2Id));
		request.setAttribute("indexHot3", this.borrowService.getBorrow(indexBorrow3Id));
		request.setAttribute("indexHot4", this.borrowService.getBorrow(indexBorrow4Id));

		// 新手标
		List<Borrow> newBorrowlist = new ArrayList<Borrow>();
		newBorrowlist.add(this.borrowService.getBorrow(newBorrowId));
		request.setAttribute("newBorrowList", newBorrowlist);

		// 热门标
		List<Borrow> hotBorrowList = new ArrayList<Borrow>();
		hotBorrowList.add(this.borrowService.getBorrow(hotBorrowId));
		request.setAttribute("hotBorrowList", hotBorrowList);

		// 投资列表
		SearchParam param = new SearchParam();
		param.addOrFilter("status", 1, 6, 7, 8).addOrder(OrderType.ASC, "status").addOrder(OrderType.DESC, "id");
		PageDataList pl = borrowService.getList(param);
		List<Borrow> borrowList = pl != null ? pl.getList() : new ArrayList<Borrow>();
		if (!borrowList.isEmpty() && borrowList.size() >= 5) {
			borrowList = borrowList.subList(0, 5);
		}
		request.setAttribute("borrowList", borrowList);

		// 媒体报道 modify by lijing 2017-3-15 11:09:21
		Site mtbdSite = articleService.getSiteByCode("mtbd");
		PageDataList data = null;
		if (articleService.checkSiteCode("mtbd")) {// 如果没有当前栏目
			message("该栏目还未添加，请联系管理员！");
			return MSG;
		}
		data = articleService.getArticleList(mtbdSite, 1, 3);
		request.setAttribute("mtbdList", data.getList());

		// 网站公告 modify by lijing 2017-3-15 11:08:32
		Site webNoticeSite = articleService.getSiteByCode("web-notice");
		PageDataList data1 = null;
		if (articleService.checkSiteCode("web-notice")) {// 如果没有当前栏目
			message("该栏目还未添加，请联系管理员！");
			return MSG;
		}
		data1 = articleService.getArticleList(webNoticeSite, 1, 3);
		request.setAttribute("webNoticeList", data1.getList());

		// add by gy 2016-9-7 09:24:44 总投资金额
		double borrowSum = 0.0;
		borrowSum = borrowService.getBorrowSum();
		request.setAttribute("borrowSum", borrowSum + 2350000);

		// add by gy 2016-9-7 09:24:44 总投资产生利息
		double borrowSumInterest = 0.0;
		borrowSumInterest = borrowService.getBorrowSumInterest();
		request.setAttribute("borrowSumInterest", borrowSumInterest + 134822);

		// add by gy 2016-9-7 09:24:44 投标总人数
		// int sumTender = borrowTenderDao.sumTender();
		// request.setAttribute("sumTender", sumTender + 1880);

		// modify by gy 2016-9-22 前台展示投标总人数，为注册用户数
		int sumRegisteredUser = userService.sumRegisteredUser();
		request.setAttribute("sumTender", sumRegisteredUser + 1880);

		// 友情链接
		Site linksite = articleService.getSiteByCode("friend_links");
		PageDataList friendLinksList = null;
		if (linksite != null) {
			SearchParam flFirstParam = SearchParam.getInstance();
			flFirstParam.addParam("site", linksite);
			friendLinksList = articleService.getArticleList(flFirstParam);

		}
		// 媒体资料
		Site site = articleService.getSiteByCode("mediaReport");
		PageDataList mediaList = null;
		PageDataList mediaFirst = null;
		if (site != null) {
			SearchParam spFirstParam = SearchParam.getInstance();
			spFirstParam.addPage(0, 1);
			spFirstParam.addParam("site", site);
			spFirstParam.addOrFilter("flag", "t", "h");
			spFirstParam.addOrder(OrderType.DESC, "publish");
			mediaFirst = articleService.getArticleList(spFirstParam);

			SearchParam sp = SearchParam.getInstance();
			sp.addPage(0, 4);
			sp.addParam("site", site);
			sp.addParam("flag", Operator.NOTEQ, "t");
			sp.addParam("flag", Operator.NOTEQ, "h");
			sp.addOrder(OrderType.DESC, "publish");
			mediaList = articleService.getArticleList(sp);
		}
		// 行业资讯
		Site inforSiste = articleService.getSiteByCode("information");
		PageDataList infoList = null;
		PageDataList infoFirst = null;
		if (inforSiste != null) {
			SearchParam infoFirstParam = SearchParam.getInstance();
			infoFirstParam.addPage(0, 1);
			infoFirstParam.addParam("site", inforSiste);
			infoFirstParam.addOrFilter("flag", "t", "h");
			infoFirstParam.addOrder(OrderType.DESC, "publish");
			infoFirst = articleService.getArticleList(infoFirstParam);

			SearchParam infoParam = SearchParam.getInstance();
			infoParam.addPage(0, 8);
			infoParam.addParam("site", inforSiste);
			infoParam.addParam("flag", Operator.NOTEQ, "t");
			infoParam.addParam("flag", Operator.NOTEQ, "h");
			infoParam.addOrder(OrderType.DESC, "publish");
			infoList = articleService.getArticleList(infoParam);
		}
		// 头条公告
		Site noticeSiste = articleService.getSiteByCode("web-notice");
		SearchParam noticeParam = SearchParam.getInstance();
		if (noticeSiste != null) {
			noticeParam.addParam("site", noticeSiste);
		}
		noticeParam.addOrFilter("flag", "t", "h", "t,h");
		PageDataList noticeList = articleService.getArticleList(noticeParam);
		if (mediaFirst != null && mediaFirst.getList() != null && mediaFirst.getList().size() >= 1) {
			request.setAttribute("mediaFirst", mediaFirst.getList().get(0));
		}
		if (mediaList != null && mediaList.getList() != null) {
			request.setAttribute("mediaList", mediaList.getList());
		}

		if (infoFirst != null && infoFirst.getList() != null && infoFirst.getList().size() >= 1) {
			request.setAttribute("infoFirst", infoFirst.getList().get(0));
		}
		if (infoList != null && infoList.getList() != null) {
			request.setAttribute("infoList", infoList.getList());
		}
		if (noticeList != null && noticeList.getList() != null) {
			request.setAttribute("noticeList", noticeList.getList());
		}
		if (friendLinksList != null && friendLinksList.getList() != null) {
			request.setAttribute("friendLinksList", friendLinksList.getList());
		}
		Map<String, List> map = getIndexLists(rule);
		request.setAttribute("successList", map.get("successList"));
		request.setAttribute("recommendList", getrecommendBorrows());
		// 需优化---end
		User user = getSessionUser();
		if (user != null) {
			session.put(Constant.SESSION_USER, user);
		}
		SearchParam picParam = SearchParam.getInstance();
		picParam.addParam("status", 1).addParam("typeId", 1);
		picParam.addOrder(OrderType.DESC, "sort");//modify by lxm 2017-3-23 14:16:18
		PageDataList plist = articleService.getScrollPicList(picParam);
		request.setAttribute("scrollPic", plist.getList());

		// 投标动态
		request.setAttribute("newTenderList", map.get("newTenderList"));
		return SUCCESS;
	}

	private List<Borrow> getrecommendBorrows() {
		// 获取推荐标
		List<Borrow> recommendList = new ArrayList<Borrow>();
		SearchParam recommendParam = SearchParam.getInstance().addParam("recommendStatus", 1).addOrder(OrderType.DESC, "addtime").addParam("status", 1).addPage(1, 1);

		PageDataList<Borrow> listData = borrowService.getList(recommendParam);

		recommendList.addAll(listData.getList());

		// if(recommendList.size() <3 ){//推荐的不够，直接从完成里边取出来就可以啦。
		// //获取推荐标
		// SearchParam fullParam = SearchParam.getInstance()
		// .addOrFilter("status", 1)
		// .addOrder(OrderType.DESC, "apr")
		// .addPage(1,3-recommendList.size());
		// PageDataList<Borrow> list1 = borrowService.getList(fullParam);
		// recommendList.addAll(list1.getList());
		// }

		if (recommendList.size() < 1) {// 推荐的不够，直接从完成里边取出来就可以啦。
			// 获取推荐标
			SearchParam fullParam = SearchParam.getInstance().addOrFilter("status", 6, 7, 8).addOrder(OrderType.DESC, "addtime").addPage(1, 1 - recommendList.size());
			PageDataList<Borrow> list1 = borrowService.getList(fullParam);
			recommendList.addAll(list1.getList());
		}

		if (recommendList.size() < 1) {// 推荐的不够，直接从完成里边取出来就可以啦。
			// 获取推荐标
			SearchParam fullParam = SearchParam.getInstance().addOrFilter("status", 0).addOrder(OrderType.DESC, "addtime").addPage(1, 1 - recommendList.size());
			PageDataList<Borrow> list1 = borrowService.getList(fullParam);
			recommendList.addAll(list1.getList());
		}

		return recommendList;
	}

	@Action(value = "safe", results = { @Result(name = "success", type = "ftl", location = "/safe.html") })
	public String safe() throws Exception {
		SearchParam param = SearchParam.getInstance().addParam("status", 1);
		List safeList = borrowService.getList(param).getList();
		request.setAttribute("safeList", safeList);
		return SUCCESS;
	}

	private Map<String, List> getIndexLists(Rule rule) {
		Map<String, List> map = new HashMap<String, List>();
		List<Borrow> indexBorrowList = new ArrayList<Borrow>();
		int borrow_num = rule.getValueIntByKey("index_borrow_num");
		if (borrow_num == 0) {
			borrow_num = 5;
		}
		String[] sorts = rule.getValueStrByKey("sort").split(",");
		if (sorts != null) {
			int j = 1;
			for (int s = 0; s < sorts.length; s++) {
				SearchParam param = SearchParam.getInstance().addOrder(OrderType.DESC, "addtime").addPage(1);
				List list = new ArrayList();
				String sort = sorts[s];
				String typeRule = rule.getValueStrByKey("not_type");
				if (!typeRule.equals("") && typeRule != null) {
					int[] types = NumberUtils.getInts(typeRule.split(","));
					for (int i = 0; i < types.length; i++) {
						param.addParam("type", Operator.NOTEQ, types[i]);
					}
				}
				if (sort.equals("a")) {// 等待初审
					param.addParam("status", 0);
				} else if (sort.equals("b")) {// 正在招标
					param.addParam("account", Operator.PROPERTY_NOTEQ, "accountYes").addParam("status", 1).addParam("recommendStatus", 0);
				} else if (sort.equals("c")) {// 等待复审
					param.addParam("status", 1).addParam("account", Operator.PROPERTY_EQ, "accountYes");
				} else if (sort.equals("d")) {// 还款中
					param.addOrFilter("status", 6, 7);
				} else if (sort.equals("e")) {// 还款完成
					param.addOrFilter("status", 8);
				} else if (sort.equals("f")) {
					param.addParam("account", Operator.PROPERTY_NOTEQ, "accountYes").addParam("status", 1).addParam("recommendStatus", 1);
				}
				list = borrowService.getList(param).getList();// 招标
				for (int i = 0; i < list.size(); i++) {
					if (j > borrow_num) {
						break;
					}
					indexBorrowList.add((Borrow) list.get(i));
					++j;
				}
			}
		}
		// 获取成功案例数据
		SearchParam sParam = SearchParam.getInstance().addOrFilter("status", 6, 7, 8).addOrder(OrderType.DESC, "addtime").addPage(1);
		List successList = borrowService.getList(sParam).getList();
		map.put("successList", successList);
		map.put("indexList", indexBorrowList);

		// 新手标 暂时这么获取 以后 在这个位置加个方法

		if (indexBorrowList != null && indexBorrowList.size() > 0) {
			for (Borrow b : indexBorrowList) {
				if (b.getId() == 66) {
					List<Borrow> indexNew = new ArrayList<Borrow>();
					indexNew.add(indexBorrowList.get(0));
					map.put("indexNew", indexNew);
				}
			}
			// List<Borrow> indexNew = new ArrayList<Borrow>();
			// indexNew.add(indexBorrowList.get(0));
			// map.put("indexNew", indexNew);

		}
		SearchParam tParam = SearchParam.getInstance().addParam("status", 0).addOrFilter("borrow.status", 1, 3, 6, 7, 8).addOrder(OrderType.DESC, "addtime");
		List newTenderList = borrowService.getTenderList(tParam).getList();
		map.put("newTenderList", newTenderList);

		return map;
	}

	/*
	 * 视频播放
	 */
	@Action(value = "showVedio", results = { @Result(name = "success", type = "ftl", location = "/showVedio.html") })
	public String showVedio() {
		return SUCCESS;
	}

	@Action(value = "danBaoCompany", results = { @Result(name = "success", type = "ftl", location = "/danbao/company.html") })
	public String danBaoCompany() {
		long warrantid = paramLong("warrantId");
		Warrant warrant = warrantService.findWarrant(warrantid);
		if (warrant != null) {
			request.setAttribute("warrant", warrant);
			return "success";
		} else {
			message("获取担保机构异常");
			return MSG;
		}

	}

	@Action(value = "ywdRegiest")
	public String ywdRegiest() {
		for (int i = 1; i <= 100000; i++) {
			String username = createYwdUsername(i);
			String passwword = (new Random().nextInt(899999) + 100000) + "";
			YwdUser user = new YwdUser(username, passwword);
			userService.ywdRegiest(user);
		}
		return MSG;
	}

	private String createYwdUsername(int i) {
		if (i > 0 && i < 10) {
			return "yw00000" + i;
		} else if (i >= 10 && i < 100) {
			return "yw0000" + i;
		} else if (i >= 100 && i < 1000) {
			return "yw000" + i;
		} else if (i >= 1000 && i < 10000) {
			return "yw00" + i;
		} else if (i >= 10000 && i < 100000) {
			return "yw0" + i;
		} else {
			return "yw" + i;
		}

	}

	@Action(value = "checkValidImgToJSON")
	public String checkValidImgToJSON() {
		boolean isSuccess = true;
		try {
			checkValidImg();
		} catch (Exception e) {
			isSuccess = false;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", isSuccess + "");
		printJson(JSON.toJSONString(map));
		return null;
	}

	@Action(value = "Safety", results = { @Result(name = "success", type = "ftl", location = "/Safety.html") })
	public String anbao() {
		return SUCCESS;
	}

	@Action(value = "Gift", results = { @Result(name = "success", type = "ftl", location = "/Gift.html") })
	public String gift() {
		return SUCCESS;
	}

	@Action(value = "xxzd", results = { @Result(name = "success", type = "ftl", location = "/xxzd.html") })
	public String xxzd() {
		return SUCCESS;
	}

	@Action(value = "help", results = { @Result(name = "success", type = "ftl", location = "/help.html") })
	public String help() {
		return SUCCESS;
	}

	@Action(value = "help2", results = { @Result(name = "success", type = "ftl", location = "/help2.html") })
	public String help2() {
		return SUCCESS;
	}
}
