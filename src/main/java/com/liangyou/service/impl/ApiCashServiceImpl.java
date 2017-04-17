package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmQueryCashBalance;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountBankDao;
import com.liangyou.dao.AccountCashDao;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.service.ApiCashService;
import com.liangyou.service.ApiRechrageService;
import com.liangyou.service.RuleService;
import com.sun.tools.javac.util.Name;

/** 
*未经授权不得进行修改、复制、出售及商业使用。
* @ClassName:RDtuoguan_P2P
* @Description: 第三方接口取现
* <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
* @author wujing  wj@erongdu.com
* @date 2014-11-1 上午9:22:27 
* @version:1.0 
*/
@Service(value="apiCashService")
@Transactional
public class ApiCashServiceImpl implements ApiCashService {
	private Logger logger = Logger.getLogger(ApiRechrageService.class);
	@Autowired
	private RuleService ruleService;
	@Autowired
	private AccountRechargeDao accountRechargeDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AccountCashDao accountCashDao;
	@Autowired
	private AccountLogDao acountlAccountLogDao;
	@Autowired
	private AccountBankDao accountBankDao;
	
	@Override
	public void doIpsCashService(AccountCashModel cashModel,AccountLog log,BorrowParam param,AccountCash cash) {
		logger.info("进入提现处理----------------------");
		cash = accountCashDao.getAccountCashByOrderNo(cashModel.getOrderId()); 
		String orderId = cash.getOrderNo(); 
		if(cash == null || cash.getStatus() != 4){
			logger.info("提现拦截，取现状态："+cash.getStatus());
			throw new BussinessException("取现回调处理状态异常status："+cash.getStatus());
		}
		double cashMoney = cash.getTotal();
		log.setUser(cash.getUser());
		log.setToUser(new User(1));
		log.setType(Constant.CASH_SUCCESS);
		if(cash.getStatus()==4){
			logger.info("提现成功回调，扣除提现款。");
			logger.info(Global.getValue("api_name")+"提现申请处理成功,order: " + orderId + " money:" + cashModel.getOrderAmount());
			accountDao.updateAccount(-cashMoney, -cashMoney, 0, cash.getUser().getUserId());
			log.setRemark("提现成功，扣除冻结金额：" + cash.getTotal()+" 元");
			log.setType(Constant.CASH_SUCCESS);
			log.setAddtime(new Date());
			cash.setStatus(1);
			cash.setFee(cashModel.getFeeAmt());
		}else{
			logger.info("提现失败++++++++++++++++");
			logger.info(Global.getValue("api_name")+"提现申请处理失败,order: " + orderId + " money:" + cashModel.getOrderAmount());
			//解冻
			//accountDao.updateAccount(0, cashMoney, -cashMoney, cash.getUser().getUserId());
			log.setRemark("提现失败，返回冻结资金"+cash.getTotal()+" 元");
			log.setType(Constant.CASH_FAIL);
			log.setAddtime(new Date());
			cash.setStatus(2);
			cash.setDealStatus(2);
		}
		
	}

