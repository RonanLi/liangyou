package com.liangyou.dao;

import com.liangyou.domain.CompensationAccount;
import com.liangyou.domain.User;


/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
public interface CompensationAccountDao extends BaseDao<CompensationAccount>{
	/**
	 * 更新补偿金账户资金表
	 * @param collectionCompensation 待收补偿金
	 * @param repayCompensation 待还补偿金
	 * @param collectionYesCompensation 已收补偿金
	 * @param repayYesCompensation 已还补偿金
	 * @param user_id 用户ID
	 */
	public void updateCompensationAccount(double collectionCompensation,
			double repayCompensation,double collectionYesCompensation,double repayYesCompensation,long user_id);
	/**
	 * 查询指定用户的补偿金账户资金
	 * @param user
	 * @return
	 */
	public CompensationAccount getCompensationAccountByUser(User user);
}
