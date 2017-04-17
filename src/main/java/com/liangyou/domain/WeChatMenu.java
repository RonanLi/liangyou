package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author lijing
 * 微信菜单
 */

@Entity
public class WeChatMenu {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Date createTime;//菜单创建时间 默认当前时间
	/**
	 * 事件消息类型；即types = click; 系统定义了2中模式  key / fix 
	 * fix 即是 固定消息id，在创建菜单时，用 _fix_开头，方便解析；
	 * 同样的开发者可以自行定义其他事件菜单
	 */
	private String mtype;//消息类型： click - 事件消息；view - 链接消息 
	private String eventType;//事件菜单
	private String name;//菜单名称
	private String inputcode;//key
	private String url;//url
	private Integer sort;//排序
	private Long parentid;//父id
	private String msgId;//指定消息id
	private String parentName;//父名称
	private Long gid;//组
	
	public WeChatMenu() {
		super();
	}
	
	public WeChatMenu(Date createTime, String mtype, String eventType, String name, String inputcode, String url,
			Integer sort, Long parentid, String msgId, String parentName, Long gid) {
		super();
		this.createTime = createTime;
		this.mtype = mtype;
		this.eventType = eventType;
		this.name = name;
		this.inputcode = inputcode;
		this.url = url;
		this.sort = sort;
		this.parentid = parentid;
		this.msgId = msgId;
		this.parentName = parentName;
		this.gid = gid;
	}

	public String getMtype() {
		return mtype;
	}
	public void setMtype(String mtype) {
		this.mtype = mtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInputcode() {
		return inputcode;
	}
	public void setInputcode(String inputcode) {
		this.inputcode = inputcode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
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
	
	
	
	
	
}
