package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowDetailTypeDao;
import com.liangyou.domain.BorrowDetailType;

/**
 *v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  
 */
@Repository(value="borrowDetailTypeDao")
public class BorrowDetailTypeDaoImpl extends ObjectDaoImpl<BorrowDetailType> implements BorrowDetailTypeDao {

	@Override
	public List<BorrowDetailType> getListByPid(String pid) {
		String jpql = " from BorrowDetailType where pid = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, pid);
		List<BorrowDetailType> list = query.getResultList();
		return list;
	}
}
