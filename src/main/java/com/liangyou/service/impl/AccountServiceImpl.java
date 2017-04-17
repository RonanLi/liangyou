package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.api.ips.IpsHelper;
import com.liangyou.api.pay.DeductSign;
import com.liangyou.api.pay.SignmanyBank;
import com.liangyou.api.pay.YzzNewDeduct;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountBankDao;
import com.liangyou.dao.AccountCashDao;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.AccountWebDeductDao;
import com.liangyou.dao.AreaBankDao;
import com.liangyou.dao.AreaDao;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.dao.CompensationAccountDao;
import com.liangyou.dao.DrawBankDao;
import com.liangyou.dao.DrawBankMmmDao;
import com.liangyou.dao.GlodTransferDao;
import com.liangyou.dao.InterestGenerateDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserInvitateCodeDao;
import com.liangyou.dao.WebGlodLogDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountOneKeyRecharge;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountWebDeduct;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.CompensationAccount;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.DrawBankMmm;
import com.liangyou.domain.GlodTransfer;
import com.liangyou.domain.InterestGenerate;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.model.account.AccountLogModel;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.model.account.BorrowSummary;
import com.liangyou.model.account.CollectSummary;
import com.liangyou.model.account.InvestSummary;
import com.liangyou.model.account.RepaySummary;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.account.WebAccountSumModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiCashService;
import com.liangyou.service.ApiRechrageService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

