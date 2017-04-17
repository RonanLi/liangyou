package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.UserTypepurview;

public interface UserTypepurviewDao extends BaseDao<UserTypepurview> {
	
	public void addUserTypePurviews(List<Integer> purviewid,long user_type_id);	
	public void delUserTypePurviews(long user_type_id) ;
	public List<UserTypepurview> getUserTypepurviewList(long user_id);
}
