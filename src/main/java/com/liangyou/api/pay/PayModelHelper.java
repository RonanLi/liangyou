package com.liangyou.api.pay;

import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.Global;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.User;
import com.liangyou.domain.YjfPay;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

public class PayModelHelper {
	private static final Logger logger = Logger.getLogger(PayModelHelper.class);
	
	private static String doSubmit(PayModel mod){
		String res = null;
		try {
			res = mod.submit();
		} catch (Exception e) {
			logger.error(e);
		}
		return res;
	}
	
	/**
	 * 是否开通线上环境配置。
	 * @return
	 */
	public static boolean isOnlineConfig(){
		return "1".equals(Global.getValue("config_online"));
	}
	
	/**
	 * 是否开通第三方接口
	 * @return
	 */
	public static boolean isOpenApi(){
		return "1".equals(Global.getValue("open_yjf"));
	}
	
	/**
	 * 易极付注册
	 * @param user
	 * @return
	 */
    public static YjfRegister  userRegister( User user){
    	
    	YjfRegister userRegister = new YjfRegister();
    	userRegister.setService("yzzUserSpecialRegister");
        userRegister.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
        userRegister.setUserName( Global.getValue("webid") + user.getUsername());
        if("1".equals(Global.getValue("company_register"))){//开通企业注册
        	userRegister.setUserType("B");
        }else{//开通个人注册。
        	userRegister.setUserType("P");
        }
        userRegister.setUserStatus("Q");
        userRegister.setRealName(user.getRealname());//现在易极付这边可以随便填，已实名为主
        userRegister.setEmail(user.getEmail());
        userRegister.setRegisterFrom("E_TURNOVER"); //易周转。
        userRegister.setOriginRegisterFrom("EXT_IMPORT");
        userRegister.setCertNo(user.getCardId());
        userRegister.setMobile(user.getPhone());
        //v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  start
        userRegister.setCertValidTime(user.getCardOff());
        //v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  end
        String result = doSubmit(userRegister);
        if(!StringUtils.isBlank(result)){
           Map<String,String> map = (Map<String, String>)JSON.parse(result);
       	   //封装返回的信息
           String channelId = map.get("channelId");
           String resultCode = map.get("resultCode");
           String resultMessage = map.get("resultMessage");
           String userId = map.get("userId");
           userRegister.setChannelId(channelId);
           userRegister.setResultCode(resultCode);
           userRegister.setUserId(userId);
           userRegister.setResultMessage(resultMessage);
        }
        
    	return userRegister;
    }
    
    /**
     * 跳转易极付登陆页面
     * @param yifId
     * @return
     */
    public static ForwardYJFSit forwardYJFSit(String yifId){
    	ForwardYJFSit  fys = new ForwardYJFSit();
    	fys.setService("forwardYJFSit");
    	fys.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	fys.setUserId(yifId);
    	return fys;
    }
    
    /**
     * 激活账户  ----页面跳转的形式
     * @param yifId
     * @return
     */
    public static YzzActivate yzzActivate(String yifId){
    	YzzActivate  ya = new YzzActivate();
    	
    	ya.setService("yzzActivate");
    	ya.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	ya.setUserId(yifId);
    	
    	String weburl=Global.getValue("weburl");
    	String returnUrl = weburl+"/member/identify/phone.html";
    	String notifyUrl = weburl+ "/public/activeCallback.html";  // 
        
    	ya.setReturnUrl(returnUrl);
    	ya.setNotifyUrl(notifyUrl);
    	
    	return ya;
    }
    
    /**
     *  易极付查询用户信息
     * @param user
     * @return
     */
    public static UserAccountQuery  userAccountQuery(User user){
    	UserAccountQuery uq = new UserAccountQuery();
    	
    	uq.setService("userAccountQuery");
    	uq.setUserId(user.getApiId()+ "");
    	uq.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	String res =  doSubmit(uq);
    	//解析返回信息  封装成对象
    	@SuppressWarnings("unchecked")
		Map<String, Object> resMap  = (Map<String,Object>)JSON.parse(res);
    	Map<String, String> data = (Map<String, String>)JSON.parse(resMap.get("data").toString());
    	uq.setBalance(data.get("balance")==null?null:(data.get("balance")+""));
    	uq.setResultCode(data.get("resultCode")==null?null:(data.get("resultCode")+""));
    	uq.setCertifyStatus(data.get("certifyStatus")==null?null:(data.get("certifyStatus")+""));
    	uq.setStatus(data.get("status")==null?null:(data.get("status")+""));
    	uq.setFreezeAmount(data.get("freezeAmount")==null?null:(data.get("freezeAmount")+""));
    	uq.setRuntimeStatus(data.get("runtimeStatus")==null?null:(data.get("runtimeStatus")+""));
    	uq.setAccountNo(data.get("accountNo")==null?null:(data.get("accountNo")+""));
    	uq.setEmail(data.get("email")==null?null:(data.get("email")+""));
    	uq.setUserStatus(data.get("userStatus")==null?null:(data.get("userStatus")+""));
    	uq.setUserName(data.get("userName")==null?null:(data.get("userName")+""));
    	uq.setSystemAmount(data.get("systemAmount")==null?null:(data.get("systemAmount")+""));
    	uq.setChannelId(data.get("channelId")==null?null:(data.get("channelId")+""));
    	uq.setCreditAmount(data.get("creditAmount")==null?null:(data.get("creditAmount")+""));
    	uq.setCreditBalance(data.get("creditBalance")==null?null:(data.get("creditBalance")+""));
    	return uq;
    }
    
    /**实名认证
	 * realNameCert.query 实名认证
	 *  "message":"添加成功","resultCode":"EXECUTE_SUCCESS","success":true
	 */
	public static RealNameCertSave realNameCertSave(User user){
		RealNameCertSave rns = new RealNameCertSave();
    	try {
    		rns.setService("realNameCert.save");
    		rns.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    		rns.setCardid(user.getCardId());
    		if(isOnlineConfig()){
    			rns.setCardpic(Global.getValue("weburl") + "/" + user.getCardPic1());
    			rns.setCardpic1(Global.getValue("weburl") + "/" + user.getCardPic2());
    		}else{
    			rns.setCardpic("http://imgstatic.baidu.com/img/image/shouye/erping1.jpg");
    			rns.setCardpic1("http://imgstatic.baidu.com/img/image/shouye/erping1.jpg");
    		}
    		logger.info("-------------card" + Global.getValue("weburl") + "/" + user.getCardPic1());
    		logger.info("-------------card" + Global.getValue("weburl") + "/" + user.getCardPic2());
    		//1.一代身份证 2.二代身份证 3.临时身份证 4.回乡证 5.台胞证 6.护照 7.港澳身份证 8.台湾身份证 9.营业执照 10.其它
    		//给他默认  第二代身份证
    		int cardType = Integer.parseInt(user.getCardType()==null? "1616":user.getCardType());
    		rns.setCardtype( getCardType(cardType));
    		
    		if("长期".equals(user.getCardOff())){//身份证到期时间有长期，这里处理 周学成
    			rns.setCardoff("0");
    		}else{
    			rns.setCardoff(user.getCardOff().replace("-", ""));
    		}
    		rns.setCertType("personal");
    		rns.setNotifyUrl(Global.getValue("weburl") + "/public/realNameCertSaveBack.html");
    	    rns.setCoreCustomerUserId(user.getApiId());
    	    rns.setNickname(user.getRealname());
    		rns.setSource("1");
    	    String res =  doSubmit(rns);
    	    //封装返回信息
    	    Map<String,Object> map = (Map<String,Object>)JSON.parse(res);
    	    rns.setMessage(map.get("message")+"");
    	    rns.setResultCode(map.get("resultCode")+"");
    	    rns.setSuccess(map.get("success")+"");
		} catch (Exception e) {
			logger.error(e);
		}
		
	    return rns;
	}
	//易极付 和 我们对应的  值 不一样 ，这里修改下
	public static String getCardType(int type){
		switch (type) {
		case 1615:
			return "1";
		case 1616:
			return "2";
		case 1617:
			return "3";
		case 1618:
			return "4";
		case 1619:
			return "5";
		case 1620:
			return "6";
		case 1621:
			return "7";
		case 1622:
			return "8";
		case 1623:
			return "9";
		case 1624:
			return "10";
		default:
			return "2";
		}
	}
	
