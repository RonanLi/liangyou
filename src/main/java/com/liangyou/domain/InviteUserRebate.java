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

import com.liangyou.context.Global;
import com.liangyou.util.DateUtils;

/**
 * 
 * @author lx add  TGPROJECT-302
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX+"invite_user_rebate")
public class InviteUserRebate implements Serializable {

	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	/**
	 * 投标Id
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id")
	private  BorrowTender borrowTender;
	/**
	 * 推荐人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="invite_user")
	private  User inviteUser;
	/**
	 * 状态：0为未审核， 1为已审核    2 为审核失败
	 */
	private  int status;
	/**
	 * 审核人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="verify_user")
	private  User verifyUser;
	/**
	 * 新增时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private  Date addtime;
	/**
	 * 新增IP
	 */
	private String addip;
	/**
	 *审核时间 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private  Date verifytime;
	/**
	 *回扣比例
	 */
	@Column(name="rebate_proportion")
	private String rebateProportion;
	
	/**
	 *回扣金额
	 */
	@Column(name="rebate_amount")
	private double rebateAmount;
	
	/**
	 *利息管理费
	 */
	@Column(name="borrow_fee")
	private double borrowFee;
	
	/**
	 *
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_colloction")
	private  BorrowCollection borrowCollection;
	
	public InviteUserRebate() {
	}
	public InviteUserRebate(int id, int tenderId, User inviteUser, int status,
			User verifyUser, Date addtime, String addip, Date verifytime,
			String rebateProportion) {
		this.id = id;
		this.inviteUser = inviteUser;
		this.status = status;
		this.verifyUser = verifyUser;
		this.addtime = addtime;
		this.addip = addip;
		this.verifytime = verifytime;
		this.rebateProportion = rebateProportion;
	}
	
	public double getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(double rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public User getInviteUser() {
		return inviteUser;
	}
	public void setInviteUser(User inviteUser) {
		this.inviteUser = inviteUser;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public User getVerifyUser() {
		return verifyUser;
	}
	public void setVerifyUser(User verifyUser) {
		this.verifyUser = verifyUser;
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
	public Date getVerifytime() {
		return verifytime;
	}
	public void setVerifytime(Date verifytime) {
		this.verifytime = verifytime;
	}
	public String getRebateProportion() {
		return rebateProportion;
	}
	public void setRebateProportion(String rebateProportion) {
		this.rebateProportion = rebateProportion;
	}

	
	public double getBorrowFee() {
		return borrowFee;
	}
	public void setBorrowFee(double borrowFee) {
		this.borrowFee = borrowFee;
	}
	public BorrowCollection getBorrowCollection() {
		return borrowCollection;
	}
	public void setBorrowCollection(BorrowCollection borrowCollection) {
		this.borrowCollection = borrowCollection;
	}
	public BorrowTender getBorrowTender() {
		return borrowTender;
	}
	public void setBorrowTender(BorrowTender borrowTender) {
		this.borrowTender = borrowTender;
	}
	@Override
	public String toString() {
		return "InviteUserRebate [id=" + id 
				+ ", inviteUser=" + inviteUser + ", status=" + status
				+ ", verifyUser=" + verifyUser + ", addtime=" + addtime
				+ ", addip=" + addip + ", verifytime=" + verifytime
				+ ", rebateProportion=" + rebateProportion + "]";
	}
	
	//导出excel用到的标的状态
		public String getStatusStr(){
			switch (this.status) {
			case 0:
				return "等待审核";
			case 1:
				return "审核通过";
			case 2:
				return "审核不通过";
			default:
				return "错误状态";
			}
		}
	
		
		
		public int getDateSubtract(){
			try {
				String date1=DateUtils.dateStr5(getBorrowTender().getBorrow().getVerifyTime());
				String nowdate=DateUtils.dateStr5(new Date());
				if(date1.equals(nowdate)){
					return 0;
				}else{
					return 1;
				}
			} catch (Exception e) {
				return 0;
			}
		}

}