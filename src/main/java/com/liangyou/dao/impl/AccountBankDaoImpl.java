package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.AccountBankDao;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.User;
@Repository
public class AccountBankDaoImpl extends ObjectDaoImpl<AccountBank> implements AccountBankDao {

	@Override
	public AccountBank getBankByNo(String cardNo,String apiId) {
		String jpql="from AccountBank ac  where ac.account=?1 and ac.user.apiId=?2 and ac.status = 1 ";
		Query query = em.createQuery(jpql).setParameter(1, cardNo).setParameter(2, apiId);
		List<AccountBank> list = query.getResultList();
		if (list.size()==0) {
			return null;
		}else{
			return list.get(0);
		}
	}

	@Override
	public List<AccountBank> getBankByUserId(long userId, int isbuild) {
		String jpql = "from AccountBank ac  where ac.user=?1 and ac.isbind=?2 and ac.status =1 ";//0：关闭，1：启用
		Query query  = em.createQuery(jpql).setParameter(1, new User(userId)).setParameter(2, isbuild);
		List<AccountBank> list = query.getResultList();
		return list;
	}
	
	/**
	 * 获取有效的提现银行
	 * @param userId
	 * @return
	 */
	@Override
	public List<AccountBank> getAllBankList(long userId){
		String sql = "from AccountBank where user=?1 and status =1 ";
		Query query = em.createQuery(sql).setParameter(1, new User(userId)); 
		return query.getResultList();
	}
	//v1.8.0.4_u2 TGPROJECT-327 lx 2014-05-30 start
	@Override
	public AccountBank getAccountBankByCardNo(String cardNo, User user){
		String sql = " from AccountBank where user =?1 and  account = ?2   and status=1 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, user)
		     .setParameter(2, cardNo);
		List<AccountBank> list = query.getResultList();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	//v1.8.0.4_u2 TGPROJECT-327 lx 2014-05-30 end

}
