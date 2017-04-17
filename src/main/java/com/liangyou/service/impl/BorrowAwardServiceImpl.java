package com.liangyou.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowAwardService;
import com.liangyou.util.NumberUtils;
/**
 * //TGPROJECT-411 wsl 2014-09-04 start
 * //TGPROJECT-411 wsl 2014-09-04 end
 * 投标奖励业务处理类
 * @author zxc
 *
 */
@Service(value="borrowAwardService")
@Transactional
public class BorrowAwardServiceImpl extends BaseServiceImpl implements BorrowAwardService {

	private static Logger logger = Logger.getLogger(BorrowAwardServiceImpl.class);

	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private ApiService apiService;

	@Override
	public void fullSuccessGiveAward(Borrow model, BorrowTender t,User borrowUser,
			User tenderUser, List<Object> taskList) {
		BorrowModel borrowModel = BorrowHelper.getHelper(model); //封装borrow
		double awardValue=borrowModel.getAwardValue(t.getAccount());//转账功能是实现 奖励的划分
		awardValue =  NumberUtils.format2(awardValue);//强制两位
		BorrowParam param = new BorrowParam();
		if(awardValue>0){
			switcherAward(model, t, borrowUser, tenderUser, taskList, awardValue,param);
		}
	}

	@Override
	public void flowBorrowGiveAward(Borrow model, BorrowTender t, User borrowUser, User tenderUser,
			List<Object> taskList, BorrowParam param) {
		BorrowModel borrowModel = BorrowHelper.getHelper(model); //封装borrow
		double awardValue=borrowModel.getAwardValue(t.getAccount());//转账功能是实现 奖励的划分
		awardValue =  NumberUtils.format2(awardValue);//强制两位
		if(awardValue>0){
			switcherAward(model, t, borrowUser, tenderUser, taskList, awardValue, param);
		}
	}

	/**判断发放奖励方式，
	 * true 前台借款人发放奖励，
	 * false 后台平台发放奖励
	 * @param model
	 * @param t
	 * @param borrowUser
	 * @param tenderUser
	 * @param taskList
	 * @param awardValue
	 * @param param
	 */
	public void switcherAward(Borrow model, BorrowTender t, User borrowUser, User tenderUser,
			List<Object> taskList, double awardValue, BorrowParam param){
		if(isBorrowUserPay(model)){
			//前台借款人发放奖励
			logger.info("借款人" + borrowUser.getUserId() + ",扣除奖励...");
			accountDao.updateAccount(-awardValue, -awardValue, 0,0, 0,borrowUser.getUserId());
			Account actAwareOut = accountDao.getAcountByUser(borrowUser);
			AccountLog logDeuct=new AccountLog(borrowUser.getUserId(),Constant.AWARD_DEDUCT,tenderUser.getUserId(),getLogRemark(model),param.getIp());
			fillAccountLog(logDeuct, Constant.AWARD_DEDUCT, actAwareOut, borrowUser, tenderUser,awardValue, 0, "投资成功，扣除奖励!" + awardValue + "元");
			accountLogDao.save(logDeuct);

			logger.info("出借人" + tenderUser.getUserId() + ",获得奖励...");
			String remark="收到标["+getLogRemark(model)+"]出借奖励!!";
			accountDao.updateAccount(awardValue, awardValue, 0,0, 0,tenderUser.getUserId());
			Account actAwareIn = accountDao.getAcountByUser(tenderUser);
			AccountLog logAward=new AccountLog(tenderUser.getUserId(),Constant.AWARD_ADD,borrowUser.getUserId(),getLogRemark(model),param.getIp());
			fillAccountLog(logAward, Constant.AWARD_ADD, actAwareIn, tenderUser,borrowUser, awardValue, 0, remark+ awardValue);
			accountLogDao.save(logAward);

			//调用易极付、汇付等接口,划分奖励
			apiService.FullSuccessAward(model, t, borrowUser, tenderUser, taskList, awardValue);
		}else{
			//后台平台发放奖励
			logger.info("出借人" + tenderUser.getUserId() + ",获得奖励...");
			String remark="收到标["+getLogRemark(model)+"]出借奖励!!";
			accountDao.updateAccount(awardValue, awardValue, 0,0, 0,tenderUser.getUserId());
			Account actAwareIn = accountDao.getAcountByUser(tenderUser);
			AccountLog logAward=new AccountLog(tenderUser.getUserId(),Constant.AWARD_ADD,borrowUser.getUserId(),getLogRemark(model),param.getIp());
			fillAccountLog(logAward, Constant.AWARD_ADD, actAwareIn, tenderUser,borrowUser, awardValue, 0, remark+ awardValue);
			accountLogDao.save(logAward);

			//调用易极付、汇付等接口,平台划分奖励
			apiService.FullSuccessAdminAward(model, t, borrowUser, tenderUser, taskList, awardValue);
		}
	}

	/**
	 * 判断是前台还是后台发放奖励
	 * @param model
	 * @return
	 */
	public boolean isBorrowUserPay(Borrow model){
		if(model.getAward()!=1 && model.getAward()!=2){
			return false;
		}
		return true;
	}

}
