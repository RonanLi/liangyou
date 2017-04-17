package com.liangyou.dao;

import java.util.Date;
import java.util.List;

import com.liangyou.domain.BorrowTender;
import com.liangyou.model.SearchParam;

/**
 * @author wujing
 *@date 2013-12-19 下午1:45:16 
 */
public interface BorrowTenderDao extends BaseDao<BorrowTender> {
	/**
	 * 根据borrowid获取BorrowTender列表
	 * @param borrowid
	 * @return
	 */
	public List getBorrowTenderList(long borrowid);
	
	public List<BorrowTender> getBorrowTenderList(String cid, Date startData, Date endDate);
	
	public Integer updateBT(long btId, String aid, String cid, String feedback, String target, String channel);
	
	/**
	 *  根据borrowid获取BorrowTender列表的前num条
	 * @param id
	 * @param num
	 * @return
	 */
	public List getBorrowTenderListByborrowId(long id,int num);
	
	/**
	 * 获取未还款的tender
	 * @param id
	 * @return
	 */
	public List getBorrowTenderListByborrowId(long id) ;
	
	/**
	 * 获取还款和未还款的tender
	 * @param id
	 * @return
	 */
	public List<BorrowTender> getBorrowTenderListByborrow(long id);
	
	public List getBorrowTenderListByborrowId(long id,long user_id) ;
	
	public List getBorrowTenderListByborrowId(long id,int start,int pernum,SearchParam param);
	
	public List getTenderListByUserId(long user_id);
	
	public List getInvestBorrowTenderListByUserid(long user_id,int start,int end,SearchParam param);
	
	public int getInvestBorrowTenderCountByUserid(long user_id,SearchParam param);
	
	public List getSuccessBorrowTenderList(long user_id);
	
	public List getSuccessBorrowTenderList(long user_id, int start, int end,SearchParam param);
	
	public double hasTenderTotalPerBorrowByUserid(long bid,long userId);
	public BorrowTender getAssignMentTender(long borrowId);
	public double sumCollectionMoney(long userid);      //获取用户的待收本金总和
	
	/**
	 * 获取投标当天的总投标数
	 * @return
	 */
	public int sumTenderByDay();
	
	/**
	 * 根据投标订单号查询投标记录
	 * @param tenderNo
	 * @return
	 */
	public int sumTenderByTenderNo(String tenderNo);
	
	/**
	 * 计算网站总投资人数
	 * @return
	 */
	public int sumTender();

	public double getTenderMoneyByTime(String time1,String time2,long userId);
	
	public int countUserTender(String time1,String time2,long userId);
	public double countUserCollectionMoney(String time1,String time2,long userId);
	//v1.8.0.4 TGPROJECT-63 lx 2014-04-16 start
	/**
	 * 统计所有收益
	 * @return
	 */
	public double sumInterest();
	//v1.8.0.4 TGPROJECT-63 lx 2014-04-16 end
	
	public double countTenderRepayment(long borrow_id);
	
	public List<BorrowTender> getBorrowTenderByOrderNo(String subOrdId);

	public BorrowTender getListByUserId(long userId);

	
}
