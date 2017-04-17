package com.liangyou.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liangyou.domain.User;
import com.liangyou.model.DetailUser;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.UserCreditSummary;

public interface UserDao extends BaseDao<User> {

	public User getUserByUsername(String username);

	public User getUserByEmail(String email);

	public User getUserByUsernameAndPassword(String username, String password);

	public List<User> getUserList(SearchParam p);

	// public PageResult<User> getUserList(PageResult<User> page);

	public boolean isRoleHasPurview(long id);

	public User getUserByCard(String card);

	/**
	 * 新增方法：根据类型获得所有该类型的的用户 修改日期：2013-3-21
	 * 
	 * @return
	 */
	public List getAllUser(int type);

	public DetailUser getDetailUserByUserid(int userid);

	public List getAllKefu();

	/**
	 * 更新易极付用户状态
	 * 
	 * @param userId
	 */
	public void updateUserApiStatus(String userId);

	public void updateRealNameStatus(String apiId, int status);

	public int checkPhone(String phone); // 校验手机号是否存在

	public User getUserByApiId(String apiId);

	public User findByPaypwd(User user);// 是否存在 支付密码

	/**
	 * 更具支付密码和用户名 查看用户。
	 * 
	 * @param username
	 * @param payPassword
	 * @return
	 */
	public User getUserByUsernameAndPayPassword(String username, String payPassword);

	/**
	 * 根据 用户名，email,phone ps 查询用户
	 * 
	 * @param username
	 * @param payPassword
	 * @param type
	 * @return
	 */
	public User getUserByInputNameAndPassword(String inputName, String payPassword, String type);

	/**
	 * 根据 用户名，email,phone ps 查询用户
	 * 
	 * @param username
	 * @param type
	 * @return
	 */
	public User getUserByInputName(String inputName, String type);

	// v1.8.0.4_u1 TGPROJECT-127 zxc start
	/**
	 * 积分，查询，我推荐的用户的投资总和，我获得的投资积分总和。
	 * 
	 * @param userId
	 * @return
	 */
	public UserCreditSummary getUserCreditSummary(long userId);
	// v1.8.0.4_u1 TGPROJECT-127 zxc end

	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start
	/**
	 * 查询义乌贷vip卡激活的的用户的数量
	 * 
	 * @return
	 */
	public int getYwdVipUsersCount();
	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 end

	/**
	 * 根据手机号码查询
	 * 
	 * @param phone
	 * @return
	 */
	public User getUserByPhone(String phone);
	
	public void checkStatus(String status);

	// add by gy 2016-9-5 19:01:15 渠道客户明细，不带分页
	public List<ChannelUserQuery> exportChannelUserDetail(User cannelAdmin, Map<String, Object> searchParam);

	// add by gy 2016-9-5 19:00:55 渠道客户明细， 带分页
	public PageDataList findChannelUserDetailPageListBySql(User cannelAdmin, SearchParam param, Map<String, Object> searchParam, Class clazz);

	// add by gy 2016-9-5 19:00:09 根据渠道标识获取用户
	public User getUserByChannelAndType(String cannel);

	// add by gy 2016-9-5 18:59:53 渠道客户汇总
	public List<ChannelUserQuery> getChannelUserSum(User channelAdmin);

	public int sumRegisteredUser();

	public Date getAddTimeById(long userId);

	public List<User> getAllUser();

	public void addCodeToUser(long userId, Date addtime, String invitateCode);

	// add by lxm 2016-11-29 11:29:52
	public List<User> getCurrentUserById();

}