	/**
	 * 实名状态查询
	 * {"coreCusUserId":"20130531010055087234","message":"个人用户查询状态成功",
	 * "stauts":"NEW_APP","success":true}
	 */
	public static  RealNameCertQuery realNameCertQuery(String apiId){
		RealNameCertQuery fdn = new RealNameCertQuery();
		
		fdn.setService("realNameCert.query");
		fdn.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		fdn.setUserId(apiId);
		String res = doSubmit(fdn);
		//封装汇付消息
		Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
		fdn.setMessage(map.get("message")+"");
		fdn.setStatus(map.get("stauts")+"");
		fdn.setSuccess(map.get("success")+"");
		fdn.setMsg(map.get("msg")+"");
		return fdn;
	}
	
	/**
	 * 绑定手机
	 */
	public static ApplyMobileBinding applyMobileBinding(String apiId, String mobile) {
		ApplyMobileBinding ambc = new ApplyMobileBinding();
		
		ambc.setService("applyMobileBindingCustomer");
		ambc.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		ambc.setUserId(apiId);
		ambc.setMobile(mobile);
		String res = doSubmit(ambc);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		ambc.setResultCode(map.get("resultCode"));
		ambc.setMessage(map.get("resultMessage"));
		return ambc;
	}
	
	/**
	 * 更新手机绑定
	 */
	public static ApplyMobileBinding updateMobileBinding(String apiId, String mobile) {
		ApplyMobileBinding ambc = new ApplyMobileBinding();
		ambc.setUserId(apiId);
		ambc.setMobile(mobile);
		ambc.setService("updateMobileBindingCustomer");
		ambc.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		String res = doSubmit(ambc);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		ambc.setResultCode(map.get("resultCode"));
		ambc.setMessage(map.get("resultMessage"));
		return ambc;
	}
	
	/**
	 * 短信发送验证码服务
	 */
	public static SmsCaptcha smsCaptcha(String userId , String username) {
		SmsCaptcha sct = new SmsCaptcha();
		
		sct.setService("smsCaptcha");
		String orderNo = OrderNoUtils.getInstance().getSerialNumber();
		sct.setOrderNo(orderNo);
		sct.setUserId(userId);
		String contxt = "本次的验证码为:C，(为了您的资金安全，请勿将验证码转告他人)【"+Global.getValue("webname")+"】";
		sct.setSmsContext(contxt);
		String res = doSubmit(sct);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		sct.setResultCode(map.get("resultCode"));
		sct.setMessage(map.get("resultMessage"));
		sct.setCheckCodeUniqueId(map.get("checkCodeUniqueId")+"");
		sct.setNotifyTime(map.get("notifyTime")+"");
		return sct;
	}
	
	/**
	 * 验证短信验证码服务
	 */
	public static SmsConfirmVerifyCode smsConfirmVerifyCode(String checkCodeUniqueId, String checkCodeString) {
		SmsConfirmVerifyCode scv = new SmsConfirmVerifyCode();
		scv.setService("smsConfirmVerifyCode");
		scv.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		scv.setCheckCodeString(checkCodeString);
		scv.setCheckCodeUniqueId(checkCodeUniqueId);
	    String res = doSubmit(scv);
	    Map<String, String> map = (Map<String, String>)JSON.parse(res);
	    scv.setResultCode(map.get("resultCode"));
	    scv.setMessage(map.get("resultMessage"));
		return scv;
	}
	
	/**
	 * 校验银行卡的有效性
	 */
	public static VerifyFacade verifyFacade (String accountNo  , DrawBank db, User user){
		
		VerifyFacade vf = new VerifyFacade();
		vf.setService("verifyFacade");
		String orderNo = OrderNoUtils.getInstance().getSerialNumber();
		vf.setOrderNo(orderNo);
		vf.setExtendId(orderNo);
		vf.setBankCode(db.getBankCode());
		vf.setChannelApi(db.getPayChannelApi());
		vf.setAccountName(user.getRealname());
		vf.setAccountNo(accountNo);
		vf.setCardType("D"); //借记卡
		
		vf.setCertType("ID");
		vf.setCertNo(user.getCardId());
		
		String res = doSubmit(vf);
		Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
	    vf.setVerifyStatus(map.get("verifyStatus") + "");
	    vf.setResultCode(map.get("resultCode")+"");
	    vf.setResultMessage(map.get("resultMessage")+"");
		return vf;
	}
	
	/**
	 * 添加银行卡绑定信息服务
	 * {"message":"添加银行卡绑定信息成功","resultCode":"EXECUTE_SUCCESS","success":true}
	 */
	public static BankCodeBindingAddInfo  bankCodeBindingAddInfo(String cardNo,String apiId,
			String name,String bankCardType,String bankType){
		BankCodeBindingAddInfo bba = new BankCodeBindingAddInfo();
		//暂时放进去   正式环境要去掉
		bba.setService("bankCodeBinding.addInfo");
		bba.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		bba.setBankCardNo(cardNo);
		bba.setUserId(apiId);
		bba.setName(name);
		bba.setBankCardType(bankCardType);
		bba.setBankType(bankType);
		String res = doSubmit(bba);
		Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
		bba.setMessage(map.get("message")+"");
		bba.setResultCode(map.get("resultCode")+"");
		bba.setSuccess(map.get("success")+"");
 		return bba;
	}
	
	/**
	 * 设置银行卡为默认绑定
	 * {"message":"设置银行卡为默认绑定","resultCode":"EXECUTE_SUCCESS","success":true}
	 */
	public static BankCodeBindingSetDefault bankCodeBindingSetDefault(String cradNo,String apiId){
		BankCodeBindingSetDefault bbs = new BankCodeBindingSetDefault();
		//暂时放进去   正式环境要去掉
		bbs.setService("bankCodeBinding.setDefault");
		bbs.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		bbs.setBankCardNo(cradNo);
		bbs.setUserId(apiId);
		String res =  doSubmit(bbs);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		bbs.setResultCode(map.get("resultCode"));
		bbs.setSuccess(map.get("success"));
		bbs.setMessage(map.get("message"));
		return bbs;
	}
	
	/**
	 * 解绑银行卡
	 * @param userId
	 * @param bankCardNo
	 * @return
	 */
	public static BankCodeBindingRemove bankCodeBindingRemove(String userId,String bankCardNo){
		BankCodeBindingRemove bb=new BankCodeBindingRemove();
		
		bb.setService("bankCodeBindingRemove");
		bb.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		
		bb.setUserId(userId);
		bb.setBankCardNo(bankCardNo);
		String res = doSubmit(bb);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		bb.setResultCode(map.get("resultCode"));
		
		return bb;
	}
    
