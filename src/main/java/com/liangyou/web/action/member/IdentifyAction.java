package com.liangyou.web.action.member;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.chinapnr.CorpRegisterQuery;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.NewAuthorizeQuery;
import com.liangyou.api.pay.PayModelHelper;
import com.liangyou.api.pay.RealNameCertQuery;
import com.liangyou.context.ChinaPnrType;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.UserInvitateCodeDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.Area;
import com.liangyou.domain.Attestation;
import com.liangyou.domain.AttestationType;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.MsgReq;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.service.ApiService;
import com.liangyou.service.AttestationService;
import com.liangyou.service.MsgService;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserInvitateCodeService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.RC4Util;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

@Namespace("/member/identify")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("mydefault"))
public class IdentifyAction extends BaseAction {

	private static Logger logger = Logger.getLogger(IdentifyAction.class);

	@Autowired
	private UserService userService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private AttestationService attestationService;
	@Autowired
	RuleService ruleService;
	@Autowired
	private MsgService msgService;
	@Autowired
	ApiService apiService;
	@Autowired
	UserCreditService userCreditService;
	//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 start
	@Autowired
	RewardExtendService rewardExtendService;
	
	//v1.8.0.4_u4  TGPROJECT-355 qinjun 2014-07-04 end
	@Autowired
	UserInvitateCodeDao userInvitateCodeDao;
	private User formUser = new User();

	public User getFormUser() {
		return formUser;
	}

	public void setFormUser(User formUser) {
		this.formUser = formUser;
	}

	private File cardPic1;
	private File cardPic2;

	public File getCardPic1() {
		return cardPic1;
	}

	public void setCardPic1(File cardPic1) {
		this.cardPic1 = cardPic1;
	}

	public File getCardPic2() {
		return cardPic2;
	}

	public void setCardPic2(File cardPic2) {
		this.cardPic2 = cardPic2;
	}

	private File litpic;
	private String litpicFileName;
	private String filePath;
	private String sep = File.separator;

	public File getLitpic() {
		return litpic;
	}

	public void setLitpic(File litpic) {
		this.litpic = litpic;
	}

	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 start
	@Action(value = "apiRealname", results = { @Result(name = "success", type = "ftl", location = "/member/identify/activate.html"),
			@Result(name="huifu",type="ftl",location="/member/huifu/UserRegister.html"),
			@Result(name="mmm",type="ftl",location="/member/mmm/mmmRegister.html"),
			@Result(name="mmmFastPay",type="ftl",location="/member/mmm/mmmLoanFastPay.html"),
			@Result(name="ipshtml",type="ftl",location="/member/ips/ipspcommit.html")
	})
	//v1.8.0.4  TGPROJECT-382 wsl 2014-08-08 end
	public String apiRealname(){
		String result ="";
		try {
			User user = getSessionUser();
			checkUserStatus(user);//校验用户的状态
			checkValidImg();// 校验验证码
			checkAPIRegister(user);//实名信息校验
			userService.updateUser(user);
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 start
			int apiType = NumberUtils.getInt(Global.getValue("api_code"));//双乾接口为3
			if(apiType==3&&(!StringUtils.isBlank(user.getApiId()) || user.getApiStatus()==1)){//  ApiId 钱多多标识  ------ApiStatus 1 激活 ， 0未激活。
				Object obj = apiService.doLoanFastPayApi(user); //第三方处理返回:MmmToLoanFastPay
				if(null != obj ){
					return madeApiRegisterReturn(obj,user);//mmm
				}else{
					throw new BussinessException("实名认证调用失败请联系管理员！");
				}
			}else{
				Object obj = apiService.doRegisterApi(user, isWap()); //第三方处理
				if(null != obj ){
					return madeApiRegisterReturn(obj,user);
				}else{
					throw new BussinessException("开户调用失败请联系管理员！");
				}
			}
			//v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 end
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			if(e instanceof BussinessException){
				result = e.getMessage();
			}else{
				result = "系统异常请联系管理员！";
			}
			switch (Global.getInt("api_code")) {
			//cancel by lxm 2017-2-13 16:46:37
//			case 2://易极付
//				printJson(JSON.toJSONString(result));
//				return null;
			default:
				message(result,"/member/apiRealname.html","返回重新输入>>" );
				return MSG;
			}
		}
	}

	
	
	

	/**
	 * 校验用户认证状态
	 * @param user
	 * @param phone
	 * @param realname
	 * @param email
	 * @return
	 */
	public void checkUserStatus(User user){
		//v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 start
		if((!StringUtils.isBlank(user.getApiId()) || !StringUtils.isBlank(user.getApiUsercustId()) || user.getApiStatus() == 1) && user.getRealStatus()==1){
			throw new BussinessException("您的"+Global.getString("api_name")+"账户已经开通，请返回查看", "/member/index.html");
		}
		//v1.8.0.4  TGPROJECT-382 wsl 2014-08-05 end
		/*if (user.getEmailStatus()!=1) {
			throw new BussinessException("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证");
		}*/
		//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
		boolean checkPhone = true;
		if(user.getPhoneStatus() == 1){
			checkPhone = false;
		}
		Rule rule = ruleService.getRuleByNid("is_phone_check");
		if(rule!=null && rule.getStatus() == 1){
			if(rule.getValueIntByKey("status")==0){
				checkPhone = false;
			}
		}
		if (checkPhone) {
			throw new BussinessException("请先进行手机认证", "/member/identify/phone.html", "点击手机认证");
		}
		//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
	}

