package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.LoanStatisticsDao;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.LoanStatistics;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.LoanStatisticsService;
import com.liangyou.util.NumberUtils;

/**
 * v1.8.0.4_u4 TGPROJECT-371 	qinjun 2014-07-22
 */
@Service(value="loanStatisticsService")
@Transactional
public class LoanStatisticsServiceImpl extends BaseServiceImpl implements LoanStatisticsService {
	
	@Autowired
	LoanStatisticsDao loanStatisticsDao;
	@Autowired
	BorrowRepaymentDao borrowRepaymentDao;
	
	@Override
	public void quartzTenderStaticstics() {
		//投资人信息
		LoanStatistics tenderLoanStatistics = null;
		List<LoanStatistics> updateTenderLoanList = new ArrayList<LoanStatistics>();
		List<LoanStatistics> tenderLoanList = loanStatisticsDao.getTenderStatisticsList();
		if(tenderLoanList!=null&&tenderLoanList.size()>0){
			for (LoanStatistics tenderLoan : tenderLoanList) {
				tenderLoanStatistics = loanStatisticsDao.getLoanStatisticsByUser(tenderLoan.getUser());
				if(tenderLoanStatistics != null){
					tenderLoanStatistics.setTenderTotal(tenderLoan.getTenderTotal());
					tenderLoanStatistics.setTenderCount(tenderLoan.getTenderCount());
					tenderLoanStatistics.setWaitCollectionCapital(tenderLoan.getWaitCollectionCapital());
					tenderLoanStatistics.setEarnInterest(tenderLoan.getEarnInterest());
				}else{//无记录
					tenderLoanStatistics = tenderLoan;
				}
				//处理用户类型
				madeUserType(tenderLoanStatistics, "tender");
				//获取未完成还款的投资笔数
				int waitTenderCount = loanStatisticsDao.getWaitTenderCountByUser(tenderLoanStatistics.getUser().getUserId());
				tenderLoanStatistics.setWaitTenderCount(waitTenderCount);
				//获取逾期的待收本金
				double lateCapital = loanStatisticsDao.getLateCapitalByUser(tenderLoanStatistics.getUser().getUserId());
				tenderLoanStatistics.setProfitAndLossTotal(tenderLoanStatistics.getEarnInterest() + lateCapital);
				tenderLoanStatistics.setUpdatetime(new Date());
				updateTenderLoanList.add(tenderLoanStatistics);
			}
			loanStatisticsDao.save(updateTenderLoanList);
		}
	}
	
	@Override
	public void quartzBorrowerStaticstics(){
		//借款人信息
		LoanStatistics borrowerLoanStatistics = null;
		List<LoanStatistics> updateBorrowerLoanList = new ArrayList<LoanStatistics>();
		List<LoanStatistics> borrowerLoanList = loanStatisticsDao.getLoanStatisticsList();
		if(borrowerLoanList!=null&&borrowerLoanList.size()>0){
			for (LoanStatistics borrowerLoan : borrowerLoanList) {
				borrowerLoanStatistics = loanStatisticsDao.getLoanStatisticsByUser(borrowerLoan.getUser());
				if(borrowerLoanStatistics!=null){
					borrowerLoanStatistics.setLoanCount(borrowerLoan.getLoanCount());
					borrowerLoanStatistics.setLoanTotal(borrowerLoan.getLoanTotal());
				}else{
					borrowerLoanStatistics = borrowerLoan; 
				}
				//处理用户类型
				madeUserType(borrowerLoanStatistics, "borrower");
				//获取用户未还款本金
				double waitRepayCapital = loanStatisticsDao.getWaitRepayCapitalByUser(borrowerLoanStatistics.getUser().getUserId());
				borrowerLoanStatistics.setWaitRepayCapital(waitRepayCapital);
				//计算网站自定义用户 借款利率=（借款本金1*借款利率1+借款本金2*借款利率2+借款本金3*借款利率3）/所有借款本金之和  
				double diyBorrowAccount = loanStatisticsDao.getDiyBorrowAcountByUser(borrowerLoanStatistics.getUser().getUserId());
				borrowerLoanStatistics.setDiyApr(NumberUtils.format2(diyBorrowAccount/borrowerLoanStatistics.getLoanTotal()));			
				borrowerLoanStatistics.setUpdatetime(new Date());
				updateBorrowerLoanList.add(borrowerLoanStatistics);
			}
			loanStatisticsDao.save(updateBorrowerLoanList);
		}
	}
	/**
	 * 处理用户类型
	 * @param loan
	 * @param type
	 */
	private void madeUserType(LoanStatistics loan , String type){
		if(type.equals("borrower")){
			if(loan.getUserType() == 1){
				loan.setUserType(3);
			}else{
				loan.setUserType(2);
			}
		}else if(type.equals("tender")){
			if(loan.getUserType() == 2){
				loan.setUserType(3);
			}else{
				loan.setUserType(1);
			}
		}
	}

	@Override
	public PageDataList<LoanStatistics> getLoanStatisticsList(SearchParam param) {
		return loanStatisticsDao.findPageList(param);
	}

	@Override
	public PageDataList<LoanStatistics> getAllLoanStatisticsList(SearchParam param) {
		return loanStatisticsDao.findAllPageList(param);
	}

	@Override
	public PageDataList<BorrowRepayment> getPageCustomAccountCount(SearchParam param) {
		PageDataList<BorrowRepayment> pageRepaymentList = borrowRepaymentDao.findPageList(param);
		return pageRepaymentList;
	}

	@Override
	public List<BorrowRepayment> getListCustomAccountCount(SearchParam param) {
		List<BorrowRepayment> repaymentList = borrowRepaymentDao.findByCriteria(param);
		return repaymentList;
	}
	
	
}
