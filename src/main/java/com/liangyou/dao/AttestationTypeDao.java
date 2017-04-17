package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.AttestationType;
import com.liangyou.model.SearchParam;


public interface AttestationTypeDao extends BaseDao<AttestationType>{
	
	public List<AttestationType> getList(SearchParam param);
}
