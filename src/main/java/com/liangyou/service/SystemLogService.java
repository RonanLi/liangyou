package com.liangyou.service;

import com.liangyou.domain.SystemLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 系统日志服务
 * 
 * @author 1432
 *
 */
public interface SystemLogService {

	public SystemLog find(long id);

	public void save(SystemLog item);

	public PageDataList<SystemLog> page(SearchParam param);

}
