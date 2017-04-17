package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.CreditDao;
import com.liangyou.dao.CreditLogDao;
import com.liangyou.dao.UserAmountApplyDao;
import com.liangyou.dao.UserAmountDao;
import com.liangyou.dao.UserAmountLogDao;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditLog;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountApply;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserAmountService;
import com.liangyou.util.NumberUtils;
 
/**
 * 
 * @author fuxingxing
 * @date 2013-5-13-下午1:59:06
 * @version
 * 
 *<b>Copyright (c)</b> 2013-51融都-版权所有<br/>
 * 
 */
@Service
@Transactional
public class UserAmountServiceImpl extends BaseServiceImpl implements UserAmountService {
	
	private static Logger logger = Logger.getLogger(UserAmountServiceImpl.class);

	@Autowired
	private UserAmountDao userAmountDao;
	@Autowired
	private UserAmountApplyDao userAmountApplyDao;
	@Autowired
	private UserAmountLogDao userAmountLogDao;
	@Autowired
	private CreditDao creditDao;
	@Autowired
	private CreditLogDao creditLogDao;

	@Override
	public UserAmount getUserAmount(long user_id) {
		
		return userAmountDao.findByProperty("user", new User(user_id)).get(0);
	}

	@Override
	public PageDataList<UserAmountApply> getUserAmountApplyList(SearchParam param) {
		return userAmountApplyDao.findPageList(param);
	}
	@Override
	/**
	 * 查询申请的集合
	 * @param userId
	 * @return
	 */
	public List<UserAmountApply> getUserAmountApplyListById(Long userId){
		return userAmountApplyDao.getUserAmountApplyList(userId);
		
	}

	@Override
	public void add(UserAmountApply userAmountApply) {
		 userAmountApplyDao.save(userAmountApply);
	}
	
	public UserAmountApply getAmountApplyById(int id) {
		return userAmountApplyDao.find(id);
	}

	@Override
	public void verifyAmountApply(UserAmountApply apply, UserAmountLog log,int giveCredit) {
		UserAmount amount=userAmountDao.getUserAmountByUser(apply.getUser());
		if(apply.getStatus()==1){
			userAmountDao.updateCreditAmount(apply.getAccountNew()-apply.getAccountOld(), apply.getAccountNew()-apply.getAccountOld(), 0, apply.getUser().getUserId());
			log.setAccount(apply.getAccount());
			log.setAccountTotal(amount.getCredit()+(apply.getAccountNew()-apply.getAccountOld()));
			log.setAccountUse(amount.getCreditUse()+(apply.getAccountNew()-apply.getAccountOld()));
			log.setAccountNouse(amount.getCreditNouse());
			log.setAccountType("credit");
			log.setType("apply_add");
			log.setRemark("申请额度审核通过");
			//好丽贷新业务：审核信用额度通过，添加管理员手动输入的奖励积分
			Credit credit = creditDao.getCreditByUser(apply.getUser());
			credit.setValue(giveCredit);//重置信用积分
			creditDao.update(credit);
			//添加信用积分日志
			CreditLog creditLog = new CreditLog();
			creditLog.setUserId(log.getUser().getUserId());
			creditLog.setOp(NumberUtils.getInt(apply.getVerifyUser().getUserId()+""));
			creditLog.setAddtime(new Date());
			creditLog.setAddip(apply.getAddip());
			creditLog.setTypeId(8);
			creditLog.setRemark(apply.getRemark());
			creditLog.setValue(giveCredit);
			creditLogDao.save(creditLog);
			
		}else{
			log.setAccount(apply.getAccount());
			log.setAccountTotal(amount.getCredit());
			log.setAccountUse(amount.getCreditUse());
			log.setAccountNouse(amount.getCreditNouse());
			log.setAccountType("credit");
			log.setType("apply_add");
			log.setRemark("申请额度审核不通过");
		}
		userAmountApplyDao.update(apply);
		userAmountLogDao.save(log);
	}

	@Override
	public void updateAmountApply(UserAmountApply apply) {
		userAmountApplyDao.update(apply);
		
	}
	
	
	
}
