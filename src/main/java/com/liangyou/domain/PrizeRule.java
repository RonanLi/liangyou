package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 抽奖规则表
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Entity
@Table(name="prize_rule")
public class PrizeRule {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	/**
	 * 规则名
	 */
	private String name;
	/**
	 * 抽奖类型:1按积分抽奖,2按次数抽奖
	 */
	@Column(name="prize_type")
	private int prizeType; 
	/**
	 * 抽奖次数限制:0不限制,1限制用户总次数,2限制当天总次数
	 */
	@Column(name="time_limit")
	private int timeLimit;
	/**
	 * 最多抽奖次数
	 */
	@Column(name="max_times")
	private int maxTimes;
	/**
	 * 基准积分
	 */
	@Column(name="base_point")
	private int basePoint;
	/**
	 * 金额限制
	 */
	@Column(name="money_limit")
	private int moneyLimit;
	/**
	 * 总金额
	 */
	@Column(name="total_money")
	private int totalMoney;
	/**
	 * 领用金额
	 */
	@Column(name="bestow_money")
	private int bestowMoney;
	
	/**
	 * 规则描叙
	 */
	private String content;
	/**
	 * 返现方式:1自动返现，2人工返现
	 */
	@Column(name="back_type")
	private int backType;
	/**
	 * 开始时间
	 */
	private Date starttime;
	/**
	 * 结束时间
	 */
	private Date endtime;
	/**
	 * 是否100%中奖  0:否,1:是
	 */
	@Column(name="is_absolute")
	private int isAbsolute;
	/**
	 * 添加时间
	 */
	private Date addtime;
	/**
	 * 添加IP
	 */
	private String addip;
	
	public PrizeRule() {}
	
	public PrizeRule(long id) {
		super();
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrizeType() {
		return prizeType;
	}
	public void setPrizeType(int prizeType) {
		this.prizeType = prizeType;
	}
	public int getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	public int getMaxTimes() {
		return maxTimes;
	}
	public void setMaxTimes(int maxTimes) {
		this.maxTimes = maxTimes;
	}
	public int getBasePoint() {
		return basePoint;
	}
	public void setBasePoint(int basePoint) {
		this.basePoint = basePoint;
	}
	public int getMoneyLimit() {
		return moneyLimit;
	}
	public void setMoneyLimit(int moneyLimit) {
		this.moneyLimit = moneyLimit;
	}
	public int getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;
	}
	public int getBestowMoney() {
		return bestowMoney;
	}
	public void setBestowMoney(int bestowMoney) {
		this.bestowMoney = bestowMoney;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getBackType() {
		return backType;
	}
	public void setBackType(int backType) {
		this.backType = backType;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public int getIsAbsolute() {
		return isAbsolute;
	}
	public void setIsAbsolute(int isAbsolute) {
		this.isAbsolute = isAbsolute;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public String getAddip() {
		return addip;
	}
	public void setAddip(String addip) {
		this.addip = addip;
	}
	
}
