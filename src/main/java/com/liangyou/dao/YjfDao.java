package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.YjfPay;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;


public interface YjfDao extends BaseDao<YjfPay> {
	/**
	 * 查询标的交易号
	 * @param userId
	 * @param borrowId
	 * @param status
	 * @return
	 */
	public YjfPay getBorrowTradeNo(String userId, String borrowId, int status);
	/**
	 * 查询所有投标成功的用户
	 * @param userId
	 * @param borrowId
	 * @param status
	 * @return
	 */
	public List<YjfPay> getTendersPayed( String borrowId);	
	/**
	 * 查询所记录
	 */
	public PageDataList<YjfPay> getList(SearchParam sp);
	public List<YjfPay> getWrongStatusYjfPayByBorrowId(long borrowId, String service);

}
