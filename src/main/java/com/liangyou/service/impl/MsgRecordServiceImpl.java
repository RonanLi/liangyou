package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.MsgRecordDao;
import com.liangyou.domain.Message;
import com.liangyou.domain.MsgOperateType;
import com.liangyou.domain.MsgRecord;
import com.liangyou.domain.User;
import com.liangyou.model.MsgReq;
import com.liangyou.sendPhone.PhoneUtil;
import com.liangyou.service.MessageService;
import com.liangyou.service.MsgRecordService;
import com.liangyou.service.MsgService;
import com.liangyou.tool.javamail.Mail;

/*****************
* @ClassName: MsgRecordServiceImpl
* @Description: 消息发送serviceImpl
* @author xx_erongdu
* @date 2013-7-11 下午4:11:32
*
*****************/ 
@Service(value="msgRecordService")
@Transactional(propagation = Propagation.REQUIRED)
public class MsgRecordServiceImpl extends BaseServiceImpl implements MsgRecordService {
	private static Logger logger = Logger.getLogger(MsgRecordServiceImpl.class);
	@Autowired
	private MsgRecordDao msgRecordDao;
	@Autowired
	private MessageService messageService;
	@Autowired
	private MsgService msgService;
	
	//发送消息
	@Override
	public String send(MsgReq req) {
		String sendResult = "";
		try {
			if(req==null || req.getReceiver()==null || req.getMsgOperate()==null || req.getMsgOperate().getIsuse()==0){
				sendResult = "消息发送失败，发信请求参数为空或不全或operate未启用";
				return sendResult;
			}
			//根据业务获取 信息发送类型及模板
			List<MsgOperateType> motList = this.msgService.listMsgOperateTypeByOperateId(req.getMsgOperate().getId());
			if(motList==null || motList.size()==0){
				sendResult = "消息发送失败，没有与该业务对应的信息类型及模板";
				return sendResult;
			}
			
			logger.info("--------------------发送信息结果sendResult"+sendResult);
			MsgOperateType mot = null;
			for (int i=0;i<motList.size();i++) {
				mot = motList.get(i);
				if(mot==null || mot.getMsgType()==null || mot.getMsgTemplate()==null || mot.getMsgType().getIsuse()==0 || mot.getMsgTemplate().getIsuse()==0){
					 continue;
				}
				User sender = req.getSender();
				User receiver = req.getReceiver();
				
				Mail email = Mail.getInstance();
				email.setBody(mot.getMsgTemplate().getContent());
				this.emailReplace(req, email, sender, receiver);
				//发送信息
				int a = mot.getMsgType().getId();
				switch(mot.getMsgType().getId()){
					//站内信
					case 1:
						if(req.getMessage()==null){
							Message message = new Message();
							message.setSentUser(sender);
							message.setReceiveUser(receiver);
							message.setStatus(0);
							message.setSented(1);
							message.setType(Constant.SYSTEM);
							message.setAddip(req.getIp());
							message.setAddtime(new Date());
							message.setName(req.getMsgOperate().getName());
							message.setContent(email.getBody());
							req.setMessage(message);
						}
						this.messageService.addMessage(req.getMessage());
						sendResult = "站内信发送成功";
						break;
					//邮件
					case 2:
						email.setTo(receiver.getEmail());
						email.sendMail();
						sendResult = "邮件发送成功";
						sender = null;
						break;
					//短信
					case 3:
						if(StringUtils.isNotBlank(receiver.getPhone()) && receiver.getPhoneStatus()==1){
							int phoneType = Global.getInt("phone_type");
							sendResult = PhoneUtil.sentPhone(phoneType, receiver.getPhone(), email.getBody());
						}
						if("短信发送成功".equals(sendResult)){
							sendResult = "短信发送成功";
							//v1.8.0.4_u2 TGPROJECT-309 lx 2014-05-22 start
							logger.info(" 短息发送成功，短信内容： "+email.getBody());
							//v1.8.0.4_u2 TGPROJECT-309 lx 2014-05-22 end
						}
						break;
					default:
						sendResult = "";
						break;
				}
				logger.info("--------------------发送信息结果sendResult"+sendResult);
				if(sendResult.contains("发送成功")){
					MsgRecord item = new MsgRecord();
					item.setSender(sender);
					item.setReceiver(receiver);
					item.setMsgOperate(mot.getMsgOperate());
					item.setMsgType(mot.getMsgType());
					item.setMsgTemplate(mot.getMsgTemplate());
					item.setSendTime(new Date());
					item.setIp(req.getIp());
					item.setSendResult(sendResult);
					this.msgRecordDao.save(item);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			sendResult = "消息发送失败，系统异常";
			logger.info("--------------------发送信息结果sendResult"+sendResult);
			return sendResult;
		}
		return sendResult;
	}
	
	/**
	 * 替换消息内容，将字符串替换为实际内容
	 * @param email
	 * @param sender
	 * @param receiver
	 * @return
	 */
	private void emailReplace(MsgReq req, Mail email, User sender, User receiver){
		Map<String, String> map = req.getMap();		
		if(!com.liangyou.util.StringUtils.isNull(receiver.getRealname()).equals("")){
			map.put("realname", receiver.getRealname());
			map.put("username", receiver.getUsername());
		}
		email.replace(map);
	}
	
	
}
