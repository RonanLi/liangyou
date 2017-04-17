package com.liangyou.web.action;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Namespaces;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Constant;
import com.liangyou.domain.Area;
import com.liangyou.domain.User;
import com.liangyou.service.UserinfoService;
import com.liangyou.tool.interest.EndInterestCalculator;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.tool.interest.MonthEqualCalculator;
import com.liangyou.tool.interest.MonthInterestCalculator;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

/**
 * 工具类Action,验证码、生产图片
 * 
 * @author fuxingxing
 *
 */
@Namespaces({ @Namespace("/"), @Namespace("/tools"), @Namespace("/plugins/ueditor/") })
@ParentPackage("p2p-default")
public class ToolAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ToolAction.class);

	// imgurl action params
	private String userid;
	private String size;

	// interest action params
	private double account;
	private double lilv;
	private int times;
	private int time_limit_day;
	private String type;

	private File upload;
	private String uploadFileName;
	private String sep = File.separator;

	// 裁剪后的图像大小
	private double cropX;
	private double cropY;
	private double cropW;
	private double cropH;

	private String plugintype;

	@Autowired
	private UserinfoService userinfoService;

	/**
	 * 动态输出图像Action
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("imgurl")
	public String imgurl() throws Exception {
		if (NumberUtils.getInt(userid) < 1) {
			userid = NumberUtils.getInt(userid) + "";
		}
		userid = (userid == null || userid.equals("")) ? "" : userid;
		size = (size == null || size.equals("")) ? "" : size;
		String[] sizes = { "big", "middle", "small" };
		List<String> sizelist = Arrays.asList(sizes);
		size = sizelist.contains(size) ? size : "big";

		String url = "data" + sep + "avatar" + sep + userid + "_avatar_" + size + ".jpg";
		String contextPath = ServletActionContext.getServletContext().getRealPath("/");
		url = contextPath + url;
		File avatarFile = new File(url);
		if (!avatarFile.exists()) {
			url = contextPath + sep + "data" + sep + "images" + sep + "avatar" + sep + "noavatar_" + size + ".gif";
		}
		logger.debug("IMG_URL:" + url);
		cteateImg(url);
		return null;
	}

	@Action("editorUploadImg")
	public String editorUploadImg() throws Exception {
		String newFileName = generateUploadFilename();
		String retMsg = "";
		if (upload == null) {
			retMsg = getRetMsg(1, "上传图片成功！", "");
		}
		if (StringUtils.isBlank(retMsg)) {
			String imgUrl = upload(upload, uploadFileName, "/data/upload", newFileName + ".jpg");
			retMsg = getRetMsg(0, "上传图片成功！", imgUrl);
		}

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(retMsg);
		out.flush();
		out.close();
		return null;
	}

	private String getRetMsg(int flag, String msg, String imgUrl) {
		JSONObject obj = new JSONObject();
		obj.put("error", flag);
		obj.put("message", msg);
		obj.put("url", request.getContextPath() + imgUrl);
		return obj.toString();
	}

	/*
	 * public String upload() throws Exception { Map map=ServletActionContext.getContext().getSession(); User user=(User)map.get(Constant.SESSION_USER); logger.info("文件："+this.upload); logger.info("文件名："+uploadFileName); PrintWriter out = response.getWriter(); if(!checkIsImage(this.upload)){ return null; } Date d=new Date(); String destfilename=ServletActionContext.getServletContext().getRealPath( "/data" ) +sep+"temp"+sep+user.getUserId()+"_"+d.getTime()+".jpg"; String destfilename2=ServletActionContext.getServletContext().getRealPath( "/data" ) +sep+"temp"+sep+user.getUserId()+".jpg"; logger.info(destfilename); logger.info(destfilename2); File imageFile=null; File imageFile2=null; try { imageFile = new File(destfilename); imageFile2 = new File(destfilename2); } catch (Exception e) { logger.error(e); logger.error(e.getMessage()); } FileUtils.copyFile(upload, imageFile); FileUtils.copyFile(upload, imageFile2); HttpServletResponse response = ServletActionContext.getResponse(); HttpServletRequest request = ServletActionContext.getRequest(); response.setContentType("text/html;charset=UTF-8"); //保存上传的临时图片名称 String src=request.getContextPath()+"/data/temp/"+user.getUserId()+"_"+d.getTime()+".jpg"; logger.debug("Print img:"+src); out.print("{"); out.print("error: '',\n"); out.print("msg: '"+src+ "'\n"); out.print("}"); out.flush(); out.close(); return null; }
	 */

	@Action(value = "upload")
	public String upload() throws Exception {
		Map map = ServletActionContext.getContext().getSession();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String retMsg = "";
		User user = (User) map.get(Constant.SESSION_USER);
		logger.info("文件：" + this.upload);
		logger.info("文件名：" + uploadFileName);
		Date d = new Date();
		String destfilename2 = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
		logger.info(destfilename2);
		File imageFile2 = null;
		try {

			imageFile2 = new File(destfilename2);
			long a = upload.length();
			if (a > 1048576) {
				retMsg = getRetMsg(1, "你上传的图片大于1M，请更换成小于1M的图片！", "");
				out.print(retMsg);
				out.flush();
				out.close();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		FileUtils.copyFile(upload, imageFile2);
		try {

			BufferedImage bi = ImageIO.read(imageFile2);
			if (bi == null) {
				logger.info("你输入的文件为无效图片，即图片文件不合法");
				retMsg = getRetMsg(1, "你输入的文件为无效图片，即图片文件不合法！", "");
				out.print(retMsg);
				out.flush();
				out.close();
				return null;
			} else {
				logger.info("你输入的文件为有效图片");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 保存上传的临时图片名称
		String src = request.getContextPath() + "/data/temp/" + user.getUserId() + ".jpg";
		BufferedImage srcImage = null;
		BufferedImage destImage = null;
		File file = new File(destfilename2);
		int newWi = 0;
		int newHi = 0;
		try {
			srcImage = ImageIO.read(file);
			int w = srcImage.getWidth();
			int h = srcImage.getHeight();
			int minW = (w > h) ? w : h;
			double newWd = (300.0 / minW) * w;
			double newHd = (300.0 / minW) * h;
			newWi = (int) newWd;
			newHi = (int) newHd;
			destImage = new BufferedImage(newWi, newHi, BufferedImage.TYPE_3BYTE_BGR);
			destImage.getGraphics().drawImage(srcImage.getScaledInstance(newWi, newHi, Image.SCALE_SMOOTH), 0, 0, null);
			ImageIO.write(destImage, "jpg", new File(destfilename2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Print img:" + src);
		// out.print(src);
		out.print("{");
		out.print("error: '',\n");
		out.print("msg: '" + src + "',\n");
		out.print("width: " + newWi + ",\n");
		out.print("height: " + newHi + "\n");
		out.print("}");
		out.flush();
		out.close();
		return null;
	}

	// 生成需要裁剪的照片
	@Action("cropimg")
	public String cropimg() throws Exception {
		try {
			Map map = ServletActionContext.getContext().getSession();
			User user = (User) map.get(Constant.SESSION_USER);
			String destfilename = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
			logger.info(destfilename);
			this.cteateImg(destfilename);
		} catch (Exception e) {
			logger.info(e.getMessage());
			logger.error(e);
		}
		return null;
	}

	// 保存头像
	@Action("saveAvatar")
	public String saveAvatar() throws Exception {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/json;charset=UTF-8");
			int x = 0, y = 0, w = 0, h = 0;
			if (cropX >= 0) {
				x = (int) cropX;
			}
			if (cropY >= 0) {
				y = (int) cropY;
			}
			if (cropW >= 0) {
				w = (int) cropW;
			}
			if (cropH >= 0) {
				h = (int) cropH;
			}

			logger.debug("X=" + x + ",Y=" + y + ",W=" + w + ",H=" + h);
			@SuppressWarnings("unused")
			boolean re = operateImg(x, y, w, h);
			Map map = new HashMap();
			if (re) {
				map.put("flag", 1);
				map.put("msg", "success");
				map.put("useravatar", "");
				String json = JSON.toJSONString(map, true);
				printJson(json);
			} else {
				map.put("flag", 0);
				map.put("msg", "success");
				map.put("useravatar", "");
				String json = JSON.toJSONString(map, true);
				printJson(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	private boolean operateImg(int x, int y, int w, int h) throws IOException {
		Map map = ServletActionContext.getContext().getSession();
		User user = (User) map.get(Constant.SESSION_USER);
		String dataPath = ServletActionContext.getServletContext().getRealPath("/data");
		String avatarDir = dataPath + sep + "avatar";
		String dest = avatarDir + sep + user.getUserId() + "_avatar_middle.jpg";
		String smalldest = avatarDir + sep + user.getUserId() + "_avatar_small.jpg";
		String src = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
		BufferedImage srcImage = null;
		try {
			srcImage = ImageIO.read(new File(src));
			BufferedImage destImage = srcImage.getSubimage(x, y, w, h);
			BufferedImage lastImage = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
			lastImage.getGraphics().drawImage(destImage, 0, 0, null);
			File avatarDirFile = new File(avatarDir);
			if (!avatarDirFile.exists()) {
				avatarDirFile.mkdir();
			}
			File newFile = new File(dest);
			File smallNewFile = new File(smalldest);
			logger.info("Avatar dest:" + dest);
			ImageIO.write(lastImage, "jpg", newFile);
			ImageIO.write(lastImage, "jpg", smallNewFile);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Action("showarea")
	public String showArea() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pid = (String) req.getParameter("pid");
		List<Area> areas = userinfoService.getAreaListByPid(NumberUtils.getInt(pid));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

	/**
	 * 以图片流形式输出
	 * 
	 * @param url
	 * @throws IOException
	 */
	private void cteateImg(String url) throws IOException {
		HttpServletResponse res = ServletActionContext.getResponse();
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "no-cache");
		res.setDateHeader("Expires", 0);
		OutputStream output = res.getOutputStream();// 得到输出流
		if (url.toLowerCase().endsWith(".jpg")) {
			// 表明生成的响应是图片
			res.setContentType("image/jpeg");
		} else if (url.toLowerCase().endsWith(".gif")) {
			res.setContentType("image/gif");
		}
		InputStream imageIn = new FileInputStream(new File(url));
		BufferedInputStream bis = new BufferedInputStream(imageIn);// 输入缓冲流
		BufferedOutputStream bos = new BufferedOutputStream(output);// 输出缓冲流
		byte data[] = new byte[4096];// 缓冲字节数
		int size = 0;
		size = bis.read(data);
		while (size != -1) {
			bos.write(data, 0, size);
			size = bis.read(data);
		}
		bis.close();
		bos.flush();// 清空输出缓冲流
		bos.close();
		output.flush();
		output.close();
	}

	/**
	 * 计算利息的Action
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "interest", results = { @Result(name = "success", type = "ftl", location = "/tools/index.html") })
	public String interest() throws Exception {
		String toolType = StringUtils.isNull(type);
		if (!StringUtils.isBlank(toolType)) {
			// v1.8.0.4_u1 TGPROJECT-170 qj 2014-04-25 start
			if (!toolType.equals("dayEndInterest") && !toolType.equals("phone") && !toolType.equals("ip")) {
				if (times > 100) {
					message("借款期限不能大于100个月", "");
					return MSG;
				}
			}
			// v1.8.0.4_u1 TGPROJECT-170 qj 2014-04-25 end
			if (toolType.equals("month")) {
				if (account <= 0 || times <= 0 || lilv <= 0) {
					message("输入的数据有误！", "");
					return MSG;
				}
				InterestCalculator ic = new MonthEqualCalculator(account, lilv / 100, times);
				// 循环每期的利息
				ic.each();
				request.setAttribute("ic", ic);
			} else if (toolType.equals("monthInterest")) {
				if (account <= 0 || times <= 0 || lilv <= 0) {
					message("输入的数据有误！", "");
					return MSG;
				}
				InterestCalculator ic = new MonthInterestCalculator(account, lilv / 100, times);
				// 循环每期的利息
				ic.each();
				request.setAttribute("ic", ic);
			} else if (toolType.equals("dayEndInterest")) {
				if (account <= 0 || time_limit_day <= 0 || lilv <= 0) {
					message("输入的数据有误！", "");
					return MSG;
				}
				InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, time_limit_day);
				// 循环每期的利息
				ic.each();
				request.setAttribute("ic", ic);
			} else if (toolType.equals("endMonthInterestCalculator")) {
				if (account <= 0 || times <= 0 || lilv <= 0) {
					message("输入的数据有误！", "");
					return MSG;
				}
				InterestCalculator ic = new EndInterestCalculator(account, lilv / 100, times, InterestCalculator.TYPE_MONTH_END);
				ic.each();
				request.setAttribute("ic", ic);
			} else if (toolType.equals("phone")) {
			} else if (toolType.equals("ip")) {
				String area = getAreaByIp(StringUtils.isNull(request.getParameter("ip")));
				request.setAttribute("ip", area);
			}
			request.setAttribute("type", toolType);
		}
		if ("invest".equals(paramString("calc"))) {
			request.setAttribute("nid", "invest");
		} else {
			request.setAttribute("nid", "borrow");
		}

		request.setAttribute("account", account);
		request.setAttribute("lilv", lilv);
		request.setAttribute("times", times);
		request.setAttribute("time_limit_day", time_limit_day);
		request.setAttribute("type", type);

		return SUCCESS;
	}

	/**
	 * 显示插件Action
	 * 
	 * @return
	 * @throws Exception
	 */
	public String plugin() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		return null;
	}

	/**
	 * 输出验证码
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("validimg")
	public String validimg() throws Exception {
		genernateCaptchaImage();
		return null;
	}

	/**
	 * wap端Ajax方式获取验证码 
	 * add by gy 2016-9-21
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("ajaxValidimg")
	public String ajaxValidimg() throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		String base64 = ajaxGenernateCaptchaImage();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("valiCodeImg", base64);
		super.getJson().setObj(dataMap);
		super.writeJson(super.getJson());
		return null;
	}

	/**
	 * 输出qq图像
	 * 
	 * @return
	 */
	@Action(value = "qqImages")
	public String qqImages() {
		String qq = paramString("qq");
		String aaUrl = "http://wpa.qq.com/pa?p=2:" + qq + ":45";
		// new一个URL对象
		try {
			URL url = new URL(aaUrl);
			logger.info("qq图像地址：" + aaUrl);
			// 打开链接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();
			ServletOutputStream out = response.getOutputStream();
			BufferedImage challenge = ImageIO.read(inStream);
			ImageIO.write(challenge, "png", out);
			out.flush();
			// 关闭输出流
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("输出qq图片出错");
		}
		return null;
	}

	/**
	 * 百度富文本编辑器需要的跳转action
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "dialogs", results = { @Result(name = "image", type = "ftl", location = "/imageUp.jsp") })
	public String dialogs() throws Exception {
		StringBuffer url = request.getRequestURL();
		if ("/plugins/ueditor/dialogs//image/image.html".equals(url)) {
			return "image";
		}
		return url.toString();
	}

	public String actionNotFound() throws Exception {
		return SUCCESS;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public double getAccount() {
		return account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getLilv() {
		return lilv;
	}

	public void setLilv(double lilv) {
		this.lilv = lilv;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public double getCropX() {
		return cropX;
	}

	public void setCropX(double cropX) {
		this.cropX = cropX;
	}

	public double getCropY() {
		return cropY;
	}

	public void setCropY(double cropY) {
		this.cropY = cropY;
	}

	public double getCropW() {
		return cropW;
	}

	public void setCropW(double cropW) {
		this.cropW = cropW;
	}

	public double getCropH() {
		return cropH;
	}

	public void setCropH(double cropH) {
		this.cropH = cropH;
	}

	public String getPlugintype() {
		return plugintype;
	}

	public void setPlugintype(String plugintype) {
		this.plugintype = plugintype;
	}

	public int getTime_limit_day() {
		return time_limit_day;
	}

	public void setTime_limit_day(int time_limit_day) {
		this.time_limit_day = time_limit_day;
	}
}