package com.liangyou.web.action;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Area;
import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.Rule;
import com.liangyou.domain.Site;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Userinfo;
import com.liangyou.domain.Usertrack;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.MsgReq;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.service.AccountService;
import com.liangyou.service.ArticleService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.LotteryService;
import com.liangyou.service.MsgService;
import com.liangyou.service.PrizeDetailService;
import com.liangyou.service.PrizeUserRelationshipService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.BASE64Decoder;
import com.liangyou.tool.coder.MD5;
import com.liangyou.tool.javamail.Mail;
import com.liangyou.tool.jcaptcha.CaptchaServiceSingleton;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/user")
@ParentPackage("p2p-default")
@Results({ @Result(name = "member", type = "redirect", location = "/member/index.html") })
public class UserAction extends BaseAction implements ModelDriven<User> {

	private static final long serialVersionUID = 916623705419638039L;
	private static Logger logger = Logger.getLogger(UserAction.class);
	private String backUrl = "";
	private StringBuffer msg = new StringBuffer();
	private User user = new User();
	@Autowired
	private ArticleService articleService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private PrizeUserRelationshipService prizeUserRelationshipService;
	@Autowired
	private PrizeDetailService prizeDetailService;

	@Override
	public User getModel() {
		return user;
	}

	@Action("login")
	public String login() {

		StringBuffer url = request.getRequestURL();
		logger.info("页面来源路径：" + url.toString());
		if (isSession()) {
			return MEMBER;
		}
		return LOGIN;
	}

	/**
	 * 登录成功 跳转领奖页面
	 *
	 * @return
	 */
	@Action(value = "award", results = { @Result(name = "success", type = "ftl", location = "/award.html") })
	public String award() {
		request.setAttribute("interfaceFlag", paramString("interfaceFlag"));
		request.setAttribute("purId", paramString("purId"));
		request.setAttribute("userId", paramString("userId"));

		return SUCCESS;
	}

	@Action(value = "checkStatus", results = { @Result(name = "login", type = "ftl", location = "/login.html") })
	public String checkStatus() {
		String status = paramString("status");
		userService.docheckStatus(status);
		return LOGIN;
	}

	/**
	 * 获取领取的奖品信息
	 *
	 * @return
	 */
	@Action(value = "getreceiceResult")
	public String getreceiceResult() {
		// 进行奖品处理
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Json receiveResult = new Json();

		Long purId = NumberUtils.getLong(paramString("purId"));
		User user = this.userService.getUserById(NumberUtils.getLong(paramString("userId")));

		logger.info("userId：" + paramString("userId") + ", " + NumberUtils.getLong(paramString("userId")));

		// 防止重复刷新页面
		SearchParam param = new SearchParam();
		param.addParam("user", user);
		param.addParam("receiveState", 1);
		param.addOrder(OrderType.DESC, "receiveTime");
		List<PrizeUserRelationship> purList = this.prizeUserRelationshipService.findByParam(param);
		if (!purList.isEmpty()) {
			receiveResult = new Json(2, false, "您已经领取过奖品，不能重复领取。<br />请到“我的账户”-->“我的奖品”中查看。<br />更多优惠，敬请期待。", dataMap, "");
			super.writeJson(receiveResult);
			return null;
		}

		PrizeUserRelationship pur = this.prizeUserRelationshipService.findById(purId);
		Prize prize = pur.getPrize();
		logger.info("prizeId：" + prize.getPrizeId() + ", prizeQuantity：" + pur.getPrize().getQuantity());
		List<PrizeDetail> pdList = this.prizeDetailService.findByPrizeIdAndStatus(prize.getPrizeId(), 0);

		if (prize != null && prize.getQuantity() > 0) {
			pur.setReceiveState(1);
			pur.setRealReceiveTime(new Date());
			pur.setUser(user);
			this.prizeUserRelationshipService.update(pur);

			// 如果是无门槛奖品，领取的时候，即使用
			if (pur.getPrize().getUseType() == 1 && !pdList.isEmpty()) {
				pur.setStatus(1);
				pur.setUseTime(new Date());

				// 奖品已经消耗，个数-1
				prize.setQuantity(prize.getQuantity() - 1);
				this.lotteryService.updatePrize(prize);

				// 奖品已经消耗，奖品详情个数-1
				PrizeDetail pd = pdList.get(0);
				pd.setStatus(1);
				pd.setPrizeUser(pur);
				pd.setUseTime(new Date());
				this.prizeDetailService.save(pd);
				dataMap.put("prizeDetail", pd.getDetail());
			}

			dataMap.put("prizeId", prize.getPrizeId());
			dataMap.put("prizeName", prize.getPrizeName());
			receiveResult = new Json("领取奖品成功！", dataMap, "");
		} else {
			receiveResult = new Json(0, false, "Sorry，您抽取的奖品因未能及时领取，已被抢光，只好返回活动页抽取其他奖品啦~", dataMap, "");
		}

		logger.info("登录成功领取奖品页面：" + JSON.toJSONString(receiveResult));
		super.writeJson(receiveResult);
		return null;

	}

