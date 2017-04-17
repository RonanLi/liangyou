package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserTypeDao;
import com.liangyou.dao.UserTypepurviewDao;
import com.liangyou.domain.Purview;
import com.liangyou.domain.User;
import com.liangyou.domain.UserType;
import com.liangyou.domain.UserTypepurview;
import com.liangyou.model.SearchParam;

@Repository
public class UserTypepurviewDaoImpl  extends ObjectDaoImpl<UserTypepurview> implements UserTypepurviewDao {
    Logger logger = Logger.getLogger(UserTypepurviewDaoImpl.class);

    @Autowired
    private UserTypeDao userTypeDao;
    @Autowired
    private UserDao userDao;
    public void addUserTypePurviews(List<Integer> purviewids,long user_type_id) {
    	UserType ut = new UserType();
    	ut.setTypeId(user_type_id);
    	List list = new ArrayList();
    	for(int i=0;i<purviewids.size();i++){
    		int id = (Integer)purviewids.get(i);
    		Purview p =  new Purview();
    		p.setId(id);
    		UserTypepurview utp = new UserTypepurview();    		
    		utp.setPurview(p);
    		utp.setUserType(ut);
    		this.merge(utp);
    	}
    }	
    
	public void delUserTypePurviews(long user_type_id) {		
		List list = userTypeDao.getUserTypepurviewsByUserTypeId(user_type_id);
		this.delete(list);
	}

	@Override
	public List<UserTypepurview> getUserTypepurviewList(long user_id) {
		
		User user = userDao.find(user_id);
		UserType ut = user.getUserType();
		SearchParam param = SearchParam.getInstance().addParam("userType", ut);
		return  this.findByCriteria(param);
	}
    
}
