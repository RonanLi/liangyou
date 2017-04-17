package com.liangyou.web.action.member;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.domain.Message;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.MessageService;
import com.liangyou.service.UserService;
import com.liangyou.tool.Page;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/member/message")
@ParentPackage("p2p-default")
public class MessageAction extends BaseAction implements ModelDriven<Message>{

	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	private Message message = new Message();
	@Override
	public Message getModel() {
		return message;
	}	
	
	@Action(value="box",results={
			@Result(name="success", type="ftl",location="/member/message/box.html")
			})
	public String box() throws Exception{
		User sessionUser=this.getSessionUser();
		long userid=sessionUser.getUserId();
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"))
				.addParam("receiveUser", new User(userid))
				.addParam("sented",Operator.NOTEQ, 0)
				.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = messageService.getMessageBySearchParam(param);
		request.setAttribute("msgList", plist.getList());
		request.setAttribute("page", plist.getPage());
		request.setAttribute("param", param.toMap());
		return SUCCESS;
	}
	
	@Action(value="inviteList",results={
			@Result(name="success", type="ftl",location="/member/message/invite.html")
	})
	public String invite() throws Exception{
		User sessionUser=this.getSessionUser();
		long userid=sessionUser.getUserId();
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"))
				.addParam("inviteUser", new User(userid))
				.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = userService.getInvitreUserBySearchParam(param);
		request.setAttribute("inviteUsers", plist.getList());
		request.setAttribute("page", plist.getPage());
		request.setAttribute("param", param.toMap());
		
		return SUCCESS;
	}
	
	@Action(value="set",results={
			@Result(name="success", type="ftl",location="/member/message/view.html")
			})
	public String set() throws Exception{
		String type = StringUtils.isNull(request.getParameter("type"));
		String ids =  paramString("ids");
		if(ids.length() > 1) {
			ids = ids.substring(0, ids.length()-1);
		}
		String allId[] = ids.split(",");
		Integer all[] = new Integer[allId.length];
		for (int i=0; i<allId.length; i++) {
			all[i] = new Integer(allId[i].trim());
		}
		if(all.length<1){
			message("您操作有误，请勿乱操作！", "/member/message/box.html");
			return MSG;
		}
		String tip="";
		if(type.equals(Constant.DEL_MSG)){
			tip="删除信息成功！";
			messageService.deleteMessage(all);
		}else if(type.equals(Constant.SET_READ_MSG)){
			tip="已标记已读 ！";
			messageService.setReadMessage(all);
		}else if(type.equals(Constant.SET_UNREAD__MSG)){
			tip="已标记未读 ！";
			messageService.setUnreadMessage(all);
		}
		message(tip, "/member/message/box.html");
		return MSG;
	}
	
