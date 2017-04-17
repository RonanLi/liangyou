package com.liangyou.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.GoodsDao;
import com.liangyou.domain.Goods;

@Repository("goodsDao")
public class GoodsDaoImpl extends ObjectDaoImpl<Goods> implements GoodsDao {
}
