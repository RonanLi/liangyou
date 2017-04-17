package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.MsgOperateDao;
import com.liangyou.domain.MsgOperate;
@Repository
public class MsgOperateDaoImpl extends ObjectDaoImpl<MsgOperate> implements MsgOperateDao {

	@Override
	public List<MsgOperate> getChilds(int pid) {
		String sql = "from MsgOperate where pid=?1 order by sort";
		Query query  = em.createQuery(sql);
		query.setParameter(1, pid);
		return query.getResultList();
	}

}
