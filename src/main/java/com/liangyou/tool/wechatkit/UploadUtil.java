package com.liangyou.tool.wechatkit;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
/**
 * 微信附件上传
 * 
 */
public class UploadUtil {
	
	public static String doUpload(String realPath ,String modulePath ,MultipartFile file){
		String fileName = file.getOriginalFilename();  
		if(!StringUtils.isEmpty(fileName)){
			String ext = fileName.substring(fileName.indexOf("."));
			fileName = Calendar.getInstance().getTimeInMillis() + ext;
			
			File targetFile = new File(realPath + modulePath + fileName);  
	        if(!targetFile.exists()){
	            targetFile.mkdirs();
	        }  
	        //保存
	        try {
	            file.transferTo(targetFile);  
	        } catch (Exception e) {
	            e.printStackTrace();  
	        } 
	        return "/wechat/" + fileName;
		}
		return null;
	}
	
	public static String byteToImg(String realPath ,byte[] bytes){
		if(bytes != null && bytes.length > 0){
			String imagePath = "/wechat/" + UUID.randomUUID().toString() + ".jpg";
			FileImageOutputStream imageOutput;
			try {
				File file = new File(realPath + imagePath);
				file.createNewFile();
				imageOutput = new FileImageOutputStream(file);
				imageOutput.write(bytes, 0, bytes.length);  
			 	imageOutput.close(); 
				return imagePath;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
		return null;
	}

	public static String doUpload(String realPath, String wechatuploadPath, File files, String filesFileName) {
		if(StringUtils.isNotBlank(filesFileName)){
	     try {
		    String ext = filesFileName.substring(filesFileName.indexOf("."));
			String fileName = Calendar.getInstance().getTimeInMillis()+ext;
			File targetDist = new File(realPath + wechatuploadPath);
			File targetFile = new File(realPath + wechatuploadPath + fileName);  
	        if(!targetDist.exists()){
	        	targetDist.mkdirs();
	        }
			FileUtils.copyFile(files, targetFile);
			return wechatuploadPath+fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}

