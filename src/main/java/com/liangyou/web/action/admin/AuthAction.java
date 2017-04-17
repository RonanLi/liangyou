package com.liangyou.web.action.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.domain.Area;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.Purview;
import com.liangyou.domain.PurviewModel;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserProperty;
import com.liangyou.service.AuthService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.tool.uchon.UchonHelper;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin")
@ParentPackage("p2p-default")
@Results({    
	  @Result(name="error", type="ftl",location="/admin/admin_login.html")
	})
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AuthAction extends BaseAction implements ModelDriven<User>{
	
	private final  static Logger logger=Logger.getLogger(AuthAction.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private AuthService authService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private InviteUserDao inviteUserDao;
	private User formUser = new User() ;
	
	@Override
	public User getModel() {
		return formUser;
	}
	
	private String valicode;

	private String uchoncode;
	private StringBuffer sb=new StringBuffer();
	//v1.8.0.3  TGPROJECT-19 lx 2014-04-03 start
	@Action(value = "modifyuser", results = { @Result(name = "success", type = "ftl", location = "/admin/userinfo/user.html") })
	public String updateUser() {
		String actionType = StringUtils.isNull(request
				.getParameter("actionType"));
		User newUser = userService.getUserById(formUser.getUserId());
		if (newUser != null) {
			newUser.setUptime(this.getTimeStr());
			newUser.setUpip(this.getRequestIp());
		}
		String realname = formUser.getRealname();
		String sex = formUser.getSex();
		String birthday = formUser.getBirthday();
		int kefu_userid = paramInt("kefu_userid");
		int typeId = paramInt("typeId");
		int islock = paramInt("islock");
		long invite_userid = paramLong("invite_userid");
		String inviteMoney = paramString("inviteMoney");
		int status = paramInt("status");
		int province = paramInt("province");
		int city = paramInt("city");
		int area = paramInt("area");
		String cardId = formUser.getCardId();
		String cardType = paramString("cardType");
		String email = paramString("email");
		String qq = paramString("qq");
		String wangwang = paramString("wangwang");
		String tel = paramString("tel");
		String phone = paramString("phone");
		String address = paramString("address");
		String serialId = paramString("serialId");
		// v1.8.0.4_u2 TGPROJECT-316 yl 2014-05-28 start
		UserCache uc = newUser.getUserCache();
		uc.setKefuUserid(kefu_userid);
		newUser.setUserCache(uc);
		// v1.8.0.4_u2 TGPROJECT-316 yl 2014-05-28 end
		
		//v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06 start
		int isContract = paramInt("isContract");
		int contractStatus = paramInt("contractStatus");
		double contractMoney = paramDouble("contractMoney");
		UserProperty userProp = newUser.getUserProperty();
		userProp.setIsContract(isContract);
		userProp.setContractStatus(contractStatus);
		userProp.setContractMoney(contractMoney);
		newUser.setUserProperty(userProp);
		//v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06 end
		
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
		newUser.setSerialId(serialId);
		userService.updateUser(newUser);
		// 更新内存中的客服列表
		Global.ALLKEFU = userService.getAllKefu();
		context.setAttribute("allkefu", Global.ALLKEFU);

		// 推荐人
		// 添加推荐人
		String invite_username = paramString("invite_username");
		InviteUser inviteUser = null;
		if (!StringUtils.isBlank(invite_username)) {
			User invite_user = userService.getUserByName(invite_username);
			if (invite_user != null && 
					(newUser.getInviteUser() == null
					|| (newUser.getInviteUser()!=null && invite_user.getUserId() != newUser.getInviteUser()
							.getInviteUser().getUserId()))) {
				inviteUser = new InviteUser();
				inviteUser.setInviteUser(invite_user);
				inviteUser.setStatus(0);
				inviteUser.setAddtime(new Date());
				inviteUser.setAddip(getRequestIp());
				inviteUser.setUser(newUser);
				inviteUserDao.save(inviteUser);
			}
		}
		
		if (StringUtils.isBlank(actionType)) {
			message("更新成功", "/admin/userinfo/user.html");
			return SUCCESS;
		} else {
			message("更新成功", "/admin/userinfo/user.html");
			User auth_user = getSessionUser();
			super.systemLogAdd(auth_user, 30, "管理员修改用户密码成功");
			return ADMINMSG;
		}
	}
	//v1.8.0.3  TGPROJECT-19 lx 2014-04-03 end
	@Action(value="z1F7zMRQvacq",results={
			@Result(name="success", type="ftl",location="/admin/admin_login.html")			
			})	
	public String index() throws Exception{
		return SUCCESS;
	}
	
	@Action(value="auth",results={
			@Result(name="success", type="redirect",location="/admin/system/welcome.html"),
			@Result(name="setadminpwd",type="ftl",location="/admin/setAdminPwd.html")
			})
	public String auth() throws Exception{
		String msg=checkAuth();
		if(!StringUtils.isBlank(msg)){
			request.setAttribute("msg", sb.toString());
			return ERROR;
		}
		User u = null;
		try{
			u=userService.login(formUser.getUsername(), formUser.getPassword());
		}catch(Exception e){
			logger.error(e);
		}
		if(u==null){
			msg="用户名密码不正确！";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			return ERROR;
		}
		if (u.getIslock() == 1) {
			msg = "该账户" + u.getUsername() + "已经被锁定！";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			return ERROR;
		}
		//拦截前台用户和企业用户登录,
		if(u.getUserType().getTypeId() == 2 || u.getUserType().getTypeId() == 28){
			msg = "用户不存在!";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			return ERROR;
		}
		//v1.8.0.4  TGPROJECT-65 lx 2014-04-16 start
		Rule rule=ruleService.getRuleByNid("dynamic_password");
		if(rule!=null && rule.getStatus() == 1 && rule.getValueIntByKey("is_use") == 1){
			if (!StringUtils.isBlank(u.getSerialId())) {
				if (StringUtils.isBlank(uchoncode)) {
					sb.append("请输入动态口令!");
					request.setAttribute("msg", sb.toString());
					return ERROR;
				}
				String result = UchonHelper.checkDymicPassword(rule.getValueStrByKey("check_url"), u.getSerialId(), uchoncode);
				Object obj = JSON.parse(result);
				if(obj==null){
					msg = "动态口令校验失败！";
					logger.error("动态口令校验失败！接口返回结果为空！");
				} else {
					Map<String, Object> map = (Map<String, Object>) obj;
					if(map.get("resCode")==null){
						msg = "动态口令校验失败！";
						logger.error("动态口令校验失败！接口返回resCode为空！");
					} else if(Integer.parseInt(map.get("resCode").toString())!=0 && Integer.parseInt(map.get("resCode").toString())!=1 ){
						msg = "动态口令校验失败！";
						logger.error("动态口令校验失败！"+map.get("msg").toString()+","+map.get("resCode").toString());
					} else if(Integer.parseInt(map.get("resCode").toString())==0){
						msg = "动态口令错误！";
					}
				}
				if (!StringUtils.isBlank(msg)) {
					sb.append(msg);
					request.setAttribute("msg", sb.toString());
					return ERROR;
				}
		    } 
		}
		//原权限校验获取的是用户所有的权限
		List<Purview> list = authService.getPurviewByUserid(u.getUserId());
		if(list==null||list.size()<1){
			msg="没有权限，非法访问！";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			return ERROR;
		}
		//判断密码是否符合密码规则,不符合规则，强制管理员进行密码修改
		if (!StringUtils.checaAdminPwd(formUser.getPassword())) {
			msg="密码不符合平台管理员密码强度要求，请进行修改！";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			request.setAttribute("username", u.getUsername());
			return "setadminpwd";
		}
		
		session.put(Constant.AUTH_PURVIEW, list);
		//查询当前菜单对应的权限
		
		session.put(Constant.AUTH_USER, u);
		super.systemLogAdd(u, 29, "管理员登录成功");
		return SUCCESS;
	}
	
	private String checkAuth(){
		if(StringUtils.isBlank(valicode)){
			sb.append("验证码不能为空！");
			return ERROR;
		}else if(!checkValidImg(valicode)){
			sb.append("验证码不正确！");
			return ERROR;
		}else if(StringUtils.isBlank(formUser.getUsername())){
			sb.append("用户名不能为空！");
			return ERROR;
		}else if(StringUtils.isBlank(formUser.getPassword())){
			sb.append("密码不能为空！");
			return ERROR;
		}
		return "";
	}
	
	
	
	@Action(value="logout",results={
			@Result(name="success", type="ftl",location="/admin/admin_login.html")			
			})
	public String logout() throws Exception{
		session.put(Constant.AUTH_USER, null);
		session.put(Constant.SESSION_USER, null);
		session.put(Constant.AUTH_PURVIEW, null);
		return SUCCESS;
	}
	
	//后台管理员修改密码的方法	
	@Action(value="resetadminpwd",results={
			@Result(name="success", type="ftl",location="/admin/msg.html"),
			@Result(name="failpwd",type="ftl",location="/admin/setAdminPwd.html")
			})
	public String resetadminpwd(){
		String pwd = paramString("newpassword");
		String checkpwd= paramString("newpassword1");
		String queryType = paramString("queryType");
		request.setAttribute("queryType", queryType);
		if (StringUtils.isBlank(formUser.getUsername())) {//拦截用户直接访问后台密码修改页面
			message("非法操作！", Global.getString("admin_url"));
			return "success";
		}
		String msg = checkAuth();
		if (!msg.equals("")) {
			request.setAttribute("msg", sb.toString());
			return "failpwd";
		}
		
		if (pwd.equals("") || checkpwd.equals("")) {
			msg = "新密码不能为空";
			sb.append(msg);
			request.setAttribute("msg", sb.toString());
			return "failpwd";
		}
		try {
			User u = null;
			if(queryType.equals("payPwd")){
				u = userService.getUserByName(formUser.getUsername());
				msg = checkPaypwd(formUser.getPassword(),checkpwd,u);
				if(!StringUtils.isBlank(msg)){
					sb.append(msg);
					request.setAttribute("msg", sb.toString());
					return "failpwd";
				}
			}else{
				u =userService.login(formUser.getUsername(), formUser.getPassword());
			}
			if (u==null) {  //校验用户输入的原始密码是否正确
				msg = "原始密码输入有误";
				sb.append(msg);
				request.setAttribute("msg", sb.toString());
				return "failpwd";
			}
			if (u.getUserType().getTypeId()==2) {  //校验用户是否是管理员
				msg ="您不是管理员，不能在后台修改密码";
				sb.append(msg);
				request.setAttribute("msg", sb.toString());
				return "failpwd";
			}
			if (!pwd.equals(checkpwd)) {  //校验两次输入的密码是否一致
				msg ="两次输入的新密码不一致";
				sb.append(msg);
				request.setAttribute("msg", sb.toString());
				return "failpwd";
			}
			if (formUser.getPassword().equals(pwd)) {
				msg ="新密码和原始密码不能相同";
				sb.append(msg);
				request.setAttribute("msg", sb.toString());
				return "failpwd";	
			}
			if (!StringUtils.checaAdminPwd(pwd)) { //校验密码是否符合规则
				msg ="新密码格式不正确";
				sb.append(msg);
				request.setAttribute("msg", sb.toString());
				return "failpwd";
			}
			MD5 md5 = new MD5();
			if(queryType.equals("payPwd")){
				u.setPaypassword(md5.getMD5ofStr(pwd));
			}else{
				u.setPassword(md5.getMD5ofStr(pwd));
			}
			userService.updateUser(u);
		} catch (Exception e) {
			logger.info("后台管理员修改密码异常！"+e.getMessage());
		}
		message("密码修改成功，返回上一页进行登录！", Global.getString("admin_url"));
		return "success";
	}
	
	private String checkPaypwd(String oldpassword, String newpassword , User user) {
		if(user == null){
			return "该用户不存在";
		}
		MD5 md5 = new MD5();
		String oldpwdmd5=md5.getMD5ofStr(oldpassword);
		String newpasswordmd5=md5.getMD5ofStr(newpassword);
		String userpaypwd=StringUtils.isNull(user.getPaypassword());
		if(StringUtils.isBlank(userpaypwd)) userpaypwd=user.getPassword();
		String userpwd=StringUtils.isNull(user.getPassword());
		if(StringUtils.isNull(oldpassword).equals("")){
			return "原始支付密码不能为空！";
		}else if(!oldpwdmd5.equals(userpaypwd)){
			return "原始支付密码不正确，请输入您的原始支付密码";
		}else if(userpaypwd.equals("")&&!oldpwdmd5.equals(userpwd)){
			return "还未设定支付密码，原始密码请输入您的登录密码 ";
		}else if(newpasswordmd5.equals(userpwd)){
			return "支付密码不能跟登陆密码相同";
		}
		return "";
	}
	
	/*public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}*/
	
	public String getValicode() {
		return valicode;
	}

	public void setValicode(String valicode) {
		this.valicode = valicode;
	}
	public String getUchoncode() {
		return uchoncode;
	}
	public void setUchoncode(String uchoncode) {
		this.uchoncode = uchoncode;
	}
	@Action("showAuthMenu")
	public String showAuthMenu(){
		User user = getAuthUser();
		if(user == null){
			request.setAttribute("msg", "用户未登陆或登陆已超时,请重新登陆!");
			return ERROR;
		}
		int level = paramInt("level");
		long pid = paramLong("pid");
		List<PurviewModel> list = new ArrayList<PurviewModel>(0);
		List<Purview> purviews = authService.getPurviewByAndLevel(level,pid,user.getUserId());
		for (Purview purview : purviews) {
			PurviewModel model = new PurviewModel(purview.getName(), purview.getUrl());
			list.add(model);
		}
		writeJson(list);
		return null;
	}
}
