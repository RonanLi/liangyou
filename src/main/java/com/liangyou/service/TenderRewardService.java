package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.TenderReward;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 *  //1.8.0.4_u3   TGPROJECT-337  qinjun 2014-06-23  start
 * 根据借款id查询所有的奖励计划
 * @param param
 * @return
 */
public interface TenderRewardService {
	
	public List<TenderReward> getTenderRewardByBorrowId(SearchParam param);
	
	public void doExtendReward(TenderReward reward);
	
	public PageDataList<TenderReward> getPageListTenderReward(SearchParam param);
	
	public TenderReward getTenderRewardById(long id);

}
