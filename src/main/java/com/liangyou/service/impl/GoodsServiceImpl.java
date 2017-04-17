package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.GoodsDao;
import com.liangyou.domain.Goods;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.GoodsService;

@Service(value="goodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService {
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 start
	@Autowired
	private GoodsDao dao;
	@Override
	public List<Goods> findAll(){
		return (List<Goods>) dao.findAll();
	}
	@Override
	public Goods find(int id){
		return dao.find(id);
	}
	@Override
	public void update(Goods goods){
		dao.merge(goods);
	}
	
	@Override
	public PageDataList<Goods> findPageList(SearchParam param) {
		
		return dao.findPageList(param);
	}
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end
}
