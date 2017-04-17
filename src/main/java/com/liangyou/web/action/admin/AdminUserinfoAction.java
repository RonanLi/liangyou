package com.liangyou.web.action.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liangyou.exception.BussinessException;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Global;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.Linkage;
import com.liangyou.domain.StarRank;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;
import com.liangyou.domain.Userinfo;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.model.account.ChannelUserQuery;
import com.liangyou.model.account.InviteUserSummary;
import com.liangyou.service.AccountService;
import com.liangyou.service.CreditConvertService;
import com.liangyou.service.LinkageService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserCreditTypeService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.tool.Page;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author Administrator
 *
 */
@Namespace("/admin/userinfo")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminUserinfoAction extends BaseAction implements  ModelDriven<Userinfo> {
	
	private static final Logger logger=Logger.getLogger(AdminUserinfoAction.class);
	
	//推荐人信息管理  给导出报表 User.getTenderMoney()传时间。
	private static String startTime;//开始时间
	private static String endTime;//结束时间
	public static String getStartTime() {
		return startTime;
	}
	public static void setStartTime(String startTime) {
		AdminUserinfoAction.startTime = startTime;
	}
	public static String getEndTime() {
		return endTime;
	}
	public static void setEndTime(String endTime) {
		AdminUserinfoAction.endTime = endTime;
	}
	

	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private LinkageService linkageService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private UserCreditTypeService userCreditTypeService;
	@Autowired
	private CreditConvertService creditConvertService;
	
	private Userinfo model = new Userinfo();
	@Override
	public Userinfo getModel() {
		return model;
	}

	
	@Action(value="userinfolist",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/userinfolist.html")			
			})
	public String userinfolist() {
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1"
				: request.getParameter("page"));
		SearchParam searchParam = new SearchParam();
		searchParam.addPage(page, Page.ROWS);
		String username=StringUtils.isNull(request.getParameter("username"));
		if(!StringUtils.isBlank(username)){
			searchParam.addParam("user.username", Operator.LIKE, username);
		}
		PageDataList<Userinfo> pageDataList = userinfoService.getUserinfoList(searchParam);
		List list = pageDataList.getList();
		List userInfoList = new ArrayList();
		for (Object u : list) {
			Map map = ((Userinfo) u).getAllStatus();
			map.put("userinfo", u);
			userInfoList.add(map);
		}
		request.setAttribute("userInfoList", userInfoList);
		request.setAttribute("page", pageDataList.getPage());
		request.setAttribute("params", searchParam.toMap());
		return "success";
	}
	
	@Action(value="edituserinfo",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/edituserinfo.html")			
			})
	public String editUserinfo() {
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		User user = userService.getUserById(userId);
		request.setAttribute("user", user);
		return SUCCESS;
	}
	
	@Action(value="updateuser",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/edituserinfo.html")			
			})
	public String updateUserinfo() {
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		User user = userService.getUserById(userId);		
		model.setUser(user);
		userinfoService.updateAll(model);//	更新缓存信息	,并保存数据库
		request.setAttribute("user", user);
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 42, "管理员修改客户信息成功");
		return SUCCESS;
	}
	
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 start
	//得到所有的积分类型
	@Action(value="usercredittypelist",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/usercredittypelist.html")			
	})
	public String usercredittypelist() {
		List<UserCreditType> list = userCreditTypeService.findAll();
		request.setAttribute("list", list);
		return "success";
	}
	//查看积分类型页面
	@Action(value="editusercredittype",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/editusercredittype.html")			
	})
	public String editusercredittype() {
		int id = NumberUtils.getInt(request.getParameter("id"));
		UserCreditType userCreditType = userCreditTypeService.find(id);
		request.setAttribute("userCreditType", userCreditType);
		return SUCCESS;
	}
	//修改积分类型
	@Action(value="updateusercredittype",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/editusercredittype.html")			
	})
	public String updateUsercreditType() {
		int id = NumberUtils.getInt(request.getParameter("id"));
		UserCreditType userCreditType = userCreditTypeService.find(id);		
		userCreditType.setAddtime(new Date());
		userCreditType.setStatus(paramInt("status"));;
		userCreditType.setCreditManagerStatus(paramInt("creditManagerStatus"));
		if(!StringUtils.isNumber(paramString("decimalManager"))||paramDouble("decimalManager")>1||paramDouble("decimalManager")<0
			||!StringUtils.isNumber(paramString("validRate"))||paramDouble("validRate")>1||paramDouble("validRate")<0
			||!StringUtils.isNumber(paramString("valueRate"))||paramDouble("valueRate")>1||paramDouble("valueRate")<0
			||!StringUtils.isNumber(paramString("value"))
				){
			throw new ManageBussinessException("输入的数据格式不正确");
		}
		try{
			Long.parseLong(paramString("value")) ;
		}catch(Exception e){
			throw new ManageBussinessException("输入的数据格式不正确或者为0");
		}
		userCreditType.setDecimalManager(paramDouble("decimalManager"));
		userCreditType.setValidRate(paramDouble("validRate"));;
		userCreditType.setValue(paramLong("value"));
		userCreditType.setValueRate(paramDouble("valueRate"));
		userCreditTypeService.update(userCreditType);//	更新缓存信息	,并保存数据库
		request.setAttribute("userCreditType", userCreditType);
		message("积分类型修改成功","/admin/userinfo/usercredittypelist.html","返回积分类型页面");
		return ADMINMSG;
	}
	//页面得到用户积分列表
	@Action(value="usercreditlist",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/usercreditlist.html")			
	})
	public String usercreditlist() {
		String username = paramString("name");
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1"
				: request.getParameter("page"));
		SearchParam param = new SearchParam();
		param.addPage(page);
		param.addOrder(OrderType.DESC, "id");
		if(username!=null&&username.trim()!=""){
			param.addParam("user.username", Operator.LIKE, username.trim());
			request.setAttribute("name", username.trim());
		}
		PageDataList<UserCredit> pageDataList = userCreditService.findPageList(param);
		setPageAttribute(pageDataList,param);
		return "success";
	}
	//查看用户积分
	@Action(value="editusercredit",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/editusercredit.html")			
	})
	public String editUserCredit() {
		long id = NumberUtils.getLong(request.getParameter("id"));
		UserCredit userCredit = userCreditService.find(id);
		request.setAttribute("userCredit", userCredit);
		return SUCCESS;
	}
	//修改用户积分
	@Action(value="updateusercredit",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/editusercredit.html")			
	})
	public String updateUserCredit() {
		long id = NumberUtils.getLong(request.getParameter("id"));
		UserCredit userCredit = userCreditService.find(id);	
		userCredit.setOpUser(getAuthUser());
		userCredit.setUpdateip(getRequestIp());
		long operation = paramLong("operation");
		if(operation==0){
			throw new ManageBussinessException("传入的数字不是整数或者是0");
		}
		if(operation+userCredit.getValidValue()<0){
			throw new ManageBussinessException("减的积分不能大于可用积分");
		}
		String remark = request.getParameter("remark");
		userCreditService.updateUserCredit(userCredit,remark,operation);
		message("用户积分修改成功","/admin/userinfo/usercreditlist.html","返回积分页面");
		return ADMINMSG;
	}
	//查看用户积分明细
	@Action(value="usercreditdetail",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/usercreditdetail.html")			
	})
	public String userCreditDetail() {
		request.setAttribute("userCreditType",userCreditTypeService.findAll());
		Map<String, Object> extraParam = new HashMap<String, Object>();
		
		int page = paramInt("page") == 0 ? 1 : paramInt("page");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page);
		param.addOrder(OrderType.DESC, "id");
		param.addParam("user.userId", Operator.EQ, paramString("userId"));
		extraParam.put("userId", paramString("userId"));
		
		if(paramInt("type_id")!=0){
			param.addParam("userCreditType.id", Operator.EQ, paramString("type_id"));
			extraParam.put("type_id", paramString("type_id"));
		}
		
		PageDataList<UserCreditLog> pageDataList = creditConvertService.findUserCreditLogList(param);
		setPageAttribute(pageDataList, extraParam);
		return SUCCESS;
	}
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-29 end
	
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 start
	@Action(value="userinvest",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/userinvest.html")			
	})
	public String userinvest() {
		SearchParam tenderParam =  SearchParam.getInstance();//出借冻结中的查询
		SearchParam userParam =  SearchParam.getInstance();//用户查询参数
		Map<String, Object> extraParam = new HashMap<String, Object>(); 
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1"
				: request.getParameter("page"));
		startTime = paramString("dotime1");
		endTime = paramString("dotime2");
		String type = paramString("actionType");
		String username=StringUtils.isNull(request.getParameter("username"));
		String realname=StringUtils.isNull(request.getParameter("realname"));
		String activityUser=StringUtils.isNull(request.getParameter("activityUser"));
		if(!StringUtils.isBlank(username)){
			userParam.addParam("user.username", username);
			extraParam.put("username", username);
		}
		if(!StringUtils.isBlank(realname)){
			userParam.addParam("user.realname", realname);
			extraParam.put("realname", realname);
		}
		if(StringUtils.isBlank(type)){//excel不需要分页，后面程序有判断。
			userParam.addPage(page);
		}
		userParam.addOrder(OrderType.DESC, "user.addtime");
		
		if(!StringUtils.isBlank(startTime)){//查询出借起始时间
			tenderParam.addParam("borrow_tender.addtime",Operator.GTE, startTime);
			extraParam.put("dotime1", startTime);
		}
		if(!StringUtils.isBlank(endTime)){//查询投资结束时间
			tenderParam.addParam("borrow_tender.addtime",Operator.LTE, endTime);
			extraParam.put("dotime2", endTime);
		}
		if(!StringUtils.isBlank(activityUser)&&"1".equals(activityUser)){//义乌贷参与活动的用户
			userParam.addParam("user.activity_status",Operator.EQ, "1");
			extraParam.put("activityUser", activityUser);
		}
		tenderParam.addGroupBy("borrow_tender.user_id"); //按照投资人分类
		PageDataList plist = accountService.getUserInvestDetail(tenderParam, userParam);
		setPageAttribute(plist, extraParam);
		
		if(StringUtils.isBlank(type)){
			return "success";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="useraccountsum_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"userName","realName","cardId","phone","tenderingCount","tenderingMoney","tenderedCount",
					                    "tenderedMoney","waitInterest","waitCapital","incomeCapital","incomeInterest"};
			String[] titles=new String[]{"用户名","姓名","身份证","手机号","冻结中出借笔数","冻结中出借总额","成功出借次数","成功出借总额","待收利息","待收本金","已收本金","已收利息"};
			
			PageDataList list=accountService.getUserInvestDetail(tenderParam, userParam);
			try {
				ExcelHelper.writeExcel(infile, list.getList(), BaseAccountSumModel.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}
	
	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 end
	@Action(value="user",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/user.html")			
			})
	public String user() {
		int page = NumberUtils.getInt(request.getParameter("page"));
		String username=StringUtils.isNull(request.getParameter("username"));
		String email=StringUtils.isNull(request.getParameter("email"));
		String realname=StringUtils.isNull(request.getParameter("realname"));
		String companyAddress=StringUtils.isNull(request.getParameter("companyAddress"));
		String companyType=StringUtils.isNull(request.getParameter("companyType"));
		String address=StringUtils.isNull(request.getParameter("address"));
		double total1 = paramDouble("total1");
		double total2 = paramDouble("total2");
		double useMoney1 = paramDouble("useMoney1");
		double useMoney2 = paramDouble("useMoney2");
		double noUseMoney1 = paramDouble("noUseMoney1");
		double noUseMoney2 = paramDouble("noUseMoney2");
		double collection1 = paramDouble("collection1");
		double collection2 = paramDouble("collection2");
		//v1.8.0.4_u1 TGPROJECT-249 lx start
		String dotime1 = paramString("dotime1");
		String dotime2 = paramString("dotime2");
		//v1.8.0.4_u1 TGPROJECT-249 lx end
		String phone = StringUtils.isNull(request.getParameter("phone"));
		if (useMoney1>0 && useMoney2>0) {
			if (useMoney1>useMoney2) {
				throw new ManageBussinessException("你输入的可用余额有误，请核对后再查询");
			}
		}
		
		if (total1>0 && total2>0) {
			if (total1>total2) {
				throw new ManageBussinessException("你输入的总金额有误，请核对后再查询");
			}
		}
		if (noUseMoney1>0 && noUseMoney2>0) {
			if (noUseMoney1>noUseMoney2) {
				throw new ManageBussinessException("你输入的冻结金额有误，请核对后再查询");
			}
		}
		if (collection1>0 && collection2>0) {
			if (collection1>collection2) {
				throw new ManageBussinessException("你输入的待收金额有误，请核对后再查询");
			}
		}
		String kefuUserName = StringUtils.isNull(request.getParameter("kefu_username"));
		String type=StringUtils.isNull(request.getParameter("type"));
		int rank = paramInt("rank");    //星级
		String name = paramString("name");   //信用等级
		//新添加条件
		
		int typeId = paramInt("userType");
	
		SearchParam param=new SearchParam();
		param.addPage(page, Page.ROWS);
		if(!StringUtils.isBlank(username)) {
			param.addParam("username", Operator.LIKE, username);
//			param.addParam("kefuUsername", Operator.NOTEQ, null);
		}
		if(!StringUtils.isBlank(realname)) {
			param.addParam("realname", Operator.LIKE, realname);
		}
		if(!StringUtils.isBlank(email)) {
			param.addParam("email", Operator.LIKE, email);
		}
		if(!StringUtils.isBlank(companyAddress)){
			param.addParam("userinfo.companyAddress", Operator.LIKE,companyAddress );
		}

		
		if(!StringUtils.isBlank(companyType)){
			Linkage linkage = linkageService.getLinkageById(NumberUtils.getInt(companyType));
			if (linkage !=null) {
				if (!"".equals(linkage.getValue()) && null != linkage.getValue()) {
					param.addParam("userinfo.companyType", Operator.EQ,companyType );
				}
				
			}
			
		}
		if(!StringUtils.isBlank(address)){
			param.addParam("userinfo.address", Operator.LIKE,address );
		}
		if(typeId != 0) {
			param.addParam("userType", typeId);
		}
		if(!StringUtils.isBlank(kefuUserName)) {			
			param.addParam("userCache.kefuUsername", kefuUserName);
		}
		if (total1>0) {
			param.addParam("account.total", Operator.GTE, total1);
		}
		if (total2>0) {
			param.addParam("account.total", Operator.LTE, total2);
			
		}
		if (collection1>0) {
			param.addParam("account.collection", Operator.GTE, collection1);
		}
		if (collection2>0) {
			param.addParam("account.collection", Operator.LTE, collection2);
		}
		if (useMoney1>0) {
			param.addParam("account.useMoney", Operator.GTE, useMoney1);
		}
		if (useMoney2>0) {
			param.addParam("account.useMoney", Operator.LTE, useMoney2);
		}
		if (noUseMoney1>0) {
			param.addParam("account.noUseMoney", Operator.GTE, noUseMoney1);
		}
		if (noUseMoney2>0) {
			param.addParam("account.noUseMoney", Operator.LTE, noUseMoney2);
		}
		if (!StringUtils.isBlank(name)) {
			CreditRank creditRank = userinfoService.getCreditRankByName(name);
			param.addParam("credit.value", Operator.GTE, creditRank.getPoint1()).addParam("credit.value", Operator.LTE, creditRank.getPoint2());
		}
		if (rank != 0) {
			StarRank start = userinfoService.getStarRankByRank(rank);
			param.addParam("credit.starScore", Operator.GTE, start.getScoreStart()).addParam("credit.starScore", Operator.LTE, start.getScoreEnd());
		}
		if(!StringUtils.isBlank(phone)){
			param.addParam("phone", Operator.LIKE, phone);
		}
		//v1.8.0.4_u1 TGPROJECT-249 lx start
		if(!StringUtils.isBlank(dotime1)){
			param.addParam("addtime",Operator.GTE, DateUtils.getDate(dotime1, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(dotime2)){
			param.addParam("addtime",Operator.LTE, DateUtils.getDate(dotime2, "yyyy-MM-dd HH:mm:ss"));
		}
		//v1.8.0.4_u1 TGPROJECT-249 lx end
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList userList = userService.getUserPageDataList(param);
		List kflist = userService.getAllKefu();
	  	request.setAttribute("kflist", kflist);
	  	List userTypeList = userService.getAllUserType();//用户类型
	  	request.setAttribute("userTypeList", userTypeList);
	  	List<CreditRank> creditRankList = Global.ALLCREDITRANK; //获取用户信用级别
	  	request.setAttribute("CreditRank", creditRankList);
	  	List<StarRank> startRankList = Global.ALLSTARRANK;   //获取信用星级
	  	request.setAttribute("startRankList", startRankList);
	  	 Map<String, Object> extraparam = new HashMap<String, Object>();
	  	extraparam.put("total1", total1);
	  	extraparam.put("total2", total2);
	  	extraparam.put("useMoney1", useMoney1);
	  	extraparam.put("useMoney2", useMoney2);
	  	extraparam.put("noUseMoney1", noUseMoney1);
	  	extraparam.put("noUseMoney2", noUseMoney2);
	  	extraparam.put("collection1", collection1);
		extraparam.put("collection2", collection2);
	  	extraparam.put("rank", rank);
	  	extraparam.put("name", name);
	  //v1.8.0.4_u1 TGPROJECT-249 lx start
	  	extraparam.put("dotime1", dotime1);
	  	extraparam.put("dotime2", dotime2);
	  //v1.8.0.4_u1 TGPROJECT-249 lx end
	  	setPageAttribute(userList, param,extraparam);
		if(StringUtils.isBlank(type)){
			return SUCCESS;
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="user_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			//v1.8.0.4 TGPROJECT-86 lx 2014-04-17 start
			String[] names=new String[]{"userId","username","realname","inviteUser/.inviteUser/.username","userCache/.kefuUsername",
					"sexStr","email","qq","phone","province/.name","city/.name","area/.name"
					,"cardId","addtime","statusStr","userType/.name","account/.total","account/.useMoney","account/.noUseMoney","account/.collection"
					,"account/.repay","starRank","creditRank","apiId"};
			String[] titles=new String[]{"ID","用户名称",
					"真实姓名","推荐人","所属客服","性别","邮箱","qq","手机","省","市","县"
					,"身份证号","添加时间时间","状态","用户类型","总金额","可用金额","冻结金额","待还金额","待收金额","客户星级","信用等级",Global.getValue("api_name")+"账号"};
			//v1.8.0.4 TGPROJECT-86 lx 2014-04-17 end
			List list=userService.getUserList(param);
			try {
				ExcelHelper.writeExcel(infile, list, User.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}
	
	// 渠道客户汇总
	@Action(value = "channelUserSum", results = {@Result(name = "success", type = "ftl", location = "/admin/userinfo/channelUserSum.html")})
	public String cannelUserQuery() {
		String type = StringUtils.isNull(request.getParameter("type"));
		User auth 	= getAuthUser();
		List<ChannelUserQuery> channelUserSumList = userService.getChannelUserSum(auth);
		request.setAttribute("channelUserSumList", channelUserSumList);// 用户类型
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} 
		return null;
	}
	
	// 渠道客户明细
	@Action(value = "channelUserDetail", results = { @Result(name = "success", type = "ftl", location = "/admin/userinfo/channelUserDetail.html") })
	public String cannelUserDetail() {
		int page 			= NumberUtils.getInt(request.getParameter("page"));
		String type	 		= StringUtils.isNull(request.getParameter("type"));
		String dotime1		= paramString("dotime1");
		String dotime2		= paramString("dotime2");
		User auth 			= getAuthUser();
		SearchParam param 	= new SearchParam();
		param.addPage(page, Page.ROWS);

		if (!StringUtils.isBlank(dotime1) && !StringUtils.isBlank(dotime2)) {
			long days = DateUtils.getDistanceTime(dotime1, dotime2);
			logger.info("渠道查询时间相差: " + days + "天！");
			if (days > 20) {
				throw new BussinessException("查询时间不能超过20天！", "/admin/userinfo/channelUserDetail.html");
			}
		}

		Map<String, Object> searchParam = new HashMap<String, Object>();
		searchParam.put("startTime", dotime1);
		searchParam.put("endTime", 	 dotime2);

		PageDataList cannelUserPageData = userService.getChannelUserDetailPageDataList(param, searchParam, auth);
		
		Map<String, Object> extraparam = new HashMap<String, Object>();
		extraparam.put("dotime1", dotime1);
		extraparam.put("dotime2", dotime2);
		setPageAttribute(cannelUserPageData, param, extraparam);
		
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} else {
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path===" + contextPath);
			String downloadFile = "渠道客户明细_" + DateUtils.dateStr3(new Date()) + ".xls";
			String infile 		= contextPath + "/data/export/" + downloadFile;
			String[] names  	= new String[] {"userName", "borrowName", "timeLimitStr", "borrowApr", "tenderMoney", "tenderYescapital", "redPakcageMoney", "tenderAddtime", "borrowFulltime", "addInterest", "addAccount", "channelName", "assignmenState"};
			String[] titles	 	= new String[] {"用户名", "标的名称", "标的期限", "预期年化收益率(%)", "出借金额", "出借本金", "红包使用金额", "投标时间", "满标时间	", "加息百分比(%)", "加息收益", "渠道来源", "转让状态"};
			try {
				List<ChannelUserQuery> list = new ArrayList<ChannelUserQuery>();
				if (cannelUserPageData != null) {
					list = userService.exportChannelUserDetail(auth, searchParam);
					logger.info("导出渠道客户明细记录个数：" + list.size());
					if (!list.isEmpty()) {
						for (ChannelUserQuery cud : list) {
							if (cud.getIsday() == 1) {
								cud.setTimeLimitStr(cud.getTimeLimitDay() + "天");
							} else if (cud.getIsday() == 0) {
								cud.setTimeLimitStr(cud.getTimeLimit() + "个月");
							} else if (cud.getType() == 101) {
								cud.setTimeLimitStr("满标即还");
							}
						}
					}
				}
				ExcelHelper.writeExcel(infile, list, ChannelUserQuery.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}
	
	
	@Action(value="edituser",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/adduser.html")			
			})
	public String edituser() {
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		User user = userService.getUserById(userId);
		List userTypeList = userService.getAllUserType();
		request.setAttribute("user", user);
		request.setAttribute("userTypeList", userTypeList);
		return SUCCESS;
	}
	
	/**
	 * 操作用户积分
	 * @return
	 */
	@Action(value="editTenderValue",results={
			@Result(name="viewCredit", type="ftl",location="/admin/userinfo/manageCredit.html")			
			})
	public String editTenderValue(){
		User auth = getAuthUser();
		String type = paramString("operaType");
		long userId = paramLong("userId");
		User user = userService.getUserById(userId);
		if (type.equals("") || type =="") {
			request.setAttribute("cuser", user);
			return "viewCredit";
		}else{
			checkAdminValidImg();//验证验证码
			int score = paramInt("starScore");
			if (score == 0) {  // 校验输入的操作数字是否为数字
				throw new ManageBussinessException("你输入的积分有误，请核对后再操作！");
			}
			Credit credit = userinfoService.findUserCredit(user);
			credit.setStarScore(credit.getStarScore()+score);
			ruleService.authUpdateStartScore(score, credit, user);
			message("积分操作成功！","/admin/userinfo/user.html");
			
			super.systemLogAdd(auth, 42, "管理员修改客户积分成功");
			return ADMINMSG;
		}
		
	}
	
	/**
	 * 处理页面对用户金额进行搜索时处理金额搜索条件的方法
	 * @param money
	 * @param type
	 * @param param
	 * @return
	 */
	private SearchParam doMoney(String money,String type,SearchParam param){
		String paramStr = "account."+type;
		if ("-1".equals(money)) {
			param.addParam(paramStr, Operator.LTE,  Double.parseDouble("10000.00"));
		}else if("20+".equals(money)){
			param.addParam(paramStr, Operator.GTE,  Double.parseDouble("200000.00"));
		}else {
			if (money.indexOf("-")>0) {
				String[] str = money.split("-");
				String money1=NumberUtils.getInt(str[0])*10000 +".00";
				String money2 = NumberUtils.getInt(str[1])*10000 +".00";
				param.addParam(paramStr, Operator.GTE, Double.parseDouble(money1));
				param.addParam(paramStr, Operator.LTE, Double.parseDouble(money2));
			}
		}
		return param;
	}
	
	//v1.8.0.4_u1 TGPROJECT-249 lx start
	@Action(value="inviteinfolist",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/inviteinfolist.html")			
			})
	public String inviteinfolist() {
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1"
				: request.getParameter("page"));
		String username=StringUtils.isNull(request.getParameter("username"));
		startTime = paramString("dotime1");
		endTime = paramString("dotime2");
		String dotime3=paramString("dotime3");
		String dotime4=paramString("dotime4");
		String type = paramString("actionType");
		
		SearchParam searchParam = new SearchParam();
		SearchParam tenderParam =  SearchParam.getInstance();//投资冻结中的查询
		if(StringUtils.isBlank(type)){//excel不需要分页，后面程序有判断。
			searchParam.addPage(page, Page.ROWS);		
		}
		if(!StringUtils.isBlank(username)){
			searchParam.addParam("user2.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(dotime3)){//查询投资起始时间
			searchParam.addParam("invite_user.addtime",Operator.GTE, dotime3);
		}
		if(!StringUtils.isBlank(dotime4)){//查询投资结束时间
			searchParam.addParam("invite_user.addtime",Operator.LTE, dotime4);
		}
		if(!StringUtils.isBlank(startTime)){//查询投资起始时间
			tenderParam.addParam("borrow_tender.addtime",Operator.GTE, startTime);
		}
		if(!StringUtils.isBlank(endTime)){//查询投资结束时间
			tenderParam.addParam("borrow_tender.addtime",Operator.LTE, endTime);
		}
		searchParam.addOrder(OrderType.DESC, "invite_user.addtime");
		tenderParam.addGroupBy("borrow_tender.user_id"); //按照投资人分类
		if(StringUtils.isBlank(type)){
			PageDataList plist = userService.getInvitreUserBySearchParam(searchParam,tenderParam);
			searchParam.addParam("dotime1", startTime);
			searchParam.addParam("dotime2", endTime);
			searchParam.addParam("dotime3", dotime3);
			searchParam.addParam("dotime4", dotime4);
			request.setAttribute("inviteUsers", plist.getList());
			request.setAttribute("page", plist.getPage());
			request.setAttribute("param", searchParam.toMap());
			return "success";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="inviteuser_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"inviteUserId","username","inviteUsername","addtime","tenderedMoney"};
			
			String[] titles=new String[]{"ID","用户名称","推荐人","注册时间","出借总额"};
			
			PageDataList list = userService.getInvitreUserBySearchParam(searchParam,tenderParam);
			try {
				ExcelHelper.writeExcel(infile, list.getList(), InviteUserSummary.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
	}
	//v1.8.0.4_u1 TGPROJECT-249 lx end
	@Action(value="usercacheinfolist",results={
			@Result(name="success", type="ftl",location="/admin/userinfo/usercacheinfolist.html")			
	})
	public String usercacheinfolist() {
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1"
				: request.getParameter("page"));
		String kefuUsername=StringUtils.isNull(request.getParameter("username"));
		startTime = paramString("dotime1");
		endTime = paramString("dotime2");
		String type = paramString("actionType");
		
		SearchParam searchParam = new SearchParam();
		searchParam.addPage(page, Page.ROWS);
		if(!StringUtils.isBlank(kefuUsername)){
			searchParam.addParam("kefuUsername", Operator.LIKE, kefuUsername);
		}/*else{
			searchParam.addParam("kefuUsername",Operator.NOTEQ,"");
		}*/
		if(!StringUtils.isBlank(startTime)){
			searchParam.addParam("kefuAddtime", Operator.GTE, DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			searchParam.addParam("kefuAddtime",Operator.LTE, DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss") );
		}
		searchParam.addOrder(OrderType.DESC, "user.addtime");
		PageDataList plist = userService.getUserCacheBySearchParam(searchParam);
		request.setAttribute("userCaches", plist.getList());
		request.setAttribute("page", plist.getPage());
		if(!StringUtils.isBlank(startTime)){
			searchParam.addParam("dotime1", Operator.GTE, DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			searchParam.addParam("dotime2",Operator.LTE, DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss") );
		}
		request.setAttribute("param", searchParam.toMap());
		
		if(StringUtils.isBlank(type)){
			return "success";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="user_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"user/.userId","user/.username","kefuUsername","user/.addtime","kefuAddtime","user/.phone","user/.(Other)countUserTenderMoney"};
			
			String[] titles=new String[]{"用户ID","用户名","客服名","注册时间","添加客服时间","联系方式","出借总额"};
			
			List list=userService.getUserCacheAllList(searchParam);
			try {
				ExcelHelper.writeExcel(infile, list, UserCache.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
	}
	
}
