package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liangyou.context.Global;

/**
 * 好友类
 * @author fuxingxing
 * @date 2012-8-31 下午6:53:52
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "friends")
public class Friend implements Serializable {

	private static final long serialVersionUID = -2603972601624997167L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long  id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="friends_userid")
	private User friendsUser;
	
	private String friends_username;
	
	private int status;
	private int type;
	private String content;
	private Date addtime;
	private String addip;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	


	public String getFriends_username() {
		return friends_username;
	}

	public void setFriends_username(String friends_username) {
		this.friends_username = friends_username;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriendsUser() {
		return friendsUser;
	}

	public void setFriendsUser(User friendsUser) {
		this.friendsUser = friendsUser;
	}
	
}
