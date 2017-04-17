package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.liangyou.api.chinapnr.UsrUnFreeze;
import com.liangyou.dao.AccountDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.model.account.BorrowSummary;
import com.liangyou.model.account.CollectSummary;
import com.liangyou.model.account.InvestSummary;
import com.liangyou.model.account.RepaySummary;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.account.WebAccountSumModel;
import com.liangyou.tool.Page;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Repository(value="accountDao")
public class AccountDaoImpl  extends ObjectDaoImpl<Account> implements AccountDao  {
    Logger logger = Logger.getLogger(AccountDaoImpl.class);
	@Override
	public UserAccountSummary getUserAccountSummary(long user_id) {
		return null;
	}
	//v1.8.0.4_u1 TGPROJECT-289 lx start
	public WebAccountSumModel getWebAccountSumModel(){
		StringBuffer webAccountSumSql=new StringBuffer();
		webAccountSumSql.append("SELECT ")
				.append(" sum(total) webTotal,sum(use_money) webUseMOney,sum(no_use_money) webNoUseMoney,sum(collection) webCollection,sum(repay) webRepay")
				.append(" from account");
		return (WebAccountSumModel) getNamedParameterJdbcTemplate().queryForObject(webAccountSumSql.toString(), new HashMap<String, Object>(), getBeanMapper(WebAccountSumModel.class));
	}
	//v1.8.0.4_u1 TGPROJECT-289 lx end
	@Override
	public void updateAccount(double total, double use, double nouse,long user_id) {
		String jpql="update Account set total=total+?1,useMoney=useMoney+?2,noUseMoney=noUseMoney+?3 where user=?4";
		Query q = em.createQuery(jpql).setParameter(1, total).setParameter(2, use)
				.setParameter(3, nouse).setParameter(4, new User(user_id));
		q.executeUpdate();
		em.refresh(getAcountByUser(new User(user_id)));//更新当前上下文的对象
	}
	
