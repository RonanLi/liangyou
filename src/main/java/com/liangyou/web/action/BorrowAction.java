package com.liangyou.web.action;

import com.alibaba.fastjson.JSON;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmLoanTransfer;
import com.liangyou.context.Constant;
import com.liangyou.context.ExcelType;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.*;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.model.borrow.protocol.BorrowProtocol;
import com.liangyou.model.themis.ContractFileCreate;
import com.liangyou.model.themis.ThemisClientInit;
import com.liangyou.service.*;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.*;
import org.mapu.themis.api.common.PersonalIdentifer;
import org.mapu.themis.api.request.contract.ContractFileDownloadUrlRequest;
import org.mapu.themis.api.request.contract.ContractFilePreservationCreateRequest;
import org.mapu.themis.api.response.contract.ContractFileDownloadUrlResponse;
import org.mapu.themis.api.response.preservation.PreservationCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Namespace("/borrow")
@ParentPackage("p2p-default")
public class BorrowAction extends BaseAction implements ModelDriven<Borrow> {
	private static Logger logger = Logger.getLogger(BorrowAction.class);
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserAmountService userAmountService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private WarrantService warrantService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private AttestationService attestationService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private BusinessTypeService businessTypeService;
	@Autowired
	private ExperienceMoneyService experienceMoneyService;
	@Autowired
	private BorrowTenderService borrowTenderService;
	
	private Borrow borrow = new Borrow();
	@Override
	public Borrow getModel() {
		return borrow;
	}

	private String sep = File.separator;
	private File[] fileImage;// 一次上传多个

	public File[] getFileImage() {
		return fileImage;
	}

	public void setFileImage(File[] fileImage) {
		this.fileImage = fileImage;
	}

	/**
	 * 我要投资的首页
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "index", results = { @Result(name = "index", type = "ftl", location = "/borrow/index.html") })
	public String index() throws Exception {
		return "index";
	}

	/**
	 * 我要借款页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "indexDetail", results = { @Result(name = "success", type = "ftl", location = "/borrow/indexDetail.html") })
	public String indexDetail() throws Exception {
		int borrowType = Constant.TYPE_ALL;
		String typeName = paramString("t"); // 标的类型
		borrowType = Global.getBorrowType(typeName);
		request.setAttribute("borrowType", borrowType);
		request.setAttribute("t", typeName);
		return SUCCESS;
	}

	// v1.8.0.4_u2 TGPROJECT-297 lx start
	/**
	 * 跳转修改标的页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "updateBorrowPage", results = { @Result(name = "update", type = "ftl", location = "/borrow/updateborrow.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String updateBorrowPage() throws Exception {
		// 获取系统担保机构
		List<Warrant> warrantList = warrantService.WarrantList();
		request.setAttribute("warrantList", warrantList); // 获取担保机构
		SearchParam sp = new SearchParam().addParam("status", 1);
		List<BusinessType> businessTypeList = businessTypeService.findListBusinessType(sp);
		request.setAttribute("businessTypeList", businessTypeList);// 获取业务部门
		Borrow b = borrowService.getBorrow(paramLong("id"));
		if (b == null) {
			message("操作有误，请刷新后重新操作", "/member/borrow/unpublish.html");
			return MSG;
		}
		request.setAttribute("borrow", b);
		return "update";
	}

	/**
	 * 修改
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "updateBorrow", interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String updateBorrow() throws Exception {
		BorrowModel model = null;
		User sessionUser = getSessionUser();
		if (sessionUser == null) {
			throw new BussinessException("请登陆后操作");
		}
		Borrow userBorrow = borrowService.getBorrow(borrow.getId());
		if (userBorrow == null) {
			throw new BussinessException("对应的标不存在");
		}
		if (sessionUser.getUserId() != userBorrow.getUser().getUserId()) {
			throw new BussinessException("非法操作");
		}
		if (userBorrow.getAccount() != borrow.getAccount()) {
			throw new BussinessException("发标金额不能修改");
		}
		model = BorrowHelper.getHelper(borrow);
		model.checkModelData(); // 校验发标合法性
		validate(false);
		checkAddBorrow(borrow); // 发标校验发标金额,发标，标的特殊属性也在这里添加
		fillBorrow(model);
		model.setViewMoney(model.getAccount()); // 发标时将viewmoney默认为发标金额
		// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-19
		BorrowProperty property = madNewBorrowProperty(userBorrow.getBorrowProperty());
		borrowService.updateBorrowAndProperty(model.getModel(), property);
		// v1.8.0.3_u3 XINHEHANG-66 wujing 2014-06-19 end
		logger.info("修改标信息成功");
		message("修改标信息成功", "/member/borrow/unpublish.html");
		return MSG;
	}

	// v1.8.0.4_u2 TGPROJECT-297 lx end

	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 start
	/**
	 * @return
	 * @throws Exception
	 */
	@Action(value = "borrowintent", results = { @Result(name = "borrowintent", type = "ftl", location = "/borrow/borrowintent.html") })
	public String borrowIntent() throws Exception {
		String type = paramString("type");
		User user = getSessionUser();
		if (!StringUtils.isBlank(type)) {
			if (!checkValidImg(StringUtils.isNull(request.getParameter("valicode")))) {
				throw new BussinessException("验证码不正确！");
			}
			BorrowIntent borrowIntent = new BorrowIntent();
			borrowIntent.setUser(user);
			borrowIntent.setProvince(new Area(paramInt("province")));
			borrowIntent.setCity(new Area(paramInt("city")));
			borrowIntent.setContactUsername(paramString("contactUsername"));
			borrowIntent.setPhone(paramString("phone"));
			borrowIntent.setIntentAccount(paramDouble("intentAccount"));
			borrowIntent.setCompanyName(paramString("companyName"));
			borrowIntent.setRemark(paramString("remark"));
			borrowIntent.setIntentTimeLimit(paramInt("intentTimeLimit"));
			// v1.8.0.3_u3 XINHEHANG-66 qinjun 2014-06-20 start
			borrowIntent.setAddtime(new Date());
			// v1.8.0.3_u3 XINHEHANG-66 qinjun 2014-06-20 end
			borrowService.addBorrowIntent(borrowIntent);
			message("新增借款意向成功！");
			return MSG;
		} else {
			return "borrowintent";
		}
	}
	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 end