    /**
     * 易极付充值 跳转收银台  封装准备信息
     * @param user
     * @return
     */
    public static Recharge recharge(User user,String money){
    	String weburl=Global.getValue("weburl");
    	
    	Recharge re = new Recharge();
    	re.setService("deposit");
    	re.setReturnUrl(weburl+"/public/chinapnr.html");
        re.setNotifyUrl(weburl+Global.getValue("yjf_callback"));
    	re.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	re.setUserId(user.getApiId() + "");
    	re.setTradeMerchantId(Global.getValue("yjf_partnerId"));
    	if(isOnlineConfig()){
    		re.setTradeBizProductCode(Global.getValue("person_recharge")); //线上环境，个人充值收费规则。
    	}else{
    		re.setTradeBizProductCode("hjd_deposit"); //测试环境，个人充值收费规则。
    	}
    	re.setDepositAmount(money);
    	
    	re.Createsign();
    	return re;
    }
    
    /**
     * 无卡代扣签约
     * @param userId
     * @return
     */
    public static DeductSign deductSign(String userId){
    	String weburl=Global.getValue("weburl");
    	DeductSign dedu = new DeductSign();
    	dedu.setService("deductSign");
    	dedu.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	dedu.setUserId(userId);
    	dedu.setReturnUrl(weburl+"/public/deductSignReturn.html");
    	dedu.setNotifyUrl(weburl+"/public/deductSignNotify.html");
    	dedu.Createsign();
    	return dedu;
    }
     
    //TGPROJECT-362 代扣签约  start
    public static SignmanyBank doSiginmanyBank(String userId){
    	SignmanyBank sigBank = new SignmanyBank();
    	String weburl = Global.getValue("weburl");
    	sigBank.setService("signmanybank");
    	sigBank.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	sigBank.setUserId(userId);
    	sigBank.setReturnUrl(weburl+"/public/signmanyBankReturn.html");
    	sigBank.setErrorNotifyUrl(weburl+"/public/signErrorBank.html");
    	sigBank.setUnionBusinessNo("easy_trade-yxyt");
    	sigBank.Createsign();
    	return sigBank;
    }
    
  //TGPROJECT-362 代扣签约  end
    
    //TGPROJECT-362 代扣充值  start
/*    userId	会员号
    bankProvName	银行卡所在省
    bankCityName	银行卡所在市
    bankAccountNo	银行卡号
    money	代扣金额
    bankCode	银行简称
    service	接口名称
    partnerId	合作伙伴ID
    orderNo	合作伙伴网站唯一订单号
    sign	签名
    signType	签名方式
    returnUrl	页面跳转同步通知页面路径
    errorNotifyUrl	请求出错时的通知页面路径
    notifyUrl	服务器异步通知页面路径*/
    
    /**
     * 代扣充值银行卡
     * @param accountBank
     * @return
     */
    public static YzzNewDeduct doYzzNewDeduct(AccountBank accountBank,double money,String ordNo){
    	YzzNewDeduct  yzzNew = new YzzNewDeduct();
    	User user = accountBank.getUser();
    	yzzNew.setUserId(user.getApiId());
    	yzzNew.setOrderNo(ordNo);
    	yzzNew.setBankAccountNo(accountBank.getAccount());
    	yzzNew.setMoney(NumberUtils.format2Str(money));
    	yzzNew.setBankProvName(accountBank.getProvince().getName());
    	yzzNew.setBankCityName(accountBank.getCity().getName());
    	yzzNew.setBankCode(accountBank.getBank().getBankCode());
    	yzzNew.setService("yzzNewDeduct");
       	yzzNew.setNotifyUrl(Global.getValue("weburl")+"/public/payYzzNewDecut.html");
    	String res = doSubmit(yzzNew);
    	logger.info("代扣充值同步返回参数："+res);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		
		yzzNew.setSuccess(map.get("success"));
		yzzNew.setOrderNo(map.get("orderNo"));
		yzzNew.setResultCode(map.get("resultCode"));
		yzzNew.setResultMessage(map.get("resultMessage"));
		yzzNew.setDepositId(map.get("depositId"));
		yzzNew.setChannelId(map.get("channelId"));
		yzzNew.setIsSuccess(map.get("isSuccess"));
		yzzNew.setOutBizNo(map.get("outBizNo"));
		yzzNew.setAmount(map.get("amount"));
		yzzNew.setAmountIn(map.get("amountIn"));
		yzzNew.setBankName(map.get("bankName"));
		yzzNew.setMessage(map.get("message"));
		yzzNew.setPayNo(map.get("payNo"));
    	return yzzNew;
    	
    }
    
    /**
    //TGPROJECT-362 代扣充值  end
    
    /**
     * 代扣充值服务
     */
    /**
     * @param deductType ：代扣类型：sync：申请同步代扣，async：申请异步代扣
     * @param certType ：证件类型如：身份证
     * @param certNo     ：证件号
     * @param userId     ：用户id
     * @param amount     ：充值金额
     * @param bizIdentity    ：请求身份表示：取值：MERCHANT (商户版代扣充值)SPECIAL_MERCHANT (特约商户版充值)
     * @param bizNo     ：业务号：值：026(商户版代扣充值)，032(特约商户版充值)
     * @param outBizNo  ：外部订单号
     * @param tradeBizProductCode    ：业务产品编号
     * @param bankAccountNo :银行卡号
     * @param bankAccountName ：银行名
     * @param bankCode :银行简写
     * @param cityName :银行卡市
     * @param provName :银行卡省份
     * @return
     */
    public static  DeductDepositApply deductDepositApply(String deductType, String certType, String certNo, 
    		String userId ,String amount,String bizIdentity,String bizNo,String tradeBizProductCode,String bankAccountNo,
    		String bankAccountName,String bankCode,String provName,String cityName,String order ){
    	String weburl=Global.getValue("weburl");
    	DeductDepositApply deap =  new DeductDepositApply();
    	String orderNo = order;
    	deap.setService("deductDeposit.apply");
    	deap.setDeductType(deductType);
    	deap.setCertType(certType);
    	deap.setCertNo(certNo);
    	deap.setUserId(userId);
    	deap.setAmount(amount);
    	deap.setOrderNo(orderNo);
    	deap.setBizIdentity(bizIdentity);
    	deap.setBizNo(bizNo);
    	deap.setOutBizNo(orderNo);
    	deap.setTradeBizProductCode(tradeBizProductCode);
    	deap.setBankAccountNo(bankAccountNo);
    	deap.setBankAccountName(bankAccountName);
    	deap.setNotifyUrl(weburl+"/public/chinapnr.html");
    	deap.setOwner("EASY_TURNOVER");
    	deap.setBankCode(bankCode);
    	deap.setPublicTag("N");
    	deap.setExtFlag("N");
    	deap.setProvName(provName);
    	deap.setCityName(cityName);
    	String str = doSubmit(deap); //封装返回信息
    	Map<String, String> map = (Map<String, String>)JSON.parse(str);
    	/**
    	 * 这个位置需要修改map无法获取到其中某些参数
    	 */
    	Object instructionInfo=map.get("instructionInfo");
    	if(instructionInfo!=null){
    		Map<String, String> instructionInfoMap = (Map<String, String>)JSON.parse(instructionInfo.toString());
    		deap.setStatus(instructionInfoMap.get("status"));
    		deap.setRawAddTime(String.valueOf(instructionInfoMap.get("rawAddTime")));
    		deap.setBankName(instructionInfoMap.get("bankName"));
    		deap.setPayAmount(String.valueOf(instructionInfoMap.get("payAmount")));
    		deap.setPayAmountIn(String.valueOf(instructionInfoMap.get("payAmountIn")));
    		deap.setDepositId(String.valueOf(instructionInfoMap.get("depositId")));
    	}
    	Object a = map.get("success");
    	a.getClass();
    	deap.setSuccess(a);
    	deap.setResultMessage(map.get("message"));
    	return deap;
    }
    
