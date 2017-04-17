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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="user_credit_log")
public class UserCreditLog implements Serializable {
	
	private static final long serialVersionUID = 4019823126620424066L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id", updatable=false)
	private UserCreditType userCreditType;
	
	private int op;//变动类型,1:增加,2:减少
	
	@Column(name="operate_value")//变动积分数值
	private long operateValue;
	
	@Column(name="valid_value")
	private long validValue;//变动有效积分
	
	private long value;//总积分
	
	
	@Column(name="tender_money")
	private double tenderMoney;//推荐积分，投资金额
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="from_user_id", updatable=false)
	private User fromUser; //推荐人
	
	@Column(name="borrow_value")
	private long borrowValue;//借款积分
	
	@Column(name="tender_value")
	private long tenderValue;//投资积分
	
	@Column(name="bbs_value")
	private long bbsValue;//论坛积分
	
	@Column(name="expense_value")
	private long expenseValue;//消费积分
	
	private String remark;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	private String addip;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id")
	private Borrow borrow ;//标，可以为空
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="repayment_id")
	private BorrowRepayment borrowRepayment;//还款计划可以为空
	
	public UserCreditLog(){
		super();
	}
	
	public UserCreditLog(User user,long operateValue,long operateValidValue, int op,UserCreditType userCreditType,UserCredit credit ){
		super();
		this.user = user;
		this.operateValue= operateValue;
		this.validValue = operateValidValue;
		this.op = op;
		this.userCreditType = userCreditType;
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

	public UserCreditType getUserCreditType() {
		return userCreditType;
	}

	public void setUserCreditType(UserCreditType userCreditType) {
		this.userCreditType = userCreditType;
	}

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getValidValue() {
		return validValue;
	}

	public void setValidValue(long validValue) {
		this.validValue = validValue;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public long getOperateValue() {
		return operateValue;
	}

	public void setOperateValue(long operateValue) {
		this.operateValue = operateValue;
	}

	public long getBorrowValue() {
		return borrowValue;
	}

	public void setBorrowValue(long borrowValue) {
		this.borrowValue = borrowValue;
	}

	public long getTenderValue() {
		return tenderValue;
	}

	public void setTenderValue(long tenderValue) {
		this.tenderValue = tenderValue;
	}

	public long getBbsValue() {
		return bbsValue;
	}

	public void setBbsValue(long bbsValue) {
		this.bbsValue = bbsValue;
	}

	public long getExpenseValue() {
		return expenseValue;
	}

	public void setExpenseValue(long expenseValue) {
		this.expenseValue = expenseValue;
	}

	public double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public Borrow getBorrow() {
		return borrow;
	}

	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}

	public BorrowRepayment getBorrowRepayment() {
		return borrowRepayment;
	}

	public void setBorrowRepayment(BorrowRepayment borrowRepayment) {
		this.borrowRepayment = borrowRepayment;
	}
	
	
}
