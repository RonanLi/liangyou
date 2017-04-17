package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.RewardUseRecord;

/**
 * @Desc 奖励使用(投标) 记录表  数据层接口
 * @author yjt_anzi
 *
 */
public interface RewardUseRecordDao extends BaseDao<RewardUseRecord> {

	public List<RewardUseRecord> getRewardUseRecordBy(String orderNo);
	
	public int updateRewardUseRecordstatus(long id);
}
