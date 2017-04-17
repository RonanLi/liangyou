package com.liangyou.context;

import com.liangyou.domain.User;

/**
 * 常用变量类
 * 
 * @author fuxingxing
 * @date 2012-7-10-上午10:26:20
 * @version
 * 
 * 			<b>Copyright (c)</b> 2012-51融都-版权所有<br/>
 * 
 */
public class Constant {
	// 标的类型
	public static int TYPE_ALL = 100;
	/**
	 * 秒标
	 */
	public static int TYPE_SECOND = 101;
	/**
	 * 信用标
	 */
	public static int TYPE_CREDIT = 102;
	/**
	 * 给力标，抵押标
	 */
	public static int TYPE_MORTGAGE = 103;
	/**
	 * 净值标
	 */
	public static int TYPE_PROPERTY = 104;
	/**
	 * 担保标
	 */
	public static int TYPE_VOUCH = 105;
	/**
	 * 艺术品
	 */
	public static int TYPE_ART = 106;
	/**
	 * 慈善标
	 */
	public static int TYPE_CHARITY = 107;
	/**
	 * 工薪信用标
	 */
	public static int TYPE_LABOR = 108;
	/**
	 * 网商信用标
	 */
	public static int TYPE_NETBUSSINESS = 109;
	/**
	 * 流转标
	 */
	public static int TYPE_FLOW = 110;
	/**
	 * 学业信用标
	 */
	public static int TYPE_STUDENT = 111;
	/**
	 * 经营信用标
	 */
	public static int TYPE_SHOPBUSINESS = 112;
	/**
	 * 转让标
	 */
	public static int TYPE_ASSIGNMENT = 114;
	/**
	 * 体验标
	 */
	public static int TYPE_EXPERIENCE = 115;

	// 借款标的全部状态
	public static int STATUS_ALL = -1;
	// 借款标的发布状态
	public static int STATUS_NEW = 0;
	// 借款标的初审状态
	public static int STATUS_TRIAL = 1;
	// 借款标的复审状态
	public static int STATUS_REVIEW = 3;
	// 借款标的取消状态
	public static int STATUS_NOREVIEWS = 4;
	// 借款标的取消状态
	public static int STATUS_CANCEL = 5;
	// 借款标的完成放款状态
	public static int STATUS_BORROWSUCCESS = 6;
	// 借款标的部分还款状态
	public static int STATUS_PARTREPAIED = 7;
	// 借款标的全部还款状态
	public static int STATUS_ALLREPAIED = 8;
	// 借款标的逾期状态
	public static int STATUS_EXPIRED = 9;

	public static int STATUS_REPAYING = 10;

	public static int STATUS_INDEX = 11;
	public static int STATUS_complete = 12;

	public static int STATUS_ZR_INDEX = 13;
	// 美贷首页显示
	public static int STATUS_md_INDEX = 14;

	// 查找标的排序
	public static int ORDER_NONE = 0;
	// 按照金额的排序
	public static int ORDER_ACCOUNT_UP = 1;
	// 按照利率的排序
	public static int ORDER_APR_UP = 2;
	// 按照进度的排序
	public static int ORDER_JINDU_UP = 3;
	// 按照信用度的排序
	public static int ORDER_CREDIT_UP = 4;
	// 按照金额的排序
	public static int ORDER_ACCOUNT_DOWN = -1;
	// 按照利率的排序
	public static int ORDER_APR_DOWN = -2;
	// 按照进度的排序
	public static int ORDER_JINDU_DOWN = -3;
	// 按照信用度的排序
	public static int ORDER_CREDIT_DOWN = -4;
	// 首页项目排序
	public static int ORDER_INDEX = 11;

	public static int ORDER_TENDER_ADDTIME_UP = 21;

	public static int ORDRE_TENDER_ADDTIME_DOWN = 21;

	public static int ORDER_BORROW_ADDTIME_UP = 5;

	public static int ORDER_BORROW_ADDTIME_DOWN = -5;

	public static int ORDER_BORROW_TYPE_UP = 6;

	public static String SESSION_USER = "session_user"; // 前台用户
	public static String AUTH_USER = "auth_user"; // 后台用户
	public static String AUTH_PURVIEW = "auth_purview"; // 用户权限
	public static String TEMP_IDENTIFY_USER = "temp_identify_user"; // 好利贷

