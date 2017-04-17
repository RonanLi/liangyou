package com.liangyou.context;

/**
 * //1.8.0.4_u4 TGPROJECT-345  wujing dytz  
 * 关于奖励类型的常量定义
 * @author wujing
 *
 */
public class RewardType {

	/**
	 * 注册奖励，需投资有回款
	 */
	public static final int REGISTER = 1;

	/**
	 * 注册和实名后投资奖励
	 */
	public static final int REGISTERANDIDENT = 2;

	/**
	 * 投资奖励
	 */
	public static final int TENDERINVITE = 3;

	/**
	 * 发放类型为现金
	 */
	public static final int EXTENDWAYREWAED=0;

	/**
	 * 发放类型为红包
	 */
	public static final int EXTENDWAYRED=1;

	/**
	 * 注册实名通过赠送红包，
	 */
	public static final int REGISTERIENTRAD =4;

	/***** 投资红包 ****/
	public static final int TENDERRAD = 6;

	/*** 充值红包 ***/
	public static final int RECHARGERAD=5;

	/**
	 * 推荐人VIP提成奖励
	 */
	public static final int VIPINVITE = 7;
	/**
	 * 推荐人利息管理费提成奖励
	 */
	public static final int INTERESTINVITE = 8;
}
