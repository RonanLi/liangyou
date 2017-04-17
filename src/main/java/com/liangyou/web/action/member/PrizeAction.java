package com.liangyou.web.action.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import sun.util.logging.resources.logging;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.PrizeResultType;
import com.liangyou.domain.PrizeGoods;
import com.liangyou.domain.PrizeRule;
import com.liangyou.domain.PrizeUser;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.prize.PrizeResult;
import com.liangyou.service.PrizeService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

/**
 * TGPROJECT-367 抽奖
 */
@Namespace("/prize")
@ParentPackage("p2p-default")

public class PrizeAction extends BaseAction {
	
	@Autowired
	private PrizeService prizeService;
	
	/**
	 * 抽奖主程序
	 * 
	 * @return forward信息
	 * @throws Exception
	 *             异常
	 */
	@Action("prize")
	public String prize() throws Exception {

		// 1. 判断用户是否登录
		User user = getSessionUser();
		if (user == null) {
			PrizeResult result = new PrizeResult();
			result.setError(PrizeResultType.RESULT_NO_REGISTER);
			awardPrintJson(result);
			return null;
		}

		// 2.判断ruleId
		long ruleId = NumberUtils.getLong(request.getParameter("id"));
		if (ruleId == 0) {
			PrizeResult result = new PrizeResult();
			result.setError(PrizeResultType.RESULT_PARAMETER_ERROR);
			awardPrintJson(result);
			return null;
		}

		// 抽奖
		PrizeResult result = prizeService.extractionPrize(ruleId,user,0);
		// 输出抽奖结果
		awardPrintJson(result);
		return null;
	}

	/**
	 * 初期显示
	 */
	@Action(value="index",results={@Result(name="success",type="ftl",location="/prize/index.html")})
	public String index(){
		// 取得抽奖类型
		int type = NumberUtils.getInt(request.getParameter("type"));
		long ruleId = NumberUtils.getLong(request.getParameter("id"));
		PrizeRule prizeRule = null;
		if (ruleId != 0) {
			prizeRule = prizeService.getPrizeRuleById(ruleId);
		} else {
			prizeRule = prizeService.getPrizeRuleByPrizeType(type);
		}
		request.setAttribute("rule", prizeRule);
		return SUCCESS;
	}

	/**
	 * 显示中奖名单
	 */
	@Action("prizeList")
	public String prizeList() throws Exception {
		SearchParam param = new SearchParam();
		List<PrizeUser> list = getPrizeList(param);
		awardPrintJson(list);
		return null;
	}

	/**
	 * 显示我的中奖名单
	 * 
	 * @return forward信息
	 * @throws Exception
	 *             异常
	 */
	@Action("myAwardList")
	public String myAwardList() throws Exception {
		User user = getSessionUser();
		PageDataList<PrizeUser> list = null;
		if (user != null) {
			SearchParam param = new SearchParam();
			param.addParam("prizeRule", new PrizeRule(paramLong("id")));
			list = prizeService.getAllPrizeUserList(param);
			awardPrintJson(list.getList());
		}
		return null;
	}

	/**
	 * 显示我的中奖名单记录
	 * 
	 * @return forward信息
	 * @throws Exception
	 *             异常
	 */
	@Action(value="myPrizeList",results={@Result(name="success",type="ftl",location="/member/prize/myPrizeList.html")},
			interceptorRefs={@InterceptorRef(value="mydefault")})
	public String myPrizeList() throws Exception {
		User user = getSessionUser();
		Map<String,Object> map = new HashMap<String, Object>();
		String startTime = StringUtils.isNull(request.getParameter("dotime1"));
		String endTime = StringUtils.isNull(request.getParameter("dotime2"));
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		param.addParam("user", user);
		param.addParam("prizeRule", new PrizeRule(paramLong("ruleId")));
		param.addParam("status", paramInt("status"));
		if (!StringUtils.isBlank(startTime)) {
			map.put("dotime1", startTime);
			startTime += " 00:00:00";
			param.addParam("addtime", Operator.GTE,
					DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if (!StringUtils.isBlank(endTime)) {
			map.put("dotime2", endTime);
			endTime += " 23:59:59";
			param.addParam("addtime", Operator.LTE,
					DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList<PrizeUser> plist = prizeService.getAllPrizeUserList(param);
		setPageAttribute(plist, param,map);
		return SUCCESS;
	}

	/**
	 * 取得中奖名单
	 * 
	 * @param ruleId
	 *            规则ID
	 * @param isOrderByLevel
	 *            是否按中奖级别排序
	 * @return 中奖名单列表
	 */
	private List<PrizeUser> getPrizeList(SearchParam param) {
		param.addParam("prizeRule", new PrizeRule(paramLong("id")));
		if (!"".equals(StringUtils.isNull(request.getParameter("level")))) {
			param.addOrder(OrderType.DESC, "prizeGoods.level");
		}else{
			param.addOrder(OrderType.DESC, "addtime");
		}
		// 中奖名单取得
		PageDataList<PrizeUser> list = prizeService.getAllPrizeUserList(param);
		List<PrizeUser> result = new ArrayList<PrizeUser>();
		for (PrizeUser prizeUser : list.getList()) {
			User user = new User();
			user.setUsername(StringUtils.hideLastChar(prizeUser.getUser().getUsername(),3));
			prizeUser.setUser(user);
			PrizeGoods goods = new PrizeGoods();
			goods.setName(prizeUser.getPrizeGoods().getName());
			prizeUser.setPrizeGoods(goods);
			result.add(prizeUser);
		}
		return result;
	}

	/**
	 * 输出JSON对象
	 * 
	 * @param obj
	 *            JSON对象
	 * @throws Exception
	 *             异常
	 */
	private void awardPrintJson(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", obj);
		printJson(JSON.toJSONString(map));
	}
}
