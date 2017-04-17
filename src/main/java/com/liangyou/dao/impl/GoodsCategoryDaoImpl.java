package com.liangyou.dao.impl;

import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.GoodsCategoryDao;
import com.liangyou.domain.GoodsCategory;

@Repository("goodsCategoryDao")
public class GoodsCategoryDaoImpl extends ObjectDaoImpl<GoodsCategory> implements GoodsCategoryDao {
	@Override
	public List<GoodsCategory> getListByParentId(int parentId) {
		String sql = "  from GoodsCategory where parentId = ?1 ";
		Query query = em.createQuery(sql).setParameter(1, parentId);
		return (List<GoodsCategory>)query.getResultList();
	}  
	
	@Override
	public List<GoodsCategory> getChildList(){
		String sql = "  from GoodsCategory where parentId <> 0 ";
		Query query = em.createQuery(sql);
		return (List<GoodsCategory>)query.getResultList();
	}
	
}