	public static int BORROW_KIND = 19;

	// 交易类型
	public static String TENDER = "tender";
	public static String EXPERIENCE_TENDER = "experience_tender"; // add by gy 2016-10-21 12:16:48 增加体验金交易类型

	public static String RECHARGE = "recharge"; // 充值
	public static String WEB_RECHARGE = "web_recharge";// 网站充值
	// public static String BACK_RECHARGE = "back_recharge";// 网站充值
	// TGPROJECT-345
	public static String TENDER_REWARD = "tender_reward"; // 投标奖励
	// TGPROJECT-345
	// TGPROJECT-409 注册送奖金 start 大互联新功能
	/**
	 * 注册奖励
	 */
	public static String REGISTER_REWARD = "register_reward";
	// TGPROJECT-409 注册送奖金 end 大互联新功能
	// 红包兑现
	public static String REWAED_TOMONEY = "reward_tomoney";
	// 红包兑现
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
	public static String INVITE_VIP_REWARD = "invite_vip_reward";
	public static String INVITE_INTEREST_REWARD = "invite_interest_reward";
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
	public static String WEB_DEDUCT = "web_deduct"; // 网站扣款
	public static String RECHARGE_APR = "recharge_apr"; // 充值费率
	public static String SYSTEM = "system"; // 系统
	public static String INVEST = "invest";// 投资
	public static String RECHARGE_SUCCESS = "recharge_success"; // 充值成功
	public static String FREEZE = "freeze";// 冻结
	public static String UNFREEZE = "unfreeze";// 解冻
	public static String RETURN_EXPERIENCEMONEY = "return_experienceMoney"; // 退回体验金
	public static String BORROW_SUCCESS = "borrow_success";
	public static String BORROW_FEE = "borrow_fee";
	public static String MANAGE_FEE = "manage_fee";
	public static String RISKRESERVE_FEE = "riskreserve_fee";// 风险准备金
	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
	public static String RISKRESERVE_FEE_TO_BORROWER = "riskreserve_fee_to_borrower";// 还款完成归还风险准备金
	// v1.8.0.4_u4 TGPROJECT-365 qinjun 2014-07-14 start
	// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 end
	public static String WEB_MANAGE_FEE = "web_manage_fee";// 平台管理费
	public static String SMALL_LOAN_FEE = "small_loan_fee";// 小贷审核费
	public static String WARRANT_FEE = "warrant_fee";// 担保公司担保费
	public static String INTRODUCE_FEE = "introduce_fee";// 服务公司推荐费
	// v1.8.0.4_u4 TGPROJECT-346 qinjun 2014-07-01 end
	public static String REPAID = "repaid";
	public static String REPAID_CAPTIAL = "repaid_captial"; // 还款-扣除本金
	public static String REPAID_INTEREST = "repaid_interest"; // 还款-扣除利息
	public static String PREREPAID = "prerepaid";
	public static String VOUCH_REPAID = "vouch_repaid";
	public static String VOUCH_WARD = "vouch_ward";
	public static String BORROW_FAIL = "borrow_fail";
	public static String VIP_FEE = "vip_fee";
	public static String INVITE_MONEY = "invite_money";
	public static String ACCOUNT_BACK = "account_back";
	public static String CASH_FROST = "cash_frost";
	public static String CASH_SUCCESS = "cash_success";
	public static String CASH_FAIL = "cash_fail";
	public static String CASH_CANCEL = "cash_cancel";
	public static String WAIT_INTEREST = "wait_interest";
	public static String AWARD_ADD = "award_add";
	public static String HUIKUAN_AWARD = "huikuan_award";
	public static String RECHARGE_FEE = "recharge_fee";
	public static String CAPITAL_COLLECT = "capital_collect";
	public static String INTEREST_COLLECT = "interest_collect";
	public static String CASH_ACCOUNT_BACK = "cash_account_back";

	public static String REPAYMENT_FAIL = "repayment_fail";

