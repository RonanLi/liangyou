package com.liangyou.web.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.Purview;
import com.liangyou.domain.User;
import com.liangyou.domain.UserType;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.purview.PurviewCheck;
import com.liangyou.model.purview.PurviewCheckSet;
import com.liangyou.service.AuthService;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin/purview")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class PurviewAction extends BaseAction implements ModelDriven<Purview> {

	private static final Logger logger = Logger.getLogger(PurviewAction.class);

	@Autowired
	private AuthService authService;
	private Purview model = new Purview();
	List<Integer> firstIds = new ArrayList<Integer>();
	List<Integer> secondIds = new ArrayList<Integer>();
	List<Integer> thirdIds  = new ArrayList<Integer>();
	
	@Override
	public Purview getModel() {
		return model;
	}
	public List<Integer> getFirstIds() {
		return firstIds;
	}
	public void setFirstIds(List<Integer> firstIds) {
		this.firstIds = firstIds;
	}
	public List<Integer> getSecondIds() {
		return secondIds;
	}
	public void setSecondIds(List<Integer> secondIds) {
		this.secondIds = secondIds;
	}
	public List<Integer> getThirdIds() {
		return thirdIds;
	}
	public void setThirdIds(List<Integer> thirdIds) {
		this.thirdIds = thirdIds;
	}


	@Action(value="allPurview",results={
			@Result(name="success",type="ftl",location="/admin/purview/allpurview.html")
	})
	public String allPurview() throws Exception {
		int pid = NumberUtils.getInt(request.getParameter("pid"));
		List list = authService.getPurviewByPid(pid);
		setMsgUrl("/admin/purview/allPurview.html");
		request.setAttribute("list", list);
		request.setAttribute("pid", pid);
		return SUCCESS;
	}
	
	@Action(value="showAllRole",results={
			@Result(name="success",type="ftl",location="/admin/purview/allrole.html")
			})
	public String showAllRole() throws Exception {
		List userTypeList = authService.getAllUserType();
		request.setAttribute("list", userTypeList);
		return SUCCESS;
	}
	
	@Action(value="addRole",results={
			@Result(name="success",type="ftl",location="/admin/purview/addrole.html")
			})
	public String addRole() throws Exception {
		String actionType = StringUtils.isNull(request
				.getParameter("actionType"));
		String msg = "增加成功！";
		String name = request.getParameter("name");
		String purview = request.getParameter("purview");
		int type = NumberUtils.getInt(request.getParameter("type"));
		long sort = paramLong("sort");
		String remark = request.getParameter("remark");
		UserType userType = new UserType();
		userType.setName(name);
		userType.setPurview(purview);
		userType.setType(type);
		userType.setSort(sort);
		userType.setRemark(remark);
		if (!StringUtils.isBlank(actionType)) {
			try{
				authService.addUserType(userType);
			}catch(Exception e){
				logger.error(e);
			}			
			message(msg, "/admin/purview/showAllRole.html");
			User auth_user = getAuthUser();
			super.systemLogAdd(auth_user, 39, "管理员添加角色成功");
			return ADMINMSG;
		}
		return SUCCESS;
	}
	
	@Action(value="delRole",results={
			@Result(name="success",type="ftl",location="/admin/purview/delrole.html")
			})
	public String delRole() throws Exception {
		int typeId = NumberUtils.getInt(request.getParameter("typeId"));
		String msg = "删除成功！";
		try {
			authService.delUserType(typeId);
		} catch (Exception e) {
			logger.debug("delRole:" + e.getMessage());
			msg = "角色删除失败，可能原因：有管理员拥有该角色!";
		}
		message(msg, "/admin/purview/showAllRole.html");
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 39, "管理员删除角色成功");
		return ADMINMSG;
	}
	
	@Action(value="modifyRole",results={
			@Result(name="success",type="ftl",location="/admin/purview/modifyrole.html")
			})
	public String modifyRole() throws Exception {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		long typeId = paramLong("typeId");
		String name = request.getParameter("name");
		String purview = request.getParameter("purview");
		int type = NumberUtils.getInt(request.getParameter("type"));
		long sort = paramLong("sort");
		String remark = request.getParameter("remark");
		UserType userType = authService.getUserType(typeId);
		
		String msg = "修改成功！";
		if (!StringUtils.isBlank(actionType)) {
			if(typeId == 0 || StringUtils.isBlank(name) || type == 0){
				throw new ManageBussinessException("参数缺失");
			}
			userType.setTypeId(typeId);
			userType.setName(name);
			userType.setPurview(purview);
			userType.setType(type);
			userType.setSort(sort);
			userType.setRemark(remark);
			authService.modifyUserType(userType);
			message(msg, "/admin/purview/showAllRole.html");
			User auth_user = getAuthUser();
			super.systemLogAdd(auth_user, 39, "管理员修改角色成功");
			return ADMINMSG;
		}

		request.setAttribute("typeId", typeId);
		request.setAttribute("userType",userType);
		return SUCCESS;
	}
	
	@Action(value="setPurview",results={
			@Result(name="success",type="ftl",location="/admin/purview/setpurview.html")
			})
	public String setPurview() throws Exception {
		int user_typeid=NumberUtils.getInt(request.getParameter("user_typeid"));
		UserType userType = authService.getUserType(user_typeid);
		if(userType==null){
			message("该角色不存在","/admin/purview/showAllRole.html");
			return ADMINMSG;
		}
		if(!StringUtils.isBlank(getActionType())){
			for(int i=0;i<secondIds.size();i++){
				firstIds.add(secondIds.get(i));
			}
			for(int i=0;i<thirdIds.size();i++){
				firstIds.add(thirdIds.get(i));
			}
			authService.addUserTypePurviews(firstIds, user_typeid);//通过获取的id添加角色权限
		}
		List<Purview> open = authService.getAllCheckedPurview(user_typeid);//开通的权限
		List<Purview> all = authService.getAllPurview();//现在所有权限326个
		List<PurviewCheck> list = new ArrayList<PurviewCheck>();
		
		for(int i=0;i<all.size();i++){
			Purview purview = (Purview)all.get(i);
			boolean flag = false;
			for(int j=0;j<open.size();j++){
				Purview openPurview = (Purview)open.get(j);
					if(openPurview.getId()==purview.getId()){
						PurviewCheck pcTrue = new PurviewCheck();
						pcTrue.setChecked(true);
						pcTrue.setPurview(openPurview);
						list.add(pcTrue);
						flag = true;
						break;
					}
			}
			if(!flag){
				PurviewCheck pcFlase = new PurviewCheck();
				pcFlase.setChecked(false);
				pcFlase.setPurview(purview);
				list.add(pcFlase);				
			}
		}		
		
		PurviewCheckSet pcs = new PurviewCheckSet(list);
		Set set = pcs.toSet();	
		request.setAttribute("purviews", set);
		request.setAttribute("role", userType);
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 40, "管理员分配权限成功");
		return SUCCESS;
	}
	
	@Action(value="addPurview",results={
			@Result(name="success",type="ftl",location="/admin/purview/addpurview.html")
			})
	public String addPurview() throws Exception {
		String actionType = StringUtils.isNull(request
				.getParameter("actionType"));
		int pid = NumberUtils.getInt(request.getParameter("pid"));
		if (!StringUtils.isBlank(actionType)) {
			Purview p = authService.getPurview(pid);
			if (p != null) {
				model.setLevel(p.getLevel() + 1);
			} else {
				model.setLevel(1);
			}
			authService.addPurview(model);
			message("新增权限成功！");
			User auth_user = getAuthUser();
			super.systemLogAdd(auth_user, 40, "管理员添加权限成功");
			return ADMINMSG;
		}
		request.setAttribute("pid", pid);
		return SUCCESS;
	}

	@Action(value="delPurview",results={
			@Result(name="success",type="ftl",location="/admin/purview/addpurview.html")
			})
	public String delPurview() throws Exception {
		long id = NumberUtils.getLong(request.getParameter("id"));
		String msg = "删除成功！";
		try {
			authService.delPurview(id);
		} catch (Exception e) {
			logger.debug("delPurview:" + e.getMessage());
			msg = "权限删除失败，可能原因：有管理员拥有该权限!";
		}
		message(msg);
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 40, "管理员删除权限成功");
		return ADMINMSG;
	}

	@Action(value="modifyPurview",results={
			@Result(name="success",type="ftl",location="/admin/purview/modifypurview.html")
			})
	public String modifyPurview() throws Exception {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		int id = NumberUtils.getInt(request.getParameter("id"));
		if (!StringUtils.isBlank(actionType)) {
			authService.modifyPurview(model);
			message("修改权限成功");
			User auth_user = getAuthUser();
			super.systemLogAdd(auth_user, 40, "管理员修改权限成功");
			return ADMINMSG;
		}
		Purview p = authService.getPurview(id);
		request.setAttribute("p", p);
		return SUCCESS;
	}
}