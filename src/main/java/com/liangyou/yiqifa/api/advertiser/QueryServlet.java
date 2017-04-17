package com.liangyou.yiqifa.api.advertiser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liangyou.util.StringUtils;
import com.liangyou.yiqifa.util.ConvertUtils;
import com.liangyou.yiqifa.util.IpUtil;
import com.liangyou.yiqifa.util.SignUtil;

/**
 * {@code QueryServlet}类继承<code>javax.servlet.http.HttpServlet</code>,为一起发提供订单数据的查询接口的父类。
 * <p>
 * <br>
 * 接收请求，根据配置文件"yiqifa-config.properties"中的设置，判断是否针对IP与参数签名进行验证.<br>
 * 
 * @author zhangchengming
 * @version 0.1.0
 * @since 0.1.0
 *
 */
public abstract class QueryServlet<T> extends HttpServlet {
	private static final long serialVersionUID = 4822754640829030109L;

	private static Log log = LogFactory.getLog(QueryServlet.class);

	private static final String QUERY_APPOINT_IP = "NO";// 指定查询IP
	private static final String ALLOW_QUERY_APPOINT_IP = "NO"; // 是否实现限定IP功能
	private static final String NEED_VALIDATE_SIGN = "NO"; // 是否实现验证签名
	public static final String DEFAULT_CHARSET = "GBK"; // 默认字符集

	protected String START_TIME_PARAMETER = "orderStartTime";
	protected String END_TIME_PARAMETER = "orderEndTime";

	protected void service(HttpServletRequest request, HttpServletResponse response, String dataKey) throws ServletException {
		PrintWriter out = null;
		response.setCharacterEncoding(DEFAULT_CHARSET);
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			log.error(e1);
			return;
		}

		String backError = null;
		// 验证IP
		if (!validationIp(request)) {// 验证不通过
			backError = "ip is limited !";
			out.print(backError);
			out.flush();
			out.close();
			return;
		}

		// 验证签名
		if (!validationSign(request)) { // 验证不通过
			backError = " sign is error !";
			out.print(backError);
			out.flush();
			out.close();
			return;
		}

		String cid = request.getParameter("cid");// 活动ID
		String st = request.getParameter(START_TIME_PARAMETER);
		String et = request.getParameter(END_TIME_PARAMETER);
		long plceStartTime = 0L;
		long plceEndTime = 0L;
		try {
			plceStartTime = Long.parseLong(StringUtils.isEmpty(st) ? "0" : st);// 按下单时间(开始)
			plceEndTime = Long.parseLong(StringUtils.isEmpty(et) ? "0" : et); // 按下单时间(开始)
		} catch (Exception e) {
			backError = "paramter is not the numeric !";
			log.error(backError);
			out.print(backError);
			out.flush();
			out.close();
			return;
		}

		Date startDate = new Date(plceStartTime * 1000);
		Date endDate = new Date(plceEndTime * 1000);

