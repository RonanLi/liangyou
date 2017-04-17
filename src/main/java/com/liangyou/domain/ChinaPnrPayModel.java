package com.liangyou.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name = "chinapnr_pay")
public class ChinaPnrPayModel {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="cmdid")
	private String cmdid;
	
	@Column(name="ordamt")
	private double ordamt;
	
	@Column(name="user_id")
	private long userId; //付款方
	
	@Column(name="usrcust_id")
	private String usrCustId;         //  付款人
	
	@Column(name="payusrcust_id")
	private String payusrCustId;       //收款人
	
	@Column(name="payuser_id")
	private String payuserId;//收款方
	
	@Column(name="status")
	private String status;
	
	@Column(name="operate_time")
	private String operateTime;
	
	@Column(name="addtime")
	private String addtime;
	
	@Column(name="borrow_id")
	private String borrowId;
	
	@Column(name="tender_id")
	private String tenderId;
	
	@Column(name="operate_type")
	private String operateType;
	
	@Column(name="error_msg")
	private String errorMsg;
	
	@Column(name="ord_id")
	private String ordId;
	
	@Column(name="orddate")
	private String orddate;
	
	@Column(name="subord_id")
	private String subordId;
	
	@Column(name="suborddate")
	private String suborddate;
	
	@Column(name="late_time")
	private String lateTime;
	
	@Column(name="manage_fee")
	private String manageFee ;//分账字符串
	
	@Column(name="trx_id")
	private String trxId;     //投标冻结流水号
	
	@Column(name="fee")
	private String fee;        //放款，还款费率
	
	@Column(name="repay_id")
	private int repayId;


	public int getRepayId() {
		return repayId;
	}

	public void setRepayId(int repayId) {
		this.repayId = repayId;
	}

	public ChinaPnrPayModel() {
		super();
	}

	public ChinaPnrPayModel(String cmdid, double ordamt, long user_id,
			String payuser_id, String status, String operate_time,
			String addtime, String operate_type) {
		super();
		this.cmdid = cmdid;
		this.ordamt = ordamt;
		this.userId = user_id;
		this.payuserId = payuser_id;
		this.status = status;
		this.operateTime = operate_time;
		this.addtime = addtime;
		this.operateType = operate_type;
	}

	
	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}
	
	public String getManageFee() {
		return manageFee;
	}

	public void setManageFee(String manageFee) {
		this.manageFee = manageFee;
	}
	
	public String getUsrCustId() {
		return usrCustId;
	}

	public void setUsrCustId(String usrCustId) {
		this.usrCustId = usrCustId;
	}

	public String getPayusrCustId() {
		return payusrCustId;
	}

	public void setPayusrCustId(String payusrCustId) {
		this.payusrCustId = payusrCustId;
	}



	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public String getOrddate() {
		return orddate;
	}

	public void setOrddate(String orddate) {
		this.orddate = orddate;
	}

	public String getSubordId() {
		return subordId;
	}

	public void setSubordId(String subordId) {
		this.subordId = subordId;
	}

	public String getSuborddate() {
		return suborddate;
	}

	public void setSuborddate(String suborddate) {
		this.suborddate = suborddate;
	}



	public String getLateTime() {
		return lateTime;
	}

	public void setLateTime(String lateTime) {
		this.lateTime = lateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCmdid() {
		return cmdid;
	}

	public void setCmdid(String cmdid) {
		this.cmdid = cmdid;
	}

	public double getOrdamt() {
		return ordamt;
	}

	public void setOrdamt(double ordamt) {
		this.ordamt = ordamt;
	}



	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}



	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
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

	public String getPayuserId() {
		return payuserId;
	}

	public void setPayuserId(String payuserId) {
		this.payuserId = payuserId;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	
}
