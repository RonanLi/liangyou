package com.liangyou.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.api.pay.ApplyWithDrawBack;
import com.liangyou.api.pay.DeductSign;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.RealNameCertSaveBack;
import com.liangyou.api.pay.Recharge;
import com.liangyou.api.pay.SignmanyBank;
import com.liangyou.api.pay.YzzNewDeduct;
import com.liangyou.api.pay.YzzNewWithraw;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.service.AccountService;
import com.liangyou.service.LotteryService;
import com.liangyou.service.MsgService;
import com.liangyou.service.PrizeDetailService;
import com.liangyou.service.PrizeUserRelationshipService;
import com.liangyou.service.UserService;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.util.YjfUtil;

@Namespace("/public")
@ParentPackage("p2p-default") 
public class PublicAction extends BaseAction {
	
	private static final Logger logger=Logger.getLogger(PublicAction.class);
    @Autowired
	private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private MsgService msgService;
    @Autowired
    private PrizeUserRelationshipService prizeUserRelationshipService;
    @Autowired
    private PrizeDetailService prizeDetailService;
    @Autowired
    private LotteryService lotteryService;
    
	public AccountService getAccountService() {
		return accountService;
	}
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public String blank() throws Exception{
		return SUCCESS;
	}
	
	@Action("chinapnr")
	public String chinapnr() throws Exception{
		
		Recharge re = this.payCallback();
		logger.info("充值回调进来..." + getRequestParams());
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  start
//		boolean  ret  = this.checkSign(re);//签证校验。
//		if(!ret){
//			throw new BussinessException("验证签名失");
//		}
		//易极付验证签名
		checkSignByYjf();
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  end
		AccountLog log=new AccountLog(1L,Constant.RECHARGE,1L,getTimeStr(), getRequestIp());
		String orderNo = re.getOrderNo(); //订单号。
		logger.info( "订单号：" + re.getOrderNo() + " 金额：" + re.getDepositAmount() 
				     + " 结果：" + re.getIsSuccess() + " 流水号：" + re.getDepositId() );
		log.setRemark("网上充值,"+Global.getValue("api_name")+"充值,订单号:"  +  orderNo);
		try {
			if(orderNo != null) {
				log.setRemark(getRequestParams());//返回的参数包含 进去
				RechargeModel reModel = new RechargeModel();  //对通用javabean进行参数封装
				reModel.setOrderAmount(re.getDepositAmount());
				reModel.setOrderId(re.getOrderNo());
				reModel.setResultMsg(re.getIsSuccess());
				reModel.setResult(re.getIsSuccess());
				reModel.setSerialNo(re.getDepositId());
				
				BorrowParam param=new BorrowParam();
				String resultFlag =  paramString("userId") + System.currentTimeMillis()+"";
				param.setResultFlag(resultFlag);
				request.setAttribute("tenderFlag", resultFlag);
				request.setAttribute("ok_url", "/member/main.html"); //成功返回地址
				request.setAttribute("r_msg","充值成功，请您返回查看");
				DisruptorUtils.doRechargeBackTask(reModel, log,param);
				return RESULT;
			}else {
				logger.info( "**********"+Global.getValue("api_name")+"充值 回调返回订单为空:" + orderNo );
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error("充值失败："+orderNo + "   " +  e );
		}
		return null;
	}
	//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  start
	public void checkSignByYjf(){
		logger.info("进入易极付验证签名"); 
		logger.info("返回参数:"+getRequestParams());
		YjfUtil.check(getRequestParamsToMap());
		logger.info("进入易极付验证签名成功");
	}
	//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  end
	public boolean checkSign(Recharge re){
		String yjfSign = re.getSign();
		String[] paramNames = new String[]{"sign","signType","depositAmount","notifyTime","orderNo","notifyType",
				                      "depositId","isSuccess"};
		re.setParamNames(paramNames);
		String mySign = re.Createsign();
		logger.info("sign_old" + yjfSign);
		logger.info("sign_new:" + mySign);
		if(yjfSign.equals(mySign)){
			return true;
		}else{
			return false;
		}
	}
	
	public Recharge payCallback(){
		Recharge re = new Recharge();
		re.setSign(paramString("sign"));
		re.setSignType(paramString("signType"));
		re.setDepositAmount(paramString("depositAmount"));
		re.setNotifyTime(paramString("notifyTime"));
		re.setOrderNo(paramString("orderNo"));
		re.setNotifyType(paramString("notifyType"));
		re.setDepositId(paramString("depositId"));
		re.setIsSuccess(paramString("isSuccess"));
	    return re;
	}
	
	@Action("verifyCashBack")
	public String verifyCashBack() throws Exception{ //提现异步回调
		logger.info(getRequestParams() +  ".....提现异步回调进来");
		 
		 String  sign = paramString("sign")  ;
		 String message= paramString("message");
		 String  amount= paramString("amount")  ;
		 String  notifyTime= paramString("notifyTime");
		 String payNo= paramString("payNo");
		 String SETTLE_REASON= paramString("SETTLE_REASON");
		 String payTypeMessage= paramString("payTypeMessage");
		 String resultCode= paramString("resultCode");
		 String  payType= paramString("payType") ; 
		 String outBizNo= paramString("outBizNo");
		 String resultMessage= paramString("resultMessage");
		 String success= paramString("success");
		
		String notice_text = getRequestParams();
        logger.info("返回参数： " + notice_text );
		if(StringUtils.isBlank(sign) || StringUtils.isBlank(message) 
				|| StringUtils.isBlank(amount) ||StringUtils.isBlank(notifyTime)
				|| StringUtils.isBlank(payNo)|| StringUtils.isBlank(SETTLE_REASON)
				|| StringUtils.isBlank(payTypeMessage)|| StringUtils.isBlank(resultCode)
				||StringUtils.isBlank(payType)||StringUtils.isBlank(outBizNo)||
				StringUtils.isBlank(resultMessage)||StringUtils.isBlank(success)){
			throw new BussinessException("参数错误...");
		}
		ApplyWithDrawBack awd = new ApplyWithDrawBack();
		awd.setSign(sign);
		awd.setMessage(message);
		awd.setAmount(amount);
		awd.setNotifyTime(notifyTime);
		awd.setPayNo(payNo);
		awd.setSETTLE_REASON(SETTLE_REASON);
		awd.setPayTypeMessage(payTypeMessage);
		awd.setResultCode(resultCode);
		awd.setPayType(payType);
		awd.setOutBizNo(outBizNo);
		awd.setResultMessage(resultMessage);
		awd.setSuccess(success);
		
		String mySign = awd.Createsign();
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  start
		checkSignByYjf();
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  end
		logger.info("提现验证签名成功... orderNo:" + outBizNo );
		//封装信息
		AccountCashModel cashModel = new AccountCashModel();
		cashModel.setOrderId(awd.getOutBizNo());
		cashModel.setOrderAmount(awd.getAmount());
		//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  start
		cashModel.setFeeAmt(NumberUtils.getDouble(awd.getAmount())-NumberUtils.getDouble(awd.getAmountIn()));//提现手续费
		//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  end
		cashModel.setResult(awd.getResultCode().contains("WITHDRAW_SUCCESS"));//包含"WITHDRAW_SUCCESS" 即为成功。
		DisruptorUtils.doVerifyCashBackTask(cashModel);
		logger.info("返回—sign:" + sign);
		logger.info("生成-sign:" + mySign);
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		return null;
	}
	//v1.8.0.4_u4 TGPROJECT-360  qinjun  2014-07-11  start
	@Action("cashNotify")
	public String cashNotify() throws Exception{ //新提现异步回调
		logger.info(getRequestParams() +  ".....提现异步回调进来");
		String  notifyTime= paramString("notifyTime");
		String  sign = paramString("sign")  ;
		String  payType= paramString("payType")  ;
		String amount= paramString("amount");
		String amountIn= paramString("amountIn");
		String outBizNo= paramString("outBizNo");
		String success= paramString("success");
		String resultMessage= paramString("resultMessage");
		String payNo= paramString("payNo");
		String resultCode = paramString("resultCode");
		
		if(StringUtils.isBlank(notifyTime) || StringUtils.isBlank(sign) 
				|| StringUtils.isBlank(payType) ||StringUtils.isBlank(amount)
				|| StringUtils.isBlank(outBizNo)|| StringUtils.isBlank(success)
				|| StringUtils.isBlank(resultMessage)|| StringUtils.isBlank(payNo)){
			throw new BussinessException("参数错误...");
		}
		YzzNewWithraw awd = new YzzNewWithraw();
		awd.setSign(sign);
		awd.setPayMode(payType);
		awd.setMoney(amount);
		awd.setOutBizNo(outBizNo);
		awd.setSuccess(success);
		awd.setResultMessage(resultMessage);
		awd.setOrderNo(payNo);
		awd.setResultCode(resultCode);
		awd.setAmountIn(amountIn);
		
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  start
		checkSignByYjf();
		//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  end
		logger.info("提现验证签名成功... orderNo:" + outBizNo );
		//封装信息
		AccountCashModel cashModel = new AccountCashModel();
		cashModel.setOrderId(awd.getOutBizNo());
		cashModel.setOrderAmount(awd.getMoney());
		//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  start
		cashModel.setFeeAmt(NumberUtils.getDouble(awd.getMoney())-NumberUtils.getDouble(awd.getAmountIn()));//提现手续费
		//v1.8.0.4_u3  TGPROJECT-342   qinjun  2014-06-24  end
		cashModel.setResult(awd.getResultCode().contains("WITHDRAW_SUCCESS"));//包含"WITHDRAW_SUCCESS" 即为成功。
		DisruptorUtils.doVerifyCashBackTask(cashModel);
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		return null;
	}
	//v1.8.0.4_u4 TGPROJECT-360  qinjun  2014-07-11  end
	/**
	 * 易极付客服 实名认证回调
	 */
	@Action("realNameCertSaveBack")
	public  String realNameCertSaveBack() throws IOException{
		logger.info(getRequestParams() + " --- 易极付客服 实名认证回调");
		
		String status = paramString("status");
		String userId = paramString("userId");
		String msg = paramString("msg");
		String notifyTime = paramString("notifyTime");
		String signType = paramString("signType");
		String sign = paramString("sign");
		String authNo = paramString("authNo");
		RealNameCertSaveBack rnb = new RealNameCertSaveBack();
		rnb.setStatus(status);
		rnb.setUserId(userId);
		rnb.setMsg(msg);
		rnb.setNotifyTime(notifyTime);
		rnb.setSignType(signType);
		rnb.setSign(sign);
		rnb.setAuthNo(authNo);
		
		if(StringUtils.isBlank(status) ||StringUtils.isBlank(userId) ||StringUtils.isBlank(msg)||
				StringUtils.isBlank(notifyTime)||StringUtils.isBlank(signType)||StringUtils.isBlank(sign)){
			throw new BussinessException("参数错误...");
		}
	    String mySign =	rnb.Createsign();
	    if(mySign!=null &&mySign.equals(sign)){
	    	logger.info("验证签名成功...realNameCertSaveBack");
	    	userService.updateRealNameStatusByCallBack(rnb);
	    	if ("success".equals(rnb.getStatus())) {
	    		//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start 
	    		if(getSessionUser()!=null){
	    			User tempUser = userService.getUserById(getSessionUser().getUserId());
	    			if (tempUser != null) {
	    				TempIdentifyUser tempIdentifyUser=userService.inintTempIdentifyUser(tempUser);
	    				session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);
	    				logger.info("更新tempIdentifyUser成功： "+tempIdentifyUser.getRealStatus());
	    			}
	    			
	    		}
				//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end
	    	}
	    }else{
	    	logger.info("验证签名失败...realNameCertSaveBack");
	    }
	    PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		
		return null;
	}
	
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	@Action("yjfRealNameNotify")
	public String yjfRealNameNotify() throws IOException{
		checkSignByYjf();
		NewAuthorize na = new NewAuthorize();
		na.setStatus(paramString("status"));
		na.setRealName(paramString("realName"));
		na.setCertNo( paramString("realName"));
		na.setUserId(paramString("userId"));
		na.setCertNo(paramString("certNo"));
		DisruptorUtils.yjfRealNameCall(na);
		PrintWriter p= response.getWriter();  //返回通知
		p.print("success");
		return null;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end
	
	/**
	 * 
	 * 签约回调
	 * @return
	 * @throws IOException 
	 */
/*	@Action("deductSign")
	public String deductSign() throws IOException{
		madeDeductSign();
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		return null;
	}*/
/*	@Action("deductSignReturn")
	public String deductSignReturn(){
    	BorrowParam param=new BorrowParam();
		String resultFlag =  paramString("userId") + System.currentTimeMillis()+"";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); //成功返回地址
		request.setAttribute("back_url", "/member/main.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，签约成功");
		madeDeductSign();
    	return RESULT;
	}*/
	
	// TGPROJECT-137  start
	@Action("signErrorBank")
	public String signErrorBank(){
		String str = getRequestParams();
		logger.info("返回参数:"+getRequestParams());
		message("签约异常，请联系管理员客服人员");
		return MSG;
	}
	
	/**
	 * 
	 * 后台回调
	 * @return
	 * @throws IOException 
	 */
	@Action("signmanyBanNotify")
	public String signmanyBanNotify() throws IOException{
		madeSignBankSign(null);
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		return null;
	}
	
	@Action("signmanyBankReturn")
	public String signmanyBankReturn(){
		BorrowParam param=new BorrowParam();
		String resultFlag =  paramString("userId") + System.currentTimeMillis()+"";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); //成功返回地址
		request.setAttribute("back_url", "/member/main.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，签约成功");
		madeSignBankSign(param);
		return RESULT;
	}
	
