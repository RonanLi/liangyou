package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.RewardRecordDao;
import com.liangyou.dao.RewardRecordSqlDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.RewardRecord;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.RewardRecordSql;
import com.liangyou.model.SearchParam;

/**
 * @Desc 奖励记录表   数据层接口实现类
 * @author yjt_anzi
 *
 */

@Repository(value = "rewardRecordDao")
public class RewardRecordDaoImpl extends ObjectDaoImpl<RewardRecord> implements
		RewardRecordDao {
	private static Logger logger = Logger.getLogger(RewardRecordDaoImpl.class);
	@Autowired
	RewardRecordSqlDao rewardRecordSqlDao;
	
	
	@Override
	public int updateRewardRecordstatus(long id){
		String sql = " UPDATE RewardRecord SET isEnable = ?1, useDate = now() " +
				" WHERE rewardRecordId = ?2 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, "1");
		query.setParameter(2, id);
		int a =	query.executeUpdate();
		return a;
	}

	@Override
	public List<RewardRecord> getRewardRecordById(long id, Long uId){
		List<RewardRecord> rl = new ArrayList<RewardRecord>();
		String sql = " from RewardRecord where rewardRecordId = ?1 and user = ?2";
		Query query = em.createQuery(sql);
		query.setParameter(1, id);
		query.setParameter(2, new User(uId));
		rl = query.getResultList();
		return rl;
	}
	public int findByDenominationCount(SearchParam param) {
		
		String sql ="SELECT COUNT(rr.reward_record_id) FROM reward_record rr\n" +
							"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id"+
							" where  \n" +
							"DATE_ADD(rr.create_date,INTERVAL  ac.reward_time_limit  DAY)>=rr.create_date\n";
							//"AND rr.isEnable = 0\n" +
							//"AND rr.denomination = 20";
							//"AND rr.user_id = 123"
		sql= sql +param.bySearchSqlFilter().toString()+param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
		Map<String, Object> param2 = new HashMap<String, Object>();
		return getNamedParameterJdbcTemplate().queryForInt(sql, param2);

		
	}

	@Override
	public List<RewardRecord> findByDenominationList(SearchParam param) {
		
		String sql ="SELECT rr.* FROM reward_record rr\n" +
							"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id"+
							" where  \n" +
							"DATE_ADD(rr.create_date,INTERVAL ac.reward_time_limit DAY)>=rr.create_date\n";
				//"AND rr.isEnable = 0\n" +
				//"AND rr.denomination = 20";
				//"AND rr.user_id = 123"
				sql= sql +param.bySearchSqlFilter().toString()+param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
				Map<String, Object> param2 = new HashMap<String, Object>();
				return getNamedParameterJdbcTemplate().query(sql, param2, getBeanMapper(RewardRecord.class));

	} 
	
	
	public List<RewardRecord> findCount(SearchParam param){
		String sql = "SELECT  denomination denomination,COUNT(rr.Reward_record_id) standbyFour FROM reward_record rr\n" +
							"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id"+
							" where  \n" +
							"DATE_ADD(rr.create_date,INTERVAL  ac.reward_time_limit  DAY)>=rr.create_date\n";
		sql= sql +param.bySearchSqlFilter().toString()+ param.byGroupBySqlFilter() +param.byOrderSqlFilter().toString() ;
		Map<String, Object> param2 = new HashMap<String, Object>();
		return getNamedParameterJdbcTemplate().query(sql, param2, getBeanMapper(RewardRecord.class));
		
	}

	//用户所有有效奖励
	@Override
	public List<RewardRecord> enableRewardByUser(String rewardType, User uInfo, Integer bRules) {
		String hql_str = " from RewardRecord where isEnable='0' and " +
				"rewardType=?1 and " +
				"user.userId =?2 and cast(activityConfig.borrowRules as integer) <= ?3 " +
				"group by denomination order by rewardRecordId";
		Query query = em.createQuery(hql_str);
		query.setParameter(1, rewardType);
		query.setParameter(2, uInfo.getUserId());
		query.setParameter(3, bRules);
		List<RewardRecord> list = query.getResultList();
		return list;
	}
	
	
	@Override
	public List<RewardRecordSql> findByValidityAll(SearchParam param){
		String sql = "SELECT 	rr.reward_record_id reward_record_id,\n" +
									"				rr.reward_type reward_type,\n" +
									"				rr.denomination denomination,\n" +
									"				rr.user_id user_id,\n" +
									"				DATE_ADD(rr.create_date, INTERVAL ac.reward_time_limit DAY) validity_date,\n" +
									"				rr.create_by create_by,\n" +
									"				rr.create_date create_date,\n" +
									"				rr.last_update_by last_update_by,\n" +
									"				rr.use_Date use_date,\n" +
									"				rr.is_enable is_enable"+
									"       \n" +
									"  FROM reward_record rr \n" +
									"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id"+
									"	where 1=1";
		sql= sql +param.bySearchSqlFilter().toString()+ param.byGroupBySqlFilter() +param.byOrderSqlFilter().toString() ;
		
		Map<String, Object> param2 = new HashMap<String, Object>();
		return rewardRecordSqlDao.getNamedParameterJdbcTemplate().query(sql, param2, getBeanMapper(RewardRecordSql.class));
	}
	
	/**
	 * 根根据条件查询各种红包的金额
	 * @param param
	 * @return
	 */
	public double findSumDenomination(SearchParam param){
		String sql ="SELECT sum(rr.denomination) FROM reward_record rr\n" +
					"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id"+
					" where 1=1 \n" ;
					//"DATE_ADD(rr.create_date,INTERVAL  ac.reward_time_limit  DAY)>=rr.create_date\n";
					//"AND rr.isEnable = 0\n" +
					//"AND rr.denomination = 20";
					//"AND rr.user_id = 123"
				sql= sql +param.bySearchSqlFilter().toString()+param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
				Map<String, Object> param2 = new HashMap<String, Object>();
				return getNamedParameterJdbcTemplate().queryForInt(sql, param2);

	}

	/**
	 * @Desc 后台-奖励记录报表
	 */
	@Override
	public PageDataList<RewardRecordSql> findByPage(SearchParam param) {
		String datasql ="SELECT\n" +
				"	u.username userName,\n" +
				"	rr.denomination,\n" +
				"	rr.create_date createDate,\n" +
				"	CASE rr.from_be WHEN '0' THEN '出借' ELSE rr.from_be END fromBe,\n" +
				"   CASE rr.is_enable WHEN '0' THEN '未使用' WHEN '1' THEN '已使用' WHEN '2' THEN '已过期' ELSE rr.is_enable END isEnable,\n" +
				"   rr.use_date useDate,\n" +
				"   rur.order_no orderNo,\n" +
				"   rr.remark\n" +
				"FROM\n" +
				"	reward_record rr\n" +
				"LEFT JOIN (\n" +
				"	SELECT\n" +
				"		reward_record_id,\n" +
				"		order_no\n" +
				"	FROM\n" +
				"		reward_use_record\n" +
				"	WHERE\n" +
				"		is_enable = '1'\n" +
				") rur ON rr.reward_record_id = rur.reward_record_id\n" +
				"LEFT JOIN borrow_tender bt ON rur.order_no = bt.sub_ord_id,\n" +
				" USER u\n" +
				"where\n" +
				"	rr.user_id = u.user_id\n" ;
		//TODO  sercher
				/*"ORDER BY\n" +
				"	rr.create_date";*/
		String countsql = "SELECT count(1) \n" +
				"FROM\n" +
				"	reward_record rr\n" +
				"LEFT JOIN (\n" +
				"	SELECT\n" +
				"		reward_record_id,\n" +
				"		order_no\n" +
				"	FROM\n" +
				"		reward_use_record\n" +
				"	WHERE\n" +
				"		is_enable = '1'\n" +
				") rur ON rr.reward_record_id = rur.reward_record_id\n" +
				"LEFT JOIN borrow_tender bt ON rur.order_no = bt.sub_ord_id,\n" +
				" USER u\n" +
				"where\n" +
				"	rr.user_id = u.user_id\n" ;
		
		Map<String, Object> param2 = new HashMap<String, Object>();
		PageDataList<RewardRecordSql> dataList = rewardRecordSqlDao.findPageListBySql(datasql, countsql, param, param2, RewardRecordSql.class);
		return dataList;
	}

	/**
	 * @Desc 根据用户ID 获取奖励数据 （分页）
	 */
	@Override
	public PageDataList<RewardRecordSql> findByUid(SearchParam param) {
		String datasql = "SELECT\n" +
				"		rr.reward_record_id rewardRecordId,\n" +
				"		CASE rr.reward_type WHEN '0' THEN '红包奖励' ELSE rr.reward_type END rewardType,\n" +
				"		rr.denomination denomination,\n" +
				"		DATE(DATE_ADD(rr.create_date, INTERVAL ac.reward_time_limit DAY)) validityDateStr,\n" +
				"		DATE_ADD(rr.create_date, INTERVAL ac.reward_time_limit DAY) validityDate,\n" +
				"		CASE rr.is_enable WHEN '0' THEN '未使用' WHEN '1' THEN '已使用' WHEN '2' THEN '已过期' ELSE rr.is_enable END isEnable,\n" +
				"		rp.use_rules useRules,\n"+
				"		rr.use_Date useDate,\n" +
				"		rr.remark,\n"+
				"		rr.user_id userId,\n" +
				"		rr.create_date createDate,\n" +
				"		rr.last_update_by lastUpdateBy\n" +
				"  FROM reward_record rr \n" +
				"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id\n" +
				"  LEFT JOIN (select distinct denomination,activity_id,use_rules from reward_param) rp ON ac.config_id = rp.activity_id\n"+
				"  where rr.denomination = rp.denomination\n";
		String countsql = "SELECT count(1) \n" +
				"FROM\n" +
				"	reward_record rr\n" +
				"  LEFT JOIN activity_config ac ON ac.config_id= rr.activity_id\n" +
				"  LEFT JOIN (select distinct denomination,activity_id,use_rules from reward_param) rp on ac.config_id = rp.activity_id\n"+
				"  where rr.denomination = rp.denomination";
		
		Map<String, Object> param2 = new HashMap<String, Object>();
		PageDataList<RewardRecordSql> dataList = rewardRecordSqlDao.findPageListBySql(datasql, countsql, param, param2, RewardRecordSql.class);
		return dataList;
	}
	
	/**
	 * @Desc 根据用户ID 统计用户奖励个数
	 * @param param
	 * @return
	 */
	public List countsByUid(SearchParam param){
		String countsSql =  "SELECT\n" +
							"	is_enable,\n" +
							"	count(1) countsnum\n" +
							"FROM\n" +
							"	reward_record\n" +
							"where 1=1";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		List countsList = rewardRecordSqlDao.findAllListBySql(countsSql,param, paramMap, RewardRecordSql.class);
		return countsList;
	}

	@Override
	public void updateStatusByDate(Date date) {
		String sql = " UPDATE RewardRecord SET isEnable = ?1 " +
				" WHERE createDate < ?2 and isEnable = ?3 ";
		Query query = em.createQuery(sql);
		query.setParameter(1, "2");
		query.setParameter(2, date);
		query.setParameter(3, "0");
		int a =	query.executeUpdate();
	}
}
