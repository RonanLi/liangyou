package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Warrant;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 担保服务
 * 
 * @author 1432
 *
 */
public interface WarrantService {
	/**
	 * 查询担保机构
	 * 
	 * @param id
	 * @return
	 */
	public Warrant findWarrant(long id);

	/**
	 * 修改担保机构
	 * 
	 * @param warrant
	 */
	public void updateWarrant(Warrant warrant);

	public PageDataList<Warrant> findListWarrant(SearchParam param);

	public void addWarrant(Warrant warrant);

	public List<Warrant> WarrantList();
}
