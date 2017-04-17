package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.TenderRewardDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.TenderReward;
import com.liangyou.domain.User;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.WebPayModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.TenderRewardService;

/**
 * //1.8.0.4_u3   TGPROJECT-337  qinjun 2014-06-23  start
 * @author wujing
 *
 */
@Service(value="tenderRewardService")
@Transactional
public class TenderRewardServiceImpl extends BaseServiceImpl implements TenderRewardService {
	private Logger logger = Logger.getLogger(TenderRewardServiceImpl.class);
	@Autowired
	private TenderRewardDao tenderRewardDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRechargeDao accountRechargeDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private BorrowService borrowService;
	@Override
	public List<TenderReward> getTenderRewardByBorrowId(SearchParam param) {
		List<TenderReward> tenderRewardList = tenderRewardDao.findByCriteria(param);
		return tenderRewardList;
	}
	
	@Override
	public void doExtendReward(TenderReward reward) {
		if (reward.getStatus() ==1) {  //审核通过，奖励发放，跟新推荐人资金信息
			//奖励发放，更新推荐人账户信息
			List<Object>  taskList = new ArrayList<Object>();
			//创建交易对象
			 WebPayModel webPay  = new WebPayModel(reward.getRewardUser(), reward.getExtendMoney());
			 apiService.doWebPayMoney(webPay, taskList);
			if (borrowService.doApiTask(taskList)) {
				Account account = accountDao.getAcountByUser(new User(reward.getRewardUser().getUserId()));
				accountDao.updateAccount(reward.getExtendMoney(), reward.getExtendMoney(), 0, reward.getRewardUser().getUserId());
				//添加资金记录
				AccountLog log = new AccountLog(reward.getRewardUser().getUserId(),Constant.TENDER_REWARD,Constant.ADMIN_ID,reward.getVerifyContent(), reward.getVerifyIp());
				log.setMoney(reward.getExtendMoney());
				log.setTotal(account.getTotal());
				log.setUseMoney(account.getUseMoney());
				log.setNoUseMoney(account.getNoUseMoney());
				log.setCollection(account.getCollection());
				log.setRepay(account.getRepay());
				log.setRemark("投标，推荐人奖励发放："+reward.getExtendMoney()+"元。");
				reward.setVerifyTime(new Date());      //审核时间
				tenderRewardDao.update(reward);
				accountLogDao.save(log);
			}else{
				throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
			}
		}
	}

	@Override
	public PageDataList<TenderReward> getPageListTenderReward(SearchParam param) {
		PageDataList<TenderReward> pageTenderRewardList = tenderRewardDao.findAllPageList(param);
		return pageTenderRewardList;
	}

	@Override
	public TenderReward getTenderRewardById(long id) {
		TenderReward tenderReward = tenderRewardDao.find(id);
		return tenderReward;
	}
	
	

}
