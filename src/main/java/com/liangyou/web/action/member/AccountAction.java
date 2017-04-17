package com.liangyou.web.action.member;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

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
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.ChinapnrHelper;
import com.liangyou.api.chinapnr.NetSave;
import com.liangyou.api.ips.IpsHelper;
import com.liangyou.api.ips.IpsNewCash;
import com.liangyou.api.ips.IpsRecharge;
import com.liangyou.api.moneymoremore.MmmCash;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmRecharge;
import com.liangyou.api.pay.PayModelHelper;
import com.liangyou.api.pay.Recharge;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountBankDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.Area;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.AreaMmm;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.DrawBankMmm;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author wujing
 * @date 2013-12-13 上午9:10:12
 */
@Namespace("/member/account")
@ParentPackage("p2p-default")
@Results({ 
	@Result(name = "success", type = "ftl", location = "/index.html"), 
	@Result(name = "member", type = "ftl", location = "/member/main.html") 
	
})
@InterceptorRefs({ @InterceptorRef("mydefault") })
public class AccountAction extends BaseAction implements ModelDriven<AccountRecharge> {

	private static final long serialVersionUID = -787973417123432434L;
	private static Logger logger = Logger.getLogger(AccountAction.class);
	private User user = new User();
	@Autowired
	private AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private AccountBankDao accountBankDao;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private RuleService ruleService;
	AccountRecharge accountRecharge = new AccountRecharge();

	@Override
	public AccountRecharge getModel() {
		return accountRecharge;
	}

