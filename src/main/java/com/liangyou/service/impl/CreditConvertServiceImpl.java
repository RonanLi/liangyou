package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.CreditType;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.GoodsCategoryDao;
import com.liangyou.dao.GoodsDao;
import com.liangyou.dao.GoodsPicDao;
import com.liangyou.dao.RuleDao;
import com.liangyou.dao.UserCacheDao;
import com.liangyou.dao.UserCreditConvertDao;
import com.liangyou.dao.UserCreditDao;
import com.liangyou.dao.UserCreditLogDao;
import com.liangyou.dao.UserCreditTypeDao;
import com.liangyou.dao.UserDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Goods;
import com.liangyou.domain.GoodsCategory;
import com.liangyou.domain.GoodsPic;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditConvert;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.CreditConvertService;
import com.liangyou.util.DateUtils;


@Service(value="creditConvertService")
@Transactional
public class CreditConvertServiceImpl implements CreditConvertService {

	@Autowired
	GoodsCategoryDao goodsCategoryDao;
	@Autowired
	GoodsDao goodsDao;
	@Autowired
	GoodsPicDao goodsPicDao;
	@Autowired
	UserCreditConvertDao userCreditConvertDao;
	@Autowired
	UserCreditDao userCreditDao;
	@Autowired
	UserCreditTypeDao userCreditTypeDao;
	@Autowired
	RuleDao ruleDao;
	@Autowired
	UserDao userDao;
	@Autowired
	UserCacheDao userCacheDao;
	@Autowired
	UserCreditLogDao userCreditLogDao;
	@Autowired
	AccountDao accountDao;
	@Autowired
	AccountLogDao accountLogDao;
	@Autowired
	UserCreditConvertDao userCreditConvertdao;
	@Autowired
	BorrowService borrowService;
	@Autowired
	ApiService apiService;
	//v1.8.0.4_u1 TGPROJECT-242  
	@Override
	public GoodsCategory getGoodCategoryById(int id) {
		return goodsCategoryDao.find(id);
	}
	
	@Override
	public List<GoodsCategory> getGoodsCategoryListByParentId(int parenId) {
		return goodsCategoryDao.getListByParentId(parenId);
	}
	
	@Override
	public List<GoodsCategory> getChildList(){
		return goodsCategoryDao.getChildList();
	}
	@Override
	public PageDataList<GoodsCategory> getGoodsCategoryList(SearchParam param){
		return goodsCategoryDao.findPageList(param);
	}
	
	@Override
	public void saveCreditLog(UserCreditLog userCreditLog){
		userCreditLogDao.save(userCreditLog);
	}
	
	@Override
	public Goods getGoodsById(int id) {
		return goodsDao.find(id);
	}
	
	@Override
	public Goods addGoods(Goods goods) {
		return goodsDao.save(goods);
	}
	
	@Override
	public List<UserCreditConvert> getCreditConvertListByUser(User user, String type){
		return userCreditConvertDao.getCreditConvertListByUser(user,type);
	}

	@Override
	public PageDataList<Goods> showGoodsList(SearchParam param) {
		return goodsDao.findPageList(param);
	}
	
	@Override
	public PageDataList<Goods> getGoodsList(SearchParam param){
		return  goodsDao.findPageList(param);
	}
	@Override
	public void saveGoodsCategory(GoodsCategory goodsCategory ){
		goodsCategoryDao.save(goodsCategory);
	}
	@Override
	public void delGoodsCategory(int cateId ){
		goodsCategoryDao.delete(cateId);
	}
	
	@Override
	public UserCreditConvert getCreditConvertById(int id) {
		return userCreditConvertDao.find(id);
	}
	
