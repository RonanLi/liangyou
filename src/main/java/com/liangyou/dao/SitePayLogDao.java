package com.liangyou.dao;

import com.liangyou.domain.SitePayLog;


public interface SitePayLogDao extends BaseDao<SitePayLog> {

	/**
	 * 获取日志的最后一条记录
	 * @return
	 */
	SitePayLog getLastSitePayLog();
	
}
