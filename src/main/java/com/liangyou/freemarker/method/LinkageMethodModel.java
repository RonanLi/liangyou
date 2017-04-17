package com.liangyou.freemarker.method;

import java.util.List;

import org.apache.log4j.Logger;

import com.liangyou.dao.LinkageDao;
import com.liangyou.domain.Linkage;
import com.liangyou.util.StringUtils;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 根据ID获取证件类型 或者获取证件列表(用户类型等)
 * 
 * @author lijie
 * 
 */
public class LinkageMethodModel implements TemplateMethodModel {
	private static Logger logger = Logger.getLogger(LinkageMethodModel.class);
	private LinkageDao linkageDao;

	public LinkageMethodModel(LinkageDao linkageDao) {
		this.linkageDao = linkageDao;
	}

	@Override
	public Object exec(List arg) throws TemplateModelException {
		String nid="",type="",value="";
		if (arg.size() ==3) {
			nid=StringUtils.isNull(arg.get(0));
			type=StringUtils.isNull(arg.get(1));
			value=StringUtils.isNull(arg.get(2));
		}else{
			return "Illegal arguments";
		}
		Linkage l=null;
		if(type.equals("id")){
			Integer id= Integer.parseInt(value);
			try {
				l=linkageDao.find(id);
			} catch (Exception e) {
				logger.error(e);
			}
		}else{
			l=linkageDao.getLinkageByValue(nid, value);
		}
		return l;
	}

}
