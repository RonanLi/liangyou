package com.liangyou.web.action.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.PrizeGoods;
import com.liangyou.domain.PrizeRule;
import com.liangyou.domain.PrizeUser;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.PrizeService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;


/**
 *  TGPROJECT-368
 *	抽奖
 */
@Namespace("/admin/prize")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManagePrizeAction extends BaseAction  {
	private static Logger logger = Logger.getLogger(ManagePrizeAction.class);
	
	@Autowired
	private PrizeService prizeService;
	private PrizeRule prizeRule = new PrizeRule();
	
	private PrizeGoods prizeGoods  = new PrizeGoods();
	
	/**
	 * 查询抽奖用户
	 */
	@Action(value="allPrizeUserList",results={@Result(name="success",type="ftl",location="/admin/prize/prizeUserList.html")})
	public String allPrizeUserList(){
		SearchParam param = new SearchParam().addPage(paramInt("page"));
		long ruleId = paramLong("ruleId");
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String status = paramString("status");
		String username = paramString("username");
		if(ruleId != 0){
			param.addParam("prizeRule", new PrizeRule(ruleId));
		}
		if(!StringUtils.isBlank(status)){
			param.addParam("status", NumberUtils.getInt(status));
		}
		if(!StringUtils.isBlank(username)){
			param.addParam("user.username", username);
		}
		if (!StringUtils.isBlank(startTime)) {
			param.addParam("addtime", Operator.GTE,
					DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if (!StringUtils.isBlank(endTime)) {
			param.addParam("addtime", Operator.LTE,
					DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList<PrizeUser> userList = prizeService.getAllPrizeUserList(param);
		setPageAttribute(userList, param);
		List<PrizeRule> prizeRuleList = prizeService.getAllPrizeRule();
		request.setAttribute("prizeRuleList", prizeRuleList);
		request.setAttribute("ruleId", ruleId);
		request.setAttribute("dotime1", startTime);
		request.setAttribute("dotime2", endTime);
		request.setAttribute("username", username);
		return SUCCESS;
	}
	
	/**
	 * 获取规则列表
	 * @return
	 */
	@Action(value="prizeRuleList",results={@Result(name="success",type="ftl",location="/admin/prize/prizeRuleList.html")})
	public String prizeRuleList(){
		List<PrizeRule> prizeRuleList = prizeService.getAllPrizeRule();
		request.setAttribute("list", prizeRuleList);
		return SUCCESS;
	}
	
	/**
	 * 查询抽奖规则对象
	 * @return
	 */
	@Action(value="modifyPrizeRule",results={@Result(name="success",type="ftl",location="/admin/prize/prizeRuleEdit.html")})
	public String modifyRule(){
		long id = paramLong("id");
		PrizeRule prizeRule = prizeService.getPrizeRuleById(id);
		if (prizeRule ==null) {
			message("获取抽奖规则异常，请联系系统管理员！");
			return ADMINMSG;
		}
		request.setAttribute("p", prizeRule);
		return SUCCESS;
	}
	
	/**
	 * 修改抽奖规则
	 * @return
	 */
	@Action(value="prizeRuleEdit")
	public String prizeRuleEdit(){
		long id = paramLong("ruleId");
		PrizeRule newPrizeRule = prizeService.getPrizeRuleById(id);
		
		if (null ==newPrizeRule) {
			message("系统异常，请联系管理员！");
			return ADMINMSG;
		}
		doPrizeRule(newPrizeRule);
		
		prizeService.updatePrizeRule(newPrizeRule);
		message("修改抽奖规则完毕","/admin/prize/prizeRuleList.html");
		return ADMINMSG;
	}
	
	@Action(value="prizeRuleAddView",results={@Result(name="success",type="ftl",location="/admin/prize/prizeRuleAddView.html")})
	public String prizeRuleAddView(){
		return SUCCESS;
	}
	
	/**
	 * 添加抽奖规则
	 * @return
	 */
	@Action(value="prizeRuleAdd")
	public String prizeRuleAdd(){
		String content = paramString("content");
		prizeRule.setContent(content);
		prizeRule.setAddtime(new Date());
		prizeRule.setAddip(this.getRequestIp());
		logger.info("prizeRule"+prizeRule.getName());
		prizeService.addPrizeRule(prizeRule);
		message("操作成功！", "/admin/prize/prizeRuleList.html");
		return ADMINMSG;
	}
	
	/**
	 * 查询奖品list
	 * @return
	 */
	@Action(value="prizeGoodsList",results={@Result(name="success",type="ftl",location="/admin/prize/prizeGoodsList.html")})
	public String prizeGoodsList(){
		long ruleId = paramLong("ruleId");  //规则id
		List<PrizeGoods> goodsList = prizeService.goodsList(ruleId);
		request.setAttribute("goodsList", goodsList);
		request.setAttribute("ruleId", ruleId);
		return SUCCESS;
	}
	
	/**
	 * 跳转修改奖品规则页面
	 * @return
	 */
	@Action(value="prizeGoodsEditView",results={@Result(name="success",type="ftl",location="/admin/prize/prizeGoodsEditView.html")})
	public String prizeGoodsEditView(){
		long id = paramLong("prizeId");  //奖品id
		PrizeGoods prizeGoods = prizeService.getPrizeGoodsById(id);
		if (null ==prizeGoods) {
			message("获取数据异常，请联系管理员");
			return ADMINMSG;
		}
		request.setAttribute("p", prizeGoods);
		return SUCCESS;
	}
	
	/**
	 * 修改奖品规则
	 * @return
	 */
	@Action("prizeGoodSEdit")
	public String prizeGoodSEdit(){
		long id = paramLong("id");
		PrizeGoods newPrizeGoods = prizeService.getPrizeGoodsById(id);
		doEditPrizeGoods(newPrizeGoods);
		prizeService.updatePrizeGoods(newPrizeGoods);
		message("修改奖品规则操作完成！","/admin/prize/prizeGoodsList.html?ruleId="+newPrizeGoods.getPrizeRule().getId());
		return ADMINMSG;
		
	}
	
	/**
	 * 跳转新增奖品页面
	 * @return
	 */
	@Action(value="prizeGoodsAddView",results={@Result(name="success",type="ftl",location="/admin/prize/prizeGoodsAddView.html")})
	public String prizeGoodsAddView(){
		long ruleId = paramLong("ruleId");
		request.setAttribute("ruleId", ruleId);
		return SUCCESS;
	}
	
	/**
	 * 新增奖品方法
	 * @return
	 */
	@Action("prizeGoodsAdd")
	public String prizeGoodsAdd(){
		long ruleId = paramLong("ruleId");
		prizeGoods.setPrizeRule(new PrizeRule(ruleId));
		//单独处理中奖率
		BigDecimal rate = new BigDecimal(StringUtils.isNull(request.getParameter("rate")));
		prizeGoods.setRate(rate.multiply(new BigDecimal(100000000)).intValue());
		prizeGoods.setAddtime(new Date());
		prizeGoods.setAddip(this.getRequestIp());
		prizeService.savePrizeGoods(prizeGoods);
		message("新增奖品成功操作成功！","/admin/prize/prizeGoodsList.html?ruleId="+ruleId);
		return ADMINMSG;
	}
	
	
	/**
	 * 抽奖规则更新参数封装
	 * @param prizeRule
	 */
	private void doPrizeRule(PrizeRule prizeRule){
		String content = paramString("content");
		prizeRule.setContent(content);
		prizeRule.setName(paramString("name"));
		prizeRule.setStarttime(DateUtils.getDate2(paramString("starttime")));
		prizeRule.setEndtime(DateUtils.getDate2(paramString("endtime")));
		prizeRule.setPrizeType(paramInt("prizeType"));
		prizeRule.setTimeLimit(paramInt("timeLimit"));
		prizeRule.setMaxTimes(paramInt("maxTimes"));
		prizeRule.setBasePoint(paramInt("basePoint"));
		prizeRule.setMoneyLimit(paramInt("moneyLimit"));
		prizeRule.setTotalMoney(paramInt("totalMoney"));
		prizeRule.setIsAbsolute(paramInt("isAbsolute"));
		prizeRule.setBackType(paramInt("backType"));
	}
	
	
	/**
	 * 修改奖品规则参数封装方法
	 * @param prizeGoods
	 */
	private void doEditPrizeGoods(PrizeGoods prizeGoods){
		prizeGoods.setName(paramString("name"));
		//单独处理中奖率
		BigDecimal rate = new BigDecimal(StringUtils.isNull(request.getParameter("rate")));
		prizeGoods.setRate(rate.multiply(new BigDecimal(100000000)).intValue());
		prizeGoods.setLevel(paramInt("level"));
		prizeGoods.setPrizeLimit(paramInt("prizeLimit"));
		prizeGoods.setTotal(paramInt("total"));
		prizeGoods.setType(paramInt("type"));
		prizeGoods.setGoodsValue(paramInt("goodsValue"));
		prizeGoods.setDescription(paramString("description"));
		
	}

	public PrizeRule getPrizeRule() {
		return prizeRule;
	}

	public void setPrizeRule(PrizeRule prizeRule) {
		this.prizeRule = prizeRule;
	}

	public PrizeGoods getPrizeGoods() {
		return prizeGoods;
	}

	public void setPrizeGoods(PrizeGoods prizeGoods) {
		this.prizeGoods = prizeGoods;
	}
	
	
	
}
