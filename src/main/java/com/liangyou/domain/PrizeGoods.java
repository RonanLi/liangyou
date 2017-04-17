package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 奖品表
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Entity
@Table(name="prize_goods")
public class PrizeGoods {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	/**
	 * 奖品名
	 */
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rule_id")
	private PrizeRule prizeRule;
	/**
	 * 奖品级别
	 */
	private int level;
	/**
	 * 奖品中奖率
	 */
	private int rate;
	/**
	 * 抽奖点数限制
	 */
	@Column(name="point_limit")
	private int pointLimit;
	/**
	 * 领用数量
	 */
	private int bestow;
	/**
	 * 奖品总量
	 */
	private int total;
	/**
	 * 奖品限制 0:奖品总量没有限制 1：奖品总量限制
	 */
	@Column(name="prize_limit")
	private int prizeLimit;
	/**
	 * 奖品描述
	 */
	private String description;
	/**
	 * 奖品类型(0:现金,1:积分)
	 */
	private int type;
	/**
	 * 奖品属性值(如面额)
	 */
	@Column(name="goods_value")
	private int goodsValue;
	/**
	 * 奖品规则描述
	 */
	private String memo;
	/**
	 * 创建时间
	 */
	private Date addtime;
	/**
	 * 创建IP
	 */
	private String addip;
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
	public PrizeRule getPrizeRule() {
		return prizeRule;
	}
	public void setPrizeRule(PrizeRule prizeRule) {
		this.prizeRule = prizeRule;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public int getBestow() {
		return bestow;
	}
	public void setBestow(int bestow) {
		this.bestow = bestow;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPrizeLimit() {
		return prizeLimit;
	}
	public void setPrizeLimit(int prizeLimit) {
		this.prizeLimit = prizeLimit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getGoodsValue() {
		return goodsValue;
	}
	public void setGoodsValue(int goodsValue) {
		this.goodsValue = goodsValue;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
	public int getPointLimit() {
		return pointLimit;
	}
	public void setPointLimit(int pointLimit) {
		this.pointLimit = pointLimit;
	}
	
}