	@Action(value = "newrecharge", results = { 
			@Result(name = "yjf", type = "ftl", location = "/member/account/chinapnr.html"), 
			@Result(name = "success", type = "ftl", location = "/member/account/newrecharge.html"), 
			@Result(name = "huifuchinapnr", type = "ftl", location = "/member/huifu/NetSave.html"), 
			@Result(name = "ips", type = "ftl", location = "/member/ips/ipscommit.html"), 
			@Result(name = "mmm", type = "ftl", location = "/member/mmm/mmmRecharge.html") 
			
	})
	public String newrecharge() throws Exception {
		User user = this.getSessionUser();
		user = userService.getUserById(user.getUserId());//当前用户本人
		request.setAttribute("user", user);
		if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
			logger.info("充值异常日志记录：" + user.getPhone() + ", 您的实名审核未通过， 导致充值失败！");
			super.systemLogAdd(user, 20, "您的实名审核未通过， 导致充值失败！");
			return MSG;
		}
		String result = checkRecharge(user); // 充值校验，由于汇付和易极付所需的东西不同，所以校验的东西也不同
		if (!result.equals("")) {
			return result;
		}
		String type = accountRecharge.getType();//充值类型 1为 网上充值 8 汇款 9快捷支付
		List<AccountBank> accountDeduBankList = accountService.getBankByUserId(getSessionUser().getUserId(), 1);// 获取用户签约的银交行卡
		request.setAttribute("bankList", accountDeduBankList); // 保存已经签约的银行卡
		double remitMoney = Global.getDouble("remit_money_lowest_limit");// 汇款充值最低限额
		request.setAttribute("remitMoney", remitMoney); // 保存汇款充值最低限额
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} else {
			String msg = checkRechargeType();
			return msg;
		}
	}

	private String checkRecharge(User user) {
		if (StringUtils.isBlank(user.getApiId())) {
			message("充值需要开通" + Global.getString("api_name") + "账户！", "/member/index.html", "返回主页，开通" + Global.getString("api_name") + "账户");
			logger.info("充值异常日志记录：" + user.getPhone() + ", 充值需要开通" + Global.getString("api_name") + "账户, 导致充值失败!");
			super.systemLogAdd(user, 20, "充值需要开通" + Global.getString("api_name") + "账户, 导致充值失败!");
			return MSG;
		}
		return "";
	}

	private String checkRechargeType() throws Exception {
		String type = accountRecharge.getType();// 1 ,8, 9 
		String payment = accountRecharge.getPayment();// 易极付这里先不用
		BigDecimal money = new BigDecimal(accountRecharge.getMoney()).setScale(2,BigDecimal.ROUND_HALF_UP);//充值金额
		logger.info("type:" + type + "   payment:" + payment + " money:" + money);
		if (money.compareTo(BigDecimal.ZERO) == 0) {
			message("充值金额不能为零", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 充值金额不能为零, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "充值金额不能为零, 导致充值失败!");
			return MSG;
		}
		// 验证码 这里加上
		// add by gy 2016年11月24日16:26:56
		// 这里判断验证码是否由Ajax请求传过来的。ajax请求的校验验证码，调用的方法不同
		boolean b = true;
		if (!StringUtils.isBlank(paramString("isPhoneRequest")) && paramInt("isPhoneRequest") == 1) {
			if (!ajaxCheckValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		} else {
			if (!checkValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		}
		if (!b) {
			message("验证码不正确！", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 验证码不正确, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "验证码不正确, 导致充值失败!");
			return MSG;
		}

		// 汇款充值限额
		BigDecimal remitMoney = new BigDecimal(Global.getDouble("remit_money_lowest_limit")).setScale(2,BigDecimal.ROUND_HALF_UP);
		if ("8".equals(type) && (money.compareTo(remitMoney) == -1)) {
			message("线下汇款充值金额必须大于等于" + remitMoney + "元！", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 线下汇款充值金额必须大于等于" + remitMoney + "元, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "线下汇款充值金额必须大于等于" + remitMoney + "元, 导致充值失败!");
			return MSG;
		}

		if ("1".equals(type) || "8".equals(type) || "9".equals(type)) {// 易极付网银支付和线下汇款充值及快捷支付
			User sessionUser = this.getSessionUser();
			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money.doubleValue());
			r.setRemark(Global.getValue("api_name") + "线上充值");
			r.setStatus(0);
			String returnType = madeRecharge(r, sessionUser); // 判断接口类型，封装充值对象。
			accountService.addRecharge(r);
			super.systemLogAdd(sessionUser, 20, "用户线上充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());//记录系统日志
			return returnType;
		} else if ("2".equals(type)) {// 线下充值
			User sessionUser = this.getSessionUser();
			// 汇款信息
			String serial_no = paramString("serial_no");//流水号
			String account_name = paramString("account_name");//汇款户名
			String bank_in = paramString("bank_in");//收款银行
			if (StringUtils.isBlank(serial_no) || StringUtils.isBlank(account_name) || StringUtils.isBlank(bank_in)) {
				throw new BussinessException("流水号/汇款户名/收款银行等必须填写");
			}
			String return_txt = "流水号：" + serial_no + ", 汇款户名：" + account_name + ",收款银行：" + bank_in + ", 联系手机号：" + sessionUser.getPhone();
			// 判断流水号唯一性

			if (!accountService.checkRechargeOffLine(serial_no)) {
				throw new BussinessException("流水号已经存在，请重新输入", "/member/account/newrecharge.html");
			}

			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money.doubleValue());
			r.setRemark("线下充值");
			r.setStatus(0);
			r.setTradeNo(OrderNoUtils.getInstance().getSerialNumber()); // 订单号
			r.setReturnTxt(return_txt);
			accountService.addRecharge(r);
			super.systemLogAdd(sessionUser, 20, "用户线下充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());
			message("尊敬的用户您线下充值" + Global.getValue("webname") + "已经受理，请您联系客服" + Global.getValue("fuwutel") + "做进一步的操作", "/member/account/recharge.html");
			return MSG;
		} else if ("3".equals(type)) { // 无卡代扣充值方式 TGPROJECT-137 start
			User sessionUser = this.getSessionUser();
			int id = paramInt("bankId");
			AccountBank bank = accountService.getAccountBankById(id);
			if (bank == null) {
				message("无签约银行卡，不能使用代扣充值！", "/member/account/newrecharge.html");
				return MSG;
			}
			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money.doubleValue());
			r.setRemark("易极付无卡代扣充值");
			r.setStatus(0);
			r.setAccountBank(bank);
			String orderNo = OrderNoUtils.getInstance().getSerialNumber();
			r.setTradeNo(orderNo); // 订单号
			AccountLog log = new AccountLog(1L, Constant.RECHARGE, 1L, getTimeStr(), getRequestIp());
			super.systemLogAdd(sessionUser, 20, "用户无卡代扣充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());
			// TGPROJECT-137 end
			return MSG;
		} else {
			message("不支持这种支付方式，请选择其他方式", "/member/account/newrecharge.html");
			return MSG;
		}

	}

	@Action(value = "recharge", results = { @Result(name = "success", type = "ftl", location = "/member/account/recharge.html") })
	public String recharge() {
		User user = getSessionUser();
		SearchParam param = SearchParam.getInstance().addParam("user", user).addPage(paramInt("page")).addOrder(OrderType.DESC, "addtime");
		PageDataList data = accountService.getRechargeList(param);
		setPageAttribute(data, param);
		return SUCCESS;
	}

	@Action(value = "log", results = { @Result(name = "success", type = "ftl", location = "/member/account/log.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String log() throws Exception {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String type = paramString("account_type");
		SearchParam param = SearchParam.getInstance().addParam("user.userId", user.getUserId()).addOrder(OrderType.DESC, "id").addPage(paramInt("page"));

		if (!StringUtils.isBlank(startTime)) {
			param.addParam("addtime", Operator.GTE, DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if (!StringUtils.isBlank(endTime)) {
			param.addParam("addtime", Operator.LTE, DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if (!StringUtils.isBlank(type) && !"0".equals(type)) {
			param.addParam("type", type);
		}

		PageDataList data = accountService.getAccontLogList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
		extraparam.put("dotime1", startTime);
		extraparam.put("dotime2", endTime);
		extraparam.put("account_type", type);
		setPageAttribute(data, param, extraparam); // 保存时间查询
		// setPageAttribute(data, param);
		String excelType = paramString("excel");
		if (excelType.isEmpty()) {
			return SUCCESS;
		} else {
			// v1.8.0.4 TGPROJECT-56 lx 2014-04-14 start
			if (getSessionUser() != null) {
				String contextPath = ServletActionContext.getServletContext().getRealPath("/");
				String downloadFile = "资金记录_" + DateUtils.dateStr3(new Date()) + ".xls";
				String infile = contextPath + "/data/export/" + downloadFile;
				String[] names = new String[] { "user/.username", "user/.realname", "toUser/.username", "typeName", "money", "total", "useMoney", "noUseMoney", "collection", "repay", "doremark", "addtime" };
				String[] titles = new String[] { "用户名", "真实姓名", "交易对方", "交易类型", "操作金额", "账户总额", "可用金额", "冻结金额", "待收金额", "待还" + "金额", "备注", "时间" };

				SearchParam param2 = SearchParam.getInstance();
				Date startDate = DateUtils.getDate2(startTime);
				Date endTimeDate = DateUtils.getDate2(endTime);
				if (!StringUtils.isBlank(startTime) && startDate != null) {
					param2.addParam("addtime", Operator.GTE, startDate);
				}
				if (!StringUtils.isBlank(endTime) && endTimeDate != null) {
					param2.addParam("addtime", Operator.LTE, endTimeDate);
				}
				if (!StringUtils.isBlank(type) && !"0".equals(type)) {
					param2.addParam("type", type);
				}
				param2.addParam("user.username", Operator.EQ, getSessionUser().getUsername());
				param2.addOrder(OrderType.ASC, "id");
				List list = accountService.getSumAccontLogList(param2);
				ExcelHelper.writeExcel(infile, list, AccountLog.class, Arrays.asList(names), Arrays.asList(titles));
				export(infile, downloadFile);
				// v1.8.0.4 TGPROJECT-56 lx 2014-04-14 end
			}
			return null;
		}
	}

	@Action(value = "cash", results = { @Result(name = "success", type = "ftl", location = "/member/account/cash.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String cash() throws Exception {
		User user = getSessionUser();
		SearchParam param = SearchParam.getInstance().addParam("user", user).addParam("status", Operator.NOTEQ, 4).addOrder(OrderType.DESC, "addtime").addPage(paramInt("page"));
		PageDataList data = accountService.getCashList(param);
		request.setAttribute("page", data.getPage());
		request.setAttribute("list", data.getList());
		request.setAttribute("param", param.toMap());
		return SUCCESS;
	}

	/**
	 * 手机验证码
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "getMobileCode", interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String getMobileCode() throws Exception {
		String rsu = "验证码已经发送成功，您可以1分钟后才重新获取";
		String codeId = "";
		try {
			checkCanGetMobileCode(1);// 拦截 一分钟
			codeId = userService.getMObileCodeOnly(getSessionUser());

			logger.info("发送验证码：" + codeId);
		} catch (Exception e) {
			rsu = e.getMessage();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("codeUniqueId", codeId);
		map.put("data", rsu);
		printJson(JSON.toJSONString(map));
		return null;
	}

	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start
	@Action(value = "bank", results = { @Result(name = "success", type = "ftl", location = "/member/account/bank.html"), @Result(name = "addbankcard", type = "ftl", location = "/member/huifu/UserBindCard.html"), @Result(name = "mmmFastPay", type = "ftl", location = "/member/mmm/mmmLoanFastPay.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end
	public String bank() throws Exception {
		User sessionUser = getSessionUser();
		long user_id = sessionUser.getUserId();
		User user = userService.getUserById(user_id);
		checkBank(user);// 绑卡校验
		String typeStr = StringUtils.isNull(paramString("type"));
		SearchParam drawBankParam = apiService.getdrawBankParam();// 第三方银行定义
		List drawList = null;
		switch (Global.getInt("api_code")) {
		//cancel by lxm 2017-2-13 09:54:02
//		case 1:// 汇付
//			break;
//		case 2:// 易极付
//			drawList = accountService.getDrawBankBySearchParam(drawBankParam);
//			request.setAttribute("drawBankList", drawList);
//			break;
		case 3:// 双乾
				// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 start 启用三合一接口注释
			drawList = accountService.getDrawBankMmmBySearchParam(drawBankParam);
			request.setAttribute("drawBankList", drawList);
			// v1.8.0.4 TGPROJECT-382 wsl 2014-08-08 end
			break;
		default:
			break;
		}
		if (typeStr.equals("add")) { // 添加提现银行卡
			String msg = checkApi(user);// 第三方校验
			if (!msg.equals("success")) {// msg不为success则直接提示用户
				return msg;
			}
		} else {// 查询银行卡列表
			SearchParam param = new SearchParam();
			param.addParam("user", new User(user.getUserId()));
			List bankList = accountService.getAccountBankList(param).getList();
			request.setAttribute("bankList", bankList);
		}
		request.setAttribute(Constant.SESSION_USER, user);
		return SUCCESS;
	}

	private void checkBank(User user) {
		String api_name = Global.getValue("api_name");
		if (user == null) {
			throw new BussinessException("请您先登录，然后操作", "/user/login.html");
		}
		if (isOpenApi()) {
			if (StringUtils.isBlank(user.getApiId())) {// 如果不存在apiId
				throw new BussinessException("为了您的资金安全，请您先按照开户流程，填写实名信息开通" + api_name + "账号 ");
			}
		}
	}

	private String checkApi(User user) {
		int apiType = Global.getInt("api_code");// 第三方参数
		String msg = "";
		switch (apiType) {
		//cancel by lxm 2017-2-13 16:39:32
		case 3:// 双乾接口:跳转钱多多绑卡页面
			msg = checkMmmBank(user);
			break;
		default:
			break;
		}
		return msg;
	}

	private String checkMmmBank(User user) {
		int drawBankId = paramInt("drawBank");
		String branchStr = paramString("branch");
		String accountStr = paramString("account");
		String accountSec = paramString("accountSec");

		// add by gy 2016年11月24日17:52:31 begin
		// 判断 如果是wap端请求， 则没有确认银行卡号的验证
		if (!StringUtils.isBlank(paramString("isPhoneRequest"))) {
			if (StringUtils.isBlank(accountStr))
				throw new BussinessException("银行卡账号输入不能为空！", "/member/account/bank.html");
		} else  {
			if (StringUtils.isBlank(accountStr) || StringUtils.isBlank(accountSec))
				throw new BussinessException("银行卡账号输入不能为空！", "/member/account/bank.html");
			if (!accountStr.equals(accountSec))
				throw new BussinessException("两次输入银行卡账号不一样！", "/member/account/bank.html");
		}
		// end

		// v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 start
		// 同个银行卡多次绑定的校验
		AccountBank ab = accountService.getAccountBankByCardNo(accountStr, user);
		if (ab != null) {
			throw new BussinessException("该卡号银行卡已绑定，请勿重复绑定！", "/member/account/bank.html");
		}
		// v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 end
		long province = paramLong("province");
		long city = paramLong("city");
		//long area = paramLong("area");
		/*if (province == 0 || city == 0 || area == 0) {
			throw new BussinessException("请选择完整的银行卡归属地！", "/member/account/bank.html");
		}*/
		/*
		 * 修改为 省市二级联动去掉了 完整校验
		 */
		if (province == 0 || city == 0 ) {
			throw new BussinessException("请选择完整的银行卡归属地！", "/member/account/bank.html");
		}
		DrawBankMmm drawBankmm = accountService.getDrawBankMmmById(drawBankId);
		if (drawBankmm == null) {
			throw new BussinessException("对不起,获取银行信息出错,请联系管理员！", "/member/account/bank.html");
		}
		AccountBank actbank = new AccountBank();
		actbank.setAccount(accountStr);
		actbank.setBankMmm(drawBankmm);
		actbank.setBranch(branchStr);
		actbank.setMmmprovince(new AreaMmm(province));
		actbank.setMmmcity(new AreaMmm(city));
		//actbank.setMmmarea(new AreaMmm(area));修改为省市二级
		actbank.setAddtime(new Date());
		actbank.setAddip(getRequestIp());
		actbank.setUser(new User(user.getUserId()));
		accountService.addAccountBank(actbank);
		message("添加银行卡成功!", "/member/account/bankcancel.html");
		super.systemLogAdd(user, 18, "用户绑定银行卡成功");
		return MSG;
	}

	
//	private String checkYjfBank(User user) {
//		int drawBankId = paramInt("drawBank");
//		String branchStr = paramString("branch");
//		String accountStr = paramString("account");
//		String accountSec = paramString("accountSec");
//		String phonecode = paramString("code");
//		// v1.8.0.4 TGPROJECT-226 lx 2014-04-15 start
//		try {
//			if (StringUtils.isBlank(accountStr) || StringUtils.isBlank(accountSec)) {
//				throw new BussinessException("银行卡账号输入不能为空！", "/member/account/bank.html");
//			}
//			if (!accountStr.equals(accountSec)) {
//				throw new BussinessException("两次输入银行卡账号不一样！", "/member/account/bank.html");
//			}
//			long province = paramLong("province");
//			long city = paramLong("city");
//			if (province == 0 || city == 0) {
//				throw new BussinessException("请选择完整的银行卡归属地！", "/member/account/bank.html");
//			}
//			DrawBank drawBank = accountService.getDrawBankById(drawBankId);
//			if (drawBank == null) {
//				throw new BussinessException("对不起,获取银行信息出错,请联系管理员！", "/member/account/bank.html");
//			}
//
//			Rule rule = ruleService.getRuleByNid("bank_check_phone");
//			if (rule != null) {
//				if (rule.getStatus() == 1) { // 校验添加银行手机校验是否启用
//					int rulerStatus = rule.getValueIntByKey("status");// 获取初审是否需要进行分布操作，1为需要，0为不需要
//					if (rulerStatus == 1) {// 1需要手机校验
//						if (StringUtils.isNull(phonecode) != null && !StringUtils.isNull(phonecode).equals("")) {// 有输入手机验证码
//							// 从session中获取手机号码，得到验证码
//							String codeUniqueId = String.valueOf(session.get(getSessionUser().getPhone()));
//							if (StringUtils.isBlank(codeUniqueId)) {
//								throw new BussinessException("处理失败，请重新操作", "/member/account/bank.html");
//							}
//							if (!phonecode.equals(codeUniqueId)) {
//								throw new BussinessException("添加银行帐号失败，输入验证码有误！请重新获取验证码进行验证");
//							}
//
//						} else {// 没输入，提示需要输入
//							throw new BussinessException("对不起,请输入手机验证码！", "/member/account/bank.html");
//						}
//						// v1.8.0.1_u1 TGPROJECT-226 start
//						session.remove(getSessionUser().getPhone());
//						// v1.8.0.1_u1 TGPROJECT-226 end
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//			printJson(JSON.toJSONString(e.getMessage()));
//		}
//		// v1.8.0.4 TGPROJECT-226 lx 2014-04-15 end
//		return SUCCESS;
//	}

	@Action(value = "bankcancel", results = { @Result(name = "success", type = "ftl", location = "/member/account/bankcancel.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String bankcancel() throws Exception {		
		User sessionUser = getSessionUser();
		long user_id = sessionUser.getUserId();
		User user = userService.getUserById(user_id);
		if (user == null) {
			throw new BussinessException("非法操作，请登录...", "/user/login.html");
		}
		String typeStr = StringUtils.isNull(paramString("type"));
		if (StringUtils.isBlank(typeStr)) {
			// 查询提现银行卡
			SearchParam drawBankParam = new SearchParam();
			if (!"ALL".equals(Constant.DRAW_CARDTYPE)) {
				drawBankParam.addParam("cardType", Constant.DRAW_CARDTYPE);
			}
			if (!"ALL".equals(Constant.DRAW_CHANNEL_NO)) {
				drawBankParam.addParam("channelNo", Constant.DRAW_CHANNEL_NO);
			}
			SearchParam param = new SearchParam();
			param.addParam("user", new User(user.getUserId())).addParam("status", 1);
			List bankList = accountService.getAccountBankList(param).getList();
			request.setAttribute("bankList", bankList);
			return SUCCESS;
		} else {// 解绑银行卡
			String phonecode = paramString("validCodeee");
			String bankid = paramString("bankid");
			if (isOpenApi()) {
				if (StringUtils.isBlank(bankid)) {
					throw new BussinessException("无效的参数，请重新操作");
				}
			}

			Rule rule = ruleService.getRuleByNid("bank_check_phone");
			if (rule != null) {
				if (rule.getStatus() == 1) { // 校验添加银行手机校验是否启用
					int rulerStatus = rule.getValueIntByKey("status");// 获取初审是否需要进行分布操作，1为需要，0为不需要
					if (rulerStatus == 1) {// 1需要手机校验						
						String codeUniqueId = String.valueOf(session.get(getSessionUser().getPhone()));
						if (StringUtils.isBlank(codeUniqueId)) {
							throw new BussinessException("处理失败，请重新操作", "/member/account/bank.html");
						}
						if (!phonecode.equals(codeUniqueId)) {
							throw new BussinessException("解绑银行卡失败，输入验证码有误！请重新获取验证码进行验证");
						}
						// v1.8.0.1_u1 TGPROJECT-226 start
						session.remove(getSessionUser().getPhone());
						// v1.8.0.1_u1 TGPROJECT-226 end
					}
				}
			}
			accountService.accountBankRemove(user, bankid);
			message("解绑银行卡成功!!", "/member/account/bankcancel.html");
			// v1.8.0.4 TGPROJECT-61 lx 2014-04-15 start
			User tempUser = userService.getUserById(user.getUserId());
			TempIdentifyUser tempIdentifyUser = userService.inintTempIdentifyUser(tempUser);
			session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);
			// v1.8.0.4 TGPROJECT-61 lx 2014-04-15 end
			super.systemLogAdd(user, 19, "用户解绑银行卡成功");
			return MSG;
		}
	}
	
	/**
	 * 
	 * 解绑银行卡获取验证码
	 */
	//v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
	@Action(value = "getPhoneCode")
	public String getPhoneCode() throws Exception {
		
		String ip = super.getRequestIp();
		logger.info("登录ip===" + ip);
		boolean isSend = false;
		String message = "";
		String codeUniqueId = "";
		int phoneType = Global.getInt("phone_type");//短信类型接口 value=1
		User sessionUser = getSessionUser();
	    //User user= (User)session.get("Contant.SESSION_USER");
	    logger.info("用户名" + sessionUser);
		String phone=sessionUser.getPhone();
		
		
		// add by gy 2016-8-31 11:42:48 增加短信验证码类型判断。
		String valCode = StringUtils.isNull(request.getParameter("valicode")); 
		logger.info("图片验证码："+valCode);
		int timeInterval = 1;

		// add by gy 2016年11月24日18:50:57 begin
        // 判断是否通过wap进行图片验证码校验
		if (!StringUtils.isBlank(paramString("isPhoneRequest")) && paramInt("isPhoneRequest") == 1) {
		    if (!ajaxCheckValidImg(valCode))
                message="请输入正确的图片验证码！";
        } else {
            if (!checkValidImg(valCode))
                message="请输入正确的图片验证码！";
        }
        // end
		
		String pt = StringUtils.isNull(request.getParameter("phoneType"));
		if (!StringUtils.isBlank(pt)) {
			phoneType = Global.getInt(pt);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isBlank(message)) {
			map.put("data", message);
			printJson(JSON.toJSONString(map));
			return null;
		}
		codeUniqueId = madeMobileCode(phoneType, phone, user);	
		map.put("data", message);
		map.put("mobile_token", saveToken("mobile_token"));
		map.put("isSend", isSend+"");
		map.put("timeInterval", String.valueOf(timeInterval));
		logger.info("------"+timeInterval);
		logger.info("短信验证码------"+codeUniqueId);
		session.put(phone, codeUniqueId);  //将验证码放入session中，在手机校验完了以后，删除此session
		printJson(JSON.toJSONString(map));
		return null;
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

	@Action("showareabank")
	public String showAreaBank() throws Exception {
		long pid = paramLong("pid");
		List<AreaBank> areas = userinfoService.getAreaBankListByPid(pid);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

	@Action("showmmmbank")
	public String showMmmBank() throws Exception {
		long pid = paramLong("pid");
		List<AreaMmm> areas = userinfoService.getMmmBankListByPid(pid);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

	@Action(value = "newcash", results = { @Result(name = "success", type = "ftl", location = "/member/account/newcash.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String newcash() throws Exception {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if (user == null) {
			throw new BussinessException("请您先登录");
		}

		//cancel by lxm 2017-2-10 17:48:44
//		if ("2".equals(Global.getValue("api_code"))) {
//			// 易极付取现要求强实名,fasle 是不通过
//			if (!checkYjfUserInfo(user.getApiId())) {
//				StringBuffer returnStr = new StringBuffer();
//				returnStr.append("<p >尊敬的" + getSessionUser().getUsername());
//				returnStr.append("您好！&nbsp;&nbsp;&nbsp;取现需要您上传本人身份证正、反面，提升您的认证等级。点击下面的按钮去提升等级吧<br>");
//				message(returnStr.toString(), "/member/identify/yjfRealName.html", "去提升认证等级>>>");
//				return MSG;
//			}
//		}

		if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
			return MSG;
		}
		List<AccountBank> banklist = accountService.getBankLists(user.getUserId()); // 获取用户可用的银行卡
		if (banklist.size() < 1) {
			message("请先添加银行卡！", "/member/account/bank.html", "点击进入绑定银行卡");
			return MSG;
		}

		Account account = accountService.getAccountByUser(user);
		int lateCount = borrowService.getLateRepaymentByUser(user); // 获取用户是否有逾期未还的记录
		if (lateCount != 0) {
			request.setAttribute("lateCount", lateCount = 0);
		}
		request.setAttribute("account", account);
		request.setAttribute("banklist", banklist);
		request.setAttribute("user", user);
		return SUCCESS;
	}

	@Action(value = "doNewcash", results = { @Result(name = "success", type = "ftl", location = "/member/account/newcash.html"), @Result(name = "huifuCash", type = "ftl", location = "/member/huifu/Cash.html"), @Result(name = "ipsCash", type = "ftl", location = "/member/ips/ipscommit.html"), @Result(name = "mmmCash", type = "ftl", location = "/member/mmm/mmmCash.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String doNewcash() throws Exception {
		// 验证码拦截避免重复提交
		String backUrl = "/member/account/newcash.html";
		User user = getSessionUser();

		//cancel by lxm 2017-2-10 17:53:11
//		if ("2".equals(Global.getValue("api_code"))) {//接口配置参数(1：汇付 2：易极付 3：双乾 4：环迅)
//			// 易极付取现要求强实名,fasle 是不通过 ，取现要求实名等级必须不是0和1.
//			if (!checkYjfUserInfo(user.getApiId())) {
//				StringBuffer returnStr = new StringBuffer();
//				returnStr.append("<p >尊敬的" + getSessionUser().getUsername());
//				returnStr.append("您好！&nbsp;&nbsp;&nbsp;取现需要您上传本人身份证正、反面，提升您的认证等级。点击下面的按钮去提升等级吧<br>");
//				message(returnStr.toString(), "/member/identify/yjfRealName.html", "去提升认证等级>>>");
//				return MSG;
//			}
//		}

		// 校验取现并填充AccountCash
		AccountCash cash = checkCashMessage(user, backUrl);
		// 提现操作
		String result = madeCash(cash, user, backUrl);
		// 更新accountCash
		accountService.updateAccountCash(cash);
		if (!"".equals(result)) {
			return result;
		}
		return MSG;
	}

	private AccountCash checkCashMessage(User user, String backUrl) {

		// add by gy 2016年11月24日16:26:56 begin
		// 这里判断验证码是否由Ajax请求传过来的。ajax请求的校验验证码，调用的方法不同
		boolean b = true;
		if (!StringUtils.isBlank(paramString("isPhoneRequest")) && paramInt("isPhoneRequest") == 1) {
			if (!ajaxCheckValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		} else {
			if (!checkValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		}
		if (!b) {
			throw new BussinessException("验证码不正确！", backUrl);
		}
		// end

		// v1.8.0.4 TGPROJECT-46
		if (user.getUserCache().getCashForbid() != 0) {
			throw new BussinessException("该用户禁止提现，请联系客服人员！", backUrl);
		}
		// v1.8.0.4 TGPROJECT-46con_lowest_cash
		double money = paramDouble("money");
		// v1.8.0.4_u4 TGPROJECT-369 qinjun 2014-07-16 start
		double minCash = NumberUtils.getDouble(Global.getValue("lowest_cash"));//平台要求的最低提现金额
		if (money < minCash) {
			throw new BussinessException("提现金额必须大于" + minCash, backUrl);
		}
		// v1.8.0.4_u4 TGPROJECT-369 qinjun 2014-07-16 end
		double maxCash = NumberUtils.getDouble(Global.getValue("most_cash"));//最大提现金额
		if (money > maxCash) {
			throw new BussinessException("提现金额不能大于" + maxCash + ", 请您联系客服");
		}
		Account at = accountService.getAccountByUser(user);
		if (at == null || at.getUseMoney() < money) { // 校验账户余额是否满足取现金额
			throw new BussinessException("可用金额不足", backUrl);
		}
		// 获取用户净值标的总待还金额
		double waitPropertyAllMoney = accountService.getAllPropertyWaitRepayMoney(user.getUserId());
		if (at == null || money > at.getUseMoney() + at.getCollection() - waitPropertyAllMoney) {
			// 校验用户可提现金额：可用金额 + 待收金额 - 净值标待还金额
			throw new BussinessException("输入的提现金额大于可提现总额，请核对后再提现！", backUrl);
		}
		String paypassword = paramString("paypassword");//用户输入的支付密码
		AccountCash cash = new AccountCash();
		// 校验，并封装提现信息。
		checkNewCash(at, backUrl, cash);
		// 申请记录保存到数据库
		User u = userService.getUserById(user.getUserId());
		if (u == null) {
			cash.setUser(new User(user.getUserId()));
		} else {
			cash.setUser(u);// 防止添加空 user
		}
		cash.setAddtime(new Date());
		cash.setAddip(getRequestIp());
		cash.setTotal(money); // 提现金额
		cash.setFee(0);
		int id = paramInt("accountBank");
		if (id != 0) {
			AccountBank accountBank = accountService.getAccountBankById(id);
			cash.setAccountBank(accountBank);
		}
		cash.setStatus(4);// 初始状态设为4
		accountService.seveCash(cash);
		return cash;
	}

	@Action(value = "cancelCash", results = { @Result(name = "success", type = "ftl", location = "/member/account/cash.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String cancelCash() {
		User user = getSessionUser();//获取当前用户
		int id = paramInt("id");
		if (id != 0) {
			AccountCash ac = accountService.getAccountCash(id);
			if (ac == null) {
				throw new BussinessException("不存在该记录");
			}

			AccountLog log = new AccountLog(user.getUserId(), Constant.CASH_CANCEL, Constant.ADMIN_ID, getTimeStr(), getRequestIp());//Constant.ADMIN_ID默认为1，发版要改过来
			log.setRemark("提现取消");
			Account account = accountService.getAccountByUser(user);
			accountService.cancelCash(account, ac, log);
		} else {
			throw new BussinessException("不存在该记录");
		}

		SearchParam param = SearchParam.getInstance().addParam("user", user).addOrder(OrderType.DESC, "addtime").addPage(paramInt("page"));
		PageDataList data = accountService.getCashList(param);
		setPageAttribute(data, param);
		return SUCCESS;
	}

	@Action("active")
	public String active() {
		message("您的" + Global.getValue("api_name") + "账户已激活成功，请您查看。");
		return MSG;
	}

	/**
	 * 对用户充值进行判断，封装不同的充值对象
	 * 
	 * @param
	 * @param user
	 * @return
	 */
	
	private String madeRecharge(AccountRecharge r, User user) {
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		//modify by lxm 注掉冗余代码  2017-2-10 11:54:29
//		case 1: // 汇付
//			NetSave netSave = ChinapnrHelper.chinaSave(user, NumberUtils.format2Str(r.getMoney()));
//			r.setTradeNo(netSave.getOrdId());
//			request.setAttribute("pnr", netSave);
//			return "huifuchinapnr";
//		case 2: // 易极付
//			Recharge re = PayModelHelper.recharge(user, r.getMoney() + "");
//			r.setTradeNo(re.getOrderNo()); // 订单号
//			request.setAttribute("pnr", re);
//			return "yjf";
		case 3: // 双乾
			MmmRecharge mmmRe = MmmHelper.mmmRecharge(user, String.valueOf(new BigDecimal(r.getMoney()).setScale(2,BigDecimal.ROUND_HALF_UP)), r.getType());
			r.setTradeNo(mmmRe.getOrderNo()); // 订单号
            //前台充值打开双乾页面需要的参数：rechargeMoneymoremore：要充值的账号的钱多多标识~platformMoneymoremore：开通乾多多帐号为平台帐号时生成，以p开头~orderNo：平台流水号~amount:金额~rechargeType:充值方式~feeType:手续费类型~returnURL:页面回调地址~notifyURL:后台回调地址~signInfo:签名参数
			logger.info("用户充值：" + user.getPhone() + "前台充值打开双乾页面需要的参数：rechargeMoneymoremore：" + mmmRe.getRechargeMoneymoremore() + ", platformMoneymoremore: " + mmmRe.getPlatformMoneymoremore() + ", orderNo: " + mmmRe.getOrderNo() + ", amount: " + mmmRe.getAmount() + ", rechargeType: +" + mmmRe.getRechargeType() + ", feeType: " + mmmRe.getFeeType() + ", returnURL: " + mmmRe.getReturnURL() + ", notifyURL: " + mmmRe.getNotifyURL() + ", signInfo: " + mmmRe.getSignInfo());
			super.systemLogAdd(user, 20, "前台充值打开双乾页面需要的参数：rechargeMoneymoremore：" + mmmRe.getRechargeMoneymoremore() + ", platformMoneymoremore: " + mmmRe.getPlatformMoneymoremore() + ", orderNo: " + mmmRe.getOrderNo() + ", amount: " + mmmRe.getAmount() + ", rechargeType: +" + mmmRe.getRechargeType() + ", feeType: " + mmmRe.getFeeType() + ", returnURL: " + mmmRe.getReturnURL() + ", notifyURL: " + mmmRe.getNotifyURL() + ", signInfo: " + mmmRe.getSignInfo());
			request.setAttribute("mmm", mmmRe);
			return "mmm";
//		case 4: // 环迅
//			IpsRecharge ipsRe = IpsHelper.ipsRecharge(r);
//			r.setTradeNo(ipsRe.getMerBillNo());
//			request.setAttribute("ips", ipsRe);
//			return "ips";
		default:
			throw new BussinessException("系统未找到此支付方，请联系客服人员！", "/member/account/newrecharge.html");
		}

	}

	
	public String madeCash(AccountCash accountCash, User user, String backUrl) {
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		//cancel by lxm 注掉冗余代码 2017-2-10 12:04:46
//		case 1:// 汇付
//				// v1.8.0.4_u4 TGPROJECT-357 qinjun 2014-07-07 start
//			double servFee = 0d;
//			if (Global.getInt("is_pay_ser_fee") == 1) {// 网站收取服务费
//				servFee = accountService.getHuifuServFee(accountCash);
//			}
//			CashOut cashOut = ChinapnrHelper.chinacash(user, NumberUtils.format2Str(accountCash.getTotal()), servFee); // 汇付取现操作，跳转页面
//			// v1.8.0.4_u4 TGPROJECT-357 qinjun 2014-07-07 end
//			accountCash.setOrderNo(cashOut.getOrdId());
//			request.setAttribute("pnr", cashOut);
//			return "huifuCash";
//		case 2:// 易极付id
//			BorrowParam param = new BorrowParam();
//			param.setIp(getRequestIp());
//			boolean result = accountService.yjfNewCash(accountCash, param);
//			String cashIsVerify = Global.getValue("cash_is_verify"); // 获取取现是否需要校验
//			double money = accountCash.getTotal();
//			// v1.8.0.4_u1 TGPROJECT-378 wsl 2014-07-31 start
//			Rule qcRule = ruleService.getRuleByNid("quick_cash_rule");
//			if (qcRule != null && qcRule.getStatus() == 1 && qcRule.getValueIntByKey("daily") == 1) {// 有此规则，并且启用
//				checkDailyCash(user, money, result, qcRule);
//			} else {
//				if (cashIsVerify.equals("1")) {// 不需要审核
//					if (result) {
//						this.message("提现成功，请到账户，查看自己信息", "/member/account/newcash.html");
//						super.systemLogAdd(user, 21, "用户提现成功,金额:" + accountCash.getTotal());
//					} else {
//						this.message("提现失败，请到账户，查看自己信息", "/member/account/newcash.html");
//						super.systemLogAdd(user, 21, "用户提现失败,金额:" + accountCash.getTotal());
//					}
//				} else {
//					this.message("成功提交提现申请，请等待审核！", "/member/account/newcash.html");
//					super.systemLogAdd(user, 21, "用户申请提现,金额:" + accountCash.getTotal());
//				}
//			}
//			// v1.8.0.4_u1 TGPROJECT-378 wsl 2014-07-31 end
//			return "";
		case 3://双乾
			String webVipFeePercent = Global.getValue("webVip_feePercent");// 平台垫付vip用户取现手续费比例
			String webFeePercent = Global.getValue("web_feePercent");// 普通用户平台垫付取现手续费比例
			int vipStatus = user.getUserCache().getVipStatus();
			String siteFee = "0";// 网站垫付比例
			// 由于公司需要，现默认改为手续费由平台支付，后期需要可放开， vipStatus==1时，为平台支付
//			if(vipStatus==1){
				siteFee = Double.parseDouble(webVipFeePercent) * 100 + "";
//			}else { 
//				siteFee = Double.parseDouble(webFeePercent)*100+""; 
//			}
			// 若启用免费提现额度and免费提现额度足够，则平台承担费用
			Rule rule = ruleService.getRuleByNid("free_cash_rule");//免费提现额度
			if (rule != null && rule.getStatus() == 1) {
				double freeCashMoney = user.getAccount().getFreeCashMoney();//免费提现金额
				double total = accountCash.getTotal();//提现总额
				if (freeCashMoney >= total) {
					siteFee = "100";
				}
			}
			MmmCash mmCash = MmmHelper.mmmCash(accountCash, siteFee);
			// v1.8.0.4_u4 TGPROJECT-349 qinjun 2014-07-02 end
			accountCash.setOrderNo(mmCash.getOrderNo());
			request.setAttribute("mmm", mmCash);
			return "mmmCash";
//		case 4: // 环迅支付
//			String pMerFee = "0"; // 手续费
//			IpsNewCash ipsCash = IpsHelper.ipsDoNewCash(accountCash, pMerFee);
//			accountCash.setOrderNo(ipsCash.getMerBillNo());
//			request.setAttribute("ips", ipsCash);
//			return "ipsCash";
		default:
			return "";
		}
	}

	/**
	 * @param at
	 */
	private void checkNewCash(Account at, String backUrl, AccountCash cash) {
		String accountId = paramString("account_id"); // 卡号
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		//cancel by lxm 2017-2-13 10:00:44
//		case 1:
//
//			break;
//		case 2:
//			if (StringUtils.isBlank(accountId)) {
//				throw new BussinessException("请选择一个账号", backUrl);
//			}
//			// v1.8.0.4 TGPROJECT-44 好利贷提现收取千三手续费 start
//			int drawType;
//			if (Global.getString("webid").equals("hld@")) {
//				drawType = 1;
//			} else {
//				drawType = paramInt("drawType");
//			}
//			// v1.8.0.4 TGPROJECT-44 好利贷提现收取千三手续费 end
//			// v1.8.0.4_u4 TGPROJECT-360 qinjun 2014-07-11 start
//			if (drawType == 1) { // 根据取现类型，判断可用余额是否大于取现手续费
//				if (at == null || at.getUseMoney() <= 5) {
//					throw new BussinessException("取现实时到账方式，账户余额必须大于5元", backUrl);
//				}
//				cash.setDrawType(0);
//			} else if (drawType == 2) {
//				if (at == null || at.getUseMoney() <= 2) {
//					throw new BussinessException("24小时到账方式，账户余额必须大于2元", backUrl);
//				}
//				cash.setDrawType(1);
//			} else {
//				if (at == null || at.getUseMoney() <= 5) {
//					throw new BussinessException("取现实时到账方式，账户余额必须大于5元", backUrl);
//				}
//				cash.setDrawType(0);
//			}
//			// v1.8.0.4_u4 TGPROJECT-360 qinjun 2014-07-11 start
//			AccountBank ab = accountService.getAccountBankById(Integer.parseInt(accountId));
//			cash.setAccountBank(ab);
//			break;
		default:
			break;
		}
	}

	//cancel by lxm 2017-2-13 10:17:02
//	// 获取信用积分
//	@Action(value = "starScore", results = { @Result(name = "success", type = "ftl", location = "/member/account/starScore.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
//	public String starScore() throws Exception {
//		User user = getSessionUser();
//
//		String startTime = paramString("dotime1");
//		String endTime = paramString("dotime2");
//		SearchParam param = SearchParam.getInstance().addOrder(OrderType.DESC, "addtime").addPage(paramInt("page")).addParam("user.userId", user.getUserId());
//
//		if (!StringUtils.isBlank(startTime)) {
//			startTime += " 00:00:00";
//			param.addParam("addtime", Operator.GTE, DateUtils.getDate2(startTime));
//		}
//		if (!StringUtils.isBlank(endTime)) {
//			endTime += " 23:59:59";
//			param.addParam("addtime", Operator.LTE, DateUtils.getDate2(endTime));
//		}
//		PageDataList data = ruleService.getStarLogList(param);
//		Map<String, Object> extraparam = new HashMap<String, Object>();
//		extraparam.put("dotime1", startTime);
//		extraparam.put("dotime2", endTime);
//		setPageAttribute(data, extraparam);
//		return SUCCESS;
//	}

	// TGPROJECT-410 wsl 2014-09-03 start
	/**
	 * 校验提现用户当日提现总额限制
	 * 
	 * @param user
	 * @param money
	 * @param result
	 * @param qcRule
	 */
	private void checkDailyCash(User user, double money, Boolean result, Rule qcRule) {

		double quickCash = qcRule.getValueDoubleByKey("quick_cash");
		double dailyMostCash = Global.getDouble("daily_most_cash");
		double lowestCash = Global.getDouble("lowest_cash");
		double dailySum = accountService.getAccountCashDailySum(user.getUserId());
		double dailySurplusCash = dailyMostCash - dailySum;

		if (money < quickCash && dailySurplusCash >= lowestCash && money < dailySurplusCash) {// 信合行直接提现不需要后台审核
			if (result) {
				this.message("提现成功，请到账户，查看自己信息", "/member/account/newcash.html");
				super.systemLogAdd(user, 21, "用户提现成功,金额:" + money);
			} else {
				this.message("提现失败，请到账户，查看自己信息", "/member/account/newcash.html");
				super.systemLogAdd(user, 21, "用户提现失败,金额:" + money);
			}
		} else {
			this.message("成功提交提现申请，请等待审核！", "/member/account/newcash.html");
			super.systemLogAdd(user, 21, "用户申请提现,金额:" + money);
		}
	}
	// TGPROJECT-410 wsl 2014-09-03 end
}
