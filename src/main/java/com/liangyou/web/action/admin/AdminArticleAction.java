package com.liangyou.web.action.admin;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.Article;
import com.liangyou.domain.ScrollPic;
import com.liangyou.domain.Site;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.tree.SiteTree;
import com.liangyou.service.ArticleService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.ImageUtil;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;


@Namespace("/admin/article")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminArticleAction extends BaseAction implements  ModelDriven<Article> {
	
	private static final Logger logger=Logger.getLogger(AdminArticleAction.class);

	@Autowired ArticleService articleService;
	private Article model = new Article();
	List<Integer> bid;
	List<Integer> hid;
	List<Integer> orders;
	int type;
	File files;
	String filesFileName;
	ScrollPic sp = new ScrollPic();
	File pic;
	private String filePath;
	private String sep = File.separator;
	
	public Article getModel() {
		return model;
	}
	//v1.8.0.4 TGPROJECT-66 lx 2014-04-22 start
	@Action(value="batchOperateArticle",results={
			@Result(name="success", type="ftl",location="/admin/article/showarticle.html")			
			})
		public String batchOperateArticle() throws Exception {
		if(bid!=null){
			if(type==2){
				for (Integer aId:bid) {
					Article a=articleService.getArticle(aId);
					if(a!=null){
						a.setStatus(1);
						articleService.modifyArticle(a, "");
					}
				}
				message("批量显示文章成功！","/admin/article/showArticle.html");
				return ADMINMSG;
			}
			if(type==3){
				for (Integer aId:bid) {
					Article a=articleService.getArticle(aId);
					if(a!=null){
					a.setStatus(0);
					articleService.modifyArticle(a, "");;
					}
				}
				message("批量隐藏文章成功！","/admin/article/showArticle.html");
				return ADMINMSG;
			}
			if(type==6){
				for (Integer aId:bid) {
					Article a=articleService.getArticle(aId);
					if(a!=null){
					articleService.delArticle(a.getId());;
					}
				}
				message("批量删除文章成功！","/admin/article/showArticle.html");
				return ADMINMSG;
			}
			message("批量操作文章成功！","/admin/article/showArticle.html");
			return ADMINMSG;
		}else{
			message("请选择批量操作的文章！","/admin/article/showArticle.html");
			return ADMINMSG;
		}
	}
	//v1.8.0.4 TGPROJECT-66 lx 2014-04-22 end
	@Action(value="showArticle",results={
			@Result(name="success", type="ftl",location="/admin/article/showarticle.html")			
			})
		public String showArticle() throws Exception {
		String status =paramString("status");//文章状态
		String siteId=paramString("siteId");//文章类型
		
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));
		if(!StringUtils.isBlank(status)){
			param.addParam("status",NumberUtils.getInt(status));
		}
		if(!StringUtils.isBlank(siteId)){
			param.addParam("site", new Site(NumberUtils.getInt(siteId)));
			request.setAttribute("siteId", NumberUtils.getInt(siteId));
		}
		PageDataList plist=articleService.getArticleList(param);
		//v1.8.0.4 TGPROJECT-66 lx 2014-04-22 start
		param.addParam("siteId", siteId);
		//v1.8.0.4 TGPROJECT-66 lx 2014-04-22 end
		setPageAttribute(plist, param);		
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		return SUCCESS;
	}
	
	@Action(value="addArticle",results={
			@Result(name="success", type="ftl",location="/admin/article/addarticle.html")			
			})
	public String addArticle() throws Exception {
		String actionType=StringUtils.isNull(request.getParameter("actionType"));
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		if(!StringUtils.isBlank(actionType)){
			int siteId = NumberUtils.getInt(request.getParameter("siteId"));
			if(siteId<1){
				message("请选择栏目!","/admin/article/addArticle.html");
				return ADMINMSG;
			}
			model.setUserId(1);
			Site site  = articleService.getSiteById(siteId);
			model.setSite(site);
			//处理附件
			String newUrl="";
			if(files!=null){
				String newFileName=generateUploadFilename(filesFileName);
				try {
					newUrl=upload(files, "", "/data/upload", newFileName);
				} catch (Exception e) {
					logger.error("上传文件出错："+e.getMessage());
				}
			}
			model.setLitpic(newUrl);
			if(model.getFlag()==null) {//防止查询出错
				model.setFlag("");
			}
			if(org.apache.commons.lang3.StringUtils.isNotBlank(model.getContent())){
				String content = model.getContent().replaceAll("\r\n", "\r\n\t");//增加转义，防止jquery ajax报parseerror
				model.setContent(content);
			}
			if(model.getAddtime() == null) {
				model.setAddtime(new Date());
			}
			if(model.getPublish()==null){
				model.setPublish(new Date());
			}
			
			articleService.addArticle(model,newUrl);
			message("新增文章成功！","/admin/article/showArticle.html");
			return ADMINMSG;
		}
		return SUCCESS;
	}

	@Action(value="modifyArticle",results={
			@Result(name="success", type="ftl",location="/admin/article/modifyarticle.html")			
			})
	public String modifyArticle() throws Exception{
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		int id=NumberUtils.getInt(request.getParameter("id"));
		int siteId = paramInt("site_id");
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		if(!StringUtils.isBlank(actionType)){
			if(siteId!=0){
				Site newSite = new Site();
				newSite.setSiteId(siteId);
				model.setSite(newSite);
			}
			model.setAddtime(new Date());
			model.setAddip(getRequestIp());
			model.setUserId(1);			
			//处理附件
			String newFileName=generateUploadFilename(filesFileName);
			String newUrl="";
		
			try {
				newUrl=upload(files, "", "/data/upload", newFileName);
			} catch (Exception e) {
				logger.error("上传文件出错："+e.getMessage());
			}
			if(!StringUtils.isBlank(newUrl)){
				model.setLitpic(newUrl);
			}else{
				Article a = articleService.getArticle(model.getId());
				model.setLitpic(a.getLitpic());
			}
			if(model.getFlag()==null) {//防止查询出错
				model.setFlag("");
			}else{
				model.setFlag(model.getFlag().replace(" ", ""));
			}
			if(org.apache.commons.lang3.StringUtils.isNotBlank(model.getContent())){
				String content = model.getContent().replaceAll("\r\n", "\r\n\t");//增加转义，防止jquery ajax报parseerror
				model.setContent(content);
			}
			
			//add by lxm 优化修改文章后该文章的点击次数重新开始计数  2017-3-16 10:51:18 
			Article a=articleService.getArticle(id);
			model.setHits(a.getHits());
			
			articleService.modifyArticle(model,newUrl);
			message("修改文章成功！","/admin/article/showArticle.html");
			return ADMINMSG;
		}
		Article a=articleService.getArticle(id);
		List files=articleService.getArticleFileds(a.getId());
		request.setAttribute("a", a);
		request.setAttribute("files", files);
		return SUCCESS;
	}
	
	@Action(value="delArticle",results={
			@Result(name="success", type="ftl",location="/admin/article/showarticle.html")			
			})
	public String delArticle() throws Exception {
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		int id=NumberUtils.getInt(request.getParameter("id"));
		articleService.delArticle(id);
		message("删除文章成功！","/admin/article/showArticle.html");
		return ADMINMSG;
	}
	
	
	
	/**
	 * 查看滚动图片
	 * 
	 * @return 
	 * @throws Exception 异常
	 */
	@Action(value="showScrollPic",results={
			@Result(name="success", type="ftl",location="/admin/article/scrollPic.html")			
			})
	public String showScrollPic() throws Exception {
		//modify by lxm 首页轮播图  2017-3-23 10:07:00
		//SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addParam("typeId", 1).addParam("status", 1);//typeId=1是首页轮播图 status=1启用
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));//全查
		PageDataList plist = articleService.getScrollPicList(param);
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		setPageAttribute(plist, param);
		return SUCCESS;
	}

	/**
	 * 滚动图片修改
	 * 
	 * @return forward页面
	 * @throws Exception 异常
	 */
	@Action(value="modifyScrollPic",results={
			@Result(name="success", type="ftl",location="/admin/article/modifyScrollPic.html")			
			})
	public String modifyScrollPic() throws Exception {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		long id = NumberUtils.getLong(request.getParameter("id"));
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		if (!StringUtils.isBlank(actionType)) {
			fillScrollPic(sp);//封装的对象，从页面获取的值
			Boolean isImage = ImageUtil.fileIsImage(pic);
			//modify by lxm 金和历程 2017-4-1 17:39:37
			if (sp.getTypeId() ==1 || sp.getTypeId() ==4  || sp.getTypeId() ==5 || sp.getTypeId() ==6 || sp.getTypeId() ==7) {//modify by lxm WAP端后台系统修改图片 2017-3-28 15:19:29
				if (this.pic == null) {
					ScrollPic scrollPic = articleService.getScrollPicListById(id);
					sp.setHits(scrollPic.getHits());//add by lxm 记录修改图片之前的点击次数 2017-3-28 14:30:10
					sp.setPic(scrollPic.getPic());
				} else if (isImage == false) {
					message("你上传的图片格式不符合要求，请重新上传", "/admin/article/showScrollPic.html");
					return ADMINMSG;
				} else {
					long a = pic.length();
					if(a>=1048576*3){
						message("你上传的图片大于3M，请更换成小于3M的图片！");
						return ADMINMSG;
					}
					moveFile(sp);
					sp.setPic(filePath);
				}
			} else if (sp.getTypeId() ==2 || sp.getTypeId() ==3) {
				ScrollPic scrollPic = articleService.getScrollPicListById(id);
				if (scrollPic.getPic() == null && this.pic == null && sp.getName().equals("")) {
					message("你上传的图片为空", "/admin/article/showScrollPic.html");
					return MSG;
				} else if (this.pic != null && isImage == false) {
					message("你上传的图片格式不符合要求，请重新上传", "/admin/article/showScrollPic.html");
					return ADMINMSG;
				} else if (this.pic != null) {
					long a = pic.length();
					if(a>=1048576*3){
						message("你上传的图片大于3M，请更换成小于3M的图片！");
						return ADMINMSG;
					}
					moveFile(sp);
					sp.setPic(filePath);
				}
			}
			if (sp.getTypeId() ==2 || sp.getTypeId() ==3) {
				if (this.pic != null) {
					articleService.modifyScrollPic(sp);
					initPic();
				}
			} else {
				articleService.modifyScrollPic(sp);
				initPic();
			}
			message("修改图片成功！", "/admin/article/showScrollPic.html");
			return ADMINMSG;
		}
		ScrollPic scrollPic = articleService.getScrollPicListById(id);
		request.setAttribute("scrollPic", scrollPic);
		return SUCCESS;
	}

	/**
	 * 滚动图片删除
	 * 
	 * @return forward页面
	 * @throws Exception
	 */
	@Action(value="deleteScrollPic",results={
			@Result(name="success", type="ftl",location="/admin/article/scrollPic.html")			
			})
	public String deleteScrollPic() throws Exception {
		long id = NumberUtils.getLong(request.getParameter("id"));
		articleService.delScrollPic(id);
		initPic();
		message("删除图片成功！", "/admin/article/showScrollPic.html");
		return ADMINMSG;
	}
	
	/**
	 * 滚动图片添加
	 * 
	 * @return forward页面
	 * @throws Exception 异常
	 */
	@Action(value="addScrollPic",results={
			@Result(name="success", type="ftl",location="/admin/article/addScrollPic.html")			
			})
	public String addScrollPic() throws Exception {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		SiteTree siteTree = articleService.getSiteTree();
		request.setAttribute("tree", siteTree);
		if (!StringUtils.isBlank(actionType)) {
			fillScrollPic(sp);//封装的对象，从页面获取的值
			logger.info(pic);
			Boolean isImage = ImageUtil.fileIsImage(pic);
			logger.info("isImage:" + isImage);
			//modify by lxm 金和历程 2017-4-1 17:40:11
			if (sp.getTypeId() ==1 || sp.getTypeId() ==4 || sp.getTypeId() ==5 || sp.getTypeId() ==6 || sp.getTypeId() ==7) { //modify by lxm type_id=4wap端的首页滚动图片   2017-3-27 15:07:49
				if (this.pic == null) {
					message("你上传的图片为空", "/admin/article/addScrollPic.html");
					return MSG;
				} else if (isImage == false) {
					message("你上传的图片格式不符合要求，请重新上传", "/admin/article/addScrollPic.html");
					return ADMINMSG;
				} 
				long a = pic.length();
				if(a>=1048576*3){
					message("你上传的图片大于3M，请更换成小于3M的图片！");
					return ADMINMSG;
				}
				moveFile(sp);
				sp.setPic(filePath);
			} else if (sp.getTypeId() ==2 || sp.getTypeId() ==3 ) { 
				if (this.pic == null && sp.getName().equals("")) {
					message("你上传的图片为空", "/admin/article/addScrollPic.html");
					return MSG;
				} else if (this.pic != null && isImage == false) {
					message("你上传的图片格式不符合要求，请重新上传", "/admin/article/addScrollPic.html");
					return ADMINMSG;
				} else if (this.pic != null) {
					long a = pic.length();
					if(a>=1048576*3){
						message("你上传的图片大于3M，请更换成小于3M的图片！");
						return ADMINMSG;
					}
					moveFile(sp);
					sp.setPic(filePath);
				}
			}
			articleService.addScrollPic(sp);
			initPic();
			request.setAttribute("scrollPic", sp);
			message("添加图片成功！", "/admin/article/showScrollPic.html");
			return ADMINMSG;
		}
		return SUCCESS;
	}

	private ScrollPic fillScrollPic(ScrollPic sp) {
		sp.setId(paramLong("id"));
		sp.setSort(paramInt("sort"));
		sp.setTypeId(paramInt("type_id"));
		sp.setUrl(paramString("url"));
		sp.setPic(paramString("pic"));
		sp.setStatus(paramInt("status"));
		sp.setName(paramString("name"));
		
		//add by lxm 金和历程 2017-4-1 11:05:04
		sp.setYear(NumberUtils.getInt(paramString("year")));
		sp.setTime(paramString("time"));
		sp.setContent(paramString("content"));
		
		return sp;
	}
	
	
	/**
	 * 存储图片
	 * @param scrollPic
	 */
	private void moveFile(ScrollPic scrollPic) {
		String dataPath = ServletActionContext.getServletContext().getRealPath("/data");//D:\jinhefax\jinhesuo\src\main\webapp\data
		String contextPath = ServletActionContext.getServletContext().getRealPath("/"); //D:\jinhefax\jinhesuo\src\main\webapp
		Date d1 = new Date();
		String upfiesDir = dataPath + sep + "upfiles" + sep + "images" + sep;           //D:\jinhefax\jinhesuo\src\main\webapp\data\ upfiles\images\           
		String destfilename1 = upfiesDir + DateUtils.dateStr2(d1) + sep + scrollPic.getId() + "_scrollPic" + "_"
				+ d1.getTime() + ".jpg";                                                //D:\jinhefax\jinhesuo\src\main\webapp\data\ upfiles\images\2017-03-15\0_scrollPic_1489558660696.jpg
		filePath = destfilename1;//                          /data/upfiles/images/2017-03-15/0_scrollPic_1489558660696.jpg
		filePath = this.truncatUrl(filePath, contextPath);// /data/upfiles/images/2017-03-15/0_scrollPic_1489558660696.jpg
		logger.info(destfilename1);
		File imageFile1 = null;
		try {
			imageFile1 = new File(destfilename1);
			FileUtils.copyFile(pic, imageFile1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}
	/**
	 * 截取url
	 * @param old
	 * @param truncat
	 * @return
	 */
	private String truncatUrl(String old, String truncat) {
		String url = "";
		url = old.replace(truncat, "");
		url = url.replace(sep, "/");
		return url;
	}
	
	public List<Integer> getBid() {
		return bid;
	}

	public void setBid(List<Integer> bid) {
		this.bid = bid;
	}

	public List<Integer> getHid() {
		return hid;
	}

	public void setHid(List<Integer> hid) {
		this.hid = hid;
	}

	public List<Integer> getOrders() {
		return orders;
	}

	public void setOrders(List<Integer> orders) {
		this.orders = orders;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}

	public String getFilesFileName() {
		return filesFileName;
	}

	public void setFilesFileName(String filesFileName) {
		this.filesFileName = filesFileName;
	}

	public void setModel(Article model) {
		this.model = model;
	}

	public ScrollPic getSp() {
		return sp;
	}

	public void setSp(ScrollPic sp) {
		this.sp = sp;
	}

	public File getPic() {
		return pic;
	}

	public void setPic(File pic) {
		this.pic = pic;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}
	
	/**
	 * 后台添加，删除，更新图片时，静态数据重新加载
	 */
	private void initPic() {
		// 首页滚动图片
		SearchParam picParam2 = SearchParam.getInstance();
		picParam2.addParam("status", 1).addParam("typeId", 1);
		picParam2.addOrder(OrderType.ASC, "sort");
		PageDataList plist2 = articleService.getScrollPicList(picParam2);
		context.setAttribute("scrollPic", plist2.getList());
		
		//add by lxm wap端  2017-3-27 15:07:16
		// WAP端首页滚动图片
		SearchParam picParam4 = SearchParam.getInstance();
		picParam4.addParam("status", 1).addParam("typeId", 4);
		picParam4.addOrder(OrderType.ASC, "sort");
	    PageDataList plist4 = articleService.getScrollPicList(picParam4);
	    context.setAttribute("wapScrollPic", plist4.getList());

		// 合作伙伴
		SearchParam picParam = SearchParam.getInstance();
		picParam.addParam("status", 1).addParam("typeId", 2);
		picParam.addOrder(OrderType.ASC, "sort");
		PageDataList plist = articleService.getScrollPicList(picParam);
		List cooperativePartnerPic = plist.getList();
		context.setAttribute("cooperativePartnerPic", cooperativePartnerPic);

		// 友情链接
		SearchParam picParam1 = SearchParam.getInstance();
		picParam1.addParam("status", 1).addParam("typeId", 3);
		picParam1.addOrder(OrderType.ASC, "sort");
		PageDataList plist1 = articleService.getScrollPicList(picParam1);
		List linksPic = plist1.getList();
		context.setAttribute("linksPic", linksPic);
	}
	
}