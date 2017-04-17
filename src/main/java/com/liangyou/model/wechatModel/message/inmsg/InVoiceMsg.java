
package com.liangyou.model.wechatModel.message.inmsg;

/**
	接收语音消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>1357290913</CreateTime>
		<MsgType><![CDATA[voice]]></MsgType>
			<MediaId><![CDATA[media_id]]></MediaId>
			<Format><![CDATA[Format]]></Format>
			<MsgId>1234567890123456</MsgId>
	</xml>
*/
public class InVoiceMsg extends InMsg {
	
	private String mediaId;
	private String format;
	private String msgId;
	
	public InVoiceMsg() {
		super();
	}

	public InVoiceMsg(String toUserName, String fromUserName, Long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}
	
	public String getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return "InVoiceMsg [mediaId=" + mediaId + ", format=" + format + ", msgId=" + msgId + ", toUserName="
				+ toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime + ", msgType=" + msgType
				+ "]";
	}
	
}





