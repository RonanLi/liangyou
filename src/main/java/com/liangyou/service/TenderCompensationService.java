package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.TenderCompensation;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
public interface TenderCompensationService {

	/**
	 * 还款给投资人，补偿金
	 * @param repayment
	 * @param taskList
	 */
	public void tenderCompensationRepay(BorrowRepayment repayment, List<Object> taskList);
	
    /**
     * 满标复审失败或撤标，添加补偿金记录及补偿金账户，再调用第三方转账接口，完成网站转账投资人流程
     * @param tender
     * @param borrow
     * @param taskList
     */
	public void failTenderCompensation(BorrowTender tender, Borrow borrow,List<Object> taskList);
	/**
	 * 满标复审通过
	 * 添加补偿金记录
	 * 补偿金账户处理
	 */
	public void verifyFullSuccessCompensation(Borrow borrow, BorrowTender tender);
	
	/**
	 * 逾期的标网站垫付的时候，支付投资人补偿金
	 * @param repayment
	 * @param taskList
	 */
	public void sitePayLateBorrowCompensation(BorrowRepayment repayment,List<Object> taskList);

	/**
	 * 用户还款给网站
	 * @param borrowRepayment
	 * @param taskList
	 */
	public void borrowUserPaySiteCompensation(BorrowRepayment borrowRepayment, List<Object> taskList);

	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	/**
	 * 后台分页查询补偿金记录
	 * @param param
	 * @return
	 */
	public PageDataList<TenderCompensation> getList(SearchParam param);

	/**
	 * 后台导出补偿金记录报表
	 * @param param
	 * @return
	 */
	public List getExportCompensationList(SearchParam param);
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
}
