package com.liangyou.web.action.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.ExceptionLog;
import com.liangyou.domain.SystemConfig;
import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserType;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SystemInfo;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.ExceptionLogService;
import com.liangyou.service.SystemService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin/system")
@ParentPackage("p2p-default")
@Results({
	@Result(name="success", type="ftl",location="/admin/system/index.html"),
	@Result(name="exceptionLogPage",type="ftl",location="/admin/system/exceptionLogPage.html"),
	@Result(name="exceptionLogShow",type="ftl",location="/admin/system/exceptionLogShow.html")
})
@InterceptorRefs({  // 管理员拦截
	@InterceptorRef("manageAuthStack")
})
public class SystemConfigAction extends BaseAction implements ModelDriven<User>  {
	
	private final  static Logger logger=Logger.getLogger(AuthAction.class);
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private ExceptionLogService exceptionLogService;
	@Autowired
	private UserService userService;
	private List<SystemConfig> sysInfo;
	protected ServletContext context;
	private User user = new User();
	private String backUrl = "";
	private StringBuffer msg = new StringBuffer();
	
	@Override
	public User getModel() {
		return user;
	}
	
	@Action("index")
	public String index() {
		sysInfo = systemService.getSystemInfoForList();
		request.setAttribute("sysInfo", sysInfo);
		return SUCCESS;
	}

