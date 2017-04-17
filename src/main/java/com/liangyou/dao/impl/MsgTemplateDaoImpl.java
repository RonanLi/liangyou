package com.liangyou.dao.impl;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.MsgTemplateDao;
import com.liangyou.domain.MsgTemplate;
@Repository("msgTemplateDao")
public class MsgTemplateDaoImpl extends ObjectDaoImpl<MsgTemplate> implements MsgTemplateDao {

}
