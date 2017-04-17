//v1.8.0.4 cq 2014-3-31 start
package com.liangyou.web.action.admin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.User;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;


@Namespace("/admin/personal")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminPersonalAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(AdminPersonalAction.class); 
	
	private String sep=File.separator;
	private File upload;
	private String uploadFileName;
	
	@Autowired
	private UserService userService;
	
	
	//裁剪后的图像大小
	private double cropX;
	private double cropY;
	private double cropW;
	private double cropH;

	@Action(value = "changePwd",
			results = { @Result(name = "success", type = "ftl", location = "/admin/personal/query.html") })
	public String changePwd() throws Exception {
		
		request.setAttribute("queryType", paramString("queryType"));
		
		String username = this.getAuthUser().getUsername();
		User user = userService.getUserByName(username);
		request.setAttribute("user", user);
		

		
		return SUCCESS;
	}
	
	@Action("modifyPwd")
	public String modifyPwd(){
		String newpassword = request.getParameter("newpassword");
		String newpassword1 = request.getParameter("newpassword1");
		String queryType = paramString("queryType");
		User user = userService.getUserById(paramLong("userId"));
		if(user == null){
			message("用户不存在！",Global.getString("con_admin_url"));
			return ADMINMSG;
		}
		if (newpassword != null) {
			checkUserpwd(newpassword ,newpassword1);
			if(queryType.equals("payPwd")){
				user.setPaypassword(new MD5().getMD5ofStr(newpassword));
			}else{
				user.setPassword(new MD5().getMD5ofStr(newpassword));
			}
			userService.updateUser(user);
			super.systemLogAdd(user, 6, "修改管理员登录密码成功");
			message("修改密码成功！","/admin/personal/changePwd.html?queryType="+queryType);
			return ADMINMSG;
		}else{
			message("修改密码失败！","/admin/personal/changePwd.html?queryType="+queryType);
			return ADMINMSG;
		}
	}
	
	@Action(value = "adminAvatar", results = { @Result(name = "success", type = "ftl", location = "/admin/personal/adminAvatar.html") })
	public String adminAvatar() {
		User authUser = getAuthUser();
		authUser = userService.getUserById(authUser.getUserId());
		session.put(Constant.AUTH_USER, authUser);
		String upload = StringUtils.isNull(request.getParameter("upload"));
		return SUCCESS;
	}
	
	//保存头像
		@Action("saveAdminAvatar")
		public String saveAdminAvatar() throws Exception {
			try {
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("application/json;charset=UTF-8");
				int x=0,y=0,w=0,h=0;
				if(cropX>=0){x=(int)cropX;}
				if(cropY>=0){y=(int)cropY;}
				if(cropW>=0){w=(int)cropW;}
				if(cropH>=0){h=(int)cropH;}
				
				logger.debug("X="+x+",Y="+y+",W="+w+",H="+h);
				@SuppressWarnings("unused")
				boolean re=operateImg(x,y,w,h);
				Map map = new HashMap();
				if(re){
					map.put("flag", 1);
					map.put("msg", "success");
					map.put("useravatar", "");
					String json=JSON.toJSONString(map,true);
					printJson(json);
				}else{
					map.put("flag", 0);
					map.put("msg", "success");
					map.put("useravatar", "");
					String json=JSON.toJSONString(map,true);
					printJson(json);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
			return  null;
		}
		
		private boolean operateImg(int x,int y,int w,int h) throws IOException{
			Map map=ServletActionContext.getContext().getSession();
			User user=(User)map.get(Constant.AUTH_USER);
			String dataPath=ServletActionContext.getServletContext().getRealPath("/data");
			String avatarDir=dataPath+sep+"avatar";
			String dest=avatarDir+sep+user.getUserId()+"_avatar_middle.jpg";
			String smalldest=avatarDir+sep+user.getUserId()+"_avatar_small.jpg";
			String src=ServletActionContext.getServletContext().getRealPath( "/data" ) +sep+"temp"+sep+user.getUserId()+".jpg";
			BufferedImage srcImage =null;
			try {
				srcImage=ImageIO.read(new File(src));
				BufferedImage destImage=srcImage.getSubimage(x,y,w,h);
				BufferedImage lastImage=new BufferedImage(w, h,
		                BufferedImage.TYPE_3BYTE_BGR);
				lastImage.getGraphics().drawImage(destImage, 0, 0, null);  
				File avatarDirFile=new File(avatarDir);
				if(!avatarDirFile.exists()){
					avatarDirFile.mkdir();
				}
				File newFile=new File(dest);
				File smallNewFile=new File(smalldest);
				logger.info("Avatar dest:"+dest);
				ImageIO.write(lastImage, "jpg", newFile);
				ImageIO.write(lastImage, "jpg", smallNewFile);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return false;
			} 
			 return true;
		}
		
		@Action(value="upload")
		public String upload() throws Exception {
			Map map=ServletActionContext.getContext().getSession();
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();  
			String retMsg="";
			User user=(User)map.get(Constant.AUTH_USER);
			logger.info("文件："+this.upload);
			logger.info("文件名："+uploadFileName);
			Date d=new Date();
			String destfilename2=ServletActionContext.getServletContext().getRealPath( "/data" ) +sep+"temp"+sep+user.getUserId()+".jpg";
			logger.info(destfilename2);
			File imageFile=null;
			File imageFile2=null;
			try {
			
				imageFile2 = new File(destfilename2);
				long a  = upload.length();
				if (a>1048576) {
					retMsg=getRetMsg(1, "你上传的图片大于1M，请更换成小于1M的图片！","");
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
					retMsg=getRetMsg(1, "你输入的文件为无效图片，即图片文件不合法！","");
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
		
			//保存上传的临时图片名称
			String src=request.getContextPath()+"/data/temp/"+user.getUserId()+".jpg";
			BufferedImage srcImage =null;
			BufferedImage destImage =null;
			File file=new File(destfilename2);
			int newWi=0;
			int newHi=0;
			try {
				srcImage=ImageIO.read(file);
				int w=srcImage.getWidth();
				int h=srcImage.getHeight();
				int minW=(w>h)?w:h;
				double newWd=(300.0/minW)*w;
				double newHd=(300.0/minW)*h;
				newWi=(int)newWd; newHi=(int)newHd;
				destImage = new BufferedImage(newWi,newHi, BufferedImage.TYPE_3BYTE_BGR);   
				destImage.getGraphics().drawImage(srcImage.getScaledInstance(newWi, newHi, Image.SCALE_SMOOTH), 0, 0, null);
				ImageIO.write(destImage, "jpg", new File(destfilename2));
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug("Print img:"+src);
	        //out.print(src);
			out.print("{");
			out.print("error: '',\n");
			out.print("msg: '"+src+ "',\n");
			out.print("width: "+newWi+ ",\n");
			out.print("height: "+newHi+ "\n");
			out.print("}");
	        out.flush();   
	        out.close();
			return null;
	   }

		private String getRetMsg(int flag,String msg,String imgUrl){
			JSONObject obj = new JSONObject();
			obj.put("error", flag);
			obj.put("message", msg);
			obj.put("url", request.getContextPath()+imgUrl);
			return obj.toString();
		}
	
	
	
	private void checkUserpwd(String newpassword,String newpassword1) {
		if (newpassword.length() < 9 || newpassword.length() > 15) {
			throw new ManageBussinessException("新密码长度必须大于10位","/admin/system/queryAdminUser.html");
		} else if(StringUtils.isBlank(newpassword) || !newpassword.equals(newpassword1) ){
			throw new ManageBussinessException("您两次输入的新密码不一样，请重新填写","/admin/system/queryAdminUser.html");
		}else if(!StringUtils.checaAdminPwd(newpassword)){  //校验密码不能为纯数字，或者是纯字母，可以为数字加字符，字母加字符
			throw new ManageBussinessException("密码必须包含数字、字母、特殊字符【~!@#$%^&*()】，长度不少于10位!","/admin/system/queryAdminUser.html");
		}
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

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
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
	
	
}
//v1.8.0.4 cq 2014-3-31 end
