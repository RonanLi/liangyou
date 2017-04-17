package com.liangyou.web.action.admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import com.liangyou.api.ips.IpsAddBorrow;
import com.liangyou.api.ips.IpsHelper;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmLoanOrderQuery;
import com.liangyou.context.Constant;
import com.liangyou.context.ExcelType;
import com.liangyou.context.Global;
import com.liangyou.context.SortType;
import com.liangyou.dao.OperateLogDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.BorrowDetail;
import com.liangyou.domain.BorrowDetailType;
import com.liangyou.domain.BorrowFee;
import com.liangyou.domain.BorrowIncomeLadder;
import com.liangyou.domain.BorrowIntent;
import com.liangyou.domain.BorrowProperty;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.BusinessType;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.OperateLog;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.Rule;
import com.liangyou.domain.TenderCompensation;
import com.liangyou.domain.User;
import com.liangyou.domain.UserAmount;
import com.liangyou.domain.UserAmountApply;
import com.liangyou.domain.UserAmountLog;
import com.liangyou.domain.UserType;
import com.liangyou.domain.Warrant;
import com.liangyou.exception.BussinessException;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.MsgReq;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.BusinessTypeService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RepaymentService;
import com.liangyou.service.RuleService;
import com.liangyou.service.TenderCompensationService;
import com.liangyou.service.UserAmountService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.service.WarrantService;
import com.liangyou.tool.Page;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.ImageUtil;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/admin/borrow")
@ParentPackage("p2p-default")
@Results({ @Result(name="allborrow",type="ftl",location="/admin/borrow/allBorrow.html") })
@InterceptorRefs({  // 管理员拦截
	@InterceptorRef("manageAuthStack")
})
public class ManageBorrowAction extends BaseAction implements ModelDriven<Borrow> {

	@Autowired 
	private BorrowService borrowService;
	@Autowired 
	private UserAmountService userAmountService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private UserService userService;
	@Autowired
	private OperateService operateService;
	@Autowired
	OperateLogDao operateLogDao;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private WarrantService warrantService;
	@Autowired
	private ApiService apiService;
	//v1.8.0.4_u2    TGPROJECT-297  lx start
	@Autowired
	private AccountService accountService ;
	//v1.8.0.4_u2    TGPROJECT-297  lx end
	//v1.8.0.4 TGPROJECT-140 lx start
	private File files;
	private String filesFileName;
	private String filePath;
	private String sep = File.separator;
	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	@Autowired
	private TenderCompensationService tenderCompensationService;
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
	//一次上传多个
	//v1.8.0.3_u3 TGPROJECT-335  wujing 2014-06-16  start 
	private File[] fileImage;

	public File[] getFileImage() {
		return fileImage;
	}

	public void setFileImage(File[] fileImage) {
		this.fileImage = fileImage;
	}

	//v1.8.0.3_u3 TGPROJECT-335  wujing 2014-06-16  end
	
	//无名重庆 WMCQ-1 2014-09-09 wujing start
	@Autowired
	private BusinessTypeService businessTypeService;
	//无名重庆 WMCQ-1 2014-09-09 wujing end

