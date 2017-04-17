package com.liangyou.service.impl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.dao.WeChatAutoReplyDao;
import com.liangyou.dao.WeChatEventPushDao;
import com.liangyou.dao.WeChatGraphicMsgDao;
import com.liangyou.dao.WeChatImageDao;
import com.liangyou.dao.WeChatKfAccountDao;
import com.liangyou.dao.WeChatMenuDao;
import com.liangyou.dao.WeChatMenuGroupDao;
import com.liangyou.dao.WeChatMsgDao;
import com.liangyou.dao.WeChatUserDao;
import com.liangyou.domain.WeChatAutoReply;
import com.liangyou.domain.WeChatEventPush;
import com.liangyou.domain.WeChatGraphicMsg;
import com.liangyou.domain.WeChatImage;
import com.liangyou.domain.WeChatKfAccount;
import com.liangyou.domain.WeChatMenu;
import com.liangyou.domain.WeChatMenuGroup;
import com.liangyou.domain.WeChatMsg;
import com.liangyou.domain.WeChatUser;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.wechatModel.ReturnCode;
import com.liangyou.model.wechatModel.WeChatMatchrule;
import com.liangyou.model.wechatModel.message.event.InClickEvent;
import com.liangyou.model.wechatModel.message.event.InSubscribeEvent;
import com.liangyou.model.wechatModel.message.event.InViewEvent;
import com.liangyou.model.wechatModel.message.inmsg.InImageMsg;
import com.liangyou.model.wechatModel.message.inmsg.InMsg;
import com.liangyou.model.wechatModel.message.inmsg.InTextMsg;
import com.liangyou.model.wechatModel.message.inmsg.InVoiceMsg;
import com.liangyou.service.WeChatService;
import com.liangyou.tool.wechatkit.AccessTokenKit;
import com.liangyou.tool.wechatkit.EhcacheUtil;
import com.liangyou.tool.wechatkit.HttpUtils;
import com.liangyou.tool.wechatkit.MessageKit;
import com.liangyou.tool.wechatkit.PropKit;
import com.liangyou.tool.wechatkit.SignatureCheckKit;
import com.liangyou.tool.wechatkit.SnsAccessToken;
import com.liangyou.tool.wechatkit.SnsAccessTokenApi;

import net.sf.ehcache.Cache;

@Service
@Transactional
public class WeChatServiceImpl implements WeChatService {
	
	Logger logger = Logger.getLogger(WeChatService.class);
	@Autowired
	WeChatMsgDao weChatMsgDao;
	@Autowired
	WeChatMenuDao weChatMenuDao;
	@Autowired
	WeChatEventPushDao weChatEventPushDao;
	@Autowired
	WeChatUserDao weChatUserDao;
	@Autowired
	WeChatMenuGroupDao weChatMenuGroupDao;
	@Autowired
	WeChatGraphicMsgDao weChatGraphicMsgDao;
	@Autowired
	WeChatAutoReplyDao weChatAutoReplyDao;
	@Autowired
	WeChatKfAccountDao weChatKfAccountDao;
	@Autowired
	WeChatImageDao weChatImageDao;

	@Override
	public void handlerConfig(HttpServletRequest request,HttpServletResponse response) {
		//微信配置请求
		isServerConfig(request, response);
		if(checkConfig(request, response)){
			logger.info("接入微信服务成功");
		}else {
			logger.info("微信服务参数校验失败!");
		}
	}
	
	/* 
	 * 微信消息类型:
	 * 普通消息:文本消息,图片消息,语音消息,视频消息,小视频消息,地理位置消息,链接消息
	 * 事件推送消息:关注/取消关注事件,扫描带参数二维码事件,上报地理位置事件,自定义菜单事件,点击菜单拉取消息时的事件推送,点击菜单跳转链接时的事件推送
	 */
	
	@Override
	public void handlerMsg(HttpServletRequest request,HttpServletResponse response) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String realPath = request.getSession().getServletContext().getRealPath("/");
		logger.info("开始处理微信推送消息:");
		try {
			//根据msgtype 的不同而不同媒体类型封装到不同的实体类,还有各种事件的推送 
			Map<String, String> requestMap = MessageKit.parseXml(request);
			logger.info("微信事件推送解析结果:"+requestMap.toString());
			if(StringUtils.isBlank(requestMap.get("msgType"))){
				render("消息类型错误,请查阅微信开发文档!", response,"text/plain");
				return;
			}
			//处理接收到的消息类型  (普通消息,事件推送消息)
			InMsg msg = MessageKit.processingMsg(requestMap);
			logger.info("微信解析消息结果:"+msg.toString());
			//收到微信推送过来的消息或者事件需要做出响应,否则会"该公众号暂时无法提供服务，请稍后再试"
			Date time = parthTime(msg);
			String openid = msg.getFromUserName();
			WeChatUser user = null;
			if(StringUtils.isNotBlank(openid)){
				 user = weChatUserDao.find(openid);
			}
			String userName = "";
			if(user != null){
				userName = user.getNickname();
			}
			//保存用户文本消息
			if(msg instanceof InTextMsg) {
				PropKit.use("weixin_config.txt");
				if(PropKit.getBoolean("transfer_customer_service")){
					dealWeChatMsgTransfer(response, msg, openid, userName);
					dealWeChatAutoReply((InTextMsg)msg);
					return;
				}
				dealTextMsg(response, msg, openid, userName);
			}
			//图片消息
			if(msg instanceof InImageMsg){
				if(PropKit.getBoolean("transfer_customer_service")){
					dealWeChatMsgTransfer(response, msg, openid, userName);
				}
				dealImageMsg(response, msg, openid, userName);
			}
			//用户语音消息
			if(msg instanceof InVoiceMsg) {
				if(PropKit.getBoolean("transfer_customer_service")){
					dealWeChatMsgTransfer(response, msg, openid, userName);
				}
				dealVoiceMsg(response, ACCESS_TOKEN, msg, openid, userName,realPath);
			}
			//用户关注,执行同步粉丝
			if(msg instanceof InSubscribeEvent){
				dealSubScribeEvent(msg,response);
			}
			
			//菜单点击推送
			if(msg instanceof InClickEvent) {
				dealClickEvent(msg);
			}
			
			//点击菜单事件推送
			if(msg instanceof InViewEvent){
				dealViewEvent(msg, time,response);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("微信消息解析出错");
		}
	}

