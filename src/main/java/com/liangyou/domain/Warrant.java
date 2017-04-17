package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author wujing 
 * @version 创建时间：2013-12-17 下午2:59:13
 * 类说明:
 * 关于担保机构添加业务方法
 */
@Entity
@Table(name="warrant")
public class Warrant {
	private static final long serialVersionUID = 1L;
	public Warrant(){
		
	}
	public Warrant(long wid){
		id = wid;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	
	private String area;
	
	private String capital;
	
	@Column(name="relation_user")
	private String relationUser;
	
	private String phone;
	
	private String address;
	
	private String summary;  //公司简介
	
	private int status;//是否开启  0 关闭 1 开启
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public String getRelationUser() {
		return relationUser;
	}

	public void setRelationUser(String relationUser) {
		this.relationUser = relationUser;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
	
	
}
