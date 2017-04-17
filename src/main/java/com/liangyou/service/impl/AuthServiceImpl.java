package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.liangyou.dao.PurviewDao;
import com.liangyou.dao.UserTypeDao;
import com.liangyou.dao.UserTypepurviewDao;
import com.liangyou.domain.Purview;
import com.liangyou.domain.PurviewModel;
import com.liangyou.domain.UserType;
import com.liangyou.domain.UserTypepurview;
import com.liangyou.service.AuthService;

@Service
@Transactional
public class AuthServiceImpl extends BaseServiceImpl implements AuthService {

	private Logger logger=Logger.getLogger(AuthServiceImpl.class);
	@Autowired
	private PurviewDao purviewDao;
	@Autowired
	private UserTypeDao userTypeDao;
	@Autowired
	private UserTypepurviewDao userTypepurviewDao;
	
	@SuppressWarnings("unchecked")
	public List getPurviewByUserid(long user_id) {
		return purviewDao.getPurviewByUserid(user_id);
	}
	
	public List getPurviewByPid(int pid) {
		return purviewDao.getPurviewByPid(pid);
	}
	
	public List getAllUserType() {
		return userTypeDao.getAllUserType();
	}
	
	public void addUserType(UserType userType) {
		userTypeDao.save(userType);
	}
	
	
	public void delUserType(long type_id) {
		userTypeDao.delete(type_id);
	}
	
	public void modifyUserType(UserType userType) {
		userTypeDao.update(userType);
	}
	
	public UserType getUserType(long type_id){
		return userTypeDao.find(type_id);
	}
	
	public List<Purview> getAllCheckedPurview(long type_id) {
		UserType  ut = userTypeDao.find(type_id);
		List<UserTypepurview> utps  = ut.getUserTypepurviews();
		List<Purview> list = new ArrayList<Purview>();
		for(int i=0;i<utps.size();i++){
			UserTypepurview utp = (UserTypepurview)utps.get(i);
			Purview p = utp.getPurview();
			list.add(purviewDao.find(p.getId()));//---
		}
		return list;
	}
	
	public Purview getPurview(long id) {
		return purviewDao.getPurview(id);
	}
	
	public  List<Purview> getAllPurview() {
		return purviewDao.findAll();
	}
	
	public void addPurview(Purview purview) {
		 purviewDao.save(purview);
	}
	
	public void delPurview(long id) {
		 purviewDao.delete(id);
	}
	
	public void modifyPurview(Purview purview) {
		purviewDao.update(purview);
	}
	
	public void addUserTypePurviews(List purviewid,long user_type_id) {
		try{
			userTypepurviewDao.delUserTypePurviews(user_type_id);
			userTypepurviewDao.addUserTypePurviews(purviewid,user_type_id);
		}catch(Exception e){
			logger.error(e);
		}		
	}
	@Override
	public List<Purview> getPurviewByAndLevel(int level, long pid,
			long userId) {
		
		return purviewDao.getPurviewByAndLevel(level,pid,userId);
	}
	
	
}
