package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.AreaMmm;

public interface AreaMmmDao extends BaseDao<AreaMmm> {
	List<AreaMmm> getListByPid(String pid);
}
