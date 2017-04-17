package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.WebRepayLogDao;
import com.liangyou.domain.WebRepayLog;
/**
 * 垫付还款记录
 * @author wujing
 *
 */
@Repository(value="webRepayLogDao")
public class WebRepayLogDaoImpl extends ObjectDaoImpl<WebRepayLog> implements
		WebRepayLogDao {

}