	// wsl 满标前补偿金功能【心意贷】2014-08-28 start
	/**
	 * 扣除补偿金
	 */
	public static String COMPENSATION_REPAID = "compensation_repaid";
	/**
	 * 收回补偿金
	 */
	public static String COMPENSATION_COLLECT = "compensation_collect";
	/**
	 * 垫付补偿金
	 */
	public static String COMPENSATION_SITE_PAY = "compensation_site_pay";
	/**
	 * 还款给网站
	 */
	public static String COMPENSATION_REPAY_SITE = "compensation_repay_site";
	/**
	 * 垫付逾期补偿金
	 */
	public static String COMPENSATION_SITE_LATE_PAY = "compensation_site_late_pay";
	// wsl 满标前补偿金功能【心意贷】2014-08-28 end

	public static String AWARD_DEDUCT = "award_deduct";
	public static String LATE_DEDUCT = "late_deduct"; // 逾期罚息扣除
	public static String LATE_REPAYMENT = "late_repayment";// 逾期还款
	public static String LATE_COLLECTION = "late_collection"; // 逾期罚息收入
	public static String WEBSITEPAY = "websitepay"; // 网站垫付
	public static String PRIORREPAID_CAPTIAL = "prior_repaid_captial"; // 提前还款-扣除本金
	public static String PRIORREPAID_INTEREST = "prior_repaid_interest"; // 提前还款-扣除本金REPAID_compen
	public static String PRIOR_REPAID_COMPEN = "prior_repaid_compen"; // 提前还款-扣除补偿金

	public static String REPAY_WEB_CAPTIAL = "repay_web_captial";// 还款给网站-本金
	public static String REPAY_WEB_INTEREST = "repay_web_interest";// 还款给网站-利息
	public static String REPAY_WEB_LATEINTEREST = "repay_web_lateinterest";// 还款给网站-逾期罚息
	// v1.8.0.4_u1 TGPROJECT-244 qj 2014-05-04 start
	public static String GOODS_EXPENSE_MONEY = "goods_expense_money";// 积分兑换
	// v1.8.0.4_u1 TGPROJECT-244 qj 2014-05-04 end
	// v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-23 start
	public static String INVITE_REBATE = "invite_rebate";// 推荐提成
	// v1.8.0.4_u2 TGPROJECT-302 lx 2014-05-23 end
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	public static String INTEREST_GENERATE_IN = "interest_generate_in";// 余额宝转入
	public static String INTEREST_GENERATE_OUT = "interest_generate_out";// 余额宝转出
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end

	// v1.8.0.4_u4 TGPROJECT-367 qinjun 2014-07-16 start
	public static String PRIZE_GIVE_MONEY = "prize_give_money";// 余额宝转出
	// v1.8.0.4_u4 TGPROJECT-367 qinjun 2014-07-16 end
	/**
	 * 借款合同费
	 */
	public static String BORROW_CONTRACT = "borrow_contract";
	// 下面垫付专用
	public static String SITE_PAY_CAPITAL = "site_pay_capital"; // 网站垫付本金
	public static String SITE_PAY_INTEREST = "site_pay_interest"; // 网站垫付利息
	public static String SITE_PAY_LATE_INTEREST = "site_pay_late_interest"; // 网站垫付逾期罚息
	public static String SITE_PAY_ALL = "site_pay_all"; // 网站垫付当前及全部

	// 消息处理类型
	public final static String SET_READ_MSG = "1";
	public final static String SET_UNREAD__MSG = "2";
	public final static String DEL_MSG = "3";

	public final static String DB_PREFIX = "dw_";

	public final static long ADMIN_ID = 1; // 发版要改过来。
	public final static int CARDID = 1616;// 身份证证件对应id

	public final static int OP_ADD = 1;
	public final static String OP_REDUCE = "2";

	public static User ADMIN_USER = new User();
	public static int KEFU_TYPEID = 3;
	public static String DRAW_CARDTYPE = "DEBIT_CARD";
	// 提现银行卡 WITHDRAW_B2C[对私] WITHDRAW_B2B[对公] ALL[所有]
	public static String DRAW_CHANNEL_NO = "WITHDRAW_B2C";
	// 验证码全局开关
	public static boolean VALIDCODEOPEN = false;
	// 债权转让全局参数----------------------------------------
	public static int ASSIGNMENT_TENDER_AFTER = 30; // 投标多少年天后可以发布转让标（单位：天）；
	public static int ASSIGNMENT_BORROW_REMAIN = 30; // 原借款剩余时间还剩多少天（单位：天）；
	public static double ASSIGNMENT_LESTMONEY = 50; // 最小转让金额（单位：元）
	public static int ASSIGNMENT_LOAN_AS_OLDTYPE = 0; // 是否按照原借款方式发标[0：按照普通标；1：原方式（按信用、净值等）；2：先按照普通表，再根据原方式]

