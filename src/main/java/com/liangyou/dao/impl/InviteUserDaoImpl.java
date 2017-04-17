package com.liangyou.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.InviteUserDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.InviteUserSummary;
import com.liangyou.tool.Page;
import com.liangyou.util.NumberUtils;


@Repository
public class InviteUserDaoImpl extends ObjectDaoImpl<InviteUser> implements InviteUserDao {

	@Override
	public List<InviteUser> getInvitreUser(long inviteUserid) {

		String jpql = " from InviteUser where inviteUser = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(inviteUserid));
		List list = query.getResultList();
		return list;
	}
	
	public InviteUser getInviter(long userid) {

		String jpql = " from InviteUser where user = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, new User(userid));
		List list = query.getResultList();
		InviteUser inviteUser = null;
		if(list!=null && list.size()>0){			
			inviteUser = (InviteUser)list.get(0);
		}
		return inviteUser;
	}
	
	//v1.8.0.4 TGPROJECT-249 lx start
	public List<InviteUserSummary> getInvitreUserBySearchParam(SearchParam  param,SearchParam  tenderParam){
		StringBuffer  tenderedSql = new StringBuffer();
		StringBuffer  inviteUserSql = new StringBuffer();
		StringBuffer  countSql = new StringBuffer();
		StringBuffer  baseSql = new StringBuffer();
		tenderedSql.append(" SELECT ");
		tenderedSql.append(" borrow_tender.user_id user_id, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.account),0) tenderedMoney");
		tenderedSql.append(" FROM borrow_tender borrow_tender ");
		tenderedSql.append(" LEFT JOIN borrow borrow ON borrow.id = borrow_tender.borrow_id ");
		tenderedSql.append(" WHERE borrow.status >= 6 ");
		tenderedSql.append(" "+tenderParam.bySearchSqlFilter()+ tenderParam.byGroupBySqlFilter() + tenderParam.byOrderSqlFilter());
		
		baseSql.append(" FROM invite_user invite_user ");
		baseSql.append(" LEFT JOIN ( ");
		baseSql.append(  tenderedSql.toString());
		baseSql.append(" ) AS M ON invite_user.user_id = M.user_id ");
		baseSql.append(" LEFT JOIN user user1 on  invite_user.user_id = user1.user_id ");
		baseSql.append(" LEFT JOIN user user2 on  invite_user.invite_user = user2.user_id ");
		baseSql.append(" WHERE 1=1");
		baseSql.append(" " + param.bySearchSqlFilter()+ param.byGroupBySqlFilter() + param.byOrderSqlFilter());
		
		inviteUserSql.append(" SELECT ");
		inviteUserSql.append(" invite_user.user_id inviteUserId, user1.username username, user2.username inviteUsername, invite_user.addtime addtime, ");
		inviteUserSql.append("M.tenderedMoney tenderedMoney");
		inviteUserSql.append(baseSql);
		
		countSql.append(" SELECT COUNT(1) ");
		countSql.append( baseSql);
		
		Page page = param.getPage();
		if( page != null){ //存在分页
			int count = getNamedParameterJdbcTemplate().queryForInt(countSql.toString(),new BeanPropertySqlParameterSource(Integer.class));
			param.addPage(count, page.getCurrentPage(), page.getPernum());
			inviteUserSql.append(" limit " + param.getPage().getStart() + "," + param.getPage().getPernum());
		}//不存在分页就查询所有
		return getNamedParameterJdbcTemplate().query(inviteUserSql.toString(), new HashMap<String, Object>(), getBeanMapper(InviteUserSummary.class));
	}
	//v1.8.0.4 TGPROJECT-249 lx end
	
	//v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-22 start
	public double getInvitreUser(User user,Borrow b){
		Map<String ,Object> map = new HashMap<String, Object>();
		map.put("user_id", user.getUserId());
		map.put("verifytime", b.getVerifyTime());
		StringBuffer  inviteUserSql = new StringBuffer();
		inviteUserSql.append(" SELECT ");
		inviteUserSql.append(" IFNULL(SUM(borrow_tender.account),0) tenderedMoney");
		inviteUserSql.append(" FROM borrow_tender borrow_tender ");
		inviteUserSql.append(" LEFT JOIN borrow borrow ON borrow.id = borrow_tender.borrow_id ");
		inviteUserSql.append(" WHERE borrow.status >= 6 ");
		inviteUserSql.append(" AND borrow_tender.user_id =:user_id ");
		inviteUserSql.append(" AND month(borrow.verify_time)=month(:verifytime) AND year(borrow.verify_time)=year(:verifytime) ");
		
		double sum = 0.0;
		SqlRowSet rs = this.getNamedParameterJdbcTemplate().queryForRowSet(inviteUserSql.toString(), map);
		if (rs.next()) {
			sum = rs.getDouble("tenderedMoney");
		}
		return sum;
	}
	//v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-22 end
	
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 获取推荐人推荐用户（用户必须投标过并且投标金额不为0）个数
	 * @param user
	 * @return
	 */
	@Override
	public int getInviteUserTotal(User user){
		String sql = " SELECT count(i) from InviteUser i where i.inviteUser = ?1 and i.isTender = 1 and i.tenderTotal > 0 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, user);
		Object ob = query.getSingleResult();
		if (ob !=null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
}
