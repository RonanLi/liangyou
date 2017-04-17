package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.context.MmmType;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.CompensationAccountDao;
import com.liangyou.dao.TenderCompensationDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.CompensationAccount;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.Rule;
import com.liangyou.domain.TenderCompensation;
import com.liangyou.domain.User;
import com.liangyou.domain.Warrant;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.BorrowService;
import com.liangyou.service.RuleService;
import com.liangyou.service.TenderCompensationService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;

/**
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 start
 * //wsl 满标前补偿金功能【心意贷】2014-08-25 end
 * @author wsl
 *
 */
@Service(value="tenderCompensationService")
@Transactional
public class TenderCompensationServiceImpl extends BaseServiceImpl implements TenderCompensationService {
	private Logger logger=Logger.getLogger(TenderCompensationServiceImpl.class);
	@Autowired
	private TenderCompensationDao tenderCompensationDao;
	@Autowired
	private CompensationAccountDao compensationAccountDao;
	@Autowired
	private BorrowDao borrowDao;
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private BorrowService borrowService;

	/**
	 * 满标复审通过
	 * 添加补偿金记录
	 * 补偿金账户处理
	 */
	@Override
	public void verifyFullSuccessCompensation(Borrow borrow ,BorrowTender tender){
		Rule rule = ruleService.getRuleByNid("tender_compenstation_rule");
		if(rule != null && rule.getStatus() == 1){
			int successTender = rule.getValueIntByKey("success_tender");
			if(successTender == 1){
				User tenderUser = tender.getUser();
				User borrowUser = borrow.getUser();
				//添加记录
				TenderCompensation tc = fillTenderCompensation(tender, borrow);
			    CompensationAccount tenderCompensationAccount = compensationAccountDao.getCompensationAccountByUser(tenderUser);
				if(tenderCompensationAccount == null){
					//添加投资人，补偿金账户
					addTenderCompensationAccount(tc, tenderUser);//一旦保存，缓存就存在
				}else{
					//更新投资人，补偿金账户
					compensationAccountDao.updateCompensationAccount(tc.getCompensation(), 0, 0,0, tenderUser.getUserId());
				}
				
				CompensationAccount borrowCompensationAccount = compensationAccountDao.getCompensationAccountByUser(borrowUser);
				if(borrowCompensationAccount == null){
					//添加借款人，补偿金账户
					addBorrowCompensationAccount(tc, borrowUser);//一旦保存，缓存就存在
				}else{
					//更新借款人，补偿金账户
					compensationAccountDao.updateCompensationAccount(0, tc.getCompensation(), 0, 0, borrowUser.getUserId());
				}
			}
		}
	}

