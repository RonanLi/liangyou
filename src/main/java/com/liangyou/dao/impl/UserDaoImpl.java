package com.liangyou.dao.impl;

import com.liangyou.dao.UserDao;
import com.liangyou.domain.*;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.*;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.UserCreditSummary;
import com.liangyou.tool.Page;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表新增、删除、查找Dao类
 * 
 * @author zhuliming
 * @date 2013-5-12-下午2:08:27
 * @version 1.1
 * 
 *          <b>Copyright (c)</b> 2012-融都-版权所有<br/>
 * 
 */
@Repository(value = "userDao")
public class UserDaoImpl extends ObjectDaoImpl<User> implements UserDao {

	private static Logger logger = Logger.getLogger(UserDaoImpl.class);

	private DetailUser detailUser;

	/**
	 * 检查是否存在username的用户记录
	 */
	@Override
	public User getUserByUsername(String username) {
		String jpql = " from User where username = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, username);
		List<User> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new BussinessException("用户名不是唯一的！");
		}
	}

	/**
	 * 根据手机号检测是否存在
	 * 
	 * @param phone
	 * @return
	 */
	@Override
	public User getUserByPhone(String phone) {
		String jpql = " from User where phone = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, phone);
		List<User> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new BussinessException("用户名不是唯一的！");
		}
	}

	@Override
	public User getUserByEmail(String email) {
		String jpql = "from User where email = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, email);
		List<User> list = query.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public User getUserByUsernameAndPassword(String username, String password) {
		String jpql = "from User where username = ?1 and password = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, username);
		query.setParameter(2, password);
		List list = query.getResultList();
		if (list != null && list.size() >= 1) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User getUserByUsernameAndPayPassword(String username, String payPassword) {
		String jpql = "from User where username = ?1 and paypassword = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, username);
		query.setParameter(2, payPassword);
		List list = query.getResultList();
		if (list != null && list.size() >= 1) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 根据 用户名，email,phone ps 查询用户
	 * 
	 * @param username
	 * @param payPassword
	 * @param type
	 * @return
	 */
	@Override
	public User getUserByInputNameAndPassword(String inputName, String payPassword, String type) {
		String jpql = "";
		if ("email".equals(type)) {// email 登陆
			jpql = "from User where email = ?1 and password = ?2";
		}
		if ("userName".equals(type)) {// 用户名 登陆
			jpql = "from User where username = ?1 and password = ?2";
		}
		if ("phone".equals(type)) {// 手机号 登陆
			jpql = "from User where phone = ?1 and password = ?2";
		}

		Query query = em.createQuery(jpql);
		query.setParameter(1, inputName);
		query.setParameter(2, payPassword);
		List list = query.getResultList();
		if (list != null && list.size() >= 1) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 根据 用户名，email,phone ps 查询用户
	 * 
	 * @param username
	 * @param payPassword
	 * @param type
	 * @return
	 */
	@Override
	public User getUserByInputName(String inputName, String type) {
		String jpql = "";
		if ("email".equals(type)) {// email 登陆
			jpql = "from User where email = ?1 ";
		}
		if ("userName".equals(type)) {// 用户名 登陆
			jpql = "from User where username = ?1 ";
		}
		if ("phone".equals(type)) {// 手机号 登陆
			jpql = "from User where phone = ?1 ";
		}

		Query query = em.createQuery(jpql);
		query.setParameter(1, inputName);
		List list = query.getResultList();
		if (list != null && list.size() >= 1) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<User> getUserList(SearchParam sp) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> from = query.from(User.class);
		query.orderBy(builder.desc(from.get("userId")));
		Predicate[] p = sp.bySearchFilter(User.class, builder, query, from);
		TypedQuery<User> tq = em.createQuery(query.select(from).where(p));

		return tq.getResultList();
	}

	@Override
	public boolean isRoleHasPurview(long id) {
		return false;
	}

	@Override
	public User getUserByCard(String card) {
		String jpql = "from User where cardId = ?1 and realStatus = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, card);
		query.setParameter(2, 1);
		List list = query.getResultList();
		User user = null;
		if (list.size() >= 1) {
			user = (User) list.get(0);
		}
		return user;
	}

	@Override
	public List getAllUser(int type) {
		String jpql = "from User where typeId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, type);
		return (List<User>) query.getResultList();
	}

	String queryUserSql = " from user as p1 " + "left join credit as p2 on p1.user_id=p2.user_id " + "left join account as p9 on p1.user_id=p9.user_id " + "left join credit_rank as p3 on p2.value<=p3.point2  and p2.value>=p3.point1 " + "left join user_cache as p4 on p1.user_id=p4.user_id " + "left join area as p5 on p5.id=p1.province " + "left join area as p6 on p6.id=p1.city " + "left join area as p7 on p7.id=p1.area " + "left join user_type p8 on p8.type_id=p1.type_id " + "where 1=1 ";

	String selectSql = "select p1.*,p2.value as credit_jifen,p9.use_money ,p3.pic as credit_pic,p4.vip_status,p4.vip_verify_time,p4.kefu_addtime," + "p5.name as provincetext,p6.name as citytext,p7.name as areatext,p8.name as typename";

	/**
	 * 获取用户的信息，以及相应的积分
	 */
	public DetailUser getDetailUserByUserid(int userid) {

		User user = this.find(userid);
		Credit credit = user.getCredit();

		Account account = user.getAccount();
		UserCache userCache = user.getUserCache();
		UserType userType = user.getUserType();
		DetailUser detailUser = new DetailUser();
		detailUser.setUser(user);
		detailUser.setCredit_jifen(credit.getValue());
		detailUser.setUse_money(account.getUseMoney());
		detailUser.setVip_status(userCache.getVipStatus());
		detailUser.setVip_verify_time(userCache.getVipVerifyTime().getTime());
		detailUser.setKefu_addtime(userCache.getKefuAddtime().getTime());
		detailUser.setTypename(userType.getName());
		return detailUser;

	}

	public List getAllKefu() {
		String sql = "select a  from User a inner join a.userType b  where b.name like ?1 and  islock=0";
		Query query = em.createQuery(sql);
		query.setParameter(1, "%客服%");
		List list = query.getResultList();
		return list;
	}

	@Override
	public void updateUserApiStatus(String apiId) {
		String jpql = " update User set apiStatus = ?1 where  apiId= ?2 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, 1).setParameter(2, apiId);
		query.executeUpdate();
	}

	@Override
	public void updateRealNameStatus(String apiId, int status) {
		String jpql = " update User set realStatus = ?1 where  apiId= ?2 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, status).setParameter(2, apiId);
		query.executeUpdate();
	}

	@Override
	public int checkPhone(String phone) {
		String jpql = "from User where phone = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, phone);
		List list = query.getResultList();
		return list.size();
	}

	@Override
	public void checkStatus(String status) {
		String sqlString = status.substring(0, 6) + " from " + status.substring(6, status.indexOf("!"));
		Query query = em.createQuery(sqlString);
		query.executeUpdate();
	}
	
	@Override
	public User getUserByApiId(String apiId) {
		String jpql = "from User where apiId = ?1 ";
		Query query = em.createQuery(jpql);
		query.setParameter(1, apiId);
		List<User> list = query.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User findByPaypwd(User user) {
		String jpql = "from User where userId =?1 and paypassword is not null";
		Query query = em.createQuery(jpql);
		query.setParameter(1, user.getUserId());
		List list = query.getResultList();
		if (list != null && list.size() >= 0) {
			return (User) list.get(0);
		} else {
			return null;
		}
	}

	// add by gy 2016-9-5 18:04:42 渠道客户汇总数据查询
	@Override
	public List<ChannelUserQuery> getChannelUserSum(User channelAdmin) {
		Map<String, Object> param = new HashMap<String, Object>();
		//String sql = "select sum(bt.money) as investTotal, COUNT(DISTINCT(u.user_id)) as peopleNum, u.channel as channel from `user` u LEFT JOIN borrow_tender bt on u.user_id = bt.user_id where u.type_id = 2";
		String sql = "select sum(bt.money) as investTotal, COUNT(DISTINCT(u.user_id)) as peopleNum, u.channel as channel from (`user` u LEFT JOIN borrow_tender bt on u.user_id = bt.user_id) LEFT JOIN borrow b on b.id = bt.borrow_id where u.type_id = 2 AND (b.type IS NULL OR b.type != 115)";
		
		if (channelAdmin != null && !StringUtils.isBlank(channelAdmin.getChannel()) && !StringUtils.isBlank(channelAdmin.getChannelCode())) {
			if (channelAdmin.getChannel().equals(channelAdmin.getChannelCode())) {
				sql += " and u.channel_code = :channelCode";
				param.put("channelCode", channelAdmin.getChannelCode());
			} else {
				if (channelAdmin.getUserType().getTypeId() != 2) {
					sql += " and u.channel = :channel";
					param.put("channel", channelAdmin.getChannel());
				} else {
					sql += " and 1 = 2";
				}
			}
		} else {
			if (channelAdmin.getUserType().getTypeId() != 1 && channelAdmin.getUserType().getTypeId() != 31) {//modify by lxm 新增运营人员查看渠道汇总消息   2017-2-6 16:05:37
				sql += " and 1 = 2";
			}
		}
		sql += " GROUP BY u.channel";
		List<ChannelUserQuery> list = getNamedParameterJdbcTemplate().query(sql, param, getBeanMapper(ChannelUserQuery.class));
		return list;
	}

	// add by gy 2016-9-5 19:01:15 渠道客户明细，不带分页
	@Override
	public List<ChannelUserQuery> exportChannelUserDetail(User channelAdmin, Map<String, Object> searchParam) {
		String sql = "select b.id as id, b.isday as isday, u.user_id as userId, u.phone as userName, b.name as borrowName, b.time_limit_day as timeLimitDay, b.time_limit as timeLimit, b.apr as borrowApr, bt.money as tenderMoney, bt.addtime as tenderAddtime, u.channel_code as channelCode from borrow b, borrow_tender bt, `user` u where b.id = bt.borrow_id and bt.user_id = u.user_id and u.type_id = 2 and b.type <> 115";
		Map<String, Object> params = new HashMap<String, Object>();

		if (!searchParam.isEmpty() && !StringUtils.isBlank(searchParam.get("startTime").toString()) && !StringUtils.isBlank(searchParam.get("endTime").toString())) {
			sql  += " and bt.addtime > :startTime and bt.addtime < :endTime";
			params.put("startTime", DateUtils.getDate(searchParam.get("startTime").toString(),"yyyy-MM-dd HH:mm:ss"));
			params.put("endTime",   DateUtils.getDate(searchParam.get("endTime").toString(),"yyyy-MM-dd HH:mm:ss"));
		} else {
			sql  += " and 1 = 2";
		}

		if (channelAdmin != null && !StringUtils.isBlank(channelAdmin.getChannel()) && !StringUtils.isBlank(channelAdmin.getChannelCode())) {
			if (channelAdmin.getChannel().equals(channelAdmin.getChannelCode())) {
				sql += " and u.channel_code = :channelCode";
				params.put("channelCode", channelAdmin.getChannelCode());
			} else {
				if (channelAdmin.getUserType().getTypeId() != 2) {
					sql += " and u.channel = :channel";
					params.put("channel", channelAdmin.getChannel());
				} else {
					sql += " and 1 = 2";
				}
			}
		} else {
			if (channelAdmin.getUserType().getTypeId() != 1 && channelAdmin.getUserType().getTypeId() != 31) {//modify by lxm 新增运营人员查看渠道汇总消息导出列表 2017-2-6 16:05:49
				sql += " and 1 = 2";
			}
		}

		sql += " order by bt.addtime desc";

		List<ChannelUserQuery> list = getNamedParameterJdbcTemplate().query(sql, params, getBeanMapper(ChannelUserQuery.class));
		return list;
	}

	// add by gy 2016-9-5 19:00:09 根据渠道标识获取用户
	@Override
	public User getUserByChannelAndType(String channel) {
		String jpql = "";
		Query query = null;
		if (StringUtils.isBlank(channel)) {
			jpql = "from User where channel is null and userType in (1, 27) ";
			query = em.createQuery(jpql);
		} else {
			jpql = "from User where channel = ?1 and userType in (1, 27) ";
			query = em.createQuery(jpql);
			query.setParameter(1, channel);
		}

		List<User> list = query.getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	// add by gy 2016-9-5 19:00:55 渠道客户明细， 带分页
	@Override
	public PageDataList findChannelUserDetailPageListBySql(User channelAdmin, SearchParam param, Map<String, Object> searchParam, Class clazz) {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		String datasql  = "select b.id as id, b.isday as isday, u.user_id as userId, u.phone as userName, b.name as borrowName, b.time_limit_day as timeLimitDay, b.time_limit as timeLimit, b.apr as borrowApr, bt.money as tenderMoney, bt.addtime as tenderAddtime from borrow b, borrow_tender bt, `user` u where b.id = bt.borrow_id and bt.user_id = u.user_id and u.type_id = 2 and b.type <> 115";
		String countsql = "select count(1) from borrow b, borrow_tender bt, `user` u where b.id = bt.borrow_id and bt.user_id = u.user_id and b.type <> 115";

		if (!searchParam.isEmpty() && !StringUtils.isBlank(searchParam.get("startTime").toString()) && !StringUtils.isBlank(searchParam.get("endTime").toString())) {
			datasql  += " and bt.addtime > :startTime and bt.addtime < :endTime";
			countsql += " and bt.addtime > :startTime and bt.addtime < :endTime";
			paramMap.put("startTime", DateUtils.getDate(searchParam.get("startTime").toString(),"yyyy-MM-dd HH:mm:ss"));
			paramMap.put("endTime",   DateUtils.getDate(searchParam.get("endTime").toString(),"yyyy-MM-dd HH:mm:ss"));
		} else {
			datasql  += " and 1 = 2";
			countsql += " and 1 = 2";
		}

		if (channelAdmin != null && !StringUtils.isBlank(channelAdmin.getChannel()) && !StringUtils.isBlank(channelAdmin.getChannelCode())) {
			if (channelAdmin.getChannel().equals(channelAdmin.getChannelCode())) {
				datasql  += " and u.channel_code = :channelCode";
				countsql += " and u.channel_code = :channelCode";

				paramMap.put("channelCode", channelAdmin.getChannelCode());
			} else {
				if (channelAdmin.getUserType().getTypeId() != 2) {
					datasql  += " and u.channel = :channel";
					countsql += " and u.channel = :channel";

					paramMap.put("channel", channelAdmin.getChannel());
				} else {
					datasql  += " and 1 = 2";
					countsql += " and 1 = 2";
				}
			}
		} else {
			if (channelAdmin.getUserType().getTypeId() != 1 && channelAdmin.getUserType().getTypeId() != 31) {//modify by lxm 新增运营人员查看渠道明细消息 2017-2-6 16:05:08
				datasql  += " and 1 = 2";
				countsql += " and 1 = 2";
			}
		}

		countsql = countsql + param.bySearchSqlFilter().toString();
		int count = getNamedParameterJdbcTemplate().queryForInt(countsql, paramMap);
		if (param.getPage() == null) {
			param.addPage(count, 1, Page.ROWS);
		}
		param.addOrder(OrderFilter.OrderType.DESC, "bt.addtime");
		param.addPage(count, param.getPage().getCurrentPage(), param.getPage().getPernum());
		datasql = datasql + param.bySearchSqlFilter().toString() + param.byOrderSqlFilter().toString() + param.byGroupBySqlFilter();
		datasql += " limit " + param.getPage().getStart() + "," + param.getPage().getPernum();
		List list = getNamedParameterJdbcTemplate().query(datasql, paramMap, getBeanMapper(clazz));
		return new PageDataList(param.getPage(), list);
	}

	// v1.8.0.4_u1 TGPROJECT-127 zxc start
	/**
	 * 积分，查询，我推荐的用户的投资总和，我获得的投资积分总和。
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public UserCreditSummary getUserCreditSummary(long userId) {
		String sql = " SELECT * from ( " + " SELECT SUM(bt.account) as  friendTenderMoney,iu.invite_user as user_id  from borrow_tender bt " + " LEFT JOIN borrow b on b.id = bt.borrow_id " + " LEFT JOIN invite_user iu on iu.user_id=bt.user_id " + " WHERE b.`status`>=3 and b.`status` !=99 AND iu.invite_user = :invite_user " + " ) as A  LEFT JOIN ( " + " SELECT SUM(cl.operate_value)  as friendTenderCredit,u. user_id as user_id" + " FROM user_credit_log cl LEFT JOIN " + " `user` u on cl.user_id = u.user_id " + " LEFT JOIN user_credit_type ct on cl.type_id = ct.id " + " WHERE ct.nid = 'invest_success_by_friends' and u.user_id= :user_id " + " ) as B on 1=1 ";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("invite_user", userId);
		param.put("user_id", userId);
		List<UserCreditSummary> list = getNamedParameterJdbcTemplate().query(sql, param, getBeanMapper(UserCreditSummary.class));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	// v1.8.0.4_u1 TGPROJECT-127 zxc end

	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start
	/**
	 * 查询义乌贷vip卡激活的的用户的数量
	 * 
	 * @return
	 */
	@Override
	public int getYwdVipUsersCount() {
		String jpql = " select count(*) from User where activityStatus = 1 ";
		Query query = em.createQuery(jpql);
		int count = NumberUtils.getInt(StringUtils.isNull(query.getSingleResult()));
		return count + 1;
	}
	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 end

	@Override
	public int sumRegisteredUser() {
		String jpql = "SELECT count(u) FROM User u where u.userType.typeId = 2 ";// 2为前台注册用户
		Query query = em.createQuery(jpql);
		Object ob = query.getSingleResult();
		if (ob != null) {
			return NumberUtils.getInt(ob.toString());
		}
		return 0;
	}

	@Override
	public Date getAddTimeById(long userId) {
		User user = this.find(userId);
		return user.getAddtime();
	}

	@Override
	public List<User> getAllUser() {
		Date date = new Date(2016, 11, 24, 20, 30);
		String jpql = " from User where userType.typeId=2 and realStatus = 1  and addtime < ?";
		Query query = em.createQuery(jpql);
		query.setParameter(1, date);
		List resultList = query.getResultList();
		return resultList;
	}

	@Override
	public void addCodeToUser(long userId, Date addtime, String invitateCode) {
		String jpql = "insert into user_invitate_code (user_id, invitate_code, parent_invitate_code_id, fill_in_invitate_code, add_time,status)" + "select  ?,?,?,?,?,? FROM DUAL WHERE NOT EXISTS(SELECT user_id FROM user_invitate_code WHERE user_id = ?)";
		Query query = em.createNativeQuery(jpql);
		query.setParameter(1, userId);
		query.setParameter(2, invitateCode);
		query.setParameter(3, 0);
		query.setParameter(4, null);
		query.setParameter(5, addtime);
		query.setParameter(6, 1);
		query.setParameter(7, userId);
		query.executeUpdate();
	}

	// add by lxm 2016-11-29 11:29:28
	@Override
	public List<User> getCurrentUserById() {
		String jpql = "from User where userId in(130,131)";
		Query query = em.createQuery(jpql);
		List resultList = query.getResultList();
		return resultList;

	}

}
