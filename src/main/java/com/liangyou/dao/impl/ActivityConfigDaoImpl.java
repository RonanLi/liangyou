package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import org.springframework.stereotype.Repository;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.ActivityConfigDao;
import com.liangyou.domain.ActivityConfig;

/**
 * @Desc 活动配置表 数据层接口实现类
 * @author yjt_anzi
 *
 */
@Repository(value="activityConfigDao")
public class ActivityConfigDaoImpl extends ObjectDaoImpl<ActivityConfig>
		implements ActivityConfigDao {
	
	@Override
	public List<ActivityConfig> getRewardRecordListByNowDate(){
		String jpql = " from ActivityConfig where activityDateStart <= now() and activityDateEnd >= now() and isEnable = 0";
		List<ActivityConfig> list = new ArrayList<ActivityConfig>();
		Query q = em.createQuery(jpql);
		list = q.getResultList();
		return list;
	}

	@Override
	public List<ActivityConfig> getActivityConfig() {
		String jpql = " from ActivityConfig where 1=1";
		List<ActivityConfig> list = new ArrayList<ActivityConfig>();
		Query q = em.createQuery(jpql);
		list = q.getResultList();
		return list;
	}

}
