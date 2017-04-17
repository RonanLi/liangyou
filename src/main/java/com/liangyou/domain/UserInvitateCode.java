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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * add by lixiaomin on 2016/11/14.
 */

@Entity
@Table(name = "user_invitate_code")
public class UserInvitateCode implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",unique=true)
	private User user;
	
	@Column(name = "invitate_code")
	private String invitateCode;    //邀请码
	
	@Column(name = "parent_invitate_code_id")
	private long parentInvitateCodeId;    //父级ID
	
	@Column(name = "fill_in_invitate_code")
	private String fillinInvitateCode;   //用户手动输入的邀请码
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "add_time")
	private Date addTime;    //添加时间
	
	@Column(name = "status")
	private int status;    //邀请码状态
	
	@Column(name = "receive_money")
	private double receiveMoney;    //邀请码状态

	public double getReceiveMoney() {
		return receiveMoney;
	}

	public void setReceiveMoney(double receiveMoney) {
		this.receiveMoney = receiveMoney;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInvitateCode() {
		return invitateCode;
	}

	public void setInvitateCode(String invitateCode) {
		this.invitateCode = invitateCode;
	}

	public long getParentInvitateCodeId() {
		return parentInvitateCodeId;
	}

	public void setParentInvitateCodeId(long parentInvitateCodeId) {
		this.parentInvitateCodeId = parentInvitateCodeId;
	}

	public String getFillinInvitateCode() {
		return fillinInvitateCode;
	}

	public void setFillinInvitateCode(String fillinInvitateCode) {
		this.fillinInvitateCode = fillinInvitateCode;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
