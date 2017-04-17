package com.liangyou.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.dao.LinkageDao;
import com.liangyou.domain.Linkage;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/**
 *各种下拉框的html输出
 * 
 * @author fuxingxing usage: <@linkage name="use" id="use" class="test"
 *         default"36" typeid=19 nid="borrow_use" disabled="disabled"
 *         type="value"></@linkage>
 *@param name
 *            表单名字,String类型,不能为空
 *@param id
 *            表单的id,String类型，可以为空
 *@param clazz
 *            表单的class,String类型，可以为空
 *@param typeid
 *            下拉框的类型,Number类型，比如借款用途为19，对应数据库中的type_id
 *@param nid
 *            下拉框的类型,String类型，比如借款用途为borrow_use，对应数据库中的nid
 *@param default 表单的默认值，String类型。
 *@param disabled
 *            表单是否可选，String类型。
 *@param type
 *            表单的值是否由linkage的Value决定,是则type='value'。
 *@return 返回拼装出来的html字符串
 */
public class LinkageDirectiveModel implements TemplateDirectiveModel {

	private static Logger logger = Logger
			.getLogger(LinkageDirectiveModel.class);

	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String CLASS = "class";
	private static final String TYPEID = "typeid";
	private static final String DEFAULT = "default";
	private static final String NID = "nid";
	private static final String DISABLED = "disabled";
	private static final String TYPE = "type";
	private static final String NOSELECT="noselect";
	private static final String PLAINTEXT="plantext";
	private static final String SCOPE="scope";
	
	@Autowired
	private LinkageDao dao;

	public LinkageDirectiveModel(LinkageDao dao) {		
		super();
		this.dao = dao;
	}

	public LinkageDirectiveModel() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Environment env, Map map, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Iterator it = map.entrySet().iterator();
		String name = "", id = "", clazz = "", defaultvalue = "", nid = "", disabled = "", type = "",noselect="",scope="";
		int typeid = 0;boolean plantext=false;
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			String paramName = entry.getKey().toString();
			TemplateModel paramValue = (TemplateModel) entry.getValue();
			if(paramName.equals(PLAINTEXT)){
				if (!(paramValue instanceof TemplateBooleanModel)) {
					throw new TemplateModelException("The \"" + plantext
							+ "\" parameter " + "must be a boolean.");
				}
				plantext = ((TemplateBooleanModel) paramValue).getAsBoolean();
			}
			if (paramName.equals(NAME)) {
				name = paramValue.toString();
			} else if (paramName.equals(ID)) {
				id = paramValue.toString();
			} else if (paramName.equals(CLASS)) {
				clazz = paramValue.toString();
			} else if (paramName.equals(DEFAULT)) {
				defaultvalue = paramValue.toString();
			} else if (paramName.equals(NID)) {
				nid = paramValue.toString();
			}else if (paramName.equals(DISABLED)) {
				disabled = paramValue.toString();
			} else if (paramName.equals(NOSELECT)) {
				noselect = paramValue.toString();
			}  else if (paramName.equals(TYPE)) {
				type = paramValue.toString();
			} else if (paramName.equals(TYPEID)) {
				if (!(paramValue instanceof TemplateNumberModel)) {
					throw new TemplateModelException("The \"" + TYPEID
							+ "\" parameter " + "must be a number.");
				}
				typeid = ((TemplateNumberModel) paramValue).getAsNumber()
						.intValue();
			} else if (paramName.equals(SCOPE)) {
				scope = paramValue.toString();
			} 
		}
		String result="";
		if(plantext){
			result = plaintext(name, id, clazz, defaultvalue, typeid, nid,
					disabled, type,noselect);
		}else{
			result = html(name, id, clazz, defaultvalue, typeid, nid,
					disabled, type,noselect,scope);
		}
		Writer out = env.getOut();
		out.write(result);
	}

	
	
	/**
	 * 
	 * @param name
	 *            表单名字,不能为空
	 * @param id
	 *            表单的id，可以为空
	 * @param clazz
	 *            表单的class，可以为空
	 * @param typeid
	 *            下拉框的类型，比如借款用途为19，对应数据库中的type_id
	 * @return 返回拼装出来的html字符串
	 */
	private String html(String name, String id, String clazz,
			String defaultvalue, int typeid, String nid, String disabled,
			String type,String noselect,String scope) {
		List<Linkage> list = null;
		type=StringUtils.isNull(type);
		if (!StringUtils.isBlank(nid)) {
			list = dao.getLinkageByNid(nid,type);
		} else {
			list = dao.getLinkageByTypeid(typeid,type);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<select name=\"").append(name).append(
				"\" autocomplete=\"off\"");
		if (!id.equals("")) {
			sb.append(" id=\"").append(id).append("\"");
		}
		if (!clazz.equals("")) {
			sb.append(" class=\"").append(clazz).append("\"");
		}
		if (!disabled.equals("")) {
			sb.append(" disabled=\"").append(disabled).append("\"");
		}
		sb.append(">");
		//没有选择处理
		if(!noselect.equals("")){
			sb.append("<option value=\"0\">"+noselect+"</option>");
		}
		for (Linkage l : list) {
			if(scope == null || scope.equals("")){//没有取值范围scope
				String value = Integer.toString(l.getId());
				if ("value".equals(type)) {
					value = l.getValue();
				}
				sb.append("<option value=\"").append(value).append("\"");
				if (!defaultvalue.equals("") && defaultvalue.equals(value)) {
					sb.append(" selected=\"selected\" ");
				}
				sb.append(">").append(l.getName()).append("</option>");
			}else{//有取值范围scope
				String value = Integer.toString(l.getId());
				if ("value".equals(type)) {
					value = l.getValue();
				}
				if(NumberUtils.getInt(scope)>=NumberUtils.getInt(l.getValue())){
					sb.append("<option value=\"").append(value).append("\"");
					if (!defaultvalue.equals("") && defaultvalue.equals(value)) {
						sb.append(" selected=\"selected\" ");
					}
					sb.append(">").append(l.getName()).append("</option>");
				}				
			}
			
		}
		if(scope.equals("0")){
			sb.append("<option value=\"").append(0).append("\"");
			sb.append(">").append("0天").append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}
	
	private String plaintext(String name, String id, String clazz,
			String defaultvalue, int typeid, String nid, String disabled,
			String type,String noselect) {
		Linkage l = null;
		if (!nid.equals("")) {
			l = dao.getLinkageByValue(nid,defaultvalue);			
		} else {
			l = dao.find(NumberUtils.getInt(defaultvalue));
		}
		if(l==null){
			return "";
		}
		String ret="";
		if(type.equals("value")){
			ret=l.getValue();
		}else{
			ret=l.getName();
		}
		return ret;
	}
}