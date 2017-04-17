package com.liangyou.model.wechatModel;

import java.util.Map;

import com.alibaba.fastjson.JSON;

public class JsTicket {
	private Integer errcode;
	private String errmsg;
	private String ticket; //ticket 
	private Integer expires_in;  //7200秒
	private Long expiredTime;		// 正确获取到 access_token 时有值，存放过期时间
	private String json;
	public JsTicket (){}
	public JsTicket(String jsonStr){
		this.json = jsonStr;
		Map<String,Object> map = JSON.parseObject(json, Map.class);
		try {
			Map<String,Object> temp = JSON.parseObject(jsonStr, Map.class);
			ticket = (String) temp.get("ticket");
			expires_in = getInt(temp, "expires_in");//7200 
			errcode = getInt(temp, "errcode");
			errmsg = (String) temp.get("errmsg");
			if (expires_in != null)
				expiredTime = System.currentTimeMillis() + ((expires_in -5) * 1000);//过期时间

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	}
	
	
	private Integer getInt(Map<String, Object> temp, String key) {
		Number number = (Number) temp.get(key);
		return number == null ? null : number.intValue();
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Integer getErrcode() {
		return errcode;
	}
	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public Integer getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
	public Long getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	public boolean matching() {
		return isAvailable();
	}
	public boolean isAvailable() {

		if (expiredTime == null) //过期时间
			return false;
		if (errcode != null)//错误码
			return false;
		if (expiredTime < System.currentTimeMillis())//过期时间
			return false;
		return ticket != null;
	}

}