		List<T> datas = queryDatas(cid, startDate, endDate);
		if (null != datas && !datas.isEmpty()) {
			// 返回数据-JSON格式
			try {
				this.outPut(datas, dataKey, out);
				return;
			} catch (Exception e) {
				backError = "request time out !";
				log.error(backError);
				out.print(backError);
				out.flush();
				out.close();
				return;
			}

		} else {
			backError = "no data !";
			log.error(backError);
			out.print(backError);
			out.flush();
			out.close();
			return;
		}
	}

	/**
	 * <p>
	 * 如果设置了IP白名单，并且请求的IP和白名单中的IP能匹配上，返回true; 如果所有的都不能匹配则返回false。
	 * </p>
	 * <p>
	 * IP白名单和是否需要进行IP验证在配置文件"yiqifa-config.properties"中设置；<br />
	 * <li>allow.query.appoint.ip,"YES"表示需要验证</li>
	 * <li>query_appoint_ip，IP白名单，不同IP以","分隔</li>
	 * </p>
	 * 
	 * @param request
	 * @see com.emar.yiqifa.util.IpUtil#getIp(HttpServletRequest)
	 * @return true表示验证通过，false表示验证失败
	 */
	protected boolean validationIp(HttpServletRequest request) {
		boolean result = true;
		String isLimitIp = ALLOW_QUERY_APPOINT_IP;
		if ("YES".equals(isLimitIp)) { // 如果需要IP限制
			String allowIps = QUERY_APPOINT_IP;
			if (null != allowIps && !"".equals(allowIps)) { // 如果IP白名单
				String accessIp = IpUtil.getIp(request); // 访问者IP
				String[] ips = allowIps.split(",");
				/*
				 * 先将验证结果设置为false，表示验证失败；如果accessIp在白名单中再设置为true，表示验证通过； 这样做的目的是使代码更简单。
				 */
				result = false;
				for (String ip : ips) {
					if (ip.equals(accessIp)) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 签名验证。<br />
	 * 根据接口参数中传过来的的活动ID（cid），下单开始(订单更新开始)时间orderStartTime（updateStartTime）， 下单结束(订单更新结束)时间orderEndTime（updateEndTime），将参数名字和值按url中参数的形式，以参数名字的字母顺序拼接后，做md5加密。<br />
	 * </p>
	 * <p>
	 * 是否需要签名验证在配置文件"yiqifa-config.properties"中设置；<br />
	 * 属性名为need_validate_sign,"YES"表示需要验证
	 * </p>
	 * <p>
	 * 示例如下： cid=1<br />
	 * orderStartTime=1234567890<br />
	 * orderEndTime=1234567890<br />
	 * 
	 * 则用于签名的字符串为“cid=1&orderEndTime=1234567890&orderStartTime=1234567890”
	 * </p>
	 * 
	 * @see com.emar.yiqifa.util.SignUtil#getMd5Sign(Map, String, String, String)
	 * @param request
	 * @return true表示验证通过，false表示验证失败
	 */
	protected boolean validationSign(HttpServletRequest request) {
		boolean result = true;
		if ("YES".equals(NEED_VALIDATE_SIGN)) {
			String mid = request.getParameter("mid");
			if (StringUtils.isEmpty(mid)) {
				result = false;
			} else {
				Map<String, String> dataMap = getParams(request);
				dataMap.remove("mid");
				String sign = SignUtil.getMd5Sign(dataMap, null, null, DEFAULT_CHARSET);
				log.info("sign :" + sign);
				if (!mid.equals(sign)) {
					result = false;
				}
			}
		}
		return result;
	}

	// 获取请求参数和参数值
	private Map<String, String> getParams(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> params = request.getParameterMap();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			map.put(key, params.get(key)[0]);
		}
		return map;
	}

	/**
	 * 该方法需要用户自定义实现，用于提供订单数据。
	 * 
	 * @param Date
	 *            startDate 查询条件：开始时间
	 * @param Date
	 *            endDate 查询条件：结束时间
	 * @return List<Order> 订单集合
	 */
	public abstract List<T> queryDatas(String cid, Date startDate, Date endDate);

	public void outPut(List<T> datas, String dataKey, PrintWriter out) {
		if (null != datas && !datas.isEmpty()) {
			StringBuilder result = new StringBuilder();
			result = new StringBuilder("{\"");
			result.append(dataKey).append("\":[");
			T data = datas.get(0);
			if (data instanceof Order) { // 如果传过一的是订单数据
				int total = datas.size();
				for (int i = 0; i < total; i++) {
					Order order = (Order) datas.get(i);
					result.append(ConvertUtils.convertOrderToJson(order));

					if (i < total - 1) { // 如果不是最后一个元素，则添加"，"
						result.append(",");
					}
				}
				result.append("]}");
			} else if (data instanceof OrderStatus) {
				int total = datas.size();
				for (int i = 0; i < total; i++) {
					OrderStatus os = (OrderStatus) datas.get(i);
					result.append(ConvertUtils.convertOrderStatusToJson(os));

					if (i < total - 1) { // 如果不是最后一个元素，则添加"，"
						result.append(",");
					}
				}
				result.append("]}");
			}
			log.info("datas:[" + result.toString() + "]");
			out.print(result.toString());
		}
		out.flush();
		out.close();
	}
}