    /**
     * 充值记录查询成功
	 * {"amounts":0.00,"amountsIn":0.00,"charges":0.00,
	 * "count":0,"currPage":1,"depositInfos":[],
	 * "errCodeCtx":{"code":"","memo":"","message":""},
	 * "message":"充值记录查询成功",
	 * "resultCode":"DEPOSIT_INQUERY_SUCCESS","success":true}
	 */
	public static DepositQuery  depositQuery(String currPage, String apiId){
		DepositQuery dpq = new DepositQuery();
		
		dpq.setService("deposit.query");
		dpq.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		dpq.setCurrPage(currPage);
		dpq.setUserId(apiId);
        String res = doSubmit(dpq);  //封装信息
        Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
        dpq.setAmounts(map.get("amounts")+"");
        dpq.setAmountsIn(map.get("amountsIn")+"");
        dpq.setCharges(map.get("charges")+"");
        dpq.setDepositInfos(map.get("depositInfos")+"");
        dpq.setErrCodeCtx(map.get("errCodeCtx")+"");
        dpq.setMessage(map.get("message")+"");
        dpq.setResultCode(map.get("resultCode")+"");
        dpq.setSuccess(map.get("success")+"");
		return dpq;
	}
	
	/**
	 * 查询支付渠道
	 * {"message":"",
	 * "apis":"[{\"bankCode\":\"ICBC\",\"bankName\":\"工商银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1879590264.png\",\"memo\":\"工商银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"icbc2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"ICBC\",\"bankName\":\"工商银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1879590264.png\",\"memo\":\"工商银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"icbc2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"ABC\",\"bankName\":\"农业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/农业银行.gif\",\"memo\":\"农业银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"abc2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"ABC\",\"bankName\":\"农业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/农业银行.gif\",\"memo\":\"农业银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"abc2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"ABC\",\"bankName\":\"农业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/农业银行.gif\",\"memo\":\"农业银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"abc2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"ABC\",\"bankName\":\"农业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/农业银行.gif\",\"memo\":\"农业银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"abc2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CCB\",\"bankName\":\"建设银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1070010641.png\",\"memo\":\"建设银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ccb2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CCB\",\"bankName\":\"建设银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1070010641.png\",\"memo\":\"建设银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ccb2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"COMM\",\"bankName\":\"交通银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2049777450.png\",\"memo\":\"交通银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"comm2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"COMM\",\"bankName\":\"交通银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2049777450.png\",\"memo\":\"交通银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"comm2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"COMM\",\"bankName\":\"交通银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2049777450.png\",\"memo\":\"交通银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"comm2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"COMM\",\"bankName\":\"交通银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2049777450.png\",\"memo\":\"交通银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"comm2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"BOC\",\"bankName\":\"中国银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/中国银行.gif\",\"memo\":\"中国银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"boc2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"BOC\",\"bankName\":\"中国银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/中国银行.gif\",\"memo\":\"中国银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"boc2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"BOC\",\"bankName\":\"中国银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/中国银行.gif\",\"memo\":\"中国银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"boc2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"BOC\",\"bankName\":\"中国银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/中国银行.gif\",\"memo\":\"中国银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"boc2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CEB\",\"bankName\":\"光大银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2094620379.png\",\"memo\":\"光大银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ceb2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CEB\",\"bankName\":\"光大银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2094620379.png\",\"memo\":\"光大银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ceb2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CEB\",\"bankName\":\"光大银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2094620379.png\",\"memo\":\"光大银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ceb2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CEB\",\"bankName\":\"光大银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2094620379.png\",\"memo\":\"光大银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"ceb2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CMBC\",\"bankName\":\"民生银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2483708159.png\",\"memo\":\"民生银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmbc2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CMBC\",\"bankName\":\"民生银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2483708159.png\",\"memo\":\"民生银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmbc2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CMBC\",\"bankName\":\"民生银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2483708159.png\",\"memo\":\"民生银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmbc2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CMBC\",\"bankName\":\"民生银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2483708159.png\",\"memo\":\"民生银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmbc2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CITIC\",\"bankName\":\"中信银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2546016383.png\",\"memo\":\"中信银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"citic2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CITIC\",\"bankName\":\"中信银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2546016383.png\",\"memo\":\"中信银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"citic2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CITIC\",\"bankName\":\"中信银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2546016383.png\",\"memo\":\"中信银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"citic2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CITIC\",\"bankName\":\"中信银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2546016383.png\",\"memo\":\"中信银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"citic2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"HXB\",\"bankName\":\"华夏银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1323060379.png\",\"memo\":\"华夏银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"hxb2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"HXB\",\"bankName\":\"华夏银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1323060379.png\",\"memo\":\"华夏银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"hxb2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"HXB\",\"bankName\":\"华夏银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1323060379.png\",\"memo\":\"华夏银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"hxb2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"HXB\",\"bankName\":\"华夏银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1323060379.png\",\"memo\":\"华夏银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"hxb2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CQRCB\",\"bankName\":\"重庆农村商业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1174227264.png\",\"memo\":\"重庆农村商业银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cqrcb2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CQRCB\",\"bankName\":\"重庆农村商业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1174227264.png\",\"memo\":\"重庆农村商业银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cqrcb2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CQRCB\",\"bankName\":\"重庆农村商业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1174227264.png\",\"memo\":\"重庆农村商业银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cqrcb2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CQRCB\",\"bankName\":\"重庆农村商业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1174227264.png\",\"memo\":\"重庆农村商业银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cqrcb2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"PSBC\",\"bankName\":\"邮政储蓄银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1360012487.png\",\"memo\":\"邮政储蓄银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"psbc2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"PSBC\",\"bankName\":\"邮政储蓄银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1360012487.png\",\"memo\":\"邮政储蓄银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"psbc2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"PSBC\",\"bankName\":\"邮政储蓄银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1360012487.png\",\"memo\":\"邮政储蓄银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"psbc2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"PSBC\",\"bankName\":\"邮政储蓄银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/1360012487.png\",\"memo\":\"邮政储蓄银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"psbc2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CIB\",\"bankName\":\"兴业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/兴业银行.gif\",\"memo\":\"兴业银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cib2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CIB\",\"bankName\":\"兴业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/兴业银行.gif\",\"memo\":\"兴业银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cib2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CIB\",\"bankName\":\"兴业银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/兴业银行.gif\",\"memo\":\"兴业银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cib2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CIB\",\"bankName\":\"兴业银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/task/2012-05/07/pub/兴业银行.gif\",\"memo\":\"兴业银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cib2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CMB\",\"bankName\":\"招商银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2511587372.png\",\"memo\":\"招商银行B2C提现借记卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmb2010\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CMB\",\"bankName\":\"招商银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2511587372.png\",\"memo\":\"招商银行B2C提现信用卡对私\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmb2011\",\"publicTag\":\"N\",\"state\":\"Y\"},{\"bankCode\":\"CMB\",\"bankName\":\"招商银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2511587372.png\",\"memo\":\"招商银行B2B提现借记卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmb2110\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CMB\",\"bankName\":\"招商银行\",\"batch\":\"N\",\"cardType\":\"CREDIT_CARD\",\"channelNo\":\"WITHDRAW_B2B\",\"env\":\"ONLINE\",\"logoUrl\":\"https://www.yiji.com/assets/upload/bank/2012-05/08/2511587372.png\",\"memo\":\"招商银行B2B提现信用卡对公\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"cmb2111\",\"publicTag\":\"Y\",\"state\":\"Y\"},{\"bankCode\":\"CITIC\",\"bankName\":\"中信银行\",\"batch\":\"N\",\"cardType\":\"DEBIT_CARD\",\"channelNo\":\"WITHDRAW_DIRECT_B2C\",\"env\":\"ONLINE\",\"logoUrl\":\"\",\"memo\":\"\",\"owner\":\"DEFAULT\",\"payChannelApi\":\"citic2210\",\"publicTag\":\"N\",\"state\":\"Y\"}]","success":"true","signType":"MD5","orderNo":"2013060100000024","notifyTime":"2013-06-01 14:37:43","partnerId":"20130319020011682493","sign":"2253d0f52f2ef36a6c55111e8840a1db"}
	 */
	public static  QueryPayChannelApi  queryPayChannelApi( ) {
		QueryPayChannelApi qpc = new QueryPayChannelApi();
		qpc.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		qpc.setService("queryPayChannelApi");
		qpc.setApiType("WITHDRAW");
		qpc.setCardType("DEBIT_CARD");
		qpc.setOwner("DEFAULT");
		qpc.setPayType("WITHDRAW");
		qpc.setOwner("EASY_TURNOVER");
		qpc.setPublicTag("N");
		doSubmit(qpc);
		return qpc;
	}
	