	private String checkAPIRegister(User user){
		String realname = paramString("realname").replace(" ", "");
		String cardId = paramString("cardId").replace(" ", "");
		String cardType = paramString("cardType");

		if(StringUtils.isBlank(realname)){
			throw new BussinessException("真实姓名不能为空");
		}
		if(StringUtils.isBlank(cardId)){
			throw new BussinessException("证件类型不能为空！"); 
		}
		if(StringUtils.isBlank(cardType)){
			throw new BussinessException("证件号码不能为空！");
		}else{
			if (!StringUtils.isCard(cardId) && user.getUserType().getTypeId() == 2) { // eidt by gy 2016年12月2日15:18:35  修改企业营业执照号不参与校验
				throw new BussinessException("证件号码格式不正确,请重新填写！");
			}
			User u = userService.getUserByCardNO(cardId);
			if (u != null) {
				throw new BussinessException("该证件号码已被他人使用！");
			}
		}
		//v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  start
		String cardOff = paramString("cardOff");
		user.setCardOff(cardOff);
		//v1.8.0.4_u4  TGPROJECT-361  qinjun  2014-07-11  end
		user.setRealname(realname);
		user.setCardId(cardId);
		user.setCardType(cardType);
		return "";
	}

	private String madeApiRegisterReturn(Object object,User user){
		int apiType = NumberUtils.getInt(Global.getValue("api_code"));
		switch (apiType) {
		case 3:// 双乾接口
			if(!StringUtils.isBlank(user.getApiId()) || user.getApiStatus()==1){
				request.setAttribute("mmm", object);
				return "mmmFastPay";
			}else{
				request.setAttribute("mmm", object);
				return "mmm";
			}
			
		case 4:
			request.setAttribute("ips", object);
			return "ipshtml";
		default:
			throw new BussinessException("系统异常！","/member/index.html","返回个人中心");
		}
	}

	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	@Action(value = "yjfRealName", results = { @Result(name = "success", type = "ftl", location = "/member/identify/yjfRealName.html") })
	public String yjfRealName(){
		User user = getSessionUser();
		NewAuthorize na = PayModelHelper.yjfRealName(user);
		request.setAttribute("yjf", na);
		return SUCCESS;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end

	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun start
	@Action("yjfRealStatusQuery")
	public String yjfRealStatusQuery(){
		User user = getSessionUser();
		NewAuthorizeQuery query = PayModelHelper.yjfRealNameQuery(user);
		message("查询信息："+query.getResultMessage());
		return MSG;
	}
	//v1.8.0.3_u2 TGPROJECT-293 2014-05-29   qinjun end

	/**
	 * 我的帐号，实名认证
	 * 
	 * @return
	 */
	@Action(value = "realname", results = { @Result(name = "success", type = "ftl", location = "/member/identify/realname.html") })
	public String realname() {
		String backUrl = "/member/identify/realname.html";
		User sessionuser = getSessionUser();
		HttpSession requestSession = request.getSession(false);
		long user_id = sessionuser.getUserId();
		User user = userService.getUserById(user_id);
		String type = StringUtils.isNull(request.getParameter("type"));
		//		checkApiRealName(user);
		if (!StringUtils.isBlank(type)) {// 如果是表单提交的数据
			requestSession.setAttribute("real_name_form", formUser);// 放进缓存，当用户输入错误的时候，还原用户输入的信息，如果保存成功，后面记得删除。
			String checkMsg = checkRealNameForm();// 校验表单数据
			String realNameImages = dealRealNameImages(user_id); // 上传图片

			if (!StringUtils.isBlank(checkMsg)) {
				message(checkMsg, backUrl, "返回重新填写");
				return MSG;
			}
			if (!StringUtils.isBlank(realNameImages)) {
				message(realNameImages, backUrl, "返回重新申请");
				return MSG;
			}
			// 更新user
			user.setRealStatus(2);// 申请数据
			user.setRealname(formUser.getRealname());
			user.setSex(formUser.getSex());
			user.setNation(formUser.getNation());
			user.setBirthday(formUser.getBirthday());
			user.setCardOff(formUser.getCardOff());
			user.setCardType(formUser.getCardType());
			user.setCardId(formUser.getCardId());
			user.setProvince(formUser.getProvince());
			user.setCity(formUser.getCity());
			user.setArea(formUser.getArea());
			user.setCardPic1(formUser.getCardPic1());
			user.setCardPic2(formUser.getCardPic2());
			message("【实名初审】申请成功，等待审核中！", backUrl);
			super.systemLogAdd(user, 10, "用户申请实名认证");

			userService.realname(user);
			requestSession.removeAttribute("real_name_form");// 删除缓存
			return MSG;
		} else {
			if (requestSession.getAttribute("real_name_form") != null) {// 还原用户输入的信息
				request.setAttribute("user",
						requestSession.getAttribute("real_name_form"));
				requestSession.removeAttribute("real_name_form");
			} else {
				request.setAttribute("user", user);
			}
		}
		return SUCCESS;
	}

	//cancel by lxm 2017-2-13 16:47:54
	//第三方校验
//	private void checkApiRealName(User user){
//		int apiCode = Global.getInt("api_code");
//		switch (apiCode) {
//		case 1:
//			/*if(user.getEmailStatus() != 1){
//				throw new BussinessException("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证");
//			}else */
//			if(user.getPhoneStatus() != 1) {
//				throw new BussinessException("请先绑定手机！", "/member/identify/phone.html","点击绑定手机");
//			}else if(user.getRealStatus() != 1){
//				throw new BussinessException("开通汇付账号！", "/member/chinapnrRegister.html","点击开通汇付账号");
//			}
//			break;
//		case 2:
//
//			break;
//
//		default:
//			break;
//		}
//	}

	/**
	 * 校验
	 * 
	 * @return
	 */
	private String checkRealNameForm() {
		// 验证表单信息
		try {
			checkValidImg();// 校验验证码
			if (StringUtils.isEmpty(formUser.getRealname())) {
				throw new BussinessException("真实姓名不能为空,请填写！");
			}
			if (StringUtils.isEmpty(formUser.getBirthday())) {
				throw new BussinessException("出生日期不能为空,请填写！");
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date birDate = null;
				try {
					birDate = sdf.parse(formUser.getBirthday());
				} catch (ParseException e) {
					throw new BussinessException("请选择正确的出生日期!");
				}
				if (birDate.getTime() > new Date().getTime()) {
					throw new BussinessException("请选择正确的出生日期!");
				}
			}
			/*if (StringUtils.isEmpty(formUser.getCardOff())) {
				throw new BussinessException("身份证到期时间不能为空,请填写！");
			} else {
				if (!(StringUtils.checkDateString(formUser.getCardOff()) || "长期"
						.equals(formUser.getCardOff()))) {
					throw new BussinessException("身份证到期时间格式错误,请填写！");
				}
			}*/
			if (StringUtils.isEmpty(formUser.getCardId())) {
				throw new BussinessException("证件号码不能为空,请填写！");
			}

			if (StringUtils.isNull(formUser.getCardType()).equals(
					Constant.CARDID + "")) {
				if (!StringUtils.isCard(formUser.getCardId())) {
					throw new BussinessException("证件号码格式不正确,请重新填写！");
				}
			}
			User u = userService.getUserByCardNO(formUser.getCardId());
			if (u != null) {
				throw new BussinessException("该证件号码已被他人使用！");
			}
			if (cardPic1 == null || cardPic2 == null) {
				throw new BussinessException("请选择你的身份证图片！");
			}
			//国控小微过滤民族和籍贯不做校验处理
			if (!"gkxw@".equals(Global.getValue("webid"))) {
				if (formUser.getProvince().getId() == 0
						|| formUser.getCity().getId() == 0
						|| formUser.getArea().getId() == 0) {
					throw new BussinessException("籍贯信息不完整,请填写！");
				}
				if (formUser.getNation() == null) {
					throw new BussinessException("民族不能为空,请填写！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	private String dealRealNameImages(long user_id) {
		// 身份证正反面文件处理
		try {
			String contextPath = ServletActionContext.getServletContext()
					.getRealPath("/");
			String upfiesDir = contextPath + "data/upfiles/images";
			String destfilename1 = upfiesDir
					+ getUploadRealnameFileName(user_id, "_1");
			String card_pic1_path = destfilename1.replace(sep, "/");
			String destfilename2 = upfiesDir
					+ getUploadRealnameFileName(user_id, "_2");
			String card_pic2_path = destfilename2.replace(sep, "/");
			;
			card_pic1_path = truncatUrlForCard(card_pic1_path, contextPath);
			card_pic2_path = truncatUrlForCard(card_pic2_path, contextPath);
			logger.info(destfilename1);
			logger.info(destfilename2);
			File imageFile1 = null;
			File imageFile2 = null;
			BufferedImage bi = null;
			BufferedImage bi1 = null;
			try {
				imageFile1 = new File(destfilename1);
				bi = ImageIO.read(cardPic1);
				imageFile2 = new File(destfilename2);
				bi1 = ImageIO.read(cardPic2);
			} catch (Exception e) {
				logger.error(e);
				throw new BussinessException("上传图片出错!");
			}
			if (bi == null || bi1 == null) {
				throw new BussinessException("请选择正确的图片格式！");
			}
			try {
				FileUtils.copyFile(cardPic1, imageFile1);
				FileUtils.copyFile(cardPic2, imageFile2);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e);
				throw new BussinessException("上传图片出错!");
			}
			formUser.setCardPic1(card_pic1_path);
			formUser.setCardPic2(card_pic2_path);
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	@Action("showarea")
	public String showArea() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pid = (String) req.getParameter("pid");
		List<Area> areas = userinfoService.getAreaListByPid(NumberUtils
				.getInt(pid));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

	// 我的账户，重新激活邮箱
	@Action(value = "email", results = { @Result(name = "success", type = "ftl", location = "/member/identify/email.html") })
	public String email() throws Exception {
		String actionType = StringUtils.isNull(request
				.getParameter("actionType"));
		String  changType = paramString("changType");
		User user = getSessionUser();
		String emailUrl = "/member/identify/email.html";
		user = userService.getUserById(user.getUserId());
		request.setAttribute("user", user);
		if (!StringUtils.isBlank(actionType)) {// 如果是表单数据
			String emailStatus = StringUtils.isNull(user.getEmailStatus());
			String email = StringUtils.isNull(request.getParameter("email"));
			if ( emailStatus.equals("1")&&!"email_change".equals(changType) || emailStatus.equals("2")) {
				message("邮箱认证已经审核成功或正在审核中！", "");
				return MSG;
			}
			String errormsg = "";
			if (StringUtils.isBlank(email)) {
				message("email不能为空！", emailUrl);
				return MSG;
			}else if(!StringUtils.isEmail(email)){
				message("email格式不正确！", emailUrl);
				return MSG;
			} else {
				if (user.getEmail() == null || !user.getEmail().equals(email)) {//用新的邮箱去激活
					if (!userService.checkEmail(email)) {
						message("邮箱已存在，请更改然后再激活", "/member/identify/email.html");
						return MSG;
					}
					user.setEmail(email);
					userService.updateUser(user);
					request.setAttribute("user", user);
				}
				try {
					sendMail(user);
				} catch (Exception e) {
					logger.error(e.getMessage());
					errormsg = "发送激活邮件失败！";
					message(errormsg, emailUrl);
					return MSG;
				}
			}
			errormsg = "发送激活邮件成功！";
			message(errormsg, emailUrl);
			return MSG;
		}
		return SUCCESS;
	}

	// 我的账号，手机认证
	@Action(value = "phone", results = { @Result(name = "success", type = "ftl", location = "/member/identify/phone.html") })
	public String phone() throws Exception {
		String phone = StringUtils.isNull(request.getParameter("phone"));
		String phoneUrl = "/member/identify/phone.html";
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		String errormsg = "";
		//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
		/*if(user.getEmailStatus() != 1){
			message("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证");
			return MSG;
		}*/
		//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 end
		if (isBlank()) {
			request.setAttribute("user", user);
			session.put(Constant.SESSION_USER, user);
			return SUCCESS;
		}
		int phonelStatus = user.getPhoneStatus();
		if (phonelStatus == 1 || phonelStatus == 2) {
			message("手机认证已经审核成功或正在审核中！", "");
			return MSG;
		}
		if (phone == null || phone.equals("")) {
			errormsg = "手机号码不能为空！";
			message(errormsg, phoneUrl);
			return MSG;
		} else {
			if (!StringUtils.isMobile(StringUtils.isNull(phone))) {
				errormsg = "手机号码格式不对！";
				message(errormsg, phoneUrl);
				return MSG;
			}
			user.setPhone(phone);
			user.setPhoneStatus(2);
			userService.updateUser(user);
			User tempUser = userService.getUserById(user.getUserId());
			if (tempUser != null) {
				session.put(Constant.SESSION_USER, tempUser);
				request.setAttribute("user", tempUser);
			}
		}
		errormsg = "手机认证申请成功，等待管理员审核!";
		message(errormsg, phoneUrl);
		return MSG;
	}
	//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
	@Action(value = "getPhoneCode")
	public String getPhoneCode() throws Exception {
		boolean isSend = false;
		String message = "";
		String codeUniqueId = "";
		int phoneType = Global.getInt("phone_type");
		String phone = StringUtils.isNull(request.getParameter("phone"));
		User user = getSessionUser();
		try {
			if (user == null) {
				throw new BussinessException("非法访问");
			}
			if(user.getPhoneStatus()!=1){//如果用户没有绑定手机，这校验此项
				int phoneResult = userService.getPhoneOnly(phone); // 查询手机号码是否存在
				if (phoneResult > 0) {
					throw new BussinessException("手机号码已经存在！");
				}
			}
			/*if(user.getEmailStatus() != 1){
				throw new BussinessException("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证");
			}
*/
			if (phone == null || phone.equals("")) {
				throw new BussinessException("手机号码不能为空！");
			} else {
				if (!StringUtils.isMobile(StringUtils.isNull(phone))) {
					throw new BussinessException("手机号码格式不对！");
				}
			}
			// 设置标记没5分钟发标能发一次请求
			checkCanGetMobileCode(1);
			codeUniqueId = madeMobileCode(phoneType, phone, user);
			if (!StringUtils.isBlank(codeUniqueId)) {
				//v1.8.0.4  TGPROJECT-145  qj 2014-4-22 start
				message = "验证码已经发送，请注意查收，"+ Global.getInt("get_phoneCode_time")+"分钟后可以重复获取";
				//v1.8.0.4  TGPROJECT-145  qj 2014-4-22 stop
				isSend = true;
			}
		} catch (Exception e) {
			logger.error(e);
			message = e.getMessage();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", message);
		map.put("mobile_token", saveToken("mobile_token"));
		map.put("isSend", isSend+"");
		session.put(phone, codeUniqueId);  //将验证码放入session中，在手机校验完了以后，删除此session
		printJson(JSON.toJSONString(map));
		return null;
	}
	//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 end 
	@Action(value = "phoneCheck")
	public String phoneCheck() throws Exception { // 易极付验证手机验证码

		String code = paramString("code");
		String phoneUrl = "/member/identify/phone.html";
		String phone = paramString("phone");
		String codeUniqueId = "";

		User user = getSessionUser();
		if (user == null) {
			throw new BussinessException("非法访问", "/user/login.html");
		} 
		// v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
		/*if(user.getEmailStatus() != 1){
			throw new BussinessException("请先进行邮箱认证", "/member/identify/email.html", "点击邮箱认证");
		}*/
		// v1.0.8.4 TGPROJECT-55 lx 2014-04-14 end
		int phoneResult = userService.getPhoneOnly(phone); // 获取用户手机是否存在
		if (phone == null || phone.equals("")) {
			throw new BussinessException("手机号码不能为空！", phoneUrl);
		}
		if (StringUtils.isBlank(code)) {
			throw new BussinessException("验证码不能为空，请重新操作", phoneUrl);
		}
		//HAOLIP-86  lx  2014-04-24 start
		//从session中获取手机验证码，校验手机号码是否错误
		codeUniqueId = String.valueOf(session.get(phone)); 
		if (StringUtils.isBlank(codeUniqueId) || codeUniqueId.equalsIgnoreCase("null")) {
			throw new BussinessException("手机号码错误，请核对后重获取验证码提交认证请求！", phoneUrl);
		}
		//HAOLIP-86  lx  2014-04-24 end
		if (phoneResult > 0) { // 校验用户手机是否存在
			throw new BussinessException("手机号码已经存在，请核实后重新绑定手机！", phoneUrl);

		}
		if (StringUtils.isBlank(codeUniqueId)) {
			throw new BussinessException("处理失败，请重新操作", phoneUrl);
		}

		checkCode(codeUniqueId, code);
		user.setPhoneStatus(1);
		user.setPhone(phone);  
		userService.updateUser(user);
		//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start  
		User tempUser = userService.getUserById(user.getUserId());
		if (tempUser != null) {
			TempIdentifyUser tempIdentifyUser=userService.inintTempIdentifyUser(tempUser);
			session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);
		}
		//v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end
		//手机认证成功，积分处理
		userCreditService.phoneCredit(tempUser);

		message("手机认证成功！", "/member/identify/phone.html");
		super.systemLogAdd(user, 12, "用户手机认证成功");

		//v1.8.0.4  重要：验证码正确，清掉缓存中的缓存的验证码 start
		session.remove(phone);  //删除内存中存放的手机验证码信息，清楚缓存
		//v1.8.0.4 end

		MsgReq req = new MsgReq();
		req.setIp("");
		req.setReceiver(user);
		req.setSender(new User(Constant.ADMIN_ID));
		req.setMsgOperate(this.msgService.getMsgOperate(7));
		req.setTime(DateUtils.dateStr4(new Date()));
		req.setType("手机认证");
		req.setStatus("通过");
		req.setMsg("手机");
		req.setNo(user.getPhone());
		DisruptorUtils.sendMsg(req);
		return MSG;
	}

	/**
	 * 校验手机验证码是否准确方法
	 * @param codeUniqueId
	 * @param code
	 */
	private void checkCode(String codeUniqueId ,String code){
		if (!code.equals(codeUniqueId)) {
			throw new BussinessException("手机认证失败，输入验证码有误！请重新获取验证码进行验证");
		}
	}

	// 修改绑定的手机
	@Action(value = "phoneChange", 
			results = { @Result(name = "success", type = "ftl", location = "/member/security.html"),
			@Result(name="phonechange",type="ftl",location="/member/identify/phonechange.html")
	})
	public String phoneChange() throws Exception {
		User user = getSessionUser();
		String cardId = request.getParameter("cardId");
		String oldPhone = request.getParameter("oldPhone");
		String realName = request.getParameter("realName");
		String type  = paramString("query_type");
		request.setAttribute("query_type", "phone_change");
		if(StringUtils.isBlank(type)){
			if(user.getPhoneStatus() !=1){
				message("你的手机还没绑定！！","/member/identify/phone.html","返回绑定手机>>");
				return MSG;
			}

			if (StringUtils.isBlank(user.getCardId())) {
				message("还没有实名认证！！","/member/apiRealname.html","返回实名认证>>");
				return MSG;
			}
			return "success"; 
		}else{

			checkValidImgWithUrl("/member/identify/phoneChange.html");//验证码校验
			//校验用户提交的信息。
			if(!user.getCardId().equals(cardId)){//校验身份证号码
				message("身份证号码输入错误","/member/identify/phoneChange.html","返回重新输入");
				return MSG;
			}
			if(!user.getRealname().equals(realName)){//校验真实姓名
				message("真实姓名输入错误","/member/identify/phoneChange.html","返回重新输入");
				return MSG;
			}
			if(!user.getPhone().equals(oldPhone)){//校验输入的手机错误
				//v1.8.0.4 HAOLIP-88 lx 2014-04-24 start
				message("原绑定手机号码输入错误","/member/identify/phoneChange.html","返回重新输入绑定手机号码");
				//v1.8.0.4 HAOLIP-88 lx 2014-04-24 end
				return MSG;
			}
			return "phonechange";
		}
	}

	// 修改绑定的  邮箱
	@Action(value = "emailChange", 
			results = { @Result(name = "success", type = "ftl", location = "/member/security.html"),
			@Result(name="emailchange",type="ftl",location="/member/identify/emailchange.html")})
	public String emailChange() throws Exception {
		User user = getSessionUser();
		String eamilType = paramString("query_type");
		String cardId = request.getParameter("cardId");
		String oldEmail = request.getParameter("oldEmail");
		String realName = request.getParameter("realName");
		request.setAttribute("query_type", "email_change");
		if (StringUtils.isBlank(eamilType)) {// 跳转页面
			if (user.getEmailStatus() != 1) {
				message("邮箱没有激活！！", "/member/identify/email.html",
						"返回激活邮箱>>");
				return MSG;
			}
			if (StringUtils.isBlank(user.getCardId())) {
				message("还没有实名认证！！", "/member/apiRealname.html",
						"返回实名认证>>");
				return MSG;
			}
			return "success";
		}else{// 重置邮箱
			checkValidImgWithUrl("/member/identify/emailChange.html");//验证码校验
			// 校验用户提交的信息。
			if (!user.getEmail().equals(oldEmail)) {// 校验邮箱
				message("原来的邮箱验证有误！","/member/identify/emailChange.html","请你重新填写邮箱>>");
				return MSG;
			}
			if(!user.getCardId().equals(cardId)){//校验身份证号码
				message("身份证号码输入错误","/member/identify/emailChange.html","返回重新输入>>");
				return MSG;
			}
			if(!user.getRealname().equals(realName)){//校验真实姓名
				message("真实姓名输入错误","/member/identify/emailChange.html","返回重新输入>>");
				return MSG;
			}
			return "emailchange";
		}
	}

	// 修改绑定的  邮箱
	@Action(value = "getEmailCode")
	public String getEmailCode(){
		String msg="发送邮件成功，请查收邮件并获取验证码！！！";
		try {
			String email = paramString("email");
			User user =  getSessionUser();
			if(!StringUtils.isEmail(email)){
				throw new BussinessException("邮箱格式错误！！");
			}
			if (!userService.checkEmail(email)) {
				throw new BussinessException("邮箱已存在，请更改");
			}
			if(user.getEmailStatus()==0){
				throw new BussinessException("您的邮箱尚未激活");
			}
			user.setEmail(email);
			Random random = new Random();  
			int numb = random.nextInt(899999)+100000;
			sendEmailCode(user,numb+""); //发送邮件验证码
			session.put(email,numb+"");
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof BussinessException ){
				msg= e.getMessage();
			}else{
				msg="发送邮件失败！！";
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", msg);
		printJson(JSON.toJSONString(map));
		return null;

	}
	@Action(value = "updateEmailWithCode")
	public String updateEmailWithCode(){
		String email = paramString("email");
		String code = paramString("emailCode");
		String sessionCode = "";
		String msg = "邮箱修改成功";
		User user = getSessionUser();
		try {
			if(!StringUtils.isEmail(email)){
				throw new BussinessException("邮箱格式错误！！");
			}
			if (!userService.checkEmail(email)) {
				throw new BussinessException("邮箱已存在，请更改");
			}
			sessionCode =StringUtils.isNull(session.get(email)); 
			if(StringUtils.isBlank(sessionCode)){
				throw new BussinessException("邮箱输入错误");
			}
			if(sessionCode.equals(code)){//验证码核对正确
				user.setEmail(email);
				userService.updateUser(user);
				session.remove(email);
			}else{
				throw new BussinessException("验证码错误");
			}
		} catch (Exception e) {
			logger.error(e);
			msg = e.getMessage();
		}
		if("邮箱修改成功".equals(msg)){
			message(msg,"/member/identify/email.html","返回查看");
		}else{
			message(msg,"");
		}
		return MSG;
	}

	@Action(value = "scene", results = { @Result(name = "success", type = "ftl", location = "/member/identify/scene.html") })
	public String scene() throws Exception {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		request.setAttribute("user", user);
		if (isBlank()) {
			user = userService.getUserById(user.getUserId());
			request.setAttribute("user", user);
			session.put(Constant.SESSION_USER, user);
			return SUCCESS;
		}
		user.setSceneStatus(2);
		userService.updateUser(user);
		User newUser = userService.getUserById(user.getUserId());
		if (newUser != null) {
			session.put(Constant.SESSION_USER, newUser);
			request.setAttribute("user", newUser);
		}
		String msg = "现场认证申请提交成功，等待管理员审核！";
		message(msg, "/member/identify/scene.html");
		super.systemLogAdd(user, 14, "用户申请现场认证");
		return MSG;
	}

	@Action(value = "video", results = { @Result(name = "success", type = "ftl", location = "/member/identify/video.html") })
	public String video() throws Exception {
		User u = getSessionUser();
		//v1.8.0.4_u1 TGPROJECT-271 lx start
		User user=null;
		if(u!=null){
			user=userService.getUserById(u.getUserId());
		}
		//v1.8.0.4_u1 TGPROJECT-271 lx end
		request.setAttribute("user", user);
		if (isBlank()) {
			return SUCCESS;
		}
		user.setVideoStatus(2);
		userService.updateUser(user);
		User newUser = userService.getUserById(user.getUserId());
		if (newUser != null) {
			UserCache userCache = newUser.getUserCache();
			session.put(Constant.SESSION_USER, newUser);
			request.setAttribute("user", newUser);
			request.setAttribute("cache", userCache);
		}
		String msg = "视频认证申请提交成功，等待管理员审核！";
		message(msg, "/member/identify/video.html");
		super.systemLogAdd(user, 13, "用户申请视频认证");
		return MSG;
	}

	public String mobileaccess() {
		// 获取当前系统时间

		// long date=DateUtils.getTime(new Date());
		//
		// if (null!=session.get("nowdate")) {
		// logger.info("当前系统时间"+date+"上一次获取时间"+session.get("nowdate"));
		// long preDate=Long.parseLong(session.get("nowdate").toString());
		// if(session.get("nowdate") != null && (date -preDate)<60 ){
		// returnmessage="请60秒后重试。";
		// JsonUtil util=new JsonUtil();
		// util.addJson("data", returnmessage);
		// try {
		// printJson(util.toString());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// logger.error(e);
		// }
		// return null;
		// }
		// }
		//
		// //随机生成六位数的验证码
		//
		// String phone=request.getParameter("name");
		// long number= Math.round(Math.random() * 1000000);
		// MobileUtil.MobileMt(phone,String.valueOf(number));
		// session.put("checknumber", number);
		// session.put("nowdate", date);
		return null;
	}

	@Action(value = "attestation", results = { @Result(name = "success", type = "ftl", location = "/member/identify/attestation.html") })
	public String attestation() throws Exception {
		User user = getSessionUser();
		String retUrl = "/member/identify/attestation.html?tab=ziliao";
		String actionType = request.getParameter("actionType");
		String name = paramString("name");
		String content = paramString("content");
		String verifyRemark = request.getParameter("verifyRemark");
		int typeId = NumberUtils.getInt(request.getParameter("typeId"));
		String tab = paramString("tab");
		Attestation newAtt = null;
		if (!StringUtils.isBlank(actionType)) {
			String validcode = request.getParameter("validcode");
			if (litpic == null) {
				message("您上传的图片为空", retUrl);
				return MSG;
			}
			long a = litpic.length();
			System.out.println(a);
			try {
				if(a>=1048576){
					message("你上传的图片大于1M，请更换成小于1M的图片！", retUrl);
					return MSG;
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			//v1.8.0.4 HAOLIP-30 lx 2014-04-24 start
			if (!litpicFileName.matches(".*(jpg|png|gif|JPG|PNG|GIF)")) {
				message("文件类型必须为.jpg, .gif或者.png类型", retUrl);
				return MSG;
			}
			//v1.8.0.4 HAOLIP-30 lx 2014-04-24 end
			if (ImageIO.read(litpic) == null) {
				message("您输入的图片格式不对", retUrl);
				return MSG;
			}
			if (StringUtils.isBlank(validcode)) {
				message("您输入的校验码为空！", retUrl);
				return MSG;
			}
			if (!this.checkValidImg(validcode)) {
				message("您输入的校验码错误！", retUrl);
				return MSG;
			}
			moveFile(user);
			Attestation att = new Attestation();
			AttestationType attType = new AttestationType();
			attType.setTypeId(typeId);
			att.setAttestationType(attType);
			att.setAddtime(new Date());
			att.setName(name);
			att.setContent(content);
			att.setStatus(2);
			att.setUser(new User(user.getUserId()));
			att.setVerifyRemark(verifyRemark);
			att.setPic(this.filePath);
			att.setAddip(this.getRequestIp());
			newAtt = attestationService.addAttestation(att);
		}
		SearchParam param = new SearchParam();
		//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
		param.addParam("user", user).addParam("status",Operator.NOTEQ, "0").addOrder(OrderType.DESC, "id");
		//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 end
		param.addPage(paramInt("page"));
		PageDataList attestionsList = attestationService.getAttestationBySearchParam(param);
		request.setAttribute("attPage", attestionsList.getPage());
		request.setAttribute("attList", attestionsList.getList());
		request.setAttribute("attParam", param.toMap());

		PageDataList list = attestationService.getSearchUserCertify(param);
		request.setAttribute("list", list);
		setPageAttribute(list, param);
		request.setAttribute("tab", tab);
		return "success";
	}
	//v1.8.0.4 TGPROJECT-58 lx start
	@Action(value = "deleteAttestation")
	public String deleteAttestation() {
		User user=getSessionUser();
		int aId=paramInt("attestationId");
		if(aId!=0){
			attestationService.deleteAttestation(user.getUserId(),aId);
			message("资料删除成功", "/member/identify/attestation.html?tab=ziliao");
		}else{
			message("资料删除失败", "/member/identify/attestation.html?tab=ziliao");
		}
		return MSG;
	}

	@Action(value = "modifyAttestation", results = { @Result(name = "success", type = "ftl", location = "/member/identify/modifyattestation.html") })
	public String modifyAttestation() throws Exception {
		String actionType = StringUtils.isNull(request.getParameter("actionType"));
		int attestationId=paramInt("attestationId");
		if (!StringUtils.isBlank(actionType)) {
			User user = getSessionUser();
			String name = paramString("name");
			String content = paramString("content");
			String verifyRemark = request.getParameter("verifyRemark");
			int typeId = NumberUtils.getInt(request.getParameter("typeId"));
			int id=paramInt("id");
			Attestation newAtt = attestationService.getAttestationById(id);
			if(newAtt.getStatus()!=2){
				message("证明资料已经审核，请刷新后重新操作");
				return MSG;
			}
			String validcode = request.getParameter("validcode");
			if (litpic != null) {
				long a = litpic.length();
				try {
					if(a>=1048576){
						message("你上传的图片大于1M，请更换成小于1M的图片！");
						return MSG;
					}
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
				if (ImageIO.read(litpic) == null) {
					message("您输入的图片格式不对");
					return MSG;
				}
				if (StringUtils.isBlank(validcode)) {
					message("您输入的校验码为空！");
					return MSG;
				}
				if (!this.checkValidImg(validcode)) {
					message("您输入的校验码错误！");
					return MSG;
				}
				moveFile(user);
				newAtt.setPic(this.filePath);
			}
			AttestationType attType = new AttestationType();
			attType.setTypeId(typeId);
			newAtt.setAttestationType(attType);
			newAtt.setAddtime(new Date());
			newAtt.setName(name);
			newAtt.setContent(content);
			newAtt.setStatus(2);
			newAtt.setUser(new User(user.getUserId()));
			newAtt.setVerifyRemark(verifyRemark);
			newAtt.setAddip(this.getRequestIp());
			attestationService.updateAttestation(newAtt);;
			message("更新资料成功","/member/identify/attestation.html?tab=ziliao");
			return MSG;
		}
		Attestation attestation=attestationService.getAttestationById(attestationId);
		request.setAttribute("attes", attestation);
		return SUCCESS;
	}
	//v1.8.0.4 TGPROJECT-58 lx start
	@Action(value = "avatar", results = { @Result(name = "success", type = "ftl", location = "/member/identify/avatar.html") })
	public String avatar() {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		session.put(Constant.SESSION_USER, user);
		String upload = StringUtils.isNull(request.getParameter("upload"));
		// if(!upload.isEmpty()){
		//
		// }
		return SUCCESS;
	}

	private void moveFile(User user) {
		String dataPath = ServletActionContext.getServletContext().getRealPath(
				"/data");
		String contextPath = ServletActionContext.getServletContext()
				.getRealPath("/");
		Date d1 = new Date();
		String upfiesDir = dataPath + sep + "upfiles" + sep + "images" + sep;
		String destfilename1 = upfiesDir + DateUtils.dateStr2(d1) + sep
				+ user.getUserId() + "_attestation" + "_" + d1.getTime()
				+ ".jpg";
		filePath = destfilename1;
		filePath = this.truncatUrl(filePath, contextPath);
		logger.info(destfilename1);
		File imageFile1 = null;
		try {
			imageFile1 = new File(destfilename1);

			FileUtils.copyFile(litpic, imageFile1);
		} catch (Exception e) {
			logger.error(e);
			logger.error(e.getMessage());
		}

	}

	private String truncatUrl(String old, String truncat) {
		String url = "";
		url = old.replace(truncat, "");
		url = url.replace(sep, "/");
		return url;
	}

	private String truncatUrlForCard(String old, String truncat) {
		old = old.replace(sep, "/");
		truncat = truncat.replace(sep, "/");
		String url = "";
		url = old.replace(truncat, "");
		return url;
	}

	private String getUploadRealnameFileName(long userid, String flag) {
		Date d = new Date();
		StringBuffer sb = new StringBuffer();
		sb.append("/").append(DateUtils.dateStr2(d)).append("/")
		.append(d.getTime()).append("_").append(userid).append(flag)
		.append(".jpg");
		return sb.toString();
	}

	/**
	 * 查看用户 YJF实名认证信息
	 */
	@Action(value = "viewRealStatus", results = { @Result(name = "success", type = "ftl", location = "/member/identify/viewYjfRealName.html") })
	public String viewRealStatus() {
		long userId = paramLong("userId");
		User queryUser = userService.getUserById(userId);
		if (queryUser == null) {
			throw new ManageBussinessException("该用户已不存在！", "");
		}
		/*
		 * YJF实名认证查询返回：UNAUTHERIZED(未认证),NEW_APP(待认证),AUDIT_DENY(公安部驳回),
		 * AUDIT_PASSED(公安部通过),CHECK_DENY(审核驳回),CHECK_PASSED(审核通过),
		 * FORCE_DENY(强制驳回),NEW_APP_DENY(待认证驳回);
		 */
		RealNameCertQuery realNameCertQuery = null;
		request.setAttribute("result", realNameCertQuery);
		request.setAttribute("user", queryUser);
		return SUCCESS;
	}

	public String getLitpicFileName() {
		return litpicFileName;
	}

	public void setLitpicFileName(String litpicFileName) {
		this.litpicFileName = litpicFileName;
	}

	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	@Action(value = "userCorpRegister", results = { @Result(name = "success", type = "ftl", location = "/member/huifu/CorpRegister.html") })
	public String userCorpRegister(){
		User user =getSessionUser();
		String cardId = paramString("cardId");
		if(user.getCustomerType().equals(ChinaPnrType.CORP)){
			if(StringUtils.isBlank(cardId)){
				throw new BussinessException("请输入营业执照！");
			}
			user.setCardId(cardId);
			CorpRegister reg = ChinapnrHelper.corpRegister(user, ChinaPnrType.BORROWER_CORP);
			userService.updateUser(user);
			request.setAttribute("reg", reg);
		}else{
			throw new BussinessException("您不是企业用户无法开户！");
		}
		return SUCCESS;
	}

	@Action(value="userCorpRegisterApply", 
			results = { @Result(name = "success", type = "ftl", location = "/member/identify/userCorpRegisterApply.html") 
	})
	public String userCorpRegisterApply(){
		User user =getSessionUser();
		String type = paramString("type");
		if(StringUtils.isBlank(user.getCardId()) || type.equals("apply")){
			request.setAttribute("type", "apply");
		}
		request.setAttribute("user", user);
		return SUCCESS;
	}

	@Action("userCorpRegisterQuery")
	public String userCorpRegisterQuery(){
		User user =getSessionUser();
		CorpRegisterQuery  query = ChinapnrHelper.corpRegisterQuery(user);
		logger.info("查询结果："+ query.getRespCode()+ "  查询状态：" + query.getAuditStat());
		message(query.getRespDesc());
		return MSG;
	}
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start

	//TGPROJECT-376  满标手机验证合同是否同意  2014-7-22 start
	/**
	 * 发送验证码
	 */
	@Action("sendPhoneCode")
	public void sendPhoneCode(){
		int phoneType = Global.getInt("phone_type");
		String codeUniqueId = "";
		String phone = "";  //手机号码
		String  message="";  //页面提示消息
		boolean  isSend =false;  //是否成功
		User user = this.getSessionUser();
		if (null ==user) { 
			phone = paramString("phone");
		}else{
			phone = user.getPhone();
		}
		// 设置标记没5分钟发标能发一次请求
		try {
			checkCanGetMobileCode(1);
			codeUniqueId = madeMobileCode(phoneType, phone, user);
			if (!StringUtils.isBlank(codeUniqueId)) {
				message = "验证码已经发送，请注意查收，"+ Global.getInt("get_phoneCode_time")+"分钟后可以重复获取";
				isSend = true;
			}
		} catch (Exception e) {
			logger.info("手机验证码发送异常："+phone);
			message = e.getMessage();
		}

		logger.info("手机号："+phone +"本次发送的验证码为："+codeUniqueId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", message);
		map.put("isSend", isSend+"");
		session.put(phone, codeUniqueId);  //将验证码放入session中，在手机校验完了以后，删除此session
		printJson(JSON.toJSONString(map));
	}
	//TGPROJECT-376  满标手机验证合同是否同意  2014-7-22 end
}
