package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AccountCashDao;
import com.liangyou.domain.AccountCash;
import com.liangyou.util.NumberUtils;
@Repository
public class AccountCashDaoImpl extends ObjectDaoImpl<AccountCash> implements AccountCashDao {

	@Override
	public AccountCash getAccountCashByOrderNo(String orderNo) {
		String jpql = " from AccountCash where orderNo = ?1 ";
		List<AccountCash> list=new ArrayList<AccountCash>();
		Query q=em.createQuery(jpql).setParameter(1, orderNo);
		list=q.getResultList();
		if(list.size() == 0){
			return null;
		}else{
			return list.get(0);
		}
	}

	//TGPROJECT-410 wsl 2014-09-03 start
	@Override
	public double getAccountCashDailySum(long userId) {
		String jpql = "SELECT SUM(ac.total) FROM account_cash ac join user u WHERE ac.user_id=u.user_id" +
				" AND DATE(ac.addtime) = CURDATE() AND ac.user_id  = ?1  AND ac.status in (0,1) ";
		Query query =  em.createNativeQuery(jpql).setParameter(1, userId);
		Object ob = query.getSingleResult();
		if(ob != null){
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}
	//TGPROJECT-410 wsl 2014-09-03 end
	
}