	/**
	 * 解绑银行卡
	 * @param districtName
	 * @param bankId
	 * @return
	 */
	public static BankNoQuery bankNoQuery (String districtName, String  bankId){
		BankNoQuery bq = new BankNoQuery();
		
	    bq.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
	    bq.setService("bankNoQuery");
	    bq.setBankId(bankId);
	    bq.setDistrictName(districtName);
	    String res = doSubmit(bq);
	    Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
	    if(map.get("bankLasalle") == null ){
	    	bq.setBankLasalle("");
	    }else{
	    	bq.setBankLasalle(map.get("bankLasalle")+"");
	    }
	    bq.setBranchName(map.get("branchName")+"");
		return bq;
		
	}
	
	/**
	 * 用户提现申请
	 * @param user
	 * @param account
	 * @param money
	 * @param province
	 * @param city
	 * @param cashNum 成功取现的笔数
	 * {"errCodeCtx":{"code":"EXECUTE_SUCCESS","memo":"处理成功","message":""},"instructionInfo":{"accountName":"qwe11","accountNo":"20130602010055091699","accountType":"PERSONAL","bankAccountName":"周学成","bankAccountNo":"6222021408000791818","bankCnapsNo":"102331002614","bankCode":"CIB","bankName":"兴业银行","bizIdentity":"SPECIAL_MERCHANT","memo":"提现","outBizContext":{"CHARGE_OPTION":"Y","SPECIAL_EASY_TRADE":"N","WITHDRAW_INTER_FACADE_FLAG":"N"},"outBizNo":"2013062000000022","payAmount":10.00,"payAmountCurrency":"CNY","payAmountIn":10.00,"rawAddTime":1371716015000,"receiptUrl":"http://192.168.0.99:10023/services/applyDeductDepositPayengineCallbackService","status":"SUBMIT_SETTLED","subContractCode":"ASYNC_WITHDRAW_CONTRACT","withdrawId":"20130620033055162917","withdrawType":"WITHDRAW_SINGLE"},"message":"处理成功","resultCode":"EXECUTE_SUCCESS","success":true}
	 */
	public static ApplyWithDraw applyWithDraw(String apiId,String realName,String channelApi, String account,
			String money, String province, String city, String bankCnapsNo,String bankCode,String cardType,
			String publicTag,String drawType,String cashNum){
		String weburl=Global.getValue("weburl");  
		ApplyWithDraw awd = new ApplyWithDraw();
		
		awd.setService("applyWithdraw");
		String orderNo = OrderNoUtils.getInstance().getSerialNumber();
		awd.setOrderNo(orderNo);
		//现在提现不走支付渠道了，这里传空
		awd.setUserId(apiId);
		awd.setAmount(money);
		awd.setBizIdentity("EASY_TURNOVER");
		awd.setBizNo("520");
		awd.setOutBizNo(orderNo); 
		awd.setBankAccountNo(account);
		awd.setBankAccountName(realName);
		awd.setBankCnapsNo(bankCnapsNo);//联行号必须有
		
		awd.setRealName(realName);
		awd.setProvName(province);
		awd.setCityName(city);
		awd.setBankCode(bankCode);
		awd.setApplyIsInter("N");//不用审核
		awd.setOwner("EASY_TURNOVER");
		awd.setCardType(cardType);
		awd.setPublicTag(publicTag);
		awd.setNotifyUrl(weburl+"/public/verifyCashBack.html");
		
		if(drawType.equals("1")){//即可取现
			logger.info("drawType:"+drawType);
			logger.info("已取现成功笔数："+cashNum);
			//v1.8.0.4_u3 TGPROJECT-338 qinjun 2014-06-23 start
			 if(NumberUtils.getInt(cashNum) < Global.getInt("cash_num")){//前几笔不收取现手续费。
			//v1.8.0.4_u3 TGPROJECT-338 qinjun 2014-06-23 end
				 	awd.setTradeBizProductCode(Global.getValue("draw_money_off"));
				 	logger.info("t+0小于等于笔数时："+Global.getValue("draw_money_off"));
			 }else{//正常取现
				 awd.setTradeBizProductCode(Global.getValue("draw_money_t0")); //提现t+0
				 logger.info("t+0大于笔数时："+Global.getValue("draw_money_t0"));
			 }
		}else{//24小时之内到账
			//v1.8.0.4_u3 TGPROJECT-338 qinjun 2014-06-23 start
			 if(NumberUtils.getInt(cashNum) < Global.getInt("cash_num")){//前几笔不收取现手续费。
			//v1.8.0.4_u3 TGPROJECT-338 qinjun 2014-06-23 end
					awd.setTradeBizProductCode(Global.getValue("draw_money_off"));
					logger.info("t+1小于等于笔数时："+Global.getValue("draw_money_off"));
			 }else{//正常取现
				 awd.setTradeBizProductCode(Global.getValue("draw_money_t1")); //提现t+1
				 logger.info("t+1大于笔数时："+Global.getValue("draw_money_t0"));
			 }
		}	
		if(isOnlineConfig()){//线上环境
		}else{
			awd.setTradeBizProductCode(Global.getValue("draw_money_t0"));
			logger.info("线下大于笔数时："+Global.getValue("draw_money_t0"));
		}
		awd.setTradeMerchantId(Global.getValue("yjf_partnerId"));//平台商，在带个交易之中提供平台服务的商家	
		logger.info("传给doSubmit的参数"+awd.getTradeBizProductCode());
		String res = doSubmit(awd);
		Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
		awd.setResultCode(map.get("resultCode")+"");
		awd.setMessage(map.get("message")+"");
		return awd;
	}
	
	
	//v1.8.0.4_u4 TGPROJECT-360  qinjun  2014-07-11  start
	/**
	 * 新提现申请
	 * @param user
	 * @param account
	 * @param money
	 * @param province
	 * @param city
	 * @param cashNum 成功取现的笔数
	 * {"errCodeCtx":{"code":"EXECUTE_SUCCESS","memo":"处理成功","message":""},"instructionInfo":{"accountName":"qwe11","accountNo":"20130602010055091699","accountType":"PERSONAL","bankAccountName":"周学成","bankAccountNo":"6222021408000791818","bankCnapsNo":"102331002614","bankCode":"CIB","bankName":"兴业银行","bizIdentity":"SPECIAL_MERCHANT","memo":"提现","outBizContext":{"CHARGE_OPTION":"Y","SPECIAL_EASY_TRADE":"N","WITHDRAW_INTER_FACADE_FLAG":"N"},"outBizNo":"2013062000000022","payAmount":10.00,"payAmountCurrency":"CNY","payAmountIn":10.00,"rawAddTime":1371716015000,"receiptUrl":"http://192.168.0.99:10023/services/applyDeductDepositPayengineCallbackService","status":"SUBMIT_SETTLED","subContractCode":"ASYNC_WITHDRAW_CONTRACT","withdrawId":"20130620033055162917","withdrawType":"WITHDRAW_SINGLE"},"message":"处理成功","resultCode":"EXECUTE_SUCCESS","success":true}
	 */
	public static YzzNewWithraw yzzNewWithraw(String apiId,String realName,String channelApi, String account,
			String money, String province, String city, String bankCnapsNo,String bankCode,String cardType,
			String publicTag,String drawType,String cashNum){
		String weburl=Global.getValue("weburl");  
		YzzNewWithraw awd = new YzzNewWithraw();
		
		awd.setService("yzzNewWithraw");
		String orderNo = OrderNoUtils.getInstance().getSerialNumber();
		awd.setOrderNo(orderNo);
		//现在提现不走支付渠道了，这里传空
		awd.setUserId(apiId);
		awd.setMoney(money);
		awd.setBankProvName(province);
		awd.setBankCityName(city);
		awd.setBankCode(bankCode);
		awd.setBankAccountNo(account);
		awd.setDelay(drawType);
		awd.setNotifyUrl(weburl+"/public/cashNotify.html");
		
		if(NumberUtils.getInt(cashNum) < Global.getInt("cash_num")){//前几笔不收取现手续费。
			logger.info("t+0小于等于笔数时，用户自己平台承担费用");
			awd.setPayMode("P");
		}else{//正常取现
			logger.info("t+0大于笔数时,用户自己承担费用");
			awd.setPayMode("U");
		}
		String res = doSubmit(awd);
		Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
		awd.setResultCode(map.get("resultCode")+"");
		awd.setResultMessage(map.get("message")+"");
		return awd;
	}
	//v1.8.0.4_u4 TGPROJECT-360  qinjun  2014-07-11  end
	
