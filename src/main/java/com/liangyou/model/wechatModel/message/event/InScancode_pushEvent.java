package com.liangyou.model.wechatModel.message.event;

/**
 * scancode_push：扫码推事件的事件推送
 * 	<xml><ToUserName><![CDATA[gh_e136c6e50636]]></ToUserName>
	<FromUserName><![CDATA[oMgHVjngRipVsoxg6TuX3vz6glDg]]></FromUserName>
	<CreateTime>1408090502</CreateTime>
	<MsgType><![CDATA[event]]></MsgType>
	<Event><![CDATA[scancode_push]]></Event>
	<EventKey><![CDATA[6]]></EventKey>
	<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType>
	<ScanResult><![CDATA[1]]></ScanResult>
	</ScanCodeInfo>
	</xml>
 *
 */
public class InScancode_pushEvent extends InEventMsg{
	private String eventKey;
	private String scanCodeInfo;
	private String scanResult;
	public InScancode_pushEvent() {
		super();
	}
	public InScancode_pushEvent(String toUserName, String fromUserName,
			Long createTime, String msgType, String event,String eventKey,String scanCodeInfo,String scanResult) {
		super(toUserName, fromUserName, createTime, msgType, event);
		this.eventKey = eventKey;
		this.scanCodeInfo = scanCodeInfo;
		this.scanResult = scanResult;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public String getScanCodeInfo() {
		return scanCodeInfo;
	}
	public void setScanCodeInfo(String scanCodeInfo) {
		this.scanCodeInfo = scanCodeInfo;
	}
	public String getScanResult() {
		return scanResult;
	}
	public void setScanResult(String scanResult) {
		this.scanResult = scanResult;
	}
	@Override
	public String toString() {
		return "InScancode_pushEvent [eventKey=" + eventKey + ", scanCodeInfo=" + scanCodeInfo + ", scanResult="
				+ scanResult + ", event=" + event + ", toUserName=" + toUserName + ", fromUserName=" + fromUserName
				+ ", createTime=" + createTime + ", msgType=" + msgType + "]";
	}
	
	
}
