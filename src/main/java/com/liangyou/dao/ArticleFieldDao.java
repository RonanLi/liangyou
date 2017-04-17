package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.ArticleField;

public interface ArticleFieldDao extends BaseDao<ArticleField>{	
	
	public List getAritcleFieldListByAid(int aid);
}
