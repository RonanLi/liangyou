package com.liangyou.tool.wechatkit;

import org.apache.log4j.Logger;

import com.liangyou.cache.DefaultAccessTokenCache;
import com.liangyou.cache.DefaultJsTicketCache;
import com.liangyou.cache.IAccessTokenCache;
import com.liangyou.cache.IJsTicketCache;
import com.liangyou.model.wechatModel.ApiConfig;


/**
 * @author lijing
 *
 *微信Api配置
 */
public class ApiConfigKit {
	private static Logger logger = Logger.getLogger(ApiConfigKit.class);
	private static ApiConfig ac;
	private static boolean devMode = false;
	//静态初始化
	static {
		ac = new ApiConfig();
		// 配置微信 API 相关常量
		if (PropKit.getProp("weixin_config.txt") == null) {
			PropKit.use("weixin_config.txt");
		}
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		ApiConfigKit.setDevMode(PropKit.getBoolean("devMode"));
		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey","setting it in config file"));
	}
	
	public static ApiConfig getApiConfig() {
		ApiConfig result = ac;
		logger.info("初始化信息:"+ac.toString());
		if (result == null)
			throw new IllegalStateException("ApiConfig未初始化");
		return result;
	}
	private static void setDevMode(Boolean devMode) {
		ApiConfigKit.devMode = devMode;
	}
	static IAccessTokenCache accessTokenCache = new DefaultAccessTokenCache();
	static IJsTicketCache jsTicketCache = new DefaultJsTicketCache();
	public static void setAccessTokenCache(IAccessTokenCache accessTokenCache) {
		ApiConfigKit.accessTokenCache = accessTokenCache;
	}

	public static IAccessTokenCache getAccessTokenCache() {
		return ApiConfigKit.accessTokenCache;
	}
	public static void setJsTicketCache(IJsTicketCache jsTicketCache) {
		ApiConfigKit.jsTicketCache = jsTicketCache;
	}
	public static IJsTicketCache getJsTicketCache() {
		return ApiConfigKit.jsTicketCache;
	}
}
