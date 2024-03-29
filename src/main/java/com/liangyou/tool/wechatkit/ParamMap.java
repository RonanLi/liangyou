package com.liangyou.tool.wechatkit;

import java.util.HashMap;
import java.util.Map;

public class ParamMap {
	
	private Map<String, String> data = new HashMap<String, String>();
	private ParamMap() {}
	
	public static ParamMap create() {
		return new ParamMap();
	}
	
	public static ParamMap create(String key, String value) {
		return create().put(key, value);
	}
	
	public ParamMap put(String key, String value) {
		data.put(key, value);
		return this;
	}
	
	public Map<String, String> getData() {
		return data;
	}
}
