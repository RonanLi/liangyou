package com.liangyou.web.action.admin;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.liangyou.domain.SystemLog;
import com.liangyou.domain.SystemOperation;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.SystemLogService;
import com.liangyou.service.SystemOperationService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;


@Namespace("/admin/system/log")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class LogAction extends BaseAction {
	
	@Autowired
	private SystemLogService systemLogService;
	@Autowired
	private SystemOperationService systemOperationService;
	
	private SystemLog item;
	private SystemOperation item1;
	
	@Action(value="index",
			results={@Result(name="index",type="ftl",location="/admin/system/log/index.html")})
	public String index() {
		Map<String, Object> map = new HashMap<String, Object>();
		String userName = paramString("userName");
		String operationId = paramString("operationId");
		String dotime1 = paramString("dotime1");
		String dotime2 = paramString("dotime2");
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrder(OrderType.DESC,"id");
		if(!StringUtils.isBlank(userName)) {
			param.addParam("user.username", Operator.LIKE, userName);
			map.put("userName", userName);
		}
		if(!StringUtils.isBlank(operationId)) {
			param.addParam("systemOperation.id", Operator.EQ, Integer.parseInt(operationId));
			map.put("operationId", operationId);
		}
		if(!StringUtils.isBlank(dotime1)){
			map.put("dotime1", dotime1);
			dotime1+=" 00:00:00";
			param.addParam("addTime", Operator.GTE, DateUtils.getDate(dotime1, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(dotime2)){
			map.put("dotime2", dotime2);
			dotime2+=" 23:59:59";
			param.addParam("addTime", Operator.LTE, DateUtils.getDate(dotime2, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList<SystemLog> list = this.systemLogService.page(param);
		setPageAttribute(list, map);
		List<SystemOperation> list0 = this.systemOperationService.getAllOperationType();
		request.setAttribute("soList0", list0);
		return "index";
	}
	
	@Action("getSoByParentId")
	public String getSoByParentId() throws Exception {
		String parentId = paramString("parentId");
		if(org.apache.commons.lang3.StringUtils.isNotBlank(parentId)){
			item1 = new SystemOperation();
			item1.setParentId(Long.parseLong(parentId));
			List<SystemOperation> soListx = this.systemOperationService.list(item1);
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			String jo = JSONArray.toJSONString(soListx);
			out.print(jo);
			out.close();
		}
		return null;
	}
	
	@Action(value="soList",results={
			@Result(name="soList",type="ftl",location="/admin/system/log/soList.html")
	})
	public String soList() throws Exception {
		String parentId = paramString("parentId");
		SystemOperation item = new SystemOperation();
		if(org.apache.commons.lang3.StringUtils.isNotBlank(parentId)){
			item.setParentId(Long.parseLong(parentId));
		}
		List<SystemOperation> list = this.systemOperationService.list(item);
		request.setAttribute("soList", list);
		request.setAttribute("parentId", parentId);
		return "soList";
	}
	
	@Action(value="soAdd",
			results={@Result(name="soAdd",type="ftl",location="/admin/system/log/soAdd.html")})
	public String soAdd(){
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		String parentId = paramString("parentId");
		if (!StringUtils.isBlank(actionType)) {
			SystemOperation parent = null;
			if(org.apache.commons.lang3.StringUtils.isNotBlank(parentId)){
				parent = this.systemOperationService.find(Integer.parseInt(parentId));
			}
			if (parent != null) {
				item1.setLevel(parent.getLevel() + 1);
			} else {
				item1.setLevel(1);
			}
			this.systemOperationService.save(item1);
			message("新增成功！");
			return ADMINMSG;
		}
		request.setAttribute("parentId", parentId);
		return "soAdd";
	}
	
	@Action(value="soEdit",
			results={@Result(name="soEdit",type="ftl",location="/admin/system/log/soEdit.html")})
	public String soModify(){
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		if (!StringUtils.isBlank(actionType)) {
			this.systemOperationService.update(item1);
			message("修改成功");
			return ADMINMSG;
		}
		String id = paramString("id");
		if(org.apache.commons.lang3.StringUtils.isNotBlank(id)){
			item1 = systemOperationService.find(Integer.parseInt(id));
		}
		request.setAttribute("item1", item1);
		return "soEdit";
	}
	

	public SystemLog getItem() {
		return item;
	}

	public void setItem(SystemLog item) {
		this.item = item;
	}

	public SystemOperation getItem1() {
		return item1;
	}

	public void setItem1(SystemOperation item1) {
		this.item1 = item1;
	}

}
