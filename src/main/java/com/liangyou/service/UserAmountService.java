package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountApply;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 用户信用额度服务
 * 
 * @author 1432
 *
 */
public interface UserAmountService {
	public UserAmount getUserAmount(long user_id);

	public PageDataList<UserAmountApply> getUserAmountApplyList(
			SearchParam param);

	/**
	 * 查询申请的集合
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserAmountApply> getUserAmountApplyListById(Long userId);

	public void add(UserAmountApply userAmountApply);

	public UserAmountApply getAmountApplyById(int id);

	public void verifyAmountApply(UserAmountApply apply, UserAmountLog log,
			int giveCredit); // 新添加申请信用额度时添加奖励信用积分的方法

	public void updateAmountApply(UserAmountApply apply); // 跟新UserAmountApply
}
