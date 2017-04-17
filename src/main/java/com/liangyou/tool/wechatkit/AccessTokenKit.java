package com.liangyou.tool.wechatkit;

import java.util.Map;
import java.util.concurrent.Callable;

import com.liangyou.cache.IAccessTokenCache;
import com.liangyou.model.wechatModel.AccessToken;
import com.liangyou.model.wechatModel.ApiConfig;


public class AccessTokenKit {

	// "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static String url = "https://api.weixin.qq.com/cgi-bin/token";

	// 利用 appId 与 accessToken 建立关联，支持多账户
	static IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();

	/**
	 * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
	 */
	public static AccessToken getAccessToken() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		AccessToken result = accessTokenCache.get(appId);
		if(result != null) {
			System.out.println(result);
		}
		if (result != null && result.isAvailable())
			return result;
		refreshAccessToken();
		return accessTokenCache.get(appId);
	}

	/**
	 * 直接获取 accessToken 字符串，方便使用
	 * 
	 * @return String accessToken
	 */
	public static String getAccessTokenStr() {
		return getAccessToken().getAccessToken();
	}

	/**
	 * 强制更新 access token 值
	 */
	public static synchronized void refreshAccessToken() {
		ApiConfig ac = ApiConfigKit.getApiConfig();
		String appId = ac.getAppId();
		String appSecret = ac.getAppSecret();
		final Map<String,String> queryParas = ParamMap.create("grant_type", "client_credential").put("appid", appId).put("secret", appSecret).getData();
		AccessToken accessToken = RetryUtils.retryOnException(3, new Callable<AccessToken>() {

			@Override
			public AccessToken call() throws Exception {
				String json = HttpUtils.doGet(url,queryParas);
				return new AccessToken(json);
			}
			
		});
		/*{"access_token":"ohCgScB8MC1iPt6fgG6xYP8RO_HBuWZBiOH7zEEGNrDk_r8gpRRpN0dlG_WlS2hbFO9mM9sv-uQ-BWsdLyGbVlSbhAfk8rFHr8mLOPI65hoMj5CMN5kHw60UFaF-BLRYMJYcAFAQNC",
		 * "expires_in":7200}*/
		
		accessTokenCache.set(appId, accessToken);
	}
	//测试
	public static void main(String[] args) {
		String json ="{\"access_token\":\"9Lqgy5qEZF6hHmIYONrH0qFcvbzhabayXEmo8CaNHwVrWgQKrAAM0cinzxvIafGAo21p5FRyH4Y7NYhxkOzp_7p1Q8-KVHe8up3QqgKUVKC2ioDiCOb1FDt445d8vEpTQEXeACAJXQ\",\"expires_in\":7200}";
		
	}
}