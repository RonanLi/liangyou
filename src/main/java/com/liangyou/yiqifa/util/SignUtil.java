package com.liangyou.yiqifa.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class SignUtil {

	/** 编码格式 */
	private String charset = "GBK";// TODO

	/**
	 * 对参数集合params做md5签名：签名原始字符串为： prefix + 参数串 + subfix
	 * 
	 * @param dataMap
	 *            数据映射
	 * 
	 * @param prefix
	 *            前缀
	 * @param subfix
	 *            后缀
	 * @param charset
	 *            查询串编码格式
	 * @return md5签名
	 */
	public static String getMd5Sign(Map<String, String> dataMap, String prefix, String subfix, String charset) {
		String queryString = map2Str(new TreeMap<String, String>(dataMap), "&", "=");
		String sign = getMd5Sign(queryString, prefix, subfix, charset);
		return sign;
	}

	/**
	 * 将数据映射Map转换为字符串
	 * 
	 * @param dataMap
	 *            数据映射
	 * @param separate
	 *            分隔符
	 * @param connector
	 *            连接符
	 * @return 字符串
	 */
	public static String map2Str(Map<String, String> dataMap, String separate, String connector) {

		if (dataMap == null || dataMap.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		if (dataMap != null) {
			Set<Entry<String, String>> entrySet = dataMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())) {
					sb.append(entry.getKey());
					if (StringUtils.isNotBlank(connector)) {
						sb.append(connector);
					}
					sb.append(entry.getValue());
					if (StringUtils.isNotBlank(separate)) {
						sb.append(separate);
					}
				}
			}
			if (!dataMap.isEmpty() && StringUtils.isNotBlank(separate)) {
				sb.setLength(sb.length() - separate.length());
			}
		}
		return sb.toString();
	}

	/**
	 * 对查询串queryString做md5签名：签名原始字符串为：prefix + queryString + subfix
	 * 
	 * @param queryString
	 *            查询串
	 * @param prefix
	 *            前缀
	 * @param subfix
	 *            后缀
	 * @param charset
	 *            查询串编码格式
	 * @return md5签名
	 */
	public static String getMd5Sign(String queryString, String prefix, String subfix, String charset) {
		String str = queryString;// 需要签名的字符串
		if (StringUtils.isBlank(str)) {
			return "";
		}

		/**
		 * 直接是apache的库
		 * 
		 * MD5加密
		 * 
		 * <pre>
		 * MD5:即Message-Digest Algorithm 5（信息-摘要算法 5），用于确保信息传输完整一致。
		 * MD5是计算机广泛使用的杂凑算法之一（又译摘要算法、哈希算法），主流编程语言普遍已有MD5实现。
		 * MD5将数据（如汉字）运算为另一固定长度值，是杂凑算法的基础原理，MD5的前身有MD2、MD3和MD4。
		 * MD5的作用是让大容量信息在用数字签名软件签署私人密钥前被"压缩"成一种保密的格式（就是把一个任意长度的字节串变换成一定长的十六进制数字串）
		 * </pre>
		 * */
		return MD5Encode.md5(str);

	}

	/**
	 * get charset
	 * 
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * set charset
	 * 
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
}
