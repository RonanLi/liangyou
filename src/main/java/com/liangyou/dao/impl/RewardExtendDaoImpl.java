package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.RewardExtendDao;
import com.liangyou.domain.RewardExtend;
import com.liangyou.util.NumberUtils;
/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz 
 * @author wujing
 *
 */
@Repository("rewardExtendDao")
public class RewardExtendDaoImpl extends ObjectDaoImpl<RewardExtend> implements
		RewardExtendDao {

	@Override
	public RewardExtend getReward(long tenderUserId, int type) {
		String jpql = "from RewardExtend where tenderUser.id = ?1 and rewardType = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, tenderUserId);
		query.setParameter(2, type);
		List list = query.getResultList();
		if (list.size()>0) {
			return (RewardExtend)list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List<RewardExtend> getRewardListByExtendWayStatus(int status,int extendWay) {
		String jpql = "from RewardExtend where status =?1 and rewardWay =?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, status);
		query.setParameter(2, extendWay);
		return query.getResultList();
	}

	@Override
	public double getSumMoneyRewardById(String[] ids) {
		if (ids==null || ids.length == 0) {
			return 0;
		}
		String jpql = "select sum(reward_money) from reward_extend where id in";
		String whereSql =doIdss(ids);
		jpql=jpql+whereSql.toString();
		logger.info("执行sql查询红包:"+jpql);
		Query query = em.createNativeQuery(jpql);
		List list = query.getResultList();
		if (list.size()>0) {
			return NumberUtils.getDouble(list.get(0).toString());
		}
		return 0;
	}

	@Override
	public int getRechargeSuccessByUser(long userId,int status) {
		String jpql = "select count(*) from account_recharge where user_id =?1 and status =?2";
		Query query = em.createNativeQuery(jpql);
		query.setParameter(1, userId);
		query.setParameter(2, status);
		List list = query.getResultList();
		if (list.size()>0) {
			return NumberUtils.getInt(list.get(0).toString());
		}
		return 0;
	}
	
	@Override
	public int getTenderSuccessByUser(long userId) {
		//v1.8.0.4_u4 TGPROJECT-398  qinjun 2014-08-04  start
		String jpql = "select count(1) from borrow_tender t left join borrow b on b.id = t.borrow_id  where t.user_id =?1 and "
				+ " (b.status in (6,7,8) or (b.status in (1,8) and b.type = 110) ) ";
		//v1.8.0.4_u4 TGPROJECT-398  qinjun 2014-08-04  end
		Query query = em.createNativeQuery(jpql);
		query.setParameter(1, userId);
		List list = query.getResultList();
		if (list.size()>0) {
			return NumberUtils.getInt(list.get(0).toString());
		}
		return 0;
	}
	
	
	@Override
	public List<RewardExtend> getRewardListByIds(String[] ids) {
		String jpql = "from RewardExtend where id in";
		String whereSql =doIdss(ids);
		jpql = jpql+whereSql;
		Query query = em.createQuery(jpql);
		List<RewardExtend> list = query.getResultList();
		return list;
	}


	private String doIdss(String[] ids){
		StringBuffer sb=new StringBuffer();
		sb.append("(");
		boolean falg = true;
		for (int i = 0; i < ids.length; i++) {
			if (!falg) {
				sb.append(",");
			}
			sb.append(ids[i]);
			falg = false;
		}
		sb.append(")");
		return sb.toString();
	}

}