	/**
	 * @return
	 * @throws Exception
	 */
	@Action(value = "loan", results = { @Result(name = "detail", type = "ftl", location = "/borrow/borrow.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String loan() throws Exception {
		int borrowType = Constant.TYPE_ALL;
		String typeName = paramString("t"); // 标的类型
		String property = paramString("property");
		int isAssignment = paramInt("isAssignment"); // 区分正常发标和债权转让标
		borrowType = Global.getBorrowType(typeName);
		boolean isOk = true;
		String checkMsg = "";
		// 第三方拦截
		apiService.checkApiLoan(getSessionUser());
		try {
			// 检查是否有标未满标,有则不能发标
			checkHasUnFinsh();
			if (isAssignment == 1) {// 如果是转让标，根据转让标发标规则检测是否可以发标
				int tenderId = paramInt("tenderId");// 债权转让标 投标的ids

				checkAssignment(tenderId);
				// 以下两个参数，用于债权转让
				request.setAttribute("isAssignment", isAssignment);
			}
			// 校验配置
			String result = checkBorrowConfig(borrowType);
			if (result != null) {
				return result;
			}
			// 获取系统担保机构
			List<Warrant> warrantList = warrantService.WarrantList();
			request.setAttribute("warrantList", warrantList); // 获取担保机构
			SearchParam sp = new SearchParam().addParam("status", 1);
			List<BusinessType> businessTypeList = businessTypeService.findListBusinessType(sp);
			request.setAttribute("businessTypeList", businessTypeList);// 获取业务部门
		} catch (Exception e) {
			isOk = false;
			e.printStackTrace();
			checkMsg = e.getMessage();
			logger.error(e.getMessage(), e.getCause());
		}
		if (!isOk) {
			message(checkMsg, "/borrow/index.html?type=" + typeName);
			return MSG;
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			request.setAttribute("borrow", session.getAttribute("borrow"));
			session.removeAttribute("borrow");
		}
		request.setAttribute("borrowType", borrowType);
		request.setAttribute("borrowProperty", property);
		findUseBorrowAccount();

		// 获取管理费限额规则
		Rule rule = ruleService.getRuleByNid("tender_reward_limit_rule");
		double partAccount_limit = 0;
		double funds_limit_low = 0;
		double funds_limit_apr = 0;
		if (rule != null && rule.getStatus() == 1) {// 有此规则，并且启用
			partAccount_limit = rule.getValueDoubleByKey("partAccount_limit");
			funds_limit_low = rule.getValueDoubleByKey("funds_limit_low");
			funds_limit_apr = rule.getValueDoubleByKey("funds_limit_apr");
		}
		request.setAttribute("partAccount_limit", partAccount_limit);
		request.setAttribute("funds_limit_low", funds_limit_low);
		request.setAttribute("funds_limit_apr", funds_limit_apr);

		return "detail";
	}

	/**
	 * 通过规则，判断是否获取用户可用借款余额
	 */
	private void findUseBorrowAccount() {
		Rule rule = ruleService.getRuleByNid("check_addBorrow");
		if (rule != null) {
			int result = rule.getValueIntByKey("check_account");
			if (result == 1) {
				double sumAccount = rule.getValueDoubleByKey("add_account");
				double NoUseaccount = borrowService.sumBorrowAccountByUserId(getSessionUser().getUserId());
				double useAccount = sumAccount - NoUseaccount;
				if (useAccount <= 0) {
					throw new BussinessException("你可用借款金额已经用完，不能再发布借款标");
				}
				request.setAttribute("useAccount", useAccount);
			}
		}
	}

	/**
	 * 增加新的标
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "add", results = { @Result(name = "detail", type = "ftl", location = "/borrow/borrow.html"), @Result(name = "hxaddBorrow", type = "ftl", location = "/member/ips/ipscommit.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String add() throws Exception {
		boolean isOk = true;
		String checkMsg = "";
		BorrowModel model = null;
		User sessionUser = getSessionUser();
		HttpSession session = request.getSession(false);

		BorrowProperty property = new BorrowProperty();
		try {
			List<Warrant> warrantList = warrantService.WarrantList();
			request.setAttribute("warrantList", warrantList); // 获取担保机构
			model = BorrowHelper.getHelper(borrow);
			property = madNewBorrowProperty(null);
			model.checkModelData(); // 校验发标合法性
			if (borrow.getIsAssignment() != 1) { // 非债权转让标
				validate(true);
				checkAddBorrow(borrow); // 发标校验发标金额,发标，标的特殊属性也在这里添加
				checkBorrowMoney(borrow.getAccount());// 校验发标金额
				checkBorrowConfig(borrow);
			}
			if (borrow.getIsAssignment() == 1) {// 如果是转让标
				int tenderId = paramInt("tenderId");// 投标id
				BorrowTender bt = borrowService.getTenderById(tenderId);// 原投标记录
				validateAssignment(tenderId, borrow, bt);
				if (bt == null) {
					throw new BussinessException("该债券转让标的原投资记录查询不到，不能转让！");
				}
				if (bt.getStatus() != 0) {
					throw new BussinessException("该债券转让标的原投资已被处理，请查看投资记录与资金记录！");
				}

				String dealMsg = borrowService.checkAssignMentBorrow(bt);
				if (!StringUtils.isBlank(dealMsg)) {
					throw new BussinessException(dealMsg);
				}
				fillAssignBorrow(model, bt);
				// 生成资金记录对象
				AccountLog log = new AccountLog(getSessionUser().getUserId(), Constant.FREEZE, getSessionUser().getUserId(), "", this.getRequestIp());

				BorrowParam param = new BorrowParam();
				String resultFlag = sessionUser.getUserId() + "-" + System.currentTimeMillis();
				param.setResultFlag(resultFlag);
				DisruptorUtils.addAssignmentBorrow(model, log, bt, param);// 债权转让标 异步处理

				request.setAttribute("tenderFlag", resultFlag);
				request.setAttribute("ok_url", "/member/borrow/unpublish.html"); // 成功返回地址
				request.setAttribute("back_url", "/borrow/index.html");// 失败返回地址
				request.setAttribute("r_msg", "恭喜您，发布债权转让成功，请您返回查看");
				return RESULT;

			} else {// 非转让标
				fillBorrow(model);
				model.setViewMoney(model.getAccount()); // 发标时将viewmoney默认为发标金额
				// 生成资金记录对象
				AccountLog log = new AccountLog(getSessionUser().getUserId(), Constant.FREEZE, getSessionUser().getUserId(), "", this.getRequestIp());

				List<BorrowDetail> details = getBorrowDetailList(fileImage, borrow);
				borrowService.addBorrow(model, log, property, details);

			}

		} catch (Exception e) {
			if (session != null) {// 保存用户信息到session,用户自动填写用户提交的信息。
				// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
				Rule rule = ruleService.getRuleByNid("borrow_detail");
				// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 end
				if (rule != null) {
					if (rule.getStatus() == 1) {
						int result = rule.getValueIntByKey("add_borrow_property");
						if (result == 1) {
							borrow.setBorrowProperty(property);
						}
					}
				}
				session.setAttribute("borrow", borrow);
			}
			isOk = false;
			checkMsg = e.getMessage();
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		if (isOk) { // 执行登陆逻辑前校验是否存在非法数据，如果有直接返回错误页面
			message("您的借款申请已成功提交，请耐心等待平台审核，如有疑问，请联系客服中心。", "/member/borrow/unpublish.html", "查看发布的借款");
			super.systemLogAdd(sessionUser, 15, "用户发标成功(" + borrow.getType() + "),标名:" + borrow.getName() + ",金额:" + borrow.getAccount());
		} else {
			logger.debug("发标失败！");
			// v1.8.0.4 HAOLIP-114 lx 2014-04-24 start
			if (property != null) {
				message(checkMsg, "/borrow/loan.html?t=" + Global.getBorrowTypeName(borrow.getType()) + "&property=" + property.getPropertyType(), "请返回重新填写");
			} else {
				message(checkMsg, "/borrow/loan.html?t=" + Global.getBorrowTypeName(borrow.getType()), "请返回重新填写");
			}

			// v1.8.0.4 HAOLIP-114 lx 2014-04-24 end
		}
		return MSG;
	}

	/**
	 * 校验发标金额是否符合网站要求：发标金额为多少是，是某一个数字的整数倍
	 */
	private void checkBorrowMoney(double money) {
		Rule rule1 = ruleService.getRuleByNid("check_addBorrow");
		if (rule1 != null) {
			int result = rule1.getValueIntByKey("check_account");
			if (result == 1) {
				double sumAccount = rule1.getValueDoubleByKey("add_account");
				double NoUseaccount = borrowService.sumBorrowAccountByUserId(getSessionUser().getUserId());
				double useAccount = sumAccount - NoUseaccount;
				if (useAccount < money) {
					throw new BussinessException("你借款金额不能大于可用借款额度");
				}
			}
		}

		Rule rule = ruleService.getRuleByNid("check_addBorrow");
		if (rule != null && rule.getStatus() == 1) {
			int result = rule.getValueIntByKey("check_money_status");
			int add_lowestaccount = rule.getValueIntByKey("add_lowestaccount");
			if (result == 1) {
				if (money < add_lowestaccount) {
					throw new BussinessException("借款金额不能小于" + add_lowestaccount / 10000 + "万元");
				}

				if (add_lowestaccount <= money && money <= 200000) {
					int leaseRult = rule.getValueIntByKey("lost_rule");// 发标时，校验发标金额是否是某一个金额的整数倍
					double leaseMessage = money % (leaseRult * 10000);
					if (leaseMessage > 0) {
						throw new BussinessException("当借款金额为" + add_lowestaccount / 10000 + "-20万时，借款金额必须为" + leaseRult + "万的整数倍！");
					}
				}
				if (200000 < money && money <= 4000000) {
					int maxRult = rule.getValueIntByKey("max_rule");// 发标时，校验发标金额是否是某一个金额的整数倍
					double maxMessage = money % (maxRult * 10000);
					if (maxMessage > 0) {
						throw new BussinessException("当借款金额为20万-400万时，借款金额必须为" + maxRult + "万的整数倍！");
					}
				}
			}
		}
	}

	// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 start
	/**
	 * @param file
	 * @param fileName
	 *            上传时输入的文件显示
	 * @return
	 * @throws IOException
	 */
	private List<BorrowDetail> getBorrowDetailList(File[] fileImages, Borrow borrow) throws IOException {
		// 文件获取
		String[] fileName = request.getParameterValues("fileViewName");
		String[] filePartName = request.getParameterValues("filePartName");
		List<BorrowDetail> borrowDetailList = new ArrayList<BorrowDetail>();
		BorrowDetail borrowDetail;
		if (fileImages != null) {
			for (int i = 0; i < fileImages.length; i++) {
				File fi = fileImages[i];
				if (fi == null) {
					continue;
				}
				if (ImageIO.read(fi) == null) {
					continue;
				}
				borrowDetail = new BorrowDetail();
				String path = upload(fi, borrow);
				borrowDetail.setContent(path);
				borrowDetail.setAddtime(new Date());
				borrowDetail.setBorrow(borrow);
				borrowDetail.setName(StringUtils.isBlank(fileName[i]) ? "" : fileName[i]);
				borrowDetail.setTypeName(new BorrowDetailType(3));
				borrowDetail.setPartName(new BorrowDetailType(NumberUtils.getLong(filePartName[i])));
				borrowDetail.setAddip(getRequestIp());
				borrowDetailList.add(borrowDetail);
			}
		}
		// 标的附加信息
		String[] detailPartName = request.getParameterValues("detailPartName");
		String[] detailContent = request.getParameterValues("detailContent");
		if (detailPartName != null) {
			for (int i = 0; i < detailPartName.length; i++) {
				if (StringUtils.isBlank(detailPartName[i]) || detailPartName[i].equals("0") || StringUtils.isBlank(detailContent[i])) {
					continue;
				}
				borrowDetail = new BorrowDetail();
				borrowDetail.setContent(detailContent[i]);
				borrowDetail.setAddtime(new Date());
				borrowDetail.setBorrow(borrow);
				borrowDetail.setTypeName(new BorrowDetailType(1));
				borrowDetail.setPartName(new BorrowDetailType(NumberUtils.getLong(detailPartName[i])));
				borrowDetail.setAddip(getRequestIp());
				borrowDetailList.add(borrowDetail);
			}
		}
		// 风控信息
		String danbaoName = paramString("danbaoName");
		String danbaoMemo = paramString("danbaoMemo");
		String danbaoContent = paramString("danbaoContent");
		if (!StringUtils.isBlank(danbaoContent)) {
			borrowDetail = new BorrowDetail();
			borrowDetail.setContent(danbaoContent);
			borrowDetail.setAddtime(new Date());
			borrowDetail.setBorrow(borrow);
			borrowDetail.setName(danbaoName);
			borrowDetail.setMemo(danbaoMemo);
			borrowDetail.setTypeName(new BorrowDetailType(2));
			borrowDetail.setAddip(getRequestIp());
			borrowDetailList.add(borrowDetail);
		}
		return borrowDetailList;
	}

	/**
	 * 用于单个方法里面上传
	 * 
	 * @param file
	 * @throws IOException
	 */
	public String upload(File file, Borrow borrow) {
		String url = "";
		String destfilename2 = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + borrow.getId() + "_" + DateUtils.getTimeStr(new Date()) + ".jpg";
		String contextPath = ServletActionContext.getServletContext().getRealPath("/");
		url = destfilename2;
		logger.info(destfilename2);
		File imageFile2 = null;
		try {
			url = this.truncatUrl(url, contextPath);
			imageFile2 = new File(destfilename2);
			FileUtils.copyFile(file, imageFile2);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return url;
	}

	/**
	 * 截取url
	 * 
	 * @param old
	 * @param truncat
	 * @return
	 */
	private String truncatUrl(String old, String truncat) {
		String url = "";
		url = old.replace(truncat, "");
		url = url.replace(sep, "/");
		return url;
	}
	// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 end

	/**
	 * 重写封装borrowProperty方法
	 * 
	 * @param borrow
	 * @return
	 */
	private BorrowProperty madNewBorrowProperty(BorrowProperty borrowProperty) {
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		Rule rule = ruleService.getRuleByNid("borrow_detail");
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 end
		if (rule != null) {
			if (rule.getStatus() == 1) {
				int result = rule.getValueIntByKey("add_borrow_property");
				if (result == 1) {
					String endtime = paramString("endtime");
					String bProperty = paramString("borrowproperty");
					long warrantId = paramLong("warrantId"); // 担保机构
					Warrant warrant = warrantService.findWarrant(warrantId);
					// v1.8.0.4_u2 TGPROJECT-297 lx start
					// 判断borrow是否已经存在了borrowproperty,borrow 已经存在，不重新生成借款编号和合同号
					if (borrowProperty == null) {
						borrowProperty = new BorrowProperty();
						// 生成借款编号
						int borrowCount = borrowService.countBorrowByDay();
						int number = (int) (Math.random() * 90) + 10; // 订单随机数
						String borrowNo = StringUtils.madeAgreementNo("JK", DateUtils.newdateStr3(new Date()), borrowCount, number); // 借款申请号
						borrowProperty.setBorrowNo(borrowNo);
						// 产生合同号：
						String borrowagreementNO = StringUtils.madeAgreementNo("HT", DateUtils.newdateStr2(new Date()), borrowCount, number);
						String verifyBorrowNo = StringUtils.madeAgreementNo("", DateUtils.newdateStr2(new Date()), borrowCount, number);
						borrowProperty.setVerifyBorrowNo(verifyBorrowNo);
						borrowProperty.setBorrowagreementNO(borrowagreementNO);
					}
					String projectType = paramString("projectType");
					borrowProperty.setProjectType(projectType);
					long businessTypeId = paramLong("businessTypeId");// 业务部门ID
					BusinessType bt = businessTypeService.findBusinessById(businessTypeId);
					borrowProperty.setBusinessType(bt);
					String usefundstime = paramString("usefundstime");

					if (!StringUtils.isBlank(usefundstime)) {
						borrowProperty.setUsefundstime(DateUtils.valueOf(usefundstime));
					}
					borrowProperty.setPropertyType(bProperty);
					borrowProperty.setWarrant(warrant);

					if (!StringUtils.isBlank(endtime)) {
						borrowProperty.setEndTime(DateUtils.valueOf(endtime));
					} else {
						borrowProperty.setEndTime(new Date());
					}

				}
			}
		}
		return borrowProperty;
	}
	// v1.8.0.3_u3 TGPROJECT-335 wujing 2014-06-18 end

	/**
	 * 针对规则表中发标规则校验
	 * 
	 * @param lowestMoney
	 * @param surplusAccount
	 * @param borrow
	 * @return
	 */
	private boolean checkAddBorrow(Borrow borrow) {
		Rule rule = ruleService.getRuleByNid("check_addBorrow");
		if (rule != null) {
			if (rule.getStatus() == 1) {
				double lowestMoney = paramDouble("lowestMoney");
				String endtime = paramString("endtime");
				long warrantId = paramLong("warrantId"); // 担保机构
				// 校验参数是否正确
				if (StringUtils.isBlank(endtime)) {
					throw new BussinessException("筹款到期时间必须选择");
				}
				if (lowestMoney == 0) {
					throw new BussinessException("最低筹集总额必须选择");
				}
				if (warrantId == 0) {
					throw new BussinessException("担保机构必须选择");
				}

				// 校验发标最低筹集金额是否合法
				int checkLostStatus = rule.getValueIntByKey("check_lostAccount");
				if (checkLostStatus == 1) {
					double lostApr = rule.getValueDoubleByKey("lostAccount_apr");
					if (lowestMoney < borrow.getAccount() * lostApr || lowestMoney > borrow.getAccount()) {
						throw new BussinessException("最低筹集总额必须大于借款金额的" + (lostApr * 100) + "%" + ",小于借款总额");
					}
				}
				// 校验借款额度是否超过网站规定的总借款额度
				int checkAddBorrowAccount = rule.getValueIntByKey("check_account");
				if (checkAddBorrowAccount == 1) {
					double borrowAccount = rule.getValueDoubleByKey("add_account"); // 获取网站规定用户借款上线
					double useBorrowAccount = borrowService.sumBorrowAccountByUserId(getSessionUser().getUserId());
					if (lowestMoney + useBorrowAccount > borrowAccount) {
						throw new BussinessException("借款总额已经超过平台规定的借款上线，请核对借款信息！");
					}
				}
				// 校验发标有效时间是否小于当前系统时间
				int checkEndtime = rule.getValueIntByKey("checkEndtime");
				if (checkEndtime == 1) {
					long endtimes = DateUtils.getTime(DateUtils.valueOf(endtime));
					long nowtime = DateUtils.getTime(new Date());
					if (endtimes < nowtime) {
						throw new BussinessException("筹款到期时间小于系统时间，请重新发标");
					}
				}
			}
		}
		return true;
	}

	/**
	 * 更新标
	 * 
	 * @return
	 * @throws Exception
	 */
	public String update() throws Exception {
		boolean isOk = true;
		String checkMsg = "";
		try {
			isOk = validate(true);
			String borrowId = request.getParameter("id");
			Borrow b = borrowService.getBorrow(Long.parseLong(borrowId));
			BorrowModel wrapModel = BorrowHelper.getHelper(b);
			fillBorrow(wrapModel);
			borrowService.updateBorrow(b);
		} catch (Exception e) {
			isOk = false;
			checkMsg = e.getMessage();
			logger.error(e.getMessage(), e.getCause());
		}
		if (isOk) {
			message("/member/index.html", "");
		} else {
			message(checkMsg, "");
		}
		return SUCCESS;
	}

	private boolean validate(boolean checkHasUnFinsh) {
		if (checkHasUnFinsh) {
			checkHasUnFinsh();
		}
		if (!checkValidImg(StringUtils.isNull(request.getParameter("valicode")))) {
			throw new BussinessException("验证码不正确！");
		}

		UserAmount amount = userAmountService.getUserAmount(getSessionUser().getUserId());
		if (borrow.getType() == Constant.TYPE_CREDIT) {
			if (amount == null || amount.getCreditUse() < borrow.getAccount()) {
				throw new BussinessException("可用信用额度不足,请到'我的账户'申请授信额度！");
			}
		}
		// 发布净值标校验
		if (borrow.getType() == Constant.TYPE_PROPERTY) {
			User user = userService.getUserById(getSessionUser().getUserId());
			List<Borrow> borrowList = borrowService.getBorrowListByUserId(user.getUserId(), Constant.TYPE_PROPERTY);
			double propertyAccountSum = 0;
			for (Borrow borrow : borrowList) {
				if (borrow.getStatus() == 1 || borrow.getStatus() == 0) {
					propertyAccountSum += borrow.getAccount();
				}
			}
			double NotMoney = user.getAccount().getTotal(); // 待收总额
			double proportion = NumberUtils.getDouble(Global.getValue("addproperty_apr")); // 发布净值标总额与净资产的百分比
			if (borrow.getAccount() > (NotMoney * proportion - propertyAccountSum)) {
				throw new BussinessException("净值标总额超过了网站规定的净资产" + proportion * 100 + "%！");
			}

		}
		if (borrow.getMostAccount() != 0) {
			if (borrow.getLowestAccount() > borrow.getMostAccount()) {
				throw new BussinessException("最多投标金额不能小于最少投标金额");
			}
		}

		// 获取管理费限额规则
		Rule rule = ruleService.getRuleByNid("tender_reward_limit_rule");
		double partAccount_limit = 0;
		double funds_limit_low = 0;
		double funds_limit_apr = 0;
		if (rule != null && rule.getStatus() == 1) {// 有此规则，并且启用
			partAccount_limit = rule.getValueDoubleByKey("partAccount_limit");
			funds_limit_low = rule.getValueDoubleByKey("funds_limit_low");
			funds_limit_apr = rule.getValueDoubleByKey("funds_limit_apr");
		}
		if (paramInt("award") == 1 && paramDouble("partAccount") <= 0) {
			throw new BussinessException("请您确认按投标金额比例奖励格式：范围：0.1%~" + partAccount_limit + "%");
		} else if (paramInt("award") == 1 && paramDouble("partAccount") > partAccount_limit) {
			throw new BussinessException("请您确认按投标金额比例奖励格式：范围：0.1%~" + partAccount_limit + "%");
		}
		if (paramInt("award") == 2 && paramDouble("funds") < funds_limit_low) {
			throw new BussinessException("请您确认按固定金额分摊奖励格式：不能低于" + funds_limit_low + "元");
		} else if (paramInt("award") == 2 && paramDouble("funds") >= paramDouble("account") * funds_limit_apr) {
			throw new BussinessException("请您确认按固定金额分摊奖励格式：不能高于总标金额的" + funds_limit_apr + "");
		}

		return true;
	}

	private boolean validateAssignment(int tenderId, Borrow borrow, BorrowTender bt) {
		// 是否有未完成的标
		checkHasUnFinsh();
		if (tenderId == 0) {
			throw new BussinessException("缺少有效参数！");
		}
		if (borrow.getAccount() > bt.getWaitAccount()) {
			throw new BussinessException("转让金额不能大于剩余本金");
		}

		// 是否存在其他债权转让标
		User user = getSessionUser();
		// 校验投标记录是否存在
		BorrowTender dbBt = borrowService.getTenderById(tenderId);
		if (dbBt == null) {
			throw new BussinessException("找不到您的投资记录，您不能债权转让", "/member/invest/assignmentList.html");
		}
		User Tenderuser = bt.getUser();
		if (Tenderuser.getUserId() != user.getUserId()) {
			throw new BussinessException("您非当事人转让债权，请联系客服，如恶意操作我们将追究刑事责任", "/member/invest/assignmentList.html");
		}

		return true;
	}

	private boolean checkHasUnFinsh() {
		List unfinshList = borrowService.unfinshBorrowList(getSessionUser().getUserId());
		/*
		 * if(unfinshList!=null&&unfinshList.size()>0){ throw new BussinessException("<p style='color:red;'>您还有尚未发布或正在招标的借款申请。如有疑问，请联系客服中心。</p>"); }
		 */
		return true;
	}

	private String checkBorrowConfig(int type) {
		/*
		 * 其中identify的字段规则： 发表做如下判断 111111，长度为6的二进制字符串,1表示该标种需要实名认证才允许发标，0表示不需要该认证就可以发标。 顺序分别是实名认证、VIP认证、邮箱认证、手机认证、视频认证、现场认证、信用额度、绑定银行卡。
		 */
		BorrowConfig bc = borrowService.getBorrowConfig(type);
		if (bc != null && !StringUtils.isBlank(bc.getIdentify())) {
			User user = userService.getUserById(getSessionUser().getUserId());
			if (user == null) {
				throw new BussinessException("非法操作");
			}
			if (StringUtils.isBlank(user.getApiId())) {
				message("请开通" + Global.getValue("api_name"), "/member/apiRealname.html", "请开通" + Global.getValue("api_name") + "账号");
				return MSG;
			}
			// 校验是否开通易极付
			if (user.getApiStatus() == 0) {
				message("请激活" + Global.getValue("api_name"), "/member/yjfActivate.html", "激活" + Global.getValue("api_name") + "账号");
				return MSG;
			}
			String[] arr = bc.getIdentify().split("");
			if ("1".equals(arr[1])) { // 实名认证
				if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
					message("请进行实名认证", "/member/apiRealname.html", "进行实名认证");
					return MSG;
				}
			}
			if ("1".equals(arr[2])) { // VIP认证
				if (user.getUserCache().getVipStatus() != 1) {
					message("请进行VIP认证", "/member/vip.html", "申请VIP");
					return MSG;
				}
			}
			if ("1".equals(arr[3])) { // 邮箱认证
				if (user.getEmailStatus() != 1) {
					message("请进行邮箱认证", "/member/identify/email.html", "进行邮箱认证");
					return MSG;
				}
			}
			if ("1".equals(arr[4])) { // 手机认证
				if (user.getPhoneStatus() != 1) {
					message("请进行手机认证", "/member/identify/phone.html", "进行手机认证");
					return MSG;
				}
			}
			if ("1".equals(arr[5])) { // 视频认证
				if (user.getVideoStatus() != 1) {
					message("请进行视频认证", "/member/identify/video.html", "进行视频认证");
					return MSG;
				}
			}
			if ("1".equals(arr[6])) { // 现场认证
				if (user.getSceneStatus() != 1) {
					message("请进行现场认证", "/member/identify/scene.html", "进行现场认证");
					return MSG;
				}
			}
			if ("1".equals(arr[7])) { // 信用额度
				if (user.getUserAmount().getCreditUse() <= 0) {
					message("请先申请信用额度", "/member/borrow/limitapp.html", "申请信用额度");
					return MSG;
				}
			}

			if ("1".equals(arr[8])) { // 绑定银行卡
				List<AccountBank> userBankList = accountService.getBankLists(user.getUserId()); // 获取用户可用的银行卡
				if (userBankList.size() < 1) {
					message("请先添加银行卡！", "/member/account/bank.html", "点击绑定银行卡");
					return MSG;
				}
			}

			if ("1".equals(arr[9])) { // 信用积分为0，不能发标
				if (user.getCredit().getValue() <= 0) {
					message("请先申请信用！", "/member/borrow/limitapp.html", "点击申请信用");
					return MSG;
				}
			}
			return addBorrowCheckAttestion(bc);
			// 发标
		}
		return null;
	}

	/**
	 * 发标校验实名信息
	 */
	public String addBorrowCheckAttestion(BorrowConfig bc) {
		String attestation = bc.getAttestation();
		if (!StringUtils.isBlank(attestation)) {
			String[] arr = attestation.split("");
			long userid = getSessionUser().getUserId();
			String msg = "";
			if ("1".equals(arr[1])) {// 地税证 id为22
				if (attestationService.getAttestations(userid, 22) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "地税证";
					} else {
						msg = msg + "、地税证";
					}
				}
			}
			if ("1".equals(arr[2])) {// 国税证 id为21
				if (attestationService.getAttestations(userid, 21) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "国税证";
					} else {
						msg = msg + "、国税证";
					}
				}
			}
			if ("1".equals(arr[3])) {// 现场照片 id为20
				if (attestationService.getAttestations(userid, 20) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "现场照片";
					} else {
						msg = msg + "、现场照片";
					}
				}
			}
			if ("1".equals(arr[4])) {// 房产证 id为19
				if (attestationService.getAttestations(userid, 19) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "房产证";
					} else {
						msg = msg + "、房产证";
					}
				}
			}
			if ("1".equals(arr[5])) {// 水费发票或电费发票或煤气发票（最近2个月） id为17
				if (attestationService.getAttestations(userid, 17) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "水费发票或电费发票或煤气发票（最近2个月）";
					} else {
						msg = msg + "、水费发票或电费发票或煤气发票（最近2个月）";
					}
				}
			}
			if ("1".equals(arr[6])) {// 营业执照副本（需要彩色） id为16
				if (attestationService.getAttestations(userid, 16) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "营业执照副本（需要彩色）";
					} else {
						msg = msg + "、营业执照副本（需要彩色）";
					}
				}
			}
			if ("1".equals(arr[7])) {// 机构代码证 id为15
				if (attestationService.getAttestations(userid, 15) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "机构代码证";
					} else {
						msg = msg + "、机构代码证";
					}
				}
			}
			if ("1".equals(arr[8])) {// 公司银行流水（近三个月） id为14
				if (attestationService.getAttestations(userid, 14) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "公司银行流水（近三个月）";
					} else {
						msg = msg + "、公司银行流水（近三个月）";
					}
				}
			}
			if ("1".equals(arr[9])) {// 身份证正面 id为29
				if (attestationService.getAttestations(userid, 29) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "身份证正面";
					} else {
						msg = msg + "、身份证正面";
					}
				}
			}
			if ("1".equals(arr[10])) {// 身份证背面 id为29
				if (attestationService.getAttestations(userid, 30) <= 0) {
					if ("".equals(msg)) {
						msg = msg + "身份证背面";
					} else {
						msg = msg + "、身份证背面";
					}
				}
			}
			if (!"".equals(msg)) {
				msg = "请上传" + msg + "!";
				// message(msg,"/member/identify/attestation.html?tab=ziliao","点击上传证明");
				message("很抱歉,您不能发布借款,您需要通过线下资质审核 </br>联系电话:" + Global.getString("fuwutel"), "/member/identify/attestation.html?tab=ziliao", "点击上传证明");
				return MSG;
			}
		}
		return null;
	}

	private void checkBorrowConfig(Borrow model) {
		BorrowConfig bc = borrowService.getBorrowConfig(model.getType());
		if (bc != null && !StringUtils.isBlank(bc.getIdentify())) {
			User user = userService.getUserById(getSessionUser().getUserId());
			if (user == null) {
				throw new BussinessException("非法操作");
			}
			String[] arr = bc.getIdentify().split("");
			if ("1".equals(arr[7])) {
				if (user.getUserAmount().getCreditUse() < model.getAccount()) {
					throw new BussinessException("发标金额不能大于您的信用额度，请申请信用额度");
				}
			}
		}
	}

	/**
	 * 投标的方法
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "tender", results = { @Result(name = "huifuTender", type = "ftl", location = "/member/huifu/InitiativeTende.html"), @Result(name = "mmm", type = "ftl", location = "/member/mmm/mmmTender.html"), @Result(name = "ipstender", type = "ftl", location = "/member/ips/ipscommit.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String tender() throws Exception {

		BorrowParam param = new BorrowParam(request);// 初始化
		Borrow borrow = borrowService.getBorrow(param.getId());

		// edit by gy 2016-10-13 18:15:13 体验标投标
		if (borrow.getType() == Constant.TYPE_EXPERIENCE) {
			response.sendRedirect("/borrow/doTenderExperienceBorrow.html");
		} else {
			User user = userService.getUserById(getSessionUser().getUserId());
			String pwd = StringUtils.isNull(param.getPwd());// 定向密码
			if (!StringUtils.isBlank(borrow.getPwd())) {// 如果 标设置定向密码
				if (StringUtils.isNull(pwd).equals("")) {
					throw new BussinessException("定向标密码不能为空! ");
				}
				if (!borrow.getPwd().equals(pwd)) {// 名文保存不要加密
					throw new BussinessException("定向标密码不正确! ");
				}
			}
			// 校验投标人和发标人是否是同一个人
			if (user.getUserId() == borrow.getUser().getUserId()) {
				throw new BussinessException("自己不能投自己的标");
			}
			if (user.getIslock() == 1) {
				throw new BussinessException("您账号已经被锁定，不能进行投标，请跟管理员联系!");
			}

			// baseAction已经判断验证码是否输入
			// checkValidImgWithUrl("/invest/detail.html?borrowid="+param.getId());

			if (param.getMoney() <= 0 && param.getTenderCount() <= 0) {
				throw new BussinessException("投标金额不能为0元！");
			}
			checkContract();// 国控小微合同
			// 第三方拦截
			apiService.checkApiLoan(user);// 授权处理
			// v1.8.0.4 TGPROJECT-42 qj 2014-04-09 stop

			logger.debug("Begin tender! uid=" + user.getUserId() + ",bid=" + param.getId() + ",tender_money=" + param.getMoney());
			String tenderReturn = madTender(param, user);// 处理发标方法，判断是否需要跳转页面

			if (!"".equals(tenderReturn)) {
				// 非体验标投标成功送奖品
				/*
				 * if(borrow.getType() != 115){ //投标成功送奖品 boolean rewardUsers = RewardUsers(user, 22, param.getMoney()); if(rewardUsers){ //给用户发站内信不能影响业务逻辑 try { //发送站内信 MsgReq req = new MsgReq(); req.setReceiver(user); req.setUsername(user.getUsername()); req.setSender(new User(Constant.ADMIN_ID)); req.setMsgOperate(this.msgService.getMsgOperate(29)); req.setTime(DateUtils.dateStr4(new Date())); DisruptorUtils.sendMsg(req); } catch (Exception e) { e.printStackTrace(); } } }
				 */
				return tenderReturn;// "mmm"
			}
			super.systemLogAdd(user, 17, "用户投标");

			logger.info("Show tender success msg! uid=" + user.getUserId() + ",bid=" + param.getId() + ",tender_money=" + param.getMoney());
			return RESULT;
		}
		return null;
	}

	/**
	 * add by gy 2016-10-13 18:15:13 体验标投标
	 *
	 * @return
	 */
	@Action(value = "doTenderExperienceBorrow")
	public String doTenderExperienceBorrow() {
		logger.info("体验金投标开始~~~~");
		if (getSessionUser() == null) {
			message("非正常访问，请您登陆", "/user/login.html");
			return MSG;
		}
		User user = userService.getUserById(getSessionUser().getUserId());
		Long borrowId = Long.parseLong(Global.getValue("experienceMoney_borrow_id"));
		Borrow borrow = borrowService.getBorrow(borrowId);
		double tenderMoney = 0;
		ExperienceMoney em = experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user).addParam("useStatus", 0));
		if (em != null) {
			tenderMoney = em.getExperienceMoney();
		} else {
			throw new BussinessException("体验金投标金额无效！");
		}

		// 校验投标人和发标人是否是同一个人
		if (user.getUserId() == borrow.getUser().getUserId()) {
			throw new BussinessException("自己不能投自己的标");
		}
		if (user.getIslock() == 1) {
			throw new BussinessException("您账号已经被锁定，不能进行投标，请跟管理员联系!");
		}

		logger.info("Begin doTenderExperience! uid=" + user.getUserId() + ",bid=" + borrowId + ",tender_money=" + tenderMoney);

		if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
			logger.info("充值异常日志记录：" + user.getPhone() + ", 您的实名审核未通过， 导致充值失败！");
			return MSG;
		}

		apiService.checkApiLoan(user);// 授权处理

		BorrowParam param = new BorrowParam();
		param.setId(borrowId);
		param.setMoney(tenderMoney);
		param.setIp(getRequestIp());
		if (isWap()) param.setWap(true);

		try {
			DisruptorUtils.experienceTender(param, user);

			String resultFlag = System.currentTimeMillis() + "";
			param.setResultFlag(resultFlag);

			if (isWap()) {
				Map<String, Object> dataMap  = new HashMap<String, Object>();
				dataMap.put("returnURL", Global.getString("weburl") + "/wap/Innerpage.html?resultFlag=" + resultFlag);
				super.writeJson(new Json(dataMap, ""));
				return null;
			}

			request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
			request.setAttribute("back_url", "/member/main.html");// 失败返回地址
			request.setAttribute("r_msg", "投标成功");
			request.setAttribute("tenderFlag", resultFlag);

			return RESULT;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 通过规则，投标是否需要同意协议
	 */
	private void checkContract() {
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		Rule rule = ruleService.getRuleByNid("borrow_detail");
		// v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		if (rule != null && rule.getStatus() == 1) {
			String result = rule.getValueStrByKey("check_contract");
			if (result.equals("1")) {
				String checkContract = request.getParameter("checkContract");
				if (checkContract == null || (!checkContract.equals("true"))) {
					throw new BussinessException("投标必须同意投标协议后，才可以进行投标操作！");
				}
			}

		}

	}

	// v1.8.0.4 TGPROJECT-140 lx 2014-04-23 start
	/**
	 * 保证书下载
	 * 
	 * @return
	 */
	@Action(value = "downloadfile")
	public String downloadfile() {
		Borrow borrow = borrowService.getBorrow(paramInt("borrowId"));
		if (borrow == null) {
			throw new BussinessException("借款标不存在！");
		} else if (borrow.getStatus() < 6) {
			if (borrow.getStatus() == 1 && borrow.getAccount() == borrow.getAccountYes()) {
			} else {
				throw new BussinessException("借款标尚未满标不能下载！");
			}
		} else if (borrow.getStatus() > 8) {
			throw new BussinessException("借款标状态错误！");
		}
		// 校验用户是否是管理员登陆，若是管理员登陆，则不校验是否为投资人或者是借款人
		User user = getSessionUser();
		User authUser = getAuthUser();
		if (authUser == null) {
			if (user == null) {
				message("非正常访问，请您登陆", "/user/login.html");
				return MSG;
			} else {
				// 检查改用户是否已经投资此借款标,否则无权访问
				SearchParam searchParam = SearchParam.getInstance();
				searchParam.addParam("borrow.id", paramInt("borrowId"));
				searchParam.addParam("user", user);
				List tenderList = borrowService.getTenderList(searchParam).getList();
				// 校验是否是投资人或者是借款人
				if (tenderList == null || tenderList.size() < 1) {
					if (borrow.getUser().getUserId() != getSessionUser().getUserId()) {
						throw new BussinessException("你不是投资人或借款人,无权访问该借款协议书！", "");
					}
				}
			}
		}

		String contextPath = ServletActionContext.getServletContext().getRealPath("/");
		InputStream ins = null;
		if (borrow.getBorrowProperty() == null) {
			throw new BussinessException("借款标附属信息有误");
		}
		if (borrow.getBorrowProperty().getFileName() == null) {
			throw new BussinessException("该借款标没有上传保证书");
		}
		String fileName = contextPath + borrow.getBorrowProperty().getFileName();
		String downloadFile = fileName.substring(fileName.lastIndexOf("/") + 1);
		try {
			ins = new BufferedInputStream(new FileInputStream(fileName));
			File file = new File(fileName);
			byte[] buffer = new byte[ins.available()];
			ins.read(buffer);
			ins.close();
			HttpServletResponse response = (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(downloadFile.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream ous = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			ous.write(buffer);
			ous.flush();
			ous.close();
		} catch (FileNotFoundException e) {
			logger.error("模板文件" + downloadFile + "未找到！");
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// v1.8.0.4 TGPROJECT-140 lx 2014-04-23 end
	/**
	 * 协议下载
	 * 
	 * @return
	 */
	@Action(value = "protocol")
	public String protocol() {

		boolean isOk = true;
		String checkMsg = "";
		// 查看该标是否满标复审，否则不让他下载
		Borrow borrow = borrowService.getBorrow(paramInt("borrowId"));
		if (borrow == null) {
			throw new BussinessException("借款标不存在！");
		} else if (borrow.getStatus() < 6) {
			if (borrow.getStatus() == 1 && borrow.getAccount() == borrow.getAccountYes()) {
			} else {
				throw new BussinessException("借款标尚未满标不能下载！");
			}
		} /*else if (borrow.getStatus() > 8) {
			throw new BussinessException("借款标状态错误！");
		}*/
		// 校验用户是否是管理员登陆，若是管理员登陆，则不校验是否为投资人或者是借款人
		User user = getSessionUser();
		User authUser = getAuthUser();
		if (authUser == null) {
			if (user == null) {
				message("非正常访问，请您登陆", "/user/login.html");
				return MSG;
			} else {
				// 检查改用户是否已经投资此借款标,否则无权访问
				SearchParam searchParam = SearchParam.getInstance();
				searchParam.addParam("borrow.id", paramInt("borrowId"));
				searchParam.addParam("user", user);
				List tenderList = borrowService.getTenderList(searchParam).getList();
				// 校验是否是投资人或者是借款人
				if (tenderList == null || tenderList.size() < 1) {
					if (borrow.getUser().getUserId() != getSessionUser().getUserId()) {
						throw new BussinessException("你不是投资人或借款人,无权访问该借款协议书！", "");
					}
				}
			}
		}
		Long tender_id = paramLong("id");
		BorrowProtocol bp = BorrowProtocol.getInstance(user, paramInt("borrowId"), tender_id, 100);

        try {
            if (bp.getTender().getPreservationId() != null) {
                logger.info("保全ID存在，直接获取下载地址进行保全下载！！！" + bp.getTender().getPreservationId());
                // 生成下载链接
                ContractFileDownloadUrlResponse getDownloadURLRes = ContractFileDownload(bp.getTender().getPreservationId());
                if (getDownloadURLRes.isSuccess()) {
                    logger.info("获取保全下载地址成功: " + JSON.toJSONString(getDownloadURLRes));
                    response.sendRedirect(getDownloadURLRes.getDownUrl());
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("下载保全失败！" + e.getMessage());
        }

		File pdfFile = new File(bp.getInPdfName());
		if (pdfFile.exists()) {
			try {
				bp.createPdf();
			} catch (Exception e) {
				isOk = false;
				checkMsg = "生成pdf文件出错！";
				e.printStackTrace();
				logger.error(e);
			}
		}
		if (!isOk) {
			message(checkMsg, "/member/invest/hasTender.html");
			return MSG;
		} else {
			try {
                logger.info("保全ID不存在，先创建保全，保存保全ID， 然后生成下载地址进行保全下载！！");
                ContractFileCreate cfc = new ContractFileCreate(pdfFile.getPath(), "YWD-" + DateUtils.dateStr(new Date(), "yyyyMMdd") + "-" + bp.getBorrow_id(),
                        bp.getTender().getMoney(), user.getCardId(), user.getRealname(), "借款电子协议" + bp.getDownloadFileName().substring(0, bp.getDownloadFileName().length() - 4),
                        "16", user.getEmail(), user.getPhone(), "", "借款电子协议");
                logger.info("初始化保全信息：" + JSON.toJSONString(cfc));
                // 创建保全
                PreservationCreateResponse preservationCreateRes = ContractFilePreservation(cfc);
                if (preservationCreateRes.isSuccess()) {
                    Long preservationId = preservationCreateRes.getPreservationId();
                    logger.info("创建保全成功：保全ID: " + preservationId + "， 保全特征码: " + preservationCreateRes.getDocHash() + ", 保全时间: " + DateUtils.dateStr(new Date(preservationCreateRes.getPreservationTime()), "yyyy-MM-dd HH:mm:ss"));
                    // 生成下载链接
                    ContractFileDownloadUrlResponse getDownloadURLRes = ContractFileDownload(preservationId);
                    if (getDownloadURLRes.isSuccess()) {
                        logger.info("获取保全下载地址成功: " + JSON.toJSONString(getDownloadURLRes));
                        bp.getTender().setPreservationId(preservationId);
                        borrowTenderService.updateBorrowTender(bp.getTender());
                        response.sendRedirect(getDownloadURLRes.getDownUrl());
                    }

                }
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
		}
//		try {
//			generateDownloadFile(bp.getInPdfName(), bp.getDownloadFileName());
//		} catch (FileNotFoundException e) {
//			logger.error("协议pdf文件" + bp.getDownloadFileName() + "未找到！");
//		} catch (IOException e) {
//			logger.error(e.getMessage());
//		} catch (Exception e) {
//			logger.error(e);
//		}
		return null;
	}

	/**
	 * 创建保全
	 *
	 * @param cfc
	 * @return
	 */
	private PreservationCreateResponse ContractFilePreservation(ContractFileCreate cfc) {
		ContractFilePreservationCreateRequest.Builder builder = new ContractFilePreservationCreateRequest.Builder();
		builder.withFile(cfc.getFile());
		builder.withPreservationTitle(cfc.getPreservationTitle());
		builder.withPreservationType(cfc.getPreservationType());
		builder.withIdentifer(new PersonalIdentifer(cfc.getUserIdentifer(), cfc.getUserRealName()));
		builder.withSourceRegistryId(cfc.getSourceRegistryId());
		builder.withContractAmount(cfc.getContractAmount());
		builder.withContractNumber(cfc.getContractNumber());
		builder.withMobilePhone(cfc.getMobilePhone());
		builder.withComments(cfc.getComments());
		builder.withIsNeedSign(true);
		PreservationCreateResponse preservationCreateRes = ThemisClientInit.getClient().createPreservation(builder.build());
		return preservationCreateRes;
	}

	/**
	 * 下载保全
	 *
	 * @param preservationId
	 * @return
	 */
	private ContractFileDownloadUrlResponse ContractFileDownload(Long preservationId) {
		ContractFileDownloadUrlRequest request = new ContractFileDownloadUrlRequest();
		request.setPreservationId(preservationId); // 这里传保全备案号
		ContractFileDownloadUrlResponse getDownloadURLRes = ThemisClientInit.getClient().getContactFileDownloadUrl(request);
		return getDownloadURLRes;
	}

	/**
	 * 债权转让标，信息填充
	 * 
	 * @param model
	 * @return
	 */
	private Borrow fillAssignBorrow(BorrowModel model, BorrowTender bt) {
		Borrow b = model.getModel();
		User user = this.getSessionUser();
		b.setUser(user);
		b.setAccount(borrow.getAccount());
		b.setName(borrow.getName());
		b.setContent(borrow.getContent());
		b.setUsetype(borrow.getUsetype());
		b.setLowestAccount(borrow.getLowestAccount());
		b.setMostAccount(borrow.getMostAccount());
		b.setValidTime(borrow.getValidTime());
		b.setPwd(StringUtils.isNull(borrow.getPwd()));
		// 投标奖励
		b.setAward(borrow.getAward());
		b.setFunds(borrow.getFunds());
		b.setPartAccount(borrow.getPartAccount());

		// 还款金额
		double repayAccount = bt.getWaitAccount() + bt.getWaitInterest();
		b.setRepaymentAccount(repayAccount);
		// IP
		b.setAddip(this.getRequestIp());
		b.setAddtime(new Date());

		return b;
	}

	private Borrow fillBorrow(BorrowModel model) {
		Borrow b = model.getModel();
		User user = this.getSessionUser();
		b.setUser(user);
		b.setAccount(borrow.getAccount());
		b.setName(borrow.getName());
		b.setContent(borrow.getContent());
		b.setUsetype(borrow.getUsetype());
		b.setLowestAccount(borrow.getLowestAccount());
		b.setMostAccount(borrow.getMostAccount());
		b.setValidTime(borrow.getValidTime());
		b.setPwd(StringUtils.isNull(borrow.getPwd()));
		// 投标奖励
		b.setAward(borrow.getAward());
		b.setFunds(borrow.getFunds());
		b.setPartAccount(borrow.getPartAccount());
		b.setViewMoney(borrow.getAccount());

		// 还款金额
		InterestCalculator ic = model.interestCalculator();
		if (b.getType() == 107) {// 利率必须是0
			b.getApr();
			b.setApr(0);
		}

		// 阶梯收益sj
		if (b.getIsIncomeLadder() == 1) {
			b.setRepaymentAccount(0);
		} else {
			double repayAccount = ic.getTotalAccount();
			b.setRepaymentAccount(repayAccount);
		}

		// IP
		b.setAddip(this.getRequestIp());
		b.setAddtime(new Date());

		return b;
	}

	public String newprotocol() {
		User user = getSessionUser();
		boolean isOk = true;
		String checkMsg = "";
		borrow = borrowService.getBorrow(borrow.getId());

		SearchParam param = new SearchParam();
		param.addParam("user", new User(user.getUserId()));
		param.addParam("borrow", borrow);

		List list = borrowService.getTenderList(param).getList();
		if (!(list.size() > 0 || user.getUserId() == borrow.getUser().getUserId())) {
			message("您不是投资人，无权访问该借款协议书！", "");
			return MSG;
		}
		Long tender_id = paramLong("tender_id");
		BorrowProtocol bp = new BorrowProtocol(user, borrow.getId(), tender_id);

		File pdfFile = new File(bp.getInPdfName());
		if (pdfFile.exists()) {
			try {
				bp.createPdf();
			} catch (Exception e) {
				isOk = false;
				checkMsg = "生成pdf文件出错！";
			}
		}
		if (!isOk) {
			message(checkMsg);
			return MSG;
		}
		try {
			generateDownloadFile(bp.getInPdfName(), bp.getDownloadFileName());
		} catch (FileNotFoundException e) {
			logger.error("协议pdf文件" + bp.getDownloadFileName() + "未找到！");
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Action("preview")
	public String preview() throws Exception {
		BorrowModel model = null;
		Map map = new HashMap();
		try {
			model = BorrowHelper.getHelper(borrow);
			model.checkModelData();
			borrow.setAddtime(new Date());
			Global.perviewBorrowThreadLocal.set(borrow);
			map.put("excuteCode", "success");
			String json = JSON.toJSONString(map, true);
			printJson(json);

		} catch (BussinessException e) {
			map.put("rsmsg", e.getMessage());
			map.put("backurl", e.getBackUrl());
			String json = JSON.toJSONString(map, true);
			printJson(json);
		}
		return null;
	}

	@Action(value = "getPreviewInfo", results = { @Result(name = "success", type = "ftl", location = "/borrow/preview.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String getPreviewInfo() throws Exception {
		Borrow b = (Borrow) Global.getPerviewBorrow();
		User user = userService.getUserById(getSessionUser().getUserId());
		user.setCredit(user.getCredit());
		CreditRank creditRank = userService.getUserCreditRankByJiFen(user.getCredit().getValue());

		// 账户详情
		UserAccountSummary summary = accountService.getUserAccountSummary(user.getUserId());

		// 已还款
		SearchParam repaymentyesParam = SearchParam.getInstance();
		repaymentyesParam.addParam("user", user).addParam("status", 8);
		PageDataList repament_scuess = borrowService.getList(repaymentyesParam);
		// 未还款
		SearchParam repayment = SearchParam.getInstance();
		repayment.addParam("user", user).addOrFilter("status", 3, 6, 7);
		PageDataList repament_failure = borrowService.getList(repayment);
		// 流标
		SearchParam flowParam = SearchParam.getInstance();
		flowParam.addParam("user", user).addOrFilter("status", 2, 4, 5);
		PageDataList flow = borrowService.getList(flowParam);
		// 提前还款
		SearchParam earlyParam = SearchParam.getInstance();
		earlyParam.addParam("borrow.user", user).addParam("status", 1).addParam("repaymentTime", Operator.PROPERTY_GT, "repaymentYestime");
		PageDataList earlyList = borrowService.getBorrowRepaymentList(earlyParam);
		// 延迟还款
		SearchParam lateParam = SearchParam.getInstance();
		lateParam.addParam("borrow.user", user).addParam("status", 1).addParam("repaymentTime", Operator.PROPERTY_LT, "repaymentYestime");
		User sessionUser = userService.getUserById(getSessionUser().getUserId());
		session.put(Constant.SESSION_USER, sessionUser);
		request.setAttribute("creditRank", creditRank);
		request.setAttribute("summary", summary);
		request.setAttribute("repament_scuess", repament_scuess.getList());
		request.setAttribute("repament_failure", repament_failure.getList());
		request.setAttribute("flow", flow.getList());
		request.setAttribute("earlyList", earlyList.getList());
		request.setAttribute("lateList", earlyList.getList());
		request.setAttribute("user", user);
		if (session.get("perviewBorrow") == null) {
			session.put("perviewBorrow", b);
		}
		return SUCCESS;
	}

	/**
	 * 转让标校验
	 * 
	 * @return
	 */
	public String checkAssignment(int tenderId) {
		// 是否存在其他债权转让标
		User user = getSessionUser();
		// 校验投标记录是否存在
		BorrowTender bt = borrowService.getTenderById(tenderId);
		if (bt == null) {
			throw new BussinessException("找不到您的投资记录，您不能债权转让", "/member/invest/assignmentList.html");
		}
		User Tenderuser = bt.getUser();
		if (Tenderuser.getUserId() != user.getUserId()) {
			throw new BussinessException("您非当事人转让债权，请联系客服，如恶意操作我们将追究刑事责任", "/member/invest/assignmentList.html");
		}
		/*
		 * //校验转让金额是否符合平台规定 double assignAccount = paramDouble("accountMoney"); //获取债权装让标的转让金额 double lowestProportion = NumberUtils.getDouble(Global.getValue("assignment_Lowest_proportion")); //获取系统中发布债权转让标最低比例 if (assignAccount<lowestProportion*bt.getWaitAccount()) { //校验发布债权标发标金额的合法性 throw new BussinessException("你的转让价格低于系统规定的最低限制，请重新填写转让金额","/member/invest/assignmentList.html"); }
		 */

		Borrow borrow = bt.getBorrow();

		String message = borrowService.checkAssignMentBorrow(bt);// 校验合法性--待优化
		if (!StringUtils.isBlank(message)) {
			throw new BussinessException(message, "/member/invest/assignmentList.html");
		}
		// 期数
		SearchParam param = SearchParam.getInstance().addParam("status", 0).addParam("webstatus", 0).addParam("borrow.id", borrow.getId()).addOrder(OrderType.ASC, "id").addPage(0, 12); // 每个标的最大12个计划
		List<BorrowRepayment> repaymentList = borrowService.getBorrowRepaymentList(param).getList();
		BorrowRepayment br = repaymentList.get(0);
		long repaymentTime = br.getRepaymentTime().getTime();
		long nowTime = new Date().getTime();
		int days = (int) ((repaymentTime - nowTime) / (60 * 60 * 24 * 1000)) - 2;// 债权转让标期限为发标日至 原标最近还款日前两天
		Map map = new HashMap();
		map.put("borrow", borrow);
		map.put("borrowTender", bt);
		map.put("days", days);
		map.put("period", repaymentList.size()); // 债权转让的期数按照 剩余的期数
		// map.put("assignAccount", assignAccount); //
		request.setAttribute("assignmentData", map);
		return "detail";
	}

	/**
	 * 发布债权转让标时，进行发标 前校验，跳转到发标信息提示页面
	 * 
	 * @return
	 */
	@Action(value = "assignBorrow", results = { @Result(name = "success", type = "ftl", location = "/borrow/assignBorrow.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String assignBorrow() {
		String tenderId = paramString("tenderId"); // 发布债权转让标的投标Id
		String isassginment = paramString("isAssignment");// 是否是债权转让标
		String borrowType = paramString("t");

		// 发标校验
		request.setAttribute("tenderId", tenderId);
		request.setAttribute("isassginment", isassginment);
		request.setAttribute("borrowType", borrowType);
		return "success";
	}

	// 投标处理
	private String madTender(BorrowParam param, User tendUser) throws Exception {
		int apiType = Global.getInt("api_code");
		String resultFlag = "";
		switch (apiType) {
		//cancel by lxm 2017-2-13 16:59:05
//		case 1: // 汇付，校验要在投标跳转之前就做校验处理
//			logger.info("---------------------汇付方式投标.....");
//			Borrow borrow = borrowService.getBorrow(param.getId());
//			User borrowUser = borrow.getUser(); // 获取发标人用户信息
//			logger.info("borrowuserapi:" + borrowUser.getApiUsercustId());
//			// v1.8.0.4 TGPROJECT-227 qj 2014-04-28 start
//			// 1.8.0.4_u3 TGPROJECT-zhaiquanzhuanrang qinjun 2014-06-26 start
//			String[][] args = new String[][] {};
//			double tenderMoney = 0;
//			if (param.getTenderCount() != 0) {
//				param.setMoney(param.getTenderCount() * borrow.getFlowMoney());
//			}
//			if (borrow.getIsAssignment() == 1) { // 是债权转让标,当投债权转让标时，投标金额需要*2
//				// 获取债权转让标的原始借款标:查询原始投资信息，获取最原始标
//				BorrowTender oldTemder = borrowService.getAssignMentTenderByBorrowId(borrow.getId());
//				User oldBUser = oldTemder.getBorrow().getUser(); // 最原始借款人
//				User assignUser = borrow.getUser();
//
//				tenderMoney = param.getMoney() * 2;
//				args = new String[][] { { oldBUser.getApiUsercustId(), NumberUtils.format2Str(param.getMoney()), NumberUtils.format2Str(1.00) }, { assignUser.getApiUsercustId(), NumberUtils.format2Str(param.getMoney()), NumberUtils.format2Str(1.00) } }; // 拼装投标信息的json格式的二维数组
//			} else {
//				args = new String[][] { { borrowUser.getApiUsercustId(), NumberUtils.format2Str(param.getMoney()), NumberUtils.format2Str(1.00) } };
//				tenderMoney = param.getMoney();
//			}
//
//			// 拼装投标信息的json格式的二维数组
//			// String pwd = new String(Base64.encode(param.getPwd().getBytes()));
//			InitiativeTender autoTender = ChinapnrHelper.chinaTender(tendUser, args, param.getId(), tenderMoney, param.getPwd(), param.getTenderCount());
//			request.setAttribute("pnr", autoTender);
//			return "huifuTender";
//		case 2: // 易极付
//			logger.info("---------------------易极付方式投标.....");
//			MD5 md5 = new MD5(); // 支付密码验证开始
//			// 是否校验支付密码
//			if (Global.getInt("use_paypwd") == 1) {
//				if (StringUtils.isNull(tendUser.getPaypassword()).equals("")) {
//					throw new BussinessException("请先设一个支付密码! ");
//				}
//				if (StringUtils.isNull(param.getPaypwd()).equals("")) {
//					throw new BussinessException("支付密码不能为空!");
//				}
//				if (!md5.getMD5ofStr(param.getPaypwd()).equals(tendUser.getPaypassword())) {
//					throw new BussinessException("支付密码不正确! ");
//				}
//			}
//			resultFlag = tendUser.getUserId() + "-" + System.currentTimeMillis() + "";
//			param.setResultFlag(resultFlag);
//			request.setAttribute("tenderFlag", resultFlag);
//			request.setAttribute("ok_url", "/member/invest/tendering.html"); // 成功返回地址
//			request.setAttribute("back_url", "/invest/detail.html?borrowid=" + param.getId());// 失败返回地址
//			request.setAttribute("r_msg", "投标资金已冻结，恭喜您投标成功");
//			DisruptorUtils.tender(param, tendUser);// 调用投标异步处理
//			return "";
//		// v1.8.0.4 TGPROJECT-27 qj 2014-04-04 start
		case 3: // 双乾
			logger.info("---------------------双乾方式投标.....");
			borrow = borrowService.getBorrow(param.getId());
			// 投标校验 2014-12-15 zp start
			if (borrow.getIsAssignment() == 1 && 1 == 1) {//IsAssignment=1债权转让标
				// 获取债权转让标的原始借款标:查询原始投资信息，获取最原始标
				BorrowTender oldTemder = borrowService.getAssignMentTenderByBorrowId(borrow.getId());
				long uu = oldTemder.getBorrow().getUser().getUserId();
				if (tendUser.getUserId() == uu) {
					throw new BussinessException("原借款人，不能投本人的债权转让标");
				}
			}
			double hasTender = borrowService.hasTenderTotalPerBorrowByUserid(borrow.getId(), tendUser.getUserId());// 已投标金额
			double mostAccount = borrow.getMostAccount(); // 最大的投标金额
			double validAccount = param.getMoney();// 本次投标金额
			//本次投标金额 + 已投标金额 > 最大的投标金额
			if (validAccount + hasTender > mostAccount && mostAccount > 0) {// 确保不高于个人投标限额（最大投标额）
				//可以投的金额
				param.setMoney(mostAccount - hasTender);
			}
			//本次投标金额 > 借款总额 - 已投金额 = 标的剩余金额
			if (validAccount > borrow.getAccount() - borrow.getAccountYes()) {// 投标金额大于标的剩余金额,有效金额自动转为可投金额
				//可以投的金额就设置成标的剩余金额
				param.setMoney(borrow.getAccount() - borrow.getAccountYes());
			}
			// 投标校验 2014-12-15 zp start
			BorrowTender tender = new BorrowTender();
			// add by gy 2016-10-26 16:57:52 如果使用体验标抵扣本金，则需要在投标记录中记录体验金关联
			ExperienceMoney em = experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", tendUser).addParam("interestUseStatus", 0).addParam("receiveStatus", 1).addParam("useStatus", 1).addParam("interestIncomeStatus", 1));
			if (em != null)
				tender.setExperienceMoney(em);
			tender.setUser(tendUser);
			tender.setBorrow(borrow);
			tender.setAccount(param.getMoney());
			MmmLoanTransfer mmm = MmmHelper.addTender(tender);
			// end
			request.setAttribute("mmm", mmm);
			return "mmm";
		// v1.8.0.4 TGPROJECT-27 qj 2014-04-04 stop

//		case 4: // 环迅
//			logger.info("---------------------环迅方式投标.....");
//			borrow = borrowService.getBorrow(param.getId());
//			BorrowTender tenders = new BorrowTender();
//			tenders.setUser(tendUser);
//			tenders.setBorrow(borrow);
//			tenders.setAccount(param.getMoney());
//			IpsTenderNBorrow ipstender = IpsHelper.doIpsTender(tenders, tendUser);
//			request.setAttribute("ips", ipstender);
//			return "ipstender";
		default:
			resultFlag = tendUser.getUserId() + "-" + System.currentTimeMillis() + "";
			param.setResultFlag(resultFlag);
			request.setAttribute("tenderFlag", resultFlag);
			request.setAttribute("ok_url", "/member/invest/tendering.html"); // 成功返回地址
			request.setAttribute("back_url", "/invest/detail.html?borrowid=" + param.getId());// 失败返回地址
			request.setAttribute("r_msg", "投标资金已冻结，恭喜您投标成功");
			DisruptorUtils.tender(param, tendUser);// 调用投标异步处理
			logger.info("投标没有任何接口，请您查看系统配置.....");
			return null;
		}
	}

	/**
	 * 查看投标的合同协议
	 * 
	 * @return
	 */
	@Action(value = "seeContract", results = { @Result(name = "see", type = "ftl", location = "/invest/seeContract.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String seeContract() {

		return "see";
	}

}
