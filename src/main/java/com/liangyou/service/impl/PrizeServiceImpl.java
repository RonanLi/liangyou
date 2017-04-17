package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.PrizeResultType;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.PrizeDetailDao;
import com.liangyou.dao.PrizeGoodsDao;
import com.liangyou.dao.PrizeRuleDao;
import com.liangyou.dao.PrizeUserDao;
import com.liangyou.dao.RewardUsersDao;
import com.liangyou.dao.UserCreditDao;
import com.liangyou.dao.UserCreditLogDao;
import com.liangyou.dao.UserCreditTypeDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeGoods;
import com.liangyou.domain.PrizeRule;
import com.liangyou.domain.PrizeUser;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.prize.PrizeResult;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.PrizeService;
import com.liangyou.service.RuleService;
import com.liangyou.util.CreditUtils;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;

/**
 * 处理抽奖业务 v1.8.0.4_u4 TGPROJECT-367 qinjun 2014-07-16
 */
@Service(value = "prizeService")
@Transactional
public class PrizeServiceImpl extends BaseServiceImpl implements PrizeService {
	private static Logger logger = Logger.getLogger(PrizeServiceImpl.class);

	@Autowired
	private PrizeUserDao prizeUserDao;
	@Autowired
	private PrizeRuleDao prizeRuleDao;
	@Autowired
	private PrizeGoodsDao prizeGoodsDao;
	@Autowired
	private PrizeDetailDao prizeDetailDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private UserCreditDao userCreditDao;
	@Autowired
	private UserCreditLogDao userCreditLogDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private UserCreditTypeDao userCreditTypeDao;
	@Autowired
	private RewardUsersDao rewardUsersDao;

	/**
	 * 根据条件查询抽奖用户
	 * 
	 * @param param
	 * @return
	 */
	@Override
	public PageDataList<PrizeUser> getAllPrizeUserList(SearchParam param) {
		return prizeUserDao.findPageList(param);
	}

	@Override
	public List<PrizeRule> getAllPrizeRule() {

		return prizeRuleDao.findAll();
	}

	@Override
	public PrizeRule getPrizeRuleById(long id) {
		return prizeRuleDao.find(id);
	}

	@Override
	public void updatePrizeRule(PrizeRule prizeRule) {
		prizeRuleDao.update(prizeRule);
	}

	@Override
	public void addPrizeRule(PrizeRule prizeRule) {
		prizeRuleDao.save(prizeRule);

	}

	@Override
	public List<PrizeGoods> goodsList(long ruleId) {
		return prizeGoodsDao.getPrizeGoodsListByRuleId(ruleId);
	}

	@Override
	public PrizeGoods getPrizeGoodsById(long id) {
		return prizeGoodsDao.find(id);
	}

	@Override
	public void savePrizeGoods(PrizeGoods prizeGoods) {
		prizeGoodsDao.save(prizeGoods);
	}

	@Override
	public void updatePrizeGoods(PrizeGoods prizeGoods) {
		prizeGoodsDao.update(prizeGoods);
	}

	@Override
	public PrizeRule getPrizeRuleByPrizeType(int prizeType) {
		return prizeRuleDao.findByPropertyForUnique("prizeType", prizeType);
	}

