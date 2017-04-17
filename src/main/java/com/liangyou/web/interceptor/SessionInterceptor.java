package com.liangyou.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.User;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class SessionInterceptor extends AbstractInterceptor {  
  
	private static final long serialVersionUID = -2239644443711524657L;

	@Override  
    public String intercept(ActionInvocation actionInvocation) throws Exception {  
        ActionContext ctx = ActionContext.getContext();  
        Map session = ctx.getSession();  
        HttpServletRequest request =  ServletActionContext.getRequest();
        String path = request.getServletPath();
        String passPath[] = {"/invest/detail.html","/invest/detailTenderForJson.html"};
        
        if( (passPath[0].equals(path) || passPath[1].equals(path))&&"1".equals(Global.getValue("is_open_borrow_detail"))){//系统配置是否登陆可查看标详情
        	return actionInvocation.invoke();
        }
        User user = (User) session.get(Constant.SESSION_USER);  
        if (user == null) {
        	String param = request.getQueryString();
        	request.setAttribute("url",request.getRequestURL().toString()+"?"+param);
            return Action.LOGIN;  
        } else {  
            return actionInvocation.invoke();  
        }  
    }  
  
}  