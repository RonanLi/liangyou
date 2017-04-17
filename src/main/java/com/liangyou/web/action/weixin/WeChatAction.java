package com.liangyou.web.action.weixin;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.liangyou.domain.WeChatAutoReply;
import com.liangyou.domain.WeChatGraphicMsg;
import com.liangyou.domain.WeChatImage;
import com.liangyou.domain.WeChatKfAccount;
import com.liangyou.domain.WeChatMenu;
import com.liangyou.domain.WeChatMenuGroup;
import com.liangyou.domain.WeChatMsg;
import com.liangyou.domain.WeChatUser;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.wechatModel.ReturnCode;
import com.liangyou.service.WeChatService;
import com.liangyou.tool.MD5Util;
import com.liangyou.tool.wechatkit.AccessTokenKit;
import com.liangyou.tool.wechatkit.EhcacheUtil;
import com.liangyou.tool.wechatkit.PropKit;
import com.liangyou.tool.wechatkit.Sign;
import com.liangyou.tool.wechatkit.SnsAccessToken;
import com.liangyou.tool.wechatkit.JSTicketKit;
import com.liangyou.tool.wechatkit.UploadUtil;
import com.liangyou.util.Constants;
import com.liangyou.util.DateUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ActionContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * @author lijing
 * 微信端
 */
@Controller
@Namespace("/wx")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("wechatStack"))
public class WeChatAction extends BaseAction{
	/**
	 * js-sdk
	 */
	@Action(value="jssdk",results={@Result(name="success",type="ftl",location="/wechat/wxcms/js-sdk.html")})
	public String jsSdk(){
		String jsapi_ticket = JSTicketKit.getJsapiTicket().getTicket();
		logger.info("微信端返回签名使用的js-ticket"+jsapi_ticket);
		Map<String, String> sign = Sign.sign(jsapi_ticket, "http://120.27.49.72/wx/jssdk.html");
		request.setAttribute("data", sign);
		return SUCCESS;
	}
	
	
	/**
	 * 微信网页授权OAuth2.0认证开发 订阅号没有相关权限 只有服务号才有
	 * 此地址应根据实际应用来定
	 * 微信端点击授权地址如下：
	 */
	
