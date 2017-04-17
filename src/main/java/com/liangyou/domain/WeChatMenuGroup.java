package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class WeChatMenuGroup {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Date createTime;//菜单创建时间 默认当前时间
	private String name;
	private Integer enable;
	public WeChatMenuGroup() {
		super();
	}
	public WeChatMenuGroup(Date createTime, String name, Integer enable) {
		super();
		this.createTime = createTime;
		this.name = name;
		this.enable = enable;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

	public Integer getEnable(){
		return enable;
	}
	public void setEnable(Integer enable){
		this.enable = enable;
	}

}
