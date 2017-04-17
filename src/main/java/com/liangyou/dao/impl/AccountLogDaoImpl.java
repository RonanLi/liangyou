package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AccountLogDao;
import com.liangyou.domain.AccountLog;
import com.liangyou.util.NumberUtils;

@Repository(value="accountLogDao")
public class AccountLogDaoImpl  extends ObjectDaoImpl<AccountLog> implements AccountLogDao  {
	
	@Override
	public double getSumRiskreserveFee(){
		String sql = " select ifnull(sum(money),0) as num from account_log where type='riskreserve_fee' ";
		List list = null;
		double num = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			list  = query.getResultList();
			Object ob = list.get(0);
			num = NumberUtils.getDouble(ob.toString());
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return num;
	}
}
