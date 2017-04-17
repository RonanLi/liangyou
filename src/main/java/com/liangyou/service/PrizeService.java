package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.domain.PrizeGoods;
import com.liangyou.domain.PrizeRule;
import com.liangyou.domain.PrizeUser;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.prize.PrizeResult;

/**
 * 处理抽奖业务 v1.8.0.4_u4 TGPROJECT-367 qinjun 2014-07-16
 */
public interface PrizeService {

	/**
	 * 根据条件查询抽奖用户
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<PrizeUser> getAllPrizeUserList(SearchParam param);

	/**
	 * 获取所有的抽奖规则
	 * 
	 * @return
	 */
	public List<PrizeRule> getAllPrizeRule();

	/**
	 * 根据id获取抽奖规则对象
	 * 
	 * @param id
	 * @return
	 */
	public PrizeRule getPrizeRuleById(long id);

	/**
	 * 根据prizeType获取抽奖规则对象
	 * 
	 * @param ruleId
	 * @param user
	 * @param money
	 * @return
	 */
	public PrizeRule getPrizeRuleByPrizeType(int prizeType);

	/**
	 * 
	 * @param ruleId
	 * @param user
	 * @param money
	 * @return
	 */
	public PrizeResult extractionPrize(long ruleId, User user, double money);

	/**
	 * 更新抽奖规则
	 * 
	 * @param prizeRule
	 */
	public void updatePrizeRule(PrizeRule prizeRule);

	/**
	 * 添加抽奖规则
	 * 
	 * @param prizeRule
	 */
	public void addPrizeRule(PrizeRule prizeRule);

	/**
	 * 查询所有的奖品
	 * 
	 * @param ruleId
	 *            规则id
	 * @return
	 */
	public List<PrizeGoods> goodsList(long ruleId);

	/**
	 * 根据id获取抽奖产品对象
	 * 
	 * @param id
	 * @return
	 */
	public PrizeGoods getPrizeGoodsById(long id);

	/**
	 * 保存奖品规则
	 * 
	 * @param prizeGoods
	 */
	public void savePrizeGoods(PrizeGoods prizeGoods);

	/**
	 * 更新奖品规则
	 * 
	 * @param prizeGoods
	 */
	public void updatePrizeGoods(PrizeGoods prizeGoods);

	/**
	 * 用户注册,实名认证,绑定双乾账号,投标送礼品
	 * 
	 * @param useType
	 * @param totalInvestment
	 * @return
	 */
	public Prize getPrizeByUseTypeAndInvestment(int useType,
			Double totalInvestment);
}
