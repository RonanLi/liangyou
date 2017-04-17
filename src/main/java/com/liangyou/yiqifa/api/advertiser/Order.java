package com.liangyou.yiqifa.api.advertiser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <code>Order</code>类用于存放订单信息。
 * <p>
 * <code>Order</code>类的属性如下：<br />
 * <li>1.订单编号：<code>orderNo</code></li>
 * <li>2.下单时间：<code>orderTime</code></li>
 * <li>3.活动ID：<code>campaignId</code>，从cookie或数据库中读取</li>
 * <li>4.反馈标签：<code>feedback</code>，从cookie或数据库中读取</li>
 * <li>5.运费：<code>fare</code></li>
 * <li>6.优惠金额：<code>favorable</code></li>
 * <li>7.优惠码：<code>favorableCode</code></li>
 * <li>8.订单状态：<code>orderStatus</code>,<code>订单状态类</code>请看{@link com.emar.yiqifa.api.advertiser.OrderStatus}</li>
 * <li>9.更新时间：<code>updateTime</code></li>
 * <li>10.商品：<code>products</code>,<code>商品类</code>请看 {@link com.emar.yiqifa.api.advertiser.Product}</li>
 * </p>
 * <p>
 * 在调用{@link #addProduct(Product product)}向订单添加商品时会计算订单总金额，记录商品的佣金类型；<br>
 * 订单总金额是在拼装亿起发的接口参数时用到，用来计算用户实际为商品支付的金额；<br>
 * 佣金类型用于判断如何拼装亿起发接口中相同佣金类型的商品的数量、金额、名称等商品信息；<br>
 * 详情看{@link com.emar.yiqifa.api.advertiser.Sender}。
 * </p>
 * <p>
 * <code>Order</code>类在亿起发实时接口中的使用方式如下：
 * </p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 *     Order order = new Order();
 *     order.setOrderNo("2011112776545");
 *     order.setOrderTime(orderTime);
 *     order.setUpdateTime(updateTime);
 *     order.setCampaignId(101L);
 *     order.setSiteId(siteId);
 *     order.setFeedback(feedback);
 *     
 *     order.setFare(fare);
 *     order.setFavorable(favorable);
 *     order.setFavorableCode(favcode);
 *     order.setOrderStatus(orderStatus);
 *     order.setPaymentStatus(payStatus);
 *     order.setPaymentType(payType);
 *     
 *     order.addProduct(product);
 *     ……
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author zhagnchegnming
 * @author houyefeng 在2013-04-01号更新注释
 * @version 0.1.0
 * @see com.emar.yiqifa.api.advertiser.Product
 * @see com.emar.yiqifa.api.advertiser.Sender#sendOrder()
 * @see com.emar.yiqifa.api.advertiser.Sender#sendOrder(javax.servlet.http.HttpServletRequest)
 * @since 0.1.0
 * 
 */
public class Order implements java.io.Serializable {
	private static final long serialVersionUID = -6994387366307454084L;
	private List<Product> products = new ArrayList<Product>();
	private String orderNo;// 订单编号
	private Date orderTime;// 下单时间
	private Date updateTime;// 更新时间
	private Long campaignId;// 活动编号
	private String feedback;// 反馈标签
	private double fare;// 运费
	private double favorable;// 优惠金额
	private String favorableCode;// 优惠码
	private OrderStatus orderStatus = new OrderStatus();

	public Order() {
	}

	/**
	 * 订单中所有商品
	 * 
	 * @return 订单中商品集合
	 * 
	 */
	public List<Product> getProducts() {

		return products;
	}

	/**
	 * 获取订单编号
	 * 
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * 添加订单编号
	 * 
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 获取订单更新时间
	 * 
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 设置订单更新时间。
	 * 
	 * @param 返回为Date类型
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 获取下单时间
	 * 
	 * @return 返回为Date类型
	 */
	public Date getOrderTime() {
		return orderTime;
	}

	/**
	 * 设置下单时间。
	 * <p>
	 */
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	/**
	 * 获取活动id
	 * 
	 */
	public Long getCampaignId() {
		return campaignId;
	}

	/**
	 * 添加活动id
	 * 
	 * @param campaignId
	 *            广告主在亿起发创建的活动的ID
	 * 
	 */
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	/**
	 * 获取运费
	 * 
	 */
	public double getFare() {
		return fare;
	}

	/**
	 * 添加运费
	 * 
	 */
	public void setFare(double fare) {
		this.fare = fare;
	}

	/**
	 * 获取优惠金额
	 * 
	 */
	public double getFavorable() {
		return favorable;
	}

	/**
	 * 添加优惠金额
	 * 
	 */
	public void setFavorable(double favorable) {
		this.favorable = favorable;
	}

	/**
	 * 获取优惠码
	 * 
	 */
	public String getFavorableCode() {
		return favorableCode;
	}

	/**
	 * 添加优惠码
	 * 
	 */
	public void setFavorableCode(String favorableCode) {
		this.favorableCode = favorableCode;
	}

	/**
	 * <code>addProduct(pro)</code>方法作用：
	 * <p>
	 * <li>向订单中添加商品</li>
	 * <li>佣金类型不为空时,将商品按佣金类型进行分类</li>
	 * <li>商品编号有重复时,过滤重复的商品编号</li>
	 * <li>计算订单的总金额</li>
	 * 
	 * @param product
	 *            订单商品
	 */
	public void addProduct(Product product) {
		products.add(product);

	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * 如果orderNo有值，orderStatus.orderNo将被orderNo的值替换; 如果feedback有傎，orderStatus.feedback将被feedback的值替换。
	 * 
	 * @param orderStatus
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
		if (StringUtils.isNotBlank(this.orderNo)) {
			orderStatus.setOrderNo(this.orderNo);
		}
		if (StringUtils.isNotBlank(this.feedback)) {
			orderStatus.setFeedback(this.feedback);
		}
		this.orderStatus = orderStatus;
	}
	
	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}


}
