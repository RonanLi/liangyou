package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liangyou.context.Global;

@Entity
@Table(name = Global.TABLE_PREFIX +  "msg_operatetype")
public class MsgOperateType implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operate_id")
	private MsgOperate msgOperate;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	private MsgType msgType;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="template_id")
	private MsgTemplate msgTemplate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
}
