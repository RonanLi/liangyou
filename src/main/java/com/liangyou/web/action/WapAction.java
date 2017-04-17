package com.liangyou.web.action;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.*;
import com.liangyou.model.OrderFilter;
import com.liangyou.service.ArticleService;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.service.*;
import com.liangyou.util.CookiesUtil;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WAP APP 跳转Action
 *
 * @author lijing
 */
@Namespace("/wap")
@ParentPackage("p2p-json")
public class WapAction extends BaseAction {
    private static Logger logger = Logger.getLogger(WapAction.class);

    @Autowired
    private BorrowService borrowService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ExperienceMoneyService experienceMoneyService;
    @Autowired
    private ArticleService articleService;
    /**
     * 首页
     *
     * @return
     */
    @Action(value = "index", results = {@Result(name = "success", type = "ftl", location = "/wap/index.html")})
    public String index() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        request.setAttribute("borrowSum", borrowService.getBorrowSum() + 2350000);// 累计成交额
        request.setAttribute("borrowList", borrowService.getWapIndexBorrowList()); // 投资列表
        //request.setAttribute("borrowList", borrowService.getList(SearchParam.getInstance().addOrFilter("status", 1, 6, 7, 8).addOrder(OrderFilter.OrderType.ASC, "status").addOrder(OrderFilter.OrderType.DESC, "type").addPage(1, 4)).getList()); // 投资列表

        //add by lxm WAP端首页轮播图 2017-3-27 15:54:09
        SearchParam picParam = SearchParam.getInstance();
 		picParam.addParam("status", 1).addParam("typeId", 4);
 		picParam.addOrder(OrderType.DESC, "sort");
 		PageDataList plist = articleService.getScrollPicList(picParam);
 		request.setAttribute("wapScrollPic", plist.getList());

        return SUCCESS;
    }

    /**
     * 退出登录按钮
     *
     * @return
     */
    @Action(value = "logout", results = {@Result(name = "success", type = "ftl", location = "/wap/aggregationPage.html")})
    public String logout() {
        Map session = (Map) ActionContext.getContext().getSession();
        session.put("session_user", null);
        session.put(Constant.TEMP_IDENTIFY_USER, null);
        CookiesUtil.addCookie(response, "jforumUserInfo", "", 0);
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 安全保障
     *
     * @return
     */
    @Action(value = "security", results = {@Result(name = "success", type = "ftl", location = "/wap/security.html")})
    public String security() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 最新活动
     *
     * @return
     */
    @Action(value = "activity", results = {@Result(name = "success", type = "ftl", location = "/wap/activity.html")})
    public String activity() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 邀请有礼
     *
     * @return
     */
    @Action(value = "invitedPresent", results = {@Result(name = "success", type = "ftl", location = "/wap/invitedPresent.html")})
    public String invitedPresent() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 投资列表
     *
     * @return
     */
    @Action(value = "listbids", results = {@Result(name = "success", type = "ftl", location = "/wap/listbids.html")})
    public String listbids() {
        logger.info("wap访问-标的列表开始！");
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        SearchParam param = new SearchParam();
        param.addOrFilter("status", 1, 6, 7, 8).addPage(1, 10);  //addParam("account", SearchFilter.Operator.PROPERTY_NOTEQ, "accountYes").
//        PageDataList pl = borrowService.getList(param);
        PageDataList pl = borrowService.findWapBorrowPageListBySql(param);
        List<Borrow> borrowList = pl != null ? pl.getList() : new ArrayList<Borrow>();
        logger.info("wap访问-标的列表：当前页标的个数：" + borrowList.size());
        request.setAttribute("borrowList", borrowList);
        logger.info("wap访问-标的列表结束！");
        return SUCCESS;
    }

    /**
     * 投资项目详情页
     *
     * @return
     */
    @Action(value = "bid_details", results = {@Result(name = "success", type = "ftl", location = "/wap/bid-details.html")})
    public String bid_details() {
        logger.info("wap访问-标的详情开始！");
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        Borrow borrow = borrowService.getBorrow(Long.parseLong(paramString("id")));
        logger.info("wap访问-标的详情： 标ID：" + borrow != null ? borrow.getId() : "标不存在！");
        ExperienceMoney em = null;
        request.setAttribute("borrow", borrow);
        if (getSessionUser() != null) {// 用户需要登陆才能查看。
            User sessionUser = userService.getUserById(getSessionUser().getUserId());
            session.put(Constant.SESSION_USER, sessionUser);
            if (borrow.getType() == Constant.TYPE_EXPERIENCE) {
                em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("useStatus", 0).addParam("user", getSessionUser()));
            } else {
                em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("interestIncomeStatus", 1).addParam("interestUseStatus", 0).addParam("user", this.getSessionUser()));
            }
        }
        request.setAttribute("em", em);
        logger.info("wap访问-标的详情开始！");

        return SUCCESS;
    }
    /**
    * 着陆页
    *
    * @return
    */
    @Action(value = "registerShow", results = {@Result(name = "success", type = "ftl", location = "/wap/registerShow.html")})
    public String registerShow() {
        // 新手标
        List<Borrow> newBorrowlist = new ArrayList<Borrow>();
        newBorrowlist.add(this.borrowService.getBorrow(Long.parseLong(Global.getValue("new_borrow_id"))));
        request.setAttribute("newBorrowList", newBorrowlist);

        // 热门标
        List<Borrow> hotBorrowList = new ArrayList<Borrow>();
        hotBorrowList.add(this.borrowService.getBorrow(Long.parseLong(Global.getValue("hot_borrow_id"))));
        request.setAttribute("hotBorrowList", hotBorrowList);

        request.setAttribute("borrowList", borrowService.getWapIndexBorrowList()); // 投资列表

        request.setAttribute("channel", paramString("channel"));
        request.setAttribute("code",    paramString("code"));
        request.setAttribute("test",    paramString("test"));

        return SUCCESS;
    }
    /**
     * 登录页面      router=login
     * 重置密码页面  router=forget
     * 注册账户页面  router=register
     *
     * @return
     */
    @Action(value = "aggregationPage", results = {@Result(name = "success", type = "ftl", location = "/wap/aggregationPage.html")})
    public String aggregationPage() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 我的账户
     *
     * @return
     */
    @Action(value = "Innerpage", results = {
            @Result(name = "success", type = "ftl", location = "/wap/Innerpage.html")
    })
    public String Innerpage() throws Exception {
        User sessionUser = getSessionUser();
        if (sessionUser != null) {
            request.setAttribute("usersession", "true");
        } else {
            request.setAttribute("usersession", "false");
            response.sendRedirect(Global.getString("weburl") + "/wap/aggregationPage.html?router=login");
            return null;
        }

        String resultFlag = paramString("resultFlag");
        if (!StringUtils.isBlank(resultFlag)) {
            request.setAttribute("resultFlag", resultFlag);
        }

        long user_id = sessionUser.getUserId();
        User user = userService.getUserById(user_id);  //查找用户的信息。
        Account account = user.getAccount(); // 账户信息表
        UserAccountSummary summary = accountService.getUserAccountSummary(user_id); // 账户资金详情
        int unRead = messageService.getUnreadMessageCount(user); // 未读消息

//        checkBank(user);// 绑卡校验
        SearchParam drawBankParam = apiService.getdrawBankParam();// 第三方银行定义
        List drawList = null;
        switch (Global.getInt("api_code")) {
            case 3:// 双乾
                drawList = accountService.getDrawBankMmmBySearchParam(drawBankParam);
                request.setAttribute("drawBankList", drawList);
                break;
            default:
                break;
        }

        List<AccountBank> banklist = accountService.getBankLists(user.getUserId()); // 获取用户可用的银行卡

        if (isOnlineConfig()) {// 线上
            request.setAttribute("mmmIndex_url", Global.getValue("mmm_service_https") + Global.getValue("mmm_service_url"));
        } else {
            request.setAttribute("mmmIndex_url", Global.getValue("mmm_service_test_url"));
        }

        // 启用体验金
        logger.info("wap端--查询是否启用体验金功能：" + isEnableExperienceMoney());
//        if (isEnableExperienceMoney()) {
        ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user));
        if (em != null) {
            request.setAttribute("em", em);
            if (em.getUseStatus() == 0 && em.getAddTime() != null) {
                request.setAttribute("experienceMoneyDeadline", DateUtils.dateStr4(DateUtils.rollDay(em.getAddTime(), em.getTimeLimit())));
            }

            if (em.getInterestIncomeStatus() == 1 && em.getInterestUseStatus() == 0 && em.getInterestIncomeTime() != null) {
                request.setAttribute("interestDeadline", DateUtils.dateStr4(DateUtils.rollDay(em.getInterestIncomeTime(), 15)));
            }
        }
