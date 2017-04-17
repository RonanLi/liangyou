package com.liangyou.service;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowModel;

/**
 * 用户积分service
 * @author zxc
 *
 */
public interface UserCreditService {
	//v1.8.0.4  TGPROJECT-169 qj 2014-04-22 start
	/**
	 * 老平台无积分登陆时插入
	 * @param user
	 */
	public void loginCredit(User user);
	//v1.8.0.4  TGPROJECT-169 qj 2014-04-22 stop
	/**
	 * 注册赠送积分
	 */
	public UserCredit registerCredit(User user);
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
	/**
	 * 调度处理借款人还款投资人送积分
	 * @param borrow
	 */
	public void borrowTenderCredit(BorrowTender tender);
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
	
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 start
	/**
	 * 调度处理借款人还款借款人送积分
	 * @param borrow
	 */
	public void quartzBorrowRepayCredit(BorrowRepayment br) ;
	// v1.8.0.4_u1 TGPROJECT-239 qj 2014-04-29 end
	
	public void quartzBorrowTenderCredit(BorrowTender tender,BorrowRepayment br);
	
	/**
	 * 正常还款，获得积分
	 */
	public void borrowRepayOnTimeCredit(BorrowRepayment repayment, User user);
	/**
	 * 逾期还款，扣除积分
	 */
	public void borrowRepayLateCredit(BorrowRepayment repayment, User user);
	/**
	 * vip审核通过，获得积分
	 */
	public void vipVerifyCredit(User user);
	 /** 实名通过赠送积分
	 * @param user
	 * @return
	 */
	public void realNameCredit(User user);
	/**
	 * 视频审核通过，获得积分s
	 */
	public void videoVerifyCredit(User user);
	/**
	 * 现场认证通过，获得积分
	 */
	public void sceneVerifyCredit(User user);
	public void borrowSuccessCredit(BorrowModel borrow,User user);
	
	/**
	 *  处理流转标借款积分(non-Javadoc)
	 * @see com.liangyou.service.UserCreditService#borrowSuccessCredit(com.liangyou.model.borrow.BorrowModel)
	 */
	public void flowBorrowSuccessCredit(User user,BorrowTender tender);
	
	/**
	 * 手机绑定赠送积分
	 */
	public void phoneCredit(User user);
	/**
	 * 邮箱激活成功赠送积分
	 */
	public void emailCredit(User user);
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end
	public UserCredit find(long id);
	public void updateUserCredit(UserCredit userCredit,String remark,long operation);
	public PageDataList<UserCredit> findPageList(SearchParam param) ;
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end

	
	//v1.8.0.4_u1 TGPROJECT-127 lx  start
	public PageDataList<UserCreditLog> findUserCreditLogList(SearchParam param);
	//v1.8.0.4_u1 TGPROJECT-127 lx  end
	
	
	public void madeRepay();
}
