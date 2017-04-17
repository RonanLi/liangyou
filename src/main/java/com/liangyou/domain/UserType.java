package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the user_type database table.
 * 
 */
@Entity
@Table(name="user_type")
public class UserType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="type_id")
	private long typeId;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String name;

	private long sort;

	private String purview;

	private String remark;

	private int status;

	private String summary;

	private int type;

	@OneToMany(mappedBy="userType",fetch=FetchType.LAZY)
	private List<UserTypepurview> userTypepurviews;

	public UserType() {
	}
	
	public UserType(long typeId) {
		this.typeId = typeId;
	}

	public long getTypeId() {
		return this.typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPurview() {
		return this.purview;
	}

	public long getSort() {
		return sort;
	}

	public void setSort(long sort) {
		this.sort = sort;
	}

	public void setPurview(String purview) {
		this.purview = purview;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<UserTypepurview> getUserTypepurviews() {
		return this.userTypepurviews;
	}

	public void setUserTypepurviews(List<UserTypepurview> userTypepurviews) {
		this.userTypepurviews = userTypepurviews;
	}

	public UserTypepurview addUserTypepurview(UserTypepurview userTypepurview) {
		getUserTypepurviews().add(userTypepurview);
		userTypepurview.setUserType(this);

		return userTypepurview;
	}

	public UserTypepurview removeUserTypepurview(UserTypepurview userTypepurview) {
		getUserTypepurviews().remove(userTypepurview);
		userTypepurview.setUserType(null);

		return userTypepurview;
	}

}