	/**
	 https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf0e81c3bee622d60&redirect_uri=http%3A%2F%2Fnba.bluewebgame.com%2Foauth_response.php&response_type=
	 code&scope=snsapi_userinfo&state=STATE#wechat_redirect 
	 */
	/**
	 * 如有需要请调用本接口获取用户的网页授权信息
	 * 使用要求 期限30天 我们服务器没有进行过重启
	 */
	@Action("/refresh_token")
	public String refreshToken(){
		String parameter = request.getParameter("openid");
		Cache cache = EhcacheUtil.getInstance().get("SnsAccessToken");
		Element element = cache.get(parameter);
		if(element == null) {
			message("没有查询到相关信息");
		}else {
			SnsAccessToken snsAccessToken = weChatService.refreshToken(element.toString());
			message("当前获取到的网页授权码为："+element.toString());
		}
		
	return SUCCESS;
	}
	/**
	 * 微信端获取网页授权返回地址编码格式的url
	 * @return
	 */
	@Action(value="/getUrl",results={@Result(name="success",type="ftl",location="/wechat/wxcms/showUrl.html")})
	public String getUrl(){
		String parameter = request.getParameter("url");
		try {
			String encode = URLEncoder.encode(parameter, "UTF-8");
			logger.info("编码后的网址为："+encode);
			request.setAttribute("url",encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 微信端获取网页授权acess_token 
	 * @return
	 */
	@Action(value="/oauth2",results={@Result(name="success",type="ftl",location="/wechat/wxcms/oauth2.html")})
	public String OAuth2(){
		String code = request.getParameter("code");
		SnsAccessToken snsAccessToken = weChatService.getOAuth2Token(code);
		if(snsAccessToken.getAccessToken() != null) {
			WeChatUser user = weChatService.getWeChatUserInfo(snsAccessToken);
			logger.info("微信端通过网页授权返回的用户昵称为："+user.getNickname());
			request.setAttribute("weChatUser", user);
		}
		return SUCCESS;
	}
	
	/**
	 * 绑定账户,将用户平台账户信息与用户微信openid 关联绑定
	 */
	
	public String bindingWeChatAccount(){
		return SUCCESS;
	}
	
	/**
	 * 解绑账户
	 */
	public String unboundWeChatAccount(){
		
		return SUCCESS;
	}
	

	
	
	/**
	 * 同步微信端聊天记录到我们服务器(微信端只能同步24小时聊天记录...后期开发)
	 */
	
	public String syncWeChatLog(){
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		if(StringUtils.isNotBlank(starttime)){
			
		}
		if(StringUtils.isNotBlank(endtime)){
			StringUtils.isNotBlank("");
		}
		return SUCCESS;
	}
	
	/***                                              分割线
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * 群发功能(默认群发为全部用户 订阅号支持按标签群发)
	 */
	
	/**
	 * 群发页面跳转
	 */
	@Action(value="toSendAllMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/sendAllMsg.html")})
	public String toSendAllMsg(){
		request.setAttribute("cur_nav","sendAll");
		return SUCCESS;
	}
	/**
	 * 群发图文(只能是单图文)
	 */
	@Action(value="sendAllGraphicMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String sendAllGraphicMsg(){
		String graphicMsgId = request.getParameter("graphicMsgId");
		WeChatGraphicMsg msg = weChatService.findGraphicMsgById(Long.parseLong(graphicMsgId));
		if(msg!=null){
			String msgString = weChatService.sendAllGraphicMsg(msg);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	
	/**
	 * 群发文本
	 */
	@Action(value="sendAllTextMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String sendAllTextMsg(){
		String content = request.getParameter("content");
		if(StringUtils.isNotBlank(content)){
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendAllTextMsg(content);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	
	/**
	 * 群发语音
	 */
	@Action(value="sendAllVoiceMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String sendAllVoiceMsg(){
		if(files!=null) {
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendAllVoiceMsg(filesFileName);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	
	/**
	 * 群发图片(直接上传至微信服务器)
	 */
	@Action(value="sendAllImgMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String sendAllImgMsg(){
		if(files!=null) {
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendAllImgMsg(filesFileName);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	
	/**
	 * 群发视频(群发视频不再是多媒体接口上传)
	 */
	@Action(value="sendAllVedioMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String sendAllVedioMsg(){
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		if(StringUtils.isBlank(title) || StringUtils.isBlank(description)){
			message("请将视频标题和描述信息补全后再提交表单");
			return MSG;
		}
		if(files!=null) {
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendAllVideoMsg(filesFileName,title,description);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	/**
	 * 群发卡券消息(暂不开发)
	 */
	
	/**
	 * 群发删除
	 * 
	 */
	/**
	1、只有已经发送成功的消息才能删除
	2、删除消息是将消息的图文详情页失效，已经收到的用户，还是能在其本地看到消息卡片。
	3、删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	4、如果多次群发发送的是一个图文消息，那么删除其中一次群发，就会删除掉这个图文消息也，导致所有群发都失效
	*/
	@Action(value="delSendAllMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String delSendAllMsg(){
		message("如需删除,请登录微信官方公众号平台进行删除!");
		return MSG;
	}
	
	
	
	/**
	 * 聊天记录  筛选条件 日期 昵称
	 * @throws Exception 
	 */
	@Action(value="getWeChatLog",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatLog.html")})
	public String WeChatLog() throws Exception{
		SearchParam param = new SearchParam().addOrder(OrderType.DESC, "messageTime").addOrder(OrderType.ASC, "userName");
		param.addParam("type", Operator.NOTEQ,3);
		param.addParam("type", Operator.NOTEQ,4);
		String userName = request.getParameter("userName");
		String startTime =  request.getParameter("startTime");
		String endTime =  request.getParameter("endTime");
		if(StringUtils.isNotBlank(userName)){
			userName = new String(userName.getBytes("ISO-8859-1"), "UTF-8");
			param.addParam("userName",userName);
			request.setAttribute("userName", userName);
		}
		if (!StringUtils.isBlank(startTime)) {
			param.addParam("messageTime", Operator.GTE, DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
			request.setAttribute("startTime", startTime);
		}
		if (!StringUtils.isBlank(endTime)) {
			param.addParam("messageTime", Operator.LTE, DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
			request.setAttribute("endTime", endTime);
		}
		PageDataList<WeChatMsg> list = weChatService.getWechatMsg(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "weChatLog");
		return SUCCESS;
	}

	/**
	 * 自动回复页面跳转
	 * @return
	 */
	@Action(value="toMergeWeChatAutoReply",results={@Result(name="success",type="ftl",location="/wechat/wxcms/mergeWeChatAutoReply.html")})
	public String ToAutoReply(){
		String id = request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			weChatAutoReply = weChatService.findWechatARepById(Long.parseLong(id));
			request.setAttribute("weChatAutoReply", weChatAutoReply);
		}
		request.setAttribute("weChatAutoReply",weChatAutoReply);
		request.setAttribute("cur_nav", "autoReply");
		return SUCCESS;
	}
	/**
	 * 自动回复 添加/修改
	 * @return
	 */
	private WeChatAutoReply weChatAutoReply = new WeChatAutoReply();
	
	public WeChatAutoReply getWeChatAutoReply() {
		return weChatAutoReply;
	}

	public void setWeChatAutoReply(WeChatAutoReply weChatAutoReply) {
		this.weChatAutoReply = weChatAutoReply;
	}

	@Action(value="mergeWeChatAutoReply",results={@Result(name="success",type="redirect",location="/wx/getWeChatAutoReply.html")})
	public String addAutoReply(){	
		if(weChatAutoReply.getId()==null){
			weChatAutoReply.setStatus(1);
		}
		weChatService.mergeWeChatAutoReply(weChatAutoReply);
		request.setAttribute("cur_nav", "autoReply");
		return SUCCESS;
	}
	/**
	 * 自动回复 删除
	 * @return
	 */
	@Action(value="delWeChatAutoReply",results={@Result(name="success",type="redirect",location="/wx/getWeChatAutoReply.html")})
	public String delWeChatAutoReply(){
		String id = request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			weChatService.delWeChatAutoReply(Long.parseLong(id));
		}
		request.setAttribute("cur_nav", "autoReply");
		return SUCCESS;
	}
	/**
	 * 自动回复列表
	 * @return
	 */
	@Action(value = "getWeChatAutoReply",results={@Result(name="success",type="ftl",location="/wechat/wxcms/weChatAutoReply.html")})
	public String getWeChatAutoReplys(){
		SearchParam param  = SearchParam.getInstance();
		param.addParam("status", 1);
		PageDataList<WeChatAutoReply> list = weChatService.getWeChatAutoReply(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "autoReply");
		return SUCCESS;
	}
	
	/**
	 * 客服账号管理
	 * 请注意，必须先在公众平台官网为公众号设置微信号后才能使用该能力。(需开通客服功能微信版本号6.0.0以上)
	 */
	@Action(value="toMergeCustomService",results={@Result(name="success",type="redirect",location="/wx/getCustomList.html")})
	public String toMergeCustomService(){
		String id = request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			WeChatKfAccount kfAccount = weChatService.findWeChatKfAccountById(Long.parseLong(id));
			request.setAttribute("kfAccount", kfAccount);
		}
		return SUCCESS;
	}
	
	
	
	
	/**
	 * 客服账号 新增/修改
	 * @return
	 */
	@Action(value="mergeCustomService",results={@Result(name="success",type="redirect",location="/wx/getCustomList.html")})
	public String mergeCustomService(){
		String id = request.getParameter("id");
		WeChatKfAccount kfAccount = new WeChatKfAccount();
		Map parameterMap = request.getParameterMap();
		String msgString = "";
		try {
			BeanUtils.populate(kfAccount, parameterMap);
		}  catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isNotBlank(id)){
			msgString  = weChatService.modifyCustomService(kfAccount);
		}else {
			msgString  = weChatService.createCustomService(kfAccount);
		}
		logger.info("客服管理微信端返回:"+msgString);
		return SUCCESS;
	}

	/**
	 * 删除微信客服
	 * @return
	 */
	
	@Action(value="delCustomService",results={@Result(name="success",type="redirect",location="/wx/getCustomList.html")})
	public String delCustomService(){
		String id = request.getParameter("id");
		String msgString  = weChatService.delCustomService(Long.parseLong(id));
		logger.info("客服管理微信端返回:"+msgString);
		return SUCCESS;
	}
	/**
	 * 设置客服账号的头像
	 */
	File files;
	String filesFileName;
	
	public File getFiles() {
		return files;
	}
	public void setFiles(File files) {
		this.files = files;
	}
	public String getFilesFileName() {
		return filesFileName;
	}
	public void setFilesFileName(String filesFileName) {
		this.filesFileName = filesFileName;
	}
	/**
	 * 可调用本接口来上传图片作为客服人员的头像，头像图片文件必须是jpg格式，推荐使用640*640大小的图片以达到最佳效果。该接口调用请求如下
	 */
	@Action("uploadHeadImg")
	public String uploadHeadImg(){
		//上传客服人员头像将直接上传至微信端服务器,可作为永久素材使用
		String account = request.getParameter("kf_account");
		if(files != null) {
			String path = files.getAbsolutePath();
			if(!filesFileName.endsWith(".jpg")){
				throw new BussinessException("微信端上传头像图片格式不正确");
			}
			String filePath = path + filesFileName;
			logger.info("上传微信客服头像的源路径:"+filePath);
			String msgString = weChatService.uploadHeadImg(filePath,account);
			message(msgString);
			return MSG;
		}
		return ERROR;
	}
	
	/**
	 * 获取所有客服账号
	 */
	@Action(value="getCustomList",results={@Result(name="success",type="ftl",location="/wechat/wxcms/customList.html")})
	public String getCustomList(){
		List<WeChatKfAccount> list = weChatService.getCustomList();
		request.setAttribute("list", list);
		request.setAttribute("cur_nav", "kf");
		return SUCCESS;
	}
	
	/**
	 * 微信管理登录页面跳转
	 * @return
	 */
	@Action(value="showLogin",results={@Result(name="index",type="ftl",location="/wechat/wxcms/index.html"),@Result(name="login",type="ftl",location="/wechat/wxcms/login.html")})
	public String showLogin(){
		WeChatUser user = (WeChatUser) ActionContext.getContext().getSession().get(Constants.WECHAT_USER);
		if(user != null) {
			return "index";
		}
		return "login";
	}
	/**
	 * 微信管理登录
	 * @return
	 */
	@Action(value="doLogin",results={@Result(name="success",type="ftl",location="/wechat/wxcms/index.html")})
	public String doLogin(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String password = (String) request.getParameter("password");
		String username = (String) request.getParameter("username");
		String md5Encode = MD5Util.MD5Encode(password, "utf-8");
		logger.info("当前密码为:"+md5Encode);
		SearchParam param = SearchParam.getInstance().addParam("nickname", username).addParam("remark", md5Encode);
		WeChatUser user = weChatService.doLogin(param);
		if(user == null){
			message("用户名或密码不正确!");
			return MSG;
		}
		HttpSession session = request.getSession();
		session.setAttribute(Constants.WECHAT_USER,user);
		return SUCCESS;
	}
	/**
	 * 微信管理退出登录
	 */
	@Action(value="weChatLogOut",results={@Result(name="success",type="redirect",location="/wx/showLogin.html")})
	public String weChatLogOut(){
		request.getSession().removeAttribute(Constants.WECHAT_USER);
		return SUCCESS;
	}
	
	/**
	 * 预先将图文消息中需要用到的图片，使用上传图文消息内图片接口，上传成功并获得图片URL
	 * 上传图文消息素材，需要用到图片时，请使用上一步获取的图片URL
	 * 建议还是用微信原版的吧,自己处理要考虑的太多
	 * 先将本地图片上传至我们的服务器
	 * 再从我们服务器上传至微信服务器生成url
	 */
	
	/**
	 * 图文消息
	 */
	@Action(value="getGraphicMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/graphicMsg.html")})
	public String getGraphicMessage(){
		int currentPage = paramInt("page");
		SearchParam param = SearchParam.getInstance();
		param.addOrder(OrderType.DESC, "update_time");
		param.addPage(currentPage == 0 ? 1 : currentPage, 10);
		PageDataList<WeChatGraphicMsg> list = weChatService.getWeChatGraphicMsg(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "news");
		return SUCCESS;
	}
	
	/**
	 * 微信图文消息内部图片上传
	 * @return
	 */
	@Action(value="uploadGraphicMsgImage",results={@Result(name="success",type="redirect",location="/wx/getWeChatImage.html")})
	public String uploadGraphicMsgImage(){
		if(files != null){
			String msgString = weChatService.uploadGraphicMsgImage(filesFileName);
			logger.info("微信图片上传返回:"+msgString);
		}
		return SUCCESS;
	}
	@Action(value="getWeChatImage",results={@Result(name="success",type="ftl",location="/wechat/wxcms/wechatImg.html")})
	public String getWeChatImage(){
		SearchParam param = new SearchParam();
		param.addOrder(OrderType.DESC,"createTime");
		PageDataList<WeChatImage> list = weChatService.getWeChatImage(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "image");
		return SUCCESS;
	}
	
	/**
	 * 删除图文消息
	 * @return
	 */
	@Action(value="delGraphicMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/graphicMsg.html")})
	public String delGraphicMsg(){
		String id = paramString("id");
		if(StringUtils.isBlank(id)){
			throw new BussinessException("删除图文消息参数异常");
		}
		String delGraphicMsg = weChatService.delGraphicMsg(Long.parseLong(id));
		message(delGraphicMsg);
		return MSG;
	}
	private WeChatGraphicMsg weChatGraphicMsg = new WeChatGraphicMsg();
	
	public WeChatGraphicMsg getWeChatGraphicMsg() {
		return weChatGraphicMsg;
	}
	public void setWeChatGraphicMsg(WeChatGraphicMsg weChatGraphicMsg) {
		this.weChatGraphicMsg = weChatGraphicMsg;
	}
	/**
	 * 添加/修改 图文消息页面跳转
	 * @return
	 */
	@Action(value="toAddgraphicMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/graphicMsgMerge.html")})
	public String toAddgraphicMsg(){
		if (StringUtils.isNotBlank(paramString("id"))) {
			weChatGraphicMsg = weChatService.findGraphicMsgById(Long.parseLong(paramString("id")));
		}
		request.setAttribute("weChatGraphicMsg", weChatGraphicMsg);
		return SUCCESS;
	}
	/**
	 * 添加/修改图文消息
	 * @return
	 */
	@Action(value="addGraphicMsg",results={@Result(name="success",type="redirect",location="/wx/getGraphicMsg.html")})
	public String addGraphicMsg(){
		logger.info("上传图文消息永久素材");
		//上传图片到我们服务器,生成可以直接访问的图片地址,然后上传到微信端
		String contextPath = request.getContextPath();
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath;
		String realPath = request.getSession().getServletContext().getRealPath("/");
		//读取配置文件上传路径
		PropKit.use("weixin_config.txt");
		String wechatuploadPath = PropKit.get("wechatupload");//上传文件到 /data/wechat/
		if(files != null){
			String tmpPath = UploadUtil.doUpload(realPath,wechatuploadPath,files,filesFileName);
			weChatGraphicMsg.setPicpath(url + tmpPath);
		}else{
			if(weChatGraphicMsg.getId() != null){//更新
				logger.info("微信图文消息修改");
				WeChatGraphicMsg news = weChatService.findGraphicMsgById(weChatGraphicMsg.getId());
				weChatGraphicMsg.setFavourcount(news.getFavourcount());
				weChatGraphicMsg.setReadcount(news.getReadcount());
				weChatGraphicMsg.setUpdate_time(new Date());
				weChatGraphicMsg.setType(news.getType());
				weChatGraphicMsg.setItemCount(news.getItemCount());
				weChatGraphicMsg.setTotalCount(news.getTotalCount());
				weChatGraphicMsg.setEnable(1);
				String msgstring = weChatService.updateWeChatGraphicMsg(weChatGraphicMsg);//更新微信端的
				weChatService.saveWeChatGraphicMsg(weChatGraphicMsg);//更新本地
				message(msgstring);
				return MSG;
			}
		}
		if(!StringUtils.isEmpty(weChatGraphicMsg.getContent_source_url())){
			String fromUrl = weChatGraphicMsg.getContent_source_url();
			if(!fromUrl.startsWith("http://")){
				weChatGraphicMsg.setContent_source_url("http://" + fromUrl);
			}
		}else{
			weChatGraphicMsg.setUrl(url + "/wxweb/msg/newsread.html");//设置微信访问的url
		}
		if(PropKit.getBoolean("synchronized")){
			List<WeChatGraphicMsg> msgNewsList = new ArrayList<WeChatGraphicMsg>();
			msgNewsList.add(weChatGraphicMsg);
			JSONObject rstObj = weChatService.uploadGraphicMsg(msgNewsList);//上传至微信
			logger.info("图文上传微信端返回结果:"+rstObj.toJSONString());
			if(rstObj.containsKey("media_id")){
				logger.info("上传图文素材成功,素材 media_id : " + rstObj.getString("media_id"));
				weChatGraphicMsg.setMedia_id(rstObj.getString("media_id"));
				weChatService.saveWeChatGraphicMsg(weChatGraphicMsg);
				return SUCCESS;
			}else{
				String msString =ReturnCode.get(rstObj.getInteger("errcode"));
				message(msString);
				return MSG;
			}
		}
		return SUCCESS;
	}
	
	
	/**
	 * 同步微信图文消息
	 * @return
	 */
	@Action(value="syncGraphicMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/graphicMsg.html")})
	public String syncGraphicMsg(){
		String msgString = weChatService.syncGraphicMsg();
		message(msgString);
		return MSG;
	}
	
	/**
	 * 用户管理
	 * @throws Exception 
	 */
	@Action(value = "getWeChatUser",results={@Result(name="success",type="ftl",location="/wechat/wxcms/wechatUser.html")})
	public String getUserList() throws Exception{
		SearchParam param = SearchParam.getInstance();
		int currentPage = paramInt("page");
		String nickname = request.getParameter("nickname");
		if(StringUtils.isNotBlank(nickname)){
			nickname = new String(nickname.getBytes("ISO-8859-1"),"UTF-8");//这种方式解决get请求中文乱码太死板了,建议直接在tomcat中server添加urlEncode
			param.addParam("nickname", nickname);
			request.setAttribute("nickname", nickname);
		}
		param.addParam("subscribe", 1);
		param.addPage(currentPage == 0 ? 1 : currentPage, 10);
		param.addParam("openid", Operator.NOTEQ,"admin");
		PageDataList<WeChatUser> list = weChatService.getWeChatUserList(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "fans");
		return SUCCESS;
	}
	
	/**
	 * 同步微信端用户
	 */
	@Action(value = "syncWeChatUserList",results={@Result(name="success",type="ftl",location="/wechat/wxcms/index.html")})
	public String syncWeChatUserList(){
		String msString = weChatService.syncWeChatUserList();
		message(msString);
		return MSG;
	}
	
	/**
	 * 客服消息管理(本地只保存文本消息)
	 */
	
	@Action(value = "getCustomTextMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/customTextMsg.html")})
	public String showCustomTextMsg(){
		int currentPage = paramInt("page");
		SearchParam param = SearchParam.getInstance();
		param.addPage(currentPage == 0?1 : currentPage, 10);
		param.addParam("messageStatus", 0);//未回复的消息
		param.addParam("type", 1);//接收到的消息
		PageDataList<WeChatMsg> list = weChatService.getWechatMsg(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "text");
		return SUCCESS;
	}
	
