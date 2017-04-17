package com.liangyou.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由于现在只有一台服务器
 * 默认将有时效的参数都放到了内存中
 *
 */
public class DefaultJsTicketCache implements IJsTicketCache{
	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) map.get(key);
	}

	@Override
	public void set(String key, Object value) {
		map.put(key, value);
	}
	
	@Override
	public void remove(String key) {
		map.remove(key);
	}
}
