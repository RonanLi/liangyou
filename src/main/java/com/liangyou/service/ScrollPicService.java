package com.liangyou.service;

import org.springframework.stereotype.Service;

import com.liangyou.domain.ScrollPic;

@Service
public interface ScrollPicService {
	public void updatePicHits(String url);

	public ScrollPic showScrollPic(String path, long typeId);
}