	/**
	 * 处理业务
	 */
	private void madeSignBankSign(BorrowParam param){
		checkSignByYjf();
		SignmanyBank signmanyBank = doSignSBankCall();
		DisruptorUtils.doSignmanysign(signmanyBank, param);
	}
	
	/**
	 * 拼接参数
	 * @return
	 */
	private SignmanyBank doSignSBankCall(){
		SignmanyBank signBank = new SignmanyBank();
		String cardNo=paramString("cardNo");   // 签约卡号
		String userId=paramString("userId");//用户易极付id
		String message = paramString("message"); //消息
		String notifyTime = paramString("notifyTime");
		String signType = paramString("signType");
		String sign = paramString("sign");
		String certNo = paramString("certNo");  //证件号
		String name = paramString("name");  //姓名
		String bankShort = paramString("bankShort");  //银行简称
		String bankName =paramString("bankName");   //银行全称
		String isSuccess = paramString("isSuccess");
		signBank.setBankName(bankName);
		signBank.setBankShort(bankShort);
		signBank.setCardNo(cardNo);
		signBank.setCertNo(certNo);
		signBank.setIsSuccess(isSuccess);
		signBank.setName(name);
		signBank.setNotifyTime(notifyTime);
		signBank.setUserId(userId);
		signBank.setSign(sign);
		signBank.setSignType(signType);
		signBank.setMessage(message);
		return signBank;
	}
	//TGPROJECT-137 签约处理完毕 end 
	
/*	private void madeDeductSign(){
		DeductSign dedu = deductSignCallBack();
		checkSignByYjf();
		DisruptorUtils.madeDeductSign(dedu);
	}*/
	
/*	public DeductSign deductSignCallBack(){
		String result=paramString("isSuccess");   //签约结果
		String cardNo=paramString("cardNo");   // 签约卡号
		String userId=paramString("userId");//用户易极付id
		String message = paramString("message"); //消息
		String notifyTime = paramString("notifyTime");
		String signType = paramString("signType");
		String sign = paramString("sign");
		DeductSign dedu =  new DeductSign();
		dedu.setIsSuccess(result);
		dedu.setMessage(message);
		dedu.setUserId(userId);
		dedu.setSign(sign);
		dedu.setNotifyTime(notifyTime);
		dedu.setSignType(signType);
		dedu.setCardNo(cardNo);
		return dedu;
	}*/
	
