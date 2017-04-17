package com.liangyou.web.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.domain.Site;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.ArticleService;

@Namespace("/about")
@ParentPackage("p2p-default")
public class AboutAction extends BaseAction {

    private static Logger logger = Logger.getLogger(AboutAction.class);

    @Autowired
    private ArticleService articleService;

    @Action(value = "xszd", results = {
            @Result(name = "flow", type = "ftl", location = "/about/flow.html"),
            @Result(name = "guide", type = "ftl", location = "/about/guide.html"),
            @Result(name = "newGuide", type = "ftl", location = "/about/newGuide.html"),
            @Result(name = "introduce", type = "ftl", location = "/about/introduce.html")
    })
    public String xszd() {
        return paramString("code");
    }

    @Action(value = "gywm", results = {
            @Result(name = "introduce", type = "ftl", location = "/about/introduce.html"),
            @Result(name = "webNews", type = "ftl", location = "/about/webNews.html"),
            @Result(name = "serviceAgreement", type = "ftl", location = "/about/serviceAgreement.html"),
            @Result(name = "joinus", type = "ftl", location = "/about/joinus.html"),
            @Result(name = "history", type = "ftl", location = "/about/history.html"),
    })
    public String gywm() {
    	//分页开始
    	PageDataList pageDataList = null;
    	PageDataList pageDataList2 = null;
    	int page = paramInt("page") == 0 ? 1 : paramInt("page");
		int pageNum = paramInt("pageNum") == 0 ? 5 : paramInt("pageNum");
		SearchParam param = null;
		SearchParam param2 = null;
		param = SearchParam.getInstance().addPage(page, pageNum);
		param2 = SearchParam.getInstance().addPage(page, pageNum);
		param.addParam("status", 1).addOrder(OrderType.DESC,"sort").addOrder(OrderType.DESC, "publish");
		param2.addParam("status", 1).addOrder(OrderType.DESC,"sort").addOrder(OrderType.DESC, "publish");
	
		List<Site> siteList = articleService.getSiteByCodes(new String[]{"mtbd"});
		if(siteList.size() > 0 && siteList != null){
			for (Site site : siteList) {
				param.addParam("site", site);
			}
		}
		List<Site> siteList2 = articleService.getSiteByCodes(new String[]{"webnotice"});
		if(siteList2.size() > 0 && siteList2 != null){
			for (Site site : siteList2) {
				param2.addParam("site",site);
			}
		}
		pageDataList = articleService.getArticleList(param);
		pageDataList2 = articleService.getArticleList(param2);
      
        setPageAttribute(pageDataList, param);
        setPageAttribute2(pageDataList2, param2);
        //分页结束
        return paramString("code");
    }
    
    @Action("nextPage")
    public void nextPage(){
    	int page = paramInt("page") == 0 ? 1 : paramInt("page");
		int pageNum = paramInt("pageNum") == 0 ? 5 : paramInt("pageNum");
		SearchParam param = null;
		SearchParam param2 = null;
    	PageDataList pageDataList = null;
    	PageDataList pageDataList2 = null;
    	param = SearchParam.getInstance().addPage(page, pageNum);
		param2 = SearchParam.getInstance().addPage(page, pageNum);
		//如果点了下一页
		String nidString = paramString("nid");
		if(nidString != null && nidString != ""){
			//前端出了字段需要根据字段查询分页
			if("mtbd".equals(nidString)){
				 try {
					 param.addOrFilter("nid", "mtbd");
					 pageDataList = articleService.getArticlePageList(param);
					 String json = JSON.toJSONStringWithDateFormat(pageDataList, "yyyy-MM-dd");
					ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
					ServletActionContext.getResponse().getWriter().write(json);
					ServletActionContext.getResponse().getWriter().flush();
					ServletActionContext.getResponse().getWriter().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if("webnotice".equals(nidString)){
				try {
					param2.addOrFilter("nid", "webnotice");
					pageDataList2 = articleService.getArticlePageList(param2);
					String json = JSON.toJSONStringWithDateFormat(pageDataList2, "yyyy-MM-dd");
					ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
					ServletActionContext.getResponse().getWriter().write(json);
					ServletActionContext.getResponse().getWriter().flush();
					ServletActionContext.getResponse().getWriter().close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
    }
    
    //add by lxm 金和历程 2017-4-6 11:49:02
    @Action(value = "jhsDevHistory", results = {@Result(name = "success", type = "ftl", location = "/about/history.html")})
	 public String jhsDevHistory(){
		 
		 //发展历程
		 SearchParam devParam = SearchParam.getInstance();
		 devParam.addParam("status", 1).addParam("typeId", 5);
		 devParam.addOrder(OrderType.DESC, "sort");
	 	 PageDataList dlist = articleService.getScrollPicList(devParam);
	 	 List yearList=articleService.getYearList();
	 	 request.setAttribute("yearList", yearList);
	 	 request.setAttribute("devHistoryList", dlist.getList());
	 	
	 	 //荣誉资质
	 	 SearchParam honParam = SearchParam.getInstance();
	 	 honParam.addParam("status", 1).addParam("typeId", 6);
	 	 honParam.addOrder(OrderType.DESC, "sort");
	 	 PageDataList hlist = articleService.getScrollPicList(honParam);
	 	 request.setAttribute("honorList", hlist.getList());
	 	 
	 	 //金和风采
	 	 SearchParam staParam = SearchParam.getInstance();
	 	 staParam.addParam("status", 1).addParam("typeId", 7);
	 	 staParam.addOrder(OrderType.DESC, "sort");
	 	 PageDataList slist = articleService.getScrollPicList(staParam);
	 	 request.setAttribute("staffList", slist.getList());
	 	 
	     return SUCCESS;
	 }
	 

}
