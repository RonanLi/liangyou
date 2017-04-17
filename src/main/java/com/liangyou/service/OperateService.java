package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.OperateLog;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;

/**
 * 操作服务
 * 
 * @author 1432
 *
 */
public interface OperateService {
	/** 关于operate的操作分割 */
	public void addOperateFlow(OperateFlow operaFlow); // 添加流程

	public void deleteOperateFlow(int id); // 删除流程

	public void updateOperateFlow(OperateFlow operateFlow); // 更新流程

	public List<OperateFlow> getOperateFlowByType(String type); // 更具type查找出一个操作的所有子步骤

	public OperateFlow getOperateById(long id);// 更具id查询

	public int getMaxSortByType(String type); // 查询最后一个操作

	public OperateFlow getUseOperateFlowBytypeSort(String type, int sort); // 根据type和传入的sort，查找下一个启用的operateFlow对象，过滤掉中间不启用的流程

	public List<OperateFlow> getOperateFlowList(SearchParam searchParam);

	public List<OperateProgress> getOperateProgressList(SearchParam searchParam);

	public OperateFlow getOperateFlowBytypeSort(String type, int sort); // 根据type和sort，查找一个operateFlow对象

	public int getMaxAllOperateSort(String type); // 获取此类型审核的最后一个顺序数

	/** 关于步骤操作记录的方法 */
	public void addOperateLog(OperateLog operateLog); // 添加操作流程记录

	public void deleteOperateLog(int id); // 删除操作记录

	public void updateOperateLog(OperateLog operateLog); // 更新操作记录

	/**
	 * 更具ordno查询出所有审核记录
	 * 
	 * @param id
	 * @return
	 */
	public List<OperateLog> getAllOperateLogByOrdNo(Borrow b);

	/**
	 * 查询一个操作，所有的审核步骤
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<OperateLog> getOperateLogByOrdNo(long id, String type);

	/**
	 * 根据orderno 查询最后一条记录
	 * 
	 * @param orderNO
	 * @return
	 */
	OperateLog getLastOperateLogByOrdNo(String type, long orderNO);

	/**
	 * 根据操作类型查询所有的流程。
	 * 
	 * @param type
	 * @return
	 */
	List<OperateFlow> getOperateFlowListByType(String type);

	/**
	 * 流程控制步骤： 1、根据用户的user, 查询用户类型， 用户的可用的操作步骤有哪些。 2、根据type, orderNo 查询
	 * 查询当前的进度，表 operate_progress 3、判断是否有权限进行此操作。 4、根据status
	 * 进一步做处理，0：返回上一步；1：成功处理； 2： 返回上一步。
	 */
	public boolean doOperate(String type, long id, User user, String memo,
			int status);

	/**
	 * @param type
	 * @param status
	 * @return
	 */
	public int getCountOperateByTypeAndStatsu(String type, int status);

	/**
	 * 根据当前类型和 id 查询出唯一的进程
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	OperateProgress getOperateProgressByTypeAndOrderNo(String type, long id);

	/**
	 * 更新指定流程
	 * 
	 * @param op
	 */
	public void updateOPerateProgress(OperateProgress op);

	/**
	 * 判断用户能否，取消标
	 * 
	 * @param orderNo
	 * @return
	 */
	public boolean checkBorrowCanCancel(long orderNo);

	// v1.8.0.4 TGPROJECT-59 lx 2014-04-17 start
	public void updateOperateFlow(List<OperateFlow> operateFlowList);
	// v1.8.0.4 TGPROJECT-59 lx 2014-04-17 end
}
