package com.liangyou.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmLoanAuthorize;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.Credit;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.InterestGenerate;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Userinfo;
import com.liangyou.domain.Usertrack;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.BorrowTenderService;
import com.liangyou.service.ExperienceMoneyService;
import com.liangyou.service.MessageService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserInvitateCodeService;
import com.liangyou.service.UserService;
import com.liangyou.tool.Page;
import com.liangyou.tool.coder.BASE64Encoder;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Namespace("/member")
@ParentPackage("p2p-default")
public class MemberAction extends BaseAction {

	private static final long serialVersionUID = -2578417106463783748L;
	private static Logger logger = Logger.getLogger(MemberAction.class);
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private ExperienceMoneyService experienceMoneyService;
	@Autowired
	private UserInvitateCodeService userInvitateCodeService;
	@Autowired
	private BorrowTenderService borrowTenderService;

	/**
	 * 用户中心模块
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("my")
	public String my() throws Exception {
		return SUCCESS;
	}

	@Action(value = "index", results = { @Result(name = "success", type = "ftl", location = "/member/main.html"), @Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String index() throws Exception {
		try {
			User sessionUser = getSessionUser();
			if (sessionUser == null) {
				return "login";
			}
			long user_id = sessionUser.getUserId();
			User user = userService.getUserById(user_id); // 查找用户的信息。
			UserCache cache = user.getUserCache(); // 用户扩展信息
			UserAmount amount = user.getUserAmount(); // 信用额度
			Account account = user.getAccount(); // 账户信息表
			Credit credit = user.getCredit(); // 用户信息表
			UserType userType = user.getUserType(); // 用户类型
			Userinfo userInfo = user.getUserinfo();
			Usertrack track = userService.getLastUserTrack(user_id);// 获取用户上次登录的时间
			String ip = this.getRequestIp();// 获取用户IP
			String city = this.getAreaByIp();
			UserAccountSummary summary = accountService.getUserAccountSummary(user_id);
			// 未读站内信
			int unRead = messageService.getUnreadMessageCount(user);
			request.setAttribute("user", user);
			request.setAttribute("cache", cache);
			request.setAttribute("amount", amount);
			request.setAttribute("account", account);
			request.setAttribute("credit", credit);
			request.setAttribute("userType", userType);
			request.setAttribute("userinfo", userInfo);
			request.setAttribute("nid", "member");
			request.setAttribute("summary", summary);
			request.setAttribute("unRead", unRead);
			request.setAttribute("track", track);
			request.setAttribute("ip", ip);
			request.setAttribute("city", city);

			if (isOnlineConfig()) {// 线上
				request.setAttribute("load_url", "https://ppm.yiji.com:8443/index.htm");
				request.setAttribute("mmmIndex_url", Global.getValue("mmm_service_https") + Global.getValue("mmm_service_url"));
			} else {
				request.setAttribute("load_url", "http://ppm.yijifu.net:8605/index.htm");
				request.setAttribute("mmmIndex_url", Global.getValue("mmm_service_test_url"));
			}
			request.setAttribute("webid", Global.getValue("webid"));

			// 启用体验金
			logger.info("是否启用体验金功能：" + isEnableExperienceMoney());
			if (isEnableExperienceMoney()) {
				ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user));
				if (em != null)
					request.setAttribute("em", em);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return "success";
	}


	// add by gy 2017-01-10 09:49:34
	// 由于需求变更, 将前台对渠道查询功能暂时屏蔽

	// 渠道客户汇总
	/*@Action(value = "channelUserSum", results = {
			@Result(name = "success", type = "ftl", location = "/member/channel/channelUserSum.html")
	})
	public String cannelUserQuery() {
		String type = StringUtils.isNull(request.getParameter("type"));
		User auth = getSessionUser();

		if (!(auth.getRealStatus() == 1 || auth.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
			logger.info("渠道信息查询：" + auth.getPhone() + ", 您的实名审核未通过， 无法查询渠道信息！");
			super.systemLogAdd(auth, 49, "您的实名审核未通过， 无法查询渠道信息！");
			return MSG;
		}

		List<ChannelUserQuery> channelUserSumList = userService.getChannelUserSum(auth);
		request.setAttribute("channelUserSumList", channelUserSumList);// 用户类型
		request.setAttribute("user", auth);
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		}
		return null;
	}

	// 渠道客户明细
	@Action(value = "channelUserDetail", results = { @Result(name = "success", type = "ftl", location = "/member/channel/channelUserDetail.html") })
	public String cannelUserDetail() {
		int page = NumberUtils.getInt(request.getParameter("page"));
		String type = StringUtils.isNull(request.getParameter("type"));
		User auth = getSessionUser(); // getAuthUser();
		SearchParam param = new SearchParam();
		param.addPage(page, Page.ROWS);

		Map<String, Object> searchParam = new HashMap<String, Object>();

		PageDataList cannelUserPageData = userService.getChannelUserDetailPageDataList(param, searchParam, auth);

		Map<String, Object> extraparam = new HashMap<String, Object>();
		setPageAttribute(cannelUserPageData, param, extraparam);
		request.setAttribute("user", auth);

		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} else {
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path===" + contextPath);
			String downloadFile = "渠道客户明细_" + DateUtils.dateStr3(new Date()) + ".xls";
			String infile = contextPath + "/data/export/" + downloadFile;
			String[] names = new String[] { "userName", "borrowName", "timeLimitStr", "borrowApr", "tenderMoney", "tenderYescapital", "redPakcageMoney", "tenderAddtime", "borrowFulltime", "addInterest", "addAccount", "channelName", "assignmenState" };
			String[] titles = new String[] { "用户名", "标的名称", "标的期限", "预期年化收益率(%)", "出借金额", "出借本金", "红包使用金额", "投标时间", "满标时间	", "加息百分比(%)", "加息收益", "渠道来源", "转让状态" };
			try {
				List<ChannelUserQuery> list = new ArrayList<ChannelUserQuery>();
				if (cannelUserPageData != null) {
					list = userService.exportChannelUserDetail(auth, searchParam);
					logger.info("导出渠道客户明细记录个数：" + list.size());
					if (!list.isEmpty()) {
						for (ChannelUserQuery cud : list) {
							if (cud.getIsday() == 1) {
								cud.setTimeLimitStr(cud.getTimeLimitDay() + "天");
							} else if (cud.getIsday() == 0) {
								cud.setTimeLimitStr(cud.getTimeLimit() + "个月");
							} else if (cud.getType() == 101) {
								cud.setTimeLimitStr("满标即还");
							}
						}
					}
				}
				ExcelHelper.writeExcel(infile, list, ChannelUserQuery.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}*/

