package com.liangyou.model.wechatModel;

import java.util.Map;

public class UserListModel {
	private Integer total;
	private Integer count;
	private Map<String, Map<String, String[]>> data;
	
	public UserListModel() {
		super();
	}
	public UserListModel(Integer total, Integer count,
			Map<String, Map<String, String[]>> data) {
		super();
		this.total = total;
		this.count = count;
		this.data = data;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Map<String, Map<String, String[]>> getdata() {
		return data;
	}
	public void setdata(Map<String, Map<String, String[]>> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "UserListModel [total=" + total + ", count=" + count
				+ ", data=" + data + "]";
	}	
	
	
}
