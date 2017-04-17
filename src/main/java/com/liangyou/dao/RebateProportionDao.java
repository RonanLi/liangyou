package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.RebateProportion;



/**
 * 
 * @author lx TGPROJECT-302 add
 *
 */
public interface RebateProportionDao extends BaseDao<RebateProportion> {
	/**
	 * 获取启用的RebateProportion
	 * @return
	 */
	List<RebateProportion> getRebateProportionAllList();
}
