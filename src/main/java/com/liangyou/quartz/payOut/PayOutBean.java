package com.liangyou.quartz.payOut;

import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.model.BorrowParam;
/**
 * vip付款、汇付金账户操作业务处理
 * @author zxc
 *
 */
public class PayOutBean {

	private String type;
	
	private CashOut cashOut;
	
	private WebGlodLog  webGlodLog;
	
	private BorrowParam borrowParam;
	
	private UserCache userCache;
	
	private AccountLog accountLog;
	
	private FssTrans fssTrans;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CashOut getCashOut() {
		return cashOut;
	}

	public void setCashOut(CashOut cashOut) {
		this.cashOut = cashOut;
	}

	public WebGlodLog getWebGlodLog() {
		return webGlodLog;
	}

	public void setWebGlodLog(WebGlodLog webGlodLog) {
		this.webGlodLog = webGlodLog;
	}

	public BorrowParam getBorrowParam() {
		return borrowParam;
	}

	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}

	public UserCache getUserCache() {
		return userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	public AccountLog getAccountLog() {
		return accountLog;
	}

	public void setAccountLog(AccountLog accountLog) {
		this.accountLog = accountLog;
	}

	public FssTrans getFssTrans() {
		return fssTrans;
	}

	public void setFssTrans(FssTrans fssTrans) {
		this.fssTrans = fssTrans;
	}
	
	
}
