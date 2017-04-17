package com.liangyou.web.action.interfac;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.liangyou.domain.*;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.json.Json;
import com.liangyou.model.MsgReq;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.CookiesUtil;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/interface/user")
@ParentPackage("p2p-json")
public class UserAction extends BaseAction implements ModelDriven<User> {

	private static Logger logger = Logger.getLogger(UserAction.class);

	private User user = new User();

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

	@Override
	public User getModel() {
		return user;
	}

	/**
	 * 登录
	 */
	@Action(value = "doLogin")
	public String doLogin() throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String message = "登录成功！";

		loginInfoCheck(); // 校验登陆信息

		User u = userService.loginWithPhoneEmailName(user.getUsername(), user.getPassword());

		if (u == null) {
			message = "用户不存在或密码错误！";
			Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
			logger.info("接口请求-登录：登录用户：" + user.getUsername() + "; 用户登录失败！返回前台json：" + JSON.toJSONString(json));
			super.writeJson(json);
			return null;
		} else {
			// 登陆规则验证
			Rule rule = ruleService.getRuleByNid("check_regiest");
			if (rule != null) {
				if (rule.getStatus() == 1) {
					// 是否需要验证邮箱是否激活
					if (rule.getValueIntByKey("check_email") == 1) {
						if (u.getEmailStatus() != 1) {
							message = "您的邮箱未激活，请先激活再登陆！<input type='button' onclick='sendActiveEmail(\"" + user.getEmail() + "\")' class='btn-action' style='width: 160px' value='重新发送激活邮件' class='subphone' />";
							Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
							logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 用户登录失败！返回前台json：" + JSON.toJSONString(json));
							super.writeJson(json);
							return null;
						}
					}
				}
			}
			if (u.getIslock() == 1) {
				message = "该账户" + u.getUsername() + "已经被锁定！";
				Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 用户登录失败！返回前台json：" + JSON.toJSONString(json));
				super.writeJson(json);
				return null;
			} else if (u.getUserType().getTypeId() != 2 && u.getUserType().getTypeId() != 28) {
				message = "用户不存在或密码错误！";
				Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 用户登录失败！返回前台json：" + JSON.toJSONString(json));
				super.writeJson(json);
				return null;
			} else {// 用户正常登陆
				Usertrack track = userService.getLastUserTrack(u.getUserId());// 获取本次信息
				if (track != null) {// 获取上次登录信息
					// v1.8.0.4_u1 TGPROJECT-393 wsl start
					u.setLasttime(track.getLoginTime());
					u.setLastip(track.getLongIp());
					// v1.8.0.4_u1 TGPROJECT-393 wsl end
					int drawCashStatus = userService.getUserDrawCashStatus(u);
					logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 实名认证状态：" + drawCashStatus);
					if (drawCashStatus > 0) { // 易极付实名认证 状态查询， 如果有就一起更新掉
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
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 保存session结束！");

				// bbs in cookie cx 2014-03-24 start
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 保存Cookie值jforumUserInfo开始！");
				Cookie cookie = new Cookie("jforumUserInfo", URLEncoder.encode(u.getUsername(), "utf-8"));
				cookie.setMaxAge(-1);
				cookie.setPath("/");
				response.addCookie(cookie);
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 保存Cookie值jforumUserInfo结束！");
				// cx 2014-03-24 end

				// 插入用户登陆信息
				Usertrack newTrack = new Usertrack();
				newTrack.setUserId(u.getUserId());
				newTrack.setLongIp(this.getRequestIp() + "");
				newTrack.setLoginTime(new Date());
				userService.addUserTrack(newTrack);
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 插入用户登录信息结束！");
				// message(msg.toString(), returnUrl, "进入用户中心");
				// v1.8.0.4 TGPROJECT-169 qj 2014-04-22 start
				userCreditService.loginCredit(u);
				super.systemLogAdd(u, 5, "接口请求：用户" + u.getPhone() + "登录成功");

				MsgReq req = new MsgReq();
				req.setIp(super.getRequestIp());
				req.setReceiver(u);
				req.setSender(new User(Constant.ADMIN_ID));
				req.setMsgOperate(this.msgService.getMsgOperate(6));
				req.setTime(DateUtils.dateStr4(new Date()));
				DisruptorUtils.sendMsg(req);
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 发送站内信结束！");

				Json json = new Json(message, dataMap, "");
				logger.info("接口请求-登录：登录用户：" + u.getPhone() + "; 用户登录成功！返回前台json：" + JSON.toJSONString(json));
				super.writeJson(json);
				return null;
			}
		}
	}

	private String loginInfoCheck() {
		if (isSession()) {// 拦截登陆
			return MEMBER;
		}
		hasCookieValue();// 没有保存cookie进行登录校验，有直接读取cookie进行登录 并设置参数。
		return null;
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

	/**
	 * 用户注册
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "doRegister")
	public String doRegister() throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		HttpSession requestSession = request.getSession(false);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String message = "注册成功！";
		try {
			if (isSession()) {
				super.getJson();
			}
			User user 				= new User();
			String phone 			= paramString("phone"); // 用户名
			String password 		= paramString("password"); // 密码
			String repeatPassword 	= paramString("repeatPassword");// 重复密码
			String validCode 		= paramString("validCode"); // 图片验证码
			String phoneCode 		= paramString("phoneCode"); // 手机验证码
			String channel 			= paramString("channel"); // 注册来源
			String channelCode 		= paramString("code");

			// 检验参数合法有效
			Json checkResultJson = checkParams(phone, validCode, phoneCode, password, message, user, repeatPassword, null);
			logger.info("接口请求--注册：参数校验结果：" + checkResultJson.isSuccess() + ", 信息：" + checkResultJson.getMsg());
			if (!checkResultJson.isSuccess()) {
				super.writeJson(checkResultJson);
				return null;
			}

			// 注册来源
			// add by gy 2016-9-5 17:39:54 增加渠道标识验证。如果是从渠道着陆页注册的用户，需要请求正确的渠道标识
			logger.info("接口请求-注册：验证渠道来源开始：" + channelCode);
			if (!StringUtils.isBlank(channelCode)) {
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
					dataMap.put("redirectURL", request.getRequestURI());
					message = "渠道标识错误！";
					Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					super.writeJson(json);
					return null;
				}
			}
			logger.info("接口请求-注册：验证渠道来源结束！");

			user.setPhone(phone);
			phone = "jhs" + phone;
			user.setUsername(phone);
			user.setPassword(password);
			user.setPhoneStatus(1);// 手机状态激活

			String pwd = user.getPassword();
			// 密码MD5加密
			MD5 md5 = new MD5();
			user.setPassword(md5.getMD5ofStr(pwd));
			// v1.8.0.1_u1 TGPROJECT-226 start
			Rule payPwdRule = ruleService.getRuleByNid("add_paypassword");
			if (payPwdRule != null) {
				if (payPwdRule.getStatus() == 1) {
					user.setPaypassword(md5.getMD5ofStr(pwd));
				}
			}
			// v1.8.0.1_u1 TGPROJECT-226 end
			user.setAddtime(new Date());
			user.setAddip(this.getRequestIp());
			logger.info("接口请求-注册：注册用户：" + user.getPhone() + "; 来源标的--WAP：" + isWap() + "; 微信：" + isWeiXin());
			if (isWeiXin()) {
				user.setFromBe(Constant.FROM_WEIXIN); // 标记微信端注册来源
			} else {
				user.setFromBe(Constant.FROM_WAP); // 标记wap端注册来源
			}

			// 邀请码
			String pCode = paramString("invitateCode");//邀请码
			logger.info("接口请求-注册：注册用户：" + user.getPhone() + "; 受邀邀请码：" + pCode);
			UserInvitateCode uic = new UserInvitateCode();
			if (!StringUtils.isBlank(pCode)) {
				uic.setFillinInvitateCode(pCode);
			}

			userService.register(user, null, uic);

			Json json = new Json(message, dataMap, "");
			logger.info("接口请求-注册：注册用户：" + user.getPhone() + "; 注册成功！返回前台json：" + JSON.toJSONString(json));
			super.writeJson(json);
		} catch (Exception e) {
			e.printStackTrace();
			message = "注册失败！";
			Json json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
			logger.info("接口请求-注册：注册用户：" + user.getPhone() + "; 注册失败！返回前台json：" + JSON.toJSONString(json));
			super.writeJson(json);
			requestSession.removeAttribute("user");// 删除缓存

		}
		return null;
	}

	/**
	 * 找回密码
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "getpwdNew")
	public String getpwdNew() throws Exception {

		String message = "找回密码成功！";

		Map<String, Object> dataMap = new HashMap<String, Object>();

		String phone = paramString("phone");// 手机号
		String password = paramString("password");// 密码
		String repeatPassword = paramString("repeatPassword"); // 重复密码
		String validCode = paramString("validCode"); // 图片验证码
		String phoneCode = paramString("phoneCode");// 手机短信验证码

		User u = userService.getUserByNameEmailPhone(phone);

		Json checkResultJson = checkParams(phone, validCode, phoneCode, password, message, u, repeatPassword, null);
		logger.info("接口请求--找回密码参数校验结果：" + checkResultJson.isSuccess() + ", 信息：" + checkResultJson.getMsg());
		if (!checkResultJson.isSuccess()) {
			super.writeJson(checkResultJson);
			return null;
		}
		MD5 md5 = new MD5();
		u.setPassword(md5.getMD5ofStr(password));
		userService.updateUser(u);

		Json json = new Json(message, dataMap, "");
		super.writeJson(json);

		return null;
	}

	/**
	 * 获取短信验证码
	 */
	@Action(value = "getPhoneCode")
	public String getPhoneCode() throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Credentials", "true");

		Map<String, Object> dataMap = new HashMap<String, Object>();
		String phone = paramString("phone");
		String validCode = paramString("validCode");

		String pt = paramString("phoneType"); // 验证码类型 1 注册短信 0 找回密码短信  2 解绑银行卡（暂时是没用的）
		int phoneType = StringUtils.isBlank(pt) ? Global.getInt("phone_type_register") : Integer.parseInt(pt);

		String message = "验证码发送成功！";
		String codeUniqueId = "";
		boolean isSend = false;

		int timeInterval = 1;

		Json checkResultJson = checkParams(phone, validCode, null, null, message, null, null, phoneType);
		logger.info("接口请求--发送验证码参数校验结果：" + checkResultJson.isSuccess() + ", 信息：" + checkResultJson.getMsg());
		if (!checkResultJson.isSuccess()) {
			super.writeJson(checkResultJson);
			return null;
		}
		codeUniqueId = madeMobileCode(phoneType, phone, user);
		dataMap.put("mobile_token", saveToken("mobile_token"));
		dataMap.put("isSend", isSend + "");
		dataMap.put("timeInterval", String.valueOf(timeInterval));
		logger.info("接口请求--短信验证码：" + codeUniqueId);
		session.put(phone, codeUniqueId); // 将验证码放入session中，在手机校验完了以后，删除此session

		Json json = new Json(message, dataMap, "");
		super.writeJson(json);
		return null;
	}

	/**
	 * 检验注册、找回密码、发送短信验证码的参数合法有效
	 * 
	 * @param phone
	 * @param validCode
	 * @param phoneCode
	 * @param password
	 * @param message
	 * @param u
	 * @param repeatPassword
	 * @param phoneType
	 * @return
	 */
	private Json checkParams(String phone, String validCode, String phoneCode, String password, String message, User u, String repeatPassword, Integer phoneType) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Json json = new Json(message, dataMap, "");
		if (StringUtils.isBlank(phone)) {
			message = "手机号不能为空！";
			json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
			return json;
		} else if (!StringUtils.isMobile(phone)) {
			message = "手机号码格式不对！";
			json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
			return json;
		} else {
			if (phoneType == null) {
				if (StringUtils.isBlank(password) || StringUtils.isBlank(repeatPassword)) {
					message = "密码不能为空！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				} else {
					if (!password.equals(repeatPassword)) {
						message = "两次密码不一致！";
						json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
						return json;
					}
				}
				if (u == null) {
					message = "用户未注册！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				}
				if (StringUtils.isBlank(phoneCode)) {
					message = "请输入短信验证码！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				} else {
					String msgVerCode = (String) session.get(phone);
					if (StringUtils.isBlank(msgVerCode) || (!StringUtils.isBlank(msgVerCode) && !msgVerCode.equals(phoneCode))) {
						message = "请输入正确的短信验证码！";
						json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
						return json;
					}
				}

			} else {
				int phoneResult = userService.getPhoneOnly(phone); // 查询手机号码是否存在
				if (phoneType == 1 && phoneResult > 0) {
					message = "手机号码已存在！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				} else if (phoneType == 0 && phoneResult == 0) {
					message = "手机号码不存在！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				}

				if (StringUtils.isBlank(validCode)) {
					message = "请输入图片验证码！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				} else if (!ajaxCheckValidImg(validCode)) {
					message = "请输入正确的图片验证码！";
					json = new Json(Json.CODE_FAILURE, false, message, dataMap, "");
					return json;
				}

			}
		}


		return json;

	}

}
