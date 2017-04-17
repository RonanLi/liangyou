package com.liangyou.quartz.verifyFullBorrow;

import com.liangyou.model.BorrowParam;
import com.liangyou.model.borrow.BorrowModel;

public class VerifyFullBorrowBean {
	
	private String type;

	private BorrowModel borrowModel;
	
	private BorrowParam borrowParam;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BorrowModel getBorrowModel() {
		return borrowModel;
	}

	public void setBorrowModel(BorrowModel borrowModel) {
		this.borrowModel = borrowModel;
	}

	public BorrowParam getBorrowParam() {
		return borrowParam;
	}

	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}
	
}
