package com.liangyou.model;

import java.util.Date;

public class RewardRecordSql {

	private long rewardRecordId;

	private String rewardType;

	private double denomination;

	private int userId;
	
	private Date validityDate;
	
	private Date createDate;

	private String createBy;
	
	private int activityConfig;
	
	private String isEnable;
	
	private Date useDate; //使用日期

	private String orderNo; //订单号

	private String fromBe; //奖励来源
	
	private Date lastUpdateDate;

	private String lastUpdateBy;
	
	private String userName;
	
	private String remark;	//奖励备注
	
	private double useRules; //奖励使用规则
	
	private String validityDateStr;//有效期 2015-05-09
	
	Integer countsnum; //用于统计

	public long getRewardRecordId() {
		return rewardRecordId;
	}

	public void setRewardRecordId(long rewardRecordId) {
		this.rewardRecordId = rewardRecordId;
	}

	public String getRewardType() {
		return rewardType;
	}

	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}

	public double getDenomination() {
		return denomination;
	}

	public void setDenomination(double denomination) {
		this.denomination = denomination;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(Date validityDate) {
		this.validityDate = validityDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public int getActivityConfig() {
		return activityConfig;
	}

	public void setActivityConfig(int activityConfig) {
		this.activityConfig = activityConfig;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getFromBe() {
		return fromBe;
	}

	public void setFromBe(String fromBe) {
		this.fromBe = fromBe;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getUseRules() {
		return useRules;
	}

	public void setUseRules(double useRules) {
		this.useRules = useRules;
	}

	public String getValidityDateStr() {
		return validityDateStr;
	}

	public void setValidityDateStr(String validityDateStr) {
		this.validityDateStr = validityDateStr;
	}

	public Integer getCountsnum() {
		return countsnum;
	}

	public void setCountsnum(Integer countsnum) {
		this.countsnum = countsnum;
	}
	
	
}
