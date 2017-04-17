package com.liangyou.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liangyou.context.Constant;
import com.liangyou.domain.User;

/**e
 * 用于session过滤，补充Struts2的sssion拦截器
 * 
 * @author fuxingxing
 * 
 */
public class SessionFilter implements Filter {
	protected String encoding = null;

	protected FilterConfig filterConfig = null;

	protected boolean ignore = false;

	protected String forwardPath = null;

	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		// String requesturi = httpServletRequest.getRequestURI();
		// 通过检查session中的变量，过虑请求
		HttpSession session = httpServletRequest.getSession();
		User sessionUser = (User) session.getAttribute(Constant.SESSION_USER);
		// 当前会话用户为空而且不是请求登录，退出登录，欢迎页面和根目录则退回到应用的根目录
		String servletPath = httpServletRequest.getServletPath();
		List<String> pathList = notNeedSessionCheck();
		if (!pathList.contains(servletPath)) {
			if (sessionUser == null) {
				httpServletResponse.sendRedirect(httpServletRequest
						.getContextPath() + "/user/login.html");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
		this.forwardPath = filterConfig.getInitParameter("forwardpath");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null)
			this.ignore = true;
		else if (value.equalsIgnoreCase("true"))
			this.ignore = true;
		else if (value.equalsIgnoreCase("yes"))
			this.ignore = true;
		else
			this.ignore = false;
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}

	private List<String> notNeedSessionCheck() {
		//v1.8.0.3_u3 XINHEHANG-66  wuing 2014-06-19  start 
		//添加region的freemark标签获取城市信息路径过滤
		String[] paths = new String[] { "/member/identify/active.html",
                "/member/pay/gopay.html",
                "/member/identify/active.action",
                "/member/pay/gopay.action",
                "/member/userinfo/showarea.html",
				"/borrow/borrowintent.html",
				"/member/identify/getUsrrJoinedList.html"};
		//v1.8.0.3_u3 XINHEHANG-66  wujing 2014-06-19  end
		return Arrays.asList(paths);
	}
}
