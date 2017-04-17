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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the attestation database table.
 * 
 */
@Entity
public class Attestation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String content;

	private int jifen;

	private String name;

	private String pic;

	private int status;

	@Column(name="verify_remark")
	private String verifyRemark;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="verify_time")
	private Date verifyTime;

	@Column(name="verify_user")
	private int verifyUser;

	//bi-directional many-to-one association to AttestationType
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	private AttestationType attestationType;

	//bi-directional many-to-one association to User
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	public Attestation() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getJifen() {
		return this.jifen;
	}

	public void setJifen(int jifen) {
		this.jifen = jifen;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPic() {
		return this.pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVerifyRemark() {
		return this.verifyRemark;
	}

	public void setVerifyRemark(String verifyRemark) {
		this.verifyRemark = verifyRemark;
	}

	public Date getVerifyTime() {
		return this.verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	public int getVerifyUser() {
		return this.verifyUser;
	}

	public void setVerifyUser(int verifyUser) {
		this.verifyUser = verifyUser;
	}

	public AttestationType getAttestationType() {
		return this.attestationType;
	}

	public void setAttestationType(AttestationType attestationType) {
		this.attestationType = attestationType;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}