	/**
	 * 提现记录查询
	 * @return 
	 */
	public static WithdrawQquery withdrawQquery(String currPage, String apiId){
		WithdrawQquery wq = new WithdrawQquery();
		wq.setService("withdraw.query");
		wq.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		wq.setCurrPage(currPage);
		wq.setUserId(apiId);
        String res = doSubmit(wq);  //封装信息
        Map<String, Object> map = (Map<String, Object>)JSON.parse(res);
        wq.setAmounts(map.get("amounts")+"");
        wq.setAmountsIn(map.get("amountsIn")+"");
        wq.setCharges(map.get("charges")+"");
        wq.setWithdrawInfos(map.get("withdrawInfos")+"");
        wq.setErrCodeCtx(map.get("errCodeCtx")+"");
        wq.setMessage(map.get("message")+"");
        wq.setResultCode(map.get("resultCode")+"");
        wq.setSuccess(map.get("success")+"");
		return wq;
	}
	
	/**
	 * 转账
	 */
	public static TradeTransfer tradeTransfer(String sellerUserId,String payerUserId, String money,String tradeName){
		TradeTransfer tt = new TradeTransfer();
		
		tt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tt.setService("tradeTransfer");
		tt.setSellerUserId(sellerUserId); //收款人
		tt.setPayerUserId(payerUserId); // 付款人
		tt.setBuyerUserId(payerUserId);// 付款人
		String name =  payerUserId + "转账给" + sellerUserId + ",money:" + money;
		tt.setTradeName(tradeName);
		if(isOnlineConfig()){
			tt.setTradeBizProductCode(Global.getValue("person_transfer"));//转账
		}
		tt.setProduct("transfer");
		tt.setTradeType("transfer");
		tt.setGatheringType("SERVICE_BUY");
		tt.setTradeAmount(money);
		tt.setCurrency("CNY");
		String goodStr = "[{'name': '" + name + "','price':" + "'"+ money + "','quantity':'1'}]";
		tt.setGoods(goodStr);
		
		String res = doSubmit(tt);
		Map<String, String> map = (Map<String, String>)JSON.parse(res); // 封装返回信息
		tt.setResultCode(map.get("resultCode"));
		tt.setResultMessage(map.get("resultMessage"));
		
		return tt;
	}
	
/*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓下面是给力标【普通标】 投标过程↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
	/**
	 * 给力标创建
	 * 集体集资交易创建接口tradeCreatePoolTogether
	 *  {"channelId":"00001d",
	 *  "orderNo":"2013060100000029",
	 *  "resultCode":"EXECUTE_SUCCESS",
	 *  "resultMessage":"执行成功",
	 *  "sign":"b36fe9d30e6ec162447798f1c70d82d3",
	 *  "signType":"MD5",
	 *  "subTradeNo":"null",
	 *  "success":"T",
	 *  "tradeNo":"20130601100055091252"}
	 */
	public static TradeCreatePoolTogether  tradeCreatePoolTogether(String money, String  apiId){
		TradeCreatePoolTogether  tpt = new TradeCreatePoolTogether();
		
		tpt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tpt.setService("tradeCreatePoolTogether");
		tpt.setTradeName(Global.getValue("trade_name_addborrow"));
		tpt.setSellerUserId(apiId);
		if(isOnlineConfig()){
			tpt.setTradeBizProductCode(Global.getValue("borrow_create"));  //给力标，创建产品编号
		}else{
			tpt.setTradeBizProductCode("20130326_together");  //线下
		}
		tpt.setProduct("POOL_TOGETHER");
		tpt.setTradeAmount(money);
		tpt.setTradeType("POOL_TOGETHER");
		tpt.setGatheringType("SERVICE_BUY");
		tpt.setCurrency("CNY");
		tpt.setGoods("[{'name':'发标创建交易号" +"','price':'" + money + "','quantity':'1'}]");
	    String res = doSubmit(tpt);
	    Map<String, String> map = (Map<String, String>)JSON.parse(res); // 封装返回信息
	    tpt.setChannelId(map.get("channelId"));
	    tpt.setResultCode(map.get("resultCode"));
	    tpt.setResultMessage(map.get("resultMessage"));
	    tpt.setSuccess(map.get("success"));
	    tpt.setTradeNo(map.get("tradeNo"));
	    return tpt;
	}
	
	 /**
     * 交易借出人申请冻结资金
     * {"channelId":"00001d",
     * "orderNo":"2013060100000033",
     * "resultCode":"EXECUTE_SUCCESS",
     * "resultData":"{\"SUB_TRADE_NO\":\"20130601100055091267\",\"TRADE_MESSAGE\":\"执行成功\",\"TRADE_RESULT_ENUM\":\"EXECUTE_SUCCESS\"}",
     * "resultMessage":"执行成功",
     * "sign":"2395b6bd81eda556d43590b2d2554083",
     * "signType":"MD5",
     * "subTradeNo":"20130601100055091267",
     * "success":"T",
     * "tradeNo":"20130601100055091252"}
     */
    public static TradePayerApplyPoolTogether tradePayerApplyPoolTogether(String payerUserId, String money, String tradeNo){
    	TradePayerApplyPoolTogether tpp = new TradePayerApplyPoolTogether();
    	
        tpp.setService("tradePayerApplyPoolTogether");
        String number =  OrderNoUtils.getInstance().getSerialNumber();
        tpp.setOrderNo(number);
        tpp.setTradeNo(tradeNo);
        tpp.setPayerUserId(payerUserId);
        tpp.setTradeAmount(money);
        tpp.setTradeName(Global.getValue("trade_name_addtender"));
        String res = doSubmit(tpp);
        Map<String, String>  map  = (Map<String, String>)JSON.parse(res);
        tpp.setResultCode(map.get("resultCode"));
        tpp.setSubTradeNo(map.get("subTradeNo"));
        tpp.setResultMessage(map.get("resultMessage"));
        return tpp;
    }
    
