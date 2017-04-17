package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.BusinessTypeDao;
import com.liangyou.domain.BusinessType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.BusinessTypeService;

/**
 * WMCQ-1 2014-09-09 wujing
 * @author Administrator
 *
 */
@Repository("businessTypeService")
@Transactional 
public class BusinessTypeServiceImpl implements BusinessTypeService {

	@Autowired
	private BusinessTypeDao businessTypeDao;
	
	@Override
	public void addBusinessType(BusinessType businessType) {
		businessTypeDao.save(businessType);
	}

	@Override
	public void updateBusinessType(BusinessType businessType) {
		businessTypeDao.update(businessType);
	}

	@Override
	public PageDataList<BusinessType> findPageListBusinessType(SearchParam param) {
		PageDataList<BusinessType> pageList = businessTypeDao.findPageList(param);
		return pageList;
	}

	@Override
	public List<BusinessType> findListBusinessType(SearchParam param) {
		List<BusinessType> businessList = businessTypeDao.findByCriteria(param);
		return businessList;
	}

	@Override
	public BusinessType findBusinessById(long id) {
		return businessTypeDao.find(id);
	}
	
	

}