	@Override
	public void tenderCompensationRepay(BorrowRepayment repayment, List<Object> taskList) {
		Rule rule = ruleService.getRuleByNid("tender_compenstation_rule");
		if(rule != null && rule.getStatus() == 1){
			int successTender = rule.getValueIntByKey("success_repay");
			if(successTender == 1 && repayment.getPeriod() == 0){
				//启用规则，必须是第一期，其他期数不进行还款
				logger.info("补偿金，借款人还款进来： " + repayment.getId() );

				Borrow model=repayment.getBorrow();
				User borrowUser=model.getUser();

				Account at = accountDao.getAcountByUser(borrowUser);

				double compenMoney = 0;//借款人，还款总的补偿金
				compenMoney = tenderCompensationDao.getSumBorrowCompensation(borrowUser,model,0);

				if(at.getUseMoney() < compenMoney ){
					//判断可用余额是否足够
					throw new BussinessException("可用余额不足还款失败。");
				}
				compenMoney= NumberUtils.formatTo2(compenMoney);
				//扣除借款人还款补偿金总额
				if(compenMoney>0){
					accountDao.updateAccount(-compenMoney, -compenMoney,0, 0, 0,borrowUser.getUserId());
					Account act=accountDao.getAcountByUser(borrowUser);
					AccountLog log=new AccountLog(borrowUser.getUserId(),Constant.COMPENSATION_REPAID,Constant.ADMIN_ID);
					fillAccountLog(log, Constant.COMPENSATION_REPAID, act, borrowUser, new User(Constant.ADMIN_ID), compenMoney, 0,
							"["+getLogRemark(model)+"]还款，扣除满标前补偿金"+compenMoney);
					accountLogDao.save(log);
					//更新补偿金账户资金
					compensationAccountDao.updateCompensationAccount(0, -compenMoney, 0, compenMoney, borrowUser.getUserId());
				}

				List<TenderCompensation> list = tenderCompensationDao.getTenderCompensationsByBorrow(borrowUser, model, 0);
				for (TenderCompensation tenderCompensation : list) {
					//给投资人补偿金
					User tenderUser = tenderCompensation.getTenderUser();
					User repayUser = tenderCompensation.getBorrowUser();
					double repayMoney =tenderCompensation.getCompensation();
					//投资人获取补偿金
					if(repayMoney > 0 ){
						Account tenderAccount = accountDao.getAcountByUser(tenderUser);
						accountDao.updateAccount(repayMoney, repayMoney,0, 0, 0,tenderUser.getUserId());

						AccountLog log=new AccountLog(tenderUser.getUserId(),Constant.COMPENSATION_COLLECT,Constant.ADMIN_ID);
						fillAccountLog(log, Constant.COMPENSATION_COLLECT, tenderAccount, tenderUser, new User(Constant.ADMIN_ID), repayMoney, 0,
								"["+getLogRemark(model)+"]还款，收取满标前补偿金"+repayMoney);
						accountLogDao.save(log);
						//更新补偿金账户资金
						compensationAccountDao.updateCompensationAccount(-repayMoney, 0, repayMoney, 0, tenderUser.getUserId());
					}
					tenderCompensation.setStatus(1);//投资人待收状态，改成已经待收
					//添加双乾任务
					MmmPay mmmPay = new MmmPay("2","2","1",repayUser.getUserId(),repayUser.getApiId(),tenderUser.getUserId(),tenderUser.getApiId(),
							repayMoney,MmmType.MMM_PAY_USER,"满标前补偿金",MmmType.DOREPAY,new Date());
					taskList.add(mmmPay);
				}
				//更新已经还款的状态
				tenderCompensationDao.update(list);
			}
		}
	}

	@Override
	public void failTenderCompensation(BorrowTender tender, Borrow borrow, List<Object> taskList) {
		Rule rule = ruleService.getRuleByNid("tender_compenstation_rule");
		if(rule != null && rule.getStatus() == 1){
			int failTender = rule.getValueIntByKey("fail_tender");
			if(failTender == 1){
				TenderCompensation tc = fillTenderCompensation(tender, borrow);//添加补偿金记录
				//给投资人补偿金
				User tenderUser = tender.getUser();
				
				double repayMoney = tc.getCompensation();
				if(repayMoney > 0 ){//投资人获取补偿金
					Account tenderAccount = accountDao.getAcountByUser(tenderUser);
					accountDao.updateAccount(repayMoney, repayMoney,0, 0, 0,tenderUser.getUserId());

					AccountLog log=new AccountLog(tenderUser.getUserId(),Constant.COMPENSATION_SITE_PAY,Constant.ADMIN_ID);
					fillAccountLog(log, Constant.COMPENSATION_SITE_PAY, tenderAccount, tenderUser, new User(Constant.ADMIN_ID), repayMoney, 0,
							"["+getLogRemark(borrow)+"]撤标，收到满标前补偿金"+repayMoney);
					accountLogDao.save(log);
					//更新补偿金账户资金
					compensationAccountDao.updateCompensationAccount(-repayMoney, 0, repayMoney, 0, tenderUser.getUserId());
				}
				CompensationAccount tenderCompensationAccount = compensationAccountDao.getCompensationAccountByUser(tenderUser);
				if(tenderCompensationAccount == null){
					//添加投资人，补偿金账户
					addTenderCompensationAccount(tc, tenderUser);//一旦保存，缓存就存在
				}else{
					//更新投资人，补偿金账户
					compensationAccountDao.updateCompensationAccount(tc.getCompensation(), 0, 0,0, tenderUser.getUserId());
				}
				
				//添加双乾任务
				MmmPay mmmPay = new MmmPay("2","2","1",Constant.ADMIN_ID,Global.getValue("plat_form_mmm"),
						tenderUser.getUserId(),tenderUser.getApiId(),repayMoney,MmmType.MMM_PAY_USER,
						"垫付满标前补偿金",MmmType.SITE_PAY_COMPENSATION,new Date());
				taskList.add(mmmPay);
			}
		}	
	}

