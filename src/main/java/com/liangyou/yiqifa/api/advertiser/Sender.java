package com.liangyou.yiqifa.api.advertiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.util.StringUtils;
import com.liangyou.yiqifa.util.ConvertUtils;

/**
 * {@code Sender} 类为实时数据接口类, 接口用于发送广告主的订单数据, 广告主需要在订单生成后、支付前调用此接口向亿起发发送订单数据。 发送状态结果请参考 {@link #send()}方法。
 */
public class Sender {
	private static Log log = LogFactory.getLog(Sender.class);
	/**
	 * 订单发送结果.
	 * 
	 * 返回0表示发送成功。 返回1表示缺少必要的参数。 返回2表示参数格式错误。 返回3表示链接超时。 返回4表示URL格式错误。 返回5表示IO异常。 返回-1表示发送失败。
	 */
	public static final int SEND_STATUS_SUCCESS = 0; // 0表示正常发送.
	public static final int SEND_STATUS_LACK_PARAMETERS = 1; // 1表示缺少必要的参数. {@code order == null} {@code order != null} 而 {@code orderNo == null}
	public static final int SEND_PARAMETER_FORMAT_ERROR = 2; // 2表示参数格式错误.
	public static final int SEND_STATUS_TIMEOUT = 3; // 3表示链接超时.
	public static final int SEND_STATUS_URL_ERRO = 4; // 4表示URL格式错误.
	public static final int SEND_STATUS_IO_ERRO = 5; // 5表示IO异常.
	public static final int SEND_STATUS_OTHER_ERRO = -1; // -1表示发送失败.
	public static final int SEND_STATUS_OTHER_SUCCESS = 0;

	private static final int CONNECT_TIMEOUT = 3000; // 链接超时的时间.
	public static final String DEFAULT_CHARSET = "GBK"; // 默认字符集

	private static final String COOLIE_NAME = "union_cookie";
	private static final String DATA_RECRIVER_URL = "http://o.yiqifa.com/servlet/handleCpsInterIn";// 订单推送地址
	private static final String ORDER_INTERFACE_ID = "57d79f10b52af54390312737"; // 订单接口ID
	private static final String ORDER_STATUS_INTERFACE_ID = ""; // 订单状态接口ID

	private Order order;
	private OrderStatus orderStatus;

	public Sender() {
	}

	public Sender(Order order) {
		this.order = order;
	}

