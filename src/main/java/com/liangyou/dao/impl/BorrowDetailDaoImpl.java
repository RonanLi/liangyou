package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowDetailDao;
import com.liangyou.domain.BorrowDetail;
import com.liangyou.domain.BorrowDetailType;

/**
 *v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  
 */
@Repository(value="borrowDetailDao")
public class BorrowDetailDaoImpl extends ObjectDaoImpl<BorrowDetail> implements BorrowDetailDao {

}
