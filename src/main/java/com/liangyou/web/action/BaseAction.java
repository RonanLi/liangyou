package com.liangyou.web.action;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liangyou.sendPhone.RDPhone;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;
import org.apache.struts2.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.liangyou.api.pay.PayModelHelper;
import com.liangyou.api.pay.UserInfoQuery;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.SystemLog;
import com.liangyou.domain.SystemOperation;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.sendPhone.PhoneUtil;
import com.liangyou.service.MsgService;
import com.liangyou.service.PrizeDetailService;
import com.liangyou.service.PrizeService;
import com.liangyou.service.PrizeUserRelationshipService;
import com.liangyou.service.SystemLogService;
import com.liangyou.service.SystemOperationService;
import com.liangyou.service.UserService;
import com.liangyou.tool.iphelper.IPSeeker;
import com.liangyou.tool.iphelper.IPUtils;
import com.liangyou.tool.javamail.Mail;
import com.liangyou.tool.jcaptcha.AjaxCaptchaServiceSingleton;
import com.liangyou.tool.jcaptcha.CaptchaServiceSingleton;
import com.liangyou.util.Base64Util;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.interceptor.SimplePropertyFilter;
import com.octo.captcha.service.CaptchaServiceException;
import com.opensymphony.xwork2.ActionContext;

@Scope("prototype")
public class BaseAction implements ServletRequestAware, ServletResponseAware, SessionAware, ServletContextAware {
	private final static Logger logger = Logger.getLogger(BaseAction.class);

	private static final String JSON_ATTRIBUTE_NAME = "json";

	public final static String SUCCESS = "success";
	public final static String ERROR = "error";
	public final static String FAIL = "fail";
	public final static String OK = "ok";
	public final static String MSG = "msg";
	public final static String ADMINMSG = "adminmsg";
	public final static String NOTFOUND = "notfound";
	public final static String LOGIN = "login";
	public final static String REGISTER = "register";
	public final static String MEMBER = "member";
	public final static String RESULT = "result";
	public final static String ADMINRESULT = "adminResult";

	protected Map<String, Object> session;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext context;

	protected String actionType;
	@Autowired
	MsgService msgService;
	@Autowired
	private SystemLogService systemLogService;
	@Autowired
	private SystemOperationService systemOperationService;
	@Autowired
	private UserService userService;
	@Autowired
	private PrizeService prizeService;
	@Autowired
	private PrizeDetailService prizeDetailService;
	@Autowired
	private PrizeUserRelationshipService prizeUserRelationshipService;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletContext(ServletContext context) {
		this.context = context;
	}

	@Action("404")
	public String notFound() {
		return NOTFOUND;
	}

