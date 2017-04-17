package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * WMCQ-1 2014-09-09 wujing
 * 系统业务部门表
 * @author 
 *
 */
@Entity
@Table(name="business_type")
public class BusinessType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7369985658909346007L;
	
	public BusinessType(){
		
	}
	
	public BusinessType(long id){
		this.id = id;
	}
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="add_userid")
	private User addUser;
	
	private Date addTime;
	
	private String content;
	
	private int status;  //状态：1、启用，2、屏蔽

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

	public User getAddUser() {
		return addUser;
	}

	public void setAddUser(User addUser) {
		this.addUser = addUser;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
