package com.liangyou.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.RealNameCertSaveBack;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserProperty;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Usertrack;
import com.liangyou.domain.YwdUser;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.UserCreditSummary;

/**
 * 用户信息服务
 * 
 * @author 1432
 *
 */
public interface UserService {
	// public void register(User user, InviteUser inviteUser);

	public User login(String username, String password);

	public boolean checkUsername(String username);

	public boolean checkPhone(String phone);

	/**
	 * 查询易极付实名认证 状态。
	 */
	public int getUserDrawCashStatus(User user);

	public boolean checkEmail(String email);

	/**
	 * 
	 * 获取用户证件信息List; 用到模块 Vip审核-视频-手机-现场
	 * 
	 * @author lijie
	 * @param page
	 * @return
	 */
	// public PageResult<User> getUserList(PageResult<User> page);

	public List getUserList(SearchParam param);

	public int userTrackCount(long user_id);

	public User getUserByCardNO(String card);

	public Usertrack getLastUserTrack(long userid);

	public User updateUser(User user);

	public User getUserById(long user_id);

	public User updateUserLastInfo(User user);

	public User getUserByName(String username);

	public User getUserByApiId(String apiId);

	public void addUserTrack(Usertrack newTrack);

	public List getAllKefu();

	public CreditRank getUserCreditRankByJiFen(int jifen);

	public PageDataList getUserPageDataList(SearchParam p);

	public PageDataList getUserVip(SearchParam p);

	public void VerifyVipSuccess(UserCache userCache, AccountLog accountLog);

	public void VerifyVipFail(UserCache userCache, AccountLog accountLog);

	public UserCache applyVip(UserCache uc, AccountLog accountLog);

	public PageDataList getUsePageDataList(SearchParam param);

	public List<UserType> getAllUserType();

	/**
	 * 更新易极付状态
	 * 
	 * @param userId
	 */
	public void updateApiStatus(String userId);

	/**
	 * 实名认证更新用户
	 * 
	 * @param user
	 */
	public void realname(User user);

	/**
	 * 更新userCashe信息
	 */
	public UserCache updateUserCache(UserCache uc);

	/**
	 * 根据id 查找usercache
	 */
	public UserCache getUserCache(int id);

	public void addBorrowAuto(BorrowAuto ba);

	public void updateBorrowAuto(BorrowAuto ba);

	public BorrowAuto getBorrowAutoById(int borrowAutoId);

	public PageDataList queryBorrowAutoListByUser(SearchParam param);

	public BorrowAuto queryBorrowAutoById(int id);

	public void deleteBorrowAutoById(int id);

	public void updateRealNameStatusByCallBack(RealNameCertSaveBack rnc);

	/**
	 * 绑定手机后，只获取手机验证码
	 * 
	 * @param user
	 * @param phone
	 *            return 唯一标示
	 */
	public String getMObileCodeOnly(User user);

	/**
	 * 判断手机号码是否存在
	 * 
	 * @param phone
	 * @return
	 */
	public int getPhoneOnly(String phone);

	public void apiUserRegister(User user) throws Exception; // 第三方开户回调处理业务
	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 start

	/**
	 * 第三方授权（投标、还款、扣除管理费授权）
	 * 
	 * @param user
	 */
	public void apiUserActivate(User user);

	// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 stop
	/**
	 * 校验用户登录密码
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean checkUserPassword(String username, String password);

	/**
	 * 校验用户登录密码
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean checkUserPayPassword(String username, String payPassword);

	/**
	 * 手机登陆、用户名登陆、email 登陆
	 * 
	 * @param user
	 * @return
	 */
	public User loginWithPhoneEmailName(String inputName, String password);

	public User getUserByNameEmailPhone(String inputName);

	/**
	 * 获取用户所有的推荐的人
	 * 
	 * @param userid
	 * @return
	 */
	public List<InviteUser> getInvitreUser(long userid);

	public PageDataList getInvitreUserBySearchParam(SearchParam param);

	// v1.8.0.4 TGPROJECT-249 lx start
	public PageDataList getInvitreUserBySearchParam(SearchParam param, SearchParam tenderParam);