	public String getActionType() {
		return StringUtils.isNull(actionType);
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * 是否开通线上环境配置。
	 *
	 * @return
	 */
	public boolean isOnlineConfig() {
		return "1".equals(Global.getValue("config_online"));
	}

	/**
	 * 是否开启验证码
	 *
	 * @return
	 */
	public boolean isOpenValidCode() {
		return "1".equals(Global.getValue("open_valid_code"));
	}

	/**
	 * 判断是否为wap端访问
	 *
	 * @return
	 */
	public boolean isWap() {
		if (request.getHeader("User-Agent").contains("Android") || request.getHeader("User-Agent").contains("iPhone")) {
			return true;
		}
		return false;
	}

	public boolean isWeiXin() {
		if (request.getHeader("User-Agent").contains("MicroMessenger")) {
			return true;
		}
		return false;
	}

	/**
	 * 是否启用亿起发推广
	 *
	 * @return
	 */
	public boolean isEnableYQFPromotion() {
		return "1".equals(Global.getValue("is_enable_YQFPromotion"));
	}

	/**
	 * 是否启用体验金
	 *
	 * @return
	 */
	public boolean isEnableExperienceMoney() {
		return "1".equals(Global.getValue("is_enable_experienceMoney"));
	}

	/**
	 * 是否启用转盘抽奖活动
	 *
	 * @return
	 */
	public boolean isEnableLottery() {
		return "1".equals(Global.getValue("is_enable_lottery"));
	}

	/**
	 * 是否开通第三方接口
	 *
	 * @return
	 */
	public boolean isOpenApi() {
		return "1".equals(Global.getValue("open_yjf"));
	}

	/**
	 * 判断是否是图片
	 *
	 * @param file
	 * @return
	 */
	public boolean checkIsImage(File file) {
		try {
			BufferedImage bi = ImageIO.read(file);
			if (bi == null) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 封装获取Session中的用户对象 user 已经是当前数据库最新的数据
	 *
	 * @return
	 */
	protected User getSessionUser() {
		User user = (User) session.get(Constant.SESSION_USER);
		if (user != null) {
			user = userService.getUserById(user.getUserId());
		}
		return user;
	}

	/**
	 * 封装获取Session中的用户对象
	 *
	 * @return
	 */
	protected User getAuthUser() {
		User user = (User) session.get(Constant.AUTH_USER);
		if (user != null) {
			user = userService.getUserById(user.getUserId());
		}
		return user;
	}

	/**
	 * 获取http请求的实际IP
	 *
	 * @return
	 */
	protected String getRequestIp() {
		String realip = IPUtils.getRemortIP(request);
		return realip;
	}

	/**
	 * 获取IP所在地
	 *
	 * @return
	 */
	protected String getAreaByIp() {
		String realip = getRequestIp();
		return getAreaByIp(realip);
	}

	protected String getAreaByIp(String ip) {
		IPSeeker ipSeeker = IPSeeker.getInstance();
		String nowarea = ipSeeker.getArea(ip);
		return nowarea;
	}

	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	protected String getTimeStr() {
		String str = Long.toString(System.currentTimeMillis() / 1000);
		return str;
	}

	/**
	 * 生产校验码
	 *
	 * @throws IOException
	 */
	protected void genernateCaptchaImage() throws IOException {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream out = response.getOutputStream();
		try {
			String captchaId = request.getSession(true).getId();
			BufferedImage challenge = (BufferedImage) CaptchaServiceSingleton.getInstance().getChallengeForID(captchaId, request.getLocale());
			ImageIO.write(challenge, "jpg", out);
			out.flush();
		} catch (CaptchaServiceException e) {
		} finally {
			out.close();
		}
	}

	/**
	 * Ajax 生产校验码 wap端Ajax 生产校验码，验证图形验证码 ，返回base64的字符串 add by gy 2016-9-20
	 *
	 * @throws IOException
	 */
	protected String ajaxGenernateCaptchaImage() throws IOException {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream b64 = new Base64Util.OutputStream(os);
		String result = "";
		try {
			String captchaId = request.getSession(true).getId();
			logger.info("接口请求--生成sessionID：" + captchaId);
			BufferedImage challenge = (BufferedImage) AjaxCaptchaServiceSingleton.getInstance().getChallengeForID(captchaId, request.getLocale());
			ImageIO.write(challenge, "jpg", b64);
			result = os.toString("UTF-8");

		} catch (CaptchaServiceException e) {
		} finally {
		}
		return result;
	}

	/**
	 * 校验校验码是否正确
	 *
	 * @param valid
	 * @return
	 */
	protected boolean checkValidImg(String valid) {
		if (isOpenValidCode()) {
			boolean b = false;
			try {
				b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), valid.toLowerCase());
			} catch (CaptchaServiceException e) {
				logger.info(e.getMessage());
				b = false;
			}
			return b;
		} else {// 本地测试不开通
			return true;
		}

	}

	/**
	 * wap端ajax发送请求，验证图形验证码 add by gy 2016-9-20
	 *
	 * @param valid
	 * @return
	 */
	protected boolean ajaxCheckValidImg(String valid) {
		if (isOpenValidCode()) {
			boolean b = false;
			try {
				logger.info("接口请求--检验sessionID：" + request.getSession().getId());
				b = AjaxCaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), valid.toLowerCase());
				AjaxCaptchaServiceSingleton.getInstance().removeCaptcha(request.getSession().getId());
			} catch (CaptchaServiceException e) {
				logger.info(e.getMessage());
				b = false;
			}
			return b;
		} else {// 本地测试不开通
			return true;
		}

	}

	protected void checkValidImgWithUrl(String backUrl) {
		if (isOpenValidCode()) {
			boolean b = false;
			String validCode = paramString("validCode");
			if (StringUtils.isBlank(validCode)) {
				throw new BussinessException("温馨提示：请输入验证码！", backUrl);
			}
			try {
				b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
			} catch (CaptchaServiceException e) {
				throw new BussinessException("温馨提示：您输入的验证码不正确，请重新输入！", backUrl);
			}
			if (!b) {
				throw new BussinessException("温馨提示：您输入的验证码不正确，请重新输入！", backUrl);
			}
		}
	}

	protected void checkAdminValidImgWithUrl(String backUrl) {
		if (isOpenValidCode()) {
			boolean b = false;
			String validCode = paramString("validCode");
			if (StringUtils.isBlank(validCode)) {
				throw new ManageBussinessException("温馨提示：请输入验证码！", backUrl);
			}
			try {
				b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
			} catch (CaptchaServiceException e) {
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！", backUrl);
			}
			if (!b) {
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！", backUrl);
			}
		}
	}

	/**
	 * 校验校验码是否正确
	 *
	 * @param valid
	 * @return
	 */
	protected void checkValidImg() {
		if (isOpenValidCode()) {
			boolean b = false;
			String validCode = paramString("validCode");
			if (StringUtils.isBlank(validCode)) {
				throw new BussinessException("温馨提示：请输入验证码！");
			}
			try {
				// 判断 是否通过Ajax请求过来的验证码 add by gy 2016-11-24 15:42:47
				if (!StringUtils.isBlank(paramString("isPhoneRequest")) && paramInt("isPhoneRequest") == 1) {
					b = AjaxCaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
				} else {
					b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
				}
			} catch (CaptchaServiceException e) {
				throw new BussinessException("温馨提示：您输入的验证码不正确，请重新输入！");
			}
			if (!b) {
				throw new BussinessException("温馨提示：您输入的验证码不正确，请重新输入！");
			}
		}
	}

	/**
	 * 校验校验码是否正确
	 *
	 * @param valid
	 * @return
	 */
	protected void checkAdminValidImg() {
		if (isOpenValidCode()) {
			boolean b = false;
			String validCode = paramString("validCode");
			if (StringUtils.isBlank(validCode)) {
				throw new ManageBussinessException("温馨提示：请输入验证码！");
			}
			try {
				b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
			} catch (CaptchaServiceException e) {
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！");
			}
			if (!b) {
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！");
			}
		}
	}

	/**
	 * 提示消息
	 *
	 * @param msg
	 * @param url
	 */
	protected void message(String msg, String url) {
		String urltext = "";
		if (!StringUtils.isBlank(url)) {
			urltext = "<a href=" + request.getContextPath() + url + " >返回上一页</a>";
			request.setAttribute("backurl", urltext);
		} else {
			urltext = "<a href='javascript:history.go(-1)'>返回上一页</a>";
		}
		message(msg, url, urltext);
	}

	protected void message(String msg) {
		this.message(msg, getMsgUrl());
	}

	/**
	 * 提示消息
	 *
	 * @param msg
	 * @param url
	 * @param text
	 */
	protected void message(String msg, String url, String text) {
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("rsmsg", msg);
		String urltext = "<a href=" + request.getContextPath() + url + " >" + text + "></a>";
		request.setAttribute("backurl", urltext);
	}

	/**
	 * 空白方法，不处理业务逻辑
	 *
	 * @return
	 * @throws Exception
	 */
	public String blank() throws Exception {
		return SUCCESS;
	}

	/**
	 * 校验参数actionType是否null或者空，是返回true.
	 *
	 * @return
	 */
	public boolean isBlank() {
		return "".equals(this.getActionType());
	}

	public void saveParam() {
		request.setAttribute("param", new SearchParam().toMap());
	}

	public boolean isSession() {
		User sessionUser = this.getSessionUser();
		if (sessionUser == null)
			return false;
		return true;
	}

	protected void setMsgUrl(String url) {
		String msgurl = (String) session.get("msgurl");
		String query = request.getQueryString();
		if (!StringUtils.isBlank(query)) {
			url = url + "?" + query;
		}
		msgurl = url;
		session.put("msgurl", msgurl);
	}

	protected String getMsgUrl() {
		String msgurl = "";
		Object o = null;
		if ((o = session.get("msgurl")) != null) {
			msgurl = (String) o;
		}
		return msgurl;
	}

	protected void setPageUrl() {
		request.setAttribute("pageUrl", request.getRequestURL().toString());
	}

	protected void setPageAttribute(PageDataList list, SearchParam param) {
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", param.toMap());
		setPageUrl();
	}

	protected void setPageAttribute2(PageDataList list, SearchParam param2) {
		request.setAttribute("page", list.getPage());
		request.setAttribute("list1", list.getList());
		request.setAttribute("param", param2.toMap());
		setPageUrl();
	}

	/**
	 * 可以添加额外的搜索条件
	 *
	 * @param list
	 * @param param
	 * @param extraParams
	 */
	protected void setPageAttribute(PageDataList list, SearchParam param, Map<String, Object> extraParams) {
		Map<String, Object> toMap = param.toMap();
		if (extraParams != null && extraParams.size() > 0) {
			toMap.putAll(extraParams);
		}
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", toMap);
		setPageUrl();
	}

	/**
	 * 只添加额外的搜索条件
	 *
	 * @param list
	 * @param param
	 * @param extraParams
	 */
	protected void setPageAttribute(PageDataList list, Map<String, Object> extraParams) {
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", extraParams);
		setPageUrl();
	}

	protected String upload(File upload, String fileName, String destDir, String destFileName) throws Exception {
		if (upload == null)
			return "";
		logger.info("文件：" + upload);
		logger.info("文件名：" + fileName);
		String destFileUrl = destDir + "/" + destFileName;
		String destfilename = ServletActionContext.getServletContext().getRealPath(destDir) + "/" + destFileName;
		logger.info(destfilename);
		File imageFile = null;
		imageFile = new File(destfilename);
		FileUtils.copyFile(upload, imageFile);
		return destFileUrl;
	}

	protected String generateUploadFilename() {
		User u = getSessionUser();
		String timeStr = DateUtils.dateStr3(new Date());
		if (u == null)
			return timeStr;
		return u.getUserId() + timeStr;
	}

	protected String generateUploadFilename(String fileName) {
		String suffix = null;
		if (fileName != null) {
			int last = fileName.lastIndexOf('.');
			suffix = fileName.substring(last);
		}
		return generateUploadFilename() + suffix;
	}

	protected String getLogRemark(Borrow b) {
		String s = "对[<a href='" + request.getContextPath() + "/invest/detail.html?borrowid=" + b.getId() + "' target=_blank>" + b.getName() + "</a>]";
		return s;

	}

	protected String getRef() {
		String ref = StringUtils.isNull(request.getParameter("ref"));
		return ref;
	}

	protected String getAndSaveRef() {
		String ref = getRef();
		request.setAttribute("ref", ref);
		return ref;
	}

	protected void printJson(String json) {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.info(e);
			new BussinessException("发送json数据，失败！！");
		}
	}

	/**
	 * map里边包含jpa对象会报错，要过滤一次。
	 *
	 * @return
	 */
	protected String getStringOfJpaMap(Map<String, Object> map) {
		SimplePropertyFilter spf = new SimplePropertyFilter();
		SerializeWriter sw = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(sw);
		serializer.getPropertyFilters().add(spf);
		serializer.write(map);
		return sw.toString();
	}

	// v1.8.0.3_u3 TGPROJECT-335 wuing 2014-06-16 start

	/**
	 * json字符串输出
	 *
	 * @param json
	 *            json字符
	 * @throws IOException
	 *             异常
	 */
	protected void printJson(Object obj) throws IOException {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(JSON.toJSON(obj));
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 *
	 * @param object
	 * @throws IOException
	 */
	public static void writeJson(Object object) {
		try {
			String json = JSON.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss");
			ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
			ServletActionContext.getResponse().getWriter().write(json);
			ServletActionContext.getResponse().getWriter().flush();
			ServletActionContext.getResponse().getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Json getJson() {
		Json json = (Json) ServletActionContext.getRequest().getAttribute(JSON_ATTRIBUTE_NAME);
		if (json == null) {
			json = new Json(true, "操作成功！");
			ServletActionContext.getRequest().setAttribute(JSON_ATTRIBUTE_NAME, json);
		}
		return json;
	}

	// v1.8.0.3_u3 TGPROJECT-335 wujing 2014-06-16 end

	protected int paramInt(String str) {
		return NumberUtils.getInt(request.getParameter(str));
	}

	protected long paramLong(String str) {
		return NumberUtils.getLong(request.getParameter(str));
	}

	protected double paramDouble(String str) {
		return NumberUtils.getDouble(request.getParameter(str));
	}

	protected String paramString(String str) {
		return StringUtils.isNull(request.getParameter(str));
	}

	protected void export(String infile, String downloadFile) throws Exception {
		File inFile = new File(infile);
		InputStream ins = new BufferedInputStream(new FileInputStream(infile));
		byte[] buffer = new byte[ins.available()];
		ins.read(buffer);
		ins.close();
		HttpServletResponse response = (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		response.setCharacterEncoding("UTF-8");
		response.reset();
		String aa = request.getHeader("User-Agent").toLowerCase();
		if (aa.contains("firefox")) {
			response.addHeader("Content-Disposition", "attachment;filename=" + new String((downloadFile).getBytes("utf-8"), "ISO8859-1"));
		} else {
			response.addHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode((downloadFile), "utf-8"));
		}
		response.addHeader("Content-Length", "" + inFile.length());
		OutputStream ous = new BufferedOutputStream(response.getOutputStream());
		response.setContentType("application/octet-stream");
		ous.write(buffer);
		ous.flush();
		ous.close();
	}

	protected String checkToken(String name) {
		String paramValue = paramString(name);
		String tokenValue = StringUtils.isNull((String) session.get(name));
		// 参数、session中都没用token值提示错误
		if (StringUtils.isBlank(paramValue) && StringUtils.isBlank(tokenValue)) {
			return "会话Token未设定！";
		} else if (StringUtils.isBlank(paramValue) && !StringUtils.isBlank(tokenValue)) {
			return "表单Token未设定！";
		} else if (paramValue.equals(tokenValue) && !StringUtils.isBlank(tokenValue)) { // session中有token,防止重复提交检查
			session.remove(name);
			return "";
		} else {
			return "请勿重复提交！";
		}
	}

	protected String saveToken(String name) {
		String token = TokenHelper.generateGUID();
		session.put(name, token);
		return token;
	}

	protected void generateDownloadFile(String inFile, String downloadFile) throws IOException {
		InputStream ins = new BufferedInputStream(new FileInputStream(inFile));
		byte[] buffer = new byte[ins.available()];
		ins.read(buffer);
		ins.close();
		HttpServletResponse response = (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		response.setCharacterEncoding("utf-8");
		response.reset();
		String aa = request.getHeader("User-Agent").toLowerCase();
		if (aa.contains("firefox")) {
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(("借款电子协议" + downloadFile).getBytes("utf-8"), "ISO8859-1"));
			;
		} else {
			response.addHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(("借款电子协议" + downloadFile), "utf-8"));
		}
		response.addHeader("Content-Length", "" + new File(inFile).length());
		OutputStream ous = new BufferedOutputStream(response.getOutputStream());
		response.setContentType("application/octet-stream");
		ous.write(buffer);
		ous.flush();
		ous.close();
	}

	/**
	 * seconds 设置几分钟后可以 重新获取验证码
	 *
	 * @param seconds
	 */

	protected void checkCanGetMobileCode(long seconds) {
		Object codeInteceptor = request.getSession().getAttribute("mobile_code_interceptor");
		if (codeInteceptor != null) {// 申请过一次 计算时间差
			long currTime = System.currentTimeMillis();
			long remainTime = currTime - Long.parseLong(codeInteceptor.toString());
			// v1.8.0.4 TGPROJECT-145 qj 2014-4-22 start
			int getPhoneCodeTime = Global.getInt("get_phoneCode_time");
			long get_time = seconds * 60 * 1000 * getPhoneCodeTime;
			if (remainTime < get_time) {
				throw new BussinessException("请您" + (get_time - remainTime) / (1000) + " 秒后，重新获取验证码");
			} else {
				request.getSession(true).setAttribute("mobile_code_interceptor", System.currentTimeMillis());
			}
			// v1.8.0.4 TGPROJECT-145 qj 2014-4-22 start
		} else {
			request.getSession(true).setAttribute("mobile_code_interceptor", System.currentTimeMillis());
		}
	}

	protected String getAllParams(boolean safety) {
		StringBuffer ps = new StringBuffer();
		Enumeration<?> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameter = (String) parameterNames.nextElement();
			String value = request.getParameter(parameter);
			if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
				if (!safety || (safety && !parameter.contains("password") && !parameter.contains("pwd"))) {// 安全性
					ps.append(parameter + "=" + value);
					if (parameterNames.hasMoreElements()) {
						ps.append("&");
					}
				}
			}
		}
		return ps.toString();
	}

	/**
	 * 记录系统日志
	 *
	 * @param user
	 * @param systemOperationId
	 * @param ip
	 * @param params
	 * @param remark
	 */
	public void systemLogAdd(User user, int systemOperationId, String remark) {
		SystemOperation systemOperation = this.systemOperationService.find(systemOperationId);
		if (systemOperation != null) {
			SystemLog item = new SystemLog();
			item.setUser(user);
			item.setSystemOperation(systemOperation);
			item.setAddTime(new Date());
			item.setIp(getRequestIp());
			String params = getAllParams(true);
			if (org.apache.commons.lang3.StringUtils.isNotBlank(params)) {
				params = request.getRequestURI() + "?" + params;
			} else {
				params = request.getRequestURI();
			}
			try {
				if (org.apache.commons.lang3.StringUtils.isNotBlank(params) && params.getBytes("Unicode").length > 512) {
					params = org.apache.commons.lang3.StringUtils.substring(params, 0, 20);
				}
				if (org.apache.commons.lang3.StringUtils.isNotBlank(remark) && remark.getBytes("Unicode").length > 255) {
					remark = org.apache.commons.lang3.StringUtils.substring(remark, 0, 10);
				}
			} catch (Exception e) {
				logger.error(e);
			}
			item.setParams(params);
			item.setRemark(remark);
			this.systemLogService.save(item);
		}
	}

	/**
	 * 激活发送邮件
	 *
	 * @param user
	 * @throws Exception
	 */
	public void sendMail(User user) throws Exception {
		String to = user.getEmail();
		Mail m = Mail.getInstance();
		m.setTo(to);
		m.readActiveMailMsg();
		m.replace(user.getUsername(), to, "/user/active.html?id=" + m.getdecodeIdStr(user));
		logger.debug("Email_msg:" + m.getBody());
		m.sendMail();
	}

	/**
	 * 修改邮箱成功，发送通知邮件
	 *
	 * @param user
	 * @throws Exception
	 */
	public void sendEmailCode(User user, String code) throws Exception {
		String to = user.getEmail();
		Mail m = Mail.getInstance();
		m.setTo(to);
		m.readMailCodeMsg();
		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("username", user.getUsername());
		infoMap.put("msg", code);
		infoMap.put("email", to);
		m.replace(infoMap);
		logger.debug("Email_msg:" + m.getBody());
		m.sendMail();
	}

	/***
	 * 根据正则表达式，验证value 是否符合要求
	 *
	 * @param regex
	 * @param value
	 * @return
	 */
	public static boolean checkParamWithRegex(String regEx, String value) {
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(value);
		return mat.find();
	}

	/**
	 * 发送短信内容。
	 * 
	 * @param phoneType
	 * @param codeUniqueId
	 * @return
	 */
	private String sendMobileMsg(int phoneType, String codeUniqueId) {

		switch (phoneType) {
		case 1:// 注册
			logger.info("发送短信验证码类型：" + phoneType + ", 用户注册");
			return "本次的验证码为:" + codeUniqueId + "，(为了您的账户安全，请勿将验证码转告他人)";
		case 0:// 找回密码
			logger.info("发送短信验证码类型：" + phoneType + ", 找回密码");
			return "本次的验证码为:" + codeUniqueId + "，(为了您的账户安全，请勿将验证码转告他人)";
		case 2:// 解绑银行卡
			logger.info("发送短信验证码类型：" + phoneType + ", 解绑银行卡");
			return "本次的验证码为:" + codeUniqueId + "，(为了您的账户安全，请勿将验证码转告他人)";
		default:
			logger.info("发送短信验证码类型：" + phoneType + ", 默认类型");
			return "本次的验证码为:" + codeUniqueId + "，(为了您的账户安全，请勿将验证码转告他人)";
		}
	}

	/**
	 * @param phoneType
	 *            :短信接口类型
	 * @param phone
	 *            ：手机号码
	 * @param user
	 *            ：发送手机的用户
	 * @throws UnsupportedEncodingException
	 * @return：返回验证码
	 */
	public String madeMobileCode(int phoneType, String phone, User user) throws UnsupportedEncodingException {
		String codeUniqueId = "111111";
		// 线上环境才会发短信
		if (isOnlineConfig()) {
			Random random = new Random();
			int numb = random.nextInt(899999) + 100000;
			codeUniqueId = numb + "";
			// String codeContent = "本次的验证码为:" + codeUniqueId + "，(为了您的账户安全，请勿将验证码转告他人)";
			String codeContent = sendMobileMsg(phoneType, codeUniqueId);
			logger.info("发送短信内容为：" + codeContent);
			String msg = PhoneUtil.sentPhone(1, phone, codeContent);
			logger.info("发送短信到" + phone + "状态:" + msg);
		}
		return codeUniqueId;
	}

	/**
	 * 校验验手机证码:当没有从session中获取到验证码 时，说明接收验证码的手机非当前验证的手机
	 *
	 * @param phone
	 *            :校验手机号
	 * @param code
	 *            :校验输入验证码
	 * @return
	 */
	public boolean checkMobileCode(String phone, String code) {
		String phoneCode = String.valueOf(session.get(phone));
		if (StringUtils.isBlank(phoneCode)) { // 判断校验验证码的手机是否为发接收验证码的手机
			return false;
		}
		if (!phoneCode.equals(code)) { // 验证码不匹配
			session.remove(phone); // 删除验证码，重新发送
			return false;
		}
		// 验证通过，删除session中的验证码
		session.remove(phone);
		return true;

	}

	/**
	 * 回调参数拼接共用方法
	 *
	 * @return
	 */
	public String getRequestParams() {
		String params = "";
		try {
			Enumeration e = (Enumeration) request.getParameterNames();
			while (e.hasMoreElements()) {
				String parName = (String) e.nextElement();
				String value = request.getParameter(parName);
				params += parName + "=" + value + "&";
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return params;
	}

	/**
	 * 易极付，实名级别判断
	 */
	public boolean checkYjfUserInfo(String apiId) {
		UserInfoQuery uq = PayModelHelper.userInfoQuery(apiId);
		if ("0".equals(uq.getCertifyLevel()) || "1".equals(uq.getCertifyLevel())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 用户 注册,实名认证,投标送礼品
	 *
	 * @param user
	 *            当前用户
	 * @param useType
	 *            礼品类型 20注册 21实名认证 22投资
	 * @param totalInvestment
	 *            投资金额
	 */
	public boolean RewardUsers(User user, int useType, Double totalInvestment) {
		Prize prize = prizeService.getPrizeByUseTypeAndInvestment(useType, totalInvestment);
		// 向prizeUserRelationship中表添加该用户的中奖信息
		if (prize != null) {
			// 如果是兑换码商品需要更新保存商品详情
			if (prize.getPrizeId() == 9 || prize.getPrizeId() == 10 || prize.getPrizeId() == 11) {
				// 查询商品详情 状态为0(未使用的)
				SearchParam searchParam = new SearchParam();
				searchParam.addOrFilter("status", 0).addOrFilter("prize", prize);
				List<PrizeDetail> list = prizeDetailService.findByParam(searchParam);
				// 如果奖品已经用完
				if (list.size() == 0 || list == null) {
					return false;
				}
				PrizeUserRelationship prizeUserRelationship = new PrizeUserRelationship();
				prizeUserRelationship.setPhone(user.getPhone());// 用户联系方式
				prizeUserRelationship.setPrize(prize);// 奖品信息
				prizeUserRelationship.setReceiveState(1);// 领取状态 0 未绑定用户 1
				// 实力领取，已绑定用户
				prizeUserRelationship.setReceiveTime(new Date());// 领取时间
				prizeUserRelationship.setUser(user);// 领取用户信息
				prizeUserRelationship.setStatus(1);// 0 已领 1 未领
				prizeUserRelationshipService.save(prizeUserRelationship);

				// 给用户绑定奖品信息
				PrizeDetail prizeDetail = list.get(0);
				prizeDetail.setStatus(1);
				prizeDetail.setPrizeUser(prizeUserRelationship);
				// 更新商品详情
				prizeDetailService.update(prizeDetail);
				return true;
			}
			// 不是兑换码商品直接保存
			PrizeUserRelationship prizeUserRelationship = new PrizeUserRelationship();
			prizeUserRelationship.setPhone(user.getPhone());// 用户联系方式
			prizeUserRelationship.setPrize(prize);// 奖品信息
			prizeUserRelationship.setReceiveState(1);// 领取状态 0 未绑定用户 1
			// 实力领取，已绑定用户
			prizeUserRelationship.setReceiveTime(new Date());// 领取时间
			prizeUserRelationship.setUser(user);// 领取用户信息
			prizeUserRelationship.setStatus(0);// 0 已领 2 未领
			prizeUserRelationshipService.save(prizeUserRelationship);
			return true;
		}
		return false;
	}

}
