package com.liangyou.model.wechatModel.message.inmsg;

/**
	接收图片消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>1348831860</CreateTime>
		<MsgType><![CDATA[image]]></MsgType>
			<PicUrl><![CDATA[this is a url]]></PicUrl>
			<MediaId><![CDATA[media_id]]></MediaId>
			<MsgId>1234567890123456</MsgId>
	</xml>
*/
public class InImageMsg extends InMsg {
	
	private String picUrl;//图片链接（由系统生成）
	private String mediaId;//图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
	private String msgId; //消息id，64位整型
	
	public InImageMsg() {
		super();
	}

	public InImageMsg(String toUserName, String fromUserName, Long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}
	
	public String getPicUrl() {
		return picUrl;
	}
	
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public String getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return "InImageMsg [picUrl=" + picUrl + ", mediaId=" + mediaId + ", msgId=" + msgId + ", toUserName="
				+ toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime + ", msgType=" + msgType
				+ "]";
	}
	
}





