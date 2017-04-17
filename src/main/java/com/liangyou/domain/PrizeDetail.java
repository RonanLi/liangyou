package com.liangyou.domain;

import java.io.Serializable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "prize_detail")
public class PrizeDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name; // 奖励描述

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prize_id")
	private Prize prize; // 所属奖品

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prize_user_id")
	private PrizeUserRelationship prizeUser;

	private String detail; // 明细
	private int status; // 状态

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "use_time")
	private Date useTime; // 使用时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Prize getPrize() {
		return prize;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public PrizeUserRelationship getPrizeUser() {
		return prizeUser;
	}

	public void setPrizeUser(PrizeUserRelationship prizeUser) {
		this.prizeUser = prizeUser;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