	// 领取体验金
	@Action(value = "receiveExperienceMoney")
	public String ReceiveExperienceMoney() {
		Json json = null;
		ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", this.getSessionUser()));
		if (em != null) {
			if (em.getReceiveStatus() == 0) {
				em.setReceiveStatus(1);
				List<ExperienceMoney> emList = new ArrayList<ExperienceMoney>();
				emList.add(em);
				boolean b = this.experienceMoneyService.update(emList);
				if (b) {
					json = new Json("领取体验金成功！", null, "");
				} else {
					json = new Json(Json.CODE_FAILURE, false, "领取体验金失败！", null, "");
				}
				super.writeJson(json);
			} else {
				json = new Json(Json.CODE_FAILURE, false, "已经领取过体验金！", null, "");
				super.writeJson(json);
			}
		} else {
			json = new Json(Json.CODE_FAILURE, false, "用户体验金不存在！", null, "");
			super.writeJson(json);
		}
		return null;
	}

	@Action(value = "vip", results = { @Result(name = "success", type = "ftl", location = "/member/vip.html"), @Result(name = "fail", type = "ftl", location = "/msg.html"), @Result(name = "payvip", type = "ftl", location = "/member/huifu/UsrAcctPay.html") })
	public String vip() throws Exception {
		User sessionUser = this.getSessionUser();
		User user = userService.getUserById(sessionUser.getUserId());
		String type = request.getParameter("type");
		String vipRemark = StringUtils.isNull(request.getParameter("vip_remark"));
		if (vipRemark.length() > 200) {
			throw new BussinessException("备注字数最大不能超过200个字符");
		}
		int kefu_userid = paramInt("kefu_userid");
		String kefu_username = "";

		// 第三方拦截
		apiService.checkApiLoan(user);

		UserCache uc = user.getUserCache();
		Account account = user.getAccount();
		if (StringUtils.isNull(type).equals("")) {
			List list = userService.getAllKefu();
			double vipFee = Global.getDouble("vip_fee"); // 获取vip的费用
			request.setAttribute("vipFee", vipFee);
			request.setAttribute("act", account);
			request.setAttribute("kflist", list);
		} else {
			Rule rule = ruleService.getRuleByNid("vip_check_kefu");
			if (rule != null) {
				if (rule.getStatus() == 1) { // 校验此规则是否启用
					int rulerStatus = rule.getValueIntByKey("status");
					if (rulerStatus == 1) {// 校验客服必须选择
						if (kefu_userid == 0) {
							message("请选择客服!", "/member/vip.html");
							return MSG;
						}
					}
				}
			}

			if (kefu_userid != 0 && userService.getUserById(kefu_userid) != null) {
				kefu_username = userService.getUserById(kefu_userid).getUsername();
			}

			double vipfee = NumberUtils.getDouble(Global.getValue("vip_fee"));
			if (account.getUseMoney() < vipfee && account.getTotal() < vipfee) {
				message("可用余额不足，请先充值", "/member/account/newrecharge.html");
				return MSG;
			}
			if (uc == null) {
				uc = new UserCache();
			}
			if (uc.getVipStatus() == 2 || uc.getVipStatus() == 1) {
				message("不允许重复申请！", "/member/vip.html");
				return "fail";
			}
			// 校验验证码
			checkValidImgWithUrl("/member/vip.html");

			if (kefu_userid != 0) {
				uc.setKefuUserid(kefu_userid);
				uc.setKefuUsername(kefu_username);
			}
			// 更新后的用户缓存
			AccountLog accountLog = new AccountLog(sessionUser.getUserId(), Constant.VIP_FEE, Constant.ADMIN_ID, this.getTimeStr(), this.getRequestIp());
			boolean isOk = true;
			String checkMsg = "";
			try {
				// 第三方处理
				String msg = checkVipWithApi(uc, accountLog, user, kefu_userid);
				if (!StringUtils.isBlank(msg)) {
					return msg;// 汇付跳转页面
				}
			} catch (BussinessException e) {
				isOk = false;
				checkMsg = e.getMessage();
			} catch (Exception e) {
				isOk = false;
				checkMsg = "系统出错，联系管理员！";
				logger.error(e.getMessage());
			}
			if (!isOk) {
				message(checkMsg, "");
				return MSG;
			}
		}
		if (uc != null && uc.getKefuUserid() != 0) {
			User kefu = userService.getUserById(uc.getKefuUserid());
			request.setAttribute("kefu", kefu);
			super.systemLogAdd(user, 8, "用户申请VIP");
		}
		Account act = userService.getUserById(user.getUserId()).getAccount();
		request.setAttribute("act", act);
		request.setAttribute("user", user);
		request.setAttribute("userCache", uc);
		return "success";
	}

