package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.liangyou.model.SearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ArticleDao;
import com.liangyou.dao.ArticleFieldDao;
import com.liangyou.dao.ScrollPicDao;
import com.liangyou.dao.SiteDao;
import com.liangyou.domain.Article;
import com.liangyou.domain.ScrollPic;
import com.liangyou.domain.Site;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.tree.SiteTree;
import com.liangyou.model.tree.Tree;
import com.liangyou.service.ArticleService;
import com.liangyou.tool.Page;

@Service(value="articleService")
@Transactional
public class ArticleServiceImpl implements ArticleService {
	@Autowired
	ArticleDao articleDao;
	@Autowired
	SiteDao siteDao;
	@Autowired
	ArticleFieldDao articleFieldDao;
	@Autowired
	ScrollPicDao scrollPicDao;
	@Override
	public Site getSiteByCode(String code) {
		return siteDao.getSiteByCode(code);
	}
	@Override
	public List<Site> getSiteByCodes(String[] codes) {
		return siteDao.getSiteByCodes(codes);
	}

	public Site getSiteById(int id) {
		return siteDao.find(id);
	}
	
	public SiteTree getSiteTree() {
		List<Site> list=siteDao.getSiteList();
		SiteTree tree=new SiteTree(null,new ArrayList<Tree>());
		for(Site s:list){
			SiteTree secTree=new SiteTree(s,new ArrayList<Tree>());
			List<Site> sublist=siteDao.getAllSubSiteList(s.getSiteId());
			for(Site ss:sublist){
				SiteTree subTree=new SiteTree(ss,null);
				secTree.addChild(subTree);
			}
			tree.addChild(secTree);
		}
		return tree;
	}
	
	public void modifySite(Site site) {
		siteDao.update(site);
	}
	
	public void addSite(Site site) {
		siteDao.save(site);
	}
	
	public void delSite(int siteId) {
		siteDao.delete(siteId);
	}
	
	public PageDataList getArticleList(int page) {
		int total=articleDao.countArticle();
		Page p=new Page(total,page,20);
		List list=articleDao.getArticleList(p.getStart(),p.getPernum());
		return new PageDataList(p,list);
	}

	public void addArticle(Article a,String files) {
		Article art=articleDao.merge(a);
	}
	
	public void delArticle(int id) {
		articleDao.delete(id);
	}
	
	public void modifyArticle(Article a,String files) {
		articleDao.update(a);
	}

	public Article getArticle(int id) {
		return articleDao.find(id);
	}
	
	public List getArticleFileds(int id) {
		return articleFieldDao.getAritcleFieldListByAid(id);
	}

	@Override
	public Article getArticleByEname(String ename) {
		return articleDao.findByPropertyForUnique("ename",ename);
	}

	@Override
	public PageDataList getArticleList(Site site, int page, int row) {
		SearchParam param=SearchParam.getInstance()
				.addParam("site",site)
				.addPage(0,page,row)
				.addParam("status",1)
				.addOrder(OrderType.DESC, "sort").
				addOrder(OrderType.DESC, "publish");
		PageDataList<Article> list=articleDao.findPageList(param);
		return list;
	}

	public PageDataList getArticleList2(List<Site> siteList, int page, int row) {
		SearchParam param = SearchParam.getInstance()
				.addOrFilter("site", siteList)
				.addParam("site",siteList.get(0))
				.addPage(0,page,row)
				.addParam("status",1)
				.addOrder(OrderType.DESC, "sort")
				.addOrder(OrderType.DESC, "publish");
		PageDataList<Article> list = articleDao.findPageList(param);
		return list;
	}

	@Override
	public boolean checkSiteCode(String code) {
		Site site  = siteDao.getSiteByCode(code);
		if(site==null){
			return true;
		}else{
			return false;
		}
	}
	
	public List getSubSiteList(int pid) {
		return siteDao.getSubSiteList(pid);
	}
	
	public List<Site> getSubSitePageDataList(SearchParam param) {
		return siteDao.findByCriteria(param);
	}

	@Override
	public PageDataList getArticleList(SearchParam param) {
		return articleDao.findPageList(param);
	}
	
	@Override
	public PageDataList<ScrollPic> getScrollPicList(SearchParam param) {
		return scrollPicDao.findAllPageList(param);
	}

	@Override
	public void delScrollPic(long id) {
		scrollPicDao.delete(id);
	}

	@Override
	public void modifyScrollPic(ScrollPic sp) {
		scrollPicDao.merge(sp);
	}

	@Override
	public ScrollPic getScrollPicListById(long id) {
		return scrollPicDao.find(id);
	}

	@Override
	public void addScrollPic(ScrollPic sp) {
		scrollPicDao.save(sp);
	}
	@Override
	public PageDataList getArticlePageList(SearchParam param) {
		
		return articleDao.findPageListBySite(param);
	}
	
	//add by lxm 统计文章（网站公告和媒体报道）被点击次数   2017-3-14 11:23:19
	@Override
	public void updateHitCount(int id) {
		articleDao.updateHitCount(id);;
		
	}
	
	//add by lxm 金和历程 2017-4-6 11:51:58
	@Override
	public List getYearList() {
		return articleDao.getYearList();
	}

	

}
