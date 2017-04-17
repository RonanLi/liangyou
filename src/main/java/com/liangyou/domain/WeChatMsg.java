package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class WeChatMsg implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String openid;//微信交互用的用户唯一识别
	private String userName;//发消息的用户
	private String messageContent;//消息内容
	private int messageStatus;//0 未回复 1 已回复
	private Date messageTime;//发送时间
	@Column(columnDefinition="INT default 0")
	private int messageOpr;//0 取消收藏 1 收藏
	@Column(columnDefinition="BIGINT default 0")
	private int type;//1 我们接收到的 2.我们回复的3.菜单自动回复消息 4.模板消息5.关注自动回复(该回复唯一)6.自动回复
	private String mediaUrl;//多媒体文件下载后的地址
	
	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public WeChatMsg() {
		super();
	}

	
	public WeChatMsg(String openid, String userName, String messageContent, int messageStatus, Date messageTime,int type) {
		super();
		this.openid = openid;
		this.userName = userName;
		this.messageContent = messageContent;
		this.messageStatus = messageStatus;
		this.messageTime = messageTime;
		this.type = type;
	}
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public int getMessageStatus() {
		return messageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}
	public Date getMessageTime() {
		return messageTime;
	}
	public void setMessageTime(Date messageTime) {
		this.messageTime = messageTime;
	}
	public int getMessageOpr() {
		return messageOpr;
	}
	public void setMessageOpr(int messageOpr) {
		this.messageOpr = messageOpr;
	}

	@Override
	public String toString() {
		return "WeChatMsg [openid=" + openid + ", userName=" + userName + ", messageContent=" + messageContent
				+ ", messageStatus=" + messageStatus + ", messageTime=" + messageTime + ", type=" + type + "]";
	}
	
}
