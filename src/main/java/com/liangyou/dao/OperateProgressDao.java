package com.liangyou.dao;

import com.liangyou.domain.OperateProgress;


public interface OperateProgressDao extends BaseDao<OperateProgress> {

	/**
	 * 根据类型和单号来查询，如果为空，要么全部完成，要么还没开始。
	 * @param type
	 * @param orderNo
	 * @return
	 */
	OperateProgress getOperateProgressByTypeAndOrderNo(String type, long orderNo);
	
	/**
	 * 根据类型，流程状态，查询出此流程是否有符合条件的流程操作
	 * @param operateType
	 * @param status
	 * @return
	 */
	int getCountByStatusAndOperateType(String operateType,int status);
	/**
	 * 根据单号来查询，
	 * @param orderNo
	 * @return
	 */
	public OperateProgress getOperateProgressByOrderNoAndStatus(long orderNo, int status);   
}
