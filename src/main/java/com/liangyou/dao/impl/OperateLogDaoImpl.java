package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.OperateLogDao;
import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.OperateLog;
@Repository(value="operateLogDao")
public class OperateLogDaoImpl extends ObjectDaoImpl<OperateLog> implements OperateLogDao {
	
	@Override
	public List<OperateLog> getAllOperateLogByOrdNo(long ordNo) {
		String jpql=" from OperateLog  where orderNo =?1 order by type";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordNo);
		List<OperateLog> listOpera=query.getResultList();
		return listOpera;
	}

	@Override
	public List<OperateLog> getOperateLogByOrdNo(long ordNo,String type) {
		String jpql=" from OperateLog  where orderNo =?1 and Type =?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordNo);
		query.setParameter(2, type);
		List<OperateLog> listOpera=query.getResultList();
		return listOpera;
	}
	
	@Override
	public OperateLog getLastOperateLogByOrdNo(String type, long orderNO){
		String  sql = " from OperateLog where  orderNo = ?1 and type = ?2  order by id desc ";
		Query query = em.createQuery(sql).setParameter(1, orderNO).setParameter(2, type);
		query.setMaxResults(1);
		query.setFirstResult(0);
		List<OperateLog> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	} 
	
	@Override
	public List<OperateLog>  getLastOperateLog(OperateFlow flow){
		String jpql=" from OperateLog a join a.operateFlow b where b.operater_type=?1 and (a.sort in (?2,?3) ) and   ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, flow);
		
		List<OperateLog> list=query.getResultList();
		return list;
	}
	
	/**
	 * 查询标进度
	 */
	@Override
	public OperateLog getLastOperateLogByOrdNo(long orderNO){
		String  sql = " from OperateLog where  orderNo = ?1 and type!='003' and type!='004' order by id desc ";
		Query query = em.createQuery(sql).setParameter(1, orderNO);
		query.setMaxResults(1);
		query.setFirstResult(0);
		List<OperateLog> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	} 
	
	/**
	 *查询标的所有进度
	 */
	@Override
	public List<OperateLog> getAllBorrowOperateLogByOrdNo(long ordNo) {
		String jpql=" from OperateLog  where orderNo =?1  and type!='003' and type!='004' order by type";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordNo);
		List<OperateLog> listOpera=query.getResultList();
		return listOpera;
	}
	
	/**
	 *查询还款计划的所有进度
	 */
	@Override
	public List<OperateLog> getAllRepayOperateLogByOrdNo(long ordNo) {
		String jpql=" from OperateLog  where orderNo =?1  and  type='004' order by type";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordNo);
		List<OperateLog> listOpera=query.getResultList();
		return listOpera;
	}
	
	/**
	 * 查询还款进度
	 */
	@Override
	public OperateLog getRepayLastOperateLogByOrdNo(long orderNO){
		String  sql = " from OperateLog where  orderNo = ?1 and type='004' order by id desc ";
		Query query = em.createQuery(sql).setParameter(1, orderNO);
		query.setMaxResults(1);
		query.setFirstResult(0);
		List<OperateLog> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	} 
		
}