	@Override
	public void addConvertGoods(UserCreditConvert creditConvert,Goods goods,int goodsNum){
		//兑换提交对应goods数量减少
		goods.setStore(goods.getStore() - goodsNum);
		goodsDao.save(goods);
		userCreditConvertDao.save(creditConvert);
		UserCredit userCredit = creditConvert.getUser().getUserCredit();
		userCredit.setValidValue(userCredit.getValidValue() - creditConvert.getCreditValue());
		userCredit.setExpenseValue(userCredit.getExpenseValue() + creditConvert.getCreditValue());
		userCreditDao.save(userCredit);
	}
	
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	@Override
	public boolean auditCreditConvertVip(UserCreditConvert userCreditConvert){
		Rule rule = ruleDao.getRuleByNid(CreditType.GOODS_INTEGRAL_VIP);
		int vip_time = rule.getValueIntByKey("vip_time");//兑换VIP有效时间（月）
		
		int convert_num = userCreditConvert.getConvertNum();
		if(convert_num <= 0) return false;
		convert_num = convert_num > 0 ? convert_num : vip_time;
		
		// 如果积分兑换信息为空或者会员为空，则return
		if(userCreditConvert == null || userCreditConvert.getUser() == null) return false;
		
		UserCreditType userCreditType = userCreditTypeDao.getUserCreditType(CreditType.GOODS_INTEGRAL_VIP);
		// 如果积分类型为空，则return
		if(userCreditType == null) return false;
		
		long credit_value = userCreditConvert.getCreditValue();
		User user = userDao.find(userCreditConvert.getUser().getUserId());
		UserCredit userCredit = user.getUserCredit();
		userCreditConvert.setVerifytime(new Date());
		// 审核通过
		if (userCreditConvert.getStatus() ==1) {
			userCreditConvertDao.save(userCreditConvert);
			UserCache userCache = user.getUserCache();
			// 更新结束时间
			String vipEndTimeString = "";
			if (userCache.getVipStatus() == 0 || userCache.getVipStatus() == -1 
					|| userCache.getVipStatus() == 3) { // 没有申请vip或申请vip失败或vip过期
				userCache.setVipStatus(1);
				vipEndTimeString = DateUtils.getTimeStr(
						DateUtils.rollMon(DateUtils.getDate(DateUtils.getNowTimeStr()), vip_time));
			} else if (userCache.getVipStatus() == 1) { // vip现在状态就是vip
				vipEndTimeString = DateUtils.getTimeStr(DateUtils.rollMon(
						DateUtils.getDate(DateUtils.getTimeStr(userCache.getVipEndTime())), vip_time));
			} else { // 如果vip正在等待审核中，则不能申请
				return false;
			}
			userCache.setVipEndTime(DateUtils.getDate(vipEndTimeString));
			userCache.setVipVerifyTime(new Date());
			userCacheDao.save(userCache);
//			//会员积分日志记录信息
//			UserCreditLog  userCreditLog = new UserCreditLog(user, 0,0, 
//					CreditType.OP_TYPE_REDUCE, userCreditType, userCredit);
//			CreditUtils.fillUserCreditLog(userCreditLog, credit_value, userCredit.getValidValue(), userCredit, "");
//			userCreditLogDao.save(userCreditLog);
		}else{// 审核失败或非法操作，返回兑换积分：消费积分减，有效积分加
			failCreditConvert(userCreditConvert);
		}
		return true;
	}
	private void failCreditConvert(UserCreditConvert userCreditConvert){
		User user =  userCreditConvert.getUser();
		UserCredit userCredit= user.getUserCredit();
		userCreditConvertDao.save(userCreditConvert);
		userCredit.setValidValue(userCredit.getValidValue() + userCreditConvert.getCreditValue());
		userCredit.setExpenseValue(userCredit.getExpenseValue() - userCreditConvert.getCreditValue());
		userCreditDao.save(userCredit);
	}
	
	@Override
	public void auditFailCreditConvert(UserCreditConvert userCreditConvert){
		failCreditConvert(userCreditConvert);
	}
	
	public void updateCreditConvertByAuthUser(User user,UserCreditConvert convert){
		convert.setVerifytime(new Date());
		convert.setVerifyUser(user);
		convert.setVerifyUsername(user.getUsername());
		userCreditConvertDao.save(convert);
	}
	
