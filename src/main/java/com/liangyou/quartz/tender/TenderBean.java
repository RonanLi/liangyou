package com.liangyou.quartz.tender;

import com.liangyou.domain.User;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.borrow.BorrowModel;
/**
 * 投标、自动投标业务处理
 * @author Administrator
 */
public class TenderBean {

	private String type;
	
	private BorrowParam borrowParam;
	
	private User user;
	
	private BorrowModel borrowModel;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BorrowParam getBorrowParam() {
		return borrowParam;
	}

	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BorrowModel getBorrowModel() {
		return borrowModel;
	}

	public void setBorrowModel(BorrowModel borrowModel) {
		this.borrowModel = borrowModel;
	}
	
	
}
