package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



/** 
*未经授权不得进行修改、复制、出售及商业使用。
* @ClassName:RDtuoguan_P2P
* @Description:用户还款触发环迅记录
* <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
* @author wujing  wj@erongdu.com
* @date 2014-10-29 下午5:10:03 
* @version:1.0 
*/
@Entity
@Table(name="ips_repay_detail")
public class IpsRepayDetail implements Serializable {


	private static final long serialVersionUID = 1L;
	
	public IpsRepayDetail(){
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private long repayId;  //还款计划id
	
	private String ordId;
	
	private String collectionIds;
	
	private int status;  //0、待处理，1、处理成功，2、处理失败，3、处理中
	
	private double capital;
	
	private double interest;
	
	private double lateFee;
	
	private double compensation; //补偿金
	
	private String returnCode;
	
	private String returnMsg;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public String getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(String collectionIds) {
		this.collectionIds = collectionIds;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getCapital() {
		return capital;
	}

	public void setCapital(double capital) {
		this.capital = capital;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

	public double getCompensation() {
		return compensation;
	}

	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public long getRepayId() {
		return repayId;
	}

	public void setRepayId(long repayId) {
		this.repayId = repayId;
	}


	
	
	
	

}
