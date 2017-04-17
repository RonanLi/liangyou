package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.domain.AccountOneKeyRecharge;
import com.liangyou.domain.AccountRecharge;
@Repository
public class AccountRechargeDaoImpl extends ObjectDaoImpl<AccountRecharge> implements AccountRechargeDao {
	private Logger logger =Logger.getLogger(AccountRechargeDaoImpl.class);

	@Override
	public AccountRecharge getRechargeByTradeno(String orderNo) {
		String sql = " from AccountRecharge where  tradeNo = ?1  ";
	    Query query =	em.createQuery(sql);
		query.setParameter(1, orderNo);
		return (AccountRecharge)query.getSingleResult();
	}
	
	@Override
	/**
	 * tradeNo  系统订单号
	 * serialNo 易极付处理产生的流水号
	 */
	public boolean updateRecharge(int status, String returnText, String tradeNo,String serialNo) {
		String sql=" update AccountRecharge set status=?1, return_txt=?2, serialNo=?3  where tradeNo=?4 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, status);
		query.setParameter(2, returnText);
		query.setParameter(3, serialNo);
		query.setParameter(4, tradeNo);
		return query.executeUpdate() != 0;
	}
	
	@Override
	public int  getCountBySerialNo(String serialNo) {
		String sql = " select count(AccountRecharge) from AccountRecharge where  returnTxt like ?1  ";
	    Query query =	em.createQuery(sql);
		query.setParameter(1, "'%"+serialNo+"%'");
		return Integer.parseInt(query.getSingleResult()+"");
	}

	@Override
	public List<AccountOneKeyRecharge> findAllRechargeUser() {
		String sqlString = "from AccountOneKeyRecharge";
		Query query = em.createQuery(sqlString);
		List resultList = query.getResultList();
		if(resultList.size() == 0 || resultList == null){
			return null;
		}
		return resultList;
	}
	
	@Override
	public void deleteAllRecharge() {
		// TODO Auto-generated method stub
		String sqlString = "delete from AccountOneKeyRecharge";
		Query query = em.createQuery(sqlString);
		query.executeUpdate();
	}

}
