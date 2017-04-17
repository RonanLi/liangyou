package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.borrow.BorrowModel;

/**
 * //TGPROJECT-411 wsl 2014-09-04 start
 * //TGPROJECT-411 wsl 2014-09-04 end
 * 投标奖励业务处理类
 * @author zxc
 *
 */
public interface BorrowAwardService {

	/**
	 * 满标复审借款人发放奖励
	 * @param model
	 * @param t
	 * @param borrowUser
	 * @param tenderUser
	 * @param taskList
	 */
	public void fullSuccessGiveAward(Borrow model, BorrowTender t, User borrowUser,
			User tenderUser, List<Object> taskList);
	
	/**
	 * 流转标投标发放奖励
	 * @param model
	 * @param t
	 * @param borrowUser
	 * @param tenderUser
	 * @param taskList
	 * @param param
	 */
	public void flowBorrowGiveAward(Borrow model, BorrowTender t, User borrowUser,
			User tenderUser, List<Object> taskList, BorrowParam param);
}
