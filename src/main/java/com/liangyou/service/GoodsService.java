package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Goods;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 商品服务
 * 
 * @author 1432
 *
 */
public interface GoodsService {
	public List<Goods> findAll();

	public Goods find(int id);

	public void update(Goods goods);

	public PageDataList<Goods> findPageList(SearchParam param);
}
