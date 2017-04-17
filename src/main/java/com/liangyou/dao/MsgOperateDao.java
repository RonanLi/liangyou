package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.MsgOperate;

public interface MsgOperateDao extends BaseDao<MsgOperate>{
	
	public List<MsgOperate> getChilds(int pid);	
}
