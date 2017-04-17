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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.LotteryDao;
import com.liangyou.domain.Prize;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.lottery.LotteryDetail;
import com.liangyou.model.prize.MyPrize;
import com.liangyou.tool.Page;

@Repository(value = "lotteryDao")
public class LotteryDaoImpl extends ObjectDaoImpl<Prize> implements LotteryDao {

	@Override
	public List<Prize> findByParam(SearchParam sp) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Prize> query = builder.createQuery(Prize.class);
		Root<Prize> from = query.from(Prize.class);
		query.orderBy(builder.asc(from.get("prizeId")));
		Predicate[] p = sp.bySearchFilter(Prize.class, builder, query, from);
		TypedQuery<Prize> tq = em.createQuery(query.select(from).where(p));

		return tq.getResultList();
	}

	@Override
	public List<MyPrize> getPrizesByUserId(long User_Id) {
		String sql = ""
				+ "SELECT prize.prize_name, prize_user_relationship.receive_time, prize_user_relationship.status, prize.img_url " 
				+ "FROM prize_user_relationship, prize " 
				+ "WHERE prize_user_relationship.user_id = ?1 and prize_user_relationship.prize_id = prize.prize_id and prize_user_relationship.receive_state = 1;";
		List<MyPrize> list = new ArrayList<MyPrize>();
		List result = null;
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, User_Id);
			result = query.getResultList();
			if (result.size() > 0) {
				Iterator iterator = result.iterator();
				while (iterator.hasNext()) {
					MyPrize mp = new MyPrize();
					Object[] row = (Object[]) iterator.next();
					String prizename = row[0].toString();
					Date prizetime = (Date) row[1];
					String prizestutas = row[2].toString();
					// String exchangeCode = row[3].toString();
					String prizeImgUrl = row[3].toString();
					mp.setPrizeName(prizename);
					mp.setPrizeTime(prizetime);
					mp.setPrizeStatus(prizestutas);
					mp.setExchangeCode("");
					mp.setPrizeImgUrl(prizeImgUrl);
					list.add(mp);
				}
			}
		} catch (Exception e) {
			logger.info("数据查询出错！，sql:" + sql);
			logger.error(e);
		}
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageDataList<LotteryDetail> findLotteryDetailPageListBySql(SearchParam param) {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		String datasql = "select u.username as userName, u.realname as realName, p.prize_name as prizeName, pur.status as status, pur.real_receive_time as realReceiveTime, pur.use_time as useTime, pd.detail as prizeDetail from  prize p, `user` u, prize_user_relationship pur LEFT JOIN prize_detail pd on pur.id = pd.prize_user_id where u.user_id = pur.user_id and pur.prize_id = p.prize_id";
		String countsql = "select count(1) from prize p, `user` u, prize_user_relationship pur LEFT JOIN prize_detail pd on pur.id = pd.prize_user_id where u.user_id = pur.user_id and pur.prize_id = p.prize_id";

		countsql = countsql + param.bySearchSqlFilter().toString();
		int count = getNamedParameterJdbcTemplate().queryForInt(countsql, paramMap);
		if (param.getPage() == null) {
			param.addPage(count, 1, Page.ROWS);
		}
		param.addPage(count, param.getPage().getCurrentPage(), param.getPage().getPernum());
		datasql = datasql + param.bySearchSqlFilter().toString() + param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
		datasql += " limit " + param.getPage().getStart() + "," + param.getPage().getPernum();
		logger.info("findPageListBySql: " + datasql);
		List<LotteryDetail> list = getNamedParameterJdbcTemplate().query(datasql, paramMap, getBeanMapper(LotteryDetail.class));
		return new PageDataList<LotteryDetail>(param.getPage(), list);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<LotteryDetail> exportLotteryDetailListBySql(SearchParam param) {
		Map<String, Object> params = new HashMap<String, Object>();
		String datasql = "select u.username as userName, u.realname as realName, p.prize_name as prizeName, pur.status as status, pur.real_receive_time as realReceiveTime, pur.use_time as useTime, pd.detail as prizeDetail from  prize p, `user` u, prize_user_relationship pur LEFT JOIN prize_detail pd on pur.id = pd.prize_user_id where u.user_id = pur.user_id and pur.prize_id = p.prize_id";
		datasql = datasql + param.bySearchSqlFilter().toString() + param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
		List<LotteryDetail> list = getNamedParameterJdbcTemplate().query(datasql, params, getBeanMapper(LotteryDetail.class));
		return list;
	}
}
