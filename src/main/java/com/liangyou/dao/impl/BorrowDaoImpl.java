package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.liangyou.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.tool.Page;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

@Repository(value="borrowDao")
public class BorrowDaoImpl extends ObjectDaoImpl<Borrow> implements BorrowDao  {

	private static Logger logger = Logger.getLogger(BorrowDaoImpl.class);

	@Override
	public List<Borrow> findAll(int start, int end, SearchParam param) {
		return null;
	}

	@Override
	public List<Borrow> unfinshBorrow(long userId) {
		String jpql=" from Borrow where user=?1 and status in (0,1) and type<>110";
		List<Borrow> list=new ArrayList<Borrow>();
		Query q=em.createQuery(jpql).setParameter(1, new User(userId));
		list=q.getResultList();
		return list;
	}

	@Override
	public List<Borrow> list() {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Borrow> query = builder.createQuery(Borrow.class);
		Root<Borrow> from = query.from(Borrow.class);
		TypedQuery<Borrow> typedQuery = getEntityManager().createQuery(
				query.select(from )
				.where(
						new Predicate[]{builder.equal((Expression)from.get("status"), 1),builder.equal((Expression)from.get("accountYes"), (Expression)from.get("account"))}
						)
				);
		List<Borrow> results = typedQuery.getResultList();
		return results;
	}

