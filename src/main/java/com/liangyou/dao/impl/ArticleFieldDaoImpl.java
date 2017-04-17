package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.ArticleFieldDao;
import com.liangyou.domain.ArticleField;


@Repository
public class ArticleFieldDaoImpl extends ObjectDaoImpl<ArticleField>  implements ArticleFieldDao {

	private static Logger logger = Logger.getLogger(ArticleFieldDaoImpl.class);

	public List getAritcleFieldListByAid(int aid) {
		String sql = "from ArticleField where aid=?1";
		Query query  = em.createQuery(sql);
		query.setParameter(1, aid);
		List list = query.getResultList();
		return list;
	}
}
