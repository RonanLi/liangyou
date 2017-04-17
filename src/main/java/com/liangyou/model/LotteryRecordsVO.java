package com.liangyou.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class LotteryRecordsVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String phone;
	private String detail;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
