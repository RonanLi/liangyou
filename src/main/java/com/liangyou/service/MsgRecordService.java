package com.liangyou.service;

import com.liangyou.model.MsgReq;

/*****************
* @ClassName: MsgRecordService
* @Description: 消息发送service
* @author xx_erongdu
* @date 2013-7-11 下午2:25:23
*
*****************/ 
public interface MsgRecordService {
	
	/**发送站内信
	 * @param req
	 * @return
	 */
	public String send(MsgReq req);
	
}
