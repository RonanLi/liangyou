package com.liangyou.web.action.member;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.liangyou.context.Constant;
import com.liangyou.domain.Area;
import com.liangyou.domain.User;
import com.liangyou.domain.Userinfo;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.util.NumberUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;



@Namespace("/member/userinfo")
@ParentPackage("p2p-default") 
@Results({    
	  @Result(name="member", type="redirect",location="/member/index.html")
	}) 
public class UserinfoAction extends BaseAction implements ModelDriven<Userinfo>{

	private final static Logger logger=Logger.getLogger(UserinfoAction.class);
	
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private UserService userService;
	private Userinfo userinfo = new Userinfo();
	private User identifyUser = new User();
	public User getIdentifyUser() {
		return identifyUser;
	}

	public void setIdentifyUser(User identifyUser) {
		this.identifyUser = identifyUser;
	}
	
	@Action(value="userinfo",results={
			@Result(name="success", type="ftl",location="/member/userinfo/userinfo.html")
			})
	public String userinfo() throws Exception {
		User user = getSessionUser();
		long user_id = user.getUserId();
		Userinfo info = null;
		// 根据type区分是否查询或者更新-
		String type = request.getParameter("type");
		if (type == null || type.equals("")) {
			info = userinfoService.getUserinfoByUserId(user_id);
		} else {
			info = userinfoService.getUserinfoByUserId(user_id);
			info.setAddress(this.userinfo.getAddress());
			info.setMarry(this.userinfo.getMarry());
			info.setChild(this.userinfo.getChild());
			info.setEducation(this.userinfo.getEducation());
			info.setIncome(this.userinfo.getIncome());
			info.setShebao(this.userinfo.getShebao());
			info.setShebaoid(this.userinfo.getShebaoid());
			info.setHousing(this.userinfo.getHousing());
			info.setCar(this.userinfo.getCar());
			info.setLate(this.userinfo.getLate());
			userinfoService.updateUserinfo(info, user_id);
			
			//修改 出生日期，籍贯,性别
			user.setBirthday(identifyUser.getBirthday());
			user.setSex(identifyUser.getSex());
			user.setProvince(identifyUser.getProvince().getId() == 0? null:identifyUser.getProvince());
			user.setCity(identifyUser.getCity().getId()==0?null:identifyUser.getCity());
			user.setArea(identifyUser.getArea().getId()==0?null:identifyUser.getArea());
			//1.8.0.4_u1 TGPROJECT-278 lx start
			userService.updateUser(user);
			//1.8.0.4_u1 TGPROJECT-278 lx end
			// 读取更新后的个人资料
			info = userinfoService.getUserinfoByUserId(user_id);
			User updateuser = userService.getUserById(user_id);
			session.put(Constant.SESSION_USER, updateuser);

			logger.debug("userinfo:" + info);
			String msg = "修改个人信息成功！";
			String errormsg = checkUserinfo();
			if (errormsg.equals("")) {
				request.setAttribute("msg", msg);
			} else {
				request.setAttribute("errormsg", errormsg);
			}
		}
		request.setAttribute("info", info);
		session.put(Constant.SESSION_USER, userService.getUserById(user.getUserId()));
		return "success";
	}
	
	@Action(value="building",results={
			@Result(name="success", type="ftl",location="/member/userinfo/building.html")
			})
	public String building() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}

	@Action(value="company",results={
			@Result(name="success", type="ftl",location="/member/userinfo/company.html")
			})
	public String company() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}
	
	@Action(value="firm",results={
			@Result(name="success", type="ftl",location="/member/userinfo/firm.html")
			})
	public String firm() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}
	
	@Action(value="finance",results={
			@Result(name="success", type="ftl",location="/member/userinfo/finance.html")
			})
	public String finance() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}
	private String checkUserinfo() {
		String msg = "";
		/*if (StringUtils.isNull(userinfo.getRealname()).equals("")) {
			msg = "真实姓名不能为空！";
			return msg;
		}*/
		/*if (StringUtils.isNull(userinfo.getShebaoid()).equals("")&&StringUtils.isNull(userinfo.getShebao()).equals("")) {
			msg = "社保号不能为空！";
		}*/
		return msg;
	}
	
	@Action(value="contact",results={
			@Result(name="success", type="ftl",location="/member/userinfo/contact.html")
			})
	public String contact() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}

	@Action(value="mate",results={
			@Result(name="success", type="ftl",location="/member/userinfo/mate.html")
			})
	public String mate() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}

	@Action(value="education",results={
			@Result(name="success", type="ftl",location="/member/userinfo/education.html")
			})
	public String education() throws Exception {
		String result = updateUserinfoByType();
		return result;
	}

	@Action(value="other",results={
			@Result(name="success", type="ftl",location="/member/userinfo/other.html")
			})
	public String other() throws Exception {		
		String result = updateUserinfoByType();
		return result;
	}
	
	public String  updateUserinfoByType(){
		String type = request.getParameter("type");
		User user = getSessionUser();
		long user_id = user.getUserId();
		Userinfo info = null;
		String msg = "";
		if("building".equals(type)){
			if (this.userinfo !=null) {
				if (userinfo.getHouseRight1()+userinfo.getHouseRight2()>100) { // 校验填写房产资料时房产所有权相加不能大于100
					message("你输入房产信息中产权份额的总额超过100，请核实后在做修改！");
					return MSG;
				}
			}
			userinfoService.updateBuilding(this.userinfo, user_id);
			msg = "修改房产信息成功！";
		}
		if("company".equals(type)){
			userinfoService.updateCompany(this.userinfo, user_id);
			msg = "修改单位信息成功！";
		}
		if("firm".equals(type)){
			userinfoService.updateFirm(this.userinfo, user_id);
			msg = "修改私营业主信息成功！";
		}
		if("finance".equals(type)){
			userinfoService.updateFinance(this.userinfo, user_id);
			msg = "修改财务状况信息成功！";
		}
		if("contact".equals(type)){
			userinfoService.updateContact(this.userinfo, user_id);
			msg = "修改联系方式成功！";
		}
		if("mate".equals(type)){
			userinfoService.updateMate(this.userinfo, user_id);
			msg = "修改配偶资料成功！";
		}
		if("education".equals(type)){
			userinfoService.updateEducation(this.userinfo, user_id);
			msg = "修改教育背景成功！";
		}
//		if("other".equals(type)){
//			userinfoService.updateOther(this.userinfo, user_id);
//			msg = "修改其他信息成功！";
//		}
		info = userinfoService.getUserinfoByUserId(user_id);
		if(msg!=null && !"".equals(msg)){
			request.setAttribute("msg", msg);	
		}
		request.setAttribute("info", info);
		return "success";
	}
	
	@Action("showarea")
	public String showArea() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pid = (String) req.getParameter("pid");
		List<Area> areas = userinfoService.getAreaListByPid(NumberUtils.getInt(pid));		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}
	@Override
	public Userinfo getModel() {		
		return userinfo;
	}
	
}

