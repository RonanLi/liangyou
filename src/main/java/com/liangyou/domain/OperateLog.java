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
 * 用于记录分割操作结果的步骤
 */
@Entity
@Table(name="operate_log")
public class OperateLog implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_progress_id", updatable=false)
	private OperateProgress operateProgress;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_user_id")
	private User operateUser;
	
	private String type;
	
	private int status;
	
	private String memo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	@Column(name="order_no")
	private long orderNo;
	
	@Column(name="operate_detail")
	private String operateDetail;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public OperateProgress getOperateProgress() {
		return operateProgress;
	}
	public void setOperateProgress(OperateProgress operateProgress) {
		this.operateProgress = operateProgress;
	}
	public User getOperateUser() {
		return operateUser;
	}
	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public long getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(long orderNo) {
		this.orderNo = orderNo;
	}
	public String getOperateDetail() {
		return operateDetail;
	}
	public void setOperateDetail(String operateDetail) {
		this.operateDetail = operateDetail;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
