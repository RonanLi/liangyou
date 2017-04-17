package com.liangyou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.BorrowConfigDao;
import com.liangyou.dao.CreditRankDao;
import com.liangyou.dao.DrawBankDao;
import com.liangyou.dao.LinkageDao;
import com.liangyou.dao.StarRankDao;
import com.liangyou.dao.SystemDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.StarRank;
import com.liangyou.domain.SystemConfig;
import com.liangyou.model.SystemInfo;
import com.liangyou.service.AutoService;
import com.liangyou.service.SystemService;
import com.liangyou.web.action.UserAction;

@Service(value="systemService")
@Transactional
public class SystemServiceImpl implements SystemService {
	
	private static Logger logger = Logger.getLogger(UserAction.class);
	
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private DrawBankDao drawBankDao;
	@Autowired
	LinkageDao  linkageDao;
	@Autowired
	BorrowConfigDao borrowConfigDao;
	@Autowired
	AutoService autoService;
	@Autowired
	UserDao userDao;
	@Autowired
	CreditRankDao creditRankDao;
	@Autowired
	StarRankDao starRankDao;
	
	
	@Override
	public SystemInfo getSystemInfo() {
		SystemInfo info = new SystemInfo(); 
		List list = systemDao.getsystem();
		for (int i = 0; i < list.size(); i++) {
			SystemConfig sys = (SystemConfig) list.get(i);
			info.addConfig(sys);
		}
		return info;
	}

	@Override
	public List getSystemInfoForList() {		
		return systemDao.getsystem();
	}

	/**
	 * 根据模块显示系统设置信息
	 * @return
	 */
	@Override
	public List getSystemInfoForListBysytle(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSystemInfo(List list) {
		systemDao.update(list);
	}
	
	/**
	 * @author lijie
	 * @param url
	 * url 为网站根目录路径
	 */
	@Override
	public void clean(String url) {
		
	}

	@Override
	@Transactional
	public void updateSystem(long id) {
		SystemConfig sc=new SystemConfig();
    	sc=systemDao.find(5);
    	sc.setName(sc.getName()+"Test");
    	sc.setValue("sdfsdfsd");
	}
	
	@Override
	@Transactional
	public void addDrawBankList(List list){
		List all = drawBankDao.findAll();
		drawBankDao.delete(all);
		drawBankDao.save(list);
	}
	
	/**
	 * 更新 系统config
	 */
	@Override
	public void updateSystemInfo(){
		
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		ServletContext context = wac.getServletContext();
		
		List list=borrowConfigDao.findAll();
		Map map=new HashMap();
		for(int i=0;i<list.size();i++){
			BorrowConfig config=(BorrowConfig)list.get(i);
			map.put(config.getId(), config);
		}
		Global.BORROWCONFIG=map;
		
		SystemInfo info = getSystemInfo();
		Global.SYSTEMINFO = info;
		setWebConfig(context,info);
		//获取所有客服
		List allKefuList = userDao.getAllKefu();
		Global.ALLKEFU = allKefuList;
		context.setAttribute("allkefu",Global.ALLKEFU);
		//获取信用等级信息
		List<CreditRank> allCreditRank = creditRankDao.findAll();
		Global.ALLCREDITRANK = allCreditRank;
		List<StarRank> allStarRank = starRankDao.findAll();
		Global.ALLSTARRANK = allStarRank;
		
	}
	
	private void setWebConfig(ServletContext context, SystemInfo info){

		String[] webinfo=Global.SYSTEMNAME;
		for(String s:webinfo){
			logger.debug(s+":"+info.getValue(s));
			context.setAttribute(s, info.getValue(s));
		}
		context.setAttribute("webroot", context.getContextPath());
	}
	
}