	/**
	 * 微信回复客服消息页面跳转
	 * @return
	 */
	@Action(value = "toReplyMessage",results={@Result(name="success",type="ftl",location="/wechat/wxcms/sendmsg.html")})
	public String toReplyMessage(){
		long id = paramLong("id");
		String openid = request.getParameter("openid");
		if(StringUtils.isNotBlank(openid)){
			weChatMsg = new WeChatMsg();
			weChatMsg.setOpenid(openid);
			WeChatUser user = weChatService.findWeChatUserById(openid);
			if(user == null) {
				throw new BussinessException("该用户不存在!");
			}
			weChatMsg.setUserName(user.getNickname());
			return SUCCESS;
		}
		if(id!= 0){
			weChatMsg = weChatService.getWechatMsgById(id);
			return SUCCESS;
		}
		return ERROR;
	}
	private WeChatMsg weChatMsg = new WeChatMsg();
	
	public WeChatMsg getWeChatMsg() {
		return weChatMsg;
	}

	public void setWeChatMsg(WeChatMsg weChatMsg) {
		this.weChatMsg = weChatMsg;
	}

	/**
	 * 发送微信客服文本消息
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomTextMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomTextMsg() throws Exception{
		WeChatMsg weChatMsg = new WeChatMsg();
		Map parameterMap = request.getParameterMap();
		try {
			long id = paramLong("msgid");
			if(id != 0){
				WeChatMsg msg = weChatService.getWechatMsgById(id);
				msg.setMessageStatus(1);//已回复
				weChatService.updateWeChatMsg(msg);
			}
			BeanUtils.populate(weChatMsg, parameterMap);
			String msgString = weChatService.sendCustomTextMsg(weChatMsg);
			logger.info("发送客服消息微信端返回:"+msgString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	

	/**
	 * 发送微信客服图片消息(发送图片消息本地不存储,不记录)
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomImgMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomImgMsg() throws Exception{
		String openid = request.getParameter("openid");
		if(files != null && StringUtils.isNotBlank(openid)){
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendCustomImgMsg(filesFileName,openid);
			logger.info("发送客服消息微信端返回:"+msgString);
		}
		
		long id = paramLong("msgid");
		if(id != 0){
			WeChatMsg msg = weChatService.getWechatMsgById(id);
			msg.setMessageStatus(1);//已回复
			weChatService.updateWeChatMsg(msg);
			}
		
		return SUCCESS;
	}
	
	/**
	 * 发送微信客服语音消息(发送语音消息本地不存储,不记录)
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomVoiceMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomVoiceMsg() throws Exception{
		String openid = request.getParameter("openid");
		if(files != null && StringUtils.isNotBlank(openid)){
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendCustomVoiceMsg(filesFileName,openid);
			logger.info("发送客服消息微信端返回:"+msgString);
		}
		long id = paramLong("msgid");
		if(id != 0){
			WeChatMsg msg = weChatService.getWechatMsgById(id);
			msg.setMessageStatus(1);//已回复
			weChatService.updateWeChatMsg(msg);
			}
		return SUCCESS;
	}
	
	/**
	 * 发送微信客服视频消息(发送视频消息本地不存储,不记录)
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomVideoMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomVideoMsg() throws Exception{
		String openid = request.getParameter("openid");
		if(files != null && StringUtils.isNotBlank(openid)){
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendCustomVideoMsg(filesFileName,openid);
			logger.info("发送客服消息微信端返回:"+msgString);
		}
		long id = paramLong("msgid");
		if(id != 0){
			WeChatMsg msg = weChatService.getWechatMsgById(id);
			msg.setMessageStatus(1);//已回复
			weChatService.updateWeChatMsg(msg);
			}
		return SUCCESS;
	}
	
	/**
	 * 发送微信客服音乐消息(发送音乐消息本地不存储,不记录)
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomMusicMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomMusicMsg() throws Exception{
		String openid = request.getParameter("openid");
		if(files != null && StringUtils.isNotBlank(openid)){
			String path = files.getAbsolutePath();
			filesFileName = filesFileName+path;
			String msgString = weChatService.sendCustomMusicMsg(filesFileName,openid);
			logger.info("发送客服消息微信端返回:"+msgString);
		}
		long id = paramLong("msgid");
		if(id != 0){
			WeChatMsg msg = weChatService.getWechatMsgById(id);
			msg.setMessageStatus(1);//已回复
			weChatService.updateWeChatMsg(msg);
			}
		return SUCCESS;
	}
	
	/**
	 * 发送微信客服图文消息
	 * @return
	 * @throws Exception
	 */
	@Action(value = "sendCustomNewsMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String sendCustomNewsMsg() throws Exception{
		String openid = request.getParameter("openid");
		String graphicMsgId = request.getParameter("graphicMsgId");
		if(StringUtils.isNotBlank(graphicMsgId) && StringUtils.isNotBlank(openid)){
			WeChatGraphicMsg weChatMsg = weChatService.findGraphicMsgById(Long.parseLong(graphicMsgId));
			String msgString = weChatService.sendCustomNewsMsg(weChatMsg,openid);
			logger.info("发送客服消息微信端返回:"+msgString);
		}
		long id = paramLong("msgid");
		if(id != 0){
			WeChatMsg msg = weChatService.getWechatMsgById(id);
			msg.setMessageStatus(1);//已回复
			weChatService.updateWeChatMsg(msg);
		}
		return SUCCESS;
	}
	
	
	/**
	 * 删除微信文本消息
	 * @return
	 */
	@Action(value = "delWeChatMsg",results={@Result(name="success",type="redirect",location="/wx/getCustomTextMsg.html")})
	public String delTextMsg(){
		String id = request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			weChatService.delWeChatMsg(Long.parseLong(id));
		}
		return SUCCESS;
	}
	/**
	 * 客服回复文本消息模板
	 * @return
	 */
	@Action(value = "getCustomTMMsg",results={@Result(name="success",type="ftl",location="/wechat/wxcms/kfMsg.html")})
	public String getCustomTMMsg(){
		int currentPage = paramInt("page");
		SearchParam param = SearchParam.getInstance().addPage(currentPage == 0 ? 1 : currentPage, 10);
		param.addParam("type", 4);// type = 4 模板消息
		//获取所有的文本消息;type=3 自动回复消息使用
		PageDataList<WeChatMsg> textList = weChatService.getWechatMsg(param);
		request.setAttribute("textList", textList.getList());
		request.setAttribute("textpage", textList.getPage());
		request.setAttribute("param", param.toMap());
		request.setAttribute("pageUrl", request.getRequestURL().toString());
		return SUCCESS;
	}
	
	/**
	 * 菜单管理
	 */
	private WeChatMenu weChatMenu;
	private WeChatMenuGroup weChatMenuGroup;
	private String gid;
	
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public WeChatMenuGroup getWeChatMenuGroup() {
		return weChatMenuGroup;
	}
	public void setWeChatMenuGroup(WeChatMenuGroup weChatMenuGroup) {
		this.weChatMenuGroup = weChatMenuGroup;
	}
	public WeChatMenu getWeChatMenu() {
		return weChatMenu;
	}
	public void setWeChatMenu(WeChatMenu weChatMenu) {
		this.weChatMenu = weChatMenu;
	}
	/**
	 * 微信端管理页面
	 * @return
	 */
	@Action(value = "index",results={@Result(name="success",type="ftl",location="/wechat/wxcms/index.html")})
	public String showIndex(){
		return SUCCESS;
	}
	/**
	 * 添加菜单组页面跳转
	 * @return
	 */
	@Action(value="toAddMenuGroup",results={@Result(name="success",type="ftl",location="/wechat/wxcms/menuList.html")})
	public String toAddMenuGroup(){
		request.setAttribute("weChatMenuGroup",new WeChatMenuGroup());
		request.setAttribute("list", new ArrayList<WeChatMenu>());
		request.setAttribute("cur_nav", "menu");
		return SUCCESS;
	}
	/**
	 * 添加菜单组
	 * @return
	 */
	@Action(value="addMenuGroup",results={@Result(name="success",type="redirect",location="/wx/showMenu.html?gid=${gid}")})
	public String addMenuGroup(){
		if(weChatMenuGroup.getId() == null){
			weChatMenuGroup.setCreateTime(new Date());
			weChatMenuGroup.setEnable(0);
			weChatService.saveMenuGroup(weChatMenuGroup);
		}else {
			weChatService.updateMenuGroup(weChatMenuGroup);
		}
		setGid(weChatMenuGroup.getId()+"");
		return SUCCESS;
	}
	/**
	 * 添加菜单页面跳转
	 * @return
	 */
	@Action(value="toAddMenu",results={@Result(name="success",type="ftl",location="/wechat/wxcms/menuMerge.html")})
	public String toAddMenu(){
		SearchParam param = SearchParam.getInstance().addParam("parentid", 0L).addParam("gid", gid);
		List<WeChatMenu> parentMenu = weChatService.getParentMenu(param);
		request.setAttribute("parentMenu", parentMenu);
		request.setAttribute("cur_nav", "menu");
		return SUCCESS;
	}
	/**
	 * 添加菜单
	 * @return
	 */
	@Action(value="addMenu",results={@Result(name="success",type="redirect",location="/wx/showMenu.html?gid=${gid}")})
	public String addMenu(){
		weChatMenu.setCreateTime(new Date());
		weChatMenu.setGid(paramLong("gid"));
		if(weChatMenu.getParentid() != null){
			WeChatMenu parentMenu = weChatService.findWeChatMenuById(weChatMenu.getParentid());
			if(parentMenu != null) {
				weChatMenu.setParentName(parentMenu.getName());
			}
		}
		weChatService.saveMenu(weChatMenu);
		return SUCCESS;
	}
	
	/**
	 * 获取微信菜单组
	 * @return
	 */
	@Action(value="getMenuGroup",results={@Result(name="success",type="ftl",location="/wechat/wxcms/menuGroup.html")})
	public String getMenuGroup(){
		int currentPage = paramInt("page");
		SearchParam param = SearchParam.getInstance();
		param.addPage(currentPage == 0 ? 1 : currentPage, 5);
		PageDataList<WeChatMenuGroup> list = weChatService.getMenuGroup(param);
		setPageAttribute(list, param);
		request.setAttribute("cur_nav", "menu");
		return SUCCESS;
	}
	/**
	 * 显示当前所有菜单信息
	 * @return
	 */
	@Action(value="showMenu",results={@Result(name="success",type="ftl",location="/wechat/wxcms/menuList.html")})
	public String showMenu(){
	long id = Long.parseLong(gid);
	int currentPage = paramInt("page");
	SearchParam param = SearchParam.getInstance();
	param.addPage(currentPage == 0 ? 1 : currentPage, 5);
	param.addParam("gid", id);
	weChatMenuGroup = weChatService.findWeChatMenuGroupById(id);
	request.setAttribute("weChatMenuGroup", weChatMenuGroup);
	PageDataList<WeChatMenu> list = weChatService.showMenu(param);
	setPageAttribute(list, param);
	request.setAttribute("cur_nav", "menu");
	return SUCCESS;
	}
	/**
	 * 添加当前菜单组配置到微信公众号端
	 * @return
	 */
	@Action(value="addWeChatMenu",results={@Result(name="success",type="ftl",location="/wechat/common/success.html")})
	public String addWeChatMenu(){
		String gid = request.getParameter("gid");
		String msgString = weChatService.addWeChatMenu(gid);
		request.setAttribute("successMsg",msgString);
		return SUCCESS;
	}
	
	/**
	 * 删除微信公众号端菜单配置
	 * @return
	 */
	@Action(value="delWechatMenu",results={@Result(name="success",type="ftl",location="/wechat/common/success.html")})
	public String delWechatMenu(){
		String msgString = weChatService.delWechatMenu();
		request.setAttribute("successMsg",msgString);
		return SUCCESS;
	}
	/**
	 * 删除微信菜单
	 * @return
	 */
	@Action(value="delMenu",results={@Result(name="success",type="ftl",location="/wechat/common/success.html")})
	public String delMenu(){
		long id = paramLong("id");
		if(id == 0){
			throw new BussinessException("系统出错,请联系管理员");
		}
		weChatService.delMenu(id);
		request.setAttribute("successMsg","删除成功");
		return SUCCESS;
	}
	/**
	 * 删除微信菜单组
	 * @return
	 */
	@Action(value="delMenuGroup",results={@Result(name="success",type="redirect",location="/wx/getMenuGroup.html")})
	public String delMenuGroup(){
		long id = paramLong("id");
		weChatService.delMenuGroup(id);
		request.setAttribute("cur_nav", "menu");
		return SUCCESS;
	}
	/**
	 * 获取微信端菜单配置所需要的固定消息,以及图文消息,在数据库中配置
	 */
	@Action(value="getMenuMsgs",results={@Result(name="success",type="ftl",location="/wechat/wxcms/menuMsgs.html")})
	public String getMenuMsgs(){
		int currentPage = paramInt("page");
		SearchParam param1 = SearchParam.getInstance().addPage(currentPage == 0 ? 1 : currentPage, 10);
		SearchParam param2 = SearchParam.getInstance().addPage(currentPage== 0 ? 1 : currentPage, 10);
		param1.addParam("type", 3);
		param2.addParam("type", 3);
		//获取所有的图文消息;
		PageDataList<WeChatGraphicMsg> newsList = weChatService.getWeChatGraphicMsg(param1);
		//获取所有的文本消息;type=3 自动回复消息使用
		PageDataList<WeChatMsg> textList = weChatService.getWechatMsg(param2);
		setPageAttribute(newsList, param1);
		setPageAttribute2(textList, param2);
		request.setAttribute("newsList", newsList.getList());
		request.setAttribute("textList", textList.getList());
		request.setAttribute("newspage", newsList.getPage());
		request.setAttribute("textpage", textList.getPage());
		request.setAttribute("param1", param1.toMap());
		request.setAttribute("param2", param2.toMap());
		request.setAttribute("pageUrl", request.getRequestURL().toString());
		return SUCCESS;
	}
	/**
	 * 获取access_token
	 */
	@Action(value = "getAccess_token",results={@Result(name="success",type="redirect",location="/wx/index.html")})
	public String getAccess_token(){
		String accessToken = AccessTokenKit.getAccessToken().getAccessToken();
		logger.info("刷新当前access_token:"+accessToken);
		context.setAttribute("access_token", accessToken);
		return SUCCESS;
	}
	
	/**
	 * 微信接入,处理推送消息
	 */
	@Action("msg")
	public void getWeiXinMsg() {
		logger.info("开始处理微信接入");
		if("get".equalsIgnoreCase(request.getMethod())){
			logger.info("启动微信接入服务");
			weChatService.handlerConfig(request, response);
		}
		if("post".equalsIgnoreCase(request.getMethod())){
			logger.info("启动消息事件服务");
			weChatService.handlerMsg(request, response);
		}
	}
	static Logger logger = Logger.getLogger(WeChatAction.class); 
	@Autowired
	private	WeChatService weChatService;
}
		