	//签约回调验签
	public boolean checkDeductSign(DeductSign dedu){
		String yjfSign = dedu.getSign();
		String[] paramNames = new String[]{"sign","signType","notifyTime","isSuccess","cardNo","userId","message"};
		dedu.setParamNames(paramNames);
		String  mySign = dedu.Createsign();
		if (yjfSign.equals(mySign)) {
			return true;
		}else{
			return true;
		}
	}
	
	public String getRequestParams(){
		String params = "";
		try {
			Enumeration  e= (Enumeration)request.getParameterNames();   
			 while(e.hasMoreElements())     {   
			    String parName=(String)e.nextElement();   
			    String value= request.getParameter(parName);
			    params += parName + "=" + value + "&";
			 } 
		} catch (Exception e) {
			logger.error(e);
		}
         return params;		 
	}
	
	//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  start
	public Map<String,String> getRequestParamsToMap(){
		Map<String,String> map = new HashMap<String, String>();
		try {
			Enumeration  e= (Enumeration)request.getParameterNames();   
			 while(e.hasMoreElements())     {   
			    String parName=(String)e.nextElement();   
			    String value= request.getParameter(parName);
			    map.put(parName, value);
			 } 
		} catch (Exception e) {
			logger.error(e);
		}
         return map;		 
	}
	//v1.8.0.4_u1  TGPROJECT-263  qj 2014-05-05  end
	/**
	 * 返回系通处理的信息
	 * @return
	 * @throws Exception
	 */
	@Action("getResult")
	public String getTenderResult() throws Exception{
		String result = "";
		String resultFlag = paramString("tenderFlag");
		if(StringUtils.isBlank(resultFlag)){
			result="查询处理信息错误";
		}else{
			result = (String)Global.RESULT_MAP.get(resultFlag);
			Global.RESULT_MAP.remove(resultFlag);//系统消息取出来之后立即删除，保证集合为空
		}
		logger.info("查询的结果：" + result + ", 参数resultFlag：" + resultFlag);
		Map<String,String> map = new HashMap<String, String>();
		map.put("msg_data", result);
		printJson(JSON.toJSONString(map));
		return null;
	}
	
