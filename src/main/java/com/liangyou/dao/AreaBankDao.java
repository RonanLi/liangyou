package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.AreaBank;

public interface AreaBankDao extends BaseDao<AreaBank> {
	List<AreaBank> getListByPid(String pid);
}
