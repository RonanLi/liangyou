package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.OperateProgressDao;
import com.liangyou.domain.OperateProgress;
@Repository(value="operateProgressDao")
public class OperateProgressDaoImpl extends ObjectDaoImpl<OperateProgress> implements OperateProgressDao {
	
	/**
	 * 根据类型和单号来查询，如果为空,即为第一次操作
	 * @param type
	 * @param orderNo
	 * @return
	 */
	@Override
	public OperateProgress getOperateProgressByTypeAndOrderNo(String type, long orderNo){
			String sql = " from OperateProgress where  operateType = ?1 and orderNo = ?2 ";
			Query query = em.createQuery(sql).setParameter(1, type)
					.setParameter(2, orderNo);
			List<OperateProgress>  list = query.getResultList();
			if(list.size()>0){
				return list.get(0);
			}else{
				return null;
			}
	}
	
	/**
	 * 根据单号来查询，
	 * @param orderNo
	 * @return
	 */
	@Override
	public OperateProgress getOperateProgressByOrderNoAndStatus(long orderNo,int status){
			String sql = " from OperateProgress where orderNo = ?1 and status=?2 ";
			Query query = em.createQuery(sql).setParameter(1, orderNo).setParameter(2, status);
			List<OperateProgress>  list = query.getResultList();
			if(list.size()>0){
				return list.get(0);
			}else{
				return null;
			}
	}
	

	@Override
	public int getCountByStatusAndOperateType(String operateType, int status) {
		String jpql = "from OperateProgress where  operateType = ?1 and status = ?2";
		Query query = em.createQuery(jpql).setParameter(1, operateType).setParameter(2, status);
		List<OperateProgress> list = query.getResultList();
		if (list == null) {
			return 0;
		}else{
			return list.size();
		}
	}
	
	
}
