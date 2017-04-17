package com.liangyou.model;
/**
 * 
 *易极付、
 *汇付等接口传参使用。
 * @author zxc
 *
 */
public class ApiPayParamModel {

	private String tenderStr = "";
	private double totalMoney = 0;
	public String getTenderStr() {
		return tenderStr;
	}

	public void setTenderStr(String tenderStr) {
		this.tenderStr = tenderStr;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	
	
	
}
