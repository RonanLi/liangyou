package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.TenderCompensation;
import com.liangyou.domain.User;

/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
public interface TenderCompensationDao extends BaseDao<TenderCompensation>{
	
	/**
	 * 更新指定用户的补偿金资金记录
	 * @param tenderMoney 投资金额
	 * @param compensation 满标前补偿金
	 * @param days 天数
	 * @param rate 补偿率
	 * @param status 状态 0 待还，1已还，2网站垫付，3已还网站
	 * @param user_id 用户ID
	 */
	public void updateTenderCompensation(double tenderMoney,double compensation,int days,double rate,int status,long user_id);
	
	/**
	 * 查询指定用户,当前标种的，补偿金总和
	 * @param user
	 * @param borrow
	 * @param status
	 * @return
	 */
	public double getSumBorrowCompensation(User borrowUser,Borrow borrow,int status);
	/**
	 * 获取当前借款人，要还款的所有投资列表
	 * @param borrowUser
	 * @param borrow
	 * @param status
	 * @return
	 */
	public List<TenderCompensation> getTenderCompensationsByBorrow(User borrowUser,Borrow borrow,int status);

	/**
	 * 获取当前投资人的补偿金记录
	 * @param tenderUser
	 * @param borrow
	 * @param status
	 * @param tender_id
	 * @return
	 */
	public TenderCompensation getTenderCompensationByUser(User tenderUser,Borrow borrow, int status, long tender_id);
}
