package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author lx 
 *
 */
@Entity
@Table(name="goods")
public class Goods implements Serializable {
	private static final long serialVersionUID = 6261541593841459554L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	
	@Column(name="credit_value")
	private int creditValue;
	
	private int store;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date shelvestime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	private double cost;
	
	@Column(name="market_price")
	private double marketPrice;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="category_id")
	private GoodsCategory category;
	
	private String description;
	
	private String attribute;
	
	private String remarks;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="create_user_id")
	private User createUser;
	
	@OneToMany(mappedBy="goods",cascade=CascadeType.PERSIST)
	private List<GoodsPic> picList;
	
	public Goods() {
	}
	
	public Goods(int id) {
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

	public int getCreditValue() {
		return creditValue;
	}

	public void setCreditValue(int creditValue) {
		this.creditValue = creditValue;
	}

	public int getStore() {
		return store;
	}

	public void setStore(int store) {
		this.store = store;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Date getShelvestime() {
		return shelvestime;
	}

	public void setShelvestime(Date shelvestime) {
		this.shelvestime = shelvestime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}


	public GoodsCategory getCategory() {
		return category;
	}

	public void setCategory(GoodsCategory category) {
		this.category = category;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<GoodsPic> getPicList() {
		return picList;
	}

	public void setPicList(List<GoodsPic> picList) {
		this.picList = picList;
	}

}
