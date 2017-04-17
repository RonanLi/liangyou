package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.InviteUserSummary;

public interface InviteUserDao extends BaseDao<InviteUser> {
	
	/**
	 * 查询我所推荐的人的列表。
	 * @param userid
	 * @return
	 */
	public List<InviteUser> getInvitreUser(long inviteUserid);
	
	/**
	 * 查询我的推荐人
	 * @param userid
	 * @return
	 */
	public InviteUser getInviter(long userid);
	
	//v1.8.0.4 TGPROJECT-249 lx start
	public List<InviteUserSummary> getInvitreUserBySearchParam(SearchParam  param,SearchParam  tenderParam);
	//v1.8.0.4 TGPROJECT-249 lx end
	//v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-22 start
	public double getInvitreUser(User user,Borrow b);
	//v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-22 end
	
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  start
	/**
	 * 获取推荐人推荐用户（用户必须投标过并且投标金额不为0）个数
	 * @param user
	 * @return
	 */
	public int getInviteUserTotal(User user);
	//v1.8.0.4_u4  TGPROJECT-356  qinjun 2014-07-04  end	
}