	@Override
	public void doYjfCashService(AccountCashModel cashModel,AccountCash cash,AccountLog log) {
		String orderNo = cashModel.getOrderId();

		logger.info("disruptor 处理提现进入：orderNo："+ orderNo);
		cash = accountCashDao.getAccountCashByOrderNo(orderNo);
		if(cash == null || cash.getStatus() != 4){
			logger.info("提现拦截，取现状态："+cash.getStatus());
			throw new BussinessException("取现回调处理状态异常status："+cash.getStatus());
		}
		double money = cash.getTotal();
		log.setUser(cash.getUser());
		log.setToUser(new User(1));
		log.setType(Constant.CASH_SUCCESS);
		if(cashModel.isResult()){
			logger.info("提现成功回调，扣除冻结款。");
			logger.info(Global.getValue("api_name")+"提现申请处理成功,order: " + orderNo + " money:" + cashModel.getOrderAmount());
			accountDao.updateAccount(-money, 0, -money, cash.getUser().getUserId());
			log.setRemark("提现成功，扣除冻结金额：" + cash.getTotal()+" 元");
			log.setType(Constant.CASH_SUCCESS);
			log.setAddtime(new Date());
			cash.setStatus(1);
			cash.setFee(cashModel.getFeeAmt());
			cash.setDealStatus(1);//易及付取现成功
		}else{
			logger.info("提现失败回调过来，解冻冻结款。");
			logger.info(Global.getValue("api_name")+"提现申请处理失败,order: " + orderNo + " money:" + cashModel.getOrderAmount());
			//解冻
			accountDao.updateAccount(0, money, -money, cash.getUser().getUserId());
			log.setRemark("提现失败，返回冻结资金"+cash.getTotal()+" 元");
			log.setType(Constant.CASH_FAIL);
			log.setAddtime(new Date());
			cash.setStatus(2);
			cash.setDealStatus(2);//易及付取现失败
		}
	}

	
	@Override
	public void doHfCashService(AccountCashModel cashModel, AccountCash cash,
			AccountLog log) {
		logger.info("取现回调进入业务方法----");
		AccountCash dbCash=accountCashDao.getAccountCashByOrderNo(cashModel.getOrderId());   //获取取现记录,回调中处理根据订单查询
		String cashIsVerify = Global.getValue("cash_is_verify"); //获取取现是否需要校验
		
		AccountBank accountBank = accountBankDao.getAccountBankByCardNo(cashModel.getCardNo(), cash.getUser());
		cash.setAccountBank(accountBank);
		double manageFee = 0d;//用户取现支付的全部费用
		if(Global.getInt("is_pay_cash") == 0){//汇付取现手续费，网站不垫付
			manageFee +=cashModel.getFeeAmt();
		}
		manageFee+=cashModel.getServFee();//当网站不收取网站服务费的时候，传进来的是0
		
		double money = cash.getTotal()+manageFee;//v1.8.0.4 周学成  加上网站服务费
		
		cash.setFee(manageFee);//v1.8.0.4 周学成  加上网站服务费
		
		cash.setCredited(cash.getTotal());
		
		//根据状态，判断操作订单
		if ("999".equals(cashModel.getReturnCode())) {  //处理中
			if (dbCash.getStatus()==4) {  //资金冻结
				doCashByChinapnrByfour(cashIsVerify,money,cash,log);	//业务处理方法
			}
		} else if("000".equals(cashModel.getReturnCode())){  //成功
			if (dbCash.getStatus()==1) {	//已经处理成功
				return;
			}
			if (dbCash.getStatus() ==8) {	//冻结状态，扣除冻结
				logger.info("订单："+cashModel.getOrderId()+"，提现成功！进入本地处理！");
				log.setRemark("提现处理成功，手续费"+manageFee+"元。");
				log.setType(Constant.CASH_SUCCESS);
				if(cash.getUser().getAccount().getUseMoney() < money ){
					money = cash.getTotal();
				}else{
					cash.setTotal(money);
				}
				accountDao.updateAccount(-money, 0, -money, cash.getUser().getUserId());
				//设置状态为取现成功
				cash.setStatus(1);
				return ;
			}
			if (dbCash.getStatus() ==4) {  //处理中状态到处理成功
				log.setRemark("提现处理成功，手续费"+manageFee+"元。");
				log.setType(Constant.CASH_SUCCESS);
				if(cash.getUser().getAccount().getUseMoney() < money ){
					money = cash.getTotal();
				}else{
					cash.setTotal(money);
				}
				accountDao.updateAccount(-money, -money, 0, cash.getUser().getUserId());
				//设置状态为取现成功
				cash.setStatus(1);
			}
			
		}
	}
	
	

