package com.liangyou.service;

import com.liangyou.domain.AccountLog;
import com.liangyou.model.APIModel.RechargeModel;

/**
 * 充值服务
 *  第三方接口充值，取现方法接口
 * 
 * @author WUJING
 *
 */
public interface ApiRechrageService {

	/**
	 * 环迅充值方法资金处理
	 * 
	 * @param reModel
	 */
	public void doIpsRechrege(RechargeModel reModel);

	/**
	 * 易极付充值方法处理
	 */
	public void doYjfRecharge(RechargeModel rem, AccountLog log, String params);

	/**
	 * 乾多多充值方法处理
	 */
	public void doMmmRecharge(RechargeModel rem, AccountLog log);

	/**
	 * 汇付充值方法处理
	 */
	public void doChinapnrRecharge(RechargeModel rem);
}
