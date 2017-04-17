package com.liangyou.context;

import java.util.HashMap;
import java.util.Map;

public class AccountLogTypeName {

	public static AccountLogTypeName accountLogTypeName;

	public Map<String, String> typeNameMap = new HashMap<String, String>();

	public AccountLogTypeName() {
		typeNameMap.put("recharge", "用户充值");
		typeNameMap.put("borrow_fee", Global.getValue("trade_name_manage_fee"));
		typeNameMap.put("cash_cancel", "取消提现");
		typeNameMap.put("cash_frost", "提现冻结");
		typeNameMap.put("tender", "投标");
		typeNameMap.put("experience_tender", "体验标投标"); // add by gy 2016-10-19 16:59:20 增加体验标日志类型
		typeNameMap.put("return_experienceMoney", "体验金退回");// add by gy 2016-10-19 16:59:20 增加体验标日志类型
		typeNameMap.put("borrow_success", "借款入帐");
		typeNameMap.put("margin", "保证金");
		typeNameMap.put("invest", "扣除冻结款");
		typeNameMap.put("fee", Global.getString("trade_name_manage_fee"));
		typeNameMap.put("award_deduct", "扣除投标奖励");
		typeNameMap.put("award_add", "投标奖励");
		typeNameMap.put("repaid", "还款");
		typeNameMap.put("invest_false", "投标失败资金返回");
		typeNameMap.put("recharge_fee", "提现手续费");
		typeNameMap.put("collection", "待收金额");
		typeNameMap.put("borrow_frost", "解冻借款担保金");
		typeNameMap.put("prepaid", "提前还款");
		typeNameMap.put("vip_fee", "vip会员费");
		typeNameMap.put("realname", "实名认证费用");
		typeNameMap.put("recharge_success", "充值成功");
		typeNameMap.put("recharge_false", "充值失败");
		typeNameMap.put("system_repayment", "逾期系统还款");
		typeNameMap.put("late_deduct", "逾期罚息扣除");
		typeNameMap.put("late_repayment", "逾期还款");
		typeNameMap.put("ticheng", "投标提成");
		typeNameMap.put("late_collection", "逾期收入");
		typeNameMap.put("scene_account", "现场认证费用");
		typeNameMap.put("vouch_advanced", "担保垫付扣费");
		typeNameMap.put("borrow_kouhui", "借款人罚金扣回");
		typeNameMap.put("vouch_awardpay", "担保奖励支付");
		typeNameMap.put("vouch_award", "担保奖励接收");
		typeNameMap.put("margin_vouch", "担保成功冻结5%");
		typeNameMap.put("video", "视频认证费用");
		typeNameMap.put("manage_fee", Global.getString("trade_name_interest_fee"));
		typeNameMap.put("freeze", "冻结资金");
		typeNameMap.put("unfreeze", "解冻资金");
		typeNameMap.put("borrow_succcess", "借款成功");
		typeNameMap.put("vouch_repaid", "担保支付");
		typeNameMap.put("borrow_fail", "借款失败");
		typeNameMap.put("wait_interest", "待收利息");
		typeNameMap.put("cash_success", "提现成功");
		typeNameMap.put("cash_fail", "提现失败");
		typeNameMap.put("account_back", "扣款");
		typeNameMap.put("invite_money", "邀请好友提成");
		typeNameMap.put("capital_collect", "还款-本金收回");
		typeNameMap.put("interest_collect", "还款-利息收回");
		typeNameMap.put("repaid_captial", "还款-扣除本金");
		typeNameMap.put("repaid_interest", "还款-扣除利息");
		// wsl 满标前补偿金功能【心意贷】2014-08-28 start
		typeNameMap.put("compensation_repaid", "扣除补偿金");
		typeNameMap.put("compensation_collect", "收回补偿金");
		typeNameMap.put("compensation_site_pay", "垫付补偿金");
		typeNameMap.put("compensation_repay_site", "还款给网站");
		typeNameMap.put("compensation_site_late_pay", "垫付逾期补偿金");
		// wsl 满标前补偿金功能【心意贷】2014-08-28 end
		typeNameMap.put("websitepay", "网站垫付");
		typeNameMap.put("web_recharge", "网站充值");
		typeNameMap.put("riskreserve_fee", "风险准备金");
		typeNameMap.put("site_pay_capital", "网站垫付本金");
		typeNameMap.put("site_pay_interest", "网站垫付利息");
		typeNameMap.put("site_pay_late_interest", "网站垫付逾期罚息");
		typeNameMap.put("borrow_contract", "合同登记费");
		typeNameMap.put("repay_web_captial", "还款给网站-本金");
		typeNameMap.put("repay_web_interest", "还款给网站-利息");
		typeNameMap.put("repay_web_lateinterest", "还款给网站-逾期罚息");
		typeNameMap.put("prior_repaid_captial", "提前还款-本金");
		typeNameMap.put("prior_repaid_interest", "提前还款-利息");
		typeNameMap.put("prior_repaid_compen", "提前还款-补偿金");
		typeNameMap.put("cash_account_back", "取现资金退回");
	}

	public static AccountLogTypeName getInstance() {
		if (accountLogTypeName == null) {
			return new AccountLogTypeName();
		} else {
			return accountLogTypeName;
		}
	}

}