    /**
     *  交易借出人申请退出集资
     * @param tradeNo
     * @param subTradeNo
     * @param payApiId   交易用户的id  易极付 那边对应的用户的id
     * @return
     * {"channelId":"00001d",
     * "orderNo":"2013060100000036",
     * "resultCode":"EXECUTE_SUCCESS",
     * "resultData":"{\"SUB_TRADE_NO\":\"20130601100055091267\"}",
     * "resultMessage":"执行成功",
     * "sign":"e850827f83a6d7622e77084d4eed398c",
     * "signType":"MD5",
     * "subTradeNo":"20130601100055091267",
     * "success":"T",
     * "tradeNo":"20130601100055091252"}
     */
    public static TradePayerQuitPoolTogether tradePayerQuitPoolTogether(String tradeNo, String subTradeNo,String payApiId){
    	TradePayerQuitPoolTogether tqt = new TradePayerQuitPoolTogether();
    	
    	tqt.setService("tradePayerQuitPoolTogether");
    	tqt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
    	tqt.setPayerUserId(payApiId);
    	tqt.setTradeNo(tradeNo);
    	tqt.setTradeMemo(Global.getValue("trade_name_cancelborrow"));
    	tqt.setSubTradeNo(subTradeNo);
    	String res = doSubmit(tqt);
    	Map<String, String> map = (Map<String, String>)JSON.parse(res);
    	tqt.setResultCode(map.get("resultCode"));
    	tqt.setResultMessage(map.get("resultMessage"));
    	return tqt;
    }
	
    /**
	 * 设置交易集体付款    这里只是处理  放款的过程中的手续费其他的不处理，  transferAmount 代表处理的手续费用
	 * {"channelId":"00001d",
		"info":"[{}]"
		"orderNo":"2013060200000041",
		"resultCode":"EXECUTE_SUCCESS",
		"resultMessage":"执行成功",
		"sign":"baa3ad8225cb40f7e9f9393078b4d0f2",
		"signType":"MD5","subTradeNo":"null","success":"T",
		"tradeNo":"20130602100055093491"}
	 * tradePayPoolTogether
	 */
	public static TradePayPoolTogether tradePayPoolTogether(String tradeNo){
	    TradePayPoolTogether tt = new TradePayPoolTogether();
	    
	    tt.setService("tradePayPoolTogether");
	    tt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
	    tt.setTradeNo(tradeNo);
	    tt.setTradeMemo(Global.getValue("trade_name_verifysuccess"));
	    String res =  doSubmit(tt);
	    Map<String, String> map = (Map<String, String>)JSON.parse(res);
	    tt.setInfo(map.get("info"));
	    tt.setResultCode(map.get("resultCode"));
	    tt.setResultMessage(map.get("resultMessage"));
	    return tt;
	}
	
	/**
	 * 给力式集资关闭交易
	 * {"channelId":"00001d","orderNo":"0101333546721111","resultCode":"EXECUTE_SUCCESS",
	 * "resultMessage":"执行成功","sign":"944e47269519baa920005a24c00b789d","signType":"MD5",
	 * "subTradeNo":"null","success":"T","tradeNo":"20130326000056639274"}
	 */
	public static TradeClosePoolTogether tradeClosePoolTogether(String tradeNo){
		TradeClosePoolTogether tt = new TradeClosePoolTogether();
		
		 tt.setService("tradeClosePoolTogether");
		 tt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		 tt.setTradeNo(tradeNo);
		 String res =  doSubmit(tt);
		 Map<String, String > map = (Map<String, String >)JSON.parse(res);
		 tt.setResultCode(map.get("resultCode"));
		 tt.setResultMessage(map.get("resultMessage"));
		 return tt;
	}
	
/*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓下面是流转标 投标过程↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
	/**
	 * 流转标
	 * 集资创建交易接口和普通标创建一样只是参数有变化  那就用同一个吧   大家写的时候要注意
	 * 
	 */
	public static TradeCreatePoolTogether tradeCreatePool(String apiId,String money){
		TradeCreatePoolTogether  tpt = new TradeCreatePoolTogether();
		
		tpt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tpt.setService("tradeCreatePool");
		tpt.setTradeName(Global.getValue("trade_name_addborrow"));
		tpt.setSellerUserId(apiId);
		if(isOnlineConfig()){
			tpt.setTradeBizProductCode(Global.getValue("borrow_flow_create"));  //流转标产品编号
		}else{
			tpt.setTradeBizProductCode("20130326_pool");  // 要确定
		}
		tpt.setProduct("POOL"); //流程产品（测试是随便给定值）  以后要确定
		tpt.setTradeAmount(money);
		tpt.setTradeType("POOL");
		tpt.setGatheringType("SERVICE_BUY");
		tpt.setCurrency("CNY");
		tpt.setGoods("[{'name':'流标创建交易号" +"','price':'" + money + "','quantity':'1'}]");
	    String res = doSubmit(tpt);
	    Map<String, String> map = (Map<String, String>)JSON.parse(res); // 封装返回信息
	    tpt.setChannelId(map.get("channelId"));
	    tpt.setResultCode(map.get("resultCode"));
	    tpt.setResultMessage(map.get("resultMessage"));
	    tpt.setSuccess(map.get("success"));
	    tpt.setTradeNo(map.get("tradeNo"));
	    return tpt;
	}
	
	/**
	 * 流转标款付款接口,没有冻结  直接转账
	 */
	public static TradePoolReceiveBorrow tradePoolReceiveBorrow(YjfPay yjfPay){
		TradePoolReceiveBorrow tt = new TradePoolReceiveBorrow();
		tt.setService("tradePoolReceiveBorrow");
	    tt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
	    tt.setTradeNo(yjfPay.getTradeno());
	    String tenderStr = yjfPay.getUserid();
	    String[] tenders = tenderStr.split("=");  
	    StringBuffer sbf =  new StringBuffer();
		sbf.append("{");
		sbf.append("'orderNo':'" + OrderNoUtils.getInstance().getSerialNumber() + "'," );
		sbf.append("'payerUserId':'" + tenders[0] + "',");
		sbf.append("'payeeUserId':'" + yjfPay.getTouserid() + "',"); //收款人
		sbf.append("'transferAmount':'" + tenders[1] + "',");
		sbf.append("'tradeName':'" + Global.getValue("trade_name_addflowtender") + "'");
		sbf.append("}");
		String subOrders = "[" + sbf.toString() + "]";
	    tt.setTradePoolSubTansferOrders(subOrders);
	    String res =  doSubmit(tt);
	    Map<String, String> map = (Map<String, String>)JSON.parse(res);
	    tt.setInfo(map.get("info"));
	    tt.setResultCode(map.get("resultCode"));
	    tt.setResultMessage(map.get("resultMessage"));
	    return tt;
	}
	
