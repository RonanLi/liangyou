package com.liangyou.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.domain.Article;
import com.liangyou.domain.Site;
import com.liangyou.domain.Warrant;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.ArticleService;
import com.liangyou.service.UserService;
import com.liangyou.service.WarrantService;
import com.liangyou.tool.Page;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Namespace("/article")
@ParentPackage("p2p-default")
public class ArticleAction extends BaseAction {

	private static Logger logger = Logger.getLogger(ArticleAction.class);
	@Autowired 
	private ArticleService articleService;
	@Autowired
	private UserService userService;
	@Autowired
	private WarrantService warrantService;
	/**
	 * 栏目页面显示
	 * @return
	 * @throws Exception
	 */
	@Action(value="list",
			results={@Result(name="list",type="ftl",location="/article/list.html")}
			)
	public String list() throws Exception {
		String code=paramString("code"); //栏目代码		
		String retType=paramString("retType");//数据类型:[json、action]
		int page=paramInt("page"); 
		int pageRow=paramInt("pageRow");
		pageRow=(pageRow<1)?Page.ROWS:pageRow;
		
		Site site=articleService.getSiteByCode(code);
		PageDataList data=null;
		Map<String,Object> map=new HashMap<String,Object>();
		if(retType.equals("json")){
			if(site!=null){
				data=articleService.getArticleList(site,page,pageRow);
				site.setArticles(null);
				for(Article a:(List<Article>)data.getList()){
					a.setSite(null);
					a.setArticleField(null);
					a.setComments(null);
				}
				map.put("site", site);
				map.put("data", data);
				map.put("msg", "success");
			}else{
				map.put("msg", "The code of site is invalid");
				logger.info("没有该栏目");
			}
			String json=JSON.toJSONString(map,true);
			printJson(json);
			return null;
		}else{
			if(articleService.checkSiteCode(code)){//如果没有当前栏目
				message("该栏目还未添加，请联系管理员！");
				return MSG;
			}
			data=articleService.getArticleList(site,page,pageRow);
			request.setAttribute("list", data.getList());
			Site psite=articleService.getSiteById(site.getPid());
			if(psite==null){
				psite=new Site();
				psite.setSiteId(0);
				psite.setName("首页");
			}
			SearchParam sp = SearchParam.getInstance()
					.addParam("pid", site.getPid())
					.addParam("status", 1)
					.addOrder(OrderType.ASC, "sort");			
			List<Site> subPageList=articleService.getSubSitePageDataList(sp);
			request.setAttribute("psite", psite);
			request.setAttribute("site", site);
			request.setAttribute("nid", site.getCode());
			request.setAttribute("sublist", subPageList);
			request.setAttribute("page", data.getPage());
			request.setAttribute("param", sp.toMap());
			return "list";
		}
	}
	@Action(value="detail",
			results={@Result(name="detail",type="ftl",location="/article/detail.html")}
			)
	public String detail() throws Exception {
		String code=paramString("code"); //栏目代码
		
		int id=NumberUtils.getInt(request.getParameter("id"));
		Article article = articleService.getArticle(id);
		if (article == null) {
			return  NOTFOUND;
		}
		
		//add by lxm 统计文章（网站公告和媒体报道）被点击次数   2017-3-14 10:53:19
		articleService.updateHitCount(id);
		logger.info("-------文章ID:"+article.getId()+",被点击次数："+(article.getHits()+1));
		
		Site site=articleService.getSiteByCode(code);
		Site psite=articleService.getSiteById(site.getPid());
		if(psite==null){
			psite=new Site();
			psite.setSiteId(0);
			psite.setName("首页");
		}
		List sublist=articleService.getSubSiteList(site.getPid());
		request.setAttribute("hits", article.getHits()+1);
		request.setAttribute("article", article);
		request.setAttribute("psite", psite);
		request.setAttribute("site", site);
		request.setAttribute("sublist", sublist);
		request.setAttribute("nid", psite.getNid());
		return "detail";
	}
	
	@Action(value="serve",
			results={@Result(name="success",type="ftl",location="/article/onlinekefu.html")}
			)
	public String serve() throws Exception {
		String code = StringUtils.isNull(request.getParameter("code"));
		List list=userService.getAllKefu();
		request.setAttribute("kflist", list);
		Site site=articleService.getSiteByCode(code);
		if(site==null){
			return NOTFOUND;
		}
		Site psite=articleService.getSiteById(site.getPid());
		if(psite==null){
			psite=new Site();
			psite.setSiteId(0);
			psite.setName("咨询服务");
		}
		List sublist = articleService.getSubSiteList(site.getSiteId());
		request.setAttribute("psite", psite);
		request.setAttribute("site", site);
		request.setAttribute("sublist", sublist);
		request.setAttribute("nid", psite.getNid());
		return SUCCESS;
	} 
	
	@Action(value="video",
			results={@Result(name="success",type="ftl",location="/article/video.html")}
			)
	public String vedio(){
		return SUCCESS;
	}
	
	@Action(value="showWrrants",
			results={@Result(name="wrrant",type="ftl",location="/article/wrrant.html")}
			)
	public String showWrrants() throws Exception {
		String code = StringUtils.isNull(request.getParameter("code"));
		Site site=articleService.getSiteByCode(code);
		if(site==null){
			return NOTFOUND;
		}
		Site psite=articleService.getSiteById(site.getPid());
		if(psite==null){
			psite=new Site();
			psite.setSiteId(0);
			psite.setName("咨询服务");
		}
		List sublist = articleService.getSubSiteList(site.getSiteId());
		String findType= StringUtils.isNull(paramString("findType"));
		String warrantName = StringUtils.isNull(paramString("name"));
		int page = paramInt("page")==0?1:paramInt("page");
		int pageNum = paramInt("pageNum")==0?Page.ROWS:paramInt("pageNum");
		
		SearchParam param = SearchParam.getInstance().addPage(page,pageNum);
		param.addParam("status", 1);
		if (findType.equals("byName")) {
			 param.addParam("name", Operator.LIKE, warrantName);
		}else if(findType.equals("byarea")){
			 param.addParam("area", Operator.LIKE, warrantName);
		}
	   
		PageDataList<Warrant> warrantList = warrantService.findListWarrant(param);
		setPageAttribute(warrantList, param);
		//这里查询担保机构
		request.setAttribute("psite", psite);
		request.setAttribute("site", site);
		request.setAttribute("sublist", sublist);
		request.setAttribute("nid", psite.getNid());
		return "wrrant";
	}
	
}
