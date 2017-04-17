package com.liangyou.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.liangyou.domain.WeChatUser;
import com.liangyou.util.Constants;
import com.liangyou.web.action.weixin.WeChatAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * @author 微信端登录拦截器
 *
 */
public class WeChatInterceptor extends BaseInterceptor{
	private static final long serialVersionUID = 1L;
	private static final Logger logger=Logger.getLogger(WeChatInterceptor.class);
	@Override
	public String intercept(ActionInvocation ai) throws Exception {  
		HttpServletRequest request =ServletActionContext.getRequest();
		String servletPath =  request.getServletPath();
		if(servletPath.startsWith("/wx/msg.html") || servletPath.startsWith("/wx/oauth2.html")){
			ai.invoke();
		}
		ActionContext ctx = ActionContext.getContext();  
        Map session = ctx.getSession();  
        WeChatUser user = (WeChatUser) session.get(Constants.WECHAT_USER);  
        if(servletPath.endsWith("Login.html")){
        	ai.invoke();
        }
        if (user == null) {
        	message("请先登录！", "/wx/showLogin.html");
    		return WeChatAction.MSG;  
        } else {
        	return ai.invoke();
        }  
    }

}
