package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 商品类别 dw_goods_category
 */
@Entity
@Table(name="goods_category")
public class GoodsCategory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;  //商品名称
	
	@Column(name="parent_id")
	private int parentId; //父类
	
	private Date addtime; //添加时间
	
	@Column(name="is_virtual")
	private int isVirtual; //0：实物  3:父类型 1：vip 2：现金
	
	public GoodsCategory(){
		
	}
	
	public GoodsCategory(int id){
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(int isVirtual) {
		this.isVirtual = isVirtual;
	}

}
