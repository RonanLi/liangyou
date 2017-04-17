package com.liangyou.model;

import com.liangyou.domain.Collection;

public class DetailCollection extends Collection {

	private static final long serialVersionUID = -5620227157548250710L;
	
	private String borrow_name;
	private long borrow_id;
	private String time_limit;
	private String username;
	private long user_id;
	private String tendertime;
	public String getBorrow_name() {
		return borrow_name;
	}
	public void setBorrow_name(String borrow_name) {
		this.borrow_name = borrow_name;
	}
	public String getTime_limit() {
		return time_limit;
	}
	public void setTime_limit(String time_limit) {
		this.time_limit = time_limit;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getBorrow_id() {
		return borrow_id;
	}
	public void setBorrow_id(long borrow_id) {
		this.borrow_id = borrow_id;
	}
	public String getTendertime() {
		return tendertime;
	}
	public void setTendertime(String tendertime) {
		this.tendertime = tendertime;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
}
