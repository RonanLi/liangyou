package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.TenderPropertyDao;
import com.liangyou.domain.TenderProperty;

/**
 * @author wujing 
 * @version 创建时间：2013-12-19 上午10:52:18
 * 类说明
 */
@Repository(value="tenderPropertyDao")
public class TenderPropertyDaoImpl extends ObjectDaoImpl<TenderProperty> implements TenderPropertyDao {

}
