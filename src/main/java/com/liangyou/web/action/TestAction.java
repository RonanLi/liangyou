package com.liangyou.web.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.liangyou.domain.Prize;
import com.liangyou.service.PrizeService;
import com.liangyou.util.NumberUtils;

@Controller
@Namespace("/test")
public class TestAction extends BaseAction{
	@Autowired
	private PrizeService prizeService;
@Action("reward")
public void test(){
	String useTypeString = request.getParameter("useType");
	String total = request.getParameter("totalInvestment");
	int useType = NumberUtils.getInt(useTypeString);
	double totalInvestment = NumberUtils.getDouble(total);
	Prize rewardUsers = RewardUsers(useType, totalInvestment);
	writeJson(rewardUsers);
	System.out.println(rewardUsers.toString());
}
public Prize RewardUsers(int useType,Double totalInvestment) {
	Prize prize = prizeService.getPrizeByUseTypeAndInvestment(
			useType, totalInvestment);
	// 向prizeUserRelationship中表添加该用户的中奖信息
	return prize;
	
}

}