	@Action("update")
	public String update() {
		if (sysInfo != null){
			systemService.updateSystemInfo(sysInfo);
		}
		sysInfo = systemService.getSystemInfoForList();
		request.setAttribute("sysInfo", sysInfo);
		message("操作成功！","");
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 41, "管理员修改系统参数成功");
		return ADMINMSG;
	}
	
	@Action(value="welcome",results={
			@Result(name="success", type="ftl",location="/admin/admin.html")			
			})
	public String welcome(){
		Properties props = System.getProperties();
		Runtime runtime = Runtime.getRuntime();
		long freeMemoery = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - freeMemoery;
		long maxMemory = runtime.maxMemory();
		long useableMemory = maxMemory - totalMemory + freeMemoery;
		request.setAttribute("props", props);
		request.setAttribute("freeMemoery", freeMemoery);
		request.setAttribute("totalMemory", totalMemory);
		request.setAttribute("usedMemory", usedMemory);
		request.setAttribute("maxMemory", maxMemory);
		request.setAttribute("useableMemory", useableMemory);
		return SUCCESS;
	}
	
	@Action("initSystemParam")
	public String initSystemParam() {
		context = ServletActionContext.getServletContext();
		SystemInfo info = systemService.getSystemInfo();
		Global.SYSTEMINFO = info;
		
		for(Map.Entry<String,SystemConfig> en: info.map.entrySet()){
			context.setAttribute(en.getKey().replace("con_", ""), en.getValue());
		}
		context.setAttribute("webroot", context.getContextPath());
		request.setAttribute("sysInfo", info);
		return SUCCESS;
	}
	public String add() {
		return SUCCESS;
	}
	
	@Action("exceptionLogPage")
	public String exceptionLogPage(){
		//时间
		//String startTime = paramString("startTime");
		//类型
		String type = paramString("type");
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrder(OrderType.DESC,"id");
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", Operator.EQ, type);
		}
		PageDataList<ExceptionLog> list = this.exceptionLogService.page(param);
		setPageAttribute(list, param);
		return "exceptionLogPage";
	}
	
	@Action("exceptionLogShow")
	public String exceptionLogShow(){
		long id = Long.parseLong(paramString("id"));
		ExceptionLog item = this.exceptionLogService.find(id);
		request.setAttribute("item", item);
		return "exceptionLogShow";
	}
	
	@Action("clean")
	public String clean(){
		systemService.updateSystemInfo();
		message("系统缓存已经更新，请查看");
		return ADMINMSG;
	}
	
	@Action(value="addAdminUser",results={
			@Result(name="success", type="ftl",location="/admin/system/addAdminUser.html")})
	public String addAdminUser(){
		String actionType = paramString("actionType");
		if(!StringUtils.isBlank(actionType)){
			try {
				//验证码 这里加上
				if(!checkValidImg(StringUtils.isNull(request.getParameter("valicode")))){
					message("验证码不正确！","/user/register.html");
					return ADMINMSG;
				}
				backUrl ="/admin/system/addAdminUser.html";
				String flag = regValidate();
				// 执行登陆逻辑前校验是否存在非法数据，如果有直接返回错误页面
				if (flag.equals("fail")) {
					logger.debug("存在非法数据");
					return flag;
				}
				String checkPwd=paramString("confirm_password");   //获取确认密码后台做比较
				//校验用户输入两次输入的密码是否正确
				checkUserpwd(user.getPassword(),checkPwd);
				String pwd=user.getPassword();
				// 密码MD5加密
				MD5 md5 = new MD5();
				user.setPassword(md5.getMD5ofStr(pwd));
				user.setAddtime(new Date());
				user.setAddip(this.getRequestIp());
				
				int typeId = paramInt("typeId");
				if(typeId != 0 && typeId != 2){//不为空及普通用户
					UserType userType = new UserType();
					userType.setTypeId(typeId);
					user.setUserType(userType);
				}else{
					message("请选择管理员类型","/user/register.html");
					return ADMINMSG;
				}
				userService.register(user, null,null);
				message("添加成功！", "/admin/system/addAdminUser.html");
				return ADMINMSG;
			} catch (Exception e) {
				logger.info( e.getMessage());
				logger.error(e);
				message(e.getMessage()+"注册失败");
				return ADMINMSG;
			}
		}
		List<UserType> userTypeList = userService.getAllUserType();
		request.setAttribute("userTypeList", userTypeList);
		return SUCCESS;
	}
	
	@Action("modifyAdminPwd")
	public String modifyAdminPwd(){
		String newpassword = request.getParameter("newpassword");
		String newpassword1 = request.getParameter("newpassword1");
		String queryType = paramString("queryType");
		User user = userService.getUserById(paramLong("userId"));
		if(user == null){
			message("用户不存在！","/admin/system/queryAdminUser.html");
			return ADMINMSG;
		}
		if (newpassword != null) {
			checkUserpwd(newpassword ,newpassword1);
			if(queryType.equals("payPwd")){
				user.setPaypassword(new MD5().getMD5ofStr(newpassword));
			}else{
				user.setPassword(new MD5().getMD5ofStr(newpassword));
			}
			userService.updateUser(user);
			super.systemLogAdd(user, 6, "修改管理员登录密码成功");
			message("修改密码成功！","/admin/system/queryAdminUser.html");
			return ADMINMSG;
		}else{
			message("修改密码失败！","/admin/system/queryAdminUser.html");
			return ADMINMSG;
		}
	}
	
	@Action(value="queryAdminUser",results={
			@Result(name="success", type="ftl",location="/admin/system/query.html"),
			@Result(name="setadminpwd",type="ftl",location="/admin/setAdminPwd.html")
			})
	public String queryAdminUser(){
		String type = paramString("type");
		request.setAttribute("queryType", paramString("queryType"));
		if(type.equals("query")){
			String username = paramString("username");
			if(StringUtils.isBlank(username)){
				message("用户名不能为空","/admin/system/queryAdminUser.html");
				return ADMINMSG;
			}
			User user = userService.getUserByName(username);
			if(user == null){
				message("用户不存在","/admin/system/queryAdminUser.html");
				return ADMINMSG;
			}else if(user.getUserType().getTypeId() == 1){
				request.setAttribute("msg", "修改超级管理员账号。");
				request.setAttribute("username", user.getUsername());
//				session.put(Constant.AUTH_USER, null);
//				session.put(Constant.SESSION_USER, null);
//				session.put(Constant.AUTH_PURVIEW, null);
				return "setadminpwd";
			}else if(user.getUserType().getTypeId() == 2){
				message("无权限修改普通用户密码","/admin/system/queryAdminUser.html");
				return ADMINMSG;
			}else{
				request.setAttribute("user", user);
				return SUCCESS;
			}
		}
		return SUCCESS;
	}
	
	@Action(value="allAdminUser",results={@Result(name="success", type="ftl",location="/admin/system/allAdminUser.html")})
	public String allAdminUser(){
		SearchParam param = new SearchParam().addParam("userType", Operator.NOTEQ, new UserType(2)).addPage(paramInt("page"));
		PageDataList<User> list = userService.getUsePageDataList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	
	private void checkUserpwd(String newpassword,String newpassword1) {
		if (newpassword.length() < 9 || newpassword.length() > 15) {
			throw new ManageBussinessException("新密码长度必须大于10位","/admin/system/queryAdminUser.html");
		} else if(StringUtils.isBlank(newpassword) || !newpassword.equals(newpassword1) ){
			throw new ManageBussinessException("您两次输入的新密码不一样，请重新填写","/admin/system/queryAdminUser.html");
		}else if(!StringUtils.checaAdminPwd(newpassword)){  //校验密码不能为纯数字，或者是纯字母，可以为数字加字符，字母加字符
			throw new ManageBussinessException("密码必须包含数字、字母、特殊字符【~!@#$%^&*()】，长度不少于10位!","/admin/system/queryAdminUser.html");
		}
	}
	
	private String regValidate() {
		String flag = loginValidate();
		if (flag.equals("fail")) {
			return ADMINMSG;
		}
		User existUser = userService.getUserByName(user.getUsername());
		if (existUser != null) {
			String errorMsg = "用户名已经存在！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return ADMINMSG;
		}else if(!StringUtils.checkUsername(user.getUsername())){
			String errorMsg = "请输入4-15位英文,数字的用户名";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return ADMINMSG;
		}
//		if (!StringUtils.isEmail(user.getEmail())) {
//			String errorMsg = "邮箱格式不对！";
//			logger.info(errorMsg);
//			msg.append(errorMsg);
//			message(msg.toString(), backUrl);
//			return ADMINMSG;
//		}
//		boolean isHasEmail = userService.checkEmail(user.getEmail());
//		if (!isHasEmail) {
//			String errorMsg = "邮箱已经存在！";
//			logger.info(errorMsg);
//			msg.append(errorMsg);
//			message(msg.toString(), backUrl);
//			return ADMINMSG;
//		}
		
		return SUCCESS;
	}
	
	// 校验表单信息是否非法
	private String loginValidate() {
		// 检查用户名和密码是否为空
		if (StringUtils.isNull(user.getUsername()).equals("")) {
			String errorMsg = "用户名不能为空！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return ADMINMSG;
		}
		if (StringUtils.isNull(user.getPassword()).equals("")) {
			String errorMsg = "密码不能为空！";
			logger.info(errorMsg);
			msg.append(errorMsg);
			message(msg.toString(), backUrl);
			return ADMINMSG;
		}
		return SUCCESS;
	}
	
	public List<SystemConfig> getSysInfo() {
		return sysInfo;
	}

	public void setSysInfo(List<SystemConfig> sysInfo) {
		this.sysInfo = sysInfo;
	}
}
