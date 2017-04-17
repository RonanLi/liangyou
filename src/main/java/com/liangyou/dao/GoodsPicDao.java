package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.GoodsPic;

public interface GoodsPicDao extends BaseDao<GoodsPic> {
	/**
	 * 通过goodId查找goodspic
	 * @param id
	 * @return
	 */
	public List<GoodsPic> getGoodsPicByGoodsId(int id);
}
