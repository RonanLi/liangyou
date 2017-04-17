package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.LotteryDao;
import com.liangyou.domain.Prize;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.lottery.LotteryDetail;
import com.liangyou.model.prize.MyPrize;
import com.liangyou.service.LotteryService;
import com.liangyou.util.StringUtils;

@Service(value = "lotteryService")
@Transactional
public class LotteryServiceImpl extends BaseServiceImpl implements LotteryService {

	@Autowired
	private LotteryDao lotteryDao;

	/**
	 * 查询全部奖品
	 */
	@Override
	public List<Prize> findAll() {
		return lotteryDao.findAll();
	}

	/**
	 * 根据状态查询奖品
	 */
	@Override
	public List<Prize> findByParam(SearchParam param) {
		return lotteryDao.findByParam(param);
	}

	/**
	 * 更新奖品
	 */
	@Override
	public void updatePrize(Prize prize) {
		lotteryDao.update(prize);
	}

	@Override
	public int doLottery(List<Double> orignalRates) {
		if (orignalRates == null || orignalRates.isEmpty()) {
			return -1;
		}

		int size = orignalRates.size();

		// 计算总概率，这样可以保证不一定总概率是1
		double sumRate = 0d;
		for (double rate : orignalRates) {
			sumRate += rate;
		}

		// 计算每个物品在总概率的基础下的概率情况
		List<Double> sortOrignalRates = new ArrayList<Double>(size);
		Double tempSumRate = 0d;
		for (double rate : orignalRates) {
			tempSumRate += rate;
			sortOrignalRates.add(tempSumRate / sumRate);
		}

		// 根据区块值来获取抽取到的物品索引
		double nextDouble = Math.random();
		sortOrignalRates.add(nextDouble);
		Collections.sort(sortOrignalRates);

		return sortOrignalRates.indexOf(nextDouble);
	}

	@Override
	public List<MyPrize> getPrizesByUserId(long User_Id) {
		return lotteryDao.getPrizesByUserId(User_Id);
	}

	@Override
	public PageDataList<LotteryDetail> findLotteryDetailPageListBySql(SearchParam p) {
		PageDataList<LotteryDetail> pageData = lotteryDao.findLotteryDetailPageListBySql(p);
		return pageData;
	}

	@Override
	public List<LotteryDetail> exportLotteryDetailListBySql(SearchParam p) {
		List<LotteryDetail> ldList = lotteryDao.exportLotteryDetailListBySql(p);
		if (!ldList.isEmpty()) {
			for (LotteryDetail ld : ldList) {
				ld.setStatusStr(ld.getStatus() == 1 ? "是" : "否");
			}
		}
		return ldList;
	}
}
