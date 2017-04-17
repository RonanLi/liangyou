package com.liangyou.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.WebGlodLogDao;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.util.NumberUtils;

@Repository
public class WebGlodLogDaoImpl  extends ObjectDaoImpl<WebGlodLog> implements WebGlodLogDao  {

	@Override
	public int sumWebLogByOrdid(String ordid) {
		String jpql = "SELECT COUNT(w) FROM WebGlodLog w WHERE  ordId=?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordid);
		Object ob = query.getSingleResult();
		if (ob !=null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}
	
}
