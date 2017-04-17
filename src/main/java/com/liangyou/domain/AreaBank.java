package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="area_bank")
public class AreaBank implements Serializable {
	private static final long serialVersionUID = 17984496L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String nid;

	private String pid;

	private String name;

	public AreaBank() {
		super();
	}

	public AreaBank(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}