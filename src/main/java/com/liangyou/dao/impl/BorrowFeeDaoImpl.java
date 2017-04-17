package com.liangyou.dao.impl;


import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowFeeDao;
import com.liangyou.domain.BorrowFee;
@Repository(value="borrowFee")
public class BorrowFeeDaoImpl extends ObjectDaoImpl<BorrowFee> implements BorrowFeeDao {

}
