package com.liangyou.web.action.admin;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.Site;
import com.liangyou.model.tree.SiteTree;
import com.liangyou.service.ArticleService;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin/article")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class SiteAction extends BaseAction implements  ModelDriven<Site> {
	
	@Autowired
	private ArticleService articleService;
	private Site model=new Site();

	@Override
	public Site getModel() {
		return model;
	}

	@Action(value="showSite",results={
			@Result(name="success", type="ftl",location="/admin/article/site.html")			
			})
	public String showSite() throws Exception {
		Site site=articleService.getSiteById(model.getSiteId());
		if(site!=null){
			Site psite=articleService.getSiteById(site.getPid());
			request.setAttribute("site", site);
			if(psite==null){
				psite=new Site();
				psite.setName("根目录");
				psite.setPid(0);
			}
			request.setAttribute("psite", psite);
		}
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		return SUCCESS;
	}
	
	@Action(value="modifySite",results={
			@Result(name="success", type="ftl",location="/admin/article/site.html")			
			})
	public String modifySite() throws Exception {		
		articleService.modifySite(model);
		message("修改栏目成功！","/admin/article/showSite.html?siteId="+model.getSiteId());
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		Site psite=articleService.getSiteById(model.getPid());
		if(psite==null){
			psite=new Site();
			psite.setName("根目录");
			psite.setPid(0);
		}
		request.setAttribute("psite", psite);
		return ADMINMSG;
	}
	
	@Action(value="addSite",results={
			@Result(name="success", type="ftl",location="/admin/article/addsite.html")			
			})
	public String addSite() throws Exception {
		String actionType=request.getParameter("actionType");
		int pid=NumberUtils.getInt(request.getParameter("pid"));
				
		if(!StringUtils.isBlank(actionType)){
			if(pid==0){//如果一级栏目
				model.setPid(0);
			}else{
				model.setPid(pid);
			}
			//检测编码
			boolean haveCode = articleService.checkSiteCode(model.getCode());
			if(!haveCode){//如果编码已存在
				message("栏目编码重复，请重新输入！","/admin/article/showSite.html?siteId?"+model.getSiteId());
			}else{
				articleService.addSite(model);
				message("新增栏目成功！","/admin/article/showSite.html");
			}
			return ADMINMSG;
		}
		
		Site psite=articleService.getSiteById(pid);
		if(psite==null){
			psite=new Site();
			psite.setName("根目录");
			psite.setPid(0);
		}
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("psite", psite);
		request.setAttribute("tree", siteTree);
		return SUCCESS;
	}
	
	@Action(value="delSite",results={
			@Result(name="success", type="ftl",location="/admin/article/site.html")			
			})
	public String delSite() throws Exception {
		int siteId=NumberUtils.getInt(request.getParameter("siteId"));
		Site psite=articleService.getSiteById(siteId);
		if(psite!=null&&psite.getPid()>0){
			articleService.delSite(psite.getSiteId());
			message("删除成功","/admin/article/showSite.html");
			//return ADMINMSG;
		}else{
			message("不能删除一级栏目！","/admin/article/showSite.html");
		}
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		return ADMINMSG;
	}
	
}
