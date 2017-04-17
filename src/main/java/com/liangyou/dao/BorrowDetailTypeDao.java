package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.BorrowDetailType;

/**
 *v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  
 */
public interface BorrowDetailTypeDao extends BaseDao<BorrowDetailType> {
	List<BorrowDetailType> getListByPid(String pid);
}
