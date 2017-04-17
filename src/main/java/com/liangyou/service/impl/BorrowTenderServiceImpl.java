package com.liangyou.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.domain.BorrowTender;
import com.liangyou.service.BorrowTenderService;

@Service(value = "borrowTenderService")
@Transactional
public class BorrowTenderServiceImpl extends BaseServiceImpl implements BorrowTenderService{

	@Autowired
    private BorrowTenderDao borrowTenderDao;
	
	@Override
	public BorrowTender getListByUserId(long userId) {
		return borrowTenderDao.getListByUserId(userId);
	}

	@Override
	public void updateBorrowTender(BorrowTender borrowTender) {
		borrowTenderDao.update(borrowTender);
	}

}
