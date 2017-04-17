package com.liangyou.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 配置文件读取工具类.
 * 
 * Copyright (c) 2010
 * FreeLand
 * @author raymond.zhu
 * @version 1.0
 */
public final class ConfigHelper {

	private static Document doc = null;
	private static Map<String,String> propertyCache = new HashMap<String,String>();
	public static final String CONFIG_FILENAME = "/config/config.xml";
	private static Logger logger = Logger.getLogger(ConfigHelper.class.getName());
	
	public ConfigHelper() {
	}
	
	static {

		InputStream is = null;
		try {
			is = ConfigHelper.class.getResourceAsStream(CONFIG_FILENAME);
			SAXBuilder sb = new SAXBuilder();
			doc = sb.build(new BufferedInputStream(is));
		}
		catch(Exception ex){
			logger.error("读取配置文件失败:",ex);
		}
		finally {
			if(is != null){	
				try{is.close();}
				catch(Exception ex){}
				is = null;
			}
		}
	}
	
	public static Document getXmlDocument(){
		return doc;
	}

	/**
	 * 重新读取参数.
	 */
	public synchronized static void reset(){
		propertyCache.clear();
	}
	
	/**
	 * 获取配置文件中的属性
	 * @param name 属性分隔各名称 如rss.url;
	 * @return
	 */
	public static String getProperty(String name) {
		String cacheProp = popProperty(name);
		if (cacheProp != null) {
            return cacheProp;
        }

        String[] propName = name.split("\\.");
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            element = element.getChild(propName[i]);
            if (element == null) {
                // 找不到对应的属�?,则返回null
                return null;
            }
        }
        // 找到对应的属�?返回其�?,若�?为空字符�? 则返回null
        String value = element.getText();
        if ("".equals(value)) {
            return null;
        }
        else {
            // 将属性缓存
            value = value.trim();
            pushProperty(name,value);
            return value;
        }
    }
	
	public static int getIntProperty(String name,int defaultValue) {
		int val = defaultValue;
		try {
			String s = getProperty(name);
			if(s != null && s.length() != 0) 
				val = Integer.parseInt(s);
		}
		catch(NumberFormatException nfe) {}
		
		return val;
	}
	
	public static double getDoubleProperty(String name, double defaultValue) {
		double val = defaultValue;
		try {
			String s = getProperty(name);
			if(s != null && s.length() != 0) 
				val = Double.parseDouble(s);
		}
		catch(NumberFormatException nfe) {}
		
		return val;
	}
	
	public static long getLongProperty(String name,long defaultValue){
        long val = defaultValue;
        try {
            String s = getProperty(name);
            if(s != null && s.length() != 0) 
                val = Long.parseLong(s);
        }
        catch (NumberFormatException x) {}

        return val;
    }
	
	public static boolean getBooleanProperty(String property,boolean defaultValue) {
        boolean b = defaultValue;
        String s = getProperty(property);
        try {
        	if(s != null && s.length() != 0){
	            b = (Integer.parseInt(s) > 0) ? true : false;
        	}
        }
        catch (NumberFormatException x) {
            // It's not a number..
        	s = s.toLowerCase();
            if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true"))
                b = true;
            else if (s.equalsIgnoreCase("no") || s.equalsIgnoreCase("off") || s.equalsIgnoreCase("false"))
                b = false;
        }

        return (b);
    }

	private synchronized static String popProperty(String name) {
		if (propertyCache.containsKey(name)) {
            return (String)propertyCache.get(name);
        }
		
		return null;
	}
	
	private synchronized static void pushProperty(String name,String value){
		propertyCache.put(name, value);
	}
	public static void main(String args[]){
		//ConfigHelper c=new ConfigHelper();
		System.out.println(ConfigHelper.getProperty("virtualuser.onlineAmStart"));
		System.out.println(ConfigHelper.getProperty("virtualuser.onlineAmEnd"));
		System.out.println(ConfigHelper.getProperty("virtualuser.onlinePmStart"));
		System.out.println(ConfigHelper.getProperty("virtualuser.onlinePmEnd"));
	}
}
