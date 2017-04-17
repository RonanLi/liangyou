package com.liangyou.web.action.admin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.domain.MsgOperate;
import com.liangyou.domain.MsgOperateType;
import com.liangyou.domain.MsgTemplate;
import com.liangyou.domain.MsgType;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.msg.MsgOperateTree;
import com.liangyou.service.MsgService;
import com.liangyou.tool.Page;
import com.liangyou.util.NumberUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

/**
 * 后台消息管理action
 */

@Namespace("/admin/msg")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class MsgAction extends BaseAction implements  ModelDriven<MsgOperate> {

	private static Logger logger = Logger.getLogger(MsgAction.class);
	@Autowired
	private MsgService msgService;
	
	private MsgOperate model = new MsgOperate();
	
	private StringBuffer tree = new StringBuffer();
	private List<MsgOperateTree> moTree = new ArrayList<MsgOperateTree>();
	
	/**
	 * 业务配置模块
	 * @return
	 */
	private List<MsgOperate> all;
	@Action(value="operate",results={
			@Result(name="success", type="ftl",location="/admin/msg/operate.html")			
			})	
	public String operate()throws Exception {
		//动态读取权限
		int purview_id = paramInt("purview_id");
		int session_purview_id=0;
		if(session!=null && session.get("purview_id")!=null){
			session_purview_id = NumberUtils.getInt(session.get("purview_id").toString());
		}
		if(purview_id == 0 && session_purview_id!=0){
			session.put("purview_id", session_purview_id);
		}else{
			session.put("purview_id", purview_id);
		}
		
		
		return SUCCESS;
	}	
	
	@Action("getTreeData")
	public String getTreeData() throws Exception {
		MsgOperateTree gen = getTree();
		String json=JSON.toJSONString(gen,true);			
		printJson(json);
		return null;
	}
	
	@Action(value="getType",results={
			@Result(name="success", type="ftl",location="/admin/msg/addTemplate.html")			
			})	
	public String getType() throws Exception {
		List<MsgType> list = msgService.getAllType();		
		request.setAttribute("typeList", list);
		return SUCCESS;
	}
	
	@Action("getTypeListJson")
	public String getTypeListJson()throws Exception {
		List<MsgType> list = msgService.getAllType();
		List<MsgType> typeList = new ArrayList<MsgType>();
		for(int i=0;i<list.size();i++){			
			MsgType mt = (MsgType)list.get(i);
			MsgType type=new MsgType();
			type.setId(mt.getId());
			type.setName(mt.getName());
			type.setIsuse(mt.getIsuse());
			type.setCode(mt.getCode());
			typeList.add(type);
		}
		String json=JSON.toJSONString(typeList,true);			
		printJson(json);
		return null;
		
	}
	
	private MsgOperateTree getTree(){
		MsgOperate treeNode = msgService.getMsgOperate(1);		
		if(treeNode == null){
			return new MsgOperateTree(treeNode);
		}else{
			List<MsgOperate> children = msgService.getMsgOperateChilds(1);
			MsgOperateTree genNode = new MsgOperateTree(treeNode);//根节点
			genNode = fillData(genNode,children);
			return genNode;
		}
	}
	
	private MsgOperateTree fillData(MsgOperateTree parentNode,List<MsgOperate> children){
		for(MsgOperate mo : children){			
			MsgOperateTree node = null;			
			List<MsgOperate> childList = msgService.getMsgOperateChilds(mo.getId());
			if( (childList!=null) && (childList.size()> 0)){//如果有子节点
				node = new MsgOperateTree(mo);
				fillData(node,childList);
			}else{
				node = new MsgOperateTree(mo);
			}
			parentNode.addChildren(node);
		}
		return parentNode;
	}

	@Action(value="del",results={
			@Result(name="success", type="ftl",location="/admin/msg/operate.html")			
			})	
	public String del()throws Exception{
		int id = paramInt("id");
		List<MsgOperate> childs = msgService.getMsgOperateChilds(id);
		if(childs.size()>0) {//如果有子节点
			msgService.delMsgOperateChilds(childs);
			msgService.delMsgOperate(id);
		}else{
			msgService.delMsgOperate(id);
		}
		Map map = new HashMap();
		map.put("excuteCode", "success");
		String json=JSON.toJSONString(map,true);			
		printJson(json);
		return null;
	}
	
	@Action(value="add",results={
			@Result(name="success", type="ftl",location="/admin/msg/operate.html")			
			})	
	public String add()throws Exception{
		if(!StringUtils.isBlank(paramString("actionType"))){
			if(StringUtils.isBlank(model.getName())){
				throw new BussinessException("节点名称不能为空");
			}
			if(StringUtils.isBlank(paramString("pid"))){
				throw new BussinessException("父节点不存在");
			}
			MsgOperate parent = msgService.getMsgOperate(paramInt("pid"));
			if(StringUtils.isBlank(model.getName())){
				throw new ManageBussinessException("节点名称不能为空！");
			}
			if(StringUtils.isBlank(model.getCode())){
				throw new ManageBussinessException("编码不能为空！");
			}
			//验证code唯一行
			if(msgService.checkCodeExist(model.getCode())){
				throw new ManageBussinessException("编码已存在！");
			}
			model.setParent(parent);
			//保存数据到msg_operate
			MsgOperate mo =  msgService.addOrModifyMsgOperate(model);
			//
			int[] types = NumberUtils.getInts(request.getParameterValues("add_types"));			
			
			if(types.length >= 1){
				List<MsgOperateType> list = new ArrayList<MsgOperateType>();
				for(int i=0;i<types.length;i++){
					if(types[i]==0){
						continue;
					}
					MsgOperateType mt = new MsgOperateType();
					mt.setMsgOperate(mo);
					mt.setMsgType(new MsgType(types[i]));
					list.add(mt);
				}
				if(list.size()>=1){
					msgService.addMsgOperateTypeList(list);
				}				
			}
			
			message("新增成功！","/admin/msg/operate.html?");
			return ADMINMSG;
		}			
		return SUCCESS;
	}

	/**
	 * 根据id查询msgOperate对象
	 * @return
	 * @throws Exception
	 */
	@Action("show")	
	public String show()throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		int id = paramInt("id");
		MsgOperate mo = msgService.getMsgOperate(id);	
		if(mo == null){
			return null;
		}else{
			List<MsgOperateType> list = mo.getMsgOperateTypes();	
			mo.setMsgOperateTypes(null);
			map.put("operate", mo);
			List<MsgType> typeList = new ArrayList<MsgType>();
			for(int i=0;i<list.size();i++){
				MsgOperateType mot = (MsgOperateType)list.get(i);
				MsgType mt = mot.getMsgType();
				MsgType type=new MsgType();
				type.setId(mt.getId());
				type.setName(mt.getName());
				type.setIsuse(mt.getIsuse());
				type.setCode(mt.getCode());
				typeList.add(type);
			}
			//map.put("typeList", typeList);
			printJson(getStringOfJpaMap(map));			
			return null;
		}
	}

	@Action(value="modify",results={
			@Result(name="success", type="ftl",location="/admin/msg/operate.html")			
			})	
	public String modify()throws Exception{
		int id = paramInt("id");
		MsgOperate mo = msgService.getMsgOperate(model.getId());
		if(mo==null){
			throw new ManageBussinessException("请选择节点");
		}
		//--暂时处理
		List<MsgOperateType> motList = msgService.getMsgOperateTypeList(model.getId());		
		mo.setMsgOperateTypes(motList);
		//--
		List<MsgType> allType = msgService.getAllType();
		request.setAttribute("allType", allType);
		request.setAttribute("msgOperate",mo );
		if(!StringUtils.isBlank(paramString("actionType"))){
			if(StringUtils.isBlank(model.getName())){
				throw new ManageBussinessException("节点名称不能为空");
			}
			if(StringUtils.isBlank(paramString("pid"))){ 
				if(mo.getId()==1){
					throw new ManageBussinessException("您不能更改根节点");
				}
				throw new ManageBussinessException("父节点不存在");
			}
			MsgOperate parent = msgService.getMsgOperate(paramInt("pid"));
			model.setParent(parent);
			
			if(!StringUtils.isBlank(model.getCode()) ){//
				if(!model.getCode().equals(mo.getCode())){//不与旧编码一致
					//验证code唯一行
					if(msgService.checkCodeExist(model.getCode())){
						throw new ManageBussinessException("编码已存在！");
					}
				}
				
			}else{
				throw new ManageBussinessException("编码不能为空！");
			}
			msgService.addOrModifyMsgOperate(model);
			//	
			//MsgOperate sec = msgService.getMsgOperate(model.getId());
			//--暂时处理
			List<MsgOperateType> oldList = msgService.getMsgOperateTypeList(model.getId());
			if(oldList!=null && oldList.size()>0){
				msgService.delMsgOperateTypeList(oldList);
			}
			mo.setMsgOperateTypes(oldList);
			//--
					
			int[] types = NumberUtils.getInts(request.getParameterValues("mod_types"));
			List<MsgOperateType> list = new ArrayList<MsgOperateType>();
			for(int i=0;i<types.length;i++){
				if(types[i] == 0){
					break;
				}
				MsgOperateType mt = new MsgOperateType();
				mt.setMsgOperate(mo);
				mt.setMsgType(new MsgType(types[i]));
				for(int j=0;j<oldList.size();j++){//循环旧的关联数据
					MsgOperateType oldMt = oldList.get(j);
					//如果旧的数据有设置模板
					if(oldMt.getMsgOperate()!=null && oldMt.getMsgType()!=null && oldMt.getMsgTemplate()!=null && oldMt.getMsgOperate().getId()==mo.getId() && oldMt.getMsgType().getId()==types[i]){						
						mt.setMsgTemplate(oldMt.getMsgTemplate());//
					}
				}
				list.add(mt);
			}
			msgService.addMsgOperateTypeList(list);
			message("修改成功！","/admin/msg/operate.html");			
			return ADMINMSG;
		}
		
		return SUCCESS;
	}

	@Action(value="template",results={
			@Result(name="success", type="ftl",location="/admin/msg/template.html")			
			})	
	public String template()throws Exception{
		int page = paramInt("page");
		int perNum = paramInt("perNum")==0 ? Page.ROWS : paramInt("perNum");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);

		PageDataList pl = msgService.getTemplateList(param);
		setPageAttribute(pl, param);
		return SUCCESS;
	}
	
	@Action(value="addTemplate",results={
			@Result(name="success", type="ftl",location="/admin/msg/addTemplate.html"),
			@Result(name="template", type="ftl",location="/admin/msg/template.html")	
			})	
	public String addTemplate()throws Exception{
		if(StringUtils.isBlank(getActionType())){
			List<MsgType> typeList = msgService.getAllType();
			request.setAttribute("typeList", typeList);
			return SUCCESS;
		}
		String name = paramString("name");
		String content = paramString("content");
		int isuse = paramInt("isuse");
		int typeId = paramInt("typeId");
		String remark  = paramString("remark");
		MsgTemplate mt = new MsgTemplate();
		mt.setContent(content);
		mt.setIsuse(isuse);
		mt.setRemark(remark);
		mt.setName(name);
		MsgType type = new MsgType(typeId);
		mt.setMsgType(type);
		msgService.addTemplate(mt);
		message("新增成功", "/admin/msg/template.html");
		return ADMINMSG;
	}
	
	@Action(value="modifyTemplate",results={
			@Result(name="success", type="ftl",location="/admin/msg/modifyTemplate.html")			
			})	
	public String modifyTemplate()throws Exception{
		int id = paramInt("id");
		String name = paramString("name");
		String content = paramString("content");
		int isuse = paramInt("isuse");
		int typeId = paramInt("typeId");
		String remark  = paramString("remark");
		MsgTemplate mt = msgService.getTemplate(id);
		
		if(mt == null){
			throw new ManageBussinessException("序号为【"+id+"】的模板不存在", "admin/msg/template.html");
		}
		
		if(StringUtils.isBlank(getActionType())){
			List<MsgType> typeList = msgService.getAllType();
			request.setAttribute("typeList", typeList);
			request.setAttribute("template", mt);
			return SUCCESS;
		}
		
		//MsgTemplate mt = new MsgTemplate();		
		//mt.setId(id);
		mt.setContent(content);
		mt.setIsuse(isuse);
		mt.setRemark(remark);
		mt.setName(name);
		MsgType type = msgService.getType(typeId);
		mt.setMsgType(type);
		msgService.modifyTemplate(mt);
		message("修改成功", "/admin/msg/template.html");
		return ADMINMSG;
	}
	
	@Action(value="delTemplate",results={
			@Result(name="success", type="ftl",location="/admin/msg/template.html")			
			})	
	public String delTemplate()throws Exception{
		int id = paramInt("id");
		MsgTemplate mt = msgService.getTemplate(id);
		if(mt!=null){
			msgService.delTemplate(id);
			message("删除成功","/admin/msg/template.html");
			return ADMINMSG;
		}else{
			throw new ManageBussinessException("该序号的模板不存在", "/admin/msg/template.html");
		}
		
	}
	
	
	@Action(value="type",results={
			@Result(name="success", type="ftl",location="/admin/msg/type.html")			
			})	
	public String type()throws Exception{
		int page = paramInt("page");
		int perNum = paramInt("perNum")==0 ? Page.ROWS : paramInt("perNum");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);
		PageDataList pl = msgService.getTypeList(param);
		setPageAttribute(pl, param);
		return SUCCESS;
	}
	
	@Action(value="viewType",results={
			@Result(name="success", type="ftl",location="/admin/msg/type.html")			
			})	
	public String viewType()throws Exception{
		int id = paramInt("id");
		String name = paramString("name");
		int isuse = paramInt("isuse");
		MsgType type = msgService.getType(id);
		if(type == null){
			throw new ManageBussinessException("该类型不存在！", "/admin/msg/type.html");
		}
		request.setAttribute("msgType", type);
		return SUCCESS;
	}
	
	@Action(value="addType",results={
			@Result(name="success", type="ftl",location="/admin/msg/addType.html")			
			})	
	public String addType()throws Exception{
		if(!StringUtils.isBlank(paramString("actionType"))){
			String name = paramString("name");
			String code = paramString("code");
			MsgType mt = msgService.getTypeByCode(code);
			if(mt != null){
				throw new ManageBussinessException("类型编码重复！", "/admin/msg/addType.html");
			}
			int isuse = paramInt("isuse");
			MsgType type = new MsgType();
			type.setName(name);
			type.setCode(code);
			type.setIsuse(isuse);
			msgService.addType(type);
			message("新增成功","/admin/msg/type.html");	
			return ADMINMSG;
		}		
		return SUCCESS;
	}
	
	@Action(value="modifyType",results={
			@Result(name="success", type="ftl",location="/admin/msg/modifyType.html")			
			})	
	public String modifyType()throws Exception{
		int id = paramInt("id");
		String name = paramString("name");
		String code = paramString("code");
		int isuse = paramInt("isuse");
		
		MsgType type = msgService.getType(id);
		if(type == null){
			throw new ManageBussinessException("该类型不存在！", "/admin/msg/type.html");
		}
		
		if(!StringUtils.isBlank(paramString("actionType"))){			
			if(StringUtils.isBlank(code)){
				throw new ManageBussinessException("编码不能为空！", "/admin/msg/type.html");
			}
			
			type.setName(name);
			type.setCode(code);
			type.setIsuse(isuse);
			msgService.modifyType(type);
			message("修改成功","/admin/msg/type.html");
			return ADMINMSG;
		}
		request.setAttribute("msgType",type);
		return SUCCESS;
	}
	
	
	@Action(value="delType",results={
			@Result(name="success", type="ftl",location="/admin/msg/type.html")			
			})	
	public String delType()throws Exception{
		int id =  paramInt("id");
		MsgType type = msgService.getType(id);
		if(type == null){
			throw new ManageBussinessException("该类型不存在！", "/admin/msg/type.html");
		}
		msgService.delType(id);
		message("删除成功","/admin/msg/type.html");
		return ADMINMSG;
	}
	
	@Action(value="bindTemplate",results={
			@Result(name="success", type="ftl",location="/admin/msg/bindTemplate.html")			
			})	
	public String bindTemplate()throws Exception{
		int page = paramInt("page");
		int perNum = paramInt("perNum")==0 ? Page.ROWS : paramInt("perNum");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);

		PageDataList pl = msgService.getMsgOperateTypeList(param);
		setPageAttribute(pl, param);
		return SUCCESS;
	}
	
	@Action(value="modifyBindTemplate",results={
			@Result(name="success", type="ftl",location="/admin/msg/modifyBindTemplate.html")			
			})	
	public String modifyBindTemplate()throws Exception{
		int id = paramInt("id");
		int templateId = paramInt("templateId");
		MsgOperateType mot = msgService.getMsgOperateType(id);
		if(mot == null){
			throw new ManageBussinessException("序号为["+id+"]的数据不存在！", "/admin/msg/bindTemplate.html");
		}
		
		if(!StringUtils.isBlank(paramString("actionType"))){			
			MsgTemplate mt = msgService.getTemplate(templateId);
			if(mt == null){
				throw new ManageBussinessException("请先添加模板！", "/admin/msg/bindTemplate.html");
			}
			mot.setMsgTemplate(mt);
			msgService.modifyMsgOperateType(mot);
			message("绑定模板成功","/admin/msg/bindTemplate.html");
			return ADMINMSG;
		}
		List<MsgTemplate> templateList = mot.getMsgType().getMsgTemplates();
		request.setAttribute("msgOperateType", mot);
		request.setAttribute("templateList", templateList);
		return SUCCESS;
	}
	
	
	@Override
	public MsgOperate getModel() {
		return model;
	}
}
