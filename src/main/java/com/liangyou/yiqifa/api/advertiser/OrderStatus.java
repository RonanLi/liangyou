/**
 * 
 */
package com.liangyou.yiqifa.api.advertiser;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>Product</code>用于存放订单中的订单状态信息 。
 * <p>
 * <code>Product</code>的属性如下：<br />
 * <li>1.订单编号：<code>orderNo</code></li>
 * <li>2.订单状态：<code>orderStatus</code></li>
 * <li>3.支付状态：<code>paymentStatus</code></li>
 * <li>4.支付方式：<code>paymentType</code></li>
 * <li>5.更新时间：<code>updateTime</code></li>
 * <li>6.反馈标签：<code>feedback</code></li>
 */
public class OrderStatus implements Serializable {
	protected static Log log = LogFactory.getLog(OrderStatus.class);
	private static final long serialVersionUID = 5002935116754282577L;

	// private String campaignId;//活动编号
	private String orderNo;// 订单编号
	private String orderStatus;// 订单状态
	private String paymentStatus;// 支付状态
	private String paymentType;// 支付方式
	private Date updateTime;// 更新时间
	private String feedback; // 反馈标签

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	// public String getCampaignId() {
	// return campaignId;
	// }
	// public void setCampaignId(String campaignId) {
	// this.campaignId = campaignId;
	// }
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public OrderStatus() {

	}
	
	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}

}
