package com.liangyou.web.action.member;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.api.chinapnr.AutoTenderPlan;
import com.liangyou.api.chinapnr.AutoTenderPlanClose;
import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.ApiService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/member/security")
@ParentPackage("p2p-default")

public class SecurityAction extends BaseAction implements ModelDriven<User>{

	@Autowired
	private UserService userService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private ApiService apiService;
	
	private User model = new User();
	@Override
	public User getModel() {
		return model;
	}
	
	private BorrowAuto ba ;
	public BorrowAuto getBa() {
		return ba;
	}
	public void setBa(BorrowAuto ba) {
		this.ba = ba;
	}

	
	@Action(value="userpwd",results={
			@Result(name="success", type="ftl",location="/member/security.html")			
			})
	public String userpwd() throws Exception {
		User user = getSessionUser();
		String errormsg="";
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		String newpassword1 = request.getParameter("newpassword1");
		String chgpwdType=request.getParameter("actionType");
		request.setAttribute("query_type", "userpwd");
		if(!StringUtils.isNull(chgpwdType).equals("")){
			if (newpassword != null) {
				errormsg= checkUserpwd(oldpassword, newpassword ,newpassword1);
				if (!errormsg.equals("")) {
					request.setAttribute("errormsg", errormsg);
					return SUCCESS;
				}
				user.setPassword(new MD5().getMD5ofStr(newpassword));
				String msg = "修改密码成功！";
				request.setAttribute("msg", msg);
				userService.updateUser(user);
				super.systemLogAdd(user, 6, "用户修改登录密码成功");
			}else{
				errormsg="修改密码失败！";
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
		}
		return SUCCESS;
	}
	
	@Action(value="addAutoInvest",results={
			@Result(name="autoTender", type="ftl",location="/member/autoTender.html")			
			})
	public String addAutoInvest() throws Exception {
		apiService.checkApiLoan(getSessionUser());
		checkValidImgWithUrl("/member/security/goAutoInvest.html");
		User user = getSessionUser();
		if(user== null){
			message("请登录,然后操作", "/user/login.html");
			return MSG;
		}
		ba.setUser(user);
		
		String[] styles = request.getParameterValues("style");
		if(styles ==null ||styles.length <= 0 ){
			ba.setBorrowStyle("");
		}else{
			ba.setBorrowStyle(StringUtils.array2Str(styles));
		}
		ba.setAddtime(new Date());
		String[] borrowTypes = request.getParameterValues("borrowType");
		if(borrowTypes ==null||borrowTypes.length <= 0){
			ba.setBorrowType("");
		}else{
			ba.setBorrowType(StringUtils.array2Str(borrowTypes));
		}
		if(ba.getId() == 0){
			SearchParam param = new SearchParam();
			param.addParam("user", getSessionUser());
			param.addParam("status",Operator.NOTEQ,-1);
			PageDataList list = userService.queryBorrowAutoListByUser(param);
			if(list.getList().size() >0){
				throw new BussinessException("规则已经存在，请删除后在重新添加！！","/member/security/myAutoInvest.html");
			}
			ba.setAddtime(new Date());
			String msg = madeApiAutoBorrow(ba);//第三方处理  业务逻辑分开
			return msg ;
		}else{
			userService.updateBorrowAuto(ba);
			message("修改自动投标设置成功！！！", "/member/security/myAutoInvest.html");
		}
		return MSG;
	}
	
	private String madeApiAutoBorrow(BorrowAuto ba){
		int code = Global.getInt("api_code");
		switch (code) {
		//cancel by lxm 2017-2-13 16:56:05
//		case 1:
//			ba.setStatus(-1);
//			userService.addBorrowAuto(ba);
//			AutoTenderPlan autoTender = ChinapnrHelper.autoTenderPlan(ba.getUser(), ba.getId());
//			request.setAttribute("pnr", autoTender);
//			return "autoTender";
//		case 2:
//			userService.addBorrowAuto(ba);
//			message("添加自动投标设置成功！！！", "/member/security/myAutoInvest.html");
//			return "msg";
		case 3:
			userService.addBorrowAuto(ba);
			message("添加自动投标设置成功！！！", "/member/security/myAutoInvest.html");
			return "msg";
		}
		return "error";
	}
	
	@Action(value="goAutoInvest",results={
			@Result(name="success", type="ftl",location="/member/addAutoInvest.html")			
			})
	public String goAutoInvest() throws Exception {
		int id = paramInt("id");
		//v1.8.0.4  TGPROJECT-42   qj  2014-04-09 start
		//第三方拦截
		apiService.checkApiLoan(getSessionUser());
		//v1.8.0.4  TGPROJECT-42   qj  2014-04-09 stop
		if(id == 0){
		}else{
			BorrowAuto ba = userService.queryBorrowAutoById(id);
			request.setAttribute("ba", ba);
			request.setAttribute("modify", "modify");
		}
		return SUCCESS;
	}
	
	@Action(value="myAutoInvest",results={
			@Result(name="success", type="ftl",location="/member/myAutoInvest.html")			
			})
	public String myAutoInvest() throws Exception {
		SearchParam param = new SearchParam();
		param.addParam("user", getSessionUser());
		param.addParam("status",Operator.NOTEQ,-1);
		PageDataList list = userService.queryBorrowAutoListByUser(param);
		request.setAttribute("auto", list.getList());
		return SUCCESS;
	}
	
	@Action(value="deleteMyAutoInvest",results={
			@Result(name="huifuClose", type="ftl",location="/member/tenderPlanClose.html")			
			})
	public String deleteQuery() throws Exception {
		int id = paramInt("id");
		String msg;
		if(id==0){
			message("参数错误！！", "/member/security/myAutoInvest.html");
		}
		try {
			 msg = madeAutoDelete(id);
		} catch (Exception e) {
			throw new BussinessException("已经删除，请点击查看", "/member/security/myAutoInvest.html");
		}
		return msg;
	}
	
	private String madeAutoDelete(int id){
		int api_code = Global.getInt("api_code");
		switch (api_code) {
		//cancel by lxm 2017-2-13 16:57:20
//		case 1:
//			AutoTenderPlanClose tpClose = ChinapnrHelper.autoTenderPlanClose(user, id);
//			request.setAttribute("pnr", tpClose);
//			return "huifuClose";
//		case 2:
//			userService.deleteBorrowAutoById(id);
//			message("删除成功，请点击查看", "/member/security/myAutoInvest.html");
//			return MSG;
		default:
			return ERROR;
		}
	}

	private String checkUserpwd(String oldpassword, String newpassword,String newpassword1) {
		User user = getSessionUser();
		if (userService.login(user.getUsername(), oldpassword) == null) {
			return "密码不正确，请输入您的旧密码 ";
		} else if (newpassword.length() < 6 || newpassword.length() > 18) {
			return "新密码长度在6到18之间";
		} else if(StringUtils.isBlank(newpassword) || !newpassword.equals(newpassword1) ){
			 return "您两次输入的新密码不一样，请重新填写";
		}else if (!(StringUtils.pwdContainStr(newpassword)&&StringUtils.pwdContainNum(newpassword))) {
			return "登陆密码不能为纯数字或者纯字母模式，请添加复杂的密码!";
		}
		return "";
	}
	
	@Action(value="paypwd",results={
			@Result(name="success", type="ftl",location="/member/security.html")			
			})
	public String paypwd() throws Exception {
		User user =getSessionUser();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		String valicode=request.getParameter("valicode");
		User backUser = null;
		if (newpassword != null) {
			String msg = "修改支付密码成功！";
			String errormsg = checkPaypwd(oldpassword, newpassword,valicode);
			if (errormsg.equals("")) {
				user.setPaypassword(new MD5().getMD5ofStr(newpassword));
				backUser = userService.updateUser(user);
				session.put(Constant.SESSION_USER, backUser);
				//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start  
				TempIdentifyUser tempIdentifyUser=userService.inintTempIdentifyUser(backUser);
				session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);
				//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end
				request.setAttribute("msg", msg);
				super.systemLogAdd(user, 6, "用户修改支付密码成功");
			} else {
				request.setAttribute("errormsg", errormsg);
			}
		}
		request.setAttribute("query_type", "paypwd");		
		return "success";
	}
	