	@Action(value = "doLogin", results = { @Result(name = "change_email", type = "ftl", location = "/user/email_update.html") }, interceptorRefs = { @InterceptorRef(value = "mylogin") })
	public String doLogin() throws Exception {

		String url = paramString("returnurl");
		String returnUrl = "/member/index.html";
		loginInfoCheck(); // 校验登陆信息
		User u = null;
		u = userService.loginWithPhoneEmailName(user.getUsername(), user.getPassword());
		if (u == null) {
			String errorMsg = "用户不存在或密码错误！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		} else {
			// 登陆规则验证
			Rule rule = ruleService.getRuleByNid("check_regiest");// 注册登陆验证
			if (rule != null) {
				if (rule.getStatus() == 1) {
					// 是否需要验证邮箱是否激活
					if (rule.getValueIntByKey("check_email") == 1) {// 0
						if (u.getEmailStatus() != 1) {
							String errorMsg = "您的邮箱未激活，请先激活再登陆！<input type='button' onclick='sendActiveEmail(\"" + user.getEmail() + "\")'  class='btn-action' style='width: 160px' value='重新发送激活邮件' class='subphone' />";
							logger.info(errorMsg);
							msg.append(errorMsg);
							message(msg.toString(), backUrl);
							return MSG;
						}
					}
				}
			}
			if (u.getIslock() == 1) {
				String errorMsg = "该账户" + u.getUsername() + "已经被锁定！";
				msg.append(errorMsg);
				message(msg.toString(), backUrl);
				return MSG;
			} else if (u.getUserType().getTypeId() != 2 && u.getUserType().getTypeId() != 28) {
				message("用户不存在或密码错误", backUrl);
				return MSG;
			} else {// 用户正常登陆
				Usertrack track = userService.getLastUserTrack(u.getUserId());// 获取本次信息
				if (track != null) {// 获取上次登录信息
					u.setLasttime(track.getLoginTime());
					u.setLastip(track.getLongIp());
					int drawCashStatus = userService.getUserDrawCashStatus(u);
					logger.info("登录用户：" + u.getPhone() + "; 实名认证状态：" + drawCashStatus);
					if (drawCashStatus > 0) { // 易极付实名认证 状态查询， 若果有就一起更新掉
						u.setRealStatus(1);
					}
					userService.updateUser(u);
				}
				session.put(Constant.SESSION_USER, u);
				TempIdentifyUser tempIdentifyUser = userService.inintTempIdentifyUser(u);
				if (tempIdentifyUser.getEmailStatus() == 1 && tempIdentifyUser.getRealStatus() == 1 && tempIdentifyUser.getPhoneStatus() == 1 && tempIdentifyUser.getPayPwdStatus() == 1 && tempIdentifyUser.getUserBankStatus() == 1) {
					tempIdentifyUser.setAllStatus(1);
				}
				session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);

				Cookie cookie = new Cookie("jforumUserInfo", URLEncoder.encode(u.getUsername(), "utf-8"));
				cookie.setMaxAge(-1);
				cookie.setPath("/");
				response.addCookie(cookie);

				// 插入用户登陆信息
				Usertrack newTrack = new Usertrack();
				newTrack.setUserId(u.getUserId());
				newTrack.setLongIp(this.getRequestIp() + "");
				newTrack.setLoginTime(new Date());
				userService.addUserTrack(newTrack);
				message(msg.toString(), returnUrl, "进入用户中心");
				userCreditService.loginCredit(u);
				super.systemLogAdd(u, 5, "用户登录成功");
				// 登录成功发送站内信
				MsgReq req = new MsgReq();
				req.setIp(super.getRequestIp());
				req.setReceiver(u);
				req.setSender(new User(Constant.ADMIN_ID));
				req.setMsgOperate(this.msgService.getMsgOperate(6));
				req.setTime(DateUtils.dateStr4(new Date()));
				DisruptorUtils.sendMsg(req);

				if (StringUtils.isBlank(url)) {
					return MEMBER;
				} else {
					response.sendRedirect(url);
					return null;
				}
			}
		}
	}

	private String loginInfoCheck() {
		backUrl = "/user/login.html";
		if (isSession()) {// 拦截登陆
			return MEMBER;
		}
		hasCookieValue();// 没有保存cookie进行登录校验，有直接读取cookie进行登录 并设置参数。
		loginValidate();// 执行登陆逻辑前校验是否存在非法数据，如果有直接返回错误页面
		// 验证码
		// checkValidImg();
		return null;
	}

	/**
	 * @return
	 * @throws Exception
	 *             注册手机获取验证码
	 */
	@Action(value = "getPhoneCode")
	public String getPhoneCode() throws Exception {

		String ip = super.getRequestIp();
		logger.info("登录ip：" + ip);

		boolean isSend = false;
		String message = "";
		String codeUniqueId = "";
		int phoneType = Global.getInt("phone_type");
		String phone = StringUtils.isNull(request.getParameter("phone"));

		// add by gy 2016-8-31 11:42:48 增加短信验证码类型判断。
		String pt = StringUtils.isNull(request.getParameter("phoneType"));
		if (!StringUtils.isBlank(pt)) {
			phoneType = Global.getInt(pt);
		}
		String valCode = StringUtils.isNull(request.getParameter("valicode"));
		logger.info("图片验证码：" + valCode);
		int timeInterval = 1;
		// 如果用户没有绑定手机，这校验此项
		int phoneResult = userService.getPhoneOnly(phone); // 查询手机号码是否存在
		if (phone == null || phone.equals("")) {
			message = "手机号码不能为空！";
		} else if (!StringUtils.isMobile(StringUtils.isNull(phone))) {
			message = "手机号码格式不对！";
			// add by gy 2016-8-31 11:42:48 增加短信验证码类型判断，找回密码，该判断不需要
		} else if (phoneResult > 0 && StringUtils.isBlank(pt)) {
			message = "手机号码已经存在！";
		}

		if (!checkValidImg(valCode)) {
			message = "请输入正确的图片验证码！";
		}

		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isBlank(message)) {
			map.put("data", message);
			printJson(JSON.toJSONString(map));
			return null;
		}

		codeUniqueId = madeMobileCode(phoneType, phone, user);

		map.put("data", message);
		map.put("mobile_token", saveToken("mobile_token"));
		map.put("isSend", isSend + "");
		map.put("timeInterval", String.valueOf(timeInterval));
		logger.info("短信验证码：" + codeUniqueId);
		session.put(phone, codeUniqueId); // 将验证码放入session中，在手机校验完了以后，删除此session
		printJson(JSON.toJSONString(map));
		return null;
	}

	@Action("register")
	public String register() throws Exception {
		HttpSession requestSession = request.getSession(false);

		// 防止登录状态，可进入注册页面
		if (isSession()) {
			return MEMBER;
		}
		if (requestSession != null && null != requestSession.getAttribute("user")) {// 删除注册缓存中注册信息
			request.setAttribute("user", requestSession.getAttribute("user"));
			requestSession.removeAttribute("user");// 删除缓存
		}
		// 取出通过好友邀请链接的inviteUser
		String inviteUsername = (String) session.get("inviteUsername");
		if (inviteUsername != null || !StringUtils.isBlank(inviteUsername)) {
			request.setAttribute("inviteUsername", inviteUsername);
		}

		return REGISTER;
	}

	/**
	 * 用户注册
	 */
	@Action(value = "doRegister", results = { @Result(name = "success", type = "ftl", location = "/msg.html") })
	public String doRegister() throws Exception {
		HttpSession requestSession = request.getSession(false);
		try {
			if (isSession()) {
				message("当前已经有用户登录,请退出后重新注册！");
				return MSG;
			}
			User user = new User();

			String phone = paramString("username"); // 用户名
			String password = paramString("password"); // 密码
			String validCodee = paramString("validCodee"); // 验证码
			String channel = paramString("channel"); // 渠道来源
			String channelCode = paramString("code");
			String pCode = paramString("invitateCode"); // 邀请码

			String msg = (String) session.get(phone);

			if (msg != null && !"".equals(msg) && validCodee != null && !"".equals(validCodee)) {
				if (!msg.equals(validCodee)) {
					message("手机验证码不正确！", "/user/register.html");
					return MSG;
				}
			} else {
				message("手机验证码不正确！", "/user/register.html");
				return MSG;
			}

			user.setPhone(phone);
			phone = "jhs" + phone;
			user.setUsername(phone);
			user.setPassword(password);
			user.setPhoneStatus(1);// 手机状态激活

			if (!StringUtils.isBlank(channelCode)) {
				// 如果同时 是渠道客户返回非法操作
				if (org.apache.commons.lang3.StringUtils.isNotBlank(pCode)) {
					logger.info("用户非法操作,原因:同时是邀请用户和渠道用户,非法操作用户的邀请码为:" + pCode + ", 渠道标识为: " + channelCode);
					throw new BussinessException("非法操作!当前日志已记录!");
				}

				String channels = Global.getValue(channelCode);
				if (!StringUtils.isBlank(channels) && channels.indexOf(",") > 0) {
					String channelss[] = channels.split(",");
					for (int i = 0; i < channelss.length; i++) {
						if (channel.equals(channelss[i])) {
							user.setChannel(channel);
							user.setChannelCode(channelCode);
							break;
						}
					}
				} else {
					message("渠道标识错误！", request.getRequestURI());
					return MSG;
				}
			}

			String pwd = user.getPassword();
			// 密码MD5加密
			MD5 md5 = new MD5();
			user.setPassword(md5.getMD5ofStr(pwd));
			Rule payPwdRule = ruleService.getRuleByNid("add_paypassword");// 注册是否默认支付密码为登陆密码
			if (payPwdRule != null) {
				if (payPwdRule.getStatus() == 1) {
					user.setPaypassword(md5.getMD5ofStr(pwd));
				}
			}
			user.setAddtime(new Date());
			user.setAddip(this.getRequestIp());

			// 增加注册用户来源标识 add by gy 2017-01-09 15:27:40
			logger.info("注册用户：" + user.getPhone() + "; 来源标识--WAP：" + isWap() + "; 微信：" + isWeiXin());
			if (isWeiXin()) {
				user.setFromBe(Constant.FROM_WEIXIN); // 标记微信端注册来源
			} 
			if (isWap()) {
				user.setFromBe(Constant.FROM_WAP); // 标记wap端注册来源
			}

			String userTypeId = paramString("userType");

			if (!StringUtils.isBlank(userTypeId)) {
				UserType userType = new UserType();
				userType.setTypeId(Long.parseLong(userTypeId));
				user.setUserType(userType);
			}

			// 邀请码
			logger.info("受邀邀请码：" + pCode);
			UserInvitateCode uic = new UserInvitateCode();
			if (!StringUtils.isBlank(pCode)) {
				uic.setFillinInvitateCode(pCode);
			}

			userService.register(user, null, uic);

			message("注册成功！", "/member/index.html");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.error(e);
			message(e.getMessage() + ",注册失败");
			request.setAttribute("user", requestSession.getAttribute("user"));
			requestSession.removeAttribute("user");// 删除缓存
			return MSG;
		}
		return "success";
	}

	/**
	 * 判断浏览器中是否有cookie保证用户密码
	 *
	 * @return
	 */

	private boolean hasCookieValue() {
		String username = "";
		String password = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if ("cookie_username".equals(StringUtils.isNull(cookie.getName()))) {
					username = cookie.getValue();
				}
				if ("cookie_password".equals(StringUtils.isNull(cookie.getName()))) {
					password = cookie.getValue();
				}
			}
			if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				return false;
			} else {
				user.setUsername(username);
				user.setPassword(password);
				return true;
			}
		}
		return false;
	}

	// 校验表单信息是否非法
	private String loginValidate() {
		// 检查用户名和密码是否为空
		if (StringUtils.isNull(user.getUsername()).equals("")) {
			String errorMsg = "用户名不能为空！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		}
		if (StringUtils.isNull(user.getPassword()).equals("")) {
			String errorMsg = "密码不能为空！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		}
		return "success";
	}

	@Action("checkUsername")
	public String checkUsername() throws Exception {
		boolean result = userService.checkUsername(user.getUsername());
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		// 直接输入响应的内容
		if (result) {
			out.println("1");
		} else {
			out.println("");
		}
		out.flush();
		out.close();
		return null;
	}

	@Action("checkPhone")
	public String checkPhone() throws Exception {
		boolean result = userService.checkPhone(user.getPhone());
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		// 直接输入响应的内容
		if (result) {
			out.println("1");
		} else {
			out.println("");
		}
		out.flush();
		out.close();
		return null;
	}

	@Action("checkValicode")
	public String checkValicode() throws Exception {
		String captchaId = request.getSession(true).getId();
		CaptchaServiceSingleton.getInstance().getChallengeForID(captchaId);
		String validcode = request.getParameter("valicode");
		boolean result = this.checkValidImg(validcode);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		// 直接输入响应的内容
		out.println(result);
		out.flush();
		out.close();
		return null;
	}

	@Action("logout")
	public String logout() throws Exception {
		Map session = (Map) ActionContext.getContext().getSession();
		session.put("session_user", null);
		session.put(Constant.TEMP_IDENTIFY_USER, null);
		Cookie cookie = new Cookie("jforumUserInfo", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		return LOGIN;
	}

	@Action(value = "updateuser", results = { @Result(name = "success", type = "ftl", location = "/admin/userinfo/user.html") })
	public String updateUser() {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		User newUser = userService.getUserById(user.getUserId());
		if (newUser != null) {
			newUser.setUptime(this.getTimeStr());
			newUser.setUpip(this.getRequestIp());
		}
		String realname = user.getRealname();
		String sex = user.getSex();
		String birthday = user.getBirthday();
		int kefu_userid = paramInt("kefu_userid");
		int islock = paramInt("islock");
		String inviteMoney = paramString("inviteMoney");
		int status = paramInt("status");
		int province = paramInt("province");
		int city = paramInt("city");
		int area = paramInt("area");
		String cardId = user.getCardId();
		String cardType = paramString("cardType");
		String email = paramString("email");
		String qq = paramString("qq");
		String wangwang = paramString("wangwang");
		String tel = paramString("tel");
		String phone = paramString("phone");
		String address = paramString("address");
		// long invite_userid = paramLong("invite_userid");
		// int typeId = paramInt("typeId");
		if (kefu_userid != 0) {
			UserCache uc = newUser.getUserCache();
			uc.setKefuUserid(kefu_userid);
			newUser.setUserCache(uc);
		}
		newUser.setRealname(realname);
		newUser.setSex(sex);
		newUser.setBirthday(birthday);
		newUser.setIslock(islock);
		newUser.setInviteMoney(inviteMoney);
		newUser.setStatus(status);
		if (province != 0) {
			Area provinceObj = new Area();
			provinceObj.setId(province);
			newUser.setProvince(provinceObj);
		}
		if (city != 0) {
			Area cityObj = new Area();
			cityObj.setId(city);
			newUser.setCity(cityObj);
		}
		if (area != 0) {
			Area areaObj = new Area();
			areaObj.setId(area);
			newUser.setArea(areaObj);
		}

		newUser.setCardId(cardId);
		newUser.setCardType(cardType);
		newUser.setEmail(email);
		newUser.setQq(qq);
		newUser.setWangwang(wangwang);
		newUser.setPhone(phone);
		newUser.setAddress(address);
		newUser.setTel(tel);
		userService.updateUser(newUser);
		// 更新内存中的客服列表
		Global.ALLKEFU = userService.getAllKefu();
		context.setAttribute("allkefu", Global.ALLKEFU);

		if (StringUtils.isBlank(actionType)) {
			msg.append("更新成功");
			message(msg.toString(), "");
			return SUCCESS;
		} else {
			message("更新成功", "/admin/userinfo/user.html");
			User auth_user = getSessionUser();
			super.systemLogAdd(auth_user, 30, "管理员修改用户密码成功");
			return ADMINMSG;
		}
	}

	@Action(value = "getpwdNew", results = { @Result(name = "success", type = "ftl", location = "/getpwd.html"), @Result(name = "fail", type = "ftl", location = "/getpwd.html") })
	public String getpwdNew() throws Exception {
		if (isBlank()) {
			request.setAttribute("getpwdtype", "pwd");
			return SUCCESS;
		}
		String username = request.getParameter("username");
		String validCodee = request.getParameter("validCodee");// 手机短信验证码
		String password = request.getParameter("password");// 密码
		User u = null;
		String msg = (String) session.get(username);
		if (StringUtils.isNull(validCodee).equals("")) {
			throw new BussinessException("手机验证码不能为空！", "/user/getpwd.html");
		} else if (StringUtils.isNull(username).equals("")) {
			throw new BussinessException("手机号不能为空！", "/user/getpwd.html");
		} else if (StringUtils.isNull(password).equals("")) {
			throw new BussinessException("密码不能为空！", "/user/getpwd.html");
		} else if (msg == null || "".equals(msg) || validCodee == null || "".equals(validCodee)) {
			throw new BussinessException("手机验证码输入不正确！", "/user/getpwd.html");
		} else if (!msg.equals(validCodee)) {
			throw new BussinessException("手机验证码输入不正确！", "/user/getpwd.html");
		} else {
			u = userService.getUserByNameEmailPhone(username);
			if (u == null) {
				throw new BussinessException("用户未注册！", "/user/getpwd.html");
			} else {
				MD5 md5 = new MD5();
				u.setPassword(md5.getMD5ofStr(password));
				userService.updateUser(u);
				// sendGetPwdMail(u);
			}
		}
		this.message("找回密码成功！", "/user/login.html");
		return MSG;

	}

	@Action(value = "show", results = { @Result(name = "success", type = "ftl", location = "/user/show.html") })
	public String show() throws Exception {
		User user = userService.getUserById(paramLong("user_id"));
		Userinfo userinfo = user.getUserinfo();

		UserAccountSummary uas = accountService.getUserAccountSummary(user.getUserId());
		SearchParam borrowParam = SearchParam.getInstance();
		borrowParam.addParam("user", user).addOrFilter("status", 3, 6, 7, 8);
		PageDataList borrowList = borrowService.getList(borrowParam);
		SearchParam investParam = SearchParam.getInstance();
		investParam.addParam("user", user);
		PageDataList investList = borrowService.getTenderList(investParam);
		List attestations = user.getAttestations();
		request.setAttribute("u", user);
		request.setAttribute("info", userinfo);
		request.setAttribute("summary", uas);
		request.setAttribute("borrowList", borrowList.getList());
		request.setAttribute("investList", investList.getList());
		request.setAttribute("attestations", attestations);
		// logger.info(showUser);
		return "success";
	}

	@Action(value = "setpwd", results = { @Result(name = "success", type = "ftl", location = "/setpwd.html"), @Result(name = "fail", type = "ftl", location = "/msg.html"), @Result(name = "ok", type = "ftl", location = "/msg.html") })
	public String setpwd() throws Exception {
		String type = StringUtils.isNull(request.getParameter("type"));
		if (type.equals("")) {// 进入找回密码界面
			String idstr = StringUtils.isNull(request.getParameter("id"));
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] idstrBytes = decoder.decodeBuffer(idstr);
			String decodeidstr = new String(idstrBytes);
			String errormsg = "";
			// [user_id,timestr,email,rancode]
			String[] idstrArr = decodeidstr.split(",");
			if (idstrArr.length < 4) {
				errormsg = "链接无效，请<a href='" + request.getContextPath() + "/getpwd.html'>点此</a>重新获取邮件！";
				this.message(errormsg, "/user/getpwd.html");
				return FAIL;
			}
			long setpwdTime = NumberUtils.getLong(idstrArr[2]);
			if (System.currentTimeMillis() - setpwdTime * 1000 > 1 * 24 * 60 * 60 * 1000) {
				errormsg = "链接有效时间1天，已经失效，请<a href='" + request.getContextPath() + "/user/getpwd.html'>点此</a>重新获取邮件!";
				this.message(errormsg, "/user/getpwd.html");
				return FAIL;
			}
			User u = userService.getUserById(NumberUtils.getLong(idstrArr[0]));
			if (u == null) {
				errormsg = "用户不存在，请联系客服!";
				this.message(errormsg, "/user/getpwd.html");
				return FAIL;
			}
			request.setAttribute("u", u);
			return SUCCESS;
		} else {// 重新设置密码
			String username = StringUtils.isNull(request.getParameter("username"));
			String password = StringUtils.isNull(request.getParameter("password"));
			String confirm_password = StringUtils.isNull(request.getParameter("confirm_password"));
			String errormsg = "";
			User user = userService.getUserByName(username);
			if (password.equals("") || confirm_password.equals("")) {
				errormsg = "新密码不能为空！";
			} else if (!password.equals(confirm_password)) {
				errormsg = "两次密码不一致！";
			} else if (!(password.length() >= 8 && password.length() <= 16)) {
				errormsg = "注册的登陆密码长度必须在8-16位!";
			} else if (!(StringUtils.pwdContainStr(password) && StringUtils.pwdContainNum(password))) {
				errormsg = "登陆密码不能为纯数字或者纯字母模式，请添加复杂的密码!";
			} else if (user == null) {
				errormsg = username + "不存在！";
			} else {
				MD5 md5 = new MD5();
				user.setPassword(md5.getMD5ofStr(password));
				userService.updateUser(user);
				this.message("重置密码成功！", "/user/login.html", "点此登录");
				return OK;
			}
			request.setAttribute("u", user);
			request.setAttribute("errormsg", errormsg);
			return SUCCESS;
		}
	}

	// 找回支付密码
	@Action(value = "getpaypwd", results = { @Result(name = "success", type = "ftl", location = "/getpaypwd.html"), @Result(name = "fail", type = "ftl", location = "/msg.html"), @Result(name = "ok", type = "ftl", location = "/msg.html") })
	public String getpaypwd() throws Exception {
		if (isBlank()) {
			request.setAttribute("getpwdtype", "paypwd");
			return SUCCESS;
		}

		String username = request.getParameter("username");
		String validcode = request.getParameter("validcode");
		request.setAttribute("getpwdtype", "paypwd");
		User u = null;
		if (StringUtils.isNull(validcode).equals("")) {
			this.message("验证码不能为空！");
			return MSG;
		} else if (StringUtils.isNull(username).equals("")) {
			this.message("用户名不能为空！");
			return MSG;
		} else if (!this.checkValidImg(validcode)) {
			this.message("验证码不正确！");
			return MSG;
		} else {
			u = userService.getUserByName(username);

			if (u == null) {
				this.message("用户名未注册！");
				return MSG;

			} else {
				sendGetPayPwdMail(u);
			}
		}
		String email = u.getEmail();
		email = (email.substring(0, 2)).concat("*").concat(email.substring(email.indexOf('@')));
		this.message("找回支付密码邮件发送至" + email + "！", "/member/index.html");

		return MSG;
	}

	@Action(value = "setpaypwd", results = { @Result(name = "success", type = "ftl", location = "/setpwd.html"), @Result(name = "fail", type = "ftl", location = "/msg.html"), @Result(name = "ok", type = "ftl", location = "/msg.html") })
	public String setpaypwd() throws Exception {
		String type = StringUtils.isNull(request.getParameter("type"));
		if (type.equals("")) {// 进入找回密码界面
			String idstr = StringUtils.isNull(request.getParameter("id"));
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] idstrBytes = decoder.decodeBuffer(idstr);
			String decodeidstr = new String(idstrBytes);
			String errormsg = "";
			// [user_id,timestr,email,rancode]
			String[] idstrArr = decodeidstr.split(",");
			if (idstrArr.length < 4) {
				errormsg = "链接无效，请<a href='" + request.getContextPath() + "/getpaypwd.html'>点此</a>重新获取邮件！";
				this.message(errormsg, "/getpaypwd.html");
				return MSG;
			}
			long setpwdTime = NumberUtils.getLong(idstrArr[2]);
			if (System.currentTimeMillis() - setpwdTime * 1000 > 1 * 24 * 60 * 60 * 1000) {
				errormsg = "链接有效时间1天，已经失效，请<a href='" + request.getContextPath() + "/getpaypwd.html'>点此</a>重新获取邮件!";
				this.message(errormsg, "/getpaypwd.html");
				return MSG;
			}
			User u = userService.getUserById(NumberUtils.getLong(idstrArr[0]));
			if (u == null) {
				errormsg = "用户不存在，请联系客服!";
				this.message(errormsg, "/getpaypwd.html");
				return MSG;
			}
			request.setAttribute("u", u);
			return SUCCESS;
		} else {// 重新设置密码
			String username = StringUtils.isNull(request.getParameter("username"));
			String password = StringUtils.isNull(request.getParameter("password"));
			String confirm_password = StringUtils.isNull(request.getParameter("confirm_password"));
			String errormsg = "";
			User user = userService.getUserByName(username);
			if (password.equals("") || confirm_password.equals("")) {
				errormsg = "新密码不能为空！";
			} else if (!password.equals(confirm_password)) {
				errormsg = "两次密码不一致！";
			} else if (!(password.length() >= 8 && password.length() <= 16)) {
				errormsg = "注册的登陆密码长度必须在8-16位!";
			} else if (!(StringUtils.pwdContainStr(password) && StringUtils.pwdContainNum(password))) {
				errormsg = "注册的登陆密码不能为纯数字或者纯字母模式，请添加复杂的密码!";
			} else if (user == null) {
				errormsg = username + "不存在！";
			} else {
				MD5 md5 = new MD5();
				user.setPaypassword(md5.getMD5ofStr(password));
				userService.updateUser(user);
				this.message("重置密码成功！", "/user/login.html", "点此登录");
				return MSG;
			}
			request.setAttribute("u", user);
			request.setAttribute("errormsg", errormsg);
			return SUCCESS;
		}
	}

	private void sendGetPayPwdMail(User user) throws Exception {
		try {
			String to = user.getEmail();
			Mail m = Mail.getInstance();
			m.setTo(to);
			m.readGetpwdMailMsg();
			m.replace(user.getUsername(), to, "/user/setpaypwd.html?id=" + m.getdecodeIdStr(user));
			logger.debug("Email_msg:" + m.getBody());
			m.sendMail();
		} catch (Exception e) {
			this.message("邮箱服务器错误，发送失败", "/user/login.html");
		}

	}

	@Action("checkPhoneToJson")
	public String checkPhoneToJson() {
		String phone = paramString("phone");
		boolean isSuccess = true;
		String msg = "";
		if (!StringUtils.isBlank(phone)) {
			if (!StringUtils.isMobile(StringUtils.isNull(phone))) {
				isSuccess = false;
				msg = "手机号码格式不对！";
			}
			int phoneResult = userService.getPhoneOnly(phone); // 查询手机号码是否存在
			if (phoneResult > 0) {
				isSuccess = false;
				msg = "手机号码已经存在！";
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", isSuccess + "");
		map.put("msg", msg);
		printJson(JSON.toJSONString(map));
		return null;
	}

	// 着陆页跳转
	@Action(value = "registerShow", results = { @Result(name = "success", type = "ftl", location = "/register_show.html") })
	public String registerShow() {
		request.setAttribute("channel", paramString("channel"));
		request.setAttribute("code", paramString("code"));
		request.setAttribute("test", paramString("test"));

		long indexBorrow1Id = Long.parseLong(Global.getValue("index_borrow1_id"));
		long indexBorrow2Id = Long.parseLong(Global.getValue("index_borrow2_id"));
		long indexBorrow3Id = Long.parseLong(Global.getValue("index_borrow3_id"));
		long indexBorrow4Id = Long.parseLong(Global.getValue("index_borrow4_id"));
		// 优质标
		request.setAttribute("indexHot1", this.borrowService.getBorrow(indexBorrow1Id));
		request.setAttribute("indexHot2", this.borrowService.getBorrow(indexBorrow2Id));
		request.setAttribute("indexHot3", this.borrowService.getBorrow(indexBorrow3Id));
		request.setAttribute("indexHot4", this.borrowService.getBorrow(indexBorrow4Id));
		// 媒体报道
		Site mtbdSite = articleService.getSiteByCode("mtbd");
		int page = paramInt("page");
		int pageRow = paramInt("pageRow");
		PageDataList data = null;
		if (articleService.checkSiteCode("mtbd")) {// 如果没有当前栏目
			message("该栏目还未添加，请联系管理员！");
			return MSG;
		}
		data = articleService.getArticleList(mtbdSite, page, pageRow);
		request.setAttribute("mtbdList", data.getList().size() < 3 ? data.getList() : data.getList().subList(0, 3));
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
		// 网站公告
		Site webNoticeSite = articleService.getSiteByCode("web-notice");
		int page1 = paramInt("page");
		int pageRow1 = paramInt("pageRow");
		PageDataList data1 = null;
		if (articleService.checkSiteCode("web-notice")) {// 如果没有当前栏目
			message("该栏目还未添加，请联系管理员！");
			return MSG;
		}
		data1 = articleService.getArticleList(webNoticeSite, page1, pageRow1);
		request.setAttribute("webNoticeList", data1.getList().size() < 3 ? data1.getList() : data1.getList().subList(0, 3));
		return "success";
	}

	/**
	 * add by gy 2016-11-14 11:11:16 企业用户注册页面
	 *
	 * @return
	 */
	@Action(value = "registerBusinessUsers", results = { @Result(name = "success", type = "ftl", location = "/register_business_users.html") })
	public String registerBusinessUsers() {
		return SUCCESS;
	}

	@Action("clear")
	public void clear() {
		userService.clear();
	}
}
