package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author lijing 事件推送统计
 */
@Entity
public class WeChatEventPush {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String eventType;// 事件类型
	private String fromUser;// 发送方帐号 openId;
	private Date createTime;// 发送时间
	private String eventKey;//事件的key:自定义菜单的key,跳转的url
	private long count;//点击次数
	public WeChatEventPush() {
		super();
	}
	public WeChatEventPush(String eventType, String fromUser, Date createTime,
			String eventKey, long count) {
		super();
		this.eventType = eventType;
		this.fromUser = fromUser;
		this.createTime = createTime;
		this.eventKey = eventKey;
		this.count = count;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "WeChatEventPush [id=" + id + ", eventType=" + eventType
				+ ", fromUser=" + fromUser + ", createTime=" + createTime
				+ ", eventKey=" + eventKey + ", count=" + count + "]";
	}
	
}
