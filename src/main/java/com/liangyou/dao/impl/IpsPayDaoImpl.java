package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.IpsPayDao;
import com.liangyou.domain.IpsPay;

@Repository
public class IpsPayDaoImpl extends ObjectDaoImpl<IpsPay> implements IpsPayDao {

	@Override
	public IpsPay getIpsPayByOrdId(String ordId) {
		String jpql = "from IpsPay where ordId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordId);
		List list = query.getResultList();
		if (list.size()>0) {
			return (IpsPay)list.get(0);
		}
		return null;
	}

	@Override
	public void updateIpsStatus(String status, String ordId,String code ,String reMsg) {
		String jpql = "update IpsPay set status =?1 ,set resultMsg=?2 ,set returnCode = ?3  where ordId = ?4";
		Query query = em.createQuery(jpql);
		query.setParameter(1, status);
		query.setParameter(2, reMsg);
		query.setParameter(3, code);
		query.setParameter(4, ordId);
		int updateResult  = query.executeUpdate();
		logger.info("更新环迅支付记录结果：订单号："+ordId+"状态："+status+",更新条数："+updateResult);
		
	}
	
	

}
