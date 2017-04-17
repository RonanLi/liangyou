package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.GoodsPicDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.Goods;
import com.liangyou.domain.GoodsPic;

@Repository("goodsPicDao")
public class GoodsPicDaoImpl extends ObjectDaoImpl<GoodsPic> implements GoodsPicDao {
	@Override
	public List<GoodsPic> getGoodsPicByGoodsId(int id){
		String jpql=" from GoodsPic where goods=?1 ";
		List<GoodsPic> list=new ArrayList<GoodsPic>();
		Query q=em.createQuery(jpql).setParameter(1, new Goods(id));
		list=q.getResultList();
		return list;
	}
}
