package com.liangyou.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import com.liangyou.util.BigDecimalUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.MsgReq;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.service.ApiRechrageService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

@Service("apiAccountService")
public class ApiRechargeServiceImpl implements ApiRechrageService {
	
	private Logger logger = Logger.getLogger(ApiRechrageService.class);

	@Autowired
	private AccountRechargeDao accountRechargeDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private AccountLogDao acountlAccountLogDao;
	@Autowired
	private RewardExtendService rewardExtendService;
	@Autowired
	private MsgService msgService;
	/* (non-Javadoc)
	 * 环迅充值平台手续费收取方式：
	 * 手续费统一外扣，充值前传入平台收取的手续费金额
	 * 若平台不代付手续费，环迅在从用户金额中额外再扣除
	 * 一部分手续费，返回给我们的是一个充值扣款总额
	 */
	@Override
	public void doIpsRechrege(RechargeModel reModel) {
		//查询充值订单
		AccountRecharge recharge = accountRechargeDao.getRechargeByTradeno(reModel.getOrderId());
		//校验订单是否成功
		if (("MG00000F").equals(reModel.getResult())) { //操作成功：表示真的处理成功
			//判断订单状态:0、提交状态，8处理中
			if (recharge.getStatus()==0 || recharge.getStatus() ==8) { //充值成功，处理交易资金
				//处理订单，将订单处理为已经成功状态，更新 用户资金
				User user = recharge.getUser();
				//更新订单状态
				recharge.setStatus(1);//设置状态
				recharge.setReturnTxt(reModel.getReturnParam());
				recharge.setSerialNo(reModel.getSerialNo());//返回流水号
				//设置手续费：先查询出平台收取的费用，在校验第三方是否收取用户充值手续费
				double webFee = recharge.getFee();  //平台收取用户费用
				double aipFee = NumberUtils.getDouble2(reModel.getOrderAmount())-webFee-recharge.getMoney(); //第三方收取用户的费用
				recharge.setFee(webFee+aipFee);
				recharge.setRemark("用户充值，收取手续费总额为："+webFee+aipFee+"元！");
				accountRechargeDao.update(recharge);
				//更新用户账户资金
				accountDao.updateAccount(recharge.getMoney(), recharge.getMoney(), 0, user.getUserId());
				Account act = accountDao.getAcountByUser(user);
				//添加资金记录
				AccountLog log = new AccountLog();
				log.setUser(recharge.getUser());
				logger.info("充值回调用户Id是"+recharge.getUser().getUserId());
				User toUser = new User();
				toUser.setUserId(Constant.ADMIN_ID);
				log.setToUser(toUser);
				log.setMoney(recharge.getMoney());
				log.setTotal(act.getTotal());
				log.setUseMoney(act.getUseMoney());
				log.setNoUseMoney(act.getNoUseMoney());
				log.setCollection(act.getCollection());
				log.setRepay(act.getRepay());
				log.setType(Constant.RECHARGE);
				log.setRemark("用户网银充值金额:"+recharge.getMoney()+",收取管理费"+(aipFee+webFee));
				acountlAccountLogDao.save(log);
			}else{
				logger.info("订单:"+reModel.getOrderId()+"已经处理，状态："+recharge.getStatus()+",交易结果："+reModel.getResult());
			}
		}else  if (("MG00008F").equals(reModel.getResult())) {   //受理中，表示钱还未到账
			if (recharge.getStatus() ==0) {
				recharge.setStatus(8);//设置状态,处理中
				recharge.setReturnTxt(reModel.getReturnParam());
				recharge.setSerialNo(reModel.getSerialNo());//返回流水号
				accountRechargeDao.update(recharge);
			}else{
				logger.info("订单:"+reModel.getOrderId()+"已经处理，状态："+recharge.getStatus()+",交易结果："+reModel.getResult());
			}
			
		}else{ //处理失败，更改订单状态
			accountRechargeDao.updateRecharge(2,reModel.getResultMsg(),reModel.getOrderId(),reModel.getSerialNo());
		}
		
	}
	
