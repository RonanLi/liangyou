package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.LinkageDao;
import com.liangyou.domain.Area;
import com.liangyou.domain.Linkage;

@Repository(value="linkageDao")
public class LinkageDaoImpl extends ObjectDaoImpl<Linkage> implements LinkageDao {

	private static Logger logger = Logger.getLogger(LinkageDaoImpl.class);
	@Override
	public List<Linkage> getLinkageByTypeid(int typeid, String type) {
		String sql = " select (a) from Linkage a inner join a.linkageType b where a.status = 1 and b.id = ?1  ";
		Query query = em.createQuery(sql).setParameter(1, typeid);
		
		return (List<Linkage>)query.getResultList();
	}

	@Override
	public List<Linkage> getLinkageByNid(String nid, String type) {
		
		String sql = " select (a) from Linkage a inner join a.linkageType b where a.status = 1 and b.nid = ?1  ";
		Query query = em.createQuery(sql).setParameter(1, nid);
		return (List<Linkage>)query.getResultList();
		
	}

	@Override
	public Linkage getLinkageByValue(String nid, String value) {
		String sql = " select (a) from Linkage a inner join a.linkageType b where a.status = 1 and b.nid = ?1   ";
		Query query = em.createQuery(sql).setParameter(1, nid);
		List<Linkage> list = query.getResultList();
		Linkage linkage = null;
		for(int i=0;i<list.size();i++){
			Linkage lk = list.get(i);
			if(value.equals(lk.getValue())){
				linkage = lk;
			}
		}
		return linkage;
	}

	private String getOrderSql(String type) {
		String orderSql = "";
		if (type.equals("value")) {
			orderSql = "order by value+\"\"";
		} else {
			orderSql = "order by a.id";
		}
		return orderSql;
	}

	@Override
	public List<Area> getAreainfoByPid(String pid) {
		String sql = "from Area where Area.pid = ?1";
		List<Area> list = null;
		
		try{
			Query query  = em.createQuery(sql);
			query.setParameter(1, pid);
			list = query.getResultList();
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		return list;
	}
	
	public Linkage getLinkageById(int id) {
		return this.find(id);
	}
	
}
