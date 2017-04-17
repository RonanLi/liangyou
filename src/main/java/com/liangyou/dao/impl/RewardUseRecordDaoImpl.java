package com.liangyou.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.RewardUseRecordDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.RewardUseRecord;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * @Desc 奖励使用记录(投标)  数据层接口实现类
 * @author yjt_anzi
 *
 */
@Repository(value="rewardUseRecordDao")
public class RewardUseRecordDaoImpl extends ObjectDaoImpl<RewardUseRecord>
		implements RewardUseRecordDao {

	@Override
	public List<RewardUseRecord> getRewardUseRecordBy(String orderNo) {
		String jpql=" from RewardUseRecord where orderNo = ?1";
		List<RewardUseRecord> list=new ArrayList<RewardUseRecord>();
		Query q=em.createQuery(jpql).setParameter(1, orderNo);
		list=q.getResultList();
		return list;
	}
	
	@Override
	public int updateRewardUseRecordstatus(long id){
		String sql = " UPDATE RewardUseRecord SET isEnable = ?1, lastUpdateDate = now() " +
				" WHERE rewardUseRecordId = ?2 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, "1");
		query.setParameter(2, id);
		int a =	query.executeUpdate();
		return a;
	} 
}
