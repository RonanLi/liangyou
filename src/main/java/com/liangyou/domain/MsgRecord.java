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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;

@Entity
@Table(name = Global.TABLE_PREFIX+"msg_record")
public class MsgRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	//发送人
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="send_user")
	private User sender;
	
	//接收人
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="receive_user")
	private User receiver;
	
	//业务
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_id")
	private MsgOperate msgOperate;
	
	//发送类型 站内信/邮件/短信
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	private MsgType msgType;
	
	//模板
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="templet_id")
	private MsgTemplate msgTemplate;
	
	//发送时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="send_time")
	private Date sendTime;
	
	//IP
	@Column(columnDefinition="VARCHAR(20)")
	private String ip;
	
	//发送结果
	@Column(name="send_result",columnDefinition="VARCHAR(256)")
	private String sendResult;
	
	public MsgRecord() {
	}

	public MsgRecord(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public MsgOperate getMsgOperate() {
		return msgOperate;
	}

	public void setMsgOperate(MsgOperate msgOperate) {
		this.msgOperate = msgOperate;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}

	public MsgTemplate getMsgTemplate() {
		return msgTemplate;
	}

	public void setMsgTemplate(MsgTemplate msgTemplate) {
		this.msgTemplate = msgTemplate;
	}

	public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

}
