package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.DrawBankDao;
import com.liangyou.domain.DrawBank;

@Repository
public class DrawBankDaoImpl extends ObjectDaoImpl<DrawBank> implements DrawBankDao {
	@Override
	public DrawBank getDrawBankByBankCode(String bankCode){
		String sql = " from DrawBank where bankCode = ?1";
		Query query = em.createQuery(sql);
		query.setParameter(1, bankCode);
		List<DrawBank> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