	/**
	 * 返回投标领奖系处理的信息
	 * @return
	 * @throws Exception
	 */
	@Action("getTenderReceivePrizeResult")
	public String getTenderReceivePrizeResult() throws Exception{
//		String result = "", prizeDetail = "";
		Json receiveResult = new Json();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		String resultFlag = paramString("tenderFlag");
		if(StringUtils.isBlank(resultFlag)){
			receiveResult = new Json(0, false, "查询处理信息错误", dataMap, "");
		}else{
			Global.RESULT_MAP.remove(resultFlag);//系统消息取出来之后立即删除，保证集合为空
		
			Long purId = NumberUtils.getLong(paramString("purId"));
			Double money = NumberUtils.getDouble2(paramString("money"));
			User user = this.userService.getUserById(NumberUtils.getLong(paramString("userId")));
			
			SearchParam param = new SearchParam();
			param.addParam("user", user);
			param.addParam("status", 1);
			param.addOrder(OrderType.DESC, "receiveTime");
			List<PrizeUserRelationship> purList = this.prizeUserRelationshipService.findByParam(param);
			if (!purList.isEmpty()) {
				receiveResult = new Json(2, false, "您已经领取过奖品，不能重复领取。<br />请到“我的账户”-->“我的奖品”中查看。<br />更多优惠，敬请期待。", dataMap, "");
				super.writeJson(receiveResult);
				return null;
			}
			
			PrizeUserRelationship pur = this.prizeUserRelationshipService.findById(purId);
			Prize prize = pur.getPrize();
//			List<PrizeDetail> pdList = this.prizeDetailService.findByPrizeIdAndStatus(prize.getPrizeId(), 0);
			
			if (prize.getQuantity() > 0/* && !pdList.isEmpty()*/) { // 奖品数量大于0才能领取
				double userRule = prize.getUseRule();
				if (money >= userRule) { // 投资额必须大于等于当前奖品的使用规则才可以领取奖品
					pur.setStatus(1);
					pur.setUseTime(new Date());
					this.prizeUserRelationshipService.update(pur);
					
//					PrizeDetail pd = pdList.get(0);
//					pd.setStatus(1);
//					pd.setUseTime(new Date());
//					this.prizeDetailService.save(pd);
					
					// 奖品消耗，个数-1
					prize.setQuantity(prize.getQuantity() - 1);
					this.lotteryService.updatePrize(prize);
					
					dataMap.put("prizeId", prize.getPrizeId());
					dataMap.put("prizeName", prize.getPrizeName());
//					dataMap.put("prizeDetail", pd);
					receiveResult = new Json("领取奖品成功！", dataMap, "");
				}else{
					dataMap.put("prizeId", prize.getPrizeId());
					dataMap.put("prizeName", prize.getPrizeName());
					receiveResult = new Json(3, false, "您的投资金额未达到领取条件！", dataMap, "");
				}
			} else {
				receiveResult = new Json(0, false, "Sorry，您抽取的奖品因未能及时领取，已被抢光，只好返回活动页抽取其他奖品啦~", dataMap, "");
			}
		}
		
		super.writeJson(receiveResult);
		return null;
	}
	
	//v1.8.0.4_u5 TGPROJECT-381 qinjun  2014-07-30  start 
	@Action(value="failRealStatus")
	public String failRealStatus(){//动态请求
		checkSignByYjf();
		String status = paramString("status");
		String apiId = paramString("userId");
		if(status.equals("expire")){
			try {
				userService.yjfFailRealName(apiId);
				PrintWriter p=response.getWriter();  //返回通知
				p.print("success");
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
		return null;
	}
	//v1.8.0.4_u5 TGPROJECT-381 qinjun  2014-07-30  end
	
	
	/**
	 * 代扣充值异步回调，业务处理
	 * @return
	 * @throws IOException 
	 */
	@Action("payYzzNewDecut")
	public String payYzzNewDecut() throws IOException{
		
		logger.info("代扣充值异步处理进来..." + getRequestParams());
		YzzNewDeduct yzznewdeduct = this.getYzzNewDeductBackParams();
		//签名
		checkSignByYjf();
		
		AccountLog log=new AccountLog(1L,Constant.RECHARGE,1L,getTimeStr(), getRequestIp());
		
		log.setRemark("代扣充值,"+Global.getValue("api_name")+"充值,订单号:"  +  yzznewdeduct.getOrderNo());
		//异步处理
		DisruptorUtils.deductRechargeOffLine(yzznewdeduct);
		
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
		
		return null;
	}
	
	
	/**
	 * 易极付代扣充值异步回调参数封装
	 * @return
	 */
	public YzzNewDeduct getYzzNewDeductBackParams(){
		
		YzzNewDeduct yzznewdeduct=new YzzNewDeduct();
		yzznewdeduct.setOrderNo(paramString("orderNo"));
		yzznewdeduct.setSuccess(paramString("success"));
		yzznewdeduct.setNotifyTime(paramString("notifyTime"));
		yzznewdeduct.setResultCode(paramString("resultCode"));
		yzznewdeduct.setResultMessage(paramString("resultMessage"));
		yzznewdeduct.setPayNo(paramString("payNo"));
		yzznewdeduct.setAmount(paramString("amount"));
		yzznewdeduct.setAmountIn(paramString("amountIn"));
		yzznewdeduct.setSuccess(paramString("success"));
		yzznewdeduct.setMessage(paramString("message"));
		yzznewdeduct.setSign(paramString("sign"));
		yzznewdeduct.setOutBizNo(paramString("outBizNo"));
		
		return yzznewdeduct;
	}
	
}
