package com.liangyou.dao;

import com.liangyou.domain.DrawBankMmm;

public interface DrawBankMmmDao extends BaseDao<DrawBankMmm> {
	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 start
	public DrawBankMmm getDrawBankMmmByBankCode(String bankCode);
	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 end
}
