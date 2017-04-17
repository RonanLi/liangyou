package com.liangyou.service.impl;

/**
 * 日志记录接口  site_pay_log  等操作接口
 * @author zxc
 */
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.SitePayLogDao;
import com.liangyou.domain.SitePayLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.LogService;

@Service(value="logService")
@Transactional
public class LogServiceImpl extends BaseServiceImpl implements LogService {
	@Autowired
	private SitePayLogDao sitePayLogDao;
	
	
	@Override
	public PageDataList getSitePayList(SearchParam param) {		
		return sitePayLogDao.findPageList(param);
	}
	
	/**
	 * 查询，所有记录
	 * @param param
	 * @return
	 */
	@Override
	public List<SitePayLog> getAllSitePayList(SearchParam param) {		
		return sitePayLogDao.findByCriteria(param);
	}
	
}
