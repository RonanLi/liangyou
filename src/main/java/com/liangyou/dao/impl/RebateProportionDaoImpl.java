package com.liangyou.dao.impl;




import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.RebateProportionDao;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.RebateProportion;
/**
 * 
 * @author lx TGPROJECT-302 add
 *
 */
@Repository
public class RebateProportionDaoImpl extends ObjectDaoImpl<RebateProportion> implements RebateProportionDao {
	@Override
	public List<RebateProportion> getRebateProportionAllList(){
		String jpql = " from RebateProportion where status = 1";
		Query query = em.createQuery(jpql);
		List<RebateProportion> list = query.getResultList();
		return list;
	}
	
	
	

}
