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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.AccountLogTypeName;


/**
 * The persistent class for the account_log database table.
 * 
 */
@Entity
@Table(name="account_log")
public class AccountLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="to_user")
	private User toUser;
	
	private String addip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private double money;

	private double total;
	
	@Column(name="no_use_money")
	private double noUseMoney;

	@Column(name="use_money")
	private double useMoney;

	private double collection;
	
	private double repay;
	
	private String remark;

	private String type;

	public AccountLog() {
	}

	public AccountLog(long user_id, String type, long to_user) {
		this(user_id,type,0d,0d,0d,0d,0d,to_user,"",new Date(),"");
	}
	
	public AccountLog(long user_id, String type, long to_user,String remark,String ip) {
		this(user_id,type,0d,0d,0d,0d,0d,to_user,remark,new Date(),ip);
	}

	public AccountLog(long user_id, String type, double total, double money,
			double use_money, double no_use_money, double collection,
			long to_user, String remark, Date addtime, String addip) {
		super();
		this.user = new User(user_id);
		this.type = type;
		this.total = total;
		this.money = money;
		this.useMoney = use_money;
		this.noUseMoney = no_use_money;
		this.collection = collection;
		this.toUser = new User(to_user);
		this.remark = remark;
		this.addtime = addtime;
		this.addip = addip;
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public double getCollection() {
		return this.collection;
	}

	public void setCollection(double collection) {
		this.collection = collection;
	}

	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getNoUseMoney() {
		return this.noUseMoney;
	}

	public void setNoUseMoney(double noUseMoney) {
		this.noUseMoney = noUseMoney;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getToUser() {
		return this.toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getUseMoney() {
		return this.useMoney;
	}

	public void setUseMoney(double useMoney) {
		this.useMoney = useMoney;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public double getRepay() {
		return repay;
	}

	public void setRepay(double repay) {
		this.repay = repay;
	}
	
	/**
	 * 获取type对应 name(用于导出excel)
	 * @return
	 */
	public String getTypeName(){
		return  AccountLogTypeName.getInstance().typeNameMap.get(type);
	}
	
	
	public String getDoremark(){
			//remark = remark.replace("[", "").replace("]", "");
			int firstBeginIndex = remark.indexOf("<");
			if(firstBeginIndex == -1)
				return remark;
			return remark.substring(0,firstBeginIndex)+remark.substring(remark.indexOf(">")+1, remark.lastIndexOf("<"))+remark.substring(remark.lastIndexOf(">")+1);
	}
	
}