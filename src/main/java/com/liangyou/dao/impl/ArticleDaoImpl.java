package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.ArticleDao;
import com.liangyou.domain.Article;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.tool.Page;

@Repository(value="articleDao")
public class ArticleDaoImpl extends ObjectDaoImpl<Article>  implements ArticleDao {

	private static Logger logger = Logger.getLogger(ArticleDaoImpl.class);

	@Override
	public List getArticleList(int start,int pernum) {
		String sql = "select a from Article a left join a.site " +
				"order by a.sort asc, a.publish desc,a.id desc,a.addtime desc";
		Query query = em.createQuery(sql);
		query.setFirstResult(start).setMaxResults(pernum);		
		List list = query.getResultList();
		return list;
	}
	@Override
	public int countArticle() {
		String sql = "select a.id from Article a left join a.site";
		Query query  = em.createQuery(sql);
		int total = query.getResultList().size();
		return total;
	}
	@Override
	public PageDataList findPageListBySite(SearchParam param) {
		// 通过网址查询
		int count=countByMyCriteria(param);
		if(param.getPage()==null){
			param.addPage(count, 1, Page.ROWS);
		}
		param.addPage(count, param.getPage().getCurrentPage(), param.getPage().getPernum());
		List<Article> list=findByMyCriteria(param, param.getPage().getStart(), param.getPage().getPernum());
		return new PageDataList(param.getPage(),list);
	}
	private List<Article> findByMyCriteria(SearchParam param, int start,
			int pernum) {
		String sql = "SELECT a.name,a.publish,a.summary,a.id,a.litpic from Article as a,Site as s WHERE a.site.siteId = s.siteId and a.status = 1 and s.nid = ?1 order by a.sort desc,a.publish desc";
		Query query  = em.createQuery(sql);
		query.setFirstResult(start);
		query.setMaxResults(pernum);
		String nidValue = (String) param.map.get("nid");
		query.setParameter(1, nidValue);
		List<Article> queryList = query.getResultList();
		return queryList;
	}
	private int countByMyCriteria(SearchParam param) {
		//自定义条件查询 通过网址来查询  对应system表中 字段 nid的 value值
		String sql = "SELECT a.id from Article as a,Site as s WHERE a.site.siteId = s.siteId and a.status = 1 and s.nid = ?1";
		Query query  = em.createQuery(sql);
		String nidValue = (String) param.map.get("nid");
		query.setParameter(1, nidValue);
		int total = query.getResultList().size();
		return total;
	}
	
	/**
	 * add by lxm 统计文章（网站公告和媒体报道）被点击次数  2017-3-14 11:24:19
	 */
	@Override
	public void updateHitCount(int id) {
		String sql = "UPDATE Article as a SET a.hits=a.hits+1 WHERE a.id = ?";
		Query query  = em.createQuery(sql);
		query.setParameter(1, id);
		query.executeUpdate();
	}
	
	/**
	 * add by lxm 金和历程 2017-4-6 11:51:58
	 */
	@Override
	public List getYearList() {
		String sql="FROM ScrollPic as s WHERE s.typeId=5 GROUP BY s.year ORDER BY s.year DESC";
		Query query  = em.createQuery(sql);
		List yearList = query.getResultList();
		return yearList;
	}
	
}