	public boolean auditCreditConvert(UserCreditConvert creditConvert){
		// 如果积分兑换信息为空或者会员ID小于等于零，则return
		if(creditConvert == null || creditConvert.getUser() == null || creditConvert.getMoney() <= 0) return false;
		double money = creditConvert.getMoney();
		User user = creditConvert.getUser();
		creditConvert.setVerifytime(new Date());
		// 审核通过
		if(creditConvert.getStatus() == 1 ){
			List<Object> taskList = new ArrayList<Object>();
			apiService.creditForMoney(user, money, taskList);//第三方接口
			if(borrowService.doApiTask(taskList)){//调用转账功能
				userCreditConvertDao.save(creditConvert);
				user = userDao.find(user.getUserId());
				Account act = user.getAccount();
				act.setTotal(act.getTotal()+money);
				act.setUseMoney(act.getUseMoney() + money);
				accountDao.save(act);
				AccountLog log = new AccountLog(user.getUserId(), Constant.GOODS_EXPENSE_MONEY,
						act.getTotal(), money, act.getUseMoney(), act.getNoUseMoney(), act.getCollection(), 1, "积分兑换现金："+money+"元", new Date(), "");
				accountLogDao.save(log);
			}else{
				throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
			}
			
		}else{// 审核失败或非法操作，返回兑换积分：消费积分减，有效积分加
			failCreditConvert(creditConvert);
		}
		return true;
	}
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 
	
	//v1.8.0.4_u1 TGPROJECT-242  
	//v1.8.0.4_u1 TGPROJECT-252  lx start
	@Override
	public Goods saveGoods(Goods goods ){
		return goodsDao.save(goods);
	}
	@Override
	public GoodsPic saveGoodsPic(GoodsPic goodsPic ){
		return goodsPicDao.save(goodsPic);
				
	}
	@Override
	public void delGoods(Goods goods){
		List<GoodsPic> picList=goodsPicDao.getGoodsPicByGoodsId(goods.getId());
		if(picList!=null){
			for (GoodsPic goodsPic:picList) {
				goodsPicDao.delete(goodsPic.getId());
			}
		}
		goodsDao.delete(goods.getId());
	}
	public void delAllGoodsPicByGoodsId(int id){
		List<GoodsPic> picList=goodsPicDao.getGoodsPicByGoodsId(id);
		if(picList!=null){
			for (GoodsPic goodsPic:picList) {
				goodsPicDao.delete(goodsPic.getId());
			}
		}
	}
	public List<GoodsPic> getGoodsPicByGoodsId(int id){
		return goodsPicDao.getGoodsPicByGoodsId(id);
	}
	//v1.8.0.4_u1 TGPROJECT-252  lx end
	
	//v1.8.0.4_u1 TGPROJECT-255  zf start
	@Override
	public UserCreditConvert findUserCreditConvert(int id){
		return userCreditConvertdao.find(id);
	}
	@Override
	public void updateUserCreditConvert(UserCreditConvert ucc){
		userCreditConvertdao.merge(ucc);
	}
	
	@Override
	public PageDataList<UserCreditConvert> findUserCreditConvertList(SearchParam param) {
		return userCreditConvertdao.findPageList(param);
	}
	
	//v1.8.0.4_u1 TGPROJECT-255  zf end
	
	// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-30 start
		@Autowired
		private UserCreditLogDao userCreditLogdao;
		
		@Override
		public UserCreditLog findUserCreditLog(int id){
			return userCreditLogdao.find(id);
		}
		@Override
		public void updateUserCreditLog(UserCreditLog ucl){
			userCreditLogdao.merge(ucl);
		}
		
		@Override
		public PageDataList<UserCreditLog> findUserCreditLogList(SearchParam param) {
			return userCreditLogdao.findPageList(param) ;
		}

		@Override
		public void delUserCreditConvert(int id) {
			userCreditConvertdao.delUserCreditConvert(id);
		}

		// v1.8.0.4_u4 TGPROJECT-128 zf 2014-04-30 end
}
