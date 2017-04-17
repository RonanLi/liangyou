package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.OperateLog;

public interface OperateLogDao extends BaseDao<OperateLog> {
	/**
	 * 
	 * @param 更具ordno查询出所有审核记录
	 * @return
	 */
	public List<OperateLog> getAllOperateLogByOrdNo(long ordNo);
	public List<OperateLog> getOperateLogByOrdNo(long ordNo,String type);  //根据operateLog中的ordno来查询出此步骤所有的操作例如borrowid等

	/**
	 * 获取最后一条记录。
	 * @param orderNO
	 * @return
	 */
	OperateLog getLastOperateLogByOrdNo(String type, long orderNO);

	public List<OperateLog>  getLastOperateLog(OperateFlow flow);
	
	public OperateLog getLastOperateLogByOrdNo(long orderNO);
	/**
	 *查询还款进度
	 */
	public OperateLog getRepayLastOperateLogByOrdNo(long orderNO);
	/**
	 *查询标的所有进度
	 */
	public List<OperateLog> getAllBorrowOperateLogByOrdNo(long ordNo);
	
	/**
	 *查询还款计划的所有进度
	 */
	public List<OperateLog> getAllRepayOperateLogByOrdNo(long ordNo);
}
