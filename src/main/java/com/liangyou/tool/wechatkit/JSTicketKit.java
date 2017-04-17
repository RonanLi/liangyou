package com.liangyou.tool.wechatkit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.cache.IAccessTokenCache;
import com.liangyou.cache.IJsTicketCache;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.wechatModel.AccessToken;
import com.liangyou.model.wechatModel.JsTicket;

public class JSTicketKit {
	private  static  Logger logger = Logger.getLogger(JSTicketKit.class);
	private  static String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
	private static IJsTicketCache jsTicketCache = ApiConfigKit.getJsTicketCache();
	public static JsTicket getJsapiTicket() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		JsTicket ticket = jsTicketCache.get(appId);
		if(ticket != null) {
			System.out.println(ticket);
		}
		if (ticket != null && ticket.isAvailable())
			return ticket;
		refreshJsTiecket();
		return jsTicketCache.get(appId);
	}
	private static synchronized void refreshJsTiecket() {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = String.format(url,ACCESS_TOKEN);
		String jsonStr = HttpUtils.doGet(apiUrl);
		JsTicket ticket = new JsTicket(jsonStr);
		PropKit.use("weixin_config.txt");
		String appId = PropKit.get("appId");
		jsTicketCache.set(appId, ticket);
	}
	

}
