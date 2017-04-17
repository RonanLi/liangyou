package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.GoodsCategory;
/**
 * 
 * @author lx
 *
 */
public interface GoodsCategoryDao extends BaseDao<GoodsCategory> {

	
	public List<GoodsCategory> getListByParentId(int parentId);
	public List<GoodsCategory> getChildList();
}
