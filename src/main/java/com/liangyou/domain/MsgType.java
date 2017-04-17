package com.liangyou.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.liangyou.context.Global;

@Entity
@Table(name = Global.TABLE_PREFIX +  "msg_type")
public class MsgType implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;                        //序号
	private String name;					//类型名称
	private String code;                    //类型编码
	
	@OneToMany(mappedBy="msgType", fetch=FetchType.LAZY)
	private List<MsgOperateType> MsgOperateTypes;  //
	
	@OneToMany(mappedBy="msgType", fetch=FetchType.LAZY)
	private List<MsgTemplate> msgTemplates;  //
	
	private int isuse; 						//类型启动  0:不启动；1：启用
	
	public MsgType(){
		super();
	}
	public MsgType(int id){
		super();
		this.id = id;
	}
	
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
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<MsgOperateType> getMsgOperateTypes() {
		return MsgOperateTypes;
	}
	public void setMsgOperateTypes(List<MsgOperateType> msgOperateTypes) {
		MsgOperateTypes = msgOperateTypes;
	}
	
	public int getIsuse() {
		return isuse;
	}
	public void setIsuse(int isuse) {
		this.isuse = isuse;
	}
	public List<MsgTemplate> getMsgTemplates() {
		return msgTemplates;
	}
	public void setMsgTemplates(List<MsgTemplate> msgTemplates) {
		this.msgTemplates = msgTemplates;
	}
}