@Service(value="accountService")
@Transactional
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {

	private Logger logger=Logger.getLogger(AccountServiceImpl.class);
	@Autowired
	AccountRechargeDao accountRechargeDao;
	@Autowired
	AccountCashDao accountCashDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AccountBankDao accountBankDao;
	@Autowired
	private DrawBankDao drawBankDao;
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private MsgService msgService;
	@Autowired 
	private BorrowTenderDao borrowTenderDao;
	@Autowired
	private AccountWebDeductDao accountWebDeductDao;
	@Autowired
	private ApiService apiService;
	@Autowired
	private GlodTransferDao glodTransferDao;
	@Autowired
	private AreaBankDao areaBankDao;
	@Autowired
	private WebGlodLogDao webGlodLogDao; 
	@Autowired
	private DrawBankMmmDao drawBankMmmDao;
	@Autowired
	private InterestGenerateDao interestGenerateDao;
	//v1.8.0.4_u4  TGPROJECT-349  qinjun  2014-07-02  start 
	@Autowired
	private RuleService ruleService;
	//v1.8.0.4_u4  TGPROJECT-349  qinjun  2014-07-02  start 
	//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 start
	@Autowired
	private RewardExtendService rewardExtendService;
	//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 end

	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	@Autowired
	private CompensationAccountDao compensationAccountDao;
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
	@Autowired
	private UserInvitateCodeDao userInvitateCodeDao;
	@Override
	public void addRecharge(AccountRecharge r) {
		accountRechargeDao.save(r);
	}
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private ApiRechrageService apiAccountService;
	
	@Autowired
	private ApiCashService apiCashService;
	
	//v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 start
	public AccountBank getAccountBankByCardNo(String cardNo, User user){
		return accountBankDao.getAccountBankByCardNo(cardNo, user);
	}
	//v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 end
	/**
	 * 获取用户逇账户详情
	 */
	public UserAccountSummary getUserAccountSummary(long user_id) {
		UserAccountSummary  uas=new UserAccountSummary();

		//账户金额信息
		User user =  userDao.find(user_id);
		Account act=user.getAccount();
		//----------------------账户总额=净资产+待还总额
		uas.setAccountTotal(act.getTotal());
		//----------------------可用余额
		uas.setAccountUseMoney(act.getUseMoney());
		//----------------------冻结总额
		uas.setAccountNoUseMoney(act.getNoUseMoney());
		//----------------------待收总额
		uas.setCollectTotal(act.getCollection());
		//----------------------待还总额
		uas.setRepayTotal(act.getRepay());   

		//还款情况
		RepaySummary repay=accountDao.getRepaySummary(user_id);
		uas.setRepayInterest(repay.getRepayInterest());//待还利息总和
		//----------------------最近待还时间
		uas.setNewestRepayDate(repay.getRepayTime());
		//----------------------最近待还总额
		uas.setNewestRepayMoney(repay.getRepayAccount());
		uas.setRecentlyRepaymentTotal(repay.getRepayTotal());//最近待还本息
		uas.setLateTotal(repay.getLateTopay()); //逾期未还款总金额
		uas.setLateRepayed(repay.getLateRepayed());//迟还款总金额
		uas.setYesRepayTotal(repay.getHasRepayed());//已经还款金额

		//借款情况
		BorrowSummary borrow=accountDao.getBorrowSummary(user_id);
		//----------------------借款总额
		uas.setBorrowTotal(borrow.getBorrowTotal());
		uas.setBorrowInterest(borrow.getBorrowInterest());
		uas.setBorrowTimes(borrow.getBorrowTimes());//成功借款多少次

		//投资情况(成功投资情况)
		InvestSummary i=accountDao.getInvestSummary(user_id);
		//----------------------投资总额
		uas.setInvestTotal(i.getInvestTotal());
		uas.setInvestInterest(i.getInvestInterest());//成功投资产生的利息
		uas.setInvestTimes(i.getInvestTimes());//成功投资次数

		//待收情况
		CollectSummary collect=accountDao.getCollectSummary(user_id);
		//----------------------最近待收总额
		uas.setNewestCollectMoney(collect.getCollectMoney());//最近待收本息
		uas.setNewestCollectInterest(collect.getCollectInterest());//最近待收利息
		//----------------------最近待收时间
		uas.setNewestCollectDate(collect.getCollectTime());
		//待收总额前边已经算过，这里不再添加
		//----------------------待收利息
		uas.setCollectInterestTotal(collect.getCollectInterestTotal());
		//----------------------净资产
		uas.setAccountOwnMoney(NumberUtils.format2(act.getTotal()-act.getRepay()));
		//充值
		uas.setRechargeTotal(accountDao.getChargeTotal(user_id));
		//提现
		uas.setCashTotal(accountDao.getCacheTotal(user_id));

		//wsl 满标前补偿金功能【心意贷】2014-09-01 start
		CompensationAccount ta = compensationAccountDao.getCompensationAccountByUser(new User(user_id));
		if(ta != null){
			uas.setCollectionCompensation(ta.getCollectionCompensation());
			uas.setCollectionYesCompensation(ta.getCollectionYesCompensation());
			uas.setRepayCompensation(ta.getRepayCompensation());
			uas.setRepayYesCompensation(ta.getRepayYesCompensation());
		}
		//wsl 满标前补偿金功能【心意贷】2014-09-01 end

		int[] countArray = accountDao.getCountRusultBySql(user_id);
		uas.setTenderingCount(countArray[0]);//正在投标的笔数
		uas.setWaitCollectCount(countArray[1]);//等待回款
		uas.setHasCollectCount(countArray[2]);//已经回款
		uas.setWaitRepayCount(countArray[3]);//等待还款
		uas.setHadRepayCount(countArray[4]);//已还款的笔数

		double[] yesIncoming = accountDao.getUserIncoming(user_id);
		//----------------------总收益
		uas.setHasCollectInterestTotal(yesIncoming[0]);
		//----------------------已收总额
		uas.setHasCollectTotal(yesIncoming[1]);//已收本金总和

		//投标奖励总和
		double awardTotal = accountDao.getSumAwardDeductByUser(user_id);
		uas.setAwardTotal(awardTotal);

		//满标待审投资情况
		double[] tenderArray = accountDao.getWaitFullSummary(user_id);//等待复审或者满标投标个数
		//v1.8.0.4_u3  TGPROJECT-339  qinjun 2014-06-24  start
		uas.setWaitFullBorrowTenderCount((int)tenderArray[0]);//等待复审或者满标投标总额
		//v1.8.0.4_u3  TGPROJECT-339  qinjun 2014-06-24  end
		uas.setWaitFullBorrowTenderTotal(tenderArray[1]);

		return uas;
	}
	@Override
	public WebAccountSumModel getWebAccountSumModel(){
		return accountDao.getWebAccountSumModel();
	}
	@Override
	public void doRechargeTask(RechargeModel re,AccountLog log){
		int apiType = Global.getInt("api_code");
		//cancel by lxm 2017-2-13 11:21:35
//		if(apiType ==1){	//汇付
//			apiAccountService.doChinapnrRecharge(re);
//		}else if(apiType ==2){	//易极付
//			apiAccountService.doYjfRecharge(re,log, log.getRemark());
//		}else 
		if(apiType ==3){	//乾多多
			apiAccountService.doMmmRecharge(re,log);
		}
//		else if (apiType ==4) {  //环迅单独处理
//			apiAccountService.doIpsRechrege(re);
//		}
		else{
			logger.info("系统接口参数错误----------------");
			failRecharge(re, log, log.getRemark());
			throw new BussinessException("充值失败！");
		}
	}

	@Override
	public void cashCallBack(AccountCashModel cashModel,BorrowParam param){
		logger.info("取现回调进入业务方法----");
		AccountCash dbCash=accountCashDao.getAccountCashByOrderNo(cashModel.getOrderId());   //获取取现记录,回调中处理根据订单查询
		if(dbCash==null){
			logger.info("订单不存在，请查询数据库");
			return;
		}
		if( dbCash.getStatus() !=4){
			if(!param.getResultCode().trim().equals("89")){
				logger.info("业务处理状态不对，orderNo:" + dbCash.getOrderNo() +"status:" + dbCash.getStatus());  
				return;
			}
		}
		AccountLog log = new AccountLog();
		AccountCash cash = new AccountCash();
		cash=accountCashDao.getAccountCashByOrderNo(cashModel.getOrderId());   //获取取现记录,回调中处理根据订单查询
		int apiType = Global.getInt("api_code");
		logger.info("接口类型"+apiType);
		cash.setLoanNo(cashModel.getLoanNo());
		//cancel by lxm 2017-2-13 11:26:27
//		if(apiType ==1){	//汇付
//			apiCashService.doHfCashService(cashModel, cash, log);
//		}else if(apiType ==2){	//易极付
//			apiCashService.doYjfCashService(cashModel, cash, log);
//		}else 
		if(apiType ==3){	//乾多多
			apiCashService.doMmmCashService(cashModel, log, param);
		}
//		else if (apiType ==4) {  //环迅单独处理
//			apiCashService.doIpsCashService(cashModel, log, param, cash);
//		}
		else{
			logger.info("系统接口参数错误----------------");
			throw new BussinessException("取现失败！");
		}
		accountCashDao.update(cash);
		Account act = accountDao.getAcountByUser(cash.getUser());
		User user =userDao.find(cash.getUser().getUserId()); 
		log.setMoney(cash.getTotal());
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		log.setRepay(act.getRepay());
		log.setUser(user);
		log.setAddtime(new Date());
		log.setToUser(new User(Constant.ADMIN_ID));
		accountLogDao.save(log);
		
	}
	
	/**
	 * 充值失败
	 * @param re
	 * @param log
	 */
	public void failRecharge(RechargeModel rem,AccountLog log , String params) {
		AccountRecharge existRecharge=accountRechargeDao.getRechargeByTradeno(rem.getOrderId());
		if(existRecharge==null){
			return ;
		}
		existRecharge.setReturnTxt(params);
		existRecharge.setTradeNo(rem.getSerialNo());
		if(existRecharge.getStatus()==0){
			accountRechargeDao.updateRecharge(2,rem.getResultMsg(), rem.getOrderId(),rem.getSerialNo());
		}
	}

	@Override
	public PageDataList<Account> getAccontList(SearchParam param) {
		return accountDao.findPageList(param);
	}

	@Override
	public List<Account> getSumAccontList(SearchParam param) {
		PageDataList<Account> pageList =  accountDao.findAllPageList(param);
		return pageList.getList();
	}

	@Override
	public List<AccountLog> getSumAccontLogList(SearchParam param) {
		PageDataList<AccountLog> pageList =  accountLogDao.findAllPageList(param);
		return pageList.getList();
	}
	@Override
	public PageDataList<AccountLog> getAccontLogList(SearchParam param) {
		return accountLogDao.findPageList(param);
	}
	@Override
	public List<AccountCash> getSumCashList(SearchParam param) {
		return accountCashDao.findAllPageList(param).getList();
	}
	/*@Override
	public List<InvestSummary>  getInvestSummaryList(int type){
		return accountDao.getInvestSummaryList(type);
	}*/
	@Override
	public PageDataList<AccountCash> getCashList(SearchParam param) {
		return accountCashDao.findPageList(param);
	}
	@Override
	public List<AccountRecharge> getSumRechargeList(SearchParam param) {
		PageDataList<AccountRecharge> pageList = accountRechargeDao.findAllPageList(param);
		return pageList.getList();
	}
	@Override
	public PageDataList<AccountRecharge> getRechargeList(SearchParam param) {
		return accountRechargeDao.findPageList(param);
	}
	@Override
	public PageDataList<AccountWebDeduct> getWebDeductList(SearchParam param) {
		return accountWebDeductDao.findPageList(param);
	}
	@Override
	public Account getAccountByUser(User u) {
		return accountDao.findByPropertyForUnique("user", u);
	}
	@Override
	public List<AccountBank> getBankListByUser(User u) {
		return accountBankDao.findByProperty("user", u);
	}
	@Override
	public void addAccountBank(Object object) {
		AccountBank ab = apiService.addAccountBank(object);
		accountBankDao.save(ab);		
	}
	@Override
	public PageDataList<AccountBank> getAccountBankList(SearchParam param) {
		return accountBankDao.findPageList(param);
	}
	@Override
	public AccountBank getAccountBankById(int id) {
		return accountBankDao.find(id);
	}
	@Override
	public AccountWebDeduct getAccountWebDeductById(int id) {
		return accountWebDeductDao.find(id);
	}
	@Override
	public void addOrUpdateAccountBank(AccountBank ab) {
		accountBankDao.merge(ab);
	}
	@Override
	public void delAccountBankById(int id) {
		accountBankDao.delete(id);
	}
	@Override
	public AccountBank getAccontBankByAccount(String account) {
		return accountBankDao.findByPropertyForUnique("account", account);
	}

	@Override
	public boolean  yjfNewCash(AccountCash accountCash,BorrowParam param) {		
		List<Object> taskList = new ArrayList<Object>();
		User user =userDao.find(accountCash.getUser().getUserId());  //重新获取user
		double maxCash=NumberUtils.getDouble(Global.getValue("most_cash"));
		double money =  accountCash.getTotal(); //提现金
		if(money>maxCash){
			throw new BussinessException("提现金额不能大于"+maxCash+", 请您联系客服");
		}
		AccountLog log = new AccountLog(user.getUserId(),Constant.CASH_FROST,Constant.ADMIN_ID,
				DateUtils.getNowTimeStr(),param.getIp());
		//执行取现操作
		String cashIsVerify = Global.getValue("cash_is_verify"); //获取取现是否需要校验
		//v1.8.0.4_u1 TGPROJECT-378 wsl 2014-07-31 start
		Rule qcRule = ruleService.getRuleByNid("quick_cash_rule");
		if(qcRule!=null && qcRule.getStatus() == 1 && qcRule.getValueIntByKey("daily") == 1){//有此规则，并且启用
			checkDailyCash(user,accountCash,money,qcRule,taskList);	
		}else{
			if (cashIsVerify.equals("1")){  //取现不需要审核触发易极付 
				dealAccountCash(accountCash, accountCash.getTotal(), taskList);//易极付取现查询银行卡是否正确
				if(borrowService.doApiTask(taskList)){//处理提现业务
					logger.info("提现订单号："+ ((YjfPay)taskList.get(0)).getOrderno());
					accountCash.setOrderNo(((YjfPay)taskList.get(0)).getOrderno());//将订单号存入
					accountCash.setStatus(4);//将状态设置为临时状态，待易极付回调处理
					accountCash.setDealStatus(4);//申请取现成功。					
				}else{
					throw new BussinessException("提现失败");
				}
			}else{
				accountCash.setStatus(0);//取现需要审核将状态设为0
			}
		}
		//v1.8.0.4_u1 TGPROJECT-378 wsl 2014-07-31 end
		log.setRemark("提现冻结");
		accountDao.updateAccount(0, -accountCash.getTotal(), accountCash.getTotal(), accountCash.getUser().getUserId());
		//保存提现申请
		accountCashDao.update(accountCash);
		Account account = accountDao.getAcountByUser(new User(accountCash.getUser().getUserId()));
		//保存资金记录
		log.setMoney(money);
		log.setTotal(account.getTotal());
		log.setUseMoney(account.getUseMoney());
		log.setNoUseMoney(account.getNoUseMoney());
		log.setCollection(account.getCollection());
		log.setRepay(account.getRepay());
		accountLogDao.save(log);
		return true;
	}

	@Override
	public void glodCashSuccess(CashOut cash,WebGlodLog log){
		logger.info("金账户取现需要复审   开始111------");
		int sum = webGlodLogDao.sumWebLogByOrdid(cash.getOrdId());
		if(sum>0){
			logger.info("已经处理过的提现："+ cash.getOrdId());
			return;
		}
		String cashIsVerify = Global.getValue("cash_is_verify"); //获取取现是否需要校验
		//boolean reflag = true ;
		String reflag = "";
		if("000".equals(reflag)){
			logger.info("金账户取现复审 成功插入记录....");
			log.setStatus(1);
			webGlodLogDao.save(log);
		}
	}
	//v1.8.0.3 TGPROJECT-64    qj     2014-04-16 start
	@Override
	public void webGlodLogBack(WebGlodLog log){
		List list = webGlodLogDao.findByProperty("ordId", log.getOrdId());
		if(list == null || list.size() == 0){
			webGlodLogDao.save(log);
		}else{
			logger.info("已经添加了金账户记录");
		}
	}
	//v1.8.0.3 TGPROJECT-64    qj     2014-04-16 stop

	@Override
	public AccountCash getAccountCash(long id) {
		return accountCashDao.find(id);
	}

	/**
	 * 处理提现的业务
	 * @param cash
	 */
	@Override
	public void dealAccountCash(AccountCash cash,double money, List<Object> taskList){
		User drawUser = userDao.find(cash.getUser().getUserId());
		AccountBank accountBank = accountBankDao.find(cash.getAccountBank().getId());   //获取取现所选择的银行卡
		DrawBank  drawBank = accountBank.getBank();  //获取银行卡的详情信息
		AreaBank province = accountBank.getProvince();//省
		AreaBank city = accountBank.getCity(); //市区
		if(province==null || city==null){
			throw new ManageBussinessException("开户银行地址不详细!","");
		}
		//统计 成功取现的个数			
		int cashNum = accountDao.getSuccessAccountCash(drawUser.getUserId());
	}

	/**
	 * 提现审核   通过， 拒绝
	 */
	@Override
	public boolean verifyCash (AccountCash cash, AccountLog log) {
		boolean result = false;
		List<Object> taskList = new ArrayList<Object>();// 任务
		AccountCash ac = accountCashDao.find(cash.getId());
		User drawUser = ac.getUser();
		double money = ac.getFee() + ac.getTotal();//提现金额+手续费，易极付，手续费默认为0
		if(cash.getStatus()==1){//审核通过
			money = apiService.verifyCash(cash, taskList);
			if(cash.getStatus() ==2){ // 第三方处理失败
				//解冻
				cash.setVerifyRemark(Global.getValue("api_name")+"处理失败，提现审核失败，返回冻结资金");
				accountDao.updateAccount(0, money, -money, cash.getUser().getUserId());
				log.setRemark(Global.getValue("api_name")+"处理失败，提现审核失败，返回冻结资金"+cash.getTotal()+" 元");
				log.setType(Constant.CASH_FAIL);

				Account act = accountDao.getAcountByUser(drawUser);
				log.setAddtime(new Date());
				log.setMoney(money);
				log.setTotal(act.getTotal());
				log.setUseMoney(act.getUseMoney());
				log.setNoUseMoney(act.getNoUseMoney());
				log.setCollection(act.getCollection());
				log.setRepay(act.getRepay());
				accountLogDao.save(log);

			}else{
				//易极付处理取现结果在回调用处理：
				int apiCode = Global.getInt("api_code");
				if (apiCode ==1 || apiCode ==3) {//汇付处理体现的方法。
					cash.setVerifyRemark(Global.getValue("api_name")+"处理成功，取现审核成功！");
					accountDao.updateAccount(-money, 0, -money, cash.getUser().getUserId());
					log.setRemark(Global.getValue("api_name")+"处理成功，提现审核成功，扣除金额"+cash.getTotal()+"元和手续费"+cash.getFee()+ "元");
					log.setType(Constant.CASH_SUCCESS);

					Account act = accountDao.getAcountByUser(drawUser);
					log.setAddtime(new Date());
					log.setMoney(money);
					log.setTotal(act.getTotal());
					log.setUseMoney(act.getUseMoney());
					log.setNoUseMoney(act.getNoUseMoney());
					log.setCollection(act.getCollection());
					log.setRepay(act.getRepay());
					accountLogDao.save(log);
				}else{
					logger.info("易极付取现复审操作不做资金处理处理，只能在回调中做业务处理");
					cash.setStatus(4);//等待回调处理
				}
				result= true;//到此易极付和 汇付都成功。
			}
		}else{//审核拒绝
			//解冻
			accountDao.updateAccount(0, money, -money, cash.getUser().getUserId());
			Account act = accountDao.getAcountByUser(drawUser);
			log.setRemark("提现审核失败，返回冻结资金"+money+" 元");
			log.setType(Constant.CASH_FAIL);
			log.setAddtime(new Date());
			log.setMoney(money);
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			accountLogDao.save(log);
		}
		accountCashDao.update(cash);
		return result;
	}

	@Override
	public List getDrawBankBySearchParam(SearchParam param) {
		return drawBankDao.findByCriteria(param);		
	}

	/**
	 * 双乾支查询银行卡
	 * @param param
	 * @return
	 */
	@Override
	public List getDrawBankMmmBySearchParam(SearchParam param) {
		return drawBankMmmDao.findByCriteria(param);		
	}

	@Override
	public DrawBank getDrawBankById(int id) {
		return drawBankDao.find(id);
	}

	@Override
	public DrawBankMmm getDrawBankMmmById(int id) {
		return drawBankMmmDao.find(id);
	}

	@Override
	public void delAccountCashById(int id) {
		accountCashDao.delete(id);
	}
	@Override
	public void updateAccountCash(AccountCash ac) {
		accountCashDao.merge(ac);
	}
	@Override
	public void cancelCash(Account act,AccountCash cash, AccountLog log) {

		cash.setStatus(2);
		updateAccountCash(cash);

		double money = cash.getTotal()+cash.getFee();
		accountDao.updateAccount(0, money, -money, cash.getUser().getUserId());
		log.setMoney(money);
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		accountLogDao.save(log);		
	}

	/**
	 * 解除绑定银行卡
	 * @param user
	 * @param accountBankId
	 */
	@Override
	public void accountBankRemove(User user, String accountBankId){
		AccountBank ab = accountBankDao.find(Integer.parseInt(accountBankId));
		ab.setStatus(0);
		accountBankDao.update(ab);
	}
	@Override
	public AccountRecharge getAccountRechargeById(int id){
		return accountRechargeDao.find(id);
	}

	@Override
	public void webRecharge(AccountLog log,AccountRecharge ar) throws  Exception {
		List<Object> taskList = new ArrayList<Object>();
		int apiType = Global.getInt("api_code");//3，乾多多
		User rechargeUser = ar.getUser();//被充值用户
		if(rechargeUser.getApiStatus()==0 || StringUtils.isBlank(rechargeUser.getApiId())){
			throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！", "/admin/account/rechargelist.html");
		}
		apiService.webRecharge(ar,taskList);//第三方接口
		if(borrowService.doApiTask(taskList)){//调用转账功能
			//修改账户金额
			Account act=accountDao.getAcountByUser(rechargeUser);
			accountDao.updateAccount(ar.getMoney(), ar.getMoney(), 0,rechargeUser.getUserId() );
			//修改订单状态
			ar.setStatus(1);
			String tradeNo = "";
			switch (apiType) {
			//cancel by lxm 
//			case 1://汇付
//				ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
//				tradeNo = cpm.getOrdId();
//				break;
//			case 2://易极付
//				YjfPay yjf = (YjfPay)taskList.get(0);
//				tradeNo = yjf.getOrderno();
//				break;

			default:
				tradeNo = OrderNoUtils.getInstance().getSerialNumber();
				break;
			}

			ar.setTradeNo(tradeNo);
			ar.setRemark("网站代理充值成功!");
			accountRechargeDao.save(ar);

			//插入资金记录表
			log.setUser(ar.getUser());
			User toUser = new User();
			log.setToUser(toUser);
			toUser.setUserId(Constant.ADMIN_ID);
			log.setMoney(ar.getMoney());
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			accountLogDao.save(log);
			//充值成功，发消息通知
			MsgReq req = new MsgReq();
			req.setReceiver(ar.getUser());
			req.setSender(new User(Constant.ADMIN_ID));
			req.setMsgOperate(this.msgService.getMsgOperate(8));
			req.setTime(DateUtils.dateStr4(new Date()));
			req.setMoney(""+ar.getMoney());
			DisruptorUtils.sendMsg(req);
		}else{
			throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
		}
	}
	
	@Override
	public void glodTransfer(GlodTransfer gt){
		List<Object> taskList = new ArrayList<Object>();
		apiService.glodTransfer(gt,taskList);//第三方接口
		if(borrowService.doApiTask(taskList)){
			glodTransferDao.save(gt);
		}else{
			ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
			throw new ManageBussinessException("易极付处理出错!"+cpm.getErrorMsg());
		}
	}
	@Override
	public PageDataList getAllGlodTransfer(SearchParam param){
		return glodTransferDao.findPageList(param);
	}

	@Override
	public void webDeduct(AccountLog log,AccountWebDeduct awd){
		User rechargeUser = awd.getUser();
		if(rechargeUser.getApiStatus()==0 || StringUtils.isBlank(rechargeUser.getApiId())){
			throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！");
		}
		accountWebDeductDao.save(awd);
	}
	@Override
	public void verifyWebDeduct(AccountLog log,AccountWebDeduct awd){
		List<Object> taskList = new ArrayList<Object>();
		int apiType = Global.getInt("api_code");
		User deductUser = awd.getUser();
		Account act=accountDao.getAcountByUser(deductUser);
		if(deductUser.getApiStatus()==0 || StringUtils.isBlank(deductUser.getApiId())){
			throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！");
		}
		if(act.getUseMoney() < awd.getMoney()){
			throw new ManageBussinessException("用户资金不足，扣款失败！");
		}
		if(awd.getStatus() == 0 || awd.getStatus() == 2){
			accountWebDeductDao.update(awd);
			return;
		}
		apiService.webDeduct(awd,taskList);//第三方接口
		if(borrowService.doApiTask(taskList)){//调用转账功能
			//修改账户金额

			accountDao.updateAccount(-awd.getMoney(), -awd.getMoney(), 0,deductUser.getUserId() );
			//修改订单状态
			awd.setStatus(1);
			String tradeNo = "";
			switch (apiType) {
			//cancel by lxm 2017-2-13 11:28:40
//			case 1://汇付
//				break;
//			case 2://易极付
//				YjfPay yjf = (YjfPay)taskList.get(0);
//				tradeNo = yjf.getOrderno();
//				break;

			default:
				tradeNo = OrderNoUtils.getInstance().getSerialNumber();
				break;
			}
			//插入订单号
			awd.setTradeNo(tradeNo);
			accountWebDeductDao.update(awd);

			//插入资金记录表
			log.setMoney(awd.getMoney());
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			log.setRemark("网站扣款，金额："+awd.getMoney()+"元。");
			accountLogDao.save(log);
		}else{
			throw new ManageBussinessException("易极付处理出错！！！");
		}
	}

	@Override
	public double getUserNetMoney(User user) {
		double accountMoney = 0;
		double collectionMoney=borrowTenderDao.sumCollectionMoney(user.getUserId());       //获取用户所有的待收本金总和
		accountMoney=user.getAccount().getUseMoney()+collectionMoney-user.getAccount().getRepay();
		return accountMoney;
	}

	@Override
	public double getAllPropertyWaitRepayMoney(long user_id) {
		return accountDao.getAllPropertyBorrowRepayAccount(user_id);
	}

	@Override
	public List<AccountBank> getBankByUserId(long userid, int isbind) {
		return accountBankDao.getBankByUserId(userid, isbind);
	}

	@Override
	public AccountBank getBankByNoAndApiId(String no, String yid) {
		return accountBankDao.getBankByNo(no, yid);
	}

	@Override
	public void updateIsbind(AccountBank bank) {
		accountBankDao.update(bank);
	}
	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public List<AccountBank> getBankLists(long userId){
		return  accountBankDao.getAllBankList(userId);
	}

	/**
	 * 
	 * @param serialNo
	 * @return
	 */
	@Override
	public boolean checkRechargeOffLine(String serialNo){
		return  accountRechargeDao.getCountBySerialNo(serialNo)==0;
	}

	

	@Override
	public void seveCash(AccountCash cash) {
		accountCashDao.save(cash);

	}

	@Override
	public void saveWebGlodLog(WebGlodLog log){
		webGlodLogDao.save(log);
	}

	@Override
	public int sumWebLogByOrdid(String ordid){
		return webGlodLogDao.sumWebLogByOrdid(ordid);
	}

	@Override
	public PageDataList<WebGlodLog> getWebGlodLogList(SearchParam param) {
		return webGlodLogDao.findPageList(param);
	}

	/**
	 * 根据查询条件，查询出所有的记录
	 * @param param
	 * @return
	 */
	@Override
	public List<WebGlodLog> getAllGlodLogList(SearchParam param) {
		return webGlodLogDao.findByCriteria(param);
	}
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 start
	/**
	 * 查询用户投资情况
	 * @param tenderParam
	 * @param userParam
	 * @return
	 */
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public PageDataList<BaseAccountSumModel> getUserInvestDetail(SearchParam tenderParam, SearchParam userParam){
		List<BaseAccountSumModel> list = accountDao.getBaseAccountSum(tenderParam, userParam);
		return new PageDataList(userParam.getPage(),list);
	}
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 end

	@Override
	public void madeDeductSign(DeductSign dedu) {
		AccountBank accountBank = accountBankDao.getBankByNo(dedu.getCardNo(), dedu.getUserId());;
		if (dedu.getIsSuccess() =="true") {
			accountBank.setStatus(1);//签约成功
		}else{
			accountBank.setStatus(2); //签约失败
		}
		logger.info("用户:"+dedu.getUserId()+",银行卡签约结果："+dedu.getMessage());
		accountBankDao.update(accountBank);
	}
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	@Override
	public void madeInterestGenerateCall(FssTrans fssTrans) {
		InterestGenerate ig = fillInterestGenerate(fssTrans);
		if(ig != null){
			double money = ig.getAmount();
			User user = ig.getUser();
			if(ig.getType().equals("in")){
				accountDao.updateAccount(-money, -money, 0,0, 0,user.getUserId());  // 修改金额
				Account act=accountDao.getAcountByUser(user);
				AccountLog log=new AccountLog(user.getUserId(),Constant.INTEREST_GENERATE_IN,user.getUserId());
				fillAccountLog(log, Constant.INTEREST_GENERATE_IN, act, user, user, money, 0, 
						"转入【生利宝】资金:"+money);
				accountLogDao.save(log);
			}else if(ig.getType().equals("out")){
				accountDao.updateAccount(money, money, 0,0, 0,user.getUserId());  // 修改金额
				Account act=accountDao.getAcountByUser(user);
				AccountLog log=new AccountLog(user.getUserId(),Constant.INTEREST_GENERATE_OUT,user.getUserId());
				fillAccountLog(log, Constant.INTEREST_GENERATE_OUT, act, user, user, money, 0, 
						"【生利宝】转出资金:"+money);
				accountLogDao.save(log);
			}
			interestGenerateDao.save(ig);
		}else{
			throw new BussinessException("操作异常！");
		}
	}
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	private InterestGenerate fillInterestGenerate(FssTrans fssTrans){
		InterestGenerate ig = null;
		String ordId = fssTrans.getOrdId();
		List<InterestGenerate> igList = interestGenerateDao.findByProperty("ordId", ordId);
		if(igList != null&&igList.size()==1){
			ig = igList.get(0);
			if(fssTrans.getUsrCustId().equals(ig.getUser().getApiUsercustId())&&ig.getStatus()==0){
				if(fssTrans.getTransType().equals("I")){//转入生利宝
					ig.setType("in");
				}else if(fssTrans.getTransType().equals("O")){//转出生利宝
					ig.setType("out");
				}else{
					throw new BussinessException("状态异常！");
				}
				ig.setAmount(NumberUtils.getDouble2(fssTrans.getTransAmt()));
				ig.setStatus(1);
				ig.setCalltime(new Date());
			}else if(fssTrans.getUsrCustId().equals(ig.getUser().getApiUsercustId())&&ig.getStatus()==1){
				throw new BussinessException("操作成功！");
			}else{
				throw new BussinessException("操作异常！");
			}
		}else{
			throw new BussinessException("操作异常！");
		}
		return ig;
	}
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	@Override
	public void addInterestGenerate(InterestGenerate ig) {
		interestGenerateDao.save(ig);
	}
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end
	//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  start
	@Override
	public double getHuifuServFee(AccountCash accountCash) {
		double fee = 0;
		User user = accountCash.getUser();
		double cashMoney = accountCash.getTotal();
		double rechargeTotal = accountDao.getChargeTotal(user.getUserId());
		double tenderTotal = accountDao.getInvestSummary(user.getUserId()).getInvestTotal();
		Rule cashRule = ruleService.getRuleByNid("huifu_cash_servfee");
		if(cashRule!= null && cashRule.getStatus() == 1){
			int yddStatus = cashRule.getValueIntByKey("ydd");
			if(yddStatus == 1){
				double ruleCashMoney = cashRule.getValueDoubleByKey("cash_money");
				double ruleCashFee = cashRule.getValueDoubleByKey("cash_fee");
				double ruleCashTenderRate = cashRule.getValueDoubleByKey("cash_tender_rate");
				double ruleCashRate = cashRule.getValueDoubleByKey("cash_rate");
				//v1.8.0.4_u4  TGPROJECT-369  qinjun  2014-07-16  start
				double cashLowestMoney = cashRule.getValueDoubleByKey("cash_lowest_money");
				logger.info("(用户:"+user.getUsername()+",提现金额："+cashMoney+",充值总金额:"+rechargeTotal+",投标总金额:"+tenderTotal
						+"),(rule参数配置：取现金额判断："+ruleCashMoney+",取现金额判断费："+ruleCashFee+"，比较投资金额大用户的费率："+ruleCashTenderRate+",小的投资金额费率："+ruleCashRate
						+"小于"+cashLowestMoney+"提现费率"+ruleCashFee);
				if(cashMoney<cashLowestMoney){//小于500，收费标准=提现金额*0.25%
					fee = cashMoney * ruleCashRate;
					logger.info("小于"+cashLowestMoney+"提现费:"+fee);
				}else{
					//v1.8.0.4_u4  TGPROJECT-369  qinjun  2014-07-16  start
					//一、累计投资金额≥充值金额 1）提现金额≤3万元，费用=3元； 2）提现金额>3万元，费用=提现金额*0.01%
					if(tenderTotal>=rechargeTotal){
						if(cashMoney<=ruleCashMoney){
							fee = ruleCashFee;
							logger.info("累计投资金额≥充值金额,提现金额≤3万元，费用=3元,提现费："+fee);
						}else{
							fee = cashMoney * ruleCashTenderRate;
							logger.info("累计投资金额≥充值金额,提现金额>3万元，费用=提现金额*0.01%,提现费："+fee);
						}
					}else{
						//累计投资金额<充值金额1）提现金额≤累计投资金额a. 提现金额≤3万元，费用=3元；b. 提现金额>3万元，费用=提现金额*0.01%；
						//2）提现金额>累计投资金额 a.提现金额≤3万元，费用=3元+（提现金额-累计投资金额）*0.25%； b.提现金额>3万元，费用=提现金额*0.01%+（提现金额-累计投资金额）*0.25%
						if(cashMoney<=tenderTotal){
							if(cashMoney<=ruleCashMoney){
								fee = ruleCashFee;
								logger.info("累计投资金额<充值金额,提现金额≤累计投资金额a. 提现金额≤3万元，费用=3元,提现费："+fee);
							}else{
								fee = cashMoney * ruleCashTenderRate;
								logger.info("累计投资金额<充值金额,提现金额≤累计投资金额b. 提现金额>3万元，费用=提现金额*0.01%；提现费："+fee);
							}
						}else{
							if(cashMoney<=ruleCashMoney){
								fee = ruleCashFee + (cashMoney-tenderTotal) * ruleCashRate;
								logger.info("累计投资金额<充值金额,提现金额>累计投资金额 a.提现金额≤3万元，费用=3元+（提现金额-累计投资金额）*0.25%,提现费："+fee);
							}else{
								fee = cashMoney * ruleCashTenderRate + (cashMoney-tenderTotal)*ruleCashRate;
								logger.info("累计投资金额<充值金额,提现金额>累计投资金额 b.提现金额>3万元，费用=提现金额*0.01%+（提现金额-累计投资金额）*0.25%,提现费："+fee);
							}
						}
					}
				}
			}
		}else{
			fee = cashMoney*Global.getDouble("serv_fee_rate");
		}
		return fee;
	}
	//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  end


	//TGPROJECT-362   无卡签约   start
	@Override
	public void doSignmanyBank(SignmanyBank signBank) {
		String cardNo = signBank.getCardNo();   //获取银行卡
		String userId = signBank.getUserId();
		AccountBank accountBank = accountBankDao.getBankByNo(cardNo, userId);
		if (signBank.getIsSuccess().equals("true")) { //判断是否签约成功
			accountBank.setStatus(1);//签约成功
		}else{
			accountBank.setStatus(2); //签约失败
			logger.info("签约失败");
		}
		logger.info("用户:"+signBank.getUserId()+",银行卡签约结果："+signBank.getMessage());
		accountBankDao.update(accountBank);
	}
	//TGPROJECT-362   无卡签约   end
	//TGPROJECT-410 wsl 2014-09-03 start
	@Override
	public double getAccountCashDailySum(long userId) {
		return accountCashDao.getAccountCashDailySum(userId);
	}
	//TGPROJECT-410 wsl 2014-09-03 end

	/**
	 * 校验提现用户当日提现总额限制
	 * @param accountCash
	 * @param money
	 * @param qcRule
	 * @param taskList
	 */
	private void checkDailyCash(User user,AccountCash accountCash,double money,Rule qcRule,List<Object> taskList){

		double quickCash =qcRule.getValueDoubleByKey("quick_cash");
		double dailyMostCash = Global.getDouble("daily_most_cash");
		double lowestCash = Global.getDouble("lowest_cash");
		double dailySum = getAccountCashDailySum(user.getUserId());
		double dailySurplusCash = dailyMostCash - dailySum;

		if(money<quickCash && dailySurplusCash >= lowestCash && money < dailySurplusCash){//信合行直接提现不需要后台审核
			dealAccountCash(accountCash, accountCash.getTotal(), taskList);//易极付取现查询银行卡是否正确
			if(borrowService.doApiTask(taskList)){//处理提现业务
				logger.info("提现订单号："+ ((YjfPay)taskList.get(0)).getOrderno());
				accountCash.setOrderNo(((YjfPay)taskList.get(0)).getOrderno());//将订单号存入
				accountCash.setStatus(4);//将状态设置为临时状态，待易极付回调处理
				accountCash.setDealStatus(4);//申请取现成功。					
			}else{
				throw new BussinessException("提现失败");
			}
		}else{
			accountCash.setStatus(0);//取现需要审核将状态设为0
		}
	}
	@Override
	public void doIpsAddBank(User user, AccountBank bank) {
		logger.info("进入环迅添加银行卡："+bank.getAccount()+"银行卡名称："+bank.getBranch());
		//校验是否已经将用户的银行卡插入到数据库中
		AccountBank accountbank = accountBankDao.getAccountBankByCardNo(bank.getAccount(), user);
		if (accountbank !=null) {
			logger.info("用户id:"+user.getUserId()+"，已经绑定银行卡:"+bank.getAccount());
			return;
		}
		//将银行卡中文名称转换为环迅银行代号
		Map<String, String> bankMap  = new HashMap<String, String>();
		try {
			bankMap = IpsHelper.queryRechargeBank();
			String bankCode = bankMap.get(bank.getBranch());
			bank.setUser(user);
			bank.setAddtime(new Date());
			bank.setBranch(bankCode);
			bank.setStatus(1);
			accountBankDao.save(bank);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}
	
	
	public void dealDeductRecharge(AccountRecharge recharge, AccountLog log) throws Exception{
		User user = recharge.getUser();
		double fee = recharge.getFee();
		recharge.setReturnTxt("");
		//修改账户金额
		Account act=accountDao.getAcountByUser(user);
		int rechargeWeb = Global.getInt("recharge_web");  //获取平台是否自己垫付充值手续费,1是垫付，0是不垫付
		double rechargeMoney = 0;
		String remark = "";
		if(rechargeWeb ==1) { 
			//平台垫付充值手续费,不收取任何手续费
			rechargeMoney=recharge.getMoney();
			remark = "网站垫付手续费：" + NumberUtils.format2(fee) +"元";
		}else{//从用户自己账户扣款
			rechargeMoney = NumberUtils.format2(recharge.getMoney() - fee);
			remark = "扣除手续费：" + NumberUtils.format2(fee) +"元" ;
		}
		recharge.setRemark(remark);
		//跟新账户信息
		accountDao.updateAccount(rechargeMoney, rechargeMoney, 0, user.getUserId() );
		//修改订单状态
		recharge.setStatus(1);//设置状态
		accountRechargeDao.update(recharge);
		
		//插入资金记录表
		log.setUser(user);
		logger.info("充值回调用户Id是"+user.getUserId());
		User toUser = new User();
		toUser.setUserId(Constant.ADMIN_ID);
		log.setToUser(toUser);
		log.setMoney(rechargeMoney);
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		log.setRepay(act.getRepay());
		log.setRemark(remark);
		accountLogDao.save(log);
		
		//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 start
		//第一次充值成功送红包
		rewardExtendService.rechargeExtendRedPacket(user,rechargeMoney);
		//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 end
		//充值成功，发消息通知
		MsgReq req = new MsgReq();
		req.setReceiver(recharge.getUser());
		req.setSender(new User(Constant.ADMIN_ID));
		req.setMsgOperate(this.msgService.getMsgOperate(8));
		req.setTime(DateUtils.dateStr4(new Date()));
		req.setMoney(""+rechargeMoney);
		DisruptorUtils.sendMsg(req);
	}
	
	
	@Override
	public void deductRechargeOffLine(YzzNewDeduct yzznewdeduct)
			throws Exception {
		//查询原有订单
		AccountRecharge recharge = accountRechargeDao.getRechargeByTradeno(yzznewdeduct.getOutBizNo());
		User user = recharge.getUser();//此处aciton中已经保存		
		
		if(recharge.getStatus() == 0 || recharge.getStatus() == 2 ){//订单重复处理业务逻辑判断
			
			AccountLog log = new AccountLog(user.getUserId(), Constant.RECHARGE, Constant.ADMIN_ID, "代扣充值", "127.0.0.1");
			
			String orderNo = yzznewdeduct.getOutBizNo(); //订单号。
			logger.info( "代扣充值，异步处理订单号：" + orderNo + " 金额：" + yzznewdeduct.getAmount() + " 结果：" + 
			yzznewdeduct.getResultCode() + " 流水号：" + yzznewdeduct.getPayNo() );
			log.setRemark("代扣充值,"+Global.getValue("api_name")+"充值,订单号:"  +  orderNo);
			
			if ("DEPOSIT_SUCCESS".equals(yzznewdeduct.getResultCode())) {
				//充值成功
				double fee = NumberUtils.getDouble2(yzznewdeduct.getAmount())- NumberUtils.getDouble2(yzznewdeduct.getAmountIn()) ;
				recharge.setReturnTxt("");//返回参数
				recharge.setSerialNo(yzznewdeduct.getPayNo());//返回流水号
				recharge.setFee(fee);
				//业务处理
				dealDeductRecharge(recharge, log);
			}else if("BIZ_PROCESSING".equals(yzznewdeduct.getResultCode())){
				logger.info( "订单号：" + yzznewdeduct.getOrderNo() + " 金额：" + yzznewdeduct.getAmount() + " 银行处理中......" );
			}else{
				logger.info("充值失败");
			}
		}
	}
	@Override
	public AccountCash getAccountCashByLoanNo(String orderNo) {
		return accountCashDao.getAccountCashByOrderNo(orderNo);
	}
	@Override
	public void webRecharge(AccountLog log, AccountRecharge ar,
			String rechargeType) throws Exception{

		List<Object> taskList = new ArrayList<Object>();
		int apiType = Global.getInt("api_code");//3，乾多多
		User rechargeUser = ar.getUser();//被充值用户
		if(rechargeUser.getApiStatus()==0 || StringUtils.isBlank(rechargeUser.getApiId())){
			throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！", "/admin/account/rechargelist.html");
		}
		apiService.webRecharge(ar,taskList);//第三方接口
		if(borrowService.doApiTask(taskList,rechargeType)){//调用转账功能
			//修改账户金额
			Account act=accountDao.getAcountByUser(rechargeUser);
			accountDao.updateAccount(ar.getMoney(), ar.getMoney(), 0,rechargeUser.getUserId() );
			//修改订单状态
			ar.setStatus(1);
			String tradeNo = "";
			
			//如果是后台邀请奖励充值修改当前用户已领取奖励资金
			if("1".equals(rechargeType)){
				UserInvitateCode userInvitateCode = userInvitateCodeDao.findByUserId(rechargeUser.getUserId());
				userInvitateCode.setReceiveMoney(userInvitateCode.getReceiveMoney()+ar.getMoney());
				userInvitateCodeDao.update(userInvitateCode);
				logger.info("--------已更新用户已领取奖励资金金额为:-------------"+userInvitateCode.getReceiveMoney());
			}
			switch (apiType) {
			//cancel by lxm 2017-2-13 11:29:17
//			case 1://汇付
//				ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
//				tradeNo = cpm.getOrdId();
//				break;
//			case 2://易极付
//				YjfPay yjf = (YjfPay)taskList.get(0);
//				tradeNo = yjf.getOrderno();
//				break;

			default:
				tradeNo = OrderNoUtils.getInstance().getSerialNumber();
				break;
			}

			ar.setTradeNo(tradeNo);
			ar.setRemark("网站代理充值成功!");
			accountRechargeDao.save(ar);

			//插入资金记录表
			log.setUser(ar.getUser());
			User toUser = new User();
			log.setToUser(toUser);
			toUser.setUserId(Constant.ADMIN_ID);
			log.setMoney(ar.getMoney());
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			accountLogDao.save(log);
			//如果是邀请人奖励充值,发送奖励提示消息
			if(!"1".equals(rechargeType)){
				//充值成功，发消息通知
				MsgReq req = new MsgReq();
				req.setReceiver(ar.getUser());
				req.setSender(new User(Constant.ADMIN_ID));
				req.setMsgOperate(this.msgService.getMsgOperate(8));
				req.setTime(DateUtils.dateStr4(new Date()));
				req.setMoney(""+ar.getMoney());
				DisruptorUtils.sendMsg(req);
			}else {
				//add by lijing充值成功，发消息通知
				MsgReq req1 = new MsgReq();
				req1.setReceiver(ar.getUser());
				req1.setSender(new User(Constant.ADMIN_ID));
				req1.setMsgOperate(this.msgService.getMsgOperate(30));
				req1.setTime(DateUtils.dateStr4(new Date()));
				req1.setMoney(""+ar.getMoney());
				DisruptorUtils.sendMsg(req1);
			}
			
			
		}else{
			throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
		}
	
		
	}
	/*
	 *  add by lijing 2016-11-25 11:09:36后台 一键充值
	 */
	@Override
	public String webBatchRecharge(String rechargeIp,String remark) {
		List<Object> taskList = new ArrayList<Object>();
		StringBuilder returnString = new StringBuilder();
		List<AccountOneKeyRecharge> recharges = accountRechargeDao.findAllRechargeUser();
		if(!Global.getBoolean("web_batch_recharge")){
			accountRechargeDao.deleteAllRecharge();
		}
		//每次操作清空数据库 上线需打开
		if(recharges == null || recharges.size() == 0) {
			throw new ManageBussinessException("当前数据库没有用户信息,请确认!");
		}
		//充值提交成功修改平台转账用户资金
		String mmmAccount = Global.getValue("plat_transfer_account");
		User transferAccount = userDao.findByPropertyForUnique("apiId", mmmAccount);
		if(transferAccount == null){
			throw new ManageBussinessException("平台转账用户参数异常，请开发人员确认！");
		}
		logger.info("==========当前充值总人数:============"+recharges.size());
		logger.info("=================批量充值开始===============:");
		//根据充值列表查询当前要充值的用户
		int i = 1;
		String generate = UUID.randomUUID().toString().replaceAll("-","");
		logger.info("生成本次充值批量号:"+generate);
		double total = 0;
		for (AccountOneKeyRecharge accountOneKeyRecharge : recharges) {
			//查询每个用户给每个用户充值
			SearchParam param = SearchParam.getInstance().addParam("phone", accountOneKeyRecharge.getPhone());
			User user = userDao.findByCriteriaForUnique(param);
			if(user.getApiStatus()==0 || StringUtils.isBlank(user.getApiId())){
				throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！");
			}
			total += accountOneKeyRecharge.getMoney();
			AccountRecharge r=new AccountRecharge();
			r.setUser(user);//被充值用户
			r.setType("2"); //后台充值
			r.setAddtime(new Date());//添加时间
			r.setAddip(rechargeIp);//添加IP
			r.setPayment("");//所属银行
			r.setMoney(accountOneKeyRecharge.getMoney());//充值金额
			r.setRemark(remark);//备注
			r.setStatus(0);//充值失败的标识(审核成功后状态为1,提交失败为2)
			r.setBatch_no(generate);//批量充值标识(其他默认为null)
			String tradeNo = "";
    		tradeNo = OrderNoUtils.getInstance().getSerialNumber();
    		r.setTradeNo(tradeNo);
			r.setVerifyRemark(remark);//网站充值备注
			accountRechargeDao.save(r);//保存该充值信息需要充值记录的id
			logger.info("成功保存当前交易:"+r.getId());
			apiService.webBatchRecharge(r,taskList);//第三方接口加入双乾审核功能
			logger.info("一键充值提交成功,用户名:"+user.getRealname()+",充值金额:"+r.getMoney()+"第"+i+"个;");
			returnString.append("一键充值,用户:"+user.getRealname()+",充值金额:"+r.getMoney()+"第"+i+"个;");
			i++;
			}
			logger.info("当前充值总金额为："+total);
			//修改转账账户用户资金 总金额 可用金额 冻结金额
			accountDao.updateAccount(0,-total, total, transferAccount.getUserId());
			logger.info("开始统一处理批量充值任务");
			boolean doApiTask = borrowService.doApiTask(taskList,"4");
			logger.info("任务统一处理结果:"+doApiTask);
			logger.info("批量充值提交结束!");
			return returnString.toString();
	}
	//add by lijing 后台收回转账
	@Override
	public void webBackRecharge(AccountLog log, AccountRecharge ar,User rechargeUser)
			throws Exception {
		List<Object> taskList = new ArrayList<Object>();
		int apiType = Global.getInt("api_code");//3，乾多多
		User user = ar.getUser();//管理员
		if(user.getApiStatus()==0 || StringUtils.isBlank(user.getApiId())){
			throw new ManageBussinessException("当前管理员账户没有开通或激活"+Global.getValue("api_name")+"账户！！", "/admin/account/rechargelist.html");
		}
		apiService.webBackRecharge(ar,taskList,rechargeUser);//第三方接口
		if(borrowService.doApiTask(taskList)){//调用转账功能
			//用户给我们自有账户转账,修改该用户资金为-Money
			accountDao.updateAccount(-ar.getMoney(), -ar.getMoney(), 0,rechargeUser.getUserId() );
			//修改订单状态
			ar.setStatus(1);
			String tradeNo = "";
			switch (apiType) {
			//cancel by lxm 2017-2-13 11:29:42
//			case 1://汇付
//				ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
//				tradeNo = cpm.getOrdId();
//				break;
//			case 2://易极付
//				YjfPay yjf = (YjfPay)taskList.get(0);
//				tradeNo = yjf.getOrderno();
//				break;

			default:
				tradeNo = OrderNoUtils.getInstance().getSerialNumber();
				break;
			}

			ar.setTradeNo(tradeNo);
			ar.setRemark("网站后台收回转账!");
			accountRechargeDao.save(ar);
		}else{
			throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
		}
	}
	//add by lijing  批量充值审核
	@Override
	public void doBatchRechargeTask(AccountRecharge accountRecharge) {
		//审核成功后修改用户账户金额
		//修改账户金额
		Account act=accountDao.getAcountByUser(accountRecharge.getUser());
		accountDao.updateAccount(accountRecharge.getMoney(), accountRecharge.getMoney(), 0,accountRecharge.getUser().getUserId());
		//修改订单状态
		accountRecharge.setStatus(1);
		String tradeNo = "";
		tradeNo = OrderNoUtils.getInstance().getSerialNumber();
		accountRecharge.setTradeNo(tradeNo);
		accountRecharge.setRemark("网站代理充值成功!");
		accountRechargeDao.save(accountRecharge);
		//插入资金记录表
		AccountLog log = new AccountLog();
		log.setUser(accountRecharge.getUser());
		User toUser = new User();
		log.setToUser(toUser);
		log.setType("web_recharge");
		log.setRemark(accountRecharge.getRemark());
		log.setAddtime(new Date());
		toUser.setUserId(Constant.ADMIN_ID);
		log.setMoney(accountRecharge.getMoney());
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		log.setRepay(act.getRepay());
		accountLogDao.save(log);
		logger.info("批量充值审核通过,后台处理成功!");
		//审核成功,给用户发送消息通知
		try {
			MsgReq req1 = new MsgReq();
			req1.setReceiver(accountRecharge.getUser());
			req1.setSender(new User(Constant.ADMIN_ID));
			req1.setMsgOperate(this.msgService.getMsgOperate(31));
			req1.setTime(DateUtils.dateStr4(new Date()));
			req1.setMoney(""+accountRecharge.getMoney());
			DisruptorUtils.sendMsg(req1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public PageDataList<AccountRecharge> getAllVerifyBR(SearchParam param) {
		
		return accountRechargeDao.findPageList(param);
	}
	@Override
	public PageDataList<AccountRecharge> getAccountRechargeByBathNo(
			String batchno) {
		SearchParam param = SearchParam.getInstance().addParam("batch_no", batchno);
		return accountRechargeDao.findPageList(param);
	}
	@Override
	public ArrayList<AccountRecharge> getAccountRechargeByBathNoAndState(String batchno) {
		SearchParam param = SearchParam.getInstance().addParam("batch_no", batchno).addParam("status", "0");
		return (ArrayList<AccountRecharge>) accountRechargeDao.findByCriteria(param);
	}
	@Override
	public void verifyBatchRecharge(List<AccountRecharge> list,String checkString) {
		DisruptorUtils.verifyBatchRecharge(list,checkString);
	}
	
}
