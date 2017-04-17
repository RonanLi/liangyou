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

/**
 * 积分兑换商品存放图片实体类
 * @author chenxin
 * 2013-12-19
 * v1.6.7.2 RDPROJECT-569
 */
@Entity
@Table(name="goods_pic")
public class GoodsPic implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods; //商品ID
	
	@Column(name="pic_name")
	private String picName;  //图片名称
	
	@Column(name="pic_url")
	private String picUrl;  //图片路径
	
	private Date addtime;  //添加时间 
	
	private int ordering;  //显示图片顺序
	
	@Column(name="compress_pic_url")
	private String compressPicUrl;  //压缩后图片

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public int getOrdering() {
		return ordering;
	}

	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}

	public String getCompressPicUrl() {
		return compressPicUrl;
	}

	public void setCompressPicUrl(String compressPicUrl) {
		this.compressPicUrl = compressPicUrl;
	}
	
	
}
