package com.liangyou.service;

/**
 * 逾期罚息计算服务
 * 
 * @author 1432
 *
 */
public interface LateInterestService {

	/**
	 * 融都计算，公共逾期罚息计算器。
	 */
	public void calculateCommonLateInterest();

	/**
	 * 国控小微逾期罚息计算方法
	 */
	public void calculateGkxw();

	/**
	 * 用于部分还款时，计算逾期罚息的算法 计算方法为：当天产生的罚息+原始罚息 计算单位为按天计算，用当（前应还本金-已还本金）*罚息比例
	 */
	public void calculatePartRepayment();

	/**
	 * 渝都贷逾期罚息计算
	 */
	public void calculateYDD();

}
