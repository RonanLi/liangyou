
package com.liangyou.model.wechatModel.message.inmsg;

/**
	接收视频消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>1357290913</CreateTime>
		<MsgType><![CDATA[shortvideo]]></MsgType>
			<MediaId><![CDATA[media_id]]></MediaId>
			<ThumbMediaId><![CDATA[thumb_media_id]]></ThumbMediaId>
			<MsgId>1234567890123456</MsgId>
	</xml>
*/
public class InShortVideoMsg extends InMsg {

	private String mediaId;
	private String thumbMediaId;
	private String msgId;

	public InShortVideoMsg() {
		super();
	}

	public InShortVideoMsg(String toUserName, String fromUserName, Long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}
	
	public String getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public String getThumbMediaId() {
		return thumbMediaId;
	}
	
	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return "InShortVideoMsg [mediaId=" + mediaId + ", thumbMediaId=" + thumbMediaId + ", msgId=" + msgId
				+ ", toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime
				+ ", msgType=" + msgType + "]";
	}
	
}



