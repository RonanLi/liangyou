package com.liangyou.model.wechatModel.message.event;
/**
 * 点击菜单跳转链接时的事件推送
<xml>
<ToUserName><![CDATA[toUser]]></ToUserName>
<FromUserName><![CDATA[FromUser]]></FromUserName>
<CreateTime>123456789</CreateTime>
<MsgType><![CDATA[event]]></MsgType>
<Event><![CDATA[VIEW]]></Event>
<EventKey><![CDATA[www.qq.com]]></EventKey>
</xml>
 */
public class InViewEvent extends InEventMsg{
	private String eventKey;

	public InViewEvent() {
		super();
	}
	
	public InViewEvent(String toUserName, String fromUserName,
			Long createTime, String msgType, String event,String eventKey) {
		super(toUserName, fromUserName, createTime, msgType, event);
		this.eventKey = eventKey;
	}
	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	@Override
	public String toString() {
		return "InViewEvent [eventKey=" + eventKey + ", event=" + event + ", toUserName=" + toUserName
				+ ", fromUserName=" + fromUserName + ", createTime=" + createTime + ", msgType=" + msgType + "]";
	}
	
}
