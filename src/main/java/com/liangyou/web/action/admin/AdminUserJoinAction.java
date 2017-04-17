package com.liangyou.web.action.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Global;
import com.liangyou.domain.User;
import com.liangyou.domain.UserJoined;
import com.liangyou.domain.UserType;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserJoinedService;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

/**
 * 加盟action
 * @author wujing
 *
 */
@Namespace("/admin/attestation")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminUserJoinAction extends BaseAction {
	
	@Autowired
	private UserJoinedService userJoinedService;
	
	@Action(value="userJoinedList",results={
			@Result(name="success", type="ftl",location="/admin/attestation/userJoinedList.html")			
			})
	public String userJoinedList(){
		Map<String, Object> extraParams = new HashMap<String, Object>();
		String status = paramString("status");
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		if (!StringUtils.isBlank(status)) {
			param.addParam("status", status);
		}
		extraParams.put("status", status);
		PageDataList<UserJoined> userjoinDataList = userJoinedService.getPageJoinList(param);
		setPageAttribute(userjoinDataList, param,extraParams);
		return "success";
	}
	
	@Action(value="viewJoined",results={
			@Result(name="success", type="ftl",location="/admin/attestation/viewJoined.html")			
			})
	public String viewJoined(){
		long id= paramLong("id");
		UserJoined joined = userJoinedService.getUserJoinedById(id);
		if (null ==joined) {
			message("获取数据异常，请重试！");
			return ADMINMSG;
		}
		if (joined.getStatus() !=0) {
			message("此申请已经处理，请不要重复处理！");
			return ADMINMSG;
		}
		request.setAttribute("joined", joined);
		return "success";
	}
	
	@Action(value="doVerifyJoined")
	public String doVerifyJoined(){
		checkAdminValidImg();//验证验证码
		long id =  paramLong("id");
		int status = paramInt("status");
		String verifyRemark = paramString("verifyRemark");
		UserJoined joined = userJoinedService.getUserJoinedById(id);
		User adminUser =getAuthUser();
		
		joined.setStatus(status);
	
		joined.setVerifyRemark(verifyRemark);
		joined.setVerifyUser(new User(adminUser.getUserId()));
		userJoinedService.doVerifyJoined(joined);
		message("审核完毕，请查看结果！");
		return ADMINMSG;
	}

}
