package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the message database table.
 * 
 */
@Entity
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	@Lob
	private String content;

	private int deltype;

	private String is_Authenticate;

	private String name;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="receive_user")
	private User receiveUser;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sent_user")
	private User sentUser;

	private int sented;

	private int status;

	private String type;

	public Message() {
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

	public int getDeltype() {
		return this.deltype;
	}

	public void setDeltype(int deltype) {
		this.deltype = deltype;
	}

	public String getIs_Authenticate() {
		return this.is_Authenticate;
	}

	public void setIs_Authenticate(String is_Authenticate) {
		this.is_Authenticate = is_Authenticate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getReceiveUser() {
		return this.receiveUser;
	}

	public void setReceiveUser(User receiveUser) {
		this.receiveUser = receiveUser;
	}

	public User getSentUser() {
		return this.sentUser;
	}

	public void setSentUser(User sentUser) {
		this.sentUser = sentUser;
	}

	public int getSented() {
		return sented;
	}

	public void setSented(int sented) {
		this.sented = sented;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}