	public Sender(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * 向亿起发推送订单数据.
	 */
	public Map<String, Object> sendOrder(Borrow borrow, BorrowTender borrowTender, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String aid = "", feedback = "", cid = "", target = "", channel = "";
		if (request != null) {
			String cookieValue = getCookieValue(COOLIE_NAME, request);
			if (cookieValue != null) {
				log.info("获取到系统cookie：" + cookieValue);
				String cookieValueReplace = cookieValue.replace("_", "");
				if (StringUtils.isBlank(cookieValueReplace)) {
					log.info("获取到系统cookie，但是cookie值为空");
					resultMap.put("resultCode", SEND_STATUS_OTHER_ERRO);
					return resultMap;
				} else {
					String[] values = cookieValue.split("_");
					aid 	 = values.length > 0 ? values[0] : "";
					channel  = values.length > 1 ? values[1] : "";
					feedback = values.length > 3 ? values[3] : "";
					target   = values.length > 4 ? values[4] : "";
					if (values.length > 2 && StringUtils.isNumeric(values[2].trim())) {
						cid = values[2].trim();
					}
					
					String category = "", commissionType = "";
					if (borrow.getTimeLimit() == 1) {
						category = "1";
						commissionType = "A";
					}
					if (borrow.getTimeLimit() == 3) {
						category = "3";
						commissionType = "B";
					}
					if (borrow.getTimeLimit() == 6) {
						category = "6";
						commissionType = "C";
					}
					
					log.info("亿起发数据推送 ：BID：" + borrow.getId() + ", category：" + category + ", commissionType：" + commissionType);
					
					order = new Order();
					orderStatus = new OrderStatus();
					Product product = new Product();
					product.setProductNo(String.valueOf(borrow.getId()));
					product.setName(borrow.getName());
					product.setAmount("1");
					product.setPrice(String.valueOf(borrowTender.getMoney()));
					product.setCategory(category);
					product.setCommissionType(commissionType);

					order.setOrderNo(borrowTender.getSubOrdId());
					order.setOrderTime(borrowTender.getAddtime());
					order.setOrderStatus(orderStatus);
					order.addProduct(product);
					order.setCampaignId(Long.parseLong(cid));
					order.setFeedback(feedback);
				}
				
				if (order == null) {
					log.error("推送订单信息异常: 没有order");
					resultMap.put("resultCode", SEND_STATUS_LACK_PARAMETERS);
					return resultMap;
				}
				if (order.getProducts().isEmpty()) {
					log.error("缺少必要的参数: product");
					resultMap.put("resultCode", SEND_STATUS_LACK_PARAMETERS);
					return resultMap;
				}
				if (order.getOrderNo() == null) {
					log.error("缺少必要的参数: orderNo");
					resultMap.put("resultCode", SEND_STATUS_LACK_PARAMETERS);
					return resultMap;
				}
				if (order.getCampaignId() == null) {
					log.error("缺少必要的参数: campaignId");
					resultMap.put("resultCode", SEND_STATUS_LACK_PARAMETERS);
					return resultMap;
				}
				if (StringUtils.isBlank(order.getFeedback())) {
					log.error("缺少必要的参数: feedback");
					resultMap.put("resultCode", SEND_STATUS_LACK_PARAMETERS);
					return resultMap;
				}

				this.sortByProductNo(order.getProducts());// 按商品编号自然排序
				StringBuilder result = new StringBuilder("{\"orders\":[");
				result.append(ConvertUtils.convertOrderToJson(order)).append("]}");
				log.info("推送订单请求json参数 : " + result.toString());
				int resultCode = Sender.httpPostData(DATA_RECRIVER_URL, CONNECT_TIMEOUT, result.toString(), ORDER_INTERFACE_ID);
				
				resultMap.put("cid", order.getCampaignId());
				resultMap.put("aid", aid);
				resultMap.put("feedback", order.getFeedback());
				resultMap.put("target", target);
				resultMap.put("channel", channel);
				resultMap.put("resultCode", resultCode);
				return resultMap;
			} else {
				log.info("推送订单信息异常: 没有找到cookie");
				resultMap.put("resultCode", SEND_STATUS_OTHER_ERRO);
				return resultMap;
			}
		}
		return resultMap;
	}

	/**
	 * 向亿起发推送订单状态
	 * 
	 * @return
	 */
	public int sendOrderStatus() {
		if (orderStatus == null) {
			log.error("缺少必要的参数: orderStatus");
			return SEND_STATUS_LACK_PARAMETERS;
		}
		if (orderStatus.getOrderNo().isEmpty()) {
			log.error("缺少必要的参数: product");
			return SEND_STATUS_LACK_PARAMETERS;
		}
		// 发送订单状态
		StringBuilder result = new StringBuilder("{\"orderStatus\":[");
		result.append(ConvertUtils.convertOrderStatusToJson(orderStatus)).append("]}");

		log.info("推送订单状态请求json参数 : " + result.toString());
		return Sender.httpPostData(DATA_RECRIVER_URL, CONNECT_TIMEOUT, result.toString(), ORDER_STATUS_INTERFACE_ID);
	}

	/**
	 * 获取cookie
	 * 
	 * @param cookieName
	 * @param request
	 * @return 返回符合条件cookie值
	 * 
	 */
	private String getCookieValue(String cookieName, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ((c.getName()).equals(cookieName)) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * 自然排序商品编号
	 * 
	 * @param products
	 * @return
	 */
	private List<Product> sortByProductNo(List<Product> products) {
		Comparator<Product> comparator = new Comparator<Product>() {
			public int compare(Product p1, Product p2) {
				return p1.getProductNo().compareTo(p2.getProductNo());
			}
		};
		Collections.sort(products, comparator);
		return products;
	}

	/**
	 * 发送请求
	 * 
	 * @param uri
	 * @param requestTimeOut
	 * @param postData
	 * @param interId
	 * @return
	 */
	private static int httpPostData(String uri, int requestTimeOut, String postData, String interId) {
		int retStr = -1;
		int tmout = 10;
		if (requestTimeOut > 0) {
			tmout = requestTimeOut;
		}
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("interId", interId));
			nameValuePairs.add(new BasicNameValuePair("json", postData));
			// 设置超时
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, tmout);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, tmout);

			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));

			HttpResponse response;
			response = httpClient.execute(httpPost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String result = reader.readLine(); // 查找返回值0：成功；非0失败
			log.info("亿起发推送结果result:" + result);
			if (code == 200 && "0".equals(result)) {
				retStr = 0;
			} else {
				return SEND_STATUS_OTHER_ERRO;
			}
			return retStr;
		} catch (ClientProtocolException e) {
			log.error("Yiqifa-Advertiser-Api:request inconformity  HTTP", e);
			return SEND_STATUS_IO_ERRO;
		} catch (IOException e) {
			log.error("Yiqifa-Advertiser-Api:interrupted I/O operations", e);
			return SEND_STATUS_IO_ERRO;
		} catch (Exception e) {
			log.error("Yiqifa-Advertiser-Api:Failed to send data", e);
			return SEND_STATUS_OTHER_ERRO;
		}
	}
}
