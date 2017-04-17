package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.BusinessType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 业务服务 WMCQ-1 2014-09-09 wujing
 * 
 * @author Administrator
 *
 */
public interface BusinessTypeService {

	/**
	 * 添加业务部门
	 * 
	 * @param businessType
	 */
	public void addBusinessType(BusinessType businessType);

	/**
	 * 更新业务部门
	 * 
	 * @param businessType
	 */
	public void updateBusinessType(BusinessType businessType);

	/**
	 * 获取带分页的业务部门集合
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<BusinessType> findPageListBusinessType(SearchParam param);

	/**
	 * 获取所有业务部门集合
	 * 
	 * @param param
	 * @return
	 */
	public List<BusinessType> findListBusinessType(SearchParam param);

	/**
	 * 根据id查询
	 * 
	 * @param id
	 * @return
	 */
	public BusinessType findBusinessById(long id);

}
