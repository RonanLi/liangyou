/**
 * 
 */
package com.liangyou.yiqifa.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liangyou.yiqifa.api.advertiser.Order;
import com.liangyou.yiqifa.api.advertiser.OrderStatus;

/**
 * @author houyefeng
 *
 */
public class ConvertUtils {
	private static Log log = LogFactory.getLog(ConvertUtils.class);

	public static String convertOrderToJson(Order order) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		StringBuilder json = new StringBuilder("{ ");
		json.append("\"orderNo\":").append("\"" + order.getOrderNo() + "\",");
		// 处理下单时间
		if (null == order.getOrderTime()) {
			log.debug("order:" + order.getOrderNo() + ", order time is NULL, use the system time to replace");
			json.append("\"orderTime\":").append("\"").append(sdf.format(new Date())).append("\",");
		} else {
			json.append("\"orderTime\":").append("\"").append(sdf.format(order.getOrderTime())).append("\",");
		}
		// 处理订单更新时间
		if (null == order.getUpdateTime()) {
			log.debug("order:" + order.getOrderNo() + ", order update time is NULL, use the system time to replace");
			json.append("\"updateTime\":").append("\"").append(sdf.format(new Date())).append("\",");
		} else {
			json.append("\"updateTime\":").append("\"").append(sdf.format(order.getUpdateTime())).append("\",");
		}
		json.append("\"campaignId\":").append(order.getCampaignId()).append(",");
		json.append("\"feedback\":\"").append((null == order.getFeedback() ? "" : order.getFeedback())).append("\",");
		json.append("\"fare\":").append(order.getFare()).append(",");
		json.append("\"favorable\":").append(order.getFavorable()).append(",");
		json.append("\"favorableCode\":\"").append((null == order.getFavorableCode() ? "" : order.getFavorableCode())).append("\",");
		json.append("\"products\":");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("products", order.getProducts());
		JSONObject products = JSONObject.fromObject(map);
		json.append(products.get("products"));
		// 处理订单状态
		if (null != order.getOrderStatus()) {
			OrderStatus os = order.getOrderStatus();
			json.append(",\"orderStatus\":\"").append((null == os.getOrderStatus() ? "" : os.getOrderStatus())).append("\",");
			json.append("\"paymentStatus\":\"").append((null == os.getPaymentStatus() ? "" : os.getPaymentStatus())).append("\",");
			json.append("\"paymentType\":\"").append((null == os.getPaymentType() ? "" : os.getPaymentType())).append("\"");
		}
		json.append("}");
		return json.toString();
	}

	public static String convertOrderStatusToJson(OrderStatus os) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		StringBuilder json = new StringBuilder("{ ");
		json.append("\"orderNo\":").append("\"" + os.getOrderNo() + "\",");
		// 处理订单更新时间
		if (null == os.getUpdateTime()) {
			log.debug("order:" + os.getOrderNo() + ", order update time is NULL, use the system time to replace");
			json.append("\"updateTime\":").append("\"").append(sdf.format(new Date())).append("\",");
		} else {
			json.append("\"updateTime\":").append("\"").append(sdf.format(os.getUpdateTime())).append("\",");
		}
		json.append("\"feedback\":\"").append((null == os.getFeedback() ? "" : os.getFeedback())).append("\",");
		// 处理订单状态
		json.append("\"orderStatus\":\"").append((null == os.getOrderStatus() ? "" : os.getOrderStatus())).append("\",");
		json.append("\"paymentStatus\":\"").append((null == os.getPaymentStatus() ? "" : os.getPaymentStatus())).append("\",");
		json.append("\"paymentType\":\"").append((null == os.getPaymentType() ? "" : os.getPaymentType())).append("\"");

		json.append("}");
		return json.toString();
	}
}
