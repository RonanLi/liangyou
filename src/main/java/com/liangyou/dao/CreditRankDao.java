package com.liangyou.dao;

import com.liangyou.domain.CreditRank;


public interface CreditRankDao extends BaseDao<CreditRank>{
	CreditRank getCreditRankByName(String name);   //根据信用等级名称查询
	
	
}
