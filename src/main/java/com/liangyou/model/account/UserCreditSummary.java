package com.liangyou.model.account;

/**
 * v1.8.0.4_u1
 * 用户积分统计
 * @author zxc
 *
 */
public class UserCreditSummary extends BaseAccountSumModel {

	private double friendTenderMoney;
	private double friendTenderCredit;
	public double getFriendTenderMoney() {
		return friendTenderMoney;
	}
	public void setFriendTenderMoney(double friendTenderMoney) {
		this.friendTenderMoney = friendTenderMoney;
	}
	public double getFriendTenderCredit() {
		return friendTenderCredit;
	}
	public void setFriendTenderCredit(double friendTenderCredit) {
		this.friendTenderCredit = friendTenderCredit;
	}
	
	
}
