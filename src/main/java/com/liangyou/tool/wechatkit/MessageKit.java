package com.liangyou.tool.wechatkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Constant;
import com.liangyou.domain.WeChatMenu;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.wechatModel.WeChatMatchrule;
import com.liangyou.model.wechatModel.message.inmsg.InMsg;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;

/**
 * @author lijing 解析微信发来的请求(xml)
 */
public class MessageKit {
	public static Logger logger = Logger.getLogger(MessageKit.class); 
	public static Map<String, String> parseXml(HttpServletRequest request)
			throws Exception {
		logger.info("开始解析微信端xml数据:");
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList){
			if(e.getName().equals("MsgType") || e.getName().equals("Event")){
				map.put(StringUtils.captureName(e.getName(), "lower"), e.getText().toLowerCase());
			}else {
				map.put(StringUtils.captureName(e.getName(), "lower"), e.getText());
			}
				
		}
		// 释放资源
		inputStream.close();
		inputStream = null;
		return map;
	}

	/**处理接收到的消息,封装到不同的实体类 
	 * @param requestMap
	 * 消息类型:text,image,voice,video,shortvideo,location,link
	 * 事件类型:subscribe,unsubscribe,SCAN,LOCATION,CLICK,VIEW
	 * @throws Exception 
	 */
	public static InMsg processingMsg(Map<String, String> requestMap) throws Exception {
		logger.info("开始解析微信端推送消息:"+requestMap.toString());
		String msgType = requestMap.get("msgType");
		logger.info("当前解析消息类型为:"+msgType);
		//根据类型封装到不同的实体类 组建实体
		String className = null;
		if(!msgType.equals("event")){
			msgType = StringUtils.captureName(msgType, "upper");
			className = new StringBuilder().append(Constant.WECHATMSG_PATH_PREFIX).append(msgType).append("Msg").toString();
		}else {
			msgType = StringUtils.captureName(requestMap.get("event"), "upper");
			if(msgType.equals("subscribe") || msgType.equals("unsubscribe")){
					//扫描带参数二维码事件
				if(StringUtils.isBlank(requestMap.get("ticket"))){
					//扫一扫关注(取消关注)事件
					className = new StringBuilder().append(Constant.WECHATEVENT_PATH_PREFIX).append(msgType).append("Event").toString();
				} else {
					//用户未关注时，进行关注后的扫描二维码事件推送
					className = new StringBuilder().append(Constant.WECHATEVENT_PATH_PREFIX).append("ScanEvent").toString();
				}
			}else {
				className = new StringBuilder().append(Constant.WECHATEVENT_PATH_PREFIX).append(msgType).append("Event").toString();
			}
			
		}
		
		Class clazz = Class.forName(className);
		Object object = clazz.newInstance();
		BeanUtils.populate(object, requestMap);
		return (InMsg) object;
		}

	/**
	 * 创建菜单
	 * @param menus
	 * @param weChatMatchrule
	 * @return
	 */
	public static String parseMenu(List<WeChatMenu> menus,WeChatMatchrule weChatMatchrule) {
		logger.info("配置微信菜单");
		if(!CollectionUtils.isEmpty(menus)){
			List<WeChatMenu> pMenu = new ArrayList<WeChatMenu>();
			Map<Long,List<JSONObject>> subMenu = new HashMap<Long,List<JSONObject>>();
			for(WeChatMenu m : menus){
				if(m.getParentid() == 0L){//一级菜单
					pMenu.add(m);
				}else{//二级菜单
					if(subMenu.get(m.getParentid()) == null){
						subMenu.put(m.getParentid(), new ArrayList<JSONObject>());
					}
					List<JSONObject> tmpMenus = subMenu.get(m.getParentid());
					tmpMenus.add(getMenuJSONObj(m));
					subMenu.put(m.getParentid(), tmpMenus);
				}
			}
			JSONArray arr = new JSONArray();
			for(WeChatMenu m : pMenu){
				if(subMenu.get(m.getId()) != null){//有子菜单
					arr.add(getParentMenuJSONObj(m,subMenu.get(m.getId())));
				}else{//没有子菜单
					arr.add(getMenuJSONObj(m));
				}
			}
			JSONObject root = new JSONObject();
			root.put("button", arr);
			root.put("matchrule", JSON.parseObject(JSON.toJSONString(weChatMatchrule)));
			return root.toJSONString();
		}
		return "error";
	}

	private static Object getParentMenuJSONObj(WeChatMenu menu,List<JSONObject> subMenu) {
		JSONObject obj = new JSONObject();
		obj.put("name", menu.getName());
		obj.put("sub_button", subMenu);
		return obj;
	}

	private static JSONObject getMenuJSONObj(WeChatMenu menu) {
		JSONObject obj = new JSONObject();
		obj.put("name", menu.getName());
		obj.put("type", menu.getMtype());
		if("click".equals(menu.getMtype())){//事件菜单
			if("fix".equals(menu.getEventType())){//fix 消息
				obj.put("key", "_fix_" + menu.getMsgId());//以 _fix_ 开头
			}else{
				if(StringUtils.isEmpty(menu.getInputcode())){//如果inputcode 为空，默认设置为 subscribe，以免创建菜单失败
					obj.put("key", "subscribe");
				}else{
					obj.put("key", menu.getInputcode());//关键字 可以用于与用户交互使用
				}
			}
		}else{//链接菜单-view
			obj.put("url", menu.getUrl());
		}
		return obj;
	}

	/**
	 * 上传
	 * @param token
	 * @param string
	 * @param picpath
	 * @return
	 */
	//String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="+ACCESS_TOKEN+"&type=%s";三天后自动删除
	//https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN  post请求
	//https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+ACCESS_TOKEN+"&type=%s" 占用使用次数
	public static JSONObject upLoadMedia(String url,String mediaType,String mediaPath) {
		String uploadMediaUrl = String.format(url,mediaType);
		String boundary = "----------" + System.currentTimeMillis();// 设置边界  
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod("POST");
			// 设置请求头Content-Type
			uploadConn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();
			URL mediaUrl = new URL(mediaPath);
			HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
			meidaConn.setDoOutput(true);
			meidaConn.setRequestMethod("GET");

			// 从请求头中获取内容类型
			String contentType = meidaConn.getHeaderField("Content-Type");
			// 根据内容类型判断文件扩展名
			String fileType = FileType.getFileType(mediaPath);
			logger.info("当前上传多媒体类型为:"+fileType);
			if(fileType == null) {
				throw new BussinessException("微信端获取多媒体类型出错请联系系统管理员!");
			}
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n",fileType).getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n",contentType).getBytes());
			
			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();
			meidaConn.disconnect();

			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();
			//解析返回结果
			JSONObject jsonObject = JSON.parseObject(buffer.toString());
			logger.info("上传多媒体文件返回信息:"+jsonObject.toJSONString());
			return jsonObject;
		} catch (Exception e) {
			String error = String.format("上传媒体文件失败：%s", e);
			System.out.println(error);
		} 
		return null;
	}

	public static String downLoadMedia(String download,String access_token,String mediaId,String format,String realPath) {
		// 微信端多媒体文件下载
		/*String upload = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		String download = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";*/
		String downloadMediaUrl = String.format(download,access_token,mediaId);
		logger.info("微信多媒体文件下载地址:"+downloadMediaUrl);
		try {
			URL downloadUrl = new URL(downloadMediaUrl);
			InputStream inputStream = downloadUrl.openStream();
			String weChatMediaPath = PropKit.get("weChatMediaPath");
			String fileName = realPath+weChatMediaPath+DateUtils.dateStr3(new Date())+"."+format;
			File file = new File(fileName);//微信端多媒体文件下载存放路径
			logger.info("微信多媒体文件路径:"+fileName);
			if(!file.exists()){
				file.getParentFile().mkdirs();
			}
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf = new byte[1024*8];
			int len = 0;
			while((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf,0,len);
			}
			outputStream.close();
			inputStream.close();
			return weChatMediaPath+DateUtils.dateStr3(new Date())+"."+format;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
