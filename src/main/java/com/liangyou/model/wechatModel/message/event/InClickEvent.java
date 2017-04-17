package com.liangyou.model.wechatModel.message.event;

/**
 * 自定义菜单事件
 <xml>
<ToUserName><![CDATA[toUser]]></ToUserName>
<FromUserName><![CDATA[FromUser]]></FromUserName>
<CreateTime>123456789</CreateTime>
<MsgType><![CDATA[event]]></MsgType>
<Event><![CDATA[CLICK]]></Event>
<EventKey><![CDATA[EVENTKEY]]></EventKey>
</xml>
*/

public class InClickEvent extends InEventMsg{
	private String eventKey;
	public InClickEvent() {
		super();
	}

	public InClickEvent(String toUserName, String fromUserName,
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
		return "InClickEvent [eventKey=" + eventKey + ", event=" + event + ", toUserName=" + toUserName
				+ ", fromUserName=" + fromUserName + ", createTime=" + createTime + ", msgType=" + msgType + "]";
	}
}
