package com.liangyou.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.TenderPropertyDao;
import com.liangyou.domain.TenderProperty;
import com.liangyou.service.TenderPropertyService;
/**
 * @author wujing 
 * @version 创建时间：2013-12-19 上午11:05:52
 * 类说明
 */
@Service(value="tenderPropertyService")
@Transactional
public class TenderPropertyServiceImpl extends BaseServiceImpl implements
		TenderPropertyService {

	@Autowired
	private TenderPropertyDao tenderPropertyDao;
	
	@Override
	public void addTenderProperty(TenderProperty tenderProperty) {
		tenderPropertyDao.save(tenderProperty);

	}

	
}
