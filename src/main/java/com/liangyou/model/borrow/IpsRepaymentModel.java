package com.liangyou.model.borrow;

/** 
*未经授权不得进行修改、复制、出售及商业使用。
* @ClassName:RDtuoguan_P2P
* @Description: 环迅还款封装类
* <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
* @author wujing  wj@erongdu.com
* @date 2014-10-29 下午4:12:54 
* @version:1.0 
*/
public class IpsRepaymentModel {
	
	private long repaymentId;  //还款计划id
	
	private String resultCode;  //返回码
	
	private String resultMsg;  //返回信息
	
	private String money;  //本次还款金额用于存放金额：本金，利息，罚息
	
	private String collectionIds;  //待收id
	
	private String ordId;

	public long getRepaymentId() {
		return repaymentId;
	}

	public void setRepaymentId(long repaymentId) {
		this.repaymentId = repaymentId;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(String collectionIds) {
		this.collectionIds = collectionIds;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}
	
	
	

}
