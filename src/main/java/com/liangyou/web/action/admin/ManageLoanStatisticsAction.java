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

import com.liangyou.context.ExcelType;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.LoanStatistics;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.BorrowService;
import com.liangyou.service.LoanStatisticsService;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.web.action.BaseAction;

/**
 * v1.8.0.4_u4 TGPROJECT-371 	qinjun 2014-07-22
 */
@Namespace("/admin/statistics")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageLoanStatisticsAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ManageLoanStatisticsAction.class);
	
	@Autowired
	LoanStatisticsService loanStatisticsService;
	@Autowired
	BorrowService borrowService;
	
	/**
	 * 出借客户情况统计表
	 * @return
	 * @throws Exception
	 */
	@Action(value="tenderStatisticsList",results={@Result(name="success",type="ftl",location="/admin/statistics/tenderStatisticsList.html")})
	public String tenderStatisticsList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addOrFilter("userType",1,3);
		PageDataList<LoanStatistics> list = loanStatisticsService.getLoanStatisticsList(param);
		setPageAttribute(list, param);
		if(paramString("type").equals("export")){
			PageDataList<LoanStatistics> listExport=loanStatisticsService.getAllLoanStatisticsList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="发标待审的标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","user/.inviteUser/.inviteUser/.username","user/.username","user/.realname","user/.userinfo/.provinceStr","user/.userinfo/.companyIndustryStr","user/.phone",
					"tenderCount","tenderTotal","waitTenderCount","waitCollectionCapital","profitAndLossTotal"};
			String[] titles={"ID","客户经理","用户名","客户姓名","地区","行业","手机","累计出借笔数","累计出借金额","未还清借款出借笔数","出借余额","盈亏总额"};
			ExcelHelper.writeExcel(infile, listExport.getList(), LoanStatistics.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		return SUCCESS;
	}
	/**
	 * 贷款客户情况统计表
	 * @return
	 * @throws Exception
	 */
	@Action(value="borrowStatisticsList",results={@Result(name="success",type="ftl",location="/admin/statistics/borrowStatisticsList.html")})
	public String borrowStatisticsList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addOrFilter("userType",2,3);
		if(paramString("type").equals("export")){
			PageDataList<LoanStatistics> listExport=loanStatisticsService.getAllLoanStatisticsList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="发标待审的标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","user/.inviteUser/.inviteUser/.username","user/.username","user/.realname","user/.userinfo/.provinceStr","user/.userinfo/.companyIndustryStr","user/.phone",
					"tenderCount","tenderTotal","waitTenderCount","waitCollectionCapital","earnInterest"};
			String[] titles={"ID","客户经理","用户名","客户姓名","地区","行业","手机","累计出借笔数","累计出借金额","未还清借款出借笔数","出借余额","盈亏总额"};
			ExcelHelper.writeExcel(infile, listExport.getList(), LoanStatistics.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}else{
			PageDataList<LoanStatistics> list = loanStatisticsService.getLoanStatisticsList(param);
			setPageAttribute(list, param);
			return SUCCESS;
		}
	}
	/**
	 * 风险准备金情况分析表
	 * @return
	 * @throws Exception
	 */
	@Action(value="borrowRiskList",results={@Result(name="success",type="ftl",location="/admin/statistics/borrowRiskList.html")})
	public String borrowRiskList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addOrFilter("status", 6,7,8).addOrder(OrderType.DESC,"id");
		PageDataList<Borrow> list = borrowService.getList(param);
		setPageAttribute(list, param);
		if(paramString("type").equals("export")){
			List<Borrow> exportList = borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="发标待审的标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","user/.inviteUser/.inviteUser/.username","user/.username","user/.realname","user/.userinfo/.provinceStr","user/.userinfo/.companyIndustryStr","user/.phone",
					"account","borrowFee/.riskreserve","riskreserveFeeStr"};
			String[] titles={"ID","客户经理","用户名","客户姓名","地区","行业","手机","贷款金额","风险准备金比例","风险准备金"};
			ExcelHelper.writeExcel(infile, exportList, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		return SUCCESS;
	}
	/**
	 * 服务费情况分析表
	 * @return
	 * @throws Exception
	 */
	@Action(value="manageFeeList",results={@Result(name="success",type="ftl",location="/admin/statistics/manageFeeList.html")})
	public String manageFeeList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addOrFilter("status", 6,7,8).addOrder(OrderType.DESC,"id");
		PageDataList<Borrow> list = borrowService.getList(param);
		setPageAttribute(list, param);
		if(paramString("type").equals("export")){
			List<Borrow> exportList = borrowService.getExportBorrowList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="发标待审的标_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","user/.username","user/.realname","user/.userinfo/.provinceStr",
					"account","borrowFee/.managefee","timeLimit","manageFeeStr"};
			String[] titles={"ID","用户名","客户姓名","地区","贷款金额","服务费比例","贷款时间（月）","服务费金额"};
			ExcelHelper.writeExcel(infile, exportList, Borrow.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		return SUCCESS;
	}
	/**
	 * 老账房：客户账户资金情况统计表
	 * @return
	 * @throws Exception
	 */
	@Action(value="customAccountCountList",
			results={@Result(name="success",type="ftl",location="/admin/statistics/customAccountCountList.html")})
	public String customAccountCountList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addParam("status", 1);
		String type = paramString("type");
		if (!type.equals("export")) {
			PageDataList<BorrowRepayment> pageList = loanStatisticsService.getPageCustomAccountCount(param);
			setPageAttribute(pageList, param);
		}else{
			List<BorrowRepayment> list = loanStatisticsService.getListCustomAccountCount(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="客户账户资金情况统计表_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","borrow/.user/.username","borrow/.user/.realname","borrow/.user/.inviteUser/.inviteUser/.username","repaymentYestime","capital",
					"interest","repaymentYescapital","repaymentYesinterest","borrow/.user/.account/.remainderMoney","borrow/.user/.account/.useMoney"};
			String[] titles={"ID","用户名","客户姓名","经办客户经理","还款日期","应还本金","应还利息","实还本金","实还利息","余额","可用余额"};
			ExcelHelper.writeExcel(infile, list, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		return SUCCESS;
	}
	
	
	/**
	 * 逾期还款客户查询
	 * @return
	 * @throws Exception
	 */
	@Action(value="lateRepayCustomList",
			results={@Result(name="success",type="ftl",location="/admin/statistics/lateRepayCustomList.html")})
	public String lateRepayCustomList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addParam("lateDays", Operator.GT, 0);
		param.addParam("status", 1);
		param.addParam("repaymentTime", Operator.PROPERTY_LT,"repaymentYestime");
		param.addParam("borrowRepayType.id", 4);
		String type = paramString("type");
		if (!type.equals("export")) {
			PageDataList<BorrowRepayment> pageList = loanStatisticsService.getPageCustomAccountCount(param);
			setPageAttribute(pageList, param);
		}else{
			List<BorrowRepayment> list = loanStatisticsService.getListCustomAccountCount(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="客户逾期还款统计表_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names= {"id","borrow/.user/.username","borrow/.user/.realname","borrow/.user/.inviteUser/.inviteUser/.username","borrow/.account","borrow/.verifyTime",
							"borrow/.endBorrowTime","repaymentTime","repaymentYestime","lateDays"};
			String[] titles={"ID","用户名","客户姓名","经办客户经理","贷款金额","起贷日期","止贷日期","逾期起始日","逾期还款时间","逾期天数"};
			ExcelHelper.writeExcel(infile, list, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		
		return SUCCESS;
	}
	
	/**
	 * 查询逾期未还款的信息
	 * @return
	 * @throws Exception 
	 */
	@Action(value="lateNoRepayCustomList",
			results={@Result(name="success",type="ftl",location="/admin/statistics/lateNoRepayCustomList.html")})
	public String lateNoRepayCustomList() throws Exception{
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addParam("status", 0);
		param.addParam("repaymentTime",Operator.LT, new Date());
		String type = paramString("type");
		if (!type.equals("export")) {
			PageDataList<BorrowRepayment> pageList = loanStatisticsService.getPageCustomAccountCount(param);
			setPageAttribute(pageList, param);
		}else{
			List<BorrowRepayment> list = loanStatisticsService.getListCustomAccountCount(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="客户逾期未还统计表_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;//
			String[] names= {"id","borrow/.user/.username","borrow/.user/.realname","borrow/.user/.inviteUser/.inviteUser/.username","borrow/.account","borrow/.verifyTime",
							"borrow/.endBorrowTime","repaymentTime","retPeriod","surplusWaitRepayAccount","capital","interest","lateDays","lateInterest"};
			String[] titles={"ID","用户名","客户姓名","经办客户经理","贷款金额","起贷日期","止贷日期","逾期起始日","逾期期数","剩余本金","逾期本金","逾期利息","逾期天数","累计罚息"};
			ExcelHelper.writeExcel(infile, list, BorrowRepayment.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
		}
		return SUCCESS;
		
	}
}
