package com.liangyou.context;


public class CreditType {
	
	/**
	 * 变动类型,1:增加
	 */
	public static final int OP_TYPE_ADD = 1;
	
	/**
	 * 变动类型,2:减少
	 */
	public static final int OP_TYPE_REDUCE = 2;
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	/**
	 * 变动类型,3:审核不通过或者用户撤回
	 */
	public static final int OP_TYPE_FAIL =3;
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 
	
	/**
	 * 邮箱认证
	 */
	public static final int CREDIT_TYPE_EMAIL = 1;
	/**
	 * 实名认证
	 */
	public static final int CREDIT_TYPE_REALNAME = 2; 
	/**
	 * 手机认证
	 */
	public static final  int CREDIT_TYPE_PHONE= 3;
	/**
	 * 视频认证
	 */
	public static final int CREDIT_TYPE_VIDEO=4;
	/**
	 * 现场认证
	 */
	public static final int CREDIT_TYPE_SCENE = 5;
	/** 
	 * 提交证件
	 */
	public static final int CREDIT_TYPE_ZHENGJIAN = 6;
	/**
	 * 投标成功
	 */
	public static final int CREDIT_TYPE_INVEST_SUCCESS = 7 ;
	/**
	 * 借款成功
	 */
	public static final int CREDIT_TYPE_BORROW_SUCCESS = 8;
	/**
	 * 论坛发稿
	 */
	public static final int CREDIT_TYPE_BBS_TOPICS =9;
	/**
	 * 提前还款
	 */
	public static final int CREDIT_TYPE_BORROW_PAYMENGT = 10;
	/**
	 * 逾期还款
	 */
	public static final int CREDIT_TYPE_BORROW_PAYMENTOVER = 11;
	/**
	 * 正常还款
	 */
	public static final int CREDIT_TYPE_ADVANCE_DAY =12;
	/**
	 * VIP审核通过
	 */
	public static final int CREDIT_TYPE_VIP =13;
	/**
	 * 注册成功
	 */
	public static final int CREDIT_TYPE_REGISTER = 14;
	/**
	 * 兑换vip
	 */
	public static final int CREDIT_TYPE_INTEGRAL_VIP = 18;
	
	/**
	 * 赠送积分
	 */
	public static final String 	GIFT = "gift";
	/**
	 * 投资积分
	 */
	public static final String TENDER = "tender";
	/**
	 * 借款积分
	 */
	public static final String BORROW = "borrow";
	/**
	 * 消费积分
	 */
	public static final String EXPENSE = "expense";
	/**
	 * 论坛积分
	 */
	public static final String 	BBS = "bbs";

	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	//*********积分商城兑换类型**************
	/**
	 * vip兑换
	 */
	public static String GOODS_INTEGRAL_VIP = "goods_integral_vip";
	
	/**
	 * 商品兑换
	 */
	public static String GOODS_EXPENSE_ENTITY = "goods_expense_entity";
	/**
	 * 商品兑换
	 */
	public static String GOODS_EXPENSE_MONEY = "goods_expense_money";
	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 
	
}
