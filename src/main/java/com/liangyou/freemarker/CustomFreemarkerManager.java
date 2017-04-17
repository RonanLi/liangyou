package com.liangyou.freemarker;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.liangyou.dao.AreaBankDao;
import com.liangyou.dao.AreaDao;
import com.liangyou.dao.AreaMmmDao;
import com.liangyou.dao.ArticleDao;
import com.liangyou.dao.AttestationDao;
import com.liangyou.dao.AttestationTypeDao;
import com.liangyou.dao.BorrowDetailTypeDao;
import com.liangyou.dao.LinkageDao;
import com.liangyou.dao.SiteDao;
import com.liangyou.dao.UserDao;
import com.liangyou.freemarker.directive.AreaBankDirectiveModel;
import com.liangyou.freemarker.directive.AreaDirectiveModel;
import com.liangyou.freemarker.directive.AreaMmmDirectiveModel;
import com.liangyou.freemarker.directive.AttestationDirectiveModel;
import com.liangyou.freemarker.directive.BorrowDetailDirectiveModel;
import com.liangyou.freemarker.directive.LinkageDirectiveModel;
import com.liangyou.freemarker.directive.SiteDirectiveModel;
import com.liangyou.freemarker.method.AttestationTypeNameModel;
import com.liangyou.freemarker.method.DateMethodModel;
import com.liangyou.freemarker.method.DateRollMethodModel;
import com.liangyou.freemarker.method.InterestMethodModel;
import com.liangyou.freemarker.method.LinkageMethodModel;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class CustomFreemarkerManager extends FreemarkerManager {
	private static Logger logger = Logger.getLogger(CustomFreemarkerManager.class);
	@Override  
    protected Configuration createConfiguration(ServletContext servletContext)  
        throws TemplateException { 
		
        Configuration cfg = super.createConfiguration(servletContext);  
        ApplicationContext ctx= WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        try{        	
			LinkageDao linkageDao = (LinkageDao)ctx.getBean("linkageDao");
	        AttestationDao attestationDao = (AttestationDao)ctx.getBean("attestationDao");
	        UserDao userDao = (UserDao) ctx.getBean("userDao");
	        ArticleDao articleDao = (ArticleDao) ctx.getBean("articleDao");
	        AreaDao areaDao = (AreaDao) ctx.getBean("areaDao");
	        SiteDao siteDao = (SiteDao) ctx.getBean("siteDao");
	        AreaBankDao areaBankDao = (AreaBankDao) ctx.getBean("areaBankDao");
	        //v1.8.0.3  乾多多绑卡  lx start
	        AreaMmmDao areaMmmDao = (AreaMmmDao) ctx.getBean("areaMmmDao");
	        //v1.8.0.3  乾多多绑卡  lx end
	        AttestationTypeDao attestationTypeDao = (AttestationTypeDao)ctx.getBean("attestationTypeDao");
	        //计算利息的自定义方法 
	        cfg.setSharedVariable("dateformat", new DateMethodModel(linkageDao) );  
	        cfg.setSharedVariable("dateroll", new DateRollMethodModel() );  
	        cfg.setSharedVariable("interest", new InterestMethodModel() );  
	        //新增自定义标签
	        cfg.setSharedVariable("linkage", new LinkageDirectiveModel(linkageDao));
	        //area标签有冲突，用region代替
	        cfg.setSharedVariable("region", new AreaDirectiveModel(areaDao));
	        cfg.setSharedVariable("regionBank", new AreaBankDirectiveModel(areaBankDao));
	        //v1.8.0.3  乾多多绑卡  lx start
	        cfg.setSharedVariable("mmmRegionBank", new AreaMmmDirectiveModel(areaMmmDao));
	        //v1.8.0.3  乾多多绑卡  lx end
	        cfg.setSharedVariable("attestation", new AttestationDirectiveModel(attestationDao,attestationTypeDao));
	        cfg.setSharedVariable("Type", new AttestationTypeNameModel(linkageDao,userDao,areaDao));
	        cfg.setSharedVariable("accountType", new AttestationTypeNameModel(linkageDao,userDao,areaDao));
	        cfg.setSharedVariable("attestationType", new AttestationTypeNameModel(linkageDao,userDao,areaDao));
	        cfg.setSharedVariable("siteDirect", new SiteDirectiveModel(siteDao));
	        cfg.setSharedVariable("getLinkage", new LinkageMethodModel(linkageDao));
	        //v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  start
	        BorrowDetailTypeDao borrowDetailTypeDao = (BorrowDetailTypeDao) ctx.getBean("borrowDetailTypeDao");
	        cfg.setSharedVariable("borrowDetailType", new BorrowDetailDirectiveModel(borrowDetailTypeDao));
	        //v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16 end  
		}catch(Exception e){
			logger.error(e);
		}
		
        return cfg;  
    }

	@Override
	public synchronized Configuration getConfiguration(
			ServletContext servletContext) {
		Configuration cfg = super.getConfiguration(servletContext);  
        cfg.setTemplateExceptionHandler(new CustomFreemarkerExceptionHandler());
        cfg.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
		return cfg;
	} 
	
}
