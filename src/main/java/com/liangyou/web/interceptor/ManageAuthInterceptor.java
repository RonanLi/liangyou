package com.liangyou.web.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.SystemDao;
import com.liangyou.domain.Purview;
import com.liangyou.domain.User;
import com.liangyou.tool.iphelper.IPUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class ManageAuthInterceptor extends BaseInterceptor {  
  
	private static final long serialVersionUID = -2239644443711524657L;
	
	private static final Logger logger=Logger.getLogger(ManageAuthInterceptor.class);
	
	@Override  
    public String intercept(ActionInvocation ai) throws Exception {  
		ActionContext ctx = ActionContext.getContext();  
        Map session = ctx.getSession();  
        User user = (User) session.get(Constant.AUTH_USER);  
        String servletPath=getServletPath();
        
		String isAllowIp_enable=Global.getValue("isAllowIp_enable");
		if(isAllowIp_enable!=null&&isAllowIp_enable.equals("1")){
			ServletContext context = ServletActionContext.getServletContext();
			ApplicationContext appctx= WebApplicationContextUtils.getRequiredWebApplicationContext(context);
			SystemDao systemDao = (SystemDao)appctx.getBean("systemDao");
			List ipList=systemDao.getAllowIp();
			String realip=IPUtils.getRemortIP(ServletActionContext.getRequest());
			if(!ipList.contains(realip)){
				logger.debug("未授权IP："+realip+"没有权限访问后台！");
        		message("未授权IP："+realip+"没有权限访问后台！", "");
        		return BaseAction.ADMINMSG;
			}
		}
        //新添加后台修改密码的拦截忽略
        if(servletPath.startsWith("/admin/auth.html")||servletPath.startsWith("/admin/z1F7zMRQvacq.html")||servletPath.startsWith("/admin/logout.html")||servletPath.startsWith("/admin/setAdminPwd.html")||servletPath.startsWith("/admin/resetadminpwd.html")||servletPath.startsWith("/admin/showAuthMenu.html")){
        	 return ai.invoke(); 
        }
        
        if (user == null) {
        	message("请先登录！", Global.getString("con_admin_url"));
    		return BaseAction.ADMINMSG;  
        } else {
        	//如果是前台用户,或者企业用户,拦截访问
        	if(user.getUserType().getTypeId() == 2 || user.getUserType().getTypeId() == 28){
        		logger.debug(user.getUsername()+"尝试了直接越权限访问后台!!!!!!!!!!");
        		message(user.getUsername()+"没有权限访问后台！", "");
        		return BaseAction.ADMINMSG;
        	}
        	//已经经过我们后台开户的用户,登录情况下,访问欢迎页面,不拦截
        	if(servletPath.startsWith("/admin/system/welcome.htm")){
        		return ai.invoke();
        	}
        	
        	List<Purview> purviewList = (List<Purview>)session.get(Constant.AUTH_PURVIEW);
        	if(purviewList.size()<1){
        		logger.debug(user.getUsername()+"没有权限访问后台！");
        		message(user.getUsername()+"没有权限访问后台！", "");
        		return BaseAction.ADMINMSG;
        	}else{
        		for(Purview p:purviewList){
        			if(servletPath.equals(p.getUrl())){
        				ai.invoke();
        				break;
        			}
        		}
        	}
        	message(user.getUsername()+"没有权限访问后台！", "");
    		return BaseAction.ADMINMSG;
        }  
    }  
	
	protected String getServletPath(){
		HttpServletRequest request =ServletActionContext.getRequest();
		String servletPath=request.getServletPath();
		String extension=ServletActionContext.getActionMapping().getExtension();
		servletPath=servletPath.replace("."+extension, ".html");
		return servletPath;
	}
  
}  