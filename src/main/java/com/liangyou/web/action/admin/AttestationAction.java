package com.liangyou.web.action.admin;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.api.pay.RealNameCertQuery;
import com.liangyou.context.AuditType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Attestation;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditLog;
import com.liangyou.domain.Message;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Userinfo;
import com.liangyou.domain.Usertrack;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.AttestationService;
import com.liangyou.service.MessageService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.tool.Page;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin/attestation")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AttestationAction extends BaseAction implements
		ModelDriven<Attestation> {

	private static Logger logger = Logger.getLogger(AttestationAction.class);
	@Autowired
	private UserService userService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private AttestationService attestationService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private UserCreditService userCreditService;
	
	private Attestation model;
	
	private Message message=new Message();
	@Override
	public Attestation getModel() {
		return model;
	}
	
	/**
	 * 认证管理
	 * */
	@Action(value="userAttestation",results={
			@Result(name="success", type="ftl",location="/admin/attestation/userAttestation.html")			
			})
	public String userAttestation() {
		int start = NumberUtils.getInt(request.getParameter("page"));
		String type = StringUtils.isNull(request.getParameter("type"));

		//modify by lxm 添加搜索条件（由数字账户可以查到该用户信息） 2017-3-17 11:20:05
		String apiUsercustId = StringUtils.isNull(request.getParameter("apiUsercustId"));
		
		String username = StringUtils.isNull(request.getParameter("username"));
		String realname = StringUtils.isNull(request.getParameter("realname"));
		int vipStatus = paramInt("vipStatus");
		//设置分页查询条件
		SearchParam param = new SearchParam().addParam("userType", new UserType(2));
		param.addOrder(OrderType.DESC, "addtime");
		param.addPage(0, start, Page.ROWS);	
		if(!StringUtils.isBlank(paramString("vipStatus"))){
			param.addParam("userCache.vipStatus", vipStatus);
		}
		
		//modify by lxm 添加搜索条件（由数字账户可以查到该用户信息） 2017-3-17 11:19:29
		if(!StringUtils.isEmpty(apiUsercustId)){
			param.addParam("apiUsercustId",Operator.LIKE, apiUsercustId);
		}
		
		if(!StringUtils.isEmpty(username)){
			param.addParam("username",Operator.LIKE, username);
		}
		if(!StringUtils.isEmpty(realname)){
			param.addParam("realname",Operator.LIKE, realname);
		}
		PageDataList pageDataList = null;
		List kflist = null;
		try{
			pageDataList = userService.getUserPageDataList(param);
			kflist = userService.getAllKefu();
		}catch(Exception e) {
			logger.error(e);
		}
		request.setAttribute("kflist", kflist);
		request.setAttribute("detailuserlist", pageDataList.getList());
		request.setAttribute("page", pageDataList.getPage());
		request.setAttribute("params", param.toMap());
		
		if(StringUtils.isBlank(type)){
			return "success";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="user_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"user_id","username","realname",
					"real_status","email_status","phone_status","vip_status","addtime"};
			String[] titles=new String[]{"ID","用户名称",
					"真实姓名","实名认证	","邮箱认证","手机认证","是否VIP","注册时间"};
			List lists = userService.getUserPageDataList(param).getList();
			try {
				ExcelHelper.writeExcel(infile, lists, User.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
	}	
	
	/**
	 * 用户认证信息
	 * */
	@Action(value="viewUserAttestation",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewUserAttestation.html")			
			})
	public String viewUserAttestation() {
		
		int userId = NumberUtils.getInt(request.getParameter("userId"));
		User user = userService.getUserById(userId);
		if(user==null){
			throw new ManageBussinessException("该用户不存在！");
		}
		Userinfo ui = user.getUserinfo();
		Usertrack ut = userService.getLastUserTrack(userId);
		int count=userService.userTrackCount(userId);
		String inviteUsername = userService.getInvitre(userId);
		request.setAttribute("user", user);
		request.setAttribute("userinfo", ui);
		request.setAttribute("userLoginCount", count);
		request.setAttribute("track", ut);
		request.setAttribute("inviteUsername", inviteUsername);
	
			
		return SUCCESS;
	}
	
	/**
	 * 实名认证
	 * */
	@Action(value="verifyRealname",results={
			@Result(name="success", type="ftl",location="/admin/attestation/realname.html")			
			})
	public String verifyRealname() {
		//获取页面参数
		int start = NumberUtils.getInt(request.getParameter("page"));
		String username=StringUtils.isNull(request.getParameter("username"));
		int realStatus=NumberUtils.getInt(request.getParameter("realStatus"));
		//分页查询条件设置
		SearchParam param = new SearchParam().addParam("userType", new UserType(2));	
		param.addPage(0, start, Page.ROWS).addOrder(OrderType.DESC, "userId");
		if(!StringUtils.isEmpty(username)){
			param.addParam("username", Operator.LIKE, username);
		}
		if(realStatus == 0 || realStatus == 2 ||realStatus == 4 ){
			param.addParam("realStatus", realStatus);
		}else if(realStatus == 1 ||realStatus == 3 ){ //通过提现实名认证的也查询出来
			param.addOrFilter("realStatus", 1,3);
		}else{
			//全部查询。
		}
		//分页查询结果
		PageDataList plist = null;
		plist=userService.getUserPageDataList(param);
		//数据回调
		if(realStatus == -1){
			param.addParam("realStatus", realStatus);
		}
		this.setPageAttribute(plist, param);
		request.setAttribute("verifyType", "realname");
		return SUCCESS;
	}
	
	/**
	 * 手机认证
	 * */
	@Action(value="verifyPhone",results={
			@Result(name="success", type="ftl",location="/admin/attestation/phone-video-scene.html")			
			})
	public String verifyPhone() {
		//获取页面参数
		int start = NumberUtils.getInt(request.getParameter("page"));
		String username=StringUtils.isNull(request.getParameter("username"));
		int phoneStatus=NumberUtils.getInt(request.getParameter("status"));
		//分页查询条件设置
		SearchParam param = new SearchParam();
		param.addPage(0, start, Page.ROWS).addOrder(OrderType.DESC, "userId").addParam("userType", new UserType(2));
		if(!StringUtils.isEmpty(username)){
			param.addParam("username", Operator.LIKE, username);
		}
		param.addParam("phoneStatus", Operator.NOTEQ, 0);
		//分页查询结果
		PageDataList plist = null;
		plist=userService.getUserPageDataList(param);
		//数据回调
		setPageAttribute(plist, param);
		request.setAttribute("verifyType", "phone");
		return SUCCESS;
	}
	
	/**
	 * 弹出审核信息审核窗口
	 */
	@Action(value="viewAudit",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewInfoAudit.html")			
			})
	public String viewAudit(){
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		String type=StringUtils.isNull(request.getParameter("type"));
		request.setAttribute("type",type);
		request.setAttribute("userId", userId);
		return SUCCESS;
	}
	
	/**
	 * 后台信息认证的审核
	 */
	@Action(value="AuditUserInfo",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewAuditInfo.html")			
			})
	public String AuditUserInfo(){
		//获取页面数据
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		int status = NumberUtils.getInt(request.getParameter("status"));
		int value = 0;
		String jifenStr = request.getParameter("jifen");
		if(jifenStr != null){
			value = NumberUtils.getInt(jifenStr);
		}
		String auditContent = request.getParameter("content");
		String type = StringUtils.isNull(request.getParameter("type"));
		String actionType = request.getParameter("actionType");
		
		String msg = "审核成功！";
		User existsUser  = userService.getUserById(userId);
		//计算积分
		Credit credit = existsUser.getCredit();
		credit.setValue(credit.getValue() + value);		
		User auth_user=getAuthUser();

		if (!StringUtils.isBlank(actionType)) {
			String validcode = request.getParameter("validcode");
			if(StringUtils.isBlank(validcode)){
				message("您输入的校验码为空！", "/admin/attestation/verifyScene.html");
				return ADMINMSG;
			}
			if (!this.checkValidImg(validcode)) {
				message("您输入的校验码错误！", "/admin/attestation/verifyScene.html");
				return ADMINMSG;
			}
			if(status == 1){
				User user = existsUser;
				//更新信用
				attestationService.updateCredit(credit);				
				//设置信用记录数据
				CreditLog creditLog = new CreditLog();
				creditLog.setUserId(userId);
				creditLog.setOp(Constant.OP_ADD);
				creditLog.setAddtime(new Date());
				creditLog.setAddip(this.getRequestIp());
				String regist_pnr=""; //接收钱管家开户返回信息
				//获取认证类型
				AuditType auditType = AuditType.getAuditType(type);
				switch (auditType) {
				case emailAudit :
					user.setEmailStatus(1);
					userService.updateUser(user);
					creditLog.setTypeId(1);
					creditLog.setValue(2);
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "邮件认证审核成功！", "邮件认证审核成功！", user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 34, "管理员审核邮箱成功");
					break;
					
				case phoneAudit:					
					user.setPhoneStatus(1);
					userService.updateUser(user);
					creditLog.setTypeId(3);
					creditLog.setValue(2);
					attestationService.addCreditLog(creditLog);
					message=getSiteMessage(auth_user, "手机认证审核成功！", "手机认证审核成功！", user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 35, "管理员审核手机成功");
					break;	
					
				case videoAudit:
					user.setVideoStatus(1);
					userService.updateUser(user);					
					creditLog.setTypeId(4);
					creditLog.setValue(10);
					attestationService.addCreditLog(creditLog);
					message=getSiteMessage(auth_user, "视频认证审核成功！", "视频认证审核成功！", user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 36, "管理员审核视频成功");
					//视频认证获得积分
					userCreditService.videoVerifyCredit(user);
					break;
					
				case sceneAudit:
					user.setSceneStatus(1);
					userService.updateUser(user);	
					creditLog.setTypeId(5);
					creditLog.setValue(10);
					attestationService.addCreditLog(creditLog);
					message=getSiteMessage(auth_user, "现场认证审核成功！", "现场认证审核成功！", user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 37, "管理员审核现场成功");
					//现场认证获得积分
					userCreditService.sceneVerifyCredit(user);
					break;
					
				case realnameAudit:
					User cardUser=userService.getUserByCardNO(existsUser.getCardId());
					if(cardUser!=null&&cardUser.getUserId()!=existsUser.getUserId()){
						msg = "该证件号码已被他人使用,不能被实名认证！";
						message(msg,"");
						return ADMINMSG;
					}
					//更新用户实名认证状态
					user.setRealStatus(1);
					userService.realname(user);
					creditLog.setTypeId(2);
					creditLog.setValue(10);
					creditLog.setRemark(auditContent);
					//添加记录
					attestationService.addCreditLog(creditLog);
					message=getSiteMessage(auth_user, "实名认证审核成功！", "实名认证审核成功！", user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 33, "管理员审核实名成功");
					
					break;
					
				default:break;
				}
				message(msg+regist_pnr, "/admin/attestation/verifyScene.html");
				return ADMINMSG;
			}else {
				
				User user = existsUser;
				message(msg, "/admin/attestation/verifyScene.html");
				AuditType auditType = AuditType.getAuditType(type);
				
				switch (auditType) {
				case emailAudit :
					user.setEmailStatus(3);
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "邮箱认证审核失败！", "邮箱认证审核失败！</br>"+"审核信息："+auditContent, user);
					messageService.addMessage(message);
					message.setIs_Authenticate("1");
					super.systemLogAdd(auth_user, 34, "管理员审核邮箱未通过");
					break;

				case phoneAudit:					
					user.setPhoneStatus(3);
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "手机认证审核失败！", "手机认证审核失败！</br>"+"审核信息："+auditContent, user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 35, "管理员审核手机未通过");
					break;
					
				case videoAudit:
					user.setVideoStatus(3);
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "视频认证审核失败！", "视频认证审核失败！</br>"+"审核信息："+auditContent, user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 36, "管理员审核视频未通过");
					break;
					
				case sceneAudit:
					user.setSceneStatus(3);
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "现场认证审核失败！", "现场认证审核失败！</br>"+"审核信息："+auditContent, user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 37, "管理员审核现场未通过");
					break;
					
				case realnameAudit:
					user.setRealStatus(4); //实名初审 状态 被拒绝
					userService.updateUser(user);
					message=getSiteMessage(auth_user, "实名认证审核失败！", "实名认证审核失败！</br>"+"审核信息："+auditContent, user);
					message.setIs_Authenticate("1");
					messageService.addMessage(message);
					super.systemLogAdd(auth_user, 33, "管理员审核实名未通过");
					break;
				default:break;
				}
				
				message("审核没有通过！", "/admin/attestation/verifyScene.html");
				return ADMINMSG;
			}
		}
		
		request.setAttribute("user",userService.getUserById(userId));
		return SUCCESS;
	}
	
	
	@Action(value="vip",results={
			@Result(name="success", type="ftl",location="/admin/attestation/vip.html")			
			})
	public String vip() {
		// vip状态
		int start = NumberUtils.getInt(request.getParameter("page"));	
		String type = StringUtils.isNull(request.getParameter("type"));
		int vipStatus = NumberUtils.getInt(request.getParameter("vip_status"));
		String username = StringUtils.isNull(request.getParameter("username"));
		String kefuUsername = StringUtils.isNull(request.getParameter("kefu_name"));
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		
		//分页查询条件设置
		SearchParam param = new SearchParam().addParam("user.userType", new UserType(2));
		param.addPage(0, start, Page.ROWS);
		
		if(!StringUtils.isEmpty(username)){
			param.addParam("user.username", Operator.LIKE, username);
		}		
		if(!StringUtils.isEmpty(kefuUsername)){
			param.addParam("kefuUsername", Operator.LIKE, kefuUsername);
		}	
		if(!StringUtils.isBlank(startTime)){
			param.addParam("kefuAddtime", Operator.GTE ,DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("kefuAddtime", Operator.LTE ,DateUtils.getDate2(endTime));
		}
		param.addOrFilter("vipStatus", 1, 2,3);//查询条件
		param.addOrder(OrderType.DESC, "kefuAddtime");//排序
		PageDataList pageDataList = userService.getUserVip(param);		
		request.setAttribute("vipinfo", pageDataList.getList());
		request.setAttribute("page", pageDataList.getPage());
		request.setAttribute("params", param.toMap());
		if(StringUtils.isBlank(type)){
			return SUCCESS;
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path==="+contextPath);
			String downloadFile="user_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"user_id","username","kefu_name",
					"kefu_addtime","type_id","account_waitvip",};
			String[] titles=new String[]{"ID","用户名称",
					"客服姓名","添加时间	","用户类型","是否缴费",};
			List list=pageDataList.getList();
			try {
				ExcelHelper.writeExcel(infile, list, UserCache.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
	}

	@Action(value="viewvip",results={
			@Result(name="success", type="ftl",location="/admin/attestation/component/viewvip.html")			
			})
	public String viewvip() {
		int vipStatus = NumberUtils.getInt(request.getParameter("vipStatus"));
		long userId = NumberUtils.getLong(request.getParameter("userId"));
		String vipVerifyRemark = request.getParameter("vip_verify_remark");
		
		SearchParam param = new SearchParam();
		User tempuser = new User(userId);
		param.addParam("user", tempuser);
		param.addOrFilter("vipStatus", 1, 2);//查询条件
		param.addPage(0, 0, Page.ROWS);
		
		UserCache vipinfo = null;
		if(userService.getUserVip(param).getList().size()>=1){
			vipinfo = (UserCache)userService.getUserVip(param).getList().get(0);
		}		
		User auth_user= getAuthUser();
		if (!StringUtils.isBlank(actionType)) {
			if (vipStatus == 1) {
				User user = userService.getUserById(userId);
				UserCache userCache = user.getUserCache();
				if(userCache.getVipStatus() == 1){
					throw new ManageBussinessException("已经处理请勿重复处理", "/admin/attestation/vip.html");
				}
				userCache.setVipStatus(1);
				userCache.setVipVerifyTime(new Date());
				userCache.setVipEndTime(DateUtils.rollYear(new Date(), 1));
				userCache.setVipVerifyRemark(vipVerifyRemark);
				
				AccountLog accountLog = new AccountLog(userId,Constant.VIP_FEE,Constant.ADMIN_ID,
						this.getTimeStr(),this.getRequestIp());
				userService.VerifyVipSuccess(userCache, accountLog);
				
				message("审核成功！", "/admin/attestation/vip.html");
				//审核成功后，发送邮件
				message = getSiteMessage(auth_user, "申请VIP成功", "申请VIP成功," + vipVerifyRemark, user );
				messageService.addMessage(message);
				
				return ADMINMSG;
			}else {
				User user = userService.getUserById(userId);
				UserCache userCache = user.getUserCache();
				userCache.setVipStatus(0);//审核不通过
		//		userinfoService.VerifyVipFail(userCache);
				userCache.setVipVerifyTime(new Date());
				userCache.setVipVerifyRemark(vipVerifyRemark);
				AccountLog accountLog = new AccountLog(userId,Constant.VIP_FEE,Constant.ADMIN_ID,
						this.getTimeStr(),this.getRequestIp());
				userService.VerifyVipFail(userCache, accountLog);
				message("审核没有通过！", "/admin/attestation/vip.html");
				
				message = getSiteMessage(auth_user, "申请VIP失败！", "申请VIP失败！</br>"+"审核信息："+vipVerifyRemark, user);
				messageService.addMessage(message);
				
				return ADMINMSG;
			}
		}
		
		request.setAttribute("vipinfo", vipinfo);
		return SUCCESS;
	}
	/**
	 * 查看用户详细信息
	 */
	@Action(value="viewUserInfo",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewUserDetailInfo.html"),
			@Result(name="viewcard", type="ftl",location="/admin/attestation/viewCard.html")
			})
	public String viewUserInfo(){
		int userId = NumberUtils.getInt(request.getParameter("userId"));
		String viewType = StringUtils.isNull(request.getParameter("viewType"));
		String viewcard = StringUtils.isNull(request.getParameter("viewcard"));
		User user = userService.getUserById(userId);
		String cardCpath="";
		String cardDpath="";
		if(!StringUtils.isBlank(viewType)){		
			
			request.setAttribute("viewcard",viewcard );
			request.setAttribute("user", user);
			return "viewcard";
		}
		request.setAttribute("user", user);		
		return SUCCESS;
	}
	
	/**
	 * 查看激活的vip用户
	 */
	@Action(value="vipActivate",results={
			@Result(name="success", type="ftl",location="/admin/attestation/vipActivate.html")
			})
	public String vipActivate(){
		User authUser = getAuthUser();
		//页面传入的分页参数
		int page = paramInt("page");
		int perNum = (paramInt("perNum") == 0) ? Page.ROWS : paramInt("perNum");
		//页面传入的查询参数
		String username = paramString("username");
		String realname = paramString("realname");		
		int vipStatus = paramInt("vipStatus");
		//查询条件
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum).addParam("user.userType", new UserType(2));
		if(!StringUtils.isBlank(paramString("vipStatus"))){
			param.addParam("vipStatus", vipStatus);
		}			
		if(!StringUtils.isBlank(paramString("username"))) {
			param.addParam("user.username", Operator.LIKE, username);
		}		
		if(!StringUtils.isBlank(realname)) {
			param.addParam("user.realname",Operator.LIKE, realname);
			logger.info("111================================"+realname);
		}
		if(!StringUtils.isBlank(paramString("type"))){//操作类型 activate:激活；cancle:取消
			int id = paramInt("id");
			UserCache uc = userService.getUserCache(id);
			if(uc.getVipStatus()==2){
				message("用户已经申请vip，请审核","/admin/attestation/vip.html?status=2","返回审核vip");
				return ADMINMSG;
			}
			if("activate".equals(paramString("type"))) {
				uc.setVipStatus(1);
				uc.setVipVerifyTime(new Date());
				uc.setKefuAddtime(new Date());
				uc.setKefuUserid((int)authUser.getUserId());
				uc.setKefuUsername(authUser.getUsername());
				ruleService.dealRule(ruleService.getRuleByNid("star_rank"),"vip_verify", uc.getUser(),0);//客户星级处理
			}
			if("cancle".equals(paramString("type"))) {
				uc.setVipStatus(0);
				uc.setVipVerifyTime(new Date());
			}
			userService.updateUserCache(uc);
		}
		
		//数据库查询
		PageDataList data = userService.getUserVip(param);
		//数据返回
		setPageAttribute(data, param);
		return SUCCESS;
	}
	
	/**
	 * 查看用户 YJF实名认证信息
	 */
	@Action(value="viewYjfRealName",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewYjfRealName.html")
			})
	public String viewYjfRealName(){
		long userId = paramLong("userId");
		User queryUser = userService.getUserById(userId);
		if(queryUser == null){
			throw new ManageBussinessException("该用户已不存在！", "");
		}		
		/*YJF实名认证查询返回：
		 *UNAUTHERIZED(未认证),
		 *NEW_APP(待认证),
		 *AUDIT_DENY(公安部驳回),
		 *AUDIT_PASSED(公安部通过),
		 *CHECK_DENY(审核驳回),
		 *CHECK_PASSED(审核通过),
		 *FORCE_DENY(强制驳回),
		 *NEW_APP_DENY(待认证驳回);
		 **/
		RealNameCertQuery realNameCertQuery = null;
		request.setAttribute("result", realNameCertQuery);
		request.setAttribute("user", queryUser);
		return SUCCESS;
	}
	

	/**
	 * 撤回实名认证
	 */	
	
	@Action("realNameRevocation")
	public String realNameRevocation(){
		long userId = paramLong("userId");
		User user = userService.getUserById(userId);
		if(user == null){
			throw new ManageBussinessException("该用户已不存在！", "/admin/attestation/verifyRealname.html?status=2");
		}
		//查询yjf实名认证信息
		RealNameCertQuery realNameCertQuery = null;
		if(!StringUtils.isBlank(user.getApiId())){		
		}else{
			throw new ManageBussinessException("该用户"+Global.getValue("api_name")+"账号不存在！", "/admin/attestation/verifyRealname.html?status=2");
		}
		if(realNameCertQuery==null){
			throw new ManageBussinessException("查询"+Global.getValue("api_name")+"实名认证信息失败！！", "/admin/attestation/verifyRealname.html?status=2");
		}
		if("CHECK_PASSED".equals(realNameCertQuery.getStatus())){
			throw new ManageBussinessException(Global.getValue("api_name")+"实名认证已通过，不能撤回！！", "/admin/attestation/verifyRealname.html?status=2");
		}
		if("NEW_APP".equals(realNameCertQuery.getStatus())){
			throw new ManageBussinessException(Global.getValue("api_name")+"实名认证审核中，不能撤回！！", "/admin/attestation/verifyRealname.html?status=2");
		}
		user.setRealStatus(0);
		userService.updateUser(user);
		message("撤回操作成功！", "/admin/attestation/verifyRealname.html?status=2");
		return ADMINMSG;
	}
	private Message getSiteMessage(User receive_user,String title,String content,User sent_user){
		message.setSentUser(receive_user);
		message.setReceiveUser(sent_user);
		message.setStatus(0);
		message.setSented(1);
		message.setType(Constant.SYSTEM);
		message.setName(title);
		message.setContent(content);
		message.setAddip(getRequestIp());
		message.setAddtime(new Date());
		return message; 
	}
}