	@Autowired
	private RepaymentService repaymentService;

	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}
	public String getFilesFileName() {
		return filesFileName;
	}

	public void setFilesFileName(String filesFileName) {
		this.filesFileName = filesFileName;
	}

	//v1.8.0.4 TGPROJECT-140 lx end


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Autowired
	private UserinfoService userinfoService;

	private Borrow  borrow = new Borrow();
	private Logger logger  = Logger.getLogger(ManageBorrowAction.class);

	@Override
	public Borrow getModel() {
		return borrow;
	}

	private void findAndSave(SearchParam param){
		PageDataList<Borrow> list=borrowService.getList(param);
		setPageAttribute(list, param);
	}
	private void findAndSaveRepayment(SearchParam param){
		PageDataList<Borrow> list=borrowService.getBorrowRepaymentList(param);
		setPageAttribute(list, param);
	}
	/**
	 * 所有借款
	 * @return
	 * @throws Exception
	 */
	@Action("showAllBorrow")
	public String showAllBorrow() throws Exception {
		//用户名
		String username = paramString("username");
		//类型
		String type = paramString("type");
		//状态
		String status = paramString("status");
		//是否是推荐标
		String recommendStatus = paramString("recommendStatus");
		//利率
		String infonlv =paramString("infonlv");
		//借款期限
		String timeLimit = paramString("timeLimit");

		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"))
				.addOrder(SortType.Get_showBorrowing_order_type(),SortType.Get_showBorrowing_order_name());

		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}
		if(!StringUtils.isBlank(status)) {
			if(status.equals("11")){//等待复审的标
				param.addParam("status", 1).addParam("account", Operator.PROPERTY_EQ, "accountYes");
			}else{
				param.addParam("status", NumberUtils.getInt(status));
			}
		}
		if(!StringUtils.isBlank(recommendStatus)) {
			param.addParam("recommendStatus", NumberUtils.getInt(recommendStatus));
		}

		if(!StringUtils.isBlank(infonlv)){
			param.addParam("apr", NumberUtils.getInt(infonlv));
		}
		if(!StringUtils.isBlank(timeLimit)){
			param.addParam("timeLimit", NumberUtils.getInt(timeLimit));
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		findAndSave(param);
		request.setAttribute("showStatus", 1);
		request.setAttribute("operateLimit", "1011");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "allborrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="所有借款_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			//HAOLIP-111  lx  2014-04-24 start
			String[] names=ExcelType.Get_manage_allborrow_names();
			String[] titles=ExcelType.Get_manage_allborrow_titles();
			//HAOLIP-111  lx  2014-04-24 end
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
	}
	/**
	 * 正在招标
	 * @return
	 * @throws Exception
	 */
	@Action("showBorrowing")
	public String showBorrowing() throws Exception {
		//用户名
		String username = paramString("username");
		//类型
		String type = paramString("type");

		SearchParam param=SearchParam.getInstance().addParam("status", 1)
				.addParam("account", Operator.PROPERTY_NOTEQ, "accountYes").addPage(paramInt("page"))
				.addOrder(SortType.Get_showBorrowing_order_type(),SortType.Get_showBorrowing_order_name());
		String recommendStatus = paramString("recommendStatus");

		String search = request.getParameter("search");

		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}
		if(!StringUtils.isBlank(recommendStatus)) {
			param.addParam("recommendStatus", NumberUtils.getInt(recommendStatus));
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		findAndSave(param);
		request.setAttribute("showRecommend", 1);
		setPageUrl();
		request.setAttribute("operateLimit", "1011");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showBorrowing");

		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "allborrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="正在招标款_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_borrow_names();
			String[] titles=ExcelType.Get_manage_borrow_titles();
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
	}
	/**
	 * 发标待审
	 * @return
	 * @throws Exception
	 */
	@Action(value="showTrialBorrow",
			results={@Result(name="showTrialBorrow",type="ftl",location="/admin/borrow/allBorrow.html")}
			)
	public String showTrialBorrow() throws Exception {
		//用户名
		String username = paramString("username");	
		//类型
		String type = paramString("type");

		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addParam("status", 0)
				.addOrder(SortType.Get_showBorrowing_order_type(),SortType.Get_showBorrowing_order_name());

		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}

		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		PageDataList<Borrow> list=borrowService.getList(param);
		setPageAttribute(list, param);
		setPageUrl();
		request.setAttribute("operateLimit", "1111");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showTrialBorrow");

		String actionType=paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "showTrialBorrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="发标待审的标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_borrow_names();
			String[] titles=ExcelType.Get_manage_borrow_titles();
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;

		}

	}
	/**
	 * 初审不通过
	 * @return
	 * @throws Exception
	 */
	@Action(value="showNotTrialBorrow",
			results={@Result(name="showNotTrialBorrow",type="ftl",location="/admin/borrow/allBorrow.html")}
			)
	public String showNotTrialBorrow() throws Exception {
		//用户名
		String username = paramString("username");	
		//类型
		String type = paramString("type");

		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrFilter("status", 5, 2).addOrder(OrderType.DESC,"id");
		String search = request.getParameter("search");
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}

		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		PageDataList<Borrow> list=borrowService.getList(param);
		setPageAttribute(list, param);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showNotTrialBorrow");

		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "showNotTrialBorrow";
		}else{
			List borrowList = borrowService.getExportBorrowList(param);//根据参数获得 所需要的列表
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");//获取根目录
			String downloadFile = "发标初审不通过的标_"+new Date()+".xls";
			String fullFile = contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_borrow_names();
			String[] titles=ExcelType.Get_manage_borrow_titles();
			ExcelHelper.writeExcel(fullFile, borrowList, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(fullFile, downloadFile);
			return null;

		}


	}
	//v1.8.0.4 HAOLIP-110 lx 2014-04-24 start
	/**
	 * 已满标待审
	 * @return
	 */
	@Action(value="showReviewBorrow",
			results={@Result(name="showReviewBorrow",type="ftl",location="/admin/borrow/fullborrow.html")}
			)
	public String showReviewBorrow()  throws Exception{
		//用户名
		String username = paramString("username");	
		//类型
		String type = paramString("type");
		SearchParam param=SearchParam.getInstance()
				.addPage(paramInt("page"))
				.addParam("status", 1)
				.addParam("account", Operator.PROPERTY_EQ, "accountYes")
				.addParam("type", Operator.NOTEQ, Constant.TYPE_FLOW)
				.addOrder(SortType.Get_showBorrowing_order_type(),SortType.Get_showBorrowing_order_name());
		PageDataList<Borrow> list=borrowService.getList(param);
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}

		setPageAttribute(list, param);
		setPageUrl();
		request.setAttribute("operateLimit", "1110");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		if(StringUtils.isBlank(actionType)){
			return "showReviewBorrow";
		}else{
			List borrowList = borrowService.getExportBorrowList(param);//根据参数获得 所需要的列表
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");//获取根目录
			String downloadFile = "已满标待审的标_"+DateUtils.dateStr2(new Date())+".xls";
			String fullFile = contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","user/.username","name",
					"user/.credit/.value","viewMoney","account","apr","tenderTimes","timeStr","statusStr","addtime","verifyTime"};
			String[] titles=new String[]{"ID","用户名","标名","信用积分","计划借款金额",
					"实际入账金额","利率","投标次数","借款期限","状态","发布时间","审核时间"};
			ExcelHelper.writeExcel(fullFile, borrowList, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(fullFile, downloadFile);
			return null;

		}
	}
	//v1.8.0.4 HAOLIP-110 lx 2014-04-24 end
	/**
	 * 撤回标，状态改成-1
	 * @return
	 * @throws Exception
	 */
	@Action(value="cancelBorrow")
	public String cancelBorrow() throws Exception {
		Borrow b = borrowService.getBorrow(paramLong("id"));
		if(b==null||(b.getStatus()>4||b.getStatus()==3)){
			this.message("非法操作！");
			return ADMINMSG;
		}
		BorrowModel wrapModel = BorrowHelper.getHelper(b);
		//校验撤标是否要进行多次审核，通过数据库中rule的规则配置
		User authUser = getAuthUser();
		Rule rule = ruleService.getRuleByNid("verify_rule");
		if (rule != null) {
			if (rule.getStatus() == 1) {    //校验此规则是否启用
				if(StringUtils.isBlank(paramString("verify_remark"))){
					throw new ManageBussinessException("审核备注必须填写", "/admin/borrow/viewFullBorrow.html?id=" + b.getId());
				}else{
					b.setVerifyRemark(paramString("verify_remark"));
				}
				int rulerStatus=rule.getValueIntByKey("verify_cancelborrow");   //获取撤标是否需要进行分布操作，1为需要，0为不需要
				if (rulerStatus == 1 ) {
					boolean operateRusult = operateService.doOperate(Constant.CANCEL_BORROE, b.getId(), authUser, wrapModel.getModel().getVerifyRemark(), borrow.getStatus());
					if (operateRusult) {     //判断是否是最后一个审核流程，是最后一个审核流程的话，开始处理后面的业务，来否则的话，不处理
						//不做处理
					}else{//不是最后一个审核流程的时候，返回给审核人提示信息
						message("撤标审核成功！","/admin/borrow/showBorrowing.html");
						return ADMINMSG;
					}
				}
			}

		}
		//审核状态

		wrapModel.verify(wrapModel.getModel().getStatus(), 5);

		AccountLog log=new AccountLog(wrapModel.getModel().getUser().getUserId(),Constant.UNFREEZE,getAuthUser().getUserId(),
				getTimeStr(),this.getRequestIp());

		//异步处理 生成本次操作标记
		BorrowParam param = new BorrowParam();
		String resultFlag = this.getAuthUser().getUserId() + "-" + System.currentTimeMillis()+"";
		param.setResultFlag(resultFlag);

		borrowService.deleteBorrow(b, log, param);
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转

		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/admin/borrow/showBorrowing.html"); //成功返回地址
		request.setAttribute("back_url", "/admin/borrow/showBorrowing.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，撤回标成功，请您返回查看");

		// 撤标审核成功，发消息通知借款人
		MsgReq req = new MsgReq();
		req.setIp(super.getRequestIp());
		req.setSender(authUser );
		req.setReceiver(this.userService.getUserById(b.getUser().getUserId()));
		req.setMsgOperate(this.msgService.getMsgOperate(22));
		req.setBorrowname(b.getName());
		req.setAccount("" + b.getAccount());
		req.setApr("" + b.getApr());
		DisruptorUtils.sendMsg(req);
		logger.debug("撤标审核成功通知借款人--------------------------------------");
		// 通知出借人
		List<BorrowTender> tenders = b.getBorrowTenders();
		for (BorrowTender borrowTender : tenders) {
			MsgReq req1 = new MsgReq();
			req1.setIp(super.getRequestIp());
			req1.setSender(authUser);
			req1.setReceiver(borrowTender.getUser());
			req1.setMsgOperate(this.msgService.getMsgOperate(23));
			//req1.setBorrowname(b.getName());
			//req1.setAccount("" + borrowTender.getAccount());
			DisruptorUtils.sendMsg(req1);
			logger.debug("撤标审核成功通知出借人--------------------------------------");
    }



		return ADMINRESULT;
	}
	/**
	 * 满标通过
	 * @return
	 */
	@Action("showIsFullBorrow")
	public String showIsFullBorrow() throws Exception {
		//用户名
		String username = paramString("username");	
		//类型
		String type = paramString("type");

		List<SearchFilter> list = new ArrayList<SearchFilter>();
		list.add(new SearchFilter("status", Operator.EQ, 7));
		list.add(new SearchFilter("status", Operator.EQ, 6));
		list.add(new SearchFilter("status", Operator.EQ, 8));
		SearchParam param=SearchParam.getInstance()
				.addOrFilter(list)
				.addParam("accountYes", Operator.PROPERTY_EQ,"account")
				.addPage(paramInt("page")).addOrder(OrderType.DESC,"id");

		String search = request.getParameter("search");
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type",  NumberUtils.getInt(type));
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		findAndSave(param);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showIsFullBorrow");
		//v1.8.0.4_u1 TGPROJECT-287  lx   start
		if(StringUtils.isBlank(actionType)){
			return "allborrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="复审通过_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_allborrow_names();
			String[] titles=ExcelType.Get_manage_allborrow_titles();
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		//v1.8.0.4_u1 TGPROJECT-287  lx   end
	}

	/**
	 * 满标审核不通过
	 * @return
	 */
	@Action("showNotFullBorrow")
	public String showNotFullBorrow() throws Exception {
		//用户名
		String username = paramString("username");	
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 49)
				.addPage(paramInt("page")).addOrder(OrderType.DESC,"id");
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		findAndSave(param);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showNotFullBorrow");
		//v1.8.0.4_u1 TGPROJECT-287  lx   start
		if(StringUtils.isBlank(actionType)){
			return "allborrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="审核不通过_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_allborrow_names();
			String[] titles=ExcelType.Get_manage_allborrow_titles();
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		//v1.8.0.4_u1 TGPROJECT-287  lx   end
	}

	/**
	 * 满标审核不通过
	 * @return
	 */
	@Action("showCancelBorrow")
	public String showCancelBorrow() throws Exception {

		//用户名
		String username = paramString("username");
		//类型
		String type = paramString("type");
		//状态
		String status = paramString("status");

		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 59, 49,2,-1)
				.addPage(paramInt("page")).addOrder(OrderType.DESC,"id");

		String search = request.getParameter("search");
		if("true".equals(search)){
			if(!StringUtils.isBlank(username)) {
				param.addParam("user.username", Operator.LIKE, username);
			}
			if(!StringUtils.isBlank(type)) {
				param.addParam("type", NumberUtils.getInt(type));
			}
			if(!StringUtils.isBlank(status)) {
				param.addParam("status", NumberUtils.getInt(status));
			}
		}
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		madeBusinessType(param);
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		findAndSave(param);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		request.setAttribute("borrowType", "showCancelBorrow");
		request.setAttribute("showStatus", 2);

		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "allborrow";
		}else{
			List listExport=borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="撤回的借款标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.Get_manage_borrow_names();
			String[] titles=ExcelType.Get_manage_borrow_titles();
			ExcelHelper.writeExcel(infile, listExport, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
	}
	/**
	 * 已经还款借款标
	 * @return
	 * @throws Exception
	 */
	@Action(value="repaid",
			results={@Result(name="success",type="ftl",location="/admin/borrow/repaid.html")}
			)
	public String repaid() throws Exception {
		//用户名
		String username = paramString("username");
		//类型
		String type = paramString("type");
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String repay_type = paramString("repay_type");//提前还款，正常还款，逾期还款

		Map<String, Object> extraparam = new HashMap<String, Object>();

		SearchParam param=SearchParam.getInstance()
				.addParam("status", 1)
				.addPage(paramInt("page")).addOrder(OrderType.DESC,"id");
		if(!StringUtils.isBlank(username)) {
			param.addParam("borrow.user.username", Operator.LIKE, username);
			extraparam.put("username", username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}

		if(!StringUtils.isBlank(startTime)){
			extraparam.put("dotime1", startTime);
			param.addParam("repaymentTime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			extraparam.put("dotime2", endTime);
			param.addParam("repaymentTime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}

		if(!StringUtils.isBlank(repay_type)){//提前还款，正常还款，逾期还款
			int beforeDays = Global.getInt("repay_before_days");//提前几天算提前还款
			long times = beforeDays*24*60*60;
			if("on_time".equals(repay_type)){//正常还款
				param.addParam("repaymentTime", Operator.DATE_LTE_TIMES, "repaymentYestime",times)
				.addParam("repaymentTime", Operator.PROPERTY_GT, "repaymentYestime");
			}else if("before_time".equals(repay_type)){//提前还款
				param.addParam("repaymentTime", Operator.DATE_GT_TIMES, "repaymentYestime",times);
			}else{//逾期还款
				param.addParam("repaymentTime", Operator.PROPERTY_LT, "repaymentYestime");
			}
			extraparam.put("repay_type", repay_type);
		}

		PageDataList<Borrow> list=borrowService.getBorrowRepaymentList(param);
		setPageAttribute(list,extraparam);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return SUCCESS;
		}else{
			List listExport=borrowService.getBorrowRepaymentExportList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="已还款_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.get_manager_borrow_repaid_names();
			String[] titles=ExcelType.get_manager_borrow_repaid_titles();
			List rlist=borrowService.getBorrowRepaymentList(param).getList();
			ExcelHelper.writeExcel(infile, listExport, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}

	}

	/**
	 * 正在还款借款标
	 * @return
	 * @throws Exception
	 */
	@Action(value="repaying",
			results={@Result(name="success",type="ftl",location="/admin/borrow/repaying.html")}
			)
	public String repaying() throws Exception {
		String username = paramString("username");
		//类型
		String type = paramString("type");
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 0, 2)
				.addPage(paramInt("page")).addOrder(OrderType.ASC,"repaymentTime");
		if(!StringUtils.isBlank(username)) {
			param.addParam("borrow.user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(type)) {
			param.addParam("type", NumberUtils.getInt(type));
		}
		if(!StringUtils.isBlank(startTime)){
			param.addParam("repaymentTime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("repaymentTime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList<Borrow> list=borrowService.getBorrowRepaymentList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
		extraparam.put("dotime1", startTime);
		extraparam.put("dotime2", endTime);
		setPageAttribute(list, param,extraparam);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		if(StringUtils.isBlank(actionType)){
			return SUCCESS;
		}else{
			List listExport=borrowService.getBorrowRepaymentExportList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="待还款_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","borrow/.user/.username","borrow/.name",
					"period","repaymentTime","repaymentYestime","repaymentAccount","capital","interest","statusStr"};
			String[] titles=new String[]{"ID","用户名","标名","期数","到期时间","还款时间","还款金额","还款本金","还款利息","状态"};
			ExcelHelper.writeExcel(infile, listExport, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
	}
	/**
	 * 未还款的流转标
	 * @return
	 * @throws Exception
	 */
	@Action("unFinishFlow")
	public String unFinishFlow() throws Exception {
		String username = paramString("username");

		SearchParam param=SearchParam.getInstance()
				.addParam("status", 1)
				.addParam("type", Constant.TYPE_FLOW)
				.addPage(paramInt("page")).addOrder(OrderType.DESC,"id");

		String search = request.getParameter("search");
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		findAndSave(param);
		setPageUrl();
		request.setAttribute("operateLimit", "1000");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		return "allborrow";
	}

	/**
	 * 流转标停止流转
	 * @return
	 * @throws Exception
	 */
	@Action("stopflow")
	public String stopflow() throws Exception {
		long borrowId = paramLong("id");
		if(borrowId == 0){
			throw new ManageBussinessException("参数错误，请刷新页面重新操作！！");
		}
		Borrow b = borrowService.getBorrow(borrowId);
		if(b.getStatus() != 1){
			throw new ManageBussinessException("此流标不在停止流转状态！！");
		}
		b.setStatus(8);
		borrowService.updateBorrow(b);
		message("操作成功！！","/admin/borrow/showBorrowing.html");
		return ADMINMSG;
	}

	/**
	 * 信用额度审批列表
	 * @return
	 * @throws Exception
	 */
	@Action(value="amountApply",
			results={@Result(name="amountApply",type="ftl",location="/admin/borrow/amountApply.html")}
			)
	public String amountApply() throws Exception {
		//用户名
		String username = paramString("username");		
		//状态
		String status = paramString("status");

		SearchParam param=SearchParam.getInstance()
				.addOrder(OrderType.DESC,"id")
				.addPage(paramInt("page"));
		if(!StringUtils.isBlank(username)) {
			param.addParam("user.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(status)) {
			param.addParam("status", NumberUtils.getInt(status));
		}
		PageDataList<UserAmountApply> list = userAmountService.getUserAmountApplyList(param);
		setPageAttribute(list, param);
		setPageUrl();
		request.setAttribute("operateLimit", "1111");//第一位：查看；第二位：审核；第三位：撤回；第四位：停止流转
		List<CreditRank> creditRankList = Global.ALLCREDITRANK; //获取用户信用级别
		request.setAttribute("CreditRank", creditRankList);
		return "amountApply";
	}

	/**
	 * 后台显示投标记录
	 * @return
	 * @throws Exception
	 */
	@Action(value="tenderlist",
			results={@Result(name="success", type="ftl",location="/admin/borrow/tenderlist.html")})
	public String tenderList() throws Exception {
		String type=StringUtils.isNull(request.getParameter("type"));
		long borrow_id=this.paramLong("borrow_id");
		Borrow b=borrowService.getBorrow(borrow_id);
		if(b==null){
			message("非法操作！","");
			return ADMINMSG;
		}
		if(StringUtils.isBlank(type)){
			int page=this.paramInt("page");
			SearchParam param=SearchParam.getInstance().addPage(page).addParam("id", borrow_id).addOrder(OrderType.DESC,"id");
			PageDataList<BorrowTender> tenders = borrowService.getTenderList(param);
			setPageAttribute(tenders, param);
			request.setAttribute("borrow", b);
			return SUCCESS;
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="tender_"+System.currentTimeMillis()+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"username","money","account","addtime"};
			String[] titles=new String[]{"投标人","投标金额","有效金额","投标时间"};
			SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addParam("id", borrow_id);
			PageDataList<BorrowTender> tenders = borrowService.getTenderList(param);
			List list=tenders.getList();
			ExcelHelper.writeExcel(infile, list, BorrowTender.class, Arrays.asList(names), Arrays.asList(titles));
			export(infile, downloadFile);
			return null;
		}
	}

	//v1.8.0.4_u2    TGPROJECT-297  lx start
	/**
	 * 跳转编辑标页面  
	 * @return
	 */
	@Action(value="updateBorrowPage",
			results={@Result(name="success",type="ftl",location="/admin/borrow/updateBorrow.html")})
	public String updateBorrowPage() throws Exception {
		if(borrow.getId()<0){
			this.message("非法操作！");
			return ADMINMSG;
		}
		//v1.8.0.4_u1 TGPROJECT-385 wsl start 获取系统担保机构 
		List<Warrant> warrantList = warrantService.WarrantList();
		request.setAttribute("warrantList", warrantList);  //获取担保机构
		//v1.8.0.4_u1 TGPROJECT-385 wsl end
		//无名重庆 WMCQ-1 2014-09-09 wsl start
		SearchParam sp = new SearchParam().addParam("status",1);
		List<BusinessType> businessTypeList = businessTypeService.findListBusinessType(sp);
		request.setAttribute("businessTypeList", businessTypeList);//获取业务部门
		//无名重庆 WMCQ-1 2014-09-09 wsl end
		Borrow b=borrowService.getBorrow(borrow.getId());


		if(b==null||(b.getStatus()!=0&&b.getStatus()!=1)){
			this.message("非法操作！");
			return ADMINMSG;
		}
		request.setAttribute("b", b);
		return SUCCESS;
	}
	/**
	 * 修改
	 * @return
	 * @throws Exception
	 */
	@Action(value="updateBorrow"
			)
	public String updateBorrow() throws Exception {
		checkAdminValidImg();//验证验证码
		Borrow b=borrowService.getBorrow(borrow.getId());
		if(b==null){
			throw new BussinessException("对应的标不存在");
		}
		if(b.getStatus() !=0 && b.getStatus() !=1 ){
			throw new ManageBussinessException("此标已经处理，无法编辑，请刷新后重新操作！", "/admin/borrow/showTrialBorrow.html");
		}
		//		if(b.getAccount() != borrow.getAccount()){
		//			throw new BussinessException("发标金额不能修改");
		//		}
		if(b.getStatus()==1){
			borrow.setAccount(b.getAccount());
			borrow.setApr(b.getApr());
			borrow.setTimeLimit(b.getTimeLimit());
			borrow.setTimeLimitDay(b.getTimeLimitDay());
			borrow.setStyle(b.getStyle());
			borrow.setStatus(b.getStatus());
		}
		BorrowModel model=null;
		double lowestMoney = paramDouble("lowestMoney");   //最低筹集金额
		model=BorrowHelper.getHelper(borrow);
		model.checkModelData(); //校验发标合法性
		validate(false);
		checkAddBorrow(lowestMoney, borrow);   //发标校验发标金额,发标，标的特殊属性也在这里添加
		fillBorrow(model,b);
		model.setViewMoney(model.getAccount());    //发标时将viewmoney默认为发标金额

		//v1.8.0.3_u3 XINHEHANG-66  wuing 2014-06-19  start
		//封装borrowproperty对象
		BorrowProperty property = madNewBorrowProperty(b.getBorrowProperty());
		borrowService.updateBorrowAndProperty(model.getModel(), property);
		//addBorrowProperty(borrow);
		//borrowService.updateBorrow(model.getModel());
		//v1.8.0.3_u3 XINHEHANG-66  wujing 2014-06-19  end

		logger.info("修改标信息成功");
		message("修改标信息成功","/admin/borrow/showTrialBorrow.html");
		return ADMINMSG;
	}
	//v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 start 
	/**
	 * 发标时，添加标额外信息
	 * @param borrow
	 */
	private void addBorrowProperty(Borrow borrow){
		//v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		Rule rule = ruleService.getRuleByNid("borrow_detail");
		//v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 end
		if (rule != null) {
			if (rule.getStatus() ==1 ) {
				int result = rule.getValueIntByKey("add_borrow_property");
				if (result == 1) {
					String endtime = paramString("endtime");
					double lowestMoney = paramDouble("lowestMoney");
					String bProperty = paramString("borrowproperty");
					long warrantId = paramLong("warrantId"); //担保机构

					Warrant warrant = warrantService.findWarrant(warrantId);
					BorrowProperty borrowPropert = new BorrowProperty();
					Borrow tempb=borrowService.getBorrow(borrow.getId());
					if(tempb.getBorrowProperty()!=null){
						borrowPropert=tempb.getBorrowProperty();
					}
					borrowPropert.setBorrow(borrowService.getBorrow(borrow.getId()));
					borrowPropert.setPropertyType(bProperty);
					borrowPropert.setWarrant(warrant);
					if(!StringUtils.isBlank(endtime)){
						borrowPropert.setEndTime(DateUtils.valueOf(endtime));
					}else{
						borrowPropert.setEndTime(new Date());
					}
					double borrowProportion = paramDouble("borrowProportion");
					if(!(borrowProportion>0 && borrowProportion<=100)){
						throw new BussinessException("借款成数为0-100之间的数字");
					}
					String usefundstime = paramString("usefundstime");
					if(!StringUtils.isBlank(usefundstime)){
						borrowPropert.setUsefundstime(DateUtils.valueOf(usefundstime));
					}
					borrowPropert.setBorrowProportion(borrowProportion);
					borrowPropert.setLowestMoney(lowestMoney);
					//生成借款编号
					int borrowCount = borrowService.countBorrowByDay();
					int number = (int)(Math.random()*90)+10; //订单随机数
					String borrowNo = StringUtils.madeAgreementNo("JK",DateUtils.newdateStr3(new Date()), borrowCount, number); //借款申请号
					borrowPropert.setBorrowNo(borrowNo);
					//产生合同号：
					String borrowagreementNO = StringUtils.madeAgreementNo("HT", DateUtils.newdateStr2(new Date()), borrowCount, number);
					String verifyBorrowNo=StringUtils.madeAgreementNo("", DateUtils.newdateStr2(new Date()), borrowCount, number);
					borrowPropert.setVerifyBorrowNo(verifyBorrowNo);
					borrowPropert.setBorrowagreementNO(borrowagreementNO);
					int securedOrder=paramInt("securedOrder");
					int riskLevel=paramInt("riskLevel");
					int houseYear=paramInt("houseYear");
					String houseAddress = paramString("houseAddress");
					double houseArea = paramDouble("houseArea");
					double housePrice = paramDouble("housePrice");
					borrowPropert.setHouseAddress(houseAddress);
					borrowPropert.setHouseArea(houseArea);
					borrowPropert.setHousePrice(housePrice);
					borrowPropert.setHouseYear(houseYear);
					borrowPropert.setSecuredOrder(securedOrder);
					borrowPropert.setRiskLevel(riskLevel);
					if(tempb.getBorrowProperty()!=null){
						borrowService.addBorrowProperty(borrowPropert);
					}else{
						borrowService.updateBorrowProperty(borrowPropert);
					}
				}
			}
		}
	}
	//v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 end

	//v1.8.0.3_u3 XINHEHANG-66  wuing 2014-06-19  start 
	/**
	 * 重写封装borrowProperty方法
	 * @param borrow
	 * @return
	 */
	private BorrowProperty madNewBorrowProperty(BorrowProperty borrowProperty){
		//v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 start
		Rule rule = ruleService.getRuleByNid("borrow_detail");
		//v1.8.0.4_u3 TGPROJECT-344 qinjun 2014-06-30 end
		if (rule != null) {
			if (rule.getStatus() ==1 ) {
				int result = rule.getValueIntByKey("add_borrow_property");
				if (result == 1) {
					String endtime = paramString("endtime");
					double lowestMoney = paramDouble("lowestMoney");
					String bProperty = paramString("borrowproperty");
					long warrantId = paramLong("warrantId"); //担保机构
					int securedOrder=paramInt("securedOrder");
					int houseYear=paramInt("houseYear");
					String houseAddress = paramString("houseAddress");
					double houseArea = paramDouble("houseArea");
					double housePrice = paramDouble("housePrice");
					int riskLevel=paramInt("riskLevel");
					Warrant warrant = warrantService.findWarrant(warrantId);
					//v1.8.0.4_u2    TGPROJECT-297  lx start
					//判断borrow是否已经存在了borrowproperty,borrow 已经存在，不重新生成借款编号和合同号
					if ( borrowProperty  == null) {
						//生成借款编号
						borrowProperty = new BorrowProperty();
						int borrowCount = borrowService.countBorrowByDay();
						int number = (int)(Math.random()*90)+10; //订单随机数
						String borrowNo = StringUtils.madeAgreementNo("JK",DateUtils.newdateStr3(new Date()), borrowCount, number); //借款申请号
						borrowProperty.setBorrowNo(borrowNo);
						//产生合同号：
						String borrowagreementNO = StringUtils.madeAgreementNo("HT", DateUtils.newdateStr2(new Date()), borrowCount, number);
						String verifyBorrowNo=StringUtils.madeAgreementNo("", DateUtils.newdateStr2(new Date()), borrowCount, number);
						borrowProperty.setVerifyBorrowNo(verifyBorrowNo);
						borrowProperty.setBorrowagreementNO(borrowagreementNO);
					}

					borrowProperty.setRiskLevel(riskLevel);
					borrowProperty.setHouseAddress(houseAddress);
					borrowProperty.setHouseArea(houseArea);
					borrowProperty.setHousePrice(housePrice);
					borrowProperty.setHouseYear(houseYear);
					borrowProperty.setSecuredOrder(securedOrder);
					double borrowProportion = paramDouble("borrowProportion");
					String usefundstime = paramString("usefundstime");

					if(!StringUtils.isBlank(usefundstime)){
						borrowProperty.setUsefundstime(DateUtils.valueOf(usefundstime));
					}
					borrowProperty.setPropertyType(bProperty);
					borrowProperty.setWarrant(warrant);

					if(!StringUtils.isBlank(endtime)){
						borrowProperty.setEndTime(DateUtils.valueOf(endtime));
					}else{
						borrowProperty.setEndTime(new Date());
					}
					borrowProperty.setLowestMoney(lowestMoney);
					if(!(borrowProportion>0 && borrowProportion<=100)){
						throw new BussinessException("借款成数为0-100之间的数字");
					}
					borrowProperty.setBorrowProportion(borrowProportion);
					//v1.8.0.4_u2    TGPROJECT-297  lx start
				}
			}
		}
		return borrowProperty;
	}
	//v1.8.0.3_u3 XINHEHANG-66  wujing 2014-06-19  end



	private Borrow fillBorrow(BorrowModel model,Borrow oldBorrow){
		Borrow b=model.getModel();
		b.setUser(oldBorrow.getUser());
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

		//还款金额
		InterestCalculator ic=model.interestCalculator();
		if(b.getType() == 107 ){//利率必须是0
			b.setApr(0);
		}
		double repayAccount=ic.getTotalAccount();
		b.setRepaymentAccount(repayAccount);
		// IP
		b.setAddip(this.getRequestIp());
		b.setAddtime(new Date());

		return b;
	}

	private boolean validate(boolean checkHasUnFinsh) {
		if(checkHasUnFinsh){
			checkHasUnFinsh();
		}
		Borrow b=borrowService.getBorrow(borrow.getId());
		UserAmount amount=userAmountService.getUserAmount(b.getUser().getUserId());
		if(borrow.getType() == Constant.TYPE_CREDIT){
			if(amount==null||amount.getCreditUse()<borrow.getAccount()){
				throw new BussinessException("可用信用额度不足,请到'我的账户'申请授信额度！");
			}
		}
		//发布净值标校验
		if (borrow.getType() ==Constant.TYPE_PROPERTY) {
			User user=userService.getUserById(b.getUser().getUserId());
			double NotMoney = accountService.getUserNetMoney(user);
			double proportion=NumberUtils.getDouble(Global.getValue("addproperty_apr"));       //发布净值标总额与净资产的百分比
			if (borrow.getAccount()>NotMoney*proportion) {
				throw new BussinessException("净值标总额超过了网站规定的净资产" + proportion*100 + "%！");
			}

		}
		if( borrow.getMostAccount()!= 0){
			if(borrow.getLowestAccount() > borrow.getMostAccount()){
				throw new BussinessException("最多投标金额不能小于最少投标金额");
			}
		}

		return true;
	}
	private boolean checkHasUnFinsh() {
		Borrow b=borrowService.getBorrow(borrow.getId());
		List unfinshList=borrowService.unfinshBorrowList(b.getUser().getUserId());
		if(unfinshList!=null&&unfinshList.size()>0){
			throw new BussinessException("<p style='color:red;'>您还有尚未发布或正在招标的借款申请。如有疑问，请联系客服中心。</p>");

		}
		return true;
	}
	private boolean checkAddBorrow(double lowestMoney,Borrow borrow){
		Rule rule = ruleService.getRuleByNid("check_addBorrow");
		Borrow b=borrowService.getBorrow(borrow.getId());
		if (rule != null) {
			if (rule.getStatus() ==1) {
				String endtime = paramString("endtime");
				long warrantId = paramLong("warrantId"); //担保机构
				//校验参数是否正确
				if(StringUtils.isBlank(endtime)){
					throw new BussinessException("筹款到期时间必须选择");
				}
				if(lowestMoney==0){
					throw new BussinessException("最低筹集总额必须选择");
				}
				if(warrantId== 0){
					throw new BussinessException("担保机构必须选择");
				}

				//校验发标最低筹集金额是否合法
				int checkLostStatus = rule.getValueIntByKey("check_lostAccount");
				if (checkLostStatus ==1) {
					double lostApr =  rule.getValueDoubleByKey("lostAccount_apr");
					if (lowestMoney<borrow.getAccount()*lostApr || lowestMoney > borrow.getAccount()) {
						throw new BussinessException("最低筹集总额必须大于借款金额的"+(lostApr*100)+"%"+",小于借款总额");
					}
				}
				//校验借款额度是否超过网站规定的总借款额度
				int checkAddBorrowAccount = rule.getValueIntByKey("check_account");
				if (checkAddBorrowAccount == 1) {
					double borrowAccount = rule.getValueDoubleByKey("add_account");  //获取网站规定用户借款上线
					double useBorrowAccount = borrowService.sumBorrowAccountByUserId(b.getUser().getUserId());
					if (lowestMoney+useBorrowAccount>borrowAccount) {
						throw new BussinessException("借款总额已经超过平台规定的借款上线，请核对借款信息！");
					}
				}
				//校验发标有效时间是否小于当前系统时间
				int checkEndtime = rule.getValueIntByKey("checkEndtime");
				if (checkEndtime ==1) {
					long endtimes = DateUtils.getTime(DateUtils.valueOf(endtime));
					long nowtime = DateUtils.getTime(new Date());
					if (endtimes<nowtime) {
						throw new BussinessException("筹款到期时间小于系统时间，请重新发标");
					}
				}
			}
		}
		return true;
	}
	private void checkBorrowMoney(double money){
		Borrow b=borrowService.getBorrow(borrow.getId());
		Rule rule1 = ruleService.getRuleByNid("check_addBorrow");
		if (rule1 !=null) {
			int result = rule1.getValueIntByKey("check_account");
			if (result ==1) {
				double sumAccount = rule1.getValueDoubleByKey("add_account");
				double NoUseaccount=borrowService.sumBorrowAccountByUserId(b.getUser().getUserId());
				double useAccount = sumAccount- NoUseaccount;
				if (useAccount<money) {
					throw new BussinessException("你借款金额不能大于可用借款额度");
				}
			}
		}

	
	}
	private void checkBorrowConfig(Borrow model){
		Borrow b=borrowService.getBorrow(borrow.getId());
		BorrowConfig bc=borrowService.getBorrowConfig(model.getType());
		if(bc!=null && !StringUtils.isBlank(bc.getIdentify())){
			User user = userService.getUserById(b.getUser().getUserId());
			if(user == null){
				throw new BussinessException("非法操作");
			}
			String[] arr = bc.getIdentify().split("");
			if("1".equals(arr[7])){ 
				if(user.getUserAmount().getCreditUse() < model.getAccount() ){
					throw new BussinessException("发标金额不能大于您的信用额度，请申请信用额度");
				}
			}
		}
	}

	//v1.8.0.4_u2    TGPROJECT-297  lx end

	/**
	 * 审核标  
	 * @return
	 */
	@Action(value="verifyBorrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/verify.html"),
					@Result(name = "ispAddborrow", type = "ftl", location = "/member/ips/ipscommit.html" )})
	public String verifyBorrow() throws Exception {

		String actionType=StringUtils.isNull(paramString("actionType"));
		if(actionType.equals("verify")){
			checkAdminValidImg();//验证验证码
			Borrow b=borrowService.getBorrow(borrow.getId());
			List<BorrowDetail> details = getBorrowDetailList(fileImage, b);
			if(b.getStatus() !=0){
				throw new ManageBussinessException("此标已经处理，请勿重复处理！！", "/admin/borrow/showTrialBorrow.html");
			}
			//审核备注不能为空
			if(StringUtils.isBlank(paramString("verify_remark"))){
				throw new ManageBussinessException("审核信息不能为空！");
			}
			//获取初审是否需要新增借款标编号，通过数据库中rule的规则配置
			Rule verifyBorrowrule = ruleService.getRuleByNid("verify_borrow_no");
			if (verifyBorrowrule != null) {
				if (verifyBorrowrule.getStatus() == 1) { //校验此规则是否启用
					int rulerStatus=verifyBorrowrule.getValueIntByKey("status");//获取初审是否需要新增借款标编号，1为需要，0为不需要
					if (rulerStatus == 1 ) {
						//借款标编号
						if(StringUtils.isBlank(paramString("verifyBorrowNo"))){
							throw new ManageBussinessException("借款标编号不能为空！");
						}else{
							BorrowProperty bp=b.getBorrowProperty();
							bp.setVerifyBorrowNo(paramString("verifyBorrowNo"));
							borrowService.updateBorrowProperty(bp);
						}
					}
				}
			}
			// 标费用对象
			BorrowFee borrowFee = new BorrowFee();
			checkBorrowFee(borrowFee, b);
			BorrowModel wrapModel = BorrowHelper.getHelper(b);

			User authUser = getAuthUser();
			//校验初审是否要进行多次审核，通过数据库中rule的规则配置
			Rule rule = ruleService.getRuleByNid("verify_rule");
			if (rule != null) {
				if (rule.getStatus() == 1) { //校验此规则是否启用
					int rulerStatus=rule.getValueIntByKey("verify_borrow");//获取初审是否需要进行分布操作，1为需要，0为不需要
					if (rulerStatus == 1 ) {
						String paramLocal = "";
						String borrow_fee = paramString("borrow_fee");
						String manage_fee = paramString("manage_fee");
						if(!StringUtils.isBlank(manage_fee)){
							paramLocal +=",借款管理费：" +manage_fee;
						}
						if(!StringUtils.isBlank(borrow_fee)){
							paramLocal +=",风险备用金比例：" +borrow_fee;
						}
						Global.paramLocal.set(paramLocal);
						boolean operateRusult = operateService.doOperate(Constant.VERIFY_BORROW, b.getId(), authUser, paramString("verify_remark"), borrow.getStatus());
						if (operateRusult) {     
							//判断是否是最后一个审核流程，是最后一个审核流程的话，开始处理后面的业务，来否则的话，不处理
						}else{//不是最后一个审核流程的时候，返回给审核人提示信息
							message("审核成功！","/admin/borrow/showTrialBorrow.html");
							return ADMINMSG;
						}
					}
				}
			}
			//审核状态 
			wrapModel.verify(wrapModel.getModel().getStatus(), borrow.getStatus());
			logger.info("再次审核标时的状态"+wrapModel.getModel().getStatus());
			AccountLog log = null;
			log=new AccountLog(b.getUser().getUserId(),Constant.UNFREEZE,
					authUser.getUserId(), this.getTimeStr(),this.getRequestIp());
			wrapModel.getModel().setVerifyTime(new Date());
			wrapModel.getModel().setVerifyRemark(paramString("verify_remark"));
			borrowService.verifyBorrow(wrapModel,log,borrowFee,details);
			
			
			//阶梯收益sj
			String[] moneys = request.getParameterValues("money");
			String[] aprs = request.getParameterValues("apr");
			List<BorrowIncomeLadder> list = new ArrayList<BorrowIncomeLadder>();
			if (moneys !=null && aprs!=null) {
				
				for (int i = 0; i < moneys.length; i++) {
					BorrowIncomeLadder bil = new BorrowIncomeLadder();
					bil.setBorrow(b);
					bil.setMoney(NumberUtils.getDouble(moneys[i]));
					bil.setApr(NumberUtils.getDouble(aprs[i]));
					bil.setAddTime(new Date());
					bil.setAddIp(Global.getIP());
					list.add(bil);
				}
				borrowService.batchSave(list);
			}
			
			
			message("审核成功！","/admin/borrow/showTrialBorrow.html");
			String retMsg =  doApiAddborrow(b);
			super.systemLogAdd(authUser, 43, "管理员初审标成功");
			return retMsg;
		}else{
			if(borrow.getId()<0){
				this.message("非法操作！");
				return ADMINMSG;
			}
			Borrow b=borrowService.getBorrow(borrow.getId());


			if(b==null||(b.getStatus()!=0&&b.getStatus()!=1)){
				this.message("非法操作！");
				return ADMINMSG;
			}
			List<OperateLog> operateLoglist=operateService.getOperateLogByOrdNo(b.getId(),Constant.VERIFY_BORROW);
			if(operateLoglist.size()>0){
				OperateProgress op = operateLoglist.get(0).getOperateProgress();
				//v1.8.0.4 TGPROJECT-269 lx start
				if(!StringUtils.isBlank(op.getParam())){
					String[] param=op.getParam().split(",");
					for (int i = 1; i < param.length; i++) {
						String[] paramStr=param[i].split("：");
						if(paramStr.length==2 && paramStr[0].equals("借款管理费")){
							request.setAttribute("manage_fee", paramStr[1]);
						}else{
							request.setAttribute("borrow_fee", paramStr[1]);
						}
					}
				}
			}
			request.setAttribute("operateLoglist", operateLoglist);
			
			// 获取管理费限额规则
			Rule rule = ruleService.getRuleByNid("global_borrow_fee_limit_rule");
			double borrow_fee_limit = 100;
			double manage_fee_limit = 100;
			if(rule != null && rule.getStatus() == 1){//有此规则，并且启用
				borrow_fee_limit = rule.getValueDoubleByKey("borrow_fee_limit");
				manage_fee_limit = rule.getValueDoubleByKey("manage_fee_limit");
			}
			request.setAttribute("borrow_fee_limit", borrow_fee_limit);
			request.setAttribute("manage_fee_limit", manage_fee_limit);
			
			request.setAttribute("b", b);
		}
		return SUCCESS;
	}

	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 start
	/**
	 * 处理管理费
	 * @param borrowFee
	 * @param borrow
	 */
	public void checkBorrowFee(BorrowFee borrowFee , Borrow borrow){
		//风险备用金
		String borrow_fee = paramString("borrow_fee");
		borrowFee = borrow.getBorrowFee() == null?borrowFee:borrow.getBorrowFee()	;
		Rule rule = ruleService.getRuleByNid("global_borrow_fee_limit_rule");
		double borrow_fee_limit = 100;
		double manage_fee_limit = 100;
		if(rule != null && rule.getStatus() == 1){//有此规则，并且启用
			borrow_fee_limit = rule.getValueDoubleByKey("borrow_fee_limit");
			manage_fee_limit = rule.getValueDoubleByKey("manage_fee_limit");
		}
		
		if(StringUtils.isBlank(borrow_fee)){//为空即为不启用
			logger.info("不启用风险备用 金");
		}else{//起用风险备用金
			if(Double.parseDouble(borrow_fee) < borrow_fee_limit && checkParamWithRegex("^[0-9]+(.[0-9]{1,5})?$", borrow_fee) || checkParamWithRegex("^0$", borrow_fee) ){
			}else{
				throw new ManageBussinessException("备用金格式错误(格式：大于等于0，小于"+borrow_fee_limit+"，小数点后精确3到5位)");
			}
			borrowFee.setRiskreserve(Double.parseDouble(borrow_fee)/100);
		}
		//借款管理费
		String manage_fee = paramString("manage_fee");
		if(StringUtils.isBlank(manage_fee)){//为空即为不启用
			logger.info("不启用借款管理费");
		}else{//启用借款管理费
			if(Double.parseDouble(manage_fee) < manage_fee_limit && checkParamWithRegex("^[0-9]+(.[0-9]{1,5})?$", manage_fee) || checkParamWithRegex("^0$", manage_fee) ){
			}else{
				throw new ManageBussinessException("借款管理费格式错误(格式：大于等于0，小于"+manage_fee_limit+"，小数点后精确3到5位)");
			}
			//借款管理费
		    borrowFee.setManagefee(Double.parseDouble(manage_fee)/100);
		}
		//处理其他贷自定义管理费
		//checkOtherDiyFee(borrowFee, borrow);
	}

	/**
	 * 帝业出借管理费
	 */
	public void checkOtherDiyFee(BorrowFee borrowFee,Borrow borrow){
		User user = null;
		//平台服务费
		double webManageFee = checkBorrowFeeStr(paramString("web_manage_fee"));
		if(webManageFee == 0){//为空即为不启用
			logger.info("不启用平台服务费");
		}else{
			user = checkApiUser(paramString("web_manage_username"));
			//借款管理费
			borrowFee.setWebManageFee(webManageFee/100);
			borrowFee.setWebManageUser(user);
		}
		//小贷审核费
		double smallLoanFee = checkBorrowFeeStr(paramString("small_loan_fee"));
		if(smallLoanFee == 0){//为空即为不启用
			logger.info("不启用小贷审核费");
		}else{//启用借款管理费
			user = checkApiUser( paramString("small_loan_username"));
			//借款管理费
			borrowFee.setSmallLoanFee(smallLoanFee/100);
			borrowFee.setSmallLoanUser(user);
		}
		//担保公司担保费
		double warrantFee = checkBorrowFeeStr(paramString("warrant_fee"));
		if(warrantFee == 0){//为空即为不启用
			logger.info("不启用担保公司担保费");
		}else{//启用管理费
			user = checkApiUser(paramString("warrant_fee_username"));
			borrowFee.setWarrantFee(warrantFee/100);
			borrowFee.setWarrantUser(user);
		}
		//服务公司推荐费
		double introduceFee = checkBorrowFeeStr(paramString("introduce_fee"));
		if(introduceFee == 0){//为空即为不启用
			logger.info("不启用服务公司推荐费");
		}else{//启用管理费
			user = checkApiUser(paramString("introduce_fee_username"));
			borrowFee.setIntroduceFee(introduceFee/100);
			borrowFee.setIntroduceUser(user);
		}
	}

	private double checkBorrowFeeStr(String fee){
		if(StringUtils.isBlank(fee)){
			return 0;
		}
		if(checkParamWithRegex("^0\\.[0-9]{1,5}$", fee) || checkParamWithRegex("^0$", fee) ){
			return NumberUtils.getDouble(fee);
		}else{
			throw new ManageBussinessException("平台服务费格式错误，请您重新填写！");
		}
	}

	private User checkApiUser(String username){
		if(StringUtils.isBlank(username)){
			throw new ManageBussinessException("请输入到账用户！");
		}else{
			User user = userService.getUserByName(username);
			if(user != null){
				if(StringUtils.isBlank(user.getApiId()) || user.getApiStatus() != 1){
					throw new ManageBussinessException("用户未开通"+Global.getString("api_name")+"账户！");
				}
				return user;
			}else{
				throw new ManageBussinessException("用户不存在！");
			}
		}
	}
	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 end

	/**
	 * 查询标的详细状态
	 * @return
	 * @throws Exception
	 */
	@Action(value="viewBorrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/view.html")})
	public String viewBorrow() throws Exception {
		Borrow b=borrowService.getBorrow(borrow.getId());
		request.setAttribute("b", b);
		List<OperateLog> operateLoglist=operateService.getAllOperateLogByOrdNo(b);
		request.setAttribute("operateLoglist", operateLoglist);
		return SUCCESS;
	}

	@Action(value="viewFullBorrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewfull.html")})
	public String viewFullBorrow() throws Exception {
		Borrow b=borrowService.getBorrow(borrow.getId());
		List<OperateLog> operateLoglist=operateService.getOperateLogByOrdNo(b.getId(),"002");
		request.setAttribute("operateLoglist", operateLoglist);
		request.setAttribute("b", b);


		return SUCCESS;
	}


	private String getApiTenderNoStr(){
		long borrowId = paramLong("borrowId");
		StringBuffer sb = new StringBuffer();
		List<BorrowTender> list = borrowService.getAllTenderByBorrowId(borrowId);
		for (int i = 0 ;i<list.size() ; i++) {
			BorrowTender borrowTender = list.get(i);
			sb.append(borrowTender.getTrxId());
			if(i<list.size()-1){
				sb.append(",");
			}
		}
		return sb.toString();
	}


	//v1.8.0.4 TGPROJECT-140 lx 2014-04-23 start
	/**
	 * 保证书下载
	 * @return
	 */
	@Action(value="downloadfile")
	public String downloadfile(){
		Borrow borrow =	borrowService.getBorrow(paramInt("borrowId"));
		if(borrow ==null ){
			throw new BussinessException("借款标不存在！");
		}else if(borrow.getStatus() < 6 ){
			if(borrow.getStatus() == 1 && borrow.getAccount() == borrow.getAccountYes()){
			}else{
				throw new BussinessException("借款标尚未满标不能下载！");
			}
		}else if(borrow.getStatus() > 8 ){
			throw new BussinessException("借款标状态错误！");
		}
		//校验用户是否是管理员登陆，若是管理员登陆，则不校验是否为出借人或者是借款人
		User user = getSessionUser();
		User authUser = getAuthUser();
		if (authUser ==null) {
			if(user == null){
				message("非正常访问，请您登陆","/user/login.html");
				return MSG;
			}else{
				//检查改用户是否已经出借此借款标,否则无权访问
				SearchParam searchParam = SearchParam.getInstance();
				searchParam.addParam("borrow.id", paramInt("borrowId"));
				searchParam.addParam("user", user);
				List tenderList = borrowService.getTenderList(searchParam).getList();
				//校验是否是出借人或者是借款人
				if(tenderList==null || tenderList.size()<1 ) {
					if (borrow.getUser().getUserId() != getSessionUser().getUserId()) {
						throw new BussinessException("你不是出借人或借款人,无权访问该借款协议书！", "");
					}
				}
			}
		}

		String contextPath = ServletActionContext.getServletContext().getRealPath("/");
		InputStream ins=null;
		if(borrow.getBorrowProperty()==null){
			throw new BussinessException("借款标附属信息有误");
		}
		if(borrow.getBorrowProperty().getFileName()==null){
			throw new BussinessException("该借款标没有上传保证书");
		}
		String fileName=contextPath+borrow.getBorrowProperty().getFileName();
		String downloadFile=fileName.substring(fileName.lastIndexOf("/")+1);
		try {
			ins= new BufferedInputStream(new FileInputStream(fileName));
			File file=new File(fileName);
			byte [] buffer = new byte[ins.available()];
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
			logger.error("模板文件"+downloadFile+"未找到！");
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//v1.8.0.4 TGPROJECT-140 lx 2014-04-23 end 满标审核
	@Action(value="verifyFullBorrow")
	public String verifyFullBorrow() throws Exception {
		checkAdminValidImg();//验证验证码
		Borrow b=borrowService.getBorrow(borrow.getId());
		if( !(b.getStatus()==1 && b.getAccount() == b.getAccountYes()) ){//初审不通过  并且借款总额=实还总额
			throw new ManageBussinessException("此标不在满标复审状态，请勿重复操作！","/admin/borrow/showReviewBorrow.html");
		}
	
		//查看是否有投标本地处理完成，但是易极付处理失败的情况
		if(StringUtils.isBlank(paramString("verify_remark"))){
			throw new ManageBussinessException("审核备注必须填写", "/admin/borrow/viewFullBorrow.html?id=" + b.getId());
		}else{
			b.setVerifyRemark(paramString("verify_remark"));
		}

		//债券转让标，满标复审，判断并添加标记
		if(b.getIsAssignment()==1){ //转让标，判断是否正在还款，并作标记
			BorrowTender bt = borrowService.getAssignMentTenderByBorrowId(b.getId());
			if(bt==null){
				throw new BussinessException("该债券转让标的原出借记录查询不到，不能转让！");
			}
			if(bt.getStatus()!=-1){
				throw new BussinessException("该债券转让标的原出借已被处理，请查看出借记录与资金记录！");
			}
		}

		Rule rule = ruleService.getRuleByNid("verify_rule");//审核规则
		if(rule != null ){
			if(rule.getStatus() ==1){//是否启用
				int is_rule = rule.getValueIntByKey("verifyFull_borrow");//复审规则是否启用
				boolean is_over = false;
				if(is_rule==1){
					switch (borrow.getStatus()) {
					case 3: //满标通过     参数（标复审流程、标ID、当前后台用户、审核备注信息、审核状态）
						is_over = operateService.doOperate(Constant.VERIFY_FULL_BORROW, b.getId(),this.getAuthUser() , b.getVerifyRemark(),1);
						break;
					case 4: //满标不通过
						is_over = operateService.doOperate(Constant.VERIFY_FULL_BORROW, b.getId(),this.getAuthUser() , b.getVerifyRemark(), 2);
						break;
					default:
						is_over = operateService.doOperate(Constant.VERIFY_FULL_BORROW, b.getId(),this.getAuthUser() , b.getVerifyRemark(), 0);
						break;
					}
				}
				if(!is_over){
					message("审核成功","/admin/borrow/showReviewBorrow.html");
					return ADMINMSG;
				}
			}
		}
		//异步处理 生成本次操作标记
		BorrowParam param = new BorrowParam();
		String resultFlag = this.getAuthUser().getUserId() + "-" + System.currentTimeMillis()+"";
		param.setResultFlag(resultFlag);

		b.setStatus(borrow.getStatus()); //审核3通过和4不通过<不通过要 解冻资金>		
		b.setVerifyTime(new Date());
		b.setVerifyRemark(paramString("verify_remark"));
		b.setVerifyUser(this.getAuthUser());
		
		//阶梯收益
		if(b.getIsIncomeLadder() == 1){
			double repayment_account = borrowService.countTenderRepayment(b.getId());//预还总额+利息
			b.setRepaymentAccount(repayment_account);
		}
		BorrowModel borrow=BorrowHelper.getHelper(b);

		borrowService.verifyFullBorrow(borrow, param);
		User auth_user = getAuthUser();
		super.systemLogAdd(auth_user, 44, "管理员满标复审成功,标名:"+b.getName());
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 start
		//满标复审成功，发消息通知借款人
		if(this.borrow.getStatus() == 3 || this.borrow.getStatus() == 4){

			MsgReq req = new MsgReq();
			req.setIp(super.getRequestIp());
			req.setSender(auth_user);
			req.setReceiver(this.userService.getUserById(b.getUser().getUserId()));
			if(this.borrow.getStatus() == 3){//通过的模板
				req.setMsgOperate(this.msgService.getMsgOperate(14));
			}else{//不通过的模板
				req.setMsgOperate(this.msgService.getMsgOperate(16));
			}
			req.setBorrowname(b.getName());
			req.setAccount(""+b.getAccount());
			req.setApr(NumberUtils.format2Str(b.getApr()));
			req.setMonthapr(NumberUtils.format2Str(b.getApr()/12));
			//v1.8.0.4_u1 TGPROJECT-260 lx start
			if(b.getBorrowProperty()!=null){
				req.setEndTime(""+DateUtils.newdateStr6(b.getBorrowProperty().getEndTime()));
			}
			//v1.8.0.4_u1 TGPROJECT-260 lx end
			req.setValueDate(DateUtils.dateStr2(DateUtils.rollDay(new Date(), 1)));
			DisruptorUtils.sendMsg(req);
		}

		//通知出借人
		List<BorrowTender> tenders  = b.getBorrowTenders();
		for (BorrowTender borrowTender : tenders) {
			MsgReq req = new MsgReq();
			req.setIp(super.getRequestIp());
			req.setSender(auth_user);
			User u = userService.getUserById(borrowTender.getUser().getUserId());
			req.setReceiver(u);
			if (this.borrow.getStatus() == 3) {//通过的模板
				req.setMsgOperate(this.msgService.getMsgOperate(19));
			} else {//不通过的模板
				req.setMsgOperate(this.msgService.getMsgOperate(18));
			}
			req.setBorrowname(b.getName());
			req.setAccount("" + b.getAccount());
			req.setApr("" + b.getApr());
			req.setMonthapr(NumberUtils.format2Str(b.getApr() / 12));
			//v1.8.0.4_u1 TGPROJECT-260 lx start
			if (b.getBorrowProperty() != null) {
				req.setEndTime("" + DateUtils.newdateStr6(b.getBorrowProperty().getEndTime()));
			}
			//v1.8.0.4_u1 TGPROJECT-260 lx end
			req.setValueDate(DateUtils.dateStr2(DateUtils.rollDay(new Date(), 1)));
			DisruptorUtils.sendMsg(req);
		}
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 end
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/admin/borrow/showReviewBorrow.html"); //成功返回地址
		request.setAttribute("back_url", "/admin/borrow/viewFullBorrow.html?id="+borrow.getId());//失败返回地址
		//v1.8.0.3_u3 TGPROJECT-335  wuing 2014-06-20  start 
		String rMsg = "";
		if (borrow.getModel().getStatus() ==6 || borrow.getModel().getStatus() ==3) {
			rMsg="恭喜您：满标复审通过操作成功";
		}else{
			rMsg="满标复审不通过审核操作成功";
		}
		request.setAttribute("r_msg",rMsg+"，请您返回查看");
		//v1.8.0.3_u3 TGPROJECT-335  wujing 2014-06-20  end
		return ADMINRESULT;
	}

	@Action(value="viewAmountApply",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewAmountApply.html")})
	public String viewAmountApply() throws Exception{
		int id = NumberUtils.getInt(request.getParameter("id"));
		UserAmountApply apply=userAmountService.getAmountApplyById(id);
		if(apply==null){
			return ADMINMSG;
		}
		List<OperateLog> operateLoglist=operateService.getOperateLogByOrdNo(id,"003");
		request.setAttribute("operateLoglist", operateLoglist);
		request.setAttribute("apply", apply);
		List<CreditRank> creditRankList = Global.ALLCREDITRANK; //获取用户信用级别
		request.setAttribute("CreditRank", creditRankList);
		message("非法操作！");
		return SUCCESS;
	}

	@Action(value="verifyAmountApply",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewAmountApply.html")})
	public String verifyAmountApply() throws Exception{
		int id = NumberUtils.getInt(request.getParameter("id"));
		int status = NumberUtils.getInt(request.getParameter("status"));
		String verifyRemark = StringUtils.isNull(request.getParameter("verify_remark"));
		double account=NumberUtils.getDouble(request.getParameter("accountPass"));
		double giveCredit =account; //获取用户奖励信用积分
		UserAmountApply apply=userAmountService.getAmountApplyById(id);
		apply.setPassAccount(account);  //设置本地审核通过的额度
		apply.setGiveAccount(giveCredit);

		checkAdminValidImg();//验证验证码
		String msg=checkAmountApply(apply,status);
		if(!StringUtils.isBlank(msg)){
			return ADMINMSG;
		}
		//将积分转换成等级
		Credit c = new Credit();
		c.setValue((int)account);
		CreditRank creditRank =  c.findCreditRank();
		StringBuffer str = new StringBuffer();
		str.append("当前通过额度：").append(creditRank.getName().toUpperCase()).append("级别").append(",").append("审核备注：").append(verifyRemark);
		Rule rule = ruleService.getRuleByNid("verify_rule");//审核规则
		if(rule != null ){
			if(rule.getStatus() ==1){//是否启用
				int is_rule = rule.getValueIntByKey("verify_amount");//复审规则是否启用
				boolean is_over = false;
				if(is_rule==1){
					is_over = operateService.doOperate(Constant.VERIFY_AMOUNT, apply.getId(),this.getAuthUser() , str.toString(), status);
					if (status == 1) {   //如果此步审核通过，更新当前步骤通过的额度
						userAmountService.updateAmountApply(apply);
					}

				}
				if(!is_over){
					message("审核成功","/admin/borrow/amountApply.html");
					return ADMINMSG;
				}
			}
		}
		double accountPass=NumberUtils.getDouble(request.getParameter("accountPass"));
		UserAmount ua = userAmountService.getUserAmount(apply.getUser().getUserId());

		if(ua==null){
			apply.setAccountOld(0);
		}else{
			apply.setAccountOld(ua.getCredit());
		}

		if(status==2){
			apply.setStatus(-1);
			apply.setAccountNew(apply.getAccountOld());
			message("审核不通过！");
		}else if(status==1){
			if(accountPass>account){
				message("通过额度不能大于申请额度！");
				return ADMINMSG;
			}
			if(account<=0){
				message("申请额度不能小于0！");
				return ADMINMSG;
			}
			if (giveCredit <0) {
				message("赠送奖励不能为负数！");
				return ADMINMSG;
			}
			apply.setStatus(1);			
			apply.setAccountNew(apply.getAccountOld()+accountPass);
			message("审核通过！","/admin/borrow/amountApply.html");
			User auth_user = getAuthUser();
			super.systemLogAdd(auth_user, 45, "管理员审批信用额度成功,额度:"+apply.getAccount());
		}else{
			message("非法操作！");
			return ADMINMSG;
		}

		apply.setVerifyRemark(verifyRemark);
		apply.setVerifyTime(new Date());
		apply.setVerifyUser(new User(Constant.ADMIN_ID));
		UserAmountLog log=new UserAmountLog();
		log.setAddtime(new Date());
		log.setAddip(this.getRequestIp());
		log.setUser(apply.getUser());


		userAmountService.verifyAmountApply(apply, log,(int)giveCredit);

		return ADMINMSG;
	}

	/**
	 * 推荐标设置和取消 
	 * recommendStatus 1:是 ；0：否。
	 * type: cancel:取消推荐标；recommend:设置成推荐标
	 * */
	@Action(value="recommend")
	public String recommend() throws Exception {
		String type = paramString("type");
		int borrowId= paramInt("id");
		int page = paramInt("page");
		if(borrowId!=0) {
			Borrow recommendBorrow = borrowService.getBorrow(borrowId);
			if(recommendBorrow == null){
				throw new ManageBussinessException("该标不存在！");
			}
			if("cancel".equals(type)) {//取消推荐
				recommendBorrow.setRecommendStatus(0);
				message("推荐标取消成功！","/admin/borrow/showBorrowing.html");
			}else if("recommend".equals(type)) {//设置为推荐
				recommendBorrow.setRecommendStatus(1);
				message("推荐标设置成功！","/admin/borrow/showBorrowing.html");
			}else{//传参错误提醒
				throw new ManageBussinessException("操作类型不合法！");
			}
			borrowService.updateBorrow(recommendBorrow);			
			return ADMINMSG;
		}
		SearchParam param=SearchParam.getInstance().addParam("status", 1).addPage(paramInt("page"));
		findAndSave(param);
		request.setAttribute("showRecommend", 1);
		return "allborrow";
	}

	private String checkAmountApply(UserAmountApply apply,int status){
		if(status!=0&&status!=1&status!=2){
			message("请您选择操作类型", "/admin/borrow/viewFullBorrow.html?id=" + apply.getId());
			return ADMINMSG;
		}else if(apply==null){
			message("非法操作！", "/admin/borrow/viewFullBorrow.html?id=" + apply.getId());
			return ADMINMSG;
		}else if(apply.getStatus()==1){
			message("已经审核通过！","/admin/borrow/viewFullBorrow.html?id=" + apply.getId());
			return ADMINMSG;
		}
		return "";
	}

	@Action(value="schedule",
			results={@Result(name="yjf",type="ftl",location="/admin/borrow/schedule.html"),
			@Result(name="huifu",type="ftl",location="/admin/borrow/hfschedule.html"),
			@Result(name="mmm",type="ftl",location="/admin/borrow/mmmschedule.html")})
	public String schedule() throws Exception {
		//页面参数
		int apiType = Global.getInt("api_code");//获取接口类型
		int page = paramInt("page");
		int pernum = paramInt("pernum")==0?2*Page.ROWS:paramInt("pernum");
		String borrowid = paramString("borrowid");
		String period = paramString("period");
		String orderno = paramString("orderno");
		String status = paramString("status");
		String operatetype = paramString("operatetype");
		String service = paramString("service");
		String ids = request.getParameter("ids");
		String[] values = null;
		if(!StringUtils.isBlank(ids)){			
			values = ids.substring(0, ids.length()-1).split(",");
			List<Object> taskList = new ArrayList<Object>();
			logger.info(" 重复处理ids : " + ids);
			for(int i=0;i<values.length;i++){
				int id = NumberUtils.getInt(values[i].trim());
				logger.info("当前处理" + id);
				if(id!=0){
					Object apiPay = findApy(id);
					taskList.add(apiPay);
				}					
			}
			borrowService.doApiTask(taskList);
		}
		//查询
		SearchParam sp = SearchParam.getInstance();
		sp.addPage(page, pernum);

		String msg = findApiAllList(sp, borrowid, period, orderno, status, operatetype, service,apiType);
		if(!StringUtils.isBlank(msg)){
			return msg;
		}else{
			message("系统异常！");
			return MSG;
		}
	}

	/**
	 * 获取重新处理第三方对象的方法
	 * @param id
	 * @return
	 */
	private Object findApy(int id){
		int result = Global.getInt("api_code");//获取接口类型
		switch (result) {
		//cancel by lxm 2017-2-13 17:05:36
//		case 1: //汇付
//			return borrowService.getChinapnrPayById(id);
//		case 2:  //易极付
//			return borrowService.getYjfPayById(id);
//			//v1.8.0.4  TGPROJECT-40   qj  2014-04-10 start
		case 3:  //双乾
			return borrowService.getMmmPayById(id);
		default:
			return null;
		}
	}

	/**
	 * 查询第三方接口处理结果的共用方法
	 * @param sp
	 * @param borrowId
	 * @param period
	 * @param orderno
	 * @param status
	 * @param operatetype
	 * @param service
	 */
	private String findApiAllList(SearchParam sp,String borrowId,String period,String orderno,
			String status,String operatetype,String service ,int apiType){
		PageDataList pl =null;
		switch (apiType) {
		case 1:
			borrowId = paramString("borrowId");//
			if(!StringUtils.isBlank(borrowId)) {
				sp.addParam("borrowId", borrowId);
			}

			if(!StringUtils.isBlank(orderno)) {
				sp.addParam("ordId", orderno);
			}
			if(!StringUtils.isBlank(status)) {
				sp.addParam("status", status);
			}
			if(!StringUtils.isBlank(operatetype)) {
				sp.addParam("operateType", operatetype);		
			}
			if (!StringUtils.isBlank(service)) {
				sp.addParam("cmdid", service);
			}
			pl = borrowService.getChinapnrList(sp);
			setPageAttribute(pl, sp);
			return "huifu";
		case 2://易极付
			if(!StringUtils.isBlank(borrowId)) {
				sp.addParam("borrowid", borrowId);
			}
			if(!StringUtils.isBlank(period)) {
				sp.addParam("period", period);		
			}
			if(!StringUtils.isBlank(orderno)) {
				sp.addParam("orderno", orderno);
			}
			if(!StringUtils.isBlank(status)) {
				sp.addParam("status", status);
			}
			if(!StringUtils.isBlank(operatetype)) {
				sp.addParam("operatetype", operatetype);		
			}
			if(!StringUtils.isBlank(service)) {
				sp.addParam("service", service);
			}
			pl= borrowService.getYjfPayList(sp);
			setPageAttribute(pl, sp);
			return "yjf";
			//v1.8.0.4  TGPROJECT-40   qj  2014-04-10 start
		case 3://双乾
			borrowId = paramString("borrowId");//
			if(!StringUtils.isBlank(borrowId)) {
				sp.addParam("borrowId", borrowId);
			}
			if(!StringUtils.isBlank(orderno)) {
				sp.addParam("orderNo", orderno);
			}
			if(!StringUtils.isBlank(status)) {
				sp.addParam("status", status);
			}
			if(!StringUtils.isBlank(operatetype)) {
				sp.addParam("operateType", operatetype);		
			}
			if(!StringUtils.isBlank(service)) {
				sp.addParam("type", service);
			}
			pl= borrowService.getMmmPayList(sp);
			setPageAttribute(pl, sp);
			return "mmm";
			//v1.8.0.4  TGPROJECT-40   qj  2014-04-10 start
		default:
			return "";
		}

	}

	@Action(value="late",
			results={@Result(name="late",type="ftl",location="/admin/borrow/late.html")})
	public String late() throws Exception{
		String keywords=StringUtils.isNull(request.getParameter("keywords"));
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page")).addOrder(OrderType.ASC,"repaymentTime")
				.addOrFilter("status", 0,2);
		if(org.apache.commons.lang3.StringUtils.isNotBlank(keywords)){
			param.addParam("borrow.name", Operator.LIKE , keywords);
		}
		if(!StringUtils.isBlank(startTime)){
			param.addParam("repaymentTime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("repaymentTime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		param.addParam("lateDays",Operator.GTE, 1);//查询逾期的标
		PageDataList plist = this.borrowService.getBorrowRepaymentList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
		extraparam.put("dotime1", startTime);
		extraparam.put("dotime2", endTime);
		setPageAttribute(plist, param,extraparam);
		setPageUrl();
		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "late";
		}else{
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="逾期_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","borrow/.user/.username","borrow/.name",
					"repaymentTime","repaymentAllAccount","capital","interest","lateDays","lateInterest","statusStr"};
			String[] titles=new String[]{"ID","用户名","标名","还款时间","还款金额","还款本金","还款利息","逾期天数","逾期罚息","状态"};
			//v1.8.0.4 TGPROJECT-146 2014-04-21 start
			List rlist=borrowService.getBorrowRepaymentExportList(param);
			//v1.8.0.4 TGPROJECT-146 2014-04-21 end
			ExcelHelper.writeExcel(infile, rlist, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}

	}

	@Action(value="showDetailWebSitePay",
			results={@Result(name="success",type="ftl",location="/admin/borrow/showDetailWebSitePay.html")})
	public String showDetailWebSitePay(){
		String repayMentIdStr = paramString("repayMentId");
		if(StringUtils.isBlank(repayMentIdStr)){
			message("参数错误", "/admin/borrow/late.html");
			return MSG;
		}
		long repayMentId = Long.parseLong(repayMentIdStr);
		BorrowRepayment br = borrowService.getBorrowRepaymentById(repayMentId);
		Borrow b = br.getBorrow();
		request.setAttribute("b", b);
		request.setAttribute("br", br);
		List<OperateLog> operateLoglist=operateService.getOperateLogByOrdNo(br.getId(),Constant.WEB_SITE_PAY_FLOW);
		request.setAttribute("operateLoglist", operateLoglist);
		OperateProgress op = operateService.getOperateProgressByTypeAndOrderNo(Constant.WEB_SITE_PAY_FLOW, repayMentId);
		request.setAttribute("op", op);
		return SUCCESS;
	}

	/**
	 * 网站垫付
	 * @return
	 */
	@Action(value="viewWebSitePay",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewWebSitePay.html")})
	public String viewWebSitePay(){
		String repayMentIdStr = paramString("repayMentId");
		if(StringUtils.isBlank(repayMentIdStr)){
			message("参数错误", "/admin/borrow/late.html");
			return MSG;
		}
		long repayMentId = Long.parseLong(repayMentIdStr);
		BorrowRepayment br = borrowService.getBorrowRepaymentById(repayMentId);
		request.setAttribute("br", br);
		return SUCCESS;
	}
	//v1.8.0.4_u1 TGPROJECT-379  wsl 2014-08-01  start 信合行逾期罚息修改功能
	/**
	 * 修改逾期标信息
	 * @return
	 * @throws Exception
	 */
	@Action(value="updateLateBorrow")
	public String updateLateBorrow() throws Exception {
		checkAdminValidImg();//验证验证码
		String repayMentIdStr = paramString("repayMentId");
		long repayMentId = Long.parseLong(repayMentIdStr);
		BorrowRepayment br = borrowService.getBorrowRepaymentById(repayMentId);
		if(StringUtils.isBlank(repayMentIdStr)){
			message("参数错误", "/admin/borrow/late.html");
			return MSG;
		}
		if(br.getBorrow()==null){
			throw new BussinessException("对应的标不存在");
		}
		double lateInterest = paramDouble("lateInterest");
		if(lateInterest<0){
			message("逾期利息不能小于0");
			return ADMINMSG;
		}else{
			br.setLateInterest(lateInterest);
		}
		borrowService.updateBorrowRepayment(br);
		logger.info("修改逾期标信息成功");
		message("修改逾期标信息成功","/admin/borrow/late.html");
		return ADMINMSG;
	}
	//v1.8.0.4_u1 TGPROJECT-379  wsl 2014-08-01  end
	/**
	 * 网站垫付
	 * @return
	 */
	@Action("websitepayborrow")
	public String websitepayborrow(){
		String message = null;
		String memo = paramString("verifyRemark");
		String statusStr = paramString("status");
		if(StringUtils.isBlank(paramString("repayMentId"))||StringUtils.isBlank(memo)){
			this.message("参数错误，联系开发人员","/admin/borrow/late.html");
			return ADMINMSG;
		}
		long repayMentId = paramLong("repayMentId");

		checkAdminValidImgWithUrl("/admin/borrow/late.html"); //校验验证码
		User user = getAuthUser();
		user= userService.getUserById(user.getUserId());
		if(user == null){
			throw new ManageBussinessException("您还没登陆，请您登陆！！！");
		}
		Rule rule = ruleService.getRuleByNid("verify_rule");
		if(rule!=null && rule.getStatus() == 1){//有此规则，并且启用
			if("1".equals(rule.getValueStrByKey("verify_websitepay"))){
				if(StringUtils.isBlank(statusStr)){
					this.message("参数错误，联系开发人员","/admin/borrow/late.html");
					return ADMINMSG;
				}
				int status = Integer.parseInt(statusStr);
				if(status == 1){  //垫付通过，垫付类型必须选择
					int type = paramInt("type");//垫付类型  1：垫付本期   2：垫付所有
					if(type == 0){
						message("请您选择垫付类型","/admin/borrow/showDetailWebSitePay.html?repayMentId="+repayMentId);
						return ADMINMSG;
					}
					Global.paramLocal.set(type);
				}
				boolean isOver =  operateService.doOperate(Constant.WEB_SITE_PAY_FLOW,repayMentId, user, memo, status);
				if(!isOver){//流程未走完，需要继续操作
					message("网站垫付审核流程成功！！","/admin/borrow/showDetailWebSitePay.html?repayMentId="+repayMentId);
					return ADMINMSG;
				}
			}
		}

		//异步处理 生成本次操作标记
		BorrowParam param = new BorrowParam();
		String resultFlag = this.getAuthUser().getUserId() + "-" + System.currentTimeMillis()+"";
		param.setResultFlag(resultFlag);
		int type = paramInt("type");//垫付类型  1：垫付本期   2：垫付所有
		if(type == 0 ){
			message("垫付失败，垫付类型错误！","/admin/borrow/showDetailWebSitePay.html?repayMentId="+repayMentId);
			return ADMINMSG;
		}
		param.setType(type);
		try {
			BorrowRepayment  br  = borrowService.getBorrowRepaymentById(repayMentId);
			Borrow model = br.getBorrow();
			List<BorrowTender> tenders = model.getBorrowTenders();
			for (BorrowTender bt : tenders) {
				if(bt.getStatus() == -1){//债权转让中，要取消掉
					Borrow assignBorrow =  borrowService.getAssignMentBorrowByTenderId(bt.getId());
					assignBorrow.setStatus(5);
					assignBorrow.setVerifyRemark("网站垫付债权转让失败，出借撤回");
					DisruptorUtils.failBorrow(BorrowHelper.getHelper(assignBorrow), null);
				}
			}
			DisruptorUtils.webSitePayForLateBorrow(repayMentId, param);
			message="垫付成功";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			message="垫付失败";
		}
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/admin/borrow/late.html"); //成功返回地址
		request.setAttribute("back_url", "/admin/borrow/late.html");//失败返回地址
		request.setAttribute("r_msg","恭喜您，网站垫付成功，请您返回查看");

		return ADMINRESULT;
	}

	//截标前展示
	@Action(value="viewEndBorrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewEndBorrow.html")})
	public String viewEndBorrow(){
		long borrowId = paramLong("borrowid");
		Borrow borrow = borrowService.getBorrow(borrowId);
		double repayAccount = borrowService.doBorrowFull(borrow.getId());
		request.setAttribute("borrow", borrow);
		request.setAttribute("repayaccount", repayAccount);
		return "success";
	}

	//管理员进行截标操作,将原始借款金额保存到viewmoney中，将实际投标金额同步到account中,债权转让标不能进行截标操作
	@Action("endborrow")
	public String endborrow(){
		String validCode = paramString("validCode");
		checkAdminValidImg();
		Borrow borrow = borrowService.getBorrow(paramLong("id"));

		double endBorrowApr=NumberUtils.getDouble(Global.getValue("endborrow_apr")); //管理员截标限制，按投标的进度控制截标、
		if (borrow.getAccountYes()/borrow.getAccount()<endBorrowApr) {
			this.message("截标金额小于网站规定的最低限制！");
			return ADMINMSG;
		}
		//流转标和秒标债权转让标不准截标
		
		if (borrow.getType() == 101 || borrow.getType() ==110 ) {
			message("该类型的标不允许截标操作！");
			return ADMINMSG;
			
		}
		
		if (borrow.getIsAssignment() ==1 ) {
			message("债权转让标不允许截标操作！");
			return ADMINMSG;
		}
		borrow.setAccount(borrow.getAccountYes());
		//更新标的应还总额
		double repayAccount = borrowService.doBorrowFull(borrow.getId());
		borrow.setRepaymentAccount(repayAccount);
		borrowService.doEndBorrow(borrow);
		message("截标成功！","/admin/borrow/showReviewBorrow.html");
		super.systemLogAdd(getAuthUser(), 47, "管理员结标成功");
		return ADMINMSG;
	}

	@Action(value="showFlowRule",
			results={@Result(name="showFlow",type="ftl",location="/admin/borrow/addoperation.html")}
			)
	public String showFlowRule(){
		List<UserType> userTypeList = userService.getAllUserType();
		request.setAttribute("userTypeList", userTypeList); //获取所有职位信息
		long id = paramLong("id");
		if(id!=0){
			OperateFlow oper = operateService.getOperateById(id);
			request.setAttribute("oper", oper);
		}
		return "showFlow";
	}

	@Action(value="allFlowRule",
			results={@Result(name="success",type="ftl",location="/admin/borrow/allFlowRule.html")
	})
	public String allFlowRule(){
		String operateType = paramString("operateType");
		if(!StringUtils.isBlank(operateType)){
			List<OperateFlow> operList = operateService.getOperateFlowByType(operateType);
			request.setAttribute("list", operList);
			return SUCCESS;
		}
		return SUCCESS;
	}

	@Action(value="addOperationFlow")
	public String addOperationFlow(){
		long id = paramLong("id"); 
		String type = paramString("type");
		String flow_name = paramString("flow_name");
		int sort = paramInt("sort");
		int status = paramInt("status");
		long user_type_id = paramLong("user_type_id");
		String validCode = paramString("validCode");
		checkAdminValidImg();
		if(StringUtils.isBlank(type) || StringUtils.isBlank(flow_name) || sort <1 ||
				user_type_id <1 || StringUtils.isBlank(validCode) || !(status==1||status==2)){
			throw new ManageBussinessException("参数错误","/admin/borrow/showFlowRule.html");
		}
		User user =  getAuthUser();
		user = userService.getUserById(user.getUserId());
		if(user == null){
			throw new ManageBussinessException("请您登陆操作");
		}
		int sortRult = operateService.getMaxAllOperateSort(type);  //查询出operateflow中最后一个流程的编号
		List<OperateFlow> operateList = operateService.getOperateFlowByType(type);   //获取当前流程已经存在的环境
		if(id==0){
			if (sort - sortRult !=1 ) {   //新增流程时校验当前的输入的sort和当前流程中最后一个sort做比较
				throw new ManageBussinessException("你输入的流程顺序有误，请核对后再做操作！");
			}
			//校验当前环节的审核专员类型是否在这个审核流程中已经存在审核工作
			for (OperateFlow operateFlow : operateList) {
				if (operateFlow.getUserType().getTypeId() == user_type_id) {
					throw new ManageBussinessException("一个审核环节中，操作用户类型不能重复添加！");
				}
			}
			OperateFlow of = new OperateFlow();
			of.setName(flow_name);
			of.setOperateType(type);
			of.setSort(sort);
			of.setUserType(new UserType(user_type_id));
			of.setOprateUser(user);
			of.setAddtime(new Date());
			of.setStatus(status);
			operateService.addOperateFlow(of);
			message("新增成功","/admin/borrow/showFlowRule.html");
		}else{
			//修改审核流程时，做修改前的校验
			OperateFlow oper = operateService.getOperateById(id);

			//校验此修改流程是否有正在审核的步骤，如果有的话，不允许用户修改
			int waitOperate = operateService.getCountOperateByTypeAndStatsu(oper.getOperateType(), 0); //获取此流程是否有正在审核的操作
			if (waitOperate > 0) {
				throw new ManageBussinessException("你修改的审核流程有正在审核的操作，不能修改此审核流程，待此流程所有操作都处理完毕以后，再进行修改！");
			}
			//v1.8.0.4 TGPROJECT-59 lx 2014-04-17 start
			/*if (sort != oper.getSort() ) {   //校验当前的修改的操作流程是否正确
				throw new ManageBussinessException("操作流程顺序不能做修改！");
			}*/
			if (!type.equals(oper.getOperateType())) {  //校验操作类型是否被修改
				throw new ManageBussinessException("操作类型不能做修改！");
			}
			/*for (OperateFlow operateFlow : operateList) {   //校验修改时，是否重复选择了审核职位 ,排序当前修改对象中所持有的usertype
				if (operateFlow.getUserType().getTypeId() == user_type_id && operateFlow.getId() != id) {
					throw new ManageBussinessException("当前你选择的审核职位在此审核环节中已经分配了审核操作，请分配给其他职位！");
				}
			}*/
			OperateFlow goalOper = operateService.getOperateFlowBytypeSort(type, sort);
			if(goalOper==null){
				throw new ManageBussinessException("输入的流程顺序有误，请输入存在的流程顺序！");
			}
			goalOper.setSort(oper.getSort());
			oper.setName(flow_name);
			oper.setOperateType(type);
			oper.setSort(sort);
			oper.setUserType(new UserType(user_type_id));
			oper.setOprateUser(user);
			oper.setAddtime(new Date());
			oper.setStatus(status);
			List<OperateFlow> operateFlowList=new ArrayList<OperateFlow>();
			operateFlowList.add(oper);
			operateFlowList.add(goalOper);
			operateService.updateOperateFlow(operateFlowList);
			message("修改成功","/admin/borrow/allFlowRule.html");
		}
		return ADMINMSG;
	}

	/**
	 * 撤标时展示标的信息
	 * @return
	 */
	@Action(value="viewCancelborrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/verifyCancelBorrow.html")
	})
	public String viewCloseborrow(){
		Borrow b = borrowService.getBorrow(paramLong("id"));
		List<OperateLog> operList = operateService.getOperateLogByOrdNo(b.getId(), Constant.CANCEL_BORROE);
		request.setAttribute("b", b);
		request.setAttribute("operateLoglist", operList);
		return "success";
	}

	/*
	 * 审核日志查询
	 */
	@Action(value="showOperateLog",
			results={@Result(name="success",type="ftl",location="/admin/borrow/operateLog.html")})
	public String showOperateLog(){
		Map<String, Object> map = new HashMap<String, Object>();
		//用户名
		String username = paramString("username");	
		String realname = paramString("realname");
		//类型

		String dotime1 = paramString("dotime1");
		String dotime2 = paramString("dotime2");

		String type = paramString("operateType")==""?paramString("type"):paramString("operateType");

		String orderId = paramString("orderNo");  //获取根据审核项id搜索时的id
		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page"));
		String search = request.getParameter("search");

		if(!StringUtils.isBlank(username)) {
			param.addParam("operateUser.username", Operator.LIKE, username);
		}

		if(!StringUtils.isBlank(realname)) {
			param.addParam("operateUser.realname", Operator.LIKE, realname);
		}

		if(!StringUtils.isBlank(type)) {
			param.addParam("type",Operator.LIKE, type);
		}
		if(!StringUtils.isBlank(dotime1)){
			param.addParam("addtime", Operator.GTE, DateUtils.getDate(dotime1, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(dotime2)){
			param.addParam("addtime", Operator.LTE, DateUtils.getDate(dotime2, "yyyy-MM-dd HH:mm:ss"));
		}

		if(!StringUtils.isBlank(orderId)){
			param.addParam("orderNo", Operator.EQ, NumberUtils.getLong(orderId));
		}

		PageDataList<OperateLog> List = operateLogDao.findPageList(param);
		Map<String, Object> extraparam = new HashMap<String, Object>();
		extraparam.put("dotime1", dotime1);
		extraparam.put("dotime2", dotime2);
		setPageAttribute(List, param, extraparam); 
		setPageUrl();
		return SUCCESS;
	}

	@Action(value="showAllNowOperate",
			results={@Result(name="success",type="ftl",location="/admin/borrow/nowOperate.html")})
	public String showAllNowOperate(){
		User user = getAuthUser();
		SearchParam param = SearchParam.getInstance().addParam("userType", user.getUserType());
		List<OperateFlow> flowList = operateService.getOperateFlowList(param);
		List<OperateProgress> opprList = new ArrayList<OperateProgress>(); 
		for (OperateFlow operateFlow : flowList) {
			param = SearchParam.getInstance().addParam("nextOperateFlow",operateFlow).addParam("status", "0");
			List<OperateProgress> olist = operateService.getOperateProgressList(param); 
			opprList.addAll(olist);
		}
		request.setAttribute("opprList", opprList);

		return SUCCESS;
	}

	@Action(value="viewMemo",
			results={@Result(name="success",type="ftl",location="/admin/borrow/viewMemo.html")})
	public String viewMemo(){
		long id = paramLong("id");

		OperateLog log = operateLogDao.find(id);
		request.setAttribute("msg", log.getMemo());
		return SUCCESS;
	}

	/**
	 * 担保机构管理（添加和修改）
	 * @return
	 */
	@Action(value="addWarrant",
			results={@Result(name="addshow",type="ftl",location="/admin/borrow/addWarrant.html")})
	public String addWarrant(){
		String addType = paramString("addType");
		if ("show".equals(addType)) { //判断否跳转到添加页面
			return "addshow";
		}
		long id = paramLong("warrant_id");
		String name = paramString("warrant_name");
		String area = paramString("warrant_area");
		String capital = paramString("warrant_capital");
		String addtime = paramString("warrant_addtime");
		String relationUser = paramString("warrant_relationUser");
		String phone = paramString("warrant_phone");
		String address = paramString("warrant_address");
		//v1.8.0.4 TGPROJECT-72 lx 2014-04-16 start
		String summary = paramString("content");
		//v1.8.0.4 TGPROJECT-72 lx 2014-04-16 end
		int status = paramInt("status");
		if (id>0) {  //执行更新
			Warrant warrant = warrantService.findWarrant(id);
			warrant.setAddress(address);
			//v1.8.0.4_u1 TGPROJECT-385 wsl start
			warrant.setAddtime(DateUtils.getDate2(addtime));
			//v1.8.0.4_u1 TGPROJECT-385 wsl end
			warrant.setArea(area);
			warrant.setCapital(capital);
			warrant.setRelationUser(relationUser);
			warrant.setPhone(phone);
			warrant.setSummary(summary);
			warrant.setName(name);
			warrant.setStatus(status);
			warrantService.updateWarrant(warrant);
			message("修改担保机构成功！", "/admin/borrow/showAllWarrant.html", "查看所有担保机构");
		}else{//新增
			Warrant warrant = new Warrant();
			warrant.setAddress(address);
			//v1.8.0.4_u1 TGPROJECT-385 wsl start
			warrant.setAddtime(DateUtils.getDate2(addtime));
			//v1.8.0.4_u1 TGPROJECT-385 wsl end
			warrant.setArea(area);
			warrant.setCapital(capital);
			warrant.setRelationUser(relationUser);
			warrant.setPhone(phone);
			warrant.setSummary(summary);
			warrant.setName(name);
			warrant.setStatus(status);
			warrantService.addWarrant(warrant);
			message("新增担保机构成功！", "/admin/borrow/showAllWarrant.html", "查看所有担保机构");
		}
		return ADMINMSG;
	}

	//v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 start 
	@Action(value="showAllBorrowIntent",
			results={@Result(name="success",type="ftl",location="/admin/borrow/showAllBorrowIntent.html")})
	public String showAllBorrowIntent(){
		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page")).addOrder(OrderType.DESC, "id");
		PageDataList<BorrowIntent>  borrowIntentList = borrowService.findListBorrowIntent(param);
		setPageAttribute(borrowIntentList, param); 
		return "success";
	}


	@Action(value="addborrow",
			results={@Result(name="success",type="ftl",location="/admin/borrow/addborrow.html"),
			@Result(name="ispAddborrow",type="ftl",location="/member/ips/ipscommit.html")}
			)
	public String addBorrow(){
		String type =paramString("addtype");
		if(!StringUtils.isBlank(type)){
			checkAdminValidImg();//验证验证码
			User user=userService.getUserByName(paramString("username"));
			if(user==null){
				throw new BussinessException("借款人输入有误！");
			}
			BorrowModel model=null;
			borrow.setUser(user);
			model=BorrowHelper.getHelper(borrow);
			model.checkModelData(); //校验发标合法性
			fillBorrow(model,borrow);
			model.setViewMoney(model.getAccount());    //发标时将viewmoney默认为发标金额
			AccountLog log=new AccountLog(user.getUserId(),Constant.FREEZE,user.getUserId(),"",this.getRequestIp());

			//关于借款申请方法修改
			BorrowProperty property = madNewBorrowProperty(null);
			borrowService.addBorrow(model,log,property,null);

			logger.info("后台发标成功");
			message("后台发标成功","/admin/borrow/showTrialBorrow.html");
	
			return ADMINMSG;
		}
		//跳转后台发标页面
		return SUCCESS;
	}
	
	/**
	 * 发标，校验是否需要跳转第三方
	 * @param borrow
	 * @return
	 */
	private String doApiAddborrow(Borrow borrow){
		
		int apiCode = Global.getInt("api_code");  
		switch (apiCode) {
		case 4:
			IpsAddBorrow  ipsBorrow = IpsHelper.doCreateIpsAddBorrow(borrow, borrow.getUser());
			request.setAttribute("ips", ipsBorrow);
			return "ispAddborrow";

		default:
			return ADMINMSG;
		}
		
	}

	@Action(value="handleBorrowIntent",
			results={@Result(name="success",type="redirect",location="/admin/borrow/showAllBorrowIntent.html")})
	public String handleBorrowIntent(){
		long id=paramLong("id");
		BorrowIntent bi=borrowService.getBorrowIntent(id);
		if(bi!=null ){
			bi.setStatus(1);
			//v1.8.0.3_u3 XINHEHANG-66  qinjun  2014-06-20  start 
			bi.setMemo(paramString("memo"));;
			bi.setUpdatetime(new Date());
			//v1.8.0.3_u3 XINHEHANG-66  qinjun  2014-06-20  end
			borrowService.updateBorrowIntent(bi);
			return SUCCESS;
		}else{
			message("处理借款请求失败","/admin/borrow/showAllBorrowIntent.html");
			return ADMINMSG;
		}
	}

	//v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 end 

	@Action(value="showAllWarrant",
			results={@Result(name="success",type="ftl",location="/admin/borrow/showAllWarrant.html")})
	public String showAllWarrant(){
		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page"));
		PageDataList<Warrant>  warrantList = warrantService.findListWarrant(param);
		setPageAttribute(warrantList, param); 
		return "success";
	}


	@Action(value="showWarrant",
			results={@Result(name="success",type="ftl",location="/admin/borrow/addWarrant.html")})
	public String showWarrant(){
		long warrantId = paramLong("warrantId");
		Warrant warrant = warrantService.findWarrant(warrantId);
		if (warrant ==null) {
			message("查询异常，请重试！");
			return ADMINMSG;
		}
		request.setAttribute("warrant", warrant);
		return "success";
	}

	@Action(value="mmmLoanQuery")
	public String mmmLoanQuery(){
		String borrowId = paramString("borrowId");
		try {
			List<MmmLoanOrderQuery> queryList = MmmHelper.loanOrderQueryByBorrowId(borrowId,"");
			request.setAttribute("queryList", queryList);
			message("查询成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "MSG";
	}
	//v1.8.0.4 TGPROJECT-140 lx start
	/**
	 * 存储图片
	 * @param scrollPic
	 */
	private void moveFile(Borrow borrow) {
		String dataPath = ServletActionContext.getServletContext().getRealPath("/data");
		String contextPath = ServletActionContext.getServletContext().getRealPath("/");
		Date d1 = new Date();
		String upfiesDir = dataPath + sep + "promise" + sep ;
		String destfilename1 = upfiesDir  + borrow.getId() + "_promise" + "_"
				+ d1.getTime() + ".jpg";
		filePath = destfilename1;
		filePath = this.truncatUrl(filePath, contextPath);
		logger.info(destfilename1);
		File imageFile1 = null;
		try {
			imageFile1 = new File(destfilename1);
			FileUtils.copyFile(files, imageFile1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}
	/**
	 * 截取url
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
	//v1.8.0.4 TGPROJECT-140 lx end

	//v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  start
	@Action("showBorrowDetailType")
	public String showBorrowDetailType() throws Exception {
		long pid = paramLong("pid");
		List<BorrowDetailType> areas = borrowService.getBorrowDetailTypeListByPid(pid);		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}
	//v1.8.0.3_u3 TGPROJECT-335  qinjun 2014-06-16  end

	//v1.8.0.3_u3 TGPROJECT-335  wujing 2014-06-16  start 
	/**
	 * @param file 
	 * @param fileName 上传时输入的文件显示
	 * @return
	 * @throws IOException 
	 */
	private List<BorrowDetail> getBorrowDetailList(File[] fileImages,Borrow borrow) throws IOException{
		//文件获取
		String[] fileName = request.getParameterValues("fileViewName");
		String[] filePartName = request.getParameterValues("filePartName");
		List<BorrowDetail> borrowDetailList =  new ArrayList<BorrowDetail>();
		BorrowDetail borrowDetail ;
		if(fileImages !=null){
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
				borrowDetail.setName(StringUtils.isBlank(fileName[i])?"":fileName[i]);
				borrowDetail.setTypeName(new BorrowDetailType(3));
				borrowDetail.setPartName(new BorrowDetailType(NumberUtils.getLong(filePartName[i])));
				borrowDetail.setAddip(getRequestIp());
				borrowDetailList.add(borrowDetail);
			}
		}		
		//标的附加信息
		String[] detailPartName = request.getParameterValues("detailPartName");
		String[] detailContent = request.getParameterValues("detailContent");
		if(detailPartName!=null){
			for (int i = 0; i < detailPartName.length; i++) {
				if(StringUtils.isBlank(detailPartName[i])|| detailPartName[i].equals("0") || StringUtils.isBlank(detailContent[i])){
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
		//风控信息
		String danbaoName = paramString("danbaoName");
		String danbaoMemo = paramString("danbaoMemo");
		String danbaoContent = paramString("danbaoContent");
		if(!StringUtils.isBlank(danbaoContent)){
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
	 * @param file
	 * @throws IOException 
	 */
	public String upload(File file,Borrow borrow) {
		String url ="";
		String destfilename2=ServletActionContext.getServletContext().getRealPath( "/data" ) +sep+"temp"+sep+borrow.getId()+"_"+DateUtils.getTimeStr(new Date())+".jpg";
		String contextPath = ServletActionContext.getServletContext()
				.getRealPath("/");
		url = destfilename2;
		logger.info(destfilename2);
		File imageFile2=null;
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
	//v1.8.0.3_u3 TGPROJECT-335  wujing 2014-06-16  end

	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 start
	@Action("checkPayUserJson")
	public String checkPayUserJson(){
		boolean isSuccess = true;
		String msg = "";
		String username = paramString("username");
		if(StringUtils.isBlank(username)){
			isSuccess = false;
			msg = "请输入到账用户！";
		}else{
			User user = userService.getUserByName(username);
			if(user != null){
				if(StringUtils.isBlank(user.getApiId()) || user.getApiStatus() != 1){
					isSuccess = false;
					msg = "用户未开通"+Global.getString("api_name")+"账户！";
				}
			}else{
				isSuccess = false;
				msg = "用户不存在！";
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", isSuccess+"");
		map.put("msg", msg);
		printJson(JSON.toJSONString(map));
		return null;
	}
	//v1.8.0.4_u4  TGPROJECT-346  qinjun 2014-06-30 end

	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing start

	/**
	 * 获取申请提前还款列表
	 * @return
	 */
	@Action(value="getApplyPriorRepayList",
			results={@Result(name="success",type="ftl",location="/admin/borrow/ApplyPriorRepayList.html")})
	public String getApplyPriorRepayList(){
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));
		int priorStatus = paramInt("priorStatus");
		if (priorStatus ==-2) {  //获取全部的提前还款申请：申请中，通过，未通过
			param.addOrFilter("priorStatus", 1,2,-1);
		}else{
			param.addParam("priorStatus", priorStatus);
		}
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("ispriors", priorStatus);
		PageDataList<Borrow> pageBorrow = borrowService.getList(param);
		setPageAttribute(pageBorrow, param,maps);
		return "success";
	}

	/**
	 * 跳转审核提前还款页面
	 * @return
	 */
	@Action(value="verifyApplyPriorRepayView",
			results={@Result(name="success",type="ftl",location="/admin/borrow/ApplyPriorRepayView.html")})
	public String verifyApplyPriorRepayView(){
		long borrowId = paramLong("borrowId");
		Borrow borrow = borrowService.getBorrow(borrowId);
		if (null ==borrow) {
			message("获取数据异常，请联系维护人员！");
			return ADMINMSG;
		}
		List<BorrowRepayment> repayMentList = repaymentService.getRepaymentListByBorrow(borrowId);
		//计算待还本金，利息，罚息
		Map<String, Double> dataResult = repaymentService.getLZFPriorMoney(borrow);
		request.setAttribute("repayMentList", repayMentList);
		request.setAttribute("b", borrow);
		request.setAttribute("dataResult", dataResult);
		return "success";

	}

	@Action("verifyApplyPriorRepay")
	public String verifyApplyPriorRepay(){
		checkAdminValidImg();//验证验证码
		long borrowId = paramLong("borrowId");
		Borrow borrow = borrowService.getBorrow(borrowId);
		int priorStatus = paramInt("priorStatus");
		borrow.setPriorStatus(priorStatus);
		borrowService.updateBorrow(borrow);
		message("审核提前还款申请操作成功！", "/admin/borrow/getApplyPriorRepayList.html?priorStatus=-2");
		return ADMINMSG;
	}

	//TGPROJECT-372 老账房项目提前还款  2014-07-21 wujing end
	
	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	@Action(value="showAllCompensation",
			results={@Result(name="success",type="ftl",location="/admin/borrow/showAllCompensation.html")})
	public String showAllCompensation() throws Exception{
		//借款名称用户名
		String name = paramString("name");

		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page")).addOrder(OrderType.DESC, "id");
		
		String search = request.getParameter("search");
		if(!StringUtils.isBlank(name)) {
			param.addParam("borrow.name", Operator.EQ, name);
		}
		PageDataList<TenderCompensation> tenderCompensationList = tenderCompensationService.getList(param);
		setPageAttribute(tenderCompensationList, param);
		
		String actionType = paramString("actionType");
		if(StringUtils.isBlank(actionType)){
			return "success";
		}else{
			List listExport=tenderCompensationService.getExportCompensationList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="所有补偿金记录_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=new String[]{"id","borrow/.user/.username","borrow/.name","tenderUser/.username",
					"tenderMoney","compensation","days","rate","addtime","status"};
			String[] titles=new String[]{"ID","借款人","借款标题","出借人","出借金额","满标前补偿金","天数","补偿率","添加时间","状态"};
			ExcelHelper.writeExcel(infile, listExport, TenderCompensation.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
	}
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
	
	
	//无名重庆 WMCQ-1 2014-09-09 wujing start

	@Action(value="showAllBusiness",
			results={@Result(name="success",type="ftl",location="/admin/borrow/showBusinessList.html")})
	public String  showBusinessList(){
		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page")).addOrder(OrderType.DESC, "id");
		
		int status = paramInt("status");
		if (status!=0) {
			param.addParam("status", status);
		}
		PageDataList<BusinessType> pageList = businessTypeService.findPageListBusinessType(param);
		setPageAttribute(pageList,param);
		return "success";
	}
	
	/**
	 * @return
	 */
	@Action(value="addBusinessType",
			results={@Result(name="success",type="ftl",location="/admin/borrow/addBusinessType.html")})
	public String addBusinessType(){
		String type =paramString("type");
		if ("add".equals(type)) {
			return "success";
			
		}
		checkAdminValidImg();//验证验证码
		BusinessType business =  new BusinessType();
		business.setAddTime(new Date());
		business.setAddUser(new User(this.getAuthUser().getUserId()));
		business.setName(paramString("name"));
		business.setContent(paramString("content"));
		business.setStatus(paramInt("status"));
		businessTypeService.addBusinessType(business);
		message("添加成功!", "/admin/borrow/showAllBusiness.html");
		return ADMINMSG;
	}
	
	@Action(value="editBusiness",
			results={@Result(name="success",type="ftl",location="/admin/borrow/editBusiness.html")})
	public String editBusiness(){
		long id = paramLong("bussinessId");
		BusinessType business = businessTypeService.findBusinessById(id);
		if (null == business) {
			message("更新数据异常！");
			return ADMINMSG;
		}
		String type = paramString("type");
		if ("show".equals(type)) {
			request.setAttribute("business", business);
			return "success";
		}
		business.setStatus(paramInt("status"));
		business.setContent(paramString("content"));
		business.setName(paramString("name"));
		businessTypeService.updateBusinessType(business);
		message("操作成功！","/admin/borrow/showAllBusiness.html");
		return ADMINMSG;
	}
	

	@SuppressWarnings("unused")
	private void madeBusinessType(SearchParam param){
		SearchParam sp = new SearchParam().addParam("status",1);
		List<BusinessType> businessTypeList = businessTypeService.findListBusinessType(sp);
		request.setAttribute("businessTypeList", businessTypeList);//获取业务部门
		
		String projectType = paramString("projectType");
		String businessType = paramString("businessType");
		if(!StringUtils.isBlank(projectType)) {
			param.addParam("borrowProperty.projectType",Operator.EQ, projectType);
		}
		if(!StringUtils.isBlank(businessType)) {
			param.addParam("borrowProperty.businessType.id",Operator.EQ, businessType);
		}
	}
	//无名重庆 WMCQ-1 2014-09-09 wujing end

}
