package com.liangyou.model.wechatModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.liangyou.context.Constant;

/**
 *获取微信配置信息
 */
public class Prop {
	Logger logger = Logger.getLogger(Prop.class);
	private Properties properties = null;
	
	public Prop(File file) {
		this(file, Constant.DEFAULT_ENCODING);
	}
	public Prop(File file,String encoding) {
		if(file == null) {
			throw new IllegalArgumentException("文件路径不能为空!");
		}
		if(file.isFile() == false) {
			throw new IllegalArgumentException("文件路径不存在!");
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			properties = new Properties();
			properties.load(new InputStreamReader(inputStream, encoding));
		} catch (Exception e) {
			throw new RuntimeException("读取配置文件出错!");
		}finally{
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
		}
	}
	public Prop(String fileName, String encoding) {
		InputStream inputStream = null;
		try {
			inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if(inputStream == null) {
				throw new IllegalArgumentException("该微信配置文件不存在!"+ fileName);
			}
			properties = new Properties();
			properties.load(new InputStreamReader(inputStream, encoding));
		} catch (Exception e) {
			throw new RuntimeException("读取配置文件出错!");
		}finally{
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	public Integer getInt(String key) {
		return getInt(key, null);
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		String value = properties.getProperty(key);
		if (value != null)
			return Integer.parseInt(value.trim());
		return defaultValue;
	}
	
	public Long getLong(String key) {
		return getLong(key, null);
	}
	
	public Long getLong(String key, Long defaultValue) {
		String value = properties.getProperty(key);
		if (value != null)
			return Long.parseLong(value.trim());
		return defaultValue;
	}
	
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			value = value.toLowerCase().trim();
			if ("true".equals(value))
				return true;
			else if ("false".equals(value))
				return false;
			throw new RuntimeException("The value can not parse to Boolean : " + value);
		}
		return defaultValue;
	}
	
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}
	
	public Properties getProperties() {
		return properties;
	}
}
