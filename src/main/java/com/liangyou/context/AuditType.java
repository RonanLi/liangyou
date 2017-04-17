package com.liangyou.context;

import com.liangyou.util.StringUtils;

public enum AuditType {
	realnameAudit,emailAudit,phoneAudit,videoAudit,sceneAudit;
	
	public static AuditType getAuditType(String name){
		name=StringUtils.isNull(name);
		if(name.equals("realname")){
			return realnameAudit;
		}else if(name.equals("email")){
			return emailAudit;
		}else if(name.equals("phone")){
			return phoneAudit;
		}else if(name.equals("video")){
			return videoAudit;
		}else if(name.equals("scene")){
			return sceneAudit;
		}else{
			return null;
		}
	}
	
}
