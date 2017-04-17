package com.liangyou.web.action.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.api.chinapnr.NetSave;
import com.liangyou.api.pay.DepositQuery;
import com.liangyou.api.pay.UserAccountQuery;
import com.liangyou.api.pay.WithdrawQquery;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountWebDeduct;
import com.liangyou.domain.Area;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.GlodTransfer;
import com.liangyou.domain.OperateLog;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.Rule;
import com.liangyou.domain.SitePayLog;
import com.liangyou.domain.TenderReward;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.domain.UserType;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.WebAccountSumModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiCashService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.BorrowTenderService;
import com.liangyou.service.LogService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RuleService;
import com.liangyou.service.TenderRewardService;
import com.liangyou.service.UserInvitateCodeService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.tool.coder.MD5;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

@Namespace("/admin/account")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageAccountAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ManageAccountAction.class);
	@Autowired
	private BorrowTenderService borrowTenderService;
	@Autowired
	private UserInvitateCodeService userInvitateCodeService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private LogService logService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private OperateService operateService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private ApiCashService apiCashService; 
	
	@Autowired
	private TenderRewardService tenderRewardService;
	@Action(value="starScoreLog",
			results={@Result(name="starScoreLog",type="ftl",location="/admin/account/starScoreLog.html")}
			)
	public String starScoreLog() throws Exception {
		String username = paramString("username");
		String type = paramString("type");
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		
		SearchParam param=SearchParam.getInstance().addOrder(OrderType.DESC,"addtime").addPage(paramInt("page"));
		if(!StringUtils.isBlank(username)){
			param.addParam("user.username", Operator.LIKE,username);
		}
		if(!StringUtils.isBlank(startTime)){
			startTime += " 00:00:00";
			param.addParam("addtime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime += " 23:59:59";
			param.addParam("addtime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList data=ruleService.getStarLogList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
		    extraparam.put("dotime1", startTime);
		    extraparam.put("dotime2", endTime);
		setPageAttribute(data,extraparam); 
		
		if(type.isEmpty()){
			return "starScoreLog";
		}else{
			return null;
		}
		
	}
	
	@Action(value="cashForbid",
				results={@Result(name="success",type="redirect",location="/admin/account/showAllAccount.html")})
	public String cashForbid(){
		int userId=paramInt("userId");
		int type=paramInt("type");
		UserCache userCache=userService.getUserCache(userId);
		if(type==1 || type==0){
			userService.updateUserCache(userCache,type);
			return SUCCESS;
		}else{
			message("取现控制更新失败","/admin/account/showAllAccount.html");
			return ADMINMSG;
		}
	}

	// add by gy 2016-11-3 11:41:04  begin
	// 增加后台补单功能
	@Action(value="makeUpOrder",
			results={@Result(name="makeUpOrder",type="ftl",location="/admin/account/makeUpOrder.html")}
	)
	public String makeUpOrder() {
		return "makeUpOrder";
	}
	// end

	@Action(value="showAllAccount",
			results={@Result(name="showAllAccount",type="ftl",location="/admin/account/allaccount.html")}
			)
	public String showAllAccount() throws Exception {
		String username = paramString("username");
		double total = paramDouble("total");
		double use_money = paramDouble("useMoney");
		double no_use_money = paramDouble("noUseMoney");
		double collection = paramDouble("collection");
		double repay = paramDouble("repay");
		String type = paramString("type");
		//modify by lxm 优化企业账户查不到记录的问题 2017-3-22 14:10:41
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrFilter("user.userType",new UserType(2),new UserType(28));
		if(!StringUtils.isBlank(username)){
			param.addParam("user.username", Operator.LIKE,username);
		}
		if(total != 0){
			param.addParam("total", total);
		}
		if(use_money != 0){
			param.addParam("useMoney", use_money);
		}
		if(no_use_money != 0){
			param.addParam("noUseMoney", no_use_money);
		}
		if(collection != 0){
			param.addParam("collection", collection);
		}
		if(repay != 0){
			param.addParam("repay", repay);
		}
		PageDataList plist = accountService.getAccontList(param);
		WebAccountSumModel wsm=accountService.getWebAccountSumModel();
		request.setAttribute("wsm", wsm);
		setPageAttribute(plist, param);
		if(type.isEmpty()){
			return "showAllAccount";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="用户资金_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"user/.userId","user/.username","user/.realname",
					"total","useMoney","noUseMoney","collection","repay"};
			String[] titles=new String[]{"ID","用户名","真实姓名","总余额",
					"可用余额","冻结金额","待收金额","待还金额"};
			List list=accountService.getSumAccontList(param);
			ExcelHelper.writeExcel(infile, list, Account.class, Arrays.asList(names), Arrays.asList(titles));
			export(infile, downloadFile);
			return null;
		}
		
	}
	@Action(value="log",
			results={@Result(name="log",type="ftl",location="/admin/account/log.html")}
			)
	public String log() throws Exception {
		Map<String, Object> extraparam = new HashMap<String, Object>();//返回参数配置
		
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String type = paramString("type");
		String username = paramString("username");//用户名
		String tousername = paramString("tousername");//交易对方名称
		SearchParam param=SearchParam.getInstance()
				.addPage(paramInt("page"));		
		if(!StringUtils.isBlank(startTime)){
			extraparam.put("dotime1", startTime);
			param.addParam("addtime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			extraparam.put("dotime2", endTime);
			param.addParam("addtime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		if(!StringUtils.isBlank(type) && !"0".equals(type)){
			extraparam.put("type", type);
			param.addParam("type", type);
		}
		if(!StringUtils.isBlank(username)){
			extraparam.put("username", username);
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(tousername)){
			extraparam.put("tousername", tousername);
			param.addParam("toUser.username", Operator.LIKE, tousername);
		}
		param.addOrder(OrderType.DESC, "id");
		
		PageDataList plist=null;
		try {
			plist = accountService.getAccontLogList(param);
		} catch (Exception e) {
			logger.error(e);
		}
	    
		setPageAttribute(plist,extraparam);
		String excelType = paramString("excel");
		if(excelType.isEmpty()){
			return "log";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="资金记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"user/.username","user/.realname","toUser/.username","typeName",
					"money","total","useMoney","noUseMoney","collection","repay"
					,"remark","addtime","addip"};
			String[] titles=new String[]{"用户名","真实姓名","交易对方","交易类型",
					"操作金额","账户总额","可用金额","冻结金额","待收金额","待还"
							+ "金额"
					,"备注","时间","IP"};
			
			SearchParam param2=SearchParam.getInstance();		
			Date startDate =  DateUtils.getDate2(startTime);
			Date endTimeDate = DateUtils.getDate2(endTime);
			if(!StringUtils.isBlank(startTime) && startDate != null){
				param2.addParam("addtime", Operator.GTE ,startDate);
			}
			if(!StringUtils.isBlank(endTime) && endTimeDate != null){
				param2.addParam("addtime", Operator.LTE , endTimeDate);
			}
			if(!StringUtils.isBlank(type) && !"0".equals(type)){
				param2.addParam("type", type);
			}
			if(!StringUtils.isBlank(username)){
				param2.addParam("user.username", Operator.LIKE, username);
			}
			if(!StringUtils.isBlank(tousername)){
				param2.addParam("toUser.username", Operator.LIKE, tousername);
			}
			param2.addOrder(OrderType.ASC, "id");
			List list=accountService.getSumAccontLogList(param2);
			ExcelHelper.writeExcel(infile, list, AccountLog.class, Arrays.asList(names), Arrays.asList(titles));
			export(infile, downloadFile);
			return null;
		}
	}
	@Action(value="showAllCash",
			results={@Result(name="success",type="ftl",location="/admin/account/cash.html")}
			)
	public String showAllCash() throws Exception {
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");		
		String username = paramString("username");
		String status = paramString("status");
		SearchParam param=SearchParam.getInstance()
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC,"id");
		if(!StringUtils.isBlank(status)){
			if (status.equals("fail")) {
				param.addOrFilter("status", "2","3");
			}else{
				param.addParam("status", status);
			}
			
		}
		if(!StringUtils.isBlank(startTime)){
			param.addParam("addtime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("addtime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(username)){
			param.addParam("user.username", Operator.LIKE, username);
		}
		request.setAttribute("searchUrl", "/admin/account/showAllCash.html");
		PageDataList plist = accountService.getCashList(param);
		 Map<String, Object> extraparam = new HashMap<String, Object>();
		    extraparam.put("dotime1", startTime);
		    extraparam.put("dotime2", endTime);
		setPageAttribute(plist, param,extraparam);
		String type = paramString("type");
		if(type.isEmpty()){
			return SUCCESS;
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="提现记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","user/.username","user/.realname",
					"accountBank/.account","accountBank/.bank/.bankName","accountBank/.bank/.memo","total","credited"
					,"fee","addtime","statusStr"};
			String[] titles=new String[]{"ID","用户名称","真实姓名",
					"提现账号","提现银行","类型","提现总额","到账金额"
					,"手续费","提现时间","状态"};
			List list=accountService.getSumCashList(param);
			ExcelHelper.writeExcel(infile, list, AccountCash.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		
	}
	
	@Action(value="rechargelist",
			results={@Result(name="rechargelist",type="ftl",location="/admin/account/rechargelist.html")}
			)
	public String rechargelist() throws Exception {
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String username = paramString("username");
		String statusStr = paramString("status");
		String accounttypeStr = paramString("account_type");
		SearchParam param=SearchParam.getInstance()
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "addtime");
		
			if(!StringUtils.isBlank(startTime)){
				startTime += " 00:00:00";
				param.addParam("addtime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
			}
			if(!StringUtils.isBlank(endTime)){
				endTime += " 23:59:59";
				param.addParam("addtime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
			}
			
			if(!StringUtils.isBlank(username)){
				param.addParam("user.username", Operator.LIKE, username);
			}
			if(!StringUtils.isBlank(statusStr)){
				int status = NumberUtils.getInt(statusStr);
				param.addParam("status", Operator.EQ, status);
			}
			if(!StringUtils.isBlank(accounttypeStr)){
				int accounttype =NumberUtils.getInt(accounttypeStr);
				param.addParam("type", Operator.EQ, accounttype);
			}
		PageDataList plist = accountService.getRechargeList(param);
		 Map<String, Object> extraparam = new HashMap<String, Object>();
		    extraparam.put("dotime1", startTime);
		    extraparam.put("dotime2", endTime);
		setPageAttribute(plist, param,extraparam);
		String type = paramString("actionType");
		if(type.isEmpty()){
			return "rechargelist";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="充值记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","tradeNo","user/.username",
					"user/.realname","typeStr","money","fee","addtime","statusStr"};
			String[] titles=new String[]{"ID","流水号","用户名称",
					"真实姓名","类型","充值金额","费用","充值时间","状态"};
			List list=accountService.getSumRechargeList(param);
			ExcelHelper.writeExcel(infile, list, AccountRecharge.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		
	}
	
	
	@Action(value="addWebRecharge",
			results={@Result(name="success",type="ftl",location="/admin/account/webrecharge.html")}
			)
	public String addWebRecharge(){
		String type = paramString("type");
		if(type.equals("query")){
			String username = paramString("username");
			if(StringUtils.isBlank(username)){
				message("用户名不能为空","/admin/account/addWebRecharge.html");
				return ADMINMSG;
			}
			User user = userService.getUserByName(username);
			if(user == null){
				message("用户不存在","/admin/account/addWebRecharge.html");
				return ADMINMSG;
			}else if(user.getApiStatus() == 0){
				message("该用户尚未激活"+Global.getValue("api_name"),"/admin/account/addWebRecharge.html");
				return ADMINMSG;
			}else{
				//查询该用户可得邀请奖励总额
				double sumMoney = calculateInviteMoney(user.getUserId());
				request.setAttribute("user", user);
				request.setAttribute("sumMoney", sumMoney);
				return SUCCESS;
			}
		}
		return SUCCESS;
	}
	
	/**add by lijing 
	 * 计算当前充值用户可得奖励总额
	 * @param userId
	 * @return
	 */
	private double calculateInviteMoney(long userId) {
		double sumMoney = 0.00;
		final double RMB = 10000.00;
		// 邀请的人数
		List<UserInvitateCode> invitateCodeList = new ArrayList<UserInvitateCode>();
	
		invitateCodeList = userInvitateCodeService.getCodeListByPid(userId);
		// 通过邀请别人赚取的金额
		if (null == invitateCodeList || invitateCodeList.size() == 0) {
			return 0;
		}
		for (UserInvitateCode invitated : invitateCodeList) {// 循环多个我邀请的人    计算出我获得的佣金
			int Limit = 1;
			double money = 0.00;
			long invitatedId = invitated.getUser().getUserId();
			Date begin = userService.getAddTimeById(invitatedId);
			Date end = new Date();
			int deDay = DateUtils.getIntervalDays(begin, end);
			if (deDay > 10) {
				continue;
			}
			
			BorrowTender borrowTender = borrowTenderService.getListByUserId(invitatedId);// 被邀请人除体验金之外首次投资的金额
			if(borrowTender != null){
				money = borrowTender.getAccount();
				Borrow borrow = borrowTender.getBorrow();
				if(borrow.getIsday() == 0){
					if(borrow.getTimeLimit() == 2){
						Limit = 2;
					}
					if(borrow.getTimeLimit() >= 3){
						Limit = 3;
					}
				}
			}
			
			if (0.1 * RMB < money && money <= 3 * RMB) {
				sumMoney += money * 0.1 / 100*Limit;
			}
			if (3 * RMB < money && money <= 5 * RMB) {
				sumMoney += money * 0.15 / 100*Limit;
			}
			if (5 * RMB < money && money <= 8 * RMB) {
				sumMoney += money * 0.2 / 100*Limit;
			}
			if (8 * RMB < money && money <= 15 * RMB) {
				sumMoney += money * 0.25 / 100*Limit;
			}
			if (15 * RMB < money && money <= 30 * RMB) {
				sumMoney += money * 0.3 / 100*Limit;
			} 
			if(money > 30 * RMB) {
				sumMoney += money * 0.4 / 100*Limit;
			}

		}
		NumberUtils.format2(sumMoney);
		//已领取佣金
		double receivedMoney=userInvitateCodeService.findByUserId(userId).getReceiveMoney();
		return (sumMoney-receivedMoney);
	}


	@Action(value="webRecharge")
	public String webRecharge(){
		String rechargeType = paramString("rechargeType");
		double account = paramDouble("account"); //网站充值金额
		String psword = paramString("psword");//管理员支付密码
		String  verify_remark = paramString("verify_remark");//网站充值备注
		User user = userService.getUserById(paramLong("userId"));//被充值用户
		System.out.println(paramLong("userId"));
		User auth_user = getAuthUser();//当前用户，即超级管理员
		if(user == null){
			message("用户不存在！","/admin/account/addWebRecharge.html");
			return ADMINMSG;
		}
		checkAdminValidImg();//验证码校验
		checkWebRecharge(account,psword,auth_user,rechargeType);//充值校验
		
		//充值记录表
		AccountRecharge r=new AccountRecharge();
		r.setUser(user);//被充值用户
		r.setType("2"); //后台充值
		r.setAddtime(new Date());//添加时间
		r.setAddip(this.getRequestIp());//添加IP
		r.setPayment("");//所属银行
		r.setMoney(account);//充值金额
		r.setRemark("线下充值");//备注
		r.setStatus(1);//1.充值成功，0.充值失败
		r.setVerifyRemark(verify_remark);//网站充值备注
		//(发版要改过来，默认为1~网站充值~被充值用户ID~网站充值备注~获取http请求的实际IP)
		AccountLog log=new AccountLog(Constant.ADMIN_ID,Constant.WEB_RECHARGE,user.getUserId(),verify_remark, getRequestIp());
		try {
			//add by lijing 添加邀请奖励充值,不影响其它业务原则上
			if(org.apache.commons.lang3.StringUtils.isNotBlank(rechargeType) && "1".equals(rechargeType)){
				//邀请奖励充值校验
				boolean checkRewardMoney = checkRewardMoney(user,r.getMoney());
				if(!checkRewardMoney){
					throw new ManageBussinessException("充值校验失败,请确认当前用户可领取金额总额!详情请查看日志记录!");
				}
				accountService.webRecharge(log, r,rechargeType);
				super.systemLogAdd(auth_user, 20, "邀请奖励用户充值,金额:"+r.getMoney()+",订单号:"+r.getTradeNo());
			}else{
			//end 
			accountService.webRecharge(log,r);
			super.systemLogAdd(auth_user, 20, "用户线下充值,金额:"+r.getMoney()+",订单号:"+r.getTradeNo());}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("充值失败："+e.getMessage());
			message("充值失败:"+e.getMessage() , "/admin/account/addWebRecharge.html");
			return ADMINMSG;
		}
		message("充值成功！！" , "/admin/account/rechargelist.html");
		super.systemLogAdd(auth_user, 48, "管理员后台充值");
		return ADMINMSG;
	}
	@Action(value = "webonekeyrecharge",results={@Result(name="success",type="ftl",location="/admin/account/webonekeyrecharge.html")})
	public String webonekeyrecharge(){
		return SUCCESS;
	}
	//add by lijing 2016-11-25  后台批量充值
	@Action(value = "oneKeyRecharge")
	public String oneKeyRecharge() throws Exception{
			User auth_user = getAuthUser();//当前用户，即超级管理员
			logger.info("=========后台批量充值接口访问=========");
			if(auth_user == null || auth_user.getUserType().getTypeId() != 1){
				throw new ManageBussinessException("非法操作,只有超级管理员具有一键充值权限!");
			}
			checkValidImg();
			String remark = paramString("remark");
			String returnString= accountService.webBatchRecharge(getRequestIp(),remark);
			message(returnString, "/admin/account/webonekeyrecharge.html");
			return ADMINMSG;
	}
	// 收回转账功能
	@Action(value="addBackRecharge",results={@Result(name="success",type="ftl",location="/admin/account/webbackrecharge.html")})
	public String addBackRecharge(){
		String type = paramString("type");
		if(type.equals("query")){
			String username = paramString("username");//从页面获取被收账用户
			if(StringUtils.isBlank(username)){
				message("用户名不能为空","/admin/account/addBackRecharge.html");
				return ADMINMSG;
			}
			User user = userService.getUserByName(username);//被收账用户
			if(user == null){
				message("用户不存在","/admin/account/addBackRecharge.html");
				return ADMINMSG;
			}else if(user.getApiStatus() == 0){
				message("该用户尚未激活"+Global.getValue("api_name"),"/admin/account/addBackRecharge.html");
				return ADMINMSG;
			}else{
				request.setAttribute("user", user);
				return SUCCESS;
			}
		}
		return SUCCESS;
	}
	// modify by lijing 2016-11-28
	@Action(value="webbackrecharge")
	public String webbackrecharge(){
		double account = paramDouble("account"); //充值金额
		User rechargeUser = userService.getUserById(paramLong("userId"));//充值用户
		long adminId = Constant.ADMIN_ID;
		User adminUser = userService.getUserById(adminId);//被充值用户应该为超级管理员
		if(rechargeUser == null){
			message("用户不存在！","/admin/account/addBackRecharge.html");
			return ADMINMSG;
		}
		checkAdminValidImg();//验证码校验
		if(account == 0 || account<0){
			throw new ManageBussinessException("充值金额不正确");
		}
		//充值记录表
		AccountRecharge r=new AccountRecharge();
		r.setUser(adminUser);//被充值用户
		r.setType("2"); //后台充值
		r.setAddtime(new Date());//添加时间
		r.setAddip(this.getRequestIp());//添加IP
		r.setPayment("");//所属银行
		r.setMoney(account);//充值金额
		r.setRemark("后台收回转账");//备注
		r.setStatus(0);//1.充值成功，0.充值失败
		r.setVerifyRemark("后台收回转账");//网站充值备注
		AccountLog log=new AccountLog(Constant.ADMIN_ID,Constant.WEB_RECHARGE,adminId,"后台收回转账", getRequestIp());
		try {
			accountService.webBackRecharge(log,r,rechargeUser);
			super.systemLogAdd(rechargeUser, 20, "后台收回转账:,金额:"+r.getMoney()+",订单号:"+r.getTradeNo());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("充值失败："+e.getMessage());
			message("充值失败:"+e.getMessage() , "/admin/account/addWebRecharge.html");
			return ADMINMSG;
		}
		message("收回转账成功！！" , "/admin/account/rechargelist.html");
		super.systemLogAdd(adminUser, 48, "管理员后台收回转账");
		return ADMINMSG;
	}
	
	
	/**	add by lijing 
	 * 后台充值校验
	 * @param user
	 * @return
	 */
	private boolean checkRewardMoney(User user,double rechargeMoney) {
		double sumMoney = 0.00;
		final double RMB = 10000.00;
		// 我邀请的人数
		List<UserInvitateCode> invitateCodeList = new ArrayList<UserInvitateCode>();
		long userId = user.getUserId();
		invitateCodeList = userInvitateCodeService.getCodeListByPid(userId);
		// 我通过邀请别人赚取的金额
		if (null == invitateCodeList || invitateCodeList.size() == 0) {
			return false;
		}
		for (UserInvitateCode invitated : invitateCodeList) {// 循环多个我邀请的人    计算出我获得的佣金
			double money = 0.00;
			int Limit = 1;
			long invitatedId = invitated.getUser().getUserId();
			Date begin = userService.getAddTimeById(invitatedId);
			Date end = new Date();
			int deDay = DateUtils.getIntervalDays(begin, end);
			if (deDay > 10) {
				continue;
			}
			BorrowTender borrowTender = borrowTenderService.getListByUserId(invitatedId);// 被邀请人除体验金之外首次投资的金额
			if(borrowTender != null){
				money = borrowTender.getAccount();
				Borrow borrow = borrowTender.getBorrow();
				if(borrow.getIsday() == 0){
					if(borrow.getTimeLimit() == 2){
						Limit =  2;
					}
					if(borrow.getTimeLimit() >= 3){
						Limit =  3;
					}
				}
			}
			
			if (0.1 * RMB < money && money <= 3 * RMB) {
				sumMoney += money * 0.1 / 100*Limit;
			}
			if (3 * RMB < money && money <= 5 * RMB) {
				sumMoney += money * 0.15 / 100*Limit;
			}
			if (5 * RMB < money && money <= 8 * RMB) {
				sumMoney += money * 0.2 / 100*Limit;
			}
			if (8 * RMB < money && money <= 15 * RMB) {
				sumMoney += money * 0.25 / 100*Limit;
			}
			if (15 * RMB < money && money <= 30 * RMB) {
				sumMoney += money * 0.3 / 100*Limit;
			} 
			if(money > 30 * RMB) {
				sumMoney += money * 0.4 / 100*Limit;
			}

		}
		NumberUtils.format2(sumMoney);
		//已领取佣金
		double receivedMoney=userInvitateCodeService.findByUserId(userId).getReceiveMoney();
		//可领取奖励金金额
		double canReceiveMoney =  sumMoney-receivedMoney;
		//如果充值金额大于可领取金额返回false
		logger.info("用户可领取奖励金为: "+canReceiveMoney+"用户充值奖励金: "+ rechargeMoney+"用户已领取奖励金"+receivedMoney);
		if(rechargeMoney > canReceiveMoney){
			return false;
		}
		
		return true;
	}


	/**
	 * 金账户转账
	 * @return
	 */
	@Action(value="glodTransfer",
			results={@Result(name="success",type="ftl",location="/admin/account/glodTransfer.html")})
	public String glodTransfer(){
		String account = paramString("account");
		String psword = paramString("psword");
		User auth_user = getAuthUser();
		String actionType = paramString("actionType");
		String remark = paramString("remark");
		String payAccount = paramString("payAccount"); 
		double money = paramDouble("money");
		if(!StringUtils.isBlank(actionType)){
			GlodTransfer gt = new GlodTransfer();
			checkAdminValidImg();//验证验证码
			checkGlodTransfer(account,psword,auth_user,remark,gt,payAccount,money);//充值校验
			accountService.glodTransfer(gt);
			message("转账成功", "/admin/account/glodTransfer.html");
			return ADMINMSG;
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("rawtypes")
	@Action(value="allGlodTransfer",results={@Result(name="success",type="ftl",location="/admin/account/allGlodTransfer.html")})
	public String allGlodTransfer(){
		SearchParam param = new SearchParam().addPage(paramInt("page")).addOrder(OrderType.DESC, "addtime");
		PageDataList list = accountService.getAllGlodTransfer(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	
	@Action(value="verifyWebDeduct",
			results={@Result(name="success",type="ftl",location="/admin/account/verifyWebDeduct.html")})
	public String verifyWebDeduct() throws Exception {
		String actionType=StringUtils.isNull(paramString("actionType"));
		int id = paramInt("id");
		AccountWebDeduct awd = accountService.getAccountWebDeductById(id);
		//审核进程
		List<OperateLog> operateLoglist=operateService.getOperateLogByOrdNo(id,Constant.OPERATE_WEB_DEDUCT);
		if(actionType.equals("verify")){
			checkAdminValidImg();//验证验证码
			String remark = paramString("remark");
			int status = paramInt("status");
			//审核备注不能为空
			if(StringUtils.isBlank(remark)){
				throw new ManageBussinessException("审核信息不能为空！");
			}
			User authUser = getAuthUser();
			//校验初审是否要进行多次审核，通过数据库中rule的规则配置
			Rule rule = ruleService.getRuleByNid("verify_rule");
			if (rule != null) {
				if (rule.getStatus() == 1) { //校验此规则是否启用
					int rulerStatus=rule.getValueIntByKey("verify_deduct");//获取初审是否需要进行分布操作，1为需要，0为不需要
					if (rulerStatus == 1 ) {
						boolean operateRusult = operateService.doOperate(Constant.OPERATE_WEB_DEDUCT, id, authUser,remark ,status );
						if (operateRusult) {     //判断是否是最后一个审核流程，是最后一个审核流程的话，开始处理后面的业务，否则的话，不处理
							//不做处理
						}else{//不是最后一个审核流程的时候，返回给审核人提示信息
							message("审核成功！","/admin/account/deductList.html");
							return ADMINMSG;
						}
					}
				}
			}
			if(operateLoglist.size()==0 && status == 0){
				message("无审核流程，无法返回上一步","/admin/account/verifyWebDeduct.html?id="+id);
				return ADMINMSG;
			}else{
				awd.setStatus(status);
				awd.setRemark(remark);
			}
			//审核状态 
			super.systemLogAdd(authUser, 43, "管理员初审标成功");
			AccountLog log=new AccountLog(awd.getUser().getUserId(),Constant.WEB_DEDUCT,1,getTimeStr(), getRequestIp());
			accountService.verifyWebDeduct(log, awd);
			message("扣款审核完成！", "/admin/account/deductList.html");
			return ADMINMSG;
		}else{
			if(operateLoglist.size()>0){
				OperateProgress op = operateLoglist.get(0).getOperateProgress();
				request.setAttribute("borrow_fee", op==null?null:op.getParam());
			}
			request.setAttribute("operateLoglist", operateLoglist);
			request.setAttribute("item",awd);
		}
		return SUCCESS;
	}
	
	@Action(value="deductList",
			results={@Result(name="success",type="ftl",location="/admin/account/deductlist.html")}
			)
	public String deductList() throws Exception {
		SearchParam param=SearchParam.getInstance()
				.addPage(paramInt("page"));
		PageDataList plist = accountService.getWebDeductList(param);
		setPageAttribute(plist, param);
		String type = paramString("actionType");
		return SUCCESS;
	}
	
	@Action(value="webDeduct")
	public String webDeduct(){
		double account = paramDouble("account"); 
		String psword = paramString("psword");
		String remark = paramString("remark");
		String dedcutAccount = paramString("dedcutAccount");
		User user = userService.getUserById(paramLong("userId"));
		User auth_user = getAuthUser();
		if(user == null){
			message("用户不存在！","/admin/system/queryAdminUser.html");
			return ADMINMSG;
		}
		checkAdminValidImg();//验证验证码
		checkWebDedcut(account,psword,user);//扣款校验
		
		AccountWebDeduct awd = new AccountWebDeduct();
		awd.setUser(user);
		awd.setAddUserid(auth_user.getUserId());
		awd.setMoney(account);
		awd.setRemark(remark);
		awd.setDedcutAccount(dedcutAccount);
		awd.setStatus(0);
		awd.setAddTime(new Date());
		awd.setAddip(getRequestIp());
		
		AccountLog log=new AccountLog(user.getUserId(),Constant.WEB_DEDUCT,1,getTimeStr(), getRequestIp());
		try {
			accountService.webDeduct(log,awd);
			super.systemLogAdd(auth_user, 20, "管理员后台添加扣款,金额:"+awd.getMoney()+",订单号:"+awd.getTradeNo());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("扣款添加失败："+e.getMessage());
			message("扣款添加失败:"+e.getMessage() , "/admin/account/addWebCheckOff.html");
			return ADMINMSG;
		}
		message("扣款添加成功！！" , "/admin/account/addWebCheckOff.html");
		return ADMINMSG;
	}
	
	@Action(value="addWebCheckOff",
			results={@Result(name="success",type="ftl",location="/admin/account/webcheckoff.html")}
			)
	public String addWebCheckOff(){
		String type = paramString("type");
		if(type.equals("query")){
			String username = paramString("username");
			if(StringUtils.isBlank(username)){
				message("用户名不能为空","/admin/account/addWebCheckOff.html");
				return ADMINMSG;
			}
			User user = userService.getUserByName(username);
			if(user == null){
				message("用户不存在","/admin/account/addWebCheckOff.html");
				return ADMINMSG;
			}
            if(user.getApiStatus() == 0){
				message("该用户尚未激活"+Global.getValue("api_name"),"/admin/account/addWebCheckOff.html");
				return ADMINMSG;
			}
            if(StringUtils.isBlank(user.getPaypassword())){
            	message("请设置代扣用户用的支付密码","/admin/account/addWebCheckOff.html");
				return ADMINMSG;
            }
			request.setAttribute("user", user);
			return SUCCESS;
		}
		return SUCCESS;
	}
	
	@Action(value="cashbank",results={
			@Result(name="success",type="ftl",location="/admin/account/allbank.html"),
			@Result(name="show",type="ftl",location="/admin/account/cashbank.html")
			})
	public String manageUserCashBank(){
		String type=StringUtils.isNull(request.getParameter("actionType"));
		//查询提现银行卡
		SearchParam drawBankParam = new SearchParam();
		if(!"ALL".equals(Constant.DRAW_CARDTYPE)){
			drawBankParam.addParam("cardType", Constant.DRAW_CARDTYPE);
		}
		if(!"ALL".equals(Constant.DRAW_CHANNEL_NO)){
			drawBankParam.addParam("channelNo", Constant.DRAW_CHANNEL_NO);
		}		
		List drawList = accountService.getDrawBankBySearchParam(drawBankParam);
		request.setAttribute("drawBankList", drawList);
		if(type.equals("show")){
			int id=NumberUtils.getInt(request.getParameter("id"));
			AccountBank ab = accountService.getAccountBankById(id);
			request.setAttribute("accountBank", ab);
			getAndSaveRef();
			return "show";
		}else if(type.equals("add")){
			int id=NumberUtils.getInt(request.getParameter("id"));
			long user_id=NumberUtils.getLong(request.getParameter("user_id"));
			String bank=StringUtils.isNull(request.getParameter("bank"));
			String branch=StringUtils.isNull(request.getParameter("branch"));
			String account=StringUtils.isNull(request.getParameter("account"));
			int province = paramInt("province");
			int city = paramInt("city");
			int area = paramInt("area");
			int drawBankId = paramInt("drawBank");
			DrawBank drawBank = accountService.getDrawBankById(drawBankId);
			if(drawBank==null){
				throw new BussinessException("对不起,获取银行信息出错,请联系管理员！");
			}
			
			if(StringUtils.isBlank(branch)){
				message("开户行名称不能为空！", getRef());
				return ADMINMSG;
			}
			if(StringUtils.isBlank(account)){
				message("银行账号不能为空！", getRef());
				return ADMINMSG;
			}
			if(province==0 || city==0 || area==0){
				message("银行卡归属地不完整！", getRef());
				return ADMINMSG;
			}
			AccountBank actbank=new AccountBank();
			if(id!=0){
				actbank.setId(id);	
			}			
			actbank.setAccount(account);
			actbank.setBranch(branch);
			actbank.setUser(new User(user_id));
			actbank.setBank(drawBank);
			actbank.setProvince(new AreaBank(province));
			actbank.setCity(new AreaBank(city) );
			actbank.setArea(new AreaBank(area) );
			actbank.setAddip(getRequestIp());
			actbank.setAddtime(new Date());
			accountService.addOrUpdateAccountBank(actbank);
			message("操作成功", getRef());
			return ADMINMSG;
		}else if(type.equals("del")){
			int id=NumberUtils.getInt(request.getParameter("id"));
			accountService.delAccountBankById(id);
			message("操作成功", getRef());
			return ADMINMSG;
		}else{
			int page=NumberUtils.getInt(request.getParameter("page"));
			String username=StringUtils.isNull(request.getParameter("username"));
			SearchParam param=new SearchParam().addPage(page);
			if(!StringUtils.isBlank(username)){
				param.addParam("user.username",Operator.LIKE,username);
			}			
			PageDataList plist=accountService.getAccountBankList(param);			
			setPageAttribute(plist, param);
			return SUCCESS;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Action(value="addbank",results={
			@Result(name="success",type="ftl",location="/admin/account/addbank.html")
			})
	public String addBank ()throws Exception {
		String actionType = paramString("actionType");
		if(!StringUtils.isBlank(actionType)){
			long user_id=NumberUtils.getLong(request.getParameter("user_id"));
			String bank=StringUtils.isNull(request.getParameter("bank"));
			String branch=StringUtils.isNull(request.getParameter("branch"));
			String account=StringUtils.isNull(request.getParameter("account"));
			int province = paramInt("province");
			int city = paramInt("city");
			int area = paramInt("area");
			int drawBankId = paramInt("drawBank");
			if(user_id==0){
				message("用户id不能为空！", "/admin/account/addbank.html");
				return ADMINMSG;
			}
			User user = userService.getUserById(user_id);
			if(user==null){
				message("用户不存在！", "/admin/account/addbank.html");
				return ADMINMSG;
			}
			if(user.getRealStatus()!=1){
				throw new ManageBussinessException("该用户还未进行实名认证", "/admin/account/addbank.html");
			}
			DrawBank drawBank = accountService.getDrawBankById(drawBankId);
			if(drawBank==null){
				throw new BussinessException("对不起,获取银行信息出错,请联系管理员！");
			}			
			if(StringUtils.isBlank(branch)){
				message("开户行名称不能为空！", "/admin/account/addbank.html");
				return ADMINMSG;
			}
			if(StringUtils.isBlank(account)){
				message("银行账号不能为空！", "/admin/account/addbank.html");
				return ADMINMSG;
			}
			if(province==0 || city==0 || area==0){
				message("银行卡归属地不完整！", "/admin/account/addbank.html");
				return ADMINMSG;
			}
		}
		//查询提现银行卡
		SearchParam drawBankParam = new SearchParam();
		if(!"ALL".equals(Constant.DRAW_CARDTYPE)){
			drawBankParam.addParam("cardType", Constant.DRAW_CARDTYPE);
		}
		if(!"ALL".equals(Constant.DRAW_CHANNEL_NO)){
			drawBankParam.addParam("channelNo", Constant.DRAW_CHANNEL_NO);
		}		
		List drawList = accountService.getDrawBankBySearchParam(drawBankParam);
		request.setAttribute("drawBankList", drawList);
		return SUCCESS;
	}
	
	
	@Action(value="viewCash",results={
			@Result(name="success",type="ftl",location="/admin/account/viewcash.html")
			})
	public String viewCash() throws Exception{
		int id=NumberUtils.getInt(request.getParameter("id"));
		AccountCash cash = accountService.getAccountCash(id);
		if(cash==null){
			message("非法操作！");
			return ADMINMSG;
		}
		request.setAttribute("cash", cash);
		request.setAttribute("cash_token", saveToken("cash_token"));
		return SUCCESS;
	}
	
	@Action(value="verifyCash",results={
			@Result(name="success",type="ftl",location="/admin/account/verifycash.html")
			})
	public String verifyCash() throws Exception{
		int id = NumberUtils.getInt(request.getParameter("id"));
		String verify_remark = StringUtils.isNull(request.getParameter("verify_remark"));
		int status = NumberUtils.getInt(request.getParameter("status"));
		AccountCash cash=accountService.getAccountCash(id);
		checkAdminValidImg();//验证验证码
		if(cash.getUser().getApiId()==null){
			throw new ManageBussinessException("该用户没有注册"+Global.getValue("api_name")+"账号，不能提现");
		}
		if(cash.getUser().getApiStatus()!=1){
			throw new ManageBussinessException("该用户还未激活"+Global.getValue("api_name")+"账户，不能提现");
		}
		if(cash==null||StringUtils.isBlank(verify_remark)||
				(status<0||status>3)){
			message("非法操作！");
			return ADMINMSG;
		}
		if(cash.getStatus()==1){
			message("该提现记录已经审核，不允许重复操作！");
			return ADMINMSG;
		}
		if(cash.getStatus()==2){
			message("该提现记录已经取消，不允许审核！");
			return ADMINMSG;
		}
		//后台管理员多次审核 lx start 
		if(cash.getStatus()==4){
			message("该提现记录已经审核通过，不允许重新审核！");
			return ADMINMSG;
		}
		// 防止重复提交
		String tokenResult =  checkToken("cash_token");
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult);
			return MSG;
		}
		//后台管理员多次审核 lx end
		User authUser=getAuthUser();
		long verify_userid=1;
		if(authUser!=null) {
			verify_userid = authUser.getUserId();
		}
		cash.setStatus(status);
		cash.setVerifyUser(new User(verify_userid));
		cash.setVerifyRemark(verify_remark);
		cash.setVerifyTime(new Date());
		
		AccountLog log=new AccountLog(cash.getUser().getUserId(),Constant.CASH_SUCCESS,verify_userid,
				getTimeStr(),getRequestIp());
		boolean result = false;
		try {
			result = accountService.verifyCash(cash, log);
		} catch (Exception e) {
			message(e.getMessage(),"/admin/account/showAllCash.html?status=0");
			return ADMINMSG;
		}
		String msg = "";
		if(result){//用户提现申请成功。
			msg="用户提现申请成功，等候第三方处理";
			super.systemLogAdd(authUser, 31, "管理员审核提现,用户提现金额:"+cash.getTotal());
			//提现审核成功，发消息通知
			MsgReq req = new MsgReq();
			req.setIp(super.getRequestIp());
			req.setSender(authUser);
			req.setReceiver(cash.getUser());
			req.setMsgOperate(this.msgService.getMsgOperate(9));
			req.setTime(DateUtils.dateStr(cash.getAddtime(), "yyyy-MM-dd HH:mm:ss"));
			req.setMoney(""+cash.getTotal());
			req.setFee(""+cash.getFee());
			DisruptorUtils.sendMsg(req);
		}else{
			msg="审核成功：提现失败！";
		}
		message("处理完成，"+msg,"/admin/account/showAllCash.html");
		return ADMINMSG;
	}
	
	@Action("showarea")
	public String showArea() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pid = (String) req.getParameter("pid");
		List<Area> areas = userinfoService.getAreaListByPid(NumberUtils.getInt(pid));		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}
	
	@Action(value="viewYjfAccount",results={
			@Result(name="success",type="ftl",location="/admin/account/viewYjfAccount.html")
			})
	public String viewYjfAccount() {
		long userId = paramLong("userId");
		User user = userService.getUserById(userId);
		if(user == null) {
			throw new ManageBussinessException("该账户不存在！", "/admin/account/allaccount.html");
		}
		UserAccountQuery uaq = null;
		if(!StringUtils.isBlank(user.getApiId())){
		}
		request.setAttribute("yjfAccount", uaq);		
		request.setAttribute("user", user);
		return SUCCESS;
	}
	
	@Action(value="queryYjfRecharge",results={
			@Result(name="success",type="ftl",location="/admin/account/yjfrechargelist.html")
			})
	public String queryYjfRecharge() {
		long userId = paramLong("userId");
		User user = userService.getUserById(userId);
		if(user == null) {
			throw new ManageBussinessException("该账户不存在！", "/admin/account/allaccount.html");
		}
		DepositQuery dq = null;
		int page = paramInt("page")==0?1:paramInt("page");
		if(!StringUtils.isBlank(user.getApiId())){
		}else{
			message("此用户尚未开通"+Global.getValue("api_name"),"/admin/account/showAllAccount.html");
			return ADMINMSG;
		}
		if(dq==null || dq.getResultCode().equals("EXTERFACE_IS_CLOSED")){
			message(Global.getValue("api_name")+"接口已经关闭","/admin/account/showAllAccount.html");
			return ADMINMSG;
		}
		request.setAttribute("yjfRecharge", dq);		
		request.setAttribute("user", user);
		request.setAttribute("page", page);
		return SUCCESS;
	}
	
	@Action(value="queryYjfCash",results={
			@Result(name="success",type="ftl",location="/admin/account/yjfcashlist.html")
			})
	public String queryYjfCash() {
		long userId = paramLong("userId");
		User user = userService.getUserById(userId);
		if(user == null) {
			throw new ManageBussinessException("该账户不存在！", "/admin/account/showAllAccount.html");
		}
		WithdrawQquery dq = null;
		int page = paramInt("page")==0?1:paramInt("page");
		if(!StringUtils.isBlank(user.getApiId())){
		}else{
			message(Global.getValue("api_name")+"接口已经关闭","/admin/account/showAllAccount.html");
			return ADMINMSG;
		}
		if(dq == null ||dq.getResultCode().equals("EXTERFACE_IS_CLOSED")){
			message(Global.getValue("api_name")+"接口已经关闭","/admin/account/showAllAccount.html");
			return ADMINMSG;
		}
		request.setAttribute("yjfCash", dq);		
		request.setAttribute("user", user);
		request.setAttribute("page", page);
		return SUCCESS;
	}
	
	@Action(value="sitePayLog",
			results={@Result(name="success",type="ftl",location="/admin/account/sitepaylog.html")})
	public String sitePayLog() throws Exception{
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrder(OrderType.ASC,"addtime");
		if(!StringUtils.isBlank(startTime)){
			param.addParam("addtime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("addtime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList plist = logService.getSitePayList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
	    extraparam.put("dotime1", startTime);
	    extraparam.put("dotime2", endTime);
		setPageAttribute(plist, param,extraparam);
		setPageUrl();
		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "success";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="垫付记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","borrow/.name","borrow/.user/.username","user/.username",
					"borrowTender/.account","typeName","moneyPay","accountPay","moneyTotal","accountTotal","addtime"};
			String[] titles=new String[]{"ID","标名","借款人","出借人","投标金额","垫付类型","网站应该垫付金额","网站实际垫付的金额","网站应该垫付的总金额","网站实际垫付的总金额","垫付时间"};
			List<SitePayLog> rlist=logService.getAllSitePayList(param);
			ExcelHelper.writeExcel(infile, rlist, SitePayLog.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		
	}
	
	private void checkGlodTransfer(String account,String psword,User auth_user,String remark,GlodTransfer gt,String payAccount,double money){
		if(money == 0){
			throw new ManageBussinessException("金额不能为空");
		}
		if(StringUtils.isBlank(account)){
			throw new ManageBussinessException("请选择转付款账户");
		}
		if(StringUtils.isBlank(payAccount)){
			throw new ManageBussinessException("请选择转接收账户");
		}
		if(StringUtils.isBlank(psword)){
			throw new ManageBussinessException("支付密码不能为空");
		}
		if(StringUtils.isBlank(auth_user.getPaypassword())){
			throw new ManageBussinessException("请您设置一个支付密码");
		}
		MD5 md5 = new MD5();   //支付密码验证开始
		if(!md5.getMD5ofStr(psword).equals(auth_user.getPaypassword())){
			throw new ManageBussinessException("支付支付密码不正确!");
		}
		if(StringUtils.isBlank(remark)){
			throw new ManageBussinessException("备注不能为空");
		}
		gt.setPayAccount(payAccount);
		gt.setAddip(getRequestIp());
		gt.setAddtime(new Date());
		gt.setMoney(money);
		gt.setAccount(account);
		gt.setRemark(remark);
		gt.setUser(auth_user);
	}
	
	private void checkWebRecharge(double account,String psword,User auth_user,String rechargeType){
		if(account == 0 || account<0){
			throw new ManageBussinessException("充值金额不正确");
		}
		if(StringUtils.isBlank(psword)){
			throw new ManageBussinessException("支付密码不能为空");
		}
		if(StringUtils.isBlank(auth_user.getPaypassword())){
			throw new ManageBussinessException("请您设置一个支付密码");
		}
		if(org.apache.commons.lang3.StringUtils.isBlank(rechargeType)){
			throw new ManageBussinessException("请选择充值类型!");
		}
		MD5 md5 = new MD5();   //支付密码验证开始
		if(!md5.getMD5ofStr(psword).equals(auth_user.getPaypassword())){
			throw new ManageBussinessException("支付支付密码不正确!");
		}
	}
	/*private void checkBackRecharge(double account){
		if(account == 0 || account<0){
			throw new ManageBussinessException("收回金额不正确");
		}
		
	}*/
	
	private void checkWebDedcut(double account,String psword,User user){
		if(account == 0 || account<0){
			throw new ManageBussinessException("充值金额不正确");
		}
		if(StringUtils.isBlank(psword)){
			throw new ManageBussinessException("支付密码不能为空");
		}
		if(StringUtils.isBlank(user.getPaypassword())){
			throw new ManageBussinessException("请您设置一个支付密码");
		}
		MD5 md5 = new MD5();   //支付密码验证开始
		if(!md5.getMD5ofStr(psword).equals(user.getPaypassword())){
			throw new ManageBussinessException("支付交易密码不正确!");
		}
		double use_money = user.getAccount().getUseMoney();
		if(account > use_money ){
			throw new ManageBussinessException("扣款金额大于该用户可用余额");
		}
	}
	
	@Action(value="allWebGlodLog",results={@Result(name="success",type="ftl",location="/admin/account/allWebGlodLog.html")})
	public String allWebGlodLog() throws Exception{
		
		String type = paramString("type");
		String username = paramString("username");
		String dotime1 = paramString("dotime1");
		String dotime2 = paramString("dotime2");
		String typestr = paramString("typestr");
		
		SearchParam param = new SearchParam().addPage(paramInt("page")).addOrder(OrderType.DESC, "addtime");
		Map<String,Object> extraMap = new HashMap<String, Object>();
		if(!StringUtils.isBlank(username)){
			param.addParam("user.username", username);
			extraMap.put("username", username);
		}
		if(!StringUtils.isBlank(dotime1)){
			if(dotime1.length()==19){
			}else{
				dotime1 +=" 00:00:00";
			}
			param.addParam("addtime", Operator.GTE, DateUtils.getDate2(dotime1));
			extraMap.put("dotime1", dotime1);
		}
		if(!StringUtils.isBlank(dotime2)){
			if(dotime2.length()==19){
			}else{
				dotime2 +=" 23:59:59";
			}
			param.addParam("addtime", Operator.LTE, DateUtils.getDate2(dotime2));
			extraMap.put("dotime2", dotime2);
		}
		if(!StringUtils.isBlank(typestr)){
			param.addParam("type",typestr );
			extraMap.put("typestr", typestr);
		}
		PageDataList list = accountService.getWebGlodLogList(param);
		
		if(type.equals("export")){
			List<WebGlodLog> listAll = accountService.getAllGlodLogList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="金账户操作记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","user/.username","money","fee",
					"typeStr","ordId","addtime","memo"};
			String[] titles=new String[]{"ID","操作管理员","操作金额","手续费",
					"操作类型","订单号","操作时间","操作备注"};
			ExcelHelper.writeExcel(infile, listAll, WebGlodLog.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		setPageAttribute(list, extraMap);
		return SUCCESS;
	}
	
	/**
	 * 关于金账户充值功能
	 * @return
	 */
	@Action(value="glodrecharge",
			results={@Result(name="recharge",type="ftl",location="/admin/account/glodrecharge.html"),
			@Result(name="huifu",type="ftl",location="/member/huifu/NetSave.html")})
	public String glodrecharge(){
		String type = paramString("type");
		if (type.equals("view")){
			return "recharge";	
		}
		double glodMoney = paramDouble("money");
		if(glodMoney>=100000000000l){
			message("专属账户充值只支持金额在11位以内的充值","/admin/account/glodrecharge.html?type=view");
			return ADMINMSG;
		}
		User auth_user = getAuthUser();
		String web_type = "recharge";
		
		String dcFlag="C";
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		String ordDate=DateUtils.newdateStr2(new Date());
		String custId = Global.getValue("chinapnr_merid");
		NetSave save=new NetSave(ordId, ordDate, dcFlag, NumberUtils.format2Str(glodMoney), custId);
		save.setRetUrl(Global.getValue("weburl")+"/admin/account/glodRechargeReturn.html");
		save.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/webGlodReturn.html");
		save.setMerPriv(auth_user.getUserId() + "," + web_type);
		try {
			save.sign();
		} catch (Exception e) {
			logger.error("生成签名失败！"+e);
		}
		request.setAttribute("pnr", save);
		return "huifu";
	}
	
	/**
	 * 金账户取现
	 * @return
	 */
	@Action(value="glodCash",
			results={@Result(name="cash",type="ftl",location="/admin/account/glodcash.html"),
			@Result(name="huifu",type="ftl",location="/member/huifu/Cash.html")})
	public String glodCash(){
		String type=paramString("type");
		if (type.equals("view")) {
			return "cash";
		}
		User auth_user = getAuthUser();
		String web_type = "cash";
		double money = paramDouble("money");
		String custId = Global.getValue("chinapnr_merid");
		CashOut cash=new CashOut(custId);
		cash.setBgRetUrl(Global.getValue("weburl")+"");
		String ordId = OrderNoUtils.getInstance().getSerialNumber();
		cash.setOrdId(ordId);
		cash.setMerPriv(auth_user.getUserId()+","+web_type);
		cash.setTransAmt(NumberUtils.format2Str(money));
		cash.setServFee("");
		cash.setServFeeAcctId("");
		cash.setRetUrl(Global.getValue("weburl")+"/admin/account/reBack.html");
		cash.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/webGlodReturn.html");
		try {
			cash.sign();
		} catch (Exception e) {
			logger.info("生成签名失败！"+e);
		}
		request.setAttribute("pnr", cash);
		return "huifu";
	}
	
	/**
	 * 后台取现，充值通知
	 * @return
	 */
	@Action("reBack")
	public String reBack(){
		String respCode = paramString("RespCode");
		String respdesc = URLDecoder.decode(paramString("RespDesc"));
		if (respCode.equals("000")) {
			double money = paramDouble("TransAmt");
			WebGlodLog wgLog = null;
			String merPriv = URLDecoder.decode(paramString("MerPriv"));//附加信息
			String type = "";//回调类型
			String ordid = paramString("OrdId");//订单号
			long userid = 0 ;
			//判断该订单是否处理过
			int sum = accountService.sumWebLogByOrdid(ordid);
			if(sum != 0){
				throw new BussinessException("重复处理的订单"+ordid);
			}
			int checkMer = merPriv.indexOf(",");
			if (checkMer!=-1) {
				String[] args = merPriv.split(",");
				userid=NumberUtils.getLong(args[0]);           //充值管理员
				type=args[1];        
			}
			logger.info("金账户操作：订单号："+ordid+"   ，附加信息："+merPriv);
			CashOut cash=this.cashcall();
			int ret =cash.callback();    //验签操作
			logger.info("金账户取现验签结果："+ret);
			if (ret==0) {
				wgLog = new WebGlodLog(new User(userid),new User(1),type,money,getRequestIp(),ordid);
				wgLog.setMemo(respdesc);
				wgLog.setFee(NumberUtils.getDouble(cash.getFeeAmt()));
				BorrowParam param=new BorrowParam();
				String resultFlag = cash.getOrdId() + "-" + System.currentTimeMillis()+"";
				param.setResultFlag(resultFlag);
				request.setAttribute("tenderFlag", resultFlag);
				request.setAttribute("ok_url", "/admin/account/allWebGlodLog.html"); //成功返回地址
				request.setAttribute("back_url", "/admin/account/allWebGlodLog.html");//失败返回地址
				request.setAttribute("r_msg","操作成功");
				DisruptorUtils.glodCashSuccess(cash, wgLog, param);
				return ADMINRESULT;
			}else{
				logger.info("取现验签失败   "+ "orderId:" + cash.getOrdId());
				throw new BussinessException("取现验签失败   "+ "orderId:" + cash.getOrdId());
			}
		}else{
			message("操作失败，失败原因："+respdesc,"/admin/account/glodCash.html?type=view");
			return ADMINMSG;
		}
	}
	
	@Action("glodRechargeReturn")
	public String glodRechargeReturn(){
		String respCode = paramString("RespCode");
		String respdesc = URLDecoder.decode(paramString("RespDesc"));
		if (respCode.equals("000")) {
			message("充值成功","/admin/account/allWebGlodLog.html","点击查询充值记录");
		}else{
			message("操作失败，失败原因："+respdesc,"/admin/account/glodrecharge.html?type=view","返回重新充值");
		}
		return ADMINMSG;
	}
	
	private NetSave chpnrNatSaveCallback(){
		NetSave n=new NetSave();
		n.setTransAmt(paramString("TransAmt"));
		n.setRespCode(paramString("RespCode"));
		n.setMerPriv(URLDecoder.decode(paramString("MerPriv")));         //此参数用户获取到用户的id，便于回调时查找到具体的用户
		n.setTrxId(paramString("TrxId"));             //钱管家交易唯一标示
		n.setChkValue(paramString("ChkValue"));      //签名信息
		n.setOrdId(paramString("OrdId"));            //订单号
		n.setMerCustId(paramString("MerCustId"));
		n.setOrdDate(paramString("OrdDate"));
		n.setRetUrl(paramString("RetUrl"));
		n.setBgRetUrl(paramString("BgRetUrl"));
		n.setCmdId(paramString("CmdId"));
		n.setUsrCustId(paramString("UsrCustId"));
		n.setGateBusiId(paramString("GateBusiId"));
		n.setGateBankId(paramString("GateBankId"));
		n.setFeeAmt(paramString("FeeAmt"));
		n.setFeeAcctId(paramString("FeeAcctId"));
		n.setFeeCustId(paramString("FeeCustId"));
		return n;	
	}
	
	private CashOut cashcall(){
		CashOut cash=new CashOut();
		cash.setCmdId(paramString("CmdId"));
		cash.setRespCode(paramString("RespCode"));
		cash.setRespDesc(paramString("RespDesc"));
		cash.setMerCustId(paramString("MerCustId"));
		cash.setOrdId(paramString("OrdId"));
		cash.setUsrCustId(paramString("UsrCustId"));
		cash.setTransAmt(paramString("TransAmt"));
		cash.setOpenAcctId(paramString("OpenAcctId"));
		cash.setOpenBankId(paramString("OpenBankId"));
		cash.setRetUrl(paramString("RetUrl"));
		cash.setBgRetUrl(paramString("BgRetUrl"));
		cash.setMerPriv(URLDecoder.decode(paramString("MerPriv")));
		cash.setChkValue(paramString("ChkValue"));
		cash.setFeeAmt(paramString("FeeAmt"));
		
		return cash;
	}
	
	//v1.8.0.4  TGPROJECT-25   qj  2014-04-03 start
	/**
	 * 查询第三方的资金情况
	 * @return
	 */
	@Action(value="queryBalance",
			results={@Result(name="success",type="ftl",location="/admin/account/queryBalance.html")})
	public String queryBalance(){
		try {
			long user_id=paramLong("userId");
			User user = userService.getUserById(user_id);
			if(user.getApiStatus() == 0){
				throw new ManageBussinessException("此用户尚未开通"+Global.getString("api_name")+"账号", "/admin/account/allaccount.html");
			}
			Map<String, String> map = apiService.findApiAccount(user);
			request.setAttribute("user", user);
			request.setAttribute("money", map);
			
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			logger.info(e.getMessage());
			message("查询失败 :" + e.getMessage());
			return ADMINMSG;
		}
		return SUCCESS;
	}
	//v1.8.0.4  TGPROJECT-25   qj  2014-04-03 stop

    /**
     * 批量查询第三方的资金情况
     *
     * @return
     */
    @Action(value = "batchQueryBalance",
            results = {@Result(name = "success", type = "ftl", location = "/admin/account/batchQueryBalance.html")
    })
    public String batchQueryBalance() {
        try {
			String userIdStart = paramString("userIdStart");
			String userIdEnd = paramString("userIdEnd");
        	logger.info("userIdStart: " + userIdStart + ", userIdEnd：" + userIdEnd);
        	if (StringUtils.isBlank(userIdStart) || StringUtils.isBlank(userIdEnd)) {
				return SUCCESS;
			}
			List<SearchFilter> sfs = new ArrayList<SearchFilter>();
			for (int i = 0; i <= Integer.parseInt(userIdEnd) - Integer.parseInt(userIdStart); i++) {
        		SearchFilter sf = new SearchFilter("userId", Operator.EQ, String.valueOf(Integer.parseInt(userIdStart) + i));
        		sfs.add(sf);
			}
//            List<User> userList = userService.getUserList(SearchParam.getInstance().addParam("apiStatus", 1).addParam("userType", 2));
			List<User> userList = userService.getUserList(SearchParam.getInstance().addParam("apiStatus", 1).addParam("userType", 2).addOrFilter(sfs));
			logger.info("当前已经实名用户数：" + userList.size());
            if (userList.isEmpty()) {
				message("查询失败：当前用户ID区间没有实名认证的用户！");
				return ADMINMSG;
			}
			if (userList.size() > 100) {
				message("查询失败：当前用户ID区间实名认证的用户数超过100，请适当缩小查询区间！");
				return ADMINMSG;
			}
			List<Map<Object, Object>> list = apiService.findBatchApiAccount(userList);
            for (Map<Object, Object> map : list) {
                User user = userService.getUserByApiId(String.valueOf(map.get("userApiId")));
                map.put("user", user);
            }
            request.setAttribute("list", list);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            logger.info(e.getMessage());
            message("查询失败 :" + e.getMessage());
            return ADMINMSG;
        }
        return SUCCESS;
    }
	
	@Action(value="huifu_query_api_test")
	public String huifuQueryApiTest(){
		
		String usrCustId = paramString("usrCustId");
		String ordId = paramString("ordId");
		String ordDate = paramString("ordDate");
		String querytransType = paramString("querytransType");
		String beginDate = paramString("beginDate");
		String endDate = paramString("endDate");
		logger.info("余额查询 (后台)");
		ChinapnrHelper.queryBalanceBg(usrCustId); //usrCustId
		logger.info("商户子账户信息查询");
		ChinapnrHelper.queryAcctschin();
		logger.info("交易状态查询");
		ChinapnrHelper.queryTransStat(ordId, ordDate, querytransType); //ordId,ordDate,querytransType
		logger.info("商户扣款对账");
		ChinapnrHelper.trfReconchina(beginDate, endDate, "1", "20");//beginDate,endDate,pageNum,pageSize
		logger.info("投标对账");
		ChinapnrHelper.reconciliationchin(beginDate, endDate, "1", "20", querytransType);//beginDate, endDate, pageNum, pageSize, queryTransType
		logger.info("取现对账");
		ChinapnrHelper.cashReconciliationchin(beginDate, endDate, "1", "20");//beginDate, endDate, pageNum, pageSize
		logger.info("充值对账");
		ChinapnrHelper.saveReconchin(beginDate, endDate, "1", "20");//beginDate, endDate, pageNum, pageSize
		return null;
	}
	//v1.8.0.4_u1 TGPROJECT-240 zf start
	//页面得到用户积分列表
	@Action(value="autoTenderLog",results={
			@Result(name="success", type="ftl",location="/admin/account/autoTenderLog.html")			
	})
	public String autoTenderLog() {
		String username = paramString("name");
		int page = paramInt("page") == 0 ? 1 : paramInt("page");
		SearchParam param = new SearchParam();
		param.addPage(page);
		if(username!=null&&username.trim()!=""){
			param.addParam("user.username", Operator.LIKE, username.trim());
		}
		request.setAttribute("name", username.trim());
		PageDataList<BorrowAuto> pageDataList = borrowService.findAutoTenderList(param);
		setPageAttribute(pageDataList,param);
		return SUCCESS;
	}
	//v1.8.0.4_u1 TGPROJECT-240 zf end
	
	
	
	//1.8.0.4_u3   TGPROJECT-337  qinjun 2014-06-23  start
	/**
	 * 查询投资奖励发放列表
	 * @return
	 */
	@Action(value="showTenderRewardList",results={
			@Result(name="success", type="ftl",location="/admin/account/showTenderRewardList.html")			
	})
	public String showTenderRewardList(){
		SearchParam param = new SearchParam().addPage(paramInt("page")).addOrder(OrderType.DESC, "addTime");
		long borrowId = paramLong("borrowId");
		int status = paramInt("status");
		if (status !=-2) {
			param.addParam("status", status);
		}
		if (borrowId > 0) {
			param.addParam("borrow.id", borrowId);
		}
		PageDataList<TenderReward> rewardList = tenderRewardService.getPageListTenderReward(param);
		setPageAttribute(rewardList, param);
		return "success";
	}
	
	/**
	 * 查看投标奖励记录
	 * @return
	 */
	@Action(value="viewReward",results={
			@Result(name="success", type="ftl",location="/admin/account/viewTenderReward.html")			
	})
	public String viewReward(){
		long rewardId = paramLong("rewardId");
		TenderReward tenderReward = tenderRewardService.getTenderRewardById(rewardId);
		if (null ==tenderReward) {
			message("非法操作！");
			return ADMINMSG;
		}
		request.setAttribute("tenderReward", tenderReward);
		return SUCCESS;
	}
	
	@Action(value="verifyReward")
	public String verifyReward(){
		checkAdminValidImg();//验证验证码
		long rewardId = paramLong("rewardId");
		TenderReward reward = tenderRewardService.getTenderRewardById(rewardId);
		if (reward ==null) {
			message("数据异常，请重新审核！", "/admin/account/showTenderRewardList.html");
			return ADMINMSG;
		}
		if (reward.getStatus()!=0) {
			message("此投标奖励已经审核！");
			return ADMINMSG;
		}
		User auth_user = getAuthUser();
		int status = paramInt("status");
		String content = paramString("verify_remark");
		double extendMoney = paramDouble("extendMoney");
		reward.setVerifyUser(getAuthUser());
		reward.setVerifyIp(getRequestIp());
		reward.setStatus(status);
		reward.setExtendMoney(extendMoney);
		reward.setVerifyContent(content);
		try {
			tenderRewardService.doExtendReward(reward);
			super.systemLogAdd(auth_user, 20, "投标奖励发放,金额:"+extendMoney+",订单号:"+"");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("奖励发放失败："+e.getMessage());
			message("奖励发放失败:"+e.getMessage() , "/admin/account/showTenderRewardList.html");
			return ADMINMSG;
		}
		message("奖励发放成功！" , "/admin/account/showTenderRewardList.html");
		super.systemLogAdd(auth_user, 48, "奖励发放");
		return ADMINMSG;
				
		
	}
	//1.8.0.4_u3   TGPROJECT-337  qinjun 2014-06-23  end
	
	/**
	 * 乾多多提现对账  huangjun 2014/11/20 
	 * @return
	 * @throws IOException 
	 */
	@Action(value="queryCashBalance",
			results={@Result(name="success",type="ftl",location="/admin/account/queryCashBalance.html")})
	public String queryCashBalance() throws IOException{
		Map<String, String> map  = new HashMap<String, String>();
		String loanNo = paramString("loanNo");
		try {
			long user_id = paramLong("userId");
			User user = userService.getUserById(user_id);
			String orderNo = paramString("orderNo");
			
			
			if (StringUtils.isBlank(loanNo)) {
				map.put("error_msg", "订单号有误");
				map.put("result", "false");
			}else{
				map = apiCashService.findMmmCashBalance(orderNo,loanNo);
				map.put("result", "true");
			}
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			logger.info(e.getMessage());
			map.put("result", "false");
			map.put("error_msg", "查询错误！");
		
		}
		map.put("loanNo", loanNo);
		printJson(map);
		return null;
	}
	
	/**
	 * 乾多多提现对账  huangjun 2014/11/20 
	 * @return
	 * @throws IOException 
	 */
	@Action(value="cashAccountBack",
			results={@Result(name="success",type="ftl",location="/admin/account/cashAccountBack.html")})
	public String cashAccountBack() {
		String loanNo = paramString("loanNo");
		String orderNo = paramString("orderNo");
		String cashStatus= paramString("cashStatus");
		AccountCash accountCash = accountService.getAccountCashByLoanNo(orderNo);
		if (loanNo.equals(accountCash.getLoanNo())) {
			boolean flag = apiCashService.cashAccountBack(accountCash, cashStatus);
		}else{
			message("订单号或流水号验证错误！");
			return ADMINMSG;
		}
		return null;
	}
	
	@Action(value="showBatchRecharge",results={@Result(name="success",type="ftl",location="/admin/account/showBatchRechart.html")})
	public String showBatchRecharge(){
		SearchParam param = SearchParam.getInstance().addParam("status",0).addParam("batch_no", Operator.ISNOT, "null").addPage(paramInt("page"));
		PageDataList<AccountRecharge> list = accountService.getAllVerifyBR(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	/**add by lijing 
	 * 后台批量充值审核
	 * @return
	 */
	@Action("verifyBatchRecharge")
	public String verifyBatchRecharge(){
		checkAdminValidImg();//验证验证码
		String batchno = paramString("batchno");
		if(StringUtils.isBlank(batchno)||"请输入批量充值编号".equals(batchno)){
			message("批量号不能为空","/admin/account/showBatchRecharge.html");
			return ADMINMSG;
		}
		//批量号长度校验
		String checkString = paramString("status");//审核 状态 1审核通过 2审核不通过
		ArrayList<AccountRecharge> list = accountService.getAccountRechargeByBathNoAndState(batchno);
		if(list==null || list.size() == 0 ) {
			message("该批量号不存在或已经审核通过","/admin/account/showBatchRecharge.html");
			return ADMINMSG;
		}
		logger.info("当前批量审核总数:"+list.size());
		accountService.verifyBatchRecharge(list,checkString);
		message("批号:"+batchno+",已批量审核成功", "/admin/account/showBatchRecharge.html");
		return MSG;
	}
	
	
}
