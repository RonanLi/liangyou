package com.liangyou.model.wechatModel.message.event;

import com.liangyou.model.wechatModel.message.inmsg.InMsg;



public abstract class InEventMsg extends InMsg
{
    protected String event;
    
    public InEventMsg() {
		super();
	}
    
	public InEventMsg(String toUserName, String fromUserName, Long createTime, String msgType, String event)
    {
        super(toUserName, fromUserName, createTime, msgType);
        this.event = event;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

	@Override
	public String toString() {
		return "InEventMsg [event=" + event + ", toUserName=" + toUserName + ", fromUserName=" + fromUserName
				+ ", createTime=" + createTime + ", msgType=" + msgType + "]";
	}
    
}
