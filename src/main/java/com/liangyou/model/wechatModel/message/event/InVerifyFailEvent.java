/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.liangyou.model.wechatModel.message.event;

/**
 <xml><ToUserName><![CDATA[toUser]]></ToUserName>
 <FromUserName><![CDATA[FromUser]]></FromUserName>
 <CreateTime>1442401061</CreateTime>
 <MsgType><![CDATA[event]]></MsgType>
 <Event><![CDATA[naming_verify_fail]]></Event>
 <FailTime>1442401061</FailTime>
 <FailReason><![CDATA[by time]]></FailReason>
 </xml>
 */
public class InVerifyFailEvent extends InEventMsg
{
    //资质认证失败
    public static final String EVENT_IN_QUALIFICATION_VERIFY_FAIL = "qualification_verify_fail";
    //名称认证失败
    public static final String EVENT_IN_NAMING_VERIFY_FAIL = "naming_verify_fail";

    private String failTime;
    private String failReason;

    public InVerifyFailEvent(String toUserName, String fromUserName, Long createTime, String msgType, String event)
    {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getFailTime()
    {
        return failTime;
    }

    public void setFailTime(String failTime)
    {
        this.failTime = failTime;
    }

    public String getFailReason()
    {
        return failReason;
    }

    public void setFailReason(String failReason)
    {
        this.failReason = failReason;
    }

	@Override
	public String toString() {
		return "InVerifyFailEvent [failTime=" + failTime + ", failReason=" + failReason + ", event=" + event
				+ ", toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime
				+ ", msgType=" + msgType + "]";
	}
    
}