	@Override
	public PrizeResult extractionPrize(long ruleId, User user, double money) {
		PrizeResult result = new PrizeResult();
		// 根据规则ID取得抽奖规则
		PrizeRule prizeRule = prizeRuleDao.find(ruleId);
		String prizeResultType = check(prizeRule, user);
		// 校验不通过的话，返回错误消息
		if (!StringUtils.isBlank(prizeResultType)) {
			result.setError(prizeResultType);
			return result;
		}
		// 抽奖
		List<PrizeGoods> objAwardList = goodsList(ruleId);
		PrizeGoods prizeGoods = getPrizeAward(objAwardList);
		// 如果没有中奖并且不是100%中奖时，则判断为不中奖
		if (prizeGoods == null && prizeRule.getIsAbsolute() == 0) {
			// 往中奖信息表中插入相应记录（不中奖）。
			addUserAward(user, prizeRule, prizeGoods);
			result.setError(PrizeResultType.RESULT_NO_AWARD);
			return result;
		} else if (prizeGoods == null && prizeRule.getIsAbsolute() != 0) {
			prizeGoods = objAwardList.get(objAwardList.size() - 1);
		}

		// 判断是否有奖品数量限制
		if (prizeGoods.getPrizeLimit() != 0) {
			// 剩余奖品数量
			long objAwardCnt = prizeGoods.getTotal() - prizeGoods.getBestow();
			// 如果没有奖品并且不是100%中奖时，则判断为不中奖
			if (objAwardCnt == 0 && prizeRule.getIsAbsolute() == 0) {
				// 往中奖信息表中插入相应记录（不中奖）。
				addUserAward(user, prizeRule, null);
				result.setError(PrizeResultType.RESULT_NO_AWARD_OBJ);
				return result;
			}
			// 如果没有奖品并且是100%中奖时，则变为概率最高的奖励
			if (objAwardCnt == 0 && prizeRule.getIsAbsolute() != 0) {
				// 奖励变为概率最高的奖励
				prizeGoods = objAwardList.get(objAwardList.size() - 1);
			}
		}

		// 更新奖品数量
		prizeGoodsDao.updateBestow(prizeRule.getId(), prizeGoods.getId());

		// 判断是否有总金额限制
		double awardMoney = 0;
		if (prizeRule.getMoneyLimit() != 0) {
			// 可用金额
			double availableMoney = prizeRule.getTotalMoney()
					- prizeRule.getBestowMoney();
			awardMoney = prizeGoods.getGoodsValue();
			// 如果中奖金额>可用金额并且不是100%中奖时，则判断为不中奖
			if (awardMoney > availableMoney && prizeRule.getIsAbsolute() == 0) {
				// 往中奖信息表中插入相应记录（不中奖）。
				addUserAward(user, prizeRule, null);
				result.setError(PrizeResultType.RESULT_MONEY_LIMIT);
				return result;
			}

			// 如果中奖金额>可用金额并且是100%中奖时，则变为概率最高的奖励
			if (awardMoney > availableMoney && prizeRule.getIsAbsolute() != 0) {
				// 奖励变为概率最高的奖励
				prizeGoods = objAwardList.get(objAwardList.size() - 1);
				awardMoney = prizeGoods.getGoodsValue();
			}

		} else {
			awardMoney = prizeGoods.getGoodsValue();
		}
		// 更新领用金额
		prizeRuleDao.updateBestowMoney(prizeRule.getId(), awardMoney);

		// 设定中奖信息
		result.setIs_success("T");
		result.setLevel_no(String.valueOf(prizeGoods.getLevel()));
		result.setName(prizeGoods.getName());
		result.setMoney(String.valueOf(awardMoney));

		List<Object> taskList = new ArrayList<Object>();
		// 如果返现方式为2:自动返现
		if (prizeRule.getBackType() == 2 && prizeGoods.getGoodsValue() > 0) {
			// 奖品类型为现金
			if (prizeGoods.getType() == 0) {
				accountDao.updateAccount(awardMoney, awardMoney, 0,
						user.getUserId());
				Account act = accountDao.getAcountByUser(user);
				// 资金日志
				AccountLog log = new AccountLog();
				fillAccountLog(log, Constant.PRIZE_GIVE_MONEY, act, user,
						new User(1), awardMoney, 0, "抽奖活动赠送现金" + awardMoney
								+ "元。");
				accountLogDao.save(log);
				// 第三方资金划款处理
				apiService.webTransfer(taskList, user, awardMoney);
				borrowService.doApiTask(taskList);
			} else if (prizeGoods.getType() == 1) {// 奖品类型为积分时
				int goodsValue = prizeGoods.getGoodsValue();
				UserCredit credit = userCreditDao.getUserCreditByUserId(user);
				credit.setValidValue(credit.getValidValue() + goodsValue);
				credit.setGiftValue(credit.getGiftValue() + goodsValue);
				credit.setValue(credit.getValue() + goodsValue);
				userCreditDao.save(credit);
				UserCreditLog userCreditLog = new UserCreditLog();
				String remark = "抽奖活动赠送积分" + goodsValue + "个";
				userCreditLog.setUser(user);
				UserCreditType rgType = userCreditTypeDao
						.getUserCreditType("prize");
				userCreditLog.setUserCreditType(rgType);
				userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog,
						goodsValue, credit.getValidValue(), credit, remark);
				userCreditLogDao.save(userCreditLog);
			}
		}

