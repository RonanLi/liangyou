package com.liangyou.dao;

import com.liangyou.domain.Usertrack;


public interface UserTrackDao extends BaseDao<Usertrack> {
	public void addUserTrack(Usertrack t);
	public Usertrack getLastUserTrack(long userid);
	public int getUserTrackCount(long userid);
}
