package com.liangyou.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AttestationTypeDao;
import com.liangyou.domain.AttestationType;
import com.liangyou.model.SearchParam;

@Repository(value="attestationTypeDao")
public class AttestationTypeDaoImpl extends ObjectDaoImpl<AttestationType> implements AttestationTypeDao {

	@Override
	public List<AttestationType> getList(SearchParam param) {
		return this.findByCriteria(param);
	}

	
}
