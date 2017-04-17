package com.liangyou.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import com.liangyou.freemarker.method.DateMethodModel;
import com.liangyou.freemarker.method.ParserDoubleMethodModel;
import com.liangyou.freemarker.method.ParserLongMethodModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * freemarker的模板处理工具类
 *
 * 未经授权不得进行修改、复制、出售及商业使用。
 * <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
 *
 * @ClassName: FreemarkerUtil
 * @Description: 
 * @author fuxingxing somesky.cn@gmail.com
 * @date 2013-8-21 下午3:54:17
 */
public class FreemarkerUtil {

	
	public static Configuration CONFIG;

	public static String renderTemplate(String s,Map<String,Object> data) throws IOException, TemplateException{
		if (CONFIG == null) {
			getConfiguration();
		}
 		Template t = new Template(null, new StringReader(s), CONFIG);
        //执行插值，并输出到指定的输出流中
		StringWriter w = new StringWriter();
		t.getConfiguration();
		t.process(data, w);
        return w.getBuffer().toString();
	}
	
	public static String renderFileTemplate(String file,Map<String,Object> data) throws IOException, TemplateException{
		Configuration cfg = CONFIG;
		cfg.setDefaultEncoding("UTF-8");  
        // 取得模板文件  
        Template t = cfg.getTemplate(file);
        //执行插值，并输出到指定的输出流中
		StringWriter w = new StringWriter();
		t.getConfiguration();
        t.process(data, w); 
        return w.getBuffer().toString();
	}
	
	// CONFIG为null时进行初始化
	public static void getConfiguration(){
		Configuration cfg = new Configuration();
        //计算利息的自定义方法 
        cfg.setSharedVariable("dateformat", new DateMethodModel() );  
        cfg.setSharedVariable("parseDouble", new ParserDoubleMethodModel());
        cfg.setSharedVariable("parseLong", new ParserLongMethodModel());      
        CONFIG = cfg;
	}
}
