/**
 * 
 */
package com.liangyou.yiqifa.api.advertiser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code QueryOrderServlet}类继承<code>com.emar.yiqifa.api.advertiser.QueryServlet</code>,为亿起发提供订单查询接口。用户需要实现<tt>queryDatas()</tt>方法
 * <p>
 * 
 * <tt>service()</tt>方法作用:<br>
 * <ul>
 * 继承{@link QueryServlet}类，提供请求参数 ；根据数据类型:<tt>dataKey</tt>
 * <li>构建方法{@link QueryServlet#service(HttpServletRequest request, HttpServletResponse response, String dataKey)}</li> <br>
 * 当前为订单接口,<tt>dataKey</tt> = <tt>orders</tt>表示返回Order数据。
 * </ul>
 * <p>
 * <tt>queryDatas()</tt>方法作用:<br>
 * <ul>
 * 用户自定义实现，获取订单:{@link Order}的数据源
 * </ul>
 * <ul>
 * 订单状态接口查询条件：订单下单时间：<tt>orderStatTime</tt>和<tt>orderEndTime</tt>;活动ID:<tt>cid</tt>
 * </ul>
 * 
 * @author zhangchengming
 * @version 0.1.0
 * @since 0.1.0
 *
 */
public abstract class QueryOrderServlet extends QueryServlet<Order> {
	private static final long serialVersionUID = -2083906495156358966L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.START_TIME_PARAMETER = "orderStartTime";
		super.END_TIME_PARAMETER = "orderEndTime";
		super.service(request, response, "orders");
	}

}
