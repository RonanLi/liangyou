package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.SystemOperation;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 系统操作服务
 * 
 * @author 1432
 *
 */
public interface SystemOperationService {

	public SystemOperation find(int id);

	public void save(SystemOperation item);

	public List<SystemOperation> list(SystemOperation item);

	public PageDataList<SystemOperation> page(SearchParam param);

	public void update(SystemOperation item);

	/**
	 * 查询所有操作类型 ，level = 2
	 * 
	 * @return
	 */
	public List<SystemOperation> getAllOperationType();

}
