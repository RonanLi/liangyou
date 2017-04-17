package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.BorrowDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.UserAmountDao;
import com.liangyou.dao.UserAmountLogDao;
import com.liangyou.dao.WebPaidDao;
import com.liangyou.dao.WebRepayLogDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayType;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.WebPaid;
import com.liangyou.domain.WebRepayLog;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.RuleService;
import com.liangyou.service.WebPaidService;
import com.liangyou.util.NumberUtils;
@Service(value="webPaidService")
@Transactional
public class WebPaidServiceImpl extends BaseServiceImpl implements WebPaidService {
	
	private Logger logger = Logger.getLogger(WebPaidServiceImpl.class);
	
	@Autowired
	private WebPaidDao webPaidDao;
	@Autowired
	private WebRepayLogDao webRepayLogDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private ApiService apiService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private BorrowRepaymentDao borrowRepaymentDao;
	@Autowired
	private UserAmountDao userAmountDao;
	@Autowired
	private UserAmountLogDao userAmountLogDao;
	@Autowired
	private BorrowDao borrowDao;

	@Override
	
	public PageDataList<WebPaid> getPageWebRepayList(SearchParam param) {
		PageDataList<WebPaid> pageWebPaidList = webPaidDao.findPageList(param);
		return pageWebPaidList;
	}

	@Override
	public void doRepayWeb() {
		Rule rule = ruleService.getRuleByNid("auto_repay_web_paid");  //判断自动扣款功能是否开启
		if (null !=rule && rule.getStatus() == 1) {
			int autoRepay = rule.getValueIntByKey("auto_repay");
			if (autoRepay == 1) {   
				//获取待还垫付记录
				List<WebPaid> webPaidList = webPaidDao.getWaitWebPaidList();
				if (null != webPaidList && webPaidList.size()>0) {  //是否有待还垫付
					for (WebPaid webPaid : webPaidList) {
						//判断是否满足条件
						BorrowRepayment repayment = webPaid.getRepayment();
						if (repayment.getStatus()==1 && repayment.getWebstatus() ==1) {
							logger.info("此笔借款用户已经偿还完毕！-----------》repaymentId:"+repayment.getId()+"借款borrowid:"+repayment.getBorrow().getId());
							continue;
						}
						User repayUser = webPaid.getBorrow().getUser();
						logger.info("待还款用户id："+repayUser.getUserId()+"待还金额："+webPaid.getWaitRepay());
						Account account = repayUser.getAccount();
						if (account.getUseMoney()>0) {  //有可用金额
							double useMoney = account.getUseMoney();  //获取用户可用金额
							double waitPayMoney = webPaid.getWaitRepay();
							double payMoney =0;
							if (useMoney <waitPayMoney) { //若用户可用余额小于待还金额，则扣除所有可用余额
								payMoney = useMoney;
							}else{
								payMoney = waitPayMoney;
							}
							logger.info("用户自动扣款给平台，userId:"+repayUser.getUserId()+"，扣款金额："+payMoney);
							 //触发第三方接口
							List<Object> taskList = new ArrayList<Object>();
							apiService.repayToWebSiteLoanMoney(webPaid.getRepayment(), payMoney,webPaid.getBorrow(), taskList);
							boolean result = borrowService.doApiTask(taskList);  //触发第三方接口
							int apiType = Global.getInt("api_code");
							if (result==true || apiType==1) {  //处理本地业务逻辑，处理本地业务：由于汇付从用户账户划账给平台与其他第三方不同，除汇付外的第三方在处理时，必须等到第三方处理成功后才能处理本地业务，汇付则不需要
								logger.info("系统自动扣款成功，处理本地业务，borrowid:"+repayment.getBorrow().getId()+",repaymentId:"+repayment.getId());
								List<AccountLog> logList = new ArrayList<AccountLog>();
								
								String remark = doRepaymentRetRemark(payMoney, repayment, account, logList);
								accountLogDao.save(logList);
								//更新repayment对象
								borrowRepaymentDao.update(repayment);
								//更新平台垫付资金记录
								webPaidDao.updateRepayWeb(webPaid.getId(), payMoney);
								//添加扣自动扣款
								WebRepayLog webRepayLog = new WebRepayLog(payMoney, new Date(), "",webPaid.getBorrow(),webPaid.getRepayment() );
								//添加订单号
								String ordNo = getOrdNo(taskList);
								webRepayLog.setContent(remark);
								webRepayLog.setOrdNo(ordNo);
								webRepayLogDao.save(webRepayLog);
								//当扣款总额与当笔借款应还金额相同时将此笔还款信息修改为已经还款状态
								doBorrowRepayMent(webPaid,payMoney);
							}else{
								MmmPay mPay = (MmmPay)taskList.get(0);
								logger.info("第三方处理失败,交易订单号为："+mPay.getOrderNo()+",交易用户id为"+account.getUser().getUserId()+"交易金额为："+payMoney);
							}
						}else{
							logger.info("用户："+repayUser.getUserId()+",无可用资金");
						}
					}
					
				}
			}
		}
	}