	/*@Override
	public void updateAccountBack(double total, double use, double nouse,long user_id) {
		String jpql="update Account set total=total-?1,useMoney=useMoney-?2,noUseMoney=noUseMoney-?3 where user=?4";
		Query q = em.createQuery(jpql).setParameter(1, total).setParameter(2, use)
				.setParameter(3, nouse).setParameter(4, new User(user_id));
		q.executeUpdate();
		em.refresh(getAcountByUser(new User(user_id)));//更新当前上下文的对象
	}*/
	
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 start
	/**
	 * 查询用户的投资详情
	 * @param tenderingParam
	 * @param tenderedParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BaseAccountSumModel> getBaseAccountSum(SearchParam tenderParam,SearchParam userParam){
		
		StringBuffer  tenderingSql = new StringBuffer();
		StringBuffer  tenderedSql = new StringBuffer();
		StringBuffer  userSql = new StringBuffer();
		StringBuffer  countSql = new StringBuffer();
		StringBuffer  baseSql = new StringBuffer();
		
		tenderingSql.append(" SELECT  ");
		tenderingSql.append(" borrow_tender.user_id user_id, COUNT(1) tenderingCount,  ");
		tenderingSql.append(" IFNULL(sum(borrow_tender.account),0) tenderingMoney  ");
		tenderingSql.append(" FROM borrow_tender borrow_tender  ");
		tenderingSql.append(" LEFT JOIN borrow borrow ON borrow.id = borrow_tender.borrow_id  ");
		tenderingSql.append(" WHERE borrow.status =1 ");
        tenderingSql.append(" " + tenderParam.bySearchSqlFilter() + tenderParam.byGroupBySqlFilter() + tenderParam.byOrderSqlFilter());
		
		tenderedSql.append(" SELECT ");
		tenderedSql.append(" borrow_tender.user_id user_id, ");
		tenderedSql.append(" COUNT(1) tenderedCount, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.account),0) tenderedMoney, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.repayment_yescapital),0) incomeCapital, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.repayment_yesinterest),0) incomeInterest, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.wait_account),0) waitCapital, ");
		tenderedSql.append(" IFNULL(SUM(borrow_tender.wait_interest),0) waitInterest ");
		tenderedSql.append(" FROM borrow_tender borrow_tender ");
		tenderedSql.append(" LEFT JOIN borrow borrow ON borrow.id = borrow_tender.borrow_id ");
		tenderedSql.append(" WHERE borrow.status >= 6 ");
		tenderedSql.append(" "+tenderParam.bySearchSqlFilter()+ tenderParam.byGroupBySqlFilter() + tenderParam.byOrderSqlFilter());
		
		baseSql.append(" FROM user user ");
		baseSql.append(" LEFT JOIN ( ");
		baseSql.append(  tenderingSql.toString());
		baseSql.append(" ) 	AS N ON user.user_id = N.user_id ");
		baseSql.append(" LEFT JOIN ( ");
		baseSql.append(  tenderedSql.toString());
		baseSql.append(" ) AS M ON user.user_id = M.user_id ");
		baseSql.append(" WHERE user.type_id = 2 ");
		baseSql.append(" " + userParam.bySearchSqlFilter()+ userParam.byGroupBySqlFilter() + userParam.byOrderSqlFilter());
		
		userSql.append(" SELECT ");
		userSql.append(" user.username, user.realname, user.card_id, user.phone, ");
		userSql.append(" N.tenderingCount,N.tenderingMoney, ");
		userSql.append(" M.tenderedCount,M.tenderedMoney,M.incomeCapital,M.incomeInterest ");
		userSql.append(baseSql);
		
		countSql.append(" SELECT COUNT(1) ");
		countSql.append( baseSql);
		
		Page page = userParam.getPage();
		if( page != null){ //存在分页
			int count = getNamedParameterJdbcTemplate().queryForInt(countSql.toString(),new BeanPropertySqlParameterSource(Integer.class));
			userParam.addPage(count, page.getCurrentPage(), page.getPernum());
			userSql.append(" limit " + userParam.getPage().getStart() + "," + userParam.getPage().getPernum());
		}//不存在分页就查询所有
		return getNamedParameterJdbcTemplate().query(userSql.toString(), new HashMap<String, Object>(), getBeanMapper(BaseAccountSumModel.class));
	}

	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 end
	@Override
	public int updateAccountNotZero(double total, double use, double nouse,long user_id) {
		String jpql="update Account set total=total+?1,useMoney=useMoney+?2,noUseMoney=noUseMoney+?3 where user=?4 and useMoney+?5>=0";
		Query q = em.createQuery(jpql).setParameter(1, total).setParameter(2, use)
				.setParameter(3, nouse).setParameter(4, new User(user_id))
				.setParameter(5, use);
		int a = q.executeUpdate();
		em.refresh(getAcountByUser(new User(user_id)));//更新当前上下文的对象
		return a;
	}

	@Override
	public void updateAccount(double total, double use, double nouse,double collect, double repay, long user_id) {
		String jpql="update Account set total=total+?1,useMoney=useMoney+?2,noUseMoney=noUseMoney+?3," +
				"collection=collection+?4,repay=repay+?5 where user=?6";
		Query q = em.createQuery(jpql).setParameter(1, total).setParameter(2, use)
				.setParameter(3, nouse).setParameter(4, collect)
				.setParameter(5, repay)
				.setParameter(6, new User(user_id));
		q.executeUpdate();
		em.refresh(getAcountByUser(new User(user_id)));//更新当前上下文的对象
	}

	@Override
	public Account getAcountByUser(User user) {
		String jpql = "from Account where user = ?1";
		Query query = em.createQuery(jpql).setParameter(1, user);
		List<Account> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			return null;
		}
	}
	
	// v1.8.0.4_u2 TGPROJECT-315 yl 2014-05-28 start
		@Override
		public RepaySummary getRepaySummary(long user_id) {
			String sql="select ifnull(sum(t.interest),0) as repayInterest," +
					" ifnull(min(t.repayment_time),null) as repayTime" +
					" from borrow_repayment t " +
					" left join borrow b on b.id = t.borrow_id" +
					" where t.repayment_yescapital <= 0 " +
					" and b.status in (6,7) and t.status!=1" +
					" and b.user_id =?1 order by t.repayment_time  ";
			//最近待还金额
			String RecentlySql = "SELECT IFNULL((t.repayment_account),0) AS repayTotal,"
					+ " IFNULL((t.capital),0) AS repayMoney  "
					+ "FROM borrow_repayment t  LEFT JOIN borrow b ON b.id = t.borrow_id "
					+ " WHERE t.repayment_yescapital <= 0  AND b.status IN (6,7) AND t.status!=1 "
					+ "AND b.user_id =?1 ORDER BY t.repayment_time asc limit 1"; 
			//逾期总额、迟还款总额、已还款总额
			StringBuffer lateSql =  new StringBuffer();
			lateSql.append(" SELECT * FROM ");
	        lateSql.append(" (SELECT IFNULL(SUM(br.repayment_account), 0) as lateRepayedTotal FROM borrow_repayment br LEFT JOIN borrow b ON br.borrow_id=b.id where br.repayment_time<br.repayment_yestime AND br.`status` = 1 AND b.user_id = ?1) as a ");
	        lateSql.append(" LEFT JOIN ");
	        lateSql.append(" (SELECT IFNULL(SUM(br.repayment_account), 0) AS lateToPay FROM borrow_repayment br LEFT JOIN borrow b ON br.borrow_id=b.id where br.repayment_time<br.repayment_yestime AND br.`status` = 0 AND b.user_id = ?2) as b ");
	        lateSql.append(" ON 1=1 ");
	        lateSql.append(" LEFT JOIN ");
	        lateSql.append(" (SELECT IFNULL(SUM(br.repayment_account), 0) AS hasPayed FROM borrow_repayment br LEFT JOIN borrow b ON br.borrow_id=b.id where br.`status` = 1 AND b.user_id = ?3) as c " );
	        lateSql.append(" ON 1=1 ");
			
			RepaySummary rs = new RepaySummary();
			List result = null;
			List RecentlyResult = null;
			try{			
				Query query = em.createNativeQuery(sql);
				query.setParameter(1, user_id);
				result = query.getResultList();
				if (result.size()>0){ 
					Iterator iterator = result.iterator(); 
					while( iterator.hasNext() ){ 
						Object[] row = ( Object[]) iterator.next();	
						
						double repayInterest = Double.parseDouble(row[0].toString());
						rs.setRepayInterest(repayInterest);
						
						Date repayTime = row[1] == null? null: ((Date)row[1]);
						rs.setRepayTime(DateUtils.getYYYYMMddHHmmss(repayTime));
					} 
				} 
				//待还金额
				Query RecentlyQuery = em.createNativeQuery(RecentlySql);
				RecentlyQuery.setParameter(1, user_id);
				RecentlyResult = RecentlyQuery.getResultList();
				if (RecentlyResult.size()>0){ 
					Iterator iterator = RecentlyResult.iterator(); 
					while( iterator.hasNext() ){ 
						Object[] row = ( Object[]) iterator.next();	
						double repayTotal = Double.parseDouble(  row[0]==null?"0":row[0].toString() );
						rs.setRepayTotal(repayTotal);
						
						double repayMoney = Double.parseDouble(  row[1]==null?"0":row[1].toString() );
						rs.setRepayAccount(repayMoney);//最近待还的本金
					} 
				} 
				//逾期总额、迟还款总额、已还款总额
				 Query queryLate = em.createNativeQuery(lateSql.toString())
						.setParameter(1, user_id)
						.setParameter(2, user_id)
						.setParameter(3, user_id);
			     List lateList = queryLate.getResultList();
			     if(lateList.size()>0){
			    	 Iterator iterator = lateList.iterator();
			    	 while (iterator.hasNext()) {
			    		 Object[] row = ( Object[]) iterator.next();
			    		 double lateRepayedTotal = Double.parseDouble(row[0].toString());
			    		 double lateToPay = Double.parseDouble(row[1].toString());
			    		 double hasPayed = Double.parseDouble(row[2].toString());
			    		 rs.setLateRepayed(lateRepayedTotal);
			    		 rs.setLateTopay(lateToPay);
			    		 rs.setHasRepayed(hasPayed);
					}
			     }
			}catch(Exception e){
				e.printStackTrace();
				logger.info("数据查询出错！，sql:"+sql);
				logger.error(e);
			}
			return rs;
		}
		// v1.8.0.4_u2 TGPROJECT-315 yl 2014-05-28 end
	
	public BorrowSummary getBorrowSummary(long user_id) {
		String sql="select ifnull(sum(b.account),0) as borrowTotal," +
				" ifnull(sum(b.repayment_account-b.account),0) as borrowInterest," +
				" ifnull(count(1),0)as borrowTimes,b.user_id " +
				" from borrow b " +
				"where b.user_id=?1 and b.status in (6,7,8) ";
		List result = null;
		BorrowSummary b = new BorrowSummary();
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			result = query.getResultList();
			if (result.size()>0){ 
				Iterator iterator = result.iterator(); 
				while( iterator.hasNext() ){ 
					Object[] row = ( Object[]) iterator.next();					
					double borrowTotal = Double.parseDouble(row[0].toString());
					double borrowInterest = Double.parseDouble(row[1].toString());
					int borrowTimes = Integer.parseInt(row[2].toString());
					b.setBorrowInterest(borrowInterest);
					b.setBorrowTotal(borrowTotal);
					b.setBorrowTimes(borrowTimes);
				} 
			} 
			
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}
		return b;
	}
	
	@Override
	public List<InvestSummary>  getInvestSummaryList(int type){
		String sql="";
		//v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 start
		switch (type) {
		case 1:
			sql="select ifnull(sum(t.account),0) as investTotal,  ifnull(sum(t.interest),0) as investInterest, "
					+ " ifnull(count(1),0) as investTimes ,t.user_id user_id,u.username username from borrow_tender t left join borrow b on b.id=t.borrow_id left join user u on u.user_id=t.user_id where b.status in(6,7,8) and  "
					+ " year(t.addtime)=year(sysdate()) group by t.user_id order by investTotal desc limit 0,10";
			break;
		case 2:
			sql="select ifnull(sum(t.account),0) as investTotal,  ifnull(sum(t.interest),0) as investInterest, "
					+ " ifnull(count(1),0) as investTimes ,t.user_id user_id ,u.username username from borrow_tender t left join borrow b on b.id=t.borrow_id left join user u on u.user_id=t.user_id where b.status in(6,7,8) and  "
					+ "quarter(t.addtime)=quarter(sysdate()) and year(t.addtime)=year(sysdate()) group by t.user_id order by investTotal desc limit 0,10";
			break;
		case 3:
			sql="select ifnull(sum(t.account),0) as investTotal,  ifnull(sum(t.interest),0) as investInterest, "
					+ " ifnull(count(1),0) as investTimes,t.user_id user_id,u.username username from borrow_tender t left join borrow b on b.id=t.borrow_id left join user u on u.user_id=t.user_id where b.status in(6,7,8)  and "
					+ "month(t.addtime)=month(sysdate()) and year(t.addtime)=year(sysdate()) group by t.user_id order by investTotal desc limit 0,10";
			break;
		default:
			sql="select ifnull(sum(t.account),0) as investTotal,  ifnull(sum(t.interest),0) as investInterest, "
					+ " ifnull(count(1),0) as investTimes,t.user_id user_id,u.username username from borrow_tender t left join borrow b on b.id=t.borrow_id left join user u on u.user_id=t.user_id where b.status in(6,7,8)   "
					+ " group by t.user_id order by investTotal desc limit 0,10";
			break;
		}
		//v1.8.0.4_u2 TGPROJECT-326 lx 2014-05-29 end
		
		List<InvestSummary> list=new ArrayList<InvestSummary>();
		List result = null;
		try{			
			Query query = em.createNativeQuery(sql);
			//query.setParameter(1, days);
			result = query.getResultList();
			if (result.size()>0){ 
				Iterator iterator = result.iterator(); 
				while( iterator.hasNext() ){ 
					InvestSummary i=new InvestSummary();
					Object[] row = ( Object[]) iterator.next();					
					double investTotal = Double.parseDouble(row[0].toString());
					double investInterest = Double.parseDouble(row[1].toString());
					int investTimes = Integer.parseInt(row[2].toString());
					String username = row[4].toString();
					i.setInvestTotal(investTotal);
					i.setInvestInterest(investInterest);
					i.setInvestTimes(investTimes);
					i.setUsername(username);
					list.add(i);					
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}		
		return list;	
	}
	public InvestSummary getInvestSummary(long user_id) {
		String sql="select ifnull(sum(t.account),0) as investTotal, " +
				" ifnull(sum(t.interest),0) as investInterest, " +
				" ifnull(count(1),0) as investTimes" +
				" from borrow_tender t " +
				" left join borrow b on b.id=t.borrow_id " +
				" where b.status in(6,7,8) " +
				" and t.user_id=?1";
		InvestSummary i=new InvestSummary();
		List result = null;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			result = query.getResultList();
			if (result.size()>0){ 
				Iterator iterator = result.iterator(); 
				while( iterator.hasNext() ){ 
					Object[] row = ( Object[]) iterator.next();					
					double investTotal = Double.parseDouble(row[0].toString());
					double investInterest = Double.parseDouble(row[1].toString());
					int investTimes = Integer.parseInt(row[2].toString());
					i.setInvestTotal(investTotal);
					i.setInvestInterest(investInterest);
					i.setInvestTimes(investTimes);
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}		
		return i;		
	}
	
	
	
	public CollectSummary getCollectSummary(long user_id) {
		CollectSummary c = new CollectSummary();
		
		String sql="  select ifnull(c.repay_account,0) as collectMoney, " +
			        " ifnull(c.interest ,0) collectInterest, " + 
					" ifnull((c.repay_time),null) as collectTime, " +
					" ifnull((c.repay_account),0) as collectTotal, " +
					" ifnull((c.interest),0) as collectInterestTotal " +
					" from borrow_collection c " +
					" left join borrow_tender t on c.tender_id = t.id and t.wait_account > 0 " +
					" left join borrow b on b.id = t.borrow_id and (b.status in(6,7) or (b.type=110 and b.status in(1,6,7,8))) " +
					" where  c.repay_time is not null and c.repay_yestime is null and  t.user_id = ?1 and t.status = 0 " +
					" order by c.repay_time asc ";
		
		
		List list = null ;
		try{
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			list  = query.getResultList();
			if (list.size()>0){ 
				Object[] row = ( Object[]) list.get(0);					
				double collectMoney  = Double.parseDouble(row[0] == null?"0":row[0].toString());
				c.setCollectMoney(collectMoney);
				
				double collectInterest  = Double.parseDouble(row[1]==null?"0":row[1].toString());
				c.setCollectInterest(collectInterest);
				
				Date collectTime = row[2] == null? null: ((Date)row[2]);
				c.setCollectTime(DateUtils.getYYYYMMddHHmmss(collectTime));

				double collectTotal1  = 0.00;
				double collectInterestTotal1  = 0.00;
				for (Object l : list) {
					Object[] row1 = (Object[]) l;
					collectTotal1 +=  Double.parseDouble(row1[3].toString());
					collectInterestTotal1 += Double.parseDouble(row1[4].toString());
				}
				
				//double collectTotal  = Double.parseDouble(row[3].toString());
				c.setCollectTotal(collectTotal1);
				
				//double collectInterestTotal  = Double.parseDouble(row[4].toString());
				c.setCollectInterestTotal(collectInterestTotal1);
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		
		return c;
	}
	
	public double getChargeTotal(long user_id) {
		String sql="select ifnull(sum(money),0) as num from account_recharge where status=1 and user_id=?1";
		List list = null;
		double num = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			list  = query.getResultList();
			Object ob = list.get(0);
			num = NumberUtils.getDouble(ob.toString());
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return num;
	}
	
	public double getCacheTotal(long user_id) {
		String sql="select ifnull(sum(total),0) as num from account_cash where status=1 and user_id=?1";
		List list = null;
		double num = 0;
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			list  = query.getResultList();
			Object ob = list.get(0);
			num = NumberUtils.getDouble(ob.toString());
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return num;
	}
	
	/**
	 *正在投标、等待回款、等待还款、已经还款笔数
	 */
	