	//汇付提现回调返回码999，状态是4，业务处理方法
	private void doCashByChinapnrByfour(String cashIsVerify,double money,AccountCash cash,AccountLog log){
		if (cashIsVerify.equals("1")) {
			//不需要审核，直接扣除
			log.setRemark("提现处理中");
			log.setType(Constant.CASH_SUCCESS);
			if(cash.getUser().getAccount().getUseMoney() < money ){
				money = cash.getTotal();
			}else{
				cash.setTotal(money);;
			}
			accountDao.updateAccount(-money, -money, 0, cash.getUser().getUserId());
			//设置状态为处理中
			cash.setStatus(8);
		}else{
			//需要审核，冻结资金
			log.setRemark("提现冻结");
			log.setMoney(money);
			cash.setCredited(cash.getTotal());
			if(cash.getUser().getAccount().getUseMoney() < money ){
				money = cash.getTotal();
			}else{
				cash.setTotal(money);;
			}
			accountDao.updateAccount(0, -money, money, cash.getUser().getUserId());
			//设置状态为取现待审核
			cash.setStatus(0);
		}
	}
//钱多多体现业务处理
	@Override
	public void doMmmCashService(AccountCashModel cashModel,AccountLog log,BorrowParam param) {
		logger.info("取现回调进入乾多多业务方法----" + "订单号：" + cashModel.getOrderId());
		String cashIsVerify = Global.getValue("cash_is_verify"); //取现是否需要审核1:不审核
		AccountCash cash = new AccountCash();
		cash=accountCashDao.getAccountCashByOrderNo(cashModel.getOrderId());   //获取取现记录,回调中处理根据订单查询
		logger.info("取现回调时取现记录状态：" + cash.getStatus() + "订单号：" + cashModel.getOrderId());
		//拦截
		if (param.getResultCode().equals("88") || param.getResultCode().equals("90")) {  //取现成功和取现审核中状态
			if (cash.getStatus() ==4) {   //如果订单状态不为4，则已经处理过，不再进行订单处理。4为提交状态，
				List<Object> taskList = new ArrayList<Object>();
				log.setType(Constant.CASH_FROST);
				log.setAddip(param.getIp());
				//执行取现操作,记住  cashModel.getFeeAmt()---->平台承担的手续费金额
				double cashFee=cashModel.getServFee();
				cash.setFee(cashFee);
				double totalmoney  = 0;
				double realCredit = 0;
				int cashFeeType = Global.getInt("cash_fee_type");
				if(cashFeeType == 1){//内扣
					totalmoney = cash.getTotal();
					realCredit = cash.getTotal() - cashFee;//实际到账(银行卡)金额
				}else{
					totalmoney = cash.getTotal() + cashFee;
					realCredit = cash.getTotal() ;//实际到账(银行卡)金额
				}
				cash.setCredited(realCredit);
				log.setMoney(totalmoney);
				if (cashIsVerify.equals("1")) { //不需要审核
					logger.info("提现不需要审核---" + "订单号：" + cashModel.getOrderId());
					log.setRemark("提现扣款,提现金额："+cash.getTotal()+", 手续费 ："+cashFee);
					log.setType(Constant.CASH_SUCCESS);
					accountDao.updateAccount(-totalmoney, -totalmoney, 0, cash.getUser().getUserId());
					cash.setStatus(1);
				}else{  //需要审核
					logger.info("提现需要审核---" + "订单号：" + cashModel.getOrderId());
					log.setRemark("提现冻结,提现金额："+cash.getTotal()+", 手续费 ："+cashFee);
					log.setType(Constant.CASH_SUCCESS);
					accountDao.updateAccount(0, -totalmoney, totalmoney, cash.getUser().getUserId());
					cash.setCredited(cash.getTotal());
					cash.setStatus(0);//0 提现需要审核
				}
				//如果取现费用为0，若规则启用，则是启用免费取现额度
				Rule rule = ruleService.getRuleByNid("free_cash_rule");//success_tender（1：投资成功给免费取现额度，0：不启用）
				logger.info("是否启用投资成功给免费取现额度" + rule.getStatus());
				if(rule != null && rule.getStatus() == 1){
					if(cashModel.getFeeAmt() == 0){
						Account act = accountDao.getAcountByUser(cash.getUser());
						act.setFreeCashMoney(act.getFreeCashMoney() - cash.getTotal());
						accountDao.save(act);
					}
				}
			}else{
				logger.info("提现拦截，取现状态："+cash.getStatus());
				//v1.8.0.4_u2 TGPROJECT-305 lx start
				logger.info("提现回调状态异常status："+cash.getStatus());
				if(cash.getStatus() == 1){
					throw new BussinessException("取现成功！");
				}else{
					throw new BussinessException("取现回调处理状态异常status："+cash.getStatus());
				}
			}
			logger.info("取现完成"+cash.getId());
		}else{  //取现失败
			logger.info("提现失败回调过来，归还提现款。");
			if(cash.getStatus() == 1){//提现申请成功，处理银行转账失败的情况。
				logger.info(Global.getValue("api_name")+"提现申请处理失败,order: " + cashModel.getOrderId() + " money:" + cashModel.getOrderAmount());
				double totalmoney = cash.getTotal();
				//解冻
				accountDao.updateAccount(totalmoney, totalmoney, 0, cash.getUser().getUserId());
				log.setRemark("提现失败，返回资金"+totalmoney+" 元");
				log.setType(Constant.CASH_FAIL);
				log.setAddtime(new Date());
				cash.setStatus(2);
			}else{
				throw new BussinessException("提现失败回调过来,取现状态异常:"+cash.getStatus());
			}
			
		}
	}
	//现金余额
	@Override
	public Map<String,String> findMmmCashBalance(String orderNo,String loanNo) {
		Map<String,String> map = new HashMap<String, String>();
		Object resp =  MmmHelper.findMmmCashBalance(orderNo,loanNo);
		JSONArray array= JSON.parseArray(resp.toString());
		for(int i=0;i<array.size();i++){
		     JSONObject jobj =  (JSONObject) array.get(i);
		     String name = jobj.getString("WithdrawsState");
		     map.put("WithdrawState", name);
		}
		
		return map;
		
	}
	//提现状态处理 
	@Override
	public boolean cashAccountBack(AccountCash accountCash, String cashStatus) {
		String cashIsVerify = Global.getValue("is_pay_cash"); //取现手续费是否垫付
		
		double cashMoney = 0;
		if ("3".equals(cashStatus)) {
			if ("0".equals(cashIsVerify)) {	//不垫付
				cashMoney = accountCash.getCredited()+accountCash.getFee();
			}else if("1".equals(cashIsVerify)){	//垫付
				cashMoney = accountCash.getCredited();
			}else{
				throw new BussinessException("取现手续费是否垫付状态错误！");
			}
		}else{
			throw new BussinessException("提现已处理成功或正在审核中！");
		}
		logger.info("进入取现退回，退回提现款。。。。。。。。。。。。。。。");
		try {
			accountDao.updateAccount(cashMoney, cashMoney, 0, accountCash.getUser().getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		accountCash.setStatus(6);	//状态为6，提现资金退回
		accountCash.setVerifyRemark("乾多多处理失败，提现退回！");
		accountCashDao.update(accountCash);
		//提现成功保存日志
		AccountLog log = new AccountLog();
		Account act = accountDao.getAcountByUser(accountCash.getUser());
		User user =userDao.find(accountCash.getUser().getUserId()); 
		log.setMoney(cashMoney);
		log.setType(Constant.CASH_ACCOUNT_BACK);
		log.setTotal(act.getTotal());
		log.setUseMoney(act.getUseMoney());
		log.setNoUseMoney(act.getNoUseMoney());
		log.setCollection(act.getCollection());
		log.setRepay(act.getRepay());
		log.setUser(user);
		log.setAddtime(new Date());
		log.setToUser(new User(Constant.ADMIN_ID));
		log.setRemark("提现资金退回"+cashMoney+"元。");
		acountlAccountLogDao.save(log);
		return true;
	}
}
