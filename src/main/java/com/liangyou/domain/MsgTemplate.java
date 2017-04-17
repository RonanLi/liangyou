package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liangyou.context.Global;

@Entity
@Table(name = Global.TABLE_PREFIX +  "msg_template")
public class MsgTemplate implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;              //模板序号
	private String name;		 //模板名称
	private String content;		 //模板内容
	private int isuse;			 //是否使用   0：不使用；1：使用
	private String remark;       //备注
	
	@OneToOne
	@JoinColumn(name="type_id")
	private MsgType msgType;     //所属类型
	//	
	public MsgTemplate(){
		super();
	}
	public MsgTemplate(int id){
		super();
		this.id = id;
	}
	//
	public int getId() { 
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIsuse() {
		return isuse;
	}
	public void setIsuse(int isuse) {
		this.isuse = isuse;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public MsgType getMsgType() {
		return msgType;
	}
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	
}
