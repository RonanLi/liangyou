package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.SiteDao;
import com.liangyou.domain.Site;

@Repository("siteDao")
public class SiteDaoImpl extends ObjectDaoImpl<Site> implements SiteDao {

	private static Logger logger = Logger.getLogger(SiteDaoImpl.class);
	public List getSubSiteList(long pid) {
		return null;
	}
	@Override
	public Site getSiteByCode(String code) {
		String sql = "from Site where code=?1";
		Query query  = em.createQuery(sql);
		query.setParameter(1, code);
		//Site site = (Site)query.getSingleResult();
		List list  = query.getResultList();
		if(list.size()>=1){
			return (Site)list.get(0);
		}else{
			return null;
		}
	}
    @Override
    public List<Site> getSiteByCodes(String[] codes) {
        String sql = "from Site ";
        String queryConditions = "";
        for (String code : codes) {
            queryConditions += "'" +  code + "',";
        }
        sql += "where nid in (" + queryConditions.substring(0, queryConditions.length() - 1) + ")";
        Query query  = em.createQuery(sql);
        List list  = query.getResultList();
        return list;
    }

	@Override
	public List getSiteList() {
		String sql="from Site where pid = 0";
		List list = null;
		try{
			Query query  = em.createQuery(sql);
			list  = query.getResultList();
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		return list;
	}
	@Override
	public List getSubSiteList(int pid) {
		String sql="from Site where pid=?1 and status = 1  order by sort asc ";
		List list= null;
		try{
			Query query  = em.createQuery(sql);
			query.setParameter(1, pid);
			list  = query.getResultList();
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		return list;
	}
	@Override
	public List getAllSubSiteList(int pid) {
		String sql="from Site where pid=?1 order by sort asc ";
		List list= null;
		try{
			Query query  = em.createQuery(sql);
			query.setParameter(1, pid);
			list  = query.getResultList();
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		return list;
	}
}