	@Override
	public void doWebPay(BorrowRepayment repayment, double webPayAccount,
			double webPayInterest, double webPayPenalty,double waitRepayAccount) {
		Borrow borrow = repayment.getBorrow();
		double webAccount = webPayAccount + webPayInterest +webPayPenalty;
		WebPaid webPaid = new WebPaid(webAccount, webAccount, new Borrow(borrow.getId()), repayment);
		webPaid.setWebPayAccount(webPayAccount);
		webPaid.setWebPayInterest(webPayInterest);
		webPaid.setWebPayPenalty(webPayPenalty);
		webPaid.setWaitReceivePenal(repayment.getLateInterest());
		webPaid.setAddTime(new Date());
		webPaid.setWaitRepay(waitRepayAccount);
		webPaidDao.save(webPaid);
	}
	
	
	

	
	@Override
	public void doUserPayWeb(BorrowRepayment repayment) {
		//重新查询repayment
		BorrowRepayment repay = borrowRepaymentDao.find(repayment.getId());
		if(repayment.getStatus()!=2&&repayment.getWebstatus()!=3){
			logger.error("非还款给网站垫付状态,请勿重复操作，repaymentId"+repayment.getId());
			return ;
		}
		//处理还款，若存在自动扣款的金额，要将此自动扣款金额减掉
		Borrow model=repayment.getBorrow();
		User borrowUser=model.getUser();
		BorrowModel borrow=BorrowHelper.getHelper(model); //封装borrow  计算费用方法。
		Account at = accountDao.getAcountByUser(borrowUser);
		WebPaid webPaid = webPaidDao.getWebPaidByBorrowIdAndRepayId(repay.getBorrow().getId(), repay.getId());
		if (null !=webPaid) {  //判断是否存在已经存在自动扣款
			double waitRepay =webPaid.getWaitRepay();   //获取剩余待还金额
			if (at.getUseMoney()<waitRepay) {   //判断可用余额是否足够
				throw new BussinessException("可用金额少于剩余应还款金额，还款失败！");
			}
			List<Object> taskList = new ArrayList<Object>();
			apiService.repayToWebSiteLoanMoney(webPaid.getRepayment(), waitRepay,webPaid.getBorrow(), taskList);
			boolean result = borrowService.doApiTask(taskList);  //触发第三方接口
			List<AccountLog> logList = new ArrayList<AccountLog>();
			doRepaymentRetRemark(waitRepay, repayment, at, logList);
			accountLogDao.save(logList);
			//更新repayment对象
			borrowRepaymentDao.update(repayment);
			//更新平台垫付资金记录
			webPaidDao.updateRepayWeb(webPaid.getId(), waitRepay);
			
			
		}else{  //不存在垫付记录，按正常垫付还款走
			if(at.getUseMoney() < repayment.getRepaymentAccount()){//判断可用余额是否足够
				throw new BussinessException("可用余额不足还款给网站垫付失败。");
			}
			double capital=repay.getCapital(); //本金
			double interest=repay.getInterest();//利息
			
			List<Object> taskList = new ArrayList<Object>();
			//还本金
			if(capital>0){
				accountDao.updateAccount(-capital, -capital, 0, 0,-capital, borrowUser.getUserId());
				Account act=accountDao.getAcountByUser(borrowUser);
				AccountLog log=new AccountLog(borrowUser.getUserId(),Constant.REPAY_WEB_CAPTIAL,Constant.ADMIN_ID);
				fillAccountLog(log, Constant.REPAY_WEB_CAPTIAL, act, borrowUser, new User(Constant.ADMIN_ID), capital, 0,
						"["+getLogRemark(model)+"]还款给网站，扣除本金"+capital);
				accountLogDao.save(log);
			}
			//还利息
			if(interest>0){
				accountDao.updateAccount(-interest, -interest,0, 0, -interest,borrowUser.getUserId());
				Account act=accountDao.getAcountByUser(borrowUser);
				AccountLog log=new AccountLog(borrowUser.getUserId(),Constant.REPAY_WEB_INTEREST,Constant.ADMIN_ID);
				fillAccountLog(log, Constant.REPAY_WEB_INTEREST, act, borrowUser, new User(Constant.ADMIN_ID), interest, 0,
						"["+getLogRemark(model)+"]还款给网站，扣除利息"+interest);
				accountLogDao.save(log);
				
			}
			//还逾期罚息
			double lateInterest = repay.getLateInterest();
			if(lateInterest > 0){
				accountDao.updateAccount(-lateInterest, -lateInterest,0, 0,0, borrowUser.getUserId());  //待还要 减掉
				Account act=accountDao.getAcountByUser(borrowUser);
				AccountLog log=new AccountLog(borrowUser.getUserId(),Constant.REPAY_WEB_LATEINTEREST,Constant.ADMIN_ID);
				fillAccountLog(log, Constant.REPAY_WEB_LATEINTEREST, act, borrowUser, new User(Constant.ADMIN_ID), lateInterest, 0,
						"["+getLogRemark(model)+"]还款给网站，扣除逾期罚息"+lateInterest);
				accountLogDao.save(log);
			}
			//如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
			if(model.getType() == Constant.TYPE_CREDIT){//还款解冻资金
				userAmountDao.updateCreditAmount(0,repay.getCapital(),-repay.getCapital(),borrowUser.getUserId());
				UserAmount  ua=userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
				userAmountLogDao.save(fillUserAmountLog(ua,Constant.REPAID, -repay.getCapital(), "还款，解冻信用额度：" + repay.getCapital(), ""));
			}
			repay.setRepaymentYescapital(capital);
			repay.setRepaymentYesinterest(interest);
			borrowService.doApiTask(taskList);
		}
		int repayStatus=7;
		if(borrow.isLastPeriod(repay.getPeriod())){
			repayStatus=8;
		}
		model.setStatus(repayStatus);
		borrowDao.update(model);
		repay.setRepaymentYestime(new Date());
		repay.setStatus(1);
		repay.setBorrowRepayType(new BorrowRepayType(Constant.REPAY_PAY_WEB));
		borrowRepaymentDao.update(repay);
		
	}