	@Override
	public List<Borrow> list(SearchParam param) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Borrow> query = builder.createQuery(Borrow.class);
		Root<Borrow> from = query.from(Borrow.class);
		Predicate[] p=param.bySearchFilter(Borrow.class, builder, query,from);
		TypedQuery<Borrow> typedQuery = em.createQuery(
				query.select(from)
				.where(p)
				);
		List<Borrow> results = typedQuery.getResultList();
		return results;
	}

	/**
	 * 原生态sql 更新投标 的 borrow
	 * @param id
	 * @param accountyes
	 * @param tenderTimes
	 * @return
	 */
	int i = 0;
	@Override
	public int updateBorrowAccountyes(long id , double accountyes, int tenderTimes){
		i+=tenderTimes;
		logger.info("zzzzzzzzzzzzzzzz " + i + " accountyes"+ accountyes +" ");
		String sql = " UPDATE Borrow SET accountYes = accountYes + ?1, tenderTimes = tenderTimes + ?2 " +
				" WHERE id = ?3  and  accountYes < account ";
		Query query = em.createQuery(sql);
		query.setParameter(1, accountyes);
		query.setParameter(2, tenderTimes);
		query.setParameter(3, id);
		int a =	query.executeUpdate();
		return a;
	} 

	@Override
	public List<Borrow> getBorrowListOrderByStatus() {
		String jpql=" from Borrow where status in (1,0,6,8,7) order by  ?1,?2,?3,?4,?5 ";
		List<Borrow> list=new ArrayList<Borrow>();
		Query q=em.createQuery(jpql);
		q.setParameter(1, "status =" + 1).setParameter(2, "status =" + 8)
		.setParameter(3, "status =" + 6).setParameter(4, "status =" + 7)
		.setParameter(5, "status =" + 0);
		list=q.getResultList();
		return list;
	}

	@Override
	public Borrow getAssignMentBorrowByTenderId(long tender_id){
		String jpql = " from Borrow where assignmentTender = ?1 and  isAssignment = 1 and status in (0,1,3,6,7,8) ";
		Query query = em.createQuery(jpql).setParameter(1, tender_id);
		List<Borrow> list = query.getResultList();
		if(list.size() == 0){
			return null;
		}else{
			return list.get(0);
		}
	}

	@Override
	public List<Borrow> getWaitCancelBorrow(long typeId) {
		//select a from OperateLog a join a.operateFlow b where  a.orderNo = ?1 and b.operateType = ?2  order by a.id desc
		String jpql = "select b from Borrow b join ";
		return null;
	}

	@Override
	public double getSuccessBorrowSumAccount(){
		String jpql = " SELECT SUM(account) FROM Borrow WHERE status in (3,6,7,8) ";
		Query query =  em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if(ob != null){
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}

	@Override
	public double getMaxAprBorrow(){
		String jpql = " SELECT MAX(apr) FROM Borrow WHERE status in (3,6,7,8) ";
		Query query =  em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if(ob != null){
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}

	@Override
	public int sumBorrowByDay() {
		String jpql = " SELECT COUNT(b) FROM Borrow b WHERE addtime>?1 and addtime<?2 ";
		Query query =  em.createQuery(jpql);

		query.setParameter(1, DateUtils.valueOf(DateUtils.dateStr2(new Date())));
		query.setParameter(2, new Date());
		Object ob = query.getSingleResult();
		//v1.8.0.4 TGPROJECT-5 zxc 2014-04-01 start
		if (ob !=null) {
			return NumberUtils.getInt(ob.toString()) +1;
		}
		return 1;
		//v1.8.0.4 TGPROJECT-5 zxc 2014-04-01 end
	}

	@Override
	public double sumBorrowAccount(long userId) {
		String jpql = " SELECT SUM(account) FROM Borrow WHERE status =1 and user.userId = ?1 ";
		Query query =  em.createQuery(jpql);
		query.setParameter(1, userId);
		Object ob = query.getSingleResult();
		if(ob != null){
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}
	//1.8.0.4_u3   TGPROJECT  qinjun 2014-06-25  start

	@Override
	public void updateBorrowStartDate(long borrowId, Date date) {
		String jpql = "UPDATE Borrow SET  startTenderTime = ?1 where id = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, date);
		query.setParameter(2, borrowId);
		int numb = query.executeUpdate();
		logger.info("审核跟新借款开始时间borrowId："+borrowId+"影响行数"+numb);
	}

	//1.8.0.4_u3   TGPROJECT  qinjun 2014-06-25  end
	//1.8.0.4_u4 TGPROJECT-345  wujing dytz  start
	public List getTenderUseAccount(double money){
		List list = new ArrayList();
		String sql = "select  t.user_id as userId ,sum(t.account) as money  from borrow_tender t "
				+ "inner join borrow b  on    t.borrow_id=b.id  where (b.status =6 or b.status =7 or b.status =8 or "
				+ "(b.status = 1 and b.type = 110 ) ) and b.type != 101 group by t.user_id ";
		if (money > 0) {
			sql=sql+" having money >="+money;
		}
		Query query = em.createNativeQuery(sql);
		list = query.getResultList();
		return list;
	}

	@Override
	public List getRecallAccount() {
		String sql = "select user_id as userId, sum(repayment_yescapital ) as capital   from borrow_tender  where status =1  group by user_id";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList(); 
		return list;
	}


	@Override
	public List getTenderRecall(long tenderUserId, double tenderAccount,
			double recallAccount) {
		String sql ="select  t.user_id as userId ,sum(t.account) as money ,sum(t.repayment_yescapital)  as yesmoney from borrow_tender t inner join borrow b  on    t.borrow_id=b.id  where (b.status =6 or b.status =7 or b.status =8 or (b.status = 1 and b.type = 110 ) ) and b.type != 101 and t.user_id = ?1 group by t.user_id having (money>=?2 and yesmoney>=?3 )";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, tenderUserId);
		query.setParameter(2, tenderAccount);
		query.setParameter(3, recallAccount);
		return query.getResultList();
	}

	//1.8.0.4_u4 TGPROJECT-345  wujing dytz  end
	@Override
	public List borrowByStatus(int borrowStatus) {
		String jpql=" from Borrow where status=?1 and type<>110 ";
		List<Borrow> list=new ArrayList<Borrow>();
		Query q=em.createQuery(jpql).setParameter(1, borrowStatus);
		list=q.getResultList();
		return list;
	}
	//系统当前融资总额
	@Override
	public double getBorrowSum() {
		String jpql = "SELECT SUM(account) FROM  BorrowTender  WHERE borrow.id in (select id from Borrow where((status IN(3,6,7,8) and type!=110) or (type=110))) ";
		Query query = em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}

	// 系统当前融资产生利息
	@Override
	public double getBorrowSumInterest() {
		String jpql = "SELECT SUM(interest) FROM  BorrowTender  WHERE borrow.id in (select id from Borrow where((status IN(3,6,7,8) and type!=110) or (type=110))) ";
		Query query = em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getDouble(ob.toString());
		}
		return 0;
	}
	
	public List<Borrow> getBorrowListByUserId(long userId,int type){
		String jpql=" from Borrow where user=?1 and status in (0,1) and type = ?2";
		List<Borrow> list=new ArrayList<Borrow>();
		Query q=em.createQuery(jpql);
		q.setParameter(1, new User(userId));
		q.setParameter(2, type);
		list=q.getResultList();
		return list;
	}

	/**
	 * add by gy 2016年12月14日15:19:35
	 * 获取wap端首页标的列表
	 * @return
	 */
	@Override
	public List<Borrow> getWapIndexBorrowList() {
		String jpql=" select b.* from borrow b where b.`status` in (1, 6, 7, 8) order by b.`status` asc, (b.account_yes/b.account) asc, b.addtime desc limit 4";
		Query query = em.createNativeQuery(jpql, Borrow.class);
		return query.getResultList();
	}

	/**
	 * add by gy 2016年12月14日15:19:35
	 * wap端获取标的列表
	 * @param param
	 * @param clazz
	 * @return
	 */
	@Override
	public PageDataList findWapBorrowPageListBySql(SearchParam param) {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		String datasql  = "select b.* from borrow b where 1 = 1";
		String countsql = "select count(1) from borrow b where 1 = 1";

		countsql= countsql + param.bySearchSqlFilter().toString();
		int count= getNamedParameterJdbcTemplate().queryForInt(countsql, paramMap);
		if(param.getPage()==null){
			param.addPage(count, 1, Page.ROWS);
		}
		param.addPage(count, param.getPage().getCurrentPage(), param.getPage().getPernum());
		datasql += param.bySearchSqlFilter().toString()/* + param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter()*/;
		datasql += " order by /*b.type desc, */b.status asc, (b.account_yes/b.account) asc, b.addtime desc";
		datasql += " limit " + param.getPage().getStart() + "," + param.getPage().getPernum();
		logger.info("-------=+++++++++++=lijing:"+datasql);
		List list = em.createNativeQuery(datasql, Borrow.class).getResultList();
//		List list = getNamedParameterJdbcTemplate().query(datasql, paramMap, getBeanMapper(clazz));
		return new PageDataList(param.getPage(),list);
	}

}