	@Override
	public void sitePayLateBorrowCompensation(BorrowRepayment repayment, List<Object> taskList){
		Rule rule = ruleService.getRuleByNid("tender_compenstation_rule");
		if(rule != null && rule.getStatus() == 1){
			int successTender = rule.getValueIntByKey("success_repay");
			//启用规则，必须是第一期，其他期数不进行还款
			if(successTender == 1 && repayment.getPeriod() == 0){
				logger.info("补偿金,网站垫付还款进来： " + repayment.getId() );

				Borrow model=repayment.getBorrow();
				User borrowUser=model.getUser();
				//查询所有借款人，应该还款的补偿金记录
				List<TenderCompensation> list = tenderCompensationDao.getTenderCompensationsByBorrow(borrowUser, model, 0);
				for (TenderCompensation tenderCompensation : list) {
					User tenderUser = tenderCompensation.getTenderUser();
					double repayMoney =tenderCompensation.getCompensation();
					//循环给投资人资金补偿。
					if(repayMoney > 0 ){
						Account tenderAccount = accountDao.getAcountByUser(tenderUser);
						accountDao.updateAccount(repayMoney, repayMoney,0, 0, 0,tenderUser.getUserId());

						AccountLog log=new AccountLog(tenderUser.getUserId(),Constant.COMPENSATION_COLLECT,Constant.ADMIN_ID);
						fillAccountLog(log, Constant.COMPENSATION_COLLECT, tenderAccount, tenderUser, new User(Constant.ADMIN_ID), repayMoney, 0,
								"["+getLogRemark(model)+"]网站垫付，收到补偿金"+repayMoney);
						accountLogDao.save(log);
						//更新补偿金账户资金
						compensationAccountDao.updateCompensationAccount(-repayMoney, 0, repayMoney, 0, tenderUser.getUserId());
					}
					tenderCompensation.setStatus(3);//逾期网站垫付，状态改成3
					//添加双乾任务
					MmmPay mmmPay = new MmmPay("2","2","1",Constant.ADMIN_ID,Global.getValue("plat_form_mmm"),
							tenderUser.getUserId(),tenderUser.getApiId(),repayMoney,MmmType.MMM_PAY_USER,
							"垫付满标前补偿金",MmmType.SITE_PAY_COMPENSATION,new Date());
					taskList.add(mmmPay);
				}
				//更新投资人，网站垫付补偿金记录状态
				tenderCompensationDao.update(list);

			}
		}
	}

	/**
	 * 用户还款给网站
	 * @param borrowRepayment
	 * @param taskList
	 */
	@Override
	public void borrowUserPaySiteCompensation(BorrowRepayment borrowRepayment, List<Object> taskList){
		Rule rule = ruleService.getRuleByNid("tender_compenstation_rule");
		if(rule != null && rule.getStatus() == 1){
			int successTender = rule.getValueIntByKey("success_repay");
			if(successTender == 1 && borrowRepayment.getPeriod() == 0){
				//启用规则，必须是第一期，其他期数不进行还款
				logger.info("补偿金，借款人还款给网站进来： " + borrowRepayment.getId() );

				Borrow model=borrowRepayment.getBorrow();
				User borrowUser=model.getUser();

				Account at = accountDao.getAcountByUser(borrowUser);

				double compenMoney = 0;//借款人，还款总的补偿金
				compenMoney = tenderCompensationDao.getSumBorrowCompensation(borrowUser,model,3);

				if(at.getUseMoney() < compenMoney ){
					//判断可用余额是否足够
					throw new BussinessException("可用余额不足还款失败。");
				}
				compenMoney= NumberUtils.formatTo2(compenMoney);
				//扣除借款人还款补偿金总额
				if(compenMoney>0){
					accountDao.updateAccount(-compenMoney, -compenMoney,0, 0, 0,borrowUser.getUserId());
					Account act=accountDao.getAcountByUser(borrowUser);
					AccountLog log=new AccountLog(borrowUser.getUserId(),Constant.COMPENSATION_REPAY_SITE,Constant.ADMIN_ID);
					fillAccountLog(log, Constant.COMPENSATION_REPAY_SITE, act, borrowUser, new User(Constant.ADMIN_ID), compenMoney, 0,
							"["+getLogRemark(model)+"]还款给网站，扣除满标前补偿金"+compenMoney);
					accountLogDao.save(log);
					//更新补偿金账户资金
					compensationAccountDao.updateCompensationAccount(0, -compenMoney, 0, compenMoney, borrowUser.getUserId());
				}
				//更新满标前补偿金的记录的状态
				List<TenderCompensation> sitePayCompensations = tenderCompensationDao.getTenderCompensationsByBorrow(borrowUser, model, 3);
				for (TenderCompensation tenderCompensation : sitePayCompensations) {
					//用户
					tenderCompensation.setStatus(1);
				}
				tenderCompensationDao.update(sitePayCompensations);
				
				//添加双乾任务
				MmmPay mmmPay = new MmmPay("2","2","1",borrowUser.getUserId(),borrowUser.getApiId(),
						Constant.ADMIN_ID,Global.getValue("plat_form_mmm"),compenMoney,MmmType.MMM_PAY_USER,
						"还款给网站补偿金",MmmType.SITE_PAY_COMPENSATION,new Date());
				taskList.add(mmmPay);

			}
		}
	}


