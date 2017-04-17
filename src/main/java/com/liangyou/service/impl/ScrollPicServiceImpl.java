package com.liangyou.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ScrollPicDao;
import com.liangyou.domain.ScrollPic;
import com.liangyou.service.ScrollPicService;

@Service(value="scrollPicService")
@Transactional
public class ScrollPicServiceImpl implements ScrollPicService{
	@Autowired
	ScrollPicDao scrollPicDao;

	@Override
	public void updatePicHits(String url) {
		scrollPicDao.updatePicHits(url);
	}

	@Override
	public ScrollPic showScrollPic(String path,long typeId) {
		return scrollPicDao.showScrollPic(path,typeId);
	}

}
