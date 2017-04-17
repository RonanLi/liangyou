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
 * 抽奖用户表
 * v1.8.0.4_u4  TGPROJECT-367 qinjun 2014-07-16
 */
@Entity
@Table(name="prize_user")
public class PrizeUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	/**
	 * 抽奖用户
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prize_id")
	private PrizeGoods prizeGoods;
	
	/**
	 * 抽奖消耗点数
	 */
	@JoinColumn(name="point_reduce")
	private int pointReduce;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rule_id")
	private PrizeRule prizeRule;
	
	/**
	 * 是否中奖:0不中，1中
	 */
	private int status;
	
	/**
	 * 奖励发放状态:0未发放，1已发放
	 */
	@Column(name="receive_status")
	private int receiveStatus;
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PrizeGoods getPrizeGoods() {
		return prizeGoods;
	}

	public void setPrizeGoods(PrizeGoods prizeGoods) {
		this.prizeGoods = prizeGoods;
	}

	public int getPointReduce() {
		return pointReduce;
	}

	public void setPointReduce(int pointReduce) {
		this.pointReduce = pointReduce;
	}

	public PrizeRule getPrizeRule() {
		return prizeRule;
	}

	public void setPrizeRule(PrizeRule prizeRule) {
		this.prizeRule = prizeRule;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getReceiveStatus() {
		return receiveStatus;
	}

	public void setReceiveStatus(int receiveStatus) {
		this.receiveStatus = receiveStatus;
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
