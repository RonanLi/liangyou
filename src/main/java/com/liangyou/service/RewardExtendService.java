package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.RewardExtend;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz 
 * 奖励业务service
 * @author wujing
 *
 */
public interface RewardExtendService {

	/**
	 * 添加奖励记录
	 * @param rewardExtend
	 */
	public void addReward(RewardExtend rewardExtend);

	/**
	 * 查询奖励
	 * @param id
	 * @return
	 */
	public RewardExtend getRewardByid(long id);


	/**
	 * 处理投标奖励：投标成功后，推荐人得1%投资金奖励(在系统满标复审的时候，一次性发放奖励)
	 * @param borrowTender
	 */
	public void doTenderRewardForInvite(List<BorrowTender> borrowTenders);

	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	public void doRewardAsReadyMoney();
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 处理注册并且实名认证通过之后，投资金额达到1000以后直接发放奖励：
	 * 备注：投资人和推荐人都发放50块钱奖励
	 */
	public void doRegisterAndIdentReward();

	/**
	 * 获取带分页的记录
	 * @param param
	 * @return
	 */
	public PageDataList<RewardExtend> getPageLIstReward(SearchParam param);

	/**
	 * 注册实名认证后送红包
	 * @param user
	 */
	public void doRegisterIdentRedPacket(User user);

	/**
	 * 处理红包变现金功能
	 * @param rewardList
	 */
	public void doRedExtend(String[] ids);

	/**
	 * 根据红包id，计算所选择的红包总额
	 * @param ids
	 * @return
	 */
	public double getSumRewardById(String[] ids);

	/**
	 * 用户充值送红包
	 * @param user
	 * @param money 充值金额
	 */
	public void rechargeExtendRedPacket(User user,double money);
	/**
	 * 用户投标送红包
	 * @param user
	 */
	public void tenderExtendRedPacket(User user,double money) ;
	//wsl 首次投资奖励功能【德益网】 2014-09-02 start
	/**
	 * 首次投资奖励功能【德益网】
	 * @param tenderUser
	 * @param tenderMoney
	 */
	public void tenderFirstReward(User tenderUser,double tenderMoney);
	//wsl 首次投资奖励功能【德益网】 2014-09-02 end
	//TGPROJECT-409 注册送奖金  start  大互联新功能
	/**
	 * 实名成功送奖励，大互联在使用
	 * @param user
	 */
	public void doRegisterApiAwardsMoney(User user);
	//TGPROJECT-409 注册送奖金  end  大互联新功能
}