//        }
        request.setAttribute("experienceBorrowId", Global.getValue("experienceMoney_borrow_id"));
        request.setAttribute("banklist", banklist);
        request.setAttribute("user", user);
        request.setAttribute("account", account);
        request.setAttribute("summary", summary);
        request.setAttribute("unRead", unRead);
        return SUCCESS;
    }

    /**
     * 关于我们
     *
     * @return
     */
    @Action(value = "aboutus", results = {@Result(name = "success", type = "ftl", location = "/wap/aboutus.html")})
    public String aboutus() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 新手指导
     *
     * @return
     */
    @Action(value = "newguidance", results = {@Result(name = "success", type = "ftl", location = "/wap/newguidance.html")})
    public String newguidance() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 用户协议
     *
     * @return
     */
    @Action(value = "protocols", results = {@Result(name = "success", type = "ftl", location = "/wap/protocols.html")})
    public String protocols() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 活动-嘉年华赚钱 
     *
     * @return
     */
    @Action(value = "activity_jnh", results = {@Result(name = "success", type = "ftl", location = "/wap/activity_jnh.html")})
    public String activity_jnh() {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        return SUCCESS;
    }

    /**
     * 网站公告
     *
     * @return
     */
    @Action(value = "article", results = {@Result(name = "success", type = "ftl", location = "/wap/article.html")})
    public String article() throws Exception {
        request.setAttribute("usersession", getSessionUser() == null ? "false" : "true");
        String code=paramString("code"); //栏目代码

        int id=NumberUtils.getInt(request.getParameter("id"));
        Article article = articleService.getArticle(id);
        if (article == null) {
            return NOTFOUND;
        }
        
        //add by lxm 与PC端同步文章类点击次数  2017-3-29 10:03:03
        articleService.updateHitCount(id);
		logger.info("-------WAP端文章ID:"+article.getId()+",被点击次数："+(article.getHits()+1));
        
        Site site=articleService.getSiteByCode(code);
        Site psite=articleService.getSiteById(site.getPid());
        if(psite==null){
            psite=new Site();
            psite.setSiteId(0);
            psite.setName("首页");
        }
        List sublist=articleService.getSubSiteList(site.getPid());
        request.setAttribute("hits", article.getHits()+1);
        request.setAttribute("article", article);
        request.setAttribute("psite", psite);
        request.setAttribute("site", site);
        request.setAttribute("sublist", sublist);
        request.setAttribute("nid", psite.getNid());
        return SUCCESS;
    }


}