	/**
	 * 获取交易订单号
	 * @param taskList
	 * @return
	 */
	private String getOrdNo(List<Object> taskList){
		String ordNo = "";
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		//cancel by lxm 2017-2-13 17:08:34
//		case 1://汇付
//			ordNo = "分笔扣款，订单号不做记录";
//			break;
//		case 2://易极付
//			YjfPay yjf = (YjfPay)taskList.get(0);
//			ordNo = yjf.getOrderno();
//			break;
		case 3:
			MmmPay mPay = (MmmPay)taskList.get(0);
			ordNo = mPay.getOrderNo();
			break;
		}
		return ordNo;
	}
	
	/**
	 * 处理关于扣款后，是否将此期还款已经偿还完毕
	 * @param webPaid
	 * @param repayMoney 本次还款金额
	 */
	private void doBorrowRepayMent(WebPaid webPaid,double repayMoney){
		if (webPaid.getWaitRepay() ==repayMoney) { //已经将网站垫付金额偿还完毕，更新还款计划为已还，
			//获取还款计划
			BorrowRepayment repayment = borrowRepaymentDao.find(webPaid.getRepayment().getId());
			repayment.setStatus(1);  //将状态修为已还
			repayment.setRepaymentYestime(new Date());  //设置还清时间
			//如果是信用标，将本次的借款金额累加回借款人的信用额度，生成信用额度流水
			Borrow model = repayment.getBorrow();
			User borrowUser = model.getUser();
			BorrowModel borrow=BorrowHelper.getHelper(model);
			if(model.getType() == Constant.TYPE_CREDIT){//还款解冻信用额度
				userAmountDao.updateCreditAmount(0,repayment.getCapital(),-repayment.getCapital(),borrowUser.getUserId());
				UserAmount  ua=userAmountDao.findByProperty("user", new User(borrowUser.getUserId())).get(0);
				userAmountLogDao.save(fillUserAmountLog(ua,Constant.REPAID, -repayment.getCapital(), "还款，解冻信用额度：" + repayment.getCapital(), ""));
			}
			int repayStatus = 7;
			if (borrow.isLastPeriod(repayment.getPeriod())) {  //判断是否是最后一期
				repayStatus =8;
			}
			model.setStatus(repayStatus);
			borrowDao.update(model);
			borrowRepaymentDao.update(repayment);
			
		}
	}
	
	/**
	 * 自动扣款时，处理扣款顺序方法：先：1、本金，2、利息，3、罚息
	 * @param repayMoney :还款金额
	 * @param repayment  :repayment对象
	 */
	private String doRepaymentRetRemark(double repayMoney ,BorrowRepayment repayment,Account account,List<AccountLog> logList){
		String remark = getLogRemark(repayment.getBorrow())+"网站已垫付，系统自动扣款";
		double moneyCapital = 0;
		//扣除本金
		double surplusCptital = repayment.getCapital() -repayment.getRepaymentYescapital();
		if (surplusCptital>0) {
			if (surplusCptital >= repayMoney) {  //判断本次扣款金额是否大于待还本金
				moneyCapital = repayMoney;
			}else{
				moneyCapital =  surplusCptital;
			}
			moneyCapital = NumberUtils.format2(moneyCapital);
			double yesRepayment = repayment.getRepaymentYescapital();
			repayment.setRepaymentYescapital(yesRepayment+moneyCapital);
			doAccountAndLog(moneyCapital, account, getLogRemark(repayment.getBorrow())+"网站已垫付，系统自动扣款，扣除本金:"+moneyCapital+"元",logList,true);
			repayMoney = repayMoney- moneyCapital;
			remark += "本金:"+moneyCapital+"元";
		}
		
		//扣除利息
		double surplusInterest = repayment.getInterest() -repayment.getRepaymentYesinterest();  //剩余未还利息
		double moneyInterest = 0;
		if (surplusInterest >0) {
			if (repayMoney >0) {  //判断是否有余额
				if (surplusInterest >= repayMoney) {  //计算垫付利息：如果待还利息大于剩余金额，则垫付金额=剩余金额，否则垫付金额=剩余利息
					moneyInterest = repayMoney;
				}else{
					moneyInterest  = surplusInterest;
				}
				moneyInterest = NumberUtils.format2(moneyInterest);
				double yesInterest = repayment.getRepaymentYesinterest();
				repayment.setRepaymentYesinterest(yesInterest + moneyInterest);
				doAccountAndLog(moneyInterest, account, getLogRemark(repayment.getBorrow())+"网站已垫付，系统自动扣款，支付利息:"+moneyInterest+"元",logList,true);
				repayMoney = repayMoney - moneyInterest;
				remark += " 利息:"+moneyInterest+"元  ";
			}
		}
		
		//扣除罚息
		double surplusLateInterest = repayment.getLateInterest() - repayment.getRepayYesLateInterest();  //获取罚息
		double moneyLate = 0;
		if (surplusLateInterest >0) { //  判断是否有罚息
			if (repayMoney >0) {  //判断是否有剩余金额
				if (surplusLateInterest >= repayMoney ) {
					moneyLate = repayMoney;
				}else{
					moneyLate = surplusLateInterest;
				}
				moneyLate = NumberUtils.format2(moneyLate);
				double yesLateInterest = repayment.getRepayYesLateInterest();
				repayment.setRepayYesLateInterest(yesLateInterest+moneyLate);
				doAccountAndLog(moneyLate, account, getLogRemark(repayment.getBorrow())+"网站已垫付，系统自动扣款，支付罚息:"+moneyLate+"元",logList,false);
				remark += "罚息:"+moneyLate+"元";
			}
		}
		return remark;
	}
	

	/**
	 * 扣款时，处理用户资金以及资金日志
	 * 备注：由于此功能涉及分步扣款，所以
	 * 将用户资金记录分开添加用户资金记录以及处理用户资金
	 * @param repayUsers  :扣款用户
	 * @param money  ：扣款金额
	 * @param logList :资金日志log
	 * @param updateNotRepay 是否更新account中的repay字段
	 */
	private void doAccountAndLog(double payMoney,Account account,String logContent ,List<AccountLog> logList,boolean updateRepay){
		User repayUser = account.getUser();
		if(updateRepay){
			accountDao.updateAccount(-payMoney, -payMoney, 0, 0,-payMoney, repayUser.getUserId());
		}else{
			accountDao.updateAccount(-payMoney, -payMoney, 0, repayUser.getUserId());
		}
		//添加资金记录
		AccountLog log = new AccountLog(repayUser.getUserId(),Constant.WEB_DEDUCT,Constant.ADMIN_ID,"", "");
		log.setMoney(payMoney);
		log.setTotal(account.getTotal());
		log.setUseMoney(account.getUseMoney());
		log.setNoUseMoney(account.getNoUseMoney());
		log.setCollection(account.getCollection());
		log.setRepay(account.getRepay());
		log.setRemark(logContent);
		logList.add(log);
	}

	@Override
	public WebPaid getWebPaidByRepayIdAndBorrowId(long repayId, long borrowId) {
		return webPaidDao.getWebPaidByBorrowIdAndRepayId(borrowId, repayId);
	}

	@Override
	public PageDataList<WebRepayLog> getRepayWebLog(SearchParam param) {
		return webRepayLogDao.findPageList(param);
	}
	
	
	

}
