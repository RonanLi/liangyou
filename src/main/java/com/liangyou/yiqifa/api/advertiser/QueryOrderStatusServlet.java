package com.liangyou.yiqifa.api.advertiser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code QueryOrderStatusServlet}类继承<code>QueryServlet</code>,为一起发提供订单状态查询接口。用户需要实现<tt>queryDatas()</tt>方法
 * <p>
 * 
 * <tt>service()</tt>方法作用:<br>
 * <ul>
 * 继承{@link QueryServlet}类，根据数据类型:<tt>dataKey</tt>
 * <li>构建方法{@link QueryServlet#service(HttpServletRequest request, HttpServletResponse response, String dataKey)}</li> <br>
 * 当前为订单状态接口,<tt>dataKey</tt> = <tt>orderStatus</tt>表示返回{@link OrderStatus}数据。
 * </ul>
 * <p>
 * <tt>queryDatas()</tt>方法作用:<br>
 * <ul>
 * 用户自定义实现，获取订单状态:{@link OrderStatus}的数据源
 * </ul>
 * <ul>
 * 订单状态接口查询条件：订单更新时间：<tt>updateStartTime</tt>和<tt>updateEndTime</tt>;活动ID:<tt>cid</tt>
 * </ul>
 * 
 * @author zhangchengming
 * @version 0.1.0
 * @since 0.1.0
 *
 */
public abstract class QueryOrderStatusServlet extends QueryServlet<OrderStatus> {
	private static final long serialVersionUID = 2811688991522503618L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.START_TIME_PARAMETER = "updateStartTime";
		super.END_TIME_PARAMETER = "updateEndTime";
		super.service(request, response, "orderStatus");
	}
}