	/**
	 * 易极付充值处理
	 */
	public void doYjfRecharge(RechargeModel rem,AccountLog log,String params){
		logger.info("进入充值"+"订单号："+rem.getOrderId());
		AccountRecharge existRecharge=accountRechargeDao.getRechargeByTradeno(rem.getOrderId());
		if(existRecharge==null){
			logger.info("订单号不存在："+ rem.getOrderId());
			throw new BussinessException("订单号不存在, orderNo:" + rem.getOrderId());
		}
		if(existRecharge.getMoney() != Double.parseDouble(rem.getOrderAmount())){ //校验金额是否相等
			logger.info("金额不相等"+Global.getValue("api_name")+"回调金额：" + rem.getOrderAmount() + " 订单：" + rem.getOrderId()
					+ "---金额不符,充值被拒绝");
			throw new BussinessException(Global.getValue("api_name")+"回调金额：" + rem.getOrderAmount() + " 订单：" + rem.getOrderId()
					+ "---金额不符,充值被拒绝");
		}
		existRecharge.setReturnTxt(params);
		long userId = existRecharge.getUser().getUserId();
		if(existRecharge.getStatus()==0||existRecharge.getStatus()==2){
			logger.debug("异步回调处理订单： " + existRecharge.getTradeNo());
			//修改账户金额
			Account act=accountDao.getAcountByUser(existRecharge.getUser());
			double fee = rechargeFee(rem);
			int rechargeWeb = Global.getInt("recharge_web");  //获取平台是否自己垫付充值手续费
			double rechargeMoney = 0;
			if (rechargeWeb ==1 || rechargeWeb==0 ) { //平台垫付充值手续费,不收取任何手续费
				rechargeMoney=existRecharge.getMoney();
			}else{//从用户自己账户扣款
				rechargeMoney = NumberUtils.format2(existRecharge.getMoney() - fee);
			}
			accountDao.updateAccount(rechargeMoney, rechargeMoney, 0,userId );

			//修改订单状态
			existRecharge.setStatus(1);//设置状态
			existRecharge.setReturnTxt(params);//返回参数
			existRecharge.setSerialNo(rem.getSerialNo());//返回流水号
			existRecharge.setFee(fee);
			if(rechargeWeb==0){//不扣手续费
				existRecharge.setRemark(existRecharge.getRemark());
			}else if(rechargeWeb==1){//平台垫付充值手续费，备注要说明
				existRecharge.setRemark(existRecharge.getRemark()+",网站垫付手续费：" + NumberUtils.format2(fee));
			}else{
				existRecharge.setRemark(existRecharge.getRemark()+",手续费：" + NumberUtils.format2(fee));
			}
			accountRechargeDao.update(existRecharge);

			//插入资金记录表
			log.setUser(existRecharge.getUser());
			logger.info("充值回调用户Id是"+userId);
			User toUser = new User();
			toUser.setUserId(Constant.ADMIN_ID);
			log.setToUser(toUser);
			log.setMoney(rechargeMoney);
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			String remark  = "";
			String logRemarks = "";
			if (rem.getRechargeType()!=3) {  //根据充值类型，填写充值记录内容
				logRemarks = "网银充值";
			}else{
				logRemarks = "代扣充值";
			} 
			if(rechargeWeb == 0){
				remark = +rechargeMoney +"元" ;
			}else if(rechargeWeb == 1){
				remark = logRemarks+rechargeMoney +"元" + ",网站垫付手续费：" + fee +"元";
			}else{
				remark = (fee==0)? (logRemarks+rechargeMoney +"元"):(logRemarks+rechargeMoney +"元 ,扣除手续费：" + fee +"元");
			}
			log.setRemark(remark);
			accountLogDao.save(log);
			//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 start
			//第一次充值成功送红包
			rewardExtendService.rechargeExtendRedPacket(existRecharge.getUser(),rechargeMoney);
			//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 end
			//充值成功，发消息通知
			MsgReq req = new MsgReq();
			req.setReceiver(existRecharge.getUser());
			req.setSender(new User(Constant.ADMIN_ID));
			req.setMsgOperate(this.msgService.getMsgOperate(8));
			req.setTime(DateUtils.dateStr4(new Date()));
			req.setMoney(""+rechargeMoney);
			try {
				DisruptorUtils.sendMsg(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}else{
			logger.info("订单状态不对：orderNo--" + existRecharge.getTradeNo() + " status:" + existRecharge.getStatus());
		}
	}

	/**
	 * 充值手续费工具方法
	 * @return
	 */
	private double rechargeFee(RechargeModel re){
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		case 1: //汇付可以直接从返回参数汇总取出返回的手续费金额
			return re.getFeeAmt();
		case 2: //易极付通过计算取得
			//充值手续费计算方法中，添加代扣充值手续费处理：TGPROJECT-137 start
			double fee  =0;
			int rechargeType = re.getRechargeType();
			if (rechargeType!=3) {
				String feeRateStr = Global.getValue("recharge_fee");
				double feeRate = 0;
				try {
					if(!StringUtils.isBlank(feeRateStr)){
						feeRate = Double.parseDouble(feeRateStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
				fee = (Double.parseDouble(re.getOrderAmount()))*feeRate;
			}else{
				fee = re.getFeeAmt();
			}
			//处理完毕TGPROJECT-137 end 

			return fee;
		default:
			return 0;
		}
	}
	
	/**
	 * 乾多多充值处理
	 */
	@Override
	public void doMmmRecharge(RechargeModel rem,AccountLog log) {
		logger.info("进入充值"+"订单号："+rem.getOrderId());
		AccountRecharge existRecharge=accountRechargeDao.getRechargeByTradeno(rem.getOrderId());//查询充值记录
		if(existRecharge==null){
			logger.info("订单号不存在："+ rem.getOrderId());
			throw new BussinessException("订单号不存在, orderNo:" + rem.getOrderId());
		}

		AccountRecharge recharge = accountRechargeDao.getRechargeByTradeno(rem.getOrderId());
		if (("true").equals(rem.getResult())) { //操作成功：表示真的处理成功
			//判断订单状态:0、提交状态，8处理中
			if (recharge.getStatus()==0 || recharge.getStatus() ==8) { //充值成功，处理交易资金
				
				//处理订单，将订单处理为已经成功状态，更新 用户资金
				User user = recharge.getUser();
				//更新订单状态
				//recharge.setStatus(1);//设置状态
				recharge.setReturnTxt(rem.getReturnParam());
				recharge.setSerialNo(rem.getSerialNo());//返回流水号
				//设置手续费：先查询出平台收取的费用，在校验第三方是否收取用户充值手续费
				double webFee = recharge.getFee();  //平台收取用户费用
				double apiFee = rem.getFeeAmt(); //第三方收取用户的费用
				recharge.setFee((webFee+apiFee));
				logger.info("用户充值，第三方收取"+apiFee+",平台收取"+webFee);
				recharge.setRemark("用户充值，收取手续费总额为："+(webFee+apiFee)+"元！");
				//判断是否是汇款充值，双钱审核中
				if (rem.getRechargeType()==3 && "88".equals(rem.getCode())) {
					//将订单状态修改为处理中
					recharge.setStatus(8);
					return ;
				}
				recharge.setStatus(1);//设置状态
				accountRechargeDao.update(recharge);
				//更新用户账户资金
				accountDao.updateAccount(recharge.getMoney(), recharge.getMoney(), 0, user.getUserId());
				Account act = accountDao.getAcountByUser(user);
				
				log.setUser(recharge.getUser());
				logger.info("充值回调用户Id是"+recharge.getUser().getUserId());
				User toUser = new User();
				toUser.setUserId(Constant.ADMIN_ID);
				log.setToUser(toUser);
				log.setMoney(recharge.getMoney());
				log.setTotal(act.getTotal());
				log.setUseMoney(act.getUseMoney());
				log.setNoUseMoney(act.getNoUseMoney());
				log.setCollection(act.getCollection());
				log.setRepay(act.getRepay());
				log.setType(Constant.RECHARGE);
				log.setRemark("用户网银充值金额:" + new BigDecimal(recharge.getMoney()).setScale(2,BigDecimal.ROUND_HALF_UP) + ",收取管理费"+(apiFee+webFee));
				acountlAccountLogDao.save(log);
				try {
					//充值成功，发消息通知
					MsgReq req = new MsgReq();
					req.setReceiver(log.getUser());
					req.setSender(new User(Constant.ADMIN_ID));
					req.setMsgOperate(this.msgService.getMsgOperate(8));
					req.setTime(DateUtils.dateStr4(new Date()));
					req.setMoney("" + new BigDecimal(recharge.getMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));
					DisruptorUtils.sendMsg(req);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				logger.info("订单:"+rem.getOrderId()+"已经处理，状态："+recharge.getStatus()+",交易结果："+rem.getResult());
			}
		}else{ //处理失败，更改订单状态
			accountRechargeDao.updateRecharge(2,rem.getResultMsg(),rem.getOrderId(),rem.getSerialNo());
		}
		
	}

	/**
	 * 汇付充值提现
	 */
	@Override
	public void doChinapnrRecharge(RechargeModel rem) {
		//查询充值订单
		AccountRecharge recharge = accountRechargeDao.getRechargeByTradeno(rem.getOrderId());
		//校验订单是否成功
		if (("true").equals(rem.getResult())) { //操作成功：表示真的处理成功
			//判断订单状态:0、提交状态，8处理中
			if (recharge.getStatus()==0 || recharge.getStatus() ==8) { //充值成功，处理交易资金
				//处理订单，将订单处理为已经成功状态，更新 用户资金
				User user = recharge.getUser();
				//更新订单状态
				recharge.setStatus(1);//设置状态
				recharge.setReturnTxt(rem.getReturnParam());
				recharge.setSerialNo(rem.getSerialNo());//返回流水号
				//设置手续费：先查询出平台收取的费用，在校验第三方是否收取用户充值手续费
				double webFee = recharge.getFee();  //平台收取用户费用
				double apiFee = rem.getFeeAmt(); //第三方收取用户的费用
				double accountMoney = recharge.getMoney()-webFee-apiFee;
				recharge.setFee((webFee+apiFee));
				logger.info("用户充值，第三方收取"+apiFee+",平台收取"+webFee);
				recharge.setRemark("用户充值，收取手续费总额为："+(webFee+apiFee)+"元！");
				accountRechargeDao.update(recharge);
				//更新用户账户资金
				accountDao.updateAccount(accountMoney, accountMoney, 0, user.getUserId());
				Account act = accountDao.getAcountByUser(user);
				//添加资金记录
				AccountLog log = new AccountLog();
				log.setUser(recharge.getUser());
				logger.info("充值回调用户Id是"+recharge.getUser().getUserId());
				User toUser = new User();
				toUser.setUserId(Constant.ADMIN_ID);
				log.setToUser(toUser);
				log.setMoney(recharge.getMoney());
				log.setTotal(act.getTotal());
				log.setUseMoney(act.getUseMoney());
				log.setNoUseMoney(act.getNoUseMoney());
				log.setCollection(act.getCollection());
				log.setRepay(act.getRepay());
				log.setType(Constant.RECHARGE);
				log.setRemark("用户网银充值金额:"+recharge.getMoney()+",收取管理费"+(apiFee+webFee));
				log.setAddtime(recharge.getAddtime());
				acountlAccountLogDao.save(log);
			}else{
				logger.info("订单:"+rem.getOrderId()+"已经处理，状态："+recharge.getStatus()+",交易结果："+rem.getResult());
			}
		}else{ //处理失败，更改订单状态
			accountRechargeDao.updateRecharge(2,rem.getResultMsg(),rem.getOrderId(),rem.getSerialNo());
		}
		
	}
	

}
