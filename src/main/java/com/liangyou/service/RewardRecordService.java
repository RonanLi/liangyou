package com.liangyou.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.domain.ActivityConfig;
import com.liangyou.domain.RewardRecord;
import com.liangyou.domain.RewardUseRecord;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.RewardRecordSql;
import com.liangyou.model.SearchParam;

/**
 * @Desc 奖励记录管理    服务层接口
 * @author yjt_anzi
 *
 */
public interface RewardRecordService {

	/**
	 * @Desc 根据id获取红包信息
	 * @param rId
	 * @return RewardRecord
	 */
	
	public List<RewardRecord> getRewardRecordById(Long rId, Long uId);
	/**
	 * @Desc 获取当前登录用户可用奖励（红包）
	 * @param rewardTypeo
	 * @return
	 */
	public List<Map<String, Object>> enableRewardByUser(String rewardTypeo,User uInfo, Integer bRules);
	
	/**
	 * @Desc 根据活动发放奖励
	 * @param activityCLisst
	 * @return
	 */
	public boolean prize(List<ActivityConfig> activityCLisst,User uInfo,String sessionUid);
	
	public void saveRewardUseRecord(RewardUseRecord ru);
	
	
	
	/**
	 * 查询相对应的红包的张数 返回INT
	 * @param param
	 * @return
	 */
	public int findByDenominationCount(SearchParam param);
	/**
	 * 查询相对应的红包 返回List
	 * @param param
	 * @return
	 */
	public List<RewardRecord> findByDenominationList(SearchParam param);
	/**
	 * 根根据条件查询各种红包的数量
	 * @param param
	 * @return
	 */
	public List<RewardRecord> findCount(SearchParam param);
	
	public PageDataList<RewardRecord> findAll(SearchParam param);
	
	
	/**
	 * 根据有效日期查询数据
	 * @param param
	 * @return
	 */
	public List<RewardRecordSql> findByValidityAll(SearchParam param);
	
	/**
	 * 根根据条件查询各种红包的金额
	 * @param param
	 * @return
	 */
	public double findSumDenomination(SearchParam param);
	
	public void saveRewardRecord(RewardRecord rr);
	
	/**
	 * @Desc 后台-奖励记录报表
	 * @param param
	 * @param paramMap
	 * @return
	 * @author yjt_anzi
	 */
	public PageDataList<RewardRecordSql> findByPage(SearchParam param);
	
	/**
	 * @Desc 查询所有奖励记录（param）
	 * @param param
	 * @return
	 * @author yjt_anzi
	 */
	public List<RewardRecordSql> getSumRewardRecordList(SearchParam param);
	
	/**
	 * @Desc 获得用户红包奖励列表
	 * @param param
	 * @return
	 */
	public PageDataList<RewardRecordSql> getRewardByUid(SearchParam param);
	
	/**
	 * @Desc 根据用户ID 统计用户奖励个数
	 * @param param
	 * @return
	 */
	public Map<String,Integer> countsByUid(SearchParam param);
	
	/**
	   @Desc 更新过期的红包状态
	 * @param param
	 * @return
	 */
	public void updateStatusByDate(Date date);
	

	/**
	 * @Desc 判断用户是否拥有过某类型的红包奖励
	 * @param userinfo
	 * @param frombe
	 * @param type
	 * @return
	 */
	public boolean isLMByType(User userinfo,String type,ActivityConfig acinfo);
	
	/**
	 * @Desc 根据活动 给用户发放奖励
	 * @param acList
	 * @param userinfo
	 * @param type
	 * @return
	 */
	public boolean prizeForActivity(List<ActivityConfig> acList,User userinfo,String type);
	
	public void prizeForWeixin(ActivityConfig ac,User user,String[] money);

}
