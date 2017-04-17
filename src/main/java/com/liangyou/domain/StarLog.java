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
@Entity
@Table(name="star_log")
public class StarLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/*@Column(name="user_id")
	private long userId;*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	
	private String type;
	
	@Column(name="operate_score")
	private double operateScore;
	
	@Column(name="total_score")
	private double totalScore;
	
	private String remark;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/*public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getOperateScore() {
		return operateScore;
	}

	public void setOperateScore(double operateScore) {
		this.operateScore = operateScore;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