	@Override
	public int[] getCountRusultBySql(long userId ) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM ")
		   .append(" ( SELECT COUNT(1) AS tenderingCount FROM borrow_tender WHERE status = 0 AND wait_account = 0 AND wait_interest = 0 AND user_id = ?1 ) AS a ")
		   .append(" LEFT JOIN")
		   .append(" ( SELECT COUNT(1) AS waitCollect FROM borrow_tender WHERE status = 0 AND wait_account > 0 AND wait_interest > 0 AND user_id = ?2 ) AS b ")
		   .append(" ON 1 = 1 ")
		   .append(" LEFT JOIN ")
		   .append(" ( SELECT COUNT(1) AS hasCollect FROM borrow_tender WHERE status = 1 AND wait_account = 0 AND wait_interest = 0 AND user_id = ?3 ) AS c ")
		   .append(" ON 1 = 1")
		   .append(" LEFT JOIN ")
		   .append(" ( SELECT count(1) AS waitrepay FROM borrow_repayment br LEFT JOIN borrow b ON br.borrow_id = b.id WHERE ( br. STATUS = 0 OR ( br. STATUS = 2 AND br.webstatus = 3 )) AND b.user_id = ?4 ) AS d ")
		   .append(" ON 1 = 1")
		   .append(" LEFT JOIN ")
		   .append(" ( SELECT count(1) AS hasrepay FROM borrow_repayment br LEFT JOIN borrow b ON br.borrow_id = b.id WHERE br. STATUS = 1 AND b.user_id = ?5 ) AS e ")
		   .append(" ON 1 = 1 ");
		
