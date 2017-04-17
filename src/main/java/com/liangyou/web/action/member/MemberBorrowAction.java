package com.liangyou.web.action.member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.api.ips.IpsRepaymentNewTrade;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Account;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.PriorRepayLog;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountApply;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.WebPaid;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.AutoService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RepaymentService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserAmountService;
import com.liangyou.service.UserService;
import com.liangyou.service.WebPaidService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/member/borrow")
@ParentPackage("p2p-default") 
@Results({    
	  @Result(name="member", type="redirect",location="/member/index.html")
	})
public class MemberBorrowAction extends BaseAction implements ModelDriven<UserAmountApply> {
	
	private static Logger logger = Logger.getLogger(MemberBorrowAction.class);
	private UserAmountApply userAmountAapply = new UserAmountApply();
	
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private RepaymentService repaymentService;
	@Autowired
	private UserAmountService userAmountService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private OperateService operateService;
	@Autowired
	private MsgService msgService;
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 start
	@Autowired
	private RuleService ruleService;
	@Autowired
	private AutoService autoService;
	@Autowired
	private BorrowCollectionDao borrowCollectionDao;
	
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 stop
	
	//TGPROJECT-351 start
	@Autowired
	private WebPaidService webPaidService;
	//TGPROJECT-351 end
	
	public UserAmountApply getModel() {
		return userAmountAapply;
	}
	
	/**
	 * 正在招标
	 * @return
	 */
	@Action(value="borrowing",results={@Result(name="success", type="ftl",location="/member/borrow/publish.html")})
	public String borrowing() {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addParam("user", user)
				.addParam("status", 1)
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		// 这里查询参数 要重新处理
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
	
		request.setAttribute("borrow_type", "borrowing");
		return SUCCESS;
	}
	
