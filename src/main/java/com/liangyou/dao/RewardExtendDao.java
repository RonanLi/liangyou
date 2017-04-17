package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.RewardExtend;

/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz 
 * 奖励发放数据层
 * @author wujing
 *
 */
public interface RewardExtendDao extends BaseDao<RewardExtend> {
	
	/**
	 * 根据投资者id与奖励类型，判断是否发送过奖励
	 * @param tenderUserId  :投资者id
	 * @param type：奖励类型
	 * @return
	 */
	public RewardExtend getReward(long tenderUserId ,int type);
	
	/**
	 * 根据奖励类型以及奖励状态，查询
	 * @param type  奖励类型
	 * @param status 状态
	 * @param extendWay  发放类型：0.奖励，1：红包
	 * @return
	 */
	public List<RewardExtend> getRewardListByExtendWayStatus(int status,int extendWay);
	
	/**
	 * 根据选择的红包，计算红包总额
	 * @param id
	 * @return
	 */
	public double getSumMoneyRewardById(String[] id);
	
	
	/**
	 * 根据用户id，获取此用户成功充值的笔数
	 * @param userId   用户id
	 * @param status  状态
	 * @return
	 */
	public int getRechargeSuccessByUser(long userId,int status);
	
	/**
	 * 根据用户id，获取此用户成功投资的笔数
	 * @param userId
	 * @return
	 */
	public int getTenderSuccessByUser(long userId);
	
	/**
	 * 根据id数组，获取对象集合
	 * @param ids
	 * @return
	 */
	public List<RewardExtend> getRewardListByIds(String[] ids);
	
	

}
