package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.WeChatMenu;

public interface WeChatMenuDao extends BaseDao<WeChatMenu>{
	List<WeChatMenu> findByGid(long id);
}
