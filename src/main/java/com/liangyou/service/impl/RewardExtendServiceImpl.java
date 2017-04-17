package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.RewardType;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.RewardExtendDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.RewardExtend;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.WebPayModel;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.PrizeService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;

import freemarker.template.utility.DateUtil;
/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz 
 * @author wujing
 * 奖励发放service
 *
 */
@Service(value="rewardExtendService")
@Transactional
public class RewardExtendServiceImpl implements RewardExtendService {

	Logger logger = Logger.getLogger(RewardExtendServiceImpl.class);

	@Autowired
	private RewardExtendDao rewardExtendDao;
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ApiService apiService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private PrizeService prizeService;

	@Override
	public void addReward(RewardExtend rewardExtend) {
		rewardExtendDao.save(rewardExtend);
	}

	@Override
	public RewardExtend getRewardByid(long id) {
		RewardExtend reward = rewardExtendDao.find(id);
		return reward;
	}


	@Override
	public PageDataList<RewardExtend> getPageLIstReward(SearchParam param) {
		PageDataList<RewardExtend> pageList = rewardExtendDao.findPageList(param);
		return pageList;
	}

	@Override
	public void doTenderRewardForInvite(List<BorrowTender> borrowTenders) {
		//判断规则是否开启
		Rule rule = ruleService.getRuleByNid("tender_reward_rule");
		if (rule != null && rule.getStatus() ==1) {
			int tenderReward = rule.getValueIntByKey("tender_reward");
			if (tenderReward==1) {
				double tenderRewardApr = Global.getDouble("tender_account_apr");  //获取投标奖励比例
				List<RewardExtend> rewardList = new ArrayList<RewardExtend>();
				for (BorrowTender borrowTender : borrowTenders) {
					User tenderUser = borrowTender.getUser();
					if (null !=tenderUser.getInviteUser()) {   //判断是否有推荐人
						double paymoney =NumberUtils.format2(borrowTender.getAccount()*tenderRewardApr) ;  //计算发放金额
						String content ="推荐人"+tenderUser.getUsername()+"投标奖励:投标金额："+borrowTender.getAccount()+"，发放比例："+tenderRewardApr*100+"%"+"发放金额："+paymoney;
						RewardExtend reward = new RewardExtend(RewardType.TENDERINVITE, paymoney, 0, tenderUser.getInviteUser().getInviteUser(),content , new Date());
						reward.setTenderUser(new User(tenderUser.getUserId()));
						reward.setBorrow(new Borrow(borrowTender.getBorrow().getId()));
						AccountLog log = new AccountLog(reward.getRewardUser().getUserId(),Constant.TENDER_REWARD , Constant.ADMIN_ID);
						log.setRemark("投标，推荐人奖励发放："+paymoney+"元。");
						log.setMoney(paymoney);
						doRewardToUser(rewardList, reward,log);
					}
				}
				if (rewardList.size()>0) {
					rewardExtendDao.save(rewardList);
				}
			}
		}
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	@Override
	public void doRewardAsReadyMoney(){
		List<RewardExtend> rewardList = rewardExtendDao.getRewardListByExtendWayStatus(0,RewardType.EXTENDWAYREWAED);
		List<RewardExtend> newList = new ArrayList<RewardExtend>();
		for (RewardExtend rewardExtend : rewardList) {
			AccountLog log = new AccountLog(rewardExtend.getRewardUser().getUserId(),"",Constant.ADMIN_ID);
			Rule rule = null ;
			boolean isGive = false;
			switch (rewardExtend.getRewardType()) {
			//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end
			case RewardType.REGISTER://注册奖励，需投资有回款
				rule =  ruleService.getRuleByNid("register_reward_rule");	
				if(rule!=null && rule.getStatus() == 1){
					if( rule.getValueIntByKey("register") == 1){
						isGive = registerRewardAsReadyMoney(rule,rewardExtend,log);
					}
				}
				break;
				//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
			case RewardType.VIPINVITE://推荐人vip提成奖励
				rule = ruleService.getRuleByNid("invite_award_rate");
				if(rule!=null && rule.getStatus() == 1){
					isGive = rewardVipAsReadyMoney(rewardExtend, log);
				}
				break;
			case RewardType.INTERESTINVITE://推荐人vip提成奖励
				rule = ruleService.getRuleByNid("invite_award_rate");
				if(rule!=null && rule.getStatus() == 1){
					isGive = rewardInterestAsReadyMoney(rewardExtend, log);
				}
				break;
			default:
				continue;
			}
			if(isGive){
				doRewardToUser(newList, rewardExtend, log);	
			}
		}
		if (newList.size()>0) {
			rewardExtendDao.update(newList);
		}
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end
	/**
	 * 处理关于注册奖励发放功能：注册送50金额，投资达到1000后，有回款才发放奖励
	 */
	private boolean registerRewardAsReadyMoney(Rule rule,RewardExtend rewardExtend,AccountLog log){
		logger.info("进入注册奖励发放------------>"+"注册送50，出借大于1000有回款后发放");
		double recallColletion = Global.getDouble("tender_recall_account");   // 获取回款多少时发放注册奖励条件
		double tenderRecall = rule.getValueDoubleByKey("tender_recall");
		List list = borrowDao.getTenderRecall(rewardExtend.getRewardUser().getUserId(),tenderRecall, recallColletion);
		if (null !=list && list.size()>0) {   //满足奖励发放条件
			String remark = "出借有效金额大于"+tenderRecall+"元，并回款大于："+recallColletion+"元，发放注册奖励";
			log.setMoney(rewardExtend.getRewardMoney());
			log.setType(Constant.TENDER_REWARD);
			log.setRemark(remark);
			rewardExtend.setContent(remark);
			return true;
		}
		return false;
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 处理vip提成奖励
	 */
	private boolean rewardVipAsReadyMoney(RewardExtend rewardExtend,AccountLog log){
		log.setMoney(rewardExtend.getRewardMoney());
		log.setType(Constant.INVITE_VIP_REWARD);
		log.setRemark(rewardExtend.getContent());	
		return true;
	}

	/**
	 * 处理利息管理费提成奖励
	 */
	private boolean rewardInterestAsReadyMoney(RewardExtend rewardExtend,AccountLog log){
		log.setMoney(rewardExtend.getRewardMoney());
		log.setType(Constant.INVITE_INTEREST_REWARD);
		log.setRemark(rewardExtend.getContent());	
		return true;
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end
	@Override
	public void doRegisterAndIdentReward() {
		Rule rule = ruleService.getRuleByNid("register_reward_rule");  //根据规则校验是否开启此功能
		if (null !=rule && rule.getStatus() ==1) {
			int registerTender = rule.getValueIntByKey("register_tender");
			if (registerTender ==1 ) {
				double rewardMOney =rule.getValueDoubleByKey("register_tender_money");  //获取奖励金额
				double tenderUseMoney = rule.getValueDoubleByKey("tender_user_money");   //出借有效金额限制
				int type =RewardType.REGISTERANDIDENT;  //奖励类型
				List tenderUseMoneyList = borrowDao.getTenderUseAccount(tenderUseMoney);  //获取有效出借金额 返回结果:userid,money
				List<RewardExtend> rewardList  = new ArrayList<RewardExtend>();
				//循环查询结果
				Iterator iterator = tenderUseMoneyList.iterator(); 
				while (iterator.hasNext()) {
					Object[] row = ( Object[]) iterator.next();
					RewardExtend reward = rewardExtendDao.getReward(NumberUtils.getLong(row[0].toString()), type);
					if (null == reward) {  //判断是否已经发送过奖励信息，不存在，说明未发送过，处理奖励发放
						logger.info("投标人："+NumberUtils.getLong(row[0].toString()));
						//被推荐人
						User tenderUser = userDao.find(NumberUtils.getLong(row[0].toString()));
						boolean result = checkUser(tenderUser, "0,0,0,1");
						if (!result) {  //判断是否已经通过系统规定认证
							continue; 
						}
						//系统发放奖励:被推荐人奖励
						double rewardMoney = rewardMOney; 
						String content ="注册并实名认证通过，出借有效金额已经超过"+tenderUseMoney+"元，奖励"+rewardMoney;
						RewardExtend tenderReward = new RewardExtend(type, rewardMoney, 0,tenderUser,content , new Date());
						tenderReward.setTenderUser(new User(tenderUser.getUserId()));
						String logRemark = "注册并实名认证通过,出借有效金额累计满"+tenderUseMoney+"元，系统赠送！";
						AccountLog log = new AccountLog(tenderReward.getRewardUser().getUserId(),Constant.TENDER_REWARD , Constant.ADMIN_ID);
						log.setRemark(logRemark);
						log.setMoney(rewardMoney);
						doRewardToUser(rewardList, tenderReward,log);
						//奖励推荐人,判断此投标用户是否有推荐人
						if (null !=tenderUser.getInviteUser()) {
							User inviteUser = tenderUser.getInviteUser().getInviteUser();
							RewardExtend inviteReward = new RewardExtend(type, rewardMoney, 0,inviteUser,content , new Date());
							inviteReward.setTenderUser(new User(tenderUser.getUserId()));
							log = new AccountLog(inviteReward.getRewardUser().getUserId(),Constant.TENDER_REWARD , Constant.ADMIN_ID);
							log.setRemark("你推荐的用户:"+tenderUser.getUsername()+"注册并实名认证通过,出借有效金额累计满"+tenderUseMoney+"元，系统赠送！");
							log.setMoney(rewardMoney);
							doRewardToUser(rewardList, inviteReward, log);
						}
					}
				}
				if (rewardList.size()>0) {
					rewardExtendDao.save(rewardList);
				}
			}
		}
	}


	/************* TGPROJECT-355  注册送红包  start  51帮你新功能**************/
	@Override
	public void doRegisterIdentRedPacket(User user) {
		Rule rule = ruleService.getRuleByNid("register_extend_red");
		if (null !=rule && rule.getStatus() ==1) {  //判断是否有开启此功能
			int isIdent = rule.getValueIntByKey("isIdent");  //是否实名通过
			double redMoney =rule.getValueDoubleByKey("red_money");   //奖励金额
			int extendWay = rule.getValueIntByKey("reward_way");  //发放方式
			int validDay = rule.getValueIntByKey("valid_day");    //红包有效期
			int validType = rule.getValueIntByKey("validType");  //红包有效期类型
			if (isIdent ==1) {
				if (user.getRealStatus() !=1) {   //实名通过
					return ;
				}
				//添加红包
				String content = "注册实名认证通过，发放红包"+redMoney+"元";
				RewardExtend reward = new RewardExtend(RewardType.REGISTERIENTRAD, redMoney, 0, user, content, new Date());
				reward.setRewardWay(extendWay);  //红包类型
				//TGPROJECT-366  处理有效期 start
				doValidTime(validType, validDay, reward);
				//TGPROJECT-366  处理有效期 end
				rewardExtendDao.save(reward);
			}
		}
	}
	/************* TGPROJECT-355  注册送红包  end 51帮你 **************/

	/************* TGPROJECT-409 注册送奖金  start  大互联新功能 **************/
	@Override
	public void doRegisterApiAwardsMoney(User user) {
		Rule rule = ruleService.getRuleByNid("register_awards_money");
		if (null !=rule && rule.getStatus() ==1) {//判断是否有开启此功能
			double registerAward =rule.getValueDoubleByKey("register_award");//注册人奖励金额
			int isInvited = rule.getValueIntByKey("is_invited");//是否注册推荐人有奖励
			double inviterAward =rule.getValueDoubleByKey("inviter_award");//推荐人奖励金额
			//判断奖励有效时间
			extendAwardsMoneyValidTime(rule);

			int type =RewardType.REGISTERIENTRAD;//注册送奖金类型
			List<RewardExtend> rewardList  = new ArrayList<RewardExtend>();
			RewardExtend reward = rewardExtendDao.getReward(user.getUserId(), type);
			if(null == reward){//判断是否已经发送过注册奖励信息，不存在，说明未发送过，处理奖励发放
				double rewardMoney = registerAward;
				String content ="用户"+user.getUsername()+"注册并实名认证通过，奖励"+rewardMoney;
				RewardExtend registerReward = new RewardExtend(type, rewardMoney, 0,user,content , new Date());
				registerReward.setTenderUser(new User(user.getUserId()));
				AccountLog log = new AccountLog(registerReward.getRewardUser().getUserId(),Constant.REGISTER_REWARD , Constant.ADMIN_ID);
				log.setRemark("注册并实名认证通过,奖励"+rewardMoney+"元，系统赠送！");
				log.setMoney(rewardMoney);
				doRewardToUser(rewardList, registerReward,log);

				//注册是否推荐人有奖励，有推荐人且推荐人有奖励才发放
				if (isInvited ==1 && null !=user.getInviteUser()) {
					rewardMoney = inviterAward; 
					User inviteUser = user.getInviteUser().getInviteUser();
					String inviteContent ="推荐人获得注册奖励"+rewardMoney+"元，系统赠送！";
					RewardExtend inviteReward = new RewardExtend(type, rewardMoney, 0,inviteUser,inviteContent , new Date());
					inviteReward.setTenderUser(new User(user.getUserId()));
					log = new AccountLog(inviteReward.getRewardUser().getUserId(),Constant.REGISTER_REWARD , Constant.ADMIN_ID);
					log.setRemark("推荐人获得注册奖励"+rewardMoney+"元，系统赠送！");
					log.setMoney(rewardMoney);
					doRewardToUser(rewardList, inviteReward, log);
				}
			}
			if (rewardList.size()>0) {
				rewardExtendDao.save(rewardList);
			}
		}
	}

	/**
	 * 判断奖励有效时间,在奖励有效期内才发放奖励
	 * @param rule
	 */
	private void extendAwardsMoneyValidTime(Rule rule){
		int isValidDay = rule.getValueIntByKey("is_valid_day");//是否启用有效时间判断
		Date dayStart = DateUtils.getDate2(rule.getValueStrByKey("day_start"));//奖励开始时间
		Date dayEnd = DateUtils.getDate2(rule.getValueStrByKey("day_end"));//奖励结束时间
		Date nowTime = new Date();
		if(isValidDay == 1){
			//当前时间与奖励开始时间和结束时间比较 是否没到时间或者已经结束
			if(nowTime.compareTo(dayStart)<0 || nowTime.compareTo(dayEnd)>0){
				return;
			}
		}

	}
	/************* TGPROJECT-409 注册送奖金  start  大互联新功能  **************/

	/**
	 * 红包兑现(51帮你)   易极付使用（如果其它接口需要使用需要重新开发） 
	 */
	@Override
	public void doRedExtend(String[] ids) {
		List<RewardExtend> rewardList = rewardExtendDao.getRewardListByIds(ids);
		for (RewardExtend reward : rewardList) {
			if (reward.getStatus()!=0) {  //判断红包状态
				throw new BussinessException("红包状态不匹配，不能兑现："+reward.getContent());
			}
			if (reward.getEndTime().getTime() < (new Date()).getTime() && reward.getValidType() !=3) {  //判断红包是否具有有效期
				throw new BussinessException("红包已经超过了有效期，不能兑现："+reward.getContent());
			}
		}
		doRewardToMoney(rewardList, ids);
	}

	private void doRewardToMoney(List<RewardExtend> rewardList,String[] ids){
		double money =rewardExtendDao.getSumMoneyRewardById(ids);
		List<Object> taskList = new ArrayList<Object>();
		User user = rewardList.get(0).getRewardUser();
		WebPayModel pay = new WebPayModel(user,money);
		apiService.doWebPayMoney(pay, taskList);
		logger.info("收款账户"+user.getApiId()+"用户id"+user.getUserId());
		try {
			borrowService.autoYjfPay((YjfPay)taskList.get(0));
			//处理成功，更新用户账户资金信息
			for (RewardExtend reward : rewardList) {
				money = reward.getRewardMoney();
				AccountLog log = new AccountLog(reward.getRewardUser().getUserId(),Constant.REWAED_TOMONEY , Constant.ADMIN_ID);
				log.setRemark(reward.getContent());
				log.setMoney(reward.getRewardMoney());
				//处理成功，更新用户账户资金信息
				Account account = accountDao.getAcountByUser(new User(user.getUserId()));
				accountDao.updateAccount(money, money, 0d, user.getUserId());
				//添加资金记录
				log.setMoney(money);
				log.setTotal(account.getTotal());
				log.setUseMoney(account.getUseMoney());
				log.setNoUseMoney(account.getNoUseMoney());
				log.setCollection(account.getCollection());
				log.setRepay(account.getRepay());
				accountLogDao.save(log);
				reward.setOrderNo(((YjfPay)taskList.get(0)).getOrderno());
				reward.setExtendTime(new Date());
				reward.setStatus(1);
				rewardExtendDao.save(reward);
			}
		} catch (Exception e) {
			logger.info("奖励发放，第三方处理异常："+e.getMessage()+"受益人："+user.getUserId());
			throw new BussinessException(e.getMessage());
		}
	}


	/**************  TGPROJECT-354用户充值送红包    *************/
	@Override
	public void rechargeExtendRedPacket(User user,double money) {
		Rule rule = ruleService.getRuleByNid("recharge_reward_rad");
		if (null !=rule && rule.getStatus() ==1) {
			int extendMoney = rule.getValueIntByKey("extend_money");  //奖励金额
			int extendWay = rule.getValueIntByKey("extend_way");  //发送方式：0奖励，1：红包
			int isFirst =rule.getValueIntByKey("is_first");  //首次充值
			int validDay = rule.getValueIntByKey("valid_day");   //红包有效期
			int validType = rule.getValueIntByKey("validType");  //红包有效期类型
			if (isFirst ==1) {  //判断是否只有首次充值赠送
				int rechargeNumber = rewardExtendDao.getRechargeSuccessByUser(user.getUserId(), 1);
				if (rechargeNumber>0) {
					logger.info("用户非第一次充值，不能享受首次充值赠送奖励");
					return ;
				}
			}
			double rewardMoney = extendMoney<1?money *extendMoney:extendMoney;  //判断充值红包奖金发送方式，按金额发还是按充值比例发送
			//添加红包
			String content = "用户充值成功注册实名认证通过，发放红包"+rewardMoney+"元";
			RewardExtend reward = new RewardExtend(RewardType.RECHARGERAD, rewardMoney, 0, user, content, new Date());
			reward.setRewardWay(extendWay);  //红包类型
			//TGPROJECT-366  处理有效期 start
			doValidTime(validType, validDay, reward);
			//TGPROJECT-366  处理有效期 end
			rewardExtendDao.save(reward);
		}
	}

	/**************  TGPROJECT-354用户第一次投标成功送红包    *************/
	@Override
	public void tenderExtendRedPacket(User user,double money) {
		Rule rule = ruleService.getRuleByNid("tender_reward_rad");
		if (null !=rule && rule.getStatus() ==1) {
			int extendMoney = rule.getValueIntByKey("extend_money");  //奖励金额
			int extendWay = rule.getValueIntByKey("extend_way");  //发送方式：0奖励，1：红包
			int isFirst =rule.getValueIntByKey("is_first");  //首次充值
			int validDay = rule.getValueIntByKey("valid_day");   //红包有效期
			int validType = rule.getValueIntByKey("validType");  //红包有效期类型
			if (isFirst ==1) {//判断是否只有首次充值赠送
				int rechargeNumber = rewardExtendDao.getTenderSuccessByUser(user.getUserId());
				if (rechargeNumber>0) {
					logger.info("用户非第一次出借，不能享受首次投标赠送奖励");
					return ;
				}
			}
			double rewardMoney = extendMoney<1?money *extendMoney:extendMoney;  //判断充值红包奖金发送方式，按金额发还是按充值比例发送
			//添加红包
			User inviteUser = user.getInviteUser().getInviteUser();
			String content = "用户"+user.getUsername()+"第一次投标成功，发放红包"+rewardMoney+"元";
			RewardExtend reward = new RewardExtend(RewardType.TENDERRAD, rewardMoney, 0, inviteUser, content, new Date());
			reward.setRewardWay(extendWay);  //红包类型
			//TGPROJECT-366  处理有效期 start
			doValidTime(validType, validDay, reward);  //设置有效日期
			//TGPROJECT-366  处理有效期 end
			rewardExtendDao.save(reward);
		}
	}

	@Override
	public void tenderFirstReward(User tenderUser,double tenderMoney){
		Rule rule = ruleService.getRuleByNid("tender_firstReward_rule");
		if (null !=rule && rule.getStatus() ==1) {

			double tenderUserReward = rule.getValueDoubleByKey("tenderUser_reward");
			double inviteUserReward = rule.getValueDoubleByKey("inviteUser_reward");
			int isInvited = rule.getValueIntByKey("is_invited");//是否投标推荐人有奖励
			double tenderCheckMoney = rule.getValueDoubleByKey("tender_check_money");//首次投标奖励限制金额

			int isFirst =rule.getValueIntByKey("is_first");//首次出借
			if (isFirst ==1) {//判断是否只有首次出借赠送
				int rechargeNumber = rewardExtendDao.getTenderSuccessByUser(tenderUser.getUserId());
				if (rechargeNumber>0) {
					logger.info("用户非第一次出借，不能享受首次投标赠送奖励");
					return ;
				}
			}
			//判断奖励有效时间
			extendAwardsMoneyValidTime(rule);

			int type =RewardType.REGISTERANDIDENT;//注册出借奖励类型
			List<RewardExtend> rewardList  = new ArrayList<RewardExtend>();
			RewardExtend reward = rewardExtendDao.getReward(tenderUser.getUserId(), type);
			if(null == reward && tenderMoney >= tenderCheckMoney){//判断是否已经发送过注册首次奖励信息，不存在，说明未发送过，处理奖励发放
				double rewardMoney = tenderUserReward;
				String content ="用户"+tenderUser.getUsername()+"首次投标成功，奖励"+rewardMoney;
				RewardExtend tenderReward = new RewardExtend(type, rewardMoney, 0,tenderUser,content , new Date());
				tenderReward.setTenderUser(new User(tenderUser.getUserId()));
				String logRemark ="首次投标成功，奖励"+rewardMoney;
				AccountLog log = new AccountLog(tenderReward.getRewardUser().getUserId(),Constant.TENDER_REWARD , Constant.ADMIN_ID);
				log.setRemark(logRemark);
				log.setMoney(rewardMoney);
				doRewardToUser(rewardList, tenderReward,log);
				//奖励推荐人,判断此投标用户是否有推荐人
				if (isInvited == 1 && null !=tenderUser.getInviteUser()) {
					rewardMoney = inviteUserReward; 
					User inviteUser = tenderUser.getInviteUser().getInviteUser();
					String inviteContent ="推荐用户出借成功，奖励" + rewardMoney + "元";
					RewardExtend inviteReward = new RewardExtend(type, rewardMoney, 0,inviteUser,inviteContent , new Date());
					inviteReward.setTenderUser(new User(tenderUser.getUserId()));
					log = new AccountLog(inviteReward.getRewardUser().getUserId(),Constant.TENDER_REWARD , Constant.ADMIN_ID);
					log.setRemark("推荐用户出借成功，奖励" + rewardMoney + "元");
					log.setMoney(rewardMoney);
					doRewardToUser(rewardList, inviteReward, log);
				}
			}
			if (rewardList.size()>0) {
				rewardExtendDao.save(rewardList);
			}
		}
	}
	//wsl 首次出借奖励功能【德益网】 2014-09-02 end

	@Override
	public double getSumRewardById(String[] ids) {
		return rewardExtendDao.getSumMoneyRewardById(ids);
	}

	/**
	 * 校验用户认证信息
	 * @param user
	 * @param str：实名，手机，邮箱,视频认证
	 * @return
	 */
	private boolean checkUser(User user,String str){
		String[] strs = str.split(",");
		if ("0".equals(strs[0])) {  //校验实名信息
			if (user.getRealStatus() ==0) {
				return false;
			}
		}
		if ("0".equals(strs[1])) {
			if (user.getPhoneStatus() ==0) {
				return false;
			}
		}
		if ("0".equals(strs[2])) {
			if (user.getEmailStatus()==0) {
				return false;
			}
		} 
		if ("0".equals(strs[3])) {
			if (user.getVideoStatus() ==0) {
				return false;
			}
		}
		return true;
	}

	/*****       红包兑现   end    ******/
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 处理奖励发放方法
	 * @param user 奖励受益人
	 * @param rewardList   奖励发放对象集合
	 * @param money  奖励金额
	 * @param reward  奖励发放对象
	 * @param logRemark   log备注
	 */
	private void doRewardToUser(List<RewardExtend> rewardList,RewardExtend reward,AccountLog log){
		User user = reward.getRewardUser();
		double money = reward.getRewardMoney();
		List<Object> taskList = new ArrayList<Object>();
		WebPayModel pay = new WebPayModel(user,money);
		apiService.doWebPayMoney(pay, taskList);
		logger.info("收款账户"+user.getApiId()+"用户id"+user.getUserId());
		String tradeNo = "";
		boolean result = borrowService.doApiTask(taskList);
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		//cancel by lxm 2017-2-13 17:13:27
//		case 1://汇付
//			ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
//			tradeNo = cpm.getOrdId();
//			break;
//		case 2://易极付
//			YjfPay yjf = (YjfPay)taskList.get(0);
//			tradeNo = yjf.getOrderno();
//			break;
		case 3://双乾
			MmmPay mmmPay = (MmmPay)taskList.get(0);
			tradeNo = mmmPay.getOrderNo();
			break;
		default:
			break;
		}

		if (result) {  //接口处理成功
			//处理成功，更新用户账户资金信息
			Account account = accountDao.getAcountByUser(new User(user.getUserId()));
			accountDao.updateAccount(money, money, 0d, user.getUserId());
			//添加资金记录
			log.setMoney(money);
			log.setTotal(account.getTotal());
			log.setUseMoney(account.getUseMoney());
			log.setNoUseMoney(account.getNoUseMoney());
			log.setCollection(account.getCollection());
			log.setRepay(account.getRepay());
			accountLogDao.save(log);
			reward.setOrderNo(tradeNo);
			reward.setExtendTime(new Date());
			reward.setStatus(1) ;
		}else{
			reward.setStatus(-1);
			logger.info("奖励发放，第三方处理异常："+reward.getContent()+"受益人："+reward.getRewardUser().getUserId());
		}
		rewardList.add(reward);
	}
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end


	//TGPROJECT-366  红包有效期判断  start

	/**
	 * @param type   有效期类型 1：按天，2：按月
	 * @param validTime   有效时间
	 * @param reward      红包对象
	 */
	private void doValidTime(int type,int validTime ,RewardExtend reward){
		switch (type) {
		case 1:   //按天计算有效期
			Date dateDay = DateUtils.rollDay(reward.getAddTime(), validTime);
			reward.setValidDay(validTime);
			reward.setValidType(type);
			reward.setEndTime(dateDay);
			break;
		case 2:   //按月计算有效期
			Date dateMonth = DateUtils.rollMon(reward.getAddTime(), validTime);
			reward.setValidDay(validTime);
			reward.setValidType(type);
			reward.setEndTime(dateMonth);
		case 3:   //当为永久时，不设置有效天数和结束日期
			reward.setValidType(type);
			break;
		default:
			break;
		}
	}
	//TGPROJECT-366  红包有效期判断  end

	


}