	/**
	 * 获取已发送的站内信
	 * @return
	 * @throws Exception
	 */
	@Action(value="sent",results={
			@Result(name="success", type="ftl",location="/member/message/sent.html")
			})
	public String sent() throws Exception{
		User sessionUser=this.getSessionUser();
		long userid=sessionUser.getUserId();
		
		int page = paramInt("page");
		int perNum = (paramInt("perNum") == 0) ? Page.ROWS : paramInt("perNum");
		
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);
		param.addParam("sentUser", sessionUser);
		param.addParam("sented", 1);
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = messageService.getMessageBySearchParam(param);
		setPageAttribute(plist, param);
		return SUCCESS;
	}
	
	@Action(value="view",results={
			@Result(name="success", type="ftl",location="/member/message/view.html")
			})
	public String view() throws Exception{
		int id=NumberUtils.getInt(request.getParameter("id"));//点击的消息对应的ID
		String type=StringUtils.isNull(request.getParameter("type"));
		if(id<1){
			message("您操作有误，请勿乱操作！", "/member/message/box.html");
			return MSG;
		}
		
		Message msg=messageService.getMessageById(id);//根据信息ID查到此信息
		if(msg==null) {
			message("您操作有误，请勿乱操作！", "/member/message/box.html");
			return MSG;
		}
		//v1.8.0.4_u2 TGPROJECT-306 lx 2014-05-27 start
		User sessionUser=getSessionUser();//当前用户
		if(sessionUser==null){
			message("请登陆后操作！");
			return MSG;
		}
		if(sessionUser.getUserId()!=msg.getReceiveUser().getUserId() && msg.getReceiveUser().getUserId()==msg.getSentUser().getUserId()){
			message("您操作有误，请勿乱操作！", "/member/message/box.html");
			return MSG;
		}

		//v1.8.0.4_u2 TGPROJECT-306 lx 2014-05-27 end
		User friend=userService.getUserById(msg.getSentUser().getUserId());//根据此消息发件人的ID查到发件人
		msg.setStatus(1);
		messageService.modifyMessge(msg);//更新信息
		request.setAttribute("msg", msg);
		request.setAttribute("friend", friend);
		request.setAttribute("type", type);
		return SUCCESS;
	}
	
	@Action(value="send",results={
			@Result(name="success", type="ftl",location="/member/message/send.html")
			})
	/**
	 * 处理发消息Action
	 * 
	 * @return
	 * @throws Exception
	 */
	public String send() throws Exception {
		String type = StringUtils.isNull(request.getParameter("type"));//提交时是reply，转发时是add
		String sendType = StringUtils.isNull(request.getParameter("sendType"));//提交时为空，转发时是forward
		int id = NumberUtils.getInt(request.getParameter("id"));//此消息ID
		Message msg = messageService.getMessageById(id);//根据消息ID查到此消息
		User sentUser=userService.getUserById(getSessionUser().getUserId());//当前登录用户，即发件人
		if (type.equals("add")) {
			String errormsg=checkMessage();
			if(!errormsg.equals("")){
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
			User receive_user=message.getReceiveUser();//接收人
			if(receive_user==null){
				errormsg="收件人不存在";
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
			if(sentUser.getUserId()==receive_user.getUserId()){
				message("收件人不能为自己！", "/member/message/send.html");
				return MSG;
			}
			Message nowMsg = new Message();
			nowMsg.setContent(message.getContent());
			nowMsg.setName(message.getName());
			nowMsg.setSentUser(sentUser);
			nowMsg.setReceiveUser(receive_user);
			nowMsg.setStatus(0);
			nowMsg.setSented(1);
			nowMsg.setType(Constant.SYSTEM);
			nowMsg.setType(Constant.SYSTEM);
			nowMsg.setAddip(getRequestIp());
			nowMsg.setAddtime(new Date());
			
			messageService.addMessage(nowMsg);
			
			message("发送消息成功！", "/member/message/sent.html");
			return MSG;
		} else if (type.equals("reply")) {
			Message message = new Message();//封装Message
			message.setSentUser(msg.getReceiveUser());
			message.setReceiveUser(msg.getSentUser());
			message.setStatus(0);
			message.setSented(1);
			message.setType(Constant.SYSTEM);
			message.setAddip(getRequestIp());
			message.setAddtime(new Date());
			message.setName("Re:" + msg.getName());
			message.setContent(msg.getContent()
					+ "</br>------------------ 原始信息 ------------------</br>"
					+ StringUtils.isNull(paramString("repContent")));

			messageService.addMessage(message);//增加此信息到数据库

			message("回复消息成功！", "/member/message/sent.html");
			return MSG;
		} else {
			request.setAttribute("msg_type", "send");
		}
		request.setAttribute("sendType", sendType);
		request.setAttribute("msg", msg);
		return SUCCESS;
	}
	
	private String checkMessage(){
		String errormsg="";
		String validcode=StringUtils.isNull(request.getParameter("valicode"));//验证码
		User receiveUser = userService.getUserByName(message.getReceiveUser().getUsername()); //收件人
		message.setReceiveUser(receiveUser);
		if(receiveUser==null){
			errormsg="对不起收件人不存在！";
			return errormsg;
		}else if(StringUtils.isNull(message.getName()).equals("")){
			errormsg="标题不能为空！";
			return errormsg;
		}else if(StringUtils.isNull(message.getContent()).equals("")){
			errormsg="内容不能为空！";
			return errormsg;
		}else if(!checkValidImg(validcode)){
			errormsg="验证码错误！";
			return errormsg;
		}
		return errormsg;
	}
	
	
}