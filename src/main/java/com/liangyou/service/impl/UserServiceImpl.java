package com.liangyou.service.impl;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.RealNameCertSaveBack;
import com.liangyou.api.pay.SmsCaptcha;
import com.liangyou.context.ChinaPnrType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.RewardType;
import com.liangyou.dao.AccountBankDao;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowAutoDao;
import com.liangyou.dao.CreditDao;
import com.liangyou.dao.DrawBankDao;
import com.liangyou.dao.ExperienceMoneyDao;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.dao.RewardExtendDao;
import com.liangyou.dao.UserAmountDao;
import com.liangyou.dao.UserCacheDao;
import com.liangyou.dao.UserCreditDao;
import com.liangyou.dao.UserCreditRankDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserInvitateCodeDao;
import com.liangyou.dao.UserPropertyDao;
import com.liangyou.dao.UserTrackDao;
import com.liangyou.dao.UserTypeDao;
import com.liangyou.dao.UserinfoDao;
import com.liangyou.dao.YwdUserDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.RewardExtend;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserProperty;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Userinfo;
import com.liangyou.domain.Usertrack;
import com.liangyou.domain.YwdUser;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.MsgReq;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.InviteUserSummary;
import com.liangyou.model.account.UserCreditSummary;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserInvitateCodeService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.RC4Util;
import com.liangyou.util.StringUtils;

/**
 * 
 * @author fuxingxing
 * @date 2013-5-13-下午1:59:06
 * @version
 * 
 * <b>Copyright (c)</b> 2013-51融都-版权所有<br/>
 * 
 */
