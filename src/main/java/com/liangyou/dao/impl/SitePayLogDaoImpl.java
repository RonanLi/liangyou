package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.SitePayLogDao;
import com.liangyou.domain.SitePayLog;
@Repository(value="sitePayLogDao")
public class SitePayLogDaoImpl extends ObjectDaoImpl<SitePayLog> implements SitePayLogDao {

	/**
	 * 获取日志的最后一条记录
	 * @return
	 */
	@Override
	public SitePayLog getLastSitePayLog(){
        String sql = " from SitePayLog order by id desc limit 1 ";	
        Query query = em.createQuery(sql);
        List<SitePayLog> list = query.getResultList();
        if(list.size()>0){
        	return list.get(0);
        }else{
        	return null;
        }
	}
}
