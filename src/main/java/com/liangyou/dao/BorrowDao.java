package com.liangyou.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

public interface BorrowDao extends BaseDao<Borrow>{

	public List<Borrow> findAll(int start,int end,SearchParam param);

	public List<Borrow> unfinshBorrow(long userId);

	public List<Borrow> list();

	public List<Borrow> list(SearchParam param);
	/**
	 * 原生态sql 更新投标 的 borrow
	 * @param id
	 * @param accountyes
	 * @param tenderTimes
	 * @return
	 */
	public int updateBorrowAccountyes(long id , double accountyes, int tenderTimes);
	public List<Borrow> getBorrowListOrderByStatus();

	public Borrow getAssignMentBorrowByTenderId(long tender_id);

	/**
	 * 根据不同的职位，查看待审的撤标
	 * @param typeId
	 * @return
	 */
	public List<Borrow> getWaitCancelBorrow(long typeId);

	/**
	 * 查询网站成功借款总额
	 * @return
	 */
	public double getSuccessBorrowSumAccount();

	/**
	 * 查询网站中利率最高的
	 * @return
	 */
	public double getMaxAprBorrow();

	/**
	 * 查询当天标的总数
	 * @return
	 */
	public int sumBorrowByDay();

	/**
	 * 查询用户所有正在招标中的借款总和
	 * @param userId
	 * @return   
	 */
	public double sumBorrowAccount(long userId);

	//1.8.0.4_u3   TGPROJECT  qinjun 2014-06-25  start
	/**
	 * 根据借款id跟新借款开始时间
	 * @param borrowId
	 * @param date
	 */
	public void updateBorrowStartDate(long borrowId ,Date date);
	//1.8.0.4_u3   TGPROJECT  qinjun 2014-06-25  end

	//1.8.0.4_u4 TGPROJECT-345  wujing dytz  start
	/**
	 * @param checkMoney:校验搜索条件（投资金额大于money）
	 * 查询所有投资人有效投资金额：
	 * @return：tenderUserId,money
	 */
	public List getTenderUseAccount(double checkMoney);

	/**
	 * 获取所有用户已收回投资总和
	 * @return
	 */
	public List getRecallAccount();

	/**
	 * 查询满足用户投标有效总额，回款总额的多少
	 * @param tenderUserId   投资人id
	 * @param tenderAccount  投标总额条件
	 * @param recallAccount   回款总额条件
	 * @return
	 */
	public List getTenderRecall(long tenderUserId ,double tenderAccount ,double recallAccount);

	//1.8.0.4_u4 TGPROJECT-345  wujing dytz  end


	/**
	 * 根据状态查询标
	 * @param userId
	 * @return   
	 */
	public List borrowByStatus(int borrowStatus);
	/**
	 * 查询融资总额
	 * @return
	 */
	public double getBorrowSum();
	/**
	 * 查询融资产生利息
	 * @return
	 */
	public double getBorrowSumInterest(); 
	/**
	 * 根据用户id查询已发布借款标
	 * @param userId
	 * @return
	 */
	public List<Borrow> getBorrowListByUserId(long userId,int type);

	/**
	 * add by gy 2016年12月14日15:19:35
	 * 获取wap端首页标的列表
	 * @return
	 */
	public List<Borrow> getWapIndexBorrowList();

	/**
	 * add by gy 2016年12月14日15:19:35
	 * wap端获取标的列表
	 * @param param
	 * @param clazz
	 * @return
	 */
	public PageDataList findWapBorrowPageListBySql(SearchParam param);
}