	/**
	 * 标初审流程
	 */
	public static String VERIFY_BORROW = "001";
	/**
	 * 标复审流程
	 */
	public static String VERIFY_FULL_BORROW = "002";
	/**
	 * 信用额度审流程
	 */
	public static String VERIFY_AMOUNT = "003";
	/**
	 * 网站垫付流程
	 */
	public static String WEB_SITE_PAY_FLOW = "004";
	/**
	 * 撤回流程
	 */
	public static String CANCEL_BORROE = "005";
	/**
	 * 扣款流程
	 */
	public static String OPERATE_WEB_DEDUCT = "006";
	/**
	 * 验证码类型为全字母
	 */
	public static String VALIDIMG_ENGLISH = "english";
	/**
	 * 验证码类型为全数字
	 */
	public static String VALIDIMG_NUMBER = "number";
	/**
	 * 验证码类型为字母+数字
	 */
	public static String VALIDIMG_ALL = "all";
	/**
	 * 邮箱登陆
	 */
	public static String EMAIL_LOGIN = "email_login";

	public static String SEARCH_UP = "up";

	public static String SEARCH_DOWN = "down";

	// 汇付处理全局参数---------------------------
	public static String LOANS = "LOANS"; // 放款接口
	public static String REPAYMENT = "REPAYMENT"; // 还款接口
	public static String UsrFreezeBg = "UsrFreezeBg"; // 冻结
	public static String UsrUnFreeze = "UsrUnFreeze"; // 解冻

	/**
	 * 正常还款
	 */
	public static int REPAY_BASE = 1;
	/**
	 * 提前还款
	 */
	public static int REPAY_PRIOR = 2;
	/**
	 * 流转标还款
	 */
	public static int REPAY_FLOW = 3;
	/**
	 * 逾期还款
	 */
	public static int REPAY_LATE = 4;
	/**
	 * 还款给网站
	 */
	public static int REPAY_PAY_WEB = 5;
	/**
	 * 网站垫付
	 */
	public static int REPAY_SITE_PAY = 6;
	/**
	 * 债权转让收款
	 */
	public static int REPAY_ASSIGNMENT_PAY = 7;

	/**
	 * add by gy 2016-10-21 12:17:11 体验金还款
	 */
	public static int REPAY_EXPERIENCEMONEY_PAY = 8;

	// v1.8.0.4_u4 TGPROJECT-353 qinjun 2014-07-03 start
	/**
	 * 利息管理费费率规则指定索引(51帮你)
	 */
	public static int INTEREST_FEE_RATE_RULE = 2;
	// v1.8.0.4_u4 TGPROJECT-353 qinjun 2014-07-03 end

	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 start
	/**
	 * VIP推荐人提成规则指定索引（51帮你）
	 */
	public static int VIP_INVITE_RATE_RULE = 2;
	/**
	 * 利息管理费推荐人提成规则指定索引（51帮你）
	 */
	public static int INTEREST_INVITE_RATE_RULE = 3;
	// v1.8.0.4_u4 TGPROJECT-356 qinjun 2014-07-04 end

	// add by gy 2016年11月25日16:06:17
	// 增加注册用户来源变量 form_wap：wap端注册
	public static String FROM_WAP = "from_wap";
	// 增加注册用户来源变量 from_weixin：微信端注册
	public static String FROM_WEIXIN = "from_weixin";
	// add by lijing 微信用常量  start
	public static final String DEFAULT_ENCODING = "utf-8";
	public static final String WECHATMSG_PATH_PREFIX = "com.liangyou.model.wechatModel.message.inmsg.In";
	public static final String WECHATEVENT_PATH_PREFIX="com.liangyou.model.wechatModel.message.event.In";
	// 微信 用 常量 end 
}
