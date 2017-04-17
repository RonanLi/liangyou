package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.OperateFlowDao;
import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.UserType;
@Repository(value="operateFlowDao")
public class OperateFlowDaoImpl extends ObjectDaoImpl<OperateFlow> implements OperateFlowDao {

	@Override
	public List<OperateFlow> getOperateFlowByType(String type) {
		String jpql="from OperateFlow where operateType=?1 order by status,sort";
		Query query = em.createQuery(jpql);
		query.setParameter(1, type);
		List<OperateFlow> list=query.getResultList();
		return list;
	}
	
	@Override
	public OperateFlow getOperateFlow(String type,int sort) {
		String jpql="from OperateFlow where operateType=?1 and sort=?2 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, type);
		query.setParameter(2, sort);
		List<OperateFlow> list =query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public int getMaxSortByType(String type){
		String jpql = " select MAX(a.sort) from OperateFlow a where a.operateType=?1 and a.status = 1 ";
	    Query query  = em.createQuery(jpql);
	    query.setParameter(1, type);
	    List list = query.getResultList();
	    if(list.size() > 0){
	    	if("null".equals(list.get(0)+"")){
	    		return 0;
	    	}else{
	    		return Integer.parseInt(list.get(0) +"");
	    	}
	    }else{
	    	return 0;
	    }
	}
	
	/**
	 * 根据操作类型，查询第一个可用的流程。
	 */
	@Override
	public OperateFlow getFirstFlowByType(String type){
		String jpql = " from OperateFlow a where a.operateType=?1 and a.status = 1  order by a.sort asc ";
	    Query query  = em.createQuery(jpql);
	    query.setParameter(1, type);
	    query.setFirstResult(0).setMaxResults(1);
	    List list = query.getResultList();
	    if(list.size()>0){
	    	return (OperateFlow)list.get(0);
	    }else{
	    	return null;
	    }
	}

	@Override
	public int getMaxAllSortByType(String type) {
		String jpql = " select MAX(a.sort) from OperateFlow a where a.operateType=?1 ";
	    Query query  = em.createQuery(jpql);
	    query.setParameter(1, type);
	    List list = query.getResultList();
	    if(list.size() > 0){
	    	if("null".equals(list.get(0)+"")){
	    		return 0;
	    	}else{
	    		return Integer.parseInt(list.get(0) +"");
	    	}
	    }else{
	    	return 0;
	    }
	}
	
	/**
	 *查询当前用户类型， 可用的
	 * @param userTypeId
	 * @return
	 */
	@Override
	public List<OperateFlow> getUsefullOperateFlowByUserType(Long userTypeId){
		String sql = " from OperateFlow where  userType = ?1 and stastus = 1 ";
		Query query = em.createQuery(sql).setParameter(1, new UserType(userTypeId));
		return query.getResultList();
	}
	
	/**
	 * 根据当前流程，获取下一个可用的流程 
	 * @param of
	 * @return
	 */
	@Override
	public OperateFlow getNextUsefullOperateFlow(OperateFlow of){
		String jpql = " from OperateFlow a where a.operateType=?1 and a.status = 1 and a.sort >?2  order by a.sort asc ";
	    Query query  = em.createQuery(jpql);
	    query.setParameter(1, of.getOperateType());
	    query.setParameter(2, of.getSort());
	    query.setFirstResult(0).setMaxResults(1);
	    List list = query.getResultList();
	    if(list.size()>0){
	    	return (OperateFlow)list.get(0);
	    }else{
	    	return null;
	    }
	}
	
	/**
	 * 根据当前流程，获取上一个可用的流程 
	 * @param of
	 * @return
	 */
	@Override
	public OperateFlow getUpUsefullOperateFlow(OperateFlow of){
		String jpql = " from OperateFlow a where a.operateType=?1 and a.status = 1 and a.sort <?2  order by a.sort desc ";
	    Query query  = em.createQuery(jpql);
	    query.setParameter(1, of.getOperateType());
	    query.setParameter(2, of.getSort());
	    query.setFirstResult(0).setMaxResults(1);
	    List list = query.getResultList();
	    if(list.size()>0){
	    	return (OperateFlow)list.get(0);
	    }else{
	    	return null;
	    }
	}
	
	

}
