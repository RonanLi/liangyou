
package com.liangyou.model.wechatModel.message.outmsg;

import com.liangyou.model.wechatModel.message.inmsg.InMsg;

/**
	回复图片消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>12345678</CreateTime>
		<MsgType><![CDATA[image]]></MsgType>
			<Image>
				<MediaId><![CDATA[media_id]]></MediaId>
			</Image>
	</xml>
 */
public class OutImageMsg extends OutMsg {
	
	private String mediaId;
	
	public OutImageMsg() {
		this.msgType = "image";
	}
	
	public OutImageMsg(InMsg inMsg) {
		super(inMsg);
		this.msgType = "image";
	}
	
	@Override
	protected void subXml(StringBuilder sb) {
		if (null == mediaId) {
			throw new NullPointerException("mediaId is null");
		}
		sb.append("<Image>\n");
		sb.append("<MediaId><![CDATA[").append(mediaId).append("]]></MediaId>\n");
		sb.append("</Image>\n");
	}
	
	public String getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

}



