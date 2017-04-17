package com.liangyou.dao;

import com.liangyou.domain.WebGlodLog;


public interface WebGlodLogDao extends BaseDao<WebGlodLog> {
	/**
	 * 根据订单查询记录的条数
	 * @param ordid
	 * @return
	 */
	public int sumWebLogByOrdid(String ordid);
}
