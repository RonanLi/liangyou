package com.liangyou.yiqifa.api.advertiser;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <code>Product</code>用于存放订单中的商品信息 。
 * <p>
 * <code>Product</code>的属性如下：<br />
 * <li>1.商品编号：<code>productNo</code></li>
 * <li>2.商品名称：<code>name</code></li>
 * <li>3.商品数量：<code>amount</code></li>
 * <li>4.商品价格：<code>price</code></li>
 * <li>5.商品类别：<code>category</code></li>
 * <li>6.佣金类型：<code>commissionType</code></li>
 * </p>
 * <br>
 * <p>
 * <code>Product</code>在亿起发实时接口中的使用方式如下： <blockquote>
 * 
 * <pre>
 *     Order order = new Order();
 *     不含有佣金类型:order.addProduct(new Product(productNo, name, amount, price, category));<br>
 *     含有佣金类型:order.addProduct(new Product(productNo, name, amount, price, category, commissionType));
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author zhangchengming
 * @version 0.1.0
 * @see com.emar.yiqifa.api.advertiser.Order
 * @since 0.1.0
 * 
 */
public class Product implements java.io.Serializable {
	private static final long serialVersionUID = 3860592331478709261L;
	private String productNo;
	private String name = "";
	private String amount;
	private String price;
	private String category;
	private String commissionType;

	public Product() {
	}

	public Product(String productNo, String name, String amount, String price, String category) {
		this.productNo = productNo;
		this.name = name;
		this.amount = amount;
		this.price = price;
		this.category = category;
	}

	public Product(String productNo, String name, String amount, String price, String category, String commissionType) {
		this.productNo = productNo;
		this.name = name;
		this.amount = amount;
		this.price = price;
		this.category = category;
		this.commissionType = commissionType;
	}

	/**
	 * 获取商品编号
	 */
	public String getProductNo() {
		return productNo;
	}

	/**
	 * 设置商品编号
	 */
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	/**
	 * 获取商品名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取商品数量
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * 设置商品数量
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * 获取商品单价
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * 设置商品单价
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * 获取商品类别
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 设置商品类别
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * 获取佣金类型
	 */
	public String getCommissionType() {
		return commissionType;
	}

	/**
	 * 添加佣金类型
	 */
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	
	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString (this);
	}


}
