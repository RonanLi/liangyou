package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 双乾处理类
 * @author Qinjun
 * //v1.8.0.4  TGPROJECT-40   qj  2014-04-10 start
 * //v1.8.0.4  TGPROJECT-40   qj  2014-04-10 start
 *
 */
@Entity
@Table(name = "mmm_pay")
public class MmmPay implements Serializable {
	private static final long serialVersionUID = 3273422155680591964L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="transfer_type")
	private String transferType; //转账方式   1.桥连（到托管账户） 2.直连（冻结资金）
	
	@Column(name="transfer_action")
	private String transferAction;       //转账类型   1.投标   2.还款 3.收回转账
	
	@Column(name="need_audit")
	private String needAudit;//通过是否需要审核   空.需要审核  1.自动通过
	
	@Column(name="user_id")
	private long userId; //付款方
	
	@Column(name="mmm_id")
	private String mmmId;//付款方
	
	@Column(name="to_user_id")
	private long toUserId;//收款方
	

	@Column(name="to_mmm_id")
	private String toMmmId;//收款方
	
	private double amount;
	//1.8.0.4_u2   TGPROJECT-304  lx start
	@Column(name="full_amount")
	private double fullAmount;
	//1.8.0.4_u2   TGPROJECT-304  lx end
	
	@Column(name="order_no")
	private String orderNo;
	
	@Column(name="secondary_user_id")
	private String secondaryUserId;//二次分配收款方
	
	@Column(name="secondary_mmm_id")
	private String secondaryMmmId;//二次分配收款方
	
	@Column(name="secondary_amount")
	private double secondaryAmount;

	private String type;//接口类型
	
	@Column(name="borrow_id")
	private String borrowId;
	
	@Column(name="tender_id")
	private String tenderId;
	
	private String status;//状态   默认0:未处理或未处理成功, 1:处理成功,2:处理失败
	
	@Column(name="operate_type")
	private String operateType;
	
	@Column(name="result_msg")
	private String resultMsg;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	public MmmPay() {
		super();
	}

	public MmmPay(String transferType, String transferAction, String needAudit,
			long userId, String mmmId, long toUserId, String toMmmId,
			double amount, String type, String borrowId, String operateType,Date addtime) {
		super();
		this.transferType = transferType;
		this.transferAction = transferAction;
		this.needAudit = needAudit;
		this.userId = userId;
		this.mmmId = mmmId;
		this.toUserId = toUserId;
		this.toMmmId = toMmmId;
		this.amount = amount;
		this.type = type;
		this.borrowId = borrowId;
		this.operateType = operateType;
		this.addtime = addtime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
	}

	public String getTransferAction() {
		return transferAction;
	}

	public void setTransferAction(String transferAction) {
		this.transferAction = transferAction;
	}

	public String getMmmId() {
		return mmmId;
	}

	public void setMmmId(String mmmId) {
		this.mmmId = mmmId;
	}

	public String getToMmmId() {
		return toMmmId;
	}

	public void setToMmmId(String toMmmId) {
		this.toMmmId = toMmmId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSecondaryUserId() {
		return secondaryUserId;
	}

	public void setSecondaryUserId(String secondaryUserId) {
		this.secondaryUserId = secondaryUserId;
	}

	public double getSecondaryAmount() {
		return secondaryAmount;
	}

	public void setSecondaryAmount(double secondaryAmount) {
		this.secondaryAmount = secondaryAmount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBorrowId() {
		return borrowId;
	}

	public void setBorrowId(String borrowId) {
		this.borrowId = borrowId;
	}

	public String getTenderId() {
		return tenderId;
	}

	public void setTenderId(String tenderId) {
		this.tenderId = tenderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSecondaryMmmId() {
		return secondaryMmmId;
	}

	public void setSecondaryMmmId(String secondaryMmmId) {
		this.secondaryMmmId = secondaryMmmId;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	//1.8.0.4_u2   TGPROJECT-304  lx start
	public double getFullAmount() {
		return fullAmount;
	}
	public void setFullAmount(double fullAmount) {
		this.fullAmount = fullAmount;
	}
		
	//1.8.0.4_u2   TGPROJECT-304  lx end
}
