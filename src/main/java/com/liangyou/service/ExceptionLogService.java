package com.liangyou.service;

import com.liangyou.domain.ExceptionLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 异常日志服务
 * 
 * @author 1432 ExceptionLog 异常表
 */
public interface ExceptionLogService {

	public ExceptionLog find(long id);

	public void save(ExceptionLog item);

	/**
	 * 分页查询异常记录表
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<ExceptionLog> page(SearchParam param);

}
