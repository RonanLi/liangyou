package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.SitePayLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 日志记录接口  site_pay_log  等操作接口
 * @author zxc
 *
 */
public interface LogService {
	
	public PageDataList getSitePayList(SearchParam param);
	/**
	 * 查询，所有记录
	 * @param param
	 * @return
	 */
	public List<SitePayLog> getAllSitePayList(SearchParam param);
}
