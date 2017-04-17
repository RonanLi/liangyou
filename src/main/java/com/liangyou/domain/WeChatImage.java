package com.liangyou.domain;

import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *微信图文消息用的内置图片需要单独上传
 */
@Entity
public class WeChatImage {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String url;
	private Date createTime;
	public Long getId() {
		return id;
	}
	
	public WeChatImage(String url, Date createTime) {
		super();
		this.url = url;
		this.createTime = createTime;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