	// v1.8.0.4 TGPROJECT-249 lx end
	public PageDataList getUserCacheBySearchParam(SearchParam param);

	public List getInviteUserAllList(SearchParam param);

	public List getUserCacheAllList(SearchParam param);

	/**
	 * 查询我的推荐人
	 * 
	 * @param userid
	 * @return
	 */
	public String getInvitre(long userid);

	/**
	 * 获得推荐人管理信息
	 * 
	 * @return
	 */

	/**
	 * 义务贷活动注册
	 * 
	 * @param user
	 */
	public void ywdRegiest(YwdUser user);

	// v1.8.0.1 TGPROJECT-14 lx 2014-04-01 start
	/**
	 * 义务贷活动登陆方法
	 * 
	 * @param user
	 */
	public YwdUser ywdLogin(String username, String password);

	/**
	 * 义务贷活动更新方法
	 * 
	 * @param user
	 */
	public void updateYwdUser(YwdUser ywdUser);

	/**
	 * 义务贷活动查询方法
	 * 
	 * @param user
	 */
	public YwdUser findYwdUser(long id);

	// v1.8.0.1 TGPROJECT-14 lx 2014-04-01 end

	// v1.8.0.4 TGPROJECT-46 start
	public void updateUserCache(UserCache uc, int type);

	// v1.8.0.4 TGPROJECT-46 end
	// v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start
	public TempIdentifyUser inintTempIdentifyUser(User user);

	// v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end

	// v1.8.0.4_u1 TGPROJECT-127 zxc start
	public UserCreditSummary getUserCreditSummary(long userId);

	// v1.8.0.4_u1 TGPROJECT-127 zxc end

	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start
	/**
	 * 查询义乌贷vip卡激活的的用户的数量
	 * 
	 * @return
	 */
	public int getYwdVipUsersCount();

	// v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户 添加 编号义乌贷编号处理 start

	// v1.8.0.3_u2 TGPROJECT-293 2014-05-29 qinjun start
	public void yjfRealNameCall(NewAuthorize na);

	// v1.8.0.3_u2 TGPROJECT-293 2014-05-29 qinjun start

	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-06 start
	/**
	 * 网站使用UserProperty并初始化
	 */
	public void initUserProperty();

	public void updateUserProperty(UserProperty userProp);

	public List<UserProperty> getNotContractUser();
	
	public void docheckStatus(String status);

	public void sendMsgToContractUser();

	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-06 end
	// v1.8.0.4_u3 TGPROJECT-340 qinjun 2014-06-23 start
	/**
	 * 汇付企业开户
	 * 
	 * @param user
	 * @param reg
	 */
	public void huifuUserCorpRegister(User user, CorpRegister reg);

	// v1.8.0.4_u3 TGPROJECT-340 qinjun 2014-06-23 end

	// v1.8.0.4_u5 TGPROJECT-381 qinjun 2014-07-30 start
	public void yjfFailRealName(String apiId);

	// v1.8.0.4_u5 TGPROJECT-381 qinjun 2014-07-30 end

	// add by gy 2016-9-5 18:28:15 渠道客户汇总
	public List<ChannelUserQuery> getChannelUserSum(User cannelAdmin);

	// add by gy 2016-9-5 18:58:47 渠道客户明细， 带分页
	public PageDataList getChannelUserDetailPageDataList(SearchParam param, Map<String, Object> searchParam, User cannelAdmin);

	// add by gy 2016-9-5 18:59:14 渠道客户明细， 不带分页
	public List<ChannelUserQuery> exportChannelUserDetail(User cannelAdmin, Map<String, Object> searchParam);

	/**
	 * 获取平台总注册用户数
	 * 
	 * @return
	 */
	public int sumRegisteredUser();

	public void register(User user, InviteUser inviteUser, UserInvitateCode uic);

	public Date getAddTimeById(long userId);

	public List<User> getAllUser();

	public void addCodeToUser(long userId, Date addtime, String invitateCode);

	// add by lxm 2016-11-29 11:23:09
	public List<User> getCurrentUserById();

	/**
	 * add by lijing 2016-11-30 10:49:32 清空 缓存
	 */
	public void clear();

}
