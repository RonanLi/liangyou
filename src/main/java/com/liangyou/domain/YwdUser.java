package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name = "ywd_user")
public class YwdUser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String username;

	private String password;
	
	private int islogin;

	

	public YwdUser() {

	}

	public YwdUser(String username,String password) {
		this.username = username;
		this.password = password;
	}


	public YwdUser(int id, String username, String password) {
		this.id=id;
		this.username = username;
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public int getIslogin() {
		return islogin;
	}

	public void setIslogin(int islogin) {
		this.islogin = islogin;
	}
}