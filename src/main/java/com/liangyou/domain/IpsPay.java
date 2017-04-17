package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/** 
* @ClassName:RDtuoguan_P2P
* @Description: TODO 环迅封装类model
* @author wujing  wj@erongdu.com
* @date 2014-10-28 上午9:12:09 
* @version:1.0 
*/
@Entity
@Table(name ="ips_pay")
public class IpsPay {
	
	public IpsPay(){
		
	}
	public IpsPay(long borrowId,long payUserid,long toUserId,String payUserApiId,String toUserApiId){
		this.borrowId = borrowId;
		this.payUserId=payUserid;
		this.toUserId = toUserId;
		this.payApiId = payUserApiId;
		this.toApiId = toUserApiId;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String status;  //状态     :1、成功，2、失败，0、待处理，3、处理中
	
	private String resultMsg;   //返回结果
	
	private String payType; //交易接口
	
	private String ordId;   //订单号
	
	private long payUserId; //付款人
	
	private long toUserId;//收款人
	
	private long borrowId;//标id
	
	private Date addTime;  //添加时间
	
	private String tenderNo;  //投标交易号 
	
	private String borrowNo;  //标号
	
	/**
	 * 1：投资（报文提交关系，转出方：转入方=N：1）， 
	 *2：代偿（报文提交关系，转出方：转入方=1：N）， 
	 *3：代偿还款（报文提交关系，转出方：转入方=1：1）， 
	 *4：债权转让（报文提交关系，转出方：转入方=1：1），
	 */
	private String transferType;  //交易类型  transfer接口专用
	
	private String payApiId;   //付款人第三方账户
	 
	private String toApiId;   //收款人第三方账户
	
	private String payUserFee;  //付款人手续费
	
	private String toUserFee;  //收款人手续费
	
	private String transMoney;  //交易金额
	
	private String returnCode;  //返回码
	
	
	
	

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}



	public long getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(long payUserId) {
		this.payUserId = payUserId;
	}

	public String getTenderNo() {
		return tenderNo;
	}

	public void setTenderNo(String tenderNo) {
		this.tenderNo = tenderNo;
	}

	public String getBorrowNo() {
		return borrowNo;
	}

	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getPayApiId() {
		return payApiId;
	}

	public void setPayApiId(String payApiId) {
		this.payApiId = payApiId;
	}

	public String getToApiId() {
		return toApiId;
	}

	public void setToApiId(String toApiId) {
		this.toApiId = toApiId;
	}

	public String getPayUserFee() {
		return payUserFee;
	}

	public void setPayUserFee(String payUserFee) {
		this.payUserFee = payUserFee;
	}

	public String getToUserFee() {
		return toUserFee;
	}

	public void setToUserFee(String toUserFee) {
		this.toUserFee = toUserFee;
	}

	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public long getBorrowId() {
		return borrowId;
	}

	public void setBorrowId(long borrowId) {
		this.borrowId = borrowId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getTransMoney() {
		return transMoney;
	}

	public void setTransMoney(String transMoney) {
		this.transMoney = transMoney;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}



	
	
}
