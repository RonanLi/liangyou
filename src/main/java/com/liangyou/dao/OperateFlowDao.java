package com.liangyou.dao;


import java.util.List;

import com.liangyou.domain.OperateFlow;

public interface OperateFlowDao extends BaseDao<OperateFlow> {
	public List<OperateFlow> getOperateFlowByType(String type);

	/**
	 * 查询当前类型 最大的排序
	 * 查询出来是启用的状态
	 * @param type
	 * @return
	 */
	int getMaxSortByType(String type);
	
	public OperateFlow getOperateFlow(String type,int sort);
	 
	/**
	 * 查询出此流程的的最后一个最大的排序，包括未启用的
	 * @param type
	 * @return
	 */
	int getMaxAllSortByType(String type);
	/**
	 *查询当前用户类型， 可用的流程
	 * @param userTypeId
	 * @return
	 */
	List<OperateFlow> getUsefullOperateFlowByUserType(Long userTypeId);
	/**
	 * 根据操作类型，查询第一个可用的流程。
	 */
	OperateFlow getFirstFlowByType(String type);
	/**
	 * 根据当前流程，获取下一个可用的流程 
	 * @param of
	 * @return
	 */
	OperateFlow getNextUsefullOperateFlow(OperateFlow of);
	/**
	 * 根据当前流程，获取上一个可用的流程 
	 * @param of
	 * @return
	 */
	OperateFlow getUpUsefullOperateFlow(OperateFlow of);

}
