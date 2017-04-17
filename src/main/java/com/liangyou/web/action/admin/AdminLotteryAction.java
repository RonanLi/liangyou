package com.liangyou.web.action.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.lottery.LotteryDetail;
import com.liangyou.service.LotteryService;
import com.liangyou.service.PrizeUserRelationshipService;
import com.liangyou.tool.Page;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author youg
 *
 */
@Namespace("/admin/lottery")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminLotteryAction extends BaseAction implements ModelDriven<PrizeUserRelationship> {

	private static final Logger logger = Logger.getLogger(AdminLotteryAction.class);

	private PrizeUserRelationship model = new PrizeUserRelationship();

	@Autowired
	private PrizeUserRelationshipService prizeUserRelationshipService;
	@Autowired
	private LotteryService lotteryService;

	@Override
	public PrizeUserRelationship getModel() {
		return model;
	}

	@Action(value = "lotteryDetail", results = { @Result(name = "success", type = "ftl", location = "/admin/lottery/lotteryDetail.html") })
	public String lotteryDetail() {
		int page = NumberUtils.getInt(request.getParameter("page") == null ? "1" : request.getParameter("page"));
		String userName = paramString("userName");
		String realName = paramString("realName");
		String prizeId = paramString("prizeId");
		String status = paramString("status");
		String realReceiveTimeStart = paramString("realReceiveTimeStart");
		String realReceiveTimeEnd = paramString("realReceiveTimeEnd");
		String useTimeStart = paramString("useTimeStart");
		String useTimeEnd = paramString("useTimeEnd");

		SearchParam searchParam = new SearchParam();
		searchParam.addPage(page, Page.ROWS);
		searchParam.addParam("pur.receive_state", 1);

		if (!StringUtils.isBlank(userName)) {
			searchParam.addParam("u.username", Operator.LIKE, userName);
		}
		if (!StringUtils.isBlank(realName)) {
			searchParam.addParam("u.realname", Operator.LIKE, realName);
		}
		if (!StringUtils.isBlank(prizeId)) {
			searchParam.addParam("p.prize_id", Long.parseLong(prizeId));
		}
		if (!StringUtils.isBlank(status)) {
			searchParam.addParam("pur.status", status);
		}
		if (!StringUtils.isBlank(realReceiveTimeStart)) {
//			searchParam.addParam("pur.real_receive_time", Operator.GTE, DateUtils.getDate(realReceiveTimeStart, "yyyy-MM-dd HH:mm:ss"));
			searchParam.addParam("pur.real_receive_time", Operator.GTE, realReceiveTimeStart);
		}
		if (!StringUtils.isBlank(realReceiveTimeEnd)) {
//			searchParam.addParam("pur.real_receive_time", Operator.LTE, DateUtils.getDate(realReceiveTimeEnd, "yyyy-MM-dd HH:mm:ss"));
			searchParam.addParam("pur.real_receive_time", Operator.LTE, realReceiveTimeEnd);
		}
		if (!StringUtils.isBlank(useTimeStart)) {
//			searchParam.addParam("pur.use_time", Operator.GTE, DateUtils.getDate(useTimeStart, "yyyy-MM-dd HH:mm:ss"));
			searchParam.addParam("pur.use_time", Operator.GTE, useTimeStart);
		}
		if (!StringUtils.isBlank(useTimeEnd)) {
//			searchParam.addParam("pur.use_time", Operator.LTE, DateUtils.getDate(useTimeEnd, "yyyy-MM-dd HH:mm:ss"));
			searchParam.addParam("pur.use_time", Operator.LTE, useTimeEnd);
		}

		searchParam.addOrder(OrderType.DESC, "pur.real_receive_time");
		PageDataList<LotteryDetail> pageDataList = lotteryService.findLotteryDetailPageListBySql(searchParam);

		Map<String, Object> extraparam = new HashMap<String, Object>();

		extraparam.put("userName", userName);
		extraparam.put("realName", realName);
		extraparam.put("prizeId", prizeId);
		extraparam.put("status", status);
		extraparam.put("realReceiveTimeStart", realReceiveTimeStart);
		extraparam.put("realReceiveTimeEnd", realReceiveTimeEnd);
		extraparam.put("useTimeStart", useTimeStart);
		extraparam.put("useTimeEnd", useTimeEnd);

		setPageAttribute(pageDataList, searchParam, extraparam);

		String type = paramString("type");
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} else {
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			logger.debug("path===" + contextPath);
			String downloadFile = "抽奖明细_" + System.currentTimeMillis() + ".xls";
			String infile = contextPath + "/data/export/" + downloadFile;
			String[] names = new String[] { "userName", "realName", "prizeName", "realReceiveTime", "statusStr", "useTime", "prizeDetail" };
			String[] titles = new String[] { "用户名称", "真实姓名", "奖品", "领取时间", "是否使用", "使用时间", "奖品明细" };
			List<LotteryDetail> purlist = lotteryService.exportLotteryDetailListBySql(searchParam);
			try {
				ExcelHelper.writeExcel(infile, purlist, LotteryDetail.class, Arrays.asList(names), Arrays.asList(titles));
				this.export(infile, downloadFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}
}
