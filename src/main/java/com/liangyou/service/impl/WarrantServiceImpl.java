package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.WarrantDao;
import com.liangyou.domain.Warrant;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.WarrantService;
/**
 * @author wujing 
 * @version 创建时间：2013-12-17 下午3:50:07
 * 类说明:担保公司
 */
@Service(value="warrantService")
@Transactional
public class WarrantServiceImpl extends BaseServiceImpl implements WarrantService {
	
	@Autowired
	private WarrantDao warrantDao;
	@Override
	public Warrant findWarrant(long id) {
		return warrantDao.find(id);
	}

	@Override
	public void updateWarrant(Warrant warrant) {
		warrantDao.update(warrant);
		
		
	}

	@Override
	public PageDataList<Warrant> findListWarrant(SearchParam param) {
		PageDataList<Warrant>  warrantList = warrantDao.findPageList(param);
		return warrantList;
	}

	
	@Override
	public List<Warrant> WarrantList() {
		return warrantDao.findAll();
	}

	@Override
	public void addWarrant(Warrant warrant) {
		warrantDao.save(warrant);
		
	}

	public WarrantDao getWarrantDao() {
		return warrantDao;
	}

	public void setWarrantDao(WarrantDao warrantDao) {
		this.warrantDao = warrantDao;
	}
	
	
}
