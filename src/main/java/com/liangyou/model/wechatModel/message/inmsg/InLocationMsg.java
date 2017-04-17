
package com.liangyou.model.wechatModel.message.inmsg;

/**
	接收地理位置消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>1351776360</CreateTime>
		<MsgType><![CDATA[location]]></MsgType>
			<Location_X>23.134521</Location_X>
			<Location_Y>113.358803</Location_Y>
			<Scale>20</Scale>
			<Label><![CDATA[位置信息]]></Label>
			<MsgId>1234567890123456</MsgId>
	</xml>
*/
public class InLocationMsg extends InMsg {
	private String location_X;
	private String location_Y;
	private String scale;
	private String label;
	private String msgId;
	
	public InLocationMsg() {
		super();
	}

	public InLocationMsg(String toUserName, String fromUserName, Long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}
	
	public String getLocation_X() {
		return location_X;
	}
	
	public void setLocation_X(String location_X) {
		this.location_X = location_X;
	}
	
	public String getLocation_Y() {
		return location_Y;
	}
	
	public void setLocation_Y(String location_Y) {
		this.location_Y = location_Y;
	}
	
	public String getScale() {
		return scale;
	}
	
	public void setScale(String scale) {
		this.scale = scale;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getMsgId() {
		return msgId;
	}
	
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return "InLocationMsg [location_X=" + location_X + ", location_Y=" + location_Y + ", scale=" + scale
				+ ", label=" + label + ", msgId=" + msgId + ", toUserName=" + toUserName + ", fromUserName="
				+ fromUserName + ", createTime=" + createTime + ", msgType=" + msgType + "]";
	}
	
}




