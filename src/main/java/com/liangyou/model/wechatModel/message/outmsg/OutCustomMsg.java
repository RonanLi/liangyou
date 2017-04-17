package com.liangyou.model.wechatModel.message.outmsg;

import org.apache.commons.lang3.StringUtils;

import com.liangyou.model.wechatModel.message.inmsg.InMsg;

/**
	转发多客服消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>12345678</CreateTime>
		<MsgType><![CDATA[transfer_customer_service]]></MsgType>
	</xml>
	或者转发到指定客服
	<xml>
		<ToUserName><![CDATA[touser]]></ToUserName>
		<FromUserName><![CDATA[fromuser]]></FromUserName>
		<CreateTime>1399197672</CreateTime>
		<MsgType><![CDATA[transfer_customer_service]]></MsgType>
		<TransInfo>
			<KfAccount><![CDATA[test1@test]]></KfAccount>
		</TransInfo>
	</xml>
*/
public class OutCustomMsg extends OutMsg {

	private TransInfo transInfo;

	public OutCustomMsg() {
		this.msgType = "transfer_customer_service";
	}

	public OutCustomMsg(InMsg inMsg) {
		super(inMsg);
		this.msgType = "transfer_customer_service";
	}
	
	@Override
	protected void subXml(StringBuilder sb) {
		if (null != transInfo && StringUtils.isNotBlank(transInfo.getKfAccount())) {
			sb.append("<TransInfo>\n");
			sb.append("<KfAccount><![CDATA[").append(transInfo.getKfAccount()).append("]]></KfAccount>\n");
			sb.append("</TransInfo>\n");
		}
	}
	
	public TransInfo getTransInfo() {
		return transInfo;
	}
	
	public void setTransInfo(TransInfo transInfo) {
		this.transInfo = transInfo;
	}
	
}