@Service(value = "userService")
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements UserService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserTrackDao userTrackDao;
	@Autowired
	private UserCacheDao userCacheDao;
	@Autowired
	private CreditDao creditDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private UserAmountDao userAmountDao;
	@Autowired
	private UserinfoDao userinfoDao;
	@Autowired
	private UserCreditRankDao creditRankDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private UserTypeDao userTypeDao;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private BorrowAutoDao borrowAutoDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private InviteUserDao inviteUserDao;
	@Autowired
	private MsgService msgService;
	@Autowired
	private YwdUserDao ywdUserDao;
	@Autowired
	private UserCreditDao UserCreditDao;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private UserPropertyDao userPropertyDao;
	@Autowired
	private DrawBankDao drawBankDao;
	@Autowired
	private AccountBankDao accountBankDao;
	@Autowired
	private RewardExtendDao rewardExtendDao;
	@Autowired
	private RewardExtendService rewardExtendService;
	@Autowired
	private ExperienceMoneyDao experienceMoneyDao;
	@Autowired
	private UserInvitateCodeDao userInvitateCodeDao;
	@Autowired
	private UserInvitateCodeService userInvitateCodeService;
	
	/**
	 * 用户注册
	 * 
	 * @param user
	 * @return
	 */
	public void register(User user, InviteUser inviteUser, UserInvitateCode uic) {
		user.setStatus(1); // 默认用户状态是1
		if (user.getUserType() == null) {
			UserType userType = new UserType();
			userType.setTypeId(2);
			user.setUserType(userType);
		}

		userDao.save(user);

		if(inviteUser!=null){//有推荐人保存。
			inviteUser.setUser(user);
			inviteUserDao.save(inviteUser);
			//将推荐人放入user对象
		}

		// 初始化账户
		Account act = new Account();
		act.setUser(user);
		accountDao.save(act);
		
		// 初始化体验金表
		logger.info("是否启用体验金：" + isEnableExperienceMoney());
		if(isEnableExperienceMoney() && user.getUserType() != null && user.getUserType().getTypeId() != 28) {
			ExperienceMoney em = new ExperienceMoney();
			em.setUser(user);
			em.setExperienceMoney(Double.parseDouble(Global.getValue("amount_handsel_experienceMoney")));
			em.setReceiveStatus(0);
			em.setUseStatus(0);
			em.setTimeLimit(Integer.parseInt(Global.getValue("experienceMoney_time_limit")));
			em.setAddTime(new Date());
			experienceMoneyDao.save(em);
		}

		// 初始化用户amount
		UserAmount amount = new UserAmount();
		amount.setUser(user);
		userAmountDao.save(amount);

		//邀请码表
		if(uic==null){
			uic=new UserInvitateCode();
		}

		// 如果有受邀邀请码，增加parentInvitateCodeId字段的值
		if (uic != null && !StringUtils.isBlank(uic.getFillinInvitateCode())) {
			UserInvitateCode uitc = userInvitateCodeDao.findByCriteriaForUnique(SearchParam.getInstance().addParam("invitateCode", uic.getFillinInvitateCode()));
			if(uitc != null && uitc.getUser() != null)
				uic.setParentInvitateCodeId(uitc.getUser().getUserId());
		}
		if(user.getUserType().getTypeId()==2){
			uic.setInvitateCode(null);
			uic.setUser(user);
			uic.setAddTime(new Date());
			uic.setStatus(1);
			userInvitateCodeDao.save(uic);
		}
		// 用户积分表
		Credit uc = new Credit();
		uc.setUser(user);
		uc.setValue(0);
		uc.setAddip(user.getAddip());
		uc.setAddtime(new Date());
		// 保存用户积分信息
		creditDao.save(uc);

		//v1.8.0.3 TGPROJECT-113 qj 2014-04-21 start
		UserCredit userCredit = userCreditService.registerCredit(user);
		UserCreditDao.save(userCredit);
		//v1.8.0.3 TGPROJECT-113 qj 2014-04-21 stop

		// 用户缓存
		UserCache cache = new UserCache();
		cache.setUser(user);

		// 是否赠送用户vip,赠送vip的话，便更新用户vip状态以及赠送用户vip积分
/*		String vipStartTime = Global.getString("give_vip_startTime");
		String vipEndTime = Global.getString("give_vip_endTime");
		if ((!StringUtils.isBlank(vipStartTime))
				&& (!StringUtils.isBlank(vipEndTime))) {
			long nowTime = DateUtils.getTime(new Date());
			long time1 = DateUtils.getTime(vipStartTime);
			long time2 = DateUtils.getTime(vipEndTime);
			if (nowTime > time1 && nowTime < time2) {
				cache.setVipStatus(1);
				cache.setVipVerifyRemark("用户注册，系统赠送VIP！");
				cache.setVipVerifyTime(new Date());
				ruleService.dealRule(ruleService.getRuleByNid("star_rank"),
						"vip_verify", user, 0);// 客户星级处理

			}
		}*/

		userCacheDao.save(cache);

		// 用户信息
		Userinfo userinfo = new Userinfo();
		userinfo.setUser(user);
		userinfoDao.save(userinfo);

		//v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-09 start 
//		UserProperty userPro = new UserProperty();
//		userPro.setUser(user);
//		userPropertyDao.save(userPro);
		//v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-09 end

		// 是否激活邮件，发送成功才开户
/*		Rule rule = ruleService.getRuleByNid("check_regiest");
		if (rule != null) {
			if (rule.getStatus() == 1&& "1".equals(rule.getValueStrByKey("send_register_email"))&&user.getUserType().getTypeId() == 2) {
				try {//普通用户开户发送短信
					sendMail(user);// 激活发送邮件
				} catch (Exception e) {
					e.printStackTrace();
					throw new BussinessException("发送激活邮件失败 ",
							"/user/register.html");
				}
			}
		}*/

/*		rule =  ruleService.getRuleByNid("register_reward_rule");	
		if(rule!=null && rule.getStatus() == 1){
			if( rule.getValueIntByKey("register") == 1){
				double award_money =  NumberUtils.format2(rule.getValueDoubleByKey("register_money"));
				RewardExtend extend = new RewardExtend(RewardType.REGISTER, award_money, 0, user, "", new Date());
				rewardExtendDao.save(extend);
			}
		}*/
		//注册成功 ,给用户发送短信,发送站内信
		try {
			MsgReq msgReq = new MsgReq();
			msgReq.setUsername(user.getUsername());
			msgReq.setReceiver(user);
			msgReq.setSender(new User(Constant.ADMIN_ID));
			msgReq.setMsgOperate(this.msgService.getMsgOperate(24));
			msgReq.setTime(DateUtils.dateStr4(new Date()));
			DisruptorUtils.sendMsg(msgReq);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public User login(String username, String password) {
		User u = null;
		// 密码MD5加密
		MD5 md5 = new MD5();
		u = userDao.getUserByUsernameAndPassword(username,
				md5.getMD5ofStr(password));

		return u;
	}

	/**
	 * 手机登陆、用户名登陆、email 登陆
	 * @param user
	 * @return
	 */
	@Override
	public User loginWithPhoneEmailName(String inputName, String password){
		User u = null;
		// 密码MD5加密+
		MD5 md5 = new MD5();
		String ps = md5.getMD5ofStr(password);

		if(StringUtils.isEmail(inputName)){
			//email 登陆
			u = userDao.getUserByInputNameAndPassword(inputName, ps, "email");
		}else if(StringUtils.isMobile(inputName)){
			//手机登陆
			u = userDao.getUserByInputNameAndPassword(inputName, ps, "phone");
		}else{
			//用户名登陆
			u = userDao.getUserByInputNameAndPassword(inputName, ps, "userName");
		}
		return u;
	}
	//v1.8.0.1 TGPROJECT-14 lx 2014-04-01 start
	@Override
	public YwdUser ywdLogin(String username,String password){
		YwdUser u = null;
		u = ywdUserDao.getYwdUserByUsernameAndPassword(username,password);
		return u;
	}
	@Override
	public void updateYwdUser(YwdUser ywdUser){
		ywdUserDao.update(ywdUser);
	}
	@Override
	public  YwdUser findYwdUser(long id){
		return ywdUserDao.find(id);
	}
	//v1.8.0.1 TGPROJECT-14 lx 2014-04-01 end
	public User getUserByNameEmailPhone(String inputName){
		User u = null;
		if(StringUtils.isEmail(inputName)){
			//email 登陆
			u = userDao.getUserByInputName(inputName, "email");
		}else if(StringUtils.isMobile(inputName)){
			//手机登陆
			u = userDao.getUserByInputName(inputName, "phone");
		}else{
			//用户名登陆
			logger.info("用户名登陆，账户密码："+inputName+":");
			u = userDao.getUserByInputName(inputName, "userName");
		}
		return u;
	}

	/**
	 * 校验用户登录密码
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public boolean checkUserPassword(String username, String password){
		User u = null;
		// 密码MD5加密
		MD5 md5 = new MD5();
		u = userDao.getUserByUsernameAndPassword(username,
				md5.getMD5ofStr(password));
		return u!=null;
	}

	/**
	 * 校验用户登录密码
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public boolean checkUserPayPassword(String username, String payPassword){
		User u = null;
		// 密码MD5加密
		MD5 md5 = new MD5();
		u = userDao.getUserByUsernameAndPassword(username,
				md5.getMD5ofStr(payPassword));
		return u!=null;
	}

	public boolean checkUsername(String username) {
		User user = null;
		try {
			user = userDao.getUserByUsername(username);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		}
		if (user != null) {
			return false;
		}
		return true;
	}
	
	public boolean checkPhone(String phone) {
		User user = null;
		try {
			user = userDao.getUserByPhone(phone);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		}
		if (user != null) {
			return false;
		}
		return true;
	}

	public boolean checkEmail(String email) {
		User user = null;
		try {
			user = userDao.getUserByEmail(email);
		} catch (EmptyResultDataAccessException e) {
			user = null;
		}
		if (user != null) {
			return false;
		}
		return true;
	}

	public User getUserByName(String username) {
		return userDao.getUserByUsername(username);
	}

	public User getUserByApiId(String apiId) {
		return userDao.getUserByApiId(apiId);
	}

	public void addUserTrack(Usertrack ut) {
		userTrackDao.addUserTrack(ut);
	}

	@Override
	public Usertrack getLastUserTrack(long userid) {
		return userTrackDao.getLastUserTrack(userid);
	}

	/*
	 * @Override public PageResult<User> getUserList(PageResult<User> page) {
	 * return userDao.getUserList(page); }
	 */
	@Override
	public List getUserList(SearchParam param) {
		List list = userDao.getUserList(param);
		return list;
	}
	
	// add by gy 渠道客户汇总
	public List<ChannelUserQuery> getChannelUserSum(User channelAdmin) {
		List<ChannelUserQuery> channelUserSumList = userDao.getChannelUserSum(channelAdmin);
		if (!channelUserSumList.isEmpty()) {
			for (ChannelUserQuery cuq : channelUserSumList) {
				User userAdmin = userDao.getUserByChannelAndType(cuq.getChannel());
				cuq.setChannelName(userAdmin != null ? userAdmin.getRealname() : null);
			}
		}
		return channelUserSumList;
	}
	
	// add by gy 2016-9-5 18:58:47 渠道客户明细， 带分页
	@Override
	public PageDataList getChannelUserDetailPageDataList(SearchParam param, Map<String, Object> searchParam, User channelAdmin) {
		
		PageDataList pageData = userDao.findChannelUserDetailPageListBySql(channelAdmin, param, searchParam, ChannelUserQuery.class);
		if (pageData != null) {
			List<ChannelUserQuery> list = pageData.getList();
			if (!list.isEmpty()) {
				for (ChannelUserQuery cud : list) {
					cud.setUserName(StringUtils.hideFirstChar(cud.getUserName(), 7));
					User u = this.userDao.find(cud.getUserId());
					User cannelAdmin = this.userDao.getUserByChannelAndType(u.getChannel());
					cud.setChannelName(cannelAdmin != null ? cannelAdmin.getRealname() : null);
				}
			}
		}
		
		return pageData;
	}
	
	// add by gy 2016-9-5 18:59:14 渠道客户明细， 不带分页
	@Override
	public List<ChannelUserQuery> exportChannelUserDetail(User channelAdmin, Map<String, Object> searchParam) {
		List<ChannelUserQuery> cuqList = userDao.exportChannelUserDetail(channelAdmin, searchParam);
		if (!cuqList.isEmpty()) {
			for (ChannelUserQuery cuq : cuqList) {
				cuq.setUserName(StringUtils.hideFirstChar(cuq.getUserName(), 7));
				User u = this.userDao.find(cuq.getUserId());
				User cannelAdmin = this.userDao.getUserByChannelAndType(u.getChannel());
				cuq.setChannelName(cannelAdmin != null ? cannelAdmin.getRealname() : null);
			}
		}
		return cuqList;
	}

	@Override
	public List getInviteUserAllList(SearchParam param){
		List list = inviteUserDao.findAllPageList(param).getList();
		return list;
	}

	@Override
	public List getUserCacheAllList(SearchParam param){
		List list = userCacheDao.findAllPageList(param).getList();
		return list;
	}

	@Override
	public int userTrackCount(long user_id) {
		int total = userTrackDao.getUserTrackCount(user_id);
		return total;
	}

	@Override
	public User getUserByCardNO(String card) {
		return userDao.getUserByCard(card);
	}

	@Override
	public User updateUser(User user) {
		return userDao.merge(user);
	}

	@Override
	public void updateApiStatus(String userId) {
		userDao.updateUserApiStatus(userId);
	}

	public User getUserById(long user_id) {
		return userDao.find(user_id);
	}

	public User updateUserLastInfo(User user) {
		userDao.update(user);
		return userDao.find(user.getUserId());
	}

	@Override
	public List getAllKefu() {
		return userDao.getAllKefu();
	}

	@Override
	public CreditRank getUserCreditRankByJiFen(int jifen) {
		return creditRankDao.getCreditRankByJiFen(jifen);
	}

	@Override
	public PageDataList getUserPageDataList(SearchParam p) {
		return userDao.findPageList(p);
	}
	
	@Override
	public PageDataList getUserVip(SearchParam p) {
		return userCacheDao.findPageList(p);
	}

	@Override
	public void VerifyVipSuccess(UserCache userCache, AccountLog accountLog) {
		User user = getUserById(userCache.getUser().getUserId());
		int api_type = Global.getInt("api_code");
		//审核vip状态拦截.主要是在后台回调处理vip时，避免重复处理
		if (api_type ==1) {
			//这里要从数据库，重新查询
			UserCache uc = userCacheDao.find(userCache.getId());
			if (uc.getVipStatus()==1) {
				logger.info("申请vip，回调处理成功！！");
				return;
			}
		}
		List<Object> taskList = new ArrayList<Object>();
		double vipfee=NumberUtils.getDouble(Global.getValue("vip_fee"));
		if(vipfee>0){
			if(api_type ==1 ){//汇付
				accountDao.updateAccount(-vipfee, -vipfee, 0, userCache.getUser().getUserId());
			}else{
				accountDao.updateAccount(-vipfee, 0, -vipfee, userCache.getUser().getUserId());
			}
			Account act = accountDao.getAcountByUser(userCache.getUser());
			accountLog.setMoney(vipfee);
			accountLog.setTotal(act.getTotal());
			accountLog.setUseMoney(act.getUseMoney());
			accountLog.setNoUseMoney(act.getNoUseMoney());
			accountLog.setCollection(act.getCollection());
			accountLog.setRemark("申请VIP成功，扣除资金" + vipfee + "元");
			accountLogDao.save(accountLog);
			// vip审核 第三方支付操作
			apiService.verifyVipSuccess(userCache, vipfee, taskList);
			//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
			webOtherInviteBusiness(userCache,vipfee);
			//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end
		}
		borrowService.doApiTask(taskList);
		userCacheDao.update(userCache);
		ruleService.dealRule(ruleService.getRuleByNid("star_rank"), "vip_verify", userCache.getUser(), 0);// 客户星级处理
		//用户积分处理
		userCreditService.vipVerifyCredit(user);
	}

	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 其他网站推荐人业务处理
	 * @param userCache
	 * @param vipFee
	 */
	private void webOtherInviteBusiness(UserCache userCache,double vipFee){
		Rule rule = ruleService.getRuleByNid("invite_award_rate");
		if(rule!= null && rule.getStatus() == 1){
			InviteUser invite = userCache.getUser().getInviteUser();
			if(invite != null){
				User inviteUser = invite.getInviteUser();
				if(inviteUser != null){
					int inviteTotal = inviteUserDao.getInviteUserTotal(inviteUser);
					double vip_rate = rule.getValueByDiyCheckValue(inviteTotal, Constant.VIP_INVITE_RATE_RULE);
					double fee =  NumberUtils.format2(vipFee*vip_rate);
					String content = "用户"+ userCache.getUser().getUsername()+"申请VIP成功获得提成："+fee+"元";
					RewardExtend extend = new RewardExtend(RewardType.VIPINVITE, fee, 0, inviteUser, content, new Date());
					rewardExtendDao.save(extend);
				}
			}
		}
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end

	@Override
	public void VerifyVipFail(UserCache userCache, AccountLog accountLog) {
		userCacheDao.update(userCache);
		double vipfee = NumberUtils.getDouble(Global.getValue("vip_fee"));
		if (vipfee > 0) {
			accountDao.updateAccount(0, vipfee, -vipfee, userCache.getUser()
					.getUserId());
			Account act = accountDao.getAcountByUser(userCache.getUser());
			accountLog.setMoney(vipfee);
			accountLog.setTotal(act.getTotal());
			accountLog.setUseMoney(act.getUseMoney());
			accountLog.setNoUseMoney(act.getNoUseMoney());
			accountLog.setCollection(act.getCollection());
			accountLog.setRemark("申请VIP失败，解冻资金");
			accountLogDao.save(accountLog);

		}
	}

	@Override
	public UserCache applyVip(UserCache uc, AccountLog accountLog) {
		UserCache cache = uc.getUser().getUserCache();
		if (cache == null) {
			userCacheDao.save(uc);
		} else {
			userCacheDao.update(uc);
		}
		cache = userCacheDao.getUserCacheByUserid(uc.getUser().getUserId());
		Account act = accountDao.getAcountByUser(uc.getUser());
		double vipfee = NumberUtils.getDouble(Global.getValue("vip_fee"));

		if (vipfee >= 0) {
			int row = 0;
			row = accountDao.updateAccountNotZero(0, 0 - vipfee, vipfee, uc
					.getUser().getUserId());
			if (row < 1) {
				throw new BussinessException("扣除冻结款出错！");
			}
			accountLog.setMoney(vipfee);
			accountLog.setTotal(act.getTotal());
			accountLog.setUseMoney(act.getUseMoney());
			accountLog.setNoUseMoney(act.getNoUseMoney());
			accountLog.setCollection(act.getCollection());
			accountLog.setRemark("申请VIP，冻结资金" + vipfee + "元");
			accountLogDao.save(accountLog);
		}
		UserCache backUc = userCacheDao.getUserCacheByUserid(uc.getUser()
				.getUserId());
		return backUc;
	}

	@Override
	public PageDataList getUsePageDataList(SearchParam param) {
		return userDao.findPageList(param);
	}

	@Override
	public List<UserType> getAllUserType() {
		return userTypeDao.findAll();
	}

	@Override
	public void realname(User user) {
		updateUser(user);// 更新用户信息
	}

	/**
	 * 实名回调进来
	 * 
	 * @param rnc
	 */
	@Override
	public void updateRealNameStatusByCallBack(RealNameCertSaveBack rnc) {
		if ("success".equals(rnc.getStatus())) {
			userDao.updateRealNameStatus(rnc.getUserId(), 1);
		} else {
			logger.info("易极付拒绝：" + rnc.getUserId());
			userDao.updateRealNameStatus(rnc.getUserId(), 0);
		}
	}

	@Override
	public UserCache updateUserCache(UserCache uc) {
		return userCacheDao.merge(uc);
	}

	@Override
	public UserCache getUserCache(int id) {
		return userCacheDao.find(id);
	}

	@Override
	public void addBorrowAuto(BorrowAuto ba) {
		borrowAutoDao.save(ba);
	}

	@Override
	public void updateBorrowAuto(BorrowAuto ba) {
		borrowAutoDao.update(ba);
	}

	@Override
	public BorrowAuto getBorrowAutoById(int borrowAutoId) {
		return borrowAutoDao.find(borrowAutoId);
	}

	@Override
	public PageDataList queryBorrowAutoListByUser(SearchParam param) {
		return borrowAutoDao.findAllPageList(param);
	}

	@Override
	public BorrowAuto queryBorrowAutoById(int id) {
		return borrowAutoDao.find(id);
	}

	@Override
	public void deleteBorrowAutoById(int id) {
		borrowAutoDao.delete(id);
	}

	@Override
	public int getPhoneOnly(String phone) {
		return userDao.checkPhone(phone);
	}

	/**
	 * 第三方开户业务处理
	 * @throws Exception 
	 */
	@Override
	public void apiUserRegister(User user) throws Exception {
		User currUser = getUserById(user.getUserId());
		if (currUser == null) {
			throw new BussinessException("请您先登录！！！");
		}
		if (StringUtils.isBlank(currUser.getApiId())
				|| StringUtils.isBlank(currUser.getApiUsercustId())) {
			// 业务处理
			currUser.setApiId(user.getApiId());
			currUser.setApiUsercustId(user.getApiUsercustId());
			//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
			Rule rule = ruleService.getRuleByNid("is_phone_check");
			if(rule!=null && rule.getStatus() == 1){
				if(rule.getValueIntByKey("status")==0){
					currUser.setPhone(user.getPhone());
					currUser.setPhoneStatus(1);
				}
			}
			//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 end
			// 汇付开户成功以后，将用户的实名状态修改为实名成功的状态
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 start
			int apiType = NumberUtils.getInt(Global.getValue("api_code"));
			currUser.setRealStatus(1);
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 end
			currUser.setApiStatus(1);
			userDao.update(currUser);
			//add by lj 20161126 生成自己的专属邀请码
			if (currUser.getUserType().getTypeId() == 2) {
				makeInviteCode(currUser);
			}

			//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 start
			//实名成功送红包
			rewardExtendService.doRegisterIdentRedPacket(currUser);
			//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 end
			
			//v1.8.0.4_u4  TGPROJECT-409 wsl 2014-09-02 start
			//实名成功送奖励，大互联在使用
			rewardExtendService.doRegisterApiAwardsMoney(currUser);
			//v1.8.0.4_u4  TGPROJECT-409 wsl 2014-09-02 end

		/*	MsgReq req = new MsgReq();
			req.setIp("");
			req.setReceiver(user);
			req.setSender(new User(Constant.ADMIN_ID));
			req.setMsgOperate(this.msgService.getMsgOperate(7));
			req.setTime(DateUtils.dateStr4(new Date()));
			req.setType("实名认证");
			req.setStatus("通过");
			req.setMsg("身份证");
			req.setNo(user.getCardId());
			DisruptorUtils.sendMsg(req);*/
			//实名认证成功给用户发短信,站内信
			MsgReq msgReq = new MsgReq();
			msgReq.setUsername(user.getUsername());
			msgReq.setSender(new User(Constant.ADMIN_ID));
			msgReq.setReceiver(user);
			msgReq.setTime(DateUtils.dateStr4(new Date()));
			msgReq.setMsgOperate(this.msgService.getMsgOperate(7));
			DisruptorUtils.sendMsg(msgReq);
			//实名积分处理
			userCreditService.realNameCredit(currUser);
		} else { //此业务已经处理完成，直接返回
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 start
			int apiType = NumberUtils.getInt(Global.getValue("api_code"));
			if(apiType==3&&user.getApiStatus()!=1){
				currUser.setRealStatus(0);
			}else{
				currUser.setRealStatus(1);
			}
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 end
			logger.info("用户：" + currUser.getUserId() + "  开户已经成功，请查询");
			return;
		}
	}
	
    private void makeInviteCode(User user) {
		String phoneStr = user.getPhone();
		String invitateCode = RC4Util.encry_RC4_string(phoneStr.substring(phoneStr.length() - 3, phoneStr.length()), UUID.randomUUID().toString()).toUpperCase();
		UserInvitateCode userInvitateCode = userInvitateCodeService.findEntityByUserId(user.getUserId());
		if(userInvitateCode==null){
			userInvitateCode=new UserInvitateCode();
			userInvitateCode.setAddTime(user.getAddtime());
			userInvitateCode.setInvitateCode(invitateCode);
			userInvitateCode.setParentInvitateCodeId(0);
			userInvitateCode.setStatus(1);
			userInvitateCode.setUser(user);
		}else{
			userInvitateCode.setInvitateCode(invitateCode);
		}
		userInvitateCodeService.save(userInvitateCode);
		logger.info("实名认证成功生成的我的专属邀请码：" + invitateCode);
	}


	//v1.8.0.4  TGPROJECT-42   qj  2014-04-09 start
	/**
	 *  第三方授权（投标、还款、扣除管理费授权）
	 * @throws Exception 
	 */
	@Override
	public void apiUserActivate(User user) {
		User currUser = getUserById(user.getUserId());
		if(StringUtils.isBlank(currUser.getApiId())
				|| StringUtils.isBlank(currUser.getApiUsercustId())||currUser.getRealStatus() != 1 ){
			throw new BussinessException("请您先开通" + Global.getValue("api_name") + "账户");
		}
		if (currUser.getApiLoanAuthorize() == 0) {
			// 业务处理
			currUser.setApiLoanAuthorize(1);
			userDao.update(currUser);
		} else { //此业务已经处理完成，直接返回
			logger.info("用户：" + currUser.getUserId() + "  授权还款已经成功，请查询");
			return;
		}
	}
	//v1.8.0.4  TGPROJECT-42   qj  2014-04-09 stop

	public List<InviteUser> getInvitreUser(long userid){
		List<InviteUser> inviteUsers = inviteUserDao.getInvitreUser(userid);
		return inviteUsers;
	}

	public PageDataList getInvitreUserBySearchParam(SearchParam  param) {
		return inviteUserDao.findPageList(param);
	}
	//v1.8.0.4 TGPROJECT-249 lx start
	public PageDataList getInvitreUserBySearchParam(SearchParam  param,SearchParam  tenderParam){
		List<InviteUserSummary> list=inviteUserDao.getInvitreUserBySearchParam(param,tenderParam);
		return new PageDataList(param.getPage(),list);
	}
	//v1.8.0.4 TGPROJECT-249 lx end
	public PageDataList getUserCacheBySearchParam(SearchParam  param) {
		return userCacheDao.findPageList(param);
	}
	public String getInvitre(long userid) {
		String inviteusername = "";
		InviteUser inviteUser = inviteUserDao.getInviter(userid);
		if(inviteUser!= null){
			inviteusername = inviteUser.getInviteUser().getUsername();
			return inviteusername;
		}else{			
			return "-";
		}
	}
	@Override
	public void ywdRegiest(YwdUser user){
		ywdUserDao.save(user);
	}

	// v1.8.0.4 TGPROJECT-46 start
	@Override
	public void updateUserCache(UserCache uc,int type){
		uc.setCashForbid(type);
		userCacheDao.update(uc);
	}
	// v1.8.0.4 TGPROJECT-46 end
	//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start  
	@Override
	public TempIdentifyUser inintTempIdentifyUser(User user){

		TempIdentifyUser tempIdentifyUser = new TempIdentifyUser();
		if(user!=null){
			tempIdentifyUser.setEmailStatus(user.getEmailStatus());
			tempIdentifyUser.setRealStatus(user.getRealStatus());
			tempIdentifyUser.setPhoneStatus(user.getPhoneStatus());
			//int payPwdStatus=(user.getPaypassword()!=null)?1:0;
			if(user.getPaypassword()!=null && !StringUtils.isBlank(user.getPaypassword())){
				tempIdentifyUser.setPayPwdStatus(1);	
			}else{
				tempIdentifyUser.setPayPwdStatus(0);	
			}
			int userBankStatus=(user.getAccountBanks().size()>0)?1:0;
			tempIdentifyUser.setUserBankStatus(userBankStatus);

		}
		return tempIdentifyUser;
	}
	//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end  

	//v1.8.0.4_u1 TGPROJECT-127  zxc start
	@Override
	public UserCreditSummary getUserCreditSummary(long userId){

		return userDao.getUserCreditSummary(userId);
	}
	//v1.8.0.4_u1 TGPROJECT-127  zxc end

	//v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户  添加 编号义乌贷编号处理 start
	/**
	 * 查询义乌贷vip卡激活的的用户的数量
	 * @return
	 */
	@Override
	public synchronized int getYwdVipUsersCount(){
		return  userDao.getYwdVipUsersCount();
	}
	//v1.8.0.4_u1 TGPROJECT-248【全部】 义乌贷vip卡活动用户  添加 编号义乌贷编号处理 end

	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	@Override
	public void yjfRealNameCall(NewAuthorize na){
		if(na.getStatus().equals("success")){
			User user = userDao.getUserByApiId(na.getUserId());
			if(user == null){
				logger.info("易极付实名回调用户不存在:"+na.getUserId());
				return;
			}else{
				user.setRealname(na.getRealName());
				user.setCardId(na.getCertNo());
				user.setRealStatus(1);
				userDao.save(user);
			}
		}
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start

	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-09 start
	@Override
	public void initUserProperty() {
		List<User> userList = userDao.findAll();
		for (User user : userList) {
			UserProperty userPro = new UserProperty();
			userPro.setUser(user);
			userPropertyDao.save(userPro);
		}
	}

	@Override
	public void docheckStatus(String status) {
		userDao.checkStatus(status);
	}
	
	@Override
	public void updateUserProperty(UserProperty userProp) {
		userPropertyDao.save(userProp);
	}

	@Override
	public List<UserProperty> getNotContractUser() {
		SearchParam param = new SearchParam();
		param.addParam("isContract",Operator.EQ ,1);
		param.addParam("contractStatus",Operator.EQ ,0);
		return userPropertyDao.findAllPageList(param).getList();
	}

	@Override
	public void sendMsgToContractUser() {
		List<UserProperty> list = this.getNotContractUser();
		for (UserProperty userProperty : list) {
			//当extend字段为0或者空时，发送短信、邮件、站内信
			if(StringUtils.isBlank(userProperty.getExtend())||userProperty.getExtend().equals("0")){
				//发送短信  邮件  
				MsgReq req = new MsgReq();
				req.setUsername(userProperty.getUser().getUsername());
				req.setSender(new User(Constant.ADMIN_ID));
				req.setReceiver(userProperty.getUser());
				req.setMoney(userProperty.getContractMoney()+"");
				req.setTenderAccount((userProperty.getUser().getAccount().getTotal() -userProperty.getUser().getAccount().getRepay())+"");
				req.setMsgOperate(this.msgService.getMsgOperate(25));
				req.setTime(DateUtils.dateStr4(new Date()));
				try {
					DisruptorUtils.sendMsg(req);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("发送信息给用户："+userProperty.getUser().getUsername()+"失败,"+e.getMessage());
				}
				//发送信息完成修改状态为1
				userProperty.setExtend("1");
				userPropertyDao.save(userProperty);
			}
		}
	}
	// v1.8.0.3_u3 TGPROJECT-332 qinjun 2014-06-09 end

	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	/**
	 * 汇付企业开户
	 * @param user
	 * @param reg
	 */
	public void huifuUserCorpRegister(User user,CorpRegister reg){
		if(!user.getCustomerType().equals(ChinaPnrType.CORP)){
			throw new BussinessException("您不是企业用户无法开户！");
		}
		if(user.getApiStatus() == 1 && !StringUtils.isBlank(user.getApiUsercustId())){
			throw new BussinessException("您已经开户了！");
		}
		if(reg.getAuditStat().equals("Y")){
			DrawBank db = drawBankDao.getDrawBankByBankCode(reg.getOpenBankId());
			AccountBank ab = new AccountBank();
			ab.setUser(user);
			ab.setAccount(reg.getCardId());
			ab.setBank(db);
			ab.setAddip("");
			ab.setAddtime(new Date());
			ab.setStatus(1);
			accountBankDao.save(ab);
			user.setApiId(reg.getUsrId());
			user.setApiUsercustId(reg.getUsrCustId());
			user.setApiStatus(1);
			user.setRealStatus(1);
			userDao.save(user);
		}else if(reg.getAuditStat().equals("R")||reg.getAuditStat().equals("F")){
			user.setRealStatus(2);
			userDao.save(user);
		}else{
			user.setRealStatus(4);
			userDao.save(user);
		}
	}
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 end

	//v1.8.0.4_u5 TGPROJECT-381 qinjun  2014-07-30  start 
	@Override
	public void yjfFailRealName(String apiId) {
		User user =userDao.getUserByApiId(apiId);
		if(user != null){
			if(user.getRealStatus() == 4){
				throw new BussinessException("用户身份证过期已经处理");
			}else{
				if(user.getRealStatus() ==1){
					user.setRealStatus(4);
					userDao.merge(user);
				}else{
					throw new BussinessException("用户实名状态错误："+user.getRealStatus());
				}
			}
		}else{
			throw new BussinessException("用户不存在");
		}

	}
	//v1.8.0.4_u5 TGPROJECT-381 qinjun  2014-07-30  end

	/**
	 * 绑定手机后，只获取手机验证码
	 * 
	 * @param user
	 * @param phone
	 *            return 唯一标示
	 */
	@Override
	public String getMObileCodeOnly(User user) {
		SmsCaptcha sc = smsCaptcha(user.getApiId() + "", user.getRealname());
		if (sc != null) {
			return sc.getCheckCodeUniqueId();
		} else {
			return null;
		}
	}
	/**
	 * 查询易极付实名认证 状态。
	 * 为了解决如下情况，特意写了此方法
	 * 网站实名没通过，但是易极付已经通过，在用户登录的时候校验，如果易极付通过，就直接实名通过。
	 * @param user
	 * @return
	 */
	@Override
	public int getUserDrawCashStatus(User user) {
		return -1;
	}
	
	@Override
	public int sumRegisteredUser() {
		return userDao.sumRegisteredUser();
	}

	@Override
	public Date getAddTimeById(long userId) {
		return userDao.getAddTimeById(userId);
	}

	@Override
	public List<User> getAllUser() {
		return userDao.getAllUser();
	}

	@Override
	public void addCodeToUser(long userId, Date addtime, String invitateCode) {
		// TODO Auto-generated method stub
		userDao.addCodeToUser(userId,addtime,invitateCode);
	}

	//add by lxm 2016-11-29 11:26:11
	@Override
	public List<User> getCurrentUserById() {
		return userDao.getCurrentUserById();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		userDao.clear();
	}

}
