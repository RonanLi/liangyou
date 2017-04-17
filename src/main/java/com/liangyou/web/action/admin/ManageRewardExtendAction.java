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

import com.liangyou.domain.RewardExtend;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.RewardExtendService;
import com.liangyou.web.action.BaseAction;

/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz
 * 奖励发放action
 * @author wujingn
 *
 */
@Namespace("/admin/account")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageRewardExtendAction extends BaseAction {
	
	@Autowired
	private RewardExtendService rewardExtendService;
	
	
	/**
	 * 获取奖励列表
	 * @return
	 */
	@Action(value="rewardExtendList",results={
			@Result(name="success", type="ftl",location="/admin/account/rewardExtendList.html")			
			})
	public String rewardExtendList(){
		Map<String, Object> extraParams = new HashMap<String, Object>();
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		int status = paramInt("status");
		if(status != -2){
			param.addParam("status",status);
		}
		extraParams.put("status", status);
		PageDataList<RewardExtend> rewardList = rewardExtendService.getPageLIstReward(param);
		setPageAttribute(rewardList, param, extraParams);
		return "success";
	}
	
	
	/**
	 * 展示红包
	 * @return
	 */
	@Action(value="rewardExtendView",results={
			@Result(name="success", type="ftl",location="/admin/account/rewardExtendView.html")			
			})
	public String rewardExtendView(){
		long id = paramLong("rewardId");
		RewardExtend reward = rewardExtendService.getRewardByid(id);
		if (null == reward) {
			return ADMINMSG;
		}
		request.setAttribute("reward", reward);
		return "success";
	}
	
	/*@Action(value="doRewardExtend",results={
			@Result(name="success", type="ftl",location="/admin/account/rewardExtendView.html")			
			})
	public String doRewardExtend(){
		String[] ids = request.getParameterValues("rewardId");
		rewardExtendService.doRedExtend(ids);
		message("重新处理完毕，请查看用户订单，是否成功","/admin/account/rewardExtendList.html");
		return ADMINMSG;
	}*/
	
	
	

}
