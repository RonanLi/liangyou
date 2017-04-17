package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Area;

public interface AreaDao extends BaseDao<Area> {
	public List<Area> getListByPid(int pid);
}
