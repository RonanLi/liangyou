package com.liangyou.yiqifa.util;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类. 根据httpServletRequest对象获取http请求来源地的ip字符串
 */
public class IpUtil {
	/**
	 * 获取发送请求的客户端(或最后一个代理)的 IP字符串
	 * 
	 * @param request
	 *            HttpServletRequest对象
	 * @return request中的IP字符串
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		} else {
			if (ip.indexOf(",") != -1) {
				String[] s = ip.split(",");
				for (int i = 0; i < s.length; i++) {
					if (s[i] != null && !"".equals(s[i]) && !"unknown".equals(s[i])) {
						return s[i].trim();
					}
				}
			} else if (ip.indexOf(";") != -1) {
				String[] s = ip.split(";");
				for (int i = 0; i < s.length; i++) {
					if (s[i] != null && !"".equals(s[i]) && !"unknown".equals(s[i])) {
						return s[i].trim();
					}
				}
			} else {
				if ("unknown".equals(ip)) {
					return ip.trim();
				}
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return (ip != null) ? ip.trim() : ip;
	}
}