	private String checkVipWithApi(UserCache uc, AccountLog accountLog, User user, long kefu_userid) throws Exception {
		int apiType = Global.getInt("api_code");// 第三方参数
		String vip_remark = request.getParameter("vip_remark");
		if (vip_remark.equals("") || vip_remark == null) {
			message("请输入备注！", "/member/vip.html");
		}
		// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 start
		switch (apiType) {
		case 3:// 双乾接口
			uc.setVipStatus(2);
			uc.setKefuAddtime(new Date());
			uc.setVipRemark(vip_remark);
			userService.applyVip(uc, accountLog);
			return "";
		default:
			return "";
		}
		// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 stop
	}

	/**
	 * 升级后的实名信息填写
	 * 
	 * @return
	 */
	@Action(value = "apiRealname", results = { @Result(name = "success", type = "ftl", location = "/member/identify/activate.html"), @Result(name = "realname", type = "ftl", location = "/member/identify/realname.html") })
	public String apiRealname() {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		// v1.8.0.4 GPROJECT-390 wsl 2014-08-07 start
		if (user.getApiStatus() == 1 || !StringUtils.isBlank(user.getApiId())) {
			user.getBirthday(user.getCardId());
		}
		// v1.8.0.4 GPROJECT-390 wsl 2014-08-07 end
		// v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start
		boolean checkPhone = true;
		if (user.getPhoneStatus() == 1) {
			checkPhone = false;
		}
		Rule rule = ruleService.getRuleByNid("is_phone_check");
		if (rule != null && rule.getStatus() == 1) {
			if (rule.getValueIntByKey("status") == 0) {
				checkPhone = false;
			}
		}
		// v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start
		/*
		 * if(user.getEmailStatus() != 1){ message("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证"); return MSG; //v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start }else
		 */

		// 启用体验金
		logger.info("是否启用体验金功能：" + isEnableExperienceMoney());
		if (isEnableExperienceMoney()) {
			ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user));
			if (em != null)
				request.setAttribute("em", em);
		}

		if (checkPhone) {
			// v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start
			message("请先绑定手机！", "/member/identify/phone.html", "点击绑定手机");
			return MSG;
		} else if (user.getRealStatus() == 0) {// 提交实名信息
			request.setAttribute("user", user);
			return SUCCESS;
		} else {
			request.setAttribute("user", user);
			return "realname";
		}
	}

	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 start
	/**
	 * 第三方授权（投标、还款、扣除管理费授权）
	 * 
	 * @return
	 */
	@Action(value = "loanAuthorize", results = { @Result(name = "success", type = "ftl", location = "/member/mmm/loanAuthorize.html") })
	public String loanAuthorize() {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		// 邮箱认证 注销掉
		/*
		 * if(user.getEmailStatus() != 1){ message("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证"); return MSG; }else
		 */
		if (user.getPhoneStatus() != 1) {
			message("请先绑定手机！", "/member/identify/phone.html", "点击绑定手机");
			return MSG;
		} else if (user.getRealStatus() == 0) {// 提交实名信息
			message("请先实名认证！", "/member/apiRealname.html", "点击实名认证");
			return MSG;
		}

		// edit by gy 2016年11月29日16:40:01
		// 增加跳转授权的标 ID标识
		MmmLoanAuthorize mmm = MmmHelper.mmmLoanAuthorize(user, paramString("borrowId"));
		// end

		request.setAttribute("mmm", mmm);
		return SUCCESS;
	}

	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 stop

	@Action(value = "invite", results = { @Result(name = "success", type = "ftl", location = "/member/huifu/UserLogin.html") })
	public String invite() throws Exception {
		User user = this.getSessionUser();
		BASE64Encoder encoder = new BASE64Encoder();
		String s = encoder.encode((user.getUserId() + "").getBytes());
		request.setAttribute("userid", s);
		return "success";
	}

	// v1.8.0.4_u1 TGPROJECT-127 lx start
	@Action(value = "usercredit", results = { @Result(name = "success", type = "ftl", location = "/member/myagent/usercredit.html") })
	public String userCredit() throws Exception {
		SearchParam param = SearchParam.getInstance();
		User user = getSessionUser();
		request.setAttribute("myagent_type", "usercredit");// tab判断条件
		if (user != null) {
			param.addParam("user", user);
			param.addPage(paramInt("page"));
			param.addOrder(OrderType.DESC, "id");
			PageDataList<UserCreditLog> userCreditLogList = userCreditService.findUserCreditLogList(param);
			setPageAttribute(userCreditLogList, param);
			return SUCCESS;
		}
		message("非法操作");
		return MSG;
	}

	@Action(value = "friendtender", results = { @Result(name = "success", type = "ftl", location = "/member/myagent/friendtender.html") })
	public String friendTender() throws Exception {
		SearchParam param = SearchParam.getInstance();
		User user = getSessionUser();
		request.setAttribute("myagent_type", "friendtender");// tab判断条件
		if (user != null) {

			request.setAttribute("userCreditSummary", userService.getUserCreditSummary(user.getUserId()));// 我推荐的用户统计

			param.addParam("user", user);
			param.addPage(paramInt("page"));
			param.addParam("userCreditType.nid", Operator.EQ, "invest_success_by_friends");
			param.addOrder(OrderType.DESC, "id");
			PageDataList<UserCreditLog> userCreditLogList = userCreditService.findUserCreditLogList(param);
			setPageAttribute(userCreditLogList, param);
			return SUCCESS;
		}
		message("非法操作");
		return MSG;
	}

	@Action(value = "friendborrow", results = { @Result(name = "success", type = "ftl", location = "/member/myagent/friendborrow.html") })
	public String friendBorrow() throws Exception {
		SearchParam param = SearchParam.getInstance();
		User user = getSessionUser();
		request.setAttribute("myagent_type", "friendborrow");
		if (user != null) {
			List<InviteUser> listinviteUsers = userService.getInvitreUser(user.getUserId());
			if (listinviteUsers.size() > 0) {
				List<SearchFilter> listFilters = new ArrayList<SearchFilter>();
				for (int i = 0; i < listinviteUsers.size(); i++) {
					listFilters.add(new SearchFilter("user.userId", Operator.EQ, listinviteUsers.get(i).getUser().getUserId()));
				}
				param.addOrFilter(listFilters);
				param.addOrFilter("status", 3, 6, 7, 8).addPage(paramInt("page")).addOrder(OrderType.DESC, "id");
				PageDataList<Borrow> list = borrowService.getFriendBorrowList(param);
				setPageAttribute(list, param);
			}
			return SUCCESS;
		}
		message("非法操作");
		return MSG;
	}

	// v1.8.0.4_u1 TGPROJECT-127 lx end

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	@Action(value = "interestGeneratedWealth", results = { @Result(name = "success", type = "ftl", location = "/member/huifu/InterestGeneratedWealth.html") })
	public String interestGeneratedWealth() {
		User user = getSessionUser();
		if (user == null) {
			throw new BussinessException("用户不存在！");
		} else {
			if (!(user.getRealStatus() == 1)) {
				message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
				return MSG;
			}
			List<AccountBank> banklist = accountService.getBankLists(user.getUserId()); // 获取用户可用的银行卡
			if (banklist.size() < 1) {
				message("请先添加银行卡！", "/member/account/bank.html", "点击进入绑定银行卡");
				return MSG;
			}
		}
		FssTrans ft = ChinapnrHelper.interestGeneratedWealth(user);
		// 添加记录
		InterestGenerate ig = new InterestGenerate(user, ft.getOrdId());
		ig.setStatus(0);
		ig.setAddtime(new Date());
		accountService.addInterestGenerate(ig);
		request.setAttribute("pnr", ft);
		return SUCCESS;
	}

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end

	@Action(value = "shareReward", results = { @Result(name = "success", type = "ftl", location = "/lottery/share.html") })
	public String shareReward() {
		final double RMB = 10000.00;
		double sumMoney = 0.00;
		User user = getSessionUser();
		if (user != null) {

			List<UserInvitateCode> invitateCodeList = new ArrayList<UserInvitateCode>();
			long userId = user.getUserId();
			invitateCodeList = userInvitateCodeService.getCodeListByPid(userId);
			logger.info("-----------------邀请人个数： " + invitateCodeList.size());
			logger.info("-------------当前邀请人：" + user.getPhone());
			if (null == invitateCodeList || invitateCodeList.size() == 0) {
				request.setAttribute("people", 0);
				request.setAttribute("canReceiveMoney", 0.00);
				request.setAttribute("receivedMoney", 0.00);
				return SUCCESS;
			}
			// 我邀请的人数
			request.setAttribute("people", invitateCodeList.size());
			for (UserInvitateCode invitated : invitateCodeList) {// 循环多个我邀请的人 计算出我获得的佣金
				int Limit = 1;
				double money = 0.00;
				long invitatedId = invitated.getUser().getUserId();
				Date begin = userService.getAddTimeById(invitatedId);
				Date end = new Date();
				int deDay = DateUtils.getIntervalDays(begin, end);
				if (deDay > 10) {
					continue;
				}
				// 被邀请人除体验金之外首次投资的标
				BorrowTender borrowTender = borrowTenderService.getListByUserId(invitatedId);
				if (borrowTender != null) {
					money = borrowTender.getAccount();
					Borrow borrow = borrowTender.getBorrow();
					if (borrow.getIsday() == 0) {
						if (borrow.getTimeLimit() == 2) {
							Limit = 2;
						}
						if (borrow.getTimeLimit() >= 3) {
							Limit = 3;
						}
					}
				}

				logger.info("被邀请人首次投资金额为: " + money);
				logger.info("被邀请人：" + invitated.getUser().getPhone());
				if (0.1 * RMB < money && money <= 3 * RMB) {
					sumMoney += money * 0.1 / 100 * Limit;

					logger.info("邀请人待收奖励总额+" + money * 0.1 / 100 * Limit + ",奖励系数：" + Limit);
				}
				if (3 * RMB < money && money <= 5 * RMB) {
					sumMoney += money * 0.15 / 100 * Limit;
					logger.info("邀请人待收奖励总额+" + money * 0.15 / 100 * Limit + ",奖励系数：" + Limit);
				}
				if (5 * RMB < money && money <= 8 * RMB) {
					sumMoney += money * 0.2 / 100 * Limit;
					logger.info("邀请人待收奖励总额+" + money * 0.2 / 100 * Limit + ",奖励系数：" + Limit);
				}
				if (8 * RMB < money && money <= 15 * RMB) {
					sumMoney += money * 0.25 / 100 * Limit;
					logger.info("邀请人待收奖励总额+" + money * 0.25 / 100 * Limit + ",奖励系数：" + Limit);
				}
				if (15 * RMB < money && money <= 30 * RMB) {
					sumMoney += money * 0.3 / 100 * Limit;
					logger.info("邀请人待收奖励总额+" + money * 0.3 / 100 * Limit + ",奖励系数：" + Limit);
				}
				if (money > 30 * RMB) {
					sumMoney += money * 0.4 / 100 * Limit;
					logger.info("邀请人待收奖励总额+" + money * 0.4 / 100 * Limit + ",奖励系数：" + Limit);
				}

			}
			logger.info("邀请人待收奖励总额为: " + sumMoney);
			NumberUtils.format2(sumMoney);
			// 已领取佣金
			double receivedMoney = userInvitateCodeService.findByUserId(userId).getReceiveMoney();
			logger.info("邀请人已收奖励总额为: " + receivedMoney);
			logger.info("邀请人还可收奖励总额为: " + (sumMoney - receivedMoney));
			request.setAttribute("receivedMoney", receivedMoney);
			// 可领取佣金
			request.setAttribute("canReceiveMoney", sumMoney - receivedMoney);
		}

		return SUCCESS;

	}
}
