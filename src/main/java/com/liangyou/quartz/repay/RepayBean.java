package com.liangyou.quartz.repay;

import com.liangyou.domain.AccountLog;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.model.borrow.IpsRepaymentModel;
/**
 * 划款、划款给网站、提前还款业务处理
 * @author zxc
 *
 */
public class RepayBean {

	private String type;
	
	private BorrowRepayment borrowRepayment;
	
	private BorrowParam borrowParam;
	
	private PriorRepayLog ppLog;
	
	private BorrowModel borrowModel;
	
	private AccountLog accountLog;
	
	private BorrowTender borrowTender;
	
	private IpsRepaymentModel ipsRepaymentModel;

	public BorrowRepayment getBorrowRepayment() {
		return borrowRepayment;
	}

	public void setBorrowRepayment(BorrowRepayment borrowRepayment) {
		this.borrowRepayment = borrowRepayment;
	}

	public BorrowParam getBorrowParam() {
		return borrowParam;
	}

	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PriorRepayLog getPpLog() {
		return ppLog;
	}

	public void setPpLog(PriorRepayLog ppLog) {
		this.ppLog = ppLog;
	}

	public BorrowModel getBorrowModel() {
		return borrowModel;
	}

	public void setBorrowModel(BorrowModel borrowModel) {
		this.borrowModel = borrowModel;
	}

	public AccountLog getAccountLog() {
		return accountLog;
	}

	public void setAccountLog(AccountLog accountLog) {
		this.accountLog = accountLog;
	}

	public BorrowTender getBorrowTender() {
		return borrowTender;
	}

	public void setBorrowTender(BorrowTender borrowTender) {
		this.borrowTender = borrowTender;
	}

	public IpsRepaymentModel getIpsRepaymentModel() {
		return ipsRepaymentModel;
	}

	public void setIpsRepaymentModel(IpsRepaymentModel ipsRepaymentModel) {
		this.ipsRepaymentModel = ipsRepaymentModel;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null){
			RepayBean repayBean = (RepayBean)obj;
			BorrowRepayment inRepay = repayBean.getBorrowRepayment();
			if (inRepay ==null) {
				return true;
			}else{
				return inRepay.getId() == borrowRepayment.getId(); 
			}
		}else{
			return true;
		}
	}
	
	@Override
	public int hashCode() {
		 int result = 17;  
		 result = 31*result +(int)borrowRepayment.getId(); 
		 return result;
	};
}
