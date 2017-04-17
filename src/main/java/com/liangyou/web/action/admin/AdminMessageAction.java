package com.liangyou.web.action.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
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

/**
 * 后台站内信管理
 * @author Administrator
 *
 */

@Namespace("/admin/msg")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminMessageAction extends BaseAction implements ModelDriven<Message> {

	private static Logger logger = Logger.getLogger(AdminMessageAction.class);
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	private Message message = new Message();
	@Override
	public Message getModel() {
		return message;
	}	
//    private Message message = new Message(); 
//	public Message getMessage() {
//		return message;
//	}
//	public void setMessage(Message message) {
//		this.message = message;
//	}

	@Action(value="box",results={
			@Result(name="success", type="ftl",location="/admin/msg/box.html")			
	})	
	public String box()throws Exception{ //收件箱
		User sessionUser=this.getAuthUser();
		Map<String, String> map = new HashMap<String, String>();
		// 用户名
		String sentUsername = paramString("sentUsername");
		String receiveUsername = paramString("receiveUsername");
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		if (!StringUtils.isBlank(sentUsername)) {
			param.addParam("sentUser.username", Operator.LIKE, sentUsername);
			map.put("sentUsername", sentUsername);
		}
		if (!StringUtils.isBlank(receiveUsername)) {
			param.addParam("receiveUser.username", Operator.LIKE, receiveUsername);
			map.put("receiveUsername", receiveUsername);
		}
			param.addParam("receiveUser.userType.typeId",Operator.NOTEQ, 2)
			.addParam("sented",Operator.NOTEQ, 0)
			.addOrder(OrderType.DESC, "addtime");
			
		PageDataList plist = messageService.getMessageBySearchParam(param);
		request.setAttribute("msgList", plist.getList());
		request.setAttribute("page", plist.getPage());
		request.setAttribute("param", map);
		return SUCCESS;
	}
	
	@Action(value="sent",results={
			@Result(name="success", type="ftl",location="/admin/msg/sent.html")			
	})	
	public String sent()throws Exception{//发件箱
		User sessionUser=this.getAuthUser();
		long userid=sessionUser.getUserId();
		Map<String, String> map = new HashMap<String, String>();
		
		int page = paramInt("page");
		int perNum = (paramInt("perNum") == 0) ? Page.ROWS : paramInt("perNum");
		String sentUsername = paramString("sentUsername");
		String receiveUsername = paramString("receiveUsername");
		
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);
		if (!StringUtils.isBlank(sentUsername)) {
			param.addParam("sentUser.username", Operator.LIKE, sentUsername);
			map.put("sentUsername", sentUsername);
		}
		if (!StringUtils.isBlank(receiveUsername)) {
			param.addParam("receiveUser.username", Operator.LIKE, receiveUsername);
			map.put("receiveUsername", receiveUsername);
		}
		param.addParam("sentUser.userType.typeId",Operator.NOTEQ, 2);
		param.addParam("sented", 1);
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = messageService.getMessageBySearchParam(param);
		request.setAttribute("page", plist.getPage());
		request.setAttribute("list", plist.getList());
		request.setAttribute("param", map);
		request.setAttribute("pageUrl", request.getRequestURL().toString());
		return SUCCESS;
	}
	@Action(value="set",results={
			@Result(name="success", type="ftl",location="/admin/msg/sent.html")			
	})	
	public String set()throws Exception{//设置信件 标记为已读 未读 删除
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
		message(tip, "/admin/msg/box.html");
		return ADMINMSG;
	}
	@Action(value="view",results={
			@Result(name="success", type="ftl",location="/admin/msg/view.html")			
	})	
	public String view()throws Exception{//回复信件
		int id=NumberUtils.getInt(request.getParameter("id"));
		String type=StringUtils.isNull(request.getParameter("type"));
		if(id<1){
			message("您操作有误，请勿乱操作！", "/admin/msg/box.html");
			return ADMINMSG;
		}

		Message msg=messageService.getMessageById(id);
		if(msg==null) {
			message("您操作有误，请勿乱操作！", "/admin/msg/box.html");
			return ADMINMSG;
		}
		User friend=userService.getUserById(msg.getSentUser().getUserId());
		msg.setStatus(1);
		messageService.modifyMessge(msg);
		request.setAttribute("msg", msg);
		request.setAttribute("friend", friend);
		request.setAttribute("type", type);
		return SUCCESS;
	}
	@Action(value="send",results={
			@Result(name="success", type="ftl",location="/admin/msg/send.html")			
	})	
	public String send()throws Exception{//  处理发送消息
		String type = StringUtils.isNull(request.getParameter("type"));
		String sendType = StringUtils.isNull(request.getParameter("sendType"));
		int id = NumberUtils.getInt(request.getParameter("id"));
		Message msg = messageService.getMessageById(id);
		User sentUser=userService.getUserById(getAuthUser().getUserId());
		if (type.equals("add")) {
			String errormsg=checkMessage();
			if(!errormsg.equals("")){
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
			User receive_user=message.getReceiveUser();
			if(receive_user==null){
				errormsg="收件人不存在";
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
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
			
			message("发送消息成功！", "/admin/msg/sent.html");
			return ADMINMSG;
		} else if (type.equals("reply")) {
			Message message = new Message();
			message.setSentUser(sentUser);
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

			messageService.addMessage(message);

			message("回复消息成功！", "/admin/msg/sent.html");
			return ADMINMSG;
		} else {
			request.setAttribute("msg_type", "send");
		}
		request.setAttribute("sendType", sendType);
		request.setAttribute("msg", msg);
		return SUCCESS;
	}
	private String checkMessage(){
		String errormsg="";
		String validcode=StringUtils.isNull(request.getParameter("valicode"));
		User receiveUser = userService.getUserByName(message.getReceiveUser().getUsername()); 
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
