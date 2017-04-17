package com.liangyou.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.liangyou.dao.AttestationDao;
import com.liangyou.dao.AttestationTypeDao;
import com.liangyou.domain.AttestationType;
import com.liangyou.model.SearchParam;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 *各种下拉框的html输出
 * 
 * @author fuxingxing usage: <@attestation name="use" id="use" class="use"></@attestation>
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
public class AttestationDirectiveModel implements TemplateDirectiveModel {

	private static Logger logger = Logger
			.getLogger(AttestationDirectiveModel.class);

	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String CLASS = "class";
	private static final String DEFAULT = "default";

	private AttestationDao dao;
	private AttestationTypeDao typeDao;

	public AttestationDirectiveModel(AttestationDao dao,AttestationTypeDao typeDao) {
		super();
		this.dao = dao;
		this.typeDao = typeDao;
	}

	public AttestationDirectiveModel() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Environment env, Map map, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Iterator it = map.entrySet().iterator();
		String name = "", id = "", clazz = "", defaultvalue = "";
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			String paramName = entry.getKey().toString();
			TemplateModel paramValue = (TemplateModel) entry.getValue();
			logger.debug("name:" + paramName);
			logger.debug("r:" + paramValue.toString());
			if (paramName.equals(NAME)) {
				name = paramValue.toString();
			} else if (paramName.equals(ID)) {
				id = paramValue.toString();
			} else if (paramName.equals(CLASS)) {
				clazz = paramValue.toString();
			} else if (paramName.equals(DEFAULT)) {
				defaultvalue = paramValue.toString();
			} 
		}
		String result = html(name, id, clazz, defaultvalue);
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
	private String html(String name, String id, String clazz,String defaultvalue) {
		List<AttestationType> list = null;
		//list=typeDao.findAll();
		SearchParam param = SearchParam.getInstance().addOrder("sort");
		list = typeDao.getList(param);
		StringBuffer sb = new StringBuffer();
		sb.append("<select name=\"").append(name).append(
				"\" autocomplete=\"off\"");
		if (!id.equals("")) {
			sb.append(" id=\"").append(id).append("\"");
		}
		if (!clazz.equals("")) {
			sb.append(" class=\"").append(clazz).append("\"");
		}
		sb.append(">");
		for (AttestationType l : list) {
			String value = l.getTypeId()+"";
			sb.append("<option value=\"").append(value).append("\"");
			sb.append(">").append(l.getName()).append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}
}
