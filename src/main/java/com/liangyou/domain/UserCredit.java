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

import com.liangyou.context.Global;

@Entity
@Table(name="user_credit")
public class UserCredit implements Serializable {
	
	private static final long serialVersionUID = 4019823126620424066L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	//会员ID
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	//总计积分
	private long value;
	
	//操作者
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="op_user")
	private User opUser;
	
	//添加时间
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	//添加者IP
	private String addip;
	
	//修改时间
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	//修改者IP
	private String updateip;
	
	//投资积分
	@Column(name="tender_value")
	private long tenderValue;
	
	//借款积分
	@Column(name="borrow_value")
	private long borrowValue;
	
	//赠送积分
	@Column(name="gift_value")
	private long giftValue;
	
	//消费积分
	@Column(name="expense_value")
	private long expenseValue;
	
	//有效积分
	@Column(name="valid_value")
	private long validValue;
	
	//论坛积分
	@Column(name="bbs_value")
	private long bbsValue;
	
	public UserCredit() {
		super();
	}
	
	public UserCredit(User user,int value,int validValue,Date addtime,String addip) {
		super();
		this.user = user;
		this.value = value;
		this.validValue = validValue;
		this.addtime = addtime;
		this.addip = addip;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public User getOpUser() {
		return opUser;
	}

	public void setOpUser(User opUser) {
		this.opUser = opUser;
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

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateip() {
		return updateip;
	}

	public void setUpdateip(String updateip) {
		this.updateip = updateip;
	}

	public long getTenderValue() {
		return tenderValue;
	}

	public void setTenderValue(long tenderValue) {
		this.tenderValue = tenderValue;
	}

	public long getBorrowValue() {
		return borrowValue;
	}

	public void setBorrowValue(long borrowValue) {
		this.borrowValue = borrowValue;
	}

	public long getGiftValue() {
		return giftValue;
	}

	public void setGiftValue(long giftValue) {
		this.giftValue = giftValue;
	}

	public long getExpenseValue() {
		return expenseValue;
	}

	public void setExpenseValue(long expenseValue) {
		this.expenseValue = expenseValue;
	}

	public long getValidValue() {
		return validValue;
	}

	public void setValidValue(long validValue) {
		this.validValue = validValue;
	}

	public long getBbsValue() {
		return bbsValue;
	}

	public void setBbsValue(long bbsValue) {
		this.bbsValue = bbsValue;
	}
	//v1.8.0.4_u1 TGPROJECT-264 start
	public CreditRank findCreditRank(){
		CreditRank backCreaditRank = null;
		for(int i=0;i<Global.ALLCREDITRANK.size();i++){
			CreditRank cr = (CreditRank)Global.ALLCREDITRANK.get(i);
			if(this.value>=cr.getPoint1()&&this.value<=cr.getPoint2()){
				backCreaditRank = cr;
				break;
			}			
		}
		return backCreaditRank;
	}
	//v1.8.0.4_u1 TGPROJECT-264 end
}
