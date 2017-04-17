package com.liangyou.dao;

import com.liangyou.domain.IpsRepayDetail;

public interface IpsRepayDetailDao extends BaseDao<IpsRepayDetail> {
	
	/**
	 * 根据订单号获取还款记录
	 * @param ordId
	 * @return
	 */
	public IpsRepayDetail getIpsRepayByOrdId(String ordId);

}
