package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Prize;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.lottery.LotteryDetail;
import com.liangyou.model.prize.MyPrize;

/**
 * 抽奖活动服务
 * 
 * @author 1432
 *
 */
public interface LotteryService {

	public int doLottery(List<Double> orignalRates);

	public List<Prize> findAll();

	public List<Prize> findByParam(SearchParam param);

	public void updatePrize(Prize prize);

	public List<MyPrize> getPrizesByUserId(long User_Id);

	public PageDataList<LotteryDetail> findLotteryDetailPageListBySql(
			SearchParam p);

	public List<LotteryDetail> exportLotteryDetailListBySql(SearchParam p);

}
