package com.liangyou.model.msg;

import java.util.ArrayList;
import java.util.List;

import com.liangyou.domain.MsgOperate;

public class MsgOperateTree {
	
	private int id;
	private int pId;
	private boolean open = true;
	private String name;
	private String url;	
	private List<MsgOperateTree> children = new ArrayList<MsgOperateTree>();;
	
	public MsgOperateTree(){
		
	}
	
	public MsgOperateTree(MsgOperate mo){
		this.id = mo.getId();
		//this.pId = mo.getParent().getId();
		if(mo.getParent()!=null){
			this.pId = mo.getParent().getId();
		}
		this.name = mo.getName();
		this.url = mo.getUrl();		
	}
	
	public void addChildren(MsgOperateTree motree){
		children.add(motree);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MsgOperateTree> getChildren() {
		return children;
	}

	public void setChildren(List<MsgOperateTree> children) {
		this.children = children;
	}
	
	
}
