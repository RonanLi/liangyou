package com.liangyou.quartz.cash;
import com.liangyou.api.pay.YzzNewDeduct;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.UserCache;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
/**
 * 取现、充值业务处理
 * @author zxc
 *
 */

public class CashBean {
	private String type;
	
	private AccountRecharge accountRecharge;
	
	private AccountCash accountCash;
	
	private AccountCashModel accountCashModel;
	
	private RechargeModel rechargeModel;
	
	private AccountLog accountLog;
	
	private BorrowParam borrowParam;
	
	private YzzNewDeduct yzzNewDeduct;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public AccountRecharge getAccountRecharge() {
		return accountRecharge;
	}
	public void setAccountRecharge(AccountRecharge accountRecharge) {
		this.accountRecharge = accountRecharge;
	}
	public AccountCash getAccountCash() {
		return accountCash;
	}
	public void setAccountCash(AccountCash accountCash) {
		this.accountCash = accountCash;
	}
	public RechargeModel getRechargeModel() {
		return rechargeModel;
	}
	public void setRechargeModel(RechargeModel rechargeModel) {
		this.rechargeModel = rechargeModel;
	}
	public AccountLog getAccountLog() {
		return accountLog;
	}
	public void setAccountLog(AccountLog accountLog) {
		this.accountLog = accountLog;
	}
	public BorrowParam getBorrowParam() {
		return borrowParam;
	}
	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}
	public AccountCashModel getAccountCashModel() {
		return accountCashModel;
	}
	public void setAccountCashModel(AccountCashModel accountCashModel) {
		this.accountCashModel = accountCashModel;
	}
	public YzzNewDeduct getYzzNewDeduct() {
		return yzzNewDeduct;
	}
	public void setYzzNewDeduct(YzzNewDeduct yzzNewDeduct) {
		this.yzzNewDeduct = yzzNewDeduct;
	}
	
	

}
