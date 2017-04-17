package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.WeChatMenuDao;
import com.liangyou.domain.WeChatMenu;
@Repository
public class WeChatMenuDaoImpl extends ObjectDaoImpl<WeChatMenu> implements WeChatMenuDao {

	@Override
	public List<WeChatMenu> findByGid(long gid) {
		String sql = "from WeChatMenu where gid = ?";
		Query query = em.createQuery(sql);
		query.setParameter(1, gid);
		return query.getResultList();
	}
}
