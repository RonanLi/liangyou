package com.liangyou.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *客服账号封装
 */
@Entity
public class WeChatKfAccount {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Long kf_id;//客服工号
	private String kf_account;
	private String kf_nick;
	private String kf_headimgurl;
	public Long getKf_id() {
		return kf_id;
	}
	public void setKf_id(Long kf_id) {
		this.kf_id = kf_id;
	}
	public String getKf_account() {
		return kf_account;
	}
	public void setKf_account(String kf_account) {
		this.kf_account = kf_account;
	}
	public String getKf_nick() {
		return kf_nick;
	}
	public void setKf_nick(String kf_nick) {
		this.kf_nick = kf_nick;
	}
	public String getKf_headimgurl() {
		return kf_headimgurl;
	}
	public void setKf_headimgurl(String kf_headimgurl) {
		this.kf_headimgurl = kf_headimgurl;
	}
	
}
