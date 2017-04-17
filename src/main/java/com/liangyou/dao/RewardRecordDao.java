package com.liangyou.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liangyou.domain.Account;
import com.liangyou.domain.RewardRecord;
import com.liangyou.model.PageDataList;
import com.liangyou.model.RewardRecordSql;
import com.liangyou.model.SearchParam;
import com.liangyou.domain.User;

/**
 * @Desc 奖励记录表  数据层接口
 * @author yjt_anzi
 *
 */
public interface RewardRecordDao extends BaseDao<RewardRecord> {
	public int updateRewardRecordstatus(long id);
	public List<RewardRecord> getRewardRecordById(long id, Long uId);
	
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
	
	/**
	 * @Desc 用户有效奖励
	 * @param rewardType
	 * @param uInfo
	 * @return
	 */
	public List<RewardRecord> enableRewardByUser(String rewardType,User uInfo, Integer bRules);
	
	
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
	
	/**
	 * @Desc 后台-奖励记录报表
	 * @param param
	 * @param paramMap
	 * @return
	 * @author yjt_anzi
	 */
	public PageDataList<RewardRecordSql> findByPage(SearchParam param);
	
	/**
	 * @Desc 根据用户ID 获取奖励数据 （分页）
	 * @param param
	 * @return
	 */
	public PageDataList<RewardRecordSql> findByUid(SearchParam param);
	/**
	 * @Desc 根据用户ID 统计用户奖励个数
	 * @param param
	 * @return
	 */
	public List countsByUid(SearchParam param);
	
	/**
	   @Desc 更新过期的红包状态
	 * @param param
	 * @return
	 */
	public void updateStatusByDate(Date date);
}
