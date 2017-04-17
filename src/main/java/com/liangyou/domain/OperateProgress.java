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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the account database table.
 * 用于记录分割操作结果的进度
 */
@Entity
@Table(name="operate_progress")
public class OperateProgress implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(name="operate_type")
	private String operateType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="now_operate_flow_id")
	private OperateFlow nowOperateFlow;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="next_operate_flow_id")
	private OperateFlow nextOperateFlow;
	
	@Column(name="order_no")
	long orderNo;
	
	private int status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_user_id")
	private User operateUser;
	
	private String param;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public OperateFlow getNowOperateFlow() {
		return nowOperateFlow;
	}

	public void setNowOperateFlow(OperateFlow nowOperateFlow) {
		this.nowOperateFlow = nowOperateFlow;
	}

	public long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(long orderNo) {
		this.orderNo = orderNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public OperateFlow getNextOperateFlow() {
		return nextOperateFlow;
	}

	public void setNextOperateFlow(OperateFlow nextOperateFlow) {
		this.nextOperateFlow = nextOperateFlow;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	

}
