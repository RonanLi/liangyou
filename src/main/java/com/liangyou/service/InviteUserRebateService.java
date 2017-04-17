package com.liangyou.service;

import java.util.List;
import java.util.Map;

import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.InviteUserRebate;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 推荐用户打折服务
 * 
 * @author lx TGPROJECT-302 add
 *
 */
public interface InviteUserRebateService {
	/**
	 * 分页查询
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<InviteUserRebate> getInviteUserRebateListBySearchParam(
			SearchParam param);

	/**
	 * 导出查询
	 * 
	 * @param param
	 * @return
	 */
	public List<InviteUserRebate> getExportInviteUserRebateList(
			SearchParam param);

	/**
	 * 通过ID查询推荐投资记录
	 * 
	 * @param id
	 * @return
	 */
	public InviteUserRebate getInviteUserRebateById(long id);

	/**
	 * 推荐提成审核不通过
	 * 
	 * @param iur
	 */
	public void cancelInviteRebate(InviteUserRebate iur);

	/**
	 * 审核通过
	 * 
	 * @param iur
	 */
	public void verify(InviteUserRebate iur, AccountLog log, AccountRecharge ar)
			throws Exception;

	/**
	 * 计算回扣金额，和比例
	 * 
	 * @param iur
	 * @return
	 */
	public Map<String, Double> calculateRebate(InviteUser iu, BorrowTender bt);

	/**
	 * 计算回扣金额，和比例
	 * 
	 * @param iur
	 * @return
	 */
	public double calculateRebate(InviteUserRebate iur);

	public void update(InviteUserRebate iur);
}
