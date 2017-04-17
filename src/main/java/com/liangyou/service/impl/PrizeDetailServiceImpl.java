package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.PrizeDetailDao;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.model.SearchParam;
import com.liangyou.service.PrizeDetailService;

@Service(value = "prizeDetailService")
@Transactional
public class PrizeDetailServiceImpl extends BaseServiceImpl implements PrizeDetailService {

	@Autowired
	private PrizeDetailDao prizeDetailDao;

	@Override
	public List<PrizeDetail> findByPrizeIdAndStatus(int prizeId, int status) {
		return prizeDetailDao.findByPrizeIdAndStatus(prizeId, status);
	}

	@Override
	public List<PrizeDetail> findByParam(SearchParam param) {
		return (List<PrizeDetail>) prizeDetailDao.findByCriteria(param);
	}

	@Override
	public void save(PrizeDetail prizeDetail) {
		prizeDetailDao.save(prizeDetail);
	}

	@Override
	public void update(PrizeDetail prizeDetail) {
		prizeDetailDao.update(prizeDetail);
	}

}
