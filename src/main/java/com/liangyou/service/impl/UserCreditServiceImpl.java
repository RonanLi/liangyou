package com.liangyou.service.impl;
/**
 * v1.8.0.4 积分  新增页面
 */
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.CreditType;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.dao.UserCreditDao;
import com.liangyou.dao.UserCreditLogDao;
import com.liangyou.dao.UserCreditTypeDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.util.CreditUtils;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;
@Service(value="userCreditService")
@Transactional
public class UserCreditServiceImpl implements UserCreditService {
	@Autowired
	UserCreditTypeDao userCreditTypeDao;
	@Autowired
	UserCreditLogDao userCreditLogDao;
	@Autowired
	UserCreditDao userCreditDao;
	@Autowired
	InviteUserDao inviteUserDao;
	@Autowired
	UserDao userDao;
	@Autowired
	BorrowDao borrowDao;
	@Autowired
	BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	BorrowTenderDao borrowTenderDao;
	//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  start
	@Autowired
	RuleService ruleService;
	//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  end
	
	//v1.8.0.4  TGPROJECT-169 qj 2014-04-22 start
	/**
	 * 老平台无积分登陆时插入
	 */
	@Override
	public void loginCredit(User user){
		//实名未通过直接return
		if(user.getUserCredit() != null )return;
		//实名成功送积分类型
		UserCredit userCredit = new UserCredit(user, 0, 0, new Date(), user.getAddip());
		userCreditDao.save(userCredit);//更新积分
	}
	//v1.8.0.4  TGPROJECT-169 qj 2014-04-22 stop
	/**
	 * 登陆赠送积分
	 */
	@Override
	public UserCredit registerCredit(User user){
		//注册成功送积分类型
		UserCredit userCredit = new UserCredit(user, 0, 0, new Date(), user.getAddip());
		UserCreditType rgType = userCreditTypeDao.getUserCreditType("register");
		if(rgType !=null&&rgType.getStatus() == 1){//注册类型积分启用
			long register_value = rgType.getValue();
			long validValue = CreditUtils.getDecimalCredit(register_value*rgType.getValidRate(), rgType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + register_value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + register_value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, register_value,validValue, 
					CreditType.OP_TYPE_ADD, rgType, userCredit);
			String remark = "注册赠送积分"+register_value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, register_value, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);
		}
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 start
		inviteUserGiftCredit(user,"register_by_friends");
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 end
		return userCredit;
	}
	/**
	 *  处理借款积分(non-Javadoc)
	 * @see com.liangyou.service.UserCreditService#borrowSuccessCredit(com.liangyou.model.borrow.BorrowModel)
	 */
	@Override
	public void borrowSuccessCredit(BorrowModel borrow,User user) {
		Borrow model = borrow.getModel();
		UserCredit userCredit = user.getUserCredit();
		String typeId = model.getType() +"";
		UserCreditType rgType = userCreditTypeDao.getUserCreditType("borrow_success");
		if (borrow.getType() == Constant.TYPE_FLOW ) {
			return;
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
		//如果是按月发放
		if(rgType.getCreditManagerStatus() == 2){
			return;
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29 end
		if(rgType !=null&&rgType.getStatus() == 1&&StringUtils.isNull(rgType.getBorrowType()).contains(typeId)){//借款成功积分启用
			//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  start
			double account = borrowSuccessForCredit(model,model.getAccount());
			long value = CreditUtils.getDecimalCredit(account * rgType.getValueRate(), rgType);//借款成功积分
			//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  end
			long validValue = CreditUtils.getDecimalCredit(value*rgType.getValidRate(), rgType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, value,validValue, 
					CreditType.OP_TYPE_ADD, rgType, userCredit);
			String remark = "【"+model.getName()+"】借款成功，赠送积分"+value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, value, validValue,userCredit, remark);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
			userCreditLog.setBorrow(new Borrow(model.getId()));
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
			userCreditLogDao.save(userCreditLog);
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
		// 投资成功，推介好友，赠送积分
		UserCreditType friendType = userCreditTypeDao.getUserCreditType("borrow_success_by_friends");
		InviteUser inviteUser = inviteUserDao.getInviter(user.getUserId());
		if (inviteUser != null) {
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if (friendType != null && friendType.getStatus() == 1&& friendUserCredit != null) {// 推荐好友投资积分启用
				String borrowType = friendType.getBorrowType();
				if (!StringUtils.isBlank(borrowType)&& friendUserCredit != null) {
					if (DateUtils.rollYear(inviteUser.getUser().getAddtime(), 1).getTime() >= new Date().getTime()) {
						if (borrowType.contains(typeId)) {
							long gift_value = CreditUtils.getDecimalCredit( model.getAccount()* friendType.getValueRate(),friendType) ;//
							long validValue = CreditUtils.getDecimalCredit( gift_value * friendType.getValidRate(),friendType);// 有效积分
							friendUserCredit.setValue(friendUserCredit.getValue() + gift_value);// 累加总积分=
							// 可用积分+消费积分
							friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);// 可用积分
							friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + gift_value);
							UserCreditLog userCreditLog = new UserCreditLog(inviteUser.getInviteUser(), gift_value,
									                                        validValue, CreditType.OP_TYPE_ADD,
									                                        friendType, friendUserCredit);
							String remark = user.getUsername()+"借款成功，推介人"+inviteUser.getInviteUser().getUsername()+"获得推介出借积分" + gift_value + "个,"+ "有效积分" + validValue + "个";
							userCreditLog = CreditUtils.fillUserCreditLog(
									userCreditLog, gift_value, validValue,
									friendUserCredit, remark);
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
							userCreditLog.setBorrow(model);
							userCreditLog.setFromUser(user);
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
							userCreditLogDao.save(userCreditLog);
							userCreditDao.update(friendUserCredit);
						}
					}
				}
			}
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
	}
	/**
	 *  处理流转标借款积分(non-Javadoc)
	 * @see com.liangyou.service.UserCreditService#borrowSuccessCredit(com.liangyou.model.borrow.BorrowModel)
	 */
	@Override
	public void flowBorrowSuccessCredit(User user,BorrowTender tender) {
		Borrow borrow = tender.getBorrow();
		UserCredit userCredit = user.getUserCredit();
		String typeId = borrow.getType() +"";
		UserCreditType rgType = userCreditTypeDao.getUserCreditType("borrow_success");
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
		//如果是按月发放
		if (borrow.getType() != Constant.TYPE_FLOW ) {
			return;
		}
		if(rgType.getCreditManagerStatus() == 2){
			return;
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29 end
		if(rgType !=null&&rgType.getStatus() == 1&&StringUtils.isNull(rgType.getBorrowType()).contains(typeId)){//借款成功积分启用
			//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  start
			double account = borrowSuccessForCredit(borrow,tender.getAccount());
			long value = CreditUtils.getDecimalCredit(account * rgType.getValueRate(), rgType);//借款成功积分
			//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  start
			long validValue = CreditUtils.getDecimalCredit(value*rgType.getValidRate(), rgType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, value,validValue, 
					CreditType.OP_TYPE_ADD, rgType, userCredit);
			String remark = "【"+borrow.getName()+"】借款成功，赠送积分"+value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, value, validValue,userCredit, remark);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
			userCreditLog.setBorrow(new Borrow(borrow.getId()));
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
			userCreditLogDao.save(userCreditLog);
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
		// 投资成功，推介好友，赠送积分
		UserCreditType friendType = userCreditTypeDao.getUserCreditType("borrow_success_by_friends");
		InviteUser inviteUser = inviteUserDao.getInviter(user.getUserId());
		if (inviteUser != null) {
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if (friendType != null && friendType.getStatus() == 1&& friendUserCredit != null) {// 推荐好友投资积分启用
				String borrowType = friendType.getBorrowType();
				if (!StringUtils.isBlank(borrowType)&& friendUserCredit != null) {
					if (DateUtils.rollYear(inviteUser.getUser().getAddtime(), 1).getTime() >= new Date().getTime()) {
						if (borrowType.contains(typeId)) {
							long gift_value = CreditUtils.getDecimalCredit( tender.getAccount()* friendType.getValueRate(),friendType) ;//
							long validValue = CreditUtils.getDecimalCredit( gift_value * friendType.getValidRate(),friendType);// 有效积分
							friendUserCredit.setValue(friendUserCredit.getValue() + gift_value);// 累加总积分=
							// 可用积分+消费积分
							friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);// 可用积分
							friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + gift_value);
							UserCreditLog userCreditLog = new UserCreditLog(inviteUser.getInviteUser(), gift_value,
									validValue, CreditType.OP_TYPE_ADD,
									friendType, friendUserCredit);
							String remark = user.getUsername()+"借款成功，推介人"+inviteUser.getInviteUser().getUsername()+"获得推介出借积分" + gift_value + "个,"+ "有效积分" + validValue + "个";
							userCreditLog = CreditUtils.fillUserCreditLog(
									userCreditLog, gift_value, validValue,
									friendUserCredit, remark);
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
							userCreditLog.setBorrow(new Borrow(borrow.getId()));
							userCreditLog.setFromUser(user);
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
							userCreditLogDao.save(userCreditLog);
							userCreditDao.update(friendUserCredit);
						}
					}
				}
			}
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
	}
	/**
	 * 实名赠送积分
	 */
	@Override
	public void realNameCredit(User user){
		//实名未通过直接return
		if(user.getRealStatus() != 1 )return;
		//实名成功送积分类型
		UserCredit userCredit = user.getUserCredit();
		UserCreditType rnType = userCreditTypeDao.getUserCreditType("realname");
		if(rnType !=null&&rnType.getStatus() == 1){//实名类型积分启用
			
			long realname_value = rnType.getValue();//赠送实际积分
			long validValue = CreditUtils.getDecimalCredit(realname_value*rnType.getValidRate(), rnType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + realname_value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + realname_value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, realname_value,validValue, 
					CreditType.OP_TYPE_ADD, rnType, userCredit);
			String remark = "实名成功赠送积分"+realname_value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, realname_value, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);//添加日志记录
			userCreditDao.save(userCredit);//更新积分
		}
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 start
		inviteUserGiftCredit(user,"realname_by_friends");
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 end
	}
	
	/**
	 * 手机绑定赠送积分
	 */
	@Override
	public void phoneCredit(User user){
		//实名未通过直接return		//实名成功送积分类型
		UserCredit userCredit = user.getUserCredit();
		UserCreditType phoneType = userCreditTypeDao.getUserCreditType("phone");
		if(phoneType !=null&&phoneType.getStatus() == 1){//手机认证类型积分启用
			
			long phone_value = phoneType.getValue();//赠送实际积分
			long validValue = CreditUtils.getDecimalCredit(phone_value*phoneType.getValidRate(), phoneType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + phone_value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + phone_value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, phone_value,validValue, 
					CreditType.OP_TYPE_ADD, phoneType, userCredit);
			String remark = "手机绑定成功赠送积分"+phone_value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, phone_value, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);//添加日志记录
			userCreditDao.save(userCredit);//更新积分
		}
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 start
		inviteUserGiftCredit(user,"phone_by_friends");
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 end
	}
	
	/**
	 * 邮箱激活成功赠送积分
	 */
	@Override
	public void emailCredit(User user){
		//实名未通过直接return		//实名成功送积分类型
		UserCredit userCredit = user.getUserCredit();
		UserCreditType emailType = userCreditTypeDao.getUserCreditType("email");
		if(emailType !=null&&emailType.getStatus() == 1){//手机认证类型积分启用
			
			long email_value = emailType.getValue();//赠送实际积分
			long validValue = CreditUtils.getDecimalCredit(email_value*emailType.getValidRate(), emailType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + email_value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + email_value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, email_value,validValue, 
					CreditType.OP_TYPE_ADD, emailType, userCredit);
			String remark = "邮箱激活定成功赠送积分"+email_value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, email_value, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);//添加日志记录
			userCreditDao.save(userCredit);//更新积分
		}
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 start
		inviteUserGiftCredit(user,"email_by_friends");
		// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 end
	}
	
	/**
	 * 正常还款，获得积分
	 */
	@Override
	public void borrowRepayOnTimeCredit(BorrowRepayment repayment, User user){
		String typeId = repayment.getBorrow().getType() +"";
		UserCreditType borrowType = userCreditTypeDao.getUserCreditType("borrow_pay_ontime");
		if(borrowType !=null&&borrowType.getStatus() == 1&&StringUtils.isNull(borrowType.getBorrowType()).contains(typeId)){//正常还款积分启用，默认按时还款一次奖励50个积分
			long repayValue = CreditUtils.getDecimalCredit(
					repayment.getRepaymentAccount() * borrowType.getValueRate(), borrowType);//借款成功积分
			long validValue = CreditUtils.getDecimalCredit(repayValue*borrowType.getValidRate(), borrowType);//有效积分
//			long repayValue = borrowType.getValue();//给定的积分
//			long validValue = CreditUtils.getDecimalCredit(repayValue*borrowType.getValidRate(), borrowType);//有效积分
			UserCredit userCredit = user.getUserCredit();
			userCredit.setValue(userCredit.getValue() + repayValue);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setBorrowValue(userCredit.getBorrowValue() + repayValue);//借款积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, repayValue,validValue, 
					CreditType.OP_TYPE_ADD, borrowType, userCredit);
			String remark = "按时还款，获得积分"+repayValue +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, repayValue, validValue,userCredit, remark);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
			userCreditLog.setBorrow(repayment.getBorrow());
			userCreditLog.setBorrowRepayment(repayment);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
			userCreditLogDao.save(userCreditLog);
			userCreditDao.update(userCredit);
		}
	}
	
	/**
	 * 逾期还款，扣除积分
	 */
	@Override
	public void borrowRepayLateCredit(BorrowRepayment repayment, User user){
		UserCreditType lateType = userCreditTypeDao.getUserCreditType("borrow_pay_late");
		String typeId = repayment.getBorrow().getType()+"";
		if(lateType !=null&&lateType.getStatus() == 1&&StringUtils.isNull(lateType.getBorrowType()).contains(typeId)){//逾期还款积分启用，默认按时还款一次扣除500个积分
			long lateValue = CreditUtils.getDecimalCredit(
					repayment.getRepaymentAccount() * lateType.getValueRate(), lateType);//借款成功积分
			long validValue = CreditUtils.getDecimalCredit(lateValue*lateType.getValidRate(), lateType);//有效积分
//			long lateValue = lateType.getValue();//给定的积分
//			long validValue = CreditUtils.getDecimalCredit(lateValue*lateType.getValidRate(), lateType);//有效积分
			UserCredit userCredit = user.getUserCredit();
			userCredit.setValue(userCredit.getValue() - lateValue);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() - validValue);//可用积分
			userCredit.setBorrowValue(userCredit.getBorrowValue() - lateValue);//借款积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, lateValue,validValue, 
					CreditType.OP_TYPE_REDUCE, lateType, userCredit);
			String remark = "逾期还款，扣除积分"+lateValue +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, lateValue, validValue,userCredit, remark);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
			userCreditLog.setBorrow(repayment.getBorrow());
			userCreditLog.setBorrowRepayment(repayment);
			// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
			userCreditLogDao.save(userCreditLog);
			userCreditDao.update(userCredit);
		}
	}
	
	/**
	 * vip审核通过，获得积分
	 */
	@Override
	public void vipVerifyCredit(User user){
		UserCreditType vipType = userCreditTypeDao.getUserCreditType("vip_verify");
		if(vipType !=null&&vipType.getStatus() == 1){//逾期还款积分启用，默认按50个积分
			long vipValue = vipType.getValue();//给定的积分
			long validValue = CreditUtils.getDecimalCredit(vipValue*vipType.getValidRate(), vipType);//有效积分
			UserCredit userCredit = user.getUserCredit();
			userCredit.setValue(userCredit.getValue() + vipValue);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + vipValue);//借款积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, vipValue,validValue, 
					CreditType.OP_TYPE_ADD, vipType, userCredit);
			String remark = "申请 vip成功，获得积分"+vipValue +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, vipValue, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);
			userCreditDao.update(userCredit);
		}
	}
	
	/**
	 * 视频审核通过，获得积分
	 */
	@Override
	public void videoVerifyCredit(User user){
		UserCreditType videoType = userCreditTypeDao.getUserCreditType("video");
		if(videoType !=null&&videoType.getStatus() == 1){//逾期还款积分启用，默认按50个积分
			long vedioValue = videoType.getValue();//给定的积分
			long validValue = CreditUtils.getDecimalCredit(vedioValue*videoType.getValidRate(), videoType);//有效积分
			UserCredit userCredit = user.getUserCredit();
			userCredit.setValue(userCredit.getValue() + vedioValue);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + vedioValue);//借款积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, vedioValue,validValue, 
					CreditType.OP_TYPE_ADD, videoType, userCredit);
			String remark = "视频认证成功，获得积分"+vedioValue +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, vedioValue, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);
			userCreditDao.update(userCredit);
		}
	}
	
	/**
	 * 现场认证通过，获得积分
	 */
	@Override
	public void sceneVerifyCredit(User user){
		UserCreditType sceneType = userCreditTypeDao.getUserCreditType("scene");
		if(sceneType !=null&&sceneType.getStatus() == 1){//逾期还款积分启用，默认按50个积分
			long sceneValue = sceneType.getValue();//给定的积分
			long validValue = CreditUtils.getDecimalCredit(sceneValue*sceneType.getValidRate(), sceneType);//有效积分
			UserCredit userCredit = user.getUserCredit();
			userCredit.setValue(userCredit.getValue() + sceneValue);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + sceneValue);//借款积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, sceneValue,validValue, 
					CreditType.OP_TYPE_ADD, sceneType, userCredit);
			String remark = "现场认证成功，获得积分"+sceneValue +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, sceneValue, validValue,userCredit, remark);
			userCreditLogDao.save(userCreditLog);
			userCreditDao.update(userCredit);
		}
	}
	
	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 start
	@Override
	public void borrowTenderCredit(BorrowTender tender) {
		// 投资成功送积分类型
		UserCredit userCredit = userCreditDao.getUserCreditByUserId(tender.getUser());
		UserCreditType tenderType = userCreditTypeDao.getUserCreditType("invest_success");
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29 start 
		//如果是按月发放
		if(tenderType.getCreditManagerStatus() == 2){
			return;
		}
		//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29 end
		if (tenderType != null && tenderType.getStatus() == 1 && userCredit!=null) {// 投资成功积分启用
			String borrowType=tenderType.getBorrowType();
			if(!StringUtils.isBlank(borrowType)){
				if(borrowType.contains(tender.getBorrow().getType()+"")){
					long tender_value = CreditUtils.getDecimalCredit(tender.getAccount() * tenderType.getValueRate(), tenderType);// 投标给的积分
					long validValue = CreditUtils.getDecimalCredit(tender_value* tenderType.getValidRate(), tenderType);// 有效积分
					userCredit.setValue(userCredit.getValue() + tender_value);// 累加总积分=
					// 可用积分+消费积分
					userCredit.setValidValue(userCredit.getValidValue() + validValue);// 可用积分
					userCredit.setTenderValue(userCredit.getTenderValue() + tender_value);
					UserCreditLog userCreditLog = new UserCreditLog(tender.getUser(), tender_value, validValue, CreditType.OP_TYPE_ADD, tenderType,userCredit);
					String remark = tender.getUser().getUsername()+"出借成功获得积分" + tender_value + "个," + "有效积分" + validValue + "个";
					userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, tender_value, validValue, userCredit, remark);
					// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
					userCreditLog.setBorrow(tender.getBorrow());
					// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
					userCreditLogDao.save(userCreditLog);
					userCreditDao.update(userCredit);
				}
			}
		}
		// 投资成功，推介好友，赠送积分
		UserCreditType friendType = userCreditTypeDao.getUserCreditType("invest_success_by_friends");
		InviteUser inviteUser = inviteUserDao.getInviter(tender.getUser().getUserId());
		if (inviteUser != null) {
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if (friendType != null && friendType.getStatus() == 1&& friendUserCredit != null) {// 推荐好友投资积分启用
				String borrowType = friendType.getBorrowType();
				if (!StringUtils.isBlank(borrowType)&& friendUserCredit != null) {
					if (DateUtils.rollYear(inviteUser.getUser().getAddtime(), 1).getTime() >= new Date().getTime()) {
						if (borrowType.contains(tender.getBorrow().getType()+ "")) {
							long gift_value = CreditUtils.getDecimalCredit( tender.getAccount()* friendType.getValueRate(),friendType);//
							long validValue = CreditUtils.getDecimalCredit( gift_value * friendType.getValidRate(),friendType);// 有效积分
							friendUserCredit.setValue(friendUserCredit.getValue() + gift_value);// 累加总积分=
							// 可用积分+消费积分
							friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);// 可用积分
							friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + gift_value);
							UserCreditLog userCreditLog = new UserCreditLog(inviteUser.getInviteUser(), gift_value,
									                                        validValue, CreditType.OP_TYPE_ADD,
									                                        friendType, friendUserCredit);
							String remark = inviteUser.getUser().getUsername()+"出借成功，推介人"+inviteUser.getInviteUser().getUsername()+"获得推介出借积分" + gift_value + "个,"+ "有效积分" + validValue + "个";
							userCreditLog = CreditUtils.fillUserCreditLog(
									userCreditLog, gift_value, validValue,
									friendUserCredit, remark);
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
							userCreditLog.setBorrow(tender.getBorrow());
							userCreditLog.setFromUser(tender.getUser());
							userCreditLog.setTenderMoney(tender.getAccount());
							// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
							userCreditLogDao.save(userCreditLog);
							userCreditDao.update(friendUserCredit);
						}
					}
				}
			}
		}
	}
	

	// v1.8.0.4 TGPROJECT-98 lx 2014-04-21 end
	

	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 start
	//根据ID得到对应的对象
	@Override
	public UserCredit find(long id) {
		
		return userCreditDao.find(id);
	}
	//后台管理员修改用户积分
	@Override
	public void updateUserCredit(UserCredit userCredit,String remark,long operation) {
		UserCreditType rgType = userCreditTypeDao.getUserCreditType("manager");
		long value = userCredit.getValue();
		UserCreditLog userCreditLog = null;
		userCredit.setValidValue(userCredit.getValidValue()+operation);
		userCredit.setUpdatetime(new Date());
		if(operation>0){
			userCredit.setValue(value+operation);
			userCreditLog = new UserCreditLog(userCredit.getUser(), value,operation, 
					CreditType.OP_TYPE_ADD, rgType, userCredit);
		}else{
			userCredit.setExpenseValue(userCredit.getExpenseValue()-operation);
			userCreditLog = new UserCreditLog(userCredit.getUser(), value,operation, 
					CreditType.OP_TYPE_REDUCE, rgType, userCredit);
		}
		userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, operation, userCredit.getValidValue(),userCredit, remark);
		userCreditLogDao.save(userCreditLog);
		userCreditDao.merge(userCredit);
	}
	
    //分页查询用户积分
	@Override
	public PageDataList<UserCredit> findPageList(SearchParam param) {
		return userCreditDao.findPageList(param);
	}
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end

	
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
	@Override
	public void quartzBorrowTenderCredit(BorrowTender tender,BorrowRepayment br) {
		//投资成功送积分类型
		UserCredit userCredit = userCreditDao.getUserCreditByUserId(tender.getUser());
		UserCreditType tenderType = userCreditTypeDao.getUserCreditType("invest_success");
		//如果是全部派送
		if(tenderType.getCreditManagerStatus() == 1){
			return;
		}
		Borrow borrow = tender.getBorrow();
		if (tenderType != null && tenderType.getStatus() == 1 && userCredit!=null) {// 投资成功积分启用
			String borrowType=tenderType.getBorrowType();
			if(!StringUtils.isBlank(borrowType)){
				if(borrowType.contains(tender.getBorrow().getType()+"")){
					long tender_value = CreditUtils.getDecimalCredit(tender.getAccount() * tenderType.getValueRate()/ borrow.getTimeLimit(), tenderType) ;// 投标给的积分
					long validValue = CreditUtils.getDecimalCredit(tender_value* tenderType.getValidRate(), tenderType);// 有效积分
					userCredit.setValue(userCredit.getValue() + tender_value);// 累加总积分=
					// 可用积分+消费积分
					userCredit.setValidValue(userCredit.getValidValue() + validValue);// 可用积分
					userCredit.setTenderValue(userCredit.getTenderValue() + tender_value);
					UserCreditLog userCreditLog = new UserCreditLog(tender.getUser(), tender_value, validValue, CreditType.OP_TYPE_ADD, tenderType,userCredit);
					String remark = tender.getUser().getUsername()+"出借【"+borrow.getName()+"】到达还款日期，获得积分" + tender_value + "个," + "有效积分" + validValue + "个";
					userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, tender_value, validValue, userCredit, remark);
					userCreditLog.setBorrow(borrow);
					userCreditLog.setBorrowRepayment(br);
					userCreditLogDao.save(userCreditLog);
					userCreditDao.update(userCredit);
				}
			}
		}
		// 投资成功，推介好友，赠送积分
		UserCreditType friendType = userCreditTypeDao.getUserCreditType("invest_success_by_friends");
		InviteUser inviteUser = inviteUserDao.getInviter(tender.getUser().getUserId());
		if (inviteUser != null) {
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if (friendType != null && friendType.getStatus() == 1&& friendUserCredit != null) {// 推荐好友投资积分启用
				String borrowType = friendType.getBorrowType();
				if (!StringUtils.isBlank(borrowType)&& friendUserCredit != null) {
					if (DateUtils.rollYear(inviteUser.getUser().getAddtime(), 1).getTime() >= new Date().getTime()) {
						if (borrowType.contains(tender.getBorrow().getType()+ "")) {
							long gift_value = CreditUtils.getDecimalCredit( tender.getAccount()* friendType.getValueRate()/ borrow.getTimeLimit(),friendType) ;//
							long validValue = CreditUtils.getDecimalCredit( gift_value * friendType.getValidRate(),friendType);// 有效积分
							friendUserCredit.setValue(friendUserCredit.getValue() + gift_value);// 累加总积分=
							// 可用积分+消费积分
							friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);// 可用积分
							friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + gift_value);
							UserCreditLog userCreditLog = new UserCreditLog(inviteUser.getInviteUser(), gift_value,
									                                        validValue, CreditType.OP_TYPE_ADD,
									                                        friendType, friendUserCredit);
							String remark = tender.getUser().getUsername()+"出借【"+borrow.getName()+"】到达还款日期，推介人"+inviteUser.getInviteUser().getUsername()+"获得推介出借积分" + gift_value + "个,"+ "有效积分" + validValue + "个";
							userCreditLog = CreditUtils.fillUserCreditLog(
									userCreditLog, gift_value, validValue,
									friendUserCredit, remark);
							userCreditLog.setBorrow(borrow);
							userCreditLog.setBorrowRepayment(br);
							userCreditLog.setFromUser(tender.getUser());
							userCreditLogDao.save(userCreditLog);
							userCreditDao.update(friendUserCredit);
						}
					}
				}
			}
		}
		
	}
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
	//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  start 
	@Override
	public void quartzBorrowRepayCredit(BorrowRepayment br) {
		Borrow borrow = br.getBorrow();
		User user = br.getBorrow().getUser();
		UserCredit userCredit = br.getBorrow().getUser().getUserCredit();
		UserCreditType rgType = userCreditTypeDao.getUserCreditType("borrow_success");
		String typeId = br.getBorrow().getType() +"";
		//如果是全部派送
		if(rgType.getCreditManagerStatus() == 1){
			return;
		}
		if(rgType !=null&&rgType.getStatus() == 1&&StringUtils.isNull(rgType.getBorrowType()).contains(typeId)){//借款成功积分启用
			long value = CreditUtils.getDecimalCredit(
					borrow.getAccount() * rgType.getValueRate()/borrow.getTimeLimit(), rgType);//借款成功积分
			long validValue = CreditUtils.getDecimalCredit(value*rgType.getValidRate(), rgType);//有效积分
			
			userCredit.setValue(userCredit.getValue() + value);//累加总积分= 可用积分+消费积分
			userCredit.setValidValue(userCredit.getValidValue() + validValue);//可用积分
			userCredit.setGiftValue(userCredit.getGiftValue() + value);//赠送积分
			UserCreditLog  userCreditLog = new UserCreditLog(user, value,validValue, 
					CreditType.OP_TYPE_ADD, rgType, userCredit);
			String remark = "借款【"+borrow.getName()+"】到达还款日期，赠送积分"+value +"个," + "有效积分"+validValue+"个";
			userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, value, validValue,userCredit, remark);
			userCreditLog.setBorrow(borrow);
			userCreditLog.setBorrowRepayment(br);
			userCreditLogDao.save(userCreditLog);
		}
		// 投资成功，推介好友，赠送积分
		UserCreditType friendType = userCreditTypeDao.getUserCreditType("borrow_success_by_friends");
		InviteUser inviteUser = inviteUserDao.getInviter(user.getUserId());
		if (inviteUser != null) {
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if (friendType != null && friendType.getStatus() == 1&& friendUserCredit != null) {// 推荐好友投资积分启用
				String borrowType = friendType.getBorrowType();
				if (!StringUtils.isBlank(borrowType)&& friendUserCredit != null) {
					if (DateUtils.rollYear(inviteUser.getUser().getAddtime(), 1).getTime() >= new Date().getTime()) {
						if (borrowType.contains(borrow.getType()+ "")) {
							long gift_value = CreditUtils.getDecimalCredit( borrow.getAccount()* friendType.getValueRate()/borrow.getTimeLimit(),friendType) ;//
							long validValue = CreditUtils.getDecimalCredit( gift_value * friendType.getValidRate(),friendType);// 有效积分
							friendUserCredit.setValue(friendUserCredit.getValue() + gift_value);// 累加总积分=
							// 可用积分+消费积分
							friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);// 可用积分
							friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + gift_value);
							UserCreditLog userCreditLog = new UserCreditLog(inviteUser.getInviteUser(), gift_value,
									                                        validValue, CreditType.OP_TYPE_ADD,
									                                        friendType, friendUserCredit);
							String remark = user.getUsername()+"借款到达还款日期，推介人"+inviteUser.getInviteUser().getUsername()+"获得推介出借积分" + gift_value + "个,"+ "有效积分" + validValue + "个";
							userCreditLog = CreditUtils.fillUserCreditLog(
									userCreditLog, gift_value, validValue,
									friendUserCredit, remark);
							userCreditLog.setBorrow(borrow);
							userCreditLog.setBorrowRepayment(br);
							userCreditLog.setFromUser(user);
							userCreditLogDao.save(userCreditLog);
							userCreditDao.update(friendUserCredit);
						}
					}
				}
			}
		}
		br.setCreditStatus(1);
		borrowRepaymentDao.save(br);
	}
	//v1.8.0.4_u1    TGPROJECT-239   qj   2014-04-29  end 

	// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 start
	private void inviteUserGiftCredit(User user,String creditType){
		//注册如果有邀请，推介好友，赠送积分
		InviteUser inviteUser =  inviteUserDao.getInviter(user.getUserId());
		if(inviteUser!=null && inviteUser.getInviteUser()!=null){//推荐人存在
			User friendUser = inviteUser.getInviteUser();
			UserCreditType friendType = userCreditTypeDao.getUserCreditType(creditType);
			UserCredit friendUserCredit = userCreditDao.getUserCreditByUserId(inviteUser.getInviteUser());
			if(friendType !=null&&friendType.getStatus() == 1){//推介类型积分启用
				if(friendUserCredit!=null){
					long value = friendType.getValue();//给定的积分
					long validValue = CreditUtils.getDecimalCredit(value*friendType.getValidRate(), friendType);//有效积分
					friendUserCredit.setValue(friendUserCredit.getValue() + value);//累加总积分= 可用积分+消费积分
					friendUserCredit.setValidValue(friendUserCredit.getValidValue() + validValue);//可用积分
					friendUserCredit.setGiftValue(friendUserCredit.getGiftValue() + value);//赠送积分
					UserCreditLog  userCreditLog = new UserCreditLog(friendUser, value,validValue, 
							CreditType.OP_TYPE_ADD, friendType, friendUserCredit);
					String remark = "赠送积分"+value +"个," + "有效积分"+validValue+"个";
					userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, value, validValue,friendUserCredit, remark);
					userCreditLog.setFromUser(user);
					userCreditLogDao.save(userCreditLog);
					userCreditDao.update(friendUserCredit);
				}else{
					UserCredit friendCredit =new UserCredit(friendUser, 0, 0, new Date(), friendUser.getAddip());
					long value = friendType.getValue();//给定的积分
					long validValue = CreditUtils.getDecimalCredit(value*friendType.getValidRate(), friendType);//有效积分
					
					friendCredit.setValue(friendCredit.getValue() + value);//累加总积分= 可用积分+消费积分
					friendCredit.setValidValue(friendCredit.getValidValue() + validValue);//可用积分
					friendCredit.setGiftValue(friendCredit.getGiftValue() + value);//赠送积分
					UserCreditLog  userCreditLog = new UserCreditLog(friendUser, value,validValue, 
							CreditType.OP_TYPE_ADD, friendType, friendCredit);
					String remark = "赠送积分"+value +"个," + "有效积分"+validValue+"个";
					userCreditLog = CreditUtils.fillUserCreditLog(userCreditLog, value, validValue,friendCredit, remark);
					userCreditLog.setFromUser(user);
					userCreditLogDao.save(userCreditLog);
					userCreditDao.save(friendCredit);
				}
				
			}
		}
	}
	// v1.8.0.4 TGPROJECT-126 lx 2014-04-21 end
	
	//v1.8.0.4_u1 TGPROJECT-127 lx  start
	public PageDataList<UserCreditLog> findUserCreditLogList(SearchParam param){
		PageDataList<UserCreditLog>  userCreditLog =userCreditLogDao.findPageList(param);
		return userCreditLog;
	}
	//v1.8.0.4_u1 TGPROJECT-127 lx  end
	@Override
	public void madeRepay() {
		List<BorrowRepayment> repayList =  borrowRepaymentDao.getNotRepayForCreditStatus();
		for (BorrowRepayment br : repayList) {
			List<BorrowTender> tenderList = borrowTenderDao.getBorrowTenderList(br.getBorrow().getId());
			for (BorrowTender borrowTender : tenderList) {
				quartzBorrowTenderCredit(borrowTender,br);
			}
			quartzBorrowRepayCredit(br);
		}

	}
	
	//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  start
	private double borrowSuccessForCredit(Borrow borrow,double account){
		Rule rule = ruleService.getRuleByNid("borrow_success_credit");
		if(rule!= null && rule.getStatus() == 1){
			int timeLimitStatus = rule.getValueIntByKey("is_timeLimit");
			if(timeLimitStatus == 1){
				account = account * borrow.getTimeLimit();
			}
		}
		return account;
	}
	//v1.8.0.4_u4  TGPROJECT-373  qinjun  2014-07-21  end
}
