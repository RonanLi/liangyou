package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Goods;
import com.liangyou.domain.GoodsCategory;
import com.liangyou.domain.GoodsPic;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCreditConvert;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 用户兑换商品服务(包含兑换现金服务)
 * 
 * @author 1432
 *
 */
public interface CreditConvertService {
	// v1.8.0.4_u1 TGPROJECT-242
	/**
	 * 根据parentId来查询商品分类
	 * 
	 * @param parenId
	 * @return
	 */
	public List<GoodsCategory> getGoodsCategoryListByParentId(int parenId);

	/**
	 * 查询商品的二级分类
	 * 
	 * @return
	 */
	public List<GoodsCategory> getChildList();

	public GoodsCategory getGoodCategoryById(int id);

	public Goods addGoods(Goods goods);

	/**
	 * 查询商品列表
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<Goods> showGoodsList(SearchParam param);

	/**
	 * 获取用户消费记录
	 * 
	 * @param user
	 * @param type
	 * @return
	 */
	public List<UserCreditConvert> getCreditConvertListByUser(User user,
			String type);

	/**
	 * 获取商品分类列表
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<GoodsCategory> getGoodsCategoryList(SearchParam param);

	/**
	 * 获取商品列表
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<Goods> getGoodsList(SearchParam param);

	public Goods getGoodsById(int id);

	/**
	 * 保存GoodsCategory
	 * 
	 * @param goodsCategory
	 */
	public void saveGoodsCategory(GoodsCategory goodsCategory);

	/**
	 * 删除GoodsCategory
	 * 
	 * @param goodsCategory
	 */
	public void delGoodsCategory(int cateId);

	// v1.8.0.4_u1 TGPROJECT-242

	// v1.8.0.4_u1 TGPROJECT-252 lx start
	/**
	 * 保存商品
	 * 
	 * @param goods
	 */
	public Goods saveGoods(Goods goods);

	/**
	 * 保存商品图片
	 * 
	 * @param goodsPic
	 */
	public GoodsPic saveGoodsPic(GoodsPic goodsPic);

	/**
	 * 删除Goods
	 * 
	 * @param goods
	 */
	public void delGoods(Goods goods);

	/**
	 * 删除GoodsPIC
	 * 
	 * @param goods
	 */
	public void delAllGoodsPicByGoodsId(int id);

	/**
	 * 通过goodId查询goodsPic
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsPic> getGoodsPicByGoodsId(int id);

	// v1.8.0.4_u1 TGPROJECT-252 lx end

	// v1.8.0.4_u1 TGPROJECT- zf start
	/**
	 * 用户兑换记录
	 * 
	 * @param id
	 * @return
	 */
	public UserCreditConvert findUserCreditConvert(int id);

	/**
	 * 更新用户兑换记录
	 * 
	 * @param ucc
	 */
	public void updateUserCreditConvert(UserCreditConvert ucc);

	/**
	 * 查询所有用户兑换记录
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<UserCreditConvert> findUserCreditConvertList(
			SearchParam param);

	// v1.8.0.4_u1 TGPROJECT-244 qj 2014-05-04 start
	public UserCreditConvert getCreditConvertById(int id);

	public boolean auditCreditConvertVip(UserCreditConvert userCreditConvert);

	/**
	 * 购买商品
	 * 
	 * @param creditConvert
	 * @param goods
	 * @param goodsNum
	 */
	public void addConvertGoods(UserCreditConvert creditConvert, Goods goods,
			int goodsNum);

	/**
	 * 积分兑换现金
	 * 
	 * @param creditConvert
	 * @return
	 */
	public boolean auditCreditConvert(UserCreditConvert creditConvert);

	public void updateCreditConvertByAuthUser(User user,
			UserCreditConvert convert);

	/**
	 * 处理审核失败的记录
	 * 
	 * @param userCreditConvert
	 * @param user
	 */
	public void auditFailCreditConvert(UserCreditConvert userCreditConvert);

	/**
	 * 保存用户消费记录
	 * 
	 * @param userCreditLog
	 */
	public void saveCreditLog(UserCreditLog userCreditLog);

	// v1.8.0.4_u1 TGPROJECT-244 qj 2014-05-04 end

	public UserCreditLog findUserCreditLog(int id);

	public void updateUserCreditLog(UserCreditLog ucl);

	public PageDataList<UserCreditLog> findUserCreditLogList(SearchParam param);

	public void delUserCreditConvert(int id);
	// v1.8.0.4_u1 TGPROJECT- zf end
}
