package com.liangyou.web.filter;
/*package com.rongdu.web.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.rongdu.context.Global;
import com.rongdu.dao.AttestationDao;
import com.rongdu.dao.LinkageDao;
import com.rongdu.freemarker.directive.AreaDirectiveModel;
import com.rongdu.freemarker.directive.AttestationDirectiveModel;
import com.rongdu.freemarker.directive.LinkageDirectiveModel;
import com.rongdu.freemarker.hashmodel.ExtHttpSessionHashModel;
import com.rongdu.freemarker.hashmodel.ExtServletContextHashModel;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.IncludePage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class FreemarkerFilter implements Filter {
	private Logger logger=Logger.getLogger(FreemarkerFilter.class);
	
	// a freemarker script.
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
	
    public static final String KEY_REQUEST = "Request";
    public static final String KEY_INCLUDE = "include_page";
    public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
    public static final String KEY_SESSION = "Session";
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    
	private ObjectWrapper wrapper=ObjectWrapper.DEFAULT_WRAPPER;
	private Configuration ftConfig; 
	private ServletContext servletCtx;
	
	private WebApplicationContext ctx;
	private LinkageDao linkageDao; 
	private AttestationDao attestationDao;

	@Override
	public void init(FilterConfig config) throws ServletException {
		ServletContext servletCtx= config.getServletContext();
		ftConfig=new Configuration();
		
		ctx=WebApplicationContextUtils.getWebApplicationContext(servletCtx);
		linkageDao=(LinkageDao)ctx.getBean("linkageDao");
		attestationDao=(AttestationDao)ctx.getBean("attestationDao");
	}
	
	@Override
	public void destroy() {
	}
	
	protected TemplateLoader createTemplateLoader(String templatePath) throws IOException
    {
		
        if (templatePath.startsWith("class://")) {
            // substring(7) is intentional as we "reuse" the last slash
            return new ClassTemplateLoader(getClass(), templatePath.substring(7));
        } else {
            if (templatePath.startsWith("file://")) {
                templatePath = templatePath.substring(7);
                return new FileTemplateLoader(new File(templatePath));
            } else {
                return new WebappTemplateLoader(servletCtx, templatePath);
            }
        }
    }
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hsRequest=(HttpServletRequest)request;
		HttpServletResponse hsResponse=(HttpServletResponse)response;
		String requestUri = hsRequest.getRequestURI();
		ServletContext sc=hsRequest.getSession().getServletContext();
		String contextPath=sc.getContextPath();
		String fromUrl=requestUri.replaceFirst(contextPath, "");
		String ftl_dir=Global.SYSTEMINFO.getValue("ftl_dir");
		InputStream is = sc.getResourceAsStream(ftl_dir+fromUrl);
		Map data=new HashMap();
		if(is==null){
			//如果静态文件 不存在，那么请求http请求。
			logger.trace(ftl_dir+fromUrl+" can't be found,http request continue.");
			chain.doFilter(request, response);
		}else{
			//使用Freemarker静态页面
			//转发FreemarkerServlet输出输出静态页面
			RequestDispatcher rd=hsRequest.getRequestDispatcher(requestUri);
			rd.forward(hsRequest, response);
			//立即使用Freemarker输出静态页面
			//doProcess(request, response);
			crateHTML(hsRequest, data, ftl_dir+fromUrl, hsResponse);
		}
	}

	private ServletContext getServletContext(HttpServletRequest request){
		ServletContext sc= request.getSession().getServletContext();
		return sc;
	}
	
	protected Configuration createConfiguration() {
        return new Configuration();
    }
	
	private void doProcess(ServletRequest request, ServletResponse response){
		HttpServletRequest hsRequest=(HttpServletRequest)request; 
		HttpServletResponse hsResponse=(HttpServletResponse)response;
		Map data = new HashMap();
		String requestUri = hsRequest.getRequestURI();
		ServletContext sc= hsRequest.getSession().getServletContext();
		String contextUri =sc.getRealPath("/");
		contextUri = contextUri.replace("\\", "/");
		if (contextUri.endsWith("/")) {
			contextUri = contextUri.substring(0, contextUri.length() - 1);
		}
		String context = hsRequest.getContextPath();
		contextUri = contextUri.replaceFirst(context, "");
		String templatePath = requestUri.replaceFirst(context, "");
		String targetPath = contextUri + requestUri;
		if (!File.separator.equals("/")) {
			targetPath = targetPath.replace("/", "\\");
		}
		crateHTML(hsRequest, data, templatePath, hsResponse);
	}
	
	
	
	public void crateHTML(HttpServletRequest request, Map data,
			String templatePath, HttpServletResponse response) {
		
		addDirectiveModel(ftConfig);
		
		ftConfig.setObjectWrapper(new DefaultObjectWrapper());
		// 加载模版
		ftConfig.setServletContextForTemplateLoading(request.getSession()
				.getServletContext(), "/");
		ftConfig.setEncoding(Locale.getDefault(), "UTF-8");
		try {
			// 指定模版路径
			Template template = ftConfig.getTemplate(templatePath, "UTF-8");
			template.setEncoding("UTF-8");
			// 页面输出
			response.setContentType("text/html;charset=UTF-8");
			Writer out = response.getWriter();

			
			Object o = request.getSession().getAttribute("session_user");
			logger.info("Session:" + o);
			//data.put("session", request.getSession());
			ServletContext servletContext = getServletContext(request);
			TemplateModel model = createModel(wrapper, servletContext, request,
					response);
			// 处理模版
			template.process(model, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
			try {
				request.setAttribute("exception", e.getMessage());
				request.setAttribute("exceptionStack", e);
				request.getRequestDispatcher("/404.jsp").forward(request,
						response);
				
			} catch (ServletException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	protected TemplateModel createModel(ObjectWrapper wrapper,
			ServletContext servletContext, final HttpServletRequest request,
			final HttpServletResponse response) throws TemplateModelException {
		try {
			AllHttpScopesHashModel params = new AllHttpScopesHashModel(wrapper,
					servletContext, request);

			// Create hash model wrapper for servlet context (the application)
			ExtServletContextHashModel servletContextModel = (ExtServletContextHashModel) servletContext
					.getAttribute(ATTR_APPLICATION_MODEL);
			if (servletContextModel == null) {
				servletContextModel = new ExtServletContextHashModel(servletContext, wrapper);
				servletContext.setAttribute(ATTR_APPLICATION_MODEL,
						servletContextModel);
				TaglibFactory taglibs = new TaglibFactory(servletContext);
				servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
				//initializeServletContext(request, response);
			}
			params.putUnlistedModel(KEY_APPLICATION, servletContextModel);
			params.putUnlistedModel(KEY_APPLICATION_PRIVATE,
					servletContextModel);
			params.putUnlistedModel(KEY_JSP_TAGLIBS,
					(TemplateModel) servletContext
							.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
			// Create hash model wrapper for session
			ExtHttpSessionHashModel sessionModel;
			HttpSession session = request.getSession(false);
			if (session != null) {
				sessionModel = (ExtHttpSessionHashModel) session
						.getAttribute(ATTR_SESSION_MODEL);
				if (sessionModel == null ) {//  || sessionModel.isOrphaned(session)
					sessionModel = new ExtHttpSessionHashModel(session, wrapper);
					session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
			        //initializeSession(request, response);
				}
			} else {
				sessionModel = new ExtHttpSessionHashModel(this, request,response, wrapper);
			}
			params.putUnlistedModel(KEY_SESSION, sessionModel);

			// Create hash model wrapper for request
			HttpRequestHashModel requestModel = (HttpRequestHashModel) request
					.getAttribute(ATTR_REQUEST_MODEL);
			if (requestModel == null || requestModel.getRequest() != request) {
				requestModel = new HttpRequestHashModel(request, response,
						wrapper);
				request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
				request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL,
						createRequestParametersHashModel(request));
			}
			params.putUnlistedModel(KEY_REQUEST, requestModel);
			params.putUnlistedModel(KEY_INCLUDE, new IncludePage(request,
					response));
			params.putUnlistedModel(KEY_REQUEST_PRIVATE, requestModel);

			// Create hash model wrapper for request parameters
			HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel) request
					.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
			params.putUnlistedModel(KEY_REQUEST_PARAMETERS,
					requestParametersModel);
			//自定义变量或者函数
			
			params.put("request", request);
			
			return params;
		} catch (Exception e) {
			throw new TemplateModelException(e);
		} 
	}
	
	//增加自定义指令
	protected void addDirectiveModel(Configuration cfg){
		//借款用途的下拉框
		cfg.setSharedVariable("linkage", new LinkageDirectiveModel(linkageDao));
		//地区的下拉框，area标签有冲突，用region代替
		cfg.setSharedVariable("region", new AreaDirectiveModel(linkageDao));
		cfg.setSharedVariable("attestation", new AttestationDirectiveModel(attestationDao));
        
	}
		
	protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
        return new HttpRequestParametersHashModel(request);
    }
		
	public void initializeSessionAndInstallModel(HttpServletRequest request,
            HttpServletResponse response, ExtHttpSessionHashModel sessionModel, 
            HttpSession session)
            throws ServletException, IOException
    {
        session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
        //initializeSession(request, response);
    }

}
*/