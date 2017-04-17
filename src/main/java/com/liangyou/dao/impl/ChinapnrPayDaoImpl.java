package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.ChinapnrPayDao;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
@Repository
public class ChinapnrPayDaoImpl extends ObjectDaoImpl<ChinaPnrPayModel> implements ChinapnrPayDao {

	@Override
	public PageDataList<ChinaPnrPayModel> getChinapnrList(SearchParam sp) {
		
		return this.findPageList(sp);
	}

	@Override
	public ChinaPnrPayModel findChinapnrModelByOrd(String ordNo) {
		String jqpl = " from ChinaPnrPayModel where ordId=?1";
		Query query = em.createQuery(jqpl);
		query.setParameter(1, ordNo);
		List<ChinaPnrPayModel> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}



}
