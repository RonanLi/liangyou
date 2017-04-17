package com.liangyou.model.wechatModel.message.inmsg;


/**
	接收链接消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>1351776360</CreateTime>
		<MsgType><![CDATA[link]]></MsgType>
			<Title><![CDATA[公众平台官网链接]]></Title>
			<Description><![CDATA[公众平台官网链接]]></Description>
			<Url><![CDATA[url]]></Url>
			<MsgId>1234567890123456</MsgId>
	</xml>
*/
public class InLinkMsg extends InMsg {
	
	private String title;
	private String description;
	private String url;
	private String msgId;
	
	public InLinkMsg() {
		super();
	}

	public InLinkMsg(String toUserName, String fromUserName, Long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return "InLinkMsg [title=" + title + ", description=" + description + ", url=" + url + ", msgId=" + msgId
				+ ", toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime
				+ ", msgType=" + msgType + "]";
	}
	
}



