package com.liangyou.web.action;

import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.Usertrack;
import com.liangyou.domain.YwdUser;
import com.liangyou.model.MsgReq;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.tool.ucenter.UCenterHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.opensymphony.xwork2.ModelDriven;

/**
 * 义乌贷
 * 
 * @author lx 2014-04-01
 * 
 *
 */
@Namespace("/user")
@ParentPackage("p2p-default")
@Results({ @Result(name = "member", type = "redirect", location = "/member/index.html") })
public class YwdAction extends BaseAction implements ModelDriven<User> {

	private static final long serialVersionUID = 916623705419638039L;
	private static Logger logger = Logger.getLogger(YwdAction.class);
	private String backUrl = "";
	private StringBuffer msg = new StringBuffer();
	private User user = new User();
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

	@Override
	public User getModel() {
		return user;
	}

	@Action(value = "ywddoLogin", results = { @Result(name = "update_user", type = "ftl", location = "/update_user.html") })
	public String ywddoLogin() throws Exception {
		String url = paramString("returnurl");
		String returnUrl = "/member/index.html";
		loginInfoCheck(); // 校验登陆信息
		User u = null;
		u = userService.loginWithPhoneEmailName(user.getUsername(),
				user.getPassword());// 本地查询用户
		if (u == null) {// 查询
			// YwdUser ywdUser=new YwdUser(1,"ywd123","123456");
			YwdUser ywdUser = null;
			ywdUser = userService.ywdLogin(user.getUsername(),
					user.getPassword());
			if (ywdUser == null) {
				String errorMsg = "用户不存在或密码错误！";
				logger.info(errorMsg);
				msg.append(errorMsg);
				message(msg.toString(), backUrl);
				return MSG;
			} else {
				request.setAttribute("ywdUser", ywdUser);
				request.getSession(false).setAttribute("ywd_user_id",
						ywdUser.getId());
				return "update_user";
			}
		} else {
			// 登陆规则验证
			Rule rule = ruleService.getRuleByNid("check_regiest");
			if (rule != null) {
				if (rule.getStatus() == 1) {
					// 是否需要验证邮箱是否激活
					if (rule.getValueIntByKey("check_email") == 1) {
						if (u.getEmailStatus() != 1) {
							String errorMsg = "您的邮件未激活，请先激活再登陆！";
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
			} else if (u.getUserType().getTypeId() != 2) {
				message("后台管理员不能登陆前台", backUrl);
				return MSG;
			} else {// 用户正常登陆
				Usertrack track = userService.getLastUserTrack(u.getUserId());// 获取本次信息
				if (track != null) {// 获取上次登录信息
					u.setLasttime(track.getLoginTime());
					int drawCashStatus = userService.getUserDrawCashStatus(u);
					logger.info("实名认证状态：" + drawCashStatus);
					if (drawCashStatus > 0) { // 易极付实名认证 状态查询， 若果有就一起更新掉
						u.setRealStatus(1);
					}
					userService.updateUser(u);
				}
				session.put(Constant.SESSION_USER, u);

				// bbs in cookie cx 2014-03-24 start
				Cookie cookie = new Cookie("jforumUserInfo", URLEncoder.encode(
						u.getUsername(), "utf-8"));
				cookie.setMaxAge(3600 * 24);
				cookie.setPath("/");
				response.addCookie(cookie);
				// cx 2014-03-24 end

				// 插入用户登陆信息
				Usertrack newTrack = new Usertrack();
				newTrack.setUserId(u.getUserId());
				newTrack.setLongIp(this.getRequestIp() + "");
				newTrack.setLoginTime(new Date());
				userService.addUserTrack(newTrack);
				message(msg.toString(), returnUrl, "进入用户中心");

				super.systemLogAdd(u, 5, "用户登录成功");

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
		loginValidate(); // 执行登陆逻辑前校验是否存在非法数据，如果有直接返回错误页面
		// 验证码
		checkValidImg();
		return null;
	}

	/**
	 * 
	 */
	@Action(value = "ywdUpdateUser")
	public String ywdUpdateUser() throws Exception {
		HttpSession requestSession = request.getSession(false);
		Long id = NumberUtils.getLong(StringUtils.isNull(requestSession
				.getAttribute("ywd_user_id")));
		if (id == 0) {
			return LOGIN;
		}
		try {
			if (isSession()) {
				return MEMBER;
			}
			requestSession.setAttribute("user", user);// 放进缓存，当用户输入错误的时候，还原用户输入的信息，如果保存成功，后面记得删除。

			String flag = regValidate();
			// 执行登陆逻辑前校验是否存在非法数据，如果有直接返回错误页面
			if (flag.equals("fail")) {
				logger.debug("存在非法数据");
				return flag;
			} else if (flag.equals(MSG)) {
				return flag;
			}
			String pwd = user.getPassword();

			// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start
			int counts = userService.getYwdVipUsersCount();
			// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 end

			// 密码MD5加密
			MD5 md5 = new MD5();
			user.setPassword(md5.getMD5ofStr(pwd));
			user.setAddtime(new Date());
			user.setActivityStatus(1);
			user.setAddip(this.getRequestIp());

			// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start
			user.setYwdUserNo(String.format("%06d", counts));
			// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start

			userService.register(user, null, null);

			if ("1".equals(Global.getValue("is_open_bbs"))) {
				try {
					UCenterHelper.ucenter_register(user.getUsername(), pwd,
							user.getEmail());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			requestSession.removeAttribute("user");// 删除缓存
			User u = userService.getUserByName(user.getUsername());
			YwdUser ywdUser = userService.findYwdUser(id);
			if (ywdUser != null) {
				ywdUser.setIslogin(1);
				userService.updateYwdUser(ywdUser);
			}
			session.put(Constant.SESSION_USER, u);

			// bbs in cookie cx 2014-03-24 start
			Cookie cookie = new Cookie("jforumUserInfo", URLEncoder.encode(
					u.getUsername(), "utf-8"));
			cookie.setMaxAge(3600 * 24);
			cookie.setPath("/");
			response.addCookie(cookie);
			// cx 2014-03-24 end

			// 插入用户登陆信息
			Usertrack newTrack = new Usertrack();
			newTrack.setUserId(u.getUserId());
			newTrack.setLongIp(this.getRequestIp() + "");
			newTrack.setLoginTime(new Date());
			userService.addUserTrack(newTrack);
			message(msg.toString(), "/member/index.html", "进入用户中心");

			requestSession.removeAttribute("ywd_user_id");
			super.systemLogAdd(u, 5, "用户登录成功");

			return MEMBER;

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.error(e);
			message(e.getMessage() + ",修改信息失败");
			request.setAttribute("user", requestSession.getAttribute("user"));
			requestSession.removeAttribute("user");// 删除缓存
			return MSG;
		}
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
				if ("cookie_username".equals(StringUtils.isNull(cookie
						.getName()))) {
					username = cookie.getValue();
				}
				if ("cookie_password".equals(StringUtils.isNull(cookie
						.getName()))) {
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

	private String regValidate() {
		String flag = loginValidate();
		if (flag.equals("fail")) {
			return MSG;
		}
		User existUser = userService.getUserByName(user.getUsername());
		if (existUser != null) {
			String errorMsg = "用户名已经存在！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		} else if (!StringUtils.checkUsername(user.getUsername())) {
			String errorMsg = "请输入4-15位由字母和数字组成的用户名";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		} else if (!StringUtils.deniedUsername(user.getUsername())) {
			String errorMsg = "用户名存在非法字符";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return MSG;
		}
		if (!(user.getPassword().length() >= 8)) {
			message("注册的登陆密码长度必须在8-16位!");
			return MSG;
		}
		if (!StringUtils.pwdContainStr(user.getPassword())) {
			message("注册的登陆密码不能为纯数字或者纯字母模式，请添加复杂的密码!");
			return MSG;
		}

		return "success";
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

}
