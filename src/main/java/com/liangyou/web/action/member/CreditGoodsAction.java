package com.liangyou.web.action.member;

import java.util.ArrayList;
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

import com.liangyou.context.CreditType;
import com.liangyou.domain.Goods;
import com.liangyou.domain.GoodsCategory;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserCreditConvert;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.credit.GoodsCategoryModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.CreditConvertService;
import com.liangyou.service.LogService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserCreditService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.web.action.BaseAction;

@Namespace("/goods")
@ParentPackage("p2p-default") 
@InterceptorRefs({@InterceptorRef("mydefault")})
public class CreditGoodsAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(CreditGoodsAction.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private LogService logService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private OperateService operateService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private CreditConvertService creditConvertService;
	
	@Action(value="shopIndex",
			results={@Result(name="success",type="ftl",location="/member/shop/index.html")}
	)
	public String index(){
		User user = userService.getUserById(getSessionUser().getUserId());
		getLeftShowByUser(user);
		int page =paramInt("page");
		int pageNum=paramInt("pageNum")==0?12:paramInt("pageNum");
		SearchParam param = new SearchParam().addPage(page).addPage(page, pageNum);
		//积分范围筛选
		int startCredit=paramInt("startCredit");
		int endCredit=paramInt("endCredit");
		//V1.8.0.4_U1 TGPROJECT-273 LX START
		if(startCredit!=0&& endCredit != 0 ){ 
			param.addParam("creditValue", Operator.GTE, startCredit);
			param.addParam("creditValue", Operator.LTE, endCredit);
		}else if(startCredit!=0&&endCredit==0){
			param.addParam("creditValue", Operator.GTE, startCredit);
		}else if(startCredit==0&&endCredit!=0){
			param.addParam("creditValue", Operator.LTE, endCredit);
		}
		//V1.8.0.4_U1 TGPROJECT-273 LX END
		//商品分类查询
		int category_Id=paramInt("category_id");
		if(category_Id!=0){
			param.addParam("category", Operator.EQ, new GoodsCategory(category_Id));
		}
		PageDataList<Goods> pList=this.creditConvertService.showGoodsList(param);
		this.setPageAttribute(pList, param);
		request.setAttribute("startCredit", startCredit);
		request.setAttribute("endCredit", endCredit);
		return SUCCESS;
	}
	
	
	private void getLeftShowByUser(User user){
		List<GoodsCategory> list = creditConvertService.getGoodsCategoryListByParentId(0);
		List<GoodsCategoryModel> cateList = new  ArrayList<GoodsCategoryModel>();
		for (GoodsCategory gc : list) {
			GoodsCategoryModel model = new GoodsCategoryModel(gc);
			List<GoodsCategory> childList = creditConvertService.getGoodsCategoryListByParentId(gc.getId());
			if(childList!=null && childList.size()>0){
				model.setChildList(childList);
			}
			cateList.add(model);
		}
		request.setAttribute("cateList", cateList);
		request.setAttribute("user", user);
	}
	
	
	/**
	 * 兑换详情页面展示
	 * @return
	 */
	@Action(value="showGoods",
			results={@Result(name="success",type="ftl",location="/member/shop/showGoods.html")}
	)
	public String showGoods(){
		User user = userService.getUserById(getSessionUser().getUserId());
		this.getLeftShowByUser(user);
		int goodsId=paramInt("goodsId");
		if(goodsId!=0){
			Goods goods=this.creditConvertService.getGoodsById(goodsId);
			request.setAttribute("goods", goods);
			return "success";
		}
		message("抱歉！您所查询的商品不存在！");
		return MSG;
	}
	// v1.8.0.4_u4 TGPROJECT-254 zf 2014-5-4 start
	/**
	 * 积分明细展示
	 * @return
	 */
	@Action(value="creditDetail",
			results={@Result(name="success",type="ftl",location="/member/shop/creditDetail.html")}
			)
	public String creditDetail(){
		User user = userService.getUserById(getSessionUser().getUserId());
		this.getLeftShowByUser(user);
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page")).addParam("user", user).addOrder(OrderType.DESC,"id");
		PageDataList<UserCreditLog> list = creditConvertService.findUserCreditLogList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	// v1.8.0.4_u4 TGPROJECT-254 zf 2014-5-4 end
	

	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	/**
	 * 积分兑换
	 * @return
	 */
	@Action(value="convertGoods")
	public String convertGoods(){
		int goodsId=paramInt("goodsId");
		int goodsNum=paramInt("goodsNum");
		String address=paramString("address");
		if(goodsId!=0){
			Goods goods=this.creditConvertService.getGoodsById(goodsId);
			User user = getSessionUser();
			//提交
			if(goods!=null){
				UserCreditConvert creditConvert=new UserCreditConvert();
				//判断该商品是否是vip is_virtual=1
				int isVirtual=goods.getCategory().getIsVirtual();
				if(isVirtual == 1){
					//兑换vip
					creditConvert.setType(CreditType.GOODS_INTEGRAL_VIP);
					// 提取积分兑换规则信息
					Rule rule = ruleService.getRuleByNid("goods_integral_vip"); 
					if(rule==null || rule.getStatus() != 1){
						throw new BussinessException("您当前不能兑换vip！","/goods/shopIndex.html");
					}
					int time_check = rule.getValueIntByKey("time_check");// 兑换vip间隔时间是否启用
					int convert_time = rule.getValueIntByKey("convert_time");// 兑换vip间隔多长时间（月）
					// 判断用户申请的间隔时间，是否可以再次申请兑换
					if(time_check > 0){
						List<UserCreditConvert> convertList = creditConvertService.getCreditConvertListByUser(user,CreditType.GOODS_INTEGRAL_VIP);
						for(UserCreditConvert con : convertList){
							if(con != null && con.getStatus() == 0 ){
								throw new BussinessException("您已经申请了vip，请不要重复申请！");
							}else if(con != null && con.getStatus() == 1){
								String vipStartTime = DateUtils.getTimeStr(DateUtils.rollMon(DateUtils.getDate(DateUtils.getNowTimeStr()),-convert_time));
								long startTime = NumberUtils.getLong(vipStartTime);
								long verifyTime = DateUtils.getTime((con.getVerifytime()));
								if(verifyTime >= startTime) {
									throw new BussinessException("您当前不能兑换vip！","/goods/shopIndex.html");
								}
							}
						}
					}
					UserCache userCache = user.getUserCache();
					// 如果vip正在等待审核中，则不能申请,或者赠送vip正在审核中，则不能申请兑换vip
					if(userCache.getVipStatus() == 2 ){
						throw new BussinessException("您已经申请了vip，请不要重复申请！");
					}
				}else{
					if(goods.getStore() < goodsNum){
						throw new BussinessException("对不起该商品数量不足！","/goods/shopIndex.html");
					}
					//积分消费
					creditConvert.setType(CreditType.GOODS_EXPENSE_ENTITY);
				}
				creditConvert.setUser(user);
				creditConvert.setCreditValue(goods.getCreditValue()*goodsNum);
				creditConvert.setConvertMoney(goods.getMarketPrice()*goodsNum);
				creditConvert.setMoney(goods.getCost()*goodsNum);
				creditConvert.setStatus(0);//未审核
				creditConvert.setAddtime(new Date());
				creditConvert.setConvertNum(goodsNum);
				creditConvert.setGoods(goods);
				creditConvert.setRemark(address);
				this.creditConvertService.addConvertGoods(creditConvert,goods,goodsNum);
				message("兑换成功请等待管理人员审核！","/goods/shopIndex.html");
			}
		}else{
			message("该商品不存在！","/goods/shopIndex.html");
		}
		return MSG;
	}
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 

	// v1.8.0.4_u4 TGPROJECT-255 zf 2014-5-4 start
	/**
	 * 消费记录明细展示
	 * @return
	 */
	@Action(value="usedCreditDetail",
			results={@Result(name="success",type="ftl",location="/member/shop/usedCreditDetail.html")}
			)
	public String usedCreditDetail(){
		User user = userService.getUserById(getSessionUser().getUserId());
		this.getLeftShowByUser(user);
		SearchParam param = SearchParam.getInstance();
		param.addPage(paramInt("page"));
		param.addParam("user", user);
		param.addParam("status",Operator.NOTEQ,4);
		PageDataList<UserCreditConvert> list = creditConvertService.findUserCreditConvertList(param);
		/*request.setAttribute("picUrl", list.getList().get(0).getGoods().getPicList().get(0).getPicUrl());*/
		setPageAttribute(list, param);
		return SUCCESS;
	}
	/**
	 * 撤回积分消费
	 * @return
	 */
	@Action(value="canceltCreditConvert",
			results={@Result(name="success",type="redirect",location="/goods/usedCreditDetail.html")}
			)
	public String canceltCreditConvert(){
		try{
			UserCreditConvert ucc = creditConvertService.findUserCreditConvert(paramInt("id"));
			ucc.setStatus(4);
			creditConvertService.auditFailCreditConvert(ucc);
			return SUCCESS;
		}catch(Exception e){
			logger.error("撤回积分消费失败");
			throw new BussinessException("撤回积分消费失败");
		}
		
	}
	// v1.8.0.4_u4 TGPROJECT-255 zf 2014-5-4 end
}
