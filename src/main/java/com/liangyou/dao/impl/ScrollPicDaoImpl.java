package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.ScrollPicDao;
import com.liangyou.domain.ScrollPic;
@Repository(value="scrollPicDao")
public class ScrollPicDaoImpl extends ObjectDaoImpl<ScrollPic> implements ScrollPicDao  {
	@Override
	public void updatePicHits(String url) {
		String sql = "UPDATE ScrollPic as s SET s.hits=s.hits+1 WHERE s.url = ?1";
		Query query  = em.createQuery(sql);
		query.setParameter(1, url);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ScrollPic showScrollPic(String path,long typeId) {
		String sql="FROM ScrollPic as s WHERE s.url=?1 AND s.typeId=?2";
		Query query  = em.createQuery(sql);
		query.setParameter(1, path);
		query.setParameter(2, typeId);
		List<ScrollPic> ScrollPics = query.getResultList();
		if (ScrollPics.size() > 0) {
			return ScrollPics.get(0);
		}else{
			return null;
		}
	}
}
