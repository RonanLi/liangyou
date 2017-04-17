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

/**
 * The persistent class for the account database table.
 * 步骤分割流程表
 */
@Entity
@Table(name="operate_flow")
public class OperateFlow implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name="operate_type")
	private String operateType;
	
	private String name ;
	
	private int sort;
	
	private int status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_user_id")
	private User oprateUser;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_type_id")
	private UserType userType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	public OperateFlow() {
		super();
	}

	public OperateFlow(long id) {
		super();
		this.id = id;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public User getOprateUser() {
		return oprateUser;
	}

	public void setOprateUser(User oprateUser) {
		this.oprateUser = oprateUser;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	

}
