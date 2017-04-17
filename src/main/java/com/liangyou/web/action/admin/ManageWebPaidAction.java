package com.liangyou.web.action.admin;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.WebPaid;
import com.liangyou.domain.WebRepayLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.WebPaidService;
import com.liangyou.web.action.BaseAction;

/**
 * 系统自动扣款业务
 * @author wujing
 *
 */
@Namespace("/admin/account")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageWebPaidAction extends BaseAction {
	
	@Autowired
	private WebPaidService webPaidService;
	
	/**
	 * 获取列表页
	 * @return
	 */
	@Action(value="getWebPaidList",
			results={@Result(name="success",type="ftl",location="/admin/account/webPaidList.html")})
	public String getWebPaidList(){
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		Map<String, Object> extraParams = new HashMap<String, Object>();
		long borrowId = paramLong("borrowId");
		long repayId = paramLong("repayId");
		if (borrowId >0) {
			param.addParam("borrow.id", borrowId);
		}
		if (repayId >0) {
			param.addParam("repayment.id", repayId);
		}
		extraParams.put("borrowId", borrowId);
		extraParams.put("repayId", repayId);
		PageDataList<WebPaid> webPaidList = webPaidService.getPageWebRepayList(param);
		setPageAttribute(webPaidList, param,extraParams);
		return "success";
	}
		
	/**
	 * 查看扣款记录
	 * @return
	 */
	@Action(value="getWebRepayLogList",
			results={@Result(name="success",type="ftl",location="/admin/account/webRepayLogList.html")})
	public String getWebRepayLogList(){
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		long borrowId = paramLong("borrowId");
		long repayId = paramLong("repayId");
		if (borrowId >0) {
			param.addParam("borrow.id", borrowId);
		}
		if (repayId >0) {
			param.addParam("repayment.id", repayId);
		}
		PageDataList<WebRepayLog> repayLogList = webPaidService.getRepayWebLog(param);
		setPageAttribute(repayLogList, param);
		return "success";
	}
	

}