		List list = null;
		int[] countArray = {0,0,0,0,0};
		try{			
			Query query = em.createNativeQuery(sql.toString())
					.setParameter(1, userId)
					.setParameter(2, userId)
					.setParameter(3, userId)
					.setParameter(4, userId)
					.setParameter(5, userId);
			
			list  = query.getResultList();
			if (list.size()>0){ 
				Iterator iterator = list.iterator(); 
				while( iterator.hasNext() ){ 
					Object[] row = ( Object[]) iterator.next();	
					int tenderingCount = Integer.parseInt(row[0].toString());
					int waitCollect = Integer.parseInt(row[1].toString());
					int hasCollect = Integer.parseInt(row[2].toString());
					int waitrepay = Integer.parseInt(row[3].toString());
					int hasrepay = Integer.parseInt(row[4].toString());
					countArray[0]= tenderingCount;
					countArray[1]= waitCollect;
					countArray[2]= hasCollect;
					countArray[3]= waitrepay;
					countArray[4]= hasrepay;
				} 
			} 
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql.toString());
			logger.error(e);
		}	
		return countArray;
	}
	
	@Override
	public double[] getUserIncoming(long user_id) {
		String sql=" select ifnull(sum(repayment_yesinterest),0) as yesInterest, ifnull(sum(repayment_yescapital),0) as yesaccount"
				+ " from borrow_tender where user_id=?1 ";
		List list = null;
		double num = 0;
		double[] yesArray = {0,0};
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			list  = query.getResultList();
			if(list.size()>0){
				Iterator iterator = list.iterator();
				while( iterator.hasNext() ){ 
					Object[] row = ( Object[]) iterator.next();	
					double yesInterest = Double.parseDouble(row[0].toString());
					double yesaccount = Double.parseDouble(row[1].toString());
					yesArray[0] = yesInterest;
					yesArray[1] = yesaccount;
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return yesArray;
	}

	@Override
	public double getAllPropertyBorrowRepayAccount(long user_id) {
		String jsql="select SUM(a.repaymentAccount) from BorrowRepayment a inner join a.borrow b where b.type=104 and a.status=0 and a.webstatus =0 and b.user = ?1 ";
	    Query query = em.createQuery(jsql).setParameter(1, new User(user_id));
	    return  NumberUtils.getDouble(StringUtils.isNull(query.getSingleResult()));
	}
	
	@Override
	public double getSumAwardDeductByUser(long user_id) {
		String jsql="select SUM(money) from AccountLog where type='award_deduct' and to_user=?1 ";
	    Query query = em.createQuery(jsql).setParameter(1, new User(user_id));
	    return  NumberUtils.getDouble(StringUtils.isNull(query.getSingleResult()));
	}
	
	@Override
	public double[] getWaitFullSummary(long user_id) {
		String sql=" select ifnull(count(1),0) as tenderCount, ifnull(sum(t.account),0) as tenderTotal" +
				" from borrow_tender t left join borrow b on b.status = 1 where t.user_id=?1 ";
		List list = null;
		double num = 0;
		double[] yesArray = {0,0};
		try{			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, user_id);
			list  = query.getResultList();
			if(list.size()>0){
				Iterator iterator = list.iterator();
				while( iterator.hasNext() ){ 
					Object[] row = ( Object[]) iterator.next();	
					int tenderCount = Integer.parseInt(row[0].toString());
					double tenderTotal = Double.parseDouble(row[1].toString());
					yesArray[0] = tenderCount;
					yesArray[1] = tenderTotal;
				} 
			}
		}catch(Exception e){
			logger.info("数据查询出错！，sql:"+sql);
			logger.error(e);
		}	
		return yesArray;
	}
	
	@Override
	public void testBuilder(){
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Date> query = builder.createQuery(Date.class);
		Root<Date> from = query.from(Date.class);
		Expression<java.sql.Date> date1 = builder.currentDate();
		Expression<Integer> second = builder.function("second", Integer.class, date1); 
		query.select(from);
		TypedQuery<Date> typedQuery = em.createQuery(query);
		List<Date> results = typedQuery.getResultList();
		for (Date date : results) {
			System.out.println();
		}
		
	}
	
	public int getSuccessAccountCash(long userId){
		String sql= "select count(*) from account_cash where user_id=?1 and  (deal_status=1 or deal_status=4 ) and month(addtime)=month(now())";
		List list = null;
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, userId);		
		list  = query.getResultList();
		Object ob = null;
		if(list!=null && list.size()>0){
			ob = list.get(0);
		}		
		int num = NumberUtils.getInt(ob.toString());
		logger.info("成功取现笔数："+num);
		return num;
		
	}
	
}
