package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.ActivityConfig;
import com.liangyou.model.SearchParam;

/**
 * @Desc 活动配置表 服务层接口
 * @author yjt_anzi
 *
 */
public interface ActivityConfigService {
	
	/**
	 * @Desc 获取当前的有效活动
	 * @return
	 */
	public List<ActivityConfig> getEnableActivity();
	
	/**当前日期前的活动
	 * @return
	 */
	public List<ActivityConfig> getRewardRecordListByNowDate();
	
	public ActivityConfig getRewardRecordById(Long id);
	
	public List<ActivityConfig> getActivityConfig();
	/**
	 * @Desc 根据条件查询活动
	 * @param param
	 * @return
	 */
	public List<ActivityConfig> findByCriteria(SearchParam param);
}
