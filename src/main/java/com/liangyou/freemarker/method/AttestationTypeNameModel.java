package com.liangyou.freemarker.method;

import java.util.List;

import org.apache.log4j.Logger;

import com.liangyou.dao.AreaDao;
import com.liangyou.dao.LinkageDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Area;
import com.liangyou.domain.Linkage;
import com.liangyou.util.NumberUtils;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 根据ID获取证件类型 或者获取证件列表(用户类型等)
 * 
 * @author lijie
 * 
 */
public class AttestationTypeNameModel implements TemplateMethodModel {
	private static Logger logger = Logger.getLogger(AttestationTypeNameModel.class);
	private LinkageDao linkageDao;
	private UserDao userDao;
	private AreaDao areaDao;

	public AttestationTypeNameModel(LinkageDao linkageDao, UserDao userdao,AreaDao areaDao) {
		this.linkageDao = linkageDao;
		this.userDao = userdao;
		this.areaDao=areaDao;
	}

	@Override
	public Object exec(List arg0) throws TemplateModelException {
		try{
			if (arg0.size() > 1) {
				if (arg0.get(0).equals("list")) {
					if (arg0.get(1).equals("usertype")) {
						return userDao.findAll();
					}
				}
				int argId=NumberUtils.getInt(arg0.get(0)+"");
				if (arg0.get(1).equals("name")){
					Linkage la=linkageDao.find(argId);
					if(la != null){
						return la.getName();
					}
				}
				if (arg0.get(1).equals("area")) {
					Area area=areaDao.find(argId);
					if(area != null){
						return area.getName();
					}					
				}
				if (arg0.get(1).equals("usertype")) {
					return userDao.find(NumberUtils.getLong(arg0.get(0)+""));
				}
				if (arg0.get(1).equals("account_type")) {
					String value = arg0.get(0).toString();		
					return linkageDao.getLinkageByValue(arg0.get(1).toString(), value);
				}
			}	
		}catch(Exception e){
			logger.error(e);
		}		
		return "-";
	}
}
