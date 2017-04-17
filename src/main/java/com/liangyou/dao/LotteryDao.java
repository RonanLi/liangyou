package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Prize;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.lottery.LotteryDetail;
import com.liangyou.model.prize.MyPrize;

public interface LotteryDao extends BaseDao<Prize> {

	public List<Prize> findByParam(SearchParam sp);

	public List<MyPrize> getPrizesByUserId(long User_Id);

	public PageDataList<LotteryDetail> findLotteryDetailPageListBySql(SearchParam param);

	public List<LotteryDetail> exportLotteryDetailListBySql(SearchParam param);

}