		// 往中奖信息表中插入相应记录（中奖）。
		addUserAward(user, prizeRule, prizeGoods);

		return result;
	}

	/**
	 * 往中奖信息表中插入相应记录（中奖，或者不中奖）
	 * 
	 * @param user
	 *            用户信息
	 * @param ruleAward
	 *            规则信息
	 * @param objAward
	 *            中奖信息
	 */
	private void addUserAward(User user, PrizeRule prizeRule,
			PrizeGoods prizeGoods) {

		PrizeUser prizeUser = new PrizeUser();
		// 抽奖用户ID
		prizeUser.setUser(user);
		// 规则ID
		prizeUser.setPrizeRule(prizeRule);
		if (prizeGoods != null) {
			// 奖品id
			prizeUser.setPrizeGoods(prizeGoods);
			// 抽奖消耗点数
			prizeUser.setPointReduce(prizeRule.getBasePoint());
			// 是否中奖:0不中，1中
			prizeUser.setStatus(1);
			if (prizeRule.getBackType() == 2) {
				prizeUser.setReceiveStatus(1);
			} else {
				prizeUser.setReceiveStatus(0);
			}
		} else {
			// 是否中奖:0不中，1中
			prizeUser.setStatus(0);
		}
		// 创建时间
		prizeUser.setAddtime(new Date());
		// 记录抽奖信息
		prizeUserDao.save(prizeUser);
	}

	/**
	 * 校验
	 * 
	 * @param ruleAward
	 *            规则信息
	 * @param userId
	 *            用户ID
	 * @return 校验结果
	 */
	private String check(PrizeRule prizeRule, User user) {
		String awardResultType = "";
		// 如果规则ID不存在
		if (prizeRule == null) {
			awardResultType = PrizeResultType.RESULT_INVALID_RULE_ID;
			return awardResultType;
		}

		// 如果当前时间 < 抽奖开始时间
		String currentTime = DateUtils.dateStr2(new Date());
		if (currentTime.compareTo(DateUtils.dateStr2(prizeRule.getStarttime())) < 0) {
			awardResultType = PrizeResultType.RESULT_BEFORE_START_TIME;
			return awardResultType;
		}

		// 如果当前时间 > 抽奖结束时间
		if (currentTime.compareTo(DateUtils.dateStr2(prizeRule.getEndtime())) > 0) {
			awardResultType = PrizeResultType.RESULT_AFTER_END_TIME;
			return awardResultType;
		}

		// 如果有抽奖次数限制时 0不限制,1限制用户总次数,2限制当天总次数
		if (prizeRule.getTimeLimit() != 0) {
			// 取得用户抽奖次数
			int userCnt = 0;
			if (prizeRule.getTimeLimit() == 1) {
				userCnt = prizeUserDao.getUserAwardTotalCnt(prizeRule.getId(),
						user.getUserId());
			} else if (prizeRule.getTimeLimit() == 2) {
				userCnt = prizeUserDao.getUserAwardDayCnt(prizeRule.getId(),
						user.getUserId());
			}
			// 如果用户的抽奖次数 大于或等于最大抽奖次数
			if (userCnt >= prizeRule.getMaxTimes()) {
				awardResultType = PrizeResultType.RESULT_TIME_LIMIT;
				return awardResultType;
			}
		}

		// 积分抽奖
		if (prizeRule.getPrizeType() == 1) {
			// 调用积分DAO，取得当前用户的可用积分
			UserCredit credit = userCreditDao.getUserCreditByUserId(user);
			long validPoint = credit.getValidValue();
			// 如果用户可用积分 < 抽奖所需积分
			if (validPoint < prizeRule.getBasePoint()) {
				awardResultType = PrizeResultType.RESULT_POINT_LIMIT;
				return awardResultType;
			} else {
				credit.setValidValue(credit.getValidValue()
						- prizeRule.getBasePoint());
				userCreditDao.save(credit);
				UserCreditLog userCreditLog = new UserCreditLog();
				String remark = "抽奖活动消耗积分" + prizeRule.getBasePoint() + "个";
				userCreditLog.setUser(user);
				userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog,
						prizeRule.getBasePoint(), credit.getValidValue(),
						credit, remark);
				userCreditLogDao.save(userCreditLog);
			}
		}
		return awardResultType;
	}

	/**
	 * 取得中奖奖品信息
	 * 
	 * @param PrizeGoodsList
	 *            奖品信息列表
	 * @return 中奖奖品信息
	 */
	private PrizeGoods getPrizeAward(List<PrizeGoods> goodsList) {
		// 取得随机数
		int start = new Random().nextInt(100000000);
		for (PrizeGoods goods : goodsList) {
			if (start < goods.getRate()) {
				return goods;
			}
			start -= goods.getRate();
		}
		return null;
	}

	@SuppressWarnings({ "unused" })
	@Override
	public Prize getPrizeByUseTypeAndInvestment(int useType,
			Double totalInvestment) {
		// 用户注册,实名认证,投资送奖品
		if (useType == 0) {
			return null;
		}
		if (useType != 20 && useType != 21 && useType != 22) {
			return null;
		}
		List<Prize> list = rewardUsersDao.findByUseType(useType);
		// 如果不是 投资送礼品 结果只有一个
		if ((null == totalInvestment || totalInvestment == 0) && useType != 22) {
			if (list == null || list.size() > 1) {
				throw new BussinessException("数据库数据错误,请联系客服人员处理!");
			}
			return list.get(0);
		}
		if (useType == 22 && totalInvestment < 0) {
			throw new BussinessException("非法请求!");
		}
		if (useType == 22 && totalInvestment >= 0) {
			for (Prize prize : list) {
				double useRule = prize.getUseRule();
				// 返回给投资人符合条件的礼品
				if (0D <= totalInvestment && totalInvestment < 3000D) {
					return null;
				}
				if (3000D <= totalInvestment && totalInvestment < 5000D) {
					// 发送level1奖品
					if (useRule == 3000.00) {
						return prize;
					}

				}
				if (5000D <= totalInvestment && totalInvestment < 8000D) {
					// 发送level2奖品
					if (useRule == 5000.00) {
						return prize;
					}

				}
				if (8000D <= totalInvestment && totalInvestment < 10000D) {
					// 发送level3奖品
					if (useRule == 8000.00) {
						return prize;
					}

				}
				if (10000D <= totalInvestment && totalInvestment < 20000D) {
					// 发送level4奖品
					if (useRule == 10000.00) {
						return prize;
					}

				}
				if (20000D <= totalInvestment && totalInvestment < 30000D) {
					// 发送level5奖品
					if (useRule == 20000.00) {
						return prize;
					}

				}
				if (30000D <= totalInvestment && totalInvestment < 50000D) {
					// 发送level6奖品
					if (useRule == 30000.00) {
						return prize;
					}

				}
				if (50000D <= totalInvestment && totalInvestment < 100000D) {
					// 发送level7奖品
					if (useRule == 50000.00) {
						return prize;
					}

				}
				if (100000D <= totalInvestment && totalInvestment < 150000D) {
					// 发送level8奖品
					if (useRule == 100000.00) {
						return prize;
					}

				}
				if (150000D <= totalInvestment && totalInvestment < 200000D) {
					// 发送level9奖品
					if (useRule == 150000.00) {
						return prize;
					}

				} else {
					// 发送level10奖品
					if (useRule == 200000.00) {
						return prize;
					}

				}

			}
		}

		return null;
	}

}
