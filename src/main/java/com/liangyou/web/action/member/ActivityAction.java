package com.liangyou.web.action.member;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.User;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.RewardRecordSql;
import com.liangyou.model.SearchParam;
import com.liangyou.service.RewardRecordService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.BASE64Encoder;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

/**
 * @Desc 用户奖励管理  控制层
 * @author yjt_anzi
 * @DateTime	2015-05-09 14:12
 *	/activity/myactivity.html
 */
@Namespace("/activity")
@ParentPackage("p2p-default") 
public class ActivityAction extends BaseAction {

	private static Logger logger = Logger.getLogger(ActivityAction.class);
	@Autowired
	private UserService userService;
	@Autowired
	private RewardRecordService rewardRecordService;
	
	/**
	 * @Desc 奖励管理
	 * @return
	 */
	@Action(value="myactivity",results={
			@Result(name="success", type="ftl",location="/member/activity/user_activity.html"),
			@Result(name="login", type="redirect",location="/user/login.html")
			})
	public String myActivity(){
		logger.info("__myactivity:");
		User sessionUser = getSessionUser();
		if (sessionUser == null) {
			return "login";
		} 
		long user_id = sessionUser.getUserId();
		User user  = userService.getUserById(user_id);  //查找用户的信息。
		// 根据用户ID获取用户 红包:0 、 加息券、体验金 （目前只有红包）
		//红包
		SearchParam param = new SearchParam();
		param.addParam("rr.user_id", user.getUserId());
		param.addParam("rr.reward_type", "0");
		param.addPage(1, 3); //取前三条
//		param.addOrder(OrderType.DESC,"rr.create_date"); //奖励发放日期  倒序	改为是否有效ID排序
		param.addOrder(OrderType.ASC,"rr.is_enable,rr.reward_record_id");
//		param.addOrder(OrderType.DESC,"rr.reward_record_id");
		PageDataList<RewardRecordSql> pLuckMoneyList = rewardRecordService.getRewardByUid(param);
		request.setAttribute("LuckMoneyList", pLuckMoneyList.getList());
		BASE64Encoder encoder = new BASE64Encoder();
		String s=encoder.encode((user.getUserId()+"").getBytes());
		request.setAttribute("userid", s);
		return "success";
	}
	
	/**
	 * @Desc 红包:0、加息券、体验金记录 
	 * @return
	 */
	@Action(value="myreward",results={
			@Result(name="success_luckmoney", type="ftl",location="/member/activity/user_luckmoney.html"),
			//@Result(name="success_interest", type="ftl",location="/member/activity/user_interest.html"),
			//@Result(name="success_experiencemoney", type="ftl",location="/member/activity/user_experiencemoney.html"),
			@Result(name="login", type="redirect",location="/user/login.html")
			})
	public String myrewardList(){
		logger.info("__myrewardList:");
		String resultstr = "";
		User sessionUser = getSessionUser();
		if (sessionUser == null) {
			return "login";
		} 
		long user_id = sessionUser.getUserId();
		User user  = userService.getUserById(user_id);  //查找用户的信息。
		String rewardType = paramString("rewardType");
		String isEnable = paramString("isEnable");
		
		SearchParam param = new SearchParam();
		param.addParam("rr.user_id", user.getUserId());
		param.addPage(paramInt("page"));
//		param.addOrder(OrderType.DESC,"rr.create_date"); //奖励发放日期  倒序	
//		param.addOrder(OrderType.ASC,"rr.is_enable");	//是否有效
		param.addOrder(OrderType.DESC,"rr.is_enable,rr.reward_record_id");	//改为ID排序
		if(!StringUtils.isBlank(isEnable))
			param.addParam("rr.is_enable", isEnable);
		
		
		if(!StringUtils.isBlank(rewardType)){ 
			//类型为 红包！
			if(rewardType.equals("0")){
				param.addParam("rr.reward_type", "0");
				PageDataList<RewardRecordSql> pLuckMoneyList = rewardRecordService.getRewardByUid(param);
				//request.setAttribute("rewardList", pLuckMoneyList);
				request.setAttribute("page", pLuckMoneyList.getPage());
				request.setAttribute("list", pLuckMoneyList.getList());
				SearchParam parampage = new SearchParam();
				parampage.addPage(paramInt("page"));
				parampage.addParam("rewardType", rewardType);
				if(!StringUtils.isBlank(isEnable))
					parampage.addParam("isEnable", isEnable);
				request.setAttribute("param", parampage.toMap());
				
				// 统计 未使用、已使用、过期 的奖励个数
				SearchParam paramcounts = new SearchParam();
				paramcounts.addParam("user_id", user.getUserId());
				paramcounts.addGroupBy("is_enable");
				Map<String,Integer> countsMap = rewardRecordService.countsByUid(paramcounts);
				request.setAttribute("countsMap", countsMap);
				
				resultstr = "success_luckmoney";
			}
		}
		return resultstr;
	}
}
