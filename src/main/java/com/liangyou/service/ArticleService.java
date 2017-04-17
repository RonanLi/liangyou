package com.liangyou.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.liangyou.domain.Article;
import com.liangyou.domain.ScrollPic;
import com.liangyou.domain.Site;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.tree.SiteTree;

/**
 * 文章服务
 * 
 * @author 1432
 *
 */
@Service
public interface ArticleService {
	// 通过网站代码获取网站地址
	public Site getSiteByCode(String code);

	// 通过网站代码获取网站地址
	public List<Site> getSiteByCodes(String[] codes);

	// 通过id获取
	public Site getSiteById(int id);

	/**
	 * 获取states=1的网址信息
	 * 
	 * @param pid
	 * @return
	 */
	public List getSubSiteList(int pid);

	public SiteTree getSiteTree();

	public void modifySite(Site site);

	// 修改网址
	public void addSite(Site site);

	public void delSite(int siteId);

	public PageDataList getArticleList(int page);

	// 获取文章
	public Article getArticle(int id);

	public void addArticle(Article a, String files);

	public void delArticle(int id);

	public void modifyArticle(Article a, String url);

	public List getArticleFileds(int id);

	public Article getArticleByEname(String ename);

	/**
	 * 获得文章列表
	 * 
	 * @param site
	 * @param page
	 * @param row
	 * @return
	 */
	public PageDataList getArticleList(Site site, int page, int row);

	public PageDataList getArticleList2(List<Site> siteList, int page, int row);

	public boolean checkSiteCode(String code);

	public List<Site> getSubSitePageDataList(SearchParam param);

	public PageDataList getArticleList(SearchParam param);

	public PageDataList<ScrollPic> getScrollPicList(SearchParam param);

	public void delScrollPic(long id);

	public void modifyScrollPic(ScrollPic sp);

	/**
	 * 获取首页滚动图片
	 * 
	 * @param id滚动图片id
	 * @return
	 */
	public ScrollPic getScrollPicListById(long id);

	/**
	 * 添加滚动图片
	 * 
	 * @param sp
	 */
	public void addScrollPic(ScrollPic sp);

	public PageDataList getArticlePageList(SearchParam param);

	public void updateHitCount(int id);

	public List getYearList();
}
