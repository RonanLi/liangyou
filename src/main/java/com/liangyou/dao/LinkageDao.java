package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Area;
import com.liangyou.domain.Linkage;

public interface LinkageDao extends BaseDao<Linkage> {
	/**
	 * 
	 * @param typeid
	 * @param type
	 * @return
	 */
	public List<Linkage> getLinkageByTypeid(int typeid,String type);

	public List<Linkage> getLinkageByNid(String nid,String type);
	
	public Linkage getLinkageByValue(String nid,String value);

	public List<Area> getAreainfoByPid(String pid);
	
	public Linkage getLinkageById(int id);
	
}
