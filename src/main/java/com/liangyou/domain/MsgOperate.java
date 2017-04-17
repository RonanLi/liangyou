package com.liangyou.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liangyou.context.Global;

@Entity
@Table(name = Global.TABLE_PREFIX +  "msg_operate")
public class MsgOperate implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected int id;
	
	//private int pid;
	@OneToOne
	@JoinColumn(name="pid")
	protected MsgOperate parent;
	
	protected String name;
	protected String code;
	protected int sort;
	private String url;
	@OneToMany(cascade={CascadeType.REMOVE},mappedBy="msgOperate",fetch=FetchType.LAZY)
	protected List<MsgOperateType> msgOperateTypes;
	protected String remark;
	protected int isuse;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public MsgOperate getParent() {
		return parent;
	}
	public void setParent(MsgOperate parent) {
		this.parent = parent;
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
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<MsgOperateType> getMsgOperateTypes() {
		return msgOperateTypes;
	}
	public void setMsgOperateTypes(List<MsgOperateType> msgOperateTypes) {
		this.msgOperateTypes = msgOperateTypes;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public int getIsuse() {
		return isuse;
	}
	public void setIsuse(int isuse) {
		this.isuse = isuse;
	}
	public List<MsgOperate> getChilds(List<MsgOperate> list) {
		List<MsgOperate> back = new ArrayList<MsgOperate>();		
		for(MsgOperate m : list) {
			if(m.getParent().getId() == this.getId()) {
				back.add(m);
			}
		}
		return back;				
	}
}
