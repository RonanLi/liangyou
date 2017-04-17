package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.PrizeDetail;
import com.liangyou.model.SearchParam;

/**
 * 奖品详情
 * 
 * @author 1432
 *
 */
public interface PrizeDetailService {

	public List<PrizeDetail> findByPrizeIdAndStatus(int prizeId, int status);

	public List<PrizeDetail> findByParam(SearchParam param);

	public void save(PrizeDetail prizeDetail);

	/**更新商品详情
	 * @param prizeDetail
	 */
	public void update(PrizeDetail prizeDetail);

}
