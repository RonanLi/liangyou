package com.liangyou.model.prize;

import java.util.Date;

public class MyPrize {

	private String prizeName;	// 奖品名称
	private Date prizeTime;		// 奖品获取时间
	private String prizeStatus;	// 奖品状态
	private String prizeImgUrl; // 图片地址
	private String exchangeCode;// 兑换码

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public Date getPrizeTime() {
		return prizeTime;
	}

	public void setPrizeTime(Date prizeTime) {
		this.prizeTime = prizeTime;
	}

	public String getPrizeStatus() {
		return prizeStatus;
	}

	public void setPrizeStatus(String prizeStatus) {
		this.prizeStatus = prizeStatus;
	}

	public String getPrizeImgUrl() {
		return prizeImgUrl;
	}

	public void setPrizeImgUrl(String prizeImgUrl) {
		this.prizeImgUrl = prizeImgUrl;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

}
