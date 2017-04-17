package com.liangyou.dao;

import com.liangyou.domain.DrawBank;

public interface DrawBankDao extends BaseDao<DrawBank> {
	public DrawBank getDrawBankByBankCode(String bankCode);
}
