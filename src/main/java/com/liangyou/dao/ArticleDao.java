package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Article;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

public interface ArticleDao extends BaseDao<Article>{	
	
	public List getArticleList(int start,int pernum);
	public int countArticle();
	public PageDataList findPageListBySite(SearchParam param);
	public void updateHitCount(int id);
	public List getYearList();
}
