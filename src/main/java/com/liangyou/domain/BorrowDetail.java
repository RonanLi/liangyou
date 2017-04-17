package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  
 */
@Entity
@Table(name="borrow_detail")
public class BorrowDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	//所属栏目
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id", updatable=false)
	private BorrowDetailType typeName;

	//类别名称
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="type_pid", updatable=false)
	private BorrowDetailType partName;
	
	//子类别(暂不使用)
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_nid", updatable=false)
	private BorrowDetailType subName;
	
	//名称
	private String name;

	private String content;
	
	private String memo;

	private Date addtime;
	
	private String addip;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id", updatable=false)
	private Borrow borrow;

	public BorrowDetail() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BorrowDetailType getTypeName() {
		return typeName;
	}

	public void setTypeName(BorrowDetailType typeName) {
		this.typeName = typeName;
	}

	public BorrowDetailType getPartName() {
		return partName;
	}

	public void setPartName(BorrowDetailType partName) {
		this.partName = partName;
	}

	public BorrowDetailType getSubName() {
		return subName;
	}

	public void setSubName(BorrowDetailType subName) {
		this.subName = subName;
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

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getAddip() {
		return addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}