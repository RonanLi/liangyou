package com.liangyou.web.action.member;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.RewardExtend;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.RewardExtendService;
import com.liangyou.service.RuleService;
import com.liangyou.util.DateUtils;
import com.liangyou.web.action.BaseAction;

/**
 * TGPROJECT-345
 * 用户模块奖励
 * @author wujing
 *
 */
@Namespace("/member/reward")
@ParentPackage("p2p-default") 
public class RewardExtendAction extends BaseAction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1284957984576480208L;

	private Logger logger = Logger.getLogger(RewardExtendAction.class);
	
	@Autowired
	private RewardExtendService rewardExtendService;
	@Autowired
	private RuleService ruleService;
	
	/**
	 * 查看红包列表
	 * @return
	 */
	@Action(value="getRewardList",results={@Result(name="success",type="ftl",location="/member/reward/rewardExtendList.html")})
	public String getRewardList(){
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		User user = this.getSessionUser();
		param.addParam("rewardUser.userId", user.getUserId());
		PageDataList<RewardExtend> rewardList = rewardExtendService.getPageLIstReward(param);
		setPageAttribute(rewardList, param);
		//获取红包最低兑现额度
		Rule rule = ruleService.getRuleByNid("red_packet_cash");  //获取红包兑现规则
		if (null  !=rule && rule.getStatus() !=0) {
			int lowestStatus = rule.getValueIntByKey("lowest_status");   //判断红包最低兑现额度
			if (lowestStatus ==1) {   //校验最低兑现额度
				double lowestMoney = rule.getValueDoubleByKey("lowest_money");
				request.setAttribute("redLowestMoney", lowestMoney);  //获取红包最低兑现额度
			}
		}
		return "success";
	}
	
	
	/**
	 * 红包对象业务处理
	 * @return
	 */
	@Action(value="doRewardCash",results={@Result(name="success",type="ftl",location="/member/reward/rewardList.html")})
	public String doRewardCash(){
		checkValidImg();
		Rule rule = ruleService.getRuleByNid("red_packet_cash");  //获取红包兑现规则
		if (null !=rule && rule.getStatus() ==1) {
			int isCash = rule.getValueIntByKey("is_cash");
			if (isCash !=1) {   //判断平台是否允许红包兑现
				message("系统禁止红包兑现！详情请咨询客服。");
				return MSG;
			}
			String[] ids = request.getParameterValues("idCheck");
			if (null ==ids || ids.length<1) {
				message("请选择红包");
				return MSG;
			}
			double redPacket = rewardExtendService.getSumRewardById(ids);    //计算用户选择的红包总额
			int lowestStatus = rule.getValueIntByKey("lowest_status");   //判断红包最低兑现额度
			if (lowestStatus ==1) {   //校验最低兑现额度
				double lowestMoney = rule.getValueDoubleByKey("lowest_money");
				if (redPacket<lowestMoney) {
					message("你兑现的红包总额为："+redPacket+"元，小于系统规定的最低额度："+lowestMoney+"元");
					return MSG;
				}
			}
			//处理红包发放业务
			BorrowParam param = new BorrowParam();
			String resultFlag = System.currentTimeMillis() + "";
			param.setResultFlag(resultFlag);
			request.setAttribute("tenderFlag", resultFlag);
			request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
			request.setAttribute("back_url", "/member/main.html");// 失败返回地址
			request.setAttribute("r_msg", "红包兑现处理完毕！");
			DisruptorUtils.doRedExtend(ids,param);
			return RESULT;
		}else{
			message("目前还不支持红包兑现功能！");
			return MSG;
		}
	}

}
