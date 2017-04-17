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
@Table(name = "prizeUserRelationship")
public class PrizeUserRelationship implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prize_id")
	private Prize prize; // 奖品

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 用户

	private String phone; // 领取手机号

	@Column(name = "receive_state")
	private int receiveState = 0; // 领取状态 0 未绑定用户 1 实力领取，已绑定用户

	private int status = 0; // 使用状态 0 未使用 1 已使用

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "receive_time")
	private Date receiveTime = new Date(); // 领取时间 此为点击领取按钮时的时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "real_receive_time")
	private Date realReceiveTime; // 实际领取时间，此为绑定用户的时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "use_time")
	private Date useTime; // 使用时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Prize getPrize() {
		return prize;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getReceiveState() {
		return receiveState;
	}

	public void setReceiveState(int receiveState) {
		this.receiveState = receiveState;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public Date getRealReceiveTime() {
		return realReceiveTime;
	}

	public void setRealReceiveTime(Date realReceiveTime) {
		this.realReceiveTime = realReceiveTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