	@Action(value="repaying",results={@Result(name="success", type="ftl",location="/member/borrow/repay.html")})
	public String repaying() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 6, 7)
				.addParam("user", user)
				.addParam("type",Operator.NOTEQ,Constant.TYPE_FLOW)
			   // .addParam("type", Operator.NOTEQ, Constant.TYPE_SECOND)
				.addPage(paramInt("page"))
				.addGroupBy("id")//add by lxm 2017-1-18 15:35:15 分期还款时，按borrow_id分组，避免列表显示多条
				.addOrder(OrderType.ASC, "borrowRepayments.repaymentTime");//modify by lxm 2017-1-18 15:33:45 正在还款列表按还款日期降序排序（原来是按ID）
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("verifyTime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("verifyTime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "repaying");
		return SUCCESS;
	}
	
	//v1.8.0.4_u1 TGPROJECT-258 lx start
	@Action(value="allrepay",results={@Result(name="success", type="ftl",location="/member/borrow/allrepay.html")})
	public String allrepay() {
		User user = getSessionUser();
		String borrow_type = paramString("borrow_type");
		int page = paramInt("page")==0?1:paramInt("page");
		SearchParam param = SearchParam.getInstance().addPage(page);		
		param.addParam("borrow.user", new User(user.getUserId()));
		param.addOrder(OrderType.ASC, "status");
		param.addOrder(OrderType.ASC, "repaymentTime");
		PageDataList<BorrowRepayment>	list = repaymentService.getRepayMentsBySearchParam(param);
		setPageAttribute(list, param); 
		request.setAttribute("borrow_type", borrow_type);
		request.setAttribute("repay_token", saveToken("repay_token"));
		return "success";
	}
	//v1.8.0.4_u1 TGPROJECT-258 lx end
	
	@Action(value="flowpay",results={@Result(name="success", type="ftl",location="/member/borrow/repay.html")})
	public String flowpay() {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 1)
				.addParam("user", user)
				.addParam("type", Constant.TYPE_FLOW)
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "flowpay");
		return SUCCESS;
	}
	
	@Action(value="repaid",results={@Result(name="success", type="ftl",location="/member/borrow/repay.html")})
	public String repaid() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 8)
				.addParam("user", user)
				.addParam("isAssignment", 0)
				.addParam("type",Operator.NOTEQ,Constant.TYPE_FLOW)
				.addGroupBy("id")
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "borrowRepayments.repaymentYestime");
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("verifyTime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("verifyTime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "repaid");
		return SUCCESS;
	}
	
	@Action(value="limitapp", 
			results={@Result(name="success",type="ftl",location="/member/borrow/limitapp.html")},
			interceptorRefs={@InterceptorRef("mydefault")})
	public String limitapp() {
		User user=getSessionUser();
		long user_id=user.getUserId();
		String action_type=request.getParameter("action_type");
		int page=NumberUtils.getInt(StringUtils.isNull(request.getAttribute("page")));
		//申请中的信用申请列表
		List amountlist=userAmountService.getUserAmountApplyListById(user_id);
		UserCache userCache= user.getUserCache();
		User newUser=userService.getUserById(user_id);
		UserAmount amount = newUser.getUserAmount();
		session.put(Constant.SESSION_USER, newUser);
		
		if(StringUtils.isNull(action_type).equals("add")){
			if(userAmountAapply.getAccount()<=0){
				throw new BussinessException("申请金额不能小于0");
			}
			
			// 防止重复提交
		     String tokenResult = checkToken("limitapp_token");
		      if (!StringUtils.isBlank(tokenResult)) {
		        message(tokenResult, "/member/borrow/limitapp.html");
		        return "msg";
		      }
			UserAmountApply acmoutApp = new UserAmountApply();
			acmoutApp.setType(userAmountAapply.getType());
			acmoutApp.setAccount(userAmountAapply.getAccount());
			acmoutApp.setAccountNew(userAmountAapply.getAccountNew());
			acmoutApp.setPassAccount(userAmountAapply.getAccount());
			acmoutApp.setGiveAccount(userAmountAapply.getAccount()); //申请的时候如果你有信用积分申请，则有此项，否则无。
			acmoutApp.setAccountOld(amount.getCreditUse());
			acmoutApp.setUser(new User(user_id));
			acmoutApp.setAddtime(new Date());
			acmoutApp.setAddip(this.getRequestIp());
			acmoutApp.setStatus(2);//默认提交管理员审核
			acmoutApp.setRemark(userAmountAapply.getRemark());
			acmoutApp.setContent(userAmountAapply.getContent());
			userAmountService.add(acmoutApp);
			message("信用评级申请提交成功，请等待工作人员审核！","/member/borrow/limitapp.html");
			super.systemLogAdd(user, 9, "用户申请信用额度,额度:"+acmoutApp.getAccount());
			return MSG;
		}
		amountlist=userAmountService.getUserAmountApplyListById(user_id);
		request.setAttribute("amountlist", amountlist);
		request.setAttribute("cache", userCache);			
		
		SearchParam param = SearchParam.getInstance().addPage(page).addParam("user", new User(user_id)).addOrder(OrderType.DESC, "addtime");
		PageDataList hasApplyList=userAmountService.getUserAmountApplyList(param);
		request.setAttribute("list", hasApplyList.getList());
		request.setAttribute("param", hasApplyList.getPage());
		List<CreditRank> creditRankList = Global.ALLCREDITRANK; //获取用户信用级别
	  	request.setAttribute("CreditRank", creditRankList);
	    request.setAttribute("borrow_type", "limitapp");
	    request.setAttribute("limitapp_token", saveToken("limitapp_token"));
		return SUCCESS;
	}
	
	@Action(value="repaydetail", 
			results={@Result(name="success",type="ftl",location="/member/borrow/repaymentdetail.html")},
			interceptorRefs={@InterceptorRef("mydefault")})
	public String repaydetail() {
		long borrowId = paramLong("id");
		String borrow_type = paramString("borrow_type");
		User user=getSessionUser();
		int page = paramInt("page")==0?1:paramInt("page");
		
		List<BorrowRepayment> list = null;
		if(borrowId == 0){
			SearchParam param = SearchParam.getInstance().addPage(page);		
			param.addParam("borrow.user", new User(user.getUserId()));
			list = repaymentService.getRepayMentsBySearchParam(param).getList();
		}else{
			list = repaymentService.getRepayMents(borrowId);
			Borrow borrow = borrowService.getBorrow(borrowId);
			if(borrow.getStatus() == 8){//8完成
				request.setAttribute("repay_status", "end");
			}
		}
		
		//cancel by lxm 2017-2-13 16:51:38
	    //环迅显示还款数
//		int apiType = Global.getInt("api_code");
//		if (apiType == 4) {
//			//查询此标的所有投资人
//		    List<BorrowCollection> allCollectionList = borrowCollectionDao.getCollectionList(borrowId);
//		    
//		    // 获取总期数
//	        int maxPeriod = 0;  
//	        for (BorrowCollection bc : allCollectionList) {  
//	            int period = bc.getPeriod();  
//	            maxPeriod = period > maxPeriod ? period : maxPeriod;
//	        }
//		    
//	        List collectionCtList = new ArrayList();
//		    for (int i = 0; i < maxPeriod+1; i++) {
//		    	
//		    	//查询此标当期的所有投资人
//		    	List<BorrowCollection> collectionList = borrowCollectionDao.getCollectionByBorrowIdAndPeriod(borrowId, i);
//			    int collectionCount = collectionList.size();
//				
//		    	//查询此标当期的已还款的投资人
//		    	List<BorrowCollection> repaidCollectionList = borrowCollectionDao.getRepayCollectionByPeriod(borrowId, i);
//			    int repdcoltCount = repaidCollectionList.size();
//			    
//		    	Map map = new HashMap();
//			    map.put("collectionCount", collectionCount);
//			    map.put("repdcoltCount", repdcoltCount);
//			    collectionCtList.add(map);
//			}
//		    request.setAttribute("collectionCtList", collectionCtList);
//		    request.setAttribute("apiType", apiType);
//		}
		
		request.setAttribute("repay", list);
		request.setAttribute("borrow_type", borrow_type);
		request.setAttribute("repay_token", saveToken("repay_token"));
		return "success";
	}
	
	@Action(value="repay",
			interceptorRefs={@InterceptorRef("mydefault")})
	public String repay() throws Exception {
		
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		String tokenResult =  checkToken("repay_token");//会话Token未设定！/表单Token未设定！/请勿重复提交   //??
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult,"/member/borrow/repaying.html");
			return MSG;
		}
		Account act = accountService.getAccountByUser(getSessionUser());//用户账户信息
	    BorrowRepayment br = repaymentService.getRepayment(paramLong("id"));
	    //v1.8.0.3  TGPROJECT-15   qj  2014-04-01 start
	    double repayAccountSum = br.getRepaymentAccount() + br.getLateInterest() + getRepayManageFee(br.getBorrow());//需要还款总额=本金+利息+管理费
	    //v1.8.0.3  TGPROJECT-15   qj  2014-04-01 stop
	    if(act.getUseMoney()< repayAccountSum){
	    	message("您的可用余额不足，请充值","/member/account/newrecharge.html");
	    	return MSG;
	    }
	    if(!repaymentService.checkNoPayedRepayment(br.getPeriod(), br.getBorrow().getId())){
	    	message("您还有未还的还款,请完成后再操作","/member/borrow/repaying.html");
	    	return MSG;
	    }
	    
	   //异步处理 生成本次操作标记
  		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);//??
	    
		repaymentService.repay(paramLong("id"), param);
		super.systemLogAdd(getSessionUser(), 22, "用户还款成功,金额:"+repayAccountSum);
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 start
		MsgReq req = new MsgReq();//消息请求参数
		req.setIp(super.getRequestIp());
		req.setSender(new User(Constant.ADMIN_ID));
		req.setReceiver(user);
		req.setMsgOperate(this.msgService.getMsgOperate(20));
		req.setBorrowname(br.getBorrow().getName());
		req.setAccount("" + br.getBorrow().getAccount());
		req.setApr("" + br.getBorrow().getApr());
		req.setMonthapr(NumberUtils.format2Str(br.getBorrow().getApr()/12));
		req.setRepaymentTime(DateUtils.dateStr2(new Date()));
		DisruptorUtils.sendMsg(req);
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
		// 通知投资人
		List<BorrowCollection> collectList=borrowService.getCollectionByBorrowIdAndPeriod(br.getBorrow().getId(), br.getPeriod());
		for(BorrowCollection c:collectList){
			MsgReq req1 = new MsgReq();
			req1.setIp(super.getRequestIp());
			req1.setSender(new User(Constant.ADMIN_ID));
			req1.setReceiver(this.userService.getUserById(c.getBorrowTender().getUser().getUserId()));
			req1.setMsgOperate(this.msgService.getMsgOperate(21));
			req1.setBorrowname(br.getBorrow().getName());
			req1.setAccount("" + c.getRepayAccount());
			req1.setApr("" +br.getBorrow().getApr());
			req1.setTenderRepaymentYesAccount(""+NumberUtils.add(c.getRepayYescapital(), c.getRepayYesinterest()));
			req1.setTenderAccount(""+c.getCapital());
			req1.setTenderInterest(""+c.getInterest());
			req1.setRepaymentTime(DateUtils.dateStr2(new Date()));
			DisruptorUtils.sendMsg(req1);
		}
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 end
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
		
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","　　您已成功还款，" + Global.getValue("webname") + "感谢您的支持！");
		
		//投资投资人，通知借款人。
		return RESULT;
	}
	
	//v1.8.0.3_u2  TGPROJECT-328   qj  2014-06-03 start
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 start
	private double getRepayManageFee(Borrow borrow){
		double fee = 0;
		Rule borrowFeeRule = ruleService.getRuleByNid("global_borrow_fee_rule");
		if(borrowFeeRule!=null && borrowFeeRule.getStatus() == 1){
			int type = borrowFeeRule.getValueIntByKey("collect_fee_type");//收取管理费类型  0：满标复审时收取  1：还款时收取
			if(type == 1){//扣除交易手续费,每次还款只是扣除一期的管理费用
				BorrowModel model=BorrowHelper.getHelper(borrow);
				model.getManageFeeWithRule(1);
			}
		}
		return fee;
	}
	//v1.8.0.3  TGPROJECT-15   qj  2014-04-01 stop
	//v1.8.0.3_u2  TGPROJECT-328   qj  2014-06-03 end
	
	/**
	 * 国控小薇网站提前还款方法
	 * @return
	 */
	@Action("priorRepay")
	public String priorRepay() throws Exception {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		String tokenResult =  checkToken("repay_token");
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult,"/member/borrow/repaying.html");
			return MSG;
		}
		long repay_id = paramLong("id");
		Account act = accountService.getAccountByUser(getSessionUser());
	    BorrowRepayment br = repaymentService.getRepayment(repay_id);
	    //提前还款一系列校验
	    double sumRepayMoney = getpriorRepayMoney(user,br);
	    
	    if(act.getUseMoney()< sumRepayMoney){
	    	throw new BussinessException("您的可用余额不足，请充值","/member/account/newrecharge.html");
	    }
	    if(!repaymentService.checkNoPayedRepayment(br.getPeriod(), br.getBorrow().getId())){
	    	throw new BussinessException("您还有未还的还款,请完成后在操作","/member/borrow/repaying.html");
	    }
	   //异步处理 生成本次操作标记
  		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);
	    
  		PriorRepayLog ppLog = new PriorRepayLog(br.getBorrow(),br,getRequestIp());
		repaymentService.priorRepay(paramLong("id"), param,ppLog);
		super.systemLogAdd(getSessionUser(), 22, "用户提前还款成功,金额:"+sumRepayMoney);
		

		MsgReq req = new MsgReq();
		req.setIp(super.getRequestIp());
		req.setSender(new User(Constant.ADMIN_ID));
		req.setReceiver(user);
		req.setMsgOperate(this.msgService.getMsgOperate(20));
		req.setBorrowname(br.getBorrow().getName());
		req.setAccount("" + br.getBorrow().getAccount());
		req.setApr("" + br.getBorrow().getApr());
		req.setRepaymentTime(DateUtils.dateStr2(new Date()));
		DisruptorUtils.sendMsg(req);

		// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
		// 通知投资人
		List<BorrowCollection> collectList = borrowService
				.getCollectionByBorrowIdAndPeriod(br.getBorrow().getId(),
						br.getPeriod());
		for (BorrowCollection c : collectList) {
			MsgReq req1 = new MsgReq();
			req1.setIp(super.getRequestIp());
			req1.setSender(new User(Constant.ADMIN_ID));
			req1.setReceiver(this.userService.getUserById(c.getBorrowTender()
					.getUser().getUserId()));
			req1.setMsgOperate(this.msgService.getMsgOperate(21));
			req1.setBorrowname(br.getBorrow().getName());
			req1.setAccount("" + br.getBorrow().getAccount());
			req1.setApr("" + br.getBorrow().getApr());
			req1.setTenderRepaymentYesAccount(""
					+ NumberUtils.add(c.getRepayYescapital(),
							c.getRepayYesinterest()));
			req1.setTenderAccount("" + c.getCapital());
			req1.setTenderInterest("" + c.getInterest());
			req1.setRepaymentTime(DateUtils.dateStr2(new Date()));
			DisruptorUtils.sendMsg(req1);
		}
		// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
		
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，提前还款成功，请您返回查看");
		return RESULT;
	}
	
	private double getpriorRepayMoney(User user,BorrowRepayment br){
		if(br == null){
			throw new BussinessException("不存在的还款");
		}
		if(br.getStatus() != 0){
			throw new BussinessException("不存在的还款");
		}
		if(user.getUserId() != br.getBorrow().getUser().getUserId()){
			throw new BussinessException("不存在的还款");
		}
		//计算提前还款总共要偿还的金额:待还本金+重新计算的利息+补偿金
	    //重新计算还款利息：借款本金*年利率*（提前还款日-借款初始日）/360
		long nowTime = NumberUtils.getLong(DateUtils.getNowTimeStr()); //当前还款的
		Date startDate = DateUtils.getNextDayYYYYMMdd(br.getBorrow().getVerifyTime());
	    long verifyTime =  DateUtils.getTime(startDate); //满标复审时间
	    long dates = (nowTime-verifyTime)%(60*60*24)>0?(nowTime-verifyTime)/(60*60*24)+1:(nowTime-verifyTime)/(60*60*24);  //计算从满标复审日到提前还款日中间的时间间隔（相当于重新计算借款时间）
	    double repayInterest = br.getBorrow().getApr()/100* br.getCapital() * dates /360;
	    //计算补偿金
	    double compenMoney = br.getBorrow().getApr()/100* br.getCapital() *30 /360;
	    double sumRepayMoney = br.getCapital() + repayInterest + compenMoney; //计算应还总额
	    return sumRepayMoney;
	}
	
	
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun start 
	/**
	 * 汇丰提前还款方法
	 * @return
	 */
	@Action("hfPriorRepay")
	public String hfPriorRepay() throws Exception {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		String tokenResult =  checkToken("repay_token");
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult,"/member/borrow/repaying.html");
			return MSG;
		}
		long id = paramLong("id");
		BorrowRepayment repay = repaymentService.getRepayment(id);
		Borrow borrow = repay.getBorrow();
	    if(borrow.getUser().getUserId() != user.getUserId()){
	    	throw new BussinessException("异常操作！");
	    }
	    if(!repaymentService.checkNoPayedRepayment(repay.getPeriod(), repay.getBorrow().getId())){
	    	throw new BussinessException("您还有未还的还款,请完成后在操作","/member/borrow/repaying.html");
	    }
	    //提前还款一系列校验
	    double sumRepayMoney = repaymentService.getHfPriorRepayMoney(borrow).get("repayAccount");
	    //异步处理 生成本次操作标记
  		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);
  		PriorRepayLog ppLog = new PriorRepayLog(borrow,repay,getRequestIp());
		repaymentService.hfPriorRepay(repay, param,ppLog);
		super.systemLogAdd(getSessionUser(), 22, "用户提前还款成功,金额:"+sumRepayMoney);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，提前还款成功，请您返回查看");
		
		MsgReq req = new MsgReq();
		req.setIp(super.getRequestIp());
		req.setSender(new User(Constant.ADMIN_ID));
		req.setReceiver(user);
		req.setMsgOperate(this.msgService.getMsgOperate(20));
		req.setBorrowname(repay.getBorrow().getName());
		req.setAccount("" + repay.getBorrow().getAccount());
		req.setApr("" + repay.getBorrow().getApr());
		req.setRepaymentTime(DateUtils.dateStr2(new Date()));
		DisruptorUtils.sendMsg(req);
		// 通知投资人
		List<BorrowCollection> collectList = borrowService
				.getCollectionByBorrowIdAndPeriod(repay.getBorrow().getId(),
						repay.getPeriod());
		for (BorrowCollection c : collectList) {
			MsgReq req1 = new MsgReq();
			req1.setIp(super.getRequestIp());
			req1.setSender(new User(Constant.ADMIN_ID));
			req1.setReceiver(this.userService.getUserById(c.getBorrowTender()
					.getUser().getUserId()));
			req1.setMsgOperate(this.msgService.getMsgOperate(21));
			req1.setBorrowname(repay.getBorrow().getName());
			req1.setAccount("" + repay.getBorrow().getAccount());
			req1.setApr("" + repay.getBorrow().getApr());
			req1.setTenderRepaymentYesAccount(""
					+ NumberUtils.add(c.getRepayYescapital(),
							c.getRepayYesinterest()));
			req1.setTenderAccount("" + c.getCapital());
			req1.setTenderInterest("" + c.getInterest());
			req1.setRepaymentTime(DateUtils.dateStr2(new Date()));
			DisruptorUtils.sendMsg(req1);
		}
		
		return RESULT;
	}
	//v1.8.0.3_u3 TGPROJECT-334  2014-06-11  qinjun end
	
	@Action(value="repayToWebSite",
			interceptorRefs={@InterceptorRef("mydefault")})
	public String repayToWebSite() { 
		
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		
		String tokenResult =  checkToken("repay_token");// 参数、session中都没用token值提示错误
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult);
			return MSG;
		}
		
		Account act = accountService.getAccountByUser(getSessionUser());
	    BorrowRepayment br = repaymentService.getRepayment(paramLong("id"));
	    if(act.getUseMoney()< br.getRepaymentAccount()){
	    	message("您的可用余额不足，请充值","/member/account/newrecharge.html");
	    	return MSG;
	    }
	    if(!repaymentService.checkNoPayedRepayment(br.getPeriod(), br.getBorrow().getId())){
	    	message("您还有未还的还款,请完成后在操作");
	    	return MSG;
	    }
	    
	  //异步处理 生成本次操作标记
  		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);
	    
		repaymentService.repayToWebSite(paramLong("id"), param);
		super.systemLogAdd(getSessionUser(), 22, "用户还款给网站成功,金额:"+br.getRepaymentAccount());
		
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，还款给网站成功，请您返回查看");
		return RESULT;
	}
	
	/**
	 * 等待初审的标
	 * @return
	 */
	@Action(value="unpublish",
			results={@Result(name="success",type="ftl",location="/member/borrow/unpublish.html")},
			interceptorRefs={@InterceptorRef("mydefault")})
	public String unpublish() {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 0)
				.addParam("user", user)			
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "unpublish");
		return SUCCESS;
	}
	
	/**
	 * 查看用户撤回的标
	 * @return
	 */
	@Action(value="showCancelBorrows",
			results={@Result(name="success",type="ftl",location="/member/borrow/showCancelBorrows.html")},
			interceptorRefs={@InterceptorRef("mydefault")})
	public String showCancelBorrows() {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status",-1,2,4,49,5,59)
				.addParam("user", user)			
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "showCancelBorrows");
		return SUCCESS;
	}

	/**
	 *  用户撤回
	 * @return
	 */
	@Action(value="cancelBorrow",interceptorRefs={@InterceptorRef("mydefault")})
	public String cancelBorrow() throws Exception {
		Borrow b = borrowService.getBorrow(paramLong("id"));
		
		if(b!=null && !operateService.checkBorrowCanCancel(b.getId())){
				message("您的借款申请正在审核中，如需撤销，请联系客服中心。" );
			return MSG;
		}
		
		checkCancelBorrow(b);
		BorrowModel wrapModel=BorrowHelper.getHelper(b);
		
		borrowService.userCancelBorrow(wrapModel);
			message("<p style='color:red;'>您的借款申请已成功撤回。如需借款，可重新提交借款申请。</p>","/member/borrow/showCancelBorrows.html");
		
		super.systemLogAdd(getSessionUser(), 16, "用户撤标成功");
		return MSG;
	}
	
	private void checkCancelBorrow(Borrow b){
		User user = getSessionUser();
		if(b == null ){
			throw new BussinessException("非法操作");
		}else{
			if(b.getStatus() == 0){//等待初审撤标
				b.setStatus(-1);
			}else if(b.getStatus() == 1){//正在招标撤标
				if(b.getAccountYes()/b.getAccount() >0.8){
					throw new BussinessException("该标已满80%，无法撤标！");
				}
				b.setStatus(5);
			}else{
				throw new BussinessException("非法操作");
			}
		}
		if(user.getUserId() != b.getUser().getUserId()){
			throw new BussinessException("非法操作");
		}
	}
	
	@Action(value="review",
			results={@Result(name="success",type="ftl",location="/member/borrow/review.html")},
			interceptorRefs={@InterceptorRef("mydefault")})
	public String review(){
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 3)
				.addParam("user", user)				
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "review");
		return SUCCESS;
	}
	
	
	@Action(value="doUserManualRepayWeb",
			interceptorRefs={@InterceptorRef("mydefault")})
	public String doUserManualRepayWeb(){
		long repayId = paramLong("id");
		BorrowRepayment repayment = borrowService.getBorrowRepaymentById(repayId);
		WebPaid webPaid = webPaidService.getWebPaidByRepayIdAndBorrowId(repayment.getId(), repayment.getBorrow().getId());
		User user = getSessionUser();
		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);
  		super.systemLogAdd(getSessionUser(), 22, "用户还款给网站成功,金额:"+webPaid.getWaitRepay());
		DisruptorUtils.doUserManualPayWeb(repayment, param);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，还款给网站成功，请您返回查看");
		return RESULT;
	}
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start
	@Action("doLZFPriorRepay")
	public String doLZFPriorRepay() throws Exception{
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		String tokenResult =  checkToken("repay_token");
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult,"/member/borrow/repaying.html");
			return MSG;
		}
		long id = paramLong("id");
		BorrowRepayment repay = repaymentService.getRepayment(id);
		Borrow borrow = repay.getBorrow();
	    if(borrow.getUser().getUserId() != user.getUserId()){
	    	throw new BussinessException("异常操作！");
	    }
	    if(!repaymentService.checkNoPayedRepayment(repay.getPeriod(), repay.getBorrow().getId())){
	    	throw new BussinessException("您还有未还的还款,请完成后在操作","/member/borrow/repaying.html");
	    }
	    //提前还款一系列校验
	    double sumRepayMoney = repaymentService.doCheckLZFPriorrepay(repay);
	    //异步处理 生成本次操作标记
  		BorrowParam param = new BorrowParam();
  		String resultFlag = user.getUserId() + "-" + System.currentTimeMillis()+"";
  		param.setResultFlag(resultFlag);
  		PriorRepayLog ppLog = new PriorRepayLog(borrow,repay,getRequestIp());
  		
  		
		repaymentService.doLZFPriorRepay(repay, param,ppLog);
		
		
		super.systemLogAdd(getSessionUser(), 22, "用户提前还款成功,金额:"+sumRepayMoney);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); //成功返回地址
		request.setAttribute("back_url", "/member/borrow/repaying.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，提前还款成功，请您返回查看");
		
		MsgReq req = new MsgReq();
		req.setIp(super.getRequestIp());
		req.setSender(new User(Constant.ADMIN_ID));
		req.setReceiver(user);
		req.setMsgOperate(this.msgService.getMsgOperate(20));
		req.setBorrowname(repay.getBorrow().getName());
		req.setAccount("" + repay.getBorrow().getAccount());
		req.setApr("" + repay.getBorrow().getApr());
		req.setRepaymentTime(DateUtils.dateStr2(new Date()));
		DisruptorUtils.sendMsg(req);
		// 通知投资人
		List<BorrowCollection> collectList = borrowService
				.getCollectionByBorrowIdAndPeriod(repay.getBorrow().getId(),
						repay.getPeriod());
		for (BorrowCollection c : collectList) {
			MsgReq req1 = new MsgReq();
			req1.setIp(super.getRequestIp());
			req1.setSender(new User(Constant.ADMIN_ID));
			req1.setReceiver(this.userService.getUserById(c.getBorrowTender()
					.getUser().getUserId()));
			req1.setMsgOperate(this.msgService.getMsgOperate(21));
			req1.setBorrowname(repay.getBorrow().getName());
			req1.setAccount("" + repay.getBorrow().getAccount());
			req1.setApr("" + repay.getBorrow().getApr());
			req1.setTenderRepaymentYesAccount(""
					+ NumberUtils.add(c.getRepayYescapital(),
							c.getRepayYesinterest()));
			req1.setTenderAccount("" + c.getCapital());
			req1.setTenderInterest("" + c.getInterest());
			req1.setRepaymentTime(DateUtils.dateStr2(new Date()));
			DisruptorUtils.sendMsg(req1);
		}
		return RESULT;
	}
	
	/**
	 * 申请提前还款操作
	 * @return
	 */
	@Action("doApplyPriorRepay")
	public String doApplyPriorRepay(){
		long repayId = paramLong("repayId");
		BorrowRepayment repayment = borrowService.getBorrowRepaymentById(repayId);
		Borrow borrow = repayment.getBorrow();
		BorrowModel model = BorrowHelper.getHelper(borrow);
		if (model.isLastPeriod(repayment.getPeriod())) {
			message("您此还款为最后一期还款，不能申请提前还款操作！","/member/borrow/repaying.html");
			return MSG;
		}
		borrow.setPriorStatus(1);
		borrowService.updateBorrow(borrow);
		message("申请提前还款操作成功，请等待审核","/member/borrow/repaying.html");
		return MSG;
	}
	
	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
	
	//TGPROJECT-376  满标手机验证合同是否同意  2014-7-22 start
	
	/**
	 * 跳转手机验证是否同意借款合同页面
	 * @return
	 */
	@Action(value="isPhoneContractView",
			results={@Result(name="success",type="ftl",location="/member/borrow/iscontractview.html")})
	public String isPhoneContractView(){
		long borrowId = paramLong("borrowId");
		Borrow borrow = borrowService.getBorrow(borrowId);
		if (borrow.getAccountYes() !=borrow.getAccount() && borrow.getStatus()!=0) {
			message("此借款不满足合同确认操作！","/member/borrow/borrowing.html");
			return MSG;
		}
		request.setAttribute("borrow", borrow);
		return "success";
	}
	
	/**
	 * @return
	 */
	@Action("doPhoneContract")
	public String doPhoneContract(){
		User user = this.getSessionUser();
		String phoneCode = paramString("phonecode");  //获取验证码
		String phone = user.getPhone();
		boolean result = checkMobileCode(phone, phoneCode);
		if (!result) {
			message("您输入的验证码有误，请核对后再验证");
			return MSG;
		}
		long borrowId = paramLong("borrowId");
		Borrow borrow = borrowService.getBorrow(borrowId);
		if (borrow.getAccountYes() !=borrow.getAccount() && borrow.getStatus()!=0) {
			message("此借款不满足合同确认操作！","/member/borrow/borrowing.html");
			return MSG;
		}
		//处理合同确认业务
		borrow.setIsPhoneContract(1);
		borrowService.updateBorrow(borrow);
		message("合同确认完毕！","/member/borrow/borrowing.html");
		return MSG;
	}
	
	//TGPROJECT-376  满标手机验证合同是否同意  2014-7-22 end
	
	/**
	 * 环迅还款
	 * @return
	 */
	@Action(value="ipsRepay",
			results={@Result(name="success",type="ftl",location="/member/ips/ipscommit.html")})
	public String ipsRepay(){
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆","/user/login.html");
	    	return MSG;
		}
		String tokenResult =  checkToken("repay_token");
		if(!StringUtils.isBlank(tokenResult)){
			message(tokenResult,"/member/borrow/repaying.html");
			return MSG;
		}
		Account act = accountService.getAccountByUser(getSessionUser());
	    BorrowRepayment br = repaymentService.getRepayment(paramLong("id"));
	    if (br ==null) {
	    	message("您本次还款操作有误，请联系客服人员核实！","/member/borrow/repaying.html");
	    	return MSG;
		}
	    if(!repaymentService.checkNoPayedRepayment(br.getPeriod(), br.getBorrow().getId())){
	    	message("您还有未还的还款,请完成后在操作","/member/borrow/repaying.html");
	    	return MSG;
	    }
	    
	  IpsRepaymentNewTrade ips =   borrowService.doIpsRepayment(br.getId());
	  request.setAttribute("ips", ips);
		return SUCCESS;
	}
}
