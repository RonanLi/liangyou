package com.liangyou.tool.wechatkit;

import java.util.HashMap;
import java.util.Map;

import com.liangyou.context.Constant;
import com.liangyou.model.wechatModel.Prop;

/**
 * @author lijing
 *
 *读取微信配置
 */
public class PropKit {
	private static Prop prop = null;
	private static final Map<String, Prop> map = new HashMap<String, Prop>();
	private PropKit(){}
	public static Prop getProp() {
		if(prop == null){
			throw new IllegalArgumentException("Load propties file by invoking PropKit.use(String fileName) method first.");
		}
		return prop;
	}
	public static Prop getProp(String fileName) {
		return map.get(fileName);
	}
	
	public static String get(String key) {
		return getProp().get(key);
	}
	
	public static String get(String key, String defaultValue) {
		return getProp().get(key, defaultValue);
	}
	
	public static Integer getInt(String key) {
		return getProp().getInt(key);
	}
	
	public static Integer getInt(String key, Integer defaultValue) {
		return getProp().getInt(key, defaultValue);
	}
	
	public static Long getLong(String key) {
		return getProp().getLong(key);
	}
	
	public static Long getLong(String key, Long defaultValue) {
		return getProp().getLong(key, defaultValue);
	}
	
	public static Boolean getBoolean(String key) {
		return getProp().getBoolean(key);
	}
	
	public static Boolean getBoolean(String key, Boolean defaultValue) {
		return getProp().getBoolean(key, defaultValue);
	}
	
	public static boolean containsKey(String key) {
		return getProp().containsKey(key);
	}
	public static Prop use(String fileName) {
		return use(fileName,Constant.DEFAULT_ENCODING);
		
	}
	public static Prop use(String fileName,String encoding){
		Prop prop = map.get(fileName);
		if(prop == null){
			synchronized (map) {
				prop = map.get(fileName);
				if(prop == null){
					prop = new Prop(fileName,encoding);
					map.put(fileName,prop);
					if(PropKit.prop == null) {
						PropKit.prop = prop;
					}
				}
			}
		}
		return prop;
	}

	
}
