package com.liangyou.context;

public class PrizeResultType {
	/**
	 * 没有登录
	 */
	public static String RESULT_NO_REGISTER = "RESULT_NO_REGISTER";
	/**
	 * 参数错误
	 */
	public static String RESULT_PARAMETER_ERROR  = "RESULT_PARAMETER_ERROR";
	/**
	 * 规则ID不存在
	 */
	public static String RESULT_INVALID_RULE_ID = "RESULT_INVALID_RULE_ID";
	/**
	 * 当前时间在抽奖开始时间之前
	 */
	public static String RESULT_BEFORE_START_TIME = "RESULT_BEFORE_START_TIME";
	/**
	 * 当前时间在抽奖结束时间之后
	 */
	public static String RESULT_AFTER_END_TIME = "RESULT_AFTER_END_TIME";
	/**
	 * 用户可用积分不足
	 */
	public static String RESULT_POINT_LIMIT="RESULT_POINT_LIMIT";
	/**
	 * 用户可用次数不足
	 */
	public static String RESULT_TIME_LIMIT = "RESULT_TIME_LIMIT";
	/**
	 * 没有中奖
	 */
	public static String RESULT_NO_AWARD = "RESULT_NO_AWARD";
	/**
	 * 没有奖品
	 */
	public static String RESULT_NO_AWARD_OBJ = "RESULT_NO_AWARD_OBJ";
	/**
	 * 抽奖可用金额不足
	 */
	public static String RESULT_MONEY_LIMIT = "RESULT_MONEY_LIMIT";
}
