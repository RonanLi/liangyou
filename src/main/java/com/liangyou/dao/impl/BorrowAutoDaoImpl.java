package com.liangyou.dao.impl;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowAutoDao;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.User;
@Repository(value="borrowAutoDao")
public class BorrowAutoDaoImpl extends ObjectDaoImpl<BorrowAuto> implements BorrowAutoDao {
	public List<User> getAutoTenderUsers(){
		String sql = " select * from view_auto_invest ";
		Query query = em.createNativeQuery(sql);
		List result = query.getResultList();
		if(result!=null){
			Iterator iterator = result.iterator(); 
			while( iterator.hasNext() ){ 
				Object[] row = ( Object[]) iterator.next();					
				double repayTotal = Double.parseDouble(row[0].toString());
				double repayInterest = Double.parseDouble(row[1].toString());
			} 
		} 
		return null;
	}

}
