package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.UserAmountApply;

public interface UserAmountApplyDao extends BaseDao<UserAmountApply> {

	public List<UserAmountApply> getUserAmountApplyList(Long userId);

}
