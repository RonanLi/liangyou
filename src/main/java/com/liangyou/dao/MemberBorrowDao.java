package com.liangyou.dao;

import java.util.List;

import com.liangyou.model.SearchParam;

public interface MemberBorrowDao {

	public List getBorrowList(String type, long user_id, int start, int end,
			SearchParam param);

	public int getBorrowCount(String type, long user_id, SearchParam param);

	/**
	 * 新增方法：根据条件查询用户的借还款情况
	 * 修改日期：2013-3-21
	 * @param type
	 * @return
	 */
	public List getRepamentList(String type, long user_id);

}
