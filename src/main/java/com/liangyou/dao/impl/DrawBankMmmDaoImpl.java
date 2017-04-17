package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.DrawBankMmmDao;
import com.liangyou.domain.DrawBankMmm;

@Repository("drawBankMmmDao")
public class DrawBankMmmDaoImpl extends ObjectDaoImpl<DrawBankMmm> implements DrawBankMmmDao {
	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 start
	@Override
	public DrawBankMmm getDrawBankMmmByBankCode(String bankCode) {
		String sql = " from DrawBankMmm where bankCode = ?1";
		Query query = em.createQuery(sql);
		query.setParameter(1, bankCode);
		List<DrawBankMmm> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 end
}
