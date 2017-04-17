package com.liangyou.dao;

import java.util.List;
import com.liangyou.domain.ActivityConfig;
/**
 * @Desc 活动配置表  数据层接口
 * @author yjt_anzi
 *
 */
public interface ActivityConfigDao extends BaseDao<ActivityConfig> {
	public List<ActivityConfig> getRewardRecordListByNowDate();
	
	public List<ActivityConfig> getActivityConfig();
}