	/**
	 * 集资借款交易完成
	 * 
	 */
	public static TradeClosePoolTogether tradeFinishPool(String tradeNo) {
		TradeClosePoolTogether tt = new TradeClosePoolTogether();
		
		 tt.setService("tradeFinishPool");
		 tt.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		 tt.setTradeNo(tradeNo);
		 String res =  doSubmit(tt);
		 Map<String, String > map = (Map<String, String >)JSON.parse(res);
		 tt.setResultCode(map.get("resultCode"));
		 tt.setResultMessage(map.get("resultMessage"));
		 return tt;
	}
	
/*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓下面是还款流程用到的方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
	/**
	 * 集资还款交易创建
	 * tradeFinishPoolReverse
	 * {"channelId":"00001d",
	 * "orderNo":"2013060200000052",
	 * "resultCode":"EXECUTE_SUCCESS",
	 * "resultMessage":"执行成功",
	 * "sign":"ea5cfcf191842ad5cb6bb4147199bc7f",
	 * "signType":"MD5","subTradeNo":"null",
	 * "success":"T",
	 * "tradeNo":"20130602100055093535"}
	 */
	public static TradeCreatePoolReverse tradeCreatePoolReverse(String payerUserId, String tradeAmount){
		TradeCreatePoolReverse tc = new TradeCreatePoolReverse();
		
		tc.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tc.setService("tradeCreatePoolReverse");
		String name = Global.getValue("trade_name_create_repay"); 
		tc.setTradeName(name);
		tc.setPayerUserId(payerUserId);
		if(isOnlineConfig()){
			tc.setTradeBizProductCode(Global.getValue("borrow_repay")); //还款,产品编号
		}else{
			tc.setTradeBizProductCode("20130326_reverse");
		}
		tc.setTradeAmount(tradeAmount);
		tc.setProduct("POOL_REVERSE");
		tc.setTradeType("POOL_REVERSE");
		tc.setGatheringType("SERVICE_BUY");
		tc.setCurrency("CNY");
		String goodStr = "[{'name':" + "'" + name + "','price':" + "'" + tradeAmount + "','quantity':'1'}]";
		tc.setGoods(goodStr);
		
		String res = doSubmit(tc);
		Map<String,String> map = (Map<String,String>)JSON.parse(res);
		tc.setResultCode(map.get("resultCode"));
		tc.setResultMessage(map.get("resultMessage"));
		tc.setTradeNo(map.get("tradeNo"));
		return tc;
	}
	
	/**
	 * 集资交易还款接口
	 * @param tradeNo
	 * @param payerUserId  付款人
	 * @param tenders  所有的收款人  包括 apiId  对应的  money  封装的 二维数组
	 *  {"channelId":"00001d",
		 "info":"[{}]"
		 "orderNo":"2013060200000053",
		 "resultCode":"EXECUTE_SUCCESS",
		 "resultMessage":"执行成功",
		 "sign":"bacfddd7c46772beaea644df64043b90","signType":"MD5","subTradeNo":"null",
		 "success":"T","tradeNo":"20130602100055093535"}
	 */
	public static TradePayPoolReverse tradePayPoolReverse(String tradeNo,String payerUserId, String tender,String money ){
		TradePayPoolReverse tpp = new TradePayPoolReverse();
		
		tpp.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tpp.setService("tradePayPoolReverse");
		tpp.setTradeNo(tradeNo);
		StringBuffer sbf =  new StringBuffer();
		sbf.append("{");
		sbf.append("'orderNo':'" + OrderNoUtils.getInstance().getSerialNumber() + "'," );
		sbf.append("'payerUserId':'" + payerUserId + "',");
		sbf.append("'payeeUserId':'" + tender + "',"); //收款人
		sbf.append("'transferAmount':'" + money + "',");
		sbf.append("'tradeName':'" + Global.getValue("trade_name_repay") + "'");
		sbf.append("}");
		String subOrders = "[" + sbf.toString() + "]";
		tpp.setSubOrders(subOrders);
	    String res = doSubmit(tpp);	   
	    Map<String, String> map =  (Map<String, String>)JSON.parse(res);
	    tpp.setInfo(map.get("info"));
	    tpp.setResultCode(map.get("resultCode"));
	    tpp.setResultMessage(map.get("resultMessage"));
	    return tpp;
	}
	
	/**
	 * 集资借款交易完成
	 * tradeFinishPool
	 */
	public static TradeClosePoolTogether tradeFinishPoolReverse(String tradeNo){
		TradeClosePoolTogether tfr = new TradeClosePoolTogether();  // 这里公用一个
		
		tfr.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		tfr.setService("tradeFinishPoolReverse");
		tfr.setTradeNo(tradeNo);
		String res = doSubmit(tfr);
		Map<String, String> map = (Map<String, String>)JSON.parse(res);
		tfr.setResultCode(map.get("resultCode"));
		tfr.setResultMessage(map.get("resultMessage"));
		
		return tfr;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	public static NewAuthorize yjfRealName(User user){
		NewAuthorize na = new NewAuthorize();
		na.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		na.setService("new_authorize");
		na.setUserId(user.getApiId());
		na.setNotifyUrl(Global.getValue("weburl")+"/public/yjfRealNameNotify.html");
		na.setReturnUrl(Global.getValue("weburl")+"/member/identify/apiRealname.html");
		na.Createsign();
		return na;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	public static NewAuthorizeQuery yjfRealNameQuery(User user){
		NewAuthorizeQuery query = new NewAuthorizeQuery();
		query.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
		query.setService("new_authorize_query");
		query.setUserId(user.getApiId());
		String result = doSubmit(query);
        if(!StringUtils.isBlank(result)){
           Map<String,String> map = (Map<String, String>)JSON.parse(result);
       	   //封装返回的信息
          query.setMessage(map.get("message"));
          query.setMethod(map.get("method"));
          query.setStatus(map.get("status"));
          query.setResultMessage(map.get("resultMessage"));
        }
        return query;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end
//	/**
//	 * 易极付新注册
//	 * @param user
//	 * @return
//	 */
//    public static ForwardConIdentify userForwardRegister(User user){
//    	ForwardConIdentify userRegister = new ForwardConIdentify();
//    	userRegister.setService("forwardConIdentify");
//        userRegister.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
//        userRegister.setUserName(Global.getValue("webid") + user.getUsername());
//        if("1".equals(Global.getValue("company_register"))){//开通企业注册
//        	userRegister.setUserType("B");
//        }else{//开通个人注册。
//        	userRegister.setUserType("P");
//        }
//        userRegister.setRealName(user.getRealname());//现在易极付这边可以随便填，已实名为主
//        userRegister.setEmail(user.getEmail());
//        userRegister.setCertNo(user.getCardId());
//        userRegister.Createsign();
//        userRegister.setReturnUrl(Global.getValue("weburl")+"/member/main.html");
//        userRegister.setNotifyUrl(Global.getValue("weburl")+"/public/forwardRegisterNotify.html");
//    	return userRegister;
//    }
	
	/**
	 * 用户信息查询
	 * @return
	 */
	public static UserInfoQuery userInfoQuery(String apiId){
		UserInfoQuery uq = new UserInfoQuery();
		try {
			uq.setService("userInfoQuery");
			uq.setOrderNo(OrderNoUtils.getInstance().getSerialNumber());
			uq.setUserId(apiId);
			String returnStr = doSubmit(uq);
			
			Map<String, String> returnMap = (Map<String,String>)JSON.parse(returnStr);
			//只要 certifyLevel 不为 0 和 1 就行。
			uq.setCertifyLevel(returnMap.get("certifyLevel"));
			
		} catch (Exception e) {
			logger.error("查询用户信息错误", e);
		}
		return uq;
	}
}
