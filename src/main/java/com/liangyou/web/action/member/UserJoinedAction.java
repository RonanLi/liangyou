package com.liangyou.web.action.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.Area;
import com.liangyou.domain.User;
import com.liangyou.domain.UserJoined;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserJoinedService;
import com.liangyou.service.UserService;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
@ParentPackage("p2p-default") 
@Namespace("/member/identify")
public class UserJoinedAction extends BaseAction {
	
	@Autowired
	private UserJoinedService userJoinedService;
	
	@Autowired
	private UserService userService;
	
	@Action(value="viewApplyJoinde",
			results={@Result(name="success",type="ftl",location="/member/identify/viewApplyJoined.html")},
			interceptorRefs={@InterceptorRef(value="mydefault")})
	public String viewApplyJoinde(){
		User applyUser = userService.getUserById(this.getSessionUser().getUserId());
		UserJoined userJoined = userJoinedService.getUserJoinedByUser(applyUser.getUserId());
		if (null !=userJoined) {
			request.setAttribute("userJoined", userJoined);
		}else{
			request.setAttribute("user", applyUser);
		}
		return "success";
	}
	
	@Action(value="doApplyJoined")
	public String doApplyJoined(){
		if(!checkValidImg(StringUtils.isNull(request.getParameter("valicode")))){
			throw new BussinessException("验证码不正确！");
		}
		int provinceId = paramInt("province"); 
		int cityId =  paramInt("city");
		String  remark = paramString("remark");
		UserJoined userJoined = new UserJoined();
		User user = this.getSessionUser();
		userJoined.setUserName(user.getUsername());
		userJoined.setJoinUser(new User(user.getUserId()));
		userJoined.setProvince(new Area(provinceId));
		userJoined.setCity(new Area(cityId));
		userJoined.setAddTime(new Date());
		userJoined.setRemark(remark);
		userJoinedService.addUserJoined(userJoined);
		message("申请完毕，请返回");
		return MSG;
	}
	
	@Action(value="getUsrrJoinedList")
	public void getUsrrJoinedList()throws Exception{
		SearchParam param = new SearchParam();
		int provinceId = paramInt("province");
		int cityId = paramInt("city");
		param.addParam("province.id", provinceId);
		param.addParam("status", 1);
		param.addParam("city.id", cityId);
		List<UserJoined> userJoinedList = userJoinedService.getUserJoinedList(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", userJoinedList);
		printJson(getStringOfJpaMap(map));
	}

}