	private void dealImageMsg(HttpServletResponse response, InMsg msg, String openid, String userName) {
		InImageMsg imageMsg = (InImageMsg) msg;
		WeChatMsg weChatMsg = new WeChatMsg(openid, userName,"图片消息", 0, new Date(), 1);
		weChatMsg.setMediaUrl(imageMsg.getPicUrl());
		weChatMsgDao.save(weChatMsg);
		render("", response, "text/plain");
		return;
	}

	private void dealSubScribeEvent(InMsg msg,HttpServletResponse response) {
		InSubscribeEvent ise = (InSubscribeEvent) msg;
		if("subscribe".equals(ise.getEvent())){
			logger.info("用户关注平台公众号,同步粉丝信息");
			this.syncWeChatUserList();
			//用户关注订阅号后进行关注内容推送
			WeChatMsg autoReplay = weChatMsgDao.findByPropertyForUnique("type",5);
			if(autoReplay == null) {
				logger.info("开发者未配置微信关注订阅号事件推送");
				render("", response, "text/plain");
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>\n");
			sb.append("<ToUserName><![CDATA[").append(ise.getFromUserName()).append("]]></ToUserName>\n");
			sb.append("<FromUserName><![CDATA[").append(ise.getToUserName()).append("]]></FromUserName>\n");
			sb.append("<CreateTime>").append(new Date().getTime()).append("</CreateTime>\n");
			sb.append("<MsgType><![CDATA[text]]></MsgType>\n");
			sb.append("<Content><![CDATA[").append(autoReplay.getMessageContent()).append("]]></Content>\n");
			sb.append("</xml>");
			logger.info("微信用户关注订阅号事件推送消息:\n"+sb.toString());
			try {
				response.getWriter().print(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}else {
			logger.info("用户取消关注平台公众号,删除粉丝信息");
			weChatUserDao.delete(ise.getFromUserName());
			render("", response, "text/plain");
			return;
		}
	}

	/**
	 <xml>
     <ToUserName><![CDATA[touser]]></ToUserName>
     <FromUserName><![CDATA[fromuser]]></FromUserName>
     <CreateTime>1399197672</CreateTime>
     <MsgType><![CDATA[transfer_customer_service]]></MsgType>
 	</xml>
	 */
	private void dealWeChatMsgTransfer(HttpServletResponse response, InMsg msg, String openid, String userName) {
		logger.info("开启微信消息转发到新版客服,服务器将不再处理用户消息!");
		PropKit.use("weixin_config.txt");
		String toUser = PropKit.get("wechatAdmin");
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>\n");
		sb.append("<ToUserName><![CDATA[").append(msg.getFromUserName()).append("]]></ToUserName>\n");
		sb.append("<FromUserName><![CDATA[").append(toUser).append("]]></FromUserName>\n");
		sb.append("<CreateTime>").append(new Date().getTime()).append("</CreateTime>\n");
		sb.append("<MsgType><![CDATA[transfer_customer_service]]></MsgType>\n");
		sb.append("</xml>");
		logger.info("多客服转发参数:"+sb.toString());
		try {
			response.getWriter().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	private void dealViewEvent(InMsg msg, Date time,HttpServletResponse response) {
		WeChatEventPush weChatEventPush;
		InViewEvent i = (InViewEvent) msg;
		weChatEventPush = weChatEventPushDao.findByPropertyForUnique("eventKey", i.getEventKey());
		//EventKey	事件KEY值，与自定义菜单接口中KEY值对应   fromusername eventkey createtime
		if(weChatEventPush != null){
			weChatEventPush.setCount(weChatEventPush.getCount()+1);
			weChatEventPushDao.update(weChatEventPush);
		}else {
		weChatEventPush = new WeChatEventPush(i.getEvent(), i.getFromUserName(),new Date(),i.getEventKey(),1);
		weChatEventPushDao.save(weChatEventPush);
		render("",response,"text/plain");
		return;
		}
	}

	private void dealClickEvent(InMsg msg) {
		InClickEvent i = (InClickEvent)msg;
		//EventKey	事件KEY值，与自定义菜单接口中KEY值对应   fromusername eventkey createtime
		String eventKey = i.getEventKey();
		logger.info("微信推送事件:"+i.toString());
		if(StringUtils.isNotBlank(eventKey) && eventKey.startsWith("_fix_")){
			dealEventPush(i);//调用接口 发送图文消息 这里调用的客服消息接口
		};
	}

	private void dealTextMsg(HttpServletResponse response, InMsg msg, String openid, String userName) {
		logger.info("处理微信端用户文本消息");
		InTextMsg inTextMsg = (InTextMsg) msg;
		dealWeChatAutoReply(inTextMsg);
		logger.info("消息内容:"+inTextMsg.toString());
		WeChatMsg weChatMsg = new WeChatMsg(openid,userName,inTextMsg.getContent(), 0,new Date(),1);
		weChatMsgDao.save(weChatMsg);
		render("", response,"text/plain");
		return;
	}

	private void dealVoiceMsg(HttpServletResponse response, String ACCESS_TOKEN, InMsg msg, String openid,
			String userName,String realPath) {
		logger.info("处理微信端用户语音消息");
		InVoiceMsg inVoiceMsg = (InVoiceMsg) msg;
		String mediaId = inVoiceMsg.getMediaId();//获取多媒体位置
		String format = inVoiceMsg.getFormat();//语音格式
		String download = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";
		String filename = MessageKit.downLoadMedia(download,ACCESS_TOKEN,mediaId,format,realPath);
		WeChatMsg weChatMsg = new WeChatMsg(openid, userName, "语音消息", 0, new Date(),1);
		weChatMsg.setMediaUrl(filename);
		weChatMsgDao.save(weChatMsg);
		render("", response,"text/plain");
		return;
	}

	private void dealWeChatAutoReply(InTextMsg inTextMsg) {
		
		List<WeChatAutoReply> list = weChatAutoReplyDao.findByCriteria(new SearchParam().addParam("status", 1));
		if(null != list && list.size() > 0) {
		for (WeChatAutoReply weChatAutoReply : list) {
			if(inTextMsg.getContent().contains(weChatAutoReply.getKeyWords())){
				logger.info("开始处理微信自动回复---关键字:"+inTextMsg.getContent());
				WeChatMsg msg = new WeChatMsg();
				msg.setMessageContent(weChatAutoReply.getContent());
				msg.setMessageStatus(1);
				msg.setType(6);//6 自动回复
				msg.setUserName("WeChatAdmin");
				msg.setOpenid(inTextMsg.getFromUserName());
				logger.info("处理微信自动回复消息:"+msg.toString());
				sendCustomTextMsg(msg);
				weChatMsgDao.save(msg);
			}
			}
		}
	}
	/**
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>12345678</CreateTime>
		<MsgType><![CDATA[news]]></MsgType>
		<ArticleCount>2</ArticleCount>
		<Articles>
		<item>
		<Title><![CDATA[title1]]></Title> 
		<Description><![CDATA[description1]]></Description>
		<PicUrl><![CDATA[picurl]]></PicUrl>
		<Url><![CDATA[url]]></Url>
		</item>
		<item>
		<Title><![CDATA[title]]></Title>
		<Description><![CDATA[description]]></Description>
		<PicUrl><![CDATA[picurl]]></PicUrl>
		<Url><![CDATA[url]]></Url>
		</item>
		</Articles>
	</xml>
	 */
	private void dealEventPush(InClickEvent i) {
		logger.info("开始处理微信端菜单事件推送:");
		List<WeChatGraphicMsg> list = weChatGraphicMsgDao.findByCriteria(SearchParam.getInstance().addParam("thumb_media_id", i.getEventKey().replace("_fix_", "")));
		//将图文消息推送给用户
		/*PropKit.use("weixin_config.txt");
		String fromUser = PropKit.get("wechatAdmin");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<xml>\n");
		sb.append("<ToUserName><![CDATA[").append(i.getFromUserName()).append("]]></ToUserName>\n");
		sb.append("<FromUserName><![CDATA[").append(fromUser).append("]]></FromUserName>\n");
		long time = new Date().getTime();
		sb.append("<CreateTime>").append(time).append("</CreateTime>\n");
		sb.append("<MsgType><![CDATA[news]]></MsgType>\n");
		sb.append("<ArticleCount>").append(graphicMsgs.size()).append("</ArticleCount>\n");
		sb.append("<Articles>\n");
		for (WeChatGraphicMsg weChatGraphicMsg : graphicMsgs) {
			if(weChatGraphicMsg.getTitle()!=null)
			sb.append("<item>\n<Title><![CDATA[").append(weChatGraphicMsg.getTitle()).append("]]></Title>\n");
			if(weChatGraphicMsg.getDigest()!=null)
			sb.append("<Description><![CDATA[").append(weChatGraphicMsg.getDigest()).append("]]></Description>\n");
			if(weChatGraphicMsg.getPicpath()!=null)
			sb.append("<PicUrl><![CDATA[").append(weChatGraphicMsg.getPicpath()).append("]]></PicUrl>\n");
			if(weChatGraphicMsg.getUrl() != null)
			sb.append("<Url><![CDATA[").append(weChatGraphicMsg.getUrl()).append("]]></Url>\n</item>\n");
		}
		sb.append("</Articles>\n");
		sb.append("</xml>");
		return sb.toString();
	*/

		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+ACCESS_TOKEN;
		JSONObject object = new JSONObject();
		object.put("touser",i.getFromUserName());
		object.put("msgtype","mpnews");
		JSONObject obj = new JSONObject();
		for (WeChatGraphicMsg msg : list) {
			obj.put("media_id",msg.getMedia_id());
		}
		object.put("mpnews", obj);
		logger.info("向微信端推送数据:"+JSON.toJSONString(object));
		String doPostSSL = HttpUtils.doPostSSL(apiUrl, JSON.toJSONString(object));
		logger.info("微信菜单推送返回:"+doPostSSL);
		return;
	}
	private Date parthTime(InMsg msg) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(msg.getCreateTime());
		Date time = calendar.getTime();
		return time;
	}

	private boolean checkConfig(HttpServletRequest request,HttpServletResponse response) {
		String echostr = request.getParameter("echostr");
		String timestamp = request.getParameter("timestamp");
		String signature = request.getParameter("signature");
		String nonce = request.getParameter("nonce");
		ServletContext context = ServletActionContext.getServletContext();
		logger.info("context"+context);
		context.setAttribute("appid",PropKit.get("appId"));
		context.setAttribute("timestamp", timestamp);
		context.setAttribute("nonceStr", nonce);
		context.setAttribute("signature", signature);
		if(StringUtils.isBlank(echostr) || StringUtils.isBlank(signature)|| StringUtils.isBlank(nonce)){
			render("check signature false",response,"text/plain");
			return false;
		}
		if(SignatureCheckKit.sign.checkSignature(signature,timestamp, nonce)){
			render(echostr, response,"text/plain");
			return true;
		}
		return false;
	}

	private boolean isServerConfig(HttpServletRequest request,
			HttpServletResponse response) {
		String echostr = request.getParameter("echostr");
		return StringUtils.isNotBlank(echostr);
	}
		

	private void render(String text, HttpServletResponse response,String contentType) {
		PrintWriter printWriter = null;
		try {
			response.setHeader("Cache-Control", "no-cache");//访问不启用缓存
			response.setHeader("Pragma", "no-cache");//页面缓存
			response.setDateHeader("Expires", 0); //过期时间
			response.setContentType(contentType);//纯文本 不做处理"text/plain"
			response.setCharacterEncoding("utf-8");
			printWriter = response.getWriter();
			printWriter.print(text);//原样输出
			printWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(printWriter != null){
				printWriter.close();
			}
		}
	}

	@Override
	public String createCustomService(WeChatKfAccount kfAccount) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token="+ACCESS_TOKEN;
		weChatKfAccountDao.save(kfAccount);
		return dealPost(JSON.toJSONString(kfAccount), apiUrl);
	}
	private String dealPost(String bodyJson, String apiUrl) {
		String doPostSSL = HttpUtils.doPostSSL(apiUrl, bodyJson);
		JSONObject jsonObject = JSON.parseObject(doPostSSL);
		Integer errCode = jsonObject.getInteger("errcode");
		String string = ReturnCode.get(errCode);
		return string;
	}
	@Override
	public String modifyCustomService(WeChatKfAccount kfAccount) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token="+ACCESS_TOKEN;
		weChatKfAccountDao.update(kfAccount);
		return dealPost(JSON.toJSONString(kfAccount), apiUrl);
	}
	@Override
	public String delCustomService(Long id) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		WeChatKfAccount kfAccount = weChatKfAccountDao.find(id);
		String apiUrl = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token="+ACCESS_TOKEN;
		weChatKfAccountDao.delete(id);
		return dealPost(JSON.toJSONString(kfAccount), apiUrl);
	}
	@Override
	public List<WeChatKfAccount> getCustomList() {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token="+ACCESS_TOKEN;
		String json = HttpUtils.doGet(apiUrl);
		JSONObject j1 = JSON.parseObject(json);
		Object object = j1.get("kf_list");
		List<WeChatKfAccount> list = JSONArray.parseArray(JSON.toJSONString(object), WeChatKfAccount.class);
		return list;
	}
	
	@Override
	public PageDataList<WeChatUser> getWeChatUserList(SearchParam param) {
		return weChatUserDao.findAllPageList(param);
	}
	@Override
	public PageDataList<WeChatMsg> getWechatMsg(SearchParam param) { 
		return weChatMsgDao.findAllPageList(param);
	}
	
	@Override
	public String getArticleTotal(String bodyJson) {
		return null;
	}
	@Override
	public String getUserShare(String bodyJson) {
		return null;
	}
	@Override
	public String addWeChatMenu(String gid) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		if(StringUtils.isNotBlank(gid)){
			long id = Long.parseLong(gid);
			List<WeChatMenu> menus = weChatMenuDao.findByGid(id);
			WeChatMatchrule weChatMatchrule = new WeChatMatchrule();
			String bodyJson = MessageKit.parseMenu(menus,weChatMatchrule);
			logger.info("生成微信端菜单参数:"+bodyJson);
			String apiUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+ACCESS_TOKEN;
			return dealPost(bodyJson, apiUrl);
		}
		return null;
	}
	@Override
	public void delMenu(long id) {
		weChatMenuDao.delete(id);
	}
	@Override
	public void delMenuGroup(long id) {
		List<WeChatMenu> menus = weChatMenuDao.findByGid(id);
		weChatMenuDao.delete(menus);
		weChatMenuGroupDao.delete(id); ;
	}
	@Override
	public void saveMenuGroup(WeChatMenuGroup weChatMenuGroup) {
		weChatMenuGroupDao.save(weChatMenuGroup);
	}
	@Override
	public void saveMenu(WeChatMenu weChatMenu) {
		weChatMenuDao.save(weChatMenu);
	}
	@Override
	public PageDataList<WeChatMenuGroup> getMenuGroup(SearchParam param) {
		return weChatMenuGroupDao.findAllPageList(param);
	}
	@Override
	public PageDataList<WeChatMenu> showMenu(SearchParam param) {
		return weChatMenuDao.findAllPageList(param);
	}
	@Override
	public String delWechatMenu() {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token="+ACCESS_TOKEN;
		String doGet = HttpUtils.doGet(apiUrl);
		JSONObject jsonObject = JSON.parseObject(doGet);
		Integer errCode = jsonObject.getInteger("errcode");
		return ReturnCode.get(errCode);
	}
	@Override
	public WeChatMenuGroup findWeChatMenuGroupById(long id) {
		return weChatMenuGroupDao.find(id);
	}
	@Override
	public void updateMenuGroup(WeChatMenuGroup weChatMenuGroup) {
		weChatMenuGroupDao.update(weChatMenuGroup);
	}
	@Override
	public List<WeChatMenu> getParentMenu(SearchParam param) {
		return weChatMenuDao.findByCriteria(param);
	}
	@Override
	public WeChatMenu findWeChatMenuById(Long parentid) {
		return weChatMenuDao.find(parentid);
	}
	@Override
	public WeChatMsg getWechatMsgById(long id) {
		return weChatMsgDao.find(id);
	}

	
	@Override
	public String sendCustomTextMsg(WeChatMsg weChatMsg) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+ACCESS_TOKEN;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"touser\":").append("\""+weChatMsg.getOpenid()+"\"").append(",\"msgtype\":\"text\",\"text\":{\"content\":").append("\""+weChatMsg.getMessageContent()+"\"").append("}}");
		String bodyJson = sb.toString();
		logger.info("微信发送客服消息提交的参数为:"+bodyJson);
		weChatMsg.setMessageOpr(0);
		weChatMsg.setMessageTime(new Date());
		weChatMsg.setType(2);//回复消息 type=2
		weChatMsg.setMessageStatus(1);
		weChatMsgDao.save(weChatMsg);
		return dealPost(bodyJson, apiUrl);
		}
	@Override
	public void updateWeChatMsg(WeChatMsg msg) {
		weChatMsgDao.update(msg);
	}
	@SuppressWarnings("unchecked")
	@Override
	public String syncWeChatUserList() {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		logger.info("开始同步微信端用户数据");
		List<WeChatUser> localUser = weChatUserDao.findAll();
		if(localUser != null){logger.info("本地用户总数为:"+localUser.size());}
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token="+ACCESS_TOKEN+"&next_openid=";
		String doGet = HttpUtils.doGet(apiUrl);
		JSONObject object = JSON.parseObject(doGet);
		Map<String,List<String>> dataMap = new HashMap<String,List<String>>();
		int count = 0;
		if(object.get("data") != null){
			dataMap = (Map<String,List<String>>) object.get("data");
			List<String> openids = dataMap.get("openid");
			if(openids == null || openids.size() == 0){return "您还没有用户关注";}
			for (String openid : openids) {
				String apiUrl2 = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+ACCESS_TOKEN+"&openid="+openid+"&lang=zh_CN";
				String doGet2 = HttpUtils.doGet(apiUrl2);
				logger.info("当前获取用户操作信息:"+doGet2);
				WeChatUser chatUser = JSON.parseObject(doGet2, WeChatUser.class);
				if(StringUtils.isNotBlank(doGet2)){
					chatUser.setOpenid(openid);
					weChatUserDao.merge(chatUser);
					count++;
				}
			}
		}
		logger.info("共同步用户: "+count+"个");
		return "同步完成";
	}
	@Override
	public PageDataList<WeChatGraphicMsg> getWeChatGraphicMsg(SearchParam param) {
		return weChatGraphicMsgDao.findAllPageList(param);
	}
	@Override
	public String delGraphicMsg(Long id) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		WeChatGraphicMsg msg = weChatGraphicMsgDao.find(id);
		weChatGraphicMsgDao.delete(id);
		//删除微信端图文消息
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token="+ACCESS_TOKEN;
		String bodyJson = "{\"media_id\":\""+msg.getMedia_id()+"\"}";
		logger.info("删除微信永久素材参数:"+bodyJson);
		return dealPost(bodyJson, apiUrl);
	}
	@Override
	public String syncGraphicMsg() {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+ACCESS_TOKEN;
		JSONObject object = new JSONObject();
		object.put("type", "news");
		object.put("offset", 0);//第一次获取前20个
		object.put("count", 20);
		String doPostSSL = HttpUtils.doPostSSL(apiUrl, object.toJSONString());
		if(StringUtils.isBlank(doPostSSL)){return "当前公众号没有该类型素材,请至微信端管理界面添加!";}
		JSONObject object2 = JSON.parseObject(doPostSSL);
		int totalCount = (Integer) object2.get("total_count");//该类型的素材的总数
		logger.info("当前公众号该类型的素材总数为:"+totalCount);
		if(totalCount == 0) {return "当前公众号没有该类型素材,请至微信端管理界面添加!";}
		int itemCount = (Integer) object2.get("item_count");//本次调用获取的素材的数量
		int count = totalCount%itemCount == 0 ? totalCount/itemCount : totalCount/itemCount+1;
		for(int i = 1; i <= count; i++){
			object.put("offset",(i-1)*itemCount);
			object.put("count", 20);
			String result2 = HttpUtils.doPostSSL(apiUrl, object.toJSONString());
			JSONObject parseObject = JSON.parseObject(result2);
			com.alibaba.fastjson.JSONArray jsonArray = (JSONArray) parseObject.get("item");
			for(int k = 0; k < jsonArray.size(); k++){
				String content = jsonArray.getString(k);// 获取 media_id update_time
				JSONObject contentObject = JSON.parseObject(content);
				JSONObject contentArray = (JSONObject) contentObject.get("content");
				JSONArray newsArray =(JSONArray) contentArray.get("news_item");
				String media_id = (String) contentObject.get("media_id");
				int time = (Integer) contentObject.get("update_time");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(time);
				Date update_time = calendar.getTime();
				List<WeChatGraphicMsg> list = JSON.parseArray(newsArray.toJSONString(), WeChatGraphicMsg.class);
				for (WeChatGraphicMsg weChatGraphicMsg : list) {
					String thumb_media_id = weChatGraphicMsg.getThumb_media_id();
					WeChatGraphicMsg msg = weChatGraphicMsgDao.findByCriteriaForUnique(new SearchParam().addParam("thumb_media_id", thumb_media_id));
					if(msg != null){
						weChatGraphicMsg.setFavourcount(msg.getFavourcount());
						weChatGraphicMsg.setInputcode(msg.getInputcode());
						weChatGraphicMsg.setMsgtype(msg.getMsgtype());
						weChatGraphicMsg.setType(msg.getType());
						weChatGraphicMsg.setReadcount(msg.getReadcount());
						weChatGraphicMsg.setEnable(msg.getEnable());
					}
					weChatGraphicMsg.setMedia_id(media_id);
					weChatGraphicMsg.setUpdate_time(update_time);
					weChatGraphicMsg.setTotalCount(totalCount);
					weChatGraphicMsg.setItemCount(itemCount);
					weChatGraphicMsgDao.merge(weChatGraphicMsg);
				}
		}
			logger.info("同步微信图文消息完成!");
	}
	return "同步微信图文消息完成!";
	}
	
	@Override
	public String updateWeChatGraphicMsg(WeChatGraphicMsg weChatGraphicMsg) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token="+ACCESS_TOKEN;
		//String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		//String upload = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token="+ACCESS_TOKEN;只会返回url
		String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		//String upload = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+ACCESS_TOKEN+"&type=%s";
		JSONObject jsonObject = new JSONObject();
		JSONObject upJsonObject = MessageKit.upLoadMedia(upload,"image",weChatGraphicMsg.getPicpath());//图文消息的封面图片素材id
		if(upJsonObject.containsKey("media_id")){
			weChatGraphicMsg.setThumb_media_id(jsonObject.getString("media_id"));
		};
		if(upJsonObject.containsKey("url")){
			String url = jsonObject.getString("url");
			weChatGraphicMsg.setPicpath(url);//封面图片地址,在其他图文消息中也可以直接使用(不需要再次上传)
		}
		jsonObject.put("media_id",weChatGraphicMsg.getMedia_id());
		jsonObject.put("index", 0);
		JSONObject jsonArray = new JSONObject();
		String title = weChatGraphicMsg.getTitle();
		String thumb_media_id = weChatGraphicMsg.getThumb_media_id();
		String author = weChatGraphicMsg.getAuthor();
		String digest = weChatGraphicMsg.getDigest();
		Integer show_cover_pic = weChatGraphicMsg.getShow_cover_pic();
		String content = weChatGraphicMsg.getContent();
		String content_source_url = weChatGraphicMsg.getContent_source_url();
		jsonArray.put("title", title);
		jsonArray.put("thumb_media_id", thumb_media_id);
		jsonArray.put("author", author);
		jsonArray.put("digest", digest);
		jsonArray.put("show_cover_pic", show_cover_pic);
		jsonArray.put("content", content);
		jsonArray.put("content_source_url", content_source_url);
		jsonObject.put("articles", jsonArray);
		return dealPost(jsonObject.toJSONString(), apiUrl);
	}
	@Override
	public WeChatGraphicMsg findGraphicMsgById(Long id) {
		return weChatGraphicMsgDao.find(id);
	}
	@Override
	public void saveWeChatGraphicMsg(WeChatGraphicMsg weChatGraphicMsg) {
		weChatGraphicMsgDao.merge(weChatGraphicMsg);
	}
	
	@Override
	public JSONObject uploadGraphicMsg(List<WeChatGraphicMsg> msgNewsList) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token="+ACCESS_TOKEN;
		//String uploadurl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		String uploadurl2 = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+ACCESS_TOKEN+"&type=%s";
		try{
			JSONArray jsonArr = new JSONArray();
			for(WeChatGraphicMsg news : msgNewsList){
				JSONObject jsonObj = new JSONObject();
				String mediaId = "";
				String url = "";
				//上传图片素材
				JSONObject jsonObject = MessageKit.upLoadMedia(uploadurl2,"image",news.getPicpath());//图文消息的封面图片素材id
				if(jsonObject.containsKey("media_id")){
					mediaId = jsonObject.getString("media_id");
				};
				if(jsonObj.containsKey("url")){
					url = jsonObject.getString("url");
					news.setPicpath(url);//封面图片地址,在其他图文消息中也可以直接使用(不需要再次上传)
				}
				jsonObj.put("thumb_media_id", mediaId);
				news.setThumb_media_id(mediaId);
				if(news.getAuthor() != null){
					jsonObj.put("author", news.getAuthor());
				}else{
					jsonObj.put("author", "");
				}
				if(news.getTitle() != null){
					jsonObj.put("title", news.getTitle());
				}else{
					jsonObj.put("title", "");
				}
				if(news.getContent_source_url() != null){
					jsonObj.put("content_source_url", news.getContent_source_url());
				}else{
					jsonObj.put("content_source_url", "");
				}
				if(news.getDigest() != null){
					jsonObj.put("digest", news.getDigest());
				}else{
					jsonObj.put("digest", "");
				}
				if(news.getShow_cover_pic() != null){
					jsonObj.put("show_cover_pic", news.getShow_cover_pic());
				}else{
					jsonObj.put("show_cover_pic", "1");
				}
				jsonObj.put("content", news.getContent());
				jsonArr.add(jsonObj);
			}
			JSONObject postObj = new JSONObject();
			postObj.put("articles", jsonArr);
			String doPostSSL = HttpUtils.doPostSSL(apiUrl, postObj.toJSONString());
			JSONObject jsonObject = JSON.parseObject(doPostSSL);
			return jsonObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public WeChatUser doLogin(SearchParam param) {
		List<WeChatUser> list = weChatUserDao.findByCriteria(param);
		if(null == list || list.size() < 1){
			return null;
		}
		return list.get(0);
	}
	@Override
	public String uploadHeadImg(String filePath,String acccount) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String url = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token="+ACCESS_TOKEN+"&kf_account="+acccount;
		JSONObject jsonObject = MessageKit.upLoadMedia(url,"image",filePath);//图文消息的封面图片素材id
		return ReturnCode.get(jsonObject.getIntValue("errcode"));
	}
	@Override
	public void delWeChatMsg(long id) {
		WeChatMsg msg = weChatMsgDao.find(id);
		msg.setMessageStatus(1);//已回复
		weChatMsgDao.update(msg);
	}
	@Override
	public void mergeWeChatAutoReply(WeChatAutoReply weChatAutoReply) {
		weChatAutoReplyDao.merge(weChatAutoReply);
	}
	@Override
	public void delWeChatAutoReply(long id) {
		weChatAutoReplyDao.delete(id);
	}
	@Override
	public PageDataList<WeChatAutoReply> getWeChatAutoReply(SearchParam param) {
		return weChatAutoReplyDao.findAllPageList(param);
	}



	@Override
	public WeChatUser findWeChatUserById(String opendid) {
		return weChatUserDao.find(opendid);
	}

	@Override
	public WeChatAutoReply findWechatARepById(long id) {
		return weChatAutoReplyDao.find(id);
	}

	@Override
	public WeChatKfAccount findWeChatKfAccountById(long id) {
		return weChatKfAccountDao.find(id);
	}

	@Override
	public List<WeChatGraphicMsg> findGraphicMsgByIds(String[] ids) {
		List<WeChatGraphicMsg> list = new ArrayList<WeChatGraphicMsg>();
		if(ids != null && ids.length > 0){
			for (String id : ids) {
				WeChatGraphicMsg graphicMsg = weChatGraphicMsgDao.find(Long.parseLong(id));
				if(graphicMsg!=null){
					list.add(graphicMsg);
				}
			}
		}
		return list;
	}

	@Override
	public String sendAllGraphicMsg(WeChatGraphicMsg msg) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+ACCESS_TOKEN;
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
		JSONObject o3 = new JSONObject();
		o2.put("is_to_all", true);
		o3.put("media_id",msg.getMedia_id());
		o1.put("filter", o2.toJSONString());
		o1.put("mpnews", o3.toJSONString());
		o1.put("msgtype","mpnews");
		o1.put("send_ignore_reprint",1);//图文消息被判定为转载时，是否继续群发。1继续
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendAllTextMsg(String content) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+ACCESS_TOKEN;
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
		JSONObject o3 = new JSONObject();
		o2.put("is_to_all", true);
		o3.put("content",content);
		o1.put("filter", o2.toJSONString());
		o1.put("text", o3.toJSONString());
		o1.put("msgtype","text");
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendAllVoiceMsg(String filesFileName) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+ACCESS_TOKEN;
		String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		JSONObject upLoadMedia = MessageKit.upLoadMedia(url,"voice",filesFileName);
		String mediaId = "";
		if(upLoadMedia.containsKey("media_id")){
			mediaId = upLoadMedia.getString("media_id");
		}else {
			throw new BussinessException("微信端语音上传出错,请联系开发人员!");
		};
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
		JSONObject o3 = new JSONObject();
		o2.put("is_to_all", true);
		o3.put("media_id",mediaId);
		o1.put("filter", o2.toJSONString());
		o1.put("voice", o3.toJSONString());
		o1.put("msgtype","voice");
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendAllImgMsg(String filesFileName) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+ACCESS_TOKEN;
		String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		JSONObject upLoadMedia = MessageKit.upLoadMedia(url,"voice",filesFileName);
		String mediaId = "";
		if(upLoadMedia.containsKey("media_id")){
			mediaId = upLoadMedia.getString("media_id");
		}else {
			throw new BussinessException("微信端图片上传出错,请联系开发人员!");
		};
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
		JSONObject o3 = new JSONObject();
		o2.put("is_to_all", true);
		o3.put("media_id",mediaId);
		o1.put("filter", o2.toJSONString());
		o1.put("image", o3.toJSONString());
		o1.put("msgtype","image");
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendAllVideoMsg(String filesFileName,String title,String description) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+ACCESS_TOKEN;
		String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		String videoUrl = "https://file.api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token="+ACCESS_TOKEN;
		JSONObject upLoadMedia = MessageKit.upLoadMedia(url,"video",filesFileName);
		String mediaId = "";
		if(upLoadMedia.containsKey("media_id")){
			mediaId = upLoadMedia.getString("media_id");
		}else {
			throw new BussinessException("微信端视频上传出错,请联系开发人员!");
		};
		JSONObject videoObject = new JSONObject();
		videoObject.put("media_id", mediaId);
		videoObject.put("title",title);
		videoObject.put("description",description);
		String post = HttpUtils.doPost(videoUrl,JSON.toJSONString(videoObject));
		JSONObject returnObject = JSON.parseObject(post);
		if(returnObject.containsKey("media_id")){
			mediaId = returnObject.getString("media_id");
		}else {
			throw new BussinessException("微信端视频上传出错,请联系开发人员!");
		}
		JSONObject o1 = new JSONObject();
		JSONObject o2 = new JSONObject();
		JSONObject o3 = new JSONObject();
		o2.put("is_to_all", true);
		o3.put("media_id",mediaId);
		o1.put("filter", o2.toJSONString());
		o1.put("mpvideo", o3.toJSONString());
		o1.put("msgtype","mpvideo");
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendCustomImgMsg(String filesFileName,String openid) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+ACCESS_TOKEN;
		String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		JSONObject upLoadMedia = MessageKit.upLoadMedia(upload, "image",filesFileName);
		String mediaId = "";
		if(upLoadMedia.containsKey("media_id")){
			mediaId = upLoadMedia.getString("media_id");
		}else {
			throw new BussinessException("微信图片上传失败,请联系开发人员");
		}
		JSONObject o1= new JSONObject();
		JSONObject o2 = new JSONObject();
		o2.put("media_id", mediaId);
		o1.put("touser",openid);
		o1.put("msgtype", "image");
		o1.put("image",JSON.toJSONString(o2));
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendCustomVoiceMsg(String filesFileName,String openid) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+ACCESS_TOKEN;
		String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";
		JSONObject upLoadMedia = MessageKit.upLoadMedia(upload, "voice",filesFileName);
		String mediaId = "";
		if(upLoadMedia.containsKey("media_id")){
			mediaId = upLoadMedia.getString("media_id");
		}else {
			throw new BussinessException("微信语音上传失败,请联系开发人员");
		}
		JSONObject o1= new JSONObject();
		JSONObject o2 = new JSONObject();
		o2.put("media_id", mediaId);
		o1.put("touser",openid);
		o1.put("msgtype", "voice");
		o1.put("voice",JSON.toJSONString(o2));
		return dealPost(JSON.toJSONString(o1), apiUrl);
	}

	@Override
	public String sendCustomVideoMsg(String filesFileName,String openid) {
		// 功能暂不实现
		return "";
	}

	@Override
	public String sendCustomMusicMsg(String filesFileName,String openid) {
		// 功能暂不实现
		return "";
	}

	@Override
	public String sendCustomNewsMsg(WeChatGraphicMsg weChatMsg,String openid) {
	String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
	String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+ACCESS_TOKEN;
	JSONObject object = new JSONObject();
	object.put("touser",openid);
	object.put("msgtype","mpnews");
	JSONObject obj = new JSONObject();
	obj.put("media_id",weChatMsg.getMedia_id());
	object.put("mpnews", obj);
	return dealPost(JSON.toJSONString(object), apiUrl);
	}
	
	@Override
	public String uploadGraphicMsgImage(String filesFileName) {
		String ACCESS_TOKEN =  AccessTokenKit.getAccessToken().getAccessToken();
		String apiUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token="+ACCESS_TOKEN;
		JSONObject upLoadMedia = MessageKit.upLoadMedia(apiUrl, "image", filesFileName);
		String url = "";
		url = upLoadMedia.getString("url");
		if(StringUtils.isNotBlank(url)){
			WeChatImage image = new WeChatImage(url, new Date());
			weChatImageDao.save(image);
		}
	
		return url;
	}

	@Override
	public PageDataList<WeChatImage> getWeChatImage(SearchParam param) {
		return weChatImageDao.findAllPageList(param);
	}
	
	@Override
	public SnsAccessToken getOAuth2Token(String code) {
		PropKit.use("weixin_config.txt");
		String appId = PropKit.get("appId");
		String secret = PropKit.get("appSecret");
		SnsAccessToken token = SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
		//将获取到的对象放到缓存中有效期一个月
		
		try {
			EhcacheUtil.getInstance().put("SnsAccessToken", token.getOpenid(), token.getRefresh_token());
			Cache cache = EhcacheUtil.getInstance().get("SnsAccessToken");
			logger.info("当前缓存信息："+cache);
		} catch (Exception e) {
			
		}
		logger.info("当前网页授权的token:"+token.toString());
		return SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
	}

	@Override
	public SnsAccessToken refreshToken(String refresh_token) {
		PropKit.use("weixin_config.txt");
		String appId = PropKit.get("appId");
		return SnsAccessTokenApi.getSnsAccessToken(appId, refresh_token);
	}

	@Override
	public WeChatUser getWeChatUserInfo(SnsAccessToken snsAccessToken) {
		String apiUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+snsAccessToken.getAccessToken()+"&openid="+snsAccessToken.getOpenid()+"&lang=zh_CN ";
		String jsonString = HttpUtils.doGet(apiUrl);
		WeChatUser user = JSON.parseObject(jsonString, WeChatUser.class);
		weChatUserDao.merge(user);
		return user;
	}
	
}
	
