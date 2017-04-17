package com.liangyou.model.account;

import com.liangyou.domain.AccountLog;

public class AccountLogModel extends AccountLog {
	private static final long serialVersionUID = 5949296689081435047L;
	
	private String typename;

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}
	
}