	/**
	 * 添加补偿金记录
	 * @param tender
	 * @param borrow
	 */
	private TenderCompensation fillTenderCompensation(BorrowTender tender, Borrow borrow){
		long tenderTime = DateUtils.getDayYYYYMMdd(tender.getAddtime());
		long verifyTime =  DateUtils.getDayYYYYMMdd(borrow.getVerifyTime());//满标复审时间
		//天数 = 满标复审通过|撤标时间  — 投标成功时间；计算这两个时间差的天数，注意不满一天按一天计算。
		long day = (verifyTime-tenderTime)/(60*60*24);
		long days = day ==0 ? 0 : day;
		double dayApr = borrow.getApr() / 360;
		double totalCompensation = NumberUtils.format2(tender.getAccount()*days*dayApr/100) ;

		User tenderUser = tender.getUser();
		User borrowUser = borrow.getUser();
		TenderCompensation tc =  new TenderCompensation();
		tc.setTenderMoney(tender.getAccount());
		tc.setTenderUser(tenderUser);
		tc.setBorrowUser(borrowUser);
		tc.setCompensation(totalCompensation);
		tc.setDays((int)days);
		tc.setBorrow(borrow);
		tc.setTender(tender);
		tc.setRate(dayApr);
		if(tender.getStatus() == 0){//审核通过，添加待处理记录
			tc.setStatus(0);
		}else if(tender.getStatus() == 2){//审核不通过，资金直接转账，添加已处理记录
			tc.setStatus(2);
		}
		tc.setAddip(Global.ipThreadLocal.get()+"");
		tc.setAddtime(new Date());
		return tenderCompensationDao.save(tc);

	}
	
	/**
	 * 添加投资人，补偿金账户
	 * @param tender
	 * @param borrow
	 */
	private void addTenderCompensationAccount(TenderCompensation tc,User tenderUser){
		CompensationAccount  ca = new CompensationAccount();
		ca.setUser(tenderUser);
		ca.setCollectionCompensation(tc.getCompensation());
		ca.setCollectionYesCompensation(0);
		ca.setAddtime(new Date());
		compensationAccountDao.save(ca);
	}
	/**
	 * 添加借款人，补偿金账户
	 * @param tender
	 * @param borrow
	 */
	private void addBorrowCompensationAccount(TenderCompensation tc,User borrowUser){
		CompensationAccount  ca = new CompensationAccount();
		ca.setUser(borrowUser);
		ca.setRepayCompensation(tc.getCompensation());
		ca.setRepayYesCompensation(0);
		ca.setAddtime(new Date());
		compensationAccountDao.save(ca);
	}
	
	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	@Override
	public PageDataList<TenderCompensation> getList(SearchParam param) {
		PageDataList<TenderCompensation>  list = tenderCompensationDao.findPageList(param);
		return list;
	}
	
	@Override
	public List getExportCompensationList(SearchParam param) {
		return tenderCompensationDao.findAllPageList(param).getList();
	}
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
}
