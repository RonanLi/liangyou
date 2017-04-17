package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ActivityConfigDao;
import com.liangyou.domain.ActivityConfig;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.ActivityConfigService;
import com.liangyou.util.DateUtils;

/**
 * @Desc 活动配置  服务层接口实现类
 * @author yjt_anzi
 *
 */
@Service(value="activityConfigService")
@Transactional 
public class ActivityConfigServiceImpl extends BaseServiceImpl implements
		ActivityConfigService {

	@Autowired
	private ActivityConfigDao activityConfigDao;
	
	//获取有效奖励
	@Override
	public List<ActivityConfig> getEnableActivity() {
		Date activeDate = new Date();
		SearchParam param = SearchParam.getInstance();	
		param.addParam("isEnable", "0");
		param.addParam("activityDateStart", Operator.LTE ,DateUtils.getDate(DateUtils.dateStr4(activeDate), "yyyy-MM-dd HH:mm:ss"));
		param.addParam("activityDateEnd", Operator.GTE ,DateUtils.getDate(DateUtils.dateStr4(activeDate), "yyyy-MM-dd HH:mm:ss"));
		List<ActivityConfig> acList = activityConfigDao.findByCriteria(param);
		return acList;
	}
	
	@Override
	public List<ActivityConfig> getRewardRecordListByNowDate(){
		return activityConfigDao.getRewardRecordListByNowDate();
	}
	
	@Override
	public ActivityConfig getRewardRecordById(Long id){
		return activityConfigDao.find(ActivityConfig.class, id);
	}
	
	/**
	 * @Desc 根据条件查询活动
	 * @param param
	 * @return
	 */
	@Override
	public List<ActivityConfig> findByCriteria(SearchParam param){
		return activityConfigDao.findByCriteria(param);
	}

	@Override
	public List<ActivityConfig> getActivityConfig(){
		return activityConfigDao.getActivityConfig();
	}
}