	private String checkPaypwd(String oldpassword, String newpassword,String valicode) {
		User user = getSessionUser();
		
		MD5 md5 = new MD5();
		String oldpwdmd5=md5.getMD5ofStr(oldpassword);
		String userpaypwd=StringUtils.isNull(user.getPaypassword());
		if(StringUtils.isBlank(userpaypwd)) userpaypwd=user.getPassword();
		String userpwd=StringUtils.isNull(user.getPassword());
		//HAOLIP-107  lx  2014-04-24 start
		if (newpassword.length() < 8 || newpassword.length() > 15) {
			return "新密码长度在8到15之间";
		}else if(StringUtils.isNull(oldpassword).equals("")){
			return "原始支付密码不能为空！ ";
		}else if(!oldpwdmd5.equals(userpaypwd)){
			return "原始支付密码不正确，请输入您的原始支付密码 ";
		}else if(userpaypwd.equals("")&&!oldpwdmd5.equals(userpwd)){
			return "还未设定支付密码，原始密码请输入您的登录密码！ ";
		}else if (newpassword.length() < 8 || newpassword.length() > 15) {
			return "新密码长度在8到15之间";
		}else if(!checkValidImg(valicode)){
			return "验证码不正确！";
		}
		//HAOLIP-107  lx  2014-04-24 start
		return "";
	}
}
