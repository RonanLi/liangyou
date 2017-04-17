package com.liangyou.dao;

import com.liangyou.domain.ScrollPic;

public interface ScrollPicDao extends BaseDao<ScrollPic> {
	public void updatePicHits(String url);

	public ScrollPic showScrollPic(String path, long